ecs_services = {
  api = {
    name                = "api"
    task_definition     = "api"
    desired_count       = 1
    launch_type         = "EC2"
    scheduling_strategy = "REPLICA"
    load_balancer = {
      target_group_key = "prod"
      container_name   = "time-eat-api"
      container_port   = 8080
    }
  }

  datadog = {
    name                = "datadog"
    task_definition     = "datadog"
    desired_count       = 1
    launch_type         = "EC2"
    scheduling_strategy = "REPLICA"
  }
}

ecs_task_definitions = {
  api = {
    cpu          = 256
    memory       = 256
    network_mode = "bridge"
    container_port = [8080, 8126]
    host_port = [0, 8126]
    log_group    = "/ecs/time-eat-api"
    environment = {}
    volumes = [
      {
        name      = "prod-api-volume"
        host_path = "/home/ec2-user/time-eat/prod/"
      }
    ]
  }

  datadog = {
    name              = "datadog-agent"
    container_image   = "public.ecr.aws/datadog/agent:latest"
    cpu               = 100
    memoryReservation = 128
    memory            = 512
    network_mode      = "bridge"
    container_port = [8125, 8126]
    host_port = [8125, 8126]
    requires_compatibilities = ["EC2"]
    environment = {
      DD_SITE                              = "datadoghq.com"
      DD_PROCESS_AGENT_ENABLED             = "true"
      DD_APM_ENABLED                       = "true"
      DD_LOGS_ENABLED                      = "true"
      DD_LOGS_CONFIG_CONTAINER_COLLECT_ALL = "true"
      DD_APM_RECEIVER_PORT                 = "8126"
      DD_APM_NON_LOCAL_TRAFFIC             = "true"
    }
    volumes = [
      {
        name      = "docker_sock"
        host_path = "/var/run/docker.sock"
      },
      {
        name      = "proc"
        host_path = "/proc/"
      },
      {
        name      = "cgroup"
        host_path = "/cgroup/"
      }
    ]
    family = "datadog-agent-task"
  }
}
