package com.btg.funds.infrastructure.adapter.out.dynamodb.repository;

import com.btg.funds.domain.Subscription;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.SubscriptionDynamoDbEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SubscriptionDynamoDbAdapterTest {

    private DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<SubscriptionDynamoDbEntity> table;
    private SubscriptionDynamoDbAdapter adapter;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        enhancedClient = mock(DynamoDbEnhancedClient.class);
        table = mock(DynamoDbTable.class);
        when(enhancedClient.table(eq("TestTable"), any(TableSchema.class))).thenReturn(table);

        adapter = new SubscriptionDynamoDbAdapter(enhancedClient, "TestTable");
    }

    @Test
    void save_ShouldPutItem() {
        Subscription subscription = new Subscription("sub-1", "client-1", "fund-1", new BigDecimal("100"), LocalDateTime.now());

        adapter.save(subscription);

        ArgumentCaptor<SubscriptionDynamoDbEntity> captor = ArgumentCaptor.forClass(SubscriptionDynamoDbEntity.class);
        verify(table).putItem(captor.capture());

        SubscriptionDynamoDbEntity captured = captor.getValue();
        assertEquals("CLIENT#client-1", captured.getPk());
        assertEquals("SUB#fund-1", captured.getSk());
    }

    @Test
    void delete_ShouldDeleteItem() {
        adapter.delete("client-1", "fund-1");

        ArgumentCaptor<Key> captor = ArgumentCaptor.forClass(Key.class);
        verify(table).deleteItem(captor.capture());

        Key captured = captor.getValue();
        assertEquals("CLIENT#client-1", captured.partitionKeyValue().s());
        assertEquals("SUB#fund-1", captured.sortKeyValue().get().s());
    }

    @Test
    void findByClientIdAndFundId_ShouldMapCancelledStatusAndHistory() {
        SubscriptionDynamoDbEntity entity = new SubscriptionDynamoDbEntity();
        entity.setId("sub-1");
        entity.setClientId("client-1");
        entity.setFundId("fund-1");
        entity.setAmount(new BigDecimal("100"));
        entity.setDate("2026-03-01T10:00:00");
        entity.setStatus("CANCELLED");
        entity.setLifecycleEvents("APERTURA|2026-03-01T10:00:00|100|Subscription opened;CANCELACION|2026-03-02T10:00:00|100|Subscription cancelled");

        when(table.getItem(any(Key.class))).thenReturn(entity);

        Optional<Subscription> found = adapter.findByClientIdAndFundId("client-1", "fund-1");

        assertTrue(found.isPresent());
        assertEquals(Subscription.Status.CANCELLED, found.get().getStatus());
        assertEquals(2, found.get().getLifecycleEvents().size());
    }
}
