package com.btg.funds.infrastructure.adapter.out.notification;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleNotificationAdapterTest {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private ConsoleNotificationAdapter adapter;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        adapter = new ConsoleNotificationAdapter();
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    void testSendEmail() {
        adapter.sendEmail("test@example.com", "Hello from BTG Funds");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("[CONSOLE SIMULATION] Sending email to: test@example.com"));
        assertTrue(output.contains("Message: Hello from BTG Funds"));
    }

    @Test
    void testSendSms() {
        adapter.sendSms("1234567890", "Hello SMS");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("[CONSOLE SIMULATION] Sending SMS to: 1234567890"));
        assertTrue(output.contains("Message: Hello SMS"));
    }
}
