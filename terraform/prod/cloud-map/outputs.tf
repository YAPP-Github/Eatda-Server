output "namespace_id" {
  value = aws_service_discovery_private_dns_namespace.prod.id
}

output "namespace_arn" {
  value = aws_service_discovery_private_dns_namespace.prod.arn
}