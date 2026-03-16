package com.btg.funds.adapter.in.web;

import com.btg.funds.adapter.in.web.dto.ActiveSubscriptionResponse;
import com.btg.funds.adapter.in.web.dto.CancellationResponse;
import com.btg.funds.adapter.in.web.dto.SubscriptionHistoryResponse;
import com.btg.funds.adapter.in.web.dto.SubscriptionRequest;
import com.btg.funds.adapter.in.web.dto.SubscriptionResponse;
import com.btg.funds.application.port.in.CancelSubscriptionUseCase;
import com.btg.funds.application.port.in.GetActiveSubscriptionsUseCase;
import com.btg.funds.application.port.in.GetSubscriptionHistoryUseCase;
import com.btg.funds.application.port.in.SubscribeToFundUseCase;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
public class SubscriptionController {

    private final SubscribeToFundUseCase subscribeToFundUseCase;
    private final CancelSubscriptionUseCase cancelSubscriptionUseCase;
    private final GetSubscriptionHistoryUseCase getSubscriptionHistoryUseCase;
    private final GetActiveSubscriptionsUseCase getActiveSubscriptionsUseCase;

    public SubscriptionController(SubscribeToFundUseCase subscribeToFundUseCase,
                                  CancelSubscriptionUseCase cancelSubscriptionUseCase,
                                  GetSubscriptionHistoryUseCase getSubscriptionHistoryUseCase,
                                  GetActiveSubscriptionsUseCase getActiveSubscriptionsUseCase) {
        this.subscribeToFundUseCase = subscribeToFundUseCase;
        this.cancelSubscriptionUseCase = cancelSubscriptionUseCase;
        this.getSubscriptionHistoryUseCase = getSubscriptionHistoryUseCase;
        this.getActiveSubscriptionsUseCase = getActiveSubscriptionsUseCase;
    }

    @Operation(summary = "Subscribe client to fund")
    @PostMapping("/{clientId}/subscriptions")
    public ResponseEntity<SubscriptionResponse> subscribe(@PathVariable String clientId,
                                                          @Valid @RequestBody SubscriptionRequest request) {
        SubscriptionResponse response = subscribeToFundUseCase.subscribe(clientId, request.getFundId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Cancel client subscription")
    @DeleteMapping("/{clientId}/subscriptions/{fundId}")
    public ResponseEntity<CancellationResponse> cancelSubscription(@PathVariable String clientId,
                                                                   @PathVariable String fundId) {
        CancellationResponse response = cancelSubscriptionUseCase.cancelSubscription(clientId, fundId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get subscription lifecycle history")
    @GetMapping("/{clientId}/subscriptions/{fundId}/history")
    public ResponseEntity<SubscriptionHistoryResponse> getSubscriptionHistory(@PathVariable String clientId,
                                                                              @PathVariable String fundId) {
        return ResponseEntity.ok(getSubscriptionHistoryUseCase.getHistory(clientId, fundId));
    }

    @Operation(summary = "Get active subscriptions for client")
    @GetMapping("/{clientId}/subscriptions/active")
    public ResponseEntity<List<ActiveSubscriptionResponse>> getActiveSubscriptions(@PathVariable String clientId) {
        return ResponseEntity.ok(getActiveSubscriptionsUseCase.getActiveSubscriptions(clientId));
    }
}
