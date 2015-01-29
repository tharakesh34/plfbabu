
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
 *//*

*//**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  OverDueRecoveryPostings.java													*                           
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
package com.pennant.backend.endofday.overduepayments;

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
import com.pennant.app.util.OverDueRecoveryPostingsUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayHierarchyConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;

/**
 * Process for Recover Penalty charges with Repayment's Hierarchy "IPCS" or "PICS"
 */
public class OverDueRecoveryPostings implements Tasklet {
	
	private Logger logger = Logger.getLogger(OverDueRecoveryPostings.class);
	
	private FinanceMainDAO financeMainDAO;
	private OverDueRecoveryPostingsUtil recoveryPostingsUtil;
	private DataSource dataSource;
	
		
	private Date dateValueDate = null;

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		
		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
		
		logger.debug("START: OverDue Recovery Postings for Value Date: "+ dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);
		
		if(PennantConstants.REPAY_HIERARCHY_METHOD.equals(RepayHierarchyConstants.REPAY_HIERARCHY_IPCS)
				|| PennantConstants.REPAY_HIERARCHY_METHOD.equals(RepayHierarchyConstants.REPAY_HIERARCHY_PICS)){

			// READ REPAYMENTS DUE TILL TODAY
			Connection connection = null;
			ResultSet resultSet = null;
			PreparedStatement sqlStatement = null;
			StringBuffer selQuery = new StringBuffer();
			selQuery = prepareSelectQuery(selQuery);
			FinanceMain financeMain = null;

			try {

				//Fetch OverDue Recoveries records data 
				connection = DataSourceUtils.doGetConnection(getDataSource());
				sqlStatement = connection.prepareStatement(selQuery.toString());
				resultSet = sqlStatement.executeQuery();

				while (resultSet.next()) {

					// Finance Main Object Fetching
					String finReference = resultSet.getString("FinReference");
					financeMain = getFinanceMainDAO().getFinanceMainForBatch(finReference);

					//Recovery Record Postings Details				
					getRecoveryPostingsUtil().recoveryPayment(financeMain, dateValueDate, 
							resultSet.getDate("FinODSchdDate"), resultSet.getString("FinODFor"), resultSet.getDate("MovementDate"),
							resultSet.getBigDecimal("PenaltyBal"), resultSet.getBigDecimal("PenaltyPaid"), 
							BigDecimal.ZERO, resultSet.getString("PenaltyType"), resultSet.getBoolean("AllowRIAInvestment"), 
							Long.MIN_VALUE, resultSet.getString("FinDivision"), true);

				}

				//Method for Processing Finances Set to Inactive State , 
				//If Finance Payment are fully Paid including Penalty Amount )if there any)
				sqlStatement = connection.prepareStatement(prepareupdateQuery(new StringBuffer()).toString());
				sqlStatement.executeUpdate();

			}catch (SQLException e) {
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
			}finally {
				resultSet.close();
				sqlStatement.close();
			}

		}
		
		logger.debug("COMPLETED: OverDue Recovery Postings for Value Date: "+ dateValueDate);
		return RepeatStatus.FINISHED;
	}
	
	/**
	 * Method for get the List of OverDue Recoveries List
	 * @param selectSql
	 * @return
	 */
	private StringBuffer prepareSelectQuery(StringBuffer selectSql){
		
		selectSql = new StringBuffer(" SELECT T1.FinReference, T1.FinODSchdDate,T1.FinODFor,T1.MovementDate,  ");
		selectSql.append(" T1.PenaltyBal, T1.PenaltyPaid, T1.PenaltyType, T3.AllowRIAInvestment, T3.FinDivision ");
		selectSql.append(" FROM FinODCRecovery_AMView AS T1 INNER JOIN FinanceMain AS T2 ON T1.FinReference = T2.FinReference ");
		selectSql.append(" INNER JOIN RMTFinanceTypes AS T3 ON T2.FinType = T3.FinType ");
		selectSql.append(" WHERE T1.FinReference not in (Select Distinct FinReference from FInODDetails WHERE FinCurODAmt!=0)  ");
		selectSql.append(" AND T1.PenaltyBal > 0 AND T1.RcdCanDel = 0 AND ISNULL(T2.ClosingStatus,'') != 'C' AND ");
		selectSql.append(" T2.FinRepayMethod = 'AUTO' AND T2.RepayAccountId !='' ORDER BY T1.FinODSchdDate, T1.FinODFor, T1.SeqNo ");
		return selectSql;
	}
	
	/**
	 * Method for Update Finances which are Preferable to InActive Status 
	 * @param selectSql
	 * @return
	 */
	private StringBuffer prepareupdateQuery(StringBuffer selectSql){
		
		selectSql = new StringBuffer(" Update FinanceMain Set FinIsActive = 0 , ClosingStatus = 'M' ");
		if(PennantConstants.REPAY_HIERARCHY_METHOD.equals(RepayHierarchyConstants.REPAY_HIERARCHY_IPCS)) {
		
			selectSql.append(" WHERE (FinAmount + FeeChargeAmt - DownPayment = FinRepaymentAmount) ");
			selectSql.append(" AND FinIsActive = 1 AND ISNULL(ClosingStatus,'') != 'C' ");
			selectSql.append(" AND FinReference NOT IN(SELECT Distinct FinReference FROM FinODDetails WHERE TotPenaltyBal != 0) ");
	
		}else if(PennantConstants.REPAY_HIERARCHY_METHOD.equals(RepayHierarchyConstants.REPAY_HIERARCHY_PICS)) {
			
			selectSql.append(" FROM  (Select FinReference,SUM((ProfitSchd-SchdPftPaid)+(DefProfitSchd-DefSchdPftPaid)) SumPft ");
			selectSql.append(" FROM FinScheduleDetails Group by FinReference) AS T2 WHERE FinanceMain.FinReference =T2.FinReference ");
			selectSql.append(" AND (FinAmount + FeeChargeAmt - DownPayment = FinRepaymentAmount) AND SumPft = 0 ");
			selectSql.append(" AND FinIsActive = 1 AND ISNULL(ClosingStatus,'') != 'C' ");
			selectSql.append(" AND FinanceMain.FinReference NOT IN(SELECT DISTINCT FinReference FROM FInODDetails WHERE TotPenaltyBal!=0) ");
			
		}
		return selectSql;
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

	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
		this.recoveryPostingsUtil = recoveryPostingsUtil;
	}
	public OverDueRecoveryPostingsUtil getRecoveryPostingsUtil() {
		return recoveryPostingsUtil;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}

}
