module "cloud_map" {
  source                         = "./cloud-map"
  ns_name                        = local.ns_name
  service_discovery_service_name = local.service_discovery_service_name
  ns_failure_threshold           = local.ns_failure_threshold
  ns_type                        = local.ns_type
  ns_ttl                         = local.ns_ttl
  vpc_id                         = var.vpc_id
  tags = local.tags
}

module "ec2" {
  source               = "./ec2"
  ec2_sg_id            = var.ec2_sg_id
  instance_definitions = local.dev_instance_definitions
  instance_subnet_map = var.instance_subnet_map
  name_prefix          = local.name_prefix
  tags = local.tags
}

module "ecs" {
  source                 = "./ecs"
  alb_target_group_arns = var.alb_target_group_arns
  ecr_repo_names = var.ecr_repo_names
  ecs_services = var.ecs_services
  ecs_task_definitions = var.ecs_task_definitions
  name_space_id          = module.cloud_map.namespace_id
  environment            = local.environment
  project_name           = var.project_name
  tags = local.tags
  region                 = var.region
}
