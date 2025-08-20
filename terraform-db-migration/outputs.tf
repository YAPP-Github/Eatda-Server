output "ecr_repository_name" {
  description = "The name of the ECR repository."
  value       = data.terraform_remote_state.common_infra.outputs.ecr_repository_name
}

output "prod_ecs_cluster_name" {
  description = "The name of the production ECS cluster."
  value       = data.terraform_remote_state.prod_infra.outputs.ecs_cluster_name
}

output "prod_ecs_api_service_name" {
  description = "The name of the production ECS API service."
  value       = data.terraform_remote_state.prod_infra.outputs.ecs_api_service_name
}

output "migration_lambda_function_name" {
  description = "The name of the Lambda function for pre/post-migration tasks."
  value       = aws_lambda_function.migration_task.function_name
}

output "private_subnet_id" {
  description = "The ID of the Private Subnet where the ECS Task will run (first from the list)."
  value       = values(data.terraform_remote_state.common_infra.outputs.private_subnet_ids)[0]
}

output "task_security_group_id" {
  description = "The ID of the Security Group to be applied to the ECS Task."
  value       = module.migration_sg.security_group_ids["ecs-task-migration"]
}

output "cloned_rds_address" {
  description = "The endpoint address of the cloned RDS instance."
  value       = aws_db_instance.cloned_rds_for_migration.address
}

output "cloned_s3_bucket_name" {
  description = "The name of the cloned S3 bucket."
  value       = module.cloned_s3_bucket.s3_bucket_id
}
