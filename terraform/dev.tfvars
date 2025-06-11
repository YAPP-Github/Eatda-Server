ecs_services = {
  dev = {
    desired_count = 1
    task_definition = ""
    load_balancer = {
      target_group_key = "dev"
      container_name   = "time-eat-api"
      container_port   = 8080
    }
  }
}

ecs_task_definitions = {
  dev = {
    cpu          = 512
    memory       = 512
    network_mode = "bridge"
    container_port = [8080, 3306]
    host_port = [0, 3306]
    requires_compatibilities = ["EC2"]
    log_group    = "/ecs/time-eat-api"
    environment = {}
    volumes = [
      {
        name      = "time-eat-log-volume"
        host_path = "/home/ec2-user/logs/time-eat/"
      }
    ]
  }

  //TODO MySQL 컨테이너 추가
}
