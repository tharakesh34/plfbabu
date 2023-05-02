/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : RepaymentPostingsUtil.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.LatePayBucketService;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentDealDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinODCAmountDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueueHeader;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.model.applicationmaster.AssignmentDealExcludedFee;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinOverDueChargeMovement;
import com.pennant.backend.model.finance.FinOverDueCharges;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.ScheduleDueTaxDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.extension.LPPExtension;
import com.pennant.pff.receipt.ClosureType;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.util.FinanceUtil;
import com.pennanttech.pff.core.util.ProductUtil;
import com.pennanttech.pff.npa.service.AssetClassificationService;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;
import com.pennanttech.pff.payment.model.LoanPayment;
import com.pennanttech.pff.payment.service.LoanPaymentService;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class RepaymentPostingsUtil {
	private static Logger logger = LogManager.getLogger(RepaymentPostingsUtil.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private CustomerStatusCodeDAO customerStatusCodeDAO;
	private FinStatusDetailDAO finStatusDetailDAO;
	private PostingsDAO postingsDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private CustomerDAO customerDAO;
	private FinODDetailsDAO finODDetailsDAO;
	protected FinanceTypeDAO financeTypeDAO;
	private AssignmentDAO assignmentDAO;
	private AssignmentDealDAO assignmentDealDAO;
	private FeeTypeDAO feeTypeDAO;

	protected GSTInvoiceTxnService gstInvoiceTxnService;
	private OverdrafLoanService overdrafLoanService;
	private LatePayMarkingService latePayMarkingService;
	private LatePayBucketService latePayBucketService;
	private AccrualService accrualService;
	private LoanPaymentService loanPaymentService;

	private OverDueRecoveryPostingsUtil recoveryPostingsUtil;
	private PostingsPreparationUtil postingsPreparationUtil;
	private AssetClassificationService assetClassificationService;
	private FeeTypeService feeTypeService;
	private FinODCAmountDAO finODCAmountDAO;

	public RepaymentPostingsUtil() {
		super();
	}

	public List<Object> postingProcess(FinanceMain fm, List<FinanceScheduleDetail> schedules, List<FinFeeDetail> fees,
			FinanceProfitDetail fpd, FinRepayQueueHeader rqh, String eventCode, Date valuedate, Date postDate,
			FinReceiptHeader rch) throws AppException {

		return postingProcessExecution(fm, schedules, fees, fpd, rqh, eventCode, valuedate, postDate, rch);
	}

	private List<Object> postingProcessExecution(FinanceMain fm, List<FinanceScheduleDetail> schedules,
			List<FinFeeDetail> fees, FinanceProfitDetail fpd, FinRepayQueueHeader rqh, String eventCode, Date valuedate,
			Date postDate, FinReceiptHeader rch) throws AppException {
		logger.debug(Literal.ENTERING);

		List<Object> actReturnList = null;
		AEEvent aeEvent = null;
		int transOrder = 0;

		// Repayments Queue list
		List<FinRepayQueue> finRepayQueueList = rqh.getQueueList();

		// Penalty Payments, if any Payment calculations done
		FinTaxIncomeDetail taxIncome = null;
		if (rqh.getPenalty().compareTo(BigDecimal.ZERO) > 0 || rqh.getPenaltyWaived().compareTo(BigDecimal.ZERO) > 0
				|| rqh.getLateProfit().compareTo(BigDecimal.ZERO) > 0
				|| rqh.getLatePftWaived().compareTo(BigDecimal.ZERO) > 0) {
			List<Object> returnList = doOverduePostings(aeEvent, finRepayQueueList, postDate, valuedate, fm, rqh);

			aeEvent = (AEEvent) returnList.get(0);

			if (aeEvent != null) {
				aeEvent.setSimulateAccounting(fm.isSimulateAccounting());
				if (!aeEvent.isPostingSucess()) {
					actReturnList = new ArrayList<Object>(2);
					actReturnList.add(false);
					actReturnList.add(aeEvent.getErrorMessage());
					return actReturnList;
				}
			}
			taxIncome = (FinTaxIncomeDetail) returnList.get(1);
		}

		// Total Schedule Payments
		BigDecimal totalPayAmount = rqh.getPrincipal().add(rqh.getProfit()).add(rqh.getLateProfit()).add(rqh.getFee())
				.add(rqh.getInsurance()).add(rqh.getPenalty());

		BigDecimal totalWaivedAmount = rqh.getPriWaived().add(rqh.getPftWaived()).add(rqh.getLatePftWaived())
				.add(rqh.getFeeWaived()).add(rqh.getPenaltyWaived()).add(rqh.getAdviseAmount());

		boolean bouncePaidExists = true;

		if ((totalPayAmount.add(totalWaivedAmount)).compareTo(BigDecimal.ZERO) > 0 || bouncePaidExists) {
			actReturnList = doSchedulePostings(rqh, valuedate, postDate, fm, schedules, fees, fpd, eventCode, aeEvent,
					rch);
			if ((boolean) actReturnList.get(0)) {
				transOrder = (int) actReturnList.get(7);
			}
		} else if (rqh.getLatePftWaived().compareTo(BigDecimal.ZERO) > 0) {

			// Method for Postings Process only for Late Pay Profit Waiver case
			aeEvent = postingEntryProcess(valuedate, postDate, valuedate, false, fm, schedules, fpd, rqh, aeEvent,
					eventCode, fees, rch);

			// Database Updations for Finance RepayQueue Details List
			List<FinODDetails> latePftODTotals = new ArrayList<>();
			for (FinRepayQueue repayQueue : finRepayQueueList) {
				BigDecimal latePayPftWaivedNow = repayQueue.getLatePayPftWaivedNow();

				if (latePayPftWaivedNow.compareTo(BigDecimal.ZERO) > 0) {
					FinODDetails od = new FinODDetails();
					od.setFinID(repayQueue.getFinID());
					od.setFinReference(repayQueue.getFinReference());
					od.setFinODSchdDate(repayQueue.getRpyDate());
					od.setPaidNow(BigDecimal.ZERO);
					od.setWaivedNow(latePayPftWaivedNow);
					latePftODTotals.add(od);
				}
			}

			if (CollectionUtils.isNotEmpty(latePftODTotals)) {
				finODDetailsDAO.updateLatePftTotals(latePftODTotals);
			}

			if (actReturnList == null) {
				actReturnList = new ArrayList<>();
			}
			actReturnList.clear();
			actReturnList.add(aeEvent.isPostingSucess());
			actReturnList.add(aeEvent.getLinkedTranId());// Linked Transaction ID
			actReturnList.add(schedules); // Schedule Details
			actReturnList.add(BigDecimal.ZERO); // UnRealized Amortized Amount

			// LPI Income details
			actReturnList.add(BigDecimal.ZERO); // UnRealized LPI Amount
			actReturnList.add(BigDecimal.ZERO); // UnRealized LPI GST Amount
			actReturnList.add(BigDecimal.ZERO); // capitalize Difference
			actReturnList.add(aeEvent.getTransOrder());
		} else {
			if (actReturnList == null) {
				actReturnList = new ArrayList<Object>();
			}
			actReturnList.clear();
			actReturnList.add(true); // Postings Success
			if (aeEvent != null) {
				actReturnList.add(aeEvent.getLinkedTranId()); // Linked Transaction ID
			} else {
				actReturnList.add(Long.MIN_VALUE); // Linked Transaction ID
			}
			actReturnList.add(schedules); // Schedule Details
			actReturnList.add(BigDecimal.ZERO); // UnRealized Amortized Amount

			// LPI Income details
			actReturnList.add(BigDecimal.ZERO); // UnRealized LPI Amount
			actReturnList.add(BigDecimal.ZERO); // UnRealized LPI GST Amount

			actReturnList.add(BigDecimal.ZERO); // capitalize Difference
			if (aeEvent != null) {
				actReturnList.add(aeEvent.getTransOrder()); // trans order
			} else {
				actReturnList.add(transOrder); // trans order
			}
		}

		// LPP Income details
		actReturnList.add(taxIncome); // UnRealized LPP Amount & LPP GST

		logger.debug(Literal.LEAVING);
		return actReturnList;
	}

	private List<Object> doOverduePostings(AEEvent aeEvent, List<FinRepayQueue> finRepayQueueList, Date postDate,
			Date dateValueDate, FinanceMain fm, FinRepayQueueHeader repayQueueHeader) throws AppException {
		logger.info(Literal.ENTERING);

		// repayQueueHeader.setLppAmzReqonME(feeType.isAmortzReq());

		ManualAdviseMovements movement = getManualAdvise(finRepayQueueList, dateValueDate);

		if (movement != null) {
			repayQueueHeader.setLppAmzReqonME(movement.isLppAmzReqonME());
			movement.setTaxHeader(getTaxSummaryHeader(finRepayQueueList));
		}

		// GST Invoice Preparation for Penalty (Debit Note)
		FinTaxIncomeDetail taxIncome = null;

		if (movement != null && (movement.getPaidAmount().compareTo(BigDecimal.ZERO) > 0
				|| movement.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0)) {
			List<Object> returnList = recoveryPostingsUtil.recoveryPayment(fm, dateValueDate, postDate, movement,
					dateValueDate, aeEvent, repayQueueHeader);

			aeEvent = (AEEvent) returnList.get(0);
			if (!aeEvent.isPostingSucess()) {
				logger.info(Literal.LEAVING);
				return returnList;
			}

			taxIncome = (FinTaxIncomeDetail) returnList.get(1);

			// Commented updating finOddetails table because updation is being done in Scheduleupdate()
			// Overdue Details Updation for Paid Penalty
			/*
			 * for (FinRepayQueue repayQueue : finRepayQueueList) { if (repayQueue.getRpyDate().compareTo(dateValueDate)
			 * >= 0) { continue; }
			 * 
			 * BigDecimal totPenalty = repayQueue.getPenaltyPayNow().add(repayQueue.getWaivedAmount());
			 * 
			 * if (totPenalty.compareTo(BigDecimal.ZERO) <= 0) { continue; }
			 * 
			 * FinODDetails detail = new FinODDetails(); detail.setFinReference(financeMain.getFinReference());
			 * detail.setFinODSchdDate(repayQueue.getRpyDate()); detail.setFinODFor(repayQueue.getFinRpyFor());
			 * detail.setTotPenaltyAmt(BigDecimal.ZERO); detail.setTotPenaltyPaid(repayQueue.getPenaltyPayNow());
			 * 
			 * if (repayQueue.getPenaltyPayNow().add(repayQueue.getWaivedAmount()).compareTo(BigDecimal.ZERO) < 0) {
			 * detail.setTotPenaltyBal((repayQueue.getPenaltyPayNow().add(repayQueue.getWaivedAmount())).negate()); }
			 * else { detail.setTotPenaltyBal((repayQueue.getPenaltyPayNow().add(repayQueue.getWaivedAmount()))); }
			 * detail.setTotWaived(repayQueue.getWaivedAmount());
			 * 
			 * if (!aeEvent.isSimulateAccounting()) { finODDetailsDAO.updateTotals(detail); } }
			 */
		}

		List<Object> returnList = new ArrayList<>();
		returnList.add(aeEvent);
		returnList.add(taxIncome);

		logger.info(Literal.LEAVING);
		return returnList;
	}

	private TaxHeader getTaxSummaryHeader(List<FinRepayQueue> finRepayQueueList) {
		TaxHeader taxHeader = new TaxHeader();

		List<Taxes> taxes = new ArrayList<>(5);

		Taxes cgst = new Taxes();
		cgst.setTaxType(RuleConstants.CODE_CGST);

		Taxes sgst = new Taxes();
		sgst.setTaxType(RuleConstants.CODE_SGST);

		Taxes igst = new Taxes();
		igst.setTaxType(RuleConstants.CODE_IGST);

		Taxes ugst = new Taxes();
		ugst.setTaxType(RuleConstants.CODE_UGST);

		Taxes cess = new Taxes();
		cess.setTaxType(RuleConstants.CODE_CESS);

		taxes.add(cgst);
		taxes.add(sgst);
		taxes.add(igst);
		taxes.add(ugst);
		taxes.add(cess);

		taxHeader.setTaxDetails(taxes);

		BigDecimal cgstPaid = BigDecimal.ZERO;
		BigDecimal sgstPaid = BigDecimal.ZERO;
		BigDecimal igstPaid = BigDecimal.ZERO;
		BigDecimal ugstPaid = BigDecimal.ZERO;
		BigDecimal cessPaid = BigDecimal.ZERO;

		BigDecimal cgstWaived = BigDecimal.ZERO;
		BigDecimal sgstWaived = BigDecimal.ZERO;
		BigDecimal igstWaived = BigDecimal.ZERO;
		BigDecimal ugstWaived = BigDecimal.ZERO;
		BigDecimal cessWaived = BigDecimal.ZERO;

		for (FinRepayQueue repayQueue : finRepayQueueList) {
			TaxHeader header = repayQueue.getTaxHeader();
			if (header == null || CollectionUtils.isEmpty(header.getTaxDetails())) {
				continue;
			}

			for (Taxes tax : header.getTaxDetails()) {
				switch (tax.getTaxType()) {
				case RuleConstants.CODE_CGST:
					cgstPaid = cgst.getPaidTax();
					cgstPaid = cgstPaid.add(tax.getPaidTax());
					cgst.setPaidTax(cgstPaid);
					cgstWaived = cgst.getWaivedTax();
					cgstWaived = cgstWaived.add(tax.getWaivedTax());
					cgst.setWaivedTax(cgstWaived);
					cgst.setTaxPerc(tax.getTaxPerc());
					break;
				case RuleConstants.CODE_SGST:
					sgstPaid = sgst.getPaidTax();
					sgstPaid = sgstPaid.add(tax.getPaidTax());
					sgst.setPaidTax(sgstPaid);
					sgstWaived = sgst.getWaivedTax();
					sgstWaived = sgstWaived.add(tax.getWaivedTax());
					sgst.setWaivedTax(sgstWaived);
					sgst.setTaxPerc(tax.getTaxPerc());
					break;
				case RuleConstants.CODE_IGST:
					igstPaid = igst.getPaidTax();
					igstPaid = igstPaid.add(tax.getPaidTax());
					igst.setPaidTax(igstPaid);
					igstWaived = igst.getWaivedTax();
					igstWaived = igstWaived.add(tax.getWaivedTax());
					igst.setWaivedTax(igstWaived);
					igst.setTaxPerc(tax.getTaxPerc());
					break;
				case RuleConstants.CODE_UGST:
					ugstPaid = ugst.getPaidTax();
					ugstPaid = ugstPaid.add(tax.getPaidTax());
					ugst.setPaidTax(ugstPaid);
					ugstWaived = ugst.getWaivedTax();
					ugstWaived = ugstWaived.add(tax.getWaivedTax());
					ugst.setWaivedTax(ugstWaived);
					ugst.setTaxPerc(tax.getTaxPerc());
					break;
				case RuleConstants.CODE_CESS:
					cessPaid = cess.getPaidTax();
					cessPaid = cessPaid.add(tax.getPaidTax());
					cess.setPaidTax(cessPaid);
					cessWaived = cess.getWaivedTax();
					cessWaived = cessWaived.add(tax.getWaivedTax());
					cess.setWaivedTax(cessWaived);
					cess.setTaxPerc(tax.getTaxPerc());
					break;

				default:
					break;
				}
			}
		}

		return taxHeader;
	}

	private ManualAdviseMovements getManualAdvise(List<FinRepayQueue> finRepayQueueList, Date dateValueDate) {
		ManualAdviseMovements movement = null;
		FeeType feeType = null;

		for (FinRepayQueue repayQueue : finRepayQueueList) {
			if (repayQueue.getRpyDate().compareTo(dateValueDate) >= 0) {
				continue;
			}

			if (feeType == null) {
				feeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(PennantConstants.FEETYPE_ODC);
			}

			if (movement == null) {
				movement = new ManualAdviseMovements();
			}

			if (feeType != null) {
				movement.setFeeTypeCode(feeType.getFeeTypeCode());
				movement.setFeeTypeDesc(feeType.getFeeTypeDesc());
				movement.setTaxApplicable(feeType.isTaxApplicable());
				movement.setTaxComponent(feeType.getTaxComponent());
				movement.setLppAmzReqonME(feeType.isAmortzReq());
				movement.setTdsReq(feeType.isTdsReq());
			} else {
				logger.warn("{} Fee code not configured in fee type master", PennantConstants.FEETYPE_ODC);
			}

			// Paid and Waived Amounts
			movement.setMovementAmount(movement.getMovementAmount().add(repayQueue.getPenaltyPayNow()));
			movement.setPaidAmount(movement.getPaidAmount().add(repayQueue.getPenaltyPayNow()));
			movement.setWaivedAmount(movement.getWaivedAmount().add(repayQueue.getWaivedAmount()));
		}

		return movement;
	}

	private List<Object> doSchedulePostings(FinRepayQueueHeader rqh, Date valueDate, Date postDate, FinanceMain fm,
			List<FinanceScheduleDetail> schedules, List<FinFeeDetail> fees, FinanceProfitDetail fpd, String eventCode,
			AEEvent aeEvent, FinReceiptHeader rch) throws AppException {
		logger.debug(Literal.ENTERING);

		List<Object> actReturnList = new ArrayList<>();

		// Method for Postings Process
		aeEvent = postingEntryProcess(valueDate, postDate, valueDate, false, fm, schedules, fpd, rqh, aeEvent,
				eventCode, fees, rch);

		if (!aeEvent.isPostingSucess()) {
			actReturnList.add(aeEvent.isPostingSucess());
			actReturnList.add("9999"); // FIXME

			logger.debug(Literal.LEAVING);
			return actReturnList;
		}

		// Schedule updations
		schedules = scheduleUpdate(fm, schedules, rqh, aeEvent.getLinkedTranId(), aeEvent.getValueDate(),
				aeEvent.getPostDate());

		actReturnList.add(aeEvent.isPostingSucess());
		actReturnList.add(aeEvent.getLinkedTranId());
		actReturnList.add(schedules); // Schedule Details

		// Unrealized Amortized Amount
		if (aeEvent.isuAmzExists()) {
			actReturnList.add(aeEvent.getAeAmountCodes().getuAmz());
		} else {
			actReturnList.add(BigDecimal.ZERO);
		}

		// LPI Income Details
		actReturnList.add(BigDecimal.ZERO);
		actReturnList.add(BigDecimal.ZERO);

		// Capitalization Difference
		if (aeEvent.isCpzChgExists()) {
			actReturnList.add(aeEvent.getAeAmountCodes().getCpzChg());
		} else {
			actReturnList.add(BigDecimal.ZERO);
		}
		actReturnList.add(aeEvent.getTransOrder());

		if (fm.isSimulateAccounting()) {
			if (CollectionUtils.isNotEmpty(fm.getReturnDataSet())) {
				fm.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
			} else {
				fm.setReturnDataSet(aeEvent.getReturnDataSet());
			}
		}

		logger.debug(Literal.LEAVING);
		return actReturnList;
	}

	// *************************************************************************//
	// **************************** Schedule Updations *************************//
	// *************************************************************************//

	private List<FinanceScheduleDetail> scheduleUpdate(FinanceMain financeMain,
			List<FinanceScheduleDetail> scheduleDetails, FinRepayQueueHeader rpyQueueHeader, long linkedTranId,
			Date valueDate, Date postDate) throws AppException {
		logger.debug(Literal.ENTERING);

		// Total Payment Amount
		BigDecimal rpyTotal = rpyQueueHeader.getPrincipal().add(rpyQueueHeader.getProfit()).add(rpyQueueHeader.getFee())
				.add(rpyQueueHeader.getLateProfit()).add(rpyQueueHeader.getInsurance())
				.add(rpyQueueHeader.getPenalty());

		// Total Payment Amount
		BigDecimal waivedTotal = rpyQueueHeader.getPriWaived().add(rpyQueueHeader.getPftWaived())
				.add(rpyQueueHeader.getFeeWaived()).add(rpyQueueHeader.getLatePftWaived())
				.add(rpyQueueHeader.getPenaltyWaived());

		// If Postings Process only for Excess Accounts
		if ((rpyTotal.add(waivedTotal)).compareTo(BigDecimal.ZERO) == 0) {
			logger.debug(Literal.LEAVING);
			return scheduleDetails;
		}

		List<FinRepayQueue> finRepayQueueList = rpyQueueHeader.getQueueList();

		Map<Date, FinanceScheduleDetail> scheduleMap = new HashMap<>();
		for (FinanceScheduleDetail detail : scheduleDetails) {
			scheduleMap.put(DateUtil.getSqlDate(detail.getSchDate()), detail);
		}

		// Database Updations for Finance RepayQueue Details List

		List<FinODDetails> latePftODTotals = new ArrayList<>();
		List<FinODDetails> odDetails = new ArrayList<>();
		List<FinanceRepayments> repayments = new ArrayList<>();

		for (FinRepayQueue repayQueue : finRepayQueueList) {
			FinanceScheduleDetail scheduleDetail = null;
			Date repayDate = DateUtil.getSqlDate(repayQueue.getRpyDate());
			if (scheduleMap.containsKey(repayDate)) {
				scheduleDetail = scheduleMap.get(repayDate);

				scheduleDetail = updateScheduleDetailsData(scheduleDetail, repayQueue);
				Date sqlDate = DateUtil.getSqlDate(scheduleDetail.getSchDate());
				scheduleMap.put(sqlDate, scheduleDetail);

				FinODDetails latePftODTotal = getLatePftODTotal(repayQueue);

				if (latePftODTotal != null) {
					if (LPPExtension.LPP_DUE_CREATION_REQ && !financeMain.isSimulateAccounting()
							&& (repayQueue.getLatePayPftPayNow().compareTo(BigDecimal.ZERO) > 0
									|| repayQueue.getLatePayPftWaivedNow().compareTo(BigDecimal.ZERO) > 0)) {
						saveFinLPPAmount(repayQueue, valueDate, rpyQueueHeader, latePftODTotal);
					}

					latePftODTotals.add(latePftODTotal);
				}

				FinODDetails odDetail = getODDetail(repayQueue);
				if (odDetail != null) {
					if (LPPExtension.LPP_DUE_CREATION_REQ && !financeMain.isSimulateAccounting()
							&& (repayQueue.getPenaltyPayNow().compareTo(BigDecimal.ZERO) > 0
									|| repayQueue.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0)) {
						saveFinODCAmount(repayQueue, valueDate, rpyQueueHeader, odDetail);
					}

					odDetails.add(odDetail);
				}

				long receiptId = rpyQueueHeader.getReceiptId();
				repayments.add(prepareRepayDetail(repayQueue, valueDate, postDate, linkedTranId, rpyTotal, receiptId));
			}
		}

		// Reset Finance Schedule Details
		scheduleDetails = new ArrayList<FinanceScheduleDetail>(scheduleMap.values());
		scheduleDetails = ScheduleCalculator.sortSchdDetails(scheduleDetails);
		if (!financeMain.isSimulateAccounting()) {
			if (CollectionUtils.isNotEmpty(latePftODTotals)) {
				finODDetailsDAO.updateLatePftTotals(latePftODTotals);
			}

			// PSD#182172 - Commented updating finOddetails table because already updation is being done in
			// doOverduePostings()
			// because of updating twice penalty amounts are being updated incorrectly in table.

			if (CollectionUtils.isNotEmpty(odDetails)) {
				finODDetailsDAO.updateTotals(odDetails);
			}

			financeRepaymentsDAO.save(repayments, "");
		}

		logger.debug(Literal.LEAVING);
		return scheduleDetails;
	}

	private FinODDetails getLatePftODTotal(FinRepayQueue repayQueue) {
		BigDecimal latePayPftPayNow = repayQueue.getLatePayPftPayNow();
		BigDecimal latePayPftWaivedNow = repayQueue.getLatePayPftWaivedNow();

		if (latePayPftPayNow.compareTo(BigDecimal.ZERO) > 0 || latePayPftWaivedNow.compareTo(BigDecimal.ZERO) > 0) {
			FinODDetails od = new FinODDetails();
			od.setFinID(repayQueue.getFinID());
			od.setFinReference(repayQueue.getFinReference());
			od.setFinODSchdDate(repayQueue.getRpyDate());
			od.setPaidNow(latePayPftPayNow);
			od.setWaivedNow(latePayPftWaivedNow);

			return od;
		}
		return null;

	}

	private FinODDetails getODDetail(FinRepayQueue repayQueue) {
		BigDecimal penaltyPayNow = repayQueue.getPenaltyPayNow();
		BigDecimal waivedAmount = repayQueue.getWaivedAmount();

		if (penaltyPayNow.compareTo(BigDecimal.ZERO) > 0 || waivedAmount.compareTo(BigDecimal.ZERO) > 0) {
			FinODDetails od = new FinODDetails();
			od.setFinID(repayQueue.getFinID());
			od.setFinReference(repayQueue.getFinReference());
			od.setFinODSchdDate(repayQueue.getRpyDate());
			od.setFinODFor(repayQueue.getFinRpyFor());
			od.setTotPenaltyAmt(BigDecimal.ZERO);
			od.setTotPenaltyPaid(penaltyPayNow);
			od.setTotPenaltyBal((penaltyPayNow.add(waivedAmount)).negate());
			od.setTotWaived(waivedAmount);

			return od;
		}

		return null;
	}

	private void saveFinODCAmount(FinRepayQueue repayQueue, Date valueDate, FinRepayQueueHeader rqh,
			FinODDetails finOd) {
		FinOverDueCharges odc = null;
		boolean createLppDue = true;
		FinODDetails odDetail = null;
		BigDecimal waivedAmount = repayQueue.getWaivedAmount();
		BigDecimal penaltyPayNow = repayQueue.getPenaltyPayNow();
		Date appDate = SysParamUtil.getAppDate();

		List<FinOverDueChargeMovement> movements = new ArrayList<>();

		List<FinOverDueCharges> finODCAmounts = finODCAmountDAO.getFinODCAmtByFinRef(repayQueue.getFinID(),
				repayQueue.getRpyDate(), RepayConstants.FEE_TYPE_LPP);

		if (CollectionUtils.isNotEmpty(rqh.getFinOdList())) {
			for (FinODDetails fod : rqh.getFinOdList()) {
				if (fod.getFinODSchdDate().compareTo(repayQueue.getRpyDate()) != 0) {
					continue;
				}
				odDetail = fod;

				if (odDetail.getLppDueAmt().compareTo(fod.getTotPenaltyAmt()) >= 0) {
					createLppDue = false;
					finOd.setLppDueAmt(fod.getLppDueAmt());
					break;
				}
				if (fod.getLppDueTillDate() != null) {
					if (valueDate.compareTo(fod.getLppDueTillDate()) > 0) {
						for (FinOverDueCharges odcAmount : finODCAmounts) {
							if (odcAmount.getValueDate().compareTo(valueDate) == 0) {
								odc = odcAmount;
								break;
							}
						}
					} else {
						createLppDue = false;
					}

				}
			}
		}

		if (odDetail != null) {

			BigDecimal prvMnthPenaltyAmt = BigDecimal.ZERO;
			for (FinOverDueCharges odchargeAmt : finODCAmounts) {
				// if (dateValueDate.compareTo(odchargeAmt.getValueDate())>=0){
				prvMnthPenaltyAmt = prvMnthPenaltyAmt.add(odchargeAmt.getAmount());
				BigDecimal balanceAmt = odchargeAmt.getBalanceAmt();
				BigDecimal payNow = BigDecimal.ZERO;
				BigDecimal waiveNow = BigDecimal.ZERO;

				if (balanceAmt.compareTo(BigDecimal.ZERO) > 0) {

					// Waived Amount Update
					if (waivedAmount.compareTo(balanceAmt) > 0) {
						waiveNow = balanceAmt;
						balanceAmt = BigDecimal.ZERO;
					} else {
						waiveNow = waivedAmount;
						balanceAmt = balanceAmt.subtract(waiveNow);
					}
					// Paid Amount Update
					if (penaltyPayNow.compareTo(balanceAmt) > 0) {
						payNow = balanceAmt;
						balanceAmt = BigDecimal.ZERO;
					} else {
						payNow = penaltyPayNow;
						balanceAmt = balanceAmt.subtract(payNow);
					}
					waivedAmount = waivedAmount.subtract(waiveNow);
					penaltyPayNow = penaltyPayNow.subtract(payNow);
				}

				if (waiveNow.compareTo(BigDecimal.ZERO) > 0 || payNow.compareTo(BigDecimal.ZERO) > 0) {
					for (FinOverDueCharges odcAmt : finODCAmounts) {
						BigDecimal balAmt = odcAmt.getBalanceAmt();
						if (balAmt.compareTo(BigDecimal.ZERO) > 0 && odcAmt.getId() == odchargeAmt.getId()) {
							odcAmt.setPaidAmount(odcAmt.getPaidAmount().add(payNow));
							odcAmt.setWaivedAmount(odcAmt.getWaivedAmount().add(waiveNow));
							odcAmt.setBalanceAmt(odcAmt.getBalanceAmt().subtract(payNow.add(waiveNow)));
							FinOverDueChargeMovement movement = new FinOverDueChargeMovement();
							movement.setMovementDate(appDate);
							movement.setChargeId(odcAmt.getId());
							movement.setMovementAmount(payNow.add(waiveNow));
							movement.setPaidAmount(payNow);
							movement.setWaivedAmount(waiveNow);
							movement.setReceiptID(rqh.getReceiptId());
							movements.add(movement);
						}
					}
				}

				if (valueDate.compareTo(odchargeAmt.getValueDate()) == 0) {
					odchargeAmt.setOdPri(odDetail.getFinCurODPri());
					odchargeAmt.setOdPft(odDetail.getFinCurODPft());
				}
				// }
			}

			finOd.setLppDueTillDate(odDetail.getLppDueTillDate());
			finOd.setLppDueAmt(odDetail.getLppDueAmt());

			if (createLppDue) {
				finOd.setLppDueTillDate(valueDate);
				if (odc == null) {
					odDetail.setTotPenaltyAmt(odDetail.getTotPenaltyAmt().subtract(prvMnthPenaltyAmt));

					if (penaltyPayNow.compareTo(BigDecimal.ZERO) > 0 || waivedAmount.compareTo(BigDecimal.ZERO) > 0) {
						odc = createDueAmounts(odDetail, valueDate, penaltyPayNow, waivedAmount, valueDate,
								RepayConstants.FEE_TYPE_LPP, finODCAmounts);
					}

					if (odc != null) {
						long referenceID = finODCAmountDAO.saveFinODCAmt(odc);
						FinOverDueChargeMovement movement = new FinOverDueChargeMovement();
						movement.setMovementDate(appDate);
						movement.setChargeId(referenceID);
						movement.setMovementAmount(odc.getPaidAmount().add(odc.getWaivedAmount()));
						movement.setPaidAmount(odc.getPaidAmount());
						movement.setWaivedAmount(odc.getWaivedAmount());
						movement.setReceiptID(rqh.getReceiptId());
						movements.add(movement);
						finOd.setLppDueAmt(finOd.getLppDueAmt().add(odc.getAmount()));
					}
				}
			}

			if (!CollectionUtils.isEmpty(movements)) {
				finODCAmountDAO.saveMovement(movements);
			}
			finODCAmountDAO.updateFinODCBalAmts(finODCAmounts);
		}
	}

	private void saveFinLPPAmount(FinRepayQueue frq, Date valueDate, FinRepayQueueHeader frqh, FinODDetails detail) {
		boolean createLpiDue = true;
		FinOverDueCharges lpi = null;
		FinODDetails odDetail = null;
		List<FinOverDueChargeMovement> movements = new ArrayList<>();
		BigDecimal lpiPayNow = frq.getLatePayPftPayNow();
		BigDecimal lpiWaivedAmount = frq.getLatePayPftWaivedNow();
		Date appDate = SysParamUtil.getAppDate();

		List<FinOverDueCharges> finLPIAmtList = finODCAmountDAO.getFinODCAmtByFinRef(frq.getFinID(), frq.getRpyDate(),
				RepayConstants.FEE_TYPE_LPI);

		if (CollectionUtils.isNotEmpty(frqh.getFinOdList())) {
			for (FinODDetails fod : frqh.getFinOdList()) {
				if (fod.getFinODSchdDate().compareTo(frq.getRpyDate()) != 0) {
					continue;
				}
				odDetail = fod;

				if (odDetail.getLpiDueAmt().compareTo(fod.getLPIAmt()) >= 0) {
					createLpiDue = false;
					detail.setLpiDueAmt(fod.getLpiDueAmt());
					break;
				}
				if (fod.getLpiDueTillDate() != null) {
					if (valueDate.compareTo(fod.getLpiDueTillDate()) > 0) {
						for (FinOverDueCharges lpiAmount : finLPIAmtList) {
							if (lpiAmount.getValueDate().compareTo(valueDate) == 0) {
								lpi = lpiAmount;
								break;
							}
						}
					} else {
						createLpiDue = false;
					}

				}
			}
		}

		if (odDetail != null) {
			BigDecimal prvMnthLPIAmt = BigDecimal.ZERO;
			for (FinOverDueCharges lpichargeAmt : finLPIAmtList) {
				// if (dateValueDate.compareTo(odchargeAmt.getValueDate())>=0){
				prvMnthLPIAmt = prvMnthLPIAmt.add(lpichargeAmt.getAmount());
				BigDecimal balanceAmt = lpichargeAmt.getBalanceAmt();
				BigDecimal payNow = BigDecimal.ZERO;
				BigDecimal waiveNow = BigDecimal.ZERO;
				if (balanceAmt.compareTo(BigDecimal.ZERO) > 0) {

					// Waived Amount Update
					if (lpiWaivedAmount.compareTo(balanceAmt) > 0) {
						waiveNow = balanceAmt;
						balanceAmt = BigDecimal.ZERO;
					} else {
						waiveNow = lpiWaivedAmount;
						balanceAmt = balanceAmt.subtract(waiveNow);
					}
					// Paid Amount Update
					if (lpiPayNow.compareTo(balanceAmt) > 0) {
						payNow = balanceAmt;
						balanceAmt = BigDecimal.ZERO;
					} else {
						payNow = lpiPayNow;
						balanceAmt = balanceAmt.subtract(payNow);
					}
					lpiWaivedAmount = lpiWaivedAmount.subtract(waiveNow);
					lpiPayNow = lpiPayNow.subtract(payNow);
				}

				if (waiveNow.compareTo(BigDecimal.ZERO) > 0 || payNow.compareTo(BigDecimal.ZERO) > 0) {
					for (FinOverDueCharges odcAmt : finLPIAmtList) {
						BigDecimal balAmt = odcAmt.getBalanceAmt();
						if (balAmt.compareTo(BigDecimal.ZERO) > 0 && odcAmt.getId() == lpichargeAmt.getId()) {
							odcAmt.setPaidAmount(odcAmt.getPaidAmount().add(payNow));
							odcAmt.setWaivedAmount(odcAmt.getWaivedAmount().add(waiveNow));
							odcAmt.setBalanceAmt(odcAmt.getBalanceAmt().subtract(payNow.add(waiveNow)));
							FinOverDueChargeMovement movement = new FinOverDueChargeMovement();
							movement.setMovementDate(appDate);
							movement.setChargeId(odcAmt.getId());
							movement.setMovementAmount(payNow.add(waiveNow));
							movement.setPaidAmount(payNow);
							movement.setWaivedAmount(waiveNow);
							movement.setReceiptID(frqh.getReceiptId());
							movements.add(movement);
						}
					}
				}

				if (valueDate.compareTo(lpichargeAmt.getValueDate()) == 0) {
					lpichargeAmt.setOdPri(odDetail.getFinCurODPri());
					lpichargeAmt.setOdPft(odDetail.getFinCurODPft());
				}
				// }
			}
			detail.setLpiDueTillDate(odDetail.getLpiDueTillDate());
			detail.setLpiDueAmt(odDetail.getLpiDueAmt());

			if (createLpiDue) {
				detail.setLpiDueTillDate(valueDate);
				if (lpi == null) {
					odDetail.setLPIAmt(odDetail.getLPIAmt().subtract(prvMnthLPIAmt));
					lpi = createDueAmounts(odDetail, valueDate, lpiPayNow, lpiWaivedAmount, valueDate,
							RepayConstants.FEE_TYPE_LPI, finLPIAmtList);
					long referenceID = finODCAmountDAO.saveFinODCAmt(lpi);
					FinOverDueChargeMovement movement = new FinOverDueChargeMovement();
					movement.setMovementDate(appDate);
					movement.setChargeId(referenceID);
					movement.setMovementAmount(lpi.getPaidAmount().add(lpi.getWaivedAmount()));
					movement.setPaidAmount(lpi.getPaidAmount());
					movement.setWaivedAmount(lpi.getWaivedAmount());
					movement.setReceiptID(frqh.getReceiptId());
					movements.add(movement);
					detail.setLpiDueAmt(detail.getLpiDueAmt().add(lpi.getAmount()));
				}
			}

			if (!CollectionUtils.isEmpty(movements)) {
				finODCAmountDAO.saveMovement(movements);
			}
			finODCAmountDAO.updateFinODCBalAmts(finLPIAmtList);
		}

	}

	public FinOverDueCharges createDueAmounts(FinODDetails finODDetail, Date valueDate, BigDecimal paidamt,
			BigDecimal waivedAmount, Date appDate, String chargeType, List<FinOverDueCharges> finODCAmounts) {

		FinOverDueCharges finLPIAmt = new FinOverDueCharges();

		if (chargeType.equals(RepayConstants.FEE_TYPE_LPI)) {

			BigDecimal lpiAmt = finODDetail.getLPIAmt().subtract(paidamt.add(waivedAmount));
			finLPIAmt.setFinID(finODDetail.getFinID());
			finLPIAmt.setSchDate(finODDetail.getFinODSchdDate());
			finLPIAmt.setPostDate(appDate);
			finLPIAmt.setValueDate(valueDate);
			finLPIAmt.setAmount(finODDetail.getLPIAmt());
			finLPIAmt.setPaidAmount(paidamt);
			finLPIAmt.setWaivedAmount(waivedAmount);
			finLPIAmt.setNewRecord(true);
			finLPIAmt.setBalanceAmt(lpiAmt.compareTo(BigDecimal.ZERO) > 0 ? lpiAmt : BigDecimal.ZERO);
			finLPIAmt.setOdPri(finODDetail.getFinCurODPri());
			finLPIAmt.setOdPft(finODDetail.getFinCurODPft());
			finLPIAmt.setFinOdTillDate(valueDate);
			finLPIAmt.setChargeType(chargeType);
		}

		if (chargeType.equals(RepayConstants.FEE_TYPE_LPP)) {

			BigDecimal penaltyAmt = finODDetail.getTotPenaltyAmt().subtract(paidamt.add(waivedAmount));
			finLPIAmt.setFinID(finODDetail.getFinID());
			finLPIAmt.setSchDate(finODDetail.getFinODSchdDate());
			finLPIAmt.setPostDate(appDate);
			finLPIAmt.setValueDate(valueDate);
			finLPIAmt.setAmount(finODDetail.getTotPenaltyAmt());
			finLPIAmt.setPaidAmount(paidamt);
			finLPIAmt.setWaivedAmount(waivedAmount);
			finLPIAmt.setNewRecord(true);
			finLPIAmt.setBalanceAmt(penaltyAmt.compareTo(BigDecimal.ZERO) > 0 ? penaltyAmt : BigDecimal.ZERO);
			finLPIAmt.setOdPri(finODDetail.getFinCurODPri());
			finLPIAmt.setOdPft(finODDetail.getFinCurODPft());
			finLPIAmt.setFinOdTillDate(valueDate);
			finLPIAmt.setChargeType(chargeType);
		}

		finLPIAmt.setDueDays(getDueDays(finODCAmounts, valueDate, finODDetail));

		return finLPIAmt;
	}

	private int getDueDays(List<FinOverDueCharges> finODCAmounts, Date valueDate, FinODDetails finODDetail) {
		Date dueDate = finODDetail.getFinODSchdDate();

		if (CollectionUtils.isNotEmpty(finODCAmounts)) {
			dueDate = finODCAmounts.get(finODCAmounts.size() - 1).getPostDate();
		}

		return DateUtil.getDaysBetween(dueDate, valueDate);
	}

	public void recalOldestDueKnockOff(FinanceMain fm, FinanceProfitDetail fpd, Date valuedate,
			List<FinanceScheduleDetail> schedules) {

		List<FinODDetails> odList = finODDetailsDAO.getFinODBalByFinRef(fm.getFinID());
		List<FinanceRepayments> repayments = financeRepaymentsDAO.getFinRepayList(fm.getFinID());

		latePayMarkingService.calPDOnBackDatePayment(fm, odList, valuedate, schedules, repayments, true, true);

		if (odList != null) {
			latePayMarkingService.updateFinPftDetails(fpd, odList, valuedate);
		}

		finODDetailsDAO.updateList(odList);
		profitDetailsDAO.update(fpd, true);
	}

	public FinanceMain updateStatus(FinanceMain financeMain, Date dateValueDate,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail pftDetail, List<FinODDetails> overdueList,
			String receiptPurpose) {

		return updateRepayStatus(financeMain, dateValueDate, scheduleDetails, pftDetail, overdueList, receiptPurpose);
	}

	private FinanceMain updateRepayStatus(FinanceMain fm, Date dateValueDate, List<FinanceScheduleDetail> schedules,
			FinanceProfitDetail pftDetail, List<FinODDetails> overdueList, String receiptPurpose) {
		logger.debug(Literal.ENTERING);

		// Finance Profit Details Updation
		String oldFinStatus = fm.getFinStatus();
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		EventProperties eventProperties = fm.getEventProperties();

		Date appDate = null;

		if (eventProperties.isParameterLoaded()) {
			appDate = eventProperties.getAppDate();
		} else {
			appDate = SysParamUtil.getAppDate();
		}

		schedules = ScheduleCalculator.sortSchdDetails(schedules);
		pftDetail.setNoInstEarlyStl(pftDetail.getNOInst());
		pftDetail = accrualService.calProfitDetails(fm, schedules, pftDetail, dateValueDate);

		// Update Overdue Details
		if (overdueList != null) {
			latePayMarkingService.updateFinPftDetails(pftDetail, overdueList, dateValueDate);
		}

		latePayBucketService.updateDPDBuketing(schedules, fm, pftDetail, appDate, false);

		fm.setFinStsReason(FinanceConstants.FINSTSRSN_MANUAL);

		boolean overDraft = ProductUtil.isOverDraft(fm);
		if (FinServiceEvent.SCHDRPY.equals(receiptPurpose) && overDraft) {
			overdrafLoanService.unBlockLimit(finID, schedules, dateValueDate);
		}

		LoanPayment lp = new LoanPayment(finID, finReference, schedules, dateValueDate);
		boolean schdFullyPaid = loanPaymentService.isSchdFullyPaid(lp);

		if (overDraft && DateUtil.compare(appDate, fm.getMaturityDate()) < 0) {
			schdFullyPaid = false;
		}

		if (schdFullyPaid && (!fm.isSanBsdSchdle() || (fm.isSanBsdSchdle()
				&& ((receiptPurpose != null && FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)))))) {
			boolean fullyPaid = true;

			if (overDraft && DateUtil.compare(appDate, fm.getMaturityDate()) < 0) {
				fullyPaid = false;
			}
			boolean oldFinActive = fm.isFinIsActive();
			if (fullyPaid) {
				pftDetail.setSvnAcrCalReq(false);
				fm.setFinIsActive(false);
				fm.setClosedDate(FinanceUtil.deriveClosedDate(fm));

				if (oldFinActive) {
					fm.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
				}

				if (FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)) {
					fm.setClosingStatus(FinanceConstants.CLOSE_STATUS_EARLYSETTLE);
					pftDetail.setSvnAcrTillLBD(pftDetail.getTotalSvnAmount());
				}

				if (fm.getClosureType() != null && FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)
						&& ClosureType.isCancel(fm.getClosureType())) {
					fm.setClosingStatus(FinanceConstants.CLOSE_STATUS_CANCELLED);
				}

				// Previous Month Amortization reset to Total Profit to avoid posting on closing Month End
				pftDetail.setPrvMthAmz(pftDetail.getTotalPftSchd());
				pftDetail.setAmzTillLBD(pftDetail.getTotalPftSchd());
			}
		} else if (FinanceConstants.CLOSE_STATUS_WRITEOFF.equals(fm.getClosingStatus())) {
			fm.setFinIsActive(false);
			fm.setClosingStatus(FinanceConstants.CLOSE_STATUS_WRITEOFF);
		} else if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())) {
			fm.setFinIsActive(false);
			fm.setClosingStatus(FinanceConstants.CLOSE_STATUS_CANCELLED);
		} else {
			fm.setFinIsActive(true);
			fm.setClosedDate(null);
			if (!fm.isWriteoffLoan()) {
				fm.setClosingStatus(null);
			}
		}

		pftDetail.setFinStatus(fm.getFinStatus());
		pftDetail.setFinStsReason(fm.getFinStsReason());
		pftDetail.setFinIsActive(fm.isFinIsActive());
		pftDetail.setClosingStatus(fm.getClosingStatus());

		// Reset Back Repayments Details
		List<FinanceRepayments> repayList = financeRepaymentsDAO.getFinRepayListByLinkedTranID(fm.getFinID());

		if (CollectionUtils.isNotEmpty(repayList)) {
			BigDecimal totPri = BigDecimal.ZERO;
			BigDecimal totPft = BigDecimal.ZERO;

			for (FinanceRepayments repay : repayList) {
				totPri = totPri.add(repay.getFinSchdPriPaid());
				totPft = totPft.add(repay.getFinSchdPftPaid());
			}

			pftDetail.setLatestRpyDate(repayList.get(0).getFinPostDate());
			pftDetail.setLatestRpyPri(totPri);
			pftDetail.setLatestRpyPft(totPft);
		} else {
			pftDetail.setLatestRpyDate(fm.getFinStartDate());
			pftDetail.setLatestRpyPri(BigDecimal.ZERO);
			pftDetail.setLatestRpyPft(BigDecimal.ZERO);
		}

		profitDetailsDAO.update(pftDetail, true);

		if (fm.getFinStatus().equals(oldFinStatus)) {
			logger.debug(Literal.LEAVING);
			return fm;
		}

		// Get Customer Status
		CustEODEvent custEODEvent = new CustEODEvent();
		custEODEvent.setEodDate(dateValueDate);
		custEODEvent.setEodValueDate(dateValueDate);

		Customer customer = new Customer();
		customer.setCustSts(customerDAO.getCustomerStatus(fm.getCustID()));
		custEODEvent.setCustomer(customer);

		latePayMarkingService.processCustomerStatus(custEODEvent);

		logger.debug(Literal.LEAVING);
		return fm;
	}

	private AEEvent postingEntryProcess(Date valueDate, Date postDate, Date dateSchdDate, boolean isEODProcess,
			FinanceMain fm, List<FinanceScheduleDetail> schedules, FinanceProfitDetail fpd, FinRepayQueueHeader rqh,
			AEEvent overdueEvent, String eventCode, List<FinFeeDetail> fees, FinReceiptHeader rch) throws AppException {
		logger.debug(Literal.ENTERING);

		// AmountCodes Preparation
		// EOD Repayments should pass the value date as schedule for which Repayments are processing
		final BigDecimal totPftSchdOld = fpd.getTotalPftSchd();
		AEEvent aeEvent = AEAmounts.procAEAmounts(fm, schedules, fpd, eventCode, valueDate, dateSchdDate);
		aeEvent.setPostRefId(rqh.getReceiptId());
		aeEvent.setPostingId(fm.getPostingId());
		if (PennantConstants.APP_PHASE_EOD.equalsIgnoreCase(rqh.getPostBranch())) {
			aeEvent.setEOD(true);
		}

		BigDecimal priDuePaid = BigDecimal.ZERO;
		BigDecimal priDueWaived = BigDecimal.ZERO;
		BigDecimal pftDuePaid = BigDecimal.ZERO;
		BigDecimal pftDueWaived = BigDecimal.ZERO;

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setBusinessvertical(fm.getBusinessVerticalCode());
		amountCodes.setAlwflexi(fm.isAlwFlexi());
		amountCodes.setFinbranch(fm.getFinBranch());
		amountCodes.setEntitycode(fm.getEntityCode());

		if (StringUtils.isEmpty(rqh.getCashierBranch())) {
			amountCodes.setUserBranch(rqh.getPostBranch());
			aeEvent.setPostingUserBranch(rqh.getPostBranch());
		} else {
			amountCodes.setUserBranch(rqh.getCashierBranch());
			aeEvent.setPostingUserBranch(rqh.getCashierBranch());
		}
		amountCodes.setPartnerBankAc(rqh.getPartnerBankAc());
		amountCodes.setPartnerBankAcType(rqh.getPartnerBankAcType());
		amountCodes.setPaymentType(rqh.getPayType());
		amountCodes.setCashAcExecuted(rqh.isStageAccExecuted() ? "Y" : "N");
		aeEvent.setPostDate(postDate);

		String presentmentType = rch.getPresentmentType();
		if (PennantConstants.PROCESS_REPRESENTMENT.equals(presentmentType)
				&& ImplementationConstants.PENALTY_CALC_ON_REPRESENTATION) {
			aeEvent.setValueDate(postDate);
		}

		aeEvent.setEntityCode(fm.getLovDescEntityCode());

		EventProperties eventProperties = fm.getEventProperties();
		Date appDate = null;

		aeEvent.setEventProperties(eventProperties);
		if (eventProperties.isParameterLoaded()) {
			appDate = eventProperties.getAppDate();
		} else {
			appDate = SysParamUtil.getAppDate();
		}

		aeEvent.setAppDate(appDate);
		aeEvent.setAppValueDate(appDate);

		if (overdueEvent != null) {
			aeEvent.setLinkedTranId(overdueEvent.getLinkedTranId());
			aeEvent.setTransOrder(overdueEvent.getTransOrder());
		}

		// amountCodes.setUserBranch(rpyQueueHeader.getPostBranch());
		amountCodes.setPaymentType(rqh.getPayType());
		amountCodes.setPpAmount(rqh.getPartialPaid());

		// Profit Change Amount Setting
		if (rqh.isPftChgAccReq()) {
			BigDecimal pftchg = amountCodes.getPft().subtract(totPftSchdOld);
			if (pftchg.compareTo(BigDecimal.ZERO) < 0) {
				pftchg = pftchg.negate();
			}
			amountCodes.setPftChg(pftchg);
		} else {
			amountCodes.setPftChg(BigDecimal.ZERO);
		}

		// Set Repay Amount Codes
		amountCodes.setRpPri(rqh.getPrincipal().subtract(rqh.getPartialPaid()));
		amountCodes.setRpPft(rqh.getProfit());
		amountCodes.setRpTot(rqh.getPrincipal().add(rqh.getProfit()));
		amountCodes.setRpTds(rqh.getTds());
		amountCodes.setLpiPaid(rqh.getLateProfit());
		amountCodes.setInstpri(rqh.getPrincipal().subtract(rqh.getPartialPaid()));
		amountCodes.setInstpft(rqh.getProfit());
		amountCodes.setInsttot(rqh.getPrincipal().add(rqh.getProfit().subtract(rqh.getPartialPaid())));

		// Fee Details
		amountCodes.setSchFeePay(rqh.getFee());

		// Waived Amounts
		amountCodes.setPriWaived(rqh.getPriWaived());
		amountCodes.setPftWaived(rqh.getPftWaived());
		amountCodes.setLpiWaived(rqh.getLatePftWaived());
		amountCodes.setFeeWaived(rqh.getFeeWaived());

		// Penalty Amounts setting in case only on GOld Loan
		amountCodes.setPenaltyPaid(rqh.getPenalty());
		amountCodes.setPenaltyWaived(rqh.getPenaltyWaived());

		amountCodes.setFuturePriPaid(rqh.getFutPrincipal());
		amountCodes.setFuturePriWaived(rqh.getFutPriWaived());

		priDuePaid = rqh.getPrincipal().subtract(rqh.getFutPrincipal());
		priDueWaived = rqh.getPriWaived().subtract(rqh.getFutPriWaived());

		pftDuePaid = rqh.getProfit().subtract(rqh.getFutProfit());
		pftDueWaived = rqh.getPftWaived().subtract(rqh.getFutPftWaived());

		amountCodes.setPriDuePaid(priDuePaid);
		amountCodes.setPriDueWaived(priDueWaived);
		amountCodes.setPftDuePaid(pftDuePaid);
		amountCodes.setPftDueWaived(pftDueWaived);

		// Accrual & Future Paid Details
		if (StringUtils.equals(eventCode, AccountingEvent.EARLYSTL)) {

			int schSize = schedules.size();
			FinanceScheduleDetail lastSchd = schedules.get(schSize - 1);

			if (ProductUtil.isOverDraft(fm)) {
				BigDecimal bigZero = BigDecimal.ZERO;
				for (int i = schSize - 1; i >= 0; i--) {
					FinanceScheduleDetail curSchd = schedules.get(i);
					BigDecimal closingBalance = curSchd.getClosingBalance();
					BigDecimal repayAmount = curSchd.getRepayAmount();

					if (closingBalance.compareTo(bigZero) == 0 && repayAmount.compareTo(bigZero) > 0) {
						break;
					} else if (closingBalance.compareTo(bigZero) == 0 && repayAmount.compareTo(bigZero) == 0) {
						schedules.remove(i);
					}

				}
				lastSchd = schedules.get(schedules.size() - 1);
			}

			FinanceScheduleDetail oldLastSchd = null;
			if (lastSchd.isFrqDate()) {
				oldLastSchd = financeScheduleDetailDAO.getFinanceScheduleDetailById(fm.getFinID(),
						lastSchd.getSchDate(), "", false);
			}

			// If Final Schedule not exists on Approved Schedule details
			if (oldLastSchd == null || !oldLastSchd.isFrqDate()) {
				// Last Schedule Interest Amounts Paid
				if (amountCodes.getPftWaived()
						.compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0) {
					amountCodes.setLastSchPftPaid(BigDecimal.ZERO);
				} else {
					amountCodes.setLastSchPftPaid(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())
							.subtract(amountCodes.getPftWaived()));
				}

				// Last Schedule Interest Amounts Waived
				if (amountCodes.getPftWaived()
						.compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0) {
					amountCodes.setLastSchPftWaived(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid()));
				} else {
					amountCodes.setLastSchPftWaived(amountCodes.getPftWaived());
				}

			} else {

				// Last Schedule Interest Amounts Paid
				amountCodes.setLastSchPftPaid(BigDecimal.ZERO);
				amountCodes.setLastSchPftWaived(BigDecimal.ZERO);
				// Profit Due Paid
				amountCodes.setPftDuePaid(amountCodes.getRpPft().subtract(rqh.getFutProfit()));

				// Profit Due Waived
				amountCodes.setPftDueWaived(amountCodes.getPftWaived().subtract(rqh.getFutPftWaived()));

				amountCodes.setPriDuePaid(amountCodes.getRpPri().subtract(rqh.getFutPrincipal()));
				amountCodes.setPriDueWaived(amountCodes.getPriWaived().subtract(rqh.getFutPriWaived()));

				amountCodes.setFuturePriPaid(rqh.getFutPrincipal());
				amountCodes.setFuturePriWaived(rqh.getFutPriWaived());
			}
			// Total Future Profit amount
			BigDecimal totFutPft = rqh.getFutProfit().add(rqh.getFutPftWaived());

			// UnAccrual Amounts
			BigDecimal unaccrue = (fpd.getTotalPftSchd().add(fpd.getTdPftCpz())).subtract(fpd.getAmzTillLBD());

			// Accrued Amount
			BigDecimal accrue = totFutPft.subtract(unaccrue);

			// UnAccrue Paid
			if (rqh.getFutPftWaived().compareTo(unaccrue) > 0) {
				amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
			} else {
				amountCodes.setUnAccruedPaid(unaccrue.subtract(rqh.getFutPftWaived()));
			}

			// UnAccrue Waived
			if (rqh.getFutPftWaived().compareTo(unaccrue) >= 0) {
				amountCodes.setUnAccrueWaived(unaccrue);
			} else {
				amountCodes.setUnAccrueWaived(rqh.getFutPftWaived());
			}

			// Accrual Paid
			if (rqh.getFutPftWaived().compareTo(unaccrue.add(accrue)) >= 0) {
				amountCodes.setAccruedPaid(BigDecimal.ZERO);
			} else {
				if (rqh.getFutPftWaived().compareTo(unaccrue) >= 0) {
					amountCodes.setAccruedPaid(accrue.add(unaccrue).subtract(rqh.getFutPftWaived()));
				} else {
					amountCodes.setAccruedPaid(accrue);
				}
			}

			// Accrual Waived
			if (rqh.getFutPftWaived().compareTo(accrue.add(unaccrue)) >= 0) {
				amountCodes.setAccrueWaived(accrue);
			} else {
				if (rqh.getFutPftWaived().compareTo(unaccrue) >= 0) {
					amountCodes.setAccrueWaived(rqh.getFutPftWaived().subtract(unaccrue));
				} else {
					amountCodes.setAccrueWaived(BigDecimal.ZERO);
				}
			}

			// TDS for Last Installment

			if (lastSchd.isTDSApplicable()) {
				amountCodes.setLastSchTds(TDSCalculator.getTDSAmount(amountCodes.getLastSchPftPaid()));

				// Splitting TDS amount into Accrued and Unaccrued Paid basis
				if (amountCodes.getAccruedPaid().compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal accrueTds = TDSCalculator.getTDSAmount(amountCodes.getAccruedPaid());
					BigDecimal unaccrueTds = amountCodes.getLastSchTds().subtract(accrueTds);

					amountCodes.setAccruedTds(accrueTds);
					amountCodes.setUnAccruedTds(unaccrueTds);

				} else {
					amountCodes.setAccruedTds(BigDecimal.ZERO);
					amountCodes.setUnAccruedTds(amountCodes.getLastSchTds());
				}

			} else {
				amountCodes.setLastSchTds(BigDecimal.ZERO);
				amountCodes.setDueTds(BigDecimal.ZERO);
			}

			// TDS Due
			if (TDSCalculator.isTDSApplicable(fm)) {
				BigDecimal dueTds = amountCodes.getRpTds().subtract(amountCodes.getAccruedTds())
						.subtract(amountCodes.getUnAccruedTds());
				amountCodes.setDueTds(dueTds);
			}

			// Balance SubVention Amount
			BigDecimal uSvnAmz = fpd.getTotalSvnAmount().subtract(fpd.getSvnAcrTillLBD());
			if (uSvnAmz.compareTo(BigDecimal.ZERO) > 0) {
				amountCodes.setdSvnAmz(uSvnAmz);
			}
		}

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		if (AccountingEvent.RESTRUCTURE.equals(eventCode)) {
			fm.setAppDate(appDate);
			fm.setRestructureDate(appDate);
			doRestructurePostings(fm, schedules, fpd, rqh);
		}

		if (StringUtils.equals(eventCode, AccountingEvent.REPAY)) {
			if (fpd.getTotalPftPaid().add(rqh.getProfit()).compareTo(fpd.getTotalPftSchd()) >= 0) {
				amountCodes
						.setUnAccruedPaid((fpd.getTotalPftSchd().add(fpd.getTdPftCpz())).subtract(fpd.getPrvMthAmz()));
			}
		}

		if (ImplementationConstants.ALLOW_NPA) {
			long finID = fm.getFinID();

			amountCodes.setNpa(assetClassificationService.isEffNpaStage(finID));

			if (amountCodes.isNpa()) {
				amountCodes.setRpPftPr(amountCodes.getRpPft());
				amountCodes.setRpTotPr(amountCodes.getRpTot());
				amountCodes.setPriPr(amountCodes.getPri());
				amountCodes.setPriSPr(amountCodes.getPriS());
			} else {
				amountCodes.setPriPr(BigDecimal.ZERO);
				amountCodes.setPriSPr(BigDecimal.ZERO);
			}
		}

		aeEvent.getAcSetIDList().clear();

		aeEvent.getAcSetIDList().add(AccountingEngine.getAccountSetID(fm, eventCode));

		// Assignment Percentage
		Set<String> excludeFees = null;
		if (fm.getAssignmentId() != null && fm.getAssignmentId() > 0) {
			Assignment assignment = assignmentDAO.getAssignment(fm.getAssignmentId(), "");
			if (assignment != null) {
				amountCodes.setAssignmentPerc(assignment.getSharingPercentage());
				List<AssignmentDealExcludedFee> excludeFeesList = assignmentDealDAO
						.getApprovedAssignmentDealExcludedFeeList(assignment.getDealId());
				if (CollectionUtils.isNotEmpty(excludeFeesList)) {
					excludeFees = new HashSet<String>();
					for (AssignmentDealExcludedFee excludeFee : excludeFeesList) {
						excludeFees.add(excludeFee.getFeeTypeCode());
					}
				}
			}
		}

		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		Map<String, Object> glSubHeadCodes = fm.getGlSubHeadCodes();

		dataMap.put("emptype", glSubHeadCodes.get("EMPTYPE"));
		dataMap.put("branchcity", glSubHeadCodes.get("BRANCHCITY"));
		dataMap.put("fincollateralreq", glSubHeadCodes.get("FINCOLLATERALREQ"));
		dataMap.put("btloan", fm.getLoanCategory());
		dataMap.put("receiptChannel", fm.getReceiptChannel());
		dataMap.put("ae_receiptChannel", fm.getReceiptChannel());

		if (excludeFees != null) {
			dataMap.put(AccountConstants.POSTINGS_EXCLUDE_FEES, excludeFees);
		}

		dataMap.put("ae_repledge", "N");

		if (rqh.getExtDataMap() != null) {
			dataMap.putAll(rqh.getExtDataMap());
			if (rqh.getExtDataMap().containsKey("PR_ReceiptAmount")) {
				BigDecimal repledgeAmt = rqh.getExtDataMap().get("PR_ReceiptAmount");
				if (repledgeAmt.compareTo(BigDecimal.ZERO) > 0) {
					dataMap.put("ae_repledge", "Y");
				}
			}
		}

		prepareFeeRulesMap(dataMap, fees, rqh.getPayType());

		if (rch != null) {
			List<ReceiptAllocationDetail> radList = rch.getAllocations();
			List<FeeType> feeTypesList = new ArrayList<>();
			List<String> feeTypeCodes = new ArrayList<>();
			for (ReceiptAllocationDetail rad : radList) {
				if (rad.getAllocationType().equals(Allocation.MANADV)) {
					feeTypeCodes.add(rad.getFeeTypeCode());
				}
			}

			for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
				if (ReceiptMode.PAYABLE.equals(rcd.getPaymentType())) {
					List<ManualAdvise> ma = rch.getPayableAdvises();
					String feeTypeCode = "";
					for (ManualAdvise ma1 : ma) {
						if (ma1.getAdviseID() == rcd.getPayAgainstID()) {
							feeTypeCode = ma1.getFeeTypeCode();
							break;
						}
					}
					feeTypeCodes.add(feeTypeCode);
				}
			}

			if (CollectionUtils.isNotEmpty(fees)) {
				for (FinFeeDetail finFeeDetail : fees) {
					feeTypeCodes.add(finFeeDetail.getFeeTypeCode());
				}

			}
			if (feeTypeCodes != null && !feeTypeCodes.isEmpty()) {
				feeTypesList = feeTypeService.getFeeTypeListByCodes(feeTypeCodes, "");
				aeEvent.setFeesList(feeTypesList);
			}
		}

		addZeroifNotContainsObj(dataMap, "bounceChargePaid");
		addZeroifNotContainsObj(dataMap, "bounceCharge_CGST_P");
		addZeroifNotContainsObj(dataMap, "bounceCharge_IGST_P");
		addZeroifNotContainsObj(dataMap, "bounceCharge_SGST_P");
		addZeroifNotContainsObj(dataMap, "bounceCharge_UGST_P");
		addZeroifNotContainsObj(dataMap, "bounceCharge_CESS_P");

		// #PSD138017
		Map<String, Object> dataMapTemp = GSTCalculator.getGSTDataMap(fm.getFinID());

		if (dataMapTemp != null && !dataMapTemp.isEmpty()) {
			dataMap.putAll(dataMapTemp);
		}

		if (rqh.getGstExecutionMap() != null) {
			dataMap.putAll(rqh.getGstExecutionMap());
		}

		aeEvent.setDataMap(dataMap);
		aeEvent.setSimulateAccounting(fm.isSimulateAccounting());

		// Accounting Entry Execution
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		if (overdueEvent != null) {
			overdueEvent.setTransOrder(aeEvent.getTransOrder());
		}

		// GST Invoice Preparation for Exempted waiver case
		pftDueWaived = amountCodes.getPftDueWaived();
		if (ImplementationConstants.ALW_PROFIT_SCHD_INVOICE && aeEvent.getLinkedTranId() > 0 && pftDueWaived != null
				&& pftDueWaived.compareTo(BigDecimal.ZERO) > 0) {
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(fm);
			financeDetail.getFinScheduleData().setFinanceType(financeTypeDAO.getFinanceTypeByFinType(fm.getFinType()));
			financeDetail.setCustomerDetails(null);
			financeDetail.setFinanceTaxDetail(null);

			if (!rqh.getQueueList().isEmpty()) {
				for (FinRepayQueue repayQueue : rqh.getQueueList()) {

					BigDecimal pftWaived = BigDecimal.ZERO;
					BigDecimal priWaived = BigDecimal.ZERO;

					switch (ImplementationConstants.GST_SCHD_CAL_ON) {
					case FinanceConstants.GST_SCHD_CAL_ON_PFT:
						pftWaived = repayQueue.getSchdPftWaivedNow();
						priWaived = repayQueue.getSchdPriWaivedNow();
						break;
					case FinanceConstants.GST_SCHD_CAL_ON_PRI:
						priWaived = repayQueue.getSchdPriWaivedNow();
						break;
					case FinanceConstants.GST_SCHD_CAL_ON_EMI:
						pftWaived = repayQueue.getSchdPftWaivedNow();
						break;
					default:
						break;
					}

					long finID = repayQueue.getFinID();
					Date rpyDate = repayQueue.getRpyDate();
					Long invoiceID = financeScheduleDetailDAO.getSchdDueInvoiceID(finID, rpyDate);

					InvoiceDetail invoiceDetail = new InvoiceDetail();
					invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
					invoiceDetail.setFinanceDetail(financeDetail);
					invoiceDetail.setPriAmount(priWaived);
					invoiceDetail.setPftAmount(pftWaived);
					invoiceDetail.setDbInvSetReq(false);
					invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED_TAX_CREDIT);
					invoiceDetail.setDbInvoiceID(invoiceID);

					if (!aeEvent.isSimulateAccounting()) {
						gstInvoiceTxnService.schdDueTaxInovicePrepration(invoiceDetail);
					}
				}
			}

		}
		if (rqh.getFutPrincipal().compareTo(BigDecimal.ZERO) > 0 && aeEvent.getLinkedTranId() > 0) {
			// GST Invoice Preparation for Exempted paid case

			BigDecimal fpftpaid = BigDecimal.ZERO;
			BigDecimal fpripaid = BigDecimal.ZERO;
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(fm);
			financeDetail.getFinScheduleData().setFinanceType(financeTypeDAO.getFinanceTypeByFinType(fm.getFinType()));
			financeDetail.setCustomerDetails(null);
			financeDetail.setFinanceTaxDetail(null);

			fpripaid = rqh.getFutPrincipal();
			fpftpaid = rqh.getFutProfit();

			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
			invoiceDetail.setFinanceDetail(financeDetail);
			invoiceDetail.setFpriAmount(fpripaid);
			invoiceDetail.setFpftAmount(fpftpaid);
			invoiceDetail.setDbInvSetReq(false);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED);

			if (!aeEvent.isSimulateAccounting()) {
				gstInvoiceTxnService.schdDueTaxInovicePrepration(invoiceDetail);
			}
		}

		// Overdue Details Updation for Paid Penalty
		/*
		 * if (aeEvent.isPostingSucess() && StringUtils.equals(financeMain.getProductCategory(),
		 * FinanceConstants.PRODUCT_GOLD)) { BigDecimal penaltyAmt =
		 * rpyQueueHeader.getPenalty().add(rpyQueueHeader.getPenaltyWaived());
		 * 
		 * //Overdue Details Updation for Totals if (penaltyAmt.compareTo(BigDecimal.ZERO) > 0) { FinODDetails detail =
		 * new FinODDetails(); detail.setFinReference(financeMain.getFinReference());
		 * detail.setFinODSchdDate(financeMain.getMaturityDate());
		 * detail.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE); detail.setTotPenaltyAmt(BigDecimal.ZERO);
		 * detail.setTotPenaltyPaid(rpyQueueHeader.getPenalty());
		 * detail.setTotPenaltyBal((rpyQueueHeader.getPenalty().add(rpyQueueHeader.getPenaltyWaived())).negate());
		 * detail.setTotWaived(rpyQueueHeader.getPenaltyWaived()); getFinODDetailsDAO().updateTotals(detail); } }
		 */

		logger.debug(Literal.LEAVING);
		return aeEvent;
	}

	private void doRestructurePostings(FinanceMain fm, List<FinanceScheduleDetail> schedules, FinanceProfitDetail fpd,
			FinRepayQueueHeader frqh) {
		List<FinanceScheduleDetail> oldSchedules = fm.getOldSchedules();
		String finReference = fm.getFinReference();

		RestructureDetail rd = financeScheduleDetailDAO.getRestructureDetail(finReference);
		Date resStartDate = rd.getRestructureDate();

		Date resEndDate = null;
		if (rd.getPriHldEndDate() != null) {
			resEndDate = rd.getPriHldEndDate();
		} else {
			resEndDate = rd.getEmiHldEndDate();
		}

		if (resEndDate == null) {
			resEndDate = fm.getAppDate();
		}

		if (DateUtil.compare(resEndDate, fm.getAppDate()) > 0) {
			resEndDate = fm.getAppDate();
		}

		if (rd != null) {
			resStartDate = rd.getRestructureDate();
		}

		List<Date> instDueDates = new ArrayList<>();
		Map<Date, FinanceScheduleDetail> scheduleMap = new HashMap<>();

		for (FinanceScheduleDetail curSchd : oldSchedules) {
			if (curSchd.getSchDate().compareTo(resEndDate) > 0) {
				break;
			}

			if (DateUtil.compare(curSchd.getSchDate(), resStartDate) >= 0) {
				if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
					instDueDates.add(curSchd.getSchDate());
					scheduleMap.put(curSchd.getSchDate(), curSchd);
				}
			}
		}

		Date restructureDate = fm.getRestructureDate();
		if (rd != null) {
			restructureDate = rd.getRestructureDate();
		}

		List<Date> amzReversalDates = new ArrayList<>();

		Date monthStartDate = null;
		for (Date instDate : instDueDates) {
			monthStartDate = DateUtil.getMonthStart(instDate);
			if (DateUtil.compare(monthStartDate, restructureDate) > 0) {
				processAmzReversal(fm, monthStartDate);
				amzReversalDates.add(monthStartDate);
			} else if (CollectionUtils.isNotEmpty(amzReversalDates) && !amzReversalDates.contains(monthStartDate)) {
				processAmzReversal(fm, monthStartDate);
				amzReversalDates.add(monthStartDate);
			}

			processInstallmentPostingsReversal(fm, instDate);
		}

		monthStartDate = DateUtil.getMonthStart(fm.getAppDate());

		if (amzReversalDates.size() > 0) {
			if (!amzReversalDates.contains(monthStartDate)) {
				processAmzReversal(fm, monthStartDate);
				amzReversalDates.add(monthStartDate);
			}
		}

		List<Date> repostDatelist = new ArrayList<>();

		repostDatelist.addAll(instDueDates);
		repostDatelist.addAll(amzReversalDates);

		Date mthStDate = DateUtil.getMonthStart(resStartDate);
		Date accruedDate = null;

		List<Date> cpzEMIHdayDates = new ArrayList<>();

		for (FinanceScheduleDetail curSchd : schedules) {
			if (curSchd.getSchDate().compareTo(resStartDate) <= 0) {
				if (curSchd.isFrqDate()) {
					accruedDate = curSchd.getSchDate();
				}
			} else {
				if (instDueDates.contains(curSchd.getSchDate())
						&& (FinanceConstants.FLAG_RESTRUCTURE.equals(curSchd.getBpiOrHoliday()))) {
					if (!curSchd.isCpzOnSchDate()) {
						instDueDates.remove(curSchd.getSchDate());
					} else {
						cpzEMIHdayDates.add(curSchd.getSchDate());
					}
				}
			}

			if (DateUtil.compare(curSchd.getSchDate(), fm.getAppDate()) > 0) {
				break;
			}
		}

		if (DateUtil.compare(mthStDate, accruedDate) > 0) {
			accruedDate = mthStDate;
		}

		accrualService.calProfitDetails(fm, schedules, fpd, accruedDate);
		fpd.setAmzTillLBD(fpd.getPftAmz());

		repostDatelist = repostDatelist.stream().distinct().sorted().collect(Collectors.toList());

		for (Date date : repostDatelist) {
			accrualService.calProfitDetails(fm, schedules, fpd, date);

			if (amzReversalDates.contains(date)) {
				postAMZPostings(fm, schedules, fpd, date);
			}

			if (instDueDates.contains(date)) {
				postInstDatePostings(fm, fpd, schedules, date, frqh);
			}
		}

		if (ImplementationConstants.ACCRUAL_DIFF_ONETIME_POST) {
			amzDifferentialPostings(fm, schedules, fpd, mthStDate);
		}
	}

	private void processAmzReversal(FinanceMain finMain, java.util.Date monthStartDate) {
		List<Long> linkedTranIdList = postingsDAO.getAMZPostings(finMain.getFinReference(), monthStartDate);
		if (CollectionUtils.isNotEmpty(linkedTranIdList)) {
			postingsPreparationUtil.postReversalsByLinkedTranID(linkedTranIdList.get(0));
		}
	}

	private void processInstallmentPostingsReversal(FinanceMain fm, Date instDate) {
		List<ReturnDataSet> datasetList = postingsDAO.getInstDatePostings(fm.getFinReference(), instDate);

		if (CollectionUtils.isEmpty(datasetList)) {
			return;
		}

		ReturnDataSet rds = datasetList.get(0);
		List<ReturnDataSet> dataSets = postingsPreparationUtil.postReversalsByLinkedTranID(rds.getLinkedTranId());

		Long invoiceID = rds.getInvoiceId();

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinanceMain(fm);
		schdData.setFinanceType(financeTypeDAO.getFinanceTypeByFinType(fm.getFinType()));
		fd.setCustomerDetails(null);
		fd.setFinanceTaxDetail(null);

		InvoiceDetail id = new InvoiceDetail();
		ReturnDataSet dataSet = dataSets.get(0);
		id.setLinkedTranId(dataSet.getLinkedTranId());
		id.setFinanceDetail(fd);
		id.setPriAmount(rds.getInvoiceAmt());
		id.setPftAmount(rds.getInvoiceAmt());
		id.setDbInvSetReq(false);
		id.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED_TAX_CREDIT);

		if (invoiceID > 0) {
			id.setDbInvoiceID(invoiceID);
		}

		gstInvoiceTxnService.schdDueTaxInovicePrepration(id);
	}

	private void amzDifferentialPostings(FinanceMain fm, List<FinanceScheduleDetail> schedules, FinanceProfitDetail fpd,
			Date monthStartDate) {

		String acceventAmz = AccountingEvent.AMZ;
		Long accountingID = AccountingEngine.getAccountSetID(fm, acceventAmz, FinanceConstants.MODULEID_FINTYPE);

		if (accountingID == null || accountingID <= 0) {
			return;
		}

		Date lstAcrDate = null;

		for (FinanceScheduleDetail curSchd : schedules) {
			if (curSchd.getSchDate().compareTo(fm.getAppDate()) <= 0) {
				lstAcrDate = curSchd.getSchDate();
			} else {
				break;
			}
		}

		if (DateUtil.compare(lstAcrDate, monthStartDate) < 0) {
			lstAcrDate = monthStartDate;
		}

		accrualService.calProfitDetails(fm, schedules, fpd, lstAcrDate);

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(fm, fpd, schedules, acceventAmz, lstAcrDate, fm.getAppDate());

		AEAmountCodes aeAmountCodes = aeEvent.getAeAmountCodes();
		aeEvent.setDataMap(aeAmountCodes.getDeclaredFieldValues());

		BigDecimal unAmz = aeAmountCodes.getdAmz();

		if (unAmz.compareTo(BigDecimal.ZERO) > 0) {
			aeEvent.getAcSetIDList().add(accountingID);
			postingsPreparationUtil.postAccounting(aeEvent);

			if (aeEvent.isPostingSucess() && aeEvent.getLinkedTranId() > 0) {
				fpd.setAmzTillLBD(fpd.getAmzTillLBD().add(unAmz));
			}
		}
	}

	private void postAMZPostings(FinanceMain fm, List<FinanceScheduleDetail> schedules, FinanceProfitDetail fpd,
			Date amzDate) {
		Long accountingID = AccountingEngine.getAccountSetID(fm, AccountingEvent.AMZ,
				FinanceConstants.MODULEID_FINTYPE);

		AEEvent acrEvt = AEAmounts.procCalAEAmounts(fm, fpd, schedules, AccountingEvent.AMZ, amzDate, fm.getAppDate());

		acrEvt.setDataMap(acrEvt.getAeAmountCodes().getDeclaredFieldValues());

		if (accountingID != null && accountingID > 0) {
			acrEvt.getAcSetIDList().add(accountingID);
		}

		postingsPreparationUtil.postAccounting(acrEvt);

		if (acrEvt.isPostingSucess() && acrEvt.getLinkedTranId() > 0) {
			fpd.setAmzTillLBD(fpd.getPftAmz());
			fpd.setAmzTillLBDNormal(fpd.getPftAmzNormal());
			fpd.setAmzTillLBDPD(fpd.getPftAmzPD());
			fpd.setAmzTillLBDPIS(fpd.getPftAmzSusp());
			fpd.setAcrTillLBD(fpd.getPftAccrued());
			fpd.setAcrSuspTillLBD(fpd.getPftAccrueSusp());
			fpd.setSvnAcrTillLBD(fpd.getSvnPftAmount());
			fpd.setGapIntAmzLbd(fpd.getGapIntAmz());
		}
	}

	private void postInstDatePostings(FinanceMain fm, FinanceProfitDetail fpd, List<FinanceScheduleDetail> schedules,
			Date schDate, FinRepayQueueHeader frqh) {

		String instEvent = AccountingEvent.INSTDATE;

		Long accountingID = AccountingEngine.getAccountSetID(fm, instEvent, FinanceConstants.MODULEID_FINTYPE);

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(fm, fpd, schedules, instEvent, schDate, schDate);

		if (accountingID != null && accountingID > 0) {
			aeEvent.getAcSetIDList().add(accountingID);
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		FinanceScheduleDetail schd = null;

		for (FinanceScheduleDetail schedule : schedules) {
			if (DateUtil.compare(schedule.getSchDate(), schDate) == 0) {
				schd = schedule;
			}
		}

		amountCodes.setInstpft(schd.getProfitSchd());
		amountCodes.setInstTds(schd.getTDSAmount());
		amountCodes.setInstpri(schd.getPrincipalSchd());
		amountCodes.setInstcpz(schd.getCpzAmount());
		amountCodes.setInsttot(amountCodes.getInstpft().add(amountCodes.getInstpri()));

		amountCodes.setPftS(fpd.getTdSchdPft());
		amountCodes.setPftSP(fpd.getTdSchdPftPaid());
		amountCodes.setPftSB(amountCodes.getPftS().subtract(amountCodes.getPftSP()));

		if (amountCodes.getPftSB().compareTo(BigDecimal.ZERO) < 0) {
			amountCodes.setPftSB(BigDecimal.ZERO);
		}

		amountCodes.setPriS(fpd.getTdSchdPri());
		amountCodes.setPriSP(fpd.getTdSchdPriPaid());
		amountCodes.setPriSB(amountCodes.getPriS().subtract(amountCodes.getPriSP()));

		if (amountCodes.getPriSB().compareTo(BigDecimal.ZERO) < 0) {
			amountCodes.setPriSB(BigDecimal.ZERO);
		}

		if (fpd.isProvision() && fpd.getCurODDays() > 0) {
			amountCodes.setInstpftPr(amountCodes.getInstpft());
			amountCodes.setInstpriPr(amountCodes.getInstpri());
			amountCodes.setdAmzPr(amountCodes.getdAmz());

			amountCodes.setInstpft(BigDecimal.ZERO);
			amountCodes.setInstpri(BigDecimal.ZERO);
			amountCodes.setdAmz(BigDecimal.ZERO);
			amountCodes.setInsttot(BigDecimal.ZERO);
		}

		dataMap = amountCodes.getDeclaredFieldValues();

		aeEvent.setDataMap(dataMap);
		aeEvent.setCustAppDate(fm.getAppDate());
		aeEvent.setPostDate(fm.getAppDate());
		aeEvent.setPostingUserBranch(frqh.getPostBranch());

		try {
			postingsPreparationUtil.postAccounting(aeEvent);
		} catch (Exception e) {
			throw new AppException("Error While Creating Postings");
		}

		fpd.setAmzTillLBD(fpd.getAmzTillLBD().add(aeEvent.getAeAmountCodes().getuAmz()));

		if (ImplementationConstants.ALW_PROFIT_SCHD_INVOICE && aeEvent.getLinkedTranId() > 0) {
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(fm);
			financeDetail.getFinScheduleData().setFinanceType(financeTypeDAO.getFinanceTypeByFinType(fm.getFinType()));
			financeDetail.setCustomerDetails(null);
			financeDetail.setFinanceTaxDetail(null);
			createInvoice(financeDetail, schd, aeEvent.getLinkedTranId());
		}
	}

	private void createInvoice(FinanceDetail fd, FinanceScheduleDetail schd, long linkedTranId) {
		BigDecimal pftAmount = BigDecimal.ZERO;
		BigDecimal priAmount = BigDecimal.ZERO;

		EventProperties eventProperties = fd.getFinScheduleData().getFinanceMain().getEventProperties();

		switch (ImplementationConstants.GST_SCHD_CAL_ON) {
		case FinanceConstants.GST_SCHD_CAL_ON_PFT:
			pftAmount = schd.getProfitSchd();
			priAmount = schd.getPrincipalSchd();
			break;
		case FinanceConstants.GST_SCHD_CAL_ON_PRI:
			priAmount = schd.getPrincipalSchd();
			break;
		case FinanceConstants.GST_SCHD_CAL_ON_EMI:
			pftAmount = schd.getProfitSchd();
			break;
		default:
			break;
		}

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setLinkedTranId(linkedTranId);
		invoiceDetail.setFinanceDetail(fd);
		invoiceDetail.setPftAmount(pftAmount);
		invoiceDetail.setPriAmount(priAmount);
		invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED);
		invoiceDetail.setEventProperties(eventProperties);

		Long invoiceID = gstInvoiceTxnService.schdDueTaxInovicePrepration(invoiceDetail);

		if (schd.getFinReference() == null) {
			schd.setFinReference(fd.getFinReference());
		}

		saveDueTaxDetail(schd, invoiceID);
	}

	private void saveDueTaxDetail(FinanceScheduleDetail schd, Long invoiceID) {
		String gstShdCalOn = ImplementationConstants.GST_SCHD_CAL_ON;

		ScheduleDueTaxDetail taxDetails = new ScheduleDueTaxDetail();
		taxDetails.setFinReference(schd.getFinReference());
		taxDetails.setSchDate(schd.getSchDate());
		taxDetails.setTaxType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED);
		taxDetails.setTaxCalcOn(gstShdCalOn);

		BigDecimal invoiceAmt = BigDecimal.ZERO;

		switch (gstShdCalOn) {
		case FinanceConstants.GST_SCHD_CAL_ON_PFT:
			invoiceAmt = schd.getProfitSchd();
			break;
		case FinanceConstants.GST_SCHD_CAL_ON_PRI:
			invoiceAmt = schd.getPrincipalSchd();
			break;
		case FinanceConstants.GST_SCHD_CAL_ON_EMI:
			invoiceAmt = schd.getPrincipalSchd().add(schd.getProfitSchd());
			break;
		default:
			break;
		}

		taxDetails.setAmount(invoiceAmt);
		taxDetails.setInvoiceID(invoiceID);

		financeScheduleDetailDAO.saveSchDueTaxDetail(taxDetails);
	}

	private void addZeroifNotContainsObj(Map<String, Object> dataMap, String key) {
		if (dataMap != null) {
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, BigDecimal.ZERO);
			}
		}
	}

	private Map<String, Object> prepareFeeRulesMap(Map<String, Object> dataMap, List<FinFeeDetail> finFeeDetailList,
			String payType) {

		if (CollectionUtils.isEmpty(finFeeDetailList)) {
			return dataMap;
		}

		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (StringUtils.startsWith(finFeeDetail.getFinEvent(), AccountingEvent.ADDDBS)) {
				continue;
			}

			dataMap.putAll(FeeCalculator.getFeeRuleMap(finFeeDetail, payType));

		}

		return dataMap;
	}

	/**
	 * Method for Upadte Data for Finance schedule Details Object
	 * 
	 * @param detail
	 * @param main
	 * @param valueDate
	 * @param repayAmtBal
	 * @return
	 */
	private FinanceScheduleDetail updateScheduleDetailsData(FinanceScheduleDetail schedule,
			FinRepayQueue finRepayQueue) {
		logger.debug("Entering");

		schedule.setFinID(finRepayQueue.getFinID());
		schedule.setFinReference(finRepayQueue.getFinReference());
		schedule.setSchDate(finRepayQueue.getRpyDate());

		// Fee Details paid Amounts updation
		schedule.setSchdFeePaid(schedule.getSchdFeePaid().add(finRepayQueue.getSchdFeePayNow())
				.add(finRepayQueue.getSchdFeeWaivedNow()));

		schedule.setSchdPftPaid(schedule.getSchdPftPaid().add(finRepayQueue.getSchdPftPayNow())
				.add(finRepayQueue.getSchdPftWaivedNow()));
		schedule.setTDSPaid(schedule.getTDSPaid().add(finRepayQueue.getSchdTdsPayNow()));
		schedule.setSchdPriPaid(schedule.getSchdPriPaid().add(finRepayQueue.getSchdPriPayNow())
				.add(finRepayQueue.getSchdPriWaivedNow()));

		// Finance Schedule Profit Balance Check
		if ((schedule.getProfitSchd().subtract(schedule.getSchdPftPaid())).compareTo(BigDecimal.ZERO) == 0) {
			schedule.setSchPftPaid(true);
		} else {
			schedule.setSchPftPaid(false);
		}

		// Finance Schedule Principal Balance Check
		if ((schedule.getPrincipalSchd().subtract(schedule.getSchdPriPaid())).compareTo(BigDecimal.ZERO) == 0) {
			schedule.setSchPriPaid(true);
		} else {
			schedule.setSchPriPaid(false);
		}

		logger.debug("Leaving");
		return schedule;
	}

	public FinanceRepayments prepareRepayDetail(FinRepayQueue queue, Date valueDate, Date postdate, long linkedTranId,
			BigDecimal totalRpyAmt, long receiptId) {

		FinanceRepayments repayment = new FinanceRepayments();
		repayment.setReceiptId(receiptId);
		repayment.setFinID(queue.getFinID());
		repayment.setFinReference(queue.getFinReference());
		repayment.setFinSchdDate(queue.getRpyDate());
		repayment.setFinRpyFor(queue.getFinRpyFor());
		repayment.setLinkedTranId(linkedTranId);

		repayment.setFinRpyAmount(totalRpyAmt);
		repayment.setFinPostDate(postdate);
		repayment.setFinValueDate(valueDate);
		repayment.setFinBranch(queue.getBranch());
		repayment.setFinType(queue.getFinType());
		repayment.setFinCustID(queue.getCustomerID());

		BigDecimal schdPftPayNow = queue.getSchdPftPayNow();
		BigDecimal schdPriPayNow = queue.getSchdPriPayNow();
		BigDecimal schdPftWaivedNow = queue.getSchdPftWaivedNow();
		BigDecimal schdPriWaivedNow = queue.getSchdPriWaivedNow();

		repayment.setFinSchdPftPaid(schdPftPayNow.add(schdPftWaivedNow));
		repayment.setFinSchdTdsPaid(queue.getSchdTdsPayNow());
		repayment.setFinSchdPriPaid(schdPriPayNow.add(schdPriWaivedNow));
		repayment.setFinTotSchdPaid(schdPftPayNow.add(schdPriPayNow).add(schdPftWaivedNow).add(schdPriWaivedNow));
		repayment.setFinFee(BigDecimal.ZERO);
		repayment.setFinWaiver(queue.getWaivedAmount());
		repayment.setFinRefund(queue.getRefundAmount());

		// Fee Details
		repayment.setSchdFeePaid(queue.getSchdFeePayNow().add(queue.getSchdFeeWaivedNow()));

		repayment.setPenaltyPaid(queue.getPenaltyPayNow());
		repayment.setPenaltyWaived(queue.getWaivedAmount());

		logger.debug("Leaving");
		return repayment;
	}

	@Deprecated
	public List<Object> postingsScreenRepayProcess(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList,
			Map<String, BigDecimal> totalsMap, String eventCode, Map<String, FeeRule> feeRuleDetailMap,
			String finDivision) throws AppException {

		return screenRepayProcess(financeMain, scheduleDetails, financeProfitDetail, finRepayQueueList, totalsMap,
				eventCode, feeRuleDetailMap, finDivision);
	}

	@Deprecated
	public List<Object> UpdateScreenPaymentsProcess(FinanceMain financeMain,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail financeProfitDetail,
			List<FinRepayQueue> finRepayQueueList, long linkedTranId, boolean isPartialRepay,
			AEAmountCodes aeAmountCodes) {

		return screenPaymentsUpdation(financeMain, scheduleDetails, financeProfitDetail, finRepayQueueList,
				linkedTranId, isPartialRepay, aeAmountCodes);
	}

	@Deprecated
	private List<Object> screenRepayProcess(FinanceMain fm, List<FinanceScheduleDetail> schedules,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList,
			Map<String, BigDecimal> totalsMap, String eventCode, Map<String, FeeRule> feeRuleDetailMap,
			String finDivison) throws AppException {

		logger.debug("Entering");
		List<Object> actReturnList = null;

		Date dateValueDate = SysParamUtil.getAppValueDate();
		Date valueDate = dateValueDate;

		// Based on repayments method then do charges postings first then profit or principal
		// C - PENALTY / CHRAGES, P - PRINCIPAL , I - PROFIT / INTEREST
		if ((ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCPI))
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCIP))) {
			List<Object> returnList = doOverduePostings(null, finRepayQueueList, dateValueDate, dateValueDate, fm,
					null);
			AEEvent aeEvent = (AEEvent) returnList.get(0);
			if (aeEvent != null) {
				return null;
			}
		}

		// Schedule Principal and Profit payments
		BigDecimal totRpyAmt = totalsMap.get("totRpyTot");
		if (totRpyAmt != null && totRpyAmt.compareTo(BigDecimal.ZERO) > 0) {
			actReturnList = doSchedulePostings(null, valueDate, valueDate, fm, schedules, null, financeProfitDetail,
					eventCode, null, null);
		} else {
			if (actReturnList == null) {
				actReturnList = new ArrayList<Object>();
			}
			actReturnList.clear();
			actReturnList.add(true);// Postings Success
			actReturnList.add(Long.MIN_VALUE);// Linked Transaction ID
			actReturnList.add(false); // Partial Repay
			actReturnList.add(null);// AE Amounts Object
			actReturnList.add(null);// Finance Account
		}

		if ((Boolean) actReturnList.get(0)) {
			if ((ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC))
					|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC))
					|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS))
					|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPICS))) {
				List<Object> returnList = doOverduePostings(null, finRepayQueueList, dateValueDate, dateValueDate, fm,
						null);
				AEEvent aeEvent = (AEEvent) returnList.get(0);
				if (aeEvent != null) {
					return null;
				}
			}
		}

		// Movements Accounting Process execution
		// (PD to Normal) or (Suspense to PD) or (Suspense to Normal)

		// Previous Details
		String execEventCode = null;
		boolean proceedFurther = true;
		if ((Boolean) actReturnList.get(0) && financeProfitDetail.getCurODDays() > 0) {

			if (financeProfitDetail.isPftInSusp()) {

				// Check Manual Suspense
				FinanceSuspHead suspHead = financeSuspHeadDAO.getFinanceSuspHeadById(fm.getFinID(), "");
				if (suspHead.isManualSusp()) {
					execEventCode = null;
					proceedFurther = false;
				}

				// Fetch Current Details
				if (proceedFurther) {

					// Get Current Over Due Details Days Count after Payment Process
					int curMaxODDays = finODDetailsDAO.getFinODDays(fm.getFinID());

					// Status of Suspense from CustStatusCodes based on OD Days when OD Days > 0
					boolean curFinIsSusp = false;
					if (curMaxODDays > 0) {
						curFinIsSusp = customerStatusCodeDAO.getFinanceSuspendStatus(curMaxODDays);
					}

					// If Finance Still in Suspense case, no need to do Any Accounting further.
					if (!curFinIsSusp) {
						if (curMaxODDays > 0) {
							execEventCode = AccountingEvent.PIS_PD;
						} else {
							execEventCode = AccountingEvent.PIS_NORM;
						}
					}
				}
			} else {

				// Get Current Over Due Details Days Count after Payment Process
				int curMaxODDays = finODDetailsDAO.getFinODDays(fm.getFinID());

				if (curMaxODDays == 0) {
					execEventCode = AccountingEvent.PD_NORM;
				}
			}

			// Do Accounting based on Accounting Event selected from above process check
			if (StringUtils.isNotEmpty(execEventCode)) {
				AEEvent aeEvent = new AEEvent();
				AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
				amountCodes = (AEAmountCodes) actReturnList.get(4);
				aeEvent.setAccountingEvent(execEventCode);
				aeEvent.setValueDate(valueDate);
				aeEvent.setSchdDate(valueDate);

				// Set O/S balances for Principal & profits in Amount Codes Data--TODO
				Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
				aeEvent.setDataMap(dataMap);

				// Reset AEAmount Code Details Bean and send for Accounting Execution.
				postingsPreparationUtil.processPostingDetails(aeEvent);
			}
		}

		logger.debug("Leaving");
		return actReturnList;
	}

	@Deprecated
	private List<Object> screenPaymentsUpdation(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList, long linkedTranId,
			boolean isPartialRepay, AEAmountCodes aeAmountCodes) throws AppException {
		logger.debug("Entering");

		List<Object> actReturnList = new ArrayList<Object>();
		Date dateValueDate = SysParamUtil.getAppValueDate();

		Map<String, FinanceScheduleDetail> scheduleMap = new HashMap<String, FinanceScheduleDetail>();
		for (FinanceScheduleDetail detail : scheduleDetails) {
			scheduleMap.put(detail.getSchDate().toString(), detail);
		}

		// AE Amounts Object Check
		BigDecimal rpyTotal = BigDecimal.ZERO;
		BigDecimal rpyPri = BigDecimal.ZERO;
		BigDecimal rpyPft = BigDecimal.ZERO;
		if (aeAmountCodes != null) {
			rpyTotal = aeAmountCodes.getRpTot();
			rpyPri = aeAmountCodes.getRpPri();
			rpyPft = aeAmountCodes.getRpPft();
		}

		// Database Updations for Finance RepayQueue Details List
		boolean isPenaltyAvail = false;
		for (FinRepayQueue repayQueue : finRepayQueueList) {

			if (rpyTotal != null && rpyTotal.compareTo(
					BigDecimal.ZERO) > 0) {/*
											 * 
											 * FinanceScheduleDetail scheduleDetail = null; if (scheduleMap.containsKey
											 * (DateUtility.formatDate(repayQueue.getRpyDate(),
											 * PennantConstants.DBDateFormat))) { scheduleDetail = scheduleMap
											 * .get(DateUtility.formatDate(repayQueue.getRpyDate(),
											 * PennantConstants.DBDateFormat)); }
											 * 
											 * List<Object> resultList = paymentProcessExecution(scheduleDetail,
											 * repayQueue, dateValueDate, linkedTranId, rpyTotal);
											 * 
											 * if (!(Boolean) resultList.get(0)) { actReturnList.add(resultList.get(0));
											 * actReturnList.add(resultList.get(2));
											 * 
											 * logger.debug("Leaving"); return actReturnList; }
											 * 
											 * scheduleMap.remove(scheduleDetail.getSchDate().toString());
											 * scheduleMap.put(scheduleDetail.getSchDate().toString(),
											 * (FinanceScheduleDetail) resultList.get(3));
											 */
			}

			if (!isPenaltyAvail && (repayQueue.getPenaltyBal().compareTo(BigDecimal.ZERO) > 0)) {
				isPenaltyAvail = true;
			}
		}

		String curFinStatus = customerStatusCodeDAO.getFinanceStatus(financeMain.getFinReference(), true);
		boolean isStsChanged = false;
		if (!StringUtils.equals(financeMain.getFinStatus(), curFinStatus)) {
			isStsChanged = true;
		}

		// Finance Status Details insertion, if status modified then change to High Risk Level
		if (isStsChanged) {
			FinStatusDetail statusDetail = new FinStatusDetail();
			statusDetail.setFinReference(financeMain.getFinReference());
			statusDetail.setValueDate(dateValueDate);
			statusDetail.setCustId(financeMain.getCustID());
			statusDetail.setFinStatus(curFinStatus);

			finStatusDetailDAO.saveOrUpdateFinStatus(statusDetail);
		}

		// Reset Finance Schedule Details
		scheduleDetails = new ArrayList<FinanceScheduleDetail>(scheduleMap.values());
		if (scheduleDetails != null && !scheduleDetails.isEmpty()) {
			scheduleDetails = ScheduleCalculator.sortSchdDetails(scheduleDetails);
		}

		// Finance Main Details Update
		financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().add(rpyPri));
		financeMain.setFinStatus(curFinStatus);
		financeMain.setFinStsReason(FinanceConstants.FINSTSRSN_MANUAL);
		BigDecimal totalFinAmt = financeMain.getFinCurrAssetValue().add(financeMain.getFeeChargeAmt());

		if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCIP)) {
			if (totalFinAmt.subtract(financeMain.getFinRepaymentAmount()).compareTo(BigDecimal.ZERO) <= 0) {
				financeMain.setFinIsActive(false);
				financeMain.setClosedDate(FinanceUtil.deriveClosedDate(financeMain));
				financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
			}
		} else if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS)) {
			if (!isPenaltyAvail
					&& totalFinAmt.subtract(financeMain.getFinRepaymentAmount()).compareTo(BigDecimal.ZERO) <= 0) {
				financeMain.setFinIsActive(false);
				financeMain.setClosedDate(FinanceUtil.deriveClosedDate(financeMain));
				financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
			}
		} else if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCPI)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPICS)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC)) {

			// Check Penalty Amount & Repayment's Principal Amount
			if (!isPenaltyAvail
					&& totalFinAmt.subtract(financeMain.getFinRepaymentAmount()).compareTo(BigDecimal.ZERO) <= 0) {

				// Check Total Finance profit Amount
				boolean pftFullyPaid = true;
				for (int i = 1; i < scheduleDetails.size(); i++) {
					FinanceScheduleDetail curSchd = scheduleDetails.get(i);
					if ((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0) {
						pftFullyPaid = false;
						break;
					}
				}

				if (pftFullyPaid) {
					financeMain.setFinIsActive(false);
					financeMain.setClosedDate(SysParamUtil.getAppDate());
					financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
				}
			}
		}

		// Finance Profit Details Updation
		financeProfitDetail = accrualService.calProfitDetails(financeMain, scheduleDetails, financeProfitDetail,
				dateValueDate);
		financeProfitDetail.setFinStatus(financeMain.getFinStatus());
		financeProfitDetail.setFinStsReason(financeMain.getFinStsReason());
		financeProfitDetail.setFinIsActive(financeMain.isFinIsActive());
		financeProfitDetail.setClosingStatus(financeMain.getClosingStatus());
		financeProfitDetail.setLatestRpyDate(dateValueDate);
		financeProfitDetail.setLatestRpyPri(rpyPri);
		financeProfitDetail.setLatestRpyPft(rpyPft);

		String curFinWorstStatus = customerStatusCodeDAO.getFinanceStatus(financeMain.getFinReference(), false);
		financeProfitDetail.setFinWorstStatus(curFinWorstStatus);
		profitDetailsDAO.update(financeProfitDetail, true);

		// Customer Status & Status Change Date(Suspense From Date) Updation
		String custSts = customerDAO.getCustWorstSts(financeMain.getCustID());
		List<Long> custIdList = new ArrayList<Long>(1);
		custIdList.add(financeMain.getCustID());
		List<FinStatusDetail> suspDateSts = financeSuspHeadDAO.getCustSuspDate(custIdList);

		Date suspFromdate = null;
		if (suspDateSts != null && !suspDateSts.isEmpty()) {
			suspFromdate = suspDateSts.get(0).getValueDate();
		}

		FinStatusDetail statusDetail = new FinStatusDetail();
		List<FinStatusDetail> custStatuses = new ArrayList<FinStatusDetail>(1);
		statusDetail.setCustId(financeMain.getCustID());
		statusDetail.setFinStatus(custSts);
		statusDetail.setValueDate(suspFromdate);
		custStatuses.add(statusDetail);

		finStatusDetailDAO.updateCustStatuses(custStatuses);

		statusDetail = null;
		custStatuses = null;
		suspDateSts = null;
		custIdList = null;

		actReturnList.add(true);
		actReturnList.add(linkedTranId);
		actReturnList.add(null);
		actReturnList.add(financeMain);
		actReturnList.add(scheduleDetails);

		logger.debug(Literal.LEAVING);
		return actReturnList;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	@Autowired
	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	@Autowired
	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	@Autowired
	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
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
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
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
	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	@Autowired
	public void setLatePayBucketService(LatePayBucketService latePayBucketService) {
		this.latePayBucketService = latePayBucketService;
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
	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
		this.recoveryPostingsUtil = recoveryPostingsUtil;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	@Autowired
	public void setAssetClassificationService(AssetClassificationService assetClassificationService) {
		this.assetClassificationService = assetClassificationService;
	}

	@Autowired
	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	@Autowired
	public void setFinODCAmountDAO(FinODCAmountDAO finODCAmountDAO) {
		this.finODCAmountDAO = finODCAmountDAO;
	}

}
