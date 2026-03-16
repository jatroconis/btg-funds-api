package com.btg.funds.application.usecase;

import com.btg.funds.adapter.in.web.dto.ActiveSubscriptionResponse;
import com.btg.funds.application.port.in.GetActiveSubscriptionsUseCase;
import com.btg.funds.application.port.out.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetActiveSubscriptionsService implements GetActiveSubscriptionsUseCase {

    private final SubscriptionRepository subscriptionRepository;

    public GetActiveSubscriptionsService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public List<ActiveSubscriptionResponse> getActiveSubscriptions(String clientId) {
        return subscriptionRepository.findByClientId(clientId).stream()
                .filter(subscription -> !subscription.isCancelled())
                .map(subscription -> new ActiveSubscriptionResponse(
                        subscription.getId(),
                        subscription.getFundId(),
                        subscription.getStatus().name(),
                        subscription.getDate()))
                .toList();
    }
}
