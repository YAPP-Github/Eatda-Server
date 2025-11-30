resource "datadog_webhook" "discord_alert" {
  custom_headers = null
  encode_as      = "json"
  name           = "discord-alert-channel"
  payload = jsonencode({
    embeds = [{
      color       = 15548997
      description = "$EVENT_MSG"
      title       = "$EVENT_TITLE"
      url         = "$LINK"
    }]
  })
  url = data.aws_ssm_parameter.discord_alert_webhook_url.value
}

resource "datadog_webhook" "discord_warn" {
  custom_headers = null
  encode_as      = "json"
  name           = "discord-warn-channel"
  payload = jsonencode({
    embeds = [{
      color       = 16776960
      description = "$EVENT_MSG"
      title       = "$EVENT_TITLE"
      url         = "$LINK"
    }]
  })
  url = data.aws_ssm_parameter.discord_warn_webhook_url.value
}

resource "datadog_webhook" "discord-recovery" {
  custom_headers = null
  encode_as      = "json"
  name           = "discord-warn-channel-recovery"
  payload = jsonencode({
    embeds = [{
      color       = 5763719
      description = "$EVENT_MSG"
      title       = "$EVENT_TITLE"
      url         = "$LINK"
    }]
  })
  url = data.aws_ssm_parameter.discord_recovery_webhook_url.value
}
