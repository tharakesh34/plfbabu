package com.pennant.corebanking.process.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.chequeverification.ChequeStatus;
import com.pennant.coreinterface.model.chequeverification.ChequeVerification;
import com.pennant.coreinterface.process.ChequeVerificationProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class ChequeVerificationProcessImpl implements ChequeVerificationProcess {
	
	private static final Logger logger = Logger.getLogger(ChequeVerificationProcessImpl.class);

	public ChequeVerificationProcessImpl() {
		super();
	}
	
	@Override
	public ChequeVerification sendChequeVerificationReq(ChequeVerification chequeVerification) 
			throws InterfaceException {
		
		logger.debug("Entering");
		
		chequeVerification.setReturnCode("0000");
		chequeVerification.setReturnText("SUCCESS");
		ChequeStatus status = new ChequeStatus();
		status.setChequeNo("3");
		status.setValidity("Validation");
		
		ChequeStatus status1 = new ChequeStatus();
		status1.setChequeNo("7");
		status1.setValidity("Validation");
		
		List<ChequeStatus> stsList = new ArrayList<ChequeStatus>();
		stsList.add(status);
		stsList.add(status1);
		chequeVerification.setChequeStsList(stsList);
		
		logger.debug("Leaving");
		return chequeVerification;
	}

}
