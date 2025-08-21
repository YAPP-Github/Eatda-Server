terraform {
  backend "s3" {
    bucket         = "eatda-tf-state"
    key            = "db-migration/terraform.tfstate"
    region         = "ap-northeast-2"
    encrypt        = true
    dynamodb_table = "eatda-tf-lock"
  }
}
