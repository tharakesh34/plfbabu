package com.pennant.eod;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.core.DateService;
import com.pennant.app.core.RepayQueueService;
import com.pennant.app.core.SnapshotService;
import com.pennant.app.util.DateUtility;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennant.eod.util.EODProperties;

public class PostEodService {
	private static Logger				logger	= Logger.getLogger(PostEodService.class);

	private DateService					dateService;
	private RepayQueueService			repayQueueService;
	private SnapshotService				snapshotService;
	private CustomerQueuingDAO			customerQueuingDAO;
	private EODProperties				eodProperties;
	private PlatformTransactionManager	transactionManager;

	public void doProcess() throws Exception {
		logger.debug(" Entering ");

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setReadOnly(true);
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus txStatus = transactionManager.getTransaction(txDef);

		try {

			Date appDate = DateUtility.getAppDate();

			// Snapshot preparation
			snapshotService.doSnapshotPreparation(appDate);

			// Log the Customer queuing data and threads status
			customerQueuingDAO.logCustomerQueuing();

			//Update value dates check Holiday 
			dateService.doUpdateValueDate();

			dateService.doUpdateAftereod(true, true);

			//clear the data which is loaded in before  end of day
			repayQueueService.clearFinanceRepayPriority();

			eodProperties.destroy();
			transactionManager.commit(txStatus);

		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			logger.error("Exception :", e);
			throw e;
		}
		logger.debug(" Leaving ");
	}

	public void setDateService(DateService dateService) {
		this.dateService = dateService;
	}

	public void setRepayQueueService(RepayQueueService repayQueueService) {
		this.repayQueueService = repayQueueService;
	}

	public void setSnapshotService(SnapshotService snapshotService) {
		this.snapshotService = snapshotService;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	public void setEodProperties(EODProperties eodProperties) {
		this.eodProperties = eodProperties;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
