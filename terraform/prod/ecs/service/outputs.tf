output "ecs_service_names" {
  value = {
    for k, v in aws_ecs_service.prod : k => v.name
  }
}