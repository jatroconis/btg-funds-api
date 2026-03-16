package com.btg.funds.domain.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String id) {
        super("Cliente no encontrado: " + id);
    }
}
