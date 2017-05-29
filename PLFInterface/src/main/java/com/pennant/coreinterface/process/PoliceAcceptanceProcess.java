package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.customer.InterfaceMortgageDetail;
import com.pennant.exception.PFFInterfaceException;


public interface PoliceAcceptanceProcess {

	InterfaceMortgageDetail getPoliceAcceptance(InterfaceMortgageDetail mortgageDetail) throws PFFInterfaceException;
	InterfaceMortgageDetail cancelMortage(String transactionId) throws PFFInterfaceException;
}
