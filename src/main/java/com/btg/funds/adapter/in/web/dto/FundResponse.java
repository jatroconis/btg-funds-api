package com.btg.funds.adapter.in.web.dto;

import java.math.BigDecimal;

public class FundResponse {
    private String id;
    private String name;
    private BigDecimal minimumAmount;
    private String category;

    public FundResponse(String id, String name, BigDecimal minimumAmount, String category) {
        this.id = id;
        this.name = name;
        this.minimumAmount = minimumAmount;
        this.category = category;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getMinimumAmount() { return minimumAmount; }
    public String getCategory() { return category; }
}
