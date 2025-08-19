output "cloudfront_domain_name" {
  value     = aws_cloudfront_distribution.prod_cdn.domain_name
  sensitive = true
}
