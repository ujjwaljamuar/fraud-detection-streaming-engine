# Event Streaming Platform (Kafka-based)

A production-style **event-driven message streaming system** built to demonstrate **Apache Kafka fundamentals**, producer–consumer decoupling, and stream processing using **Python** and **Spring Boot**.

> ⚠️ Note  
> Fraud detection is used only as a **sample processing workload**.  
> The core objective of this project is **message streaming architecture**, not fraud logic.

---

## 📌 Project Goals

- Understand and implement **Kafka-based event streaming**
- Build a **producer → broker → consumer** pipeline
- Demonstrate **consumer groups, partitions, offsets**
- Persist streamed data into a downstream database
- Add **application-level observability**
- Keep the design **broker-agnostic** for future systems (Kafka → Kinesis)

---

## 🏗️ High-Level Architecture

Python Producer
|
v
Apache Kafka (Broker)
|
v
Spring Boot Consumer
|
v
PostgreSQL (via Prisma)


Additional components:
- Kafka UI for broker observability
- Prometheus + Grafana for application metrics

---

## 🧰 Technology Stack

### Streaming & Messaging
- Apache Kafka
- Zookeeper

### Producer
- Python
- kafka-python
- Faker (synthetic event generation)

### Consumer
- Spring Boot
- Spring Kafka
- Spring Data JPA
- Hibernate ORM

### Persistence
- PostgreSQL
- Prisma (managed DB)

### Observability
- Kafka UI
- Spring Boot Actuator
- Prometheus
- Grafana

### Infrastructure
- Docker
- Docker Compose
- Named volumes for persistence

---

## 🔄 Kafka Design

### Topics

| Topic | Purpose |
|------|--------|
| `transactions` | Incoming raw events |

(Planned extensions)
- `transactions-processed`
- `transactions-fraud`
- `transactions-dlq`

---

### Topic Creation

Topics are created automatically during Kafka startup using:

```bash
kafka-topics \
  --bootstrap-server kafka:9092 \
  --create \
  --if-not-exists \
  --topic transactions \
  --partitions 3 \
  --replication-factor 1
```