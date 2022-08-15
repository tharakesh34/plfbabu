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
package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;

public class SuspenseService extends ServiceHelper {
	private static final long serialVersionUID = -7469564513544156223L;
	private static Logger logger = LogManager.getLogger(SuspenseService.class);

	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private FinSuspHoldDAO finSuspHoldDAO;
	private PostingsPreparationUtil postingsPreparationUtil;

	public SuspenseService() {
		super();
	}

	public void processSuspense(Date date, FinanceMain fm, FinRepayQueue finRepayQueue) {
		suspensePreparation(fm, finRepayQueue, date, false);
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
			suspReleasePreparation(fm, finRepayQueue.getSchdPftPayNow(), finRepayQueue, date);
		}

	}

	private void suspensePreparation(FinanceMain fm, FinRepayQueue repayQueue, Date valueDate,
			boolean isPastDeferment) {
		logger.debug(Literal.ENTERING);

		long finID = fm.getFinID();
		int curOdDays = finODDetailsDAO.getFinCurSchdODDays(finID, repayQueue.getRpyDate());

		// Check Profit will Suspend or not based upon Current Overdue Days
		boolean suspendProfit = customerStatusCodeDAO.getFinanceSuspendStatus(curOdDays);
		if (!suspendProfit) {
			return;
		}

		FinanceType fintype = getFinanceType(repayQueue.getFinType());
		// Check suspense has hold or not
		boolean holdsuspense = finSuspHoldDAO.holdSuspense(fintype.getFinCategory(), fintype.getFinType(),
				repayQueue.getFinReference(), fm.getCustID());
		if (holdsuspense) {
			return;
		}

		FinanceSuspHead suspHead = financeSuspHeadDAO.getFinanceSuspHeadById(finID, "");
		if (suspHead != null && suspHead.isFinIsInSusp()) {
			return;
		}

		// Finance Related Details Fetching
		BigDecimal suspAmount = financeScheduleDetailDAO.getSuspenseAmount(finID, valueDate);

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setSuspNow(suspAmount);
		aeEvent.setAccountingEvent(AccountingEvent.NORM_PIS);
		aeEvent.setValueDate(valueDate);
		aeEvent.setSchdDate(valueDate);

		// FIXME: PV 07MAY17: To be addressed when suspense related changes released.
		// Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);
		// finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		long linkedTranId = aeEvent.getLinkedTranId();

		if (suspHead != null) {
			// Update Finance Suspend Head
			suspHead = prepareSuspHeadData(suspHead, repayQueue, valueDate, suspAmount, isPastDeferment);
			financeSuspHeadDAO.update(suspHead, "");
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
			financeSuspHeadDAO.save(suspHead, "");
		}

		// Insert Finance Suspend Details data
		FinanceSuspDetails suspDetails = prepareSuspDetail(suspHead, suspHead.getFinSuspAmt(), suspHead.getFinSuspSeq(),
				valueDate, repayQueue.getRpyDate(), "S", valueDate, linkedTranId);
		financeSuspHeadDAO.saveSuspenseDetails(suspDetails, "");

		logger.debug(Literal.LEAVING);
	}

	private void suspReleasePreparation(FinanceMain fm, BigDecimal releasePftAmount, FinRepayQueue repayQueue,
			Date valueDate) {
		logger.debug(Literal.ENTERING);

		long finID = repayQueue.getFinID();

		// Fetch the Finance Suspend head
		FinanceSuspHead suspHead = financeSuspHeadDAO.getFinanceSuspHeadById(finID, "");
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
		int curOverDueDays = finODDetailsDAO.getPendingOverDuePayment(finID);
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

		amountCodes.setSuspRls(suspAmtToMove);
		aeEvent.setAccountingEvent(AccountingEvent.PIS_NORM);
		aeEvent.setValueDate(valueDate);
		aeEvent.setSchdDate(suspFromDate);

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		// FIXME: PV: 07MAY17 to be addressed when suspense related changes released
		// Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);
		// finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		long linkedTranId = aeEvent.getLinkedTranId();

		// Finance Suspend Head
		suspHead.setFinIsInSusp(isInSuspNow);
		suspHead.setFinCurSuspAmt(suspHead.getFinCurSuspAmt().subtract(suspAmtToMove));
		if (!isInSuspNow && !suspHead.isManualSusp()) {
			suspHead.setFinSuspTrfDate(null);
		}

		financeSuspHeadDAO.update(suspHead, "");

		// Finance Suspend Details Record Insert
		FinanceSuspDetails suspDetails = prepareSuspDetail(suspHead, suspAmtToMove, 1, valueDate,
				repayQueue.getRpyDate(), "R", suspFromDate, linkedTranId);
		financeSuspHeadDAO.saveSuspenseDetails(suspDetails, "");

		logger.debug(Literal.LEAVING);
	}

	private FinanceSuspHead prepareSuspHeadData(FinanceSuspHead head, FinRepayQueue repayQueue, Date suspFromDate,
			BigDecimal suspAmount, boolean isPastDeferment) {

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

		return head;
	}

	private FinanceSuspDetails prepareSuspDetail(FinanceSuspHead head, BigDecimal suspAmt, int suspSeq, Date valueDate,
			Date oDDate, String trfMvt, Date suspFromDate, long linkedTranId) {

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

		return suspDetails;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinSuspHoldDAO(FinSuspHoldDAO finSuspHoldDAO) {
		this.finSuspHoldDAO = finSuspHoldDAO;
	}

}
