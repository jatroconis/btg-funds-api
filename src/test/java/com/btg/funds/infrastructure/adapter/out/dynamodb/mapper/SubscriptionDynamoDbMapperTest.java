package com.btg.funds.infrastructure.adapter.out.dynamodb.mapper;

import com.btg.funds.domain.Subscription;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.SubscriptionDynamoDbEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubscriptionDynamoDbMapperTest {

    @Test
    void toEntity() {
        LocalDateTime now = LocalDateTime.now();
        Subscription domain = new Subscription("sub-1", "client-1", "fund-1", new BigDecimal("100"), now);

        SubscriptionDynamoDbEntity entity = SubscriptionDynamoDbMapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals("CLIENT#client-1", entity.getPk());
        assertEquals("SUB#fund-1", entity.getSk());
        assertEquals("sub-1", entity.getId());
        assertEquals("client-1", entity.getClientId());
        assertEquals("fund-1", entity.getFundId());
        assertEquals(new BigDecimal("100"), entity.getAmount());
    }

    @Test
    void toDomain_ShouldPreserveLifecycleEventOrderAndStatus() {
        SubscriptionDynamoDbEntity entity = new SubscriptionDynamoDbEntity();
        entity.setId("sub-1");
        entity.setClientId("client-1");
        entity.setFundId("fund-1");
        entity.setAmount(new BigDecimal("100"));
        entity.setDate("2026-03-01T10:00:00");
        entity.setStatus("CANCELLED");
        entity.setLifecycleEvents("APERTURA|2026-03-01T10:00:00|100|Subscription opened;CANCELACION|2026-03-02T10:00:00|100|Subscription cancelled");

        Subscription domain = SubscriptionDynamoDbMapper.toDomain(entity);

        assertEquals(Subscription.Status.CANCELLED, domain.getStatus());
        assertEquals(
                List.of("APERTURA", "CANCELACION"),
                domain.getLifecycleEvents().stream().map(Subscription.LifecycleEvent::type).toList()
        );
    }
}
