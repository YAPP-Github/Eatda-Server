variable "project_name" {
  type = string
}

variable "request_threshold" {
  type        = number
  description = "Rate-Limit 규칙에 적용할 5분당 IP별 최대 요청 수"
}

variable "tags" {
  type = map(string)
}
