package com.btg.funds.infrastructure.adapter.out.dynamodb.mapper;

import com.btg.funds.domain.Fund;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.FundDynamoDbEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FundDynamoDbMapperTest {

    @Test
    void shouldMapToEntity() {
        Fund fund = new Fund("f1", "FPV", new BigDecimal("50000"), "FPV");
        
        FundDynamoDbEntity entity = FundDynamoDbMapper.toEntity(fund);
        
        assertEquals("FUND", entity.getPk());
        assertEquals("FUND#f1", entity.getSk());
        assertEquals("f1", entity.getId());
        assertEquals("FPV", entity.getName());
        assertEquals(new BigDecimal("50000"), entity.getMinimumAmount());
        assertEquals("FPV", entity.getCategory());
    }

    @Test
    void shouldMapToDomain() {
        FundDynamoDbEntity entity = new FundDynamoDbEntity();
        entity.setPk("FUND");
        entity.setSk("FUND#f2");
        entity.setId("f2");
        entity.setName("FIC");
        entity.setMinimumAmount(new BigDecimal("75000"));
        entity.setCategory("FIC");
        
        Fund fund = FundDynamoDbMapper.toDomain(entity);
        
        assertEquals("f2", fund.getId());
        assertEquals("FIC", fund.getName());
        assertEquals(new BigDecimal("75000"), fund.getMinimumAmount());
        assertEquals("FIC", fund.getCategory());
    }
}
