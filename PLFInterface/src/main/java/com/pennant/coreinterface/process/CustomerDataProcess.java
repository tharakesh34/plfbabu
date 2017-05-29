package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.CoreBankAvailCustomer;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CustomerCollateral;
import com.pennant.coreinterface.model.customer.FinanceCustomerDetails;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.exception.PFFInterfaceException;

public interface CustomerDataProcess {

	InterfaceCustomerDetail getCustomerFullDetails(String custCIF, String custLoc) throws PFFInterfaceException;
	
	List<CustomerCollateral> getCustomerCollateral(String custCIF) throws PFFInterfaceException;
	
	CoreBankAvailCustomer fetchAvailInformation(CoreBankAvailCustomer coreCust) throws PFFInterfaceException;

	CoreBankingCustomer fetchInformation(CoreBankingCustomer coreCust) throws PFFInterfaceException;

	FinanceCustomerDetails fetchFinCustDetails(FinanceCustomerDetails financeCustomerDetails) throws PFFInterfaceException;
	
}
