locals {
  default_ecr_lifecycle_policy = jsonencode({
    rules = [
      {
        rulePriority = 1,
        description  = "7일이 지난 마이그레이션 테스트용 임시 이미지 자동 삭제",
        selection = {
          tagStatus     = "tagged",
          tagPrefixList = ["migration-test-"],
          countType     = "sinceImagePushed",
          countUnit     = "days",
          countNumber   = 7
        },
        action = {
          type = "expire"
        }
      }
    ]
  })
}
