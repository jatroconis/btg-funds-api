package com.btg.funds.infrastructure.adapter.out.dynamodb.mapper;

import com.btg.funds.domain.Client;
import com.btg.funds.domain.NotificationChannel;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.ClientDynamoDbEntity;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientDynamoDbMapper {

    private static final String PK_PREFIX = "CLIENT#";
    private static final String SK_VALUE = "PROFILE";

    public static ClientDynamoDbEntity toEntity(Client domain) {
        if (domain == null) return null;
        
        ClientDynamoDbEntity entity = new ClientDynamoDbEntity();
        entity.setPk(PK_PREFIX + domain.getId());
        entity.setSk(SK_VALUE);
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setBalance(domain.getBalance());
        entity.setEmail(domain.getEmail());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setNotificationChannels(toChannelsString(domain.getNotificationPreferences()));
        
        return entity;
    }

    public static Client toDomain(ClientDynamoDbEntity entity) {
        if (entity == null) return null;
        
        return new Client(
                entity.getId(),
                entity.getName(),
                entity.getBalance(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                parseChannels(entity.getNotificationChannels())
        );
    }

    private static String toChannelsString(Set<NotificationChannel> channels) {
        if (channels == null || channels.isEmpty()) {
            return null;
        }
        return channels.stream().map(Enum::name).sorted().collect(Collectors.joining(","));
    }

    private static Set<NotificationChannel> parseChannels(String rawChannels) {
        if (rawChannels == null || rawChannels.isBlank()) {
            return null;
        }
        return Arrays.stream(rawChannels.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(NotificationChannel::valueOf)
                .collect(Collectors.toSet());
    }
}
