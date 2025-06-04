ecr_repositories = {
  issuefy-was = {
    scan_on_push         = false
    image_tag_mutability = "MUTABLE"
    tags = {
      Service = "was"
    }
  }
}
