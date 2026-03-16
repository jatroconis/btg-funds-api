package com.btg.funds.infrastructure.adapter.out.notification;

import com.btg.funds.application.port.out.NotificationSender;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!aws")
public class ConsoleNotificationAdapter implements NotificationSender {
    @Override
    public void sendEmail(String email, String message) {
        System.out.println(">>> [CONSOLE SIMULATION] Sending email to: " + email);
        System.out.println(">>> Message: " + message);
    }
    
    @Override
    public void sendSms(String phoneNumber, String message) {
        System.out.println(">>> [CONSOLE SIMULATION] Sending SMS to: " + phoneNumber);
        System.out.println(">>> Message: " + message);
    }
}
