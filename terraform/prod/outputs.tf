output "ecr_repository_name" {
  value = data.terraform_remote_state.bootstrap.outputs.ecr_repo_names["prod"]
}

output "ecs_cluster_name" {
  value = module.ecs.cluster_name
}

output "ecs_api_service_name" {
  value = module.ecs.service_names["api-prod"]
}

output "ecs_api_container_name" {
  value = module.ecs.container_names["api-prod"]
}

output "rds_endpoint" {
  description = "RDS endpoint address"
  value       = module.rds.endpoint
  sensitive   = true
}

output "rds_arn" {
  description = "RDS instance ARN"
  value       = module.rds.arn
  sensitive   = true
}

output "rds_instance_identifier" {
  description = "The identifier of the production RDS instance."
  value       = module.rds.rds_instance_identifier
}

output "rds_instance_id" {
  value     = module.rds.rds_instance_id
  sensitive = true
}

output "rds_instance_address" {
  description = "The endpoint address of the production RDS instance."
  value       = module.rds.rds_instance_address
  sensitive   = true
}

output "prod_s3_bucket_id" {
  value = module.s3.s3_bucket_id
}

output "prod_s3_bucket_arn" {
  value = module.s3.s3_bucket_arn
}
