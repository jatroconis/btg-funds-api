package com.btg.funds.application.port.out;

public interface NotificationSender {
    void sendEmail(String email, String message);
    void sendSms(String phoneNumber, String message);
}
