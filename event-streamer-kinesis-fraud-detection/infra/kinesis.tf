resource "aws_kinesis_stream" "transactions" {
  name             = var.stream_name
  shard_count      = 1
  retention_period = 24

  stream_mode_details {
    stream_mode = "PROVISIONED"
  }

  tags = {
    Project = "streamshield-kinesis"
    Env     = "dev"
  }
}