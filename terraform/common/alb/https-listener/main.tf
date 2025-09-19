resource "aws_alb_listener" "https" {
  load_balancer_arn = var.alb_arn
  port              = var.https_listener.port
  protocol          = var.https_listener.protocol
  certificate_arn   = var.https_listener.certificate_arn

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "Access Denied: Invalid Host"
      status_code  = "403"
    }
  }
}

resource "aws_alb_listener_rule" "common" {
  for_each     = var.listener_rules
  listener_arn = aws_alb_listener.https.arn
  priority     = each.value.priority

  condition {
    host_header {
      values = [each.value.host_header]
    }
  }

  action {
    type             = each.value.action_type
    target_group_arn = each.value.target_group_arn
  }

  tags = {
    Name = each.key
  }
}
