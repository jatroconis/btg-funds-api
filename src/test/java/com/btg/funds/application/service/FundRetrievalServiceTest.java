package com.btg.funds.application.service;

import com.btg.funds.adapter.in.web.dto.FundResponse;
import com.btg.funds.application.port.out.FundRepository;
import com.btg.funds.application.port.out.SubscriptionRepository;
import com.btg.funds.application.port.out.TransactionRepository;
import com.btg.funds.domain.Client;
import com.btg.funds.domain.Fund;
import com.btg.funds.domain.Subscription;
import com.btg.funds.domain.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FundRetrievalServiceTest {

    private FundRepository fundRepository;
    private SubscriptionRepository subscriptionRepository;
    private TransactionRepository transactionRepository;
    private FundRetrievalService service;

    @BeforeEach
    void setUp() {
        fundRepository = mock(FundRepository.class);
        subscriptionRepository = mock(SubscriptionRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        service = new FundRetrievalService(fundRepository, subscriptionRepository, transactionRepository);
    }

    @Test
    void getAvailableFunds_ShouldReturnAllFunds() {
        Fund fund = new Fund("fund-1", "Fondo A", new BigDecimal("100"), "Categoria 1");
        when(fundRepository.findAll()).thenReturn(List.of(fund));

        List<FundResponse> responses = service.getAvailableFunds();

        assertEquals(1, responses.size());
        assertEquals("Fondo A", responses.get(0).getName());
    }

    @Test
    void getHistory_ShouldReturnClientTransactionsSortedDesc() {
        Subscription sub = new Subscription("sub-1", "client-1", "fund-1", new BigDecimal("100"), LocalDateTime.now());
        when(subscriptionRepository.findByClientId("client-1")).thenReturn(List.of(sub));
        when(fundRepository.findAll()).thenReturn(List.of(new Fund("fund-1", "Fondo A", new BigDecimal("100"), "Categoria")));
        when(transactionRepository.findBySubscriptionId("sub-1")).thenReturn(List.of(
                new Transaction("tx-1", "sub-1", "APERTURA", new BigDecimal("100"), LocalDateTime.of(2026, 1, 1, 10, 0)),
                new Transaction("tx-2", "sub-1", "CANCELACION", new BigDecimal("100"), LocalDateTime.of(2026, 1, 2, 10, 0))
        ));

        List<com.btg.funds.adapter.in.web.dto.TransactionResponse> responses = service.getHistory("client-1");

        assertEquals(2, responses.size());
        assertEquals("CANCELACION", responses.get(0).getType());
    }
}
