package com.btg.funds.application.service;

import com.btg.funds.adapter.in.web.dto.CancellationResponse;
import com.btg.funds.adapter.in.web.dto.SubscriptionResponse;
import com.btg.funds.application.port.in.CancelSubscriptionUseCase;
import com.btg.funds.application.port.in.SubscribeToFundUseCase;
import com.btg.funds.application.port.out.ClientRepository;
import com.btg.funds.application.port.out.FundRepository;
import com.btg.funds.application.port.out.SubscriptionRepository;
import com.btg.funds.application.port.out.TransactionRepository;
import com.btg.funds.application.port.out.NotificationSender;
import com.btg.funds.domain.Client;
import com.btg.funds.domain.Fund;
import com.btg.funds.domain.NotificationChannel;
import com.btg.funds.domain.Subscription;
import com.btg.funds.domain.Transaction;
import com.btg.funds.domain.exception.ClientNotFoundException;
import com.btg.funds.domain.exception.FundNotFoundException;
import com.btg.funds.domain.exception.SubscriptionNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FundSubscriptionService implements SubscribeToFundUseCase, CancelSubscriptionUseCase {

    private final ClientRepository clientRepository;
    private final FundRepository fundRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TransactionRepository transactionRepository;
    private final List<NotificationSender> notificationSenders;

    public FundSubscriptionService(ClientRepository clientRepository,
                                   FundRepository fundRepository,
                                   SubscriptionRepository subscriptionRepository,
                                   TransactionRepository transactionRepository,
                                   List<NotificationSender> notificationSenders) {
        this.clientRepository = clientRepository;
        this.fundRepository = fundRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.transactionRepository = transactionRepository;
        this.notificationSenders = notificationSenders;
    }

    @Override
    public SubscriptionResponse subscribe(String clientId, String fundId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> new FundNotFoundException(fundId));

        BigDecimal amount = fund.getMinimumAmount();

        String subscriptionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        Subscription subscription = Subscription.create(subscriptionId, client, fund, amount, now);

        subscriptionRepository.save(subscription);
        clientRepository.save(client);

        Transaction transaction = new Transaction(subscriptionId, "APERTURA", amount, now);
        transactionRepository.save(transaction);

        notifyClient(client, "Suscripción exitosa al fondo " + fund.getName() + " por un monto de " + amount);

        return new SubscriptionResponse(subscription.getId(), fund.getName(), subscription.getAmount(), client.getBalance());
    }

    @Override
    public CancellationResponse cancelSubscription(String clientId, String fundId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
        Subscription subscription = subscriptionRepository.findByClientIdAndFundId(clientId, fundId)
                .orElseThrow(() -> new SubscriptionNotFoundException(fundId));

        if (subscription.isCancelled()) {
            throw new SubscriptionNotFoundException(fundId);
        }

        boolean wasCancelled = subscription.isCancelled();
        subscription.cancel(client);
        subscriptionRepository.save(subscription);
        clientRepository.save(client);

        if (!wasCancelled) {
            Transaction transaction = new Transaction(subscription.getId(), "CANCELACION", subscription.getAmount(), LocalDateTime.now());
            transactionRepository.save(transaction);
        }
        
        notifyClient(client,
                "Cancelación exitosa de la suscripción al fondo " + fundRepository.findById(subscription.getFundId())
                        .map(com.btg.funds.domain.Fund::getName)
                        .orElse(subscription.getFundId()) +
                        " por un monto de " + subscription.getAmount());

        return new CancellationResponse(fundId, subscription.getAmount(), client.getBalance());
    }

    private void notifyClient(Client client, String message) {
        boolean hasExplicitPreference = !client.getNotificationPreferences().isEmpty();
        boolean sendEmail = hasExplicitPreference
                ? client.getNotificationPreferences().contains(NotificationChannel.EMAIL)
                : client.canNotifyByEmail();
        boolean sendSms = hasExplicitPreference && client.getNotificationPreferences().contains(NotificationChannel.SMS);

        for (NotificationSender sender : notificationSenders) {
            if (sendEmail && client.canNotifyByEmail()) {
                sender.sendEmail(client.getEmail(), message);
            }
            if (sendSms && client.canNotifyBySms()) {
                sender.sendSms(client.getPhoneNumber(), message);
            }
        }
    }
}
