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
 * FileName : ScheduleCalculator.java *
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;

public class FeeScheduleCalculator {
	private final static Logger	logger					= Logger.getLogger(FeeScheduleCalculator.class);

	private FinScheduleData		finScheduleData;

	public FeeScheduleCalculator() {

	}

	public static FinScheduleData getFeeScheduleDetails(FinScheduleData finScheduleData) {
		return new FeeScheduleCalculator(finScheduleData).getFinScheduleData();
	}

	// Constructors
	private FeeScheduleCalculator(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		setFinScheduleData(prepareFeeScheduleData(finScheduleData));
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
		
		List<FinFeeDetail> finFeeDetailList = finScheduleData.getFinFeeDetailList();
		List<FinFeeScheduleDetail> finFeeScheduleDetailList;
		Set<Date> feeScheduleDatesSet = new HashSet<Date>(); 
			
		if (finFeeDetailList != null && !finFeeDetailList.isEmpty()
				&& finScheduleData.getFinanceScheduleDetails() != null
				&& !finScheduleData.getFinanceScheduleDetails().isEmpty()) {
			FinFeeScheduleDetail finFeeScheduleDetail = null;

			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				finFeeScheduleDetailList = new ArrayList<FinFeeScheduleDetail>();
				if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)) {
					for (FinanceScheduleDetail finScheduleDetailTemp : finScheduleData.getFinanceScheduleDetails()) {
						if (!allowFeeToSchedule(finScheduleDetailTemp, finScheduleData)) {
							continue;
						}
												
						finFeeScheduleDetail = new FinFeeScheduleDetail();
						finFeeScheduleDetail.setFeeID(finFeeDetail.getFeeID());
						finFeeScheduleDetail.setSchDate(finScheduleDetailTemp.getSchDate());
						finFeeScheduleDetail.setSchAmount(finFeeDetail.getRemainingFee());
						finFeeScheduleDetailList.add(finFeeScheduleDetail);
						
						if (feeScheduleDatesSet.contains(finScheduleDetailTemp.getSchDate())) {
							finScheduleDetailTemp.setFeeSchd(finScheduleDetailTemp.getFeeSchd().add(
									finFeeDetail.getRemainingFee()));
						} else {
							finScheduleDetailTemp.setFeeSchd(finFeeDetail.getRemainingFee());
							feeScheduleDatesSet.add(finScheduleDetailTemp.getSchDate());
						}
						break;
					}
				} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)) {
					Map<Date, FinanceScheduleDetail> paymentDates = new HashMap<Date, FinanceScheduleDetail>();
					BigDecimal feeSchdAmt = BigDecimal.ZERO;
					BigDecimal totFeeSchdAmt = BigDecimal.ZERO;
						
					for (int i = 1; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
						FinanceScheduleDetail finScheduleDetailTemp = finScheduleData.getFinanceScheduleDetails().get(i);
						if (!allowFeeToSchedule(finScheduleDetailTemp, finScheduleData)) {
							continue;
						}
						paymentDates.put(finScheduleDetailTemp.getSchDate(), finScheduleDetailTemp);
					}

					// Payment Dates should never be Zero in the below condition and hence not handling Div/Zero
					feeSchdAmt = finFeeDetail.getRemainingFee().divide(new BigDecimal(paymentDates.size()), 0, RoundingMode.HALF_DOWN);
					int count = 0 ;
					for (FinanceScheduleDetail finScheduleDetail : paymentDates.values()) {
						count++;
						if(count == paymentDates.size()){
							feeSchdAmt = finFeeDetail.getRemainingFee().subtract(totFeeSchdAmt);
						}
						
						totFeeSchdAmt = totFeeSchdAmt.add(feeSchdAmt);
						finFeeScheduleDetail = new FinFeeScheduleDetail();
						finFeeScheduleDetail.setFeeID(finFeeDetail.getFeeID());
						finFeeScheduleDetail.setSchDate(finScheduleDetail.getSchDate());
						finFeeScheduleDetail.setSchAmount(feeSchdAmt);
						finFeeScheduleDetailList.add(finFeeScheduleDetail);
						
						if (feeScheduleDatesSet.contains(finScheduleDetail.getSchDate())) {
							finScheduleDetail.setFeeSchd(finScheduleDetail.getFeeSchd().add(feeSchdAmt));
						} else {
							finScheduleDetail.setFeeSchd(feeSchdAmt);
							feeScheduleDatesSet.add(finScheduleDetail.getSchDate());
						}
					}
				} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
					if (finFeeDetail.getTerms() == 0) {
						continue;
					}

					int count = 0;
					BigDecimal feeSchdAmt = BigDecimal.ZERO;
					feeSchdAmt = finFeeDetail.getRemainingFee().divide(new BigDecimal(finFeeDetail.getTerms()), 0, RoundingMode.HALF_DOWN);
					BigDecimal totFeeSchdAmt = BigDecimal.ZERO;
					
					for (FinanceScheduleDetail finScheduleDetail : finScheduleData.getFinanceScheduleDetails()) {
						if (count == finFeeDetail.getTerms()) {
							break;
						}
						if (!allowFeeToSchedule(finScheduleDetail, finScheduleData)) {
							continue;
						}
						if (count == finFeeDetail.getTerms() - 1) {
							feeSchdAmt = finFeeDetail.getRemainingFee().subtract(totFeeSchdAmt);
						}
						finFeeScheduleDetail = new FinFeeScheduleDetail();
						finFeeScheduleDetail.setFeeID(finFeeDetail.getFeeID());
						finFeeScheduleDetail.setSchDate(finScheduleDetail.getSchDate());
						finFeeScheduleDetail.setSchAmount(feeSchdAmt);
						totFeeSchdAmt = totFeeSchdAmt.add(feeSchdAmt);
						finFeeScheduleDetailList.add(finFeeScheduleDetail);

						if (feeScheduleDatesSet.contains(finScheduleDetail.getSchDate())) {
							finScheduleDetail.setFeeSchd(finScheduleDetail.getFeeSchd().add(feeSchdAmt));
						} else {
							finScheduleDetail.setFeeSchd(feeSchdAmt);
							feeScheduleDatesSet.add(finScheduleDetail.getSchDate());
						}
						count++;
					}
				}

				Comparator<FinFeeScheduleDetail> beanComp = new BeanComparator("schDate");
				Collections.sort(finFeeScheduleDetailList, beanComp);
				finFeeDetail.setFinFeeScheduleDetailList(finFeeScheduleDetailList);
			}
		}
		
		logger.debug("Leaving");
		
		return finScheduleData;
	}

	
	private boolean allowFeeToSchedule(FinanceScheduleDetail finScheduleDetail,FinScheduleData finScheduleDataTemp){
		/*if (finScheduleDetail.isDisbOnSchDate() || !finScheduleDetail.isRepayOnSchDate() || StringUtils.isNotEmpty(finScheduleDetail.getBpiOrHoliday()) || 
				finScheduleDetail.getSchDate().compareTo(finScheduleDataTemp.getFinanceMain().getGrcPeriodEndDate()) <= 0 ||
				finScheduleDetail.getSchDate().compareTo(finScheduleDataTemp.getFinanceMain().getCalMaturity()) > 0){
			return false;
		} else {
			return true;
		}*/
		
		if (finScheduleDetail.isDisbOnSchDate() || (StringUtils.isNotEmpty(finScheduleDetail.getBpiOrHoliday()) && 
				!StringUtils.equals(finScheduleDetail.getBpiOrHoliday(), FinanceConstants.FLAG_HOLDEMI))
				|| (!finScheduleDetail.isPftOnSchDate() && !finScheduleDetail.isRepayOnSchDate())) {
			return false;
		}

		return true;
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