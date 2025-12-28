variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "ap-south-1"
}

variable "stream_name" {
  description = "Kinesis stream name"
  type        = string
  default     = "transactions-stream"
}
