package com.btg.funds.domain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Client {
    private String id;
    private String name;
    private BigDecimal balance;
    private String email;
    private String phoneNumber;
    private Set<NotificationChannel> notificationPreferences;

    public Client(String id, String name, BigDecimal balance, String email) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.email = email;
        this.phoneNumber = null;
        this.notificationPreferences = null;
    }

    public Client(String id,
                  String name,
                  BigDecimal balance,
                  String email,
                  String phoneNumber,
                  Set<NotificationChannel> notificationPreferences) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.email = email;
        this.phoneNumber = phoneNumber;
        setNotificationPreferences(notificationPreferences);
    }

    public Client(String id, String name, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.email = null;
    }

    public static Client createDefault(String id, String name) {
        return new Client(id, name, new BigDecimal("500000"));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<NotificationChannel> getNotificationPreferences() {
        return notificationPreferences == null ? Set.of() : Collections.unmodifiableSet(notificationPreferences);
    }

    public void setNotificationPreferences(Set<NotificationChannel> notificationPreferences) {
        if (notificationPreferences == null || notificationPreferences.isEmpty()) {
            this.notificationPreferences = null;
            return;
        }
        this.notificationPreferences = new HashSet<>(notificationPreferences);
    }

    public boolean canNotifyByEmail() {
        if (notificationPreferences == null || notificationPreferences.isEmpty()) {
            return email != null && !email.isBlank();
        }
        return notificationPreferences.contains(NotificationChannel.EMAIL) && email != null && !email.isBlank();
    }

    public boolean canNotifyBySms() {
        if (notificationPreferences == null || notificationPreferences.isEmpty()) {
            return false;
        }
        return notificationPreferences.contains(NotificationChannel.SMS) && phoneNumber != null && !phoneNumber.isBlank();
    }

    public void deductBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
