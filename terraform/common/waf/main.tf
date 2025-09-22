resource "aws_wafv2_web_acl" "this" {
  name  = "${var.project_name}-web-acl"
  scope = "REGIONAL"

  default_action {
    allow {}
  }

  # Rate-based Rule (HTTP Flood)
  rule {
    name     = "Rate-Limit-Rule"
    priority = 1
    action {
      block {}
    }
    statement {
      rate_based_statement {
        limit              = var.request_threshold
        aggregate_key_type = "IP"
      }
    }
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "rate-limit-rule"
      sampled_requests_enabled   = true
    }
  }

  # AWS Managed Core Rule Set
  rule {
    name     = "AWS-Managed-Core-Rule-Set"
    priority = 10
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

  # Scanners & Probes Protection
  rule {
    name     = "AWS-Managed-Known-Bad-Inputs-Rule-Set"
    priority = 20
    override_action {
      none {}
    }
    statement {
      managed_rule_group_statement {
        vendor_name = "AWS"
        name        = "AWSManagedRulesKnownBadInputsRuleSet"
      }
    }
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "aws-managed-bad-inputs"
      sampled_requests_enabled   = true
    }
  }

  # Reputation Lists Protection
  rule {
    name     = "AWS-Managed-Amazon-IP-Reputation-List"
    priority = 30
    override_action {
      none {}
    }
    statement {
      managed_rule_group_statement {
        vendor_name = "AWS"
        name        = "AWSManagedRulesAmazonIpReputationList"
      }
    }
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "aws-managed-ip-rep"
      sampled_requests_enabled   = true
    }
  }

  # Bad Bot Protection
  rule {
    name     = "AWS-Managed-Bot-Control-Rule-Set"
    priority = 40

    override_action {
      none {}
    }

    statement {
      managed_rule_group_statement {
        vendor_name = "AWS"
        name        = "AWSManagedRulesBotControlRuleSet"

        rule_action_override {
          name = "SignalNonBrowserUserAgent"
          action_to_use {
            count {}
          }
        }
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "aws-managed-bot-control"
      sampled_requests_enabled   = true
    }
  }

  # 임시 조치로 ua가 node일 경우만 통과시킴
  rule {
    name     = "Block-Non-Node-User-Agents"
    priority = 41

    action {
      block {}
    }

    statement {
      and_statement {
        statement {
          label_match_statement {
            scope = "LABEL"
            key   = "awswaf:managed:aws:bot-control:signal:non_browser_user_agent"
          }
        }

        statement {
          not_statement {
            statement {
              byte_match_statement {
                search_string = "node"
                field_to_match {
                  single_header {
                    name = "user-agent"
                  }
                }
                positional_constraint = "CONTAINS"
                text_transformation {
                  priority = 0
                  type     = "NONE"
                }
              }
            }
          }
        }
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "block-non-node-uas"
      sampled_requests_enabled   = true
    }
  }

  # Anonymous IP list
  rule {
    name     = "AWS-Managed-Anonymous-IP-List"
    priority = 50
    override_action {
      none {}
    }
    statement {
      managed_rule_group_statement {
        vendor_name = "AWS"
        name        = "AWSManagedRulesAnonymousIpList"
      }
    }
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "aws-managed-anonymous-ip"
      sampled_requests_enabled   = true
    }
  }

  # SQL database
  rule {
    name     = "AWS-Managed-SQLi-Rule-Set"
    priority = 60
    override_action {
      none {}
    }
    statement {
      managed_rule_group_statement {
        vendor_name = "AWS"
        name        = "AWSManagedRulesSQLiRuleSet"
      }
    }
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "aws-managed-sql-db"
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
