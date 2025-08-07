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
}

ecs_task_definitions_base = {
  api-dev = {
    cpu          = 512
    memory       = 512
    network_mode = "bridge"
    requires_compatibilities = ["EC2"]

    port_mappings = [
      {
        container_port = 8080,
        host_port      = 0,
        protocol       = "tcp"
      }
    ]

    environment = {}
    volumes = [
      {
        name          = "dev-api-volume"
        host_path     = "/home/ec2-user/logs/"
        containerPath = "/logs"
        readOnly      = false
      },
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

  mysql-dev = {
    cpu               = 256
    memoryReservation = 128
    memory            = 512
    network_mode      = "bridge"
    requires_compatibilities = ["EC2"]
    container_image   = "mysql:8"

    port_mappings = [
      {
        container_port = 3306,
        host_port      = 3306,
        protocol       = "tcp"
      }
    ]

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
}
