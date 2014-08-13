package com.pennant.coredb.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.coredb.dao.CoreDBDAO;
import com.pennant.coreinterface.exception.CustomerLimitProcessException;
import com.pennant.coreinterface.vo.CustomerLimit;

public class CustomerLimitProcess {

	private static Logger logger = Logger.getLogger(CustomerLimitProcess.class);

	private CoreDBDAO coreDBDao;

	/**
	 * Method for Fetching List of Limit details depends on Parameter key fields
	 * @param CustomerLimit
	 * @return List<CustomerLimit>
	 * @throws 	CustomerLimitProcessException
	 * */
	public List<CustomerLimit> fetchLimitDetails(CustomerLimit custLimit) throws CustomerLimitProcessException {
		logger.debug("Entering");
		List<CustomerLimit> list = new ArrayList<CustomerLimit>();
		try {
			// TODO
		} catch (Exception e) {
			logger.error("Exception " + e);
			throw new CustomerLimitProcessException(e.getMessage());
		}
		logger.debug("Leaving");
		return list;
	}
	
	/**
	 * Method for Fetching List of Group Limit details depends on Parameter key fields
	 * @param CustomerLimit
	 * @return List<CustomerLimit>
	 * @throws 	CustomerLimitProcessException
	 * */
	public List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit custLimit) throws CustomerLimitProcessException {
		logger.debug("Entering");
		List<CustomerLimit> list = new ArrayList<CustomerLimit>();
		try {
			// TODO
		} catch (Exception e) {
			logger.error("Exception " + e);
			throw new CustomerLimitProcessException(e.getMessage());
		}
		logger.debug("Leaving");
		return list;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public CoreDBDAO getCoreDBDao() {
		return coreDBDao;
	}

	public void setCoreDBDao(CoreDBDAO coreDBDao) {
		this.coreDBDao = coreDBDao;
	}
}
