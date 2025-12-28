package com.streamshield.consumer.rules;

import com.streamshield.consumer.model.Transaction;

public interface FraudRule {

    /**
     * @return true if transaction violates this rule
     */
    boolean evaluate(Transaction transaction);

    /**
     * @return rule name for audit & metrics
     */
    String ruleName();

    /**
     * @return risk score contributed by this rule
     */
    int riskScore();
}
