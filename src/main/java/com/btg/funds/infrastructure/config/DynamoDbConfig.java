package com.btg.funds.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.TransactionDynamoDbEntity;
import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Value("${aws.dynamodb.endpoint:#{null}}")
    private String dynamoDbEndpoint;

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    @Value("${TABLE_NAME:BtgFundsTable}")
    private String tableName;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        DynamoDbClientBuilder builder = DynamoDbClient.builder();
        if (dynamoDbEndpoint != null && !dynamoDbEndpoint.isEmpty()) {
            builder.endpointOverride(URI.create(dynamoDbEndpoint))
                   .region(Region.of(awsRegion))
                   .credentialsProvider(StaticCredentialsProvider.create(
                       AwsBasicCredentials.create("fakeMyKeyId", "fakeSecretAccessKey")));
        }
        return builder.build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public ApplicationRunner initializeDynamoDb(DynamoDbEnhancedClient enhancedClient) {
        return args -> {
            if (dynamoDbEndpoint != null && !dynamoDbEndpoint.isEmpty()) {
                try {
                    enhancedClient.table(tableName, TableSchema.fromBean(TransactionDynamoDbEntity.class))
                            .createTable(CreateTableEnhancedRequest.builder()
                                    .globalSecondaryIndices(
                                            EnhancedGlobalSecondaryIndex.builder()
                                                    .indexName("GSI1")
                                                    .projection(p -> p.projectionType(ProjectionType.ALL))
                                                    .build()
                                    )
                                    .build());
                    System.out.println("Created table " + tableName + " with GSI1 in local DynamoDB.");
                } catch (ResourceInUseException e) {
                    System.out.println("Table " + tableName + " already exists in local DynamoDB.");
                } catch (Exception e) {
                    System.err.println("Error initializing DynamoDB: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }
}
