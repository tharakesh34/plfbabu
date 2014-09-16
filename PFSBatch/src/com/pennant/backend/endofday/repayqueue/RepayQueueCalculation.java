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
import java.util.List;
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
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;

public class RepayQueueCalculation implements Tasklet {
	
	private Logger logger = Logger.getLogger(RepayQueueCalculation.class);

	private FinanceRepayPriorityDAO financeRepayPriorityDAO;
	private FinRepayQueueDAO finRepayQueueDAO;
	private DataSource dataSource;

	private Date dateValueDate = null;
	private Map<String, Integer> priorityMap = null;
	private BigDecimal zeroValue = BigDecimal.ZERO;
	
	int repayments = 0;
	int processed = 0;
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());

		logger.debug("START: Repayments Queue Today Queuing for Value Date: " + dateValueDate);

		// FETCH Finance type Repayment Priorities
		List<ValueLabel> finRpyPriorities = getFinanceRepayPriorityDAO().getFinanceRepayPriorities();

		priorityMap = new HashMap<String, Integer>();
		for (int i = 0; i < finRpyPriorities.size(); i++) {
			priorityMap.put(finRpyPriorities.get(i).getLabel(),Integer.parseInt(finRpyPriorities.get(i).getValue()));
		}

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {
			
			//Clear Repay Queue Details
			getFinRepayQueueDAO().deleteRepayQueue();
			
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));			
			resultSet = sqlStatement.executeQuery();
			resultSet.next();	
			repayments  = resultSet.getInt(1);
			BatchUtil.setExecution(context,  "TOTAL", String.valueOf(repayments));
			
			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = sqlStatement.executeQuery();
			
			List<FinRepayQueue> repayQueuList = new ArrayList<FinRepayQueue>();

			while (resultSet.next()) {
				
				if (resultSet.getBigDecimal("DefProfitbal").compareTo(zeroValue) > 0
						|| resultSet.getBigDecimal("DefPrincipalBal").compareTo(zeroValue) > 0) {
					repayQueuList.add(doWriteDataToBean(resultSet, PennantConstants.DEFERED));
				}

				if (resultSet.getBigDecimal("SchdPftBal").compareTo(zeroValue) > 0 
						|| resultSet.getBigDecimal("SchdPriBal").compareTo(zeroValue) > 0) {
					repayQueuList.add(doWriteDataToBean(resultSet, PennantConstants.SCHEDULE));
				}
				
				if(repayQueuList.size() == 299 || repayQueuList.size() == 300){
					getFinRepayQueueDAO().setFinRepayQueueRecords(repayQueuList);
					repayQueuList.clear();
				}
				
				processed = resultSet.getRow();
				BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(processed));
				BatchUtil.setExecution(context,  "INFO", getInfo());

			}
			
			if(repayQueuList.size() > 0){
				getFinRepayQueueDAO().setFinRepayQueueRecords(repayQueuList);
				repayQueuList = null;
			}
			
			BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(processed));
			BatchUtil.setExecution(context,  "INFO", getInfo());
			
		} catch (Exception e) {
			logger.error(e);
			throw e;
		} finally {
			priorityMap = null;
			if(resultSet != null) {
				resultSet.close();
			}
			if(sqlStatement != null) {
				sqlStatement.close();
			}
		}
		logger.debug("COMPLETE: Repayments Queue Today Queuing for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}
	
	/**
	 * Method for get count of Schedule data
	 * @return selQuery 
	 */
	private String getCountQuery() {
		
		StringBuilder selQuery = new StringBuilder(" SELECT count(F.FinReference) ");
		selQuery.append(" FROM FinanceMain F , FinScheduleDetails S WHERE F.FinReference = S.FinReference");
		selQuery.append(" AND S.SchDate <= ? AND (S.RepayOnSchDate = '1' OR S.DeferedPay= '1' OR (S.PftOnSchDate = '1' AND RepayAmount > 0)) AND F.FinIsActive = '1'");
		selQuery.append(" AND (S.PrincipalSchd <> S.SchdPriPaid OR S.ProfitSchd <> S.SchdPftPaid OR S.DefPrincipalSchd <> S.DefSchdPriPaid OR S.DefProfitSchd <> S.DefSchdPftPaid)");
		return selQuery.toString();
		
	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private String prepareSelectQuery() {
		
		StringBuilder selQuery = new StringBuilder(" SELECT F.FinReference, F.FinBranch, F.FinType ,F.CustID , ");
		selQuery.append(" S.SchDate, S.DefProfitSchd ,S.DefPrincipalSchd, S.ProfitSchd, S.PrincipalSchd, S.DefSchdPftPaid, S.DefSchdPriPaid,");
		selQuery.append(" S.SchdPftpaid, S.SchdPriPaid, (S.ProfitSchd - S.SchdPftPaid) As SchdPftBal, (S.PrincipalSchd - S.SchdPriPaid) As SchdPriBal,");
		selQuery.append(" (S.DefPrincipalSchd - S.DefSchdPriPaid) As DefPrincipalBal, (S.DefProfitSchd - S.DefSchdPftPaid) As DefProfitbal ");
		selQuery.append(" FROM FinanceMain F , FinScheduleDetails S WHERE F.FinReference = S.FinReference");
		selQuery.append(" AND S.SchDate <= ? AND  (S.RepayOnSchDate = '1' OR S.DeferedPay= '1' OR (S.PftOnSchDate = '1' AND RepayAmount > 0))  AND F.FinIsActive = '1' ");
		selQuery.append(" AND (S.PrincipalSchd <> S.SchdPriPaid OR S.ProfitSchd <> S.SchdPftPaid OR S.DefPrincipalSchd <> S.DefSchdPriPaid OR S.DefProfitSchd <> S.DefSchdPftPaid)");
		return selQuery.toString();
		
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
		
		FinRepayQueue finRepayQueue = new FinRepayQueue();

		try {
			
			finRepayQueue.setFinReference(resultSet.getString("FinReference"));
			finRepayQueue.setBranch(resultSet.getString("FinBranch"));
			finRepayQueue.setFinType(resultSet.getString("FinType"));
			finRepayQueue.setCustomerID(resultSet.getLong("CustID"));
			finRepayQueue.setRpyDate(resultSet.getDate("SchDate"));
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

			if (finRepayQueue.getSchdPftBal().compareTo(BigDecimal.ZERO) == 0) {
				finRepayQueue.setSchdIsPftPaid(true);
			} else {
				finRepayQueue.setSchdIsPftPaid(false);
			}

			if (finRepayQueue.isSchdIsPftPaid() && 
					finRepayQueue.getSchdPriBal().compareTo(BigDecimal.ZERO) == 0) {
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
	
	private String getInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append("Total Repayment's").append(": ").append(repayments);		
		return builder.toString();
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

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
}