variable "dev_ecs_services" {
  type = map(object({
    name                = string
    launch_type         = string
    task_definition     = string
    desired_count       = number
    scheduling_strategy = string
  }))
}

variable "cluster_id" {
  type = string
}

variable "deployment_controller_type" {
  type = string
}