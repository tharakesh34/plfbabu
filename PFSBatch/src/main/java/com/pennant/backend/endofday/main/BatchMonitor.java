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
 * FileName    		:  BatchMonitor.java													*                           
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
package com.pennant.backend.endofday.main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;

public class BatchMonitor {
	private static final Logger logger = Logger.getLogger(BatchMonitor.class);
	private static BatchMonitor instance = null;
	
	private static ClassPathXmlApplicationContext jobMonitorContext;	
	private static SimpleJobExplorer jobMonitorExplorer;
	
	private static  DataSource  	dataSource;	
	private static List<StepExecution> stepExecutions = new ArrayList<StepExecution>();
	
	public static long jobExecutionId = 0;
	public static long avgTime = 0;
	private static String processingTime = "00:00:00";
	private static Date jobStartTime = null;
	private static Date jobEndTime = null;
	private static Calendar calendar = Calendar.getInstance();
	
	private BatchMonitor() {
		jobMonitorContext 	= new ClassPathXmlApplicationContext("launch-context.xml");
		jobMonitorExplorer = (SimpleJobExplorer) jobMonitorContext.getBean("jobExplorer");
		dataSource 			= (DataSource)jobMonitorContext.getBean("dataSource");
	}

	public static BatchMonitor getInstance() {
		if (instance == null) {
			instance = new BatchMonitor();
		}
		return instance;
	}
	
	public static synchronized JobExecution getJobExecution() {
		return jobMonitorExplorer.getJobExecution(getJobExecutionId());
	}
	
	/**
	 * 
	 * This Method will return all the StepExecutions of latest jobInstance and
	 * prepare the Time taken for (completed/failed/running) Job
	 * 
	 * @param JobInstance
	 *            (jobInstance)
	 * @return List<StepExecution>(stepExecutions)
	 */
	public static synchronized List<StepExecution> getStepExecution(JobInstance jobInstance) {
		StringBuilder builder = null;

		List<JobExecution> jobExecutions = jobMonitorExplorer.getJobExecutions(jobInstance);

		if (jobExecutions != null) {
			stepExecutions.clear();
			long timeTaken1 = 0, h = 0, m = 0, s =0, timeTaken2 = 0;
			Collections.reverse(jobExecutions);
			for (JobExecution jobExecution : jobExecutions) {

				jobStartTime = jobExecution.getStartTime();
				jobEndTime = jobExecution.getEndTime();

				if(jobExecution.isRunning()) {
					jobEndTime = new Date(System.currentTimeMillis());
				}

				if (jobEndTime != null) {
					timeTaken1 = jobEndTime.getTime() - jobStartTime.getTime();
				}
				timeTaken2 = timeTaken2 + timeTaken1;

				h = h + timeTaken1 / (60 * 60 * 1000) % 24;
				m = m + timeTaken1 / (60 * 1000) % 60;
				s = s + timeTaken1 / 1000 % 60;			
				stepExecutions.addAll(jobExecution.getStepExecutions());
			}
			builder = new StringBuilder();
			builder.append(StringUtils.leftPad(String.valueOf(h), 2, "0")).append(":");
			builder.append(StringUtils.leftPad(String.valueOf(m), 2, "0")).append(":");
			builder.append(StringUtils.leftPad(String.valueOf(s), 2, "0"));

			processingTime = builder.toString();

			if(timeTaken2 > 0) {
				calendar.setTimeInMillis(timeTaken2);
			}
		}

		return stepExecutions;
	}
	
	
	/**
	 * This method check's whether if the job is running or not.
	 * 
	 * @return boolean
	 */
	public static boolean isEodRunning() {
		JobExecution jobExecution = null;
		if (instance == null) {
			return false;
		} else {
			jobExecution = getJobExecution();
		}

		if (jobExecution != null) {
			return jobExecution.isRunning();
		} else {
			return false;
		}
	}


	/**
	 * This method returns latest JobExecutionId, If the jobExecutionId is
	 * 0.</br> While START/RESTRT The Job make sure that JobExecutionId reset
	 * to 0</br>
	 * 
	 * @return long(jobExecutionId)
	 */
	private static long getJobExecutionId() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		if (jobExecutionId == 0) {
			try {
				connection = DataSourceUtils.doGetConnection(dataSource);
				statement = connection.createStatement();
				resultSet = statement.executeQuery("SELECT COALESCE(MAX(JOB_EXECUTION_ID), 0) JOBID FROM BATCH_JOB_EXECUTION");
				if (resultSet.next()) {
					jobExecutionId = resultSet.getLong(1);
				}
			} catch (Exception e) {
				logger.warn("Exception: ", e);
			} finally {
				try {
					if (resultSet != null) {
						resultSet.close();
					}
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (Exception e) {
					logger.warn("Exception: ", e);
				}
			}

		}
		return jobExecutionId;
	}
	
	/**
	 * This method returns average time of last 30 success full jobs, the job
	 * which is not restarted.
	 * 
	 * @return
	 */
	private static long getAvgTime() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		if(avgTime == 0) {
			try {
				connection = DataSourceUtils.doGetConnection(dataSource);
				statement = connection.createStatement();
				StringBuilder query = new StringBuilder();	

				if (App.DATABASE == Database.ORACLE) {
					query.append("  SELECT CEIL(AVG((END_TIME-START_TIME)*24*60*60*1000))  avg FROM");
					query.append("  (SELECT * FROM BATCH_JOB_EXECUTION WHERE JOB_INSTANCE_ID NOT IN(SELECT JOB_INSTANCE_ID");
					query.append("  FROM BATCH_JOB_EXECUTION WHERE STATUS IN ('FAILED', 'STARTED', 'STOPPED'))) T");

				} else {
					query.append(" SELECT AVG(DATEDIFF (millisecond, START_TIME, END_TIME))  avg FROM");
					query.append(" (SELECT * FROM BATCH_JOB_EXECUTION WHERE JOB_INSTANCE_ID NOT IN(SELECT JOB_INSTANCE_ID");
					query.append(" FROM BATCH_JOB_EXECUTION WHERE STATUS IN ('FAILED', 'STARTED', 'STOPPED')))  T");
				}
				resultSet = statement.executeQuery(query.toString());
				if (resultSet.next()) {
					avgTime = resultSet.getLong(1);
				}
			} catch (Exception e) {
				logger.warn("Exception: ", e);
			} finally {
				try {
					if (resultSet != null) {
						resultSet.close();
					}
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (Exception e) {
					logger.warn("Exception: ", e);
				}
			}
		}
	
		return avgTime;
	}
	
	/**
	 * This method return Processing Time
	 * 
	 * @return String(HH:MM:SS)
	 */
	public static String getProcessingTime() {
		return processingTime;
	}
	
	/**
	 * This method return Estimated Completion Time
	 * 
	 * @return String(HH:MM:SS)
	 */
	public static String getEstimateTime() {
		if (getAvgTime() > 0) {
			return DateUtility.timeBetween(new Date(avgTime), calendar.getTime());
		} 
		return "00:00:00";
	}
	
	

}