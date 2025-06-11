output "user_name" {
  value = [for user in aws_iam_user.user : user.name]
}

output "group_names" {
  value = [for user in aws_iam_user.user : user.name]
}
