output "user_name" {
  value = aws_iam_user.this.name
}

output "group_name" {
  value = aws_iam_group.this.name
}
