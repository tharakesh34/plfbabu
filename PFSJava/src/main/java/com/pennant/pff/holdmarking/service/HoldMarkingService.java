package com.pennant.pff.holdmarking.service;

import java.math.BigDecimal;

import com.pennant.backend.model.finance.FinanceMain;

public interface HoldMarkingService {

	void removeHold(FinanceMain fm);

	void updateHoldRemoval(BigDecimal amount, long finId, boolean isFateCorrection);

	void updateFundRecovery(BigDecimal amount, String accNum, long finId);
}