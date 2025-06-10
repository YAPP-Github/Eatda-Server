ecs_services = {
  was = {
    task_definition_name = "was"
    desired_count        = 1
    load_balancer = {
      target_group_key = "prod"
      container_name   = "time-eat-was"
      container_port   = 8080
    }
  }
}

ecs_task_definitions = {
  was = {
    cpu             = 512
    memory          = 512
    network_mode    = "bridge"
    container_image = "123456789012.dkr.ecr.ap-northeast-2.amazonaws.com/time-eat-was:latest"
    container_port = [8080, 8126]
    host_port = [0, 8126]
    log_group       = "/ecs/time-eat-was"
    environment = {}
    volumes = [
      {
        name      = "time-eat-log-volume"
        host_path = "/home/ec2-user/logs/time-eat/"
      }
    ]
  }

  datadog = {
    name            = "datadog-agent"
    container_image = "public.ecr.aws/datadog/agent:latest"
    cpu             = 100
    memory          = 512
    network_mode    = "bridge"
    container_port = [8126]
    host_port = [8126]
    log_group       = "/ecs/time-eat/dev/was"
    environment = {}
    volumes = [
      {
        name      = "time-eat-log-volume"
        host_path = "/home/ec2-user/logs/time-eat/dev/"
      }
    ]
  }
}
