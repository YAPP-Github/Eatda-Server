output "cloudfront_domain_name" {
  value     = aws_cloudfront_distribution.dev_cdn.domain_name
  sensitive = true
}

output "s3_bucket_id" {
  value = aws_s3_bucket.dev.id
}

output "s3_bucket_arn" {
  value = aws_s3_bucket.dev.arn
}
