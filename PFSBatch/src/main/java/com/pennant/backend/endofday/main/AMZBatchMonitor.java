/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : AMZBatchMonitor.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.endofday.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.backend.util.AmortizationConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class AMZBatchMonitor {

	private static final Logger logger = LogManager.getLogger(AMZBatchMonitor.class);
	private static AMZBatchMonitor instance = null;

	private static ClassPathXmlApplicationContext jobMonitorContext;
	private static SimpleJobExplorer jobMonitorExplorer;

	private static DataSource dataSource;
	private static List<StepExecution> stepExecutions = new ArrayList<StepExecution>();

	public static long avgTime = 0;
	public static long jobExecutionId = 0;
	private static LocalDateTime jobStartTime = null;
	private static LocalDateTime jobEndTime = null;
	private static String processingTime = "00:00:00";

	private static Calendar calendar = Calendar.getInstance();

	private AMZBatchMonitor() {

		jobMonitorContext = new ClassPathXmlApplicationContext("launch-context.xml");
		jobMonitorExplorer = (SimpleJobExplorer) jobMonitorContext.getBean("jobExplorer");
		dataSource = (DataSource) jobMonitorContext.getBean("dataSource");
	}

	public static AMZBatchMonitor getInstance() {

		if (instance == null) {
			instance = new AMZBatchMonitor();
		}
		return instance;
	}

	public static synchronized JobExecution getJobExecution() {
		return jobMonitorExplorer.getJobExecution(getJobExecutionId());
	}

	/**
	 * 
	 * This Method will return all the StepExecutions of latest jobInstance and prepare the Time taken for
	 * (completed/failed/running) Job
	 * 
	 * @param JobInstance (jobInstance)
	 * @return List<StepExecution>(stepExecutions)
	 */
	public static synchronized List<StepExecution> getStepExecution(JobInstance jobInstance) {
		StringBuilder builder = null;

		List<JobExecution> jobExecutions = jobMonitorExplorer.getJobExecutions(jobInstance);

		if (jobExecutions != null) {
			stepExecutions.clear();
			long timeTaken1 = 0, h = 0, m = 0, s = 0, timeTaken2 = 0;
			Collections.reverse(jobExecutions);
			for (JobExecution jobExecution : jobExecutions) {

				jobStartTime = jobExecution.getStartTime();
				jobEndTime = jobExecution.getEndTime();

				if (jobExecution.isRunning()) {
					jobEndTime = LocalDateTime.now();
				}

				if (jobEndTime != null) {
					timeTaken1 = Duration.between(jobStartTime, jobEndTime).toMillis();
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

			if (timeTaken2 > 0) {
				calendar.setTimeInMillis(timeTaken2);
			}
		}

		return stepExecutions;
	}

	/**
	 * This method returns latest JobExecutionId, If the jobExecutionId is 0.</br>
	 * While START/RESTRT The Job make sure that JobExecutionId reset to 0</br>
	 * 
	 * @return long(jobExecutionId)
	 */
	private static long getJobExecutionId() {

		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		if (jobExecutionId == 0) {

			try {

				connection = DataSourceUtils.doGetConnection(dataSource);
				sqlStatement = connection.prepareStatement(BatchJobStatements.prepareJobExecutionQuery());
				sqlStatement.setString(1, AmortizationConstants.AMZ_JOB_NAME);
				resultSet = sqlStatement.executeQuery();

				if (resultSet.next()) {
					jobExecutionId = resultSet.getLong(1);
				}
			} catch (SQLException e) {
				logger.warn(Literal.EXCEPTION, e);
			} finally {
				try {
					if (resultSet != null) {
						resultSet.close();
					}
					if (sqlStatement != null) {
						sqlStatement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.warn(Literal.EXCEPTION, e);
				}
			}

		}
		return jobExecutionId;
	}

	/**
	 * This method returns average time of last 30 success full jobs, the job which is not restarted.
	 * 
	 * @return
	 */
	private static long getAvgTime() {

		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		if (avgTime == 0) {

			try {

				connection = DataSourceUtils.doGetConnection(dataSource);
				sqlStatement = connection.prepareStatement(BatchJobStatements.prepareAvgTimeCalQuery());

				// consider only EOD Job details
				sqlStatement.setString(1, AmortizationConstants.AMZ_JOB_NAME);
				resultSet = sqlStatement.executeQuery();

				if (resultSet.next()) {
					avgTime = resultSet.getLong(1);
				}

			} catch (SQLException e) {
				logger.warn("Exception: ", e);
			} finally {
				try {
					if (resultSet != null) {
						resultSet.close();
					}
					if (sqlStatement != null) {
						sqlStatement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
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
			return DateUtil.timeBetween(new Date(avgTime), calendar.getTime());
		}
		return "00:00:00";
	}
}