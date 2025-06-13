data "aws_caller_identity" "current" {}

locals {
  cluster_name        = "${var.environment}-cluster"
  launch_type         = "EC2"
  scheduling_strategy = "REPLICA"

  settings = {
    name  = "containerInsights"
    value = "disabled"
  }

  deployment_controller_type = "ECS"

  resolved_task_definitions = {
    for name, def in var.ecs_task_definitions :
    name => name == "api" ? merge(def, {
      task_definition_name = "time-eat-prod"
      container_image      = "${var.ecr_repo_names["prod"]}:latest"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
      environment = {}
      secrets = []
    }) : name == "datadog" ? merge(def, {
      task_definition_name = "datadog-agent"
      container_image      = "public.ecr.aws/datadog/agent:latest"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
      environment = {
        DD_SITE = "datadoghq.com"
        DD_PROCESS_AGENT_ENABLED = "true"
        DD_APM_ENABLED = "true"
        DD_LOGS_ENABLED = "true"
        DD_LOGS_CONFIG_CONTAINER_COLLECT_ALL = "true"
        DD_APM_RECEIVER_PORT = "8126"
        DD_APM_NON_LOCAL_TRAFFIC = "true"
      }
      secrets = [
        {
          name      = "DD_API_KEY"
          valueFrom = "/prod/DD_API_KEY"
        }
      ]
    }) : merge(def, {
      task_definition_name = "dummy"
      container_image      = "dummy"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
      environment = {}
      secrets = [
        {
          name      = "DUMMY"
          valueFrom = "/dummy"
        }
      ]
    })
  }

  resolved_ecs_services = {
    for name, def in var.ecs_services : name => {
      name                = name
      launch_type         = def.launch_type
      task_definition     = def.task_definition
      desired_count       = def.desired_count
      scheduling_strategy = def.scheduling_strategy
      iam_role_arn        = var.ecs_task_definitions[name].task_role_arn
      load_balancer       = try(def.load_balancer, null)
    }
  }

  container_definitions_map = {
    for svc, def in local.resolved_task_definitions : svc => [
      {
        name      = def.task_definition_name
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

        secrets = [
          for s in def.secrets : {
            name      = s.name
            valueFrom = s.valueFrom
          }
        ]

        mountPoints = [
          for vol in (def.volumes != null ? def.volumes : []) : {
            sourceVolume = vol.name
            containerPath = lookup(var.volume_mount_paths, vol.name, "/time-eat")
            readOnly     = false
          }
        ]
      }
    ]
  }
}
