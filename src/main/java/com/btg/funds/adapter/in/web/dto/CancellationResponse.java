package com.btg.funds.adapter.in.web.dto;

import java.math.BigDecimal;

public class CancellationResponse {
    private String fundId;
    private BigDecimal amountReturned;
    private BigDecimal clientBalance;

    public CancellationResponse(String fundId, BigDecimal amountReturned, BigDecimal clientBalance) {
        this.fundId = fundId;
        this.amountReturned = amountReturned;
        this.clientBalance = clientBalance;
    }

    public String getFundId() { return fundId; }
    public BigDecimal getAmountReturned() { return amountReturned; }
    public BigDecimal getClientBalance() { return clientBalance; }
}
