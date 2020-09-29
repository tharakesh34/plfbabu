/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  RepaymentPostingsUtil.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.io.Serializable;
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
import com.pennant.app.constants.AccountEventConstants;
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
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.TableType;

public class RepaymentPostingsUtil implements Serializable {
	private static final long serialVersionUID = 4165353615228874397L;
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

	public RepaymentPostingsUtil() {
		super();
	}

	/**
	 * Method for Posting Repayments and Update Repayments Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	public List<Object> postingProcess(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			List<FinFeeDetail> finFeeDetailList, FinanceProfitDetail financeProfitDetail,
			FinRepayQueueHeader rpyQueueHeader, String eventCode, Date valuedate, Date postDate)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		return postingProcessExecution(financeMain, scheduleDetails, finFeeDetailList, financeProfitDetail,
				rpyQueueHeader, eventCode, valuedate, postDate);
	}

	/**
	 * Method for Posting Repayments and Update Repayments related Tables in Manual Payment Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	private List<Object> postingProcessExecution(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			List<FinFeeDetail> finFeeDetailList, FinanceProfitDetail financeProfitDetail,
			FinRepayQueueHeader rpyQueueHeader, String eventCode, Date valuedate, Date postDate)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> actReturnList = null;
		AEEvent aeEvent = null;
		int transOrder = 0;

		// Repayments Queue list
		List<FinRepayQueue> finRepayQueueList = rpyQueueHeader.getQueueList();

		// Penalty Payments, if any Payment calculations done
		FinTaxIncomeDetail taxIncome = null;
		if (rpyQueueHeader.getPenalty().compareTo(BigDecimal.ZERO) > 0
				|| rpyQueueHeader.getPenaltyWaived().compareTo(BigDecimal.ZERO) > 0) {
			List<Object> returnList = doOverduePostings(aeEvent, finRepayQueueList, postDate, valuedate, financeMain,
					rpyQueueHeader);

			aeEvent = (AEEvent) returnList.get(0);
			if (aeEvent != null) {
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
		BigDecimal totalPayAmount = rpyQueueHeader.getPrincipal().add(rpyQueueHeader.getProfit())
				.add(rpyQueueHeader.getLateProfit()).add(rpyQueueHeader.getFee()).add(rpyQueueHeader.getInsurance())
				.add(rpyQueueHeader.getSuplRent()).add(rpyQueueHeader.getIncrCost()).add(rpyQueueHeader.getPenalty());

		BigDecimal totalWaivedAmount = rpyQueueHeader.getPriWaived().add(rpyQueueHeader.getPftWaived())
				.add(rpyQueueHeader.getLatePftWaived()).add(rpyQueueHeader.getFeeWaived())
				.add(rpyQueueHeader.getInsWaived()).add(rpyQueueHeader.getSuplRentWaived())
				.add(rpyQueueHeader.getIncrCostWaived()).add(rpyQueueHeader.getPenaltyWaived())
				.add(rpyQueueHeader.getAdviseAmount());

		boolean bouncePaidExists = true;

		if ((totalPayAmount.add(totalWaivedAmount)).compareTo(BigDecimal.ZERO) > 0 || bouncePaidExists) {
			actReturnList = doSchedulePostings(rpyQueueHeader, valuedate, postDate, financeMain, scheduleDetails,
					finFeeDetailList, financeProfitDetail, eventCode, aeEvent);
			if ((boolean) actReturnList.get(0)) {
				transOrder = (int) actReturnList.get(7);
			}
		} else if (rpyQueueHeader.getLatePftWaived().compareTo(BigDecimal.ZERO) > 0) {

			//Method for Postings Process only for Late Pay Profit Waiver case
			aeEvent = postingEntryProcess(valuedate, postDate, valuedate, false, financeMain, scheduleDetails,
					financeProfitDetail, rpyQueueHeader, aeEvent, eventCode, finFeeDetailList);

			//Database Updations for Finance RepayQueue Details List
			for (FinRepayQueue repayQueue : finRepayQueueList) {
				if (repayQueue.getLatePayPftWaivedNow().compareTo(BigDecimal.ZERO) > 0) {
					getFinODDetailsDAO().updateLatePftTotals(repayQueue.getFinReference(), repayQueue.getRpyDate(),
							BigDecimal.ZERO, repayQueue.getLatePayPftWaivedNow());
				}
			}

			if (actReturnList == null) {
				actReturnList = new ArrayList<Object>();
			}
			actReturnList.clear();
			actReturnList.add(aeEvent.isPostingSucess());
			actReturnList.add(aeEvent.getLinkedTranId());// Linked Transaction ID
			actReturnList.add(scheduleDetails); // Schedule Details
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
			actReturnList.add(scheduleDetails); // Schedule Details
			actReturnList.add(BigDecimal.ZERO); // UnRealized Amortized Amount

			// LPI Income details
			actReturnList.add(BigDecimal.ZERO); // UnRealized LPI Amount
			actReturnList.add(BigDecimal.ZERO); // UnRealized LPI GST Amount

			actReturnList.add(BigDecimal.ZERO); // capitalize Difference
			if (aeEvent != null) {
				actReturnList.add(aeEvent.getTransOrder()); //trans order
			} else {
				actReturnList.add(transOrder); // trans order
			}
		}

		// LPP Income details
		actReturnList.add(taxIncome); // UnRealized LPP Amount & LPP GST

		logger.debug("Leaving");
		return actReturnList;
	}

	/**
	 * Method for processing Penalty Details for Postings
	 * 
	 * @param linkedTranId
	 * @param finRepayQueueList
	 * @param dateValueDate
	 * @param financeMain
	 * @param finDivison
	 * @return
	 * @throws InterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private List<Object> doOverduePostings(AEEvent aeEvent, List<FinRepayQueue> finRepayQueueList, Date postDate,
			Date dateValueDate, FinanceMain financeMain, FinRepayQueueHeader repayQueueHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		//repayQueueHeader.setLppAmzReqonME(feeType.isAmortzReq());

		ManualAdviseMovements movement = getManualAdvise(finRepayQueueList, dateValueDate);

		if (movement != null) {
			repayQueueHeader.setLppAmzReqonME(movement.isLppAmzReqonME());
			movement.setTaxHeader(getTaxSummaryHeader(finRepayQueueList));
		}

		// GST Invoice Preparation for Penalty (Debit Note)
		FinTaxIncomeDetail taxIncome = null;

		if (movement != null && (movement.getPaidAmount().compareTo(BigDecimal.ZERO) > 0
				|| movement.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0)) {
			List<Object> returnList = recoveryPostingsUtil.recoveryPayment(financeMain, dateValueDate, postDate,
					movement, dateValueDate, aeEvent, repayQueueHeader);

			aeEvent = (AEEvent) returnList.get(0);
			if (!aeEvent.isPostingSucess()) {
				logger.debug("Leaving");
				return returnList;
			}

			taxIncome = (FinTaxIncomeDetail) returnList.get(1);

			//Overdue Details Updation for Paid Penalty
			for (FinRepayQueue repayQueue : finRepayQueueList) {
				if (repayQueue.getRpyDate().compareTo(dateValueDate) >= 0) {
					continue;
				}

				BigDecimal totPenalty = repayQueue.getPenaltyPayNow().add(repayQueue.getWaivedAmount());

				if (totPenalty.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				FinODDetails detail = new FinODDetails();
				detail.setFinReference(financeMain.getFinReference());
				detail.setFinODSchdDate(repayQueue.getRpyDate());
				detail.setFinODFor(repayQueue.getFinRpyFor());
				detail.setTotPenaltyAmt(BigDecimal.ZERO);
				detail.setTotPenaltyPaid(repayQueue.getPenaltyPayNow());
				detail.setTotPenaltyBal((repayQueue.getPenaltyPayNow().add(repayQueue.getWaivedAmount())).negate());
				detail.setTotWaived(repayQueue.getWaivedAmount());
				getFinODDetailsDAO().updateTotals(detail);
			}
		}

		List<Object> returnList = new ArrayList<>();
		returnList.add(aeEvent);
		returnList.add(taxIncome);

		logger.debug("Leaving");
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

	/**
	 * Method for Processing Schedule details for Postings Execution
	 * 
	 * @param rpyQueueHeader
	 * @param valueDate
	 * @param dateValueDate
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param eventCode
	 * @return
	 * @throws InterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private List<Object> doSchedulePostings(FinRepayQueueHeader rpyQueueHeader, Date valueDate, Date postDate,
			FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails, List<FinFeeDetail> finFeeDetailList,
			FinanceProfitDetail financeProfitDetail, String eventCode, AEEvent aeEvent)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> actReturnList = new ArrayList<Object>();

		//Method for Postings Process
		aeEvent = postingEntryProcess(valueDate, postDate, valueDate, false, financeMain, scheduleDetails,
				financeProfitDetail, rpyQueueHeader, aeEvent, eventCode, finFeeDetailList);

		if (!aeEvent.isPostingSucess()) {
			actReturnList.add(aeEvent.isPostingSucess());
			actReturnList.add("9999"); //FIXME

			logger.debug("Leaving");
			return actReturnList;
		}

		// Schedule updations
		scheduleDetails = scheduleUpdate(financeMain, scheduleDetails, rpyQueueHeader, aeEvent.getLinkedTranId(),
				aeEvent.getValueDate(), aeEvent.getPostDate());

		actReturnList.add(aeEvent.isPostingSucess());
		actReturnList.add(aeEvent.getLinkedTranId());
		actReturnList.add(scheduleDetails); // Schedule Details

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
		logger.debug("Leaving");
		return actReturnList;
	}

	//*************************************************************************//
	//**************************** Schedule Updations *************************//
	//*************************************************************************//

