package com.btg.funds.adapter.in.web.dto;

import java.time.LocalDateTime;

public class ActiveSubscriptionResponse {
    private final String subscriptionId;
    private final String fundId;
    private final String status;
    private final LocalDateTime subscribedAt;

    public ActiveSubscriptionResponse(String subscriptionId, String fundId, String status, LocalDateTime subscribedAt) {
        this.subscriptionId = subscriptionId;
        this.fundId = fundId;
        this.status = status;
        this.subscribedAt = subscribedAt;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getFundId() {
        return fundId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getSubscribedAt() {
        return subscribedAt;
    }
}
