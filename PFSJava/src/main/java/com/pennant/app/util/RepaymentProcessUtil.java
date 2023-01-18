package com.pennant.app.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentDealDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinStageAccountingLogDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
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
import com.pennant.backend.model.finance.FinDueData;
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
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.ProductUtil;
import com.pennanttech.pff.npa.service.AssetClassificationService;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;
import com.pennanttech.pff.payment.model.LoanPayment;
import com.pennanttech.pff.payment.service.LoanPaymentService;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.receipt.util.ReceiptUtil;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class RepaymentProcessUtil {
	private static final Logger logger = LogManager.getLogger(RepaymentProcessUtil.class);

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
	private LimitManagement limitManagement;
	private ReceiptAllocationDetailDAO allocationDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private PostingsDAO postingsDAO;
	private FinStageAccountingLogDAO finStageAccountingLogDAO;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private FeeTypeDAO feeTypeDAO;
	private AssignmentDAO assignmentDAO;
	private AssignmentDealDAO assignmentDealDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;

	private GSTInvoiceTxnService gstInvoiceTxnService;
	private OverdrafLoanService overdrafLoanService;
	private AccrualService accrualService;
	private LoanPaymentService loanPaymentService;

	private RepaymentPostingsUtil repaymentPostingsUtil;
	private PostingsPreparationUtil postingsPreparationUtil;

	private ReceiptCalculator receiptCalculator;
	private AssetClassificationService assetClassificationService;

	public RepaymentProcessUtil() {
		super();
	}

	public void calcualteAndPayReceipt(ReceiptDTO receiptDTO) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = receiptDTO.getFinanceMain();
		Customer customer = receiptDTO.getCustomer();
		List<FinanceScheduleDetail> scheduleDetails = receiptDTO.getSchedules();
		List<FinFeeDetail> finFeeDetailList = receiptDTO.getFees();
		FinanceProfitDetail profitDetail = receiptDTO.getProfitDetail();
		FinReceiptHeader rch = receiptDTO.getFinReceiptHeader();
		Date valuedate = receiptDTO.getValuedate();
		Date postDate = receiptDTO.getPostDate();

		long finID = fm.getFinID();
		String finType = fm.getFinType();

		finReceiptHeaderDAO.generatedReceiptID(rch);

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = new FinScheduleData();
		schdData.setFinanceScheduleDetails(scheduleDetails);

		List<FinanceScheduleDetail> schdDtls = new ArrayList<>();
		for (FinanceScheduleDetail schd : scheduleDetails) {
			schdDtls.add(schd.copyEntity());
		}

		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, "", false));
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, "", false));
		schdData.setFinanceType(FinanceConfigCache.getCacheFinanceType(finType));
		schdData.setFinPftDeatil(profitDetail);
		schdData.setFinanceMain(fm);

		fd.setFinScheduleData(schdData);

		List<FinReceiptDetail> rcdList = sortReceiptDetails(rch.getReceiptDetails());

		final List<XcessPayables> xcsPaybles = new ArrayList<>();
		xcsPaybles.addAll(rch.getXcessPayables());

		schdData.getFinanceMain().setRecordType("");
		schdData.getFinanceMain().setVersion(schdData.getFinanceMain().getVersion() + 1);

		FinReceiptData rd = new FinReceiptData();
		rd.setFinanceDetail(fd);
		rd.setBuildProcess("I");

		rd.setValueDate(valuedate);
		rch.setValueDate(null);
		rd.setReceiptHeader(rch);

		// if presentment receipt created through EOD then both Scheduledate and value date are same.
		rd.setPresentmentSchDate(valuedate);

		rd = receiptCalculator.initiateReceipt(rd, true);

		prepareFinDueData(rd);

		if (PennantConstants.PROCESS_REPRESENTMENT.equals(rch.getPresentmentType())) {
			processRepresentment(receiptDTO, rd);
		}

		BigDecimal totDues = rd.getReceiptHeader().getTotalPastDues().getTotalDue();

		if (rd.isPresentment() && ProductUtil.isOverDraft(fm)) {
			totDues = totDues.add(rd.getReceiptHeader().getTotalRcvAdvises().getTotalDue());
		}

		FinReceiptDetail recdtl = rch.getReceiptDetails().get(0);

		if (recdtl.getDueAmount().compareTo(totDues) > 0) {
			recdtl.setDueAmount(totDues);
		}

		rd = receiptCalculator.recalAutoAllocation(rd, true);

		rd.setBuildProcess("R");
		rd.getRepayMain().setRepayAmountNow(BigDecimal.ZERO);
		rd.getRepayMain().setPrincipalPayNow(BigDecimal.ZERO);
		rd.getRepayMain().setProfitPayNow(BigDecimal.ZERO);

		int receiptPurposeCtg = 0;
		receiptPurposeCtg = ReceiptUtil.getReceiptPurpose(rd.getReceiptHeader().getReceiptPurpose());
		if (receiptPurposeCtg == 1) {
			schdData = ScheduleCalculator.recalEarlyPaySchedule(schdData, rch.getValueDate(), null, rd.getRemBal(),
					schdData.getFinanceType().getFinScheduleOn());
			receiptCalculator.addPartPaymentAlloc(rd);
		}

		for (ReceiptAllocationDetail rad : rd.getReceiptHeader().getAllocations()) {
			rad.setPaidAvailable(rad.getPaidAmount());
			rad.setWaivedAvailable(rad.getWaivedAmount());
			rad.setBalance(rad.getTotalDue());
			receiptCalculator.resetPaidAllocations(rad);
		}

		rd = receiptCalculator.initiateReceipt(rd, true);
		rd.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(schdDtls);

		rd.getReceiptHeader().getXcessPayables().clear();
		rd.getReceiptHeader().getXcessPayables().addAll(xcsPaybles);

		rch.setValueDate(valuedate);
		List<Object> returnList = doProcessReceipts(fm, schdDtls, profitDetail, rch, finFeeDetailList, schdData,
				valuedate, postDate, fd);
		scheduleDetails = (List<FinanceScheduleDetail>) returnList.get(0);

		BigDecimal priPaynow = BigDecimal.ZERO;

		for (ReceiptAllocationDetail allocate : rch.getAllocations()) {
			if (Allocation.PRI.equals(allocate.getAllocationType())) {
				priPaynow = allocate.getPaidAmount();
				break;
			}
		}

		// Preparing Total Principal Amount
		BigDecimal totPriPaid = BigDecimal.ZERO;
		for (FinReceiptDetail rcd : rcdList) {
			if (rcd.getRepayHeader() != null) {
				FinRepayHeader repayHeader = rcd.getRepayHeader();
				int i = 1;
				if (CollectionUtils.isNotEmpty(repayHeader.getRepayScheduleDetails())) {
					for (RepayScheduleDetail rpySchd : repayHeader.getRepayScheduleDetails()) {
						totPriPaid = totPriPaid
								.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));

						i++;
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

			if (schdDate.compareTo(presentmentSchDate) > 0) {
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

		// OD DETAILS UPDATE AND PFTDETAILS UPDATE
		if (ReceiptMode.PRESENTMENT.equals(rch.getReceiptMode()) && ImplementationConstants.ALLOW_OLDEST_DUE) {
			repaymentPostingsUtil.recalOldestDueKnockOff(fm, profitDetail, valuedate, scheduleDetails);
		}

		// If all presentment also Approved and upload status is succeed then only need to check the case
		if (presentmentDetailDAO.getApprovedPresentmentCount(fm.getFinReference()) == 0) {
			LoanPayment lp = new LoanPayment(fm.getFinID(), fm.getFinReference(), scheduleDetails, rch.getValueDate());
			boolean isFinFullyPaid = loanPaymentService.isSchdFullyPaid(lp);

			Date appDate = SysParamUtil.getAppDate();

			if (ProductUtil.isOverDraft(fm) && DateUtil.compare(appDate, fm.getMaturityDate()) < 0) {
				isFinFullyPaid = false;
			}

			if (isFinFullyPaid) {
				fm.setFinIsActive(false);
				fm.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
				profitDetail.setFinIsActive(false);
				profitDetail.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
			} else {
				fm.setFinIsActive(true);
			}
		} else {
			fm.setFinIsActive(true);
		}

		profitDetail = accrualService.calProfitDetails(fm, scheduleDetails, profitDetail, valuedate);
		profitDetail.setFinIsActive(fm.isFinIsActive());
		profitDetail.setClosingStatus(fm.getClosingStatus());
		profitDetailsDAO.update(profitDetail, true);

		financeMainDAO.updatePaymentInEOD(fm);
		limitManagement.processLoanRepay(fm, customer, priPaynow);
		logger.debug(Literal.LEAVING);
	}

	private void processRepresentment(ReceiptDTO receiptDTO, FinReceiptData frd) {
		logger.debug(Literal.ENTERING);

		PresentmentHeader ph = receiptDTO.getPresentmentHeader();
		PresentmentDetail pd = receiptDTO.getPresentmentDetail();
		FinReceiptHeader rch = receiptDTO.getFinReceiptHeader();

		if (ph == null || pd == null) {
			return;
		}

		if (!ph.isLppReq() && !ph.isBounceReq()) {
			return;
		}

		FinReceiptHeader finReceiptHeader = frd.getReceiptHeader();
		Date receiptDate = finReceiptHeader.getReceiptDate();

		if (ph.isLppReq()) {
			receiptCalculator.fetchLppForRePresentment(frd, receiptDate, pd.getSchDate());
		}

		if (ph.isBounceReq()) {
			receiptCalculator.fetchBouncedAdviseDetailsForRepresentment(frd, receiptDate);
		}

		List<ReceiptAllocationDetail> allocationDetails = finReceiptHeader.getAllocations();
		BigDecimal lppAmount = BigDecimal.ZERO;
		BigDecimal bounceAmount = BigDecimal.ZERO;

		for (ReceiptAllocationDetail allocate : allocationDetails) {
			if (Allocation.BOUNCE.equalsIgnoreCase(allocate.getAllocationType())) {
				allocate.setPaidAmount(allocate.getTotalDue());
				allocate.setTotalPaid(allocate.getTotalDue());
				bounceAmount = bounceAmount.add(allocate.getTotalDue());
			} else if (Allocation.ODC.equalsIgnoreCase(allocate.getAllocationType())) {
				allocate.setPaidAmount(allocate.getTotalDue());
				allocate.setTotalPaid(allocate.getTotalDue());
				lppAmount = lppAmount.add(allocate.getTotalDue());
			}
		}

		pd.setLppAmount(lppAmount);
		pd.setBounceAmount(bounceAmount);
		pd.setPresentmentAmt(pd.getPresentmentAmt().add(lppAmount).add(bounceAmount));

		BigDecimal receiptAmount = pd.getPresentmentAmt();
		rch.setReceiptAmount(receiptAmount);

		receiptCalculator.recalAutoAllocation(frd, true);

		if (pd.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
			FinReceiptDetail rcd = new FinReceiptDetail();
			rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			rcd.setPaymentType(RepayConstants.PAYTYPE_PRESENTMENT);
			rcd.setPayAgainstID(0);
			rcd.setAmount(pd.getPresentmentAmt());
			rcd.setDueAmount(pd.getPresentmentAmt());
			rcd.setValueDate(pd.getSchDate());
			rcd.setReceivedDate(receiptDTO.getPostDate());
			rcd.setPartnerBankAc(pd.getAccountNo());
			rcd.setPartnerBankAcType(pd.getAcType());

			rch.getReceiptDetails().add(rcd);
		}

		logger.debug(Literal.LEAVING);
	}

	public List<Object> doProcessReceipts(FinanceMain fm, List<FinanceScheduleDetail> schedules,
			FinanceProfitDetail pfd, FinReceiptHeader rch, List<FinFeeDetail> finFeeDetailList,
			FinScheduleData logScheduleData, Date valueDate, Date postingDate, FinanceDetail financeDetail)
			throws AppException {
		logger.debug(Literal.ENTERING);

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
		if (FinServiceEvent.EARLYRPY.equals(receiptPurpose) || FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)) {
			isSchdLogReq = true;
		}
		BigDecimal receiptFromBank = BigDecimal.ZERO;
		BigDecimal totXcessAmount = BigDecimal.ZERO;
		Map<String, BigDecimal> extDataMap = new HashMap<>();

		long linkedTranId = 0;
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		// Create log entry for Action for Schedule Modification
		FinLogEntryDetail entryDetail = null;
		long logKey = 0;
		Date postDate = getPostDate(postingDate);
		if (isSchdLogReq) {
			entryDetail = new FinLogEntryDetail();
			entryDetail.setFinID(finID);
			entryDetail.setFinReference(finReference);
			entryDetail.setEventAction(receiptPurpose);
			entryDetail.setSchdlRecal(false);
			entryDetail.setPostDate(postDate);
			entryDetail.setReversalCompleted(false);

			if (!fm.isSimulateAccounting()) {
				logKey = finLogEntryDetailDAO.save(entryDetail);

				// Save Schedule Details For Future Modifications
				FinScheduleData oldFinSchdData = getFinSchDataByFinRef(finID, "");
				oldFinSchdData.setFinanceMain(fm);
				oldFinSchdData.setFinID(finID);
				oldFinSchdData.setFinReference(finReference);
				listSave(oldFinSchdData, "_Log", logKey);
			}

		}

		if (StringUtils.equals(FinServiceEvent.EARLYRPY, receiptPurpose)
				|| StringUtils.equals(FinServiceEvent.EARLYSETTLE, receiptPurpose)) {
			isSchdLogReq = true;
		}

		BigDecimal receiptAmount = BigDecimal.ZERO;
		for (FinReceiptDetail rcd : rcdList) {
			receiptAmount = receiptAmount.add(rcd.getAmount());
			String paymentType = rcd.getPaymentType();
			movements.addAll(rcd.getAdvMovements());
			if (!ReceiptMode.EMIINADV.equals(paymentType) && !ReceiptMode.EXCESS.equals(paymentType)
					&& !ReceiptMode.PAYABLE.equals(paymentType) && !ReceiptMode.ADVINT.equals(paymentType)
					&& !ReceiptMode.ADVEMI.equals(paymentType) && !ReceiptMode.CASHCLT.equals(paymentType)
					&& !ReceiptMode.DSF.equals(paymentType)) {
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

		Map<String, Object> map = fm.getGlSubHeadCodes();

		if (MapUtils.isEmpty(map)) {
			map.putAll(financeMainDAO.getGLSubHeadCodes(finID));
		}

		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(finID, financeDetail.getFinanceTaxDetail());
		fm.setGstExecutionMap(gstExecutionMap);

		/**
		 * Defaulting with ZERO
		 */
		extDataMap.put("EX_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("ET_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("EA_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("EAI_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("EAM_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("CACLT_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("DSF_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("PB_ReceiptAmount", BigDecimal.ZERO);
		extDataMap.put("Restruct_Bpi", rch.getBpiAmount());

		List<ManualAdviseMovements> payableAdvMovements = new ArrayList<>();

		// Put Xcess Payables to Map along with GST
		List<XcessPayables> xcessPayables = rch.getXcessPayables();
		Map<String, BigDecimal> taxPercmap = GSTCalculator.getTaxPercentages(fm);
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
			case RepayConstants.EXAMOUNTTYPE_TEXCESS:
				extDataMap.put("ET_ReceiptAmount", totPaidNow);
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
					taxPercmap = GSTCalculator.getTaxPercentages(fm);
				}

				ManualAdviseMovements adviseMovements = preparePayableMovement(taxPercmap, xcessPayable);
				if (adviseMovements != null) {
					payableAdvMovements.add(adviseMovements);
				}
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
			if (ReceiptMode.REPLEDGE.equals(frd.getPaymentType())) {
				extDataMap.put("PR_ReceiptAmount", frd.getAmount());
			}
		}

		addZeroifNotContains(extDataMap, "EX_ReceiptAmount");
		addZeroifNotContains(extDataMap, "ET_ReceiptAmount");
		addZeroifNotContains(extDataMap, "EA_ReceiptAmount");
		addZeroifNotContains(extDataMap, "PB_ReceiptAmount");
		addZeroifNotContains(extDataMap, "Restruct_Bpi");

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
		aeEvent.setFinID(finID);
		aeEvent.setFinReference(finReference);
		aeEvent.setFinType(finType);
		aeEvent.setPromotion(fm.getPromotionCode());
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setPostingUserBranch(cashierBranch);
		aeEvent.setLinkedTranId(0);
		aeEvent.setAccountingEvent(AccountingEvent.REPAY);
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
		amountCodes.setManualTds(rch.getTdsAmount());

		if (StringUtils.isNotBlank(fm.getPromotionCode())
				&& (fm.getPromotionSeqId() != null && fm.getPromotionSeqId() == 0)) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(fm.getPromotionCode(),
					AccountingEvent.REPAY, FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(fm.getFinType(), AccountingEvent.REPAY,
					FinanceConstants.MODULEID_FINTYPE));
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

		BigDecimal toExcess = rch.getReceiptAmount().subtract(adjustedToReceipt);
		if (StringUtils.equals(FinServiceEvent.EARLYRPY, receiptPurpose)) {
			adjustedToReceipt = adjustedToReceipt.add(toExcess);
			toExcess = BigDecimal.ZERO;
		}

		amountCodes.setIntAdv(AdvanceType.hasAdvInterest(fm));

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		BigDecimal unAdjAdvIntTds = BigDecimal.ZERO;
		if (fm.istDSApplicable() && amountCodes.isIntAdv()
				&& FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())) {
			unAdjAdvIntTds = financeScheduleDetailDAO.getUnpaidTdsAmount(fm.getFinReference());
		}
		extDataMap.put("ae_intTdsUnpaid", unAdjAdvIntTds);

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
			case ReceiptMode.DSF:
				toExcess = adjustExcessForAdvInt(rch, amountCodes, toExcess);
				extDataMap.put("ae_toDSFAmt", toExcess);
				dataMap.put("ae_toDSFAmt", toExcess);
				break;
			case ReceiptMode.CASHCLT:
				toExcess = adjustExcessForAdvInt(rch, amountCodes, toExcess);
				extDataMap.put("ae_toCashCollAmt", toExcess);
				dataMap.put("ae_toCashCollAmt", toExcess);
				break;

			default:
				break;
			}

		}

		if (excludeFees != null) {
			dataMap.put(AccountConstants.POSTINGS_EXCLUDE_FEES, excludeFees);
		}

		if (!feesExecuted && !FinServiceEvent.SCHDRPY.equals(receiptPurpose)) {
			Map<String, BigDecimal> feeMap = new HashMap<>();

			if (finFeeDetailList != null) {
				prepareFeeRulesMap(feeMap, finFeeDetailList, rcd.getPaymentType());
			}
			extDataMap.putAll(feeMap);
		}

		List<Object> returnList = null;

		if (adjustedToReceipt.compareTo(BigDecimal.ZERO) <= 0 && !rch.isClosureWithFullWaiver()) {
			dataMap.putAll(extDataMap);
			Map<String, Object> glSubHeadCodes = fm.getGlSubHeadCodes();
			dataMap.put("emptype", glSubHeadCodes.get("EMPTYPE"));
			dataMap.put("ae_receiptChannel", fm.getReceiptChannel());
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
		linkedTranId = (long) returnList.get(1);
		for (FinReceiptDetail rcDtl : rch.getReceiptDetails()) {
			FinRepayHeader rpyh = rcDtl.getRepayHeader();
			if (rpyh != null) {
				rpyh.setLinkedTranId(linkedTranId);
			}
		}
		rph.setLinkedTranId(linkedTranId);
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
						if (AdviseType.isPayable(manualAdvise.getAdviseType())) {
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
					if (AdviseType.isPayable(manualAdvise.getAdviseType())) {
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
		String restructFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_RESTRUCT_CPZ);

		List<FinFeeDetail> finFeeDetailList = financeDetail.getFinScheduleData().getFinFeeDetailList();
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (AccountingEvent.RESTRUCTURE.equals(finFeeDetail.getFinEvent())
					&& StringUtils.equals(finFeeDetail.getFeeTypeCode(), restructFeeCode)) {
				continue;
			}

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
			if (FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())) {
				for (ReceiptAllocationDetail rad : rch.getAllocations()) {
					String allocationType = rad.getAllocationType();
					if (Allocation.FUT_TDS.equals(allocationType)) {
						toExcess = toExcess.subtract(rad.getDueAmount());
						toExcess = toExcess.subtract(rad.getDueAmount());
					}
				}
			}
		}
		return toExcess;
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

		String bounceComponent = feeTypeDAO.getTaxComponent(Allocation.BOUNCE);

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

			String feeTypeCode = movement.getFeeTypeCode();
			String taxComponent = feeTypeDAO.getTaxComponent(feeTypeCode);

			if (StringUtils.isEmpty(feeTypeCode) || Allocation.BOUNCE.equals(feeTypeCode)) {

				if (taxComponent == null) {
					taxComponent = bounceComponent;
				}
				if (bounceComponent == null) {
					continue;
				}

				addAmountToMap(movementMap, "bounceChargePaid", paidAmt);
				addAmountToMap(movementMap, "bounceChargeWaived", waivedAmt);
				addAmountToMap(movementMap, "bounceCharge" + "_CGST_P", cgstPaid);
				addAmountToMap(movementMap, "bounceCharge" + "_SGST_P", sgstPaid);
				addAmountToMap(movementMap, "bounceCharge" + "_IGST_P", igstPaid);
				addAmountToMap(movementMap, "bounceCharge" + "_UGST_P", ugstPaid);
				addAmountToMap(movementMap, "bounceCharge" + "_CESS_P", cessPaid);

				addAmountToMap(movementMap, "bounceCharge" + "_CGST_W", cgstWaived);
				addAmountToMap(movementMap, "bounceCharge" + "_SGST_W", sgstWaived);
				addAmountToMap(movementMap, "bounceCharge" + "_IGST_W", igstWaived);
				addAmountToMap(movementMap, "bounceCharge" + "_UGST_W", ugstWaived);
				addAmountToMap(movementMap, "bounceCharge" + "_CESS_W", cessWaived);

				addAmountToMap(movementMap, "bounceCharge" + "_TDS_P", tdsPaid);

			} else {
				addAmountToMap(movementMap, feeTypeCode + "_P", paidAmt);
				addAmountToMap(movementMap, feeTypeCode + "_W", waivedAmt);
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

	private Map<String, BigDecimal> prepareFeeRulesMap(Map<String, BigDecimal> dataMap,
			List<FinFeeDetail> finFeeDetailList, String payType) {
		logger.debug(Literal.ENTERING);

		String restructFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_RESTRUCT_CPZ);

		for (FinFeeDetail fee : finFeeDetailList) {
			if (!fee.isRcdVisible()) {
				continue;
			}

			if (AccountingEvent.RESTRUCTURE.equals(fee.getFinEvent())
					&& StringUtils.equals(fee.getFeeTypeCode(), restructFeeCode)) {
				continue;
			}

			dataMap.putAll(FeeCalculator.getFeeRuleMap(fee, payType));
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
		logger.debug(Literal.ENTERING);

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
					if (Allocation.MANADV.equals(allocationType)) {
						if (allocation.getPaidAmount().compareTo(BigDecimal.ZERO) > 0
								|| allocation.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {

							ManualAdvise ma = new ManualAdvise();
							ma.setAdviseID(allocationTo);

							for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
								for (ManualAdviseMovements movement : rcd.getAdvMovements()) {
									if (allocationTo == movement.getAdviseID()) {
										// Paid Details
										ma.setPaidAmount(ma.getPaidAmount().add(movement.getPaidAmount()));
										ma.setTdsPaid(ma.getTdsPaid().add(movement.getTdsPaid()));
										// advise.setPaidAmount(advise.getPaidAmount().subtract(advise.getTdsPaid()));
										// Waiver Details
										ma.setWaivedAmount(ma.getWaivedAmount().add(movement.getWaivedAmount()));
										ma.setAdviseType(movement.getAdviseType());

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

							if (AdviseType.isPayable(ma.getAdviseType())) {
								ma.setBalanceAmt(ma.getPaidAmount().add(ma.getWaivedAmount()).negate());
							}

							// FIXME: PV. loan manual advise movements in receipts calculator
							manualAdviseDAO.updateAdvPayment(ma, TableType.MAIN_TAB);
						}
					}

					// Bounce Charges Update
					if (Allocation.BOUNCE.equals(allocationType)) {
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
		Date appValueDate = SysParamUtil.getAppValueDate();

		for (FinReceiptDetail rcd : rcdList) {
			rcd.setReceiptID(receiptID);
			if (isApproval) {
				rcd.setStatus(RepayConstants.PAYSTATUS_APPROVED);
			} else {
				rcd.setStatus(rch.getReceiptModeStatus());
			}
			long receiptSeqID = finReceiptDetailDAO.save(rcd, TableType.MAIN_TAB);

			// Excess Amounts
			if (ReceiptMode.EXCESS.equals(rcd.getPaymentType()) || ReceiptMode.EMIINADV.equals(rcd.getPaymentType())
					|| ReceiptMode.ADVINT.equals(rcd.getPaymentType())
					|| ReceiptMode.ADVEMI.equals(rcd.getPaymentType())
					|| ReceiptMode.CASHCLT.equals(rcd.getPaymentType())
					|| ReceiptMode.DSF.equals(rcd.getPaymentType())) {

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
								payAgainstID, RepayConstants.RECEIPTTYPE_RECIPT);
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
			if (StringUtils.equals(rcd.getPaymentType(), ReceiptMode.PAYABLE)) {

				long payAgainstID = rcd.getPayAgainstID();

				// Payable Advise Amount make utilization
				if (payAgainstID != 0) {

					if (isApproval) {
						BigDecimal payableAmt = rcd.getAmount();
						if (rcd.getPayAdvMovement() != null) {
							TaxHeader taxHeader = rcd.getPayAdvMovement().getTaxHeader();
							if (taxHeader != null) {
								List<Taxes> taxDetails = taxHeader.getTaxDetails();
								if (CollectionUtils.isNotEmpty(taxDetails)) {
									for (Taxes taxes : taxDetails) {
										if (StringUtils.equals(taxes.getTaxType(),
												FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
											payableAmt = payableAmt.subtract(taxes.getPaidTax());
										}
									}
								}
							}
						}

						BigDecimal payAdv = BigDecimal.ZERO;
						List<ManualAdvise> payableAdvises = rch.getPayableAdvises();
						for (ManualAdvise ma : payableAdvises) {
							if (rcd.getPayAgainstID() == ma.getAdviseID()) {
								payAdv = ma.getReservedAmt();
								break;
							}
						}

						if (rcd.isNoReserve()) {
							manualAdviseDAO.updateUtiliseOnly(payAgainstID, payableAmt);
						} else {
							manualAdviseDAO.updateUtilise(payAgainstID, payAdv, rcd.isNoManualReserve());
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

							TaxHeader taxHeader = rcd.getPayAdvMovement().getTaxHeader();
							if (taxHeader != null && CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {
								List<Taxes> taxDetails = taxHeader.getTaxDetails();
								Long headerId = taxHeaderDetailsDAO.save(taxHeader, TableType.MAIN_TAB.getSuffix());
								for (Taxes taxes : taxDetails) {
									taxes.setReferenceId(headerId);
								}
								taxHeaderDetailsDAO.saveTaxes(taxDetails, TableType.MAIN_TAB.getSuffix());
								rcd.getPayAdvMovement().setTaxHeaderId(headerId);
							}

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
				createMAMovements(receiptID, rcd, receiptSeqID);
			}

			FinRepayHeader rph = rcd.getRepayHeader();
			rph.setReceiptSeqID(receiptSeqID);
			rph.setValueDate(appValueDate);
			rph.setFinID(rch.getFinID());
			rph.setFinReference(rch.getReference());
			rph.setFinEvent(rch.getReceiptPurpose());

			if (rph.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {
				FinExcessAmount excess = new FinExcessAmount();
				excess.setFinID(rch.getFinID());
				excess.setFinReference(rch.getReference());
				excess.setAmountType(rch.getExcessAdjustTo());
				excess.setAmount(rph.getExcessAmount());
				excess.setUtilisedAmt(BigDecimal.ZERO);
				excess.setBalanceAmt(rph.getExcessAmount());
				excess.setReservedAmt(BigDecimal.ZERO);
				excess.setReceiptID(rch.getReceiptID());
				excess.setValueDate(rch.getValueDate());
				excess.setPostDate(SysParamUtil.getAppDate());

				if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_DEPOSITED)) {
					excess.setBalanceAmt(BigDecimal.ZERO);
					excess.setReservedAmt(rph.getExcessAmount());
					excess.setAmount(rph.getExcessAmount());
				}

				finExcessAmountDAO.saveExcess(excess);
			}

			// Saving record while doing receipt for OD loans
			if (isApproval) {
				overdrafLoanService.createPayment(rch);
			}

			// Save Repay Header details
			long repayID = financeRepaymentsDAO.saveFinRepayHeader(rph, TableType.MAIN_TAB);

			List<RepayScheduleDetail> rpySchdList = rph.getRepayScheduleDetails();
			if (CollectionUtils.isNotEmpty(rpySchdList)) {
				int i = 0;
				for (RepayScheduleDetail rpySchd : rpySchdList) {
					rpySchd.setFinID(rch.getFinID());
					rpySchd.setFinReference(rch.getReference());
					rpySchd.setRepayID(repayID);
					rpySchd.setRepaySchID(++i);
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

					}
				}

				// Save Repayment Schedule Details
				financeRepaymentsDAO.saveRpySchdList(rpySchdList, TableType.MAIN_TAB);
			}
		}

		allocationPaidMap = null;
		allocationWaivedMap = null;
		logger.debug(Literal.LEAVING);
	}

	private void createMAMovements(long receiptID, FinReceiptDetail rcd, long receiptSeqID) {
		for (ManualAdviseMovements movement : rcd.getAdvMovements()) {
			movement.setReceiptID(receiptID);
			movement.setReceiptSeqID(receiptSeqID);

			TaxHeader taxHeader = movement.getTaxHeader();
			if (taxHeader == null) {
				manualAdviseDAO.saveMovement(movement, "");
				continue;
			}

			List<Taxes> taxDetails = taxHeader.getTaxDetails();
			if (taxDetails == null) {
				manualAdviseDAO.saveMovement(movement, "");
				continue;
			}

			Long headerId = taxHeaderDetailsDAO.save(taxHeader, "");

			for (Taxes taxes : taxDetails) {
				taxes.setReferenceId(headerId);
				String taxType = taxes.getTaxType();

				switch (taxType) {
				case RuleConstants.CODE_CGST:
					movement.setPaidCGST(taxes.getPaidTax());
					movement.setWaivedCGST(taxes.getWaivedTax());
					break;
				case RuleConstants.CODE_SGST:
					movement.setPaidSGST(taxes.getPaidTax());
					movement.setWaivedSGST(taxes.getWaivedTax());
					break;
				case RuleConstants.CODE_IGST:
					movement.setPaidIGST(taxes.getPaidTax());
					movement.setWaivedIGST(taxes.getWaivedTax());
					break;
				case RuleConstants.CODE_UGST:
					movement.setPaidUGST(taxes.getPaidTax());
					movement.setWaivedUGST(taxes.getWaivedTax());
					break;
				case RuleConstants.CODE_CESS:
					movement.setPaidCESS(taxes.getPaidTax());
					movement.setWaivedCESS(taxes.getWaivedTax());
					break;
				default:
					break;
				}
			}
			taxHeaderDetailsDAO.saveTaxes(taxDetails, "");
			movement.setTaxHeaderId(headerId);

			manualAdviseDAO.saveMovement(movement, "");
		}
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

	/**
	 * Method for updating Fee Schedule Details based on Allocations and Paid Amounts
	 * 
	 * @param rpySchd
	 * @param allocationPaidMap
	 * @param allocationWaivedMap
	 */
	private void updateFeeDetails(RepayScheduleDetail rpySchd, List<FinFeeDetail> finFeeDetails,
			Map<String, BigDecimal> allocationPaidMap, Map<String, BigDecimal> allocationWaivedMap) {
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Fee Schedule Updation absed on Repayment Schedule date selection
	 */
	private FinFeeScheduleDetail feeSchdUpdation(FinFeeScheduleDetail feeSchd, BigDecimal paidBal, BigDecimal waivedBal,
			Map<String, BigDecimal> allocationPaidMap, Map<String, BigDecimal> allocationWaivedMap) {
		logger.debug(Literal.ENTERING);

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
			if (allocationPaidMap.containsKey(Allocation.FEE + "_" + feeSchd.getFeeID())) {
				BigDecimal remPaidBal = allocationPaidMap.get(Allocation.FEE + "_" + feeSchd.getFeeID());
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
				if (allocationWaivedMap.containsKey(Allocation.FEE + "_" + feeSchd.getFeeID())) {
					BigDecimal remWaivedBal = allocationWaivedMap.get(Allocation.FEE + "_" + feeSchd.getFeeID());
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
				if (allocationWaivedMap.containsKey(Allocation.FEE + "_" + feeSchd.getFeeID())) {
					BigDecimal remWaivedBal = allocationWaivedMap.get(Allocation.FEE + "_" + feeSchd.getFeeID());
					allocationWaivedMap.put(Allocation.FEE + "_" + feeSchd.getFeeID(), remWaivedBal.subtract(feeBal));
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
				if (allocationPaidMap.containsKey(Allocation.FEE + "_" + feeSchd.getFeeID())) {
					BigDecimal remPaidBal = allocationPaidMap.get(Allocation.FEE + "_" + feeSchd.getFeeID());
					allocationPaidMap.put(Allocation.FEE + "_" + feeSchd.getFeeID(), remPaidBal.subtract(feeBal));
				}
			}
		}

		feeSchd.setOsAmount(
				feeSchd.getSchAmount().subtract(feeSchd.getPaidAmount()).subtract(feeSchd.getWaiverAmount()));

		logger.debug(Literal.LEAVING);
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
	 */
	public FinanceMain updateStatus(FinanceMain financeMain, Date valueDate,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail profitDetail,
			List<FinODDetails> overdueList, String receiptPurpose, boolean isPresentProc) {
		return repaymentPostingsUtil.updateStatus(financeMain, valueDate, scheduleDetails, profitDetail, overdueList,
				receiptPurpose, isPresentProc);
	}

	private List<Object> doRepayPostings(FinanceDetail financeDetail, FinReceiptHeader rch,
			Map<String, BigDecimal> extDataMap, Map<String, Object> gstExecutionMap, Date postDate, long repayID)
			throws AppException {
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

				finRepayQueue.setFinID(fm.getFinID());
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

			// Setting Manual TDS to Map
			extDataMap.put("ae_manualTds", rch.getTdsAmount());

			BigDecimal totRecvAmount = BigDecimal.ZERO;
			List<ReceiptAllocationDetail> radList = rch.getAllocations();

			for (ReceiptAllocationDetail rad : radList) {
				String allocType = rad.getAllocationType();
				BigDecimal paidNow = rad.getPaidAmount();
				BigDecimal waivedNow = rad.getWaivedAmount();
				if (waivedNow.compareTo(BigDecimal.ZERO) <= 0 && rch.isClosureWithFullWaiver()) {
					waivedNow = rad.getWaivedAvailable();
				}

				switch (allocType) {
				case Allocation.EMI:
					break;
				case Allocation.PRI:
					rpyQueueHeader.setPrincipal(rpyQueueHeader.getPrincipal().add(paidNow));
					rpyQueueHeader.setPriWaived(rpyQueueHeader.getPriWaived().add(waivedNow));
					break;
				case Allocation.FUT_PRI:
					rpyQueueHeader.setPrincipal(rpyQueueHeader.getPrincipal().add(paidNow));
					rpyQueueHeader.setPriWaived(rpyQueueHeader.getPriWaived().add(waivedNow));

					rpyQueueHeader.setFutPrincipal(paidNow);
					rpyQueueHeader.setFutPriWaived(waivedNow);

					break;
				case Allocation.PP:
					rpyQueueHeader.setPrincipal(rpyQueueHeader.getPrincipal().add(paidNow));
					rpyQueueHeader.setPriWaived(rpyQueueHeader.getPriWaived().add(waivedNow));

					rpyQueueHeader.setPartialPaid(paidNow);

					break;
				case Allocation.PFT:
					rpyQueueHeader.setProfit(rpyQueueHeader.getProfit().add(paidNow));
					rpyQueueHeader.setPftWaived(rpyQueueHeader.getPftWaived().add(waivedNow));
					rpyQueueHeader.setTds(rpyQueueHeader.getTds().add(rad.getTdsPaid()));
					break;
				case Allocation.FUT_PFT:
					rpyQueueHeader.setProfit(rpyQueueHeader.getProfit().add(paidNow));
					rpyQueueHeader.setPftWaived(rpyQueueHeader.getPftWaived().add(waivedNow));
					rpyQueueHeader.setTds(rpyQueueHeader.getTds().add(rad.getTdsPaid()));

					rpyQueueHeader.setFutTds(rad.getTdsPaid());
					rpyQueueHeader.setFutProfit(paidNow);
					rpyQueueHeader.setFutPftWaived(waivedNow);
					break;
				case Allocation.LPFT:
					rpyQueueHeader.setLateProfit(rpyQueueHeader.getLateProfit().add(paidNow));
					rpyQueueHeader.setLatePftWaived(rpyQueueHeader.getLatePftWaived().add(waivedNow));
					rpyQueueHeader.setFeeTds(rpyQueueHeader.getFeeTds().add(rad.getTdsPaid()));
					break;
				case Allocation.ODC:
					rpyQueueHeader.setPenalty(rpyQueueHeader.getPenalty().add(paidNow));
					rpyQueueHeader.setPenaltyWaived(rpyQueueHeader.getPenaltyWaived().add(waivedNow));
					extDataMap.put("LPP_TDS_P", rad.getTdsPaid());
					extDataMap.put("LPP_TDS_W", rad.getTdsWaived());
					break;
				case Allocation.FEE:
					rpyQueueHeader.setFee(rpyQueueHeader.getFee().add(paidNow));
					rpyQueueHeader.setFeeWaived(rpyQueueHeader.getFeeWaived().add(waivedNow));
					break;
				case Allocation.MANADV:
				case Allocation.BOUNCE:
					rpyQueueHeader.setAdviseAmount(rpyQueueHeader.getAdviseAmount().add(paidNow).add(waivedNow));
					break;
				default:
					break;
				}

				if (!Allocation.EMI.equals(allocType) && !Allocation.PP.equals(allocType)) {
					totRecvAmount = totRecvAmount.add(paidNow);
				}
			}

			BigDecimal receiptAmount = rch.getReceiptAmount();
			if (FinServiceEvent.EARLYRPY.equals(rch.getReceiptPurpose())
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
			List<FinFeeDetail> finFeeDetailList = financeDetail.getFinScheduleData().getFinFeeDetailList();
			FinanceProfitDetail profitDetail = fsd.getFinPftDeatil();
			String eventCode = getEventCode(rch.getReceiptPurpose(), rch.getReceiptMode());

			returnList = repaymentPostingsUtil.postingProcess(fm, scheduleDetails, finFeeDetailList, profitDetail,
					rpyQueueHeader, eventCode, rch.getValueDate(), postDate, rch);

		} catch (InterfaceException e) {
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
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);
		return finRepayQueue;
	}

	public FinScheduleData getFinSchDataByFinRef(long finID, String type) {
		FinScheduleData schdData = new FinScheduleData();

		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));
		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, false));

		return schdData;
	}

	public void listSave(FinScheduleData schdData, String tableType, long logKey) {
		logger.debug(Literal.ENTERING);

		Map<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		for (int i = 0; i < schedules.size(); i++) {
			FinanceScheduleDetail curSchd = schedules.get(i);
			curSchd.setLastMntBy(fm.getLastMntBy());
			curSchd.setFinID(finID);
			curSchd.setFinReference(finReference);
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

		financeScheduleDetailDAO.saveList(schedules, tableType, false);

		// Schedule Version Updating
		if (StringUtils.isBlank(tableType)) {
			financeMainDAO.updateSchdVersion(fm, false);
		}

		if (logKey != 0) {
			// Finance Disbursement Details
			mapDateSeq = new HashMap<Date, Integer>();
			Date curBDay = SysParamUtil.getAppDate();

			for (FinanceDisbursement disbursement : schdData.getDisbursementDetails()) {
				disbursement.setFinID(finID);
				disbursement.setFinReference(finReference);
				disbursement.setDisbReqDate(curBDay);
				disbursement.setDisbIsActive(true);
				disbursement.setLogKey(logKey);
				disbursement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				disbursement.setLastMntBy(fm.getLastMntBy());
			}

			financeDisbursementDAO.saveList(schdData.getDisbursementDetails(), tableType, false);

		}

		// Finance Repay Instruction Details
		if (schdData.getRepayInstructions() != null) {
			for (int i = 0; i < schdData.getRepayInstructions().size(); i++) {
				RepayInstruction curSchd = schdData.getRepayInstructions().get(i);

				curSchd.setFinID(finID);
				curSchd.setFinReference(finReference);
				curSchd.setLogKey(logKey);
			}

			repayInstructionDAO.saveList(schdData.getRepayInstructions(), tableType, false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Fetching Accounting Event Code based on Finance Event Action
	 * 
	 * @param finEvent
	 * @return
	 */
	private String getEventCode(String finEvent, String receiptMode) {
		switch (finEvent) {
		case FinServiceEvent.SCHDRPY:
			if (ImplementationConstants.PRESENTMENT_STAGE_ACCOUNTING_REQ) {
				if (ReceiptMode.PRESENTMENT.equals(receiptMode)) {
					return AccountingEvent.PRSNT;
				}
			}
			return AccountingEvent.REPAY;
		case FinServiceEvent.EARLYRPY:
			return AccountingEvent.EARLYPAY;
		case FinServiceEvent.EARLYSETTLE:
			return AccountingEvent.EARLYSTL;
		case FinServiceEvent.RESTRUCTURE:
			return AccountingEvent.RESTRUCTURE;
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

	public void processAutoKnockOff(FinReceiptData receiptData) {
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
			if (Allocation.PRI.equals(allocate.getAllocationType())) {
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
		if (StringUtils.equals(FinServiceEvent.EARLYSETTLE, rch.getReceiptPurpose())) {
			reqMaxODDate = rch.getValueDate();
		}
		if (!ImplementationConstants.LPP_CALC_SOD) {
			reqMaxODDate = DateUtility.addDays(reqMaxODDate, -1);
		}
		List<FinODDetails> overdueList = finODDetailsDAO.getFinODBalByFinRef(fm.getFinID());
		if (CollectionUtils.isNotEmpty(overdueList)) {
			overdueList = receiptCalculator.calPenalty(scheduleData, receiptData, reqMaxODDate, overdueList);
			finODDetailsDAO.updateList(overdueList);
		}

		fm = updateStatus(fm, appDate, scheduleDetails, profitDetail, overdueList, rch.getReceiptPurpose(), false);

		financeMainDAO.updatePaymentInEOD(fm);
		limitManagement.processLoanRepay(fm, customerDetails.getCustomer(), priPaynow);
	}

	public void processSuccessPresentment(long receiptId) {
		FinReceiptHeader rch = finReceiptHeaderDAO.getReceiptHeaderByID(receiptId, "");
		if (rch == null) {
			return;
		}

		Date appDate = SysParamUtil.getAppDate();

		String excessAdjustTo = rch.getExcessAdjustTo();
		List<FinReceiptDetail> rcdDtls = finReceiptDetailDAO.getReceiptHeaderByID(receiptId, "");

		for (FinReceiptDetail recDtl : rcdDtls) {
			FinRepayHeader rph = financeRepaymentsDAO.getFinRepayHeadersByReceipt(recDtl.getReceiptSeqID(), "");
			// updating fixexcess amount after realization
			if (FinServiceEvent.SCHDRPY.equals(rch.getReceiptPurpose())) {
				if (rph != null && rph.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {
					finExcessAmountDAO.updExcessAfterRealize(rph.getFinID(), excessAdjustTo, rph.getExcessAmount());
				}
			}
		}

		finReceiptHeaderDAO.updateReceiptStatusAndRealizationDate(receiptId, RepayConstants.PAYSTATUS_REALIZED,
				appDate);
		finReceiptDetailDAO.updateReceiptStatusByReceiptId(receiptId, RepayConstants.PAYSTATUS_REALIZED);
	}

	public List<FinDueData> sortDueDetailsByRelHierarchy(List<FinDueData> dueDetails) {
		if (CollectionUtils.isEmpty(dueDetails)) {
			return dueDetails;
		}

		Collections.sort(dueDetails, new Comparator<FinDueData>() {
			@Override
			public int compare(FinDueData detail1, FinDueData detail2) {
				int i = detail1.getRelativeHierarchy() - detail2.getRelativeHierarchy();

				if (i != 0)
					return i;
				i = detail1.getDueDate().compareTo(detail2.getDueDate());
				if (i != 0)
					return i;

				return Integer.compare(detail1.getHierarchy(), detail2.getHierarchy());
			}
		});

		return dueDetails;
	}

	public void prepareDueData(FinReceiptData rd) {
		Date valueDate = rd.getValueDate();
		FinReceiptHeader rch = rd.getReceiptHeader();
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData fsd = fd.getFinScheduleData();
		List<FinanceScheduleDetail> schedules = fsd.getFinanceScheduleDetails();
		FinanceMain fm = fsd.getFinanceMain();
		String repayHierarchy = assetClassificationService.getNpaRepayHierarchy(fm.getFinID());

		List<FinDueData> duesList = new ArrayList<>();

		duesList.addAll(addAllocations(new FinReceiptData(), valueDate, new FinReceiptHeader(), schedules));
		duesList.addAll(addFeeAllocations(rch));
		duesList.addAll(addLatePayAllocations(fsd, valueDate));

		if (CollectionUtils.isEmpty(duesList)) {
			return;
		}

		duesList = duesList.stream().sorted((d1, d2) -> DateUtil.compare(d1.getDueDate(), d2.getDueDate()))
				.collect(Collectors.toList());

		if ("".equals(repayHierarchy)) {
			repayHierarchy = fsd.getFinanceType().getRpyHierarchy();
		}

		setFinDueDataByHierarchy(duesList, repayHierarchy);

		duesList = sortDueDetailsByRelHierarchy(duesList);
		rd.setDueDataList(duesList);
	}

	public FinReceiptData prepareFinDueData(FinReceiptData rd) {
		logger.debug(Literal.ENTERING);

		Date valueDate = rd.getValueDate();
		FinReceiptHeader rch = rd.getReceiptHeader();
		FinScheduleData fsd = rd.getFinanceDetail().getFinScheduleData();
		List<FinanceScheduleDetail> schdDtls = fsd.getFinanceScheduleDetails();

		List<FinDueData> dueDataDtls = addAllocations(rd, valueDate, rch, schdDtls);

		if (CollectionUtils.isEmpty(dueDataDtls)) {
			logger.debug(Literal.LEAVING);
			return rd;
		}

		dueDataDtls = dueDataDtls.stream().sorted((d1, d2) -> DateUtil.compare(d1.getDueDate(), d2.getDueDate()))
				.collect(Collectors.toList());

		setFinDueDataByHierarchy(dueDataDtls, fsd.getFinanceType().getRpyHierarchy());

		dueDataDtls = sortDueDetailsByRelHierarchy(dueDataDtls);
		rd.setDueDataList(dueDataDtls);

		logger.debug(Literal.LEAVING);
		return rd;
	}

	public List<FinDueData> addAllocations(FinReceiptData rd, Date valueDate, FinReceiptHeader rch,
			List<FinanceScheduleDetail> schdDtls) {
		List<FinDueData> dueDataDtls = new ArrayList<>();
		int schdIdx = 0;

		for (FinanceScheduleDetail curSchd : schdDtls) {
			if (DateUtil.compare(valueDate, curSchd.getSchDate()) >= 0) {
				if (!ImplementationConstants.ALLOW_OLDEST_DUE) {
					if (rd.isPresentment() && (DateUtil.compare(rch.getValueDate(), curSchd.getSchDate()) != 0)) {
						schdIdx++;
						continue;
					}
				}

				if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) > 0) {
					FinDueData dueData = new FinDueData();
					dueData.setAllocType(RepayConstants.DUETYPE_PRINICIPAL);
					dueData.setDueDate(curSchd.getSchDate());
					dueData.setDueAmount(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
					dueData.setSchdIdx(schdIdx);

					dueDataDtls.add(dueData);
				}

				if (curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) > 0) {
					FinDueData dueData = new FinDueData();
					dueData.setAllocType(RepayConstants.DUETYPE_PROFIT);
					dueData.setDueDate(curSchd.getSchDate());
					dueData.setSchdIdx(schdIdx);
					dueData.setDueAmount(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));

					dueDataDtls.add(dueData);
				}
			}

			schdIdx++;
		}
		return dueDataDtls;
	}

	public List<FinDueData> addLatePayAllocations(FinScheduleData fsd, Date valueDate) {
		List<FinDueData> duesList = new ArrayList<>();

		int schdIdx = 0;
		for (FinODDetails fod : fsd.getFinODDetails()) {
			BigDecimal lpiBal = fod.getLPIBal();
			BigDecimal lppBal = fod.getTotPenaltyBal();

			if (DateUtil.compare(valueDate, fod.getFinODSchdDate()) >= 0) {
				if (lppBal.compareTo(BigDecimal.ZERO) > 0) {
					FinDueData dueData = new FinDueData();
					dueData.setAllocType(RepayConstants.DUETYPE_ODC);
					dueData.setDueDate(fod.getFinODSchdDate());
					dueData.setDueAmount(lppBal);
					dueData.setSchdIdx(schdIdx);

					duesList.add(dueData);
				}
				if (lpiBal.compareTo(BigDecimal.ZERO) > 0) {
					FinDueData dueData = new FinDueData();
					dueData.setAllocType(RepayConstants.DUETYPE_LPFT);
					dueData.setDueDate(fod.getFinODSchdDate());
					dueData.setDueAmount(lpiBal);
					dueData.setSchdIdx(schdIdx);

					duesList.add(dueData);
				}
			}

			schdIdx++;
		}

		return duesList;
	}

	public List<FinDueData> addFeeAllocations(FinReceiptHeader rch) {
		List<FinDueData> duesList = new ArrayList<>();
		for (ReceiptAllocationDetail rad : rch.getAllocations()) {
			if (rad.getAllocationTo() == 0) {
				continue;
			}

			String allocationType = rad.getAllocationType();
			FinDueData dueData = new FinDueData();

			switch (allocationType) {
			case Allocation.MANADV:
				dueData.setAllocType(RepayConstants.DUETYPE_MANUALADVISE);
				dueData.setAdviseId(rad.getAllocationTo());
				dueData.setDueDate(rad.getValueDate());
				dueData.setDueAmount(rad.getTotalDue());

				duesList.add(dueData);
				break;
			case Allocation.BOUNCE:
				dueData.setAllocType(RepayConstants.DUETYPE_BOUNCE);
				dueData.setAdviseId(rad.getAllocationTo());
				dueData.setDueDate(rad.getValueDate());
				dueData.setDueAmount(rad.getTotalDue());

				duesList.add(dueData);
				break;
			case Allocation.FEE:
				dueData.setAllocType(RepayConstants.DUETYPE_FEES);
				dueData.setFeeTypeCode(rad.getFeeTypeCode());
				dueData.setDueDate(rad.getValueDate());
				dueData.setDueAmount(rad.getTotalDue());

				duesList.add(dueData);
				break;
			default:
				break;
			}
		}

		return duesList;
	}

	public void setFinDueDataByHierarchy(List<FinDueData> dueDataDtls, String rpyHierarchy) {
		String[] relHierarchy = rpyHierarchy.split("\\|");
		int index = 0;

		for (String rh : relHierarchy) {
			int hierarchy = 0;
			String[] hier = rh.split(",");
			for (String hi : hier) {
				for (FinDueData dueData : dueDataDtls) {
					if (!dueData.isAdjust()) {
						if (hi.equals(dueData.getAllocType())) {
							dueData.setRelativeHierarchy(index);
							dueData.setHierarchy(hierarchy);
							dueData.setAdjust(true);
						} else {
							dueData.setAdjust(false);
						}
					}
				}
				hierarchy++;
			}

			index++;
		}
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	@Autowired
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	@Autowired
	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	@Autowired
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	@Autowired
	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	@Autowired
	public void setAllocationDetailDAO(ReceiptAllocationDetailDAO allocationDetailDAO) {
		this.allocationDetailDAO = allocationDetailDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	@Autowired
	public void setFinStageAccountingLogDAO(FinStageAccountingLogDAO finStageAccountingLogDAO) {
		this.finStageAccountingLogDAO = finStageAccountingLogDAO;
	}

	@Autowired
	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	@Autowired
	public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
		this.assignmentDAO = assignmentDAO;
	}

	@Autowired
	public void setAssignmentDealDAO(AssignmentDealDAO assignmentDealDAO) {
		this.assignmentDealDAO = assignmentDealDAO;
	}

	@Autowired
	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	@Autowired
	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	@Autowired
	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		this.overdrafLoanService = overdrafLoanService;
	}

	@Autowired
	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	@Autowired
	public void setLoanPaymentService(LoanPaymentService loanPaymentService) {
		this.loanPaymentService = loanPaymentService;
	}

	@Autowired
	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	@Autowired
	public void setAssetClassificationService(AssetClassificationService assetClassificationService) {
		this.assetClassificationService = assetClassificationService;
	}

}
