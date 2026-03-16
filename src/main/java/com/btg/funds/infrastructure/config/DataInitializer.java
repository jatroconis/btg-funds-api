package com.btg.funds.infrastructure.config;

import com.btg.funds.application.port.out.ClientRepository;
import com.btg.funds.application.port.out.FundRepository;
import com.btg.funds.domain.Client;
import com.btg.funds.domain.Fund;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final ClientRepository clientRepository;
    private final FundRepository fundRepository;

    public DataInitializer(ClientRepository clientRepository, FundRepository fundRepository) {
        this.clientRepository = clientRepository;
        this.fundRepository = fundRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (clientRepository.findById("1").isEmpty()) {
            Client client = new Client("1", "Juan", new BigDecimal("500000"), "juan@example.com");
            clientRepository.save(client);
            System.out.println(">>> [SEED] Client '1' (Juan) created.");
        }

        List<Fund> funds = List.of(
                new Fund("f1", "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), "FPV"),
                new Fund("f2", "FPV_BTG_PACTUAL_ECOPETROL", new BigDecimal("125000"), "FPV"),
                new Fund("f3", "DEUDAPRIVADA", new BigDecimal("50000"), "FIC"),
                new Fund("f4", "FDO-ACCIONES", new BigDecimal("250000"), "FIC"),
                new Fund("f5", "FPV_BTG_PACTUAL_DINAMICA", new BigDecimal("100000"), "FPV")
        );

        for (Fund fund : funds) {
            if (fundRepository.findById(fund.getId()).isEmpty()) {
                fundRepository.save(fund);
                System.out.println(">>> [SEED] Fund '" + fund.getName() + "' created.");
            }
        }
    }
}
