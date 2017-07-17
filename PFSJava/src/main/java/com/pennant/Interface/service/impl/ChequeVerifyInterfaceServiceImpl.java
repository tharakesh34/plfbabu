package com.pennant.Interface.service.impl;

import org.apache.log4j.Logger;

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
		return getChequeVerificationProcess().sendChequeVerificationReq(chequeVerification);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public ChequeVerificationProcess getChequeVerificationProcess() {
		return chequeVerificationProcess;
	}

	public void setChequeVerificationProcess(
			ChequeVerificationProcess chequeVerificationProcess) {
		this.chequeVerificationProcess = chequeVerificationProcess;
	}

}
