output "cloudfront_domain_name" {
  value     = aws_cloudfront_distribution.dev_cdn.domain_name
  sensitive = true
}
