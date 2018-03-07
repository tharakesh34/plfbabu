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
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.LatePayBucketService;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueueHeader;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;

public class RepaymentPostingsUtil implements Serializable {
	private static final long			serialVersionUID	= 4165353615228874397L;
	private static Logger				logger				= Logger.getLogger(RepaymentPostingsUtil.class);

	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private FinanceMainDAO				financeMainDAO;
	private FinRepayQueueDAO			finRepayQueueDAO;
	private FinanceRepaymentsDAO		financeRepaymentsDAO;
	private CustomerStatusCodeDAO		customerStatusCodeDAO;
	private FinStatusDetailDAO			finStatusDetailDAO;
	private OverDueRecoveryPostingsUtil	recoveryPostingsUtil;
	private SuspensePostingUtil			suspensePostingUtil;
	private PostingsPreparationUtil		postingsPreparationUtil;
	private FinanceProfitDetailDAO		profitDetailsDAO;
	private FinanceSuspHeadDAO			financeSuspHeadDAO;
	private CustomerDAO					customerDAO;
	private OverdueChargeRecoveryDAO	recoveryDAO;
	private FinODDetailsDAO				finODDetailsDAO;
	private LatePayMarkingService		latePayMarkingService;
	private LatePayBucketService		latePayBucketService;
	private AccrualService 				accrualService;

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
	public List<Object> postingProcess(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,List<FinFeeDetail> finFeeDetailList,
			FinanceProfitDetail financeProfitDetail, FinRepayQueueHeader rpyQueueHeader, String eventCode, Date valuedate,Date postDate)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		return postingProcessExecution(financeMain, scheduleDetails, finFeeDetailList, financeProfitDetail, rpyQueueHeader, eventCode,
				valuedate,postDate);
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
	private List<Object> postingProcessExecution(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,List<FinFeeDetail> finFeeDetailList,
			FinanceProfitDetail financeProfitDetail, FinRepayQueueHeader rpyQueueHeader, String eventCode, Date valuedate,Date postDate)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> actReturnList = null;
		AEEvent aeEvent = null;

		// Repayments Queue list
		List<FinRepayQueue> finRepayQueueList = rpyQueueHeader.getQueueList();

		// Penalty Payments, if any Payment calculations done
		if (rpyQueueHeader.getPenalty().compareTo(BigDecimal.ZERO) > 0 || rpyQueueHeader.getPenaltyWaived().compareTo(BigDecimal.ZERO) > 0) {
			aeEvent = doOverduePostings(aeEvent, finRepayQueueList, postDate, valuedate, financeMain, rpyQueueHeader);

			if (aeEvent != null) {
				if (!aeEvent.isPostingSucess()) {
					actReturnList = new ArrayList<Object>(2);
					actReturnList.add(false);
					actReturnList.add(aeEvent.getErrorMessage());
					return actReturnList;
				}
			}
		}

		// Total Schedule Payments
		BigDecimal totalPayAmount = rpyQueueHeader.getPrincipal().add(rpyQueueHeader.getProfit())
				.add(rpyQueueHeader.getLateProfit()).add(rpyQueueHeader.getFee()).add(rpyQueueHeader.getInsurance())
				.add(rpyQueueHeader.getSuplRent()).add(rpyQueueHeader.getIncrCost());
		
		BigDecimal totalWaivedAmount = rpyQueueHeader.getPriWaived().add(rpyQueueHeader.getPftWaived())
				.add(rpyQueueHeader.getLatePftWaived()).add(rpyQueueHeader.getFeeWaived()).add(rpyQueueHeader.getInsWaived())
				.add(rpyQueueHeader.getSuplRentWaived()).add(rpyQueueHeader.getIncrCostWaived());


