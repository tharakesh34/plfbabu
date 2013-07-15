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
package com.pennant.backend.endofday.provision;

import java.lang.reflect.InvocationTargetException;
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
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class ProvisionCalculation implements Tasklet {
	
	private Logger logger = Logger.getLogger(ProvisionCalculation.class);
	
	private ProvisionCalculationUtil provisionCalculationUtil;
	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;
	
	
	private Date dateValueDate = null;
	
	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
		
		logger.debug("START: Provision Calculation for Value Date: "+ dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		selQuery = prepareOverDueSelectQuery(selQuery);
			
		try {
			
			// Fetch Overdue Fianances for Provision Calculation
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				
				Provision provision = prepareProvisionData(resultSet, false);
				
				//Call Method for Provision Calculation
				getProvisionCalculationUtil().processProvCalculations(provision, dateValueDate,
						false,false, resultSet.getBoolean("AllowRIAInvestment"));
				getBatchAdminDAO().saveStepDetails(provision.getFinReference(), getProvisions(provision), 
						context.getStepContext().getStepExecution().getId());
				context.getStepContext().getStepExecution().getExecutionContext().putInt("FIELD_COUNT", resultSet.getRow());
			}
			
			//Fetch Provisions records which were not in overdue but pending for provision recovery
			selQuery = prepareProvisionSelectQuery(new StringBuffer());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			
			while (resultSet.next()) {
				
				Provision provision = prepareProvisionData(resultSet, true);
				//Call Method for Provision Calculation
				getProvisionCalculationUtil().processProvCalculations(provision, dateValueDate, 
						true,false,resultSet.getBoolean("AllowRIAInvestment"));
				
				getBatchAdminDAO().saveStepDetails(provision.getFinReference(), getProvisions(provision), context.getStepContext().getStepExecution().getId());
				context.getStepContext().getStepExecution().getExecutionContext().putInt("FIELD_COUNT", resultSet.getRow());
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new SQLException(e.getMessage()) {};
		} catch (AccountNotFoundException e) {
			logger.error(e);
			throw new AccountNotFoundException(e.getMessage()) {};
		} catch (IllegalAccessException e) {
			logger.error(e);
			throw new IllegalAccessException(e.getMessage()) {};
		} catch (InvocationTargetException e) {
			logger.error(e);
			throw new InvocationTargetException(e , e.getMessage()) {};
		} finally {
			resultSet.close();
			sqlStatement.close();
		}
		
		logger.debug("COMPLETED: Provision Calculation for Value Date: "+ dateValueDate);
		return RepeatStatus.FINISHED;
	}
	


	/**
	 * Method for Preparation Of Provision Object data By ResultSet
	 * @param resultSet
	 * @param isProvCal
	 * @return
	 * @throws SQLException
	 */
	private Provision prepareProvisionData(ResultSet resultSet, boolean isProvCal) throws SQLException{
		
		Provision provision = new Provision();
		provision.setFinReference(resultSet.getString("FinReference"));
		provision.setFinBranch(resultSet.getString("FinBranch"));
		provision.setFinType(resultSet.getString("FinType"));
		provision.setCustID(resultSet.getLong("CustID"));
		
		if(!isProvCal){
			provision.setProvisionCalDate(resultSet.getDate("FinODSchdDate"));
		}else{
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
	 * Method for preparation of Select Query To get OverDue Details data
	 * @param selQuery
	 * @return
	 */
	private StringBuffer prepareOverDueSelectQuery(StringBuffer selQuery) {
		
		selQuery.append(" SELECT T1.FinReference , T1.FinBranch, T1.FinType, T1.CustID , " );
		selQuery.append(" T1.FinODSchdDate, T2.AllowRIAInvestment " );
		selQuery.append(" FROM FinODDetails AS T1 ");
		selQuery.append(" INNER JOIN RMTFinanceTypes AS T2 ON T1.FinType = T2.FinType ");
		selQuery.append(" WHERE T1.FinCurODDays > 0  AND " );
		selQuery.append(" T1.FinCurODDays = (SELECT Max(FinCurODDays) FROM FinODDetails T3 WHERE T3.FinReference = T1.FinReference) " );
		selQuery.append(" GROUP BY T1.FinReference, T1.FinBranch, T1.FinType, " );
		selQuery.append(" T1.CustID , T1.FinODSchdDate, T2.AllowRIAInvestment ");
		return selQuery;
		
	}
	
	/**
	 * Method for preparation of Select Query To get Provision Details data
	 * @param selQuery
	 * @return
	 */
	private StringBuffer prepareProvisionSelectQuery(StringBuffer selQuery) {
		
		selQuery.append(" SELECT T1.FinReference, T1.FinBranch, T1.FinType, T1.CustID, T1.ProvisionCalDate," );
		selQuery.append(" T1.ProvisionedAmt, T1.ProvisionAmtCal, T1.ProvisionDue, T2.AllowRIAInvestment, " );
		selQuery.append(" T1.NonFormulaProv, T1.UseNFProv, T1.AutoReleaseNFP " );
		selQuery.append(" FROM FinProvisions AS T1 " );
		selQuery.append(" INNER JOIN RMTFinanceTypes AS T2 ON T1.FinType = T2.FinType " );
		selQuery.append(" WHERE T1.ProvisionCalDate <> '"+dateValueDate+"' " );
		selQuery.append(" AND T1.ProvisionedAmt <> 0 AND T1.FinReference IN (  " );
		selQuery.append(" SELECT FinReference FROM FinODDetails WHERE FincurODDays = 0 " );
		selQuery.append(" EXCEPT SELECT FinReference FROM FinODDetails WHERE FincurODDays <> 0) " );
		return selQuery;
	
	}
	
	private String getProvisions(Provision provision) {
		StringBuffer strprovsn = new StringBuffer();

		if (provision != null) {
			strprovsn.append("FinBranch");
			strprovsn.append("-");
			strprovsn.append(provision.getFinBranch());
			strprovsn.append(";");
			
			strprovsn.append("FinType");
			strprovsn.append("-");
			strprovsn.append(provision.getFinType());
			strprovsn.append(";");
			
			strprovsn.append("CustID");
			strprovsn.append("-");
			strprovsn.append(provision.getLovDescCustCIF());
			strprovsn.append(";");
			
			strprovsn.append("ProvisionCalDate");
			strprovsn.append("-");
			strprovsn.append(DateUtility.formatUtilDate(provision.getProvisionCalDate(), PennantConstants.dateFormat));
			strprovsn.append(";");
			
			strprovsn.append("ProvisionCalDate");
			strprovsn.append("-");
			strprovsn.append(DateUtility.formatUtilDate(provision.getProvisionCalDate(), PennantConstants.dateFormat));
			strprovsn.append(";");
			
			strprovsn.append("ProvisionedAmt");
			strprovsn.append("-");
			strprovsn.append(provision.getProvisionedAmt()); //TODO AMTFORMART
			strprovsn.append(";");
			
			strprovsn.append("ProvisionAmtCal");
			strprovsn.append("-");
			strprovsn.append(provision.getProvisionAmtCal()); //TODO AMTFORMART
			strprovsn.append(";");
			
			strprovsn.append("ProvisionDue");
			strprovsn.append("-");
			strprovsn.append(provision.getProvisionDue()); //TODO AMTFORMART
			strprovsn.append(";");
			
			
			
		}
		return strprovsn.toString();

	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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

	public BatchAdminDAO getBatchAdminDAO() {
		return batchAdminDAO;
	}

	public void setBatchAdminDAO(BatchAdminDAO batchAdminDAO) {
		this.batchAdminDAO = batchAdminDAO;
	}
	
}
