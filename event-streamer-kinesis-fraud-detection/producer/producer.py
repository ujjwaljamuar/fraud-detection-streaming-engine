import json
import time
import uuid
import random
from datetime import datetime

import boto3
from faker import Faker

from config import AWS_REGION, KINESIS_STREAM_NAME, PRODUCER_SLEEP_SECONDS

fake = Faker()

kinesis = boto3.client("kinesis", region_name=AWS_REGION)


def generate_transaction():
    return {
        "transaction_id": str(uuid.uuid4()),
        "user_id": random.randint(1, 10000),
        "amount": round(random.uniform(10, 150000), 2),
        "currency": "INR",
        "merchant": fake.company(),
        "location": fake.city(),
        "timestamp": datetime.utcnow().isoformat()
    }


def send_to_kinesis(transaction: dict):
    response = kinesis.put_record(
        StreamName=KINESIS_STREAM_NAME,
        Data=json.dumps(transaction),
        PartitionKey=str(transaction["user_id"])
    )

    print(
            f"Sent transaction_id={transaction['transaction_id']} "
            f"Shard={response['ShardId']} "
            f"Seq={response['SequenceNumber']}"
        )


if __name__ == "__main__":
    print("Starting Kinesis Producer...")

    while True:
        txn = generate_transaction()
        send_to_kinesis(txn)
        time.sleep(PRODUCER_SLEEP_SECONDS)