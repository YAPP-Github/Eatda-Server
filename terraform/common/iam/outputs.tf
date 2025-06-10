output "user_name" {
  value = aws_iam_user.user.name
}

output "group_name" {
  value = aws_iam_group.admin.name
}
