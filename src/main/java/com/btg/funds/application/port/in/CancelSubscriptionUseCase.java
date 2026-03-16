package com.btg.funds.application.port.in;

import com.btg.funds.adapter.in.web.dto.CancellationResponse;

public interface CancelSubscriptionUseCase {
    CancellationResponse cancelSubscription(String clientId, String fundId);
}