		if ((totalPayAmount.add(totalWaivedAmount)).compareTo(BigDecimal.ZERO) > 0) {
			actReturnList = doSchedulePostings(rpyQueueHeader, valuedate,postDate, financeMain, scheduleDetails,
					finFeeDetailList,financeProfitDetail, eventCode, aeEvent);
		} else if (rpyQueueHeader.getLatePftWaived().compareTo(BigDecimal.ZERO) > 0) {
			
			//Method for Postings Process only for Late Pay Profit Waiver case
			aeEvent = postingEntryProcess(valuedate, postDate,valuedate, false, financeMain,
					scheduleDetails, financeProfitDetail, rpyQueueHeader, aeEvent, eventCode, finFeeDetailList);
			
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

		} else {
			if (actReturnList == null) {
				actReturnList = new ArrayList<Object>();
			}
			actReturnList.clear();
			actReturnList.add(true); // Postings Success
			if(aeEvent != null){
				actReturnList.add(aeEvent.getLinkedTranId()); // Linked Transaction ID
			}else{
				actReturnList.add(Long.MIN_VALUE); // Linked Transaction ID
			}
			actReturnList.add(scheduleDetails); // Schedule Details
		}

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
	private AEEvent doOverduePostings(AEEvent aeEvent, List<FinRepayQueue> finRepayQueueList, Date postDate,
			Date dateValueDate, FinanceMain financeMain, FinRepayQueueHeader repayQueueHeader) throws InterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");
		for (FinRepayQueue repayQueue : finRepayQueueList) {

			if (repayQueue.getRpyDate().compareTo(dateValueDate) < 0) {

				aeEvent = getRecoveryPostingsUtil().recoveryPayment(financeMain, dateValueDate, postDate, repayQueue.getRpyDate(), 
						repayQueue.getFinRpyFor(), dateValueDate, repayQueue.getPenaltyPayNow(), repayQueue.getWaivedAmount(), 
						repayQueue.getChargeType(), aeEvent, repayQueueHeader);

				if (!aeEvent.isPostingSucess()) {
					return aeEvent;
				} 
			}
		}
		logger.debug("Leaving");
		return aeEvent;
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
	private List<Object> doSchedulePostings(FinRepayQueueHeader rpyQueueHeader, Date valueDate,Date postDate,
			FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,List<FinFeeDetail> finFeeDetailList,
			FinanceProfitDetail financeProfitDetail, String eventCode, AEEvent aeEvent) throws InterfaceException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> actReturnList = new ArrayList<Object>();

		//Method for Postings Process
		aeEvent = postingEntryProcess(valueDate, postDate,valueDate, false, financeMain,
				scheduleDetails, financeProfitDetail, rpyQueueHeader, aeEvent, eventCode, finFeeDetailList);

		if (!aeEvent.isPostingSucess()) {
			actReturnList.add(aeEvent.isPostingSucess());
			actReturnList.add("9999"); //FIXME

			logger.debug("Leaving");
			return actReturnList;
		}

		// Schedule updations
		scheduleDetails = scheduleUpdate(financeMain, scheduleDetails, rpyQueueHeader, aeEvent.getLinkedTranId(),
				aeEvent.getValueDate(),aeEvent.getPostDate());

