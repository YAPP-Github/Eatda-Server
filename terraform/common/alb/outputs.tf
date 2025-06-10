output "alb_arn" {
  value = aws_alb.timeeat_alb.arn
}

output "target_group_arns" {
  value = module.timeeat_target_group.target_group_arns
}

output "target_group_names" {
  value = module.timeeat_target_group.target_group_names
}