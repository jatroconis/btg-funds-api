package com.btg.funds.application.port.out;

import com.btg.funds.domain.Fund;
import java.util.List;
import java.util.Optional;

public interface FundRepository {
    Optional<Fund> findById(String id);
    List<Fund> findAll();
    void save(Fund fund);
}
