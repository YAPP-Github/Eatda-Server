resource "aws_ecs_service" "ecs_dev_services" {
  for_each = var.dev_ecs_services

  name                = each.key
  cluster             = var.cluster_id
  launch_type         = each.value.launch_type
  task_definition     = each.value.task_definition
  desired_count       = each.value.desired_count
  scheduling_strategy = each.value.scheduling_strategy

  deployment_controller {
    type = var.deployment_controller_type
  }

  tags = {
    Name = each.key
  }
}
