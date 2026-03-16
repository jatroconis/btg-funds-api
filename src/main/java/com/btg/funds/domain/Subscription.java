package com.btg.funds.domain;

import com.btg.funds.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Subscription {
    public record LifecycleEvent(String type, LocalDateTime occurredAt, BigDecimal amount, String detail) {}

    public enum Status {
        ACTIVE,
        CANCELLED
    }

    private String id;
    private String clientId;
    private String fundId;
    private BigDecimal amount;
    private LocalDateTime date;
    private Status status;
    private final List<LifecycleEvent> lifecycleEvents;

    public Subscription(String id, String clientId, String fundId, BigDecimal amount, LocalDateTime date) {
        this(id, clientId, fundId, amount, date, Status.ACTIVE, null);
    }

    public Subscription(String id,
                        String clientId,
                        String fundId,
                        BigDecimal amount,
                        LocalDateTime date,
                        Status status,
                        List<LifecycleEvent> lifecycleEvents) {
        this.id = id;
        this.clientId = clientId;
        this.fundId = fundId;
        this.amount = amount;
        this.date = date;
        this.status = status == null ? Status.ACTIVE : status;
        if (lifecycleEvents == null || lifecycleEvents.isEmpty()) {
            this.lifecycleEvents = new ArrayList<>();
            this.lifecycleEvents.add(new LifecycleEvent("APERTURA", date, amount, "Subscription opened"));
        } else {
            this.lifecycleEvents = new ArrayList<>(lifecycleEvents);
        }
    }

    public static Subscription create(String id, Client client, Fund fund, BigDecimal amount, LocalDateTime date) {
        if (client.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(fund.getName());
        }
        
        client.deductBalance(amount);
        
        return new Subscription(id, client.getId(), fund.getId(), amount, date);
    }

    public void cancel(Client client) {
        cancel(client, LocalDateTime.now());
    }

    public void cancel(Client client, LocalDateTime cancellationTime) {
        if (!this.clientId.equals(client.getId())) {
            throw new IllegalArgumentException("La suscripción no pertenece a este cliente");
        }
        if (this.status == Status.CANCELLED) {
            return;
        }
        client.addBalance(this.amount);
        this.status = Status.CANCELLED;
        this.lifecycleEvents.add(new LifecycleEvent("CANCELACION", cancellationTime, amount, "Subscription cancelled"));
    }

    public void markCancelled(LocalDateTime cancellationTime) {
        if (this.status == Status.CANCELLED) {
            return;
        }
        this.status = Status.CANCELLED;
        this.lifecycleEvents.add(new LifecycleEvent("CANCELACION", cancellationTime, amount, "Subscription cancelled"));
    }

    public String getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getFundId() {
        return fundId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isCancelled() {
        return status == Status.CANCELLED;
    }

    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public List<LifecycleEvent> getLifecycleEvents() {
        return List.copyOf(lifecycleEvents);
    }
}
