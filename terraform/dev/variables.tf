variable "project_name" {
  type = string
}

variable "region" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "ec2_sg_id" {
  type = string
}

variable "instance_subnet_map" {
  type = map(string)
}

variable "alb_target_group_arns" {
  type = map(string)
}

variable "ecr_repo_names" {
  type = map(string)
}

variable "ecs_services" {
  type = map(object({
    desired_count = number
    load_balancer = optional(object({
      target_group_key = string
      container_name   = string
      container_port   = number
    }))
  }))
}

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
    volumes = list(object({
      name      = string
      host_path = string
    }))
  }))
}

variable "tags" {
  type = map(string)
  default = {}
}