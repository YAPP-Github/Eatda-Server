output "ecs_task_definitions_check" {
  value = local.ecs_task_definitions
}

output "ecr_repository_names" {
  value = data.terraform_remote_state.bootstrap.outputs.ecr_repo_names["dev"]
}