package com.streamshield.consumer.service;

public record FraudResult(
        boolean fraud,
        int riskScore) {
}