package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.deposits.FetchDeposit;
import com.pennant.coreinterface.model.deposits.FetchDepositDetail;
import com.pennant.exception.PFFInterfaceException;

public interface DepositDetailProcess {

	FetchDeposit fetchDeposits(FetchDeposit fetchDeposit) throws PFFInterfaceException;
	
	FetchDepositDetail fetchDepositDetails(FetchDepositDetail fetchDepositDetail) throws PFFInterfaceException;
}
