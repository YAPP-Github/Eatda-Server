output "vpc_id" {
  description = "The ID of the VPC"
  value       = module.timeeat_vpc.vpc_id
}

output "public_subnet_ids" {
  description = "Map of public subnet IDs for environments"
  value = {
    dev  = module.timeeat_vpc.public_subnet_ids.dev
    prod = module.timeeat_vpc.public_subnet_ids.prod
  }
}

output "private_subnet_ids" {
  description = "List of IDs of private subnets"
  value = {
    dev  = module.timeeat_vpc.private_subnet_ids.dev
    prod = module.timeeat_vpc.private_subnet_ids.prod
  }
}

output "security_group_ids" {
  value = module.security_group.security_group_ids
}

output "target_group_arns" {
  value = module.timeeat_alb.target_group_arns
}

output "target_group_names" {
  value = module.timeeat_alb.target_group_names
}

output "user_name" {
  value = module.timeeat_iam.user_name
}

output "group_name" {
  value = module.timeeat_iam.group_name
}

output "role_name" {
  value = module.timeeat_iam_role.role_name
}

output "role_arn" {
  value = module.timeeat_iam_role.role_arn
}

output "instance_profile_name" {
  value = module.timeeat_iam_role.instance_profile_name
}

