package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.CoreBankAvailCustomer;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CustomerCollateral;
import com.pennant.coreinterface.model.customer.FinanceCustomerDetails;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennanttech.pennapps.core.InterfaceException;

public interface CustomerDataProcess {

	InterfaceCustomerDetail getCustomerFullDetails(String custCIF, String custLoc) throws InterfaceException;
	
	List<CustomerCollateral> getCustomerCollateral(String custCIF) throws InterfaceException;
	
	CoreBankAvailCustomer fetchAvailInformation(CoreBankAvailCustomer coreCust) throws InterfaceException;

	CoreBankingCustomer fetchInformation(CoreBankingCustomer coreCust) throws InterfaceException;

	FinanceCustomerDetails fetchFinCustDetails(FinanceCustomerDetails financeCustomerDetails) throws InterfaceException;
	
}
