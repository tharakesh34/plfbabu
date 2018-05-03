package com.pennant.mq.process.impl;

import com.pennant.coreinterface.model.customer.InterfaceMortgageDetail;
import com.pennant.coreinterface.process.PoliceAcceptanceProcess;
import com.pennant.mq.processutil.PoliceAcceptanceUtilProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class PoliceAcceptanceServiceImpl implements PoliceAcceptanceProcess {

	private PoliceAcceptanceUtilProcess policeAcceptanceUtilProcess;

	public PoliceAcceptanceServiceImpl() {
		
	}

	@Override
	public InterfaceMortgageDetail getPoliceAcceptance(InterfaceMortgageDetail mortgageDetail) throws InterfaceException {
		return getPoliceAcceptanceUtilProcess().getPoliceAcceptance(mortgageDetail, InterfaceMasterConfigUtil.CREATE_MORTGAGE);
	}
	
	@Override
	public InterfaceMortgageDetail cancelMortage(String transactionId) throws InterfaceException {
		return getPoliceAcceptanceUtilProcess().cancelMortage(transactionId, InterfaceMasterConfigUtil.CANCEL_MORTGAGE);
	}
	
	public PoliceAcceptanceUtilProcess getPoliceAcceptanceUtilProcess() {
		return policeAcceptanceUtilProcess;
	}
	public void setPoliceAcceptanceUtilProcess(
			PoliceAcceptanceUtilProcess policeAcceptanceUtilProcess) {
		this.policeAcceptanceUtilProcess = policeAcceptanceUtilProcess;
	}

}
