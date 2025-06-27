locals {
  cluster_name        = "${var.environment}-cluster"
  launch_type         = "EC2"
  scheduling_strategy = "DAEMON"

  settings = {
    name  = "containerInsights"
    value = "disabled"
  }

  deployment_controller_type = "ECS"

  resolved_task_definitions = {
    for name, def in var.ecs_task_definitions :
    name => name == "api-dev" ? merge(def, {
      task_definition_name = "api-dev"
      container_image      = "${var.ecr_repo_urls["dev"]}:placeholder"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
      environment = {}
      secrets = []
    }) : name == "mysql-dev" ? merge(def, {
      task_definition_name = "mysql-dev"
      container_image      = "mysql:8"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
      environment = {
        MYSQL_DATABASE = "eatda"
      }
      secrets = [
        {
          name      = "MYSQL_USER"
          valueFrom = "/dev/MYSQL_USER_NAME"
        },
        {
          name      = "MYSQL_ROOT_PASSWORD"
          valueFrom = "/dev/MYSQL_ROOT_PASSWORD"
        },
        {
          name      = "MYSQL_PASSWORD"
          valueFrom = "/dev/MYSQL_PASSWORD"
        }
      ]
    }) : merge(def, {
      task_definition_name = "dummy"
      container_image      = "dummy"
      task_role_arn        = def.task_role_arn
      execution_role_arn   = def.execution_role_arn
      environment = {
        MYSQL_DATABASE = "dummy"
      }
      secrets = [
        {
          name      = "DUMMY"
          valueFrom = "/dummy"
        },
        {
          name      = "DUMMY"
          valueFrom = "/dummy"
        },
        {
          name      = "DUMMY"
          valueFrom = "/dummy"
        }
      ]
    })
  }

  resolved_ecs_services = {
    for name, def in var.ecs_services : name => {
      name         = name
      iam_role_arn = var.ecs_task_definitions[name].task_role_arn
      load_balancer = try(def.load_balancer, null)
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
        command   = svc == "api-dev" ? [
          "java",
          "-Dspring.profiles.active=dev",
          "-jar",
          "/api.jar"
        ] : null

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
            containerPath = lookup(var.volume_mount_paths, vol.name, "/eatda")
            readOnly     = false
          }
        ]
      }
    ]
  }

  final_task_definitions = {
    for key, task_def in local.resolved_task_definitions : key => merge(
      task_def,
      {
        container_definitions = local.container_definitions_map[key]
      }
    )
  }
}
