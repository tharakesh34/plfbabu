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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.SMTParameterConstants;

public class LatePayBuketService extends ServiceHelper {

	private static final long	serialVersionUID	= 6161809223570900644L;
	private static Logger		logger				= Logger.getLogger(LatePayBuketService.class);

	/**
	 * Default constructor
	 */
	public LatePayBuketService() {
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
			boolean isFinStsChanged = updateDPDBuketing(finEODEvent.getFinanceMain(), finEODEvent.getFinProfitDetail(),
					valueDate);

			if (isFinStsChanged) {
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("FinStatus");
				finEODEvent.addToFinMianUpdate("DueBucket");
				finEODEvent.getFinProfitDetail().setFinStatus(finEODEvent.getFinanceMain().getFinStatus());
				finEODEvent.getFinProfitDetail().setDueBucket(finEODEvent.getFinanceMain().getDueBucket());
			}
		}

		logger.debug(" Leaving ");
		return custEODEvent;
	}

	/**
	 * @param connection
	 * @param custId
	 * @param valueDate
	 * @throws Exception
	 */
	public boolean updateDPDBuketing(FinanceMain financeMain, FinanceProfitDetail pftDetail, Date valueDate) {

		List<FinanceScheduleDetail> scheduleDetails = new ArrayList<FinanceScheduleDetail>();

		for (FinanceScheduleDetail scheduleDetail : scheduleDetails) {

			if (scheduleDetail.getSchDate().compareTo(valueDate) > 0) {
				break;
			}

			if (scheduleDetail.isRepayOnSchDate() || scheduleDetail.isPftOnSchDate()) {

			}
		}

		int dueDays = pftDetail.getCurODDays();
		int newDueBucket = (new BigDecimal(dueDays).divide(new BigDecimal(30), 0, RoundingMode.UP)).intValue();
		int dueBucket = financeMain.getDueBucket();
		BigDecimal minDuePerc = BigDecimal.ZERO;

		String newFinStatus = FinanceConstants.FINSTSRSN_SYSTEM;
		String finStatus = StringUtils.trimToEmpty(financeMain.getFinStatus());
		String productCode = pftDetail.getFinCategory();

		BigDecimal duePercentage = BigDecimal.ZERO;

		//No current OD Days and No change in the Bucket Status and Number of Buckets
		if (pftDetail.getCurODDays() == 0) {
			if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket) {
				return false;
			}
		}

		//No current OD Buckets and No change in the Bucket Status and Number of Buckets
		if (newDueBucket == 0) {
			if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket) {
				return false;
			}
		}

		BigDecimal netSchdAmount = pftDetail.getTdSchdPri().add(pftDetail.getTdSchdPft());
		BigDecimal netDueAmount = netSchdAmount.subtract(pftDetail.getTdSchdPriPaid())
				.subtract(pftDetail.getTdSchdPftPaid()).subtract(pftDetail.getEmiInAdvanceBal())
				.subtract(pftDetail.getExcessAmtBal());

		if (netSchdAmount.compareTo(BigDecimal.ZERO) > 0) {
			duePercentage = (netDueAmount.divide(netSchdAmount, 0, RoundingMode.HALF_DOWN))
					.multiply(new BigDecimal(100));
		}

		//get ignore bucket configuration from SMT parameter
		Object object = SysParamUtil.getValue(SMTParameterConstants.IGNORING_BUCKET);
		if (object != null) {
			minDuePerc = (BigDecimal) object;
		}

		if (duePercentage.compareTo(minDuePerc) <= 0) {
			newDueBucket = 0;
		}

		//No change in the Bucket Status and Number of Buckets
		if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket) {
			return false;
		}

		long bucketID = 0;
		List<DPDBucketConfiguration> list = getBucketConfigurations(productCode);
		sortBucketConfig(list);
		for (DPDBucketConfiguration dpdBucketConfiguration : list) {

			if (dpdBucketConfiguration.getDueDays() > newDueBucket) {
				break;
			}

			bucketID = dpdBucketConfiguration.getBucketID();
		}

		if (bucketID != 0) {
			newFinStatus = getBucket(bucketID);
		}

		if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket) {
			return false;
		}

		financeMain.setFinStatus(newFinStatus);
		financeMain.setDueBucket(newDueBucket);

		return true;
	}

}