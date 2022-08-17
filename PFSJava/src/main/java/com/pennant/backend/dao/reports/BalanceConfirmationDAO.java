package com.pennant.backend.dao.reports;

import com.pennant.backend.model.systemmasters.BalanceConfirmation;

public interface BalanceConfirmationDAO {

	BalanceConfirmation getBalanceConfirmation(String finReference);

}
