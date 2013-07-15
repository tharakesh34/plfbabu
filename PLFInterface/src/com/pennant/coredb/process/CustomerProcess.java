package com.pennant.coredb.process;

import org.apache.log4j.Logger;

import com.pennant.coredb.dao.CoreDBDAO;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.vo.CoreBankingCustomer;

public class CustomerProcess {

	private static Logger logger = Logger.getLogger(CustomerProcess.class);

	private CoreDBDAO coreDBDao;

	/**
	 * This method fetches the Customer information for the given
	 * CustomerID(CIF) by calling Equation Program PTKAS13PR.
	 * 
	 * @Param Customer
	 * @Return Customer
	 */
	public CoreBankingCustomer fetchInformation(CoreBankingCustomer coreCust) throws CustomerNotFoundException {
		logger.debug("Entering");

		try {
			coreCust = this.coreDBDao.fetchCustomerDetails(coreCust);
		} catch (Exception e) {
			logger.error("Exception " + e);
			throw new CustomerNotFoundException(e.getMessage());
		}
		logger.debug("Leaving");
		return coreCust;
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
