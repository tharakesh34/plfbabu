package com.pennant.eod;

import java.sql.Connection;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import com.pennant.app.core.AccrualService;
import com.pennant.app.core.AutoDisbursementService;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.DateRollOverService;
import com.pennant.app.core.InstallmentDueService;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.core.LoadFinanceData;
import com.pennant.app.core.NPAService;
import com.pennant.app.core.RateReviewService;
import com.pennant.app.core.ReceiptPaymentService;

public class EodService {

	@SuppressWarnings("unused")
	private DataSource					dataSource;

	private LatePayMarkingService		latePayMarkingService;
	private NPAService					npaService;
	private DateRollOverService			dateRollOverService;
	private LoadFinanceData				loadFinanceData;
	private RateReviewService			rateReviewService;
	private AccrualService				accrualService;
	private AutoDisbursementService		autoDisbursementService;
	private ReceiptPaymentService		receiptPaymentService;
	private InstallmentDueService		installmentDueService;

	private PlatformTransactionManager	transactionManager;


	public EodService() {
		super();
	}

	public void doProcess(Connection connection, long custId, Date date) throws Exception {

		/**************** Fetch and Set EOD Event ***********/
		CustEODEvent custEODEvent = new CustEODEvent();
		custEODEvent.setEodDate(date);
		custEODEvent.setEodValueDate(date);

		custEODEvent = loadFinanceData.prepareFinEODEvents(custEODEvent, custId);

		//late pay marking
		if (custEODEvent.isPastDueExist()) {
			custEODEvent = latePayMarkingService.processLatePayMarking(custEODEvent);
		}

		//DPD Bucketing
		custEODEvent = latePayMarkingService.processDPDBuketing(custEODEvent);

		//customer status update
		custEODEvent = latePayMarkingService.processCustomerStatus(custEODEvent);

		//NPA Service
		custEODEvent = npaService.processNPABuckets(custEODEvent);

		/**************** SOD ***********/
		//Date rollover
		if (custEODEvent.isDateRollover()) {
			custEODEvent = dateRollOverService.process(custEODEvent);
		}

		//Rate review
		if (custEODEvent.isRateRvwExist()) {
			custEODEvent = rateReviewService.processRateReview(custEODEvent);
		}

		//Accrual
		custEODEvent = accrualService.processAccrual(custEODEvent);

		//Auto disbursements
		if (custEODEvent.isDisbExist()) {
			autoDisbursementService.processDisbursementPostings(custEODEvent);
		}

		//installment
		if (custEODEvent.isDueExist()) {
			installmentDueService.processDueDatePostings(custEODEvent);
		}

		//update customer EOD
		loadFinanceData.updateFinEODEvents(custEODEvent);

		//receipt postings
		if (custEODEvent.isCheckPresentment()) {
			receiptPaymentService.processrReceipts(custEODEvent);
		}

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

	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
