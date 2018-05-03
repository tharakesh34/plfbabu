package com.pennant.corebanking.process.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.model.CoreBankNewCustomer;
import com.pennant.coreinterface.model.CoreCustomerDedup;
import com.pennant.coreinterface.model.customer.InterfaceCustomer;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.coreinterface.process.CustomerCreationProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerCreationProcessImpl extends GenericProcess implements CustomerCreationProcess {
	
	private static Logger logger = Logger.getLogger(CustomerCreationProcessImpl.class);
	private InterfaceDAO interfaceDAO;
	
	public CustomerCreationProcessImpl()  {
		super();
	}

	@Override
	public String generateNewCIF(CoreBankNewCustomer customer) throws InterfaceException {	
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return "";
	}
	
	/**
	 * Method for fetching customer dedup details from corebank system
	 * 
	 */
	@Override
	public List<CoreCustomerDedup> fetchCustomerDedupDetails(CoreCustomerDedup customerDedup) {
		return getInterfaceDAO().fetchCustomerDedupDetails(customerDedup);
	}

	/**
	 * Method to create new customer in corebank system
	 * 
	 */
	@Override
	public String createNewCustomer(InterfaceCustomerDetail customerDetail)	throws InterfaceException {
		return "0000";
	}

	@Override
	public void updateCoreCustomer(InterfaceCustomerDetail interfaceCustomerDetail)	throws InterfaceException {
	}

	@Override
	public String reserveCIF(InterfaceCustomer coreCusomer) {
		return "0000";
	}

	@Override
	public String releaseCIF(InterfaceCustomer coreCustomer, String reserveRefNum) throws InterfaceException {
		return "0000";
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public InterfaceDAO getInterfaceDAO() {
		return interfaceDAO;
	}
	public void setInterfaceDAO(InterfaceDAO interfaceDAO) {
		this.interfaceDAO = interfaceDAO;
	}

}
