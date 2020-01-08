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
 *																							*
 * FileName    		:  AccrualService.java                                                  * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.util.DateUtil;

public class AccrualReversalService extends ServiceHelper {
	private static final long serialVersionUID = 6161809223570900644L;
	private static Logger logger = Logger.getLogger(AccrualReversalService.class);

	public CustEODEvent processAccrual(CustEODEvent custEODEvent) throws Exception {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {
			if(finEODEvent.getAccruedAmount().compareTo(BigDecimal.ZERO) == 0){
				continue;
			}
			finEODEvent = calculateAccruals(finEODEvent, custEODEvent);
		}
		return custEODEvent;
	}

	public FinEODEvent calculateAccruals(FinEODEvent finEODEvent, CustEODEvent custEODEvent) throws Exception {

		//Post Accruals on Application Extended Month End OR Application Month End OR Daily
		int amzPostingEvent = SysParamUtil.getValueAsInt(AccountConstants.AMZ_POSTING_EVENT);
		boolean isAmzPostToday = false;
		if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_MTH_END) {
			if (DateUtil.compare(custEODEvent.getEodDate(), DateUtil.getMonthEnd(custEODEvent.getEodDate())) == 0) {
				isAmzPostToday = true;
			}
		} else if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_EXT_MTH_END) {
			if (getEodConfig() != null && getEodConfig().isInExtMnth()) {
				if (DateUtil.compare(getEodConfig().getMnthExtTo(), custEODEvent.getEodDate()) == 0) {
					isAmzPostToday = true;
				}
			}

		} else {
			isAmzPostToday = true;
		}

		if (isAmzPostToday) {
			postMonthEndReversals(finEODEvent, custEODEvent);
		}

		return finEODEvent;
	}

	/**
	 * Method for Reversing the Accruals happened at Month end using Amortization Event
	 * @param financeMain
	 * @param resultSet
	 * @throws Exception
	 */
	public void postMonthEndReversals(FinEODEvent finEODEvent, CustEODEvent custEODEvent) throws Exception {
		String eventCode = AccountEventConstants.ACCEVENT_AMZ_MON;
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();
		FinanceMain main = finEODEvent.getFinanceMain();

		long accountingID = getAccountingID(main, eventCode);
		if (accountingID == Long.MIN_VALUE) {
			logger.debug(" Leaving. Accounting Not Found");
			return;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(finPftDetail, finEODEvent.getFinanceScheduleDetails(), eventCode,
				custEODEvent.getEodValueDate(), custEODEvent.getEodValueDate());

		// Y - Accrual Effective Post Date will be Value Date, N - Accrual Effective Post Date will be APP Date
		String acc_eff_postDate = SysParamUtil.getValueAsString(SMTParameterConstants.ACCREV_EFF_POSTDATE);

		if (StringUtils.equals(acc_eff_postDate, "N")) {
			aeEvent.setPostDate(SysParamUtil.getPostDate());
		}

		AEAmountCodes aeAmountCodes = aeEvent.getAeAmountCodes();
		aeAmountCodes.setdAmz(finEODEvent.getAccruedAmount());
		aeEvent.setDataMap(aeAmountCodes.getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());

		//Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);

		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		finEODEvent.setUpdLBDPostings(true);

		//posting done update the accrual balance
		finPftDetail.setAmzTillLBD(finPftDetail.getPftAmz().subtract(finEODEvent.getAccruedAmount()));
	}

}
