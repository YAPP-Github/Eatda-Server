resource "datadog_monitor" "cpu_usage" {
  draft_status             = "published"
  enable_logs_sample       = null
  enable_samples           = null
  escalation_message       = null
  evaluation_delay         = 0
  force_delete             = null
  group_retention_duration = null
  groupby_simple_monitor   = null
  include_tags             = true
  message                  = <<-EOT
    ## 🚨 [System] CPU 사용량 임계치 초과
    **Host:** {{host.name}} / **Usage:** {{value}}%

    {{#is_alert}}
    **[CRITICAL] CPU가 포화 상태입니다. (90% 이상)**
    - **영향:** 애플리케이션 처리 지연 및 타임아웃 발생 위험
    - **조치:** `top` 명령어로 고부하 프로세스 확인 및 스케일링 검토 필요
    {{/is_alert}}

    {{#is_warning}}
    **[WARNING] CPU 사용량이 증가하고 있습니다. (70% 이상)**
    - 배치 작업이나 특정 프로세스의 리소스 점유율 확인 필요
    {{/is_warning}}

    ${local.notification_footer}
  EOT
  name                     = "CPU usage is high for host {{host.name}}"
  new_group_delay          = 300
  notification_preset_name = null
  notify_audit             = false
  notify_by                = []
  notify_no_data           = false
  on_missing_data          = null
  priority                 = null
  query                    = "avg(last_5m):100 - avg:system.cpu.idle{*} by {host} > 90"
  renotify_interval        = 0
  renotify_occurrences     = 0
  renotify_statuses        = null
  require_full_window      = false
  restricted_roles         = null
  tags                     = ["integration:host"]
  timeout_h                = 0
  type                     = "query alert"
  validate                 = null
  monitor_thresholds {
    critical          = jsonencode(90)
    critical_recovery = null
    ok                = null
    unknown           = null
    warning           = jsonencode(70)
    warning_recovery  = null
  }
}

resource "datadog_monitor" "memory_usage" {
  draft_status             = "published"
  enable_logs_sample       = null
  enable_samples           = null
  escalation_message       = null
  evaluation_delay         = 0
  force_delete             = null
  group_retention_duration = null
  groupby_simple_monitor   = null
  include_tags             = false
  message                  = <<-EOT
    ## 💾 [System] 메모리 부족 위험
    **Host:** {{host.name}} / **Usage:** {{value}}%

    {{#is_alert}}
    **[CRITICAL] 가용 메모리가 10% 미만입니다.**
    - **영향:** OOM Killer로 인한 주요 프로세스 강제 종료 위험
    - **조치:** 메모리 누수 확인 및 덤프 분석 권장
    {{/is_alert}}

    {{#is_warning}}
    **[WARNING] 메모리 사용량이 안전 구간을 벗어났습니다.**
    - 지속적인 증가 추세인지 모니터링 필요
    {{/is_warning}}

    ${local.notification_footer}
  EOT
  name                     = "Memory space is high for host {{host.name}}"
  new_group_delay          = 300
  notification_preset_name = null
  notify_audit             = false
  on_missing_data          = "default"
  priority                 = null
  query                    = "avg(last_5m):avg:system.mem.pct_usable{*} by {host} > 0.9"
  renotify_interval        = 0
  renotify_occurrences     = 0
  renotify_statuses        = null
  require_full_window      = false
  restricted_roles         = null
  tags                     = ["integration:host"]
  timeout_h                = 0
  type                     = "query alert"
  validate                 = null
  monitor_thresholds {
    critical          = jsonencode(0.9)
    critical_recovery = null
    ok                = null
    unknown           = null
    warning           = jsonencode(0.7)
    warning_recovery  = null
  }
}

resource "datadog_monitor" "eatda_availability" {
  draft_status             = "published"
  enable_logs_sample       = null
  enable_samples           = null
  escalation_message       = null
  evaluation_delay         = 0
  force_delete             = null
  group_retention_duration = null
  groupby_simple_monitor   = null
  include_tags             = true
  message                  = <<-EOT
    ## 🔥 [Service] 가용성(Availability) SLO 위험
    **Metric:** 30-day Availability Burn Rate

    {{#is_alert}}
    **[CRITICAL] 에러율이 급증하여 가용성 목표를 위협하고 있습니다.**
    - **영향:** 다수의 API 요청 실패 (5xx Error)
    - **조치:** APM을 통한 에러 원인 분석 및 최근 배포 사항 점검
    {{/is_alert}}

    {{#is_warning}}
    **[WARNING] 에러 예산 소진 속도가 빨라지고 있습니다.**
    - 간헐적인 에러 발생 여부 확인 필요
    {{/is_warning}}

    ${local.notification_footer}
  EOT
  name                     = "[Eatda-prod-api] Availability - 30d"
  new_group_delay          = 0
  notification_preset_name = null
  notify_audit             = false
  notify_by                = []
  notify_no_data           = false
  on_missing_data          = null
  priority                 = null
  query                    = "burn_rate(\"c2ba09c7153a5bcd91e9ba4f92245579\").over(\"30d\").long_window(\"1h\").short_window(\"5m\") > 14.4"
  renotify_interval        = 0
  renotify_occurrences     = 0
  renotify_statuses        = null
  require_full_window      = false
  restricted_roles         = null
  tags                     = []
  timeout_h                = 0
  type                     = "slo alert"
  validate                 = null
  monitor_thresholds {
    critical          = jsonencode(14.4)
    critical_recovery = null
    ok                = null
    unknown           = null
    warning           = jsonencode(5.6)
    warning_recovery  = null
  }
}

resource "datadog_monitor" "eatda_latency" {
  draft_status             = "published"
  enable_logs_sample       = null
  enable_samples           = null
  escalation_message       = null
  evaluation_delay         = 0
  force_delete             = null
  group_retention_duration = null
  groupby_simple_monitor   = null
  include_tags             = true
  message                  = <<-EOT
    ## 🐢 [Service] 응답 지연(Latency) 감지
    **Target:** P95 < 500ms

    {{#is_alert}}
    **[CRITICAL] 응답 속도가 목표치보다 현저히 느립니다.**
    - **영향:** 사용자 경험 저하 및 클라이언트 타임아웃
    - **조치:** Slow Query 확인 및 APM 병목 구간 분석
    {{/is_alert}}

    {{#is_warning}}
    **[WARNING] 응답 속도가 평소보다 느려지고 있습니다.**
    - 특정 API의 성능 저하인지 확인 필요
    {{/is_warning}}

    ${local.notification_footer}
  EOT
  name                     = "[Eatda-prod-api] Latency (P95 < 500ms)"
  new_group_delay          = 0
  notification_preset_name = null
  notify_audit             = false
  notify_by                = []
  notify_no_data           = false
  on_missing_data          = null
  priority                 = null
  query                    = "burn_rate(\"7beca231285d5639b23be8d182cd8d4a\").over(\"30d\").long_window(\"6h\").short_window(\"30m\") > 14.4"
  renotify_interval        = 0
  renotify_occurrences     = 0
  renotify_statuses        = null
  require_full_window      = false
  restricted_roles         = null
  tags                     = []
  timeout_h                = 0
  type                     = "slo alert"
  validate                 = null
  monitor_thresholds {
    critical          = jsonencode(14.4)
    critical_recovery = null
    ok                = null
    unknown           = null
    warning           = jsonencode(5.6)
    warning_recovery  = null
  }
}
