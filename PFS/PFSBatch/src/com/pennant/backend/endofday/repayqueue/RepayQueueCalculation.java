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
 * FileName    		:  RepayQueueCalculation.java													*                           
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
package com.pennant.backend.endofday.repayqueue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.util.PennantConstants;

public class RepayQueueCalculation implements Tasklet {
	
	private Logger logger = Logger.getLogger(RepayQueueCalculation.class);

	private FinanceRepayPriorityDAO financeRepayPriorityDAO;
	private FinRepayQueueDAO finRepayQueueDAO;
	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;
	

	private FinRepayQueue finRepayQueue;
	private Date dateValueDate = null;
	private Map<String, Integer> priorityMap = null;
	private BigDecimal zeroValue = BigDecimal.ZERO;

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());

		logger.debug("START: Repayments Queue Today Queuing for Value Date: " + dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		// FETCH Finance type Repayment Priorities
		ArrayList<ValueLabel> finRpyPriorities = new ArrayList<ValueLabel>(getFinanceRepayPriorityDAO()
				.getFinanceRepayPriorities(""));

		priorityMap = new HashMap<String, Integer>();
		for (int i = 0; i < finRpyPriorities.size(); i++) {
			priorityMap.put(finRpyPriorities.get(i).getLabel(),Integer.parseInt(finRpyPriorities.get(i).getValue()));
		}

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

				if (resultSet.getBigDecimal("DefProfitbal").compareTo(zeroValue) > 0
						|| resultSet.getBigDecimal("DefPrincipalBal").compareTo(zeroValue) > 0) {
					finRepayQueue = doWriteDataToBean(resultSet, PennantConstants.DEFERED);
					finRepayQueueDAO.setFinRepayQueueRecords(finRepayQueue, "");
				}

				if (resultSet.getBigDecimal("SchdPftBal").compareTo(zeroValue) > 0 
						|| resultSet.getBigDecimal("SchdPriBal").compareTo(zeroValue) > 0) {
					finRepayQueue = doWriteDataToBean(resultSet, PennantConstants.SCHEDULE);
					finRepayQueueDAO.setFinRepayQueueRecords(finRepayQueue, "");
				}
				
				getBatchAdminDAO().saveStepDetails(resultSet.getString("FinReference"), getRepayQueue(finRepayQueue), context.getStepContext().getStepExecution().getId());
				context.getStepContext().getStepExecution().getExecutionContext().putInt("FIELD_COUNT", resultSet.getRow());

			}
		} catch (SQLException e) {
			logger.error(e);
			throw new SQLException(e.getMessage()) {};
		} finally {
			finRepayQueue = null;
			priorityMap = null;
			resultSet.close();
			sqlStatement.close();
		}
		logger.debug("COMPLETE: Repayments Queue Today Queuing for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private StringBuffer prepareSelectQuery(StringBuffer selQuery) {
		
		selQuery.append(" SELECT FinanceMain.FinReference, FinanceMain.FinBranch, FinanceMain.FinType ,FinanceMain.CustID , ");
		selQuery.append(" DefSchdDate, DefProfitSchd ,DefPrincipalSchd,ProfitSchd,PrincipalSchd,DefSchdPftPaid,DefSchdPriPaid,");
		selQuery.append(" SchdPftpaid,SchdPriPaid, (ProfitSchd - SchdPftPaid) As SchdPftBal, ");
		selQuery.append(" (PrincipalSchd - SchdPriPaid) As SchdPriBal,");
		selQuery.append(" (DefPrincipalSchd - DefSchdPriPaid) As DefPrincipalBal, ");
		selQuery.append(" (DefProfitSchd - DefSchdPftPaid) As DefProfitbal ");
		selQuery.append(" FROM FinanceMain, FinScheduleDetails ");
		selQuery.append(" WHERE FinanceMain.FinReference = FinScheduleDetails.FinReference");
		selQuery.append(" AND DefSchdDate <= '" + dateValueDate + "'");
		selQuery.append(" AND RepayOnSchDate = '1'");
		selQuery.append(" AND (PrincipalSchd <> SchdPriPaid OR ProfitSchd <> SchdPftPaid OR DefPrincipalSchd <> DefSchdPriPaid OR DefProfitSchd <> DefSchdPftPaid)");
		return selQuery;
		
	}

	/**
	 * Method for Creating RepayQueue Object using resultSet
	 * 
	 * @param resultSet
	 * @return
	 */
	@SuppressWarnings("serial")
	private FinRepayQueue doWriteDataToBean(ResultSet resultSet, String rpyFor) {
		logger.debug("Entering");
		
		finRepayQueue = new FinRepayQueue();

		try {
			
			finRepayQueue.setFinReference(resultSet.getString("FinReference"));
			finRepayQueue.setBranch(resultSet.getString("FinBranch"));
			finRepayQueue.setFinType(resultSet.getString("FinType"));
			finRepayQueue.setCustomerID(resultSet.getLong("CustID"));
			finRepayQueue.setRpyDate(resultSet.getDate("DefSchdDate"));
			finRepayQueue.setFinRpyFor(rpyFor);

			if (priorityMap.containsKey(finRepayQueue.getFinType())) {
				finRepayQueue.setFinPriority(priorityMap.get(finRepayQueue.getFinType()));
			} else {
				finRepayQueue.setFinPriority(9999);
			}

			if (rpyFor.equals(PennantConstants.DEFERED)) {
				finRepayQueue.setSchdPft(resultSet.getBigDecimal("DefProfitSchd"));
				finRepayQueue.setSchdPri(resultSet.getBigDecimal("DefPrincipalSchd"));
				finRepayQueue.setSchdPftPaid(resultSet.getBigDecimal("DefSchdPftPaid"));
				finRepayQueue.setSchdPriPaid(resultSet.getBigDecimal("DefSchdPriPaid"));
				finRepayQueue.setSchdPftBal(resultSet.getBigDecimal("DefProfitbal"));
				finRepayQueue.setSchdPriBal(resultSet.getBigDecimal("DefPrincipalBal"));

			} else {
				finRepayQueue.setSchdPft(resultSet.getBigDecimal("ProfitSchd"));
				finRepayQueue.setSchdPri(resultSet.getBigDecimal("PrincipalSchd"));
				finRepayQueue.setSchdPftPaid(resultSet.getBigDecimal("SchdPftpaid"));
				finRepayQueue.setSchdPriPaid(resultSet.getBigDecimal("SchdPriPaid"));
				finRepayQueue.setSchdPftBal(resultSet.getBigDecimal("SchdPftBal"));
				finRepayQueue.setSchdPriBal(resultSet.getBigDecimal("SchdPriBal"));
			}

			if (finRepayQueue.getSchdPftBal().compareTo(new BigDecimal(0)) == 0) {
				finRepayQueue.setSchdIsPftPaid(true);
			} else {
				finRepayQueue.setSchdIsPftPaid(false);
			}

			if (finRepayQueue.isSchdIsPftPaid() && 
					finRepayQueue.getSchdPriBal().compareTo(new BigDecimal(0)) == 0) {
				finRepayQueue.setSchdIsPriPaid(true);
			} else {
				finRepayQueue.setSchdIsPriPaid(false);
			}

		} catch (SQLException e) {
			logger.error(e);
			throw new DataAccessException(e.getMessage()) { };
		}
		logger.debug("Leaving");
		return finRepayQueue;
	}
	
	private String getRepayQueue(FinRepayQueue repayQueue) {
		StringBuffer strRepayQueue = new StringBuffer();


		if (strRepayQueue != null) {
			strRepayQueue.append("RpyDate");
			strRepayQueue.append("-");
			strRepayQueue.append(DateUtility.formatUtilDate(repayQueue.getRpyDate(), PennantConstants.dateFormat));
			strRepayQueue.append(";");

			strRepayQueue.append("FinRpyFor");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getFinRpyFor());
			strRepayQueue.append(";");
			
			strRepayQueue.append("FinPriority");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getFinPriority());
			strRepayQueue.append(";");

			strRepayQueue.append("FinType");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getFinType());
			strRepayQueue.append(";");
			
						
			strRepayQueue.append("FinBranch");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getBranch());
			strRepayQueue.append(";");
				
			strRepayQueue.append("SchdPft");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getSchdPft()); //TODO AMTFORMART
			strRepayQueue.append(";");
			
			strRepayQueue.append("SchdPri");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getSchdPri()); //TODO AMTFORMART
			strRepayQueue.append(";");

			strRepayQueue.append("SchdPftPaid");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getSchdPftPaid()); //TODO AMTFORMART
			strRepayQueue.append(";");
			
			strRepayQueue.append("SchdPriPaid");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getSchdPriPaid()); //TODO AMTFORMART
			strRepayQueue.append(";");
			
			strRepayQueue.append("SchdPftBal");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getSchdPftBal()); //TODO AMTFORMART
			strRepayQueue.append(";");
			
			strRepayQueue.append("SchdPriBal");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getSchdPriBal()); //TODO AMTFORMART
			strRepayQueue.append(";");
			
			strRepayQueue.append("SchdIsPftPaid");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.isSchdIsPftPaid());
			strRepayQueue.append(";");

			strRepayQueue.append("RefundAmount");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getRefundAmount()); //TODO AMTFORMART
			strRepayQueue.append(";");
			
			strRepayQueue.append("RcdNotExist");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.isRcdNotExist()); 
			strRepayQueue.append(";");
	
		}

		return strRepayQueue.toString();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinanceRepayPriorityDAO(FinanceRepayPriorityDAO financeRepayPriorityDAO) {
		this.financeRepayPriorityDAO = financeRepayPriorityDAO;
	}
	public FinanceRepayPriorityDAO getFinanceRepayPriorityDAO() {
		return financeRepayPriorityDAO;
	}

	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		this.finRepayQueueDAO = finRepayQueueDAO;
	}
	public FinRepayQueueDAO getFinRepayQueueDAO() {
		return finRepayQueueDAO;
	}

	public void setFinRepayQueue(FinRepayQueue finRepayQueue) {
		this.finRepayQueue = finRepayQueue;
	}
	public FinRepayQueue getFinRepayQueue() {
		return finRepayQueue;
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