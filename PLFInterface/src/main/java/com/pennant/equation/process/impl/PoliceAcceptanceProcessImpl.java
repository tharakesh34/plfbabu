package com.pennant.equation.process.impl;

import com.pennant.coreinterface.model.customer.InterfaceMortgageDetail;
import com.pennant.coreinterface.process.PoliceAcceptanceProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class PoliceAcceptanceProcessImpl implements PoliceAcceptanceProcess {

	public PoliceAcceptanceProcessImpl() {
		
	}

	@Override
	public InterfaceMortgageDetail getPoliceAcceptance(InterfaceMortgageDetail mortgageDetail) throws InterfaceException {
		return null;
	}

	@Override
	public InterfaceMortgageDetail cancelMortage(String transactionId) throws InterfaceException {
		return null;
	}
	
}
