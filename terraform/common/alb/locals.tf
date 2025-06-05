locals {
  alb_name            = "timeeat-alb"
  deletion_protection = false
  loadbalancer_type = "application"
  internal          = false
  alb_tags = {
    Environment = local.alb_name
  }
}

locals {
  target_groups = {
    "dev-timeeat" = {
      env         = "dev"
      port        = 8080
      protocol    = "HTTP"
      target_type = "instance"
      health_check = {
        path                = "/"
        protocol            = "HTTP"
        interval            = 30
        matcher             = "200"
        healthy_threshold   = 5
        unhealthy_threshold = 5
      }
    }
    "prod-timeeat" = {
      env         = "prod"
      port        = 8080
      protocol    = "HTTP"
      target_type = "instance"
      health_check = {
        path                = "/"
        protocol            = "HTTP"
        interval            = 30
        matcher             = "200"
        healthy_threshold   = 5
        unhealthy_threshold = 5
      }
    }
  }
}


locals {
  http_listener = {
    port     = 80
    protocol = "HTTP"
    type     = "redirect"
    redirect = {
      port        = 443
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

locals {
  https_listener = {
    port             = 443
    protocol         = "HTTPS"
    type             = "forward"
    target_group_arn = module.timeeat_target_group.target_group_arns["dev-timeeat"]
  }
}

locals {
  listener_rules = {
    "prod-path-rule" = {
      priority = 1
      host_header       = "timeeat.site"
      action_type       = "forward"
      target_group_arn  = module.timeeat_target_group.target_group_arns["prod-timeeat"]
    }
    "dev-path-rule" = {
      priority = 2
      host_header       = "dev.timeeat.site"
      action_type       = "forward"
      target_group_arn  = module.timeeat_target_group.target_group_arns["dev-timeeat"]
    }
  }
}
