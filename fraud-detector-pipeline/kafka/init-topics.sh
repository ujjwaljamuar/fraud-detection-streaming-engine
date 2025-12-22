#!/bin/bash

# create topic 'transactions'
kafka-topics --bootstrap-server kafka:9092 --create \
    --if-not-exists \
    --topic transactions \
    --partitions 3 \
    --replication-factor 1