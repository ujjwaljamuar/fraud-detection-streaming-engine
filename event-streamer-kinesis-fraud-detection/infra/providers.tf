terraform {
  required_version = ">= 1.5.0"

  backend "s3" {
    bucket = "uj-terraform-state-files"
    key    = "streamshield-kinesis-tfstate/terraform.tfstate"
    region = "ap-south-1"
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}
