import json
import random
import time
from datetime import datetime

from faker import Faker
from kafka import KafkaProducer

from config import KAFKA_BOOTSTRAP_SERVERS, KAFKA_TOPIC

fake = Faker()

producer = KafkaProducer(
    bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
    value_serializer=lambda v: json.dumps(v).encode("utf-8"),
)

def generate_transaction():
    return {
        "transaction_id": fake.uuid4(),
        "user_id": random.randint(1000, 1100),
        "amount": round(random.uniform(10, 100000), 2),
        "currency": "INR",
        "merchant": fake.company(),
        "location": fake.city(),
        "timestamp": datetime.utcnow().isoformat()
    }

if __name__ == "__main__":
    print("Starting Kafka Producer...")

    while True:
        transaction = generate_transaction()
        # print(transaction)
        # producer.send(KAFKA_TOPIC, value=transaction)
        # print(f"Sent: {transaction}")
        if random.random() < 0.2:
            producer.send("transactions", value="INVALID_JSON")
            print(f"Sent: INVALID_JSON")
        else:
            producer.send("transactions", value=transaction)
            print(f"Sent: {transaction}")

        time.sleep(5)
