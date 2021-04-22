package com.pennant.equation.process.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.coreinterface.model.deposits.FetchDeposit;
import com.pennant.coreinterface.model.deposits.FetchDepositDetail;
import com.pennant.coreinterface.process.DepositDetailProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class DepositDetailProcessImpl implements DepositDetailProcess {

	private static final Logger logger = LogManager.getLogger(DepositDetailProcessImpl.class);

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
