package com.btg.funds.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private String id;
    private String subscriptionId;
    private String type;
    private BigDecimal amount;
    private LocalDateTime date;

    public Transaction(String id, String subscriptionId, String type, BigDecimal amount, LocalDateTime date) {
        if (!"APERTURA".equals(type) && !"CANCELACION".equals(type)) {
            throw new IllegalArgumentException("Type must be APERTURA or CANCELACION");
        }
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public Transaction(String subscriptionId, String type, BigDecimal amount, LocalDateTime date) {
        this(UUID.randomUUID().toString(), subscriptionId, type, amount, date);
    }

    public String getId() {
        return id;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
