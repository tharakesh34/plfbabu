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
package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.financemanagement.FinSuspHoldDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSuspDetails;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.PennantConstants;

public class SuspenseService extends ServiceHelper {
	private static final long		serialVersionUID	= -7469564513544156223L;
	private static Logger			logger				= Logger.getLogger(SuspenseService.class);

	private FinanceSuspHeadDAO		financeSuspHeadDAO;
	private FinSuspHoldDAO			finSuspHoldDAO;
	private PostingsPreparationUtil	postingsPreparationUtil;

	public SuspenseService() {
		super();
	}

	public void processSuspense(Date date, FinanceMain financeMain, FinRepayQueue finRepayQueue) throws Exception {

		suspensePreparation(financeMain, finRepayQueue, date, false);
		// SUSPENSE RELEASE
		boolean releaseSuspemnse = false;
		BigDecimal principlebal = finRepayQueue.getSchdPri().subtract(finRepayQueue.getSchdPriPaid());
		BigDecimal profitbal = finRepayQueue.getSchdPft().subtract(finRepayQueue.getSchdPftPaid());

		if ((principlebal.add(profitbal)).compareTo(BigDecimal.ZERO) == 0) {
			releaseSuspemnse = true;
		}

		if (finRepayQueue.getSchdPftPayNow().compareTo(BigDecimal.ZERO) > 0) {
			releaseSuspemnse = true;
		}

		if (releaseSuspemnse) {
			suspReleasePreparation(financeMain, finRepayQueue.getSchdPftPayNow(), finRepayQueue, date);
		}

	}

	/**
	 * Method for preparation of Finance Suspend Data
	 * 
	 * @param financeMain
	 * @param details
	 * @param valueDate
	 * @param isEODProcess
	 * @throws Exception
	 */
	private void suspensePreparation(FinanceMain financeMain, FinRepayQueue repayQueue, Date valueDate,
			boolean isPastDeferment) throws Exception {
		logger.debug("Entering");

		int curOdDays = getFinODDetailsDAO()
				.getFinCurSchdODDays(financeMain.getFinReference(), repayQueue.getRpyDate());

		// Check Profit will Suspend or not based upon Current Overdue Days
		boolean suspendProfit = getCustomerStatusCodeDAO().getFinanceSuspendStatus(curOdDays);
		if (!suspendProfit) {
			return;
		}

		FinanceType fintype = getFinanceType(repayQueue.getFinType());
		// Check suspense has hold or not
		boolean holdsuspense = getFinSuspHoldDAO().holdSuspense(fintype.getFinCategory(), fintype.getFinType(),
				repayQueue.getFinReference(), financeMain.getCustID());
		if (holdsuspense) {
			return;
		}

		FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(financeMain.getFinReference(), "");
		if (suspHead != null && suspHead.isFinIsInSusp()) {
			return;
		}

		//Finance Related Details Fetching
		BigDecimal suspAmount = getFinanceScheduleDetailDAO().getSuspenseAmount(financeMain.getFinReference(),
				valueDate);

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setSuspNow(suspAmount);
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_NORM_PIS);
		aeEvent.setValueDate(valueDate);
		aeEvent.setSchdDate(valueDate);

		//FIXME: PV 07MAY17: To be addressed when suspense related changes released.
		//Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);
		//finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		long linkedTranId = aeEvent.getLinkedTranId();

		if (suspHead != null) {
			// Update Finance Suspend Head
			suspHead = prepareSuspHeadData(suspHead, repayQueue, valueDate, suspAmount, isPastDeferment);
			getFinanceSuspHeadDAO().update(suspHead, "");
		} else {
			// Insert Finance Suspend Head
			suspHead = prepareSuspHeadData(suspHead, repayQueue, valueDate, suspAmount, isPastDeferment);
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

		// Insert Finance Suspend Details data
		FinanceSuspDetails suspDetails = prepareSuspDetail(suspHead, suspHead.getFinSuspAmt(),
				suspHead.getFinSuspSeq(), valueDate, repayQueue.getRpyDate(), "S", valueDate, linkedTranId);
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
	 * @throws Exception
	 */
	private void suspReleasePreparation(FinanceMain financeMain, BigDecimal releasePftAmount,
			FinRepayQueue finRepayQueue, Date valueDate) throws Exception {
		logger.debug("Entering");

		// Fetch the Finance Suspend head
		FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(finRepayQueue.getFinReference(), "");
		if (suspHead == null || !suspHead.isFinIsInSusp()) {
			return;
		}

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		boolean isInSuspNow = true;
		BigDecimal suspAmtToMove = BigDecimal.ZERO;
		Date suspFromDate = null;

		// Pending OverDue Details for that particular Schedule date and overDue
		// For
		int curOverDueDays = getFinODDetailsDAO().getPendingOverDuePayment(finRepayQueue.getFinReference());
		int suspenceGraceDays = SysParamUtil.getValueAsInt("SUSP_AFTER");

		if (curOverDueDays > suspenceGraceDays) {

			suspFromDate = DateUtility.addDays(valueDate, -suspenceGraceDays);

			// Suspend Amount Calculation
			if (suspFromDate.compareTo(valueDate) > 0 && !suspHead.isManualSusp()) {
				suspAmtToMove = suspHead.getFinCurSuspAmt();
				isInSuspNow = false;
			} else {
				suspAmtToMove = releasePftAmount;
				isInSuspNow = true;
			}

		} else {
			suspFromDate = suspHead.getFinSuspDate();
			if (!suspHead.isManualSusp()) {
				suspAmtToMove = suspHead.getFinCurSuspAmt();
				isInSuspNow = false;
			}
		}

		amountCodes.setSuspRls(suspAmtToMove);
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_PIS_NORM);
		aeEvent.setValueDate(valueDate);
		aeEvent.setSchdDate(suspFromDate);

		HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		//FIXME: PV: 07MAY17 to be addressed when suspense related changes released
		//Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);
		//finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		
		long linkedTranId = aeEvent.getLinkedTranId();

		// Finance Suspend Head
		suspHead.setFinIsInSusp(isInSuspNow);
		suspHead.setFinCurSuspAmt(suspHead.getFinCurSuspAmt().subtract(suspAmtToMove));
		if (!isInSuspNow && !suspHead.isManualSusp()) {
			suspHead.setFinSuspTrfDate(null);
		}

		getFinanceSuspHeadDAO().update(suspHead, "");

		// Finance Suspend Details Record Insert
		FinanceSuspDetails suspDetails = prepareSuspDetail(suspHead, suspAmtToMove, 1, valueDate,
				finRepayQueue.getRpyDate(), "R", suspFromDate, linkedTranId);
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
	private FinanceSuspHead prepareSuspHeadData(FinanceSuspHead head, FinRepayQueue repayQueue, Date suspFromDate,
			BigDecimal suspAmount, boolean isPastDeferment) {
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

		if (!isPastDeferment) {
			head.setFinSuspTrfDate(suspFromDate);
		} else {
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
	private FinanceSuspDetails prepareSuspDetail(FinanceSuspHead head, BigDecimal suspAmt, int suspSeq, Date valueDate,
			Date oDDate, String trfMvt, Date suspFromDate, long linkedTranId) {
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public FinSuspHoldDAO getFinSuspHoldDAO() {
		return finSuspHoldDAO;
	}

	public void setFinSuspHoldDAO(FinSuspHoldDAO finSuspHoldDAO) {
		this.finSuspHoldDAO = finSuspHoldDAO;
	}

}
