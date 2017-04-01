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
 * FileName    		:  RateReview.java													*                           
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
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.core.RateReviewService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.BatchUtil;

public class RateReview implements Tasklet {

	private Logger logger = Logger.getLogger(RateReview.class);

	private DataSource dataSource;
	private RateReviewService rateReviewService;

	private Date dateValueDate = null;
	
	int processed = 0;
	int postings = 0;
	
	public RateReview() {
		
	}
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		dateValueDate = DateUtility.getValueDate();

		logger.debug("START: Rate Review for Value Date: " + DateUtility.addDays(dateValueDate, -1));
		
		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			sqlStatement.setDate(2, DateUtility.getDBDate(dateValueDate.toString()));
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
			sqlStatement.setDate(2, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = sqlStatement.executeQuery();


			while (resultSet.next()) {
				//FIXME change the method as per new rate review process				
				//				getRateReviewService().processRateReview(resultSet.getString("FinReference"),dateValueDate);
				
				processed = resultSet.getRow();
				BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(processed));
				BatchUtil.setExecution(context,  "INFO", getInfo());
			}
			
			BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(processed));
			BatchUtil.setExecution(context,  "INFO", getInfo());

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			if(resultSet != null) {
				resultSet.close();
			}
			
			if(sqlStatement != null) {
				sqlStatement.close();
			}
		}
		logger.debug("COMPLETE: Rate Review for Value Date: " + DateUtility.addDays(dateValueDate, -1));
		return RepeatStatus.FINISHED;

	}
	
	/**
	 * Method for preparation of Select Query To get Finances , which are
	 * changed rates on Particular dates
	 * 
	 * @param selQuery
	 * @return
	 */
	private String getCountQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT count(fm.FinReference)");
		selQuery.append(" FROM FinanceMain  fm");
		selQuery.append(" WHERE fm.FinIsActive = 1  AND AllowGrcPftRvw = 1 AND LastRepayRvwDate < GrcPeriodEndDate ");
		selQuery.append(" AND NextGrcPftRvwDate = ? AND GraceBaseRate IS NOT NULL AND GraceBaseRate <> ''");
		selQuery.append(" UNION ");
		selQuery.append(" SELECT count(fm.FinReference) ");
		selQuery.append(" FROM FinanceMain  fm");
		selQuery.append(" WHERE fm.FinIsActive = 1  AND AllowRepayRvw = 1 AND LastRepayRvwDate < MaturityDate ");
		selQuery.append(" AND NextRepayRvwDate = ? AND RepayBaseRate IS NOT NULL AND RepayBaseRate <> ''");
		return selQuery.toString();

	}

	/**
	 * Method for preparation of Select Query To get Finances , which are
	 * changed rates on Particular dates
	 * 
	 * @param selQuery
	 * @return
	 */
	private String prepareSelectQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT fm.FinReference  FinReference ");
		selQuery.append(" FROM FinanceMain  fm");
		selQuery.append(" WHERE fm.FinIsActive = 1  AND AllowGrcPftRvw = 1 AND LastRepayRvwDate < GrcPeriodEndDate ");
		selQuery.append(" AND NextGrcPftRvwDate = ? AND GraceBaseRate IS NOT NULL AND GraceBaseRate <> ''");
		selQuery.append(" UNION ");
		selQuery.append(" SELECT fm.FinReference  FinReference ");
		selQuery.append(" FROM FinanceMain  fm");
		selQuery.append(" WHERE fm.FinIsActive = 1  AND AllowRepayRvw = 1 AND LastRepayRvwDate < MaturityDate ");
		selQuery.append(" AND NextRepayRvwDate = ? AND RepayBaseRate IS NOT NULL AND RepayBaseRate <> ''");
		return selQuery.toString();

	}
	
	private String getInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append("Total Rate Change Posting's").append(": ").append(postings);
		return builder.toString();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//



	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public RateReviewService getRateReviewService() {
		return rateReviewService;
	}

	public void setRateReviewService(RateReviewService rateReviewService) {
		this.rateReviewService = rateReviewService;
	}
	
}
