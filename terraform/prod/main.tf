data "aws_ssm_parameter" "rds_user_name" {
  name            = "/prod/mysql-pw"
  with_decryption = true
}

data "aws_ssm_parameter" "rds_password" {
  name            = "/prod/mysql-name"
  with_decryption = true
}

module "ec2" {
  source               = "./ec2"
  ec2_sg_id            = var.ec2_sg_id
  instance_definitions = local.dev_instance_definitions
  instance_subnet_map  = var.instance_subnet_map
  name_prefix          = local.name_prefix
  tags                 = var.tags
}

module "ecs" {
  source                = "./ecs"
  alb_target_group_arns = var.alb_target_group_arns
  ecr_repo_names        = var.ecr_repo_names
  ecs_services          = var.ecs_services
  ecs_task_definitions  = var.ecs_task_definitions
  project_name          = var.project_name
  environment           = local.environment
  tags                  = var.tags
}

module "rds" {
  source                 = "./rds"
  vpc_id                 = var.vpc_id
  vpc_cidr               = var.vpc_cidr
  availability_zones     = var.availability_zones
  identifier             = local.identifier
  instance_class         = local.instance_class
  engine                 = local.engine
  engine_version         = local.engine_version
  allocated_storage      = local.allocated_storage
  username               = data.aws_ssm_parameter.rds_user_name.value
  password               = data.aws_ssm_parameter.rds_password.value
  vpc_security_group_ids = var.vpc_security_group_ids
  multi_az               = false
  storage_encrypted      = true
  tags                   = var.tags
}