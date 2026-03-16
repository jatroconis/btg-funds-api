package com.btg.funds.infrastructure.adapter.out.dynamodb;

import com.btg.funds.domain.Client;
import com.btg.funds.domain.Fund;
import com.btg.funds.domain.Subscription;
import com.btg.funds.domain.Transaction;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.ClientDynamoDbEntity;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.FundDynamoDbEntity;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.SubscriptionDynamoDbEntity;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.TransactionDynamoDbEntity;
import com.btg.funds.infrastructure.adapter.out.dynamodb.repository.ClientDynamoDbAdapter;
import com.btg.funds.infrastructure.adapter.out.dynamodb.repository.FundDynamoDbAdapter;
import com.btg.funds.infrastructure.adapter.out.dynamodb.repository.SubscriptionDynamoDbAdapter;
import com.btg.funds.infrastructure.adapter.out.dynamodb.repository.TransactionDynamoDbAdapter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@org.junit.jupiter.api.Disabled("Fails in agent CI environments without Docker")
class DynamoDbAdapterIntegrationTest {

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.3.2"))
            .withServices(LocalStackContainer.Service.DYNAMODB);

    static DynamoDbEnhancedClient enhancedClient;
    static ClientDynamoDbAdapter clientAdapter;
    static FundDynamoDbAdapter fundAdapter;
    static SubscriptionDynamoDbAdapter subscriptionAdapter;
    static TransactionDynamoDbAdapter transactionAdapter;
    static final String TABLE_NAME = "BtgFundsTable";

    @BeforeAll
    static void setUp() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create(localStack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())))
                .region(Region.of(localStack.getRegion()))
                .build();

        enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        clientAdapter = new ClientDynamoDbAdapter(enhancedClient, TABLE_NAME);
        fundAdapter = new FundDynamoDbAdapter(enhancedClient, TABLE_NAME);
        subscriptionAdapter = new SubscriptionDynamoDbAdapter(enhancedClient, TABLE_NAME);
        transactionAdapter = new TransactionDynamoDbAdapter(enhancedClient, TABLE_NAME);

        // Create table with GSI1
        enhancedClient.table(TABLE_NAME, TableSchema.fromBean(SubscriptionDynamoDbEntity.class))
                .createTable(CreateTableEnhancedRequest.builder()
                        .globalSecondaryIndices(
                                EnhancedGlobalSecondaryIndex.builder()
                                        .indexName("GSI1")
                                        .projection(p -> p.projectionType(ProjectionType.ALL))
                                        .build()
                        )
                        .build());
    }

    @Test
    void shouldSaveAndRetrieveClient() {
        Client client = new Client("c1", "Juan", new BigDecimal("50000"));
        clientAdapter.save(client);

        Optional<Client> retrieved = clientAdapter.findById("c1");
        assertTrue(retrieved.isPresent());
        assertEquals("Juan", retrieved.get().getName());
    }

    @Test
    void shouldSaveAndRetrieveFund() {
        Fund fund = new Fund("f1", "FPV", new BigDecimal("10000"), "FPV");
        fundAdapter.save(fund);

        Optional<Fund> retrieved = fundAdapter.findById("f1");
        assertTrue(retrieved.isPresent());
        assertEquals("FPV", retrieved.get().getName());

        List<Fund> all = fundAdapter.findAll();
        assertTrue(all.stream().anyMatch(f -> f.getId().equals("f1")));
    }

    @Test
    void shouldSaveAndRetrieveSubscriptionWithGsi() {
        Subscription sub = new Subscription("s1", "c1", "f1", new BigDecimal("10000"), LocalDateTime.now());
        subscriptionAdapter.save(sub);

        Optional<Subscription> retrieved = subscriptionAdapter.findById("s1");
        assertTrue(retrieved.isPresent());
        assertEquals("f1", retrieved.get().getFundId());

        List<Subscription> clientSubs = subscriptionAdapter.findByClientId("c1");
        assertEquals(1, clientSubs.size());
        assertEquals("s1", clientSubs.get(0).getId());
    }

    @Test
    void shouldSaveAndRetrieveTransactionWithGsi() {
        Transaction tx = new Transaction("t1", "s1", "APERTURA", new BigDecimal("10000"), LocalDateTime.now());
        transactionAdapter.save(tx);

        List<Transaction> subTxs = transactionAdapter.findBySubscriptionId("s1");
        assertEquals(1, subTxs.size());
        assertEquals("t1", subTxs.get(0).getId());
    }
}
