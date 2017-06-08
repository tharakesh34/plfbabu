package com.pennant.corebanking.process.impl;

import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.handlinginstructions.HandlingInstruction;
import com.pennant.coreinterface.process.FinanceMaintenanceProcess;
import com.pennanttech.pff.core.InterfaceException;

public class FinanceMaintenanceProcessImpl implements FinanceMaintenanceProcess {

private final static Logger logger = Logger.getLogger(FinanceMaintenanceProcessImpl.class);
	
	public FinanceMaintenanceProcessImpl() {
		
	}
	
	@Override
	public HandlingInstruction sendHandlingInstruction(HandlingInstruction handlingInstruction)
			throws InterfaceException {
		logger.debug("Entering");
		HandlingInstruction handlInstResponse = new HandlingInstruction();
		handlInstResponse.setReferenceNum(handlingInstruction.getFinanceRef());
		handlInstResponse.setReturnCode("0000");
		handlInstResponse.setReturnText("SUCCESS");
		logger.debug("Leaving");
		return handlInstResponse;
	}

}
