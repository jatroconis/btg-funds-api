package com.btg.funds.infrastructure.config;

import com.btg.funds.application.port.out.ClientRepository;
import com.btg.funds.application.port.out.FundRepository;
import com.btg.funds.domain.Client;
import com.btg.funds.domain.Fund;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private FundRepository fundRepository;

    @Mock
    private ApplicationArguments args;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void run_whenDataNotExists_shouldCreateData() throws Exception {
        when(clientRepository.findById("1")).thenReturn(Optional.empty());
        when(fundRepository.findById(anyString())).thenReturn(Optional.empty());

        dataInitializer.run(args);

        verify(clientRepository, times(1)).save(any(Client.class));
        verify(fundRepository, times(5)).save(any(Fund.class));
    }

    @Test
    void run_whenDataExists_shouldNotCreateData() throws Exception {
        when(clientRepository.findById("1")).thenReturn(Optional.of(new Client("1", "Juan", new BigDecimal("500000"), "juan@example.com")));
        when(fundRepository.findById(anyString())).thenReturn(Optional.of(new Fund("f1", "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), "FPV")));

        dataInitializer.run(args);

        verify(clientRepository, never()).save(any(Client.class));
        verify(fundRepository, never()).save(any(Fund.class));
    }
}
