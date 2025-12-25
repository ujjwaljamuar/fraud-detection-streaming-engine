package com.streamshield.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.streamshield.consumer.model.Transaction;
import com.streamshield.consumer.repository.TransactionRepository;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
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

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }    
}
