package com.btg.funds.application.service;

import com.btg.funds.adapter.in.web.dto.CancellationResponse;
import com.btg.funds.adapter.in.web.dto.SubscriptionResponse;
import com.btg.funds.application.port.out.ClientRepository;
import com.btg.funds.application.port.out.FundRepository;
import com.btg.funds.application.port.out.SubscriptionRepository;
import com.btg.funds.application.port.out.TransactionRepository;
import com.btg.funds.application.port.out.NotificationSender;
import com.btg.funds.domain.Client;
import com.btg.funds.domain.Fund;
import com.btg.funds.domain.NotificationChannel;
import com.btg.funds.domain.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FundSubscriptionServiceTest {

    private ClientRepository clientRepository;
    private FundRepository fundRepository;
    private SubscriptionRepository subscriptionRepository;
    private TransactionRepository transactionRepository;
    private NotificationSender notificationSender;
    private FundSubscriptionService service;

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        fundRepository = mock(FundRepository.class);
        subscriptionRepository = mock(SubscriptionRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        notificationSender = mock(NotificationSender.class);
        service = new FundSubscriptionService(clientRepository, fundRepository, subscriptionRepository, transactionRepository, List.of(notificationSender));
    }

    @Test
    void subscribe_ShouldSuccess() {
        Client client = new Client("client-1", "N A", new BigDecimal("1000"), "e");
        Fund fund = new Fund("fund-1", "Fondo A", new BigDecimal("100"), "Cat");
        
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(fundRepository.findById("fund-1")).thenReturn(Optional.of(fund));

        SubscriptionResponse response = service.subscribe("client-1", "fund-1");

        assertNotNull(response);
        assertEquals(new BigDecimal("900"), client.getBalance());
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void cancelSubscription_ShouldSuccess() {
        Client client = new Client("client-1", "N A", new BigDecimal("900"), "e");
        Subscription sub = new Subscription("sub-1", "client-1", "fund-1", new BigDecimal("100"), LocalDateTime.now());
        
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(subscriptionRepository.findByClientIdAndFundId("client-1", "fund-1")).thenReturn(Optional.of(sub));

        CancellationResponse response = service.cancelSubscription("client-1", "fund-1");

        assertNotNull(response);
        assertEquals(new BigDecimal("1000"), client.getBalance());
        verify(subscriptionRepository).save(any(Subscription.class));
        verify(subscriptionRepository, never()).delete("client-1", "fund-1");
    }

    @Test
    void cancelSubscription_ShouldThrowWhenAlreadyCancelled() {
        Client client = new Client("client-1", "N A", new BigDecimal("900"), "e");
        Subscription sub = new Subscription("sub-1", "client-1", "fund-1", new BigDecimal("100"), LocalDateTime.now());
        sub.markCancelled(LocalDateTime.now().plusDays(1));

        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(subscriptionRepository.findByClientIdAndFundId("client-1", "fund-1")).thenReturn(Optional.of(sub));

        assertThrows(com.btg.funds.domain.exception.SubscriptionNotFoundException.class,
                () -> service.cancelSubscription("client-1", "fund-1"));

        assertEquals(new BigDecimal("900"), client.getBalance());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void subscribe_ShouldRouteNotificationByEmailPreferenceOnly() {
        Client client = new Client("client-1", "N A", new BigDecimal("1000"), "mail@test.com");
        client.setNotificationPreferences(Set.of(NotificationChannel.EMAIL));
        Fund fund = new Fund("fund-1", "Fondo A", new BigDecimal("100"), "Cat");

        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(fundRepository.findById("fund-1")).thenReturn(Optional.of(fund));

        service.subscribe("client-1", "fund-1");

        verify(notificationSender, times(1)).sendEmail(eq("mail@test.com"), anyString());
        verify(notificationSender, never()).sendSms(anyString(), anyString());
    }

    @Test
    void subscribe_ShouldRouteNotificationBySmsPreferenceOnly() {
        Client client = new Client("client-1", "N A", new BigDecimal("1000"), "mail@test.com");
        client.setPhoneNumber("+573001112233");
        client.setNotificationPreferences(Set.of(NotificationChannel.SMS));
        Fund fund = new Fund("fund-1", "Fondo A", new BigDecimal("100"), "Cat");

        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(fundRepository.findById("fund-1")).thenReturn(Optional.of(fund));

        service.subscribe("client-1", "fund-1");

        verify(notificationSender, never()).sendEmail(anyString(), anyString());
        verify(notificationSender, times(1)).sendSms(eq("+573001112233"), anyString());
    }

    @Test
    void subscribe_ShouldRouteNotificationByDualPreference() {
        Client client = new Client("client-1", "N A", new BigDecimal("1000"), "mail@test.com");
        client.setPhoneNumber("+573001112233");
        client.setNotificationPreferences(Set.of(NotificationChannel.EMAIL, NotificationChannel.SMS));
        Fund fund = new Fund("fund-1", "Fondo A", new BigDecimal("100"), "Cat");

        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(fundRepository.findById("fund-1")).thenReturn(Optional.of(fund));

        service.subscribe("client-1", "fund-1");

        verify(notificationSender, times(1)).sendEmail(eq("mail@test.com"), anyString());
        verify(notificationSender, times(1)).sendSms(eq("+573001112233"), anyString());
    }

    @Test
    void subscribe_ShouldUseLegacyFallbackToEmail() {
        Client client = new Client("client-1", "N A", new BigDecimal("1000"), "mail@test.com");
        Fund fund = new Fund("fund-1", "Fondo A", new BigDecimal("100"), "Cat");

        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(fundRepository.findById("fund-1")).thenReturn(Optional.of(fund));

        service.subscribe("client-1", "fund-1");

        verify(notificationSender, times(1)).sendEmail(eq("mail@test.com"), anyString());
        verify(notificationSender, never()).sendSms(anyString(), anyString());
    }
}
