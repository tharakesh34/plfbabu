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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.SMTParameterConstants;

public class LatePayBucketService extends ServiceHelper {

	private static final long	serialVersionUID	= 6161809223570900644L;
	private static Logger		logger				= Logger.getLogger(LatePayBucketService.class);
	@Autowired
	private FinExcessAmountDAO	finExcessAmountDAO;

	/**
	 * Default constructor
	 */
	public LatePayBucketService() {
		super();
	}

	/**
	 * @param connection
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public CustEODEvent processDPDBuketing(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");
		
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {
			boolean isFinStsChanged = updateDPDBuketing(finEODEvent.getFinanceScheduleDetails(),
					finEODEvent.getFinanceMain(), finEODEvent.getFinProfitDetail(), valueDate);

			if (isFinStsChanged) {
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("FinStatus");
				finEODEvent.addToFinMianUpdate("DueBucket");
			}
			finEODEvent.getFinProfitDetail().setFinStatus(finEODEvent.getFinanceMain().getFinStatus());
			finEODEvent.getFinProfitDetail().setDueBucket(finEODEvent.getFinanceMain().getDueBucket());
		}

		logger.debug(" Leaving ");
		return custEODEvent;
	}

	/**
	 * @param listScheduleDetails
	 * @param financeMain
	 * @param valueDate
	 * @return
	 */
	public boolean updateDPDBuketing(List<FinanceScheduleDetail> listScheduleDetails, FinanceMain financeMain,
			FinanceProfitDetail finProfitDetail, Date valueDate) {
		logger.debug(" Entering ");

		Map<Date, BigDecimal> reallocationMap = new TreeMap<Date, BigDecimal>();
		BigDecimal netSchdAmount = BigDecimal.ZERO;
		BigDecimal netSchdDue = BigDecimal.ZERO;
		BigDecimal totalPaid = BigDecimal.ZERO;
		BigDecimal minDuePerc = BigDecimal.ZERO;
		BigDecimal duePercentage = BigDecimal.ZERO;
		Date firstDuedate = null;
		int newCurODDays = 0;
		long bucketID = 0;

		String finStatus = StringUtils.trimToEmpty(financeMain.getFinStatus());
		String newFinStatus = FinanceConstants.FINSTSRSN_SYSTEM;
		int curODDays = finProfitDetail.getCurODDays();
		int dueBucket = financeMain.getDueBucket();

		//prepare the re allocation required records i.e. schedule Date with scheduled amount and total paid till today
		for (FinanceScheduleDetail schDetail : listScheduleDetails) {
			if (schDetail.getSchDate().compareTo(valueDate) > 0) {
				break;
			}

			if (schDetail.isRepayOnSchDate() || schDetail.isPftOnSchDate()) {
				totalPaid = totalPaid.add(schDetail.getSchdPriPaid().add(schDetail.getSchdPftPaid()));
				reallocationMap.put(schDetail.getSchDate(),
						schDetail.getPrincipalSchd().add(schDetail.getProfitSchd()));
			}
		}

		// fin excess amount
		BigDecimal excessBalAmt = getDeductedAmt(financeMain.getFinReference());

		// consider excess amount to calculate ODDays and DueBucket
		totalPaid = totalPaid.add(excessBalAmt);

		//reallocate and find the first due date.
		for (Entry<Date, BigDecimal> entry : reallocationMap.entrySet()) {
			if (totalPaid.compareTo(entry.getValue()) >= 0) {
				totalPaid = totalPaid.subtract(entry.getValue());
			} else {

				if (firstDuedate == null) {
					firstDuedate = entry.getKey();
				}
				netSchdDue = netSchdDue.add(entry.getValue().subtract(totalPaid));
				totalPaid = BigDecimal.ZERO;
				netSchdAmount = netSchdAmount.add(entry.getValue());
			}
		}

		if (firstDuedate != null) {
			newCurODDays = DateUtility.getDaysBetween(firstDuedate, valueDate);
		}

		// calculate DueBucket
		int newDueBucket = (new BigDecimal(newCurODDays).divide(new BigDecimal(30), 0, RoundingMode.UP)).intValue();

		// for due percentage calculation
		if (netSchdAmount.compareTo(BigDecimal.ZERO) > 0) {
			duePercentage = (netSchdDue.multiply(new BigDecimal(100))).divide(netSchdAmount, 2, RoundingMode.HALF_DOWN);
		}

		//get ignore bucket configuration from SMT parameter
		Object object = SysParamUtil.getValue(SMTParameterConstants.IGNORING_BUCKET);
		if (object != null) {
			minDuePerc = (BigDecimal) object;
		}

		if (duePercentage.compareTo(minDuePerc) <= 0) {
			newDueBucket = 0;
		}

		//No current OD Days and No change in the Bucket Status and Number of Buckets
		if (newCurODDays == 0 || newDueBucket == 0) {

			// No change in the Bucket Status and Number of Buckets
			if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket && curODDays == newCurODDays) {
				return false;
			} else {
				doWriteDPDBuketData(financeMain, finProfitDetail, newFinStatus, newDueBucket, newCurODDays);
				return true;
			}
		}

		// DPD Configuration
		List<DPDBucketConfiguration> list = getBucketConfigurations(financeMain.getFinCategory());
		sortBucketConfig(list);

		for (DPDBucketConfiguration dpdBucketConfiguration : list) {

			if (dpdBucketConfiguration.getDueDays() >= newDueBucket) {
				bucketID = dpdBucketConfiguration.getBucketID();
				break;
			}
		}

		// newFinStatus is BucketCode based on Bucket Configuration
		if (bucketID != 0) {
			newFinStatus = getBucket(bucketID);
		}

		//No change in the Bucket Status and Number of Buckets
		if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket && curODDays == newCurODDays) {
			return false;
		}

		doWriteDPDBuketData(financeMain, finProfitDetail, newFinStatus, newDueBucket, newCurODDays);

		logger.debug(" Leaving ");
		return true;
	}
	
	/**
	 * 
	 * @param financeMain
	 * @param finProfitDetail
	 * @param newFinStatus
	 * @param newDueBucket
	 * @param newCurODDays
	 */
	private void doWriteDPDBuketData(FinanceMain financeMain, FinanceProfitDetail finProfitDetail, String newFinStatus,
			int newDueBucket, int newCurODDays) {

		financeMain.setFinStatus(newFinStatus);
		financeMain.setDueBucket(newDueBucket);

		finProfitDetail.setFinStatus(newFinStatus);
		finProfitDetail.setDueBucket(newDueBucket);

		finProfitDetail.setActualODDays(finProfitDetail.getCurODDays());
		if (ImplementationConstants.VARTUAL_DPD) {
			finProfitDetail.setCurODDays(newCurODDays);
		}
	}
	
	/**
	 * @param finReference
	 * @return
	 */
	private BigDecimal getDeductedAmt(String finReference) {

		BigDecimal balanceAmt = BigDecimal.ZERO;
		List<FinExcessAmount> finExcessAmounts = finExcessAmountDAO.getExcessAmountsByRef(finReference);
		if (finExcessAmounts.size() > 0) {
			for (FinExcessAmount finExcessAmount : finExcessAmounts) {

				// DPD calculation considering both balance amount and reserved amount
				balanceAmt.add(finExcessAmount.getAmount().subtract(finExcessAmount.getUtilisedAmt()));
			}
		}
		return balanceAmt;
	}
}