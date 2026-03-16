package com.btg.funds.infrastructure.adapter.out.dynamodb.mapper;

import com.btg.funds.domain.Transaction;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.TransactionDynamoDbEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionDynamoDbMapperTest {

    @Test
    void shouldMapToEntity() {
        LocalDateTime now = LocalDateTime.now();
        Transaction transaction = new Transaction("t1", "s1", "APERTURA", new BigDecimal("100"), now);
        
        TransactionDynamoDbEntity entity = TransactionDynamoDbMapper.toEntity(transaction);
        
        assertEquals("TX#t1", entity.getPk());
        assertEquals("INFO", entity.getSk());
        assertEquals("SUB#s1", entity.getGsi1pk());
        assertEquals("TX#t1", entity.getGsi1sk());
        assertEquals("t1", entity.getId());
        assertEquals("s1", entity.getSubscriptionId());
        assertEquals("APERTURA", entity.getType());
        assertEquals(new BigDecimal("100"), entity.getAmount());
        assertEquals(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), entity.getDate());
    }

    @Test
    void shouldMapToDomain() {
        LocalDateTime now = LocalDateTime.now();
        TransactionDynamoDbEntity entity = new TransactionDynamoDbEntity();
        entity.setPk("TX#t2");
        entity.setSk("INFO");
        entity.setGsi1pk("SUB#s2");
        entity.setGsi1sk("TX#t2");
        entity.setId("t2");
        entity.setSubscriptionId("s2");
        entity.setType("CANCELACION");
        entity.setAmount(new BigDecimal("200"));
        entity.setDate(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        Transaction transaction = TransactionDynamoDbMapper.toDomain(entity);
        
        assertEquals("t2", transaction.getId());
        assertEquals("s2", transaction.getSubscriptionId());
        assertEquals("CANCELACION", transaction.getType());
        assertEquals(new BigDecimal("200"), transaction.getAmount());
        assertEquals(now, transaction.getDate());
    }
}
