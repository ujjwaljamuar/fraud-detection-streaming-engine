package com.streamshield.consumer.service;

import org.springframework.stereotype.Service;

import com.streamshield.consumer.model.Transaction;
import com.streamshield.consumer.repository.TransactionRepository;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final FraudDetectionService fraudDetectionService;

    public TransactionService(
            TransactionRepository transactionRepository,
            FraudDetectionService fraudDetectionService) {
        this.transactionRepository = transactionRepository;
        this.fraudDetectionService = fraudDetectionService;
    }

    public void process(Transaction transaction) {

        // Evaluate fraud
        FraudResult result = fraudDetectionService.evaluate(transaction);

        if (result.fraud()) {
            System.out.println("FRAUD DETECTED | Risk Score: " + result.riskScore());
        } else {
            System.out.println("CLEAN TRANSACTION");
        }

        // Save transaction
        transactionRepository.save(transaction);
    }

    public boolean detectFraud(Transaction transaction) {
        // Implement fraud detection logic
        if (transaction.getAmount() > 100000) {
            transaction.setIsFraud(true);
            return true;
        } else {
            transaction.setIsFraud(false);
            return false;
        }
    }

    // public void saveTransaction(Transaction transaction) {
    //     transactionRepository.save(transaction);
    // }
}
