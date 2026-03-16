package com.btg.funds.infrastructure.adapter.out.notification;

import com.btg.funds.application.port.out.NotificationSender;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Profile("aws")
public class SesEmailSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(SesEmailSender.class);

    @Override
    public void sendEmail(String email, String message) {
        log.info("[AWS SES] Sending email to {} with message: {}", email, message);
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        // Ignored by SES
    }
}
