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
package com.pennant.backend.endofday.tasklet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.core.RepayQueueService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.util.BatchUtil;

public class RepayQueueCalculation implements Tasklet {

	private Logger				logger	= Logger.getLogger(RepayQueueCalculation.class);

	private DataSource			dataSource;
	private RepayQueueService	repayQueueService;

	public RepayQueueCalculation() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		logger.debug("START: Repayments Queue Today Queuing for Value Date: " + DateUtility.getValueDate());
		String logRefernce = "";
		int processed = 0;

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {

			// Clear Repay Queue Details
			getRepayQueueService().deleteRepayQueue();
			getRepayQueueService().loadFinanceRepayPriority();

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(DateUtility.getValueDate().toString()));
			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			resultSet.close();
			sqlStatement.close();

			sqlStatement = connection.prepareStatement(customeFinance);
			sqlStatement.setDate(1, DateUtility.getDBDate(DateUtility.getValueDate().toString()));
			resultSet = sqlStatement.executeQuery();

			List<FinRepayQueue> repayQueuList = new ArrayList<FinRepayQueue>();

			while (resultSet.next()) {
				FinRepayQueue finrpy = getRepayQueueService().doWriteDataToBean(resultSet);
				logRefernce = finrpy.getFinReference();
				repayQueuList.add(finrpy);

				if (repayQueuList.size() == 299 || repayQueuList.size() == 300) {
					getRepayQueueService().save(repayQueuList);
					repayQueuList.clear();
				}

				processed = resultSet.getRow();
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));
			}

			if (repayQueuList.size() > 0) {
				getRepayQueueService().save(repayQueuList);
				repayQueuList = null;
			}

		} catch (Exception e) {
			logger.error("Finrefernce :" + logRefernce, e);
			throw e;
		} finally {
			getRepayQueueService().clearFinanceRepayPriority();
			
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
		logger.debug("COMPLETE: Repayments Queue Today Queuing for Value Date: " + DateUtility.getValueDate());
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for get count of Schedule data
	 * 
	 * @return selQuery
	 */
	private String getCountQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT count(F.FinReference) ");
		selQuery.append(" FROM FinanceMain F , FinScheduleDetails S WHERE F.FinReference = S.FinReference");
		selQuery.append(" AND S.SchDate <= ? AND (S.RepayOnSchDate = 1 OR (S.PftOnSchDate = 1 AND RepayAmount > 0)) AND F.FinIsActive = 1");
		selQuery.append(" AND (S.PrincipalSchd <> S.SchdPriPaid OR S.ProfitSchd <> S.SchdPftPaid)");
		return selQuery.toString();

	}

	String	customeFinance	= " SELECT F.FinReference, F.FinBranch Branch, F.FinType ,F.CustID CustomerID ,F.LinkedFinRef,  S.SchDate RpyDate,"
									+ "  S.PrincipalSchd, S.SchdPriPaid, (S.ProfitSchd - S.SchdPftPaid) As SchdPftBal,  S.ProfitSchd, S.SchdPftpaid, (S.PrincipalSchd - S.SchdPriPaid) As SchdPriBal,"
									+ " S.SuplRent SchdSuplRent, S.SuplRentPaid SchdSuplRentPaid, (S.SuplRent -  S.SuplRentPaid) SchdSuplRentBal,"
									+ " S.IncrCost SchdIncrCost, S.IncrCostPaid SchdIncrCostPaid, (S.IncrCost - S.IncrCostPaid) SchdIncrCostBal,"
									+ " (S.IncrCost  - S.IncrCostPaid) SchdCrInsBal,"
									+ " S.FeeSchd SchdFee , S.SchdFeePaid , (S.FeeSchd - S.SchdFeePaid) SchdFeeBal, "
									+ " S.InsSchd SchdIns, S.SchdInsPaid SchdInsPaid, (S.InsSchd - S.SchdInsPaid) SchdInsBal,"
									+ " S.AdvCalRate, S.AdvProfit, S.CalculatedRate  FROM FinanceMain F , FinScheduleDetails S WHERE F.FinReference = S.FinReference  AND S.SchDate <= ? "
									+ " AND  (S.RepayOnSchDate = 1 OR (S.PftOnSchDate = 1 AND RepayAmount > 0))  AND F.FinIsActive = 1 "
									+ " AND (S.PrincipalSchd <> S.SchdPriPaid OR S.ProfitSchd <> S.SchdPftPaid "
									+ "  OR S.SuplRent <> S.SuplRentPaid OR  S.IncrCost <> S.IncrCostPaid "
									+ " OR S.FeeSchd <> S.SchdFeePaid OR S.InsSchd <>  S.SchdInsPaid ) "
									+ " order by F.LinkedFinRef asc";

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public RepayQueueService getRepayQueueService() {
		return repayQueueService;
	}

	public void setRepayQueueService(RepayQueueService repayQueueService) {
		this.repayQueueService = repayQueueService;
	}
}
