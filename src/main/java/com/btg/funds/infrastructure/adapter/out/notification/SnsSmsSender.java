package com.btg.funds.infrastructure.adapter.out.notification;

import com.btg.funds.application.port.out.NotificationSender;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Profile("aws")
public class SnsSmsSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(SnsSmsSender.class);

    @Override
    public void sendEmail(String email, String message) {
        // Ignored by SNS
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("[AWS SNS] Sending SMS to {} with message: {}", phoneNumber, message);
    }
}
