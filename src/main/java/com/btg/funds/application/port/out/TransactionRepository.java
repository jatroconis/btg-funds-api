package com.btg.funds.application.port.out;

import com.btg.funds.domain.Transaction;
import java.util.List;

public interface TransactionRepository {
    List<Transaction> findBySubscriptionId(String subscriptionId);
    void save(Transaction transaction);
}
