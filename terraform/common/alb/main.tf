resource "aws_alb" "timeeat_alb" {
  name               = local.alb_name
  internal           = local.internal
  load_balancer_type = local.loadbalancer_type
  security_groups = [var.alb_security_group_id]
  subnets            = var.subnets

  enable_deletion_protection = local.deletion_protection

  tags = {
    Environment = local.alb_tags
  }
}

module "timeeat_http_listener" {
  source        = "./http-listener"
  alb_arn       = aws_alb.timeeat_alb.arn
  http_listener = local.http_listener
}

module "timeeat_https_listener" {
  source         = "./https-listener"
  alb_arn        = aws_alb.timeeat_alb.arn
  https_listener = local.https_listener
  listener_rules = local.listener_rules
}

module "timeeat_target_group" {
  source        = "./target-group"
  target_groups = local.target_groups
  vpc_id        = var.vpc_id
}