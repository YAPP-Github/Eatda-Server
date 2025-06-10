instance_definitions = {
  prod = {
    ami                  = "ami-012ea6058806ff688"
    instance_type        = "t3a.small"
    iam_instance_profile = "ec2-to-ecs"
    key_name             = "issuefy-key"
    user_data            = <<-EOF
#!/bin/bash
echo ECS_CLUSTER=issuefy-cluster >> /etc/ecs/ecs.config
EOF
  }

  monitoring = {
    ami                  = "ami-05377cf8cfef186c2"
    instance_type        = "t2.micro"
    iam_instance_profile = "ec2-monitoring"
    key_name             = "issuefy-key"
    user_data            = <<-EOF
#!/bin/bash
dnf update -y
dnf install -y docker
systemctl enable docker
systemctl start docker
EOF
  }

  nat = {
    ami           = "ami-0fa9216d5e4fcd66d"
    instance_type = "t3.nano"
    key_name      = "issuefy-key"
  }
}
