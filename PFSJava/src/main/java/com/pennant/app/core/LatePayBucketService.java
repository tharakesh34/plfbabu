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
 * FileName : LatePayMarkingService.java *
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
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.extension.DPDExtension;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.util.ProductUtil;

public class LatePayBucketService extends ServiceHelper {

	public LatePayBucketService() {
		super();
	}

	public void processDPDBuketing(CustEODEvent custEODEvent) throws Exception {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {
			FinanceProfitDetail fpd = finEODEvent.getFinProfitDetail();
			FinanceMain fm = finEODEvent.getFinanceMain();
			List<FinanceScheduleDetail> fsd = finEODEvent.getFinanceScheduleDetails();

			boolean isFinStsChanged = updateDPDBuketing(fsd, fm, fpd, valueDate, true);

			if (isFinStsChanged) {
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("FinStatus");
				finEODEvent.addToFinMianUpdate("DueBucket");
			}
			fpd.setFinStatus(fm.getFinStatus());
			fpd.setDueBucket(fm.getDueBucket());
		}
	}

	public boolean updateDPDBuketing(List<FinanceScheduleDetail> schedules, FinanceMain fm, FinanceProfitDetail pfd,
			Date valueDate, boolean eodEvent) {

		Map<Date, BigDecimal> reallocationMap = new TreeMap<>();
		BigDecimal netSchdAmount = BigDecimal.ZERO;
		BigDecimal netSchdDue = BigDecimal.ZERO;
		BigDecimal totalPaid = BigDecimal.ZERO;
		BigDecimal minDuePerc = BigDecimal.ZERO;
		BigDecimal duePercentage = BigDecimal.ZERO;
		Date firstDuedate = null;
		int newCurODDays = 0;
		long bucketID = 0;
		boolean dpdCalcIncExs = false;

		String finStatus = StringUtils.trimToEmpty(fm.getFinStatus());
		String newFinStatus = FinanceConstants.FINSTSRSN_SYSTEM;
		int curODDays = pfd.getCurODDays();
		int dueBucket = fm.getDueBucket();

		EventProperties eventProperties = fm.getEventProperties();

		// prepare the re allocation required records i.e. schedule Date with scheduled amount and total paid till today
		for (FinanceScheduleDetail schd : schedules) {
			if (schd.getSchDate().compareTo(valueDate) > 0) {
				break;
			}

			if (schd.isRepayOnSchDate() || schd.isPftOnSchDate()) {
				totalPaid = totalPaid.add(schd.getSchdPriPaid().add(schd.getSchdPftPaid()));

				BigDecimal partialPaidAmt = BigDecimal.ZERO;
				if (DPDExtension.EXCLUDE_VD_PART_PAYMENT) {
					partialPaidAmt = schd.getPartialPaidAmt();
					totalPaid = totalPaid.subtract(partialPaidAmt);
				}

				BigDecimal schdEmi = schd.getPrincipalSchd().add(schd.getProfitSchd());

				schdEmi = schdEmi.subtract(partialPaidAmt);

				reallocationMap.put(schd.getSchDate(), schdEmi);
			}
		}

		if (eventProperties.isParameterLoaded()) {
			dpdCalcIncExs = eventProperties.isDpdCalIncludeExcess();
			minDuePerc = eventProperties.getIgnoringBucket();
		} else {
			dpdCalcIncExs = SysParamUtil.isAllowed("DPD_CALC_INCLUDE_EXCESS");
			Object object = SysParamUtil.getValue(SMTParameterConstants.IGNORING_BUCKET);

			if (object != null) {
				minDuePerc = (BigDecimal) object;
			}
		}

		if (dpdCalcIncExs) {
			// fin excess amount
			BigDecimal excessBalAmt = getDeductedAmt(fm.getFinID());

			// consider excess amount to calculate ODDays and DueBucket
			totalPaid = totalPaid.add(excessBalAmt);
		}

		int odGrcDays = 0;
		if (ProductUtil.isOverDraft(fm)) {
			odGrcDays = overdrafLoanService.getGraceDays(fm);
		}

		// reallocate and find the first due date.
		for (Entry<Date, BigDecimal> entry : reallocationMap.entrySet()) {
			if (totalPaid.compareTo(entry.getValue()) >= 0) {
				totalPaid = totalPaid.subtract(entry.getValue());
			} else {

				if (firstDuedate == null) {
					firstDuedate = DateUtil.addDays(entry.getKey(), odGrcDays);
				}
				netSchdDue = netSchdDue.add(entry.getValue().subtract(totalPaid));
				totalPaid = BigDecimal.ZERO;
				netSchdAmount = netSchdAmount.add(entry.getValue());
			}
		}

		if (firstDuedate != null) {
			Date odtCaldate = valueDate;
			if (ImplementationConstants.LP_MARK_FIRSTDAY && eodEvent) {
				odtCaldate = DateUtil.addDays(valueDate, 1);
			}

			if (odtCaldate.compareTo(firstDuedate) > 0) {
				newCurODDays = DateUtil.getDaysBetween(firstDuedate, odtCaldate);
			} else {
				newCurODDays = 0;
			}
		}

		// calculate DueBucket
		int newDueBucket = (new BigDecimal(newCurODDays).divide(new BigDecimal(30), 0, RoundingMode.UP)).intValue();

		// for due percentage calculation
		if (netSchdAmount.compareTo(BigDecimal.ZERO) > 0) {
			duePercentage = (netSchdDue.multiply(new BigDecimal(100))).divide(netSchdAmount, 2, RoundingMode.HALF_DOWN);
		}

		if (duePercentage.compareTo(minDuePerc) <= 0) {
			newDueBucket = 0;
		}

		// No current OD Days and No change in the Bucket Status and Number of Buckets
		if (newCurODDays == 0 || newDueBucket == 0) {

			// No change in the Bucket Status and Number of Buckets
			if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket && curODDays == newCurODDays) {
				return false;
			} else {
				doWriteDPDBuketData(fm, pfd, newFinStatus, newDueBucket, newCurODDays);
				return true;
			}
		}

		// DPD Configuration
		newFinStatus = String.valueOf(newDueBucket);

		// No change in the Bucket Status and Number of Buckets
		if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket && curODDays == newCurODDays) {
			return false;
		}

		doWriteDPDBuketData(fm, pfd, newFinStatus, newDueBucket, newCurODDays);

		return true;
	}

	private void doWriteDPDBuketData(FinanceMain fm, FinanceProfitDetail pfd, String newFinStatus, int newDueBucket,
			int newCurODDays) {

		fm.setFinStatus(newFinStatus);
		fm.setDueBucket(newDueBucket);

		pfd.setFinStatus(newFinStatus);
		pfd.setDueBucket(newDueBucket);

		pfd.setActualODDays(pfd.getCurODDays());
		if (DPDExtension.VARTUAL_DPD) {
			pfd.setCurODDays(newCurODDays);
		}
	}

	private BigDecimal getDeductedAmt(long finID) {
		BigDecimal balanceAmt = BigDecimal.ZERO;
		List<FinExcessAmount> excess = finExcessAmountDAO.getExcessAmountsByRef(finID);
		for (FinExcessAmount finExcessAmount : excess) {
			balanceAmt = balanceAmt.add(finExcessAmount.getBalanceAmt());
		}

		return balanceAmt;
	}
}