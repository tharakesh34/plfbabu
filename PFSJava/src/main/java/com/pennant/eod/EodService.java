package com.pennant.eod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.DateRollOverService;
import com.pennant.app.core.InstallmentDueService;
import com.pennant.app.core.RateReviewService;
import com.pennant.app.core.RepayQueueService;
import com.pennant.app.core.ServiceUtil;
import com.pennant.app.core.StatusMovementService;
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

	private ServiceUtil					serviceUtil;
	private AccrualService				accrualService;
	private RateReviewService			rateReviewService;
	private StatusMovementService		statusMovementService;
	private RepayQueueService			repayQueueService;
	private DateRollOverService			dateRollOverService;
	private InstallmentDueService		installmentDueService;

	// Constants
	private static final String			SQL		= "select * from CustomerQueuing where ThreadId=? And ( Progress is null or Status= ?)";

	public EodService() {
		super();
	}

	/**
	 * @param threadId
	 * @throws SQLException
	 */
	public void startProcess(Date date, String threadId) throws SQLException {
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
		} catch (Exception e) {
			logger.warn("Exception: ", e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
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
		//prepare customer queue
		repayQueueService.saveQueue(connection, custId, date);

		//process payments from queue
		serviceUtil.processQueue(connection, custId, date);

		//Accrual
		accrualService.processAccrual(connection, custId, date);

		//Status movements
		statusMovementService.processMovements(connection, custId, date);

		//Rate review
		//FIXME Rate review process should checked after the completion new method in schedule calculator
		rateReviewService.processRateReview(connection, custId, date);

		//installment 
		installmentDueService.processDueDatePostings(connection, custId, date);
		//Date roll over
		dateRollOverService.process(connection, custId, date);

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

	public void setServiceUtil(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
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

	public void setStatusMovementService(StatusMovementService statusMovementService) {
		this.statusMovementService = statusMovementService;
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
}
