package com.pennant.eod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.HolidayHandlerTypes;
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
import com.pennant.app.util.BusinessCalendar;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerDatesDAO;
import com.pennant.eod.dao.CustomerQueuingDAO;

public class EodService {

	private static Logger			logger	= Logger.getLogger(EodService.class);

	private DataSource				dataSource;
	private CustomerDAO				customerDAO;
	private CustomerDatesDAO		customerDatesDAO;
	private CustomerQueuingDAO		customerQueuingDAO;

	private LatePayMarkingService	latePayMarkingService;
	private LatePayPenaltyService	latePayPenaltyService;
	private LatePayInterestService	latePayInterestService;
	private NPAService				npaService;
	private DateRollOverService		dateRollOverService;
	private LoadFinanceData			loadFinanceData;
	private RateReviewService		rateReviewService;
	private AccrualService			accrualService;
	private AutoDisbursementService	autoDisbursementService;
	private ReceiptPaymentService	receiptPaymentService;

	private InstallmentDueService	installmentDueService;

	// Constants
	private static final String		SQL		= "SELECT * FROM CustomerQueuing WHERE ThreadId=? AND Progress IS NULL ";

	public EodService() {
		super();
	}

	/**
	 * @param threadId
	 * @throws Exception
	 * @throws SQLException
	 */
	public void startProcess(Date date, String threadId) throws Exception {

		System.out.println("process Statred by the Thread :" + threadId + " with data" + date.toString());
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		long custId = 0;
		try {
			connection = DataSourceUtils.doGetConnection(dataSource);
			sqlStatement = connection.prepareStatement(SQL);
			sqlStatement.setString(1, threadId);
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				custId = resultSet.getLong("CustId");
				//process
				doProcess(connection, custId, date);
				//Update Status
				updateEnd(date, custId);
			}

			resultSet.close();
			sqlStatement.close();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}

	private void doProcess(Connection connection, long custId, Date date) throws Exception {

		//_____________________________________________________________________________________________________________
		//Fetch and Set EOD Event
		//_____________________________________________________________________________________________________________

		CustEODEvent custEODEvent = new CustEODEvent();
		custEODEvent.setCustomer(getCustomerDAO().getCustomerEOD(custId));
		custEODEvent.setEodDate(date);
		custEODEvent.setEodValueDate(date);

		custEODEvent = loadFinanceData.prepareFinEODEvents(custEODEvent);

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

		//_____________________________________________________________________________________________________________
		//Date roll over
		//_____________________________________________________________________________________________________________
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

	public void updateEnd(Date date, long custId) {
		logger.debug("Entering");

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custId);
		customerQueuing.setEodDate(date);
		customerQueuing.setProgress(EodConstants.PROGRESS_COMPLETED);
		customerQueuingDAO.updateProgress(customerQueuing);

		logger.debug("Leaving");
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

	public LoadFinanceData getLoadFinanceData() {
		return loadFinanceData;
	}

	public void setLoadFinanceData(LoadFinanceData loadFinanceData) {
		this.loadFinanceData = loadFinanceData;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}
}
