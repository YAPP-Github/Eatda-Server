output "task_definition_arns" {
  value = {
    for k, task in aws_ecs_task_definition.dev :
    k => task.arn
  }
}

output "container_names" {
  value = {
    for k, v in aws_ecs_task_definition.dev : k => v.family
  }
}
