ecs_services = {
  api = {
    desired_count   = 1
    task_definition = "api"
    load_balancer = {
      target_group_key = "dev"
      container_name   = "time-eat-dev"
      container_port   = 8080
    }
  }

  mysql = {
    desired_count   = 1
    task_definition = "mysql"
  }
}

ecs_task_definitions = {
  api = {
    cpu          = 256
    memory       = 256
    network_mode = "bridge"
    container_port = [8080]
    host_port = [0]
    requires_compatibilities = ["EC2"]
    environment = {}
    volumes = [
      {
        name      = "dev-api-volume"
        host_path = "/home/ec2-user/time-eat/dev/"
      }
    ]
  }

  mysql = {
    cpu               = 256
    memoryReservation = 128
    memory = 512
    network_mode      = "bridge"
    container_port = [3306]
    host_port = [3306]
    requires_compatibilities = ["EC2"]
    container_image   = "mysql:8"
    environment = {
      MYSQL_DATABASE = "time-eat"
    }
    secrets = [
      {
        name      = "MYSQL_USER"
        valueFrom = "/dev/mysql-name"
      },
      {
        name      = "MYSQL_ROOT_PASSWORD"
        valueFrom = "/dev/mysql-root-pw"
      },
      {
        name      = "MYSQL_PASSWORD"
        valueFrom = "/dev/mysql-pw"
      }
    ]
    volumes = [
      {
        name      = "dev-mysql-volume"
        host_path = "/home/ec2-user/time-eat/mysql/"
      }
    ]
  }
}
