package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.customer.InterfaceMortgageDetail;
import com.pennanttech.pennapps.core.InterfaceException;


public interface PoliceAcceptanceProcess {

	InterfaceMortgageDetail getPoliceAcceptance(InterfaceMortgageDetail mortgageDetail) throws InterfaceException;
	InterfaceMortgageDetail cancelMortage(String transactionId) throws InterfaceException;
}
