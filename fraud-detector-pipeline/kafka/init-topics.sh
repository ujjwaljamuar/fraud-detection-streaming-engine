#!/bin/bash

# create topic 'transactions'
kafka-topics --bootstrap-server kafka:9092 --create \
    --if-not-exists \
    --topic transactions \
    --replication-factor 1 \
    --partitions 3