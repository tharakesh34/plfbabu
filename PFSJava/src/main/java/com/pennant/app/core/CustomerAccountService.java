package com.pennant.app.core;

import org.apache.log4j.Logger;

public class CustomerAccountService extends ServiceHelper {
	private static final long	serialVersionUID	= 1442146139821584760L;
	private Logger				logger				= Logger.getLogger(CustomerAccountService.class);

	/**
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processCustomerAccountUpdate() throws Exception {
		logger.debug(" Entering ");

		getPostingsDAO();
		getAccountProcessUtil();

		logger.debug(" Leaving ");

	}

}
