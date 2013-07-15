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
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.OverDueRecoveryPostingsUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class OverDueRecoveryPostings implements Tasklet {
	
	private Logger logger = Logger.getLogger(OverDueRecoveryPostings.class);
	
	private OverdueChargeRecoveryDAO recoveryDAO;
	private OverDueRecoveryPostingsUtil recoveryPostingsUtil;
	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;
	
		
	private Date dateValueDate = null;

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		
		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
		
		logger.debug("START: OverDue Recovery Postings for Value Date: "+ dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);
		
		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		selQuery = prepareSelectQuery(selQuery);

		try {
			
			//Fetch OverDue Recoveries records data 
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			 			
			while (resultSet.next()) {
				
				//Check Pendings for Recovery
				BigDecimal pendingAmount = resultSet.getBigDecimal("FinODCPenalty")
						.subtract(resultSet.getBigDecimal("FinODCPaid"));
				
				if(pendingAmount.compareTo(new BigDecimal(0)) == 0){
					
					//Update Recovery Status to 'C'(Completed)
					doUpdateRecoveryData(resultSet,true, new BigDecimal(0),new BigDecimal(0), true);
					
				}else{
					
					//Update Recovery Data By Check FundingBalance Amount & Posting Status
					OverdueChargeRecovery chargeRecovery  = doUpdateRecoveryData(resultSet,false, new BigDecimal(0), new BigDecimal(0), false);
					
					List<Object> list = getRecoveryPostingsUtil().oDRPostingProcess(chargeRecovery, dateValueDate, 
							resultSet.getBoolean("AllowRIAInvestment"));
					BigDecimal penaltyPaid = (BigDecimal) list.get(0);
					BigDecimal waiverPaid = (BigDecimal) list.get(1);
					
					if(penaltyPaid.compareTo(new BigDecimal(-1)) != 0){
						doUpdateRecoveryData(resultSet,false, penaltyPaid, waiverPaid, true);
					}
					getBatchAdminDAO().saveStepDetails(resultSet.getString("FinReference"), getODChargeRecory(chargeRecovery.getFinODCPLShare(), penaltyPaid, waiverPaid), context.getStepContext().getStepExecution().getId());
					context.getStepContext().getStepExecution().getExecutionContext().putInt("FIELD_COUNT", resultSet.getRow());
				}
						
			}
			
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
		
		logger.debug("COMPLETED: OverDue Recovery Postings for Value Date: "+ dateValueDate);
		return RepeatStatus.FINISHED;
	}
	
	/**
	 * Method for get the List of OverDue Recoveries List
	 * @param selectSql
	 * @return
	 */
	private StringBuffer prepareSelectQuery(StringBuffer selectSql){
		
		selectSql = new StringBuffer(" SELECT T1.FinReference, T1.FinSchdDate, T1.FinODCPLShare," );
		selectSql.append(" T1.FinODFor, T1.FinBranch, T1.FinType, T1.FinCustID, T1.FinCcy, T1.FinODDate," );
		selectSql.append(" T1.FinODCSweep, T1.FinODCPenalty, T1.FinODCWaived, T1.FinODCPLPenalty, " );
		selectSql.append(" T1.FinODCCPenalty, T1.FinODCPaid, T1.FinODCWaiverPaid, T1.FinODCLastPaidDate, T2.AllowRIAInvestment " );
		selectSql.append(" FROM FinODCRecovery AS T1 " );
		selectSql.append(" INNER JOIN RMTFinanceTypes AS T2 ON T1.FinType = T2.FinType " );
		selectSql.append(" WHERE T1.FinODCRecoverySts = 'R'");
		return selectSql;
	}
	
	/**
	 * Prepare OverDue Recovery Data and update DB on Condition
	 * @param set
	 * @param isPaidClear
	 * @param penaltyPaid
	 * @param dbUpdate
	 * @return
	 */
	@SuppressWarnings("serial")
	private OverdueChargeRecovery doUpdateRecoveryData(ResultSet set, boolean isPaidClear,
			BigDecimal penaltyPaid, BigDecimal waiverPaid, boolean dbUpdate){
		logger.debug("Entering");
		
		OverdueChargeRecovery recovery = new OverdueChargeRecovery();
		try {
			recovery.setFinReference(set.getString("FinReference"));
			recovery.setFinSchdDate(set.getDate("FinSchdDate"));
			recovery.setFinODFor(set.getString("FinODFor"));
			recovery.setFinODCPaid(set.getBigDecimal("FinODCPaid"));
			recovery.setFinODCLastPaidDate(set.getDate("FinODCLastPaidDate"));
			recovery.setFinODCWaiverPaid(set.getBigDecimal("FinODCWaiverPaid"));
			recovery.setFinODCWaived(set.getBigDecimal("FinODCWaived"));
			
			if(!dbUpdate){
				
				//Used for Accounting Engine Postings
				recovery.setFinODCSweep(set.getBoolean("FinODCSweep"));
				recovery.setFinODCPenalty(set.getBigDecimal("FinODCPenalty"));
				recovery.setFinODCPLPenalty(set.getBigDecimal("FinODCPLPenalty"));
				recovery.setFinODCCPenalty(set.getBigDecimal("FinODCCPenalty"));
				recovery.setFinODCPLShare(set.getBigDecimal("FinODCPLShare"));
				
			}else{

				if(isPaidClear){
					recovery.setFinODCRecoverySts("C");
				}else{
					BigDecimal balance = set.getBigDecimal("FinODCPenalty").subtract(set.getBigDecimal("FinODCPaid"))
								.subtract(penaltyPaid);

					recovery.setFinODCPaid(set.getBigDecimal("FinODCPaid").add(penaltyPaid));
					recovery.setFinODCWaiverPaid(set.getBigDecimal("FinODCWaiverPaid").add(waiverPaid));
					recovery.setFinODCLastPaidDate(dateValueDate);
					if(balance.compareTo(new BigDecimal(0)) == 0){
						recovery.setFinODCRecoverySts("C");
					}else{
						recovery.setFinODCRecoverySts("R");
					}
				}
				
				getRecoveryDAO().update(recovery, "");
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new DataAccessException(e.getMessage()) { };
		}
		logger.debug("Leaving");
		return recovery;
	}
	
	private String getODChargeRecory(BigDecimal ODCPLShare, BigDecimal penaltyPaid, BigDecimal waiverPaid) {
		StringBuffer strodcr = new StringBuffer();
		
		strodcr.append("ODCPLShare");
		strodcr.append("-");
		strodcr.append(ODCPLShare);
		strodcr.append(";");

		strodcr.append("Penalty Paid");
		strodcr.append("-");
		strodcr.append(penaltyPaid);
		strodcr.append(";");

		strodcr.append("Waiver Paid");
		strodcr.append("-");			
		strodcr.append(waiverPaid);
		strodcr.append(";");
		
		
		
		
		return strodcr.toString();

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}
	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return recoveryDAO;
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

	public BatchAdminDAO getBatchAdminDAO() {
		return batchAdminDAO;
	}

	public void setBatchAdminDAO(BatchAdminDAO batchAdminDAO) {
		this.batchAdminDAO = batchAdminDAO;
	}
	
}
