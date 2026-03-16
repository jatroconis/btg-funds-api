package com.btg.funds.application.port.out;

import com.btg.funds.domain.Client;
import java.util.Optional;

public interface ClientRepository {
    Optional<Client> findById(String id);
    void save(Client client);
}
