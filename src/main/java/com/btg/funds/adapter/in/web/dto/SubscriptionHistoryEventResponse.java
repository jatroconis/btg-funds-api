package com.btg.funds.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SubscriptionHistoryEventResponse {
    private final String eventType;
    private final LocalDateTime eventTimestamp;
    private final BigDecimal amount;
    private final String detail;

    public SubscriptionHistoryEventResponse(String eventType, LocalDateTime eventTimestamp, BigDecimal amount, String detail) {
        this.eventType = eventType;
        this.eventTimestamp = eventTimestamp;
        this.amount = amount;
        this.detail = detail;
    }

    public String getEventType() {
        return eventType;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDetail() {
        return detail;
    }
}
