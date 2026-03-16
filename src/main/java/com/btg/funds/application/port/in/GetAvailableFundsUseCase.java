package com.btg.funds.application.port.in;

import com.btg.funds.adapter.in.web.dto.FundResponse;
import java.util.List;

public interface GetAvailableFundsUseCase {
    List<FundResponse> getAvailableFunds();
}
