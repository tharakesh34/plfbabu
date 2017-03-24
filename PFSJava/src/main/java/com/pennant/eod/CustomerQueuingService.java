package com.pennant.eod;

import java.util.Date;

import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;

public class CustomerQueuingService {

	private static Logger logger = Logger.getLogger(CustomerQueuingService.class);

	private CustomerQueuingDAO customerQueuingDAO;

	/**
	 * Delete the existing data and load the active finance customers while running EOD.<br> 
	 *    Which is one time process per EOD
	 * 
	 * @param date
	 */
	public void loadCustIds(Date date) {
		logger.debug("Entering");
		getCustomerQueuingDAO().delete();
		getCustomerQueuingDAO().prepareCustomerQueue(date);
		logger.debug("Leaving");
	}


	public void updateNoofRows(Date date,long noOfRows,String threadId) {
		getCustomerQueuingDAO().updateNoofRows(date,noOfRows, threadId);
	}


	public void updateAll(Date date,String threadId) {
		getCustomerQueuingDAO().updateAll(date,threadId);
	}

	/**
	 * Method for update customer NO-EOD status details after completion of NO-EOD process.
	 * 
	 * @param custId
	 * @param status
	 * @param error
	 */
	public void updateStart(Date date,long custId) {
		logger.debug("Entering");

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custId);
		customerQueuing.setEodDate(date);
		customerQueuing.setProgress(EodConstants.PROGRESS_START);
		customerQueuing.setStartTime(DateUtility.getSysDate());
		getCustomerQueuingDAO().update(customerQueuing,true);

		logger.debug("Leaving");
	}

	public void updateEnd(Date date,long custId) {
		logger.debug("Entering");

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custId);
		customerQueuing.setEodDate(date);
		customerQueuing.setEndTime(DateUtility.getSysDate());
		customerQueuing.setStatus(EodConstants.STATUS_SUCCESS);
		customerQueuing.setProgress(EodConstants.PROGRESS_COMPLETED);
		getCustomerQueuingDAO().update(customerQueuing,false);

		logger.debug("Leaving");
	}

	public void updateSucessFail(Date date,long custId,String error) {
		logger.debug("Entering");

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custId);
		customerQueuing.setEodDate(date);
		customerQueuing.setStatus(EodConstants.STATUS_FAILED);
		customerQueuing.setErrorLog(error);
		customerQueuing.setEndTime(DateUtility.getSysDate());
		customerQueuing.setProgress(EodConstants.PROGRESS_COMPLETED);
		getCustomerQueuingDAO().update(customerQueuing,false);

		logger.debug("Leaving");
	}

	public long getCustomerIdCount(Date date,String progress) {
		return getCustomerQueuingDAO().getCustomerIdCount(date,progress);
	}

	public CustomerQueuingDAO getCustomerQueuingDAO() {
		return customerQueuingDAO;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}
}
