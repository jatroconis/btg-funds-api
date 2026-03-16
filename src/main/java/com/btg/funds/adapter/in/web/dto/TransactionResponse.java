package com.btg.funds.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
    private String fundName;
    private String type;
    private BigDecimal amount;
    private LocalDateTime date;

    public TransactionResponse(String fundName, String type, BigDecimal amount, LocalDateTime date) {
        this.fundName = fundName;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public String getFundName() { return fundName; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }
}
