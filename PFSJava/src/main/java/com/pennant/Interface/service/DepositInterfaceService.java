package com.pennant.Interface.service;

import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.coreinterface.model.deposits.FetchDeposit;
import com.pennant.exception.PFFInterfaceException;

public interface DepositInterfaceService {

	FetchDeposit fetchDeposits(FetchDeposit fetchDeposit) throws PFFInterfaceException;
	
	FinCollaterals fetchDepositDetails(String depositReference) throws PFFInterfaceException;
}
