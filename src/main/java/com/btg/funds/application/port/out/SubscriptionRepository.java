package com.btg.funds.application.port.out;

import com.btg.funds.domain.Subscription;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {
    Optional<Subscription> findById(String id);
    Optional<Subscription> findByClientIdAndFundId(String clientId, String fundId);
    List<Subscription> findByClientId(String clientId);
    void save(Subscription subscription);
    void delete(String clientId, String fundId);
}
