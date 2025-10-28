data "aws_ssm_parameter" "x_origin_verify" {
  name = "/common/CLIENT_HEADER_SECRET"
}

resource "aws_wafv2_web_acl" "this" {
  name  = "${var.project_name}-web-acl"
  scope = "REGIONAL"

  default_action {
    block {}
  }

  rule {
    name     = "Allow-Verified-Server-Requests"
    priority = 5
    action {
      allow {}
    }
    statement {
      and_statement {
        statement {
          or_statement {
            statement {
              byte_match_statement {
                field_to_match {
                  single_header {
                    name = "user-agent"
                  }
                }
                search_string         = "node"
                positional_constraint = "CONTAINS"
                text_transformation {
                  priority = 0
                  type     = "NONE"
                }
              }
            }
            statement {
              byte_match_statement {
                field_to_match {
                  single_header {
                    name = "user-agent"
                  }
                }
                search_string         = "Vercel"
                positional_constraint = "CONTAINS"
                text_transformation {
                  priority = 0
                  type     = "NONE"
                }
              }
            }
          }
        }
        statement {
          byte_match_statement {
            field_to_match {
              single_header {
                name = "x-origin-verify"
              }
            }
            search_string         = data.aws_ssm_parameter.x_origin_verify.value
            positional_constraint = "EXACTLY"
            text_transformation {
              priority = 0
              type     = "NONE"
            }
          }
        }
      }
    }
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "allow-verified-server-requests"
      sampled_requests_enabled   = true
    }
  }

  rule {
    name     = "Allow-Browser-Requests"
    priority = 10
    action {
      allow {}
    }
    statement {
      or_statement {
        statement {
          byte_match_statement {
            field_to_match {
              single_header {
                name = "origin"
              }
            }
            search_string         = "eatda.net"
            positional_constraint = "ENDS_WITH"
            text_transformation {
              priority = 0
              type     = "LOWERCASE"
            }
          }
        }
        statement {
          byte_match_statement {
            field_to_match {
              single_header {
                name = "referer"
              }
            }
            search_string         = "eatda.net"
            positional_constraint = "CONTAINS"
            text_transformation {
              priority = 0
              type     = "LOWERCASE"
            }
          }
        }
        statement {
          byte_match_statement {
            field_to_match {
              single_header {
                name = "origin"
              }
            }
            search_string         = "dev.eatda.net"
            positional_constraint = "ENDS_WITH"
            text_transformation {
              priority = 0
              type     = "LOWERCASE"
            }
          }
        }
        statement {
          byte_match_statement {
            field_to_match {
              single_header {
                name = "referer"
              }
            }
            search_string         = "dev.eatda.net"
            positional_constraint = "CONTAINS"
            text_transformation {
              priority = 0
              type     = "LOWERCASE"
            }
          }
        }
        statement {
          byte_match_statement {
            field_to_match {
              single_header {
                name = "origin"
              }
            }
            search_string         = "http://localhost:3000"
            positional_constraint = "EXACTLY"
            text_transformation {
              priority = 0
              type     = "NONE"
            }
          }
        }
        statement {
          byte_match_statement {
            field_to_match {
              single_header {
                name = "referer"
              }
            }
            search_string         = "http://localhost:3000/"
            positional_constraint = "STARTS_WITH"
            text_transformation {
              priority = 0
              type     = "NONE"
            }
          }
        }
      }
    }
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "allow-browser-requests"
      sampled_requests_enabled   = true
    }
  }

  # AWS Managed Core Rule Set
  rule {
    name     = "AWS-Managed-Core-Rule-Set"
    priority = 30
    override_action {
      none {}
    }
    statement {
      managed_rule_group_statement {
        vendor_name = "AWS"
        name        = "AWSManagedRulesCommonRuleSet"
      }
    }
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "aws-managed-common"
      sampled_requests_enabled   = true
    }
  }

  visibility_config {
    cloudwatch_metrics_enabled = true
    metric_name                = "${var.project_name}-web-acl"
    sampled_requests_enabled   = true
  }

  tags = var.tags
}
