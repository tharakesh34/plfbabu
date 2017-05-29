package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.CoreBankNewCustomer;
import com.pennant.coreinterface.model.CoreCustomerDedup;
import com.pennant.coreinterface.model.customer.InterfaceCustomer;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.exception.PFFInterfaceException;

public interface CustomerCreationProcess {

	String generateNewCIF(CoreBankNewCustomer customer)	throws PFFInterfaceException;
	
	String createNewCustomer(InterfaceCustomerDetail customerDetail) throws PFFInterfaceException;
	
	List<CoreCustomerDedup> fetchCustomerDedupDetails(CoreCustomerDedup customerDedup) throws PFFInterfaceException;

	void updateCoreCustomer(InterfaceCustomerDetail interfaceCustomerDetail) throws PFFInterfaceException;

	String reserveCIF(InterfaceCustomer coreCusomer) throws PFFInterfaceException;

	String releaseCIF(InterfaceCustomer coreCustomer, String reserveRefNum) throws PFFInterfaceException;
}
