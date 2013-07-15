package com.pennant.backend.service.financerepay;

import java.math.BigDecimal;

public interface RepayDataService {
	BigDecimal getRepayData(String finReference);
}
