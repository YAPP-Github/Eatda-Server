output "cloudfront_domain_name" {
  value     = aws_cloudfront_distribution.prod_cdn.domain_name
  sensitive = true
}

output "s3_bucket_id" {
  value = aws_s3_bucket.prod.id
}

output "s3_bucket_arn" {
  value = aws_s3_bucket.prod.arn
}
