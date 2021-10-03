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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.LatePayBucketService;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentDealDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
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
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class RepaymentPostingsUtil {
	private static Logger logger = LogManager.getLogger(RepaymentPostingsUtil.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinRepayQueueDAO finRepayQueueDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private CustomerStatusCodeDAO customerStatusCodeDAO;
	private FinStatusDetailDAO finStatusDetailDAO;
	private OverDueRecoveryPostingsUtil recoveryPostingsUtil;
	private SuspensePostingUtil suspensePostingUtil;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private CustomerDAO customerDAO;
	private OverdueChargeRecoveryDAO recoveryDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private LatePayMarkingService latePayMarkingService;
	private LatePayBucketService latePayBucketService;
	private AccrualService accrualService;
	private ManualAdviseDAO manualAdviseDAO;
	private ProvisionDAO provisionDAO;

	// Assignments
	private AssignmentDAO assignmentDAO;
	private AssignmentDealDAO assignmentDealDAO;
	private FeeTypeDAO feeTypeDAO;
	protected GSTInvoiceTxnService gstInvoiceTxnService;
	protected FinanceTypeDAO financeTypeDAO;
	private FeeTypeService feeTypeService;

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

			// Overdue Details Updation for Paid Penalty
			for (FinRepayQueue repayQueue : finRepayQueueList) {
				if (repayQueue.getRpyDate().compareTo(dateValueDate) >= 0) {
					continue;
				}

				BigDecimal totPenalty = repayQueue.getPenaltyPayNow().add(repayQueue.getWaivedAmount());

				if (totPenalty.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				FinODDetails fod = new FinODDetails();
				fod.setFinID(fm.getFinID());
				fod.setFinReference(fm.getFinReference());
				fod.setFinODSchdDate(repayQueue.getRpyDate());
				fod.setFinODFor(repayQueue.getFinRpyFor());
				fod.setTotPenaltyAmt(BigDecimal.ZERO);
				fod.setTotPenaltyPaid(repayQueue.getPenaltyPayNow());

				if (repayQueue.getPenaltyPayNow().add(repayQueue.getWaivedAmount()).compareTo(BigDecimal.ZERO) < 0) {
					fod.setTotPenaltyBal((repayQueue.getPenaltyPayNow().add(repayQueue.getWaivedAmount())).negate());
				} else {
					fod.setTotPenaltyBal((repayQueue.getPenaltyPayNow().add(repayQueue.getWaivedAmount())));
				}
				fod.setTotWaived(repayQueue.getWaivedAmount());

				if (!aeEvent.isSimulateAccounting()) {
					finODDetailsDAO.updateTotals(fod);
				}
			}
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
			}

			scheduleDetail = updateScheduleDetailsData(scheduleDetail, repayQueue);
			Date sqlDate = DateUtil.getSqlDate(scheduleDetail.getSchDate());
			scheduleMap.put(sqlDate, scheduleDetail);

			FinODDetails latePftODTotal = getLatePftODTotal(repayQueue);
			if (latePftODTotal != null) {
				latePftODTotals.add(latePftODTotal);
			}

			FinODDetails odDetail = getODDetail(repayQueue);
			if (odDetail != null) {
				odDetails.add(odDetail);
			}

			long receiptId = rpyQueueHeader.getReceiptId();
			repayments.add(prepareRepayDetail(repayQueue, valueDate, postDate, linkedTranId, rpyTotal, receiptId));
		}

		// Reset Finance Schedule Details
		scheduleDetails = new ArrayList<FinanceScheduleDetail>(scheduleMap.values());
		scheduleDetails = sortSchdDetails(scheduleDetails);

		if (!financeMain.isSimulateAccounting()) {
			if (CollectionUtils.isNotEmpty(latePftODTotals)) {
				finODDetailsDAO.updateLatePftTotals(latePftODTotals);
			}

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

	/**
	 * Method for Sorting Schedule Details
	 * 
	 * @param schedules
	 * @return
	 */
	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> schedules) {

		if (schedules != null && schedules.size() > 0) {
			Collections.sort(schedules, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return detail1.getSchDate().compareTo(detail2.getSchDate());
				}
			});
		}

		return schedules;
	}

	/**
	 * Method for Posting Repayments and Update Repayments Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws Exception
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	public FinanceMain updateStatus(FinanceMain financeMain, Date dateValueDate,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail pftDetail, List<FinODDetails> overdueList,
			String receiptPurpose, boolean isPresentProc) throws Exception {

		return updateRepayStatus(financeMain, dateValueDate, scheduleDetails, pftDetail, overdueList, receiptPurpose,
				isPresentProc);
	}

	private FinanceMain updateRepayStatus(FinanceMain fm, Date dateValueDate, List<FinanceScheduleDetail> schedules,
			FinanceProfitDetail pftDetail, List<FinODDetails> overdueList, String receiptPurpose, boolean isPresentProc)
			throws Exception {
		logger.debug(Literal.ENTERING);

		// Finance Profit Details Updation
		String oldFinStatus = fm.getFinStatus();
		long finID = fm.getFinID();
		EventProperties eventProperties = fm.getEventProperties();

		Date appDate = null;

		if (eventProperties.isParameterLoaded()) {
			appDate = eventProperties.getAppDate();
		} else {
			appDate = SysParamUtil.getAppDate();
		}

		schedules = sortSchdDetails(schedules);
		pftDetail.setNoInstEarlyStl(pftDetail.getNOInst());
		pftDetail = accrualService.calProfitDetails(fm, schedules, pftDetail, dateValueDate);

		// Update Overdue Details
		if (overdueList != null) {
			latePayMarkingService.updateFinPftDetails(pftDetail, overdueList, dateValueDate);
		}

		latePayBucketService.updateDPDBuketing(schedules, fm, pftDetail, appDate, false);

		fm.setFinStsReason(FinanceConstants.FINSTSRSN_MANUAL);

		// If Penalty fully paid && Schedule payment completed then make status
		// as Inactive
		// (!financeMain.isSanBsdSchdle() || (financeMain.isSanBsdSchdle() &&
		// ((receiptPurpose != null
		// && StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE,
		// receiptPurpose))) this condition sanction based loans closed after scheduled payment.

		if (!isPresentProc && isSchdFullyPaid(finID, schedules) && (!fm.isSanBsdSchdle() || (fm.isSanBsdSchdle()
				&& ((receiptPurpose != null && FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)))))) {

			pftDetail.setSvnAcrCalReq(false);
			fm.setFinIsActive(false);
			fm.setClosedDate(appDate);
			fm.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);

			if (FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)) {
				fm.setClosingStatus(FinanceConstants.CLOSE_STATUS_EARLYSETTLE);
				pftDetail.setSvnAcrTillLBD(pftDetail.getTotalSvnAmount());
			}

			// Previous Month Amortization reset to Total Profit to avoid posting on closing Month End
			pftDetail.setPrvMthAmz(pftDetail.getTotalPftSchd());

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
		List<FinanceRepayments> repayList = financeRepaymentsDAO.getFinRepayListByFinRef(fm.getFinID(), true, "");

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
		Customer customer = customerDAO.getCustomerStatus(fm.getCustID());
		custEODEvent.setCustomer(customer);
		latePayMarkingService.processCustomerStatus(custEODEvent);

		logger.debug(Literal.LEAVING);
		return fm;
	}

	public boolean isSchdFullyPaid(long finID, List<FinanceScheduleDetail> schedules) {
		// Check Total Finance profit Amount
		boolean fullyPaid = true;
		for (FinanceScheduleDetail curSchd : schedules) {
			// Profit
			if ((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Principal
			if ((curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Fees
			if ((curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

		}

		// Check Penalty Paid Fully or not
		if (fullyPaid) {
			FinODDetails overdue = finODDetailsDAO.getTotals(finID);
			if (overdue != null) {
				BigDecimal balPenalty = overdue.getTotPenaltyAmt().subtract(overdue.getTotPenaltyPaid())
						.subtract(overdue.getTotWaived())
						.add(overdue.getLPIAmt().subtract(overdue.getLPIPaid()).subtract(overdue.getLPIWaived()));

				// Penalty Not fully Paid
				if (balPenalty.compareTo(BigDecimal.ZERO) > 0) {
					fullyPaid = false;
				}
			}
		}

		// Check Receivable Advises paid Fully or not
		if (fullyPaid) {
			BigDecimal adviseBal = manualAdviseDAO.getBalanceAmt(finID);
			// Penalty Not fully Paid
			if (adviseBal != null && adviseBal.compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
			}
		}

		return fullyPaid;
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

		aeEvent.setEventProperties(eventProperties);
		if (eventProperties.isParameterLoaded()) {
			aeEvent.setAppDate(eventProperties.getAppDate());
			aeEvent.setAppValueDate(eventProperties.getAppDate());
		} else {
			Date appDate = SysParamUtil.getAppDate();
			aeEvent.setAppDate(appDate);
			aeEvent.setAppValueDate(appDate);
		}

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
		amountCodes.setRpTot(rqh.getPrincipal().add(rqh.getProfit()).add(rqh.getLateProfit()));
		amountCodes.setRpPft(rqh.getProfit());
		amountCodes.setLpiPaid(rqh.getLateProfit());
		amountCodes.setRpPri(rqh.getPrincipal());
		amountCodes.setInstpri(rqh.getPrincipal().subtract(rqh.getPartialPaid()));
		amountCodes.setRpTds(rqh.getTds());
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

				// Profit Due Paid
				if (amountCodes.getPftWaived()
						.compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0) {
					amountCodes.setPftDuePaid(amountCodes.getRpPft());
				} else {
					amountCodes.setPftDuePaid(amountCodes.getRpPft()
							.subtract(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid()))
							.add(amountCodes.getPftWaived()));
				}

			} else {

				// Last Schedule Interest Amounts Paid
				amountCodes.setLastSchPftPaid(BigDecimal.ZERO);
				amountCodes.setLastSchPftWaived(BigDecimal.ZERO);

				// Profit Due Paid
				amountCodes.setPftDuePaid(amountCodes.getRpPft().subtract(rqh.getFutProfit()));

				// Profit Due Waived
				amountCodes.setPftDueWaived(amountCodes.getPftWaived().subtract(rqh.getFutPftWaived()));
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
			BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());

			if (lastSchd.isTDSApplicable()) {

				amountCodes.setLastSchTds((amountCodes.getLastSchPftPaid().multiply(tdsPerc))
						.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN));

				// Splitting TDS amount into Accrued and Unaccrued Paid basis
				if (amountCodes.getAccruedPaid().compareTo(BigDecimal.ZERO) > 0) {

					BigDecimal accrueTds = (amountCodes.getAccruedPaid().multiply(tdsPerc))
							.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN);
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
				amountCodes.setDueTds((amountCodes.getPftDuePaid().multiply(tdsPerc)).divide(BigDecimal.valueOf(100), 0,
						RoundingMode.HALF_DOWN));
			}

			// Balance SubVention Amount
			BigDecimal uSvnAmz = fpd.getTotalSvnAmount().subtract(fpd.getSvnAcrTillLBD());
			if (uSvnAmz.compareTo(BigDecimal.ZERO) > 0) {
				amountCodes.setdSvnAmz(uSvnAmz);
			}
		}

		if (StringUtils.equals(eventCode, AccountingEvent.REPAY)) {
			if (fpd.getTotalPftPaid().add(rqh.getProfit()).compareTo(fpd.getTotalPftSchd()) >= 0) {
				amountCodes
						.setUnAccruedPaid((fpd.getTotalPftSchd().add(fpd.getTdPftCpz())).subtract(fpd.getPrvMthAmz()));
			}
			if (ImplementationConstants.ALLOW_NPA_PROVISION) {
				boolean isExists = provisionDAO.isProvisionExists(fm.getFinID(), TableType.MAIN_TAB);
				// NPA Provision related
				if (isExists && fpd.getCurODDays() > 0) {
					amountCodes.setRpPftPr(amountCodes.getRpPft());
					amountCodes.setRpTotPr(amountCodes.getRpTot());
					amountCodes.setPriPr(amountCodes.getPri());
					amountCodes.setPriSPr(amountCodes.getPriS());
					amountCodes.setRpPft(BigDecimal.ZERO);
					amountCodes.setRpTot(BigDecimal.ZERO);
				} else {
					amountCodes.setPriPr(BigDecimal.ZERO);
					amountCodes.setPriSPr(BigDecimal.ZERO);
				}
			}
		}

		aeEvent.getAcSetIDList().clear();
		if (StringUtils.isNotBlank(fm.getPromotionCode())
				&& (fm.getPromotionSeqId() != null && fm.getPromotionSeqId() == 0)) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getCacheAccountSetID(fm.getPromotionCode(), eventCode,
					FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getCacheAccountSetID(fm.getFinType(), eventCode,
					FinanceConstants.MODULEID_FINTYPE));
		}

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

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		Map<String, Object> glSubHeadCodes = fm.getGlSubHeadCodes();

		dataMap.put("emptype", glSubHeadCodes.get("EMPTYPE"));
		dataMap.put("branchcity", glSubHeadCodes.get("BRANCHCITY"));
		dataMap.put("fincollateralreq", glSubHeadCodes.get("FINCOLLATERALREQ"));
		dataMap.put("btloan", fm.getLoanCategory());

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
				if (rad.getAllocationType().equals(RepayConstants.ALLOCATION_MANADV)) {
					feeTypeCodes.add(rad.getFeeTypeCode());
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
				try {
					postingsPreparationUtil.processPostingDetails(aeEvent);
				} catch (AccountNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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
		Date dateValueDate = DateUtility.getAppValueDate();

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
			scheduleDetails = sortSchdDetails(scheduleDetails);
		}

		// Finance Main Details Update
		financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().add(rpyPri));
		financeMain.setFinStatus(curFinStatus);
		financeMain.setFinStsReason(FinanceConstants.FINSTSRSN_MANUAL);
		BigDecimal totalFinAmt = financeMain.getFinCurrAssetValue().add(financeMain.getFeeChargeAmt());

		if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCIP)) {
			if (totalFinAmt.subtract(financeMain.getFinRepaymentAmount()).compareTo(BigDecimal.ZERO) <= 0) {
				financeMain.setFinIsActive(false);
				financeMain.setClosedDate(DateUtility.getAppDate());
				financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
			}
		} else if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS)) {
			if (!isPenaltyAvail
					&& totalFinAmt.subtract(financeMain.getFinRepaymentAmount()).compareTo(BigDecimal.ZERO) <= 0) {
				financeMain.setFinIsActive(false);
				financeMain.setClosedDate(DateUtility.getAppDate());
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
					financeMain.setClosedDate(DateUtility.getAppDate());
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

		// TODO: need to rename method : Postings for Accrual status Modifications
		// accrualPostings(financeProfitDetail, valueDate, (AEAmountCodes)actReturnList.get(3),
		// (Long)actReturnList.get(1));

		logger.debug("Leaving");
		return actReturnList;
	}

	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		this.finRepayQueueDAO = finRepayQueueDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
		this.recoveryPostingsUtil = recoveryPostingsUtil;
	}

	public void setSuspensePostingUtil(SuspensePostingUtil suspensePostingUtil) {
		this.suspensePostingUtil = suspensePostingUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	public void setLatePayBucketService(LatePayBucketService latePayBucketService) {
		this.latePayBucketService = latePayBucketService;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
		this.assignmentDAO = assignmentDAO;
	}

	public void setAssignmentDealDAO(AssignmentDealDAO assignmentDealDAO) {
		this.assignmentDealDAO = assignmentDealDAO;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

}
