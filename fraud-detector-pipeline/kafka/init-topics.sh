#!/bin/bash
set -e

echo "Waiting for Kafka..."
sleep 20

kafka-topics \
  --bootstrap-server kafka:9092 \
  --create \
  --if-not-exists \
  --topic transactions \
  --partitions 3 \
  --replication-factor 1

kafka-topics \
  --bootstrap-server kafka:9092 \
  --create \
  --if-not-exists \
  --topic transactions-dlq \
  --partitions 3 \
  --replication-factor 1

echo "Kafka topics created"
