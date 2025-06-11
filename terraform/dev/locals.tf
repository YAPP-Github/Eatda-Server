data "terraform_remote_state" "bootstrap" {
  backend = "s3"

  config = {
    bucket = "timeeat-tf-state"
    key    = "bootstrap/terraform.tfstate"
    region = "ap-northeast-2"
  }
}

locals {
  environment                    = "dev"
  ns_name                        = "time-eat.com"
  ns_type                        = "A"
  service_discovery_service_name = "api"
  ns_failure_threshold           = 2
  ns_ttl                         = 10
  name_prefix                    = "time_eat"

  tags = merge(var.tags, {
    Environment = local.environment
  })
}

locals {
  dev_instance_definitions = {
    ami                  = "ami-012ea6058806ff688"
    instance_type        = "t2.micro"
    role                 = "dev"
    iam_instance_profile = "ec2-to-ecs"
    key_name             = "ec2-dev-key"
    user_data            = <<-EOF
#!/bin/bash
echo ECS_CLUSTER=time-eat-prod-cluster >> /etc/ecs/ecs.config
EOF
  }
}

locals {
  ecs_services         = var.ecs_services
}

locals {
  ecs_task_definitions = {
    for k, v in var.ecs_task_definitions : k => merge(v, {
      container_image          = data.terraform_remote_state.bootstrap.outputs.ecr_repo_names[k]
    })
  }
}
