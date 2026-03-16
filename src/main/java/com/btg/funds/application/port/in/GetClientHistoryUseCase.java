package com.btg.funds.application.port.in;

import com.btg.funds.adapter.in.web.dto.TransactionResponse;
import java.util.List;

public interface GetClientHistoryUseCase {
    List<TransactionResponse> getHistory(String clientId);
}
