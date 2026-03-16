package com.btg.funds.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientTest {

    @Test
    void shouldInitializeWithDefaultBalance() {
        Client client = Client.createDefault("1", "John Doe");
        assertEquals(new BigDecimal("500000"), client.getBalance());
        assertEquals("1", client.getId());
        assertEquals("John Doe", client.getName());
    }

    @Test
    void shouldDeductBalance() {
        Client client = Client.createDefault("1", "John Doe");
        client.deductBalance(new BigDecimal("75000"));
        assertEquals(new BigDecimal("425000"), client.getBalance());
    }

    @Test
    void shouldAddBalance() {
        Client client = Client.createDefault("1", "John Doe");
        client.addBalance(new BigDecimal("75000"));
        assertEquals(new BigDecimal("575000"), client.getBalance());
    }
}
