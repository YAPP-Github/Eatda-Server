variable "name_prefix" {
  description = "project name prefix"
  type        = string
  default     = "issuefy"
}

variable "instance_definitions" {
  type = map(object({
    ami           = string
    instance_type = string
    iam_instance_profile = optional(string)
    key_name      = string
    user_data = optional(string)
  }))
}

variable "ecs_task_definitions" {
  type = map(object({
    cpu                      = number
    memory                   = number
    network_mode             = string
    container_image          = string
    container_port           = list(number)
    host_port                = list(number)
    log_group                = string
    task_role_arn            = string
    execution_role_arn       = string
    environment              = map(string)
    volumes = optional(list(object({
      name      = string
      host_path = string
    })), [])
  }))
}

variable "ecs_services" {
  type = map(object({
    task_definition = string
    desired_count   = number
    load_balancer = object({
      target_group_arn = string
      container_name   = string
      container_port   = number
    })
  }))
}


variable "tags" {
  type = map(string)
  default = {}
}


