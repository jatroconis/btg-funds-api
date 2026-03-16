package com.btg.funds.adapter.in.web;

import com.btg.funds.adapter.in.web.dto.FundResponse;
import com.btg.funds.application.port.in.GetAvailableFundsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundControllerTest {

    @Mock
    private GetAvailableFundsUseCase getAvailableFundsUseCase;

    @InjectMocks
    private FundController fundController;

    private List<FundResponse> funds;

    @BeforeEach
    void setUp() {
        funds = Collections.singletonList(
                new FundResponse("fund123", "FPV_BTG", new BigDecimal("500000"), "FPV")
        );
    }

    @Test
    void shouldReturnAvailableFunds() {
        when(getAvailableFundsUseCase.getAvailableFunds()).thenReturn(funds);

        ResponseEntity<List<FundResponse>> result = fundController.getFunds();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(funds, result.getBody());
        verify(getAvailableFundsUseCase, times(1)).getAvailableFunds();
    }
}
