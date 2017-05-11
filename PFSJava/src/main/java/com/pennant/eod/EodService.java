package com.pennant.eod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.core.AccrualService;
import com.pennant.app.core.AutoDisbursementService;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.DateRollOverService;
import com.pennant.app.core.InstallmentDueService;
import com.pennant.app.core.LatePayInterestService;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.core.LatePayPenaltyService;
import com.pennant.app.core.LoadFinanceData;
import com.pennant.app.core.NPAService;
import com.pennant.app.core.RateReviewService;
import com.pennant.app.core.ReceiptPaymentService;

public class EodService {

	private static Logger				logger	= Logger.getLogger(EodService.class);

	private DataSource					dataSource;

	private LatePayMarkingService		latePayMarkingService;
	private LatePayPenaltyService		latePayPenaltyService;
	private LatePayInterestService		latePayInterestService;
	private NPAService					npaService;
	private DateRollOverService			dateRollOverService;
	private LoadFinanceData				loadFinanceData;
	private RateReviewService			rateReviewService;
	private AccrualService				accrualService;
	private AutoDisbursementService		autoDisbursementService;
	private ReceiptPaymentService		receiptPaymentService;
	private InstallmentDueService		installmentDueService;

	private PlatformTransactionManager	transactionManager;

	// Constants
	private static final String			SQL		= "SELECT * FROM CustomerQueuing WHERE ThreadId=? AND Progress IS NULL ";

	public EodService() {
		super();
	}

	/**
	 * @param threadId
	 * @throws Exception
	 * @throws SQLException
	 */
	public void startProcess(Date date, String threadId) throws Exception {

		logger.info("process Statred by the Thread :" + threadId + " with date" + date.toString());
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		long custId = 0;
		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setReadOnly(true);
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus txStatus = null;

		try {
			connection = DataSourceUtils.doGetConnection(dataSource);
			sqlStatement = connection.prepareStatement(SQL);
			sqlStatement.setString(1, threadId);
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				//BEGIN TRANSACTION
				txStatus = transactionManager.getTransaction(txDef);

				custId = resultSet.getLong("CustId");

				//process
				doProcess(connection, custId, date);

				//Update Status
				loadFinanceData.updateEnd(date, custId);

				//COMMIT THE TRANSACTION
				transactionManager.commit(txStatus);
			}

			resultSet.close();
			sqlStatement.close();
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			logger.error("Exception: ", e);
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}

	private void doProcess(Connection connection, long custId, Date date) throws Exception {

		/**************** Fetch and Set EOD Event ***********/
		CustEODEvent custEODEvent = new CustEODEvent();
		custEODEvent.setEodDate(date);
		custEODEvent.setEodValueDate(date);

		custEODEvent = loadFinanceData.prepareFinEODEvents(custEODEvent, custId);

		//late pay marking
		custEODEvent = latePayMarkingService.processLatePayMarking(custEODEvent);

		//DPD Bucketing
		custEODEvent = latePayMarkingService.processDPDBuketing(custEODEvent);

		//customer status update
		custEODEvent = latePayMarkingService.processCustomerStatus(custEODEvent);

		//late pay penalty
		custEODEvent = latePayPenaltyService.processLatePayPenalty(custEODEvent);

		//late pay interest
		custEODEvent = latePayInterestService.processLatePayInterest(custEODEvent);

		//NPA Service
		custEODEvent = npaService.processNPABuckets(custEODEvent);

		/**************** SOD ***********/

		//date rollover
		custEODEvent = dateRollOverService.process(custEODEvent);

		//Rate review
		custEODEvent = rateReviewService.processRateReview(custEODEvent);

		//Accrual
		custEODEvent = accrualService.processAccrual(custEODEvent);

		//Auto disbursements
		autoDisbursementService.processDisbursementPostings(custEODEvent);

		//installment 
		installmentDueService.processDueDatePostings(custEODEvent);

		//update customer EOD
		loadFinanceData.updateFinEODEvents(custEODEvent);

		//receipt postings
		receiptPaymentService.processrReceipts(custEODEvent);

		//customer Date update
		loadFinanceData.updateCustomerDate(custId, date);

		//clear data after the process
		custEODEvent.getFinEODEvents().clear();
		custEODEvent = null;

	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	public void setRateReviewService(RateReviewService rateReviewService) {
		this.rateReviewService = rateReviewService;
	}

	public void setDateRollOverService(DateRollOverService dateRollOverService) {
		this.dateRollOverService = dateRollOverService;
	}

	public void setInstallmentDueService(InstallmentDueService installmentDueService) {
		this.installmentDueService = installmentDueService;
	}

	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	public void setLatePayPenaltyService(LatePayPenaltyService latePayPenaltyService) {
		this.latePayPenaltyService = latePayPenaltyService;
	}

	public void setLatePayInterestService(LatePayInterestService latePayInterestService) {
		this.latePayInterestService = latePayInterestService;
	}

	public void setNpaService(NPAService npaService) {
		this.npaService = npaService;
	}

	public void setAutoDisbursementService(AutoDisbursementService autoDisbursementService) {
		this.autoDisbursementService = autoDisbursementService;
	}

	public void setReceiptPaymentService(ReceiptPaymentService receiptPaymentService) {
		this.receiptPaymentService = receiptPaymentService;
	}

	public LoadFinanceData getLoadFinanceData() {
		return loadFinanceData;
	}

	public void setLoadFinanceData(LoadFinanceData loadFinanceData) {
		this.loadFinanceData = loadFinanceData;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
