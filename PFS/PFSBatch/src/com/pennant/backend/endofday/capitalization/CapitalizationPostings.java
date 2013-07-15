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
 * FileName    		:  CapitalizationPostings.java													*                           
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
package com.pennant.backend.endofday.capitalization;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
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
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SuspensePostingUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class CapitalizationPostings implements Tasklet {

	private Logger logger = Logger.getLogger(CapitalizationPostings.class);

	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private SuspensePostingUtil suspensePostingUtil;
	
	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;
	

	private Date dateValueDate = null;
	private Date dateAppDate = null;

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
		dateAppDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString());

		logger.debug("START: Capitalization Postings for Value Date: "+ dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);
		

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		selQuery = prepareSelectQuery(selQuery);

		FinanceProfitDetail pftDetail = null;

		try {
			
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			
			while (resultSet.next()) {

				pftDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(resultSet.getString("FinReference"));

				//Amount Codes preparation using FinProfitDetails
				AEAmountCodes amountCodes = new AEAmountCodes();
				amountCodes.setFinReference(resultSet.getString("FinReference"));
				amountCodes.setCpzCur(resultSet.getBigDecimal("CpzAmount"));
				amountCodes.setCpzPrv(resultSet.getBigDecimal("PrvCpzAmt") == null ? 
						new BigDecimal(0) : resultSet.getBigDecimal("PrvCpzAmt"));
				amountCodes.setCpzTot(resultSet.getBigDecimal("TotalCpz"));
				amountCodes.setCpzNxt(amountCodes.getCpzTot().subtract(amountCodes.getCpzPrv())
						.subtract(amountCodes.getCpzCur()));

				// +++++Accounting Set Execution for Amortization+++++++//

				//DataSet Object preparation for AccountingSet Execution
				DataSet dataSet = new DataSet();
				dataSet.setFinReference(resultSet.getString("FinReference"));
				dataSet.setFinEvent("COMPOUND");
				dataSet.setFinBranch(resultSet.getString("FinBranch"));
				dataSet.setFinCcy(resultSet.getString("FinCcy"));
				dataSet.setPostDate(dateAppDate);
				dataSet.setValueDate(dateValueDate);
				dataSet.setSchdDate(resultSet.getDate("NextRepayDate"));
				dataSet.setFinType(resultSet.getString("FinType"));
				dataSet.setCustId(resultSet.getLong("CustID"));
				dataSet.setDisburseAccount(resultSet.getString("DisbAccountId"));
				dataSet.setRepayAccount(resultSet.getString("RepayAccountId"));
				dataSet.setFinAccount(resultSet.getString("FinAccount"));
				dataSet.setFinCustPftAccount(resultSet.getString("FinCustPftAccount"));
				dataSet.setFinAmount(resultSet.getBigDecimal("FinAmount"));
				dataSet.setDisburseAmount(resultSet.getBigDecimal("DisburseAmount"));
				dataSet.setDownPayment(resultSet.getBigDecimal("DownPayment"));
				dataSet.setNoOfTerms(resultSet.getInt("NumberOfTerms"));
				dataSet.setNewRecord(false);

				//Postings Process
				getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, true,
						resultSet.getBoolean("AllowRIAInvestment"),"Y", dateAppDate, null,false);
				
				//Update Finance Profit Details
				pftDetail.setTdPftCpz(resultSet.getBigDecimal("CpzAmount").add(resultSet.getBigDecimal("PrvCpzAmt")));
				pftDetail.setLastMdfDate(dateValueDate);
				getFinanceProfitDetailDAO().update(pftDetail);
				
				//Capitalization Relase from Suspense
				amountCodes.setSUSPRLS(resultSet.getBigDecimal("CpzAmount"));
				dataSet.setFinEvent("M_AMZ");
				getSuspensePostingUtil().capitalizationSuspRelease(dataSet, amountCodes, true,
						resultSet.getBoolean("AllowRIAInvestment"));
				
				getBatchAdminDAO().saveStepDetails(dataSet.getFinReference(), getCapitalization(dataSet), context.getStepContext().getStepExecution().getId());
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
			pftDetail = null;
			resultSet.close();
			sqlStatement.close();
		}

		logger.debug("COMPLETE: Capitalization Postings for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private StringBuffer prepareSelectQuery(StringBuffer selQuery) {
		
		selQuery.append(" SELECT T1.FinReference, T1.FinBranch, T1.FinType, T1.FinAccount, T1.FinCustPftAccount," );
		selQuery.append(" T1.FinCcy ,T1.NextRepayDate ,T1.CustID , T1.DisbAccountId , T1.RepayAccountId ,");
		selQuery.append(" T2.CpzAmount, T1.FinAmount AS DisburseAmount , (T1.FinAmount - T1.FinRepaymentAmount) AS FinAmount ," );
		selQuery.append(" T1.DownPayment , T1.NumberOfTerms ,T1.TotalCpz ," );
		selQuery.append(" (SELECT SUM(CpzAmount) AS TotCurCpzAmt FROM  FinScheduleDetails " );
		selQuery.append(" WHERE DefSchdDate < '" + dateValueDate + "' ) AS PrvCpzAmt, T3.AllowRIAInvestment ");
		selQuery.append(" FROM FinanceMain AS T1 INNER JOIN RMTFinanceTypes AS T3  " );
		selQuery.append(" ON T1.FinType = T3.FinType , FinScheduleDetails AS T2 " );
		selQuery.append(" WHERE T1.FinReference = T2.FinReference " );
		selQuery.append(" AND T2.DefSchdDate = '" + dateValueDate + "'" );
		selQuery.append(" AND T2.CpzOnSchDate= '1' AND T2.CpzAmount > '0' ");
		selQuery.append(" AND T1.FinIsActive = '1' ");
		return selQuery;
		
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	private String getCapitalization(DataSet dataSet) {
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

			strprovsn.append("FinAmount");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinAmount());
			strprovsn.append(";");

			strprovsn.append("FinCustPftAccount");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinCustPftAccount());
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

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}
	public void setFinanceProfitDetailDAO(
			FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}
	
	public SuspensePostingUtil getSuspensePostingUtil() {
		return suspensePostingUtil;
	}
	public void setSuspensePostingUtil(SuspensePostingUtil suspensePostingUtil) {
		this.suspensePostingUtil = suspensePostingUtil;
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