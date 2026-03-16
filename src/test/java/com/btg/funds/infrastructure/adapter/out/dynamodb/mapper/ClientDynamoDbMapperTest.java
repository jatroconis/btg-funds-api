package com.btg.funds.infrastructure.adapter.out.dynamodb.mapper;

import com.btg.funds.domain.Client;
import com.btg.funds.domain.NotificationChannel;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.ClientDynamoDbEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientDynamoDbMapperTest {

    @Test
    void shouldMapToEntity() {
        Client client = new Client("c1", "Juan", new BigDecimal("1000"));
        
        ClientDynamoDbEntity entity = ClientDynamoDbMapper.toEntity(client);
        
        assertEquals("CLIENT#c1", entity.getPk());
        assertEquals("PROFILE", entity.getSk());
        assertEquals("c1", entity.getId());
        assertEquals("Juan", entity.getName());
        assertEquals(new BigDecimal("1000"), entity.getBalance());
    }

    @Test
    void shouldMapToDomain() {
        ClientDynamoDbEntity entity = new ClientDynamoDbEntity();
        entity.setPk("CLIENT#c2");
        entity.setSk("PROFILE");
        entity.setId("c2");
        entity.setName("Maria");
        entity.setBalance(new BigDecimal("2000"));
        
        Client client = ClientDynamoDbMapper.toDomain(entity);
        
        assertEquals("c2", client.getId());
        assertEquals("Maria", client.getName());
        assertEquals(new BigDecimal("2000"), client.getBalance());
    }

    @Test
    void shouldMapNotificationPreferences() {
        Client client = new Client("c3", "Lina", new BigDecimal("3000"), "lina@test.com");
        client.setPhoneNumber("+573001112233");
        client.setNotificationPreferences(Set.of(NotificationChannel.EMAIL, NotificationChannel.SMS));

        ClientDynamoDbEntity entity = ClientDynamoDbMapper.toEntity(client);
        Client mapped = ClientDynamoDbMapper.toDomain(entity);

        assertEquals("+573001112233", mapped.getPhoneNumber());
        assertEquals(Set.of(NotificationChannel.EMAIL, NotificationChannel.SMS), mapped.getNotificationPreferences());
    }
}
