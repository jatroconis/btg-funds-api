package com.btg.funds.infrastructure.adapter.out.dynamodb.repository;

import com.btg.funds.application.port.out.ClientRepository;
import com.btg.funds.domain.Client;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.ClientDynamoDbEntity;
import com.btg.funds.infrastructure.adapter.out.dynamodb.mapper.ClientDynamoDbMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
public class ClientDynamoDbAdapter implements ClientRepository {

    private final DynamoDbTable<ClientDynamoDbEntity> table;

    public ClientDynamoDbAdapter(DynamoDbEnhancedClient enhancedClient, 
                                 @Value("${TABLE_NAME:BtgFundsTable}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(ClientDynamoDbEntity.class));
    }

    @Override
    public Optional<Client> findById(String id) {
        Key key = Key.builder()
                .partitionValue("CLIENT#" + id)
                .sortValue("PROFILE")
                .build();
        ClientDynamoDbEntity entity = table.getItem(key);
        return Optional.ofNullable(ClientDynamoDbMapper.toDomain(entity));
    }

    @Override
    public void save(Client client) {
        ClientDynamoDbEntity entity = ClientDynamoDbMapper.toEntity(client);
        table.putItem(entity);
    }
}
