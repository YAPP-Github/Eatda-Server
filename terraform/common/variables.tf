variable "project_name" {
  description = "Name of the project"
  type        = string
}

variable "tags" {
  type = map(string)
  default = {}
}