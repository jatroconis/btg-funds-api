package com.btg.funds.infrastructure.adapter.out.dynamodb.repository;

import com.btg.funds.domain.Client;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.ClientDynamoDbEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientDynamoDbAdapterTest {

    @Mock
    private DynamoDbEnhancedClient enhancedClient;

    @Mock
    private DynamoDbTable<ClientDynamoDbEntity> table;

    private ClientDynamoDbAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(enhancedClient.table(eq("TestTable"), any(TableSchema.class))).thenReturn(table);
        adapter = new ClientDynamoDbAdapter(enhancedClient, "TestTable");
    }

    @Test
    void findByIdReturnsClient() {
        ClientDynamoDbEntity entity = new ClientDynamoDbEntity();
        entity.setPk("CLIENT#1");
        entity.setSk("PROFILE");
        entity.setId("1");
        entity.setName("Test");
        entity.setBalance(new BigDecimal("1000"));

        when(table.getItem(any(Key.class))).thenReturn(entity);

        Optional<Client> result = adapter.findById("1");

        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
    }

    @Test
    void savePutsItem() {
        Client client = new Client("1", "Test", new BigDecimal("1000"));

        adapter.save(client);

        verify(table).putItem(any(ClientDynamoDbEntity.class));
    }
}
