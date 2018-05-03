package com.pennant.equation.process.impl;

import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.handlinginstructions.HandlingInstruction;
import com.pennant.coreinterface.process.FinanceMaintenanceProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class FinanceMaintenanceProcessImpl implements FinanceMaintenanceProcess {

private static final Logger logger = Logger.getLogger(FinanceMaintenanceProcessImpl.class);
	
	public FinanceMaintenanceProcessImpl() {
		
	}
	
	@Override
	public HandlingInstruction sendHandlingInstruction(HandlingInstruction handlingInstruction)
			throws InterfaceException {
		logger.debug("Entering");
		HandlingInstruction handlInstResponse = new HandlingInstruction();
		handlInstResponse.setReturnCode("0000");
		handlInstResponse.setReturnText("SUCCESS");
		logger.debug("Leaving");
		return handlInstResponse;
	}

}
