package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.customer.InterfaceMortgageDetail;
import com.pennant.exception.InterfaceException;


public interface PoliceAcceptanceProcess {

	InterfaceMortgageDetail getPoliceAcceptance(InterfaceMortgageDetail mortgageDetail) throws InterfaceException;
	InterfaceMortgageDetail cancelMortage(String transactionId) throws InterfaceException;
}
