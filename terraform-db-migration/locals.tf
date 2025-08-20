data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

locals {
  iam_roles = {
    "db-migration-lambda-role" = {
      assume_role_services = ["lambda.amazonaws.com"]
      policy_arns = [
        "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
      ]
      custom_inline_policies = {
        s3_ssm_access_for_migration = {
          name        = "s3-ssm-access-for-migration-lambda"
          description = "Allow S3 object and SSM parameter access for DB migration"
          policy_document = {
            Version = "2012-10-17"
            Statement = [
              {
                Effect = "Allow",
                Action = ["s3:GetObject", "s3:PutObject", "s3:CopyObject", "s3:HeadObject"],
                Resource = [
                  // TODO 테스트 버킷으로 명시, 테스트 후 ssm에서 동적으로 가져오게 수정
                  "arn:aws:s3:::test-bucket/*"
                ]
              },
              {
                Effect = "Allow",
                Action = "ssm:GetParameter",
                Resource = [
                  "arn:aws:ssm:${data.aws_region.current.id}:${data.aws_caller_identity.current.account_id}:parameter/prod/MYSQL_*"
                ]
              }
            ]
          }
        }
      }
      tags = {
        Purpose = "DB Migration Lambda Execution"
      }
    }
  }
}

locals {
  security_groups = {
    "lambda-migration" = {
      name        = "eatda-lambda-migration-sg-temp"
      description = "Temporary SG for DB Migration Lambda"
      tags        = { Name = "eatda-lambda-migration-sg-temp", Purpose = "Ephemeral" }
    },
    "cloned-rds" = {
      name        = "eatda-cloned-rds-sg-temp"
      description = "Temporary SG for Cloned RDS instance"
      tags        = { Name = "eatda-cloned-rds-sg-temp", Purpose = "Ephemeral" }
    }
    "jump-host" = {
      name        = "eatda-jump-host-sg-temp"
      description = "Temporary SG for Jump Host EC2"
      tags        = { Name = "eatda-jump-host-sg-temp", Purpose = "Ephemeral" }
    }
  }

  egress_rules = {
    "lambda_allow_all_outbound" = {
      security_group_key = "lambda-migration"
      from_port          = 0
      to_port            = 0
      protocol           = "-1"
      cidr_blocks        = ["0.0.0.0/0"]
      description        = "Allow all outbound traffic from Lambda"
    }
    "rds_allow_all_outbound" = {
      security_group_key = "cloned-rds"
      from_port          = 0
      to_port            = 0
      protocol           = "-1"
      cidr_blocks        = ["0.0.0.0/0"]
      description        = "Allow all outbound traffic from Cloned RDS"
    }
    "jump_host_allow_all_outbound" = {
      security_group_key = "jump-host"
      from_port          = 0
      to_port            = 0
      protocol           = "-1"
      cidr_blocks        = ["0.0.0.0/0"]
      description        = "Allow all outbound traffic from Jump Host"
    }
  }

  cross_reference_rules = {
    "cloned_rds_from_lambda" = {
      source_security_group_key = "lambda-migration"
      target_security_group_key = "cloned-rds"
      from_port                 = 3306
      to_port                   = 3306
      protocol                  = "tcp"
      description               = "Allow MySQL from Migration Lambda to Cloned RDS"
    }
    "cloned_rds_from_jump_host" = {
      source_security_group_key = "jump-host"
      target_security_group_key = "cloned-rds"
      from_port                 = 3306
      to_port                   = 3306
      protocol                  = "tcp"
      description               = "Allow MySQL from Jump Host to Cloned RDS"
    }
  }
}

locals {
  cloned_s3_bucket_prefix   = "eatda-dev-clone"
  cloned_s3_environment     = "migration-test"
  cloned_s3_allowed_origins = ["https://*.example.com"]
}

locals {
  jump_host = {
    key_name      = "eatda-dev-jump-key"
    instance_type = "t2.nano"
    ami_id        = "ami-012ea6058806ff688"
  }
}
