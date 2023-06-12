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
 * FileName : AMZBatchAdmin.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 13-10-2018 *
 * 
 * Modified Date : 13-10-2018 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-10-2018 Satya 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.endofday.main;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennant.backend.util.AmortizationConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class AMZBatchAdmin implements Serializable {

	private static final long serialVersionUID = 4441350054219737955L;
	private static final Logger logger = LogManager.getLogger(AMZBatchAdmin.class);

	private static AMZBatchAdmin instance = null;
	private static ClassPathXmlApplicationContext AMZ_JOB_CONTEXT;

	private static JobParametersBuilder builder;
	private static JobParameters jobParameters;
	private static JobRepository jobRepository;
	private static SimpleJobOperator jobOperator;
	private static JobLauncher jobLauncher;
	private static Job job;

	private static String args[] = new String[1];
	private static String runType = "";

	private AMZBatchAdmin() {
		logger.debug(Literal.ENTERING);

		// Application Context For END OF DAY job initiation
		AMZ_JOB_CONTEXT = new ClassPathXmlApplicationContext("amz-batch-config.xml");

		jobRepository = (JobRepository) AMZ_JOB_CONTEXT.getBean("jobRepository");
		jobOperator = (SimpleJobOperator) AMZ_JOB_CONTEXT.getBean("jobOperator");
		jobLauncher = (JobLauncher) AMZ_JOB_CONTEXT.getBean("jobLauncher");
		job = (Job) AMZ_JOB_CONTEXT.getBean(AmortizationConstants.AMZ_JOB_NAME);

		builder = new JobParametersBuilder();

		logger.debug(Literal.LEAVING);
	}

	public static AMZBatchAdmin getInstance() {
		if (instance == null) {
			instance = new AMZBatchAdmin();
		}
		return instance;
	}

	public static boolean resetStaleJob(JobExecution jobExecution) {
		logger.debug(Literal.ENTERING);

		if (jobExecution == null) {
			logger.debug(Literal.LEAVING);
			return false;
		}

		try {
			jobOperator.stop(jobExecution.getId());
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		jobExecution = getJobExecution();
		jobExecution.setStatus(BatchStatus.STOPPED);
		jobExecution.setExitStatus(ExitStatus.STOPPED);
		jobExecution.setEndTime(DateUtil.getSysDate());
		jobRepository.update(jobExecution);

		logger.debug(Literal.LEAVING);
		return true;
	}

	public static void statrJob() {
		logger.debug(Literal.ENTERING);

		try {
			if ("START".equals(runType)) {
				builder.addString(AmortizationConstants.AMZ_JOB_PARAM, (String) args[0]);
				jobParameters = builder.toJobParameters();
				jobLauncher.run(job, jobParameters).getId();
			} else {
				// this.jobOperator.startNextInstance(this.job.getName());
				jobOperator.restart(getJobExecution().getId());
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	public static JobExecution getJobExecution() {

		builder.addString(AmortizationConstants.AMZ_JOB_PARAM, (String) args[0]);
		jobParameters = builder.toJobParameters();

		return jobRepository.getLastJobExecution(job.getName(), jobParameters);
	}

	public static void destroy() {

		instance = null;
		AMZ_JOB_CONTEXT = null;
		builder = null;
		jobParameters = null;
		jobRepository = null;
		jobOperator = null;
		jobLauncher = null;
		job = null;
	}

	// getters / setters

	public static String[] getArgs() {
		return args;
	}

	public static void setArgs(String[] args) {
		AMZBatchAdmin.args = args;
	}

	public static String getRunType() {
		return runType;
	}

	public static void setRunType(String runType) {
		AMZBatchAdmin.runType = runType;
	}
}