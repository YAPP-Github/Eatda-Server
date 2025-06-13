variable "project_name" {
  description = "Name of the project"
  type        = string
}

variable "environment" {
  type = string
}

variable "ecr_repo_names" {
  type = map(string)
}

variable "ecs_task_definitions" {
  type = map(object({
    cpu                = number
    memory             = number
    network_mode       = string
    container_port = list(number)
    host_port = list(number)
    environment = map(string)
    requires_compatibilities = optional(list(string))
    container_image = optional(string)
    execution_role_arn = optional(string)
    task_role_arn = optional(string)
    volumes = list(object({
      name      = string
      host_path = string
    }))
  }))
}

variable "ecs_services" {
  type = map(object({
    name                = string
    launch_type         = string
    task_definition     = string
    desired_count       = number
    scheduling_strategy = string
  }))
}

variable "name_prefix" {
  type    = string
  default = "time-eat"
}

variable "default_stop_timeout" {
  type    = number
  default = 30
}

variable "default_protocol" {
  type    = string
  default = "tcp"
}

variable "volume_mount_paths" {
  type = map(string)
  default = {}
}

variable "alb_target_group_arns" {
  type = map(string)
}

variable "tags" {
  type = map(string)
  default = {}
}

