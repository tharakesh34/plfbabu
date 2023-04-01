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
 * FileName : SuspensePostingUtil.java *
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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSuspDetails;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;

public class SuspensePostingUtil implements Serializable {
	private static final long serialVersionUID = -7469564513544156223L;
	private static Logger logger = LogManager.getLogger(SuspensePostingUtil.class);

	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private CustomerStatusCodeDAO customerStatusCodeDAO;
	private PostingsPreparationUtil postingsPreparationUtil;

	public SuspensePostingUtil() {
		super();
	}

	public List<Object> suspensePreparation(FinanceMain fm, FinRepayQueue repayQueue, Date valueDate,
			boolean isPastDeferment) throws AppException {
		logger.debug("Entering");

		List<Object> returnList = new ArrayList<Object>(3);
		boolean isPostingSuccess = true;

		boolean isDueSuspNow = false;
		long finID = fm.getFinID();
		int curOdDays = getFinODDetailsDAO().getFinCurSchdODDays(finID, repayQueue.getRpyDate());

		// Check Profit will Suspend or not based upon Current Overdue Days
		boolean suspendProfit = getCustomerStatusCodeDAO().getFinanceSuspendStatus(curOdDays);
		if (!suspendProfit) {
			returnList.add(isPostingSuccess);
			returnList.add(isDueSuspNow);
			returnList.add(null);
			return returnList;
		}

		FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(finID, "");
		if (suspHead != null && suspHead.isFinIsInSusp()) {
			returnList.add(isPostingSuccess);
			returnList.add(isDueSuspNow);
			returnList.add(null);
			return returnList;
		}

		Date suspFromDate = null;
		BigDecimal suspAmount = BigDecimal.ZERO;

		// Finance Related Details Fetching
		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		suspAmount = getFinanceScheduleDetailDAO().getSuspenseAmount(finID, valueDate);
		suspFromDate = DateUtil.addDays(repayQueue.getRpyDate(), curOdDays);

		aeEvent.setFinID(fm.getFinID());
		aeEvent.setFinReference(fm.getFinReference());
		amountCodes.setSuspNow(suspAmount);
		aeEvent.setAccountingEvent(AccountingEvent.NORM_PIS);
		aeEvent.setValueDate(valueDate);
		aeEvent.setSchdDate(suspFromDate);

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		aeEvent.setDataMap(dataMap);

		aeEvent.setEOD(false);

		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		if (!phase.equals(PennantConstants.APP_PHASE_DAY)) {
			aeEvent.setEOD(true);
		}

		// Postings Preparation
		Date dateAppDate = SysParamUtil.getAppDate();
		aeEvent.setAppDate(dateAppDate);
		aeEvent.setAppValueDate(dateAppDate);
		aeEvent.setValueDate(valueDate);
		aeEvent.setPostDate(dateAppDate);

		aeEvent = getPostingsPreparationUtil().processPostingDetails(aeEvent);

		isPostingSuccess = aeEvent.isPostingSucess();
		long linkedTranId = aeEvent.getLinkedTranId();

		// Check Status for Postings
		if (!isPostingSuccess) {
			returnList.add(isPostingSuccess);
			returnList.add(isDueSuspNow);
			returnList.add(aeEvent.getErrorMessage());
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
		FinanceSuspDetails suspDetails = prepareSuspDetail(suspHead, suspHead.getFinSuspAmt(), suspHead.getFinSuspSeq(),
				valueDate, repayQueue.getRpyDate(), "S", suspFromDate, linkedTranId);
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
	 * @param fm
	 * @param profitDetail
	 * @param details
	 * @param valueDate
	 * @param isEODProcess
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	public void suspReleasePreparation(FinanceMain fm, BigDecimal releasePftAmount, FinRepayQueue finRepayQueue,
			Date valueDate, boolean isEODProcess) throws AppException {
		logger.debug("Entering");

		// Fetch the Finance Suspend head
		FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(finRepayQueue.getFinID(), "");
		if (suspHead == null || !suspHead.isFinIsInSusp()) {
			return;
		}

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		boolean isInSuspNow = true;
		BigDecimal suspAmtToMove = BigDecimal.ZERO;
		Date suspFromDate = null;

		// Pending OverDue Details for that particular Schedule date and overDue For
		int curOverDueDays = getFinODDetailsDAO().getPendingOverDuePayment(finRepayQueue.getFinID());
		int suspenceGraceDays = SysParamUtil.getValueAsInt("SUSP_AFTER");

		if (curOverDueDays > suspenceGraceDays) {

			suspFromDate = DateUtil.addDays(valueDate, -suspenceGraceDays);

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

		// Creating DataSet using Finance Details
		aeEvent.setFinID(fm.getFinID());
		aeEvent.setFinReference(fm.getFinReference());
		amountCodes.setSuspRls(suspAmtToMove);
		aeEvent.setAccountingEvent(AccountingEvent.PIS_NORM);
		aeEvent.setValueDate(valueDate);
		aeEvent.setSchdDate(suspFromDate);

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		aeEvent.setDataMap(dataMap);

		// Postings Preparation
		Date dateAppDate = SysParamUtil.getAppDate();
		aeEvent.setAppDate(dateAppDate);
		aeEvent.setAppValueDate(dateAppDate);
		aeEvent.setValueDate(valueDate);
		aeEvent.setPostDate(dateAppDate);

		aeEvent = getPostingsPreparationUtil().processPostingDetails(aeEvent);

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
		suspDetails.setFinODDate(DateUtil.getDatePart(oDDate));
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
