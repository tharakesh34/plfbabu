package com.pennant.coreinterface.service;

import java.util.List;

import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.model.CoreBankAvailCustomer;
import com.pennant.coreinterface.model.CoreBankNewCustomer;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CustomerCollateral;
import com.pennant.coreinterface.model.CustomerInterfaceData;

public interface CustomerDataProcess {

	CustomerInterfaceData getCustomerFullDetails(String custCIF, String custLoc) throws CustomerNotFoundException;
	
	List<CustomerCollateral> getCustomerCollateral(String custCIF) throws CustomerNotFoundException;
	
	CoreBankAvailCustomer fetchAvailInformation(CoreBankAvailCustomer coreCust) throws CustomerNotFoundException;

	String generateNewCIF(CoreBankNewCustomer customer)	throws CustomerNotFoundException;

	CoreBankingCustomer fetchInformation(CoreBankingCustomer coreCust) throws CustomerNotFoundException;
}
