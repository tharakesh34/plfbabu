package com.pennant.eod;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualReversalService;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.AutoDisbursementService;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.DateRollOverService;
import com.pennant.app.core.InstallmentDueService;
import com.pennant.app.core.LatePayBucketService;
import com.pennant.app.core.LatePayDueCreationService;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.core.LoadFinanceData;
import com.pennant.app.core.NPAService;
import com.pennant.app.core.ProjectedAmortizationService;
import com.pennant.app.core.RateReviewService;
import com.pennant.app.core.ReceiptPaymentService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.limitservice.LimitRebuild;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pff.advancepayment.service.AdvancePaymentService;

public class EodService {
	private LatePayMarkingService latePayMarkingService;
	private LatePayBucketService latePayBuketService;
	private NPAService npaService;
	private DateRollOverService dateRollOverService;
	private LoadFinanceData loadFinanceData;
	private RateReviewService rateReviewService;
	private AccrualService accrualService;
	private AutoDisbursementService autoDisbursementService;
	private ReceiptPaymentService receiptPaymentService;
	private InstallmentDueService installmentDueService;
	private AdvancePaymentService advancePaymentService;
	private LimitRebuild limitRebuild;
	private ProjectedAmortizationService projectedAmortizationService;
	private LatePayDueCreationService latePayDueCreationService;
	private AccrualReversalService accrualReversalService;

	public EodService() {
		super();
	}

	public void doUpdate(CustEODEvent custEODEvent) throws Exception {
		Customer customer = custEODEvent.getCustomer();
		//update customer EOD
		loadFinanceData.updateFinEODEvents(custEODEvent);
		//receipt postings on SOD
		if (custEODEvent.isCheckPresentment()) {
			receiptPaymentService.processrReceipts(custEODEvent);
		}

		limitRebuild.processCustomerRebuild(custEODEvent.getCustomer().getCustID(), true);
		//customer Date update
		String newCustStatus = null;
		if (custEODEvent.isUpdCustomer()) {
			newCustStatus = customer.getCustSts();
		}

		loadFinanceData.updateCustomerDate(customer.getCustID(), custEODEvent.getEodValueDate(), newCustStatus);

	}

	public void doUpdate(CustEODEvent custEODEvent, boolean isLimitRebuild) throws Exception {
		Customer customer = custEODEvent.getCustomer();
		//update customer EOD
		loadFinanceData.updateFinEODEvents(custEODEvent);
		//receipt postings on SOD
		if (custEODEvent.isCheckPresentment()) {
			receiptPaymentService.processrReceipts(custEODEvent);
		}

		if (isLimitRebuild) {
			this.limitRebuild.processCustomerRebuild(custEODEvent.getCustomer().getCustID(), true);
		}

		//customer Date update
		String newCustStatus = null;
		if (custEODEvent.isUpdCustomer()) {
			newCustStatus = customer.getCustSts();
		}

		loadFinanceData.updateCustomerDate(customer.getCustID(), custEODEvent.getEodValueDate(), newCustStatus);
	}

	public void processCustomerRebuild(long custID, boolean rebuildOnStrChg) {
		limitRebuild.processCustomerRebuild(custID, rebuildOnStrChg);
	}

	public void doProcess(CustEODEvent custEODEvent) throws Exception {

		/**************** Fetch and Set EOD Event ***********/

		long custId = custEODEvent.getCustomer().getCustID();
		custEODEvent = loadFinanceData.prepareFinEODEvents(custEODEvent, custId);

		boolean skipLatePayMarking = false;

		if ("Y".equals(SysParamUtil.getValueAsString("EOD_SKIP_LATE_PAY_MARKING"))) {
			skipLatePayMarking = true;
		}

		if (!skipLatePayMarking) {
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

			if (custEODEvent.isExecuteNPAAndProvision()) {
				custEODEvent = npaService.processProvisions(custEODEvent);
			}

		}

		//LatePay Due creation Service
		custEODEvent = latePayDueCreationService.processLatePayAccrual(custEODEvent);

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

		//NPA Accounting
		if (custEODEvent.isExecuteNPAAndProvision()) {
			custEODEvent = npaService.processAccounting(custEODEvent);
		}

		// Penalty Accrual posted on EOD only
		custEODEvent = latePayDueCreationService.processLatePayAccrual(custEODEvent);

		// ACCRUALS calculation for Amortization
		String accrualCalForAMZ = SysParamUtil.getValueAsString(AmortizationConstants.MONTHENDACC_CALREQ);
		if (StringUtils.endsWithIgnoreCase(accrualCalForAMZ, "Y")) {

			if (custEODEvent.getEodDate().compareTo(DateUtility.getMonthEnd(custEODEvent.getEodDate())) == 0
					|| StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("EOM_ON_EOD"))) {

				// Calculate MonthEnd ACCRUALS
				custEODEvent = projectedAmortizationService.prepareMonthEndAccruals(custEODEvent);
			}
		}

		//Auto disbursements
		if (ImplementationConstants.ALLOW_ADDDBSF) {
			if (custEODEvent.isDisbExist()) {
				autoDisbursementService.processDisbursementPostings(custEODEvent);
			}
		}

		//Accrual reversals
		if (SysParamUtil.isAllowed(SMTParameterConstants.ACCRUAL_REVERSAL_REQ)) {
			accrualReversalService.processAccrual(custEODEvent);
		}

		//installment
		if (custEODEvent.isDueExist()) {
			installmentDueService.processDueDatePostings(custEODEvent);
			advancePaymentService.processAdvansePayments(custEODEvent);
		}

	}

	@Autowired
	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	@Autowired
	public void setRateReviewService(RateReviewService rateReviewService) {
		this.rateReviewService = rateReviewService;
	}

	@Autowired
	public void setDateRollOverService(DateRollOverService dateRollOverService) {
		this.dateRollOverService = dateRollOverService;
	}

	@Autowired
	public void setAdvancePaymentService(AdvancePaymentService advancePaymentService) {
		this.advancePaymentService = advancePaymentService;
	}

	@Autowired
	public void setInstallmentDueService(InstallmentDueService installmentDueService) {
		this.installmentDueService = installmentDueService;
	}

	@Autowired
	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	@Autowired
	public void setLatePayBuketService(LatePayBucketService latePayBuketService) {
		this.latePayBuketService = latePayBuketService;
	}

	@Autowired
	public void setNpaService(NPAService npaService) {
		this.npaService = npaService;
	}

	@Autowired
	public void setAutoDisbursementService(AutoDisbursementService autoDisbursementService) {
		this.autoDisbursementService = autoDisbursementService;
	}

	@Autowired
	public void setLoadFinanceData(LoadFinanceData loadFinanceData) {
		this.loadFinanceData = loadFinanceData;
	}

	public LoadFinanceData getLoadFinanceData() {
		return loadFinanceData;
	}

	@Autowired
	public void setReceiptPaymentService(ReceiptPaymentService receiptPaymentService) {
		this.receiptPaymentService = receiptPaymentService;
	}

	@Autowired
	public void setProjectedAmortizationService(ProjectedAmortizationService projectedAmortizationService) {
		this.projectedAmortizationService = projectedAmortizationService;
	}

	@Autowired
	public void setLimitRebuild(LimitRebuild limitRebuild) {
		this.limitRebuild = limitRebuild;
	}

	@Autowired
	public void setLatePayDueCreationService(LatePayDueCreationService latePayDueCreationService) {
		this.latePayDueCreationService = latePayDueCreationService;
	}

	@Autowired
	public void setAccrualReversalService(AccrualReversalService accrualReversalService) {
		this.accrualReversalService = accrualReversalService;
	}

}
