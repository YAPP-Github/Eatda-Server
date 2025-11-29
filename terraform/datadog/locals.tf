data "aws_ssm_parameter" "datadog_api" {
  name = "/prod/DD_API_KEY"
}

data "aws_ssm_parameter" "datadog_app" {
  name = "/prod/DD_APP_KEY"
}

data "aws_ssm_parameter" "discord_alert_webhook_url" {
  name = "/discord/discord_alert_webhook_url"
}

data "aws_ssm_parameter" "discord_warn_webhook_url" {
  name = "/discord/discord_warn_webhook_url"
}

data "aws_ssm_parameter" "discord_recovery_webhook_url" {
  name = "/discord/discord_recovery_webhook_url"
}

locals {
  notification_footer = <<-EOT

    ---

    {{#is_alert}}
    🚨 **CRITICAL ALERT**
    @webhook-discord-alert-channel
    {{/is_alert}}

    {{#is_warning}}
    ⚠️ **WARNING ALERT**
    @webhook-discord-warn-channel
    {{/is_warning}}

    {{#is_recovery}}
    ✅ **RECOVERY**
    @webhook-discord-warn-channel-recovery
    {{/is_recovery}}
  EOT
}
