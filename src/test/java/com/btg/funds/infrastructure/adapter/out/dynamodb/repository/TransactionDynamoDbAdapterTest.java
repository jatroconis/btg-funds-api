package com.btg.funds.infrastructure.adapter.out.dynamodb.repository;

import com.btg.funds.domain.Transaction;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.TransactionDynamoDbEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionDynamoDbAdapterTest {

    @Mock
    private DynamoDbEnhancedClient enhancedClient;

    @Mock
    private DynamoDbTable<TransactionDynamoDbEntity> table;

    @Mock
    private DynamoDbIndex<TransactionDynamoDbEntity> gsi1;

    @Mock
    private PageIterable<TransactionDynamoDbEntity> pageIterable;

    @Mock
    private SdkIterable<TransactionDynamoDbEntity> sdkIterable;

    private TransactionDynamoDbAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(enhancedClient.table(eq("TestTable"), any(TableSchema.class))).thenReturn(table);
        when(table.index("GSI1")).thenReturn(gsi1);
        adapter = new TransactionDynamoDbAdapter(enhancedClient, "TestTable");
    }

    @Test
    void savePutsItem() {
        Transaction tx = new Transaction("1", "s1", "APERTURA", new BigDecimal("100"), LocalDateTime.now());

        adapter.save(tx);

        verify(table).putItem(any(TransactionDynamoDbEntity.class));
    }

    @Test
    void findBySubscriptionIdReturnsTransactions() {
        TransactionDynamoDbEntity entity = new TransactionDynamoDbEntity();
        entity.setId("1");
        entity.setSubscriptionId("s1");
        entity.setType("APERTURA");
        entity.setAmount(new BigDecimal("100"));
        entity.setDate(LocalDateTime.now().toString());

        Page<TransactionDynamoDbEntity> page = Page.create(List.of(entity));
        when(gsi1.query(any(QueryConditional.class))).thenReturn(pageIterable);
        when(pageIterable.stream()).thenReturn(Stream.of(page));

        List<Transaction> result = adapter.findBySubscriptionId("s1");

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }
}
