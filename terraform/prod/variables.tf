variable "project_name" {
  description = "Name of the project"
  type        = string
}

variable "vpc_id" {
  type = string
}

variable "vpc_cidr" {
  type = string
}

variable "availability_zones" {
  type = list(string)
}

variable "ec2_sg_id" {
  type = string
}

variable "instance_subnet_map" {
  type = map(string)
}

variable "alb_target_group_arns" {
  type = map(string)
  description = "Map of target group names to their ARNs"
}

variable "ecr_repo_names" {
  type = map(string)
}

variable "ecs_services" {
  type = map(object({
    desired_count       = number
    task_definition     = string
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

variable "private_subnet_ids" {
  description = "Private subnet IDs to associate with DB subnet group"
  type = list(string)
}

variable "vpc_security_group_ids" {
  description = "List of VPC security group IDs"
  type = list(string)
}

variable "environment" {
  type = string
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

variable "tags" {
  type = map(string)
  default = {}
}