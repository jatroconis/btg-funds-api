package com.btg.funds.infrastructure.adapter.out.dynamodb.mapper;

import com.btg.funds.domain.Transaction;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.TransactionDynamoDbEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionDynamoDbMapper {

    private static final String PK_PREFIX = "TX#";
    private static final String SK_VALUE = "INFO";
    private static final String SUB_PREFIX = "SUB#";

    public static TransactionDynamoDbEntity toEntity(Transaction domain) {
        if (domain == null) return null;
        
        TransactionDynamoDbEntity entity = new TransactionDynamoDbEntity();
        entity.setPk(PK_PREFIX + domain.getId());
        entity.setSk(SK_VALUE);
        entity.setGsi1pk(SUB_PREFIX + domain.getSubscriptionId());
        entity.setGsi1sk(PK_PREFIX + domain.getId());
        
        entity.setId(domain.getId());
        entity.setSubscriptionId(domain.getSubscriptionId());
        entity.setType(domain.getType());
        entity.setAmount(domain.getAmount());
        entity.setDate(domain.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return entity;
    }

    public static Transaction toDomain(TransactionDynamoDbEntity entity) {
        if (entity == null) return null;
        
        return new Transaction(
            entity.getId(),
            entity.getSubscriptionId(),
            entity.getType(),
            entity.getAmount(),
            LocalDateTime.parse(entity.getDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}
