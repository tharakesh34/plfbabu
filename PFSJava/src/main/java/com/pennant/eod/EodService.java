package com.pennant.eod;

import java.util.Date;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.AutoDisbursementService;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.DateRollOverService;
import com.pennant.app.core.InstallmentDueService;
import com.pennant.app.core.LatePayBucketService;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.core.LoadFinanceData;
import com.pennant.app.core.NPAService;
import com.pennant.app.core.ProjectedAmortizationService;
import com.pennant.app.core.RateReviewService;
import com.pennant.app.core.ReceiptPaymentService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.limitservice.LimitRebuild;

public class EodService {

	@SuppressWarnings("unused")
	private DataSource dataSource;

	private LatePayMarkingService latePayMarkingService;
	@Autowired
	private LatePayBucketService latePayBuketService;
	private NPAService npaService;
	private DateRollOverService dateRollOverService;
	private LoadFinanceData loadFinanceData;
	private RateReviewService rateReviewService;
	private AccrualService accrualService;
	private AutoDisbursementService autoDisbursementService;
	private ReceiptPaymentService receiptPaymentService;
	private InstallmentDueService installmentDueService;
	@Autowired
	private LimitRebuild limitRebuild;

	private PlatformTransactionManager transactionManager;
	private ProjectedAmortizationService projectedAmortizationService;

	public EodService() {
		super();
	}

	public void doUpdate(CustEODEvent custEODEvent) throws Exception {
		Customer customer = custEODEvent.getCustomer();
		//update customer EOD
		getLoadFinanceData().updateFinEODEvents(custEODEvent);
		//receipt postings on SOD
		if (custEODEvent.isCheckPresentment()) {
			getReceiptPaymentService().processrReceipts(custEODEvent);
		}

		limitRebuild.processCustomerRebuild(custEODEvent.getCustomer().getCustID(), true);
		//customer Date update
		String newCustStatus = null;
		if (custEODEvent.isUpdCustomer()) {
			newCustStatus = customer.getCustSts();
		}

		getLoadFinanceData().updateCustomerDate(customer.getCustID(), custEODEvent.getEodValueDate(), newCustStatus);

	}

	public void doUpdate(CustEODEvent custEODEvent, boolean isLimitRebuild) throws Exception {
		Customer customer = custEODEvent.getCustomer();
		//update customer EOD
		getLoadFinanceData().updateFinEODEvents(custEODEvent);
		//receipt postings on SOD
		if (custEODEvent.isCheckPresentment()) {
			getReceiptPaymentService().processrReceipts(custEODEvent);
		}

		if (isLimitRebuild) {
			this.limitRebuild.processCustomerRebuild(custEODEvent.getCustomer().getCustID(), true);
		}

		//customer Date update
		String newCustStatus = null;
		if (custEODEvent.isUpdCustomer()) {
			newCustStatus = customer.getCustSts();
		}

		getLoadFinanceData().updateCustomerDate(customer.getCustID(), custEODEvent.getEodValueDate(), newCustStatus);
	}

	public void processCustomerRebuild(long custID, boolean rebuildOnStrChg) {
		limitRebuild.processCustomerRebuild(custID, rebuildOnStrChg);
	}

	public void doProcess(CustEODEvent custEODEvent) throws Exception {

		/**************** Fetch and Set EOD Event ***********/

		long custId = custEODEvent.getCustomer().getCustID();
		custEODEvent = loadFinanceData.prepareFinEODEvents(custEODEvent, custId);

		//late pay marking
		if (custEODEvent.isPastDueExist()) {
			//overdue calculated on EOD
			//LPP calculated on the SOD
			//LPI calculated on the SOD
			custEODEvent = latePayMarkingService.processLatePayMarking(custEODEvent);
		}

		//DPD Bucketing
		custEODEvent = latePayBuketService.processDPDBuketing(custEODEvent);

		//customer status update
		custEODEvent = latePayMarkingService.processCustomerStatus(custEODEvent);

		//NPA Service
		custEODEvent = npaService.processNPABuckets(custEODEvent);

		/**************** SOD ***********/
		//moving customer date to sod
		Date eodValueDate = DateUtility.addDays(custEODEvent.getEodDate(), 1);
		custEODEvent.setEodValueDate(eodValueDate);
		custEODEvent.getCustomer().setCustAppDate(eodValueDate);

		//Date rollover
		if (custEODEvent.isDateRollover()) {
			custEODEvent = dateRollOverService.process(custEODEvent);
		}

		//Rate review
		if (custEODEvent.isRateRvwExist()) {
			custEODEvent = rateReviewService.processRateReview(custEODEvent);
		}

		//Accrual posted on EOD only
		custEODEvent = accrualService.processAccrual(custEODEvent);

		// MonthEnd ACCRUALS, Income and Expense Amortization
		//custEODEvent = projectedAmortizationService.processAmortization(custEODEvent);

		//Auto disbursements
		if (ImplementationConstants.ALLOW_ADDDBSF) {
			if (custEODEvent.isDisbExist()) {
				autoDisbursementService.processDisbursementPostings(custEODEvent);
			}
		}

		//installment
		if (custEODEvent.isDueExist()) {
			installmentDueService.processDueDatePostings(custEODEvent);
		}

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

	public ReceiptPaymentService getReceiptPaymentService() {
		return receiptPaymentService;
	}

	public void setReceiptPaymentService(ReceiptPaymentService receiptPaymentService) {
		this.receiptPaymentService = receiptPaymentService;
	}

	public void setProjectedAmortizationService(ProjectedAmortizationService projectedAmortizationService) {
		this.projectedAmortizationService = projectedAmortizationService;
	}
}
