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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class RepaymentPostingsUtil implements Serializable {

    private static final long serialVersionUID = 4165353615228874397L;
	private static Logger logger = Logger.getLogger(RepaymentPostingsUtil.class);

	private static FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private static FinanceMainDAO financeMainDAO;
	private static FinRepayQueueDAO finRepayQueueDAO;
	private static FinanceRepaymentsDAO financeRepaymentsDAO;
	private static DefermentDetailDAO defermentDetailDAO;
	private static CustomerStatusCodeDAO customerStatusCodeDAO;
	private static FinStatusDetailDAO finStatusDetailDAO;
	private static OverDueRecoveryPostingsUtil recoveryPostingsUtil;
	private static SuspensePostingUtil suspensePostingUtil;
	private static PostingsPreparationUtil postingsPreparationUtil;
	private static FinanceProfitDetailFiller financeProfitDetailFiller;
	private static FinanceProfitDetailDAO profitDetailsDAO;
	private static FinanceSuspHeadDAO financeSuspHeadDAO;
	private static CustomerDAO customerDAO;

	private List<Object> actreturnList = null;
	private FinanceScheduleDetail financeScheduleDetail = null;
	
	public RepaymentPostingsUtil() {
	    super();
    }
	
	/**
	 * Method for Posting Repayments and Update Repayments related Tables.
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public List<Object> postingsEODRepayProcess(FinanceMain financeMain,
	        List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail financeProfitDetail,
	        Date dateValueDate, FinRepayQueue finRepayQueue, BigDecimal repayAmountBal, boolean isRIAFinance, long linkedTranId)
	        		throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		
		return new RepaymentPostingsUtil(financeMain,scheduleDetails, financeProfitDetail,
		        dateValueDate, finRepayQueue, repayAmountBal, isRIAFinance, linkedTranId).getActreturnList();
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
	 * @throws AccountNotFoundException
	 */
	public List<Object> postingsScreenRepayProcess(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList, 
			Map<String,BigDecimal> totalsMap, boolean isRIAFinance, String eventCode, Map<String, FeeRule> feeRuleDetailMap, String finDivision)
					throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		
		return new RepaymentPostingsUtil(financeMain, scheduleDetails, financeProfitDetail, finRepayQueueList, 
				totalsMap, isRIAFinance, eventCode, feeRuleDetailMap, finDivision).getActreturnList();
	}
	
	/**
	 * Method for Process Updations After Repayments Process will Success
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param finRepayQueueList
	 * @param linkedTranId
	 * @param isPartialRepay
	 * @param isRIAFinance
	 * @return 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public List<Object> UpdateScreenPaymentsProcess(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList, 
			long linkedTranId , boolean isPartialRepay, boolean isRIAFinance, AEAmountCodes aeAmountCodes) 
					throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		
		return new RepaymentPostingsUtil(financeMain, scheduleDetails,financeProfitDetail,finRepayQueueList, 
				linkedTranId , isPartialRepay, isRIAFinance, aeAmountCodes).getActreturnList();
	}
	
	/**
	 * Method for Processing Updating Schedule Details
	 * @param finRepayQueue
	 * @return
	 */
	public FinanceScheduleDetail updateSchdlDetail(FinRepayQueue finRepayQueue){
		return new RepaymentPostingsUtil(finRepayQueue).getFinanceScheduleDetail();
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++++ Constructors ++++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	private RepaymentPostingsUtil(FinanceMain financeMain,
	        List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail financeProfitDetail,
	        Date dateValueDate, FinRepayQueue finRepayQueue, BigDecimal repayAmountBal, boolean isRIAFinance, long linkedTranId)
	        		throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		
		setActreturnList(endOfDayRepayProcess(financeMain, scheduleDetails, financeProfitDetail, dateValueDate, finRepayQueue,
				repayAmountBal, isRIAFinance, linkedTranId));
	}
	
	private RepaymentPostingsUtil(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList, 
			Map<String,BigDecimal> totalsMap, boolean isRIAFinance, String eventCode, Map<String, FeeRule> feeRuleDetailMap, String finDivision)
					throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		
		setActreturnList(screenRepayProcess(financeMain, scheduleDetails, financeProfitDetail, finRepayQueueList,
				totalsMap, isRIAFinance, eventCode, feeRuleDetailMap, finDivision));
	}
	
	private RepaymentPostingsUtil(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList, 
			long linkedTranId , boolean isPartialRepay, boolean isRIAFinance, AEAmountCodes aeAmountCodes) 
					throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		
		setActreturnList(screenPaymentsUpdation(financeMain, scheduleDetails, financeProfitDetail, finRepayQueueList, linkedTranId, 
				isPartialRepay, isRIAFinance, aeAmountCodes));
	}
	
	private RepaymentPostingsUtil(FinRepayQueue finRepayQueue) {
		setFinanceScheduleDetail(scheduleUpdation(finRepayQueue));
	}

	/**
	 * Method for Posting Repayments and Update Repayments related Tables.
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	private List<Object> endOfDayRepayProcess(FinanceMain financeMain,
	        List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail financeProfitDetail,
	        Date dateValueDate, FinRepayQueue finRepayQueue, BigDecimal repayAmountBal, boolean isRIAFinance, long linkedTranId)
	        		throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {

		logger.debug("Entering");
		List<Object> actreturnList = new ArrayList<Object>();
		Date valueDate = finRepayQueue.getRpyDate();

		//Repayments Amount calculation
		BigDecimal totRpyPri = BigDecimal.ZERO;
		BigDecimal totRpyPft = BigDecimal.ZERO;
		if (repayAmountBal.compareTo(finRepayQueue.getSchdPftBal()) >= 0) {
			totRpyPft = finRepayQueue.getSchdPftBal();
			totRpyPri = repayAmountBal.subtract(totRpyPft);
			finRepayQueue.setSchdPftPayNow(totRpyPft);
			finRepayQueue.setSchdPriPayNow(totRpyPri);
		} else {
			totRpyPft = repayAmountBal;
			finRepayQueue.setSchdPftPayNow(totRpyPft);
			finRepayQueue.setSchdPriPayNow(BigDecimal.ZERO);
		}

		boolean isPartialRepay = false;
		//Partial Repay Check
		if(totRpyPft.compareTo(BigDecimal.ZERO) > 0){
			isPartialRepay = true;
		}

		Map<String,BigDecimal> totalsMap = new HashMap<String, BigDecimal>();
		totalsMap.put("totRpyTot", repayAmountBal);
		totalsMap.put("totRpyPri", totRpyPri);
		totalsMap.put("totRpyPft", totRpyPft);
		totalsMap.put("totRefund", finRepayQueue.getRefundAmount());
		totalsMap.put("INSREFUND", BigDecimal.ZERO);

		//Method for Postings Process
		List<Object> resultList = postingEntryProcess(valueDate, dateValueDate,  finRepayQueue.getRpyDate(), true,
				financeMain, scheduleDetails, financeProfitDetail, totalsMap, isRIAFinance, linkedTranId,"REPAY", null);
		
		AEAmountCodes amountCodes = (AEAmountCodes) resultList.get(5);

		boolean isPostingSuccess = (Boolean) resultList.get(0);
		linkedTranId = (Long) resultList.get(1);

		if (!isPostingSuccess) {
			actreturnList.add(isPostingSuccess);
			actreturnList.add(resultList.get(3));
			actreturnList.add(true);
			
			//Overdue Details preparation
			getRecoveryPostingsUtil().overDueDetailPreparation(finRepayQueue, financeMain.getProfitDaysBasis(), dateValueDate, true);
		
			logger.debug("Leaving");
			return actreturnList;
		}

		//Database Updations for Finance Related Tables using Repay Details
		List<Object> returnList = paymentProcessExecution(financeMain, null, finRepayQueue, dateValueDate, 
				linkedTranId, true, isPartialRepay, financeProfitDetail, isRIAFinance, amountCodes.getRpTot());

		isPostingSuccess = (Boolean) returnList.get(0);
		boolean isDueSuspNow = (Boolean) returnList.get(1);

		if (!isPostingSuccess) {
			actreturnList.add(isPostingSuccess);
			actreturnList.add(returnList.get(2));
			actreturnList.add(false);
			logger.debug("Leaving");
			return actreturnList;
		}

		//Check Current Finance Max Status For updation
		String curFinStatus = getCustomerStatusCodeDAO().getFinanceStatus(financeMain.getFinReference(), true);
		String finStsReason = financeMain.getFinStsReason();
		boolean isStsChanged = false;
		boolean isStsRsnChanged = false;
		if(isDueSuspNow && !PennantConstants.FINSTSRSN_SYSTEM.equals(finStsReason)){
			finStsReason = PennantConstants.FINSTSRSN_SYSTEM;
			isStsRsnChanged = true;
		}

		if(!financeMain.getFinStatus().equals(curFinStatus)){
			isStsChanged = true;
		}

		// Finance Main Details Update
		financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().add(amountCodes.getRpPri()));

		if (amountCodes.getRpPri().compareTo(BigDecimal.ZERO) > 0 || isStsChanged || isStsRsnChanged) {
			getFinanceMainDAO().updateRepaymentAmount(financeMain.getFinReference(), financeMain.getFinAmount().add(
					financeMain.getFeeChargeAmt() == null? BigDecimal.ZERO : financeMain.getFeeChargeAmt())
					.subtract(financeMain.getDownPayment() == null ? BigDecimal.ZERO : financeMain.getDownPayment()), 
					financeMain.getFinRepaymentAmount(), curFinStatus, finStsReason ,false);
		}
		
		//Updating Latest Repayment Details
		financeProfitDetail.setLatestRpyDate(dateValueDate);
		financeProfitDetail.setLatestRpyPri(amountCodes.getRpPri());
		financeProfitDetail.setLatestRpyPft(amountCodes.getRpPft());
		getProfitDetailsDAO().updateLatestRpyDetails(financeProfitDetail);

		//Finance Status Details insertion, if status modified then change to High Risk Level
		if(isStsChanged){
			FinStatusDetail statusDetail = new FinStatusDetail();
			statusDetail.setFinReference(financeMain.getFinReference());
			statusDetail.setValueDate(dateValueDate);
			statusDetail.setCustId(financeMain.getCustID());
			statusDetail.setFinStatus(curFinStatus);			
			statusDetail.setFinStatusReason(finStsReason);	

			getFinStatusDetailDAO().saveOrUpdateFinStatus(statusDetail);
		}

		actreturnList.add(isPostingSuccess);
		actreturnList.add(null);

		logger.debug("Leaving");
		return actreturnList;
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
	 * @throws AccountNotFoundException
	 */
	private List<Object> screenRepayProcess(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList, 
			Map<String,BigDecimal> totalsMap, boolean isRIAFinance, String eventCode, Map<String, FeeRule> feeRuleDetailMap, String finDivison)
					throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {

		logger.debug("Entering");
		List<Object> actReturnList = new ArrayList<Object>();

		Date dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
		Date valueDate = dateValueDate;

		//Overdue Recovery Postings
		long linkedTranId = Long.MIN_VALUE;
		for (FinRepayQueue repayQueue : finRepayQueueList) {
			if(repayQueue.getRpyDate().compareTo(dateValueDate) < 0 &&
					(repayQueue.getPenaltyAmount().compareTo(BigDecimal.ZERO) > 0 || 
							repayQueue.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) ){
				List<Object> returnList = getRecoveryPostingsUtil().oDRPostingProcess(financeMain, dateValueDate, repayQueue.getRpyDate(),
						repayQueue.getFinRpyFor(), dateValueDate, repayQueue.getPenaltyAmount(),  BigDecimal.ZERO, 
						repayQueue.getWaivedAmount(), repayQueue.getChargeType(),isRIAFinance, linkedTranId, finDivison);

				if(!(Boolean) returnList.get(0)){
					actReturnList.add(returnList.get(0));
					actReturnList.add(returnList.get(2));

					logger.debug("Leaving");
					returnList = null;
					return actReturnList;
				}

				linkedTranId = Long.MIN_VALUE;
				//linkedTranId = (Long) returnList.get(1);
			}
		}

		boolean isPartialRepay = false;
		//Partial Repay Check
		if(totalsMap.get("totRpyPft").compareTo(BigDecimal.ZERO) > 0){
			isPartialRepay = true;
		}

		//Remove Below line for Single Transaction Posting Entry
		linkedTranId = Long.MIN_VALUE;

		//Method for Postings Process
		List<Object> resultList = postingEntryProcess(valueDate, dateValueDate, valueDate, false,financeMain, scheduleDetails, financeProfitDetail, 
				totalsMap, isRIAFinance, linkedTranId, eventCode, feeRuleDetailMap);

		boolean isPostingSuccess = (Boolean) resultList.get(0);
		linkedTranId = (Long) resultList.get(1);

		if(!isPostingSuccess){
			actReturnList.add(resultList.get(0));
			actReturnList.add(resultList.get(3));

			logger.debug("Leaving");
			resultList = null;
			return actReturnList;
		}

		actReturnList.add(isPostingSuccess);
		actReturnList.add(linkedTranId);
		actReturnList.add(isPartialRepay);
		actReturnList.add(resultList.get(5)); // Amount Codes
		actReturnList.add(resultList.get(4)); // Finance Account if Exists
		
		logger.debug("Leaving");
		return actReturnList;
	}
	
	/**
	 * Method for Process Updations After Repayments Process will Success
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param finRepayQueueList
	 * @param linkedTranId
	 * @param isPartialRepay
	 * @param isRIAFinance
	 * @return 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	private List<Object> screenPaymentsUpdation(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList, 
			long linkedTranId , boolean isPartialRepay, boolean isRIAFinance, AEAmountCodes aeAmountCodes) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		List<Object> actReturnList = new ArrayList<Object>();
		Date dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
		
		Map<String,FinanceScheduleDetail> scheduleMap = new HashMap<String, FinanceScheduleDetail>();
		for (FinanceScheduleDetail detail : scheduleDetails) {
            scheduleMap.put(detail.getSchDate().toString(), detail);
        }
		
		//Database Updations for Finance RepayQueue Details List
		for (FinRepayQueue repayQueue : finRepayQueueList) {
			
			FinanceScheduleDetail scheduleDetail = null;
			if(scheduleMap.containsKey(DateUtility.formatDate(repayQueue.getRpyDate(),PennantConstants.DBDateFormat))){
				scheduleDetail = scheduleMap.get(DateUtility.formatDate(repayQueue.getRpyDate(),PennantConstants.DBDateFormat));
			}
			
			List<Object> resultList = paymentProcessExecution(financeMain, scheduleDetail, repayQueue, dateValueDate, 
					linkedTranId, false, isPartialRepay, financeProfitDetail, isRIAFinance, aeAmountCodes.getRpTot());

			if (!(Boolean) resultList.get(0)) {
				actReturnList.add(resultList.get(0));
				actReturnList.add(resultList.get(2));

				logger.debug("Leaving");
				return actReturnList;
			}
			
			scheduleMap.remove(scheduleDetail.getSchDate().toString());
			scheduleMap.put(scheduleDetail.getSchDate().toString(), (FinanceScheduleDetail)resultList.get(3));
		}

		String curFinStatus = getCustomerStatusCodeDAO().getFinanceStatus(financeMain.getFinReference(),true);
		boolean isStsChanged = false;
		if(!financeMain.getFinStatus().equals(curFinStatus)){
			isStsChanged = true;
		}

		//Finance Status Details insertion, if status modified then change to High Risk Level
		if(isStsChanged){
			FinStatusDetail statusDetail = new FinStatusDetail();
			statusDetail.setFinReference(financeMain.getFinReference());
			statusDetail.setValueDate(dateValueDate);
			statusDetail.setCustId(financeMain.getCustID());
			statusDetail.setFinStatus(curFinStatus);			

			getFinStatusDetailDAO().saveOrUpdateFinStatus(statusDetail);
		}
		
		//Reset Finance Schedule Details
		scheduleDetails = new ArrayList<FinanceScheduleDetail>(scheduleMap.values());
		scheduleDetails = sortSchdDetails(scheduleDetails);
		
		// Finance Main Details Update
		BigDecimal totalRpyPri = aeAmountCodes.getRpPri();
		BigDecimal totalRpyPft = aeAmountCodes.getRpPft();
		financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().add(totalRpyPri));
		financeMain.setFinStatus(curFinStatus);
		financeMain.setFinStsReason(PennantConstants.FINSTSRSN_MANUAL);
		BigDecimal totalFinAmt = financeMain.getFinAmount().add(financeMain.getFeeChargeAmt() == null ? BigDecimal.ZERO : financeMain.getFeeChargeAmt()).subtract(financeMain.getDownPayment());
		if(totalFinAmt.subtract(financeMain.getFinRepaymentAmount()).compareTo(BigDecimal.ZERO) <= 0){
			financeMain.setFinIsActive(false);
			financeMain.setClosingStatus(PennantConstants.CLOSE_STATUS_MATURED);
		}

		//Finance Profit Details Updation
		aeAmountCodes = AEAmounts.procAEAmounts(financeMain, scheduleDetails, financeProfitDetail,dateValueDate);
		financeProfitDetail = getFinanceProfitDetailFiller().prepareFinPftDetails(aeAmountCodes, financeProfitDetail, dateValueDate);
		financeProfitDetail.setFinStatus(financeMain.getFinStatus());
		financeProfitDetail.setFinStsReason(financeMain.getFinStsReason());
		financeProfitDetail.setFinIsActive(financeMain.isFinIsActive());
		financeProfitDetail.setClosingStatus(financeMain.getClosingStatus());
		financeProfitDetail.setLatestRpyDate(dateValueDate);
		financeProfitDetail.setLatestRpyPri(totalRpyPri);
		financeProfitDetail.setLatestRpyPft(totalRpyPft);
		
		String curFinWorstStatus = getCustomerStatusCodeDAO().getFinanceStatus(financeMain.getFinReference(),false);
		financeProfitDetail.setFinWorstStatus(curFinWorstStatus);
		getProfitDetailsDAO().update(financeProfitDetail, true);
		
		//Customer Status & Status Change Date(Suspense From Date) Updation
		String custSts = getCustomerDAO().getCustWorstSts(financeMain.getCustID());
		List<Long> custIdList = new ArrayList<Long>(1);
		custIdList.add(financeMain.getCustID());
		List<FinStatusDetail> suspDateSts = getFinanceSuspHeadDAO().getCustSuspDate(custIdList);
		
		Date suspFromdate = null;
		if(suspDateSts != null && !suspDateSts.isEmpty()){
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

		logger.debug("Leaving");
		return actReturnList;
	}
	
	public List<FinanceScheduleDetail> sortSchdDetails(
	        List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					if (detail1.getSchDate().after(detail2.getSchDate())) {
						return 1;
					}
					return 0;
				}
			});
		}

		return financeScheduleDetail;
	}
	
	/**
	 * Method for Posting Process execution in Single Entry Event for Total Repayment Amount
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
	 * @param isRIAFinance
	 * @return
	 * @throws AccountNotFoundException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private List<Object> postingEntryProcess(Date valueDate, Date dateValueDate, Date dateSchdDate,
			boolean isEODProcess, FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail financeProfitDetail,
			Map<String, BigDecimal> repayDetailMap, boolean isRIAFinance,long linkedTranId, String eventCode, Map<String, FeeRule> feeRuleDetailMap) 
			throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		// DataSet Creation
		DataSet dataSet = AEAmounts.createDataSet(financeMain, eventCode, dateValueDate,dateSchdDate);
		dataSet.setNewRecord(false);

		// AmountCodes Preparation
		// EOD Repayments should pass the value date as schedule for which
		// Repayments are processing
		AEAmountCodes amountCodes = AEAmounts.procAEAmounts(financeMain, scheduleDetails, financeProfitDetail,valueDate);

		//Set Repay Amount Codes
		amountCodes.setRpTot(repayDetailMap.get("totRpyTot"));
		amountCodes.setRpPft(repayDetailMap.get("totRpyPft"));
		amountCodes.setRpPri(repayDetailMap.get("totRpyPri"));
		amountCodes.setRefund(repayDetailMap.get("totRefund"));
		amountCodes.setInsRefund(repayDetailMap.get("INSREFUND"));

		Date dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());
		List<Object> resultList = getPostingsPreparationUtil().processPostingDetailsWithFee(dataSet,
		        amountCodes, isEODProcess,isRIAFinance, "Y", dateAppDate, true, linkedTranId, feeRuleDetailMap);
		
		resultList.add(amountCodes);
		
		logger.debug("Leaving");
		return resultList;
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
	 * @param isRIAFinance 
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	private List<Object> paymentProcessExecution(FinanceMain financeMain, FinanceScheduleDetail scheduleDetail, FinRepayQueue finRepayQueue,
			Date dateValueDate, long linkedTranId, boolean isEODProcess, boolean isPartialRepay,
			FinanceProfitDetail financeProfitDetail, boolean isRIAFinance, BigDecimal totalRpyAmt)
					throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{

		logger.debug("Entering");

		boolean isDueSuspNow = false;
		boolean suspPostingsSuccess = true;
		String errorCode = null;

		//Schedule Updation depends on Finance Repay Queue Details
		if(scheduleDetail == null){
			scheduleDetail = updateSchdlDetail(finRepayQueue);
		}else{
			scheduleDetail = updateScheduleDetailsData(scheduleDetail, finRepayQueue);
		}

		// Finance Deferment Details Update
		DefermentDetail defermentDetail = null;
		if (finRepayQueue.getFinRpyFor().equals(PennantConstants.DEFERED)) {
			defermentDetail = getDefermentDetailDAO().getDefermentDetailForBatch(
					finRepayQueue.getFinReference(), finRepayQueue.getRpyDate());
			defermentDetail = updateDefermentDetailsData(defermentDetail, finRepayQueue);
			getDefermentDetailDAO().updateBatch(defermentDetail);
		}

		// Finance Repayments Details
		FinanceRepayments repayment = prepareRepayDetailsData(finRepayQueue, dateValueDate, linkedTranId, isEODProcess, totalRpyAmt);
		getFinanceRepaymentsDAO().save(repayment, "");

		// Finance Repay Queue Data Updation
		finRepayQueue = prepareQueueData(finRepayQueue);

		//Check for Schedule is Completely paid or not
		boolean isCompletlyPaid = false;
		if (scheduleDetail.isSchPftPaid() && scheduleDetail.isSchPriPaid()
				&& (scheduleDetail.getDefProfitSchd().compareTo(scheduleDetail.getDefSchdPftPaid()) == 0)
				&& (scheduleDetail.getDefPrincipalSchd().compareTo(scheduleDetail.getDefSchdPriPaid()) == 0)) {
			isCompletlyPaid = true;
			isPartialRepay = false;
		}

		boolean isLatePay = false;
		if (isEODProcess) {
			if ((finRepayQueue.getRpyDate().compareTo(dateValueDate) < 0) ||
					(finRepayQueue.getRpyDate().compareTo(dateValueDate) == 0 && !isCompletlyPaid)) {
				isLatePay = true;
			}
		} else {
			if ((finRepayQueue.getRpyDate().compareTo(dateValueDate) < 0)) {
				isLatePay = true;
			}
		}

		if (isLatePay) {

			//Overdue Details preparation
			getRecoveryPostingsUtil().overDueDetailPreparation(finRepayQueue, financeMain.getProfitDaysBasis(), dateValueDate, isEODProcess);
		
			//SUSPENSE
			if (isEODProcess) {

				//Suspense Details Preparation
				List<Object> returnList = getSuspensePostingUtil().suspensePreparation(financeMain,  finRepayQueue, dateValueDate, isRIAFinance, false);
				suspPostingsSuccess = (Boolean) returnList.get(0);
				isDueSuspNow = (Boolean) returnList.get(1);
				errorCode = (String) returnList.get(2);
			}

			//SUSPENSE RELEASE
			if (!isDueSuspNow && (isCompletlyPaid || isPartialRepay)) {
				getSuspensePostingUtil().suspReleasePreparation(financeMain,
						finRepayQueue.getSchdPftPayNow(), finRepayQueue, dateValueDate, isEODProcess, isRIAFinance);
			}
		}

		List<Object> returnList = new ArrayList<Object>(4);
		returnList.add(suspPostingsSuccess);
		returnList.add(isDueSuspNow);
		returnList.add(errorCode);
		returnList.add(scheduleDetail);

		logger.debug("Leaving");
		return returnList;
	}
	
	/**
	 * Method for updating Schedule Details
	 * @param finRepayQueue
	 * @return
	 */
	private FinanceScheduleDetail scheduleUpdation(FinRepayQueue finRepayQueue){
		logger.debug("Entering");
		
		// Finance Schedule Details Update
		FinanceScheduleDetail scheduleDetail = getFinanceScheduleDetailDAO().getFinanceScheduleDetailById(
				finRepayQueue.getFinReference(), finRepayQueue.getRpyDate(), "", false);
		
		scheduleDetail = updateScheduleDetailsData(scheduleDetail, finRepayQueue);
		getFinanceScheduleDetailDAO().updateForRpy(scheduleDetail, finRepayQueue.getFinRpyFor());
		
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

		// Finance Repayment for Defferment & Schedule 
		if (finRepayQueue.getFinRpyFor().equals(PennantConstants.DEFERED)) {

			schedule.setDefSchdPftPaid(schedule.getDefSchdPftPaid().add(finRepayQueue.getSchdPftPayNow()));
			schedule.setDefSchdPriPaid(schedule.getDefSchdPriPaid().add(finRepayQueue.getSchdPriPayNow()));

			// Finance Deffered Schedule Profit Balance Check
			if ((schedule.getDefProfitSchd().subtract(schedule.getDefSchdPftPaid())).compareTo(BigDecimal.ZERO) == 0) {
				schedule.setDefSchPftPaid(true);

				// Finance Deffered Schedule Principal Balance Check
				if ((schedule.getDefPrincipalSchd().subtract(schedule.getDefSchdPriPaid())).compareTo(BigDecimal.ZERO) == 0) {
					schedule.setDefSchPriPaid(true);
				}else{
					schedule.setDefSchPriPaid(false);
				}
			}else{
				schedule.setDefSchPftPaid(false);
			}

		} else if (finRepayQueue.getFinRpyFor().equals(PennantConstants.SCHEDULE)) {
			
			schedule.setDefSchPftPaid(true);
			schedule.setDefSchPriPaid(true);

			schedule.setSchdPftPaid(schedule.getSchdPftPaid().add(finRepayQueue.getSchdPftPayNow()));
			schedule.setSchdPriPaid(schedule.getSchdPriPaid().add(finRepayQueue.getSchdPriPayNow()));

			// Finance Schedule Profit Balance Check
			if ((schedule.getProfitSchd().subtract(schedule.getSchdPftPaid())).compareTo(BigDecimal.ZERO) == 0) {
				schedule.setSchPftPaid(true);

				// Finance Schedule Principal Balance Check
				if ((schedule.getPrincipalSchd().subtract(schedule.getSchdPriPaid())).compareTo(BigDecimal.ZERO) == 0) {
					schedule.setSchPriPaid(true);
				}else{
					schedule.setSchPriPaid(false);
				}
			}else{
				schedule.setSchPftPaid(false);
			}
		}
		logger.debug("Leaving");
		return schedule;
	}

	/**
	 * Method for Upadte Data for Finance Deferment Details Object
	 * 
	 * @param scheduleDetail
	 * @param finRepayQueue
	 * @return
	 */
	private DefermentDetail updateDefermentDetailsData(DefermentDetail detail,FinRepayQueue finRepayQueue) {
		logger.debug("Entering");
		detail.setDefPaidPftTillDate(detail.getDefPaidPftTillDate().add(finRepayQueue.getSchdPftPayNow()));
		detail.setDefPaidPriTillDate(detail.getDefPaidPriTillDate().add(finRepayQueue.getSchdPriPayNow()));
		detail.setDefPftBalance(detail.getDefPftBalance().subtract(finRepayQueue.getSchdPftPayNow()));
		detail.setDefPriBalance(detail.getDefPriBalance().subtract(finRepayQueue.getSchdPriPayNow()));
		logger.debug("Leaving");
		return detail;
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
	private FinanceRepayments prepareRepayDetailsData(FinRepayQueue queue, Date valueDate,
	        long linkedTranId, boolean isEODProcess, BigDecimal totalRpyAmt) {

		logger.debug("Entering");
		FinanceRepayments repayment = new FinanceRepayments();
		Date curAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());

		repayment.setFinReference(queue.getFinReference());
		repayment.setFinSchdDate(queue.getRpyDate());
		repayment.setFinRpyFor(queue.getFinRpyFor());
		repayment.setLinkedTranId(linkedTranId);

		repayment.setFinRpyAmount(totalRpyAmt);
		repayment.setFinPostDate(curAppDate);
		repayment.setFinValueDate(valueDate);
		repayment.setFinBranch(queue.getBranch());
		repayment.setFinType(queue.getFinType());
		repayment.setFinCustID(queue.getCustomerID());
		repayment.setFinSchdPftPaid(queue.getSchdPftPayNow());
		repayment.setFinSchdPriPaid(queue.getSchdPriPayNow());
		repayment.setFinTotSchdPaid(queue.getSchdPftPayNow().add(queue.getSchdPriPayNow()));
		repayment.setFinFee(BigDecimal.ZERO);
		repayment.setFinWaiver(queue.getWaivedAmount());
		repayment.setFinRefund(queue.getRefundAmount());

		logger.debug("Leaving");
		return repayment;
	}

	/**
	 * Method for Updating the Finance RepayQueue Data
	 * 
	 * @param repayQueue
	 * @param repayAmtBal
	 * @return
	 */
	private FinRepayQueue prepareQueueData(FinRepayQueue repayQueue) {
		logger.debug("Entering");
		repayQueue.setSchdPftPaid(repayQueue.getSchdPftPaid().add(repayQueue.getSchdPftPayNow()));
		repayQueue.setSchdPriPaid(repayQueue.getSchdPriPaid().add(repayQueue.getSchdPriPayNow()));
		repayQueue.setSchdPftBal(repayQueue.getSchdPftBal().subtract(repayQueue.getSchdPftPayNow()));
		repayQueue.setSchdPriBal(repayQueue.getSchdPriBal().subtract(repayQueue.getSchdPriPayNow()));

		// Modified Conditions for Balances Paid or not
		if (repayQueue.getSchdPftBal().compareTo(BigDecimal.ZERO) == 0) {

			repayQueue.setSchdIsPftPaid(true);
			if (repayQueue.getSchdPriBal().compareTo(BigDecimal.ZERO) == 0) {
				repayQueue.setSchdIsPriPaid(true);
			}
		}

		logger.debug("Leaving");
		return repayQueue;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		RepaymentPostingsUtil.finRepayQueueDAO = finRepayQueueDAO;
	}
	public static FinRepayQueueDAO getFinRepayQueueDAO() {
		return finRepayQueueDAO;
	}

	public static FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		RepaymentPostingsUtil.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		RepaymentPostingsUtil.financeMainDAO = financeMainDAO;
	}
	public static FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		RepaymentPostingsUtil.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
	public static FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setDefermentDetailDAO(DefermentDetailDAO defermentDetailDAO) {
		RepaymentPostingsUtil.defermentDetailDAO = defermentDetailDAO;
	}
	public static DefermentDetailDAO getDefermentDetailDAO() {
		return defermentDetailDAO;
	}
	
	public static CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
    	return customerStatusCodeDAO;
    }
	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		RepaymentPostingsUtil.customerStatusCodeDAO = customerStatusCodeDAO;
    }
	
	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		RepaymentPostingsUtil.finStatusDetailDAO = finStatusDetailDAO;
    }
	public static FinStatusDetailDAO getFinStatusDetailDAO() {
	    return finStatusDetailDAO;
    }

	public static OverDueRecoveryPostingsUtil getRecoveryPostingsUtil() {
    	return recoveryPostingsUtil;
    }
	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
		RepaymentPostingsUtil.recoveryPostingsUtil = recoveryPostingsUtil;
    }

	public void setSuspensePostingUtil(SuspensePostingUtil suspensePostingUtil) {
		RepaymentPostingsUtil.suspensePostingUtil = suspensePostingUtil;
	}
	public static SuspensePostingUtil getSuspensePostingUtil() {
		return suspensePostingUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		RepaymentPostingsUtil.postingsPreparationUtil = postingsPreparationUtil;
	}
	public static PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public static FinanceProfitDetailFiller getFinanceProfitDetailFiller() {
    	return financeProfitDetailFiller;
    }
	public void setFinanceProfitDetailFiller(FinanceProfitDetailFiller financeProfitDetailFiller) {
		RepaymentPostingsUtil.financeProfitDetailFiller = financeProfitDetailFiller;
    }

	public static FinanceProfitDetailDAO getProfitDetailsDAO() {
    	return profitDetailsDAO;
    }
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		RepaymentPostingsUtil.profitDetailsDAO = profitDetailsDAO;
    }

	public static CustomerDAO getCustomerDAO() {
    	return customerDAO;
    }
	public void setCustomerDAO(CustomerDAO customerDAO) {
		RepaymentPostingsUtil.customerDAO = customerDAO;
    }

	public static FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
	    return financeSuspHeadDAO;
    }
	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		RepaymentPostingsUtil.financeSuspHeadDAO = financeSuspHeadDAO;
    }
	
	public List<Object> getActreturnList() {
    	return actreturnList;
    }
	public void setActreturnList(List<Object> actreturnList) {
    	this.actreturnList = actreturnList;
    }

	public FinanceScheduleDetail getFinanceScheduleDetail() {
	    return financeScheduleDetail;
    }
	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
	    this.financeScheduleDetail = financeScheduleDetail;
    }

}
