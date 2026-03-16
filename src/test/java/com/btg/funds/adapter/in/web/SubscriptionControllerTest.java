package com.btg.funds.adapter.in.web;

import com.btg.funds.adapter.in.web.advice.GlobalExceptionHandler;
import com.btg.funds.adapter.in.web.dto.ActiveSubscriptionResponse;
import com.btg.funds.adapter.in.web.dto.CancellationResponse;
import com.btg.funds.adapter.in.web.dto.SubscriptionHistoryEventResponse;
import com.btg.funds.adapter.in.web.dto.SubscriptionHistoryResponse;
import com.btg.funds.adapter.in.web.dto.SubscriptionResponse;
import com.btg.funds.application.port.in.CancelSubscriptionUseCase;
import com.btg.funds.application.port.in.GetActiveSubscriptionsUseCase;
import com.btg.funds.application.port.in.GetSubscriptionHistoryUseCase;
import com.btg.funds.application.port.in.SubscribeToFundUseCase;
import com.btg.funds.domain.exception.InsufficientBalanceException;
import com.btg.funds.domain.exception.SubscriptionNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SubscriptionController.class)
@Import(GlobalExceptionHandler.class)
class SubscriptionControllerTest {

    private static final String INSUFFICIENT_BALANCE_JSON =
            "{\"error\":\"INSUFFICIENT_BALANCE\",\"message\":\"No tiene saldo disponible para vincularse al fondo FDO-ACCIONES\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscribeToFundUseCase subscribeToFundUseCase;

    @MockBean
    private CancelSubscriptionUseCase cancelSubscriptionUseCase;

    @MockBean
    private GetSubscriptionHistoryUseCase getSubscriptionHistoryUseCase;

    @MockBean
    private GetActiveSubscriptionsUseCase getActiveSubscriptionsUseCase;

    @Test
    void subscribe_ShouldReturnCreated() throws Exception {
        SubscriptionResponse response = new SubscriptionResponse(
                "sub-1",
                "Fondo A",
                new BigDecimal("75000"),
                new BigDecimal("425000")
        );
        when(subscribeToFundUseCase.subscribe("client-1", "fund-1")).thenReturn(response);

        mockMvc.perform(post("/api/v1/clients/{clientId}/subscriptions", "client-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fundId\":\"fund-1\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subscriptionId").value("sub-1"))
                .andExpect(jsonPath("$.fundName").value("Fondo A"))
                .andExpect(jsonPath("$.amountLinked").value(75000))
                .andExpect(jsonPath("$.clientBalance").value(425000));
    }

    @Test
    void subscribe_ShouldReturnExactInsufficientBalanceJson() throws Exception {
        when(subscribeToFundUseCase.subscribe("client-1", "fund-acciones"))
                .thenThrow(new InsufficientBalanceException("FDO-ACCIONES"));

        mockMvc.perform(post("/api/v1/clients/{clientId}/subscriptions", "client-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fundId\":\"fund-acciones\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(INSUFFICIENT_BALANCE_JSON));
    }

    @Test
    void cancelSubscription_ShouldReturnOk() throws Exception {
        CancellationResponse response = new CancellationResponse(
                "fund-1",
                new BigDecimal("75000"),
                new BigDecimal("500000")
        );
        when(cancelSubscriptionUseCase.cancelSubscription("client-1", "fund-1")).thenReturn(response);

        mockMvc.perform(delete("/api/v1/clients/{clientId}/subscriptions/{fundId}", "client-1", "fund-1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fundId").value("fund-1"))
                .andExpect(jsonPath("$.amountReturned").value(75000))
                .andExpect(jsonPath("$.clientBalance").value(500000));
    }

    @Test
    void getSubscriptionHistory_ShouldReturnOrderedEvents() throws Exception {
        SubscriptionHistoryResponse response = new SubscriptionHistoryResponse(
                "sub-1",
                List.of(
                        new SubscriptionHistoryEventResponse("APERTURA", LocalDateTime.of(2026, 3, 1, 10, 0), new BigDecimal("75000"), "Subscription opened"),
                        new SubscriptionHistoryEventResponse("CANCELACION", LocalDateTime.of(2026, 3, 2, 10, 0), new BigDecimal("75000"), "Subscription cancelled")
                )
        );
        when(getSubscriptionHistoryUseCase.getHistory("client-1", "fund-1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/clients/{clientId}/subscriptions/{fundId}/history", "client-1", "fund-1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subscriptionId").value("sub-1"))
                .andExpect(jsonPath("$.events[0].eventType").value("APERTURA"))
                .andExpect(jsonPath("$.events[1].eventType").value("CANCELACION"));
    }

    @Test
    void getSubscriptionHistory_ShouldReturnNotFound() throws Exception {
        when(getSubscriptionHistoryUseCase.getHistory("client-1", "fund-404"))
                .thenThrow(new SubscriptionNotFoundException("fund-404"));

        mockMvc.perform(get("/api/v1/clients/{clientId}/subscriptions/{fundId}/history", "client-1", "fund-404"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("SUBSCRIPTION_NOT_FOUND"));
    }

    @Test
    void getActiveSubscriptions_ShouldReturnOnlyActiveOnes() throws Exception {
        ActiveSubscriptionResponse active = new ActiveSubscriptionResponse(
                "sub-1",
                "fund-1",
                "ACTIVE",
                LocalDateTime.of(2026, 3, 1, 10, 0)
        );
        when(getActiveSubscriptionsUseCase.getActiveSubscriptions("client-1"))
                .thenReturn(List.of(active));

        mockMvc.perform(get("/api/v1/clients/{clientId}/subscriptions/active", "client-1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].subscriptionId").value("sub-1"))
                .andExpect(jsonPath("$[0].fundId").value("fund-1"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void getActiveSubscriptions_ShouldReturnEmptyArray() throws Exception {
        when(getActiveSubscriptionsUseCase.getActiveSubscriptions("client-1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/clients/{clientId}/subscriptions/active", "client-1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }
}
