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

import org.apache.log4j.Logger;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.eod.constants.EodSql;

public class AccrualService extends ServiceHelper {

	private static Logger		logger				= Logger.getLogger(AccrualService.class);
	private static final long	serialVersionUID	= 6161809223570900644L;

	public void processAccrual(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = connection.prepareStatement(EodSql.accrual);
			sqlStatement.setDate(1, DateUtility.getDBDate(date.toString()));
			sqlStatement.setLong(2, custId);
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				calculateAccruals(resultSet.getString("FinReference"), date);
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
	}

	public void calculateAccruals(String finReference, Date valueDate) {
		logger.debug(" Entering ");

		// get Finance main
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainForPftCalc(finReference);
		// get Schedule Details
		List<FinanceScheduleDetail> scheduleDetailList = getFinanceScheduleDetailDAO().getFinSchdDetailsForBatch(
				finReference);
		// call the Calculations
		Date dateCal = DateUtility.addDays(valueDate, 1);

		FinanceProfitDetail finPftDetail = AEAmounts.calProfitDetails(financeMain, scheduleDetailList, null, dateCal);
		finPftDetail.setFinStatus(financeMain.getFinStatus());
		finPftDetail.setFinStsReason(financeMain.getFinStsReason());
		finPftDetail.setFinIsActive(financeMain.isFinIsActive());
		finPftDetail.setClosingStatus(financeMain.getClosingStatus());
		String worstSts = getCustomerStatusCodeDAO().getFinanceStatus(finReference, false);
		finPftDetail.setFinWorstStatus(worstSts);

		getFinanceProfitDetailDAO().update(finPftDetail, false);
		logger.debug(" Leaving ");
	}

}
