package com.btg.funds.infrastructure.adapter.out.dynamodb.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.math.BigDecimal;

@DynamoDbBean
public class SubscriptionDynamoDbEntity {
    private String pk; // CLIENT#<clientId>
    private String sk; // SUB#<fundId>
    
    private String id; // kept for compatibility if needed
    private String clientId;
    private String fundId;
    private BigDecimal amount;
    private String date; // Store as ISO-8601 string for DynamoDB
    private String status;
    private String lifecycleEvents;

    @DynamoDbPartitionKey
    public String getPk() { return pk; }
    public void setPk(String pk) { this.pk = pk; }

    @DynamoDbSortKey
    public String getSk() { return sk; }
    public void setSk(String sk) { this.sk = sk; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getFundId() { return fundId; }
    public void setFundId(String fundId) { this.fundId = fundId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLifecycleEvents() { return lifecycleEvents; }
    public void setLifecycleEvents(String lifecycleEvents) { this.lifecycleEvents = lifecycleEvents; }
}
