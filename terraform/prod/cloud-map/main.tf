resource "aws_service_discovery_private_dns_namespace" "prod" {
  name = var.ns_name
  vpc  = var.vpc_id
  tags = var.tags
}

resource "aws_service_discovery_service" "api" {
  name = var.service_discovery_service_name
  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.prod.id
    dns_records {
      ttl  = var.ns_ttl
      type = var.ns_type
    }
  }

  health_check_custom_config {
    failure_threshold = var.ns_failure_threshold
  }

  tags = var.tags
}