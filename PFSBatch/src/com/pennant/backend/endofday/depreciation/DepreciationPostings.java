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
 * FileName    		:  DepreciationPostings.java													*                           
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
package com.pennant.backend.endofday.depreciation;

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
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class DepreciationPostings implements Tasklet {

	private Logger logger = Logger.getLogger(DepreciationPostings.class);

	private PostingsPreparationUtil postingsPreparationUtil;
	private DataSource dataSource;

	private Date dateValueDate = null;
	private Date dateAppDate = null;
	
	int postings = 0;
	int processed = 0;
	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
		dateAppDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());

		logger.debug("START: Depreciation Postings Caluclation for Value Date: "+ dateValueDate);
		
		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		
		try {
			
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setString(1, "IJARAH");
			sqlStatement.setDate(2, DateUtility.getDBDate(DateUtility.getYearStartDate(dateValueDate).toString()));
			sqlStatement.setDate(3, DateUtility.getDBDate(dateValueDate.toString()));
			
			resultSet = sqlStatement.executeQuery();
			resultSet.next();			
			BatchUtil.setExecution(context,  "TOTAL", String.valueOf(resultSet.getInt(1)));
			
			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			sqlStatement.setString(2, "IJARAH");
			sqlStatement.setDate(3, DateUtility.getDBDate(DateUtility.getYearStartDate(dateValueDate).toString()));
			sqlStatement.setDate(4, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = sqlStatement.executeQuery();
		
			long linkedTranId = Long.MIN_VALUE;
			
			while (resultSet.next()) {

				//Amount Codes preparation using FinProfitDetails
				AEAmountCodes amountCodes = new AEAmountCodes();
				amountCodes.setFinReference(resultSet.getString("FinReference"));
				amountCodes.setElpDays(DateUtility.getDaysBetween(dateValueDate, resultSet.getDate("FinStartDate")));
				amountCodes.setElpMnts(DateUtility.getMonthsBetween(dateValueDate, resultSet.getDate("FinStartDate")));
				amountCodes.setElpTerms(resultSet.getInt("ElapsedTerms"));
				amountCodes.setTtlDays(DateUtility.getDaysBetween(resultSet.getDate("MaturityDate"), resultSet.getDate("FinStartDate")));
				amountCodes.setTtlMnts(DateUtility.getMonthsBetween(resultSet.getDate("MaturityDate"), resultSet.getDate("FinStartDate")));
				amountCodes.setTtlTerms(resultSet.getInt("NumberOfTerms") + resultSet.getInt("GraceTerms"));
				amountCodes.setAccumulatedDepPri(resultSet.getBigDecimal("AccumulatedDepPri"));
				amountCodes.setDepreciatePri(resultSet.getBigDecimal("DepreciatePri"));
				amountCodes.setPriAP(resultSet.getBigDecimal("TotalPriPaid"));
				amountCodes.setFinisActive(resultSet.getBoolean("FinIsActive"));
				
				//DataSet Object preparation for AccountingSet Execution
				DataSet dataSet = new DataSet();
				dataSet.setFinReference(resultSet.getString("FinReference"));
				dataSet.setFinEvent("DPRCIATE");
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
				dataSet.setNoOfTerms(resultSet.getInt("NumberOfTerms") + resultSet.getInt("GraceTerms"));
				dataSet.setNewRecord(false);

				//Postings Process
				List<Object> returnList = getPostingsPreparationUtil().processDepreciatePostings(dataSet, amountCodes, 
						resultSet.getBoolean("AllowRIAInvestment"),dateAppDate , linkedTranId);

				if(returnList!=null && !returnList.isEmpty()) {
					if((Boolean)returnList .get(0)) {
						postings++;
						linkedTranId = (Long) returnList.get(1);
					}
				}
				processed = resultSet.getRow();
				BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(processed));
				BatchUtil.setExecution(context,  "INFO", getInfo());
				
				returnList = null;
			}
			
			BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(processed));
			BatchUtil.setExecution(context,  "INFO", getInfo());

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
			if(resultSet != null) {
				resultSet.close();
			}
			if(sqlStatement != null) {
				sqlStatement.close();
			}
			
		}

		logger.debug("COMPLETE: Depreciation Postings Caluclation for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for get count of Schedule data
	 * @return selQuery 
	 */
	private String getCountQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT count(T1.FinReference)");
		selQuery.append(" FROM FinanceMain AS T1 INNER JOIN RMTFinanceTypes AS T3 ON T1.FinType=T3.FinType " );
		selQuery.append(" INNER JOIN FinPftDetails AS T4 ON T1.FinReference = T4.FinReference  " );
		selQuery.append(" WHERE ISNULL(T1.DepreciationFrq,'') <> '' AND T3.FinCategory = ? ");
		selQuery.append(" AND (T1.FinIsActive = 1 OR  (T1.FinIsActive = 0 AND T4.LatestRpyDate >= ? AND T4.LatestRpyDate <= ?)) " );
		selQuery.append(" AND ISNULL(T1.ClosingStatus , '') <> 'C'");
		return selQuery.toString();
	}
	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private String prepareSelectQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT T1.FinReference , T1.FinBranch, T1.FinType," );
		selQuery.append(" T1.FinCcy ,T1.NextRepayDate ,T1.CustID , T1.DisbAccountId ,T1.RepayAccountId ," );
		selQuery.append(" T1.FinAmount AS DisburseAmount , (T1.FinAmount - T1.FinRepaymentAmount) AS FinAmount ," );
		selQuery.append(" T1.DownPayment , T1.NumberOfTerms, T1.GraceTerms, " );
		selQuery.append(" T1.FinStartDate , T1.MaturityDate ,T1.FinAccount, T1.FinCustPftAccount , T1.FinIsActive, " );
		selQuery.append(" (SELECT COUNT(T2.FinReference) FROM FinScheduleDetails AS T2 WHERE " );
		selQuery.append(" T1.Finreference =T2.finreference " );
		selQuery.append(" AND (T2.RepayonSchdate='1' OR T2.DeferedPay= '1' OR (T2.PftOnSchDate = '1' AND T2.RepayAmount > 0)) and T2.DefSchdDate <= ?)  AS ElapsedTerms, " );
		selQuery.append(" T3.AllowRIAInvestment , T4.AccumulatedDepPri , T4.DepreciatePri, T4.TotalPriPaid " );
		selQuery.append(" FROM FinanceMain AS T1 INNER JOIN RMTFinanceTypes AS T3 ON T1.FinType=T3.FinType " );
		selQuery.append(" INNER JOIN FinPftDetails AS T4 ON T1.FinReference = T4.FinReference  " );
		selQuery.append(" WHERE ISNULL(T1.DepreciationFrq,'') <> '' AND T3.FinCategory = ? ");
		selQuery.append(" AND (T1.FinIsActive = 1 OR  (T1.FinIsActive = 0 AND T4.LatestRpyDate >= ? AND T4.LatestRpyDate <= ?)) ");
		selQuery.append(" AND ISNULL(T1.ClosingStatus , '') <> 'C'");
		return selQuery.toString();
	}
	
	private String getInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append("Total Depreciation Posting's").append(": ").append(postings);
		return builder.toString();
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
	
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
