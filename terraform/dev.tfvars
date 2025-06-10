ecs_services = {
  was = {
    task_definition_name = "was"
    desired_count        = 1
    load_balancer = {
      target_group_key = "dev"
      container_name   = "time-eat-was"
      container_port   = 8080
    }
  }
}

ecs_task_definitions = {
  was = {
    cpu             = 512
    memory          = 512
    container_port  = [8080, 3306]
    host_port       = [0, 3306]
    log_group       = "/ecs/time-eat-was"
    environment     = {}
    volumes = [
      {
        name      = "time-eat-log-volume"
        host_path = "/home/ec2-user/logs/time-eat/"
      }
    ]
  }

  datadog = {
    cpu             = 100
    memory          = 512
    container_port  = [8126]
    host_port       = [8126]
    log_group       = "/ecs/time-eat/dev/was"
    environment     = {}
    volumes = [
      {
        name      = "time-eat-log-volume"
        host_path = "/home/ec2-user/logs/time-eat/dev/"
      }
    ]
  }
}
