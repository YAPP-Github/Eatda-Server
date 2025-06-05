output "user_name" {
  value = aws_iam_user.timeeat_iam_user.name
}

output "group_name" {
  value = aws_iam_group.timeeat_iam_group.name
}
