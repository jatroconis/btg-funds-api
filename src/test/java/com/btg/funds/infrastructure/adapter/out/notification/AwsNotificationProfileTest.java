package com.btg.funds.infrastructure.adapter.out.notification;

import com.btg.funds.application.port.out.NotificationSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringJUnitConfig(classes = {
        SesEmailSender.class,
        SnsSmsSender.class,
        ConsoleNotificationAdapter.class
})
@ActiveProfiles("aws")
class AwsNotificationProfileTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Map<String, NotificationSender> notificationSenders;

    @Test
    void awsProfile_ShouldWireAwsNotificationAdaptersOnly() {
        assertFalse(applicationContext.containsBean("consoleNotificationAdapter"));
        assertEquals(2, notificationSenders.size());
        assertInstanceOf(SesEmailSender.class, notificationSenders.get("sesEmailSender"));
        assertInstanceOf(SnsSmsSender.class, notificationSenders.get("snsSmsSender"));
    }

    @Test
    void awsProfile_BeansShouldBeUsable() {
        NotificationSender emailSender = notificationSenders.get("sesEmailSender");
        NotificationSender smsSender = notificationSenders.get("snsSmsSender");

        assertDoesNotThrow(() -> emailSender.sendEmail("mail@test.com", "mensaje"));
        assertDoesNotThrow(() -> smsSender.sendSms("+573001112233", "mensaje"));
    }

}
