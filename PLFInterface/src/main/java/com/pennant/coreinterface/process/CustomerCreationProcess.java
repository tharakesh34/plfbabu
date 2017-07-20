package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.CoreBankNewCustomer;
import com.pennant.coreinterface.model.CoreCustomerDedup;
import com.pennant.coreinterface.model.customer.InterfaceCustomer;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennanttech.pennapps.core.InterfaceException;

public interface CustomerCreationProcess {

	String generateNewCIF(CoreBankNewCustomer customer)	throws InterfaceException;
	
	String createNewCustomer(InterfaceCustomerDetail customerDetail) throws InterfaceException;
	
	List<CoreCustomerDedup> fetchCustomerDedupDetails(CoreCustomerDedup customerDedup) throws InterfaceException;

	void updateCoreCustomer(InterfaceCustomerDetail interfaceCustomerDetail) throws InterfaceException;

	String reserveCIF(InterfaceCustomer coreCusomer) throws InterfaceException;

	String releaseCIF(InterfaceCustomer coreCustomer, String reserveRefNum) throws InterfaceException;
}
