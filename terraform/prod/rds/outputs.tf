output "endpoint" {
  description = "RDS endpoint address"
  value       = aws_db_instance.prod.endpoint
}

output "arn" {
  description = "RDS instance ARN"
  value       = aws_db_instance.prod.arn
}

output "rds_instance_identifier" {
  description = "The identifier of the production RDS instance."
  value       = aws_db_instance.prod.identifier
}

output "rds_instance_id" {
  value = aws_db_instance.prod.id
}

output "rds_instance_address" {
  description = "The endpoint address of the production RDS instance."
  value       = aws_db_instance.prod.address
}
