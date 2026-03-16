package com.btg.funds.domain;

import java.math.BigDecimal;

public class Fund {
    private String id;
    private String name;
    private BigDecimal minimumAmount;
    private String category;

    public Fund(String id, String name, BigDecimal minimumAmount, String category) {
        if (minimumAmount == null || minimumAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto mínimo debe ser mayor a cero");
        }
        this.id = id;
        this.name = name;
        this.minimumAmount = minimumAmount;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMinimumAmount() {
        return minimumAmount;
    }

    public String getCategory() {
        return category;
    }
}
