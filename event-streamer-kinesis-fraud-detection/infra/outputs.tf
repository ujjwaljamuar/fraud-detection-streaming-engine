output "kinesis_stream_name" {
  value = aws_kinesis_stream.transactions.name
}

output "kinesis_stream_arn" {
  value = aws_kinesis_stream.transactions.arn
}
