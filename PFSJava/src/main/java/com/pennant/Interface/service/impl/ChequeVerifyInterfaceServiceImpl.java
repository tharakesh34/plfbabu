package com.pennant.Interface.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.Interface.service.ChequeVerifyInterfaceService;
import com.pennant.coreinterface.model.chequeverification.ChequeVerification;
import com.pennanttech.pennapps.core.InterfaceException;

public class ChequeVerifyInterfaceServiceImpl implements ChequeVerifyInterfaceService {

	private static final Logger logger = LogManager.getLogger(ChequeVerifyInterfaceServiceImpl.class);

	public ChequeVerifyInterfaceServiceImpl() {

	}

	/**
	 * Method for sending cheque verification request to middleware
	 * 
	 * @param chequeVerification
	 * 
	 * @return ChequeVerification
	 * @throws InterfaceException
	 */
	@Override
	public ChequeVerification verifySecurityCheque(ChequeVerification chequeVerification) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return null;
	}
}
