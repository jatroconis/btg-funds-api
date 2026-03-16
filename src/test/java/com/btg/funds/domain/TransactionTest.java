package com.btg.funds.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void shouldCreateAperturaTransactionWithUuid() {
        Transaction tx = new Transaction("sub1", "APERTURA", new BigDecimal("75000"), LocalDateTime.now());

        assertNotNull(tx.getId());
        assertEquals("sub1", tx.getSubscriptionId());
        assertEquals("APERTURA", tx.getType());
        assertEquals(new BigDecimal("75000"), tx.getAmount());
    }

    @Test
    void shouldCreateCancelacionTransactionWithUuid() {
        Transaction tx = new Transaction("sub1", "CANCELACION", new BigDecimal("75000"), LocalDateTime.now());

        assertNotNull(tx.getId());
        assertEquals("CANCELACION", tx.getType());
    }

    @Test
    void shouldThrowExceptionForInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction("sub1", "INVALID", new BigDecimal("75000"), LocalDateTime.now());
        });
    }
}
