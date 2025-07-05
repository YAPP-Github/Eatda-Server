variable "domain_name" {
  description = "eatda.net"
  type        = string
}

variable "subdomains" {
  description = "map of subdomains and their ALB info"
  type = map(object({
    alb_dns_name = string
    alb_zone_id  = string
  }))
}

variable "frontend_domains" {
  description = "frontend domains (A record or CNAME)"
  type = map(object({
    type  = string
    value = string
  }))
}

variable "validation_method" {
  type = string
}

variable "record_type" {
  type = string
}
