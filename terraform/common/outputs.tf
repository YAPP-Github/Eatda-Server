output "vpc_id" {
  description = "The ID of the VPC"
  value       = module.vpc.vpc_id
}

output "public_subnet_ids" {
  description = "Map of public subnet IDs for environments"
  value = {
    dev  = module.vpc.public_subnet_ids.dev
    prod = module.vpc.public_subnet_ids.prod
  }
}

output "private_subnet_ids" {
  description = "List of IDs of private subnets"
  value = {
    dev  = module.vpc.private_subnet_ids.dev
    prod = module.vpc.private_subnet_ids.prod
  }
}

output "security_group_ids" {
  value = {
    for k, sg in module.security_group :
    k => sg.security_group_id
  }
}

output "target_group_arns" {
  value = module.alb.target_group_arns
}

output "target_group_names" {
  value = module.alb.target_group_names
}

output "user_name" {
  value = module.iam.user_name
}

output "group_name" {
  value = module.iam.group_names
}

output "role_name" {
  value = {
    for k, mod in module.iam_role :
    k => mod.role_name
  }
}

output "role_arn" {
  value = {
    for k, mod in module.iam_role :
    k => mod.role_arn
  }
}

output "instance_profile_name" {
  value = {
    for k, mod in module.iam_role :
    k => mod.instance_profile_name
  }
}


