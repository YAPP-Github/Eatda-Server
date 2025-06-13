variable "ecs_task_definitions" {
  type = map(object({
    cpu          = number
    memory       = number
    network_mode = string
    container_port = list(number)
    host_port = list(number)
    environment = map(string)
    requires_compatibilities = optional(list(string))
    container_image = optional(string)
    execution_role_arn = optional(string)
    task_role_arn = optional(string)
    volumes = optional(list(object({
      name      = string
      host_path = string
    })), [])
  }))
  default = {}
}

variable "ecs_services" {
  type = map(object({
    name                = string
    launch_type         = string
    task_definition     = string
    desired_count       = number
    scheduling_strategy = string
    load_balancer = optional(object({
      target_group_key = string
      container_name   = string
      container_port   = number
    }))
  }))
  default = {}
}
