module "time_eat_dev_cluster" {
  source = "./cluster"
  cluster_name = local.cluster_name
  cluster_settings = local.settings
  namespace_id = var.name_space_id
}

module "time_eat_ecs_service" {
  source = "./service"
  cluster_id = module.time_eat_dev_cluster.cluster_id
  deployment_controller_type = local.deployment_controller_type
  dev_ecs_services = var.ecs_services
}

module "time_eat_ecs_task" {
  source = "./task"
  container_definitions_map = local.container_definitions_map
  task_definitions = var.ecs_task_definitions
}