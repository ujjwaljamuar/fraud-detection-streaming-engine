package com.streamshield.consumer.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.streamshield.consumer.model.Transaction;
import com.streamshield.consumer.service.FraudDetectionService;
import com.streamshield.consumer.service.FraudResult;
import com.streamshield.consumer.service.TransactionService;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class TransactionConsumer {

    private final TransactionService transactionService;
    private final FraudDetectionService fraudDetectionService;

    private final Counter recordsConsumed;
    private final Counter fraudDetected;

    @Autowired
    public TransactionConsumer(TransactionService transactionService, MeterRegistry meterRegistry, FraudDetectionService fraudDetectionService) {
        this.transactionService = transactionService;

        this.recordsConsumed = Counter.builder("fraud.records.consumed")
                .description("Total Kafka records consumed")
                .register(meterRegistry);

        this.fraudDetected = Counter.builder("fraud.records.flagged")
                .description("Total fraudulent transactions detected")
                .register(meterRegistry);
        this.fraudDetectionService = fraudDetectionService;

    }

    @KafkaListener(topics = "transactions", groupId = "fraud-detection-group")
    public void consume(Transaction transaction) {
        try {
            recordsConsumed.increment();

            System.out.println("----------------------------------------");
            System.out.println("Received transaction:");
            System.out.println("Transaction ID : " + transaction.getTransaction_id());
            System.out.println("User ID        : " + transaction.getUser_id());
            System.out.println("Amount         : " + transaction.getAmount());
            System.out.println("Currency       : " + transaction.getCurrency());
            System.out.println("Merchant       : " + transaction.getMerchant());
            System.out.println("Location       : " + transaction.getLocation());
            System.out.println("Timestamp      : " + transaction.getTimestamp());

            FraudResult result = fraudDetectionService.evaluate(transaction);

            if (result.fraud()) {
                fraudDetected.increment();
                // System.out.println("FRAUD DETECTED | Risk Score: " + result.riskScore());
            } else {
                // System.out.println("CLEAN TRANSACTION");
            }

            // Save transaction
            // transactionService.saveTransaction(transaction);
            transactionService.process(transaction);

            System.out.println(
                "Transaction " + transaction.getTransaction_id() + " processed and saved."
            );

        } catch (Exception e) {
            System.err.println("Failed to process message");
            e.printStackTrace();
        }
    }

}
