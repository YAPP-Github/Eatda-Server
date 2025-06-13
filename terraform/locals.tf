locals {
  backend = "s3"
  bootstrap_remote_state = {
    bucket = "timeeat-tf-state"
    key    = "bootstrap/terraform.tfstate"
    region = "ap-northeast-2"
  }
}


locals {
  project_name = "time-eat"
  region       = "ap-northeast-2"
  ecr_repo_names = {
    dev = "dev"
    prod = "prod"
  }
  common_tags = {
    Project   = local.project_name
    ManagedBy = "terraform"
  }
}

locals {
  ecs_services         = var.ecs_services
}

locals {
  ecs_task_definitions = {
    for k, v in var.ecs_task_definitions : k => merge(v, {
      execution_role_arn = module.common.role_arn["ecsTaskExecutionRole"]
      task_role_arn      = module.common.role_arn["ecsAppTaskRole"]
    })
  }
}