package com.pennant.eod;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.pennant.app.core.DateService;
import com.pennant.app.core.RepayQueueService;
import com.pennant.app.core.SnapshotService;
import com.pennant.app.util.DateUtility;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennant.eod.util.EODProperties;

public class PostEodService {
	private static Logger				logger	= Logger.getLogger(PostEodService.class);

	private DataSource					dataSource;
	private DateService					dateService;
	private RepayQueueService			repayQueueService;
	private SnapshotService				snapshotService;
	private ThirdPartyPostingService	thirdPartyPostingService;
	private ArchivalService				archivalService;
	private CustomerQueuingDAO			customerQueuingDAO;
	private EODProperties				eodProperties;

	public void doProcess() throws Exception {
		logger.debug(" Entering ");

		Date appDate = DateUtility.getAppDate();

		// Snapshot preparation
		 getSnapshotService().doSnapshotPreparation(appDate);

		// Third party Postings on monthly basis
		//getThirdPartyPostingService().processThirdPartyPostings(dateAppDate);

		// Document Archival
		//getArchivalService().processDocumentArchive(dateAppDate);

		// Log the Customer queuing data and threads status
		getCustomerQueuingDAO().logCustomerQueuing();

		//Update value dates check Holiday 
		getDateService().doUpdateValueDate();

		getDateService().doUpdateAftereod(true);

		//clear the data which is loaded in before  end of day
		getRepayQueueService().clearFinanceRepayPriority();
		
		eodProperties.destroy();

		logger.debug(" Leaving ");
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

	public SnapshotService getSnapshotService() {
		return snapshotService;
	}

	public void setSnapshotService(SnapshotService snapshotService) {
		this.snapshotService = snapshotService;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ThirdPartyPostingService getThirdPartyPostingService() {
		return thirdPartyPostingService;
	}

	public void setThirdPartyPostingService(ThirdPartyPostingService thirdPartyPostingService) {
		this.thirdPartyPostingService = thirdPartyPostingService;
	}

	public ArchivalService getArchivalService() {
		return archivalService;
	}

	public void setArchivalService(ArchivalService archivalService) {
		this.archivalService = archivalService;
	}

	public CustomerQueuingDAO getCustomerQueuingDAO() {
		return customerQueuingDAO;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	public EODProperties getEodProperties() {
		return eodProperties;
	}

	public void setEodProperties(EODProperties eodProperties) {
		this.eodProperties = eodProperties;
	}
}
