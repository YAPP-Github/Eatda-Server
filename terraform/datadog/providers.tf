terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "6.23.0"
    }

    datadog = {
      source  = "DataDog/datadog"
      version = "~> 3.80"
    }
  }
}

provider "datadog" {
  api_key = data.aws_ssm_parameter.datadog_api.value
  app_key = data.aws_ssm_parameter.datadog_app.value
  api_url = "https://api.us5.datadoghq.com/"
}
