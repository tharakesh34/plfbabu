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
 * FileName    		:  SuspensePostingUtil.java													*                           
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSuspDetails;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class SuspensePostingUtil implements Serializable {

    private static final long serialVersionUID = -7469564513544156223L;
	private static Logger logger = Logger.getLogger(SuspensePostingUtil.class);

	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private CustomerStatusCodeDAO customerStatusCodeDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private long linkedTranId;

	public SuspensePostingUtil() {
	    super();
    }

	/**
	 * Method for preparation of Finance Suspend Data
	 * 
	 * @param financeMain
	 * @param details
	 * @param valueDate
	 * @param isEODProcess
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public List<Object> suspensePreparation(FinanceMain financeMain, FinRepayQueue repayQueue,
			Date valueDate, boolean isRIAFinance, boolean isPastDeferment)throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		List<Object> returnList = new ArrayList<Object>(3);
		boolean isPostingSuccess = true;

		boolean isDueSuspNow = false;
		int curOdDays = getFinODDetailsDAO().getFinCurSchdODDays(financeMain.getFinReference(), repayQueue.getRpyDate(), repayQueue.getFinRpyFor());
		
		// Check Profit will Suspend or not based upon Current Overdue Days
		boolean suspendProfit = getCustomerStatusCodeDAO().getFinanceSuspendStatus(curOdDays);
		if (!suspendProfit) {
			returnList.add(isPostingSuccess);
			returnList.add(isDueSuspNow);
			returnList.add(null);
			return returnList;
		} 

		
		FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(financeMain.getFinReference(),"");
		if (suspHead != null && suspHead.isFinIsInSusp()) {
			returnList.add(isPostingSuccess);
			returnList.add(isDueSuspNow);
			returnList.add(null);
			return returnList;
		} 
		
		Date suspFromDate = null;
		BigDecimal suspAmount = BigDecimal.ZERO;

		//Finance Related Details Fetching
		AEAmountCodes amountCodes = new AEAmountCodes();
		suspAmount = getFinanceScheduleDetailDAO().getSuspenseAmount(financeMain.getFinReference(), valueDate);
		suspFromDate = DateUtility.addDays( repayQueue.getRpyDate(), curOdDays);

		DataSet dataSet = AEAmounts.createDataSet(financeMain, "M_NONAMZ", valueDate, suspFromDate);
		amountCodes.setFinReference(dataSet.getFinReference());
		amountCodes.setSUSPNOW(suspAmount);
		dataSet.setNewRecord(false);
		
		boolean isEODProcess = false;
		String phase = StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_PHASE).toString());
		if (!phase.equals(PennantConstants.APP_PHASE_DAY)) {
			isEODProcess = true;
		}

		//Postings Preparation
		Date dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());
		List<Object> result = getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes,
				isEODProcess ,isRIAFinance,  "Y", dateAppDate,false, Long.MIN_VALUE);
		isPostingSuccess = (Boolean)result.get(0);
		linkedTranId = (Long) result.get(1);

		//Check Status for Postings
		if (!isPostingSuccess) {
			returnList.add(isPostingSuccess);
			returnList.add(isDueSuspNow);
			returnList.add(result.get(3));
			return returnList;
		}

		if (suspHead != null) {
			// Update Finance Suspend Head
			suspHead = prepareSuspHeadData(suspHead, repayQueue, suspFromDate, suspAmount, isPastDeferment);
			getFinanceSuspHeadDAO().update(suspHead, "");
		} else {
			// Insert Finance Suspend Head
			suspHead = prepareSuspHeadData(suspHead, repayQueue, suspFromDate, suspAmount, isPastDeferment);
			suspHead.setVersion(0);
			suspHead.setLastMntBy(9999);
			suspHead.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			suspHead.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			suspHead.setRoleCode("");
			suspHead.setNextRoleCode("");
			suspHead.setTaskId("");
			suspHead.setNextTaskId("");
			suspHead.setRecordType("");
			suspHead.setWorkflowId(0);
			getFinanceSuspHeadDAO().save(suspHead, "");
		}
		isDueSuspNow = true;

		// Insert Finance Suspend Details data
		FinanceSuspDetails suspDetails = prepareSuspDetail(suspHead, suspHead.getFinSuspAmt(), 
				suspHead.getFinSuspSeq(), valueDate,  repayQueue.getRpyDate(), "S", suspFromDate, linkedTranId);
		getFinanceSuspHeadDAO().saveSuspenseDetails(suspDetails, "");
		
		returnList.add(isPostingSuccess);
		returnList.add(isDueSuspNow);
		returnList.add(null);
		
		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * Method for update of Finance Suspend Data for Release
	 * 
	 * @param financeMain
	 * @param profitDetail
	 * @param details
	 * @param valueDate
	 * @param isEODProcess
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public void suspReleasePreparation(FinanceMain financeMain, BigDecimal releasePftAmount,
			FinRepayQueue finRepayQueue, Date valueDate, boolean isEODProcess, boolean isRIAFinance)
	throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		//Condition Checking For Payment Profit Amount Now
		if(finRepayQueue.getSchdPftPayNow().compareTo(BigDecimal.ZERO) == 0){
			return;
		}
		
		//Fetch the Finance Suspend head
		FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(finRepayQueue.getFinReference(),"");
		if (suspHead == null || !suspHead.isFinIsInSusp()) {
			return;
		}
		
		AEAmountCodes amountCodes = new AEAmountCodes();
		boolean isInSuspNow = true;
		BigDecimal suspAmtToMove = BigDecimal.ZERO;
		Date suspFromDate = null;

		//Pending OverDue Details for that particular Schedule date and overDue For
		int curOverDueDays = getFinODDetailsDAO().getPendingOverDuePayment(finRepayQueue.getFinReference());
		int suspenceGraceDays = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("SUSP_AFTER").toString());
		
		if (curOverDueDays > suspenceGraceDays) {

			suspFromDate = DateUtility.addDays(valueDate, -suspenceGraceDays);

			//Suspend Amount Calculation
			if (suspFromDate.compareTo(valueDate) > 0 && !suspHead.isManualSusp()) {
				suspAmtToMove = suspHead.getFinCurSuspAmt();
				isInSuspNow = false;
			} else {
				suspAmtToMove = releasePftAmount;
				isInSuspNow = true;
			}

		} else {
			suspFromDate = suspHead.getFinSuspDate();
			if(!suspHead.isManualSusp()){
				suspAmtToMove = suspHead.getFinCurSuspAmt();
				isInSuspNow = false;
			}
		}

		//Creating DataSet using Finance Details
		DataSet dataSet = AEAmounts.createDataSet(financeMain, "M_AMZ", valueDate, suspFromDate);
		amountCodes.setFinReference(dataSet.getFinReference());
		amountCodes.setSUSPRLS(suspAmtToMove);
		dataSet.setNewRecord(false);

		//Postings Preparation
		Date dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());
		linkedTranId = (Long) getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes,
				isEODProcess, isRIAFinance, "Y", dateAppDate, false, Long.MIN_VALUE).get(1);

		//Finance Suspend Head
		suspHead.setFinIsInSusp(isInSuspNow);
		suspHead.setFinCurSuspAmt(suspHead.getFinCurSuspAmt().subtract(suspAmtToMove));
		if (suspHead.getFinCurSuspAmt().compareTo(BigDecimal.ZERO) == 0 && !suspHead.isManualSusp()) {
			suspHead.setFinIsInSusp(false);
			suspHead.setFinSuspTrfDate(null);
		}

		getFinanceSuspHeadDAO().update(suspHead, "");

		//Finance Suspend Details Record Insert
		FinanceSuspDetails suspDetails = prepareSuspDetail(suspHead, suspAmtToMove, 1, valueDate,
				finRepayQueue.getRpyDate(), "R", suspFromDate, linkedTranId);
		getFinanceSuspHeadDAO().saveSuspenseDetails(suspDetails, "");

		logger.debug("Leaving");
	}

	/**
	 * Method for update of Finance Suspend Data for Release
	 * 
	 * @param financeMain
	 * @param profitDetail
	 * @param details
	 * @param valueDate
	 * @param isEODProcess
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public void capitalizationSuspRelease(DataSet dataSet, AEAmountCodes amountCodes,
			boolean isEODProcess,boolean isRIAFinance) throws AccountNotFoundException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		boolean isInSuspNow = true;
		BigDecimal suspAmtToMove = BigDecimal.ZERO;
		Date suspFromDate = null;

		//Fetch the Fiance Suspend head
		FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(
				dataSet.getFinReference(),"");
		if (suspHead == null || !suspHead.isFinIsInSusp()) {
			return;
		} 

		//Pending OverDue Details for that particular Schedule date and overDue For
		int curOverDueDays = getFinODDetailsDAO().getPendingOverDuePayment(dataSet.getFinReference());
		int suspenceGraceDays = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("SUSP_AFTER").toString());
		Date odDate = null;
		if (curOverDueDays > suspenceGraceDays) {

			suspFromDate = DateUtility.addDays(dataSet.getValueDate(), -suspenceGraceDays);

			//Suspend Amount Calculation
			if (suspFromDate.compareTo(DateUtility.getDBDate(SystemParameterDetails
					.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString())) > 0 && !suspHead.isManualSusp()) {
				suspAmtToMove = suspHead.getFinCurSuspAmt();
				isInSuspNow = false;
			} else {
				suspAmtToMove = amountCodes.getSUSPRLS();
				isInSuspNow = true;
			}

			int odGraceDays = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("ODC_GRACE").toString());
			odDate = DateUtility.addDays(dataSet.getValueDate(), -(suspenceGraceDays+odGraceDays));

		} else {
			
			suspFromDate = suspHead.getFinSuspDate();
			if(!suspHead.isManualSusp()){
				suspAmtToMove = suspHead.getFinCurSuspAmt();
				isInSuspNow = false;
			}
		}

		amountCodes.setSUSPRLS(suspAmtToMove);

		//Postings Preparation
		Date dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());
		linkedTranId = (Long) getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, isEODProcess, 
				 isRIAFinance, "Y", dateAppDate,false, Long.MIN_VALUE).get(1);

		//Finance Suspend Head
		suspHead.setFinIsInSusp(isInSuspNow);
		suspHead.setFinCurSuspAmt(suspHead.getFinCurSuspAmt().subtract(suspAmtToMove));
		if (suspHead.getFinCurSuspAmt().compareTo(BigDecimal.ZERO) == 0 && !suspHead.isManualSusp()) {
			suspHead.setFinIsInSusp(false);
		}
		getFinanceSuspHeadDAO().update(suspHead, "");

		//Finance Suspend Details Record Insert
		FinanceSuspDetails suspDetails = prepareSuspDetail(suspHead, suspAmtToMove, 1,
				DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(
						PennantConstants.APP_DATE_VALUE).toString()), odDate, "R", suspFromDate, linkedTranId);
		getFinanceSuspHeadDAO().saveSuspenseDetails(suspDetails, "");

		logger.debug("Leaving");
	}

	/**
	 * Prepare data for Finance Suspend Head
	 * 
	 * @param head
	 * @param financeMain
	 * @param suspFromDate
	 * @param suspAmount
	 * @return
	 */
	private FinanceSuspHead prepareSuspHeadData(FinanceSuspHead head, FinRepayQueue repayQueue,
			Date suspFromDate, BigDecimal suspAmount, boolean isPastDeferment) {
		logger.debug("Entering");
		if (head == null) {
			head = new FinanceSuspHead();
			head.setFinReference(repayQueue.getFinReference());
			head.setFinBranch(repayQueue.getBranch());
			head.setFinType(repayQueue.getFinType());
			head.setCustId(repayQueue.getCustomerID());
			head.setFinSuspSeq(1);
		} else {
			head.setFinSuspSeq(head.getFinSuspSeq() + 1);
		}

		head.setFinIsInSusp(true);
		head.setFinSuspDate(suspFromDate);
		head.setFinSuspAmt(suspAmount);
		head.setFinCurSuspAmt(suspAmount);
		
		if(!isPastDeferment){
			head.setFinSuspTrfDate(suspFromDate);
		}else{
			head.setFinSuspDate(head.getFinSuspTrfDate());
		}
		logger.debug("Leaving");
		return head;
	}

	/**
	 * Method for Preparation of Finance Suspend Details data
	 * 
	 * @param head
	 * @param valueDate
	 * @param oDDate
	 * @return
	 */
	private FinanceSuspDetails prepareSuspDetail(FinanceSuspHead head, BigDecimal suspAmt,
			int suspSeq, Date valueDate, Date oDDate, String trfMvt, Date suspFromDate, long linkedTranId) {
		logger.debug("Entering");

		FinanceSuspDetails suspDetails = new FinanceSuspDetails();
		suspDetails.setFinReference(head.getFinReference());
		suspDetails.setFinBranch(head.getFinBranch());
		suspDetails.setFinType(head.getFinType());
		suspDetails.setCustId(head.getCustId());
		suspDetails.setFinTrfDate(valueDate);
		suspDetails.setFinTrfMvt(trfMvt);
		suspDetails.setFinSuspSeq(suspSeq);
		suspDetails.setFinTrfAmt(suspAmt);
		Date date = DateUtility.getDBDate(oDDate.toString());
		suspDetails.setFinODDate(date);
		suspDetails.setFinTrfFromDate(suspFromDate);
		suspDetails.setLinkedTranId(linkedTranId);
		logger.debug("Leaving");
		return suspDetails;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}
	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}
	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}
	
	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
    	return customerStatusCodeDAO;
    }
	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
    	this.customerStatusCodeDAO = customerStatusCodeDAO;
    }

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

}
