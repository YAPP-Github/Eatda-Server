output "ecr_repo_names" {
  description = "Map of created ECR repository names"
  value = {
    for name, mod in module.ecr :
    name => mod.repository_url
  }
}
