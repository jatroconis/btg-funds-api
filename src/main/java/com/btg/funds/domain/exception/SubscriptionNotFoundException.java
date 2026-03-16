package com.btg.funds.domain.exception;

public class SubscriptionNotFoundException extends RuntimeException {
    public SubscriptionNotFoundException(String id) {
        super("Suscripción no encontrada: " + id);
    }
}
