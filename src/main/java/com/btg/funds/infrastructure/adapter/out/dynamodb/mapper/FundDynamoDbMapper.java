package com.btg.funds.infrastructure.adapter.out.dynamodb.mapper;

import com.btg.funds.domain.Fund;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.FundDynamoDbEntity;

public class FundDynamoDbMapper {

    private static final String PK_VALUE = "FUND";
    private static final String SK_PREFIX = "FUND#";

    public static FundDynamoDbEntity toEntity(Fund domain) {
        if (domain == null) return null;
        
        FundDynamoDbEntity entity = new FundDynamoDbEntity();
        entity.setPk(PK_VALUE);
        entity.setSk(SK_PREFIX + domain.getId());
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setMinimumAmount(domain.getMinimumAmount());
        entity.setCategory(domain.getCategory());
        
        return entity;
    }

    public static Fund toDomain(FundDynamoDbEntity entity) {
        if (entity == null) return null;
        
        return new Fund(entity.getId(), entity.getName(), entity.getMinimumAmount(), entity.getCategory());
    }
}
