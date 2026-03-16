package com.btg.funds.application.usecase;

import com.btg.funds.adapter.in.web.dto.SubscriptionHistoryEventResponse;
import com.btg.funds.adapter.in.web.dto.SubscriptionHistoryResponse;
import com.btg.funds.application.port.in.GetSubscriptionHistoryUseCase;
import com.btg.funds.application.port.out.SubscriptionRepository;
import com.btg.funds.domain.Subscription;
import com.btg.funds.domain.exception.SubscriptionNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetSubscriptionHistoryService implements GetSubscriptionHistoryUseCase {

    private final SubscriptionRepository subscriptionRepository;

    public GetSubscriptionHistoryService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public SubscriptionHistoryResponse getHistory(String clientId, String fundId) {
        Subscription subscription = subscriptionRepository.findByClientIdAndFundId(clientId, fundId)
                .orElseThrow(() -> new SubscriptionNotFoundException(fundId));

        return new SubscriptionHistoryResponse(
                subscription.getId(),
                subscription.getLifecycleEvents().stream()
                        .map(event -> new SubscriptionHistoryEventResponse(
                                event.type(),
                                event.occurredAt(),
                                event.amount(),
                                event.detail()))
                        .toList());
    }
}
