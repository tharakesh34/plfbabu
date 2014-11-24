package com.pennant.corebanking.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.exception.CustomerLimitProcessException;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.service.CustomerLimitProcess;

public class CustomerLimitProcessImpl extends GenericProcess implements CustomerLimitProcess {

	private static Logger logger = Logger.getLogger(CustomerLimitProcessImpl.class);

	private InterfaceDAO interfaceDAO;
	
	/**
	 * Method for Fetching List of Limit Category Customers
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws CustomerLimitProcessException 
	 */
	@Override
	public Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize) throws CustomerLimitProcessException {
		logger.debug("Entering");

		logger.debug("Leaving");
		return null;
	}

	/**
	 * Method for Fetching List of Limit details depends on Parameter key fields
	 * @param CustomerLimit
	 * @return List<CustomerLimit>
	 * @throws 	CustomerLimitProcessException
	 * */
	@Override
	public List<CustomerLimit> fetchLimitDetails(CustomerLimit custLimit) throws CustomerLimitProcessException {
		logger.debug("Entering");

		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Method for Fetching List of Limit details depends on Parameter key fields
	 * @param CustomerLimit
	 * @return List<CustomerLimit>
	 * @throws 	CustomerLimitProcessException
	 * */
	@Override
	public List<CustomerLimit> fetchLimitEnqDetails(CustomerLimit custLimit) throws CustomerLimitProcessException {
		logger.debug("Entering");

		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Method for Fetching List of Group Limit details depends on Parameter key fields
	 * @param CustomerLimit
	 * @return List<CustomerLimit>
	 * @throws 	CustomerLimitProcessException
	 * */
	@Override
	public List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit custLimit) throws CustomerLimitProcessException {
		logger.debug("Entering");

		logger.debug("Leaving");
		return null;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public InterfaceDAO getInterfaceDAO() {
		return interfaceDAO;
	}
	public void setInterfaceDAO(InterfaceDAO interfaceDAO) {
		this.interfaceDAO = interfaceDAO;
	}

}
