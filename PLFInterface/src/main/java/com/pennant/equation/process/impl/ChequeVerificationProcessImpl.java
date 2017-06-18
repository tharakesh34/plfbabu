package com.pennant.equation.process.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.chequeverification.ChequeStatus;
import com.pennant.coreinterface.model.chequeverification.ChequeVerification;
import com.pennant.coreinterface.process.ChequeVerificationProcess;
import com.pennanttech.pff.core.InterfaceException;

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
		status.setChequeNo("1234567899");
		status.setValidity("Validation");
		
		List<ChequeStatus> stsList = new ArrayList<ChequeStatus>();
		stsList.add(status);
		chequeVerification.setChequeStsList(stsList);
		
		logger.debug("Leaving");
		return chequeVerification;
	}

}
