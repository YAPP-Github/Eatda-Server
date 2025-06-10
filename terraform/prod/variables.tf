variable "project_name" {
  description = "Name of the project"
  type        = string
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
  type = string
}

variable "unified_role_arn" {
  type = string
}

variable "ecr_repo_names" {
  type = map(string)
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

variable "private_subnet_ids" {
  description = "Private subnet IDs to associate with DB subnet group"
  type = list(string)
}

variable "vpc_security_group_ids" {
  description = "List of VPC security group IDs"
  type = list(string)
}

variable "tags" {
  type = map(string)
  default = {}
}