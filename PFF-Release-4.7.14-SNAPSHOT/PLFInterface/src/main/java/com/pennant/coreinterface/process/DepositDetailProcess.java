package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.deposits.FetchDeposit;
import com.pennant.coreinterface.model.deposits.FetchDepositDetail;
import com.pennanttech.pennapps.core.InterfaceException;

public interface DepositDetailProcess {

	FetchDeposit fetchDeposits(FetchDeposit fetchDeposit) throws InterfaceException;
	
	FetchDepositDetail fetchDepositDetails(FetchDepositDetail fetchDepositDetail) throws InterfaceException;
}
