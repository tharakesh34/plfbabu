package com.pennant.eod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualReversalService;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.AutoDisbursementService;
import com.pennant.app.core.AutoKnockOffService;
import com.pennant.app.core.ChangeGraceEndService;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.DateRollOverService;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.InstallmentDueService;
import com.pennant.app.core.LatePayBucketService;
import com.pennant.app.core.LatePayDueCreationService;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.core.LoadFinanceData;
import com.pennant.app.core.ProjectedAmortizationService;
import com.pennant.app.core.RateReviewService;
import com.pennant.app.core.ReceiptPaymentService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.limitservice.LimitRebuild;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.autorefund.service.AutoRefundService;
import com.pennant.pff.core.loan.util.DPDStringCalculator;
import com.pennant.pff.extension.PresentmentExtension;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.service.AdvancePaymentService;
import com.pennanttech.pff.closure.service.impl.ClosureService;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.npa.service.AssetClassificationService;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class EodService {
	private static Logger logger = LogManager.getLogger(EodService.class);

	private LatePayMarkingService latePayMarkingService;
	private LatePayBucketService latePayBuketService;
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
	private ChangeGraceEndService changeGraceEndService;
	private AutoKnockOffService eodAutoKnockOffService;
	private OverdrafLoanService overdrafLoanService;
	private ManualAdviseService manualAdviseService;
	private AssetClassificationService assetClassificationService;
	private PresentmentDetailDAO presentmentDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ClosureService closureService;
	private AutoRefundService autoRefundService;

	public EodService() {
		super();
	}

	public void doUpdate(CustEODEvent custEODEvent) throws Exception {
		loadFinanceData.updateFinEODEvents(custEODEvent);

		if (custEODEvent.isCheckPresentment()) {
			createPresentmentReceipts(custEODEvent);
		}

		limitRebuild.processCustomerRebuild(custEODEvent.getCustomer().getCustID(), true);

		loadFinanceData.updateCustomerDate(custEODEvent);
	}

	private PresentmentDetail getPresentmentDetail(List<PresentmentDetail> pd, String finReference, Date schDate) {
		for (PresentmentDetail detail : pd) {
			if (detail.getFinReference().equals(finReference) && detail.getSchDate().compareTo(schDate) == 0) {
				return detail;
			}

		}
		return null;
	}

	private void createPresentmentReceipts(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		Date businessDate = custEODEvent.getEodValueDate();
		Customer customer = custEODEvent.getCustomer();

		List<PresentmentDetail> presentments = presentmentDetailDAO.getPresentmenToPost(customer, businessDate);

		for (FinEODEvent finEODEvent : finEODEvents) {
			FinanceMain fm = finEODEvent.getFinanceMain();
			String finReference = fm.getFinReference();
			List<FinExcessAmount> emiInAdvance = new ArrayList<>();

			if (finEODEvent.getIdxPresentment() < 0) {
				continue;
			}

			FinanceScheduleDetail schedule = finEODEvent.getFinanceScheduleDetails()
					.get(finEODEvent.getIdxPresentment());

			BigDecimal balanceAmt = BigDecimal.ZERO;

			List<FinExcessAmount> finExcessAmounts = finEODEvent.getFinExcessAmounts();
			for (FinExcessAmount fea : finExcessAmounts) {
				if (RepayConstants.EXAMOUNTTYPE_EMIINADV.equals(fea.getAmountType())
						&& DateUtil.compare(schedule.getSchDate(), fea.getValueDate()) > 0) {
					emiInAdvance.add(fea);
					balanceAmt = balanceAmt.add(fea.getBalanceAmt());
				}
			}

			PresentmentDetail pd = getPresentmentDetail(presentments, finReference, businessDate);

			if (pd == null && (CollectionUtils.isEmpty(emiInAdvance) || balanceAmt.compareTo(BigDecimal.ZERO) <= 0)) {
				continue;
			}

			if (pd != null) {
				pd.setExcessMovements(
						finExcessAmountDAO.getExcessMovementList(pd.getId(), RepayConstants.RECEIPTTYPE_PRESENTMENT));
			}

			ReceiptDTO receiptDTO = new ReceiptDTO();
			receiptDTO.setRequestSource(RequestSource.EOD);
			receiptDTO.setCreatePrmntReceipt(PresentmentExtension.DUE_DATE_RECEIPT_CREATION);

			receiptDTO.setFinType(finEODEvent.getFinType());
			receiptDTO.setPresentmentDetail(pd);
			receiptDTO.setBussinessDate(businessDate);
			receiptDTO.setCustomer(customer);
			receiptDTO.setFinanceMain(fm);
			receiptDTO.setEmiInAdvance(emiInAdvance);
			receiptDTO.setProfitDetail(finEODEvent.getFinProfitDetail());
			receiptDTO.setSchedules(finEODEvent.getFinanceScheduleDetails());
			receiptDTO.setValuedate(businessDate);
			receiptDTO.setPostDate(businessDate);

			receiptPaymentService.processReceipts(receiptDTO, finEODEvent.getIdxPresentment());
		}
	}

	public void doUpdate(CustEODEvent custEODEvent, boolean isLimitRebuild) throws Exception {
		// update customer EOD

		autoRefundService.updateRefunds(custEODEvent);

		loadFinanceData.updateFinEODEvents(custEODEvent);
		// receipt postings on SOD
		if (custEODEvent.isCheckPresentment()) {
			createPresentmentReceipts(custEODEvent);
		}

		if (isLimitRebuild) {
			// FIXME Customer CorBank-ID
			this.limitRebuild.processCustomerRebuild(custEODEvent.getCustomer().getCustID(), true);
		}

		loadFinanceData.updateCustomerDate(custEODEvent);

		EventProperties eventProperties = custEODEvent.getEventProperties();

		if (ImplementationConstants.ALLOW_AUTO_KNOCK_OFF && !ImplementationConstants.AUTO_KNOCK_OFF_ON_DUE_DATE) {
			processAutoKnockOff(custEODEvent, eventProperties);
		}
	}

	private void processAutoKnockOff(CustEODEvent custEODEvent, EventProperties eventProperties) {
		Date appDate = DateUtil.addDays(eventProperties.getAppDate(), 1);
		long custId = custEODEvent.getCustomer().getCustID();

		logger.info("Auto-Knock-Off process started for the Customer ID >> {} ", custId);
		eodAutoKnockOffService.processKnockOff(custId, appDate);
		logger.info("Auto-Knock-Off process completed for the Customer ID >> {} ", custId);
	}

	public void processCustomerRebuild(long custID, boolean rebuildOnStrChg) {
		// FIXME Customer CorBank-ID
		limitRebuild.processCustomerRebuild(custID, rebuildOnStrChg);
	}

	public void doProcess(CustEODEvent custEODEvent) throws Exception {
		EventProperties eventProperties = custEODEvent.getEventProperties();

		if (ImplementationConstants.ALLOW_AUTO_KNOCK_OFF && ImplementationConstants.AUTO_KNOCK_OFF_ON_DUE_DATE) {
			processAutoKnockOff(custEODEvent, eventProperties);
		}

		if (!eventProperties.isSkipLatePay()) {
			// late pay marking

			if (custEODEvent.isPastDueExist()) {
				// overdue calculated on EOD
				// LPP calculated on the SOD
				// LPI calculated on the SOD
				latePayMarkingService.processLatePayMarking(custEODEvent);
			}

			// DPD Bucketing
			latePayBuketService.processDPDBuketing(custEODEvent);

			// customer status update
			latePayMarkingService.processCustomerStatus(custEODEvent);
		} else {
			logger.info(
					"Late Pay Marking, DPD Buketing and Processing of provisions skipped due to {} SMT parameter value is true",
					SMTParameterConstants.EOD_SKIP_LATE_PAY_MARKING);
		}

		// LatePay Due creation Service
		latePayDueCreationService.processLatePayAccrual(custEODEvent);

		autoRefundService.executeRefund(custEODEvent);

		/**************** SOD ***********/
		// moving customer date to sod
		Date eodValueDate = eventProperties.getBusinessDate();
		custEODEvent.setEodValueDate(eodValueDate);
		custEODEvent.getCustomer().setCustAppDate(eodValueDate);

		// Date rollover
		if (custEODEvent.isDateRollover()) {
			dateRollOverService.process(custEODEvent);
		}

		// EarlySetlement
		closureService.processTerminationClosure(custEODEvent);

		// Rate review
		if (custEODEvent.isRateRvwExist()) {
			logger.info("Processing Rate Review started...");
			rateReviewService.processRateReview(custEODEvent);
			logger.info("Processing Rate Review completed.");
		}

		// if month end then only it should run
		if (custEODEvent.getEodDate().compareTo(DateUtility.getMonthEnd(custEODEvent.getEodDate())) == 0
				|| eventProperties.isEomOnEOD()) {
			// Calculate MonthEnd LPI
			logger.info("Processing Late Pay interest started...");
			custEODEvent = latePayMarkingService.processLPIAccrual(custEODEvent);
			logger.info("Processing Late Pay interest Completed...");
		}

		// Accrual posted on EOD only
		logger.info("Processing Accruals started...");
		accrualService.processAccrual(custEODEvent);
		logger.info("Preparing Accruals completed.");

		// Penalty Accrual posted on EOD only
		latePayDueCreationService.processLatePayAccrual(custEODEvent);

		// ACCRUALS calculation for Amortization
		if (eventProperties.isMonthEndAccCallReq()
				&& (custEODEvent.getEodDate().compareTo(eventProperties.getMonthEndDate()) == 0
						|| eventProperties.isEomOnEOD())) {
			// Calculate MonthEnd ACCRUALS
			logger.info("Processing MonthEndAccruals started...");
			projectedAmortizationService.prepareMonthEndAccruals(custEODEvent);
			logger.info("Processing MonthEndAccruals completed.");
		}

		if (custEODEvent.getEodDate().compareTo(DateUtility.getMonthEnd(custEODEvent.getEodDate())) == 0
				|| eventProperties.isEomOnEOD()) {
			// Calculate MonthEnd Penalty
			logger.info("Processing Late Pay Accruals started...");
			latePayMarkingService.processLatePayAccrual(custEODEvent);
			logger.info("Processing Late Pay Accruals completed...");
		}

		// Auto disbursements
		if (ImplementationConstants.ALLOW_ADDDBSF && custEODEvent.isDisbExist()) {
			autoDisbursementService.processDisbursementPostings(custEODEvent);
		}

		// Accrual reversals
		if (eventProperties.isAccrualReversalReq()) {
			logger.info("Accruals Process started...");
			accrualReversalService.processAccrual(custEODEvent);
			logger.info("Accruals Process completed.");
		}

		logger.info("Charge Advice Creation for Overdraft Loans started...");
		overdrafLoanService.createPenalties(custEODEvent);
		logger.info("Charge Advice Creation for Overdraft Loans completed...");

		// installment
		if (custEODEvent.isDueExist()) {
			logger.info("Instalment due date posting process started...");
			installmentDueService.processDueDatePostings(custEODEvent);
			logger.info("Instalment due date posting process completed.");

			logger.info("Advance payment process started...");
			advancePaymentService.processAdvansePayments(custEODEvent);
			logger.info("Advance payment process completed.");
		}

		logger.info("Auto grace extension process started...");
		changeGraceEndService.processChangeGraceEnd(custEODEvent);
		logger.info("Auto grace extension process completed.");

		logger.info("Future dated manual advice process started...");
		manualAdviseService.cancelFutureDatedAdvises(custEODEvent);
		manualAdviseService.prepareManualAdvisePostings(custEODEvent);
		logger.info("Future dated manual advice process completed.");

		logger.info("Updating the OverDraft loans closing status process started...");
		overdrafLoanService.closeByMaturity(custEODEvent);
		logger.info("Updating the OverDraft loans closing status process completed.");

		if (ImplementationConstants.ALLOW_NPA) {
			assetClassificationService.process(custEODEvent);
		}

		int dpdStringCal = eventProperties.getDpdStringCal();
		if ((custEODEvent.getEodDate().compareTo(DateUtil.getMonthEnd(custEODEvent.getEodDate())) == 0
				|| eventProperties.isEomOnEOD()) && dpdStringCal == 0) {
			DPDStringCalculator.process(custEODEvent, true);
		}

		if (dpdStringCal == 1) {
			DPDStringCalculator.process(custEODEvent, false);
		}
	}

	public void processAutoRefund(CustEODEvent custEODEvent) {
		logger.info("Process the auto Refund for the Customer who is no active loans");
		autoRefundService.executeRefund(custEODEvent);

		autoRefundService.updateRefunds(custEODEvent);
	}

	public void prepareFinEODEvents(CustEODEvent custEODEvent) {
		loadFinanceData.prepareFinEODEvents(custEODEvent);
	}

	public void loadAutoRefund(CustEODEvent custEODEvent) {
		autoRefundService.loadAutoRefund(custEODEvent);
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

	@Autowired
	public void setChangeGraceEndService(ChangeGraceEndService changeGraceEndService) {
		this.changeGraceEndService = changeGraceEndService;
	}

	@Autowired
	public void setEodAutoKnockOffService(AutoKnockOffService eodAutoKnockOffService) {
		this.eodAutoKnockOffService = eodAutoKnockOffService;
	}

	@Autowired
	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		this.overdrafLoanService = overdrafLoanService;
	}

	@Autowired
	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	@Autowired
	public void setAssetClassificationService(AssetClassificationService assetClassificationService) {
		this.assetClassificationService = assetClassificationService;
	}

	@Autowired
	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setClosureService(ClosureService closureService) {
		this.closureService = closureService;
	}

	@Autowired
	public void setAutoRefundService(AutoRefundService autoRefundService) {
		this.autoRefundService = autoRefundService;
	}

}
