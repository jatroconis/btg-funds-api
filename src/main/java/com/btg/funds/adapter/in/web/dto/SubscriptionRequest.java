package com.btg.funds.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public class SubscriptionRequest {
    @NotBlank(message = "fundId is mandatory")
    private String fundId;

    public String getFundId() { return fundId; }
    public void setFundId(String fundId) { this.fundId = fundId; }
}
