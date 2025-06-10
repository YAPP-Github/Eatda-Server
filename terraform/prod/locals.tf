locals {
  environment = "prod"
}

locals {
  ns_name                        = "time-eat.com"
  ns_type                        = "A"
  service_discovery_service_name = "api"
  ns_failure_threshold           = 2
  ns_ttl                         = 10

  tags = merge(var.tags, {
    Environment = local.environment
  })
}

locals {
  dev_instance_definitions = {
    ami                  = "ami-012ea6058806ff688"
    instance_type        = "t3.micro"
    iam_instance_profile = "ec2-to-ecs"
    key_name             = "time-eat-prod-key"
    user_data            = <<-EOF
#!/bin/bash
echo ECS_CLUSTER=time-eat-prod-cluster >> /etc/ecs/ecs.config
EOF
  }
}

locals {
  ecs_services = {
    task_definition_name = "a"
    desired_count        = 1
    load_balancer = {
      target_group_key = string
      container_name   = string
      container_port   = number
    }
  }
}

locals {
  allocated_storage = 20
  identifier = "${var.project_name}-${local.environment}-db"
  instance_class = "db.t3.micro"
  engine_version = "8.0.42"
  engine = "MySQL"
  storage_encrypted = true
  multi_az = false
}
