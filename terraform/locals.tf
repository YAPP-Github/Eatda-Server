locals {
  backend = "s3"
  bootstrap_remote_state = {
    bucket = "timeeat-prod-terraform-state-ap-northeast-2"
    key    = "bootstrap/terraform.tfstate"
    region = "ap-northeast-2"
  }
}


locals {
  project_name = "time-eat"

  common_tags = {
    Project   = local.project_name
    ManagedBy = "terraform"
  }
}

locals {
  ecs_services         = var.ecs_services
  ecs_task_definitions = var.ecs_task_definitions
}
