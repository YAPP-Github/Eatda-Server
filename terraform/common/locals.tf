locals {
  group_name = "power"

  policy_arns = [
    "arn:aws:iam::aws:policy/AdministratorAccess",
    "arn:aws:iam::aws:policy/AmazonElasticContainerRegistryPublicPowerUser",
    "arn:aws:iam::aws:policy/AmazonS3FullAccess",
    "arn:aws:iam::aws:policy/ElasticLoadBalancingReadOnly"
  ]

  user = ["roy-test"]
}

locals {
  iam_roles = {
    "ec2-to-ecs" = {
      assume_role_services = ["ec2.amazonaws.com"]
      policy_arns = [
        "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role",
        "arn:aws:iam::aws:policy/AmazonEC2ReadOnlyAccess",
        "arn:aws:iam::aws:policy/AmazonECS_FullAccess"
      ]
      tags = {
        Purpose = "ECS EC2 Registration"
      }
    }

    "ecsTaskExecutionRole" = {
      assume_role_services = ["ecs-tasks.amazonaws.com"]
      policy_arns = [
        "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
      ]
      tags = {
        Purpose = "ECS Task Execution"
      }
    }
  }
}

locals {
  domain_name       = "timeeat.site"
  validation_method = "DNS"
  recode_type       = "A"
  subdomains = [
    "prod.timeeat.site",
    "dev.timeeat.site"
  ]
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
        },
        {
          description              = "HTTP"
          from_port                = 8080
          to_port                  = 8080
          protocol                 = "tcp"
          source_security_group_id = module.security_group["alb"].security_group_ids
        }
      ]
    }

    rds = {
      name        = "timeeat-rds-sg"
      description = "RDS SG"
      ingress = [
        {
          description = "MySQL"
          from_port   = 3306
          to_port     = 3306
          protocol    = "tcp"
          cidr_blocks = module.timeeat_vpc.vpc_cidr_block
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
}



