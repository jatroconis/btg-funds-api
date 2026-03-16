package com.btg.funds.domain.exception;

public class FundNotFoundException extends RuntimeException {
    public FundNotFoundException(String id) {
        super("Fondo no encontrado: " + id);
    }
}
