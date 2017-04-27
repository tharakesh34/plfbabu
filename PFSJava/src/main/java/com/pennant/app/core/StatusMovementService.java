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
 *
 * FileName    		:  RepaymentService.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.BatchUtil;

public class StatusMovementService extends ServiceHelper {

	private static final long	serialVersionUID	= 4165353615228874397L;
	private static Logger		logger				= Logger.getLogger(StatusMovementService.class);

	static final String			NORM_PD				= "select * from FinPftDetails where CurODDays=1 and PrvODDate = ? and  CustId = ?";
	static final String			PD_NORM				= " select * from (select FinReference, SUM(FinCurODAmt) FinCurODAmt,MAX(FinODTillDate) FinODTillDate  from FInODDetails group by FinReference)t "
															+ "	inner join FinPftDetails fpd on fpd.FinReference=t.FinReference  where FinODTillDate=? and fpd.CurODDays=0 and fpd.CustId = ?";
	static final String			PD_PIS				= "select * from (select FinReference from FinSuspHead where FinIsInSusp=1 and FinSuspTrfDate= ?) t"
															+ "	inner join FinPftDetails fpd on fpd.FinReference=t.FinReference and fpd.CustId = ?";

	/**
	 * @param custid
	 * @param valueDate
	 * @throws Exception
	 */
	public void processMovements(Connection connection, long custId, Date date) throws Exception {
		try {
			if (connection == null) {
				connection = DataSourceUtils.doGetConnection(getDataSource());
			}
			processMovement(connection, date, custId, AccountEventConstants.ACCEVENT_NORM_PD, NORM_PD);
			processMovement(connection, date, custId, AccountEventConstants.ACCEVENT_PD_NORM, PD_NORM);
			processMovement(connection, date, custId, AccountEventConstants.ACCEVENT_PD_PIS, PD_PIS);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
	}

	public void processMovement(ChunkContext context, Connection connection, String sql, String event, Date valueDate,
			int processed) throws Exception {

		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.prepareStatement(sql);
			statement.setDate(1, DateUtility.getDBDate(valueDate.toString()));
			resultSet = statement.executeQuery();

			while (resultSet.next()) {

				processPostings(resultSet, event, valueDate);

				processed++;
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {

			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}
		}

	}

	/**
	 * @param connection
	 * @param valueDate
	 * @param custId
	 * @throws Exception
	 */
	private void processMovement(Connection connection, Date valueDate, long custId, String event, String sql)
			throws Exception {

		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.prepareStatement(sql);
			statement.setDate(1, DateUtility.getDBDate(valueDate.toString()));
			statement.setLong(2, custId);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				processPostings(resultSet, event, valueDate);

			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
		}

	}

	private void processPostings(ResultSet resultSet, String event, Date valueDate) throws Exception {
		// Amount Codes preparation using FinProfitDetails
		AEAmountCodes amountCodes = getAEAmountCodes(resultSet, event,  valueDate);
		FinanceType financeType = getFinanceType(amountCodes.getFinType());
		HashMap<String, Object> executingMap = amountCodes.getDeclaredFieldValues();

		List<ReturnDataSet> list = prepareAccounting(executingMap, financeType);
		saveAccounting(list);
	}

	private AEAmountCodes getAEAmountCodes(ResultSet resultSet, String event, Date valueDate) throws SQLException {
		AEAmountCodes amountCodes = new AEAmountCodes();
		amountCodes.setFinReference(resultSet.getString("FinReference"));
		amountCodes.setFinType(resultSet.getString("FinType").trim());

		amountCodes.setFinEvent(event);
		amountCodes.setBranch(resultSet.getString("FinBranch"));
		amountCodes.setCcy(resultSet.getString("FinCcy"));
		amountCodes.setPostDate(DateUtility.getSysDate());
		amountCodes.setSchdDate(resultSet.getDate("NextRpySchDate"));
		amountCodes.setCustID(resultSet.getLong("CustID"));
		

		//FIXME: PV: 14APR17 change as per new finpftdetails
		amountCodes.setDAccrue(resultSet.getBigDecimal(""));
		amountCodes.setPft(resultSet.getBigDecimal("TotalPftSchd").add(resultSet.getBigDecimal("TotalPftCpz")));
		amountCodes.setPftAB(resultSet.getBigDecimal("TotalPftBal"));
		amountCodes.setPftAP(resultSet.getBigDecimal("TotalPftPaid"));
		amountCodes.setPftS(resultSet.getBigDecimal("TdSchdPft").add(resultSet.getBigDecimal("TdPftCpz")));
		amountCodes.setPftSB(resultSet.getBigDecimal("TdSchdPftBal"));
		amountCodes.setPftSP(resultSet.getBigDecimal("TdSchdPftPaid"));
		amountCodes.setAccrueTsfd(resultSet.getBigDecimal("pftAccrued").subtract(
				resultSet.getBigDecimal("PftAccrueSusp")));// Distributed
		amountCodes.setValueDate(valueDate);
		amountCodes.setSchdDate(valueDate);
		return amountCodes;
	}

	
	@SuppressWarnings("unused")
	private String getPSIToNormal() {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" select * from (select FinReference from FinSuspHead where FinIsInSusp=0 and FinSuspTrfDate= ?) t  ");
		sqlQuery.append(" inner join FinPftDetails fpd on fpd.FinReference=t.FinReference  ");
		return sqlQuery.toString();
	}

}
