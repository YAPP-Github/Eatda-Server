module "ec2" {
  source               = "./ec2"
  ec2_sg_id            = var.ec2_sg_id
  instance_definitions = local.dev_instance_definitions
  instance_subnet_map  = var.instance_subnet_map
  name_prefix          = local.name_prefix
  tags                 = local.tags
}

module "ecs" {
  source                = "./ecs"
  alb_target_group_arns = var.alb_target_group_arns
  ecr_repo_names        = var.ecr_repo_names
  ecs_services          = var.ecs_services
  ecs_task_definitions  = var.ecs_task_definitions
  environment           = local.environment
  tags                  = local.tags
}
