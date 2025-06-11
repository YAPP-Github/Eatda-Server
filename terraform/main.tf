data "terraform_remote_state" "bootstrap" {
  backend = local.backend
  config = {
    bucket = local.bootstrap_remote_state.bucket
    key    = local.bootstrap_remote_state.key
    region = local.bootstrap_remote_state.region
  }
}

module "common" {
  source       = "./common"
  project_name = local.project_name
}

module "dev" {
  source                = "./dev"
  project_name          = local.project_name
  alb_target_group_arns = module.common.target_group_arns
  ec2_sg_id             = module.common.security_group_ids["ec2"]
  ecr_repo_names        = data.terraform_remote_state.bootstrap.outputs.ecr_repo_names
  ecs_services          = local.ecs_services
  ecs_task_definitions  = local.ecs_task_definitions
  instance_subnet_map   = module.common.public_subnet_ids
  vpc_id                = module.common.vpc_id
  tags                  = local.common_tags
  region                = local.region
}

module "prod_module" {
  source                = "./prod"
  project_name          = local.project_name
  vpc_id                = module.common.vpc_id
  private_subnet_ids = [module.common.private_subnet_ids.prod]
  vpc_security_group_ids = [module.common.security_group_ids["rds"]]
  alb_target_group_arns = module.common.target_group_arns
  ec2_sg_id             = module.common.security_group_ids["ec2"]
  ecr_repo_names        = data.terraform_remote_state.bootstrap.outputs.ecr_repo_names
  ecs_services          = local.ecs_services
  ecs_task_definitions  = local.ecs_task_definitions
  instance_subnet_map   = module.common.public_subnet_ids.prod
  tags                  = local.common_tags
}