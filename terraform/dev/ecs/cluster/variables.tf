variable "cluster_name" {
  description = "dev cluster name"
  default     = "dev-cluster"
}

variable "namespace_id" {
  description = "dev namespace id"
  type        = string
}

variable "cluster_settings" {
  type = object({
    name  = string
    value = string
  })

  validation {
    condition     = var.cluster_settings.name == "containerInsights"
    error_message = "Only 'containerInsights' is supported for ECS cluster setting.name."
  }
}

variable "tags" {
  type = map(string)
  default = {}
}