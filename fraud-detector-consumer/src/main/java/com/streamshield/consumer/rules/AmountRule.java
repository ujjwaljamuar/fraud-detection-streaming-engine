package com.streamshield.consumer.rules;

import org.springframework.stereotype.Component;
import com.streamshield.consumer.model.Transaction;

@Component
public class AmountRule implements FraudRule {

    private static final double MAX_ALLOWED_AMOUNT = 50_000.00;

    @Override
    public boolean evaluate(Transaction transaction) {
        return transaction.getAmount() > MAX_ALLOWED_AMOUNT;
    }

    @Override
    public String ruleName() {
        return "AMOUNT_THRESHOLD_RULE";
    }

    @Override
    public int riskScore() {
        return 50;
    }
}