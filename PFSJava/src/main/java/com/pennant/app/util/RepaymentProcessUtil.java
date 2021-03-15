package com.pennant.app.util;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentDealDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinStageAccountingLogDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.insurancedetails.FinInsurancesDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueueHeader;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.model.applicationmaster.AssignmentDealExcludedFee;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.core.TableType;

public class RepaymentProcessUtil {
	private static final Logger logger = LogManager.getLogger(RepaymentProcessUtil.class);

	private RepaymentPostingsUtil repayPostingUtil;
	private FinODDetailsDAO finODDetailsDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinLogEntryDetailDAO finLogEntryDetailDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinFeeScheduleDetailDAO finFeeScheduleDetailDAO;
	private FinInsurancesDAO finInsurancesDAO;
	private LimitManagement limitManagement;
	private ReceiptAllocationDetailDAO allocationDetailDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceMainDAO financeMainDAO;
	private PostingsDAO postingsDAO;
	private FinStageAccountingLogDAO finStageAccountingLogDAO;
	private ReceiptCalculator receiptCalculator;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private FeeTypeDAO feeTypeDAO;
	private AssignmentDAO assignmentDAO;
	private AssignmentDealDAO assignmentDealDAO;

	public RepaymentProcessUtil() {
		super();
	}

	/**
	 * Method for Calculation of Schedule payment based on Allocated Details from Receipts
	 * 
	 * @param scheduleData
	 * 
	 * @param receiptData
	 * @param scheduleData
	 * @return
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public void calcualteAndPayReceipt(FinanceMain fm, Customer customer, List<FinanceScheduleDetail> scheduleDetails,
			List<FinFeeDetail> finFeeDetailList, FinanceProfitDetail profitDetail, FinReceiptHeader rch,
			String repayHierarchy, Date valuedate, Date postDate)
			throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug(Literal.ENTERING);

		String finrefer = fm.getFinReference();
		finReceiptHeaderDAO.generatedReceiptID(rch);
		// Prepare schedule data for log
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData fsd = new FinScheduleData();
		fsd.setFinanceScheduleDetails(scheduleDetails);

		List<FinanceScheduleDetail> schdDtls = new ArrayList<>();
		for (FinanceScheduleDetail schd : scheduleDetails) {
			schdDtls.add(schd.copyEntity());
		}

		fsd.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finrefer, "", false));
		fsd.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finrefer, "", false));
		FinanceType finType = FinanceConfigCache.getCacheFinanceType(StringUtils.trimToEmpty(fm.getFinType()));
		fsd.setFinanceType(finType);
		fsd.setFinPftDeatil(profitDetail);
		fsd.setFinanceMain(fm);
		List<FinReceiptDetail> rcdList = sortReceiptDetails(rch.getReceiptDetails());
		FinReceiptData finReceiptData = new FinReceiptData();
		// TDS Calculation, if Applicable
		financeDetail.setFinScheduleData(fsd);
		/*
		 * final List<XcessPayables> xcsPaybles = new ArrayList<>(); xcsPaybles.addAll(rch.getXcessPayables());
		 */

		fsd.getFinanceMain().setRecordType("");
		fsd.getFinanceMain().setVersion(fsd.getFinanceMain().getVersion() + 1);
		finReceiptData.setFinanceDetail(financeDetail);
		finReceiptData.setBuildProcess("I");

		finReceiptData.setValueDate(valuedate);
		rch.setValueDate(null);
		finReceiptData.setReceiptHeader(rch);
		finReceiptData = receiptCalculator.initiateReceipt(finReceiptData, true);
		BigDecimal totDues = finReceiptData.getReceiptHeader().getTotalPastDues().getTotalDue();
		FinReceiptDetail recdtl = rch.getReceiptDetails().get(0);
		if (recdtl.getDueAmount().compareTo(totDues) > 0) {
			recdtl.setDueAmount(totDues);
		}
		finReceiptData = receiptCalculator.recalAutoAllocation(finReceiptData, valuedate, true);

		finReceiptData.setBuildProcess("R");
		finReceiptData.getRepayMain().setRepayAmountNow(BigDecimal.ZERO);
		finReceiptData.getRepayMain().setPrincipalPayNow(BigDecimal.ZERO);
		finReceiptData.getRepayMain().setProfitPayNow(BigDecimal.ZERO);

		for (ReceiptAllocationDetail allocate : finReceiptData.getReceiptHeader().getAllocations()) {
			allocate.setPaidAvailable(allocate.getPaidAmount());
			allocate.setWaivedAvailable(allocate.getWaivedAmount());
			allocate.setPaidAmount(BigDecimal.ZERO);
			allocate.setPaidGST(BigDecimal.ZERO);
			allocate.setTotalPaid(BigDecimal.ZERO);
			allocate.setBalance(allocate.getTotalDue());
			allocate.setWaivedAmount(BigDecimal.ZERO);
			allocate.setWaivedGST(BigDecimal.ZERO);
			allocate.setTdsPaid(BigDecimal.ZERO);
			allocate.setTdsWaived(BigDecimal.ZERO);
		}

		finReceiptData = receiptCalculator.initiateReceipt(finReceiptData, true);
		finReceiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(schdDtls);
		/*
		 * finReceiptData.getReceiptHeader().getXcessPayables().clear();
		 * finReceiptData.getReceiptHeader().getXcessPayables().addAll(xcsPaybles);
		 */
		rch.setValueDate(valuedate);
		List<Object> returnList = doProcessReceipts(fm, schdDtls, profitDetail, rch, finFeeDetailList, fsd, valuedate,
				postDate, financeDetail);
		scheduleDetails = (List<FinanceScheduleDetail>) returnList.get(0);

		BigDecimal priPaynow = BigDecimal.ZERO;

		for (ReceiptAllocationDetail allocate : rch.getAllocations()) {
			if (StringUtils.equals(RepayConstants.ALLOCATION_PRI, allocate.getAllocationType())) {
				priPaynow = allocate.getPaidAmount();
				break;
			}
		}

		// Preparing Total Principal Amount
		BigDecimal totPriPaid = BigDecimal.ZERO;
		for (FinReceiptDetail rcd : rcdList) {
			if (rcd.getRepayHeader() != null) {
				FinRepayHeader repayHeader = rcd.getRepayHeader();
				if (repayHeader.getRepayScheduleDetails() != null && !repayHeader.getRepayScheduleDetails().isEmpty()) {
					for (RepayScheduleDetail rpySchd : repayHeader.getRepayScheduleDetails()) {
						totPriPaid = totPriPaid
								.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
					}
				}
			}
		}
		fm.setFinRepaymentAmount(fm.getFinRepaymentAmount().add(totPriPaid));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();
		Date presentmentSchDate = rch.getPresentmentSchDate() != null ? rch.getPresentmentSchDate() : valuedate;

		for (FinanceScheduleDetail schd : scheduleDetails) {
			Date schdDate = schd.getSchDate();
			// Skip if Repayment date after Current Business date
			if (!ImplementationConstants.ALLOW_OLDEST_DUE) {
				if (schdDate.compareTo(presentmentSchDate) != 0) {
					continue;
				}

				financeScheduleDetailDAO.updateForRpy(schd);
				break;
			}

			schedules.add(schd);
			if (schedules.size() > 100) {
				financeScheduleDetailDAO.updateListForRpy(schedules);
				schedules.clear();
			}
		}

		if (CollectionUtils.isNotEmpty(schedules)) {
			financeScheduleDetailDAO.updateListForRpy(schedules);
		}

		doSaveReceipts(rch, null, true);

		financeMainDAO.updatePaymentInEOD(fm);
		limitManagement.processLoanRepay(fm, customer, priPaynow, profitDetail.getFinCategory());
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Payment details as per receipt details
	 * 
	 * @param receiptHeaderFinReceiptDetail
	 */
	@SuppressWarnings("unchecked")
	public List<Object> doProcessReceipts(FinanceMain fm, List<FinanceScheduleDetail> schedules,
			FinanceProfitDetail pfd, FinReceiptHeader rch, List<FinFeeDetail> finFeeDetailList,
			FinScheduleData logScheduleData, Date valueDate, Date postingDate, FinanceDetail financeDetail)
			throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering");

		BigDecimal uAmz = BigDecimal.ZERO;
		BigDecimal uLpi = BigDecimal.ZERO;
		BigDecimal cpzChg = BigDecimal.ZERO;
		BigDecimal uGstLpi = BigDecimal.ZERO;
		BigDecimal uLpp = BigDecimal.ZERO;
		BigDecimal uGstLpp = BigDecimal.ZERO;
		// FIX ME:PSD# 144078-LAN is not foreclosed if the early settlement is done by Receipt Upload process
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		List<ManualAdviseMovements> movements = new ArrayList<>();

		// Find out Is there any schedule payment done or not, If exists Log
		// will be captured
		boolean isSchdLogReq = false;

		String receiptPurpose = rch.getReceiptPurpose();
		if (FinanceConstants.FINSER_EVENT_EARLYRPY.equals(receiptPurpose)
				|| FinanceConstants.FINSER_EVENT_EARLYSETTLE.equals(receiptPurpose)) {
			isSchdLogReq = true;
		}
		BigDecimal receiptFromBank = BigDecimal.ZERO;
		BigDecimal totXcessAmount = BigDecimal.ZERO;
		Map<String, BigDecimal> extDataMap = new HashMap<>();

