resource "aws_ecs_service" "dev" {
  for_each = var.ecs_services

  name                = each.key
  cluster             = var.cluster_id
  launch_type         = var.launch_type
  task_definition     = var.task_definition_arn
  desired_count       = each.value.desired_count
  scheduling_strategy = var.scheduling_strategy

  load_balancer {
    target_group_arn = var.alb_target_group_arns[each.value.load_balancer.target_group_key]
    container_name   = each.value.load_balancer.container_name
    container_port   = each.value.load_balancer.container_port
  }

  deployment_controller {
    type = var.deployment_controller_type
  }

  tags = merge(var.tags, {
    Service = each.key
  })
}
