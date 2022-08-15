package com.pennant.backend.service.reports;

import com.pennant.backend.model.systemmasters.BalanceConfirmation;

public interface BalanceConfirmationService {

	BalanceConfirmation getBalanceConfirmation(String finReference);
}
