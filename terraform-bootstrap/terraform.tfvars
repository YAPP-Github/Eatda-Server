ecr_repositories = {
  timeeat_prod = {
    scan_on_push         = false
    image_tag_mutability = "MUTABLE"
    tags = {
      Service = "prod"
    }
  }

  timeeat_dev = {
    scan_on_push         = false
    image_tag_mutability = "MUTABLE"
    tags = {
      Service = "dev"
    }
  }
}
