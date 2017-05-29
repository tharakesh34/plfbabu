package com.pennant.Interface.service.impl;

import org.apache.log4j.Logger;

import com.pennant.Interface.service.ChequeVerifyInterfaceService;
import com.pennant.coreinterface.model.chequeverification.ChequeVerification;
import com.pennant.coreinterface.process.ChequeVerificationProcess;
import com.pennant.exception.PFFInterfaceException;

public class ChequeVerifyInterfaceServiceImpl implements ChequeVerifyInterfaceService {

	private final static Logger logger = Logger.getLogger(ChequeVerifyInterfaceServiceImpl.class);
	
	public ChequeVerifyInterfaceServiceImpl() {
		
	}
	
	private ChequeVerificationProcess chequeVerificationProcess;

	/**
	 * Method for sending cheque verification request to middleware
	 * 
	 * @param chequeVerification
	 * 
	 * @return ChequeVerification
	 * @throws PFFInterfaceException 
	 */
	@Override
	public ChequeVerification verifySecurityCheque(ChequeVerification chequeVerification) throws PFFInterfaceException {
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
