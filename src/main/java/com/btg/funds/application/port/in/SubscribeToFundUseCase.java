package com.btg.funds.application.port.in;

import com.btg.funds.adapter.in.web.dto.SubscriptionResponse;
import java.math.BigDecimal;

public interface SubscribeToFundUseCase {
    SubscriptionResponse subscribe(String clientId, String fundId);
}
