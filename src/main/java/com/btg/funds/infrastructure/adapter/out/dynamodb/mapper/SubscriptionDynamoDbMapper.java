package com.btg.funds.infrastructure.adapter.out.dynamodb.mapper;

import com.btg.funds.domain.Subscription;
import com.btg.funds.infrastructure.adapter.out.dynamodb.entity.SubscriptionDynamoDbEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionDynamoDbMapper {

    private static final String CLIENT_PREFIX = "CLIENT#";
    private static final String SUB_PREFIX = "SUB#";

    public static SubscriptionDynamoDbEntity toEntity(Subscription domain) {
        if (domain == null) return null;
        
        SubscriptionDynamoDbEntity entity = new SubscriptionDynamoDbEntity();
        entity.setPk(CLIENT_PREFIX + domain.getClientId());
        entity.setSk(SUB_PREFIX + domain.getFundId());
        
        entity.setId(domain.getId());
        entity.setClientId(domain.getClientId());
        entity.setFundId(domain.getFundId());
        entity.setAmount(domain.getAmount());
        entity.setDate(domain.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        entity.setStatus(domain.getStatus().name());
        entity.setLifecycleEvents(serializeLifecycleEvents(domain.getLifecycleEvents()));
        
        return entity;
    }

    public static Subscription toDomain(SubscriptionDynamoDbEntity entity) {
        if (entity == null) return null;
        
        return new Subscription(
            entity.getId(),
            entity.getClientId(),
            entity.getFundId(),
            entity.getAmount(),
            LocalDateTime.parse(entity.getDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            entity.getStatus() == null ? Subscription.Status.ACTIVE : Subscription.Status.valueOf(entity.getStatus()),
            deserializeLifecycleEvents(entity.getLifecycleEvents())
        );
    }

    private static String serializeLifecycleEvents(List<Subscription.LifecycleEvent> events) {
        if (events == null || events.isEmpty()) {
            return null;
        }
        return events.stream()
                .map(event -> String.join("|",
                        escape(event.type()),
                        event.occurredAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        event.amount().toPlainString(),
                        escape(event.detail())))
                .collect(Collectors.joining(";"));
    }

    private static List<Subscription.LifecycleEvent> deserializeLifecycleEvents(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return Arrays.stream(raw.split(";"))
                .filter(value -> !value.isBlank())
                .map(SubscriptionDynamoDbMapper::toLifecycleEvent)
                .toList();
    }

    private static Subscription.LifecycleEvent toLifecycleEvent(String rawEvent) {
        String[] tokens = rawEvent.split("\\|", 4);
        if (tokens.length < 4) {
            throw new IllegalArgumentException("Invalid lifecycle event payload");
        }
        return new Subscription.LifecycleEvent(
                unescape(tokens[0]),
                LocalDateTime.parse(tokens[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                new BigDecimal(tokens[2]),
                unescape(tokens[3])
        );
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("|", "\\|").replace(";", "\\;");
    }

    private static String unescape(String value) {
        return value == null ? null : value.replace("\\;", ";").replace("\\|", "|");
    }
}
