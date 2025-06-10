resource "aws_ecs_service" "prod" {
  for_each = var.service

  name                = each.key
  cluster             = var.cluster_id
  launch_type         = each.value.launch_type
  task_definition     = each.value.task_definition
  desired_count       = each.value.desired_count
  scheduling_strategy = each.value.scheduling_strategy

  deployment_controller {
    type = var.deployment_controller_type
  }

  tags = merge(var.tags, {
    Service = each.key
  })
}
