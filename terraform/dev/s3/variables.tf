variable "bucket_name_prefix" {
  type = string
}

variable "environment" {
  type = string
}

variable "allowed_origins" {
  type = list(string)
}

variable "force_destroy" {
  type    = bool
  default = false
}
