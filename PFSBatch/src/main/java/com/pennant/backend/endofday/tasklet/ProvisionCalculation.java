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
/*

 *//**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  ProvisionCalculation.java													*                           
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
package com.pennant.backend.endofday.tasklet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ProvisionCalculationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.util.EODProperties;

public class ProvisionCalculation implements Tasklet {
	private Logger						logger			= Logger.getLogger(ProvisionCalculation.class);

	private ProvisionCalculationUtil	provisionCalculationUtil;
	private DataSource					dataSource;

	private Date						dateValueDate	= null;

	int									overDues		= 0;
	int									provisions		= 0;
	int									processed		= 0;
	String								alwAutoProv;

	public ProvisionCalculation() {
		//
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		dateValueDate = DateUtility.getValueDate();

		logger.debug("START: Provision Calculation for Value Date: " + dateValueDate);

		//Process for Provision Calcualtion IF only Bank Allowed for ALLOWED AUTO PROVISION
		alwAutoProv = SysParamUtil.getValueAsString("ALW_PROV_EOD");
		if ("Y".equals(alwAutoProv)) {

			// READ REPAYMENTS DUE TODAY
			Connection connection = null;
			ResultSet resultSet = null;
			PreparedStatement sqlStatement = null;

			FinanceType financeType = null;

			try {

				// Fetch Overdue Finances for Provision Calculation
				connection = DataSourceUtils.doGetConnection(getDataSource());

				sqlStatement = connection.prepareStatement(getOverDueCountQuery());
				resultSet = sqlStatement.executeQuery();
				resultSet.next();
				overDues = resultSet.getInt(1);
				resultSet.close();
				sqlStatement.close();
				sqlStatement = connection.prepareStatement(getCountQuery());
				sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
				resultSet = sqlStatement.executeQuery();
				resultSet.next();
				provisions = resultSet.getInt(1);

				BatchUtil.setExecution(context, "TOTAL", String.valueOf(overDues + provisions));

				sqlStatement = connection.prepareStatement(prepareOverDueSelectQuery());
				resultSet = sqlStatement.executeQuery();

				while (resultSet.next()) {
					financeType = EODProperties.getFinanceType(resultSet.getString("FinType").trim());
					Provision provision = prepareProvisionData(resultSet, false);

					//Call Method for Provision Calculation
					getProvisionCalculationUtil()
							.processProvCalculations(provision, dateValueDate, false, false, false);

					processed = resultSet.getRow();
					BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));
					BatchUtil.setExecution(context, "INFO", getInfo());
				}

				//Fetch Provisions records which were not in overdue but pending for provision recovery
				sqlStatement = connection.prepareStatement(prepareProvisionSelectQuery());
				sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
				resultSet = sqlStatement.executeQuery();

				while (resultSet.next()) {
					financeType = EODProperties.getFinanceType(resultSet.getString("FinType").trim());
					Provision provision = prepareProvisionData(resultSet, true);

					//Call Method for Provision Calculation
					getProvisionCalculationUtil().processProvCalculations(provision, dateValueDate, true, false, false);

					processed = processed + resultSet.getRow();
					BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));
					BatchUtil.setExecution(context, "INFO", getInfo());
				}

				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));
				BatchUtil.setExecution(context, "INFO", getInfo());

			} catch (Exception e) {
				logger.error("Exception: ", e);
				throw e;
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
				if (sqlStatement != null) {
					sqlStatement.close();
				}
			}
		} else {
			BatchUtil.setExecution(context, "PROCESSED", String.valueOf("0"));
			BatchUtil.setExecution(context, "TOTAL", String.valueOf("0"));
			BatchUtil.setExecution(context, "INFO", getInfo());
		}

		logger.debug("COMPLETED: Provision Calculation for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for Preparation Of Provision Object data By ResultSet
	 * 
	 * @param resultSet
	 * @param isProvCal
	 * @return
	 * @throws SQLException
	 */
	private Provision prepareProvisionData(ResultSet resultSet, boolean isProvCal) throws SQLException {

		Provision provision = new Provision();
		provision.setFinReference(resultSet.getString("FinReference"));
		provision.setFinBranch(resultSet.getString("FinBranch"));
		provision.setFinType(resultSet.getString("FinType"));
		provision.setCustID(resultSet.getLong("CustID"));

		if (!isProvCal) {
			provision.setProvisionCalDate(resultSet.getDate("FinODSchdDate"));
		} else {
			provision.setProvisionCalDate(resultSet.getDate("ProvisionCalDate"));
			provision.setProvisionAmtCal(resultSet.getBigDecimal("ProvisionAmtCal"));
			provision.setProvisionedAmt(resultSet.getBigDecimal("ProvisionedAmt"));
			provision.setProvisionDue(resultSet.getBigDecimal("ProvisionDue"));
			provision.setNonFormulaProv(resultSet.getBigDecimal("NonFormulaProv"));
			provision.setUseNFProv(resultSet.getBoolean("UseNFProv"));
			provision.setAutoReleaseNFP(resultSet.getBoolean("AutoReleaseNFP"));
		}

		return provision;
	}

	/**
	 * Method for get count of Provisions
	 * 
	 * @return selQuery
	 */
	private String getOverDueCountQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT count(1)");
		selQuery.append(" FROM FinODDetails AS T1 ");
		selQuery.append(" WHERE T1.FinCurODAmt > 0  AND ");
		selQuery.append(" T1.FinCurODDays = (SELECT Max(FinCurODDays) FROM FinODDetails T3 WHERE T3.FinReference = T1.FinReference) ");
		return selQuery.toString();

	}

	/**
	 * Method for preparation of Select Query To get OverDue Details data
	 * 
	 * @param selQuery
	 * @return
	 */
	private String prepareOverDueSelectQuery() {

		StringBuilder selQuery = new StringBuilder(
				" SELECT T1.FinReference , T1.FinBranch, T1.FinType, T1.CustID,  T1.FinODSchdDate ");
		selQuery.append(" FROM FinODDetails AS T1 ");
		selQuery.append(" WHERE T1.FinCurODAmt > 0  AND ");
		selQuery.append(" T1.FinCurODDays = (SELECT Max(FinCurODDays) FROM FinODDetails T3 WHERE T3.FinReference = T1.FinReference) ");
		selQuery.append(" GROUP BY T1.FinReference, T1.FinBranch, T1.FinType, ");
		selQuery.append(" T1.CustID , T1.FinODSchdDate");
		return selQuery.toString();

	}

	/**
	 * Method for preparation of Select Query To get Provision Details data
	 * 
	 * @param selQuery
	 * @return
	 */
	private String getCountQuery() {

		StringBuilder selQuery = new StringBuilder();
		selQuery.append(" SELECT count(1) ");
		selQuery.append(" FROM FinProvisions AS T1 ");
		selQuery.append(" WHERE T1.ProvisionCalDate <> ? ");
		selQuery.append(" AND T1.ProvisionedAmt <> 0 AND T1.FinReference IN (  ");
		selQuery.append(" SELECT FinReference FROM FinODDetails WHERE FincurODDays = 0 ");
		selQuery.append(" EXCEPT SELECT FinReference FROM FinODDetails WHERE FincurODDays <> 0) ");
		return selQuery.toString();

	}

	/**
	 * Method for preparation of Select Query To get Provision Details data
	 * 
	 * @param selQuery
	 * @return
	 */
	private String prepareProvisionSelectQuery() {

		StringBuilder selQuery = new StringBuilder();
		selQuery.append(" SELECT T1.FinReference, T1.FinBranch, T1.FinType, T1.CustID, T1.ProvisionCalDate,");
		selQuery.append(" T1.ProvisionedAmt, T1.ProvisionAmtCal, T1.ProvisionDue,");
		selQuery.append(" T1.NonFormulaProv, T1.UseNFProv, T1.AutoReleaseNFP ");
		selQuery.append(" FROM FinProvisions AS T1 ");
		selQuery.append(" WHERE T1.ProvisionCalDate <> ? ");
		selQuery.append(" AND T1.ProvisionedAmt <> 0 AND T1.FinReference IN (  ");
		selQuery.append(" SELECT FinReference FROM FinODDetails WHERE FincurODDays = 0 ");
		selQuery.append(" EXCEPT SELECT FinReference FROM FinODDetails WHERE FincurODDays <> 0) ");
		return selQuery.toString();

	}

	private String getInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append("Allow Auto Provision Calculation").append(": ").append(alwAutoProv);
		builder.append("\n");
		builder.append("Total Overdue's").append(": ").append(overDues);
		builder.append("\n");
		builder.append("Total Provision's").append(": ").append(provisions);

		return builder.toString();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setProvisionCalculationUtil(ProvisionCalculationUtil provisionCalculationUtil) {
		this.provisionCalculationUtil = provisionCalculationUtil;
	}

	public ProvisionCalculationUtil getProvisionCalculationUtil() {
		return provisionCalculationUtil;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

}
