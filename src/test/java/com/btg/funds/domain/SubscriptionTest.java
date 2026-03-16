package com.btg.funds.domain;

import com.btg.funds.domain.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubscriptionTest {

    @Test
    void shouldCreateSubscriptionAndDeductBalance() {
        Client client = Client.createDefault("1", "John Doe");
        Fund fund = new Fund("f1", "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), "FPV");

        Subscription subscription = Subscription.create("sub1", client, fund, new BigDecimal("75000"), LocalDateTime.now());

        assertEquals("sub1", subscription.getId());
        assertEquals("1", subscription.getClientId());
        assertEquals("f1", subscription.getFundId());
        assertEquals(new BigDecimal("75000"), subscription.getAmount());
        assertEquals(new BigDecimal("425000"), client.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInsufficient() {
        Client client = new Client("1", "John Doe", new BigDecimal("50000"));
        Fund fund = new Fund("f1", "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), "FPV");

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            Subscription.create("sub1", client, fund, new BigDecimal("75000"), LocalDateTime.now());
        });

        assertEquals("No tiene saldo disponible para vincularse al fondo FPV_BTG_PACTUAL_RECAUDADORA", exception.getMessage());
        assertEquals(new BigDecimal("50000"), client.getBalance()); // Balance should remain unchanged
    }

    @Test
    void shouldRefundBalanceWhenSubscriptionIsCanceled() {
        Client client = new Client("1", "John Doe", new BigDecimal("500000"));
        Fund fund = new Fund("f1", "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), "FPV");

        Subscription subscription = Subscription.create("sub1", client, fund, new BigDecimal("75000"), LocalDateTime.now());
        assertEquals(new BigDecimal("425000"), client.getBalance());

        subscription.cancel(client);

        assertEquals(new BigDecimal("500000"), client.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenCancelingSubscriptionWithWrongClient() {
        Client client1 = new Client("1", "John Doe", new BigDecimal("500000"));
        Client client2 = new Client("2", "Jane Doe", new BigDecimal("500000"));
        Fund fund = new Fund("f1", "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), "FPV");

        Subscription subscription = Subscription.create("sub1", client1, fund, new BigDecimal("75000"), LocalDateTime.now());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subscription.cancel(client2);
        });

        assertEquals("La suscripción no pertenece a este cliente", exception.getMessage());
        assertEquals(new BigDecimal("425000"), client1.getBalance());
        assertEquals(new BigDecimal("500000"), client2.getBalance());
    }

    @Test
    void shouldAppendOnlyOneCancellationEventAndKeepPreviousHistory() {
        Client client = new Client("1", "John Doe", new BigDecimal("500000"));
        Fund fund = new Fund("f1", "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), "FPV");

        Subscription subscription = Subscription.create("sub1", client, fund, new BigDecimal("75000"), LocalDateTime.now());
        List<String> eventTypesBeforeCancel = subscription.getLifecycleEvents().stream().map(Subscription.LifecycleEvent::type).toList();

        subscription.cancel(client, LocalDateTime.now().plusDays(1));

        assertEquals(List.of("APERTURA"), eventTypesBeforeCancel);
        assertEquals(2, subscription.getLifecycleEvents().size());
        assertEquals("CANCELACION", subscription.getLifecycleEvents().get(1).type());
    }

    @Test
    void shouldIgnoreRepeatedCancellationWithoutChangingHistory() {
        Client client = new Client("1", "John Doe", new BigDecimal("500000"));
        Fund fund = new Fund("f1", "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), "FPV");

        Subscription subscription = Subscription.create("sub1", client, fund, new BigDecimal("75000"), LocalDateTime.now());
        subscription.cancel(client, LocalDateTime.now().plusDays(1));
        int historySizeAfterFirstCancel = subscription.getLifecycleEvents().size();
        BigDecimal balanceAfterFirstCancel = client.getBalance();

        subscription.cancel(client, LocalDateTime.now().plusDays(2));

        assertEquals(historySizeAfterFirstCancel, subscription.getLifecycleEvents().size());
        assertEquals(balanceAfterFirstCancel, client.getBalance());
        assertTrue(subscription.isCancelled());
    }
}
