package com.btg.funds.application.service;

import com.btg.funds.adapter.in.web.dto.FundResponse;
import com.btg.funds.adapter.in.web.dto.TransactionResponse;
import com.btg.funds.application.port.in.GetAvailableFundsUseCase;
import com.btg.funds.application.port.in.GetClientHistoryUseCase;
import com.btg.funds.application.port.out.FundRepository;
import com.btg.funds.application.port.out.SubscriptionRepository;
import com.btg.funds.application.port.out.TransactionRepository;
import com.btg.funds.domain.Fund;
import com.btg.funds.domain.Subscription;
import com.btg.funds.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FundRetrievalService implements GetAvailableFundsUseCase, GetClientHistoryUseCase {

    private final FundRepository fundRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TransactionRepository transactionRepository;

    public FundRetrievalService(FundRepository fundRepository,
                                SubscriptionRepository subscriptionRepository,
                                TransactionRepository transactionRepository) {
        this.fundRepository = fundRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<FundResponse> getAvailableFunds() {
        return fundRepository.findAll().stream()
                .map(fund -> new FundResponse(fund.getId(), fund.getName(), fund.getMinimumAmount(), fund.getCategory()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getHistory(String clientId) {
        List<Subscription> subscriptions = subscriptionRepository.findByClientId(clientId);
        
        Map<String, String> fundNames = fundRepository.findAll().stream()
                .collect(Collectors.toMap(Fund::getId, Fund::getName));
                
        List<TransactionResponse> history = new ArrayList<>();
        
        for (Subscription sub : subscriptions) {
            String fundName = fundNames.getOrDefault(sub.getFundId(), "Unknown");
            List<Transaction> txs = transactionRepository.findBySubscriptionId(sub.getId());
            for (Transaction tx : txs) {
                history.add(new TransactionResponse(fundName, tx.getType(), tx.getAmount(), tx.getDate()));
            }
        }
        
        history.sort(Comparator.comparing(TransactionResponse::getDate).reversed());
        
        return history;
    }
}
