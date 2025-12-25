package com.streamshield.consumer.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.streamshield.consumer.model.Transaction;
import com.streamshield.consumer.service.TransactionService;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class TransactionConsumer {

    private final TransactionService transactionService;
    private final Counter recordsConsumed;
    private final Counter fraudDetected;

    @Autowired
    public TransactionConsumer(TransactionService transactionService, MeterRegistry meterRegistry) {
        this.transactionService = transactionService;

        this.recordsConsumed = Counter.builder("fraud.records.consumed")
                .description("Total Kafka records consumed")
                .register(meterRegistry);

        this.fraudDetected = Counter.builder("fraud.records.flagged")
                .description("Total fraudulent transactions detected")
                .register(meterRegistry);

    }

    @KafkaListener(topics = "transactions", groupId = "fraud-detection-group")
    public void consume(Transaction transaction) {
        try {
            recordsConsumed.increment();

            System.out.println("----------------------------------------");
            System.out.println("Received transaction:");
            System.out.println(transaction.getTransaction_id());
            System.out.println(transaction.getUser_id());
            System.out.println(transaction.getAmount());
            System.out.println(transaction.getCurrency());
            System.out.println(transaction.getMerchant());
            System.out.println(transaction.getLocation());
            System.out.println(transaction.getTimestamp());

            boolean isFraud = transactionService.detectFraud(transaction);

            if (isFraud) {
                fraudDetected.increment();
            }

            // Save transaction to DB
            transactionService.saveTransaction(transaction);

            System.out.println("Transaction " + transaction.getTransaction_id() + " processed and saved.");
        } catch (Exception e) {
            System.err.println("Failed to process message: ");
            e.printStackTrace();
        }
    }
}
