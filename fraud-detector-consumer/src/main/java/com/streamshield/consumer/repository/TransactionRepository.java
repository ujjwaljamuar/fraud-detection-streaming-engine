package com.streamshield.consumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streamshield.consumer.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
}
