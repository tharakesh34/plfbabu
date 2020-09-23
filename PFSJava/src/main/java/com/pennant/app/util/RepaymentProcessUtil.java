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

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

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
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class RepaymentProcessUtil {
	private static final Logger logger = Logger.getLogger(RepaymentProcessUtil.class);

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
	public void calcualteAndPayReceipt(FinanceMain financeMain, Customer customer,
			List<FinanceScheduleDetail> scheduleDetails, List<FinFeeDetail> finFeeDetailList,
			FinanceProfitDetail profitDetail, FinReceiptHeader rch, String repayHierarchy, Date valuedate,
			Date postDate) throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering");

		String finrefer = financeMain.getFinReference();
		getFinReceiptHeaderDAO().generatedReceiptID(rch);
		//Prepare schedule data for log
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = new FinScheduleData();
		scheduleData.setFinanceScheduleDetails(scheduleDetails);
		Cloner cloner = new Cloner();
		List<FinanceScheduleDetail> schdDtls = cloner.deepClone(scheduleDetails);
		scheduleData
				.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finrefer, "", false));
		scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finrefer, "", false));
		FinanceType finType = FinanceConfigCache.getCacheFinanceType(StringUtils.trimToEmpty(financeMain.getFinType()));
		scheduleData.setFinanceType(finType);
		scheduleData.setFinPftDeatil(profitDetail);
		scheduleData.setFinanceMain(financeMain);
		List<FinReceiptDetail> rcdList = sortReceiptDetails(rch.getReceiptDetails());
		FinReceiptData finReceiptData = new FinReceiptData();
		//TDS Calculation, if Applicable
		financeDetail.setFinScheduleData(scheduleData);
		/*
		 * final List<XcessPayables> xcsPaybles = new ArrayList<>(); xcsPaybles.addAll(rch.getXcessPayables());
		 */

		scheduleData.getFinanceMain().setRecordType("");
		scheduleData.getFinanceMain().setVersion(scheduleData.getFinanceMain().getVersion() + 1);
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
		}

		finReceiptData = receiptCalculator.initiateReceipt(finReceiptData, true);
		finReceiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(schdDtls);
		/*
		 * finReceiptData.getReceiptHeader().getXcessPayables().clear();
		 * finReceiptData.getReceiptHeader().getXcessPayables().addAll(xcsPaybles);
		 */

		List<Object> returnList = doProcessReceipts(financeMain, schdDtls, profitDetail, rch, finFeeDetailList,
				scheduleData, valuedate, postDate, financeDetail);
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
		financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().add(totPriPaid));

		FinanceScheduleDetail curSchd = null;

		for (FinanceScheduleDetail financeScheduleDetail : scheduleDetails) {
			Date schdDate = financeScheduleDetail.getSchDate();
			// Skip if Repayment date after Current Business date
			if (!ImplementationConstants.ALLOW_OLDEST_DUE) {
				if (schdDate.compareTo(valuedate) != 0) {
					continue;
				}
				curSchd = financeScheduleDetail;
				financeScheduleDetailDAO.updateForRpy(curSchd);
				break;
			}
			curSchd = financeScheduleDetail;
			financeScheduleDetailDAO.updateForRpy(curSchd);
		}

		doSaveReceipts(rch, null, true);

		financeMainDAO.updatePaymentInEOD(financeMain);
		limitManagement.processLoanRepay(financeMain, customer, priPaynow, profitDetail.getFinCategory());
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Payment details as per receipt details
	 * 
	 * @param receiptHeaderFinReceiptDetail
	 */
	@SuppressWarnings("unchecked")
	public List<Object> doProcessReceipts(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail profitDetail, FinReceiptHeader rch, List<FinFeeDetail> finFeeDetailList,
			FinScheduleData logScheduleData, Date valueDate, Date postingDate, FinanceDetail financeDetail)
			throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering");

		BigDecimal uAmz = BigDecimal.ZERO;
		BigDecimal uLpi = BigDecimal.ZERO;
		BigDecimal cpzChg = BigDecimal.ZERO;
		BigDecimal uGstLpi = BigDecimal.ZERO;
		BigDecimal uLpp = BigDecimal.ZERO;
		BigDecimal uGstLpp = BigDecimal.ZERO;
		//FIX ME:PSD# 144078-LAN is not foreclosed if the early settlement is done by Receipt Upload process 
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		List<ManualAdviseMovements> movements = new ArrayList<>();

		// Find out Is there any schedule payment done or not, If exists Log
		// will be captured
		boolean isSchdLogReq = false;
		String receiptPurpose = rch.getReceiptPurpose();
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, receiptPurpose)
				|| StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, receiptPurpose)) {
			isSchdLogReq = true;
		}
		BigDecimal repledgeReceipt = BigDecimal.ZERO;
		BigDecimal receiptFromBank = BigDecimal.ZERO;
		BigDecimal totXcessAmount = BigDecimal.ZERO;
		Map<String, BigDecimal> extDataMap = new HashMap<>();

		long linkedTranId = 0;
		String finReference = financeMain.getFinReference();
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
			logKey = getFinLogEntryDetailDAO().save(entryDetail);

			// Save Schedule Details For Future Modifications
			FinScheduleData oldFinSchdData = getFinSchDataByFinRef(finReference, "");
			oldFinSchdData.setFinanceMain(financeMain);
			oldFinSchdData.setFinReference(finReference);
			listSave(oldFinSchdData, "_Log", logKey);
		}

		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, receiptPurpose)
				|| StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, receiptPurpose)) {
			isSchdLogReq = true;
		}

		for (FinReceiptDetail rcd : rcdList) {
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

		long postingId = postingsDAO.getPostingId();
		financeMain.setPostingId(postingId);

		// Accounting Postings Process Execution
		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes = new AEAmountCodes();

		//Last Receipt record for banking details
		FinReceiptDetail rcd = rcdList.get(rcdList.size() - 1);
		FinRepayHeader rph = rcd.getRepayHeader();

		FinanceProfitDetail pftDetailTemp = new FinanceProfitDetail();
		BeanUtils.copyProperties(profitDetail, pftDetailTemp);

		financeMain.setGlSubHeadCodes(financeMainDAO.getGLSubHeadCodes(finReference));

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
				extDataMap.put("EX_ReceiptAmount", totPaidNow);
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
		//receiptFromBank = totXcessAmount.subtract(receiptFromBank);
		if (receiptFromBank.compareTo(BigDecimal.ZERO) > 0) {
			extDataMap.put("PB_ReceiptAmount", receiptFromBank);
		}

		// FIXME: NO SURE ON GOLD LOAN. SO FOR LOOP KEPT AS IS
		for (int i = 0; i < rcdList.size(); i++) {
			if (StringUtils.equals(rcdList.get(i).getPaymentType(), RepayConstants.RECEIPTMODE_REPLEDGE)) {
				repledgeReceipt = rcdList.get(i).getAmount();
				extDataMap.put("PR_ReceiptAmount", repledgeReceipt);
			}
		}

		// Branch Cash Update
		/*
		 * if (StringUtils.equals(rch.getReceiptMode(), RepayConstants.RECEIPTMODE_CASH)) {
		 * branchCashDetailDAO.updateBranchCashDetail(rch.getUserDetails().getBranchCode(), receiptFromBank,
		 * CashManagementConstants.Add_Receipt_Amount); }
		 */
		
		String finType = financeMain.getFinType();
		String cashierBranch = rch.getCashierBranch();
		String entityCode = financeMain.getEntityCode();
		
		aeEvent.setCustID(financeMain.getCustID());
		aeEvent.setFinReference(finReference);
		aeEvent.setFinType(finType);
		aeEvent.setPromotion(financeMain.getPromotionCode());
		aeEvent.setBranch(financeMain.getFinBranch());
		aeEvent.setCcy(financeMain.getFinCcy());
		aeEvent.setPostingUserBranch(cashierBranch);
		aeEvent.setLinkedTranId(0);
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_REPAY);
		aeEvent.setValueDate(valueDate);
		aeEvent.setPostRefId(rch.getReceiptID());
		aeEvent.setPostingId(financeMain.getPostingId());
		aeEvent.setEntityCode(entityCode);

		amountCodes.setUserBranch(cashierBranch);
		amountCodes.setFinType(finType);
		amountCodes.setPartnerBankAc(rcd.getPartnerBankAc());
		amountCodes.setPartnerBankAcType(rcd.getPartnerBankAcType());
		amountCodes.setToExcessAmt(BigDecimal.ZERO);
		amountCodes.setToEmiAdvance(BigDecimal.ZERO);
		amountCodes.setPaymentType(rcd.getPaymentType());
		amountCodes.setBusinessvertical(financeMain.getBusinessVerticalCode());
		amountCodes.setEntitycode(entityCode);

		if (StringUtils.isNotBlank(financeMain.getPromotionCode())
				&& (financeMain.getPromotionSeqId() != null && financeMain.getPromotionSeqId() == 0)) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getPromotionCode(),
					AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finType,
					AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_FINTYPE));
		}

		// Assignment Percentage
		Set<String> excludeFees = null;
		if (financeMain.getAssignmentId() != null && financeMain.getAssignmentId() > 0) {
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

		//Put Manual Advises to the map
		/*
		 * List<ReceiptAllocationDetail> radList = rch.getAllocations(); for (int i = 0; i < radList.size(); i++) {
		 * ReceiptAllocationDetail rad = radList.get(i); String allocType = rad.getAllocationType(); String bounceChg =
		 * "bounceCharge";
		 * 
		 * if (StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_BOUNCE)) {
		 * extDataMap.put((bounceChg + "Paid"), rad.getTotalPaid()); extDataMap.put((bounceChg + "_CGST_P"),
		 * rad.getPaidCGST()); extDataMap.put((bounceChg + "_SGST_P"), rad.getPaidSGST()); extDataMap.put((bounceChg +
		 * "_UGST_P"), rad.getPaidUGST()); extDataMap.put((bounceChg + "_IGST_P"), rad.getPaidIGST()); } else if
		 * (StringUtils.equals(allocType, RepayConstants.ALLOCATION_MANADV)) { // Paid Amount. GST Tax to be included
		 * after Bajaj Test extDataMap.put((rad.getFeeTypeCode() + "_P"), rad.getTotalPaid());
		 * extDataMap.put((rad.getFeeTypeCode() + "_CGST_P"), rad.getPaidCGST()); extDataMap.put((rad.getFeeTypeCode() +
		 * "_SGST_P"), rad.getPaidSGST()); extDataMap.put((rad.getFeeTypeCode() + "_UGST_P"), rad.getPaidUGST());
		 * extDataMap.put((rad.getFeeTypeCode() + "_IGST_P"), rad.getPaidIGST()); } }
		 */

		extDataMap.putAll(prepareMovementMap(movements));

		BigDecimal adjustedToReceipt = BigDecimal.ZERO;
		adjustedToReceipt = rch.getTotalPastDues().getTotalPaid();
		adjustedToReceipt = adjustedToReceipt.add(rch.getTotalRcvAdvises().getTotalPaid());
		adjustedToReceipt = adjustedToReceipt.add(rch.getTotalBounces().getTotalPaid());
		adjustedToReceipt = adjustedToReceipt.add(rch.getTotalFees().getTotalPaid());

		BigDecimal toExcess = rch.getReceiptAmount().subtract(adjustedToReceipt);
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, receiptPurpose)) {
			adjustedToReceipt = adjustedToReceipt.add(toExcess);
			toExcess = BigDecimal.ZERO;
		}

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

		if (!feesExecuted && !StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			Map<String, BigDecimal> feeMap = new HashMap<>();

			if (finFeeDetailList != null) {
				prepareFeeRulesMap(amountCodes, feeMap, finFeeDetailList, rcd.getPaymentType());
			}
			extDataMap.putAll(feeMap);
		}

		List<Object> returnList = null;
		if (adjustedToReceipt.compareTo(BigDecimal.ZERO) > 0) {
			/*
			 * At the time of EOD postDate should not be APP date we need to consider value date hence parameterized the
			 * postDate
			 */

			returnList = doRepayPostings(financeDetail, rch, extDataMap, gstExecutionMap, postingDate,
					rph.getRepayID());

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
			scheduleDetails = (List<FinanceScheduleDetail>) returnList.get(2);

			if (CollectionUtils.isNotEmpty(finFeeDetailList)) {
				createDebitInvoice(linkedTranId, financeDetail);
			}

			// Waiver Fees Invoice Preparation
			if (ImplementationConstants.TAX_DFT_CR_INV_REQ) {
				createCreditInvoice(linkedTranId, financeDetail);
			}

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

		} else {
			dataMap.putAll(extDataMap);
			Map<String, Object> glSubHeadCodes = financeMain.getGlSubHeadCodes();
			dataMap.put("emptype", glSubHeadCodes.get("emptype"));
			aeEvent.setDataMap(dataMap);
			aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
			rph.setLinkedTranId(aeEvent.getLinkedTranId());

		}

		extDataMap = null;
		returnList = new ArrayList<>();
		returnList.add(scheduleDetails);
		returnList.add(uAmz);
		returnList.add(uLpi);
		returnList.add(uGstLpi);
		returnList.add(uLpp);
		returnList.add(uGstLpp);
		returnList.add(cpzChg);

		logger.debug("Leaving");

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
		String isGSTInvOnDue = SysParamUtil.getValueAsString("GST_INV_ON_DUE");

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
					if (StringUtils.equals(isGSTInvOnDue, PennantConstants.NO)) {
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

		//GST Invoice for Bounce/Manual Advise
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

		//GST Invoice for Payable Advises
		if (CollectionUtils.isNotEmpty(payPaidMovementList)) {
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(linkedTranId);
			invoiceDetail.setFinanceDetail(financeDetail);
			invoiceDetail.setMovements(payPaidMovementList);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);

			long invoiceID = this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

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

		//Assignment Percentage
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
		getPostingsPreparationUtil().postAccounting(aeEvent);
		if (aeEvent.isPostingSucess()) {
			repayHeader.setLinkedTranId(aeEvent.getLinkedTranId());
		}

		// preparing GST Invoice Report for Bounce/ManualAdvise
		List<ManualAdviseMovements> movementList = new ArrayList<ManualAdviseMovements>();
		if (CollectionUtils.isNotEmpty(movements) && financeDetail != null) {
			FeeType bounceFee = null;

			// GST Invoice data resetting based on Accounting Process
			String isGSTInvOnDue = SysParamUtil.getValueAsString("GST_INV_ON_DUE");

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

					ManualAdvise manualAdvise = getManualAdviseDAO().getManualAdviseById(movement.getAdviseID(),
							"_AView");
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

				//GST Invoice for Bounce/Manual Advise Waivers
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

		// Summing Same Type of Fee Types to Single Field
		HashMap<String, BigDecimal> movementMap = new HashMap<>();

		FeeType bounceFee = null;

		for (int m = 0; m < movements.size(); m++) {

			ManualAdviseMovements movement = movements.get(m);

			BigDecimal amount = BigDecimal.ZERO;
			String keyCode = null;

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

			if (StringUtils.isEmpty(movement.getFeeTypeCode())
					|| StringUtils.equals(movement.getFeeTypeCode(), RepayConstants.ALLOCATION_BOUNCE)) {

				if (bounceFee == null) {
					bounceFee = feeTypeDAO.getApprovedFeeTypeByFeeCode(PennantConstants.FEETYPE_BOUNCE);
				}

				if (movementMap.containsKey("bounceChargePaid")) {
					amount = movementMap.get("bounceChargePaid");
				}

				if (bounceFee != null) {
					if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(bounceFee.getTaxComponent())) {

						BigDecimal gst = cgstTax.getPaidTax().add(sgstTax.getPaidTax()).add(igstTax.getPaidTax())
								.add(ugstTax.getPaidTax()).add(cessTax.getPaidTax());
						movementMap.put("bounceChargePaid",
								amount.add(movement.getPaidAmount().subtract(gst)).add(movement.getTdsPaid()));

						BigDecimal waiverGst = cgstTax.getWaivedTax().add(sgstTax.getWaivedTax())
								.add(igstTax.getWaivedTax()).add(ugstTax.getWaivedTax()).add(cessTax.getWaivedTax());
						amount = BigDecimal.ZERO;
						if (movementMap.containsKey("bounceChargeWaived")) {
							amount = movementMap.get("bounceChargeWaived");
						}
						movementMap.put("bounceChargeWaived",
								amount.add(movement.getWaivedAmount().subtract(waiverGst)));

					} else {
						movementMap.put("bounceChargePaid",
								amount.add(movement.getPaidAmount()).add(movement.getTdsPaid()));
						amount = BigDecimal.ZERO;
						if (movementMap.containsKey("bounceChargeWaived")) {
							amount = movementMap.get("bounceChargeWaived");
						}
						movementMap.put("bounceChargeWaived", amount.add(movement.getWaivedAmount()));
					}
				}
				keyCode = "bounceCharge";
			} else {

				keyCode = movement.getFeeTypeCode();

				if (movementMap.containsKey(keyCode + "_P")) {
					amount = movementMap.get(keyCode + "_P");
				}

				ManualAdvise manualAdvise = manualAdviseDAO.getManualAdviseById(movement.getAdviseID(), "_AView");

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(manualAdvise.getTaxComponent())) {
					BigDecimal gst = cgstTax.getPaidTax().add(sgstTax.getPaidTax()).add(igstTax.getPaidTax())
							.add(ugstTax.getPaidTax()).add(cessTax.getPaidTax());
					movementMap.put(keyCode + "_P",
							amount.add(movement.getPaidAmount().subtract(gst)).add(movement.getTdsPaid()));

					BigDecimal waiverGst = cgstTax.getWaivedTax().add(sgstTax.getWaivedTax())
							.add(igstTax.getWaivedTax()).add(ugstTax.getWaivedTax()).add(cessTax.getWaivedTax());

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_W")) {
						amount = movementMap.get(keyCode + "_W");
					}
					movementMap.put(movement.getFeeTypeCode() + "_W",
							amount.add(movement.getWaivedAmount().subtract(waiverGst)));
				} else {
					movementMap.put(keyCode + "_P", amount.add(movement.getPaidAmount()).add(movement.getTdsPaid()));
					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_W")) {
						amount = movementMap.get(keyCode + "_W");
					}
					movementMap.put(movement.getFeeTypeCode() + "_W", amount.add(movement.getWaivedAmount()));
				}

			}
			// Tax Details
			//Paid GST Details
			amount = BigDecimal.ZERO;
			if (movementMap.containsKey(keyCode + "_CGST_P")) {
				amount = movementMap.get(keyCode + "_CGST_P");
			}
			movementMap.put(keyCode + "_CGST_P", amount.add(cgstTax.getPaidTax()));

			amount = BigDecimal.ZERO;
			if (movementMap.containsKey(keyCode + "_SGST_P")) {
				amount = movementMap.get(keyCode + "_SGST_P");
			}
			movementMap.put(keyCode + "_SGST_P", amount.add(sgstTax.getPaidTax()));

			amount = BigDecimal.ZERO;
			if (movementMap.containsKey(keyCode + "_IGST_P")) {
				amount = movementMap.get(keyCode + "_IGST_P");
			}
			movementMap.put(keyCode + "_IGST_P", amount.add(igstTax.getPaidTax()));

			amount = BigDecimal.ZERO;
			if (movementMap.containsKey(keyCode + "_UGST_P")) {
				amount = movementMap.get(keyCode + "_UGST_P");
			}
			movementMap.put(keyCode + "_UGST_P", amount.add(ugstTax.getPaidTax()));

			amount = BigDecimal.ZERO;
			if (movementMap.containsKey(keyCode + "_CESS_P")) {
				amount = movementMap.get(keyCode + "_CESS_P");
			}
			movementMap.put(keyCode + "_CESS_P", amount.add(cessTax.getPaidTax()));

			//Waiver GST Details
			amount = BigDecimal.ZERO;
			if (movementMap.containsKey(keyCode + "_CGST_W")) {
				amount = movementMap.get(keyCode + "_CGST_W");
			}
			movementMap.put(keyCode + "_CGST_W", amount.add(cgstTax.getWaivedTax()));

			amount = BigDecimal.ZERO;
			if (movementMap.containsKey(keyCode + "_SGST_W")) {
				amount = movementMap.get(keyCode + "_SGST_W");
			}
			movementMap.put(keyCode + "_SGST_W", amount.add(sgstTax.getWaivedTax()));

			amount = BigDecimal.ZERO;
			if (movementMap.containsKey(keyCode + "_IGST_W")) {
				amount = movementMap.get(keyCode + "_IGST_W");
			}
			movementMap.put(keyCode + "_IGST_W", amount.add(igstTax.getWaivedTax()));

			amount = BigDecimal.ZERO;
			if (movementMap.containsKey(keyCode + "_UGST_W")) {
				amount = movementMap.get(keyCode + "_UGST_W");
			}
			movementMap.put(keyCode + "_UGST_W", amount.add(ugstTax.getWaivedTax()));

			amount = BigDecimal.ZERO;
			if (movementMap.containsKey(keyCode + "_CESS_W")) {
				amount = movementMap.get(keyCode + "_CESS_W");
			}
			movementMap.put(keyCode + "_CESS_W", amount.add(cessTax.getWaivedTax()));

			if (movementMap.containsKey(keyCode + "_TDS_P")) {
				amount = movementMap.get(keyCode + "_TDS_P");
			}
			movementMap.put(keyCode + "_TDS_P", amount.add(movement.getTdsPaid()));
		}

		addZeroifNotContains(movementMap, "bounceChargePaid");
		addZeroifNotContains(movementMap, "bounceCharge_CGST_P");
		addZeroifNotContains(movementMap, "bounceCharge_IGST_P");
		addZeroifNotContains(movementMap, "bounceCharge_SGST_P");
		addZeroifNotContains(movementMap, "bounceCharge_UGST_P");
		addZeroifNotContains(movementMap, "bounceCharge_CESS_P");
		//Waiver GST Details
		addZeroifNotContains(movementMap, "bounceChargeWaived");
		addZeroifNotContains(movementMap, "bounceCharge_CGST_W");
		addZeroifNotContains(movementMap, "bounceCharge_IGST_W");
		addZeroifNotContains(movementMap, "bounceCharge_SGST_W");
		addZeroifNotContains(movementMap, "bounceCharge_UGST_W");
		addZeroifNotContains(movementMap, "bounceCharge_CESS_W");
		return movementMap;
	}

	/**
	 * Method for Preparation of Fees Data in Receipts
	 * 
	 * @param amountCodes
	 * @param dataMap
	 * @param finFeeDetailList
	 * @return
	 */
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

		long receiptID = getFinReceiptHeaderDAO().save(rch, TableType.MAIN_TAB);
		rch.setReceiptID(receiptID);

		// Receipt Allocation Details
		Map<String, BigDecimal> allocationPaidMap = null;
		Map<String, BigDecimal> allocationWaivedMap = null;
		if (rch.getAllocations() != null && !rch.getAllocations().isEmpty()) {
			if (isApproval) {
				allocationPaidMap = new HashMap<>();
				allocationWaivedMap = new HashMap<>();
			}

			List<Long> bounceAdvises = null;
			for (int i = 0; i < rch.getAllocations().size(); i++) {
				ReceiptAllocationDetail allocation = rch.getAllocations().get(i);
				allocation.setReceiptID(receiptID);
				allocation.setAllocationID(i + 1);

				if (isApproval) {
					allocationPaidMap.put(allocation.getAllocationType() + "_" + allocation.getAllocationTo(),
							allocation.getPaidAmount());
					allocationWaivedMap.put(allocation.getAllocationType() + "_" + allocation.getAllocationTo(),
							allocation.getWaivedAmount());

					// Manual Advises update
					if (StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_MANADV)) {
						if (allocation.getPaidAmount().compareTo(BigDecimal.ZERO) > 0
								|| allocation.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {

							ManualAdvise advise = new ManualAdvise();
							advise.setAdviseID(allocation.getAllocationTo());

							for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
								for (ManualAdviseMovements movement : rcd.getAdvMovements()) {
									if (allocation.getAllocationTo() == movement.getAdviseID()) {
										//Paid Details
										advise.setPaidAmount(advise.getPaidAmount().add(movement.getPaidAmount()));
										advise.setTdsPaid(advise.getTdsPaid().add(movement.getTdsPaid()));

										//Waiver Details
										advise.setWaivedAmount(
												advise.getWaivedAmount().add(movement.getWaivedAmount()));

										TaxHeader taxHeader = movement.getTaxHeader();
										if (taxHeader != null) {
											List<Taxes> taxDetails = taxHeader.getTaxDetails();
											if (CollectionUtils.isNotEmpty(taxDetails)) {
												for (Taxes taxes : taxDetails) {
													if (RuleConstants.CODE_CGST.equals(taxes.getTaxType())) {
														advise.setPaidCGST(
																advise.getPaidCGST().add(taxes.getPaidTax()));
														advise.setWaivedCGST(
																advise.getWaivedCGST().add(taxes.getWaivedTax()));
													} else if (RuleConstants.CODE_SGST.equals(taxes.getTaxType())) {
														advise.setPaidSGST(
																advise.getPaidSGST().add(taxes.getPaidTax()));
														advise.setWaivedSGST(
																advise.getWaivedSGST().add(taxes.getWaivedTax()));
													} else if (RuleConstants.CODE_IGST.equals(taxes.getTaxType())) {
														advise.setPaidIGST(
																advise.getPaidIGST().add(taxes.getPaidTax()));
														advise.setWaivedIGST(
																advise.getWaivedIGST().add(taxes.getWaivedTax()));
													} else if (RuleConstants.CODE_UGST.equals(taxes.getTaxType())) {
														advise.setPaidUGST(
																advise.getPaidUGST().add(taxes.getPaidTax()));
														advise.setWaivedUGST(
																advise.getWaivedUGST().add(taxes.getWaivedTax()));
													} else if (RuleConstants.CODE_CESS.equals(taxes.getTaxType())) {
														advise.setPaidCESS(
																advise.getPaidCESS().add(taxes.getPaidTax()));
														advise.setWaivedCESS(
																advise.getWaivedCESS().add(taxes.getWaivedTax()));
													}
												}
											}
										}
									}
								}
							}

							advise.setBalanceAmt((advise.getPaidAmount().add(advise.getWaivedAmount())).negate());

							//FIXME: PV. loan manual advise movements in receipts calculator
							getManualAdviseDAO().updateAdvPayment(advise, TableType.MAIN_TAB);
						}
					}

					// Bounce Charges Update
					if (StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_BOUNCE)) {
						if (allocation.getPaidAmount().compareTo(BigDecimal.ZERO) > 0
								|| allocation.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {

							/*
							 * if (bounceAdvises == null) { bounceAdvises =
							 * getManualAdviseDAO().getBounceAdvisesListByRef(rch.getReference(),
							 * FinanceConstants.MANUAL_ADVISE_RECEIVABLE, ""); }
							 */

							ManualAdvise advise = new ManualAdvise();
							advise.setAdviseID(allocation.getAllocationTo());

							List<FinReceiptDetail> rcdList = sortReceiptDetails(rch.getReceiptDetails());
							for (FinReceiptDetail rcd : rcdList) {
								for (ManualAdviseMovements movement : rcd.getAdvMovements()) {
									if (allocation.getAllocationTo() == movement.getAdviseID()) {

										advise.setPaidAmount(advise.getPaidAmount().add(movement.getPaidAmount()));
										advise.setTdsPaid(advise.getTdsPaid().add(movement.getTdsPaid()));

										//Waiver Details
										advise.setWaivedAmount(movement.getWaivedAmount());

										TaxHeader taxHeader = movement.getTaxHeader();
										if (taxHeader != null) {
											List<Taxes> taxDetails = taxHeader.getTaxDetails();
											if (CollectionUtils.isNotEmpty(taxDetails)) {
												for (Taxes taxes : taxDetails) {
													if (RuleConstants.CODE_CGST.equals(taxes.getTaxType())) {
														advise.setPaidCGST(
																advise.getPaidCGST().add(taxes.getPaidTax()));
														advise.setWaivedCGST(
																advise.getWaivedCGST().add(taxes.getWaivedTax()));
													} else if (RuleConstants.CODE_SGST.equals(taxes.getTaxType())) {
														advise.setPaidSGST(
																advise.getPaidSGST().add(taxes.getPaidTax()));
														advise.setWaivedSGST(
																advise.getWaivedSGST().add(taxes.getWaivedTax()));
													} else if (RuleConstants.CODE_IGST.equals(taxes.getTaxType())) {
														advise.setPaidIGST(
																advise.getPaidIGST().add(taxes.getPaidTax()));
														advise.setWaivedIGST(
																advise.getWaivedIGST().add(taxes.getWaivedTax()));
													} else if (RuleConstants.CODE_UGST.equals(taxes.getTaxType())) {
														advise.setPaidUGST(
																advise.getPaidUGST().add(taxes.getPaidTax()));
														advise.setWaivedUGST(
																advise.getWaivedUGST().add(taxes.getWaivedTax()));
													} else if (RuleConstants.CODE_CESS.equals(taxes.getTaxType())) {
														advise.setPaidCESS(
																advise.getPaidCESS().add(taxes.getPaidTax()));
														advise.setWaivedCESS(
																advise.getWaivedCESS().add(taxes.getWaivedTax()));
													}
												}
											}
										}
									}
								}
							}
							getManualAdviseDAO().updateAdvPayment(advise, TableType.MAIN_TAB);

						}
					}
				}
			}

			if (CollectionUtils.isNotEmpty(rch.getAllocations())) {
				for (ReceiptAllocationDetail allocation : rch.getAllocations()) {
					if (StringUtils.isNotBlank(allocation.getTaxType()) && allocation.getTaxHeader() != null) {
						List<Taxes> taxDetails = allocation.getTaxHeader().getTaxDetails();
						if (CollectionUtils.isNotEmpty(taxDetails)) {
							Long headerId = getTaxHeaderDetailsDAO().save(allocation.getTaxHeader(),
									TableType.MAIN_TAB.getSuffix());
							for (Taxes taxes : taxDetails) {
								taxes.setReferenceId(headerId);
							}
							getTaxHeaderDetailsDAO().saveTaxes(taxDetails, TableType.MAIN_TAB.getSuffix());
							allocation.setTaxHeaderId(headerId);
						}
					}
				}
			}

			getAllocationDetailDAO().saveAllocations(rch.getAllocations(), TableType.MAIN_TAB);
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
			long receiptSeqID = getFinReceiptDetailDAO().save(rcd, TableType.MAIN_TAB);

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
							//update only utilization
							getFinExcessAmountDAO().updateUtiliseOnly(payAgainstID, rcd.getAmount());
						} else {
							getFinExcessAmountDAO().updateUtilise(payAgainstID, rcd.getAmount());
						}

						// Delete Reserved Log against Excess and Receipt ID
						getFinExcessAmountDAO().deleteExcessReserve(receiptSeqID, payAgainstID,
								RepayConstants.RECEIPTTYPE_RECIPT);

						// Excess Movement Creation
						FinExcessMovement movement = new FinExcessMovement();
						movement.setExcessID(payAgainstID);
						movement.setReceiptID(receiptSeqID);
						movement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
						movement.setTranType(AccountConstants.TRANTYPE_DEBIT);
						movement.setAmount(rcd.getAmount());
						getFinExcessAmountDAO().saveExcessMovement(movement);
					} else {

						// Excess Amount make utilization
						FinExcessAmountReserve exReserve = getFinExcessAmountDAO().getExcessReserve(receiptSeqID,
								payAgainstID);
						if (exReserve != null) {

							// Update Reserve Amount in FinExcessAmount
							getFinExcessAmountDAO().updateExcessReserve(payAgainstID,
									exReserve.getReservedAmt().negate());

							// Delete Reserved Log against Excess and Receipt ID
							getFinExcessAmountDAO().deleteExcessReserve(receiptSeqID, payAgainstID,
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
							getManualAdviseDAO().updateUtiliseOnly(payAgainstID, payableAmt);
						} else {
							getManualAdviseDAO().updateUtilise(payAgainstID, payableAmt);
						}

						// Delete Reserved Log against Advise and Receipt Seq ID
						getManualAdviseDAO().deletePayableReserve(receiptSeqID, payAgainstID);

						// Payable Advise Movement Creation
						//FIXME
						if (rcd.getPayAdvMovement() != null) {
							rcd.getPayAdvMovement().setAdviseID(payAgainstID);
							rcd.getPayAdvMovement().setReceiptID(receiptID);
							rcd.getPayAdvMovement().setReceiptSeqID(receiptSeqID);
							rcd.getPayAdvMovement().setMovementDate(SysParamUtil.getAppDate());
							getManualAdviseDAO().saveMovement(rcd.getPayAdvMovement(), TableType.MAIN_TAB.getSuffix());
						}

					} else {
						// Payable Amount make utilization
						ManualAdviseReserve payableReserve = getManualAdviseDAO().getPayableReserve(receiptSeqID,
								payAgainstID);
						if (payableReserve != null) {

							// Update Reserve Amount in ManualAdvise
							getManualAdviseDAO().updatePayableReserve(payAgainstID,
									payableReserve.getReservedAmt().negate());

							// Delete Reserved Log against Payable Advise ID and Receipt ID
							getManualAdviseDAO().deletePayableReserve(receiptSeqID, payAgainstID);
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
					getManualAdviseDAO().saveMovement(movement, TableType.MAIN_TAB.getSuffix());
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
					recordCount = getFinExcessAmountDAO().updateExcessReserveByRef(rch.getReference(),
							rch.getExcessAdjustTo(), rph.getExcessAmount());
				} else {
					recordCount = getFinExcessAmountDAO().updateExcessBalByRef(rch.getReference(),
							rch.getExcessAdjustTo(), rph.getExcessAmount());
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
					getFinExcessAmountDAO().saveExcess(excess);
				}
			}

			// Save Repay Header details
			long repayID = getFinanceRepaymentsDAO().saveFinRepayHeader(rph, TableType.MAIN_TAB);

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

						getTaxHeaderDetailsDAO().saveTaxes(taxDetails, TableType.MAIN_TAB.getSuffix());
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
				getFinanceRepaymentsDAO().saveRpySchdList(rpySchdList, TableType.MAIN_TAB);
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
					if (detail1.getPayOrder() > detail2.getPayOrder()) {
						return 1;
					} else if (detail1.getPayOrder() < detail2.getPayOrder()) {
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
		return getRepayPostingUtil().updateStatus(financeMain, valueDate, scheduleDetails, profitDetail, overdueList,
				receiptPurpose, isPresentProc);
	}

	/**
	 * Method for Repayment Details Posting Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param rsdList
	 * @param insRefund
	 * @return
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 */
	private List<Object> doRepayPostings(FinanceDetail financeDetail, FinReceiptHeader rch,
			Map<String, BigDecimal> extDataMap, Map<String, Object> gstExecutionMap, Date postDate, long repayID)
			throws IllegalAccessException, InterfaceException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> returnList = new ArrayList<Object>();
		List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
		FinRepayQueue finRepayQueue = null;
		FinRepayQueueHeader rpyQueueHeader = new FinRepayQueueHeader();

		FinScheduleData fsd = financeDetail.getFinScheduleData();
		FinanceMain financeMain = fsd.getFinanceMain();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		rcdList = sortReceiptDetails(rcdList);
		FinReceiptDetail rcd = rcdList.get(rcdList.size() - 1);
		List<RepayScheduleDetail> rsdList = new ArrayList<RepayScheduleDetail>(1);

		for (int i = 0; i < rcdList.size(); i++) {
			FinReceiptDetail rcdTemp = rcdList.get(i);
			if (rcdTemp.getRepayHeader() != null && rcdTemp.getRepayHeader().getRepayScheduleDetails() != null
					&& !rcdTemp.getRepayHeader().getRepayScheduleDetails().isEmpty()) {
				rsdList.addAll(rcdTemp.getRepayHeader().getRepayScheduleDetails());
			}
		}

		try {
			if (rsdList != null && !rsdList.isEmpty()) {
				for (int i = 0; i < rsdList.size(); i++) {
					finRepayQueue = new FinRepayQueue();

					finRepayQueue.setFinReference(financeMain.getFinReference());
					finRepayQueue.setRpyDate(rsdList.get(i).getSchDate());
					finRepayQueue.setFinRpyFor(rsdList.get(i).getSchdFor());
					finRepayQueue.setRcdNotExist(true);
					finRepayQueue = doWriteDataToBean(finRepayQueue, financeMain, rsdList.get(i));

					finRepayQueue.setRefundAmount(rsdList.get(i).getRefundReq());
					finRepayQueue.setPenaltyPayNow(rsdList.get(i).getPenaltyPayNow());
					finRepayQueue.setWaivedAmount(rsdList.get(i).getWaivedAmt());
					finRepayQueue
							.setPenaltyBal(rsdList.get(i).getPenaltyAmt().subtract(rsdList.get(i).getPenaltyPayNow()));
					finRepayQueue.setChargeType(rsdList.get(i).getChargeType());

					// Tax Header Setting
					finRepayQueue.setTaxHeader(rsdList.get(i).getTaxHeader());
					finRepayQueues.add(finRepayQueue);
				}
			}
			BigDecimal totRecvAmount = BigDecimal.ZERO;
			List<ReceiptAllocationDetail> radList = rch.getAllocations();
			for (int i = 0; i < radList.size(); i++) {
				ReceiptAllocationDetail rad = radList.get(i);
				String allocType = rad.getAllocationType();
				BigDecimal paidNow = rad.getPaidAmount();
				BigDecimal waivedNow = rad.getWaivedAmount();

				if (StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_EMI)) {
					continue;
				} else if (StringUtils.equals(allocType, RepayConstants.ALLOCATION_PRI)
						|| StringUtils.equals(allocType, RepayConstants.ALLOCATION_FUT_PRI)
						|| StringUtils.equals(allocType, RepayConstants.ALLOCATION_PP)) {
					rpyQueueHeader.setPrincipal(rpyQueueHeader.getPrincipal().add(paidNow));
					rpyQueueHeader.setPriWaived(rpyQueueHeader.getPriWaived().add(waivedNow));
					if (StringUtils.equals(allocType, RepayConstants.ALLOCATION_PP)) {
						rpyQueueHeader.setPartialPaid(paidNow);
					}
					if (StringUtils.equals(allocType, RepayConstants.ALLOCATION_FUT_PRI)) {
						rpyQueueHeader.setFutPrincipal(paidNow);
						rpyQueueHeader.setFutPriWaived(waivedNow);
					}

				} else if (StringUtils.equals(allocType, RepayConstants.ALLOCATION_PFT)
						|| StringUtils.equals(allocType, RepayConstants.ALLOCATION_FUT_PFT)) {
					rpyQueueHeader.setProfit(rpyQueueHeader.getProfit().add(paidNow));
					rpyQueueHeader.setPftWaived(rpyQueueHeader.getPftWaived().add(waivedNow));
					rpyQueueHeader.setTds(rpyQueueHeader.getTds().add(rad.getTdsPaid()));
					if (StringUtils.equals(allocType, RepayConstants.ALLOCATION_FUT_PFT)) {
						rpyQueueHeader.setFutTds(rad.getTdsPaid());
						rpyQueueHeader.setFutProfit(paidNow);
					}
				} else if (StringUtils.equals(allocType, RepayConstants.ALLOCATION_LPFT)) {
					rpyQueueHeader.setLateProfit(rpyQueueHeader.getLateProfit().add(paidNow));
					rpyQueueHeader.setLatePftWaived(rpyQueueHeader.getLatePftWaived().add(waivedNow));
					rpyQueueHeader.setFeeTds(rpyQueueHeader.getFeeTds().add(rad.getTdsPaid()));
				} else if (StringUtils.equals(allocType, RepayConstants.ALLOCATION_ODC)) {
					rpyQueueHeader.setPenalty(rpyQueueHeader.getPenalty().add(paidNow));
					rpyQueueHeader.setPenaltyWaived(rpyQueueHeader.getPenaltyWaived().add(waivedNow));
					extDataMap.put("LPP" + "_TDS", rad.getTdsPaid());
					//rpyQueueHeader.setFeeTds(rpyQueueHeader.getFeeTds().add(rad.getTdsPaid()));
				} else if (StringUtils.equals(allocType, RepayConstants.ALLOCATION_FEE)) {
					rpyQueueHeader.setFee(rpyQueueHeader.getFee().add(paidNow));
					rpyQueueHeader.setFeeWaived(rpyQueueHeader.getFeeWaived().add(waivedNow));
				} else if (StringUtils.equals(allocType, RepayConstants.ALLOCATION_INS)) {
					rpyQueueHeader.setInsurance(rpyQueueHeader.getInsurance().add(paidNow));
					rpyQueueHeader.setInsWaived(rpyQueueHeader.getInsWaived().add(waivedNow));
				} else if (StringUtils.equals(allocType, RepayConstants.ALLOCATION_MANADV)
						|| StringUtils.equals(allocType, RepayConstants.ALLOCATION_BOUNCE)) {
					rpyQueueHeader.setAdviseAmount(rpyQueueHeader.getAdviseAmount().add(paidNow).add(waivedNow));

				}

				if (!StringUtils.equals(allocType, RepayConstants.ALLOCATION_PP)) {
					totRecvAmount = totRecvAmount.add(paidNow);
				}

			}

			if (StringUtils.equals(rch.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYRPY)
					&& rch.getReceiptAmount().compareTo(totRecvAmount) > 0) {
				rpyQueueHeader.setFutPrincipal(rch.getReceiptAmount().subtract(totRecvAmount));
			}
			//Repayments Process For Schedule Repay List	
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
			if (StringUtils.equals(financeMain.getProductCategory(), FinanceConstants.PRODUCT_GOLD)
					&& rcd.getReceiptID() > 0) {
				int stageAccTranCount = finStageAccountingLogDAO.getTranCountByReceiptNo(rcd.getPaymentRef());
				if (stageAccTranCount > 0) {
					isStageAccExecuted = true;
				}
			}
			rpyQueueHeader.setStageAccExecuted(isStageAccExecuted);

			List<FinanceScheduleDetail> scheduleDetails = fsd.getFinanceScheduleDetails();
			List<FinFeeDetail> finFeeDetailList = financeDetail.getFinFeeDetails();
			FinanceProfitDetail profitDetail = fsd.getFinPftDeatil();
			String eventCode = getEventCode(rch.getReceiptPurpose());

			returnList = getRepayPostingUtil().postingProcess(financeMain, scheduleDetails, finFeeDetailList,
					profitDetail, rpyQueueHeader, eventCode, rch.getValueDate(), postDate);

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

		logger.debug("Leaving");
		return returnList;
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
		//	1. Schedule Fee Amount
		finRepayQueue.setSchdFee(rsd.getSchdFee());
		finRepayQueue.setSchdFeeBal(rsd.getSchdFeeBal());
		finRepayQueue.setSchdFeePayNow(rsd.getSchdFeePayNow());
		finRepayQueue.setSchdFeePaid(rsd.getSchdFeePaid());
		finRepayQueue.setSchdFeeWaivedNow(rsd.getSchdFeeWaivedNow());

		//	2. Schedule Insurance Amount
		finRepayQueue.setSchdIns(rsd.getSchdIns());
		finRepayQueue.setSchdInsBal(rsd.getSchdInsBal());
		finRepayQueue.setSchdInsPayNow(rsd.getSchdInsPayNow());
		finRepayQueue.setSchdInsPaid(rsd.getSchdInsPaid());
		finRepayQueue.setSchdInsWaivedNow(rsd.getSchdInsWaivedNow());

		//	3. Schedule Supplementary Rent Amount
		finRepayQueue.setSchdSuplRent(rsd.getSchdSuplRent());
		finRepayQueue.setSchdSuplRentBal(rsd.getSchdSuplRentBal());
		finRepayQueue.setSchdSuplRentPayNow(rsd.getSchdSuplRentPayNow());
		finRepayQueue.setSchdSuplRentPaid(rsd.getSchdSuplRentPaid());
		finRepayQueue.setSchdSuplRentWaivedNow(rsd.getSchdSuplRentWaivedNow());

		//	4. Schedule Increased Cost Amount
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
		logger.debug("Entering");
		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceScheduleDetails(
				getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));
		finSchData.setDisbursementDetails(
				getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));
		logger.debug("Leaving");
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

		getFinanceScheduleDetailDAO().saveList(scheduleData.getFinanceScheduleDetails(), tableType, false);

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

			getFinanceDisbursementDAO().saveList(scheduleData.getDisbursementDetails(), tableType, false);

		}

		//Finance Repay Instruction Details
		if (scheduleData.getRepayInstructions() != null) {
			for (int i = 0; i < scheduleData.getRepayInstructions().size(); i++) {
				RepayInstruction curSchd = scheduleData.getRepayInstructions().get(i);

				curSchd.setFinReference(scheduleData.getFinReference());
				curSchd.setLogKey(logKey);
			}
			getRepayInstructionDAO().saveList(scheduleData.getRepayInstructions(), tableType, false);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method for Fetching Accounting Event Code based on Finance Event Action
	 * 
	 * @param finEvent
	 * @return
	 */
	private String getEventCode(String finEvent) {
		if (StringUtils.equals(finEvent, FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			return AccountEventConstants.ACCEVENT_REPAY;
		} else if (StringUtils.equals(finEvent, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			return AccountEventConstants.ACCEVENT_EARLYPAY;
		} else if (StringUtils.equals(finEvent, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			return AccountEventConstants.ACCEVENT_EARLYSTL;
		}
		return "";
	}

	private Date getPostDate(Date appDate) {
		Calendar cal = Calendar.getInstance();
		Calendar appCal = Calendar.getInstance();
		cal.setTime(DateUtility.getSysDate());
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

		Date appDate = SysParamUtil.getAppDate();

		finReceiptHeaderDAO.generatedReceiptID(rch);

		List<Object> returnList = doProcessReceipts(fm, schdDtls, profitDetail, rch, null, scheduleData,
				rch.getValueDate(), appDate, financeDetail);

		@SuppressWarnings("unchecked")
		List<FinanceScheduleDetail> scheduleDetails = (List<FinanceScheduleDetail>) returnList.get(0);
		scheduleData.setFinanceScheduleDetails(scheduleDetails);

		BigDecimal priPaynow = BigDecimal.ZERO;

		for (ReceiptAllocationDetail allocate : rch.getAllocations()) {
			if (StringUtils.equals(RepayConstants.ALLOCATION_PRI, allocate.getAllocationType())) {
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
			if (repayScheduleDetails != null && !repayScheduleDetails.isEmpty()) {
				for (RepayScheduleDetail rpySchd : repayScheduleDetails) {
					totPriPaid = totPriPaid.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
				}
			}
		}

		fm.setFinRepaymentAmount(fm.getFinRepaymentAmount().add(totPriPaid));

		FinanceScheduleDetail curSchd = null;

		for (FinanceScheduleDetail financeScheduleDetail : scheduleDetails) {
			Date schdDate = financeScheduleDetail.getSchDate();
			if (schdDate.compareTo(rch.getValueDate()) > 0) {
				break;
			}
			curSchd = financeScheduleDetail;
			financeScheduleDetailDAO.updateForRpy(curSchd);
		}

		doSaveReceipts(rch, null, true);

		Date reqMaxODDate = appDate;
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, rch.getReceiptPurpose())) {
			reqMaxODDate = rch.getValueDate();
		}
		if (!ImplementationConstants.LPP_CALC_SOD) {
			reqMaxODDate = DateUtility.addDays(reqMaxODDate, -1);
		}
		List<FinODDetails> overdueList = getFinODDetailsDAO().getFinODBalByFinRef(fm.getFinReference());
		if (CollectionUtils.isNotEmpty(overdueList)) {
			overdueList = receiptCalculator.calPenalty(scheduleData, receiptData, reqMaxODDate, overdueList);
			finODDetailsDAO.updateList(overdueList);
		}

		fm = updateStatus(fm, appDate, scheduleDetails, profitDetail, overdueList, rch.getReceiptPurpose(), false);

		financeMainDAO.updatePaymentInEOD(fm);
		limitManagement.processLoanRepay(fm, customerDetails.getCustomer(), priPaynow, profitDetail.getFinCategory());
	}

	public void processSuccessPresentment(long receiptId) {
		FinReceiptHeader rch = getFinReceiptHeaderDAO().getReceiptHeaderByID(receiptId, "");
		if (rch == null) {
			return;
		}
		List<FinReceiptDetail> rcdDtls = getFinReceiptDetailDAO().getReceiptHeaderByID(receiptId, "");
		for (FinReceiptDetail recDtl : rcdDtls) {
			FinRepayHeader rph = getFinanceRepaymentsDAO().getFinRepayHeadersByReceipt(recDtl.getReceiptSeqID(), "");
			// updating fixexcess amount after realization
			if (StringUtils.equals(rch.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_SCHDRPY)) {
				if (rph != null && rph.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {
					finExcessAmountDAO.updExcessAfterRealize(rch.getReference(), rch.getExcessAdjustTo(),
							rph.getExcessAmount());
				}
			}
		}
		getFinReceiptHeaderDAO().updateReceiptStatusAndRealizationDate(receiptId, RepayConstants.PAYSTATUS_REALIZED,
				DateUtility.getAppDate());
		getFinReceiptDetailDAO().updateReceiptStatusByReceiptId(receiptId, RepayConstants.PAYSTATUS_REALIZED);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public RepaymentPostingsUtil getRepayPostingUtil() {
		return repayPostingUtil;
	}

	public void setRepayPostingUtil(RepaymentPostingsUtil repayPostingUtil) {
		this.repayPostingUtil = repayPostingUtil;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
		return finLogEntryDetailDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
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

	public ReceiptAllocationDetailDAO getAllocationDetailDAO() {
		return allocationDetailDAO;
	}

	public void setAllocationDetailDAO(ReceiptAllocationDetailDAO allocationDetailDAO) {
		this.allocationDetailDAO = allocationDetailDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinStageAccountingLogDAO(FinStageAccountingLogDAO finStageAccountingLogDAO) {
		this.finStageAccountingLogDAO = finStageAccountingLogDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public AssignmentDAO getAssignmentDAO() {
		return assignmentDAO;
	}

	public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
		this.assignmentDAO = assignmentDAO;
	}

	public AssignmentDealDAO getAssignmentDealDAO() {
		return assignmentDealDAO;
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

	public TaxHeaderDetailsDAO getTaxHeaderDetailsDAO() {
		return taxHeaderDetailsDAO;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

}
