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
 * FILE HEADER 																				*
 ******************************************************************************************** 
 * 
 * FileName : ScheduleCalculator.java 														*
 * 
 * Author : PENNANT TECHONOLOGIES 															*
 * 
 * Creation Date : 26-04-2011 																*
 * 
 * Modified Date : 30-07-2011 																*
 * 
 * Description : 																			*
 * 
 ******************************************************************************************** 
 * Date Author Version Comments 															*
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 																	*
 ******************************************************************************************** 
 */
package com.pennant.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;

public class FeeScheduleCalculator {
	private final static Logger logger = Logger.getLogger(FeeScheduleCalculator.class);

	private FinScheduleData finScheduleData;

	public FeeScheduleCalculator() {

	}

	public static FinScheduleData getFeeScheduleDetails(FinScheduleData finScheduleData) {
		return new FeeScheduleCalculator(finScheduleData).getFinScheduleData();
	}

	// Constructors
	private FeeScheduleCalculator(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		if (finScheduleData.getFinanceMain().isNew()
				|| StringUtils.equals(finScheduleData.getFinanceMain().getRecordType(),
						PennantConstants.RECORD_TYPE_NEW)) {
			setFinScheduleData(prepareFeeScheduleData(finScheduleData));
		} else {
			
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Schedule calculation to get the Total Desired Profit by including Planned Deferment Terms
	 * 
	 * @param finScheduleData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private FinScheduleData prepareFeeScheduleData(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinFeeDetail> finFeeDetails = finScheduleData.getFinFeeDetailList();
		List<FinFeeScheduleDetail> finFeeScheduleDetails;
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		FinFeeScheduleDetail finFeeScheduleDetail = null;

		// No Fees available
		if (finFeeDetails == null || finFeeDetails.isEmpty()) {
			return finScheduleData;
		}

		// Schedule Detail not available
		if (finSchdDetails == null || finSchdDetails.isEmpty()) {
			return finScheduleData;
		}

		// Find 1st Schedule Date, Last Schedule Date and Total Terms
		int reqTerms = 0;
		
		for (FinFeeDetail finFeeDetail : finFeeDetails) {
			
			if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
					CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)) {
				reqTerms = 1;
			} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
					CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
				reqTerms = finFeeDetail.getTerms();
			} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
					CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)) {
				reqTerms = finMain.getCalTerms();
			} else {
				continue;
			}
			
			int schTerms = 0;
			finFeeScheduleDetails = new ArrayList<FinFeeScheduleDetail>();
			BigDecimal totalFeeSchdAmt = BigDecimal.ZERO;
			BigDecimal feeSchdAmt = (finFeeDetail.getRemainingFee()).divide(new BigDecimal(reqTerms), 0, RoundingMode.HALF_DOWN);
			feeSchdAmt = CalculationUtil.roundAmount(feeSchdAmt, finMain.getCalRoundingMode(), finMain.getRoundingTarget());
			
			for (FinanceScheduleDetail curSchd : finSchdDetails) {
				if (StringUtils.isEmpty(curSchd.getBpiOrHoliday()) && curSchd.isRepayOnSchDate()) {
					schTerms = schTerms + 1;
					
					if (reqTerms == schTerms) {
						feeSchdAmt = finFeeDetail.getRemainingFee().subtract(totalFeeSchdAmt);
					}
					
					totalFeeSchdAmt = totalFeeSchdAmt.add(feeSchdAmt);

					finFeeScheduleDetail = new FinFeeScheduleDetail();
					finFeeScheduleDetail.setFeeID(finFeeDetail.getFeeID());
					finFeeScheduleDetail.setSchDate(curSchd.getSchDate());
					finFeeScheduleDetail.setSchAmount(feeSchdAmt);
					finFeeScheduleDetails.add(finFeeScheduleDetail);

					curSchd.setFeeSchd(curSchd.getFeeSchd().add(feeSchdAmt));

					if (reqTerms == schTerms) {
						break;
					}
				}
			}
			
			Comparator<FinFeeScheduleDetail> beanComp = new BeanComparator("schDate");
			Collections.sort(finFeeScheduleDetails, beanComp);
			finFeeDetail.setFinFeeScheduleDetailList(finFeeScheduleDetails);
		}
		
		logger.debug("Leaving");

		return finScheduleData;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

}