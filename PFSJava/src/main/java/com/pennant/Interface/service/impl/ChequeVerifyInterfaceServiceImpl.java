package com.pennant.Interface.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.Interface.service.ChequeVerifyInterfaceService;
import com.pennant.coreinterface.model.chequeverification.ChequeVerification;
import com.pennant.coreinterface.process.ChequeVerificationProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class ChequeVerifyInterfaceServiceImpl implements ChequeVerifyInterfaceService {

	private static final Logger logger = Logger.getLogger(ChequeVerifyInterfaceServiceImpl.class);

	public ChequeVerifyInterfaceServiceImpl() {

	}

	private ChequeVerificationProcess chequeVerificationProcess;

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
		if (chequeVerificationProcess != null) {
			return chequeVerificationProcess.sendChequeVerificationReq(chequeVerification);
		}
		return null;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/*
	 * public ChequeVerificationProcess getChequeVerificationProcess() { return chequeVerificationProcess; }
	 */
	@Autowired(required = false)
	public void setChequeVerificationProcess(ChequeVerificationProcess chequeVerificationProcess) {
		this.chequeVerificationProcess = chequeVerificationProcess;
	}

}
