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
 * FileName    		:  ProgressClaimAmountPostings.java										*                           
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
package com.pennant.backend.endofday.progressclaim;

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

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.finance.FinBillingDetailDAO;
import com.pennant.backend.model.finance.FinBillingDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class ProgressClaimAmountPostings implements Tasklet {

	private Logger logger = Logger.getLogger(ProgressClaimAmountPostings.class);

	private PostingsPreparationUtil postingsPreparationUtil;
	private FinBillingDetailDAO finBillingDetailDAO;
	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;

	private Date dateValueDate = null;
	private Date dateAppDate = null;

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
		dateAppDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString());
	
		logger.debug("START: Progree Claim Postings for Value Date: "+ dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(
				context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		selQuery = prepareSelectQuery(selQuery);

		try {
			
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			
			while (resultSet.next()) {

				//Amount Codes preparation using FinProfitDetails
				AEAmountCodes amountCodes = new AEAmountCodes();
				amountCodes.setFinReference(resultSet.getString("FinReference"));
				amountCodes.setPft(resultSet.getBigDecimal("TotalProfit"));
				amountCodes.setPri(resultSet.getBigDecimal("FinAmount"));
				amountCodes.setCLAIMAMT(resultSet.getBigDecimal("ProgClaimAmount"));
				amountCodes.setDEFFEREDCOST(resultSet.getBigDecimal("PreContrOrDeffCost"));

				//DataSet Object preparation for AccountingSet Execution
				DataSet dataSet = new DataSet();
				dataSet.setFinReference(resultSet.getString("FinReference"));
				dataSet.setFinEvent("PRGCLAIM");
				dataSet.setFinBranch(resultSet.getString("FinBranch"));
				dataSet.setFinCcy(resultSet.getString("FinCcy"));
				dataSet.setCustId(resultSet.getLong("CustId"));
				dataSet.setPostDate(dateAppDate);
				dataSet.setValueDate(dateValueDate);
				dataSet.setSchdDate(resultSet.getDate("ProgClaimDate"));
				dataSet.setFinType(resultSet.getString("FinType"));
				dataSet.setFinAmount(resultSet.getBigDecimal("FinAmount"));
				dataSet.setNewRecord(false);

				//Postings Preparation for Progress Claims
				List<Object> returnlist = getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, true,
						false, "Y", dateAppDate, null, false);
				
				//Update Bill Retained for Progress Claim Date for particular Finance
				FinBillingDetail detail = null;
				if(returnlist != null && ((Boolean) returnlist.get(0))){
					detail = new FinBillingDetail(resultSet.getString("FinReference"));
					detail.setProgClaimDate(resultSet.getDate("ProgClaimDate"));
					detail.setProgClaimBilled(true);
					
					getFinBillingDetailDAO().updateClaim(detail, "");
				}

				getBatchAdminDAO().saveStepDetails(dataSet.getFinReference(), getProgressClaim(resultSet), context.getStepContext().getStepExecution().getId());
				context.getStepContext().getStepExecution().getExecutionContext().putInt("FIELD_COUNT", resultSet.getRow());
			}
			
		} catch (AccountNotFoundException e) {
			logger.error(e);
			throw new AccountNotFoundException(e.getMessage()) {};
		} catch (SQLException e) {
			logger.error(e);
			throw new SQLException(e.getMessage()) {};
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

		logger.debug("COMPLETE: Progress Claim Postings for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for preparation of Select Query To get Progress Claim data
	 * 
	 * @param selQuery
	 * @return
	 */
	private StringBuffer prepareSelectQuery(StringBuffer selQuery) {
		
		selQuery.append(" SELECT T1.FinReference , T3.CustId, T3.FinType, T3.FinBranch, T3.FinCcy , T3.FinAmount, T1.ProgClaimDate, " );
		selQuery.append(" T1.ProgClaimAmount , T2.PreContrOrDeffCost, (T3.TotalGrossPft + T3.TotalGrossGrcPft) AS TotalProfit  " );
		selQuery.append(" FROM FinBillingDetail T1 INNER JOIN " );
		selQuery.append(" FinBillingHeader T2 on T2.FinReference = T1.FinReference INNER JOIN  " );
		selQuery.append(" FinanceMain T3 on T3.FinReference = T1.FinReference " );
		selQuery.append(" WHERE T1.ProgClaimDate <='"+  dateValueDate +"' " );
		selQuery.append(" AND T1.ProgClaimBilled = 0 AND T2.AutoAcClaimDate = 1 ");
		return selQuery;
		
	}
	
	private String getProgressClaim(ResultSet resultSet) throws SQLException {
		StringBuffer strodcr = new StringBuffer();
		
		if(resultSet != null) {
		
			strodcr.append("FinBranch");
			strodcr.append("-");
			strodcr.append(resultSet.getString("FinBranch"));
			strodcr.append(";");
			
			strodcr.append("FinType");
			strodcr.append("-");
			strodcr.append(resultSet.getString("FinType"));
			strodcr.append(";");
			
			strodcr.append("CustId");
			strodcr.append("-");
			strodcr.append(resultSet.getLong("CustId"));
			strodcr.append(";");
			
			strodcr.append("PRI");
			strodcr.append("-");
			strodcr.append(resultSet.getBigDecimal("FinAmount"));
			strodcr.append(";");
			
			strodcr.append("PFT");
			strodcr.append("-");
			strodcr.append(resultSet.getBigDecimal("TotalProfit"));
			strodcr.append(";");
			
			strodcr.append("ProgClaimDate");
			strodcr.append("-");
			strodcr.append(resultSet.getDate("ProgClaimDate"));
			strodcr.append(";");
			
			strodcr.append("ProgClaimAmount");
			strodcr.append("-");
			strodcr.append(resultSet.getBigDecimal("ProgClaimAmount"));
			strodcr.append(";");
			
			strodcr.append("PreContrOrDeffCost");
			strodcr.append("-");
			strodcr.append(resultSet.getBigDecimal("PreContrOrDeffCost"));
			strodcr.append(";");
		}
		
		return strodcr.toString();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}
	
	public FinBillingDetailDAO getFinBillingDetailDAO() {
		return finBillingDetailDAO;
	}
	public void setFinBillingDetailDAO(FinBillingDetailDAO finBillingDetailDAO) {
		this.finBillingDetailDAO = finBillingDetailDAO;
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