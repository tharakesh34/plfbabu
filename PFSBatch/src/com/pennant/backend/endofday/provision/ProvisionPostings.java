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
 * FileName    		:  ProvisionPostings.java													*                           
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
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class ProvisionPostings  implements Tasklet {
	
	private Logger logger = Logger.getLogger(ProvisionPostings.class);
	
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;
	
	
	private Date dateValueDate = null;
	private Date dateAppDate = null;

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		
		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
		dateAppDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString());

		logger.debug("START: Provision Postings for Value Date: "+ dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);
		
		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		selQuery = prepareProvMovementSelectQuery(selQuery);
		
		try {
			
			// Fetch Overdue Fianances for Provision Calculation
			connection = DataSourceUtils.doGetConnection(getDataSource());	
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			
			FinanceMain financeMain = null;
			List<FinanceScheduleDetail> schdDetails = null;
			FinanceProfitDetail pftDetail = null;

			while (resultSet.next()) {
				
				ProvisionMovement movement = prepareProvisionMovementData(resultSet);
				
				financeMain = getFinanceMainDAO().getFinanceMainForDataSet(resultSet.getString("FinReference"));
				schdDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(resultSet.getString("FinReference"), "", false);
				pftDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(resultSet.getString("FinReference"));
				
				AEAmounts aeAmounts = new AEAmounts();
				AEAmountCodes amountCodes = aeAmounts.procAEAmounts(financeMain, schdDetails, pftDetail, dateValueDate);
				DataSet dataSet = aeAmounts.createDataSet(financeMain, "PROVSN", dateValueDate, resultSet.getDate("DueFromDate"));
				dataSet.setNewRecord(false);
				amountCodes.setPROVDUE(movement.getProvisionDue());
				
				//Provision Posting Process
				getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, true,
						resultSet.getBoolean("AllowRIAInvestment"), "Y", dateAppDate, movement, true);
				
				getBatchAdminDAO().saveStepDetails(dataSet.getFinReference(), getProvisions(dataSet), context.getStepContext().getStepExecution().getId());
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
			throw new InvocationTargetException(e, e.getMessage()) {};
		} finally {
			resultSet.close();
			sqlStatement.close();
		}
		
		logger.debug("COMPLETED: Provision Postings for Value Date: "+ dateValueDate);
		return RepeatStatus.FINISHED;
	}

	

	/**
	 * Method for Preparation for Provision Movement Details
	 * @param provision
	 * @param valueDate
	 * @throws SQLException 
	 */
	private ProvisionMovement prepareProvisionMovementData(ResultSet resultSet) throws SQLException{
		
		ProvisionMovement movement = new ProvisionMovement();
		movement.setFinReference(resultSet.getString("FinReference"));
		movement.setProvMovementDate(resultSet.getDate("ProvMovementDate"));
		movement.setProvMovementSeq(resultSet.getInt("ProvMovementSeq"));
		movement.setDueFromDate(resultSet.getDate("DueFromDate"));
		movement.setProvisionDue(resultSet.getBigDecimal("ProvisionDue"));
		movement.setProvisionedAmt(resultSet.getBigDecimal("ProvisionedAmt"));
		return movement;
		
	}
	/**
	 * Method for preparation of Select Query To get Provision Details data
	 * @param selQuery
	 * @return
	 */
	private StringBuffer prepareProvMovementSelectQuery(StringBuffer selQuery) {
		
		selQuery.append(" SELECT T1.FinReference,T1.ProvMovementDate,T1.ProvMovementSeq , T1.DueFromDate, ");
		selQuery.append(" T1.ProvisionedAmt, T1.ProvisionDue, T3.AllowRIAInvestment " );
		selQuery.append(" FROM FinProvMovements AS T1 " );
		selQuery.append(" INNER JOIN FinanceMain AS T2 ON T1.FinReference = T2.FinReference " );
		selQuery.append(" INNER JOIN RMTFinanceTypes AS T3 ON T2.FinType = T3.FinType " );
		selQuery.append(" WHERE T1.ProvisionPostSts = 'R'" );
		return selQuery;
		
	}
	
	private String getProvisions(DataSet dataSet) {
		StringBuffer strprovsn = new StringBuffer();

		if (dataSet != null) {
			strprovsn.append("FinBranch");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinBranch());
			strprovsn.append(";");

			strprovsn.append("PostDate");
			strprovsn.append("-");
			strprovsn.append(DateUtility.formatUtilDate(dataSet.getPostDate(), PennantConstants.dateFormat));
			strprovsn.append(";");

			strprovsn.append("ValueDate");
			strprovsn.append("-");
			strprovsn.append(DateUtility.formatUtilDate(dataSet.getValueDate(), PennantConstants.dateFormat));
			strprovsn.append(";");

			strprovsn.append("SchdDate");
			strprovsn.append("-");
			strprovsn.append(DateUtility.formatUtilDate(dataSet.getSchdDate(), PennantConstants.dateFormat));
			strprovsn.append(";");

			strprovsn.append("FinType");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinType());
			strprovsn.append(";");
			
			strprovsn.append("FinCcy");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinCcy());
			strprovsn.append(";");

			strprovsn.append("DisburseAccount");
			strprovsn.append("-");
			strprovsn.append(dataSet.getDisburseAccount());
			strprovsn.append(";");

			strprovsn.append("RepayAccount");
			strprovsn.append("-");
			strprovsn.append(dataSet.getRepayAccount());
			strprovsn.append(";");

			strprovsn.append("FinAccount");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinAccount());
			strprovsn.append(";");

			strprovsn.append("FinCustPftAccount");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinCustPftAccount());
			strprovsn.append(";");

			strprovsn.append("FinAmount");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinAmount());
			strprovsn.append(";");

			strprovsn.append("NewRecord");
			strprovsn.append("-");
			strprovsn.append(dataSet.isNewRecord());
			strprovsn.append(";");

			strprovsn.append("DownPayment");
			strprovsn.append("-");
			strprovsn.append(dataSet.getDownPayment()); //TODO AMTFORMART
			strprovsn.append(";");

			strprovsn.append("NoOfTerms");
			strprovsn.append("-");
			strprovsn.append(dataSet.getNoOfTerms());
			strprovsn.append(";");



		}

		return strprovsn.toString();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
	
	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
	
	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}
	public void setFinanceProfitDetailDAO(
			FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}
	
	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}
	public void setPostingsPreparationUtil(
			PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
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