		actReturnList.add(aeEvent.isPostingSucess());
		actReturnList.add(aeEvent.getLinkedTranId());
		actReturnList.add(scheduleDetails); // Schedule Details

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
			List<FinanceScheduleDetail> scheduleDetails, FinRepayQueueHeader rpyQueueHeader, long linkedTranId, Date valueDate,Date postDate)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");


		// Total Payment Amount
		BigDecimal rpyTotal = rpyQueueHeader.getPrincipal().add(rpyQueueHeader.getProfit()).add(rpyQueueHeader.getFee()).add(rpyQueueHeader.getLateProfit())
				.add(rpyQueueHeader.getInsurance()).add(rpyQueueHeader.getSuplRent()).add(rpyQueueHeader.getIncrCost());

		// Total Payment Amount
		BigDecimal waivedTotal = rpyQueueHeader.getPriWaived().add(rpyQueueHeader.getPftWaived()).add(rpyQueueHeader.getFeeWaived()).add(rpyQueueHeader.getLatePftWaived())
				.add(rpyQueueHeader.getInsWaived()).add(rpyQueueHeader.getSuplRentWaived()).add(rpyQueueHeader.getIncrCostWaived());

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

			scheduleDetail = paymentUpdate(financeMain, scheduleDetail, repayQueue, valueDate,postDate, linkedTranId,
					rpyTotal);
			scheduleMap.remove(scheduleDetail.getSchDate());
			scheduleMap.put(scheduleDetail.getSchDate(), scheduleDetail);
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
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail pftDetail, List<FinODDetails> overdueList, String receiptPurpose) throws Exception {

		return updateRepayStatus(financeMain, dateValueDate, scheduleDetails, pftDetail,overdueList, receiptPurpose);
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
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail pftDetail, List<FinODDetails> overdueList, String receiptPurpose) throws Exception {
		logger.debug("Entering");

		//Finance Profit Details Updation
		String oldFinStatus = financeMain.getFinStatus();
		scheduleDetails = sortSchdDetails(scheduleDetails);
		pftDetail = accrualService.calProfitDetails(financeMain, scheduleDetails, pftDetail, dateValueDate);
		
		// Update Overdue Details
		if (overdueList != null) {
			latePayMarkingService.updateFinPftDetails(pftDetail, overdueList, dateValueDate);
		}
		
		latePayBucketService.updateDPDBuketing(scheduleDetails, financeMain, pftDetail, dateValueDate);

		financeMain.setFinStsReason(FinanceConstants.FINSTSRSN_MANUAL);

		// If Penalty fully paid && Schedule payment completed then make status as Inactive
		if (isSchdFullyPaid(financeMain.getFinReference(), scheduleDetails)) {
			financeMain.setFinIsActive(false);
			financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
			if (receiptPurpose !=null && StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, receiptPurpose)) {
				financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_EARLYSETTLE);	
			}
		} else {
			financeMain.setFinIsActive(true);
			financeMain.setClosingStatus(null);
		}

		pftDetail.setFinStatus(financeMain.getFinStatus());
		pftDetail.setFinStsReason(financeMain.getFinStsReason());
		pftDetail.setFinIsActive(financeMain.isFinIsActive());
		pftDetail.setClosingStatus(financeMain.getClosingStatus());
		
		//Reset Back Repayments Details
		List<FinanceRepayments> repayList = getFinanceRepaymentsDAO().getFinRepayListByFinRef(financeMain.getFinReference(), true, "");
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
		if(!StringUtils.equals(oldFinStatus, financeMain.getFinStatus())){
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
		for (int i = 1; i < scheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = scheduleDetails.get(i);

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
				BigDecimal balPenalty = overdue.getTotPenaltyAmt().subtract(overdue.getTotPenaltyPaid()).subtract(overdue.getTotWaived())
						.add(overdue.getLPIAmt().subtract(overdue.getLPIPaid()).subtract(overdue.getLPIWaived()));

				// Penalty Not fully Paid
				if (balPenalty.compareTo(BigDecimal.ZERO) > 0) {
					fullyPaid = false;
				}
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
	private AEEvent postingEntryProcess(Date valueDate,Date postDate, Date dateSchdDate,
			boolean isEODProcess, FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, FinRepayQueueHeader rpyQueueHeader, AEEvent overdueEvent,
			String eventCode, List<FinFeeDetail> finFeeDetailList) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		// AmountCodes Preparation
		// EOD Repayments should pass the value date as schedule for which Repayments are processing
		final BigDecimal totPftSchdOld = financeProfitDetail.getTotalPftSchd();
		AEEvent aeEvent = AEAmounts.procAEAmounts(financeMain, scheduleDetails, financeProfitDetail, eventCode,
				valueDate, dateSchdDate);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		aeEvent.setPostingUserBranch(rpyQueueHeader.getPostBranch());
		amountCodes.setPartnerBankAc(rpyQueueHeader.getPartnerBankAc());
		amountCodes.setPartnerBankAcType(rpyQueueHeader.getPartnerBankAcType());
		aeEvent.setPostDate(postDate);
		
		if(overdueEvent != null){
			aeEvent.setLinkedTranId(overdueEvent.getLinkedTranId());
			aeEvent.setTransOrder(overdueEvent.getTransOrder());
		}
		
		// Profit Change Amount Setting
		if(rpyQueueHeader.isPftChgAccReq()){
			BigDecimal pftchg = amountCodes.getPft().subtract(totPftSchdOld);
			if(pftchg.compareTo(BigDecimal.ZERO) < 0){
				pftchg = pftchg.negate();
			}
			amountCodes.setPftChg(pftchg);
		}else{
			amountCodes.setPftChg(BigDecimal.ZERO);
		}

		//Set Repay Amount Codes
		amountCodes.setRpTot(rpyQueueHeader.getPrincipal().add(rpyQueueHeader.getProfit()).add(rpyQueueHeader.getLateProfit()));
		amountCodes.setRpPft(rpyQueueHeader.getProfit().add(rpyQueueHeader.getLateProfit()));
		amountCodes.setRpPri(rpyQueueHeader.getPrincipal());
		amountCodes.setRpTds(rpyQueueHeader.getTds());

		// Fee Details
		amountCodes.setSchFeePay(rpyQueueHeader.getFee());
		amountCodes.setInsPay(rpyQueueHeader.getInsurance());
		amountCodes.setSuplRentPay(rpyQueueHeader.getSuplRent());
		amountCodes.setIncrCostPay(rpyQueueHeader.getIncrCost());

		// Waived Amounts
		amountCodes.setPriWaived(rpyQueueHeader.getPriWaived());
		amountCodes.setPftWaived(rpyQueueHeader.getPftWaived().add(rpyQueueHeader.getLatePftWaived()));
		amountCodes.setFeeWaived(rpyQueueHeader.getFeeWaived());
		amountCodes.setInsWaived(rpyQueueHeader.getInsWaived());
		
		// Accrual & Future Paid Details
		if(StringUtils.equals(eventCode, AccountEventConstants.ACCEVENT_EARLYSTL)){
			
			int schSize = scheduleDetails.size();
			FinanceScheduleDetail lastSchd = scheduleDetails.get(schSize - 1);
			
			// Last Schedule Interest Amounts Paid
			if(amountCodes.getPftWaived().compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0){
				amountCodes.setLastSchPftPaid(BigDecimal.ZERO);
			}else{
				amountCodes.setLastSchPftPaid(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid()).subtract(amountCodes.getPftWaived()));
			}
			
			// Last Schedule Interest Amounts Waived
			if(amountCodes.getPftWaived().compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0){
				amountCodes.setLastSchPftWaived(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid()));
			}else{
				amountCodes.setLastSchPftWaived(amountCodes.getPftWaived());
			}
			
			// Profit Due Paid
			if(amountCodes.getPftWaived().compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0){
				amountCodes.setPftDuePaid(amountCodes.getRpPft());
			}else{
				amountCodes.setPftDuePaid(amountCodes.getRpPft().subtract(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())).add(amountCodes.getPftWaived()));
			}
			
			// Profit Due Waived
			if(amountCodes.getPftWaived().compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0){
				amountCodes.setPftDueWaived(amountCodes.getPftWaived().subtract(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())));
			}else{
				amountCodes.setPftDueWaived(BigDecimal.ZERO);
			}
			
			// Principal Due Paid
			if(amountCodes.getPriWaived().compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0){
				amountCodes.setPriDuePaid(amountCodes.getRpPri());
			}else{

				amountCodes.setPriDuePaid(amountCodes.getRpPri().subtract(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())).add(amountCodes.getPriWaived()));
			}
			
			// Principal Due Waived
			if(amountCodes.getPriWaived().compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0){
				amountCodes.setPriDueWaived(amountCodes.getPriWaived().subtract(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())));
			}else{
				amountCodes.setPriDueWaived(BigDecimal.ZERO);
			}
			
			Date curMonthStartDate = DateUtility.getMonthStartDate(lastSchd.getSchDate());
				
			// UnAccrual Calculation
			BigDecimal unaccrue = BigDecimal.ZERO;
			/*if(DateUtility.compare(curMonthStartDate, financeMain.getFinStartDate()) <= 0){
				curMonthStartDate = financeMain.getFinStartDate();
				unaccrue = lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid());
			}else{
				int curDays = DateUtility.getDaysBetween(lastPrvSchd.getSchDate(), lastSchd.getSchDate());
				int daysTillTodayFromMS = DateUtility.getDaysBetween(curMonthStartDate, lastSchd.getSchDate());
				
				// If Previous schedule date greater than current last installment month start date
				if(DateUtility.compare(curMonthStartDate, lastPrvSchd.getSchDate()) < 0){
					
					int daysFromMSToPrvInst = DateUtility.getDaysBetween(curMonthStartDate, lastPrvSchd.getSchDate());
					BigDecimal eachDayPft = lastPrvSchd.getProfitSchd().divide(new BigDecimal(lastPrvSchd.getNoOfDays()), 9, RoundingMode.HALF_DOWN);
					BigDecimal totalPftFromPrvSchd = eachDayPft.multiply(new BigDecimal(daysFromMSToPrvInst));
					
					// If Last Previous Installment was paid already
					if(lastPrvSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0){
						BigDecimal balPrvSchdPft = lastPrvSchd.getProfitSchd().subtract(lastPrvSchd.getSchdPftPaid());
						if(totalPftFromPrvSchd.compareTo(balPrvSchdPft) > 0){
							totalPftFromPrvSchd = balPrvSchdPft;
						}
					}
					
					unaccrue = lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid()).add(totalPftFromPrvSchd);
				}else{
					amountCodes.setAccruedPaid(acrTillMonthEndFromLastDue);
				}

				// Accrual Waived
				if(amountCodes.getPftWaived().compareTo(lastSchd.getProfitSchd().subtract(acrTillMonthEndFromLastDue)) > 0){
					BigDecimal diff = amountCodes.getPftWaived().subtract(lastSchd.getProfitSchd().subtract(acrTillMonthEndFromLastDue));
					if(diff.compareTo(acrTillMonthEndFromLastDue) > 0){
						amountCodes.setAccrueWaived(acrTillMonthEndFromLastDue);
					}else{
						amountCodes.setAccrueWaived(acrTillMonthEndFromLastDue.subtract(diff));

					unaccrue = (lastSchd.getProfitSchd().divide(new BigDecimal(curDays), 9, RoundingMode.HALF_DOWN)).multiply(new BigDecimal(daysTillTodayFromMS));
					if(lastSchd.getSchdPftPaid().compareTo(lastSchd.getProfitSchd().subtract(unaccrue)) > 0){
						BigDecimal diff = lastSchd.getSchdPftPaid().subtract(lastSchd.getProfitSchd().subtract(unaccrue));
						unaccrue = unaccrue.subtract(diff);
					}
				}
				unaccrue = CalculationUtil.roundAmount(unaccrue, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
			}*/
			
			// Without Recalculation of Unaccrue to make exact value , subtracting total previous month accrual from actual total profit
			unaccrue = amountCodes.getPft().subtract(financeProfitDetail.getPrvMthAmz());

			// UnAccrue Paid
			if(amountCodes.getPftWaived().compareTo(unaccrue) > 0){
				amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
			}else{
				amountCodes.setUnAccruedPaid(unaccrue.subtract(amountCodes.getPftWaived()));
			}

			// UnAccrue Waived
			if(amountCodes.getPftWaived().compareTo(unaccrue) > 0){
				amountCodes.setUnAccrueWaived(unaccrue);
			}else{
				amountCodes.setUnAccrueWaived(amountCodes.getPftWaived());
			}

			// Accrual Calculation
			if(DateUtility.compare(curMonthStartDate, financeMain.getFinStartDate()) <= 0){
				amountCodes.setAccruedPaid(BigDecimal.ZERO);
				amountCodes.setAccrueWaived(BigDecimal.ZERO);
			}else{
				BigDecimal totalUnAccrue = amountCodes.getUnAccrueWaived().add(amountCodes.getUnAccruedPaid());
				BigDecimal totalAccrue = amountCodes.getRpPft().subtract(totalUnAccrue);
				
				// Accrual Paid
				if(amountCodes.getPftWaived().compareTo(totalUnAccrue) >= 0){
					amountCodes.setAccruedPaid(totalAccrue.add(totalUnAccrue).subtract(amountCodes.getPftWaived()));
				}else{
					amountCodes.setAccruedPaid(totalAccrue);
				}
				
				// Accrual Waived
				if(amountCodes.getPftWaived().compareTo(totalUnAccrue) >= 0){
					amountCodes.setAccrueWaived(amountCodes.getPftWaived().subtract(totalUnAccrue));
				}else{
					amountCodes.setAccrueWaived(BigDecimal.ZERO);
				}
			}
			
			// Future Principal Paid
			if(amountCodes.getPriWaived().compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0){
				amountCodes.setFuturePriPaid(BigDecimal.ZERO);
			}else{
				amountCodes.setFuturePriPaid(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid()).subtract(amountCodes.getPriWaived()));
			}
			
			// Future Principal Waived
			if(amountCodes.getPriWaived().compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0){
				amountCodes.setFuturePriWaived(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid()));
			}else{
				amountCodes.setFuturePriWaived(amountCodes.getPriWaived());
			}
		
			if(financeMain.isTDSApplicable()){
				// TDS for Last Installment
				BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
				amountCodes.setLastSchTds((amountCodes.getLastSchPftPaid().multiply(tdsPerc)).divide(BigDecimal.valueOf(100),0,RoundingMode.HALF_DOWN));
			
				// TDS Due
				amountCodes.setDueTds((amountCodes.getPftDuePaid().multiply(tdsPerc)).divide(BigDecimal.valueOf(100),0,RoundingMode.HALF_DOWN));
			}else{
				amountCodes.setLastSchTds(BigDecimal.ZERO);
				amountCodes.setDueTds(BigDecimal.ZERO);
			}
		}
		
		aeEvent.getAcSetIDList().clear();
		if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getPromotionCode(), eventCode, FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getFinType(), eventCode, FinanceConstants.MODULEID_FINTYPE));
		}

		HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues(); 
		if(rpyQueueHeader.getExtDataMap() != null){
			dataMap.putAll(rpyQueueHeader.getExtDataMap());
		}
		prepareFeeRulesMap(amountCodes, dataMap, finFeeDetailList, rpyQueueHeader.getPayType());
		aeEvent.setDataMap(dataMap);

		// Accounting Entry Execution
		aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
		
		logger.debug("Leaving");
		return aeEvent;
	}
	
	private HashMap<String, Object> prepareFeeRulesMap(AEAmountCodes amountCodes, HashMap<String, Object> dataMap, List<FinFeeDetail> finFeeDetailList,
			String payType) {
		logger.debug("Entering");

		if (finFeeDetailList != null) {

			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				if(!finFeeDetail.isRcdVisible()){
					continue;
				}
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_C", finFeeDetail.getActualAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_W", finFeeDetail.getWaivedAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_P", finFeeDetail.getPaidAmount());
				
				if(StringUtils.equals(payType, RepayConstants.PAYTYPE_EXCESS)){
					payType = "EX_";
				}else if(StringUtils.equals(payType, RepayConstants.PAYTYPE_EMIINADV)){
					payType = "EA_";
				}else if(StringUtils.equals(payType, RepayConstants.PAYTYPE_PAYABLE)){
					payType = "PA_";
				}else{
					payType = "PB_";
				}
				dataMap.put(payType + finFeeDetail.getFeeTypeCode() + "_P", finFeeDetail.getPaidAmount());
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
			FinRepayQueue finRepayQueue, Date dateValueDate,Date postDate, long linkedTranId, BigDecimal totalRpyAmt)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		logger.debug("Entering");

		//Schedule Updation depends on Finance Repay Queue Details
		scheduleDetail = updateScheduleDetailsData(scheduleDetail, finRepayQueue);

		// Late Profit Updation
		if (finRepayQueue.getLatePayPftPayNow().compareTo(BigDecimal.ZERO) > 0 || 
				finRepayQueue.getLatePayPftWaivedNow().compareTo(BigDecimal.ZERO) > 0) {
			getFinODDetailsDAO().updateLatePftTotals(finRepayQueue.getFinReference(), finRepayQueue.getRpyDate(),
					finRepayQueue.getLatePayPftPayNow(), finRepayQueue.getLatePayPftWaivedNow());
		}

		// Finance Repayments Details
		FinanceRepayments repayment = prepareRepayDetailData(finRepayQueue, dateValueDate,postDate ,linkedTranId, totalRpyAmt);
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
	private FinanceScheduleDetail updateScheduleDetailsData(FinanceScheduleDetail schedule, FinRepayQueue finRepayQueue) {
		logger.debug("Entering");

		schedule.setFinReference(finRepayQueue.getFinReference());
		schedule.setSchDate(finRepayQueue.getRpyDate());

		// Fee Details paid Amounts updation
		schedule.setSchdFeePaid(schedule.getSchdFeePaid().add(finRepayQueue.getSchdFeePayNow()).add(finRepayQueue.getSchdFeeWaivedNow()));
		schedule.setSchdInsPaid(schedule.getSchdInsPaid().add(finRepayQueue.getSchdInsPayNow()).add(finRepayQueue.getSchdInsWaivedNow()));
		schedule.setSuplRentPaid(schedule.getSuplRentPaid().add(finRepayQueue.getSchdSuplRentPayNow()).add(finRepayQueue.getSchdSuplRentWaivedNow()));
		schedule.setIncrCostPaid(schedule.getIncrCostPaid().add(finRepayQueue.getSchdIncrCostPayNow()).add(finRepayQueue.getSchdIncrCostWaivedNow()));

		schedule.setSchdPftPaid(schedule.getSchdPftPaid().add(finRepayQueue.getSchdPftPayNow()).add(finRepayQueue.getSchdPftWaivedNow()));
		schedule.setTDSPaid(schedule.getTDSPaid().add(finRepayQueue.getSchdTdsPayNow()));
		schedule.setSchdPriPaid(schedule.getSchdPriPaid().add(finRepayQueue.getSchdPriPayNow()).add(finRepayQueue.getSchdPriWaivedNow()));

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
	public FinanceRepayments prepareRepayDetailData(FinRepayQueue queue, Date valueDate,Date postdate, long linkedTranId,
			BigDecimal totalRpyAmt) {
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
		repayment.setFinTotSchdPaid(queue.getSchdPftPayNow().add(queue.getSchdPriPayNow()).add(queue.getSchdPftWaivedNow()).add(queue.getSchdPriWaivedNow()));
		repayment.setFinFee(BigDecimal.ZERO);
		repayment.setFinWaiver(queue.getWaivedAmount());
		repayment.setFinRefund(queue.getRefundAmount());

		//Fee Details
		repayment.setSchdFeePaid(queue.getSchdFeePayNow().add(queue.getSchdFeeWaivedNow()));
		repayment.setSchdInsPaid(queue.getSchdInsPayNow().add(queue.getSchdInsWaivedNow()));
		repayment.setSchdSuplRentPaid(queue.getSchdSuplRentPayNow().add(queue.getSchdSuplRentWaivedNow()));
		repayment.setSchdIncrCostPaid(queue.getSchdIncrCostPayNow().add(queue.getSchdIncrCostWaivedNow()));

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
	public List<Object> postingsScreenRepayProcess(FinanceMain financeMain,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail financeProfitDetail,
			List<FinRepayQueue> finRepayQueueList, Map<String, BigDecimal> totalsMap, String eventCode,
			Map<String, FeeRule> feeRuleDetailMap, String finDivision) throws InterfaceException,
			IllegalAccessException, InvocationTargetException {

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
			AEAmountCodes aeAmountCodes) throws InterfaceException, IllegalAccessException,
			InvocationTargetException {

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
			AEEvent aeEvent = doOverduePostings(null, finRepayQueueList, dateValueDate,dateValueDate, financeMain, null);
			if (aeEvent != null) {
				return null;
			}
		}

		// Schedule Principal and Profit payments
		BigDecimal totRpyAmt = totalsMap.get("totRpyTot");
		if (totRpyAmt.compareTo(BigDecimal.ZERO) > 0) {
			actReturnList = doSchedulePostings(null, valueDate,valueDate, financeMain, scheduleDetails,null,
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
				AEEvent aeEvent = doOverduePostings(null, finRepayQueueList, dateValueDate,dateValueDate,
						financeMain, null);
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
				FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(
						financeMain.getFinReference(), "");
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
				HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
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
			boolean isPartialRepay, AEAmountCodes aeAmountCodes) throws InterfaceException, IllegalAccessException,
			InvocationTargetException {
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

			if (rpyTotal.compareTo(BigDecimal.ZERO) > 0) {/*
														 * 
														 * FinanceScheduleDetail scheduleDetail = null; if
														 * (scheduleMap.containsKey
														 * (DateUtility.formatDate(repayQueue.getRpyDate(),
														 * PennantConstants.DBDateFormat))) { scheduleDetail =
														 * scheduleMap
														 * .get(DateUtility.formatDate(repayQueue.getRpyDate(),
														 * PennantConstants.DBDateFormat)); }
														 * 
														 * List<Object> resultList =
														 * paymentProcessExecution(scheduleDetail, repayQueue,
														 * dateValueDate, linkedTranId, rpyTotal);
														 * 
														 * if (!(Boolean) resultList.get(0)) {
														 * actReturnList.add(resultList.get(0));
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
				financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
			}
		} else if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS)) {
			if (!isPenaltyAvail
					&& totalFinAmt.subtract(financeMain.getFinRepaymentAmount()).compareTo(BigDecimal.ZERO) <= 0) {
				financeMain.setFinIsActive(false);
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

}
