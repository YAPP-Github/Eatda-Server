variable "vpc_id" {}

variable "ns_name" {
  type = string
}

variable "ns_ttl" {
  type = number
}

variable "ns_type" {
  type = string
}

variable "ns_failure_threshold" {
  type = number
}

variable "service_discovery_service_name" {
  type = string
}

variable "tags" {
  type = map(string)
  default = {}
}