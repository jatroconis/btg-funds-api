package com.btg.funds.application.port.in;

import com.btg.funds.adapter.in.web.dto.SubscriptionHistoryResponse;

public interface GetSubscriptionHistoryUseCase {
    SubscriptionHistoryResponse getHistory(String clientId, String fundId);
}
