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

variable "name_space_id" {
  type = string
}

variable "ecs_task_definitions" {
  type = map(object({
    cpu          = number
    memory       = number
    network_mode = string
    container_port = list(number)
    host_port = list(number)
    log_group    = string
    environment = map(string)
    volumes = list(object({
      name      = string
      host_path = string
    }))
  }))
}

variable "ecs_services" {
  type = map(object({
    task_definition_name = string
    desired_count        = number
    load_balancer = object({
      target_group_key = string
      container_name   = string
      container_port   = number
    })
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

variable "log_stream_prefix" {
  type    = string
  default = "ecs"
}

variable "volume_mount_paths" {
  type = map(string)
  default = {}
}

variable "ecs_unified_role_arn" {
  type = string
}

variable "alb_target_group_arns" {
  type = map(string)
}

variable "tags" {
  type = map(string)
  default = {}
}

