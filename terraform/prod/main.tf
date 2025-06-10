data "aws_ssm_parameter" "rds_user_name" {
  name            = "/rds-user-name"
  with_decryption = true
}

data "aws_ssm_parameter" "rds_password" {
  name            = "/rds-password"
  with_decryption = true
}

module "cloud_map" {
  source                         = "./cloud-map"
  ns_name                        = local.ns_name
  service_discovery_service_name = local.service_discovery_service_name
  ns_failure_threshold           = local.ns_failure_threshold
  ns_type                        = local.ns_type
  ns_ttl                         = local.ns_ttl
  vpc_id                         = var.vpc_id
  tags                           = local.tags
}

module "ec2" {
  source               = "./ec2"
  ec2_sg_id            = var.ec2_sg_id
  instance_definitions = local.dev_instance_definitions
  instance_subnet_map  = var.instance_subnet_map
  name_prefix          = local.environment
  tags                 = local.tags
}

module "ecs" {
  source                = "./ecs"
  alb_target_group_arns = var.alb_target_group_arns
  ecr_repo_names        = var.ecr_repo_names
  ecs_services          = var.ecs_services
  ecs_task_definitions  = var.ecs_task_definitions
  name_space_id         = module.cloud_map.namespace_id
  ecs_unified_role_arn  = var.unified_role_arn
  project_name          = var.project_name
  environment           = local.environment
  tags                  = local.tags
}

module "rds" {
  source            = "./rds"
  identifier        = local.identifier
  instance_class    = local.instance_class
  engine            = local.engine
  engine_version    = local.engine_version
  allocated_storage = local.allocated_storage
  username          = data.aws_ssm_parameter.rds_user_name.value
  password          = data.aws_ssm_parameter.rds_password.value
  private_subnet_ids = var.private_subnet_ids
  vpc_security_group_ids = var.vpc_security_group_ids
  multi_az          = false
  storage_encrypted = true
  tags              = local.tags
}