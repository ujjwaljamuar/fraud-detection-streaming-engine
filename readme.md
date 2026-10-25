# StreamShield: Real-Time Fraud Detection Streaming Engine

A hands-on event streaming project that generates synthetic payment transactions, routes them through a streaming platform, and evaluates them for fraud indicators. The repository contains two implementations of the ingestion side: a local Apache Kafka pipeline with a Spring Boot consumer, and an AWS Kinesis producer backed by Terraform-managed infrastructure.

The Kafka consumer currently applies a configurable rule interface with an amount threshold rule: transactions over INR 50,000 receive a risk score of 50 and are marked as potentially fraudulent. This is a learning and demonstration system, not a production fraud decision service.

## What’s in the repository

```text
.
├── event-streamer-kafka-fraud-detection/
│   ├── fraud-detector-pipeline/
│   │   ├── docker-compose.yaml       # Kafka, Zookeeper, Kafka UI, Prometheus, Grafana
│   │   ├── kafka/init-topics.sh      # Creates transactions and transactions-dlq
│   │   ├── producer/                 # Python transaction generator
│   │   └── prometheus/               # Prometheus scrape configuration
│   └── fraud-detector-consumer/      # Spring Boot Kafka consumer and fraud rules
└── event-streamer-kinesis-fraud-detection/
    ├── producer/                     # Python Kinesis transaction generator
    └── infra/                        # Terraform Kinesis stream resources
```

## Kafka pipeline

The Kafka path is the most complete end-to-end example in this repository:

```text
Synthetic Python producer → Kafka `transactions` topic → Spring Boot consumer
                                                        ├─ fraud rules and risk score
                                                        ├─ PostgreSQL persistence (JPA)
                                                        └─ Prometheus counters
```

The local Docker Compose stack includes Zookeeper, a single Kafka broker, Kafka UI, Prometheus, and Grafana. Kafka exposes `localhost:29092` to applications running on your machine and `kafka:9092` to other Compose services. Startup creates the `transactions` and `transactions-dlq` topics, each with three partitions.

The producer emits JSON transactions with an ID, user, amount, currency, merchant, location, and timestamp, about every five seconds. It intentionally sends an invalid sample occasionally to exercise consumer error handling. The consumer listens in the `fraud-detection-group`, evaluates registered `FraudRule` implementations, and persists transactions through Spring Data JPA. Its configured Kafka error handler retries failed records and publishes records that still fail to a matching dead-letter topic (for `transactions`, this is `transactions-dlq`).

### Run Kafka locally

Prerequisites: Docker with the Compose plugin, Python 3, and Java 17. PostgreSQL must also be available if you want the consumer to persist records.

1. Start the local infrastructure:

   ```bash
   cd event-streamer-kafka-fraud-detection/fraud-detector-pipeline
   docker compose up -d
   ```

2. Install the producer dependencies and start generating transactions:

   ```bash
   cd producer
   python -m pip install -r requirements.txt
   python producer.py
   ```

   The producer connects to `localhost:29092` and publishes to `transactions` by default. Keep this process running while consuming events.

3. Configure and run the Spring Boot consumer from another terminal. The repository does not include its Spring `application.yml` because that file is ignored; provide your own local configuration for Kafka at `localhost:29092` and PostgreSQL connection/JPA settings before starting it:

   ```bash
   cd event-streamer-kafka-fraud-detection/fraud-detector-consumer
   ./mvnw spring-boot:run
   ```

   On Windows, use `mvnw.cmd spring-boot:run`.

Useful local interfaces:

| Service | Address |
| --- | --- |
| Kafka external listener | `localhost:29092` |
| Kafka UI | <http://localhost:8080> |
| Prometheus | <http://localhost:9090> |
| Grafana | <http://localhost:3000> |

Prometheus is configured to scrape the Spring Actuator Prometheus endpoint at `host.docker.internal:8081/actuator/prometheus`. Make sure your local consumer exposes that endpoint on port 8081 (and enables the Prometheus endpoint) if you want metrics to appear. The consumer registers `fraud.records.consumed` and `fraud.records.flagged` counters.

To stop the local services, run `docker compose down` from `fraud-detector-pipeline`. Compose named volumes retain Kafka, Zookeeper, and Grafana data; `docker compose down -v` removes those volumes as well.

## AWS Kinesis path

The Kinesis example separates stream provisioning from event production:

- Terraform in `event-streamer-kinesis-fraud-detection/infra` defines a provisioned Kinesis stream with one shard and 24-hour retention. The default stream name is `transactions-stream` in `ap-south-1`.
- The Python producer creates transaction-shaped JSON events and writes each one with `PutRecord`, using the user ID as the partition key.

To provision the stream, use an AWS account configured with credentials for Terraform and the AWS provider, plus Terraform 1.5 or later:

```bash
cd event-streamer-kinesis-fraud-detection/infra
terraform init
terraform plan
terraform apply
```

The Terraform backend is configured to use an S3 state bucket; ensure that backend exists and is accessible to your AWS identity before initializing. The producer’s settings are in `producer/config.py`; its stream name and region must match the provisioned stream. Install dependencies and run it with:

```bash
cd event-streamer-kinesis-fraud-detection/producer
python -m pip install -r requirements.txt
python producer.py
```

The producer also needs AWS credentials with permission to write records to the stream. Terraform destroy removes the provisioned stream:

```bash
cd ../infra
terraform destroy
```

## Transaction shape

Both producers generate records broadly in this form:

```json
{
  "transaction_id": "a UUID",
  "user_id": 1042,
  "amount": 52350.75,
  "currency": "INR",
  "merchant": "Example Trading",
  "location": "Pune",
  "timestamp": "2026-10-25T12:00:00"
}
```

The Kafka consumer’s `Transaction` entity adds `isFraud` and `riskScore` fields for its evaluation results. The included amount rule flags amounts greater than 50,000 and contributes 50 risk points. Rules implement the `FraudRule` interface and can be added as Spring components.

## Current scope

- Kafka ingestion, a Spring Kafka consumer, a basic fraud rule, retry/dead-letter handling, JPA persistence, and metric counters are present in the code.
- Kinesis ingestion and Terraform stream provisioning are present as a separate path. There is not currently a Kinesis consumer in this repository.
- PostgreSQL setup and the consumer’s Spring configuration are environment-specific and are not committed.
- The Kafka producer’s malformed test messages are plain strings, while the consumer expects transaction JSON; they are intended to exercise the failure path.

## Development notes

The Kafka consumer project includes a Maven wrapper and a Spring Boot test source. The Kafka and Kinesis Python producers each declare their dependencies in their respective `requirements.txt` files. Keep cloud credentials and database secrets in your local environment or secret manager; do not commit them to this repository.
