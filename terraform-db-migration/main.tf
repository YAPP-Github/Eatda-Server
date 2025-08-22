data "terraform_remote_state" "common_infra" {
  backend = "s3"
  config = {
    bucket = "eatda-tf-state"
    key    = "common/terraform.tfstate"
    region = "ap-northeast-2"
  }
}

data "terraform_remote_state" "bootstrap_infra" {
  backend = "s3"
  config = {
    bucket = "eatda-tf-state"
    key    = "bootstrap/terraform.tfstate"
    region = "ap-northeast-2"
  }
}

data "terraform_remote_state" "prod_infra" {
  backend = "s3"
  config = {
    bucket = "eatda-tf-state"
    key    = "prod/terraform.tfstate"
    region = "ap-northeast-2"
  }
}

data "aws_db_snapshot" "latest_prod_snapshot" {
  db_instance_identifier = data.terraform_remote_state.prod_infra.outputs.rds_instance_identifier
  most_recent            = true
  snapshot_type          = "automated"
}

data "aws_iam_instance_profile" "ec2_to_ecs" {
  name = "ec2-to-ecs"
}

module "migration_iam_role" {
  source = "../terraform/common/iam-role"

  for_each = local.iam_roles

  name                   = each.key
  assume_role_services   = each.value.assume_role_services
  policy_arns            = each.value.policy_arns
  custom_inline_policies = local.custom_inline_policies
  tags                   = try(each.value.tags, {})
}

module "migration_sg" {
  source = "../terraform/common/security-group"

  vpc_id                = data.terraform_remote_state.common_infra.outputs.vpc_id
  security_groups       = local.security_groups
  egress_rules          = local.egress_rules
  cross_reference_rules = local.cross_reference_rules
  ingress_rules         = {}
}

module "cloned_s3_bucket" {
  source = "../terraform/prod/s3"

  bucket_name_prefix = local.cloned_s3_bucket_prefix
  environment        = local.cloned_s3_environment
  allowed_origins    = local.cloned_s3_allowed_origins
  force_destroy      = local.force_destroy
}

resource "aws_vpc_endpoint" "s3_gateway" {
  vpc_id = data.terraform_remote_state.common_infra.outputs.vpc_id

  service_name = "com.amazonaws.${data.aws_region.current.id}.s3"

  route_table_ids = data.terraform_remote_state.common_infra.outputs.private_route_table_ids

  tags = {
    Name    = "vpce-s3-gateway-for-migration"
    Purpose = "Ephemeral"
  }
}

resource "aws_lambda_function" "migration_task" {
  function_name = local.migration_lambda_function_name
  handler       = "handler.lambda_handler"
  runtime       = "python3.12"
  timeout       = 900

  filename         = "./build/migration_lambda.zip"
  source_code_hash = filebase64sha256("./build/migration_lambda.zip")

  role = module.migration_iam_role["db-migration-lambda-role"].role_arn

  vpc_config {
    subnet_ids         = values(data.terraform_remote_state.common_infra.outputs.private_subnet_ids)
    security_group_ids = [module.migration_sg.security_group_ids["lambda-migration"]]
  }

  environment {
    variables = {
      TEST_ORIGIN_SOURCE_BUCKET = data.terraform_remote_state.prod_infra.outputs.prod_s3_bucket_id
      TEST_TARGET_BUCKET        = module.cloned_s3_bucket.s3_bucket_id

      SOURCE_DB_ENDPOINT = data.terraform_remote_state.prod_infra.outputs.rds_instance_address
      SSM_PARAMETER_PATH = "/prod/MYSQL_"
    }
  }

  depends_on = [
    aws_vpc_endpoint.s3_gateway,
    aws_vpc_endpoint.ssm_interface
  ]

  tags = {
    Name    = "db-migration-task-temporary"
    Purpose = "Ephemeral"
  }
}

resource "aws_db_subnet_group" "migration_rds_subnet_group" {
  name = "db-migration-subnet-group-temporary"

  subnet_ids = values(data.terraform_remote_state.common_infra.outputs.private_subnet_ids)

  tags = {
    Name    = "db-migration-subnet-group-temporary"
    Purpose = "Ephemeral"
  }
}

resource "aws_db_instance" "cloned_rds_for_migration" {
  identifier           = "eatda-rds-clone-for-migration"
  snapshot_identifier  = data.aws_db_snapshot.latest_prod_snapshot.id
  instance_class       = "db.t3.micro"
  db_subnet_group_name = aws_db_subnet_group.migration_rds_subnet_group.name

  vpc_security_group_ids = [module.migration_sg.security_group_ids["cloned-rds"]]

  storage_type      = "gp3"
  allocated_storage = 20
  multi_az          = false
  storage_encrypted = true

  skip_final_snapshot = true
  deletion_protection = false

  tags = {
    Name    = "eatda-rds-clone-for-migration"
    Purpose = "Ephemeral"
  }
}

resource "aws_instance" "jump_host" {
  ami                  = local.jump_host.ami_id
  instance_type        = local.jump_host.instance_type
  key_name             = local.jump_host.key_name
  iam_instance_profile = data.aws_iam_instance_profile.ec2_to_ecs.name

  subnet_id = data.terraform_remote_state.common_infra.outputs.public_subnet_ids["prod"]

  vpc_security_group_ids      = [module.migration_sg.security_group_ids["jump-host"]]
  associate_public_ip_address = true
  user_data                   = local.jump_host.user_data

  tags = {
    Name    = "eatda-jump-host-for-migration"
    Purpose = "Ephemeral"
  }
}

resource "aws_security_group_rule" "allow_ssh_to_jump_host" {
  type              = "ingress"
  from_port         = 22
  to_port           = 22
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = module.migration_sg.security_group_ids["jump-host"]
  description       = "Allow SSH from anywhere for temporary access"
}

resource "aws_ssm_parameter" "temp_migration_rds_url" {
  name  = "/prod/MIGRATION_RDS_URL"
  type  = "SecureString"
  value = "jdbc:mysql://${aws_db_instance.cloned_rds_for_migration.address}/eatda?useUnicode=true&characterEncoding=UTF-8"

  tags = {
    Purpose = "Ephemeral"
  }
}

resource "aws_ssm_parameter" "temp_migration_s3_bucket" {
  name  = "/prod/MIGRATION_S3_BUCKET"
  type  = "String"
  value = module.cloned_s3_bucket.s3_bucket_id

  tags = {
    Purpose = "Ephemeral"
  }
}

resource "aws_ecs_cluster" "migration_cluster" {
  name = "migration-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Name    = "migration-cluster"
    Purpose = "Ephemeral"
  }
}
