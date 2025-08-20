variable "ecr_repositories" {
  type = map(object({
    scan_on_push         = bool
    image_tag_mutability = string
    tags                 = map(string)
  }))
}

variable "migration_test_ecr_lifecycle_policy" {
  type = any
}
