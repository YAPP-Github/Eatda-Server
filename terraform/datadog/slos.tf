resource "datadog_service_level_objective" "eatda_latency" {
  description       = "30일간 P95가 500ms 이내로 유지되는 SLO"
  force_delete      = null
  groups            = null
  monitor_ids       = null
  name              = "[Eatda-prod-api] Latency (P95 < 500ms)"
  tags              = ["service:eatda-api-prod"]
  target_threshold  = 99
  timeframe         = "30d"
  type              = "time_slice"
  validate          = null
  warning_threshold = 99.5
  sli_specification {
    time_slice {
      comparator             = "<="
      query_interval_seconds = 300
      threshold              = 0.5
      query {
        formula {
          formula_expression = "query1"
        }
        query {
          metric_query {
            data_source = "metrics"
            name        = "query1"
            query       = "p95:trace.servlet.request{env:prod}"
          }
        }
      }
    }
  }
  thresholds {
    target    = 99
    timeframe = "30d"
    warning   = 99.5
  }
}

resource "datadog_service_level_objective" "eatda_availability" {
  description       = "30일간 가용성 SLO"
  force_delete      = null
  groups            = null
  monitor_ids       = null
  name              = "[Eatda-prod-api] Availability - 30d"
  tags              = ["service:eatda-api-prod"]
  target_threshold  = 99
  timeframe         = "30d"
  type              = "metric"
  validate          = null
  warning_threshold = 99.5
  query {
    denominator = "count:trace.servlet.request{service:eatda-api-prod}.as_count()"
    numerator   = "count:trace.servlet.request{service:eatda-api-prod, !http.status_code:5*, error:false}.as_count()"
  }
  thresholds {
    target    = 99
    timeframe = "30d"
    warning   = 99.5
  }
}
