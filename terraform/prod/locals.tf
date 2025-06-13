data "terraform_remote_state" "bootstrap" {
  backend = "s3"

  config = {
    bucket = "timeeat-tf-state"
    key    = "bootstrap/terraform.tfstate"
    region = "ap-northeast-2"
  }
}

locals {
  environment = "prod"
  name_prefix = "time-eat"

  tags = merge(var.tags, {
    Environment = local.environment
  })
}

locals {
  dev_instance_definitions = {
    ami                  = "ami-012ea6058806ff688"
    instance_type        = "t3.micro"
    role                 = "prod"
    iam_instance_profile = "ec2-to-ecs"
    key_name             = "time-eat-ec2-prod-key"
    user_data            = <<-EOF
#!/bin/bash
echo ECS_CLUSTER=time-eat-prod-cluster >> /etc/ecs/ecs.config
EOF
  }
}

locals {
  ecs_services = var.ecs_services
}

locals {
  ecs_task_definitions = {
    for k, v in var.ecs_task_definitions :
    k => k == "api" ?
      merge(v, { container_image = "${data.terraform_remote_state.bootstrap.outputs.ecr_repo_names["dev"]}:latest" }) :
      v
  }
}

locals {
  allocated_storage = 20
  identifier        = "${var.project_name}-${local.environment}-db"
  instance_class    = "db.t3.micro"
  engine_version    = "8.0.42"
  engine            = "MySQL"
  storage_encrypted = true
  multi_az          = false
}
