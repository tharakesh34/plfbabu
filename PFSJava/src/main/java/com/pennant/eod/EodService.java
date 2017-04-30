package com.pennant.eod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.AutoDisbursementService;
import com.pennant.app.core.DateRollOverService;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.InstallmentDueService;
import com.pennant.app.core.LatePayInterestService;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.core.LatePayPenaltyService;
import com.pennant.app.core.LoadFinanceData;
import com.pennant.app.core.NPAService;
import com.pennant.app.core.RateReviewService;
import com.pennant.app.core.ReceiptPaymentService;
import com.pennant.app.core.RepayQueueService;
import com.pennant.app.util.BusinessCalendar;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerDatesDAO;

public class EodService {

	private static Logger				logger	= Logger.getLogger(EodService.class);

	private DataSource					dataSource;
	private PlatformTransactionManager	transactionManager;
	private CustomerDatesDAO			customerDatesDAO;
	private CustomerQueuingService		customerQueuingService;

	private RepayQueueService			repayQueueService;
	private LatePayMarkingService		latePayMarkingService;
	private LatePayPenaltyService		latePayPenaltyService;
	private LatePayInterestService		latePayInterestService;
	private NPAService					npaService;
	private DateRollOverService			dateRollOverService;
	private RateReviewService			rateReviewService;
	private AccrualService				accrualService;
	private AutoDisbursementService		autoDisbursementService;
	private ReceiptPaymentService		receiptPaymentService;

	private InstallmentDueService		installmentDueService;
	private LoadFinanceData				loadFinanceData;




	// Constants
	private static final String			SQL		= "select * from CustomerQueuing where ThreadId=? And ( Progress is null or Status= ?)";

	public EodService() {
		super();
	}

	/**
	 * @param threadId
	 * @throws SQLException
	 */
	public void startProcess(Date date, String threadId) {
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		long custId = 0;
		try {
			connection = DataSourceUtils.doGetConnection(dataSource);
			sqlStatement = connection.prepareStatement(SQL);
			sqlStatement.setString(1, threadId);
			sqlStatement.setString(2, EodConstants.STATUS_FAILED);
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				try {
					custId = resultSet.getLong("CustId");
					//Update start
					customerQueuingService.updateStart(date, custId);
					//process
					processCustomerFinances(date, custId);
					//Update Status
					customerQueuingService.updateEnd(date, custId);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					String errorMsg = "";
					StringBuilder builder = new StringBuilder(StringUtils.trimToEmpty(e.getMessage()));
					StackTraceElement[] stackTrace = e.getStackTrace();
					for (int i = 0; i < stackTrace.length; i++) {
						builder.append(stackTrace[i]);
					}
					errorMsg = builder.toString();
					if (errorMsg.length() > 2000) {
						errorMsg = errorMsg.substring(0, 2000);
					}

					customerQueuingService.updateSucessFail(date, custId, errorMsg);
				}
			}

			resultSet.close();
			sqlStatement.close();
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}

	/**
	 * Customer based core process and transaction is applied for the process
	 * 
	 * @param date2
	 * 
	 * @param custId
	 * @param connection
	 * @throws Exception
	 */
	public void processCustomerFinances(Date date, long custId) throws Exception {
		logger.debug(" Entering ");
		//transaction create for customer based
		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setReadOnly(true);
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus txStatus = transactionManager.getTransaction(txDef);
		Connection connection = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			doProcess(connection, custId, date);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			logger.error("Exception :", e);
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}

	private void doProcess(Connection connection, long custId, Date date) throws Exception {

		List<FinEODEvent> custEODEvents = loadFinanceData.prepareFinEODEvents(custId, date);
		
		//prepare customer queue
		repayQueueService.prepareRepayQueue(connection, custId, date);

		//late pay marking
		latePayMarkingService.processLatePayMarking(connection, custId, date);

		//DPD Bucketing
		latePayMarkingService.processDPDBuketing(connection, custId, date);

		//customer status update
		latePayMarkingService.processCustomerStatus(custId, date, null, null);

		//late pay penalty
		latePayPenaltyService.processLatePayPenalty(connection, custId, date);

		//late pay interest
		latePayInterestService.processLatePayInterest(connection, custId, date);

		//NPA Service
		npaService.processNPABuckets(connection, custId, date);

		//_____________________________________________________________________________________________________________
		//Date roll over
		//_____________________________________________________________________________________________________________

		custEODEvents = dateRollOverService.process(custEODEvents);

		//Rate review
		custEODEvents = rateReviewService.processRateReview(custEODEvents);

		//Accrual
		custEODEvents = accrualService.processAccrual(custEODEvents);

		//Auto disbursements
		autoDisbursementService.processDisbursementPostings(connection, custId, date);

		//receipt postings
		receiptPaymentService.processrReceipts(connection, custId, date, custEODEvents);

		//installment 
		installmentDueService.processDueDatePostings(connection, custId, date);

		//Date and holiday check
		Date nextDate = DateUtility.addDays(date, 1);
		String localCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
		Calendar nextBusDate = BusinessCalendar.getWorkingBussinessDate(localCcy, HolidayHandlerTypes.MOVE_NEXT, date);
		if (DateUtility.matches(nextDate, nextBusDate.getTime())) {
			//update customer business Dates
			Date tempNextBussDate = BusinessCalendar.getWorkingBussinessDate(localCcy, HolidayHandlerTypes.MOVE_NEXT,
					nextBusDate.getTime()).getTime();
			customerDatesDAO.updateCustomerDates(custId, nextDate, nextDate, tempNextBussDate);
		} else {
			doProcess(connection, custId, nextDate);
		}

	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setCustomerQueuingService(CustomerQueuingService customerQueuingService) {
		this.customerQueuingService = customerQueuingService;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	public void setRateReviewService(RateReviewService rateReviewService) {
		this.rateReviewService = rateReviewService;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setRepayQueueService(RepayQueueService repayQueueService) {
		this.repayQueueService = repayQueueService;
	}

	public void setCustomerDatesDAO(CustomerDatesDAO customerDatesDAO) {
		this.customerDatesDAO = customerDatesDAO;
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
	
	public void setLoadFinanceData(LoadFinanceData loadFinanceData) {
		this.loadFinanceData = loadFinanceData;
	}
}
