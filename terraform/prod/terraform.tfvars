ecs_services = {
  api-prod = {
    task_definition = "api-prod"
    load_balancer = {
      target_group_key = "api-prod"
      container_name   = "api-prod"
      container_port   = 8080
    }
  }

  datadog = {
    task_definition     = "datadog"
    launch_type         = "EC2"
    scheduling_strategy = "DAEMON"
  }
}

ecs_task_definitions_base = {
  api-prod = {
    cpu            = 1500
    memory         = 1024
    network_mode   = "host"
    container_port = [8080]
    host_port      = [8080]
    log_group      = "/ecs/eatda-api"
    environment    = {}
    volumes = [
      {
        name      = "prod-api-volume"
        host_path = "/home/ec2-user/logs/"
      }
    ]
  }

  datadog = {
    container_image          = "public.ecr.aws/datadog/agent:latest"
    cpu                      = 256
    memoryReservation        = 256
    memory                   = 512
    network_mode             = "host"
    container_port           = [8125, 8126]
    host_port                = [8125, 8126]
    requires_compatibilities = ["EC2"]
    environment = {
      DD_SITE                              = "datadoghq.com"
      DD_PROCESS_AGENT_ENABLED             = "true"
      DD_APM_ENABLED                       = "true"
      DD_LOGS_ENABLED                      = "true"
      DD_LOGS_CONFIG_CONTAINER_COLLECT_ALL = "true"
      DD_APM_RECEIVER_PORT                 = "8126"
      DD_APM_NON_LOCAL_TRAFFIC             = "true"
      DD_EC2_USE_IMDSV2                    = "true"
      DD_COLLECT_EC2_TAGS                  = "true"
      DD_COLLECT_EC2_METADATA              = "true"
      DD_SERVICE                           = "eatda-api-prod"
      DD_ENV                               = "prod"
      DD_VERSION                           = "v1"
    }
    volumes = [
      {
        name      = "docker_sock"
        host_path = "/var/run/docker.sock"
      },
      {
        name      = "proc"
        host_path = "/proc"
      },
      {
        name      = "cgroup"
        host_path = "/sys/fs/cgroup"
      }
    ]
    family = "datadog-agent-task"
  }
}