		long linkedTranId = 0;
		String finReference = fm.getFinReference();
		// Create log entry for Action for Schedule Modification
		FinLogEntryDetail entryDetail = null;
		long logKey = 0;
		Date postDate = getPostDate(postingDate);
		if (isSchdLogReq) {
			entryDetail = new FinLogEntryDetail();
			entryDetail.setFinReference(finReference);
			entryDetail.setEventAction(receiptPurpose);
			entryDetail.setSchdlRecal(false);
			entryDetail.setPostDate(postDate);
			entryDetail.setReversalCompleted(false);

			if (!fm.isSimulateAccounting()) {
				logKey = finLogEntryDetailDAO.save(entryDetail);

				// Save Schedule Details For Future Modifications
				FinScheduleData oldFinSchdData = getFinSchDataByFinRef(finReference, "");
				oldFinSchdData.setFinanceMain(fm);
				oldFinSchdData.setFinReference(finReference);
				listSave(oldFinSchdData, "_Log", logKey);
			}

		}

		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, receiptPurpose)
				|| StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, receiptPurpose)) {
			isSchdLogReq = true;
		}

		BigDecimal receiptAmount = BigDecimal.ZERO;
		for (FinReceiptDetail rcd : rcdList) {
			receiptAmount = receiptAmount.add(rcd.getAmount());
			String paymentType = rcd.getPaymentType();
			movements.addAll(rcd.getAdvMovements());
			if (!RepayConstants.RECEIPTMODE_EMIINADV.equals(paymentType)
					&& !RepayConstants.RECEIPTMODE_EXCESS.equals(paymentType)
					&& !RepayConstants.RECEIPTMODE_PAYABLE.equals(paymentType)
					&& !RepayConstants.RECEIPTMODE_ADVINT.equals(paymentType)
					&& !RepayConstants.RECEIPTMODE_ADVEMI.equals(paymentType)
					&& !RepayConstants.RECEIPTMODE_CASHCLT.equals(paymentType)
					&& !RepayConstants.RECEIPTMODE_DSF.equals(paymentType)) {
				receiptFromBank = receiptFromBank.add(rcd.getAmount());
			}
		}

		boolean feesExecuted = false;

		if (!fm.isSimulateAccounting()) {
			long postingId = postingsDAO.getPostingId();
			fm.setPostingId(postingId);
		}

		// Accounting Postings Process Execution
		AEEvent aeEvent = new AEEvent();

		EventProperties eventProperties = fm.getEventProperties();
		aeEvent.setEventProperties(eventProperties);
		if (eventProperties.isParameterLoaded()) {
			aeEvent.setPostDate(eventProperties.getPostDate());
		} else {
			aeEvent.setPostDate(SysParamUtil.getPostDate());
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes = new AEAmountCodes();

		// Last Receipt record for banking details
		FinReceiptDetail rcd = rcdList.get(rcdList.size() - 1);
		FinRepayHeader rph = rcd.getRepayHeader();

		fm.setGlSubHeadCodes(financeMainDAO.getGLSubHeadCodes(finReference));

		// GST Mapping details
		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(finReference);

		/**
		 * Defaulting with ZERO
		 */
		extDataMap.put("EX_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("EA_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("EAI_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("EAM_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("CACLT_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("DSF_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("PB_ReceiptAmount", BigDecimal.ZERO);

		List<ManualAdviseMovements> payableAdvMovements = new ArrayList<>();

		// Put Xcess Payables to Map along with GST
		List<XcessPayables> xcessPayables = rch.getXcessPayables();
		Map<String, BigDecimal> taxPercmap = GSTCalculator.getTaxPercentages(finReference);
		for (XcessPayables xcessPayable : xcessPayables) {
			BigDecimal totPaidNow = xcessPayable.getTotPaidNow();
			if (totPaidNow.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			String payableType = xcessPayable.getPayableType();
			String feeTypeCode = xcessPayable.getFeeTypeCode();

			switch (payableType) {
			case RepayConstants.EXAMOUNTTYPE_EXCESS:
				extDataMap.put("EX_ReceiptAmount", xcessPayable.getTotPaidNow());
				break;
			case RepayConstants.EXAMOUNTTYPE_EMIINADV:
				extDataMap.put("EA_ReceiptAmount", totPaidNow);
				break;
			case RepayConstants.EXAMOUNTTYPE_ADVINT:
				extDataMap.put("EAI_ReceiptAmount", totPaidNow);
				break;
			case RepayConstants.EXAMOUNTTYPE_ADVEMI:
				extDataMap.put("EAM_ReceiptAmount", totPaidNow);
				break;
			case RepayConstants.EXAMOUNTTYPE_CASHCLT:
				extDataMap.put("CACLT_ReceiptAmount", totPaidNow);
				break;
			case RepayConstants.EXAMOUNTTYPE_DSF:
				extDataMap.put("DSF_ReceiptAmount", totPaidNow);
				break;

			default:
				extDataMap.put((feeTypeCode + "_P"), totPaidNow);
				extDataMap.put((feeTypeCode + "_CGST_P"), xcessPayable.getPaidCGST());
				extDataMap.put((feeTypeCode + "_SGST_P"), xcessPayable.getPaidSGST());
				extDataMap.put((feeTypeCode + "_UGST_P"), xcessPayable.getPaidUGST());
				extDataMap.put((feeTypeCode + "_IGST_P"), xcessPayable.getPaidIGST());
				extDataMap.put((feeTypeCode + "_TDS_P"), xcessPayable.getTdsAmount());
				extDataMap.put((feeTypeCode + "_CESS_P"), xcessPayable.getPaidCESS());

				if (taxPercmap == null) {
					taxPercmap = GSTCalculator.getTaxPercentages(finReference);
				}

				payableAdvMovements.add(preparePayableMovement(taxPercmap, xcessPayable));
				break;
			}

			totXcessAmount = totXcessAmount.add(totPaidNow);
		}

		movements.addAll(payableAdvMovements);

		// Put Receipt amount from Bank/Cash Map along with GST
		// receiptFromBank = totXcessAmount.subtract(receiptFromBank);
		if (receiptFromBank.compareTo(BigDecimal.ZERO) > 0) {
			extDataMap.put("PB_ReceiptAmount", receiptFromBank);
		}

		// FIXME: NO SURE ON GOLD LOAN. SO FOR LOOP KEPT AS IS
		for (FinReceiptDetail frd : rcdList) {
			if (RepayConstants.RECEIPTMODE_REPLEDGE.equals(frd.getPaymentType())) {
				extDataMap.put("PR_ReceiptAmount", frd.getAmount());
			}
		}

		addZeroifNotContains(extDataMap, "EX_ReceiptAmount");
		addZeroifNotContains(extDataMap, "EA_ReceiptAmount");
		addZeroifNotContains(extDataMap, "PB_ReceiptAmount");

		// Branch Cash Update
		/*
		 * if (StringUtils.equals(rch.getReceiptMode(), RepayConstants.RECEIPTMODE_CASH)) {
		 * branchCashDetailDAO.updateBranchCashDetail(rch.getUserDetails().getBranchCode(), receiptFromBank,
		 * CashManagementConstants.Add_Receipt_Amount); }
		 */

		String finType = fm.getFinType();
		String cashierBranch = rch.getCashierBranch();
		String entityCode = fm.getEntityCode();

		if (FinanceConstants.PRODUCT_GOLD.equals(fm.getProductCategory())
				&& rch.getRefWaiverAmt().compareTo(BigDecimal.ZERO) > 0) {
			extDataMap.put("ae_refWaiver", rch.getRefWaiverAmt());
		}

		aeEvent.setCustID(fm.getCustID());
		aeEvent.setFinReference(finReference);
		aeEvent.setFinType(finType);
		aeEvent.setPromotion(fm.getPromotionCode());
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setPostingUserBranch(cashierBranch);
		aeEvent.setLinkedTranId(0);
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_REPAY);
		aeEvent.setValueDate(valueDate);
		aeEvent.setAppDate(eventProperties.getAppDate());
		aeEvent.setAppValueDate(eventProperties.getAppValueDate());
		aeEvent.setPostRefId(rch.getReceiptID());
		aeEvent.setPostingId(fm.getPostingId());
		aeEvent.setEntityCode(entityCode);

		amountCodes.setUserBranch(cashierBranch);
		amountCodes.setFinType(finType);
		amountCodes.setPartnerBankAc(rcd.getPartnerBankAc());
		amountCodes.setPartnerBankAcType(rcd.getPartnerBankAcType());
		amountCodes.setToExcessAmt(BigDecimal.ZERO);
		amountCodes.setToEmiAdvance(BigDecimal.ZERO);
		amountCodes.setPaymentType(rcd.getPaymentType());
		amountCodes.setBusinessvertical(fm.getBusinessVerticalCode());
		amountCodes.setEntitycode(entityCode);

		if (StringUtils.isNotBlank(fm.getPromotionCode())
				&& (fm.getPromotionSeqId() != null && fm.getPromotionSeqId() == 0)) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(fm.getPromotionCode(),
					AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(fm.getFinType(),
					AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_FINTYPE));
		}

		// Assignment Percentage
		Set<String> excludeFees = null;
		if (fm.getAssignmentId() != null && fm.getAssignmentId() > 0) {
			Assignment assignment = assignmentDAO.getAssignment(fm.getAssignmentId(), "");
			if (assignment != null) {
				amountCodes.setAssignmentPerc(assignment.getSharingPercentage());
				List<AssignmentDealExcludedFee> excludeFeesList = this.assignmentDealDAO
						.getApprovedAssignmentDealExcludedFeeList(assignment.getDealId());
				if (CollectionUtils.isNotEmpty(excludeFeesList)) {
					excludeFees = new HashSet<String>();
					for (AssignmentDealExcludedFee excludeFee : excludeFeesList) {
						excludeFees.add(excludeFee.getFeeTypeCode());
					}
				}
			}
		}

		extDataMap.putAll(prepareMovementMap(movements));

		BigDecimal adjustedToReceipt = BigDecimal.ZERO;
		adjustedToReceipt = rch.getTotalPastDues().getPaidAmount();
		adjustedToReceipt = adjustedToReceipt.add(rch.getTotalRcvAdvises().getPaidAmount());
		adjustedToReceipt = adjustedToReceipt.add(rch.getTotalBounces().getPaidAmount());
		adjustedToReceipt = adjustedToReceipt.add(rch.getTotalFees().getPaidAmount());

		BigDecimal toExcess = receiptAmount.subtract(adjustedToReceipt);
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, receiptPurpose)) {
			adjustedToReceipt = adjustedToReceipt.add(toExcess);
			toExcess = BigDecimal.ZERO;
		}

		amountCodes.setIntAdv(AdvanceType.hasAdvInterest(fm));

		if (toExcess.compareTo(BigDecimal.ZERO) > 0) {
			switch (rch.getExcessAdjustTo()) {
			case RepayConstants.EXCESSADJUSTTO_EMIINADV:
				extDataMap.put("ae_toEmiAdvance", toExcess);
				break;
			case RepayConstants.EXCESSADJUSTTO_BOUNCE:
				extDataMap.put("ae_toBounce", toExcess);
				break;
			case RepayConstants.EXCESSADJUSTTO_SETTLEMENT:
				extDataMap.put("ae_toSettlement", toExcess);
				break;
			case RepayConstants.EXCESSADJUSTTO_EXCESS:
				toExcess = adjustExcessForAdvInt(rch, amountCodes, toExcess);
				extDataMap.put("ae_toExcessAmt", toExcess);
				break;

			default:
				break;
			}

		}

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		if (excludeFees != null) {
			dataMap.put(AccountConstants.POSTINGS_EXCLUDE_FEES, excludeFees);
		}

		if (!feesExecuted && !FinanceConstants.FINSER_EVENT_SCHDRPY.equals(receiptPurpose)) {
			Map<String, BigDecimal> feeMap = new HashMap<>();

			if (finFeeDetailList != null) {
				prepareFeeRulesMap(amountCodes, feeMap, finFeeDetailList, rcd.getPaymentType());
			}
			extDataMap.putAll(feeMap);
		}

		List<Object> returnList = null;

		if (adjustedToReceipt.compareTo(BigDecimal.ZERO) <= 0) {
			dataMap.putAll(extDataMap);
			Map<String, Object> glSubHeadCodes = fm.getGlSubHeadCodes();
			dataMap.put("emptype", glSubHeadCodes.get("EMPTYPE"));
			aeEvent.setDataMap(dataMap);
			aeEvent.setSimulateAccounting(fm.isSimulateAccounting());
			aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
			rph.setLinkedTranId(aeEvent.getLinkedTranId());

			extDataMap = null;
			returnList = new ArrayList<>();
			returnList.add(schedules);
			returnList.add(uAmz);
			returnList.add(uLpi);
			returnList.add(uGstLpi);
			returnList.add(uLpp);
			returnList.add(uGstLpp);
			returnList.add(cpzChg);

			if (fm.isSimulateAccounting()) {
				if (CollectionUtils.isNotEmpty(fm.getReturnDataSet())) {
					fm.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
				} else {
					fm.setReturnDataSet(aeEvent.getReturnDataSet());
				}
			}

			return returnList;
		}

		/*
		 * At the time of EOD postDate should not be APP date we need to consider value date hence parameterized the
		 * postDate
		 */

		returnList = doRepayPostings(financeDetail, rch, extDataMap, gstExecutionMap, postingDate, rph.getRepayID());

		if (!(Boolean) returnList.get(0)) {
			String errParm = (String) returnList.get(1);
			throw new InterfaceException("9999", errParm);
		}
		int transOrder = 0;
		linkedTranId = (long) returnList.get(1);
		for (FinReceiptDetail rcDtl : rch.getReceiptDetails()) {
			FinRepayHeader rpyh = rcDtl.getRepayHeader();
			if (rpyh != null) {
				rpyh.setLinkedTranId(linkedTranId);
			}
		}
		rph.setLinkedTranId(linkedTranId);
		transOrder = (int) returnList.get(7);
		rph.setValueDate(postDate);
		schedules = (List<FinanceScheduleDetail>) returnList.get(2);

		// Unrealized Income amount
		uAmz = uAmz.add((BigDecimal) returnList.get(3));
		rph.setRealizeUnAmz(uAmz);

		// Unrealized LPI Amount
		uLpi = uLpi.add((BigDecimal) returnList.get(5));
		uGstLpi = uGstLpi.add((BigDecimal) returnList.get(5));
		rph.setRealizeUnLPI(uLpi);

		// Capitalization Change Amount
		cpzChg = cpzChg.add((BigDecimal) returnList.get(6));
		rph.setCpzChg(cpzChg);

		// LPP Income Amount
		FinTaxIncomeDetail taxIncome = (FinTaxIncomeDetail) returnList.get(8);
		if (taxIncome != null) {
			uLpp = uLpp.add(taxIncome.getReceivedAmount());
			uGstLpp = uGstLpp.add(CalculationUtil.getTotalGST(taxIncome));
		}

		// Setting/Maintaining Log key for Last log of Schedule Details
		rcdList.get(rcdList.size() - 1).setLogKey(logKey);

		if (fm.isSimulateAccounting()) {
			extDataMap = null;
			returnList = new ArrayList<>();
			returnList.add(schedules);
			returnList.add(uAmz);
			returnList.add(uLpi);
			returnList.add(uGstLpi);
			returnList.add(uLpp);
			returnList.add(uGstLpp);
			returnList.add(cpzChg);

			return returnList;
		}

		if (CollectionUtils.isNotEmpty(finFeeDetailList)) {
			createDebitInvoice(linkedTranId, financeDetail);

			// Waiver Fees Invoice Preparation
			if (ImplementationConstants.TAX_DFT_CR_INV_REQ) {
				createCreditInvoice(linkedTranId, financeDetail);
			}
		}

		/* Preparing GST Invoice Report for Manual Advises and Bounce */
		if (financeDetail != null) {
			if (CollectionUtils.isNotEmpty(movements)) {
				if (generateInvoice(financeDetail, movements, linkedTranId)) {
					payableAdvMovements.clear();
				}
			}

			if (CollectionUtils.isNotEmpty(payableAdvMovements)) {
				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setLinkedTranId(linkedTranId);
				invoiceDetail.setFinanceDetail(financeDetail);
				invoiceDetail.setMovements(payableAdvMovements);
				invoiceDetail.setWaiver(false);
				invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);

				this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

				payableAdvMovements.clear();
			}
		}

		extDataMap = null;
		returnList = new ArrayList<>();
		returnList.add(schedules);
		returnList.add(uAmz);
		returnList.add(uLpi);
		returnList.add(uGstLpi);
		returnList.add(uLpp);
		returnList.add(uGstLpp);
		returnList.add(cpzChg);

		logger.debug(Literal.LEAVING);

		return returnList;
	}

	private boolean generateInvoice(FinanceDetail financeDetail, List<ManualAdviseMovements> movements,
			long linkedTranId) {

		boolean generateInvoice = false;
		List<ManualAdviseMovements> rcvPaidMovementList = new ArrayList<>();
		List<ManualAdviseMovements> payPaidMovementList = new ArrayList<>();
		List<ManualAdviseMovements> waivedMovementList = new ArrayList<>();
		FeeType bounceFee = null;

		// GST Invoice data resetting based on Accounting Process
		String isGSTInvOnDue = SysParamUtil.getValueAsString(SMTParameterConstants.GST_INV_ON_DUE);

		for (ManualAdviseMovements movement : movements) {
			TaxHeader taxHeader = movement.getTaxHeader();
			Taxes cgstTax = new Taxes();
			Taxes sgstTax = new Taxes();
			Taxes igstTax = new Taxes();
			Taxes ugstTax = new Taxes();
			Taxes cessTax = new Taxes();
			List<Taxes> taxDetails = taxHeader.getTaxDetails();
			if (taxHeader != null && CollectionUtils.isNotEmpty(taxDetails)) {
				for (Taxes taxes : taxDetails) {
					switch (taxes.getTaxType()) {
					case RuleConstants.CODE_CGST:
						cgstTax = taxes;
						break;
					case RuleConstants.CODE_SGST:
						sgstTax = taxes;
						break;
					case RuleConstants.CODE_IGST:
						igstTax = taxes;
						break;
					case RuleConstants.CODE_UGST:
						ugstTax = taxes;
						break;
					case RuleConstants.CODE_CESS:
						cessTax = taxes;
						break;

					default:
						break;
					}
				}
			}

			BigDecimal paidGST = cgstTax.getPaidTax().add(sgstTax.getPaidTax()).add(igstTax.getPaidTax())
					.add(ugstTax.getPaidTax()).add(cessTax.getPaidTax());
			BigDecimal waivedGST = cgstTax.getWaivedTax().add(sgstTax.getWaivedTax()).add(igstTax.getWaivedTax())
					.add(ugstTax.getWaivedTax()).add(cessTax.getWaivedTax());

			if (paidGST.compareTo(BigDecimal.ZERO) > 0 || waivedGST.compareTo(BigDecimal.ZERO) > 0) {
				ManualAdvise manualAdvise = manualAdviseDAO.getManualAdviseById(movement.getAdviseID(), "_AView");

				if (StringUtils.isBlank(manualAdvise.getFeeTypeCode()) && manualAdvise.getBounceID() > 0) {
					if (bounceFee == null) {
						bounceFee = feeTypeDAO.getApprovedFeeTypeByFeeCode(PennantConstants.FEETYPE_BOUNCE);
					}
					movement.setFeeTypeCode(bounceFee.getFeeTypeCode());
					movement.setFeeTypeDesc(bounceFee.getFeeTypeDesc());
					movement.setTaxApplicable(bounceFee.isTaxApplicable());
					movement.setTaxComponent(bounceFee.getTaxComponent());

				} else {

					movement.setFeeTypeCode(manualAdvise.getFeeTypeCode());
					movement.setFeeTypeDesc(manualAdvise.getFeeTypeDesc());
					movement.setTaxApplicable(manualAdvise.isTaxApplicable());
					movement.setTaxComponent(manualAdvise.getTaxComponent());

				}

				boolean dueCreated = manualAdvise.isDueCreation();

				if (dueCreated) {
					// Due Created and GST invoice not Generated
					if (PennantConstants.NO.equals(isGSTInvOnDue)) {
						if (manualAdvise.getAdviseType() == FinanceConstants.MANUAL_ADVISE_PAYABLE) {
							payPaidMovementList.add(movement);
						} else {
							rcvPaidMovementList.add(movement);
						}
					} else {
						// Waiver GST only in case Due Created & GST Invoice raised
						if (movement.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
							// Setting Debit Invoice ID
							Long dbInvID = manualAdviseDAO.getDebitInvoiceID(movement.getAdviseID());
							movement.setDebitInvoiceId(dbInvID);
							waivedMovementList.add(movement);

							InvoiceDetail invoiceDetail = new InvoiceDetail();
							invoiceDetail.setLinkedTranId(linkedTranId);
							invoiceDetail.setFinanceDetail(financeDetail);
							invoiceDetail.setMovements(waivedMovementList);
							invoiceDetail.setWaiver(true);
							invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);

							Long invoiceID = this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

							if (taxHeader != null) {
								taxHeader.setInvoiceID(invoiceID);
							}

							waivedMovementList.clear();
						}
					}

				} else {
					if (manualAdvise.getAdviseType() == FinanceConstants.MANUAL_ADVISE_PAYABLE) {
						payPaidMovementList.add(movement);
					} else {
						rcvPaidMovementList.add(movement);
					}
				}
			}
		}

		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();

		if (fm.isSimulateAccounting()) {
			return generateInvoice;
		}

		// GST Invoice for Bounce/Manual Advise
		if (CollectionUtils.isNotEmpty(rcvPaidMovementList)) {
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(linkedTranId);
			invoiceDetail.setFinanceDetail(financeDetail);
			invoiceDetail.setMovements(rcvPaidMovementList);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);

			Long invoiceID = this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

			for (ManualAdviseMovements advMov : rcvPaidMovementList) {
				if (advMov.getTaxHeader() != null) {
					advMov.getTaxHeader().setInvoiceID(invoiceID);
				}
			}
		}

		// GST Invoice for Payable Advises
		if (CollectionUtils.isNotEmpty(payPaidMovementList)) {
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(linkedTranId);
			invoiceDetail.setFinanceDetail(financeDetail);
			invoiceDetail.setMovements(payPaidMovementList);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);

			Long invoiceID = this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

			for (ManualAdviseMovements advMov : payPaidMovementList) {
				if (advMov.getTaxHeader() != null) {
					advMov.getTaxHeader().setInvoiceID(invoiceID);
				}
			}

			generateInvoice = true;
		}

		return generateInvoice;

	}

	private void createCreditInvoice(long linkedTranId, FinanceDetail financeDetail) {
		List<FinFeeDetail> waiverFees = new ArrayList<>();
		List<FinFeeDetail> finFeeDetailList = financeDetail.getFinScheduleData().getFinFeeDetailList();
		for (FinFeeDetail fee : finFeeDetailList) {
			if (fee.isTaxApplicable() && fee.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
				waiverFees.add(fee);
			}
		}

		if (CollectionUtils.isEmpty(waiverFees)) {
			return;
		}

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setLinkedTranId(linkedTranId);
		invoiceDetail.setFinanceDetail(financeDetail);
		invoiceDetail.setFinFeeDetailsList(waiverFees);
		invoiceDetail.setOrigination(false);
		invoiceDetail.setWaiver(true);
		invoiceDetail.setDbInvSetReq(false);
		invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);

		Long dueInvoiceID = gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);

		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			TaxHeader taxHeader = finFeeDetail.getTaxHeader();
			if (taxHeader != null && finFeeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
				taxHeader.setInvoiceID(dueInvoiceID);
			}
		}

	}

	private void createDebitInvoice(long linkedTranId, FinanceDetail financeDetail) {
		List<FinFeeDetail> finFeeDetails = new ArrayList<>();

		List<FinFeeDetail> finFeeDetailList = financeDetail.getFinScheduleData().getFinFeeDetailList();
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (!finFeeDetail.isOriginationFee()) {
				finFeeDetails.add(finFeeDetail);
			}
		}

		if (CollectionUtils.isEmpty(finFeeDetails)) {
			return;
		}

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setLinkedTranId(linkedTranId);
		invoiceDetail.setFinanceDetail(financeDetail);
		invoiceDetail.setFinFeeDetailsList(finFeeDetailList);
		invoiceDetail.setOrigination(false);
		invoiceDetail.setWaiver(false);
		invoiceDetail.setDbInvSetReq(false);
		invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);

		Long dueInvoiceID = this.gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);

		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			TaxHeader taxHeader = finFeeDetail.getTaxHeader();
			if (taxHeader != null && finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
				taxHeader.setInvoiceID(dueInvoiceID);
			}
		}
	}

	private ManualAdviseMovements preparePayableMovement(Map<String, BigDecimal> taxPercmap,
			XcessPayables xcessPayable) {

		ManualAdvise manualAdvise = manualAdviseDAO.getManualAdviseById(xcessPayable.getPayableID(), "_AView");

		if (manualAdvise == null) {
			return null;
		}

		if (manualAdvise.isDueCreation()) {
			return null;
		}

		// Creation of Payable Movements
		ManualAdviseMovements advMovement = new ManualAdviseMovements();
		advMovement.setAdviseID(manualAdvise.getAdviseID());
		advMovement.setAdviseType(manualAdvise.getAdviseType());
		advMovement.setFeeTypeCode(xcessPayable.getFeeTypeCode());
		advMovement.setFeeTypeDesc(xcessPayable.getPayableDesc());
		advMovement.setTaxComponent(xcessPayable.getTaxType());
		advMovement.setPaidAmount(xcessPayable.getTotPaidNow());

		// Prepare Tax Details
		TaxHeader taxHeader = new TaxHeader();
		taxHeader.setNewRecord(true);
		taxHeader.setRecordType(PennantConstants.RCD_ADD);
		taxHeader.setVersion(taxHeader.getVersion() + 1);

		Taxes cgstTax = getTaxDetail(RuleConstants.CODE_CGST, taxPercmap.get(RuleConstants.CODE_CGST));
		Taxes sgstTax = getTaxDetail(RuleConstants.CODE_SGST, taxPercmap.get(RuleConstants.CODE_SGST));
		Taxes igstTax = getTaxDetail(RuleConstants.CODE_IGST, taxPercmap.get(RuleConstants.CODE_IGST));
		Taxes ugstTax = getTaxDetail(RuleConstants.CODE_UGST, taxPercmap.get(RuleConstants.CODE_UGST));
		Taxes cessTax = getTaxDetail(RuleConstants.CODE_CESS, taxPercmap.get(RuleConstants.CODE_CESS));

		// Paid GST Values
		cgstTax.setPaidTax(xcessPayable.getPaidCGST());
		sgstTax.setPaidTax(xcessPayable.getPaidSGST());
		igstTax.setPaidTax(xcessPayable.getPaidIGST());
		ugstTax.setPaidTax(xcessPayable.getPaidUGST());
		cessTax.setPaidTax(xcessPayable.getPaidCESS());

		taxHeader.getTaxDetails().add(cgstTax);
		taxHeader.getTaxDetails().add(sgstTax);
		taxHeader.getTaxDetails().add(igstTax);
		taxHeader.getTaxDetails().add(ugstTax);
		taxHeader.getTaxDetails().add(cessTax);
		advMovement.setTaxHeader(taxHeader);

		return advMovement;
	}

	private Taxes getTaxDetail(String taxType, BigDecimal taxPerc) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPerc);
		return taxes;
	}

	private BigDecimal adjustExcessForAdvInt(FinReceiptHeader rch, AEAmountCodes amountCodes, BigDecimal toExcess) {
		if (amountCodes.isIntAdv() && toExcess.compareTo(BigDecimal.ZERO) > 0) {
			if (FinanceConstants.FINSER_EVENT_EARLYSETTLE.equals(rch.getReceiptPurpose())) {
				for (ReceiptAllocationDetail rad : rch.getAllocations()) {
					String allocationType = rad.getAllocationType();
					if (RepayConstants.ALLOCATION_FUT_TDS.equals(allocationType)) {
						toExcess = toExcess.subtract(rad.getDueAmount());
						toExcess = toExcess.subtract(rad.getDueAmount());
					}
				}
			}
		}
		return toExcess;
	}

	/**
	 * Method for Processing Manual Advise Postings
	 * 
	 * @param rcd
	 * @param financeMain
	 * @param movements
	 * @param postBranch
	 * @param dateValueDate
	 * @param postDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	private void procManualAdvPostings(FinReceiptDetail rcd, FinanceMain financeMain,
			List<ManualAdviseMovements> movements, String postBranch, Map<String, BigDecimal> extDataMap,
			Date dateValueDate, FinanceDetail financeDetail, FinRepayHeader repayHeader, int transOrder)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		// Accounting Postings Process Execution
		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		aeEvent.setCustID(financeMain.getCustID());
		aeEvent.setFinReference(financeMain.getFinReference());
		aeEvent.setFinType(financeMain.getFinType());
		aeEvent.setPromotion(financeMain.getPromotionCode());
		aeEvent.setBranch(financeMain.getFinBranch());
		aeEvent.setCcy(financeMain.getFinCcy());
		aeEvent.setPostingUserBranch(postBranch);
		aeEvent.setLinkedTranId(repayHeader.getLinkedTranId());
		aeEvent.setTransOrder(transOrder);
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_REPAY);
		aeEvent.setValueDate(dateValueDate);
		aeEvent.setPostRefId(rcd.getReceiptID());
		aeEvent.setPostingId(financeMain.getPostingId());

		aeEvent.getAcSetIDList().clear();
		if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getPromotionCode(),
					AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getFinType(),
					AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_FINTYPE));
		}

		amountCodes.setFinType(financeMain.getFinType());
		amountCodes.setPartnerBankAc(rcd.getPartnerBankAc());
		amountCodes.setPartnerBankAcType(rcd.getPartnerBankAcType());
		amountCodes.setPaymentType(rcd.getPaymentType());

		// Assignment Percentage
		Set<String> excludeFees = null;
		if (financeMain.getAssignmentId() > 0) {
			Assignment assignment = assignmentDAO.getAssignment(financeMain.getAssignmentId(), "");
			if (assignment != null) {
				amountCodes.setAssignmentPerc(assignment.getSharingPercentage());
				List<AssignmentDealExcludedFee> excludeFeesList = this.assignmentDealDAO
						.getApprovedAssignmentDealExcludedFeeList(assignment.getDealId());
				if (CollectionUtils.isNotEmpty(excludeFeesList)) {
					excludeFees = new HashSet<String>();
					for (AssignmentDealExcludedFee excludeFee : excludeFeesList) {
						excludeFees.add(excludeFee.getFeeTypeCode());
					}
				}
			}
		}

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		if (excludeFees != null) {
			dataMap.put(AccountConstants.POSTINGS_EXCLUDE_FEES, excludeFees);
		}

		dataMap.putAll(prepareMovementMap(movements));
		if (extDataMap != null) {
			dataMap.putAll(extDataMap);
		}
		aeEvent.setDataMap(dataMap);

		// Accounting Entry Execution
		postingsPreparationUtil.postAccounting(aeEvent);
		if (aeEvent.isPostingSucess()) {
			repayHeader.setLinkedTranId(aeEvent.getLinkedTranId());
		}

		// preparing GST Invoice Report for Bounce/ManualAdvise
		List<ManualAdviseMovements> movementList = new ArrayList<ManualAdviseMovements>();
		if (CollectionUtils.isNotEmpty(movements) && financeDetail != null) {
			FeeType bounceFee = null;

			// GST Invoice data resetting based on Accounting Process
			String isGSTInvOnDue = SysParamUtil.getValueAsString(SMTParameterConstants.GST_INV_ON_DUE);

			for (ManualAdviseMovements movement : movements) {

				TaxHeader taxHeader = movement.getTaxHeader();
				Taxes cgstTax = new Taxes();
				Taxes sgstTax = new Taxes();
				Taxes igstTax = new Taxes();
				Taxes ugstTax = new Taxes();
				Taxes cessTax = new Taxes();
				List<Taxes> taxDetails = taxHeader.getTaxDetails();
				if (taxHeader != null && CollectionUtils.isNotEmpty(taxDetails)) {
					for (Taxes taxes : taxDetails) {
						if (StringUtils.equals(RuleConstants.CODE_CGST, taxes.getTaxType())) {
							cgstTax = taxes;
						} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
							sgstTax = taxes;
						} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
							igstTax = taxes;
						} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
							ugstTax = taxes;
						} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
							cessTax = taxes;
						}
					}
				}

				BigDecimal paidGST = cgstTax.getPaidTax().add(sgstTax.getPaidTax()).add(igstTax.getPaidTax())
						.add(ugstTax.getPaidTax()).add(cessTax.getPaidTax());

				if (paidGST.compareTo(BigDecimal.ZERO) > 0) {

					ManualAdvise manualAdvise = manualAdviseDAO.getManualAdviseById(movement.getAdviseID(), "_AView");
					boolean prepareInvoice = false;
					if (StringUtils.isBlank(manualAdvise.getFeeTypeCode()) && manualAdvise.getBounceID() > 0) {
						if (bounceFee == null) {
							bounceFee = feeTypeDAO.getApprovedFeeTypeByFeeCode(PennantConstants.FEETYPE_BOUNCE);
						}
						movement.setFeeTypeCode(bounceFee.getFeeTypeCode());
						movement.setFeeTypeDesc(bounceFee.getFeeTypeDesc());
						movement.setTaxApplicable(bounceFee.isTaxApplicable());
						movement.setTaxComponent(bounceFee.getTaxComponent());
						if (StringUtils.equals(isGSTInvOnDue, PennantConstants.NO)) {
							prepareInvoice = true;
						}
					} else {
						movement.setFeeTypeCode(manualAdvise.getFeeTypeCode());
						movement.setFeeTypeDesc(manualAdvise.getFeeTypeDesc());
						movement.setTaxApplicable(manualAdvise.isTaxApplicable());
						movement.setTaxComponent(manualAdvise.getTaxComponent());
						prepareInvoice = true;
					}

					if (prepareInvoice) {
						movementList.add(movement);
					}
				}
			}

			if (CollectionUtils.isNotEmpty(movementList)) {
				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
				invoiceDetail.setFinanceDetail(financeDetail);
				invoiceDetail.setMovements(movementList);
				invoiceDetail.setWaiver(false);
				invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);

				this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

				// GST Invoice for Bounce/Manual Advise Waivers
				List<ManualAdviseMovements> waiverMovements = new ArrayList<ManualAdviseMovements>();

				for (ManualAdviseMovements movement : movementList) {
					if (movement.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
						waiverMovements.add(movement);
					}
				}
				// preparing GST Invoice Report for Bounce/ManualAdvise Waivers
				if (CollectionUtils.isNotEmpty(waiverMovements)) {
					invoiceDetail.setFinanceDetail(financeDetail);
					invoiceDetail.setMovements(waiverMovements);
					invoiceDetail.setWaiver(true);
					invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);

					this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);
				}
			}
		}

		logger.debug("Leaving");
	}

	private Map<String, BigDecimal> prepareMovementMap(List<ManualAdviseMovements> movements) {
		Map<String, BigDecimal> movementMap = new HashMap<>();

		addAmountToMap(movementMap, "bounceChargePaid", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_CGST_P", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_IGST_P", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_SGST_P", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_UGST_P", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_CESS_P", BigDecimal.ZERO);

		addAmountToMap(movementMap, "bounceChargeWaived", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_CGST_W", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_IGST_W", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_SGST_W", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_UGST_W", BigDecimal.ZERO);
		addAmountToMap(movementMap, "bounceCharge_CESS_W", BigDecimal.ZERO);

		String bounceComponent = feeTypeDAO.getTaxComponent(RepayConstants.ALLOCATION_BOUNCE);

		for (ManualAdviseMovements movement : movements) {
			TaxHeader taxHeader = movement.getTaxHeader();

			Taxes cgstTax = new Taxes();
			Taxes sgstTax = new Taxes();
			Taxes igstTax = new Taxes();
			Taxes ugstTax = new Taxes();
			Taxes cessTax = new Taxes();

			List<Taxes> taxDetails = taxHeader.getTaxDetails();

			if (CollectionUtils.isNotEmpty(taxDetails)) {
				for (Taxes taxes : taxDetails) {
					switch (taxes.getTaxType()) {
					case RuleConstants.CODE_CGST:
						cgstTax = taxes;
						break;
					case RuleConstants.CODE_SGST:
						sgstTax = taxes;
						break;
					case RuleConstants.CODE_IGST:
						igstTax = taxes;
						break;
					case RuleConstants.CODE_UGST:
						ugstTax = taxes;
						break;
					case RuleConstants.CODE_CESS:
						cessTax = taxes;
						break;

					default:
						break;
					}
				}
			}

			BigDecimal cgstPaid = cgstTax.getPaidTax();
			BigDecimal sgstPaid = sgstTax.getPaidTax();
			BigDecimal igstPaid = igstTax.getPaidTax();
			BigDecimal ugstPaid = ugstTax.getPaidTax();
			BigDecimal cessPaid = cessTax.getPaidTax();

			BigDecimal cgstWaived = cgstTax.getWaivedTax();
			BigDecimal sgstWaived = sgstTax.getWaivedTax();
			BigDecimal igstWaived = igstTax.getWaivedTax();
			BigDecimal ugstWaived = ugstTax.getWaivedTax();
			BigDecimal cessWaived = cessTax.getWaivedTax();

			BigDecimal paidAmt = movement.getPaidAmount();
			BigDecimal waivedAmt = movement.getWaivedAmount();
			BigDecimal tdsPaid = movement.getTdsPaid();

			BigDecimal totPaidGST = cgstPaid.add(sgstPaid).add(igstPaid).add(ugstPaid).add(cessPaid);
			BigDecimal totWaivedGST = cgstWaived.add(sgstWaived).add(igstWaived).add(ugstWaived).add(cessWaived);

			String feeTypeCode = movement.getFeeTypeCode();
			String taxComponent = feeTypeDAO.getTaxComponent(feeTypeCode);

			if (StringUtils.isEmpty(feeTypeCode) || RepayConstants.ALLOCATION_BOUNCE.equals(feeTypeCode)) {

				if (taxComponent == null) {
					taxComponent = bounceComponent;
				}
				if (bounceComponent == null) {
					continue;
				}

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
					addAmountToMap(movementMap, "bounceChargePaid", paidAmt);
					addAmountToMap(movementMap, "bounceChargeWaived", waivedAmt);
				} else {
					addAmountToMap(movementMap, "bounceChargePaid", paidAmt.add(totPaidGST));
					addAmountToMap(movementMap, "bounceChargeWaived", waivedAmt.add(totWaivedGST));
				}
			} else {
				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
					addAmountToMap(movementMap, feeTypeCode + "_P", paidAmt);
					addAmountToMap(movementMap, feeTypeCode + "_W", waivedAmt);
				} else {
					addAmountToMap(movementMap, feeTypeCode + "_P", paidAmt.add(totPaidGST));
					addAmountToMap(movementMap, feeTypeCode + "_W", waivedAmt.add(totWaivedGST));
				}
			}

			addAmountToMap(movementMap, feeTypeCode + "_CGST_P", cgstPaid);
			addAmountToMap(movementMap, feeTypeCode + "_SGST_P", sgstPaid);
			addAmountToMap(movementMap, feeTypeCode + "_IGST_P", igstPaid);
			addAmountToMap(movementMap, feeTypeCode + "_UGST_P", ugstPaid);
			addAmountToMap(movementMap, feeTypeCode + "_CESS_P", cessPaid);

			addAmountToMap(movementMap, feeTypeCode + "_CGST_W", cgstWaived);
			addAmountToMap(movementMap, feeTypeCode + "_SGST_W", sgstWaived);
			addAmountToMap(movementMap, feeTypeCode + "_IGST_W", igstWaived);
			addAmountToMap(movementMap, feeTypeCode + "_UGST_W", ugstWaived);
			addAmountToMap(movementMap, feeTypeCode + "_CESS_W", cessWaived);

			addAmountToMap(movementMap, feeTypeCode + "_TDS_P", tdsPaid);
		}

		return movementMap;
	}

	private void addAmountToMap(Map<String, BigDecimal> movementMap, String feeCode, BigDecimal amount) {
		BigDecimal amt = movementMap.computeIfAbsent(feeCode, code -> BigDecimal.ZERO);

		movementMap.put(feeCode, amt.add(amount));
	}

	private Map<String, BigDecimal> prepareFeeRulesMap(AEAmountCodes amountCodes, Map<String, BigDecimal> dataMap,
			List<FinFeeDetail> finFeeDetailList, String payType) {
		logger.debug(Literal.ENTERING);

		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (!finFeeDetail.isRcdVisible()) {
				continue;
			}

			dataMap.putAll(FeeCalculator.getFeeRuleMap(payType, finFeeDetail));
		}

		logger.debug(Literal.LEAVING);
		return dataMap;
	}

	/**
	 * Method for Saving the Receipt records
	 * 
	 * @param rch
	 */
	public void doSaveReceipts(FinReceiptHeader rch, List<FinFeeDetail> finFeeDetails, boolean isApproval) {
		logger.debug("Entering");

		long receiptID = finReceiptHeaderDAO.save(rch, TableType.MAIN_TAB);
		rch.setReceiptID(receiptID);

		// Receipt Allocation Details
		Map<String, BigDecimal> allocationPaidMap = null;
		Map<String, BigDecimal> allocationWaivedMap = null;

		if (rch.getAllocations() != null && !rch.getAllocations().isEmpty()) {
			if (isApproval) {
				allocationPaidMap = new HashMap<>();
				allocationWaivedMap = new HashMap<>();
			}

			for (int i = 0; i < rch.getAllocations().size(); i++) {
				ReceiptAllocationDetail allocation = rch.getAllocations().get(i);
				allocation.setReceiptID(receiptID);
				allocation.setAllocationID(i + 1);

				if (isApproval) {
					String allocationType = allocation.getAllocationType();
					long allocationTo = allocation.getAllocationTo();
					allocationPaidMap.put(allocationType + "_" + allocationTo, allocation.getPaidAmount());
					allocationWaivedMap.put(allocationType + "_" + allocationTo, allocation.getWaivedAmount());

					// Manual Advises update
					if (RepayConstants.ALLOCATION_MANADV.equals(allocationType)) {
						if (allocation.getPaidAmount().compareTo(BigDecimal.ZERO) > 0
								|| allocation.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {

							ManualAdvise ma = new ManualAdvise();
							ma.setAdviseID(allocationTo);
							ma.setWaivedAmount(allocation.getWaivedAmount());

							for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
								for (ManualAdviseMovements movement : rcd.getAdvMovements()) {
									if (allocationTo == movement.getAdviseID()) {
										//Paid Details
										ma.setPaidAmount(ma.getPaidAmount().add(movement.getPaidAmount()));
										ma.setTdsPaid(ma.getTdsPaid().add(movement.getTdsPaid()));
										//advise.setPaidAmount(advise.getPaidAmount().subtract(advise.getTdsPaid()));
										//Waiver Details
										ma.setWaivedAmount(ma.getWaivedAmount().add(movement.getWaivedAmount()));

										TaxHeader taxHeader = movement.getTaxHeader();
										if (taxHeader != null) {
											List<Taxes> taxDetails = taxHeader.getTaxDetails();
											if (CollectionUtils.isNotEmpty(taxDetails)) {
												for (Taxes tax : taxDetails) {
													String taxType = tax.getTaxType();

													switch (taxType) {
													case RuleConstants.CODE_CGST:
														ma.setPaidCGST(ma.getPaidCGST().add(tax.getPaidTax()));
														ma.setWaivedCGST(ma.getWaivedCGST().add(tax.getWaivedTax()));
														break;
													case RuleConstants.CODE_SGST:
														ma.setPaidSGST(ma.getPaidSGST().add(tax.getPaidTax()));
														ma.setWaivedSGST(ma.getWaivedSGST().add(tax.getWaivedTax()));
														break;
													case RuleConstants.CODE_IGST:
														ma.setPaidIGST(ma.getPaidIGST().add(tax.getPaidTax()));
														ma.setWaivedIGST(ma.getWaivedIGST().add(tax.getWaivedTax()));
														break;
													case RuleConstants.CODE_UGST:
														ma.setPaidUGST(ma.getPaidUGST().add(tax.getPaidTax()));
														ma.setWaivedUGST(ma.getWaivedUGST().add(tax.getWaivedTax()));
														break;
													case RuleConstants.CODE_CESS:
														ma.setPaidCESS(ma.getPaidCESS().add(tax.getPaidTax()));
														ma.setWaivedCESS(ma.getWaivedCESS().add(tax.getWaivedTax()));
														break;
													default:
														break;
													}
												}
											}
										}
									}
								}
							}

							ma.setBalanceAmt(ma.getPaidAmount().add(ma.getWaivedAmount()).negate());

							// FIXME: PV. loan manual advise movements in receipts calculator
							manualAdviseDAO.updateAdvPayment(ma, TableType.MAIN_TAB);
						}
					}

					// Bounce Charges Update
					if (RepayConstants.ALLOCATION_BOUNCE.equals(allocationType)) {
						if (allocation.getPaidAmount().compareTo(BigDecimal.ZERO) > 0
								|| allocation.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
							/*
							 * if (bounceAdvises == null) { bounceAdvises =
							 * getManualAdviseDAO().getBounceAdvisesListByRef(rch.getReference(),
							 * FinanceConstants.MANUAL_ADVISE_RECEIVABLE, ""); }
							 */

							ManualAdvise ma = new ManualAdvise();
							ma.setAdviseID(allocationTo);
							ma.setWaivedAmount(allocation.getWaivedAmount());

							List<FinReceiptDetail> rcdList = sortReceiptDetails(rch.getReceiptDetails());

							for (FinReceiptDetail rcd : rcdList) {
								for (ManualAdviseMovements movement : rcd.getAdvMovements()) {
									if (allocationTo == movement.getAdviseID()) {

										ma.setPaidAmount(ma.getPaidAmount().add(movement.getPaidAmount()));
										ma.setTdsPaid(ma.getTdsPaid().add(movement.getTdsPaid()));

										// Waiver Details
										ma.setWaivedAmount(movement.getWaivedAmount());

										TaxHeader taxHeader = movement.getTaxHeader();
										if (taxHeader != null) {
											List<Taxes> taxDetails = taxHeader.getTaxDetails();

											if (CollectionUtils.isNotEmpty(taxDetails)) {
												for (Taxes tax : taxDetails) {
													String taxType = tax.getTaxType();

													switch (taxType) {
													case RuleConstants.CODE_CGST:
														ma.setPaidCGST(ma.getPaidCGST().add(tax.getPaidTax()));
														ma.setWaivedCGST(ma.getWaivedCGST().add(tax.getWaivedTax()));
														break;
													case RuleConstants.CODE_SGST:
														ma.setPaidSGST(ma.getPaidSGST().add(tax.getPaidTax()));
														ma.setWaivedSGST(ma.getWaivedSGST().add(tax.getWaivedTax()));
														break;
													case RuleConstants.CODE_IGST:
														ma.setPaidIGST(ma.getPaidIGST().add(tax.getPaidTax()));
														ma.setWaivedIGST(ma.getWaivedIGST().add(tax.getWaivedTax()));
														break;
													case RuleConstants.CODE_UGST:
														ma.setPaidUGST(ma.getPaidUGST().add(tax.getPaidTax()));
														ma.setWaivedUGST(ma.getWaivedUGST().add(tax.getWaivedTax()));
														break;
													case RuleConstants.CODE_CESS:
														ma.setPaidCESS(ma.getPaidCESS().add(tax.getPaidTax()));
														ma.setWaivedCESS(ma.getWaivedCESS().add(tax.getWaivedTax()));
														break;
													default:
														break;
													}
												}
											}
										}
									}
								}
							}
							manualAdviseDAO.updateAdvPayment(ma, TableType.MAIN_TAB);
						}
					}
				}
			}

			if (CollectionUtils.isNotEmpty(rch.getAllocations())) {
				for (ReceiptAllocationDetail allocation : rch.getAllocations()) {
					TaxHeader taxHeader = allocation.getTaxHeader();
					if (StringUtils.isNotBlank(allocation.getTaxType()) && taxHeader != null) {
						List<Taxes> taxDetails = taxHeader.getTaxDetails();
						if (CollectionUtils.isNotEmpty(taxDetails)) {
							Long headerId = taxHeaderDetailsDAO.save(taxHeader, TableType.MAIN_TAB.getSuffix());
							for (Taxes taxes : taxDetails) {
								taxes.setReferenceId(headerId);
							}
							taxHeaderDetailsDAO.saveTaxes(taxDetails, TableType.MAIN_TAB.getSuffix());
							allocation.setTaxHeaderId(headerId);
						}
					}
				}
			}

			allocationDetailDAO.saveAllocations(rch.getAllocations(), TableType.MAIN_TAB);
		}

		// Save Receipt Detail List by setting Receipt Header ID
		List<FinReceiptDetail> rcdList = sortReceiptDetails(rch.getReceiptDetails());
		for (FinReceiptDetail rcd : rcdList) {
			rcd.setReceiptID(receiptID);
			if (isApproval) {
				rcd.setStatus(RepayConstants.PAYSTATUS_APPROVED);
			} else {
				rcd.setStatus(rch.getReceiptModeStatus());
			}
			long receiptSeqID = finReceiptDetailDAO.save(rcd, TableType.MAIN_TAB);

			// Excess Amounts
			if (RepayConstants.RECEIPTMODE_EXCESS.equals(rcd.getPaymentType())
					|| RepayConstants.RECEIPTMODE_EMIINADV.equals(rcd.getPaymentType())
					|| RepayConstants.RECEIPTMODE_ADVINT.equals(rcd.getPaymentType())
					|| RepayConstants.RECEIPTMODE_ADVEMI.equals(rcd.getPaymentType())
					|| RepayConstants.RECEIPTMODE_CASHCLT.equals(rcd.getPaymentType())
					|| RepayConstants.RECEIPTMODE_DSF.equals(rcd.getPaymentType())) {

				long payAgainstID = rcd.getPayAgainstID();

				// Excess Amount make utilization
				if (payAgainstID != 0) {

					if (isApproval) {
						if (rcd.isNoReserve()) {
							// update only utilization
							finExcessAmountDAO.updateUtiliseOnly(payAgainstID, rcd.getAmount());
						} else {
							finExcessAmountDAO.updateUtilise(payAgainstID, rcd.getAmount());
						}

						// Delete Reserved Log against Excess and Receipt ID
						finExcessAmountDAO.deleteExcessReserve(receiptSeqID, payAgainstID,
								RepayConstants.RECEIPTTYPE_RECIPT);

						// Excess Movement Creation
						FinExcessMovement movement = new FinExcessMovement();
						movement.setExcessID(payAgainstID);
						movement.setReceiptID(receiptSeqID);
						movement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
						movement.setTranType(AccountConstants.TRANTYPE_DEBIT);
						movement.setAmount(rcd.getAmount());
						finExcessAmountDAO.saveExcessMovement(movement);
					} else {

						// Excess Amount make utilization
						FinExcessAmountReserve exReserve = finExcessAmountDAO.getExcessReserve(receiptSeqID,
								payAgainstID);
						if (exReserve != null) {

							// Update Reserve Amount in FinExcessAmount
							finExcessAmountDAO.updateExcessReserve(payAgainstID, exReserve.getReservedAmt().negate());

							// Delete Reserved Log against Excess and Receipt ID
							finExcessAmountDAO.deleteExcessReserve(receiptSeqID, payAgainstID,
									RepayConstants.RECEIPTTYPE_RECIPT);
						}
					}
				}
			}

			// Payable Advise Amounts
			if (StringUtils.equals(rcd.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE)) {

				long payAgainstID = rcd.getPayAgainstID();

				// Payable Advise Amount make utilization
				if (payAgainstID != 0) {

					if (isApproval) {

						BigDecimal payableAmt = rcd.getAmount();
						if (rcd.getPayAdvMovement() != null) {

							TaxHeader taxHeader = rcd.getPayAdvMovement().getTaxHeader();
							List<Taxes> taxDetails = taxHeader.getTaxDetails();
							if (taxHeader != null && CollectionUtils.isNotEmpty(taxDetails)) {
								for (Taxes taxes : taxDetails) {
									if (StringUtils.equals(taxes.getTaxType(),
											FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
										payableAmt = payableAmt.subtract(taxes.getPaidTax());
									}
								}
							}
						}

						if (rcd.isNoReserve()) {
							manualAdviseDAO.updateUtiliseOnly(payAgainstID, payableAmt);
						} else {
							manualAdviseDAO.updateUtilise(payAgainstID, payableAmt, rcd.isNoManualReserve());
						}

						// Delete Reserved Log against Advise and Receipt Seq ID
						manualAdviseDAO.deletePayableReserve(receiptSeqID, payAgainstID);

						// Payable Advise Movement Creation
						// FIXME
						if (rcd.getPayAdvMovement() != null) {
							rcd.getPayAdvMovement().setAdviseID(payAgainstID);
							rcd.getPayAdvMovement().setReceiptID(receiptID);
							rcd.getPayAdvMovement().setReceiptSeqID(receiptSeqID);
							rcd.getPayAdvMovement().setMovementDate(SysParamUtil.getAppDate());
							manualAdviseDAO.saveMovement(rcd.getPayAdvMovement(), TableType.MAIN_TAB.getSuffix());
						}

					} else {
						// Payable Amount make utilization
						ManualAdviseReserve payableReserve = manualAdviseDAO.getPayableReserve(receiptSeqID,
								payAgainstID);
						if (payableReserve != null) {

							// Update Reserve Amount in ManualAdvise
							manualAdviseDAO.updatePayableReserve(payAgainstID,
									payableReserve.getReservedAmt().negate());

							// Delete Reserved Log against Payable Advise ID and Receipt ID
							manualAdviseDAO.deletePayableReserve(receiptSeqID, payAgainstID);
						}
					}
				}
			}

			// Manual Advise Movements
			if (isApproval) {
				for (ManualAdviseMovements movement : rcd.getAdvMovements()) {
					if (movement.getTaxHeader() != null
							&& CollectionUtils.isNotEmpty(movement.getTaxHeader().getTaxDetails())) {
						List<Taxes> taxDetails = movement.getTaxHeader().getTaxDetails();
						Long headerId = taxHeaderDetailsDAO.save(movement.getTaxHeader(),
								TableType.MAIN_TAB.getSuffix());
						for (Taxes taxes : taxDetails) {
							taxes.setReferenceId(headerId);
						}
						taxHeaderDetailsDAO.saveTaxes(taxDetails, TableType.MAIN_TAB.getSuffix());
						movement.setTaxHeaderId(headerId);
					}
					movement.setReceiptID(receiptID);
					movement.setReceiptSeqID(receiptSeqID);
					manualAdviseDAO.saveMovement(movement, TableType.MAIN_TAB.getSuffix());
				}
			}

			FinRepayHeader rph = rcd.getRepayHeader();
			rph.setReceiptSeqID(receiptSeqID);
			rph.setValueDate(SysParamUtil.getAppValueDate());
			rph.setFinReference(rch.getReference());
			rph.setFinEvent(rch.getReceiptPurpose());
			if (rph.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {
				int recordCount = 0;
				if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_DEPOSITED)) {
					recordCount = finExcessAmountDAO.updateExcessReserveByRef(rch.getReference(),
							rch.getExcessAdjustTo(), rph.getExcessAmount());
				} else {
					recordCount = finExcessAmountDAO.updateExcessBalByRef(rch.getReference(), rch.getExcessAdjustTo(),
							rph.getExcessAmount());
				}
				// If record Not found then record count should be zero. Need to create new Excess Record
				if (recordCount <= 0) {
					FinExcessAmount excess = new FinExcessAmount();
					excess.setFinReference(rch.getReference());
					excess.setAmountType(rch.getExcessAdjustTo());
					excess.setAmount(rph.getExcessAmount());
					excess.setUtilisedAmt(BigDecimal.ZERO);
					excess.setBalanceAmt(rph.getExcessAmount());
					excess.setReservedAmt(BigDecimal.ZERO);
					if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_DEPOSITED)) {
						excess.setBalanceAmt(BigDecimal.ZERO);
						excess.setReservedAmt(rph.getExcessAmount());
						excess.setAmount(rph.getExcessAmount());

					}
					finExcessAmountDAO.saveExcess(excess);
				}
			}

			// Save Repay Header details
			long repayID = financeRepaymentsDAO.saveFinRepayHeader(rph, TableType.MAIN_TAB);

			List<RepayScheduleDetail> rpySchdList = rph.getRepayScheduleDetails();
			if (CollectionUtils.isNotEmpty(rpySchdList)) {
				for (int i = 0; i < rpySchdList.size(); i++) {

					RepayScheduleDetail rpySchd = rpySchdList.get(i);
					rpySchd.setFinReference(rch.getReference());
					rpySchd.setRepayID(repayID);
					rpySchd.setRepaySchID(i + 1);
					rpySchd.setLinkedTranId(rph.getLinkedTranId());

					if (rpySchd.getTaxHeader() != null
							&& CollectionUtils.isNotEmpty(rpySchd.getTaxHeader().getTaxDetails())) {
						List<Taxes> taxDetails = rpySchd.getTaxHeader().getTaxDetails();
						Long headerId = taxHeaderDetailsDAO.save(rpySchd.getTaxHeader(),
								TableType.MAIN_TAB.getSuffix());
						for (Taxes taxes : taxDetails) {
							taxes.setReferenceId(headerId);
						}

						taxHeaderDetailsDAO.saveTaxes(taxDetails, TableType.MAIN_TAB.getSuffix());
						rpySchd.setTaxHeaderId(headerId);
					}

					if (isApproval) {
						// update fee schedule details
						updateFeeDetails(rpySchd, finFeeDetails, allocationPaidMap, allocationWaivedMap);

						// update insurance schedule details
						updateInsuranceDetails(rpySchd);
					}
				}

				// Save Repayment Schedule Details
				financeRepaymentsDAO.saveRpySchdList(rpySchdList, TableType.MAIN_TAB);
			}
		}

		allocationPaidMap = null;
		allocationWaivedMap = null;
		logger.debug("Leaving");
	}

	/**
	 * Method for Sorting Receipt Details From Receipts
	 * 
	 * @param rcdList
	 * @return
	 */
	private List<FinReceiptDetail> sortReceiptDetails(List<FinReceiptDetail> rcdList) {

		if (rcdList != null && !rcdList.isEmpty()) {
			Collections.sort(rcdList, new Comparator<FinReceiptDetail>() {
				@Override
				public int compare(FinReceiptDetail detail1, FinReceiptDetail detail2) {
					int payOrder1 = detail1.getPayOrder();
					int payOrder2 = detail2.getPayOrder();
					if (payOrder1 > payOrder2) {
						return 1;
					} else if (payOrder1 < payOrder2) {
						return -1;
					}
					return 0;
				}
			});
		}
		return rcdList;
	}

	private void updateInsuranceDetails(RepayScheduleDetail rpySchd) {
		logger.debug("Entering");

		BigDecimal remBalPaidAmount = rpySchd.getSchdInsPayNow();
		if (remBalPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		List<FinSchFrqInsurance> updateInsList = new ArrayList<>();
		List<FinSchFrqInsurance> list = finInsurancesDAO.getInsScheduleBySchDate(rpySchd.getFinReference(),
				rpySchd.getSchDate());

		for (FinSchFrqInsurance insSchd : list) {

			if (remBalPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
			BigDecimal insBal = insSchd.getAmount().subtract(insSchd.getInsurancePaid())
					.subtract(insSchd.getInsuranceWaived());
			if (insBal.compareTo(remBalPaidAmount) > 0) {
				insBal = remBalPaidAmount;
			}
			insSchd.setInsurancePaid(insSchd.getInsurancePaid().add(insBal));
			updateInsList.add(insSchd);
			remBalPaidAmount = remBalPaidAmount.subtract(insBal);

		}

		if (!updateInsList.isEmpty()) {
			finInsurancesDAO.updateInsPaids(updateInsList);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for updating Fee Schedule Details based on Allocations and Paid Amounts
	 * 
	 * @param rpySchd
	 * @param allocationPaidMap
	 * @param allocationWaivedMap
	 */
	private void updateFeeDetails(RepayScheduleDetail rpySchd, List<FinFeeDetail> finFeeDetails,
			Map<String, BigDecimal> allocationPaidMap, Map<String, BigDecimal> allocationWaivedMap) {
		logger.debug("Entering");

		BigDecimal paidBal = rpySchd.getSchdFeePayNow();
		BigDecimal waivedBal = rpySchd.getSchdFeeWaivedNow();

		if (paidBal.compareTo(BigDecimal.ZERO) == 0 && waivedBal.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		// If Process for EOD , Fees should be updated immediately
		List<FinFeeScheduleDetail> updateFeeList = new ArrayList<>();
		List<FinFeeScheduleDetail> list = new ArrayList<>();
		if (finFeeDetails == null) {
			list = finFeeScheduleDetailDAO.getFeeSchedules(rpySchd.getFinReference(), rpySchd.getSchDate());

			// Schedule Fee Updation
			for (FinFeeScheduleDetail feeSchd : list) {
				feeSchd = feeSchdUpdation(feeSchd, paidBal, waivedBal, allocationPaidMap, allocationWaivedMap);
				if (feeSchd != null) {
					updateFeeList.add(feeSchd);
				}

			}
		} else {
			for (FinFeeDetail fee : finFeeDetails) {

				if (StringUtils.equals(fee.getStatus(), FinanceConstants.FEE_STATUS_CANCEL)) {
					continue;
				}
				if (fee.getFinFeeScheduleDetailList() != null && !fee.getFinFeeScheduleDetailList().isEmpty()) {
					for (FinFeeScheduleDetail feeSchd : fee.getFinFeeScheduleDetailList()) {
						if (DateUtility.compare(feeSchd.getSchDate(), rpySchd.getSchDate()) == 0) {
							feeSchdUpdation(feeSchd, paidBal, waivedBal, allocationPaidMap, allocationWaivedMap);
						}
					}
				}
			}
		}

		if (!updateFeeList.isEmpty()) {
			finFeeScheduleDetailDAO.updateFeePaids(updateFeeList);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Fee Schedule Updation absed on Repayment Schedule date selection
	 */
	private FinFeeScheduleDetail feeSchdUpdation(FinFeeScheduleDetail feeSchd, BigDecimal paidBal, BigDecimal waivedBal,
			Map<String, BigDecimal> allocationPaidMap, Map<String, BigDecimal> allocationWaivedMap) {
		logger.debug("Entering");

		// No balance to adjust, should return back
		if (paidBal.compareTo(BigDecimal.ZERO) == 0 && waivedBal.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}

		BigDecimal feeBal = feeSchd.getSchAmount()
				.subtract(feeSchd.getPaidAmount().subtract(feeSchd.getWaiverAmount()));
		if (feeBal.compareTo(paidBal) > 0) {
			feeBal = paidBal;
		}

		// If allocation map is present then Paid adjustment based on Allocations only
		if (allocationPaidMap != null) {
			if (allocationPaidMap.containsKey(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID())) {
				BigDecimal remPaidBal = allocationPaidMap.get(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID());
				if (feeBal.compareTo(remPaidBal) > 0) {
					feeBal = remPaidBal;
				}
			} else {
				feeBal = BigDecimal.ZERO;
			}
		}

		if (paidBal.compareTo(BigDecimal.ZERO) == 0) {
			if (feeBal.compareTo(waivedBal) > 0) {
				feeBal = waivedBal;
			}

			// If allocation map is present then Waived adjustment based on Allocations only
			if (allocationWaivedMap != null) {
				if (allocationWaivedMap.containsKey(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID())) {
					BigDecimal remWaivedBal = allocationWaivedMap
							.get(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID());
					if (feeBal.compareTo(remWaivedBal) > 0) {
						feeBal = remWaivedBal;
					}
				} else {
					feeBal = BigDecimal.ZERO;
				}
			}

			// If Fees not allocated on paids then No updates
			if (feeBal.compareTo(BigDecimal.ZERO) == 0) {
				return null;
			}

			feeSchd.setWaiverAmount(feeSchd.getWaiverAmount().add(feeBal));
			waivedBal = waivedBal.subtract(feeBal);

			// Allocation map Balance adjustment after Collection(Paid/waived)
			if (allocationWaivedMap != null) {
				if (allocationWaivedMap.containsKey(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID())) {
					BigDecimal remWaivedBal = allocationWaivedMap
							.get(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID());
					allocationWaivedMap.put(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID(),
							remWaivedBal.subtract(feeBal));
				}
			}

		} else {

			// If Fees not allocated on paids then No updates
			if (feeBal.compareTo(BigDecimal.ZERO) == 0) {
				return null;
			}

			feeSchd.setPaidAmount(feeSchd.getPaidAmount().add(feeBal));
			paidBal = paidBal.subtract(feeBal);

			// Allocation map Balance adjustment after Collection(Paid/waived)
			if (allocationPaidMap != null) {
				if (allocationPaidMap.containsKey(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID())) {
					BigDecimal remPaidBal = allocationPaidMap
							.get(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID());
					allocationPaidMap.put(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID(),
							remPaidBal.subtract(feeBal));
				}
			}
		}

		feeSchd.setOsAmount(
				feeSchd.getSchAmount().subtract(feeSchd.getPaidAmount()).subtract(feeSchd.getWaiverAmount()));

		logger.debug("Leaving");
		return feeSchd;
	}

	/**
	 * Method for Status updation on Finance
	 * 
	 * @param financeMain
	 * @param valueDate
	 * @param scheduleDetails
	 * @param profitDetail
	 * @return
	 * @throws Exception
	 */
	public FinanceMain updateStatus(FinanceMain financeMain, Date valueDate,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail profitDetail,
			List<FinODDetails> overdueList, String receiptPurpose, boolean isPresentProc) throws Exception {
		return repayPostingUtil.updateStatus(financeMain, valueDate, scheduleDetails, profitDetail, overdueList,
				receiptPurpose, isPresentProc);
	}

	private List<Object> doRepayPostings(FinanceDetail financeDetail, FinReceiptHeader rch,
			Map<String, BigDecimal> extDataMap, Map<String, Object> gstExecutionMap, Date postDate, long repayID)
			throws IllegalAccessException, InterfaceException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		List<Object> returnList = new ArrayList<Object>();
		List<FinRepayQueue> finRepayQueues = new ArrayList<>();
		FinRepayQueue finRepayQueue = null;
		FinRepayQueueHeader rpyQueueHeader = new FinRepayQueueHeader();

		FinScheduleData fsd = financeDetail.getFinScheduleData();
		FinanceMain fm = fsd.getFinanceMain();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		rcdList = sortReceiptDetails(rcdList);
		FinReceiptDetail rcd = rcdList.get(rcdList.size() - 1);
		List<RepayScheduleDetail> rsdList = new ArrayList<>(1);

		for (FinReceiptDetail rcdTemp : rcdList) {
			FinRepayHeader repayHeader = rcdTemp.getRepayHeader();
			if (repayHeader == null) {
				continue;
			}

			List<RepayScheduleDetail> repayScheduleDetails = repayHeader.getRepayScheduleDetails();
			if (CollectionUtils.isNotEmpty(repayScheduleDetails)) {
				rsdList.addAll(repayScheduleDetails);
			}
		}

		try {
			List<RepayScheduleDetail> tempRpySchdList = new ArrayList<>();
			for (RepayScheduleDetail rsd : rsdList) {
				tempRpySchdList.add(rsd.copyEntity());
			}

			Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();

			for (RepayScheduleDetail rpySchd : tempRpySchdList) {
				RepayScheduleDetail curRpySchd = null;

				if (rpySchdMap.containsKey(rpySchd.getSchDate())) {
					curRpySchd = rpySchdMap.get(rpySchd.getSchDate());

					if (curRpySchd.getPrincipalSchdBal().compareTo(rpySchd.getPrincipalSchdBal()) < 0) {
						curRpySchd.setPrincipalSchdBal(rpySchd.getPrincipalSchdBal());
					}

					if (curRpySchd.getProfitSchdBal().compareTo(rpySchd.getProfitSchdBal()) < 0) {
						curRpySchd.setProfitSchdBal(rpySchd.getProfitSchdBal());
					}

					curRpySchd.setPrincipalSchdPayNow(
							curRpySchd.getPrincipalSchdPayNow().add(rpySchd.getPrincipalSchdPayNow()));
					curRpySchd.setProfitSchdPayNow(curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
					curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
					curRpySchd.setLatePftSchdPayNow(
							curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));

					curRpySchd.setSchdFeePayNow(curRpySchd.getSchdFeePayNow().add(rpySchd.getSchdFeePayNow()));
					curRpySchd.setSchdInsPayNow(curRpySchd.getSchdInsPayNow().add(rpySchd.getSchdInsPayNow()));
					curRpySchd.setPenaltyPayNow(curRpySchd.getPenaltyPayNow().add(rpySchd.getPenaltyPayNow()));

					rpySchdMap.remove(rpySchd.getSchDate());
				} else {
					curRpySchd = rpySchd;
				}

				// Adding New Repay Schedule Object to Map after Summing
				// data
				rpySchdMap.put(rpySchd.getSchDate(), curRpySchd);
			}

			rsdList = sortRpySchdDetails(new ArrayList<>(rpySchdMap.values()));

			for (int i = 0; i < rsdList.size(); i++) {
				finRepayQueue = new FinRepayQueue();

				finRepayQueue.setFinReference(fm.getFinReference());
				finRepayQueue.setRpyDate(rsdList.get(i).getSchDate());
				finRepayQueue.setFinRpyFor(rsdList.get(i).getSchdFor());
				finRepayQueue.setRcdNotExist(true);
				finRepayQueue = doWriteDataToBean(finRepayQueue, fm, rsdList.get(i));

				finRepayQueue.setRefundAmount(rsdList.get(i).getRefundReq());
				finRepayQueue.setPenaltyPayNow(rsdList.get(i).getPenaltyPayNow());
				finRepayQueue.setWaivedAmount(rsdList.get(i).getWaivedAmt());
				finRepayQueue.setPenaltyBal(rsdList.get(i).getPenaltyAmt().subtract(rsdList.get(i).getPenaltyPayNow()));
				finRepayQueue.setChargeType(rsdList.get(i).getChargeType());

				// Tax Header Setting
				finRepayQueue.setTaxHeader(rsdList.get(i).getTaxHeader());
				finRepayQueues.add(finRepayQueue);
			}
			BigDecimal totRecvAmount = BigDecimal.ZERO;
			List<ReceiptAllocationDetail> radList = rch.getAllocations();

			for (ReceiptAllocationDetail rad : radList) {
				String allocType = rad.getAllocationType();
				BigDecimal paidNow = rad.getPaidAmount();
				BigDecimal waivedNow = rad.getWaivedAmount();

				switch (allocType) {
				case RepayConstants.ALLOCATION_EMI:
					break;
				case RepayConstants.ALLOCATION_PRI:
					rpyQueueHeader.setPrincipal(rpyQueueHeader.getPrincipal().add(paidNow));
					rpyQueueHeader.setPriWaived(rpyQueueHeader.getPriWaived().add(waivedNow));
					break;
				case RepayConstants.ALLOCATION_FUT_PRI:
					rpyQueueHeader.setPrincipal(rpyQueueHeader.getPrincipal().add(paidNow));
					rpyQueueHeader.setPriWaived(rpyQueueHeader.getPriWaived().add(waivedNow));

					rpyQueueHeader.setFutPrincipal(paidNow);
					rpyQueueHeader.setFutPriWaived(waivedNow);

					break;
				case RepayConstants.ALLOCATION_PP:
					rpyQueueHeader.setPrincipal(rpyQueueHeader.getPrincipal().add(paidNow));
					rpyQueueHeader.setPriWaived(rpyQueueHeader.getPriWaived().add(waivedNow));

					rpyQueueHeader.setPartialPaid(paidNow);

					break;
				case RepayConstants.ALLOCATION_PFT:
					rpyQueueHeader.setProfit(rpyQueueHeader.getProfit().add(paidNow));
					rpyQueueHeader.setPftWaived(rpyQueueHeader.getPftWaived().add(waivedNow));
					rpyQueueHeader.setTds(rpyQueueHeader.getTds().add(rad.getTdsPaid()));
					break;
				case RepayConstants.ALLOCATION_FUT_PFT:
					rpyQueueHeader.setProfit(rpyQueueHeader.getProfit().add(paidNow));
					rpyQueueHeader.setPftWaived(rpyQueueHeader.getPftWaived().add(waivedNow));
					rpyQueueHeader.setTds(rpyQueueHeader.getTds().add(rad.getTdsPaid()));

					rpyQueueHeader.setFutTds(rad.getTdsPaid());
					rpyQueueHeader.setFutProfit(paidNow);
					break;
				case RepayConstants.ALLOCATION_LPFT:
					rpyQueueHeader.setLateProfit(rpyQueueHeader.getLateProfit().add(paidNow));
					rpyQueueHeader.setLatePftWaived(rpyQueueHeader.getLatePftWaived().add(waivedNow));
					rpyQueueHeader.setFeeTds(rpyQueueHeader.getFeeTds().add(rad.getTdsPaid()));
					break;
				case RepayConstants.ALLOCATION_ODC:
					rpyQueueHeader.setPenalty(rpyQueueHeader.getPenalty().add(paidNow));
					rpyQueueHeader.setPenaltyWaived(rpyQueueHeader.getPenaltyWaived().add(waivedNow));
					extDataMap.put("LPP_TDS_P", rad.getTdsPaid());
					extDataMap.put("LPP_TDS_W", rad.getTdsWaived());
					break;
				case RepayConstants.ALLOCATION_FEE:
					rpyQueueHeader.setFee(rpyQueueHeader.getFee().add(paidNow));
					rpyQueueHeader.setFeeWaived(rpyQueueHeader.getFeeWaived().add(waivedNow));
					break;
				case RepayConstants.ALLOCATION_INS:
					rpyQueueHeader.setInsurance(rpyQueueHeader.getInsurance().add(paidNow));
					rpyQueueHeader.setInsWaived(rpyQueueHeader.getInsWaived().add(waivedNow));
					break;
				case RepayConstants.ALLOCATION_MANADV:
				case RepayConstants.ALLOCATION_BOUNCE:
					rpyQueueHeader.setAdviseAmount(rpyQueueHeader.getAdviseAmount().add(paidNow).add(waivedNow));
					break;
				default:
					break;
				}

				if (!RepayConstants.ALLOCATION_EMI.equals(allocType)
						&& RepayConstants.ALLOCATION_PP.equals(allocType)) {
					totRecvAmount = totRecvAmount.add(paidNow);
				}
			}

			BigDecimal receiptAmount = rch.getReceiptAmount();
			if (FinanceConstants.FINSER_EVENT_EARLYRPY.equals(rch.getReceiptPurpose())
					&& receiptAmount.compareTo(totRecvAmount) > 0) {
				rpyQueueHeader.setFutPrincipal(receiptAmount.subtract(totRecvAmount));
			}

			// Repayments Process For Schedule Repay List
			rpyQueueHeader.setQueueList(finRepayQueues);
			rpyQueueHeader.setPayType(rcd.getPaymentType());
			rpyQueueHeader.setPostBranch(rch.getPostBranch());
			rpyQueueHeader.setCashierBranch(rch.getCashierBranch());
			rpyQueueHeader.setPartnerBankAc(rcd.getPartnerBankAc());
			rpyQueueHeader.setPartnerBankAcType(rcd.getPartnerBankAcType());
			rpyQueueHeader.setPftChgAccReq(true);
			rpyQueueHeader.setExtDataMap(extDataMap);
			rpyQueueHeader.setGstExecutionMap(gstExecutionMap);
			rpyQueueHeader.setReceiptId(rch.getReceiptID());
			rpyQueueHeader.setRepayID(repayID);

			// Cash Transaction payment Type Verification
			boolean isStageAccExecuted = false;
			if (FinanceConstants.PRODUCT_GOLD.equals(fm.getProductCategory()) && rcd.getReceiptID() > 0) {
				if (finStageAccountingLogDAO.getTranCountByReceiptNo(rcd.getPaymentRef()) > 0) {
					isStageAccExecuted = true;
				}
			}
			rpyQueueHeader.setStageAccExecuted(isStageAccExecuted);

			List<FinanceScheduleDetail> scheduleDetails = fsd.getFinanceScheduleDetails();
			List<FinFeeDetail> finFeeDetailList = financeDetail.getFinFeeDetails();
			FinanceProfitDetail profitDetail = fsd.getFinPftDeatil();
			String eventCode = getEventCode(rch.getReceiptPurpose(), rch.getReceiptMode());

			returnList = repayPostingUtil.postingProcess(fm, scheduleDetails, finFeeDetailList, profitDetail,
					rpyQueueHeader, eventCode, rch.getValueDate(), postDate);

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug(Literal.LEAVING);
		return returnList;
	}

	public List<RepayScheduleDetail> sortRpySchdDetails(List<RepayScheduleDetail> repayScheduleDetails) {
		if (CollectionUtils.isEmpty(repayScheduleDetails)) {
			return repayScheduleDetails;
		}

		Collections.sort(repayScheduleDetails, new Comparator<RepayScheduleDetail>() {
			@Override
			public int compare(RepayScheduleDetail rsd1, RepayScheduleDetail rsd2) {
				return DateUtil.compare(rsd1.getSchDate(), rsd2.getSchDate());
			}
		});

		return repayScheduleDetails;
	}

	/**
	 * Method for prepare RepayQueue data
	 * 
	 * @param resultSet
	 * @return
	 */
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, FinanceMain financeMain,
			RepayScheduleDetail rsd) {
		logger.debug("Entering");

		finRepayQueue.setBranch(financeMain.getFinBranch());
		finRepayQueue.setFinType(financeMain.getFinType());
		finRepayQueue.setCustomerID(financeMain.getCustID());
		finRepayQueue.setFinPriority(9999);

		// Principal Amount
		finRepayQueue.setSchdPft(rsd.getProfitSchd());
		finRepayQueue.setSchdPftPaid(rsd.getProfitSchdPaid());
		finRepayQueue.setSchdPftBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
		finRepayQueue.setSchdPftPayNow(rsd.getProfitSchdPayNow());
		finRepayQueue.setSchdPftWaivedNow(rsd.getPftSchdWaivedNow());
		finRepayQueue.setSchdTdsPayNow(rsd.getTdsSchdPayNow());

		// Profit Amount
		finRepayQueue.setSchdPri(rsd.getPrincipalSchd());
		finRepayQueue.setSchdPriBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
		finRepayQueue.setSchdPriPaid(rsd.getPrincipalSchdPaid());
		finRepayQueue.setSchdPriPayNow(rsd.getPrincipalSchdPayNow());
		finRepayQueue.setSchdPriWaivedNow(rsd.getPriSchdWaivedNow());

		// Late Pay Profit Amount
		finRepayQueue.setLatePayPftPayNow(rsd.getLatePftSchdPayNow());
		finRepayQueue.setLatePayPftWaivedNow(rsd.getLatePftSchdWaivedNow());

		// Fee Details
		// 1. Schedule Fee Amount
		finRepayQueue.setSchdFee(rsd.getSchdFee());
		finRepayQueue.setSchdFeeBal(rsd.getSchdFeeBal());
		finRepayQueue.setSchdFeePayNow(rsd.getSchdFeePayNow());
		finRepayQueue.setSchdFeePaid(rsd.getSchdFeePaid());
		finRepayQueue.setSchdFeeWaivedNow(rsd.getSchdFeeWaivedNow());

		// 2. Schedule Insurance Amount
		finRepayQueue.setSchdIns(rsd.getSchdIns());
		finRepayQueue.setSchdInsBal(rsd.getSchdInsBal());
		finRepayQueue.setSchdInsPayNow(rsd.getSchdInsPayNow());
		finRepayQueue.setSchdInsPaid(rsd.getSchdInsPaid());
		finRepayQueue.setSchdInsWaivedNow(rsd.getSchdInsWaivedNow());

		// 3. Schedule Supplementary Rent Amount
		finRepayQueue.setSchdSuplRent(rsd.getSchdSuplRent());
		finRepayQueue.setSchdSuplRentBal(rsd.getSchdSuplRentBal());
		finRepayQueue.setSchdSuplRentPayNow(rsd.getSchdSuplRentPayNow());
		finRepayQueue.setSchdSuplRentPaid(rsd.getSchdSuplRentPaid());
		finRepayQueue.setSchdSuplRentWaivedNow(rsd.getSchdSuplRentWaivedNow());

		// 4. Schedule Increased Cost Amount
		finRepayQueue.setSchdIncrCost(rsd.getSchdIncrCost());
		finRepayQueue.setSchdIncrCostBal(rsd.getSchdIncrCostBal());
		finRepayQueue.setSchdIncrCostPayNow(rsd.getSchdIncrCostPayNow());
		finRepayQueue.setSchdIncrCostPaid(rsd.getSchdIncrCostPaid());
		finRepayQueue.setSchdIncrCostWaivedNow(rsd.getSchdIncrCostWaivedNow());

		logger.debug("Leaving");
		return finRepayQueue;
	}

	/**
	 * Method for Preparation of Repayment Schedule Details
	 * 
	 * @param curSchd
	 * @param rsd
	 * @param rpyTo
	 * @param balPayNow
	 * @return
	 */
	private RepayScheduleDetail prepareRpyRecord(FinanceScheduleDetail curSchd, RepayScheduleDetail rsd, char rpyTo,
			BigDecimal balPayNow, Date valueDate) {
		logger.debug("Entering");

		if (rsd == null) {
			rsd = new RepayScheduleDetail();
			rsd.setFinReference(curSchd.getFinReference());
			rsd.setSchDate(curSchd.getSchDate());
			rsd.setDefSchdDate(curSchd.getSchDate());

			rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			rsd.setProfitSchd(curSchd.getProfitSchd());
			rsd.setProfitSchdPaid(curSchd.getSchdPftPaid());
			rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));

			rsd.setPrincipalSchd(curSchd.getPrincipalSchd());
			rsd.setPrincipalSchdPaid(curSchd.getSchdPriPaid());
			rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));

			rsd.setSchdIns(curSchd.getInsSchd());
			rsd.setSchdInsPaid(curSchd.getSchdInsPaid());
			rsd.setSchdInsBal(rsd.getSchdIns().subtract(rsd.getSchdInsPaid()));

			rsd.setSchdSuplRent(curSchd.getSuplRent());
			rsd.setSchdSuplRentPaid(curSchd.getSuplRentPaid());
			rsd.setSchdSuplRentBal(rsd.getSchdSuplRent().subtract(rsd.getSchdSuplRentPaid()));

			rsd.setSchdIncrCost(curSchd.getIncrCost());
			rsd.setSchdIncrCostPaid(curSchd.getIncrCostPaid());
			rsd.setSchdIncrCostBal(rsd.getSchdIncrCost().subtract(rsd.getSchdIncrCostPaid()));

			rsd.setSchdFee(curSchd.getFeeSchd());
			rsd.setSchdFeePaid(curSchd.getSchdFeePaid());
			rsd.setSchdFeeBal(rsd.getSchdFee().subtract(rsd.getSchdFeePaid()));

			rsd.setDaysLate(DateUtility.getDaysBetween(curSchd.getSchDate(), valueDate));
			rsd.setDaysEarly(0);
		}

		// Principal Payment
		if (rpyTo == RepayConstants.REPAY_PRINCIPAL) {
			rsd.setPrincipalSchdPayNow(balPayNow);
		}

		// Profit Payment
		if (rpyTo == RepayConstants.REPAY_PROFIT) {
			rsd.setProfitSchdPayNow(balPayNow);
		}

		// Late Payment Profit Payment
		if (rpyTo == RepayConstants.REPAY_LATEPAY_PROFIT) {
			rsd.setLatePftSchdPayNow(balPayNow);
		}

		// Fee Detail Payment
		if (rpyTo == RepayConstants.REPAY_FEE) {
			rsd.setSchdFeePayNow(balPayNow);
		}

		// TDS Payment
		if (rpyTo == RepayConstants.REPAY_TDS) {
			rsd.setTdsSchdPayNow(balPayNow);
		}

		// Insurance Detail Payment
		if (rpyTo == RepayConstants.REPAY_INS) {
			rsd.setSchdInsPayNow(balPayNow);
		}

		// Penalty Charge Detail Payment
		if (rpyTo == RepayConstants.REPAY_PENALTY) {
			rsd.setPenaltyPayNow(balPayNow);
		}

		logger.debug("Leaving");
		return rsd;

	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 **/
	public FinScheduleData getFinSchDataByFinRef(String finReference, String type) {
		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finReference, type, false));
		finSchData.setDisbursementDetails(
				financeDisbursementDAO.getFinanceDisbursementDetails(finReference, type, false));
		finSchData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finReference, type, false));

		return finSchData;
	}

	public void listSave(FinScheduleData scheduleData, String tableType, long logKey) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < scheduleData.getFinanceScheduleDetails().size(); i++) {

			FinanceScheduleDetail curSchd = scheduleData.getFinanceScheduleDetails().get(i);
			curSchd.setLastMntBy(scheduleData.getFinanceMain().getLastMntBy());
			curSchd.setFinReference(scheduleData.getFinReference());
			int seqNo = 0;

			if (mapDateSeq.containsKey(curSchd.getSchDate())) {
				seqNo = mapDateSeq.get(curSchd.getSchDate());
				mapDateSeq.remove(curSchd.getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(curSchd.getSchDate(), seqNo);
			curSchd.setSchSeq(seqNo);
			curSchd.setLogKey(logKey);
		}

		financeScheduleDetailDAO.saveList(scheduleData.getFinanceScheduleDetails(), tableType, false);

		if (logKey != 0) {
			// Finance Disbursement Details
			mapDateSeq = new HashMap<Date, Integer>();
			Date curBDay = SysParamUtil.getAppDate();

			for (FinanceDisbursement disbursement : scheduleData.getDisbursementDetails()) {
				disbursement.setFinReference(scheduleData.getFinReference());
				disbursement.setDisbReqDate(curBDay);
				disbursement.setDisbIsActive(true);
				disbursement.setDisbDisbursed(true);
				disbursement.setLogKey(logKey);
			}

			financeDisbursementDAO.saveList(scheduleData.getDisbursementDetails(), tableType, false);

		}

		// Finance Repay Instruction Details
		if (scheduleData.getRepayInstructions() != null) {
			for (int i = 0; i < scheduleData.getRepayInstructions().size(); i++) {
				RepayInstruction curSchd = scheduleData.getRepayInstructions().get(i);

				curSchd.setFinReference(scheduleData.getFinReference());
				curSchd.setLogKey(logKey);
			}
			repayInstructionDAO.saveList(scheduleData.getRepayInstructions(), tableType, false);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method for Fetching Accounting Event Code based on Finance Event Action
	 * 
	 * @param finEvent
	 * @return
	 */
	private String getEventCode(String finEvent, String receiptMode) {
		switch (finEvent) {
		case FinanceConstants.FINSER_EVENT_SCHDRPY:
			if (ImplementationConstants.ALLOW_PRESENTMENT_STAGE_ACCOUNTING) {
				if (RepayConstants.RECEIPTMODE_PRESENTMENT.equals(receiptMode)) {
					return AccountEventConstants.ACCEVENT_PRSNT;
				}
			}
			return AccountEventConstants.ACCEVENT_REPAY;
		case FinanceConstants.FINSER_EVENT_EARLYRPY:
			return AccountEventConstants.ACCEVENT_EARLYPAY;
		case FinanceConstants.FINSER_EVENT_EARLYSETTLE:
			return AccountEventConstants.ACCEVENT_EARLYSTL;
		default:
			return "";
		}
	}

	private Date getPostDate(Date appDate) {
		Calendar cal = Calendar.getInstance();
		Calendar appCal = Calendar.getInstance();
		cal.setTime(DateUtil.getSysDate());
		appCal.setTime(appDate);
		cal.set(Calendar.YEAR, appCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, appCal.get(Calendar.MONTH));
		cal.set(Calendar.DATE, appCal.get(Calendar.DATE));
		return cal.getTime();
	}

	/**
	 * Method for Sorting Schedule Details
	 * 
	 * @param financeScheduleDetail
	 * @return
	 */
	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	private void addZeroifNotContains(Map<String, BigDecimal> dataMap, String key) {
		if (dataMap != null) {
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, BigDecimal.ZERO);
			}
		}
	}

	public void processAutoKnockOff(FinReceiptData receiptData) throws Exception {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain fm = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> schdDtls = scheduleData.getFinanceScheduleDetails();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinanceProfitDetail profitDetail = scheduleData.getFinPftDeatil();
		EventProperties eventProperties = fm.getEventProperties();

		Date appDate = null;
		if (eventProperties.isParameterLoaded()) {
			appDate = eventProperties.getAppDate();
		} else {
			appDate = SysParamUtil.getAppDate();
		}

		finReceiptHeaderDAO.generatedReceiptID(rch);

		if (rch.getReceiptDetails() != null && !rch.getReceiptDetails().isEmpty()) {
			for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
				FinReceiptDetail receiptDetail = rch.getReceiptDetails().get(i);
				receiptDetail.getRepayHeader().setRepayID(financeRepaymentsDAO.getNewRepayID());
			}
		}

		List<Object> returnList = doProcessReceipts(fm, schdDtls, profitDetail, rch, null, scheduleData,
				rch.getValueDate(), appDate, financeDetail);

		@SuppressWarnings("unchecked")
		List<FinanceScheduleDetail> scheduleDetails = (List<FinanceScheduleDetail>) returnList.get(0);
		scheduleData.setFinanceScheduleDetails(scheduleDetails);

		BigDecimal priPaynow = BigDecimal.ZERO;

		for (ReceiptAllocationDetail allocate : rch.getAllocations()) {
			if (RepayConstants.ALLOCATION_PRI.equals(allocate.getAllocationType())) {
				priPaynow = allocate.getPaidAmount();
				break;
			}
		}

		// Preparing Total Principal Amount
		BigDecimal totPriPaid = BigDecimal.ZERO;
		for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
			if (rcd.getRepayHeader() == null) {
				continue;
			}

			FinRepayHeader repayHeader = rcd.getRepayHeader();
			List<RepayScheduleDetail> repayScheduleDetails = repayHeader.getRepayScheduleDetails();

			if (CollectionUtils.isNotEmpty(repayScheduleDetails)) {
				for (RepayScheduleDetail rpySchd : repayScheduleDetails) {
					totPriPaid = totPriPaid.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
				}
			}
		}

		fm.setFinRepaymentAmount(fm.getFinRepaymentAmount().add(totPriPaid));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();
		for (FinanceScheduleDetail fsd : scheduleDetails) {
			Date schdDate = fsd.getSchDate();
			if (schdDate.compareTo(rch.getValueDate()) > 0) {
				break;
			}

			schedules.add(fsd);
			if (schedules.size() > 100) {
				financeScheduleDetailDAO.updateListForRpy(schedules);
				schedules.clear();
			}
		}

		if (CollectionUtils.isNotEmpty(schedules)) {
			financeScheduleDetailDAO.updateListForRpy(schedules);
		}

		doSaveReceipts(rch, null, true);

		Date reqMaxODDate = appDate;
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, rch.getReceiptPurpose())) {
			reqMaxODDate = rch.getValueDate();
		}
		if (!ImplementationConstants.LPP_CALC_SOD) {
			reqMaxODDate = DateUtility.addDays(reqMaxODDate, -1);
		}
		List<FinODDetails> overdueList = finODDetailsDAO.getFinODBalByFinRef(fm.getFinReference());
		if (CollectionUtils.isNotEmpty(overdueList)) {
			overdueList = receiptCalculator.calPenalty(scheduleData, receiptData, reqMaxODDate, overdueList);
			finODDetailsDAO.updateList(overdueList);
		}

		fm = updateStatus(fm, appDate, scheduleDetails, profitDetail, overdueList, rch.getReceiptPurpose(), false);

		financeMainDAO.updatePaymentInEOD(fm);
		limitManagement.processLoanRepay(fm, customerDetails.getCustomer(), priPaynow, profitDetail.getFinCategory());
	}

	public void processSuccessPresentment(long receiptId) {
		FinReceiptHeader rch = finReceiptHeaderDAO.getReceiptHeaderByID(receiptId, "");
		if (rch == null) {
			return;
		}
		String reference = rch.getReference();
		String excessAdjustTo = rch.getExcessAdjustTo();
		List<FinReceiptDetail> rcdDtls = finReceiptDetailDAO.getReceiptHeaderByID(receiptId, "");

		for (FinReceiptDetail recDtl : rcdDtls) {
			FinRepayHeader rph = financeRepaymentsDAO.getFinRepayHeadersByReceipt(recDtl.getReceiptSeqID(), "");
			// updating fixexcess amount after realization
			if (FinanceConstants.FINSER_EVENT_SCHDRPY.equals(rch.getReceiptPurpose())) {
				if (rph != null && rph.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {
					finExcessAmountDAO.updExcessAfterRealize(reference, excessAdjustTo, rph.getExcessAmount());
				}
			}
		}
		finReceiptHeaderDAO.updateReceiptStatusAndRealizationDate(receiptId, RepayConstants.PAYSTATUS_REALIZED,
				SysParamUtil.getAppDate());
		finReceiptDetailDAO.updateReceiptStatusByReceiptId(receiptId, RepayConstants.PAYSTATUS_REALIZED);
	}

	public void setRepayPostingUtil(RepaymentPostingsUtil repayPostingUtil) {
		this.repayPostingUtil = repayPostingUtil;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFinInsurancesDAO(FinInsurancesDAO finInsurancesDAO) {
		this.finInsurancesDAO = finInsurancesDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public void setAllocationDetailDAO(ReceiptAllocationDetailDAO allocationDetailDAO) {
		this.allocationDetailDAO = allocationDetailDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinStageAccountingLogDAO(FinStageAccountingLogDAO finStageAccountingLogDAO) {
		this.finStageAccountingLogDAO = finStageAccountingLogDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
		this.assignmentDAO = assignmentDAO;
	}

	public void setAssignmentDealDAO(AssignmentDealDAO assignmentDealDAO) {
		this.assignmentDealDAO = assignmentDealDAO;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

}
