variable "repository_name" {
  type = string
}

variable "scan_on_push" {
  type = bool
}

variable "image_tag_mutability" {
  type = string
}

variable "tags" {
  type = map(string)
}

variable "migration_test_ecr_lifecycle_policy" {
  type = any
}
