resource "aws_s3_bucket" "prod" {
  bucket        = "${var.bucket_name_prefix}-${var.environment}"
  force_destroy = var.force_destroy

  tags = {
    Name        = "${var.bucket_name_prefix}-${var.environment}"
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

resource "aws_s3_object" "app-backup-log-script" {
  bucket       = aws_s3_bucket.prod.bucket
  key          = "scripts/app-backup-prod-logs.sh"
  source       = "${path.module}/scripts/app-backup-prod-logs.sh"
  etag         = filemd5("${path.module}/scripts/app-backup-prod-logs.sh")
  content_type = "text/x-sh"
}

resource "aws_s3_bucket_public_access_block" "prod" {
  bucket = aws_s3_bucket.prod.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "prod" {
  bucket = aws_s3_bucket.prod.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_cors_configuration" "prod" {
  bucket = aws_s3_bucket.prod.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["GET", "PUT"]
    allowed_origins = var.allowed_origins
    expose_headers  = ["ETag"]
    max_age_seconds = 3000
  }
}

resource "aws_s3_bucket_versioning" "prod" {
  bucket = aws_s3_bucket.prod.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "prod_temp" {
  bucket = aws_s3_bucket.prod.id

  rule {
    id     = "expire-temp-objects"
    status = "Enabled"

    filter {
      prefix = "temp/"
    }

    expiration {
      days = 7
    }
  }
}

resource "aws_cloudfront_origin_access_control" "prod_oac" {
  name                              = "oac-for-${aws_s3_bucket.prod.bucket}"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

resource "aws_cloudfront_distribution" "prod_cdn" {
  enabled = true
  comment = "CloudFront for ${var.environment}"

  origin {
    domain_name = aws_s3_bucket.prod.bucket_regional_domain_name
    origin_id   = "S3-${aws_s3_bucket.prod.bucket}"

    origin_access_control_id = aws_cloudfront_origin_access_control.prod_oac.id
  }

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD", "OPTIONS"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "S3-${aws_s3_bucket.prod.bucket}"

    viewer_protocol_policy = "redirect-to-https"
    compress               = true
    cache_policy_id        = "658327ea-f89d-4fab-a63d-7e88639e58f6"
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  tags = {
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

resource "aws_s3_bucket_policy" "main_policy" {
  bucket = aws_s3_bucket.prod.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect    = "Allow",
        Principal = { Service = "cloudfront.amazonaws.com" },
        Action    = "s3:GetObject",
        Resource  = "${aws_s3_bucket.prod.arn}/*",
        Condition = {
          StringEquals = {
            "AWS:SourceArn" = aws_cloudfront_distribution.prod_cdn.arn
          }
        }
      }
    ]
  })
}
