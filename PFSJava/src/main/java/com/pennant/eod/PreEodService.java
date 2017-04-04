package com.pennant.eod;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.core.DateService;
import com.pennant.app.core.RepayQueueService;
import com.pennant.app.util.DateUtility;
import com.pennant.eod.dao.CustomerDatesDAO;
import com.pennant.eod.util.EODProperties;

public class PreEodService {

	private static Logger				logger	= Logger.getLogger(PreEodService.class);
	private CustomerQueuingService		customerQueuingService;
	private DateService					dateService;
	private RepayQueueService			repayQueueService;
	private CustomerDatesDAO			customerDatesDAO;
	private EODProperties				eodProperties;
	private PlatformTransactionManager	transactionManager;

	public void doProcess(Date date) {

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setReadOnly(true);
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus txStatus = transactionManager.getTransaction(txDef);

		try {
			eodProperties.init();
			// Save customer level AppDate, ValueDate and NextBussinessDate
			prepareCustomerDates();
			// dump the total customer Id's with allocated Date
			customerQueuingService.loadCustIds(date);
			//update value and next business date
			dateService.doUpdatebeforeEod(true);
			//load fin priority
			repayQueueService.loadFinanceRepayPriority();
			//Daily downloads
			//TODO: Need to use Data-Engine project 

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			logger.error("Exception :", e);
			throw e;
		}

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
		customerDatesDAO.saveCustomerDates(appDate, valueDate, nextBusinessDate);
	}

	public void setCustomerQueuingService(CustomerQueuingService customerQueuingService) {
		this.customerQueuingService = customerQueuingService;
	}

	public void setDateService(DateService dateService) {
		this.dateService = dateService;
	}

	public void setRepayQueueService(RepayQueueService repayQueueService) {
		this.repayQueueService = repayQueueService;
	}

	public void setCustomerDatesDAO(CustomerDatesDAO customerDatesDAO) {
		this.customerDatesDAO = customerDatesDAO;
	}

	public void setEodProperties(EODProperties eodProperties) {
		this.eodProperties = eodProperties;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
