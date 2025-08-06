ecs_services = {
  api-dev = {
    task_definition = "api-dev"
    load_balancer = {
      target_group_key = "api-dev"
      container_name   = "api-dev"
      container_port   = 8080
    }
  }

  mysql-dev = {
    task_definition = "mysql"
  }

  datadog-agent-task = {
    task_definition     = "datadog-agent-task"
    scheduling_strategy = "DAEMON"
  }
}

ecs_task_definitions_base = {
  api-dev = {
    cpu          = 256
    memory       = 256
    network_mode = "bridge"
    container_port = [8080]
    host_port = [0]
    requires_compatibilities = ["EC2"]
    environment = {}
    volumes = [
      {
        name          = "dev-api-volume"
        host_path     = "/home/ec2-user/logs/"
        containerPath = "/logs"
        readOnly      = false
      }
    ]
  }

  mysql-dev = {
    cpu               = 256
    memoryReservation = 128
    memory            = 512
    network_mode      = "bridge"
    container_port = [3306]
    host_port = [3306]
    requires_compatibilities = ["EC2"]
    container_image   = "mysql:8"
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
    volumes = [
      {
        name          = "dev-mysql-volume"
        host_path     = "/home/ec2-user/mysql/"
        containerPath = "/var/lib/mysql"
        readOnly      = false
      }
    ]
  }

  "datadog-agent-task" = {
    cpu             = 100
    memory          = 128
    network_mode    = "bridge"
    requires_compatibilities = ["EC2"]
    container_image = "public.ecr.aws/datadog/agent:latest"
    container_port = [8125, 8126]
    host_port = [8125, 8126]

    port_mappings = [
      {
        container_port = 8126,
        host_port      = 8126,
        protocol       = "tcp"
      },
      {
        container_port = 8125,
        host_port      = 8125,
        protocol       = "udp"
      }
    ]

    environment = {
      DD_SITE                              = "us5.datadoghq.com"
      DD_PROCESS_AGENT_ENABLED             = "true"
      DD_APM_ENABLED                       = "true"
      DD_LOGS_ENABLED                      = "true"
      DD_LOGS_CONFIG_CONTAINER_COLLECT_ALL = "true"
      DD_EC2_USE_IMDSV2                    = "true"
      DD_DOGSTATSD_NON_LOCAL_TRAFFIC       = "true"
      DD_SERVICE                           = "eatda-api"
      DD_ENV                               = "dev"
      DD_VERSION                           = "v1"
    }

    volumes = [
      {
        name          = "docker_sock"
        host_path     = "/var/run/docker.sock"
        containerPath = "/var/run/docker.sock"
        readOnly      = true
      },
      {
        name          = "proc"
        host_path     = "/proc/"
        containerPath = "/host/proc"
        readOnly      = true
      },
      {
        name          = "cgroup"
        host_path     = "/sys/fs/cgroup/"
        containerPath = "/host/sys/fs/cgroup"
        readOnly      = true
      }
    ]
  }
}
