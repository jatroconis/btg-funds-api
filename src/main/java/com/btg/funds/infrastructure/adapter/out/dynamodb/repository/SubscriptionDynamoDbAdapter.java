package com.btg.funds.infrastructure.adapter.out.dynamodb.repository;

import com.btg.funds.application.port.out.SubscriptionRepository;
import com.btg.funds.domain.Subscription;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.SubscriptionDynamoDbEntity;
import com.btg.funds.infrastructure.adapter.out.dynamodb.mapper.SubscriptionDynamoDbMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SubscriptionDynamoDbAdapter implements SubscriptionRepository {

    private final DynamoDbTable<SubscriptionDynamoDbEntity> table;

    public SubscriptionDynamoDbAdapter(DynamoDbEnhancedClient enhancedClient, 
                                       @Value("${TABLE_NAME:BtgFundsTable}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(SubscriptionDynamoDbEntity.class));
    }

    @Override
    public Optional<Subscription> findById(String id) {
        // Not used efficiently since id is not PK anymore, returning empty if called.
        // The real method to use is findByClientIdAndFundId
        return Optional.empty();
    }

    @Override
    public Optional<Subscription> findByClientIdAndFundId(String clientId, String fundId) {
        Key key = Key.builder()
                .partitionValue("CLIENT#" + clientId)
                .sortValue("SUB#" + fundId)
                .build();
        SubscriptionDynamoDbEntity entity = table.getItem(key);
        return Optional.ofNullable(SubscriptionDynamoDbMapper.toDomain(entity));
    }

    @Override
    public List<Subscription> findByClientId(String clientId) {
        QueryConditional queryConditional = QueryConditional.sortBeginsWith(
                Key.builder().partitionValue("CLIENT#" + clientId).sortValue("SUB#").build()
        );
        return table.query(queryConditional)
                .stream()
                .flatMap(page -> page.items().stream())
                .map(SubscriptionDynamoDbMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Subscription subscription) {
        SubscriptionDynamoDbEntity entity = SubscriptionDynamoDbMapper.toEntity(subscription);
        table.putItem(entity);
    }

    @Override
    public void delete(String clientId, String fundId) {
        Key key = Key.builder()
                .partitionValue("CLIENT#" + clientId)
                .sortValue("SUB#" + fundId)
                .build();
        table.deleteItem(key);
    }
}
