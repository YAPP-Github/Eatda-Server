output "alb_arn" {
  value = aws_alb.common.arn
}

output "target_group_arns" {
  value = module.target_groups.target_group_arns
}

output "target_group_names" {
  value = module.target_groups.target_group_names
}