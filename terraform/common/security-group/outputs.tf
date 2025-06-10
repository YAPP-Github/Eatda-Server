output "security_group_ids" {
  value = {
    for k, sg in aws_security_group.common :
    k => sg.id
  }
}
