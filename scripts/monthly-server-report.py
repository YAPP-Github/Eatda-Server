import boto3
import os
import requests
from datadog_api_client import ApiClient, Configuration
from datadog_api_client.v1.api.events_api import EventsApi
from datadog_api_client.v1.api.service_level_objectives_api import ServiceLevelObjectivesApi
from datetime import datetime
from dateutil.relativedelta import relativedelta

DD_API_KEY = os.getenv("DD_API_KEY")
DD_APP_KEY = os.getenv("DD_APP_KEY")
DISCORD_WEBHOOK_URL = os.getenv("DISCORD_WEBHOOK_URL")
AWS_REGION = os.getenv("AWS_REGION", "ap-northeast-2")
WAF_WEB_ACL_NAME = "eatda-web-acl"

SLO_AVAILABILITY_ID = "c2ba09c7153a5bcd91e9ba4f92245579"
SLO_LATENCY_ID = "7beca231285d5639b23be8d182cd8d4a"


def get_date_ranges():
    today = datetime.now()
    this_month_start = today.replace(day=1, hour=0, minute=0, second=0, microsecond=0)
    last_month_start = this_month_start - relativedelta(months=1)
    month_before_last_start = last_month_start - relativedelta(months=1)

    report_period = {
        'start_ts': int(last_month_start.timestamp()),
        'end_ts': int(this_month_start.timestamp()),
        'start_dt': last_month_start,
        'end_dt': this_month_start,
        'start_iso': last_month_start.strftime('%Y-%m-%d'),
        'end_iso': this_month_start.strftime('%Y-%m-%d'),
        'month_str': last_month_start.strftime("%Y년 %m월")
    }

    prev_period = {
        'start_iso': month_before_last_start.strftime('%Y-%m-%d'),
        'end_iso': last_month_start.strftime('%Y-%m-%d')
    }

    return report_period, prev_period


def normalize_slo_value(value):
    if value is None:
        return 0.0
    if 0 < value <= 1.0:
        return value * 100
    return value


def get_datadog_metrics(start_ts, end_ts):
    configuration = Configuration()
    configuration.host = "https://api.us5.datadoghq.com"
    configuration.api_key["apiKeyAuth"] = DD_API_KEY
    configuration.api_key["appKeyAuth"] = DD_APP_KEY

    data = {'slo_avail': 0.0, 'slo_latency': 0.0, 'alert_count': 0}

    try:
        with ApiClient(configuration) as api_client:
            slo_api = ServiceLevelObjectivesApi(api_client)

            try:
                avail = slo_api.get_slo_history(SLO_AVAILABILITY_ID, from_ts=start_ts, to_ts=end_ts)
                data['slo_avail'] = normalize_slo_value(avail.data.overall.sli_value)
            except Exception as e:
                print(f"⚠️ Error fetching Availability SLO: {e}")

            try:
                latency = slo_api.get_slo_history(SLO_LATENCY_ID, from_ts=start_ts, to_ts=end_ts)
                data['slo_latency'] = normalize_slo_value(latency.data.overall.sli_value)
            except Exception as e:
                print(f"⚠️ Error fetching Latency SLO: {e}")

            event_api = EventsApi(api_client)
            try:
                events = event_api.list_events(
                    start=start_ts,
                    end=end_ts,
                    tags="status:error,source:monitor,service:eatda-api-prod"
                )
                data['alert_count'] = len(events.events) if events.events else 0
            except Exception as e:
                print(f"⚠️ Error fetching Events: {e}")
    except Exception as e:
        print(f"❌ Critical Datadog API Error: {e}")

    return data


def get_aws_waf_stats(start_dt, end_dt):
    client = boto3.client('cloudwatch', region_name=AWS_REGION)

    def get_metric(metric_name):
        response = client.get_metric_statistics(
            Namespace='AWS/WAFV2',
            MetricName=metric_name,
            Dimensions=[
                {'Name': 'WebACL', 'Value': WAF_WEB_ACL_NAME},
                {'Name': 'Rule', 'Value': 'ALL'},
                {'Name': 'Region', 'Value': AWS_REGION},
            ],
            StartTime=start_dt,
            EndTime=end_dt,
            Period=86400,
            Statistics=['Sum']
        )
        try:
            if response['Datapoints']:
                return int(sum([dp['Sum'] for dp in response['Datapoints']]))

            print(f"⚠️ No datapoints for WAF metric: {metric_name}")
            return 0
        except Exception as e:
            print(f"❌ Error fetching WAF metric {metric_name}: {e}")
            return 0

    return {
        'allowed': get_metric('AllowedRequests'),
        'blocked': get_metric('BlockedRequests')
    }


def get_total_cost(start_iso, end_iso):
    client = boto3.client('ce', region_name='us-east-1')
    try:
        response = client.get_cost_and_usage(
            TimePeriod={'Start': start_iso, 'End': end_iso},
            Granularity='MONTHLY',
            Metrics=['UnblendedCost']
        )
        if response['ResultsByTime']:
            return float(response['ResultsByTime'][0]['Total']['UnblendedCost']['Amount'])
        return 0.0
    except Exception as e:
        print(f"❌ Error fetching AWS Cost: {e}")
        return 0.0


def send_discord_report():
    print("🚀 Starting Monthly Report Generation...")
    current_period, prev_period = get_date_ranges()

    dd_data = get_datadog_metrics(current_period['start_ts'], current_period['end_ts'])

    waf_data = get_aws_waf_stats(current_period['start_dt'], current_period['end_dt'])

    curr_cost = get_total_cost(current_period['start_iso'], current_period['end_iso'])
    prev_cost = get_total_cost(prev_period['start_iso'], prev_period['end_iso'])

    cost_diff = curr_cost - prev_cost
    cost_diff_str = f"+${cost_diff:.2f}" if cost_diff >= 0 else f"-${abs(cost_diff):.2f}"
    cost_emoji = "📈" if cost_diff > 0 else "📉" if cost_diff < 0 else "➡️"

    total_req = waf_data['allowed'] + waf_data['blocked']
    if total_req == 0: total_req = 1

    message = f"""
📊 **[Eatda] {current_period['month_str']} 월간 통합 리포트**

**1. Datadog (서비스 품질)**
- 🩺 **가용성 SLO:** `{dd_data['slo_avail']:.3f}%`
- 🐢 **응답속도 SLO:** `{dd_data['slo_latency']:.3f}%`
- 🚨 **발생한 장애:** `{dd_data['alert_count']}건`

**2. AWS WAF (보안)**
- 🛡️ **총 요청:** `{total_req:,}건`
- ✅ **허용됨:** `{waf_data['allowed']:,}건` ({waf_data['allowed'] / total_req * 100:.1f}%)
- 🚫 **차단됨:** `{waf_data['blocked']:,}건`

**3. AWS Cost (비용)**
- 💰 **이번 달:** `${curr_cost:.2f}`
- {cost_emoji} **전월 대비:** `{cost_diff_str}` ({'증가' if cost_diff > 0 else '감소' if cost_diff < 0 else '변동없음'})
""".strip()

    try:
        requests.post(DISCORD_WEBHOOK_URL, json={
            "username": "Eatda Manager",
            "embeds": [{
                "title": f"📅 {current_period['month_str']} 운영 결산 보고",
                "description": message,
                "color": 5763719 if dd_data['slo_avail'] >= 99 else 15548997
            }]
        })
        print("✅ Report sent successfully to Discord!")
    except Exception as e:
        print(f"❌ Failed to send Discord webhook: {e}")


if __name__ == "__main__":
    send_discord_report()
