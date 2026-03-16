package com.btg.funds.adapter.in.web;

import com.btg.funds.adapter.in.web.dto.FundResponse;
import com.btg.funds.application.port.in.GetAvailableFundsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/funds")
public class FundController {

    private final GetAvailableFundsUseCase getAvailableFundsUseCase;

    public FundController(GetAvailableFundsUseCase getAvailableFundsUseCase) {
        this.getAvailableFundsUseCase = getAvailableFundsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<FundResponse>> getFunds() {
        return ResponseEntity.ok(getAvailableFundsUseCase.getAvailableFunds());
    }
}