	/**
	 * Method for Process Updations After Repayments Process will Success
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param finRepayQueueList
	 * @param linkedTranId
	 * @param valueDate
	 * @param isPartialRepay
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	private List<FinanceScheduleDetail> scheduleUpdate(FinanceMain financeMain,
			List<FinanceScheduleDetail> scheduleDetails, FinRepayQueueHeader rpyQueueHeader, long linkedTranId,
			Date valueDate, Date postDate)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		// Total Payment Amount
		BigDecimal rpyTotal = rpyQueueHeader.getPrincipal().add(rpyQueueHeader.getProfit()).add(rpyQueueHeader.getFee())
				.add(rpyQueueHeader.getLateProfit()).add(rpyQueueHeader.getInsurance())
				.add(rpyQueueHeader.getSuplRent()).add(rpyQueueHeader.getIncrCost()).add(rpyQueueHeader.getPenalty());

		// Total Payment Amount
		BigDecimal waivedTotal = rpyQueueHeader.getPriWaived().add(rpyQueueHeader.getPftWaived())
				.add(rpyQueueHeader.getFeeWaived()).add(rpyQueueHeader.getLatePftWaived())
				.add(rpyQueueHeader.getInsWaived()).add(rpyQueueHeader.getSuplRentWaived())
				.add(rpyQueueHeader.getIncrCostWaived()).add(rpyQueueHeader.getPenaltyWaived());

		// If Postings Process only for Excess Accounts
		if ((rpyTotal.add(waivedTotal)).compareTo(BigDecimal.ZERO) == 0) {
			logger.debug("Leaving");
			return scheduleDetails;
		}

		List<FinRepayQueue> finRepayQueueList = rpyQueueHeader.getQueueList();

		Map<Date, FinanceScheduleDetail> scheduleMap = new HashMap<Date, FinanceScheduleDetail>();
		for (FinanceScheduleDetail detail : scheduleDetails) {
			scheduleMap.put(DateUtility.getSqlDate(detail.getSchDate()), detail);
		}

		//Database Updations for Finance RepayQueue Details List
		for (FinRepayQueue repayQueue : finRepayQueueList) {
			FinanceScheduleDetail scheduleDetail = null;
			if (scheduleMap.containsKey(DateUtility.getSqlDate(repayQueue.getRpyDate()))) {
				scheduleDetail = scheduleMap.get(DateUtility.getSqlDate(repayQueue.getRpyDate()));
			}

			scheduleDetail = paymentUpdate(financeMain, scheduleDetail, repayQueue, valueDate, postDate, linkedTranId,
					rpyTotal, rpyQueueHeader.getReceiptId());
			scheduleMap.remove(DateUtility.getSqlDate(scheduleDetail.getSchDate()));
			scheduleMap.put(DateUtility.getSqlDate(scheduleDetail.getSchDate()), scheduleDetail);
		}

		//Reset Finance Schedule Details
		scheduleDetails = new ArrayList<FinanceScheduleDetail>(scheduleMap.values());
		scheduleDetails = sortSchdDetails(scheduleDetails);

		logger.debug("Leaving");
		return scheduleDetails;
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

	/**
	 * Method for updating Status details and Profit details updation
	 * 
	 * @param financeMain
	 * @param dateValueDate
	 * @param scheduleDetails
	 * @param pftDetail
	 * @return
	 * @throws Exception
	 */
	private FinanceMain updateRepayStatus(FinanceMain financeMain, Date dateValueDate,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail pftDetail, List<FinODDetails> overdueList,
			String receiptPurpose, boolean isPresentProc) throws Exception {
		logger.debug("Entering");

		//Finance Profit Details Updation
		String oldFinStatus = financeMain.getFinStatus();
		String finReference = financeMain.getFinReference();

		scheduleDetails = sortSchdDetails(scheduleDetails);
		pftDetail.setNoInstEarlyStl(pftDetail.getNOInst());
		pftDetail = accrualService.calProfitDetails(financeMain, scheduleDetails, pftDetail, dateValueDate);

		Date appDate = SysParamUtil.getAppDate();

		// Update Overdue Details
		if (overdueList != null) {
			latePayMarkingService.updateFinPftDetails(pftDetail, overdueList, dateValueDate);
		}

		latePayBucketService.updateDPDBuketing(scheduleDetails, financeMain, pftDetail, appDate, false);

		financeMain.setFinStsReason(FinanceConstants.FINSTSRSN_MANUAL);

		// If Penalty fully paid && Schedule payment completed then make status
		// as Inactive
		// (!financeMain.isSanBsdSchdle() || (financeMain.isSanBsdSchdle() &&
		// ((receiptPurpose != null
		// && StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE,
		// receiptPurpose))) this condition sanction based loans closed after scheduled payment.

		if (!isPresentProc && isSchdFullyPaid(finReference, scheduleDetails)
				&& (!financeMain.isSanBsdSchdle() || (financeMain.isSanBsdSchdle() && ((receiptPurpose != null
						&& StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, receiptPurpose)))))) {

			pftDetail.setSvnAcrCalReq(false);
			financeMain.setFinIsActive(false);
			financeMain.setClosedDate(appDate);
			financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);

