package com.btg.funds.adapter.in.web.dto;

import java.util.List;

public class SubscriptionHistoryResponse {
    private final String subscriptionId;
    private final List<SubscriptionHistoryEventResponse> events;

    public SubscriptionHistoryResponse(String subscriptionId, List<SubscriptionHistoryEventResponse> events) {
        this.subscriptionId = subscriptionId;
        this.events = events;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public List<SubscriptionHistoryEventResponse> getEvents() {
        return events;
    }
}
