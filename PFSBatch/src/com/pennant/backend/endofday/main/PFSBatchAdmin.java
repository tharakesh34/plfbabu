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
 * FileName    		:  PFSEndOfDayJob.java													*                           
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

import org.apache.log4j.Logger;
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

import com.pennant.app.util.DateUtility;

public class PFSBatchAdmin {

	private static final long serialVersionUID = -1648550888126596125L;
	private final static Logger logger = Logger.getLogger(PFSBatchAdmin.class);
	
	private static PFSBatchAdmin instance = null;
	private static ClassPathXmlApplicationContext PFS_JOB_CONTEXT;	
	
	
	private static  JobParametersBuilder 	builder;
	private static  JobParameters 			jobParameters;
	private static  JobRepository  			jobRepository;
	private static  SimpleJobOperator 		jobOperator;
	private static  JobLauncher 			jobLauncher;
	private static  Job 					job;

	private static String args[] = new String[1];
	private static String runType = "";

	private PFSBatchAdmin() {
		logger.debug("Entering");
		
		//Application Context For END OF DAY job initiation
		PFS_JOB_CONTEXT = new ClassPathXmlApplicationContext("PFSEquationEodJob.xml");	
		
		jobRepository 	= (JobRepository)PFS_JOB_CONTEXT.getBean("jobRepository");
		jobOperator 	= (SimpleJobOperator)PFS_JOB_CONTEXT.getBean("jobOperator");
		jobLauncher 	= (JobLauncher)PFS_JOB_CONTEXT.getBean("jobLauncher");
		job 			= (Job) PFS_JOB_CONTEXT.getBean("equationEodJob");
		
		builder = new JobParametersBuilder();		
		logger.debug("Leaving");
	}
	
	public static PFSBatchAdmin getInstance() {
		if (instance == null) {
			instance = new PFSBatchAdmin();
		}
		return instance;
	}

	public static boolean resetStaleJob(JobExecution jobExecution) throws Exception {
		logger.debug("Entering");

		if (jobExecution == null) {
			logger.debug("Leaving");
			return false;
		}
		
		try {
			jobOperator.stop(jobExecution.getId());
		} catch (Exception e) {	
			// 
		}
		
		jobExecution = getJobExecution();
		jobExecution.setStatus(BatchStatus.STOPPED);
		jobExecution.setExitStatus(ExitStatus.STOPPED);
		jobExecution.setEndTime(DateUtility.getUtilDate());
		jobRepository.update(jobExecution);
		
		logger.debug("Leaving");
		return true;
	}

	public static void statrJob() {
		logger.debug("Entering");
		try {
			if (runType.equals("START")) {
				builder.addString("Date", (String) args[0]);				
				jobParameters = builder.toJobParameters();				
				jobLauncher.run(job, jobParameters).getId();
			} else {
				//this.jobOperator.startNextInstance(this.job.getName());
				jobOperator.restart(getJobExecution().getId());
			}
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				logger.error(e1.getMessage());
			}
		}
		logger.debug("Leaving");
	}
	
	public static JobExecution getJobExecution() {
		builder.addString("Date", (String) args[0]);				
		jobParameters = builder.toJobParameters();		
		return jobRepository.getLastJobExecution(job.getName(), jobParameters);
	}
	
	
	public static void destroy() {
		instance = null;
		PFS_JOB_CONTEXT = null;
		builder = null;
		jobParameters = null;
		jobRepository = null;
		jobOperator = null;
		jobLauncher = null;
		job = null;

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public static String[] getArgs() {
		return args;
	}

	public static void setArgs(String[] args) {
		PFSBatchAdmin.args = args;
	}

	public static String getRunType() {
		return runType;
	}

	public static void setRunType(String runType) {
		PFSBatchAdmin.runType = runType;
	}


}