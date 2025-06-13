variable "ecs_services" {
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

variable "launch_type" {
  type    = string
  default = "EC2"
}

variable "scheduling_strategy" {
  type    = string
  default = "REPLICA"
}

variable "task_definition_arn" {
  type = map(string)
}

variable "tags" {
  type = map(string)
  default = {}
}