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
 * FileName    		:  InstallmentDuedatePostings.java										*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  01-06-2015															*
 *                                                                  
 * Modified Date    :  01-06-2015															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-06-2015       Pennant	                 0.1                                            * 
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
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.core.InstallmentDueService;
import com.pennant.app.core.ServiceHelper;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.BatchUtil;

public class InstallmentDueDatePostings extends ServiceHelper implements Tasklet {
	private static final long		serialVersionUID	= -8982220639289126200L;
	private Logger					logger				= Logger.getLogger(InstallmentDueDatePostings.class);

	private InstallmentDueService	installmentDueService;
	
	public InstallmentDueDatePostings() {
		super();
	}

	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		logger.debug("Entering");
		Date dateValueDate = DateUtility.getValueDate();

		logger.debug("START: Installment Due Date Postings Caluclation for Value Date: " + dateValueDate);

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			resultSet.close();
			sqlStatement.close();

			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = sqlStatement.executeQuery();
			int processed = 0;
			while (resultSet.next()) {
				getInstallmentDueService().postAdvancePayments(resultSet);
				processed = resultSet.getRow();
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));

			}

			BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));

		} catch (Exception e) {
			logger.error(e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
		logger.debug("COMPLETE: Installment Due Date Postings Caluclation for Value Date: " + dateValueDate);
		logger.debug("Leaving");
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for get count of Repay postings
	 * 
	 * @return selQuery
	 */
	private String getCountQuery() {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select Count(*) ");
		selectSql.append(" from  ");
		selectSql.append(" (select FinReference,FinSchdDate from FinRepayDetails where FinPostDate < FinSchdDate and ");
		selectSql.append(" FinSchdDate=? group by FinReference,FinSchdDate) t ");
		selectSql.append(" inner join  FinScheduleDetails fsd on t.FinReference=fsd.FinReference and t.FinSchdDate=fsd.SchDate");
		selectSql.append(" inner join FinanceMain fm on t.FinReference=fm.FinReference");
		return selectSql.toString();
	}

	/**
	 * Method for Preparation of Select Query for Preparing resultSet
	 * 
	 * @param selectSql
	 * @return
	 */
	private String prepareSelectQuery() {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select fm.FinReference,fm.FinType,fm.FinBranch,fsd.SchDate,SchdPftPaid,SchdPriPaid from  ");
		selectSql.append(" (select FinReference,FinSchdDate from FinRepayDetails where FinPostDate < FinSchdDate and ");
		selectSql.append(" FinSchdDate=? group by FinReference,FinSchdDate) t ");
		selectSql.append(" inner join  FinScheduleDetails fsd on t.FinReference=fsd.FinReference and t.FinSchdDate=fsd.SchDate");
		selectSql.append(" inner join FinanceMain fm on t.FinReference=fm.FinReference");
		logger.debug("Leaving");
		return selectSql.toString();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public InstallmentDueService getInstallmentDueService() {
		return installmentDueService;
	}

	public void setInstallmentDueService(InstallmentDueService installmentDueService) {
		this.installmentDueService = installmentDueService;
	}

}
