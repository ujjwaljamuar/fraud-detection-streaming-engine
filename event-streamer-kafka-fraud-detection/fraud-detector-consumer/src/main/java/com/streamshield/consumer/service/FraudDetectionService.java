package com.streamshield.consumer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.streamshield.consumer.model.Transaction;
import com.streamshield.consumer.rules.FraudRule;;

@Service
public class FraudDetectionService {

    private final List<FraudRule> fraudRules;

    public FraudDetectionService(List<FraudRule> fraudRules) {
        this.fraudRules = fraudRules;
    }

    public FraudResult evaluate(Transaction transaction) {

        int totalRiskScore = 0;
        boolean isFraud = false;

        for (FraudRule rule : fraudRules) {
            if (rule.evaluate(transaction)) {
                totalRiskScore += rule.riskScore();
                isFraud = true;
                transaction.setIsFraud(isFraud);
                transaction.setRiskScore(totalRiskScore);
            }
        }

        return new FraudResult(isFraud, totalRiskScore);
    }
}