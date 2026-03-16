package com.btg.funds.infrastructure.adapter.out.dynamodb.repository;

import com.btg.funds.application.port.out.FundRepository;
import com.btg.funds.domain.Fund;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.FundDynamoDbEntity;
import com.btg.funds.infrastructure.adapter.out.dynamodb.mapper.FundDynamoDbMapper;
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
public class FundDynamoDbAdapter implements FundRepository {

    private final DynamoDbTable<FundDynamoDbEntity> table;

    public FundDynamoDbAdapter(DynamoDbEnhancedClient enhancedClient, 
                               @Value("${TABLE_NAME:BtgFundsTable}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(FundDynamoDbEntity.class));
    }

    @Override
    public Optional<Fund> findById(String id) {
        Key key = Key.builder()
                .partitionValue("FUND")
                .sortValue("FUND#" + id)
                .build();
        FundDynamoDbEntity entity = table.getItem(key);
        return Optional.ofNullable(FundDynamoDbMapper.toDomain(entity));
    }

    @Override
    public List<Fund> findAll() {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue("FUND").build());
        return table.query(queryConditional)
                .items()
                .stream()
                .map(FundDynamoDbMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Fund fund) {
        FundDynamoDbEntity entity = FundDynamoDbMapper.toEntity(fund);
        table.putItem(entity);
    }
}
