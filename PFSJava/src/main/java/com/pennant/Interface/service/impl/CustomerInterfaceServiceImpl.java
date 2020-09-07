package com.pennant.Interface.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.systemmasters.DesignationDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.reports.AvailCustomerDetail;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.jdbc.search.Filter;

public class CustomerInterfaceServiceImpl implements CustomerInterfaceService {

	private static Logger logger = Logger.getLogger(CustomerInterfaceServiceImpl.class);

	private CustomerStatusCodeDAO customerStatusCodeDAO;
	private DesignationDAO designationDAO;

	public CustomerInterfaceServiceImpl() {
		super();
	}

	public Customer fetchCustomerDetails(Customer customer) throws InterfaceException {
		logger.debug("Entering");

		return customer;
	}

	/**
	 * Method for Creating Customer CIF in Core Banking System
	 */
	@Override
	public String generateNewCIF(String operation, Customer customer, String finReference) throws InterfaceException {
		logger.debug("Entering");

		return "";
	}

	/**
	 * Method for Fetch Customer Availment Ticket Details
	 * 
	 * @throws CustomerNotFoundException
	 */
	@Override
	public AvailCustomerDetail fetchAvailCustDetails(AvailCustomerDetail detail, BigDecimal newExposure, String ccy)
			throws InterfaceException {
		logger.debug("Entering");

		return detail;
	}

	/**
	 * Method for Fetching Customer Details information
	 */
	@Override
	public CustomerDetails getCustomerInfoByInterface(String custCIF, String custLoc) throws InterfaceException {
		logger.debug("Entering");

		return null;
	}

	public Filter[] getDescription(String filed, Object value) {
		Filter[] masterCodeFiler = new Filter[1];
		masterCodeFiler[0] = new Filter(filed, value, Filter.OP_EQUAL);
		return masterCodeFiler;
	}

	/**
	 * get the duplicate customers from Interface DB based on the rule executed
	 * 
	 * @throws InterfaceException
	 */
	@Override
	public List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup customerDedup) throws InterfaceException {
		return new ArrayList<CustomerDedup>();
	}

	/**
	 * Method for create new Customer by sending request through MQ
	 */
	@Override
	public String createNewCustomer(CustomerDetails customerDetail) throws InterfaceException {
		logger.debug("Entering");

		return null;
	}

	/**
	 * Method for update core customer details
	 * 
	 * @throws InterfaceException
	 * 
	 */
	@Override
	public void updateCoreCustomer(CustomerDetails customerDetails) throws InterfaceException {
		logger.debug("Entering");
	}

	/**
	 * Method for send ReserveCIF request to MDM Interface
	 * 
	 * @param customer
	 * @throws InterfaceException
	 */
	@Override
	public String reserveCIF(Customer customer) throws InterfaceException {
		logger.debug("Entering");

		return null;
	}

	/**
	 * Method for send ReleaseCIF request to MDM Interface
	 * 
	 * @param customer
	 * @param reserveRefNum
	 * @throws InterfaceException
	 */
	@Override
	public String releaseCIF(Customer customer, String reserveRefNum) throws InterfaceException {
		return null;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
		return customerStatusCodeDAO;
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public DesignationDAO getDesignationDAO() {
		return designationDAO;
	}

	public void setDesignationDAO(DesignationDAO designationDAO) {
		this.designationDAO = designationDAO;
	}
}
