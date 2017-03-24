package com.pennant.eod;

import java.util.Date;

import com.pennant.app.core.DateService;
import com.pennant.app.core.RepayQueueService;
import com.pennant.app.util.DateUtility;
import com.pennant.eod.dao.CustomerDatesDAO;
import com.pennant.eod.util.EODProperties;

public class PreEodService {

	private CustomerQueuingService	customerQueuingService;
	private DateService				dateService;
	private RepayQueueService		repayQueueService;
	private CustomerDatesDAO		customerDatesDAO;
	private EODProperties			eodProperties;

	public void doProcess(Date date) {
		
		eodProperties.init();
		
		// Save customer level AppDate, ValueDate and NextBussinessDate
		prepareCustomerDates();

		// dump the total customer Id's with allocated Date
		getCustomerQueuingService().loadCustIds(date);
		
		//update value and next business date
		getDateService().doUpdatebeforeEod(true);
		
		//load fin priority
		getRepayQueueService().loadFinanceRepayPriority();
		
		//Daily downloads
		//TODO: Need to use Data-Engine project 

	}

	/**
	 * Save the customer business dates values when EOD starts.
	 * 
	 * @param appDate
	 */
	private void prepareCustomerDates() {
		
		Date appDate = DateUtility.getAppDate();
		Date valueDate = DateUtility.getValueDate();
		Date nextBusinessDate = DateUtility.getNextBusinessdate();
		
		// save customer business dates when EOD starts
		getCustomerDatesDAO().saveCustomerDates(appDate, valueDate, nextBusinessDate);
	}

	private CustomerQueuingService getCustomerQueuingService() {
		return customerQueuingService;
	}

	public void setCustomerQueuingService(CustomerQueuingService customerQueuingService) {
		this.customerQueuingService = customerQueuingService;
	}

	private DateService getDateService() {
		return dateService;
	}

	public void setDateService(DateService dateService) {
		this.dateService = dateService;
	}

	public RepayQueueService getRepayQueueService() {
		return repayQueueService;
	}

	public void setRepayQueueService(RepayQueueService repayQueueService) {
		this.repayQueueService = repayQueueService;
	}

	public CustomerDatesDAO getCustomerDatesDAO() {
		return customerDatesDAO;
	}

	public void setCustomerDatesDAO(CustomerDatesDAO customerDatesDAO) {
		this.customerDatesDAO = customerDatesDAO;
	}

	public void setEodProperties(EODProperties eodProperties) {
		this.eodProperties = eodProperties;
	}
}
