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
 * FileName : OverDueRecoveryPostingsUtil.java *
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public class AccrualService extends ServiceHelper {

	private static Logger		logger				= Logger.getLogger(AccrualService.class);
	private static final long	serialVersionUID	= 6161809223570900644L;

	//P.AcrTillLBD, P.PftAmzSusp,P.AmzTillLBD, P.FirstODDate, P.PrvODDate
	//INNER JOIN FinPftDetails P ON F.FinReference = P.FinReference
	public static final String	accrual				= "SELECT F.FinReference FROM "
															+ "FinanceMain F  WHERE F.FinIsActive = 1  AND F.FinStartDate <=? And F.CustID=? ";

	public void processAccrual(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";
		
		try {
			sqlStatement = connection.prepareStatement(accrual);
			sqlStatement.setDate(1, DateUtility.getDBDate(date.toString()));
			sqlStatement.setLong(2, custId);
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				finreference = resultSet.getString("FinReference");
				calculateAccruals(finreference, date);
			}
		} catch (Exception e) {
			logger.error("Exception: Finreference :" + finreference, e);
			throw new Exception("Exception: Finreference :" + finreference,  e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
	}

	public void calculateAccruals(String finReference, Date valueDate) throws Exception {
		logger.debug(" Entering ");

		// get Finance main
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainForPftCalc(finReference);
		// get Schedule Details
		List<FinanceScheduleDetail> scheduleDetailList = getFinanceScheduleDetailDAO().getFinSchdDetailsForBatch(
				finReference);

		//FIXME: REMOVE THIS CODE AFTER 
		if (StringUtils.equals(finReference, "WB1703200727") || StringUtils.equals(finReference, "WB1703200499")
				|| StringUtils.equals(finReference, "PB1703201087") || StringUtils.equals(finReference, "PB1703201067")) {

			logger.debug(" DEBUG Starts for ACCRUAL");

		}

		FinanceProfitDetail profitDetail = getFinanceProfitDetailDAO().getFinPftDetailForBatch(finReference);

		// call the Calculations
		Date dateCal = DateUtility.addDays(valueDate, 1);

		FinanceProfitDetail finPftDetail = AEAmounts.calProfitDetails(financeMain, scheduleDetailList, profitDetail,
				dateCal);
		String worstSts = getCustomerStatusCodeDAO().getFinanceStatus(finReference, false);
		finPftDetail.setFinWorstStatus(worstSts);
		getFinanceProfitDetailDAO().update(finPftDetail, false);

		//post accruals
		postAccruals(financeMain, finPftDetail, valueDate);
		logger.debug(" Leaving ");
	}

	/**
	 * @param resultSet
	 * @throws Exception
	 */
	public void postAccruals(FinanceMain financeMain, FinanceProfitDetail finPftDetail, Date valueDate)
			throws Exception {
		logger.debug(" Entering ");

		String finref = financeMain.getFinReference();
		AEAmountCodes amountCodes = AEAmounts.procCalAEAmounts(financeMain, finPftDetail, valueDate);
		amountCodes.setFinReference(finref);

		String eventCode = AccountEventConstants.ACCEVENT_AMZ;
		if (finPftDetail.isPftInSusp()) {
			eventCode = AccountEventConstants.ACCEVENT_AMZSUSP;
		}

		DataSet dataSet = AEAmounts.createDataSet(financeMain, eventCode, valueDate, valueDate);

		//Postings Process
		FinanceType financeType = getFinanceType(financeMain.getFinType());
		List<ReturnDataSet> list = prepareAccounting(dataSet, amountCodes, financeType);
		saveAccounting(list);
		//posting done update the accrual balance
		finPftDetail.setAmzTillLBD(finPftDetail.getPftAmz());
		finPftDetail.setAmzTillLBDNormal(finPftDetail.getPftAmzNormal());
		finPftDetail.setAmzTillLBDPD(finPftDetail.getPftAmzPD());
		finPftDetail.setAmzTillLBDPIS(finPftDetail.getPftAmzSusp());
		finPftDetail.setAcrTillLBD(finPftDetail.getPftAccrued());
		finPftDetail.setAcrSuspTillLBD(finPftDetail.getPftAccrueSusp());

		boolean isMonthEnd = false;

		if (DateUtility.getDay(valueDate) == 1) {
			isMonthEnd = true;
		}

		getFinanceProfitDetailDAO().updateLBDAccruals(finPftDetail, isMonthEnd);
		logger.debug(" Leaving ");
	}

}
