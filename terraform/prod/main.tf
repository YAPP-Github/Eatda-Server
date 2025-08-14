resource "random_pet" "rds_user_name" {
  length    = 2
  separator = "_"
}

resource "random_password" "rds_password" {
  length  = 16
  special = true
}

resource "aws_ssm_parameter" "mysql_user_name" {
  name        = "/prod/MYSQL_USER_NAME"
  type        = "SecureString"
  value       = random_pet.rds_user_name.id
  description = "Generated MySQL user name for prod RDS"
  overwrite   = true
}

resource "aws_ssm_parameter" "mysql_password" {
  name        = "/prod/MYSQL_PASSWORD"
  type        = "SecureString"
  value       = random_password.rds_password.result
  description = "Generated MySQL user name for prod RDS"
  overwrite   = true
}

module "ec2" {
  source               = "./ec2"
  ec2_sg_id            = local.ec2_sg_id
  instance_definitions = local.prod_instance_definitions
  instance_subnet_map  = local.instance_subnet_map
  name_prefix          = local.name_prefix
  tags                 = local.common_tags
  depends_on           = [module.s3]
}

module "ecs" {
  source                = "./ecs"
  alb_target_group_arns = local.alb_target_group_arns
  ecr_repo_urls         = local.ecr_repo_urls
  ecs_services          = var.ecs_services
  ecs_task_definitions  = local.ecs_task_definitions
  environment           = local.environment
  tags                  = local.common_tags
}

module "rds" {
  source                  = "./rds"
  vpc_id                  = local.vpc_id
  vpc_cidr                = local.vpc_cidr
  availability_zones      = local.availability_zones
  identifier              = local.identifier
  instance_class          = local.instance_class
  engine                  = local.engine
  engine_version          = local.engine_version
  allocated_storage       = local.allocated_storage
  db_name                 = local.db_name
  username                = random_pet.rds_user_name.id
  password                = random_password.rds_password.result
  vpc_security_group_ids  = local.vpc_security_group_ids
  multi_az                = false
  backup_retention_period = 7
  storage_encrypted       = true
  tags                    = local.common_tags

  depends_on = [
    aws_ssm_parameter.mysql_user_name,
    aws_ssm_parameter.mysql_password
  ]
}

module "s3" {
  source             = "./s3"
  bucket_name_prefix = local.bucket_name_prefix
  environment        = local.environment
  allowed_origins    = local.allowed_origins
}
