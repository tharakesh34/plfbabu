package com.pennant.equation.process.impl;

import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.deposits.FetchDeposit;
import com.pennant.coreinterface.model.deposits.FetchDepositDetail;
import com.pennant.coreinterface.process.DepositDetailProcess;
import com.pennant.exception.InterfaceException;

public class DepositDetailProcessImpl implements DepositDetailProcess {

	private final static Logger logger = Logger.getLogger(DepositDetailProcessImpl.class);
	
	public DepositDetailProcessImpl() {
		
	}
	
	@Override
	public FetchDeposit fetchDeposits(FetchDeposit fetchDeposit) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return null;
	}

	@Override
	public FetchDepositDetail fetchDepositDetails(FetchDepositDetail fetchDepositDetail) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return null;
	}

}
