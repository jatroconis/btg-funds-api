package com.btg.funds.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FundTest {

    @Test
    void shouldCreateFundSuccessfully() {
        Fund fund = new Fund("1", "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), "FPV");
        assertEquals("1", fund.getId());
        assertEquals("FPV_BTG_PACTUAL_RECAUDADORA", fund.getName());
        assertEquals(new BigDecimal("75000"), fund.getMinimumAmount());
        assertEquals("FPV", fund.getCategory());
    }

    @Test
    void shouldThrowExceptionWhenMinimumAmountIsZeroOrLess() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Fund("1", "Fund 1", BigDecimal.ZERO, "Category");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Fund("1", "Fund 1", new BigDecimal("-100"), "Category");
        });
    }
}
