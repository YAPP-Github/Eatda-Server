variable "bucket_name_prefix" {
  type = string
}

variable "environment" {
  type = string
}

variable "allowed_origins" {
  type = list(string)
}
