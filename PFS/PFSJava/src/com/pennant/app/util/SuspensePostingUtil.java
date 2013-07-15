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
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceSuspDetails;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class SuspensePostingUtil implements Serializable {

    private static final long serialVersionUID = -7469564513544156223L;
	private static Logger logger = Logger.getLogger(SuspensePostingUtil.class);

	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private long linkedTranId;

	/**
	 * Method for preparation of Finance Suspend Data
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
	public boolean suspensePreparation(FinanceMain financeMain, FinanceProfitDetail profitDetail,
			FinODDetails details, Date valueDate, boolean isRIAFinance)
	throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		boolean isDueSuspNow = false;
		int suspenceGraceDays = Integer.parseInt(SystemParameterDetails.getSystemParameterValue(
		"SUSP_AFTER").toString());
		Date suspFromDate = null;
		BigDecimal suspAmount = BigDecimal.ZERO;

		if (details.getFinCurODDays() < suspenceGraceDays) {
			return isDueSuspNow;
		} 

		boolean isPostingSuccess = false;
		FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(
				details.getFinReference(),"");
		if (suspHead != null && suspHead.isFinIsInSusp()) {
			return isDueSuspNow;
		} 

		//Finance Related Details Fetching
		AEAmounts aeAmounts = new AEAmounts();
		AEAmountCodes amountCodes = new AEAmountCodes();
		suspAmount = getFinanceScheduleDetailDAO().getSuspenseAmount(
				details.getFinReference(), valueDate);
		suspFromDate = DateUtility.addDays(details.getFinODSchdDate(), suspenceGraceDays);

		DataSet dataSet = aeAmounts.createDataSet(financeMain, "M_NONAMZ", valueDate,
				suspFromDate);
		amountCodes.setFinReference(dataSet.getFinReference());
		amountCodes.setSUSPNOW(suspAmount);
		dataSet.setNewRecord(false);

		//Postings Preparation
		Date dateAppDate = DateUtility.getDBDate(SystemParameterDetails
				.getSystemParameterValue("APP_DATE").toString());
		List<Object> result = getPostingsPreparationUtil().processPostingDetails(
				dataSet, amountCodes, true,isRIAFinance,  "Y", dateAppDate, null, false);
		isPostingSuccess = (Boolean)result.get(0);
		linkedTranId = (Long) result.get(1);


		//Check Status for Postings
		if (!isPostingSuccess) {
			return isDueSuspNow;
		}

		if (suspHead != null) {
			// Update Finance Suspend Head
			suspHead = prepareSuspHeadData(suspHead, details, suspFromDate, suspAmount);
			getFinanceSuspHeadDAO().update(suspHead, "");
		} else {
			// Insert Finance Suspend Head
			suspHead = prepareSuspHeadData(suspHead, details, suspFromDate, suspAmount);
			getFinanceSuspHeadDAO().save(suspHead, "");
		}
		isDueSuspNow = true;

		// Insert Finance Suspend Details data
		FinanceSuspDetails suspDetails = prepareSuspDetail(suspHead,
				suspHead.getFinSuspAmt(), suspHead.getFinSuspSeq(), valueDate,
				details.getFinODSchdDate(), "S", suspFromDate, linkedTranId);
		getFinanceSuspHeadDAO().saveSuspenseDetails(suspDetails, "");
		
		logger.debug("Leaving");
		return isDueSuspNow;
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

		//Condition for Suspence release after Value Date
		if (finRepayQueue.getRpyDate().compareTo(valueDate) >= 0) {
			return;
		}

		AEAmountCodes amountCodes = new AEAmountCodes();
		AEAmounts aeAmounts = new AEAmounts();
		boolean isInSuspNow = true;
		BigDecimal suspAmtToMove = BigDecimal.ZERO;
		Date suspFromDate = null;

		//Fetch the Finance Suspend head
		FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(
				finRepayQueue.getFinReference(),"");
		if (suspHead == null || !suspHead.isFinIsInSusp()) {
			return;
		}

		//Pending OverDue Details for that particular Schedule date and overDue For
		int curOverDueDays = getFinODDetailsDAO().getPendingOverDuePayment(finRepayQueue.getFinReference());
		int suspenceGraceDays = Integer.parseInt(SystemParameterDetails
				.getSystemParameterValue("SUSP_AFTER").toString());
		
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
		DataSet dataSet = aeAmounts.createDataSet(financeMain, "M_AMZ", valueDate, suspFromDate);
		amountCodes.setFinReference(dataSet.getFinReference());
		amountCodes.setSUSPRLS(suspAmtToMove);
		dataSet.setNewRecord(false);

		//Postings Preparation
		Date dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(
		"APP_DATE").toString());
		linkedTranId = (Long) getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes,
				isEODProcess, isRIAFinance, "Y", dateAppDate, null, false).get(1);

		//Finance Suspend Head
		suspHead.setFinIsInSusp(isInSuspNow);
		suspHead.setFinCurSuspAmt(suspHead.getFinCurSuspAmt().subtract(suspAmtToMove));
		if (suspHead.getFinCurSuspAmt().compareTo(BigDecimal.ZERO) == 0 && !suspHead.isManualSusp()) {
			suspHead.setFinIsInSusp(false);
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
					.getSystemParameterValue("APP_VALUEDATE").toString())) > 0 && !suspHead.isManualSusp()) {
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
		Date dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString());
		linkedTranId = (Long) getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, isEODProcess, 
				 isRIAFinance, "Y", dateAppDate, null, false).get(1);

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
						"APP_VALUEDATE").toString()), odDate, "R", suspFromDate, linkedTranId);
		getFinanceSuspHeadDAO().saveSuspenseDetails(suspDetails, "");

		logger.debug("Leaving");
	}

	/**
	 * Prepare data for Finance Suspend Head
	 * 
	 * @param head
	 * @param odDetails
	 * @param suspFromDate
	 * @param suspAmount
	 * @return
	 */
	private FinanceSuspHead prepareSuspHeadData(FinanceSuspHead head, FinODDetails odDetails,
			Date suspFromDate, BigDecimal suspAmount) {
		logger.debug("Entering");
		if (head == null) {
			head = new FinanceSuspHead();
			head.setFinReference(odDetails.getFinReference());
			head.setFinBranch(odDetails.getFinBranch());
			head.setFinType(odDetails.getFinType());
			head.setCustId(odDetails.getCustID());
			head.setFinSuspSeq(1);
		} else {
			head.setFinSuspSeq(head.getFinSuspSeq() + 1);
		}

		head.setFinIsInSusp(true);
		head.setFinSuspDate(suspFromDate);
		head.setFinSuspAmt(suspAmount);
		head.setFinCurSuspAmt(suspAmount);
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

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

}
