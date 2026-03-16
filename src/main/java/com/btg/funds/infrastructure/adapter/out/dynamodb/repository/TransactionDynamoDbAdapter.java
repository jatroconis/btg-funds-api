package com.btg.funds.infrastructure.adapter.out.dynamodb.repository;

import com.btg.funds.application.port.out.TransactionRepository;
import com.btg.funds.domain.Transaction;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.TransactionDynamoDbEntity;
import com.btg.funds.infrastructure.adapter.out.dynamodb.mapper.TransactionDynamoDbMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TransactionDynamoDbAdapter implements TransactionRepository {

    private final DynamoDbTable<TransactionDynamoDbEntity> table;
    private final DynamoDbIndex<TransactionDynamoDbEntity> gsi1;

    public TransactionDynamoDbAdapter(DynamoDbEnhancedClient enhancedClient, 
                                      @Value("${TABLE_NAME:BtgFundsTable}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(TransactionDynamoDbEntity.class));
        this.gsi1 = this.table.index("GSI1");
    }

    @Override
    public List<Transaction> findBySubscriptionId(String subscriptionId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder().partitionValue("SUB#" + subscriptionId).build()
        );
        return gsi1.query(queryConditional)
                .stream()
                .flatMap(page -> page.items().stream())
                .map(TransactionDynamoDbMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Transaction transaction) {
        TransactionDynamoDbEntity entity = TransactionDynamoDbMapper.toEntity(transaction);
        table.putItem(entity);
    }
}
