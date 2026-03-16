package com.btg.funds.application.port.in;

import com.btg.funds.adapter.in.web.dto.ActiveSubscriptionResponse;
import java.util.List;

public interface GetActiveSubscriptionsUseCase {
    List<ActiveSubscriptionResponse> getActiveSubscriptions(String clientId);
}