			if (FinanceConstants.FINSER_EVENT_EARLYSETTLE.equals(receiptPurpose)) {
				financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_EARLYSETTLE);
				pftDetail.setSvnAcrTillLBD(pftDetail.getTotalSvnAmount());
			}

			// Previous Month Amortization reset to Total Profit to avoid posting on closing Month End
			pftDetail.setPrvMthAmz(pftDetail.getTotalPftSchd());

		} else {
			financeMain.setFinIsActive(true);
			financeMain.setClosedDate(null);
			financeMain.setClosingStatus(null);
		}

		pftDetail.setFinStatus(financeMain.getFinStatus());
		pftDetail.setFinStsReason(financeMain.getFinStsReason());
		pftDetail.setFinIsActive(financeMain.isFinIsActive());
		pftDetail.setClosingStatus(financeMain.getClosingStatus());

		//Reset Back Repayments Details
		List<FinanceRepayments> repayList = financeRepaymentsDAO.getFinRepayListByFinRef(finReference, true, "");
		if (repayList != null && !repayList.isEmpty()) {
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
			pftDetail.setLatestRpyDate(financeMain.getFinStartDate());
			pftDetail.setLatestRpyPri(BigDecimal.ZERO);
			pftDetail.setLatestRpyPft(BigDecimal.ZERO);
		}

		getProfitDetailsDAO().update(pftDetail, true);

		//Get Customer Status
		if (!StringUtils.equals(oldFinStatus, financeMain.getFinStatus())) {
			CustEODEvent custEODEvent = new CustEODEvent();
			custEODEvent.setEodDate(dateValueDate);
			custEODEvent.setEodValueDate(dateValueDate);
			Customer customer = customerDAO.getCustomerStatus(financeMain.getCustID());
			custEODEvent.setCustomer(customer);
			latePayMarkingService.processCustomerStatus(custEODEvent);
		}

		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * Method for Checking Schedule is Fully Paid or not
	 * 
	 * @param finReference
	 * @param scheduleDetails
	 * @return
	 */
	public boolean isSchdFullyPaid(String finReference, List<FinanceScheduleDetail> scheduleDetails) {
		//Check Total Finance profit Amount
		boolean fullyPaid = true;
		for (FinanceScheduleDetail curSchd : scheduleDetails) {
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

			// Insurance
			if ((curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Supplementary Rent
			if ((curSchd.getSuplRent().subtract(curSchd.getSuplRentPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Increased Cost
			if ((curSchd.getIncrCost().subtract(curSchd.getIncrCostPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}
		}

		// Check Penalty Paid Fully or not
		if (fullyPaid) {
			FinODDetails overdue = getFinODDetailsDAO().getTotals(finReference);
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
			BigDecimal adviseBal = manualAdviseDAO.getBalanceAmt(finReference);
			// Penalty Not fully Paid
			if (adviseBal != null && adviseBal.compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
			}
		}

		return fullyPaid;
	}

	/**
	 * Method for Posting Process execution in Single Entry Event for Total Repayment Amount
	 * 
	 * @param valueDate
	 * @param dateValueDate
	 * @param dateSchdDate
	 * @param isEODProcess
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param rpyTotal
	 * @param rpyPri
	 * @param rpyPft
	 * @param rpyRefund
	 * @return
	 * @throws InterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private AEEvent postingEntryProcess(Date valueDate, Date postDate, Date dateSchdDate, boolean isEODProcess,
			FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, FinRepayQueueHeader rpyQueueHeader, AEEvent overdueEvent,
			String eventCode, List<FinFeeDetail> finFeeDetailList)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		// AmountCodes Preparation
		// EOD Repayments should pass the value date as schedule for which Repayments are processing
		final BigDecimal totPftSchdOld = financeProfitDetail.getTotalPftSchd();
		AEEvent aeEvent = AEAmounts.procAEAmounts(financeMain, scheduleDetails, financeProfitDetail, eventCode,
				valueDate, dateSchdDate);
		aeEvent.setPostRefId(rpyQueueHeader.getReceiptId());
		aeEvent.setPostingId(financeMain.getPostingId());
		if (PennantConstants.APP_PHASE_EOD.equalsIgnoreCase(rpyQueueHeader.getPostBranch())) {
			aeEvent.setEOD(true);
		}
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setBusinessvertical(financeMain.getBusinessVerticalCode());
		amountCodes.setAlwflexi(financeMain.isAlwFlexi());
		amountCodes.setFinbranch(financeMain.getFinBranch());
		amountCodes.setEntitycode(financeMain.getEntityCode());

		if (StringUtils.isEmpty(rpyQueueHeader.getCashierBranch())) {
			amountCodes.setUserBranch(rpyQueueHeader.getPostBranch());
			aeEvent.setPostingUserBranch(rpyQueueHeader.getPostBranch());
		} else {
			amountCodes.setUserBranch(rpyQueueHeader.getCashierBranch());
			aeEvent.setPostingUserBranch(rpyQueueHeader.getCashierBranch());
		}
		amountCodes.setPartnerBankAc(rpyQueueHeader.getPartnerBankAc());
		amountCodes.setPartnerBankAcType(rpyQueueHeader.getPartnerBankAcType());
		amountCodes.setPaymentType(rpyQueueHeader.getPayType());
		amountCodes.setCashAcExecuted(rpyQueueHeader.isStageAccExecuted() ? "Y" : "N");
		aeEvent.setPostDate(postDate);
		aeEvent.setEntityCode(financeMain.getLovDescEntityCode());
		if (overdueEvent != null) {
			aeEvent.setLinkedTranId(overdueEvent.getLinkedTranId());
			aeEvent.setTransOrder(overdueEvent.getTransOrder());
		}

		//amountCodes.setUserBranch(rpyQueueHeader.getPostBranch());
		amountCodes.setPaymentType(rpyQueueHeader.getPayType());
		amountCodes.setPpAmount(rpyQueueHeader.getPartialPaid());

		// Profit Change Amount Setting
		if (rpyQueueHeader.isPftChgAccReq()) {
			BigDecimal pftchg = amountCodes.getPft().subtract(totPftSchdOld);
			if (pftchg.compareTo(BigDecimal.ZERO) < 0) {
				pftchg = pftchg.negate();
			}
			amountCodes.setPftChg(pftchg);
		} else {
			amountCodes.setPftChg(BigDecimal.ZERO);
		}

		//Set Repay Amount Codes
		amountCodes.setRpTot(
				rpyQueueHeader.getPrincipal().add(rpyQueueHeader.getProfit()).add(rpyQueueHeader.getLateProfit()));
		amountCodes.setRpPft(rpyQueueHeader.getProfit());
		amountCodes.setLpiPaid(rpyQueueHeader.getLateProfit());
		amountCodes.setRpPri(rpyQueueHeader.getPrincipal());
		amountCodes.setInstpri(rpyQueueHeader.getPrincipal().subtract(rpyQueueHeader.getPartialPaid()));
		amountCodes.setRpTds(rpyQueueHeader.getTds());
		amountCodes.setInsttot(rpyQueueHeader.getPrincipal()
				.add(rpyQueueHeader.getProfit().subtract(rpyQueueHeader.getPartialPaid())));

		// Fee Details
		amountCodes.setSchFeePay(rpyQueueHeader.getFee());
		amountCodes.setInsPay(rpyQueueHeader.getInsurance());
		amountCodes.setSuplRentPay(rpyQueueHeader.getSuplRent());
		amountCodes.setIncrCostPay(rpyQueueHeader.getIncrCost());

		// Waived Amounts
		amountCodes.setPriWaived(rpyQueueHeader.getPriWaived());
		amountCodes.setPftWaived(rpyQueueHeader.getPftWaived());
		amountCodes.setLpiWaived(rpyQueueHeader.getLatePftWaived());
		amountCodes.setFeeWaived(rpyQueueHeader.getFeeWaived());
		amountCodes.setInsWaived(rpyQueueHeader.getInsWaived());

		// Penalty Amounts setting in case only on GOld Loan
		amountCodes.setPenaltyPaid(rpyQueueHeader.getPenalty());
		amountCodes.setPenaltyWaived(rpyQueueHeader.getPenaltyWaived());

		// Accrual & Future Paid Details
		if (StringUtils.equals(eventCode, AccountEventConstants.ACCEVENT_EARLYSTL)) {

			int schSize = scheduleDetails.size();
			FinanceScheduleDetail lastSchd = scheduleDetails.get(schSize - 1);

			FinanceScheduleDetail oldLastSchd = null;
			if (lastSchd.isFrqDate()) {
				oldLastSchd = getFinanceScheduleDetailDAO().getFinSchduleDetails(financeMain.getFinReference(),
						lastSchd.getSchDate(), false);
			}

			// If Final Schedule not exists on Approved Schedule details
			if (oldLastSchd == null) {
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

				// Profit Due Waived
				if (amountCodes.getPftWaived()
						.compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0) {
					amountCodes.setPftDueWaived(amountCodes.getPftWaived()
							.subtract(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())));
				} else {
					amountCodes.setPftDueWaived(BigDecimal.ZERO);
				}

				// Principal Due Paid
				if (amountCodes.getPriWaived()
						.compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0) {
					amountCodes.setPriDuePaid(amountCodes.getRpPri());
				} else {
					amountCodes.setPriDuePaid(amountCodes.getRpPri()
							.subtract(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid()))
							.add(amountCodes.getPriWaived()));
				}

				// Principal Due Waived
				if (amountCodes.getPriWaived()
						.compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0) {
					amountCodes.setPriDueWaived(amountCodes.getPriWaived()
							.subtract(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())));
				} else {
					amountCodes.setPriDueWaived(BigDecimal.ZERO);
				}
			} else {

				// Last Schedule Interest Amounts Paid
				amountCodes.setLastSchPftPaid(BigDecimal.ZERO);
				amountCodes.setLastSchPftWaived(BigDecimal.ZERO);

				// Profit Due Paid
				amountCodes.setPftDuePaid(amountCodes.getRpPft().subtract(rpyQueueHeader.getFutProfit()));

				// Profit Due Waived
				amountCodes.setPftDueWaived(amountCodes.getPftWaived().subtract(rpyQueueHeader.getFutPftWaived()));

				amountCodes.setPriDuePaid(amountCodes.getRpPri().subtract(rpyQueueHeader.getFutPrincipal()));
				amountCodes.setPriDueWaived(amountCodes.getPriWaived().subtract(rpyQueueHeader.getFutPriWaived()));

				/*
				 * BigDecimal lastSchdPriBal = lastSchd.getPrincipalSchd()
				 * .subtract(oldLastSchd.getPrincipalSchd().subtract(oldLastSchd.getSchdPriPaid()));
				 * 
				 * // Principal Due Paid if (amountCodes.getPriWaived().compareTo(lastSchdPriBal) > 0) {
				 * 
				 * } else { amountCodes.setPriDuePaid(
				 * amountCodes.getRpPri().subtract(lastSchdPriBal).add(amountCodes.getPriWaived())); }
				 * 
				 * // Principal Due Waived if (amountCodes.getPriWaived().compareTo(lastSchdPriBal) > 0) {
				 * amountCodes.setPriDueWaived(amountCodes.getPriWaived().subtract(lastSchdPriBal)); } else {
				 * amountCodes.setPriDueWaived(BigDecimal.ZERO); }
				 */
			}

			Date curMonthStartDate = DateUtility.getMonthStart(lastSchd.getSchDate());

			// UnAccrual Calculation
			BigDecimal unaccrue = BigDecimal.ZERO;
			/*
			 * if(DateUtility.compare(curMonthStartDate, finMain.getFinStartDate()) <= 0){ curMonthStartDate =
			 * finMain.getFinStartDate(); unaccrue = lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid());
			 * }else{ int curDays = DateUtility.getDaysBetween(lastPrvSchd.getSchDate(), lastSchd.getSchDate()); int
			 * daysTillTodayFromMS = DateUtility.getDaysBetween(curMonthStartDate, lastSchd.getSchDate());
			 * 
			 * // If Previous schedule date greater than current last installment month start date
			 * if(DateUtility.compare(curMonthStartDate, lastPrvSchd.getSchDate()) < 0){
			 * 
			 * int daysFromMSToPrvInst = DateUtility.getDaysBetween(curMonthStartDate, lastPrvSchd.getSchDate());
			 * BigDecimal eachDayPft = lastPrvSchd.getProfitSchd().divide(new BigDecimal(lastPrvSchd.getNoOfDays()), 9,
			 * RoundingMode.HALF_DOWN); BigDecimal totalPftFromPrvSchd = eachDayPft.multiply(new
			 * BigDecimal(daysFromMSToPrvInst));
			 * 
			 * // If Last Previous Installment was paid already
			 * if(lastPrvSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0){ BigDecimal balPrvSchdPft =
			 * lastPrvSchd.getProfitSchd().subtract(lastPrvSchd.getSchdPftPaid());
			 * if(totalPftFromPrvSchd.compareTo(balPrvSchdPft) > 0){ totalPftFromPrvSchd = balPrvSchdPft; } }
			 * 
			 * unaccrue = lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid()).add(totalPftFromPrvSchd); }else{
			 * unaccrue = (lastSchd.getProfitSchd().divide(new BigDecimal(curDays), 9,
			 * RoundingMode.HALF_DOWN)).multiply(new BigDecimal(daysTillTodayFromMS));
			 * if(lastSchd.getSchdPftPaid().compareTo(lastSchd.getProfitSchd().subtract(unaccrue)) > 0){ BigDecimal diff
			 * = lastSchd.getSchdPftPaid().subtract(lastSchd.getProfitSchd().subtract(unaccrue)); unaccrue =
			 * unaccrue.subtract(diff); } } unaccrue = CalculationUtil.roundAmount(unaccrue,
			 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); }
			 */

			// Without Recalculation of Unaccrue to make exact value , subtracting total previous month accrual from actual total profit
			//unaccrue = totalPftSchdNew.subtract(newProfitDetail.getPrvMthAmz());//IF UMFC EXISTS

			if (oldLastSchd == null) {
				FinanceScheduleDetail lastPrvSchd = scheduleDetails.get(schSize - 2);
				if (DateUtility.compare(curMonthStartDate, lastPrvSchd.getSchDate()) <= 0) {

					// Accrual amounts
					amountCodes.setAccruedPaid(BigDecimal.ZERO);
					amountCodes.setAccrueWaived(BigDecimal.ZERO);

					//UnAccrual Amounts
					unaccrue = financeProfitDetail.getTotalPftSchd().subtract(financeProfitDetail.getAmzTillLBD());

					// UnAccrue Paid
					if (amountCodes.getPftWaived().compareTo(unaccrue) > 0) {
						amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
					} else {
						amountCodes.setUnAccruedPaid(unaccrue.subtract(amountCodes.getPftWaived()));
					}

					// UnAccrue Waived
					if (amountCodes.getPftWaived().compareTo(unaccrue) >= 0) {
						amountCodes.setUnAccrueWaived(unaccrue);
					} else {
						amountCodes.setUnAccrueWaived(amountCodes.getPftWaived());
					}
				} else {

					//UnAccrual Amounts
					unaccrue = financeProfitDetail.getTotalPftSchd().subtract(financeProfitDetail.getPrvMthAmz());

					// UnAccrue Paid
					if (amountCodes.getPftWaived().compareTo(unaccrue) > 0) {
						amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
					} else {
						amountCodes.setUnAccruedPaid(unaccrue.subtract(amountCodes.getPftWaived()));
					}

					// UnAccrue Waived
					if (amountCodes.getPftWaived().compareTo(unaccrue) >= 0) {
						amountCodes.setUnAccrueWaived(unaccrue);
					} else {
						amountCodes.setUnAccrueWaived(amountCodes.getPftWaived());
					}

					//Accrual amounts
					/*
					 * BigDecimal accrue = financeProfitDetail.getTotalPftSchd()
					 * .subtract(financeProfitDetail.getAmzTillLBD()).subtract(unaccrue);
					 */

					/*
					 * modified the above on 25th Dec-19, to ensure accrual amount posting will happen during
					 * fore-closer.
					 */

					BigDecimal accrue = rpyQueueHeader.getFutProfit().subtract(unaccrue);

					// Accrual Paid
					if (amountCodes.getPftWaived().compareTo(unaccrue.add(accrue)) >= 0) {
						amountCodes.setAccruedPaid(BigDecimal.ZERO);
					} else {
						if (amountCodes.getPftWaived().compareTo(unaccrue) >= 0) {
							amountCodes.setAccruedPaid(accrue.add(unaccrue).subtract(amountCodes.getPftWaived()));
						} else {
							amountCodes.setAccruedPaid(accrue);
						}
					}

					// Accrual Waived
					if (amountCodes.getPftWaived().compareTo(accrue.add(unaccrue)) >= 0) {
						amountCodes.setAccrueWaived(accrue);
					} else {
						if (amountCodes.getPftWaived().compareTo(unaccrue) >= 0) {
							amountCodes.setAccrueWaived(amountCodes.getPftWaived().subtract(unaccrue));
						} else {
							amountCodes.setAccrueWaived(BigDecimal.ZERO);
						}
					}
				}
				// Future Principal Paid
				if (amountCodes.getPriWaived()
						.compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0) {
					amountCodes.setFuturePriPaid(BigDecimal.ZERO);
				} else {
					amountCodes.setFuturePriPaid(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())
							.subtract(amountCodes.getPriWaived()));
				}

				// Future Principal Waived
				if (amountCodes.getPriWaived()
						.compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0) {
					amountCodes.setFuturePriWaived(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid()));
				} else {
					amountCodes.setFuturePriWaived(amountCodes.getPriWaived());
				}
			} else {

				// Accrual amounts
				amountCodes.setAccruedPaid(BigDecimal.ZERO);
				amountCodes.setAccrueWaived(BigDecimal.ZERO);

				// UnAccrual amounts
				amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
				amountCodes.setUnAccrueWaived(BigDecimal.ZERO);

				amountCodes.setFuturePriPaid(rpyQueueHeader.getFutPrincipal());
				amountCodes.setFuturePriWaived(rpyQueueHeader.getFutPriWaived());

				BigDecimal lastSchdPriBal = lastSchd.getPrincipalSchd()
						.subtract(oldLastSchd.getPrincipalSchd().subtract(oldLastSchd.getSchdPriPaid()));

				/*
				 * // Future Principal Paid if (amountCodes.getPriWaived().compareTo(lastSchdPriBal) > 0) {
				 * amountCodes.setFuturePriPaid(BigDecimal.ZERO); } else {
				 * amountCodes.setFuturePriPaid(lastSchdPriBal.subtract(amountCodes.getPriWaived())); }
				 */
				// Future Principal Waived
				/*
				 * if (amountCodes.getPriWaived().compareTo(lastSchdPriBal) > 0) {
				 * amountCodes.setFuturePriWaived(lastSchdPriBal); } else {
				 * amountCodes.setFuturePriWaived(amountCodes.getPriWaived()); }
				 */
			}

			if (lastSchd.isTDSApplicable()) {
				// TDS for Last Installment
				BigDecimal tdsPerc = new BigDecimal(
						SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
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

				// TDS Due
				amountCodes.setDueTds((amountCodes.getPftDuePaid().multiply(tdsPerc)).divide(BigDecimal.valueOf(100), 0,
						RoundingMode.HALF_DOWN));
			} else {
				amountCodes.setLastSchTds(BigDecimal.ZERO);
				amountCodes.setDueTds(BigDecimal.ZERO);
			}

			// Balance SubVention Amount
			BigDecimal uSvnAmz = financeProfitDetail.getTotalSvnAmount()
					.subtract(financeProfitDetail.getSvnAcrTillLBD());
			if (uSvnAmz.compareTo(BigDecimal.ZERO) > 0) {
				amountCodes.setdSvnAmz(uSvnAmz);
			}
		}

		if (StringUtils.equals(eventCode, AccountEventConstants.ACCEVENT_REPAY)) {
			if (financeProfitDetail.getTotalPftPaid().add(rpyQueueHeader.getProfit())
					.compareTo(financeProfitDetail.getTotalPftSchd()) >= 0) {
				amountCodes.setUnAccruedPaid(
						financeProfitDetail.getTotalPftSchd().subtract(financeProfitDetail.getPrvMthAmz()));
			}
			if (ImplementationConstants.ALLOW_NPA_PROVISION) {
				boolean isExists = provisionDAO.isProvisionExists(financeMain.getFinReference(), TableType.MAIN_TAB);
				// NPA Provision related
				if (isExists && financeProfitDetail.getCurODDays() > 0) {
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
		if (StringUtils.isNotBlank(financeMain.getPromotionCode())
				&& (financeMain.getPromotionSeqId() != null && financeMain.getPromotionSeqId() == 0)) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getPromotionCode(),
					eventCode, FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getFinType(), eventCode,
					FinanceConstants.MODULEID_FINTYPE));
		}

		//Assignment Percentage
		Set<String> excludeFees = null;
		if (financeMain.getAssignmentId() != null && financeMain.getAssignmentId() > 0) {
			Assignment assignment = assignmentDAO.getAssignment(financeMain.getAssignmentId(), "");
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
		Map<String, Object> glSubHeadCodes = financeMain.getGlSubHeadCodes();

		dataMap.put("emptype", glSubHeadCodes.get("emptype"));
		dataMap.put("branchcity", glSubHeadCodes.get("branchcity"));
		dataMap.put("fincollateralreq", glSubHeadCodes.get("fincollateralreq"));
		dataMap.put("btloan", financeMain.getLoanCategory());

		if (excludeFees != null) {
			dataMap.put(AccountConstants.POSTINGS_EXCLUDE_FEES, excludeFees);
		}

		dataMap.put("ae_repledge", "N");

		if (rpyQueueHeader.getExtDataMap() != null) {
			dataMap.putAll(rpyQueueHeader.getExtDataMap());
			if (rpyQueueHeader.getExtDataMap().containsKey("PR_ReceiptAmount")) {
				BigDecimal repledgeAmt = rpyQueueHeader.getExtDataMap().get("PR_ReceiptAmount");
				if (repledgeAmt.compareTo(BigDecimal.ZERO) > 0) {
					dataMap.put("ae_repledge", "Y");
				}
			}
		}

		prepareFeeRulesMap(amountCodes, dataMap, finFeeDetailList, rpyQueueHeader.getPayType());
		addZeroifNotContainsObj(dataMap, "bounceChargePaid");
		addZeroifNotContainsObj(dataMap, "bounceCharge_CGST_P");
		addZeroifNotContainsObj(dataMap, "bounceCharge_IGST_P");
		addZeroifNotContainsObj(dataMap, "bounceCharge_SGST_P");
		addZeroifNotContainsObj(dataMap, "bounceCharge_UGST_P");
		addZeroifNotContainsObj(dataMap, "bounceCharge_CESS_P");

		//#PSD138017
		Map<String, Object> dataMapTemp = GSTCalculator.getGSTDataMap(financeMain.getFinReference());

		if (dataMapTemp != null && !dataMapTemp.isEmpty()) {
			dataMap.putAll(dataMapTemp);
		}

		if (rpyQueueHeader.getGstExecutionMap() != null) {
			dataMap.putAll(rpyQueueHeader.getGstExecutionMap());
		}

		aeEvent.setDataMap(dataMap);

		// Accounting Entry Execution
		aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
		if (overdueEvent != null) {
			overdueEvent.setTransOrder(aeEvent.getTransOrder());
		}

		//GST Invoice Preparation for Exempted waiver case
		BigDecimal pftDueWaived = amountCodes.getPftDueWaived();
		if (ImplementationConstants.ALW_PROFIT_SCHD_INVOICE && aeEvent.getLinkedTranId() > 0 && pftDueWaived != null
				&& pftDueWaived.compareTo(BigDecimal.ZERO) > 0) {
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			financeDetail.getFinScheduleData()
					.setFinanceType(financeTypeDAO.getFinanceTypeByFinType(financeMain.getFinType()));
			financeDetail.setCustomerDetails(null);
			financeDetail.setFinanceTaxDetail(null);

			if (!rpyQueueHeader.getQueueList().isEmpty()) {
				for (FinRepayQueue repayQueue : rpyQueueHeader.getQueueList()) {

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

					String finReference = repayQueue.getFinReference();
					Date rpyDate = repayQueue.getRpyDate();
					Long invoiceID = financeScheduleDetailDAO.getSchdDueInvoiceID(finReference, rpyDate);

					InvoiceDetail invoiceDetail = new InvoiceDetail();
					invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
					invoiceDetail.setFinanceDetail(financeDetail);
					invoiceDetail.setPriAmount(priWaived);
					invoiceDetail.setPftAmount(pftWaived);
					invoiceDetail.setDbInvSetReq(false);
					invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED_TAX_CREDIT);
					invoiceDetail.setDbInvoiceID(invoiceID);

					gstInvoiceTxnService.schdDueTaxInovicePrepration(invoiceDetail);
				}
			}

		}
		if (rpyQueueHeader.getFutPrincipal().compareTo(BigDecimal.ZERO) > 0 && aeEvent.getLinkedTranId() > 0) {
			//GST Invoice Preparation for Exempted paid case

			BigDecimal fpftpaid = BigDecimal.ZERO;
			BigDecimal fpripaid = BigDecimal.ZERO;
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			financeDetail.getFinScheduleData()
					.setFinanceType(financeTypeDAO.getFinanceTypeByFinType(financeMain.getFinType()));
			financeDetail.setCustomerDetails(null);
			financeDetail.setFinanceTaxDetail(null);

			fpripaid = rpyQueueHeader.getFutPrincipal();
			fpftpaid = rpyQueueHeader.getFutProfit();

			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
			invoiceDetail.setFinanceDetail(financeDetail);
			invoiceDetail.setFpriAmount(fpripaid);
			invoiceDetail.setFpftAmount(fpftpaid);
			invoiceDetail.setDbInvSetReq(false);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED);
			gstInvoiceTxnService.schdDueTaxInovicePrepration(invoiceDetail);
		}

		//Overdue Details Updation for Paid Penalty
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

		logger.debug("Leaving");
		return aeEvent;
	}

	private void addZeroifNotContainsObj(Map<String, Object> dataMap, String key) {
		if (dataMap != null) {
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, BigDecimal.ZERO);
			}
		}
	}

	private Map<String, Object> prepareFeeRulesMap(AEAmountCodes amountCodes, Map<String, Object> dataMap,
			List<FinFeeDetail> finFeeDetailList, String payType) {
		logger.debug("Entering");

		if (finFeeDetailList != null) {

			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				if (StringUtils.startsWith(finFeeDetail.getFinEvent(), AccountEventConstants.ACCEVENT_ADDDBS)) {
					continue;
				}

				TaxHeader taxHeader = finFeeDetail.getTaxHeader();
				Taxes cgstTax = new Taxes();
				Taxes sgstTax = new Taxes();
				Taxes igstTax = new Taxes();
				Taxes ugstTax = new Taxes();
				Taxes cessTax = new Taxes();
				List<Taxes> taxDetails = taxHeader.getTaxDetails();

				if (taxHeader != null) {
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
				}

				String feeTypeCode = finFeeDetail.getFeeTypeCode();
				dataMap.put(feeTypeCode + "_C", finFeeDetail.getActualAmount());

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
					dataMap.put(feeTypeCode + "_W",
							finFeeDetail.getWaivedAmount()
									.subtract(cgstTax.getWaivedTax().add(sgstTax.getWaivedTax())
											.add(igstTax.getWaivedTax()).add(ugstTax.getWaivedTax())
											.add(cessTax.getWaivedTax())));
				} else {
					dataMap.put(feeTypeCode + "_W", finFeeDetail.getWaivedAmount());
				}

				dataMap.put(feeTypeCode + "_P", finFeeDetail.getPaidAmount());

				switch (payType) {
				case RepayConstants.RECEIPTMODE_EXCESS:
					payType = "EX_";
					break;
				case RepayConstants.RECEIPTMODE_EMIINADV:
					payType = "EA_";
					break;
				case RepayConstants.RECEIPTMODE_PAYABLE:
					payType = "PA_";
					break;
				default:
					payType = "PB_";
					break;
				}

				dataMap.put(payType + feeTypeCode + "_P", finFeeDetail.getPaidAmount());

				// Calculated Amount
				dataMap.put(feeTypeCode + "_CGST_C", cgstTax.getActualTax());
				dataMap.put(feeTypeCode + "_SGST_C", sgstTax.getActualTax());
				dataMap.put(feeTypeCode + "_IGST_C", igstTax.getActualTax());
				dataMap.put(feeTypeCode + "_UGST_C", ugstTax.getActualTax());
				dataMap.put(feeTypeCode + "_CESS_C", cessTax.getActualTax());

				// Paid Amount
				dataMap.put(feeTypeCode + "_CGST_P", cgstTax.getPaidTax());
				dataMap.put(feeTypeCode + "_SGST_P", sgstTax.getPaidTax());
				dataMap.put(feeTypeCode + "_IGST_P", igstTax.getPaidTax());
				dataMap.put(feeTypeCode + "_UGST_P", ugstTax.getPaidTax());
				dataMap.put(feeTypeCode + "_CESS_P", cessTax.getPaidTax());

				// Net Amount
				dataMap.put(feeTypeCode + "_CGST_N", cgstTax.getNetTax());
				dataMap.put(feeTypeCode + "_SGST_N", sgstTax.getNetTax());
				dataMap.put(feeTypeCode + "_IGST_N", igstTax.getNetTax());
				dataMap.put(feeTypeCode + "_UGST_N", ugstTax.getNetTax());
				dataMap.put(feeTypeCode + "_CESS_N", cessTax.getNetTax());

				// Waiver GST Amounts (GST Waiver Changes)
				dataMap.put(feeTypeCode + "_CGST_W", cgstTax.getWaivedTax());
				dataMap.put(feeTypeCode + "_SGST_W", sgstTax.getWaivedTax());
				dataMap.put(feeTypeCode + "_IGST_W", igstTax.getWaivedTax());
				dataMap.put(feeTypeCode + "_UGST_W", ugstTax.getWaivedTax());
				dataMap.put(feeTypeCode + "_CESS_W", cessTax.getWaivedTax());
			}
		}

		logger.debug("Leaving");
		return dataMap;
	}

	/**
	 * Database Updations related Repayments Schedule Details
	 * 
	 * @param isPostingSuccess
	 * @param financeMain
	 * @param finRepayQueue
	 * @param dateValueDate
	 * @param linkedTranId
	 * @param isEODProcess
	 * @param isPartialRepay
	 * @param financeProfitDetail
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	public FinanceScheduleDetail paymentUpdate(FinanceMain financeMain, FinanceScheduleDetail scheduleDetail,
			FinRepayQueue finRepayQueue, Date dateValueDate, Date postDate, long linkedTranId, BigDecimal totalRpyAmt,
			long receiptId) throws InterfaceException, IllegalAccessException, InvocationTargetException {

		logger.debug("Entering");

		//Schedule Updation depends on Finance Repay Queue Details
		scheduleDetail = updateScheduleDetailsData(scheduleDetail, finRepayQueue);

		// Late Profit Updation
		if (finRepayQueue.getLatePayPftPayNow().compareTo(BigDecimal.ZERO) > 0
				|| finRepayQueue.getLatePayPftWaivedNow().compareTo(BigDecimal.ZERO) > 0) {
			getFinODDetailsDAO().updateLatePftTotals(finRepayQueue.getFinReference(), finRepayQueue.getRpyDate(),
					finRepayQueue.getLatePayPftPayNow(), finRepayQueue.getLatePayPftWaivedNow());
		}

		//FIXME Temporarly we are commented the below code for avoiding 2 time update on FinOdDetails
		/*
		 * if (finRepayQueue.getPenaltyPayNow().compareTo(BigDecimal.ZERO) > 0 ||
		 * finRepayQueue.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) { FinODDetails detail = new FinODDetails();
		 * detail.setFinReference(finRepayQueue.getFinReference()); detail.setFinODSchdDate(finRepayQueue.getRpyDate());
		 * detail.setFinODFor(finRepayQueue.getFinRpyFor()); detail.setTotPenaltyAmt(BigDecimal.ZERO);
		 * detail.setTotPenaltyPaid(finRepayQueue.getPenaltyPayNow());
		 * detail.setTotPenaltyBal((finRepayQueue.getPenaltyPayNow().add(finRepayQueue.getWaivedAmount())).negate());
		 * detail.setTotWaived(finRepayQueue.getWaivedAmount()); getFinODDetailsDAO().updateTotals(detail); }
		 */

		// Finance Repayments Details
		FinanceRepayments repayment = prepareRepayDetailData(finRepayQueue, dateValueDate, postDate, linkedTranId,
				totalRpyAmt);
		repayment.setReceiptId(receiptId);
		getFinanceRepaymentsDAO().save(repayment, "");

		logger.debug("Leaving");
		return scheduleDetail;
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

		schedule.setFinReference(finRepayQueue.getFinReference());
		schedule.setSchDate(finRepayQueue.getRpyDate());

		// Fee Details paid Amounts updation
		schedule.setSchdFeePaid(schedule.getSchdFeePaid().add(finRepayQueue.getSchdFeePayNow())
				.add(finRepayQueue.getSchdFeeWaivedNow()));
		schedule.setSchdInsPaid(schedule.getSchdInsPaid().add(finRepayQueue.getSchdInsPayNow())
				.add(finRepayQueue.getSchdInsWaivedNow()));
		schedule.setSuplRentPaid(schedule.getSuplRentPaid().add(finRepayQueue.getSchdSuplRentPayNow())
				.add(finRepayQueue.getSchdSuplRentWaivedNow()));
		schedule.setIncrCostPaid(schedule.getIncrCostPaid().add(finRepayQueue.getSchdIncrCostPayNow())
				.add(finRepayQueue.getSchdIncrCostWaivedNow()));

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

	/**
	 * Method for Preparing Data for Finance Repay Details Object
	 * 
	 * @param detail
	 * @param main
	 * @param valueDate
	 * @param repayAmtBal
	 * @return
	 */
	public FinanceRepayments prepareRepayDetailData(FinRepayQueue queue, Date valueDate, Date postdate,
			long linkedTranId, BigDecimal totalRpyAmt) {
		logger.debug("Entering");

		FinanceRepayments repayment = new FinanceRepayments();

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
		repayment.setFinSchdPftPaid(queue.getSchdPftPayNow().add(queue.getSchdPftWaivedNow()));
		repayment.setFinSchdTdsPaid(queue.getSchdTdsPayNow());
		repayment.setFinSchdPriPaid(queue.getSchdPriPayNow().add(queue.getSchdPriWaivedNow()));
		repayment.setFinTotSchdPaid(queue.getSchdPftPayNow().add(queue.getSchdPriPayNow())
				.add(queue.getSchdPftWaivedNow()).add(queue.getSchdPriWaivedNow()));
		repayment.setFinFee(BigDecimal.ZERO);
		repayment.setFinWaiver(queue.getWaivedAmount());
		repayment.setFinRefund(queue.getRefundAmount());

		//Fee Details
		repayment.setSchdFeePaid(queue.getSchdFeePayNow().add(queue.getSchdFeeWaivedNow()));
		repayment.setSchdInsPaid(queue.getSchdInsPayNow().add(queue.getSchdInsWaivedNow()));
		repayment.setSchdSuplRentPaid(queue.getSchdSuplRentPayNow().add(queue.getSchdSuplRentWaivedNow()));
		repayment.setSchdIncrCostPaid(queue.getSchdIncrCostPayNow().add(queue.getSchdIncrCostWaivedNow()));

		repayment.setPenaltyPaid(queue.getPenaltyPayNow());
		repayment.setPenaltyWaived(queue.getWaivedAmount());

		logger.debug("Leaving");
		return repayment;
	}

	/**
	 * Method for Posting Repayments and Update Repayments related Tables in Manual Payment Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	@Deprecated
	public List<Object> postingsScreenRepayProcess(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList,
			Map<String, BigDecimal> totalsMap, String eventCode, Map<String, FeeRule> feeRuleDetailMap,
			String finDivision) throws InterfaceException, IllegalAccessException, InvocationTargetException {

		return screenRepayProcess(financeMain, scheduleDetails, financeProfitDetail, finRepayQueueList, totalsMap,
				eventCode, feeRuleDetailMap, finDivision);
	}

	/**
	 * Method for Process Updations After Repayments Process will Success
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param finRepayQueueList
	 * @param linkedTranId
	 * @param isPartialRepay
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	@Deprecated
	public List<Object> UpdateScreenPaymentsProcess(FinanceMain financeMain,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail financeProfitDetail,
			List<FinRepayQueue> finRepayQueueList, long linkedTranId, boolean isPartialRepay,
			AEAmountCodes aeAmountCodes) throws InterfaceException, IllegalAccessException, InvocationTargetException {

		return screenPaymentsUpdation(financeMain, scheduleDetails, financeProfitDetail, finRepayQueueList,
				linkedTranId, isPartialRepay, aeAmountCodes);
	}

	/**
	 * Method for Posting Repayments and Update Repayments related Tables in Manual Payment Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	@Deprecated
	private List<Object> screenRepayProcess(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList,
			Map<String, BigDecimal> totalsMap, String eventCode, Map<String, FeeRule> feeRuleDetailMap,
			String finDivison) throws InterfaceException, IllegalAccessException, InvocationTargetException {

		logger.debug("Entering");
		List<Object> actReturnList = null;

		Date dateValueDate = DateUtility.getAppValueDate();
		Date valueDate = dateValueDate;

		// Based on repayments method then do charges postings first then profit or principal
		// C - PENALTY / CHRAGES, P - PRINCIPAL , I - PROFIT / INTEREST
		if ((ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCPI))
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCIP))) {
			List<Object> returnList = doOverduePostings(null, finRepayQueueList, dateValueDate, dateValueDate,
					financeMain, null);
			AEEvent aeEvent = (AEEvent) returnList.get(0);
			if (aeEvent != null) {
				return null;
			}
		}

		// Schedule Principal and Profit payments
		BigDecimal totRpyAmt = totalsMap.get("totRpyTot");
		if (totRpyAmt != null && totRpyAmt.compareTo(BigDecimal.ZERO) > 0) {
			actReturnList = doSchedulePostings(null, valueDate, valueDate, financeMain, scheduleDetails, null,
					financeProfitDetail, eventCode, null);
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
				List<Object> returnList = doOverduePostings(null, finRepayQueueList, dateValueDate, dateValueDate,
						financeMain, null);
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
				FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(financeMain.getFinReference(),
						"");
				if (suspHead.isManualSusp()) {
					execEventCode = null;
					proceedFurther = false;
				}

				// Fetch Current Details
				if (proceedFurther) {

					// Get Current Over Due Details Days Count after Payment Process
					int curMaxODDays = getFinODDetailsDAO().getFinODDays(financeMain.getFinReference(), "");

					// Status of Suspense from CustStatusCodes based on OD Days when OD Days > 0 
					boolean curFinIsSusp = false;
					if (curMaxODDays > 0) {
						curFinIsSusp = getCustomerStatusCodeDAO().getFinanceSuspendStatus(curMaxODDays);
					}

					// If Finance Still in Suspense case, no need to do Any Accounting further.
					if (!curFinIsSusp) {
						if (curMaxODDays > 0) {
							execEventCode = AccountEventConstants.ACCEVENT_PIS_PD;
						} else {
							execEventCode = AccountEventConstants.ACCEVENT_PIS_NORM;
						}
					}
				}
			} else {

				// Get Current Over Due Details Days Count after Payment Process
				int curMaxODDays = getFinODDetailsDAO().getFinODDays(financeMain.getFinReference(), "");

				if (curMaxODDays == 0) {
					execEventCode = AccountEventConstants.ACCEVENT_PD_NORM;
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
					getPostingsPreparationUtil().processPostingDetails(aeEvent);
				} catch (AccountNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		logger.debug("Leaving");
		return actReturnList;
	}

	/**
	 * Method for Process Updations After Repayments Process will Success
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param finRepayQueueList
	 * @param linkedTranId
	 * @param isPartialRepay
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	@Deprecated
	private List<Object> screenPaymentsUpdation(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList, long linkedTranId,
			boolean isPartialRepay, AEAmountCodes aeAmountCodes)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
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

		//Database Updations for Finance RepayQueue Details List
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

		String curFinStatus = getCustomerStatusCodeDAO().getFinanceStatus(financeMain.getFinReference(), true);
		boolean isStsChanged = false;
		if (!StringUtils.equals(financeMain.getFinStatus(), curFinStatus)) {
			isStsChanged = true;
		}

		//Finance Status Details insertion, if status modified then change to High Risk Level
		if (isStsChanged) {
			FinStatusDetail statusDetail = new FinStatusDetail();
			statusDetail.setFinReference(financeMain.getFinReference());
			statusDetail.setValueDate(dateValueDate);
			statusDetail.setCustId(financeMain.getCustID());
			statusDetail.setFinStatus(curFinStatus);

			getFinStatusDetailDAO().saveOrUpdateFinStatus(statusDetail);
		}

		//Reset Finance Schedule Details
		scheduleDetails = new ArrayList<FinanceScheduleDetail>(scheduleMap.values());
		if (scheduleDetails != null && !scheduleDetails.isEmpty()) {
			scheduleDetails = sortSchdDetails(scheduleDetails);
		}

		// Finance Main Details Update
		financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().add(rpyPri));
		financeMain.setFinStatus(curFinStatus);
		financeMain.setFinStsReason(FinanceConstants.FINSTSRSN_MANUAL);
		BigDecimal totalFinAmt = financeMain.getFinCurrAssetValue().add(financeMain.getFeeChargeAmt())
				.add(financeMain.getInsuranceAmt());

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

			//Check Penalty Amount & Repayment's Principal Amount
			if (!isPenaltyAvail
					&& totalFinAmt.subtract(financeMain.getFinRepaymentAmount()).compareTo(BigDecimal.ZERO) <= 0) {

				//Check Total Finance profit Amount
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

		//Finance Profit Details Updation
		financeProfitDetail = accrualService.calProfitDetails(financeMain, scheduleDetails, financeProfitDetail,
				dateValueDate);
		financeProfitDetail.setFinStatus(financeMain.getFinStatus());
		financeProfitDetail.setFinStsReason(financeMain.getFinStsReason());
		financeProfitDetail.setFinIsActive(financeMain.isFinIsActive());
		financeProfitDetail.setClosingStatus(financeMain.getClosingStatus());
		financeProfitDetail.setLatestRpyDate(dateValueDate);
		financeProfitDetail.setLatestRpyPri(rpyPri);
		financeProfitDetail.setLatestRpyPft(rpyPft);

		String curFinWorstStatus = getCustomerStatusCodeDAO().getFinanceStatus(financeMain.getFinReference(), false);
		financeProfitDetail.setFinWorstStatus(curFinWorstStatus);
		getProfitDetailsDAO().update(financeProfitDetail, true);

		//Customer Status & Status Change Date(Suspense From Date) Updation
		String custSts = getCustomerDAO().getCustWorstSts(financeMain.getCustID());
		List<Long> custIdList = new ArrayList<Long>(1);
		custIdList.add(financeMain.getCustID());
		List<FinStatusDetail> suspDateSts = getFinanceSuspHeadDAO().getCustSuspDate(custIdList);

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

		getFinStatusDetailDAO().updateCustStatuses(custStatuses);

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
		//accrualPostings(financeProfitDetail, valueDate, (AEAmountCodes)actReturnList.get(3), (Long)actReturnList.get(1));

		logger.debug("Leaving");
		return actReturnList;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		this.finRepayQueueDAO = finRepayQueueDAO;
	}

	public FinRepayQueueDAO getFinRepayQueueDAO() {
		return finRepayQueueDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
		return customerStatusCodeDAO;
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	public FinStatusDetailDAO getFinStatusDetailDAO() {
		return finStatusDetailDAO;
	}

	public OverDueRecoveryPostingsUtil getRecoveryPostingsUtil() {
		return recoveryPostingsUtil;
	}

	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
		this.recoveryPostingsUtil = recoveryPostingsUtil;
	}

	public void setSuspensePostingUtil(SuspensePostingUtil suspensePostingUtil) {
		this.suspensePostingUtil = suspensePostingUtil;
	}

	public SuspensePostingUtil getSuspensePostingUtil() {
		return suspensePostingUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return profitDetailsDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return recoveryDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public LatePayMarkingService getLatePayMarkingService() {
		return latePayMarkingService;
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

	public ProvisionDAO getProvisionDAO() {
		return provisionDAO;
	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}
}
