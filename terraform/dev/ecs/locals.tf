data "aws_caller_identity" "current" {}

locals {
  cluster_name = "${var.project_name}-${var.environment}-cluster"
  launch_type  = "EC2"
  scheduling_strategy = "REPLICA"

  settings = {
    name  = "containerInsights"
    value = "disabled"
  }

  deployment_controller_type = "ECS"

  ecr_repo_urls = {
    for name, repo in var.ecr_repo_names :
    name => "${data.aws_caller_identity.current.account_id}.dkr.ecr.ap-northeast-2.amazonaws.com/${repo}"
  }

  resolved_task_definitions = {
    for name, def in var.ecs_task_definitions : name => merge(def, {
      task_definition_name = "time-eat-dev-api"
      container_image      = "${local.ecr_repo_urls[name]}:latest"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
    })
  }

  resolved_ecs_services = {
    for name, def in var.ecs_services : name => {
      name          = name
      desired_count = def.desired_count
      iam_role_arn  = var.ecs_task_definitions[name].task_role_arn
      load_balancer = {
        target_group_arn = var.alb_target_group_arns[def.load_balancer.target_group_key]
        container_name   = def.load_balancer.container_name
        container_port   = def.load_balancer.container_port
      }
    }
  }

  container_definitions_map = {
    for svc, def in local.resolved_task_definitions : svc => [
      {
        name      = "${var.name_prefix}-${svc}"
        image     = def.container_image
        cpu       = def.cpu
        memory    = def.memory
        essential = true
        stopTimeout = lookup(def, "stop_timeout", var.default_stop_timeout)

        portMappings = [
          for idx, port in def.container_port : {
            name          = "${svc}-${port}-tcp"
            containerPort = port
            hostPort      = def.host_port[idx]
            protocol = lookup(def, "protocol", var.default_protocol)
          }
        ]

        environment = [
          for k, v in def.environment : {
            name  = k
            value = v
          }
        ]

        logConfiguration = {
          logDriver = "awslogs"
          options = {
            awslogs-group         = def.log_group
            awslogs-create-group  = "true"
            awslogs-region        = var.region
            awslogs-stream-prefix = var.log_stream_prefix
          }
        }

        mountPoints = [
          for vol in (def.volumes != null ? def.volumes : []) : {
            sourceVolume = vol.name
            containerPath = lookup(var.volume_mount_paths, vol.name, "/logs")
            readOnly     = false
          }
        ]
      }
    ]
  }
}
