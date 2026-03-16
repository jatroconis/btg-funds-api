package com.btg.funds.infrastructure.adapter.out.dynamodb.repository;

import com.btg.funds.domain.Fund;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.FundDynamoDbEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FundDynamoDbAdapterTest {

    @Mock
    private DynamoDbEnhancedClient enhancedClient;

    @Mock
    private DynamoDbTable<FundDynamoDbEntity> table;

    @Mock
    private PageIterable<FundDynamoDbEntity> pageIterable;

    @Mock
    private SdkIterable<FundDynamoDbEntity> sdkIterable;

    private FundDynamoDbAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(enhancedClient.table(eq("TestTable"), any(TableSchema.class))).thenReturn(table);
        adapter = new FundDynamoDbAdapter(enhancedClient, "TestTable");
    }

    @Test
    void findByIdReturnsFund() {
        FundDynamoDbEntity entity = new FundDynamoDbEntity();
        entity.setPk("FUND");
        entity.setSk("FUND#1");
        entity.setId("1");
        entity.setName("FIC");
        entity.setMinimumAmount(new BigDecimal("1000"));
        entity.setCategory("FIC");

        when(table.getItem(any(Key.class))).thenReturn(entity);

        Optional<Fund> result = adapter.findById("1");

        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
    }

    @Test
    void savePutsItem() {
        Fund fund = new Fund("1", "FIC", new BigDecimal("1000"), "FIC");

        adapter.save(fund);

        verify(table).putItem(any(FundDynamoDbEntity.class));
    }

    @Test
    void findAllReturnsFunds() {
        FundDynamoDbEntity entity = new FundDynamoDbEntity();
        entity.setPk("FUND");
        entity.setSk("FUND#1");
        entity.setId("1");
        entity.setName("FIC");
        entity.setMinimumAmount(new BigDecimal("1000"));
        entity.setCategory("FIC");

        when(table.query(any(QueryConditional.class))).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(Stream.of(entity));

        List<Fund> result = adapter.findAll();

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }
}
