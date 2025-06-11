locals {
  group_name   = "power"
  project_name = var.project_name

  policy_arns = [
    "arn:aws:iam::aws:policy/AdministratorAccess",
    "arn:aws:iam::aws:policy/AmazonElasticContainerRegistryPublicPowerUser",
    "arn:aws:iam::aws:policy/AmazonS3FullAccess",
    "arn:aws:iam::aws:policy/ElasticLoadBalancingReadOnly"
  ]

  user = ["roy-test", "leegwichan"]
}

locals {
  iam_roles = {
    "ec2-to-ecs" = {
      assume_role_services = ["ec2.amazonaws.com"]
      policy_arns = [
        "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role",
        "arn:aws:iam::aws:policy/AmazonEC2ReadOnlyAccess",
        "arn:aws:iam::aws:policy/AmazonECS_FullAccess",
        "arn:aws:iam::aws:policy/AmazonS3FullAccess",
      ]
      tags = {
        Purpose = "ECS EC2 Registration"
      }
    }

    "ecsTaskExecutionRole" = {
      assume_role_services = ["ecs-tasks.amazonaws.com"]
      policy_arns = [
        "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy",
        "arn:aws:iam::aws:policy/AmazonS3FullAccess",
        "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy",
        "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly",
        "arn:aws:iam::aws:policy/CloudWatchLogsFullAccess"
      ]
      tags = {
        Purpose = "ECS Task Execution"
      }
    }
  }
}

locals {
  domain_name       = "time-eat.com"
  validation_method = "DNS"
  recode_type       = "A"
  alb_alias = {
    alb_dns_name = module.alb.alb_dns_name
    alb_zone_id  = module.alb.alb_zone_id
  }

  subdomains = {
    prod = local.alb_alias
    dev  = local.alb_alias
  }
}

locals {
  all_egress = [
    {
      description = "Allow all"
      from_port   = 0
      to_port     = 0
      protocol    = "-1"
      cidr_blocks = ["0.0.0.0/0"]
    }
  ]

  security_group_defs = {
    ec2 = {
      name        = "timeeat-ec2-sg"
      description = "EC2 SG"
      ingress = [
        {
          description = "SSH"
          from_port   = 22
          to_port     = 22
          protocol    = "tcp"
          cidr_blocks = ["0.0.0.0/0"]
        }
      ]
    }

    rds = {
      name        = "timeeat-rds-sg"
      description = "RDS SG"
      ingress = [
        {
          description = "MySQL from VPC"
          from_port   = 3306
          to_port     = 3306
          protocol    = "tcp"
          cidr_blocks = [module.vpc.vpc_cidr_block]
        }
      ]
    }

    alb = {
      name        = "timeeat-alb-sg"
      description = "ALB SG"
      ingress = [
        {
          description = "HTTP"
          from_port   = 80
          to_port     = 80
          protocol    = "tcp"
          cidr_blocks = ["0.0.0.0/0"]
        },
        {
          description = "HTTPS"
          from_port   = 443
          to_port     = 443
          protocol    = "tcp"
          cidr_blocks = ["0.0.0.0/0"]
        }
      ]
    }
  }

  security_group_cross_refs = {
    "ec2_from_alb" = {
      from_port                = 8080
      to_port                  = 8080
      protocol                 = "tcp"
      source_security_group_id = module.security_group["alb"].security_group_id
      target_security_group_id = module.security_group["ec2"].security_group_id
      description              = "HTTP from ALB"
    }

    "rds_from_ec2" = {
      from_port                = 3306
      to_port                  = 3306
      protocol                 = "tcp"
      source_security_group_id = module.security_group["ec2"].security_group_id
      target_security_group_id = module.security_group["rds"].security_group_id
      description              = "MySQL from EC2"
    }
  }
}




