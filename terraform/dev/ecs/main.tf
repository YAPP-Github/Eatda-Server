module "cluster" {
  source = "./cluster"
  cluster_name = local.cluster_name
  cluster_settings = local.settings
  namespace_id = var.name_space_id
  tags = var.tags
}

module "service" {
  source = "./service"
  cluster_id = module.cluster.cluster_id
  deployment_controller_type = local.deployment_controller_type
  dev_ecs_services = var.ecs_services
  tags = var.tags
}

module "task" {
  source = "./task"
  container_definitions_map = local.container_definitions_map
  task_definitions = var.ecs_task_definitions
  tags = var.tags
}