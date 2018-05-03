package com.pennant.corebanking.process.impl;

import org.apache.commons.lang.StringUtils;

import com.pennant.coreinterface.model.customer.InterfaceMortgageDetail;
import com.pennant.coreinterface.process.PoliceAcceptanceProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class PoliceAcceptanceProcessImpl implements PoliceAcceptanceProcess {

	public PoliceAcceptanceProcessImpl() {
		
	}

	@Override
	public InterfaceMortgageDetail getPoliceAcceptance(InterfaceMortgageDetail mortgageDetail) throws InterfaceException {
		InterfaceMortgageDetail mortDetail = new InterfaceMortgageDetail();
		if(StringUtils.equals("7894588855", mortgageDetail.getChassisNo())) {
			throw new InterfaceException("PTI3001", "Failed");
		}
		mortDetail.setTransactionId("998877");
		return mortDetail;
	}

	@Override
	public InterfaceMortgageDetail cancelMortage(String transactionId) throws InterfaceException {
		
		InterfaceMortgageDetail mortDetail = new InterfaceMortgageDetail();
		if(StringUtils.isBlank(transactionId)) {
			throw new InterfaceException("PTI3001", "Mortgage Cancellation Failed.");
		}
		mortDetail.setReturncode("0000");
		mortDetail.setReturnText("Success");
		return mortDetail;
	}

}
