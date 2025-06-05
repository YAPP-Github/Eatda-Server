output "role_name" {
  value = aws_iam_role.timeeat_iam_role.name
}

output "role_arn" {
  value = aws_iam_role.timeeat_iam_role.arn
}

output "instance_profile_name" {
  value = aws_iam_instance_profile.timeeat_iam_instance_profile.name
}
