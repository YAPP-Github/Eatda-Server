resource "aws_ecr_repository" "common" {
  name                 = var.repository_name
  image_tag_mutability = var.image_tag_mutability

  image_scanning_configuration {
    scan_on_push = var.scan_on_push
  }

  tags = var.tags
}

resource "aws_ecr_lifecycle_policy" "migration_test_image_cleanup" {
  repository = aws_ecr_repository.common.name
  policy     = var.migration_test_ecr_lifecycle_policy
}
