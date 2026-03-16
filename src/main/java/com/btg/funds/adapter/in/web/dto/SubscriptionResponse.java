package com.btg.funds.adapter.in.web.dto;

import java.math.BigDecimal;

public class SubscriptionResponse {
    private String subscriptionId;
    private String fundName;
    private BigDecimal amountLinked;
    private BigDecimal clientBalance;

    public SubscriptionResponse(String subscriptionId, String fundName, BigDecimal amountLinked, BigDecimal clientBalance) {
        this.subscriptionId = subscriptionId;
        this.fundName = fundName;
        this.amountLinked = amountLinked;
        this.clientBalance = clientBalance;
    }

    public String getSubscriptionId() { return subscriptionId; }
    public String getFundName() { return fundName; }
    public BigDecimal getAmountLinked() { return amountLinked; }
    public BigDecimal getClientBalance() { return clientBalance; }
}
