package com.infnet.microservicesKafka.Services.paymentService.exception;

public class PaymentProcessingException extends RuntimeException {
    public PaymentProcessingException(String message) {
        super(message);
    }
}