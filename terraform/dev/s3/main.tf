resource "aws_s3_bucket" "dev" {
  bucket = "${var.bucket_name_prefix}-${var.environment}"

  tags = {
    Name        = "${var.bucket_name_prefix}-${var.environment}"
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

resource "aws_s3_object" "app-backup-log-script" {
  bucket       = aws_s3_bucket.dev.bucket
  key          = "scripts/app-backup-dev-logs.sh"
  source       = "${path.module}/scripts/app-backup-dev-logs.sh"
  etag = filemd5("${path.module}/scripts/app-backup-dev-logs.sh")
  content_type = "text/x-sh"
}

resource "aws_s3_object" "mysql-backup-script" {
  bucket       = aws_s3_bucket.dev.bucket
  key          = "scripts/mysql-backup.sh"
  source       = "${path.module}/scripts/mysql-backup.sh"
  etag = filemd5("${path.module}/scripts/mysql-backup.sh")
  content_type = "text/x-sh"
}

resource "aws_s3_bucket_public_access_block" "dev" {
  bucket = aws_s3_bucket.dev.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "dev" {
  bucket = aws_s3_bucket.dev.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_cors_configuration" "dev" {
  bucket = aws_s3_bucket.dev.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["GET"]
    allowed_origins = var.allowed_origins
    expose_headers = ["ETag"]
    max_age_seconds = 3000
  }
}
