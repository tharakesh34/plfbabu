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

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennant.app.util.DateUtility;

public class PFSBatchAdmin implements Runnable, Serializable {

	private static final long serialVersionUID = -1648550888126596125L;
	private final static Logger logger = Logger.getLogger(PFSBatchAdmin.class);
	
	private JobLauncher 			joblauncher;
	private Job 					job;
	private JobRepository 			jobRepository;
	private JobParametersBuilder 	builder;
	private JobParameters 			jobParameters;
	private JobOperator 			jobOperator;

	private String 					jobStatus;
	private JobExecution 			jobExecution;
	private SimpleJobExplorer 		simpleJobExplorer;

	private String args[] = null;
	private static ClassPathXmlApplicationContext PFS_JOB_CONTEXT;
	//private static ClassPathXmlApplicationContext PFS_JOB_CONTEXT;

	public PFSBatchAdmin() {
		logger.debug("Entering");
		
		//Application Context For END OF DAY job intiation
		PFS_JOB_CONTEXT = new ClassPathXmlApplicationContext("PFSEquationEodJob.xml");
		//PFS_JOB_CONTEXT = new ClassPathXmlApplicationContext("PFSBDEodJob.xml");//--FOR CORE DB
		
		//Initialization of Properties of Spring Job through Context
		this.joblauncher = (JobLauncher) PFS_JOB_CONTEXT.getBean("jobLauncher");
		setSimpleJobExplorer((SimpleJobExplorer) PFS_JOB_CONTEXT.getBean("jobExplorer"));
		//this.job = (Job) PFS_JOB_CONTEXT.getBean("dbEodJob");//--FOR CORE DB
		this.job = (Job) PFS_JOB_CONTEXT.getBean("equationEodJob");
		this.jobRepository = (JobRepository) PFS_JOB_CONTEXT.getBean("jobRepository");
		this.jobOperator = (SimpleJobOperator) PFS_JOB_CONTEXT.getBean("jobOperator");
		
		logger.debug("Leaving");
	}

	public JobExecution getJobExecution(String[] args) {
		logger.debug("Entering");
		
		this.builder = new JobParametersBuilder();
		this.builder.addString("Date", (String) args[0]);
		this.jobExecution = this.jobRepository.getLastJobExecution(this.job.getName(), builder.toJobParameters());
		
		logger.debug("Leaving");
		return this.jobExecution;
	}

	public void statrJob(String args[]) {
		logger.debug("Entering");
		
		this.args = args;
		this.builder = new JobParametersBuilder();
		this.builder.addString("Date", (String) args[0]);
		this.jobParameters = this.builder.toJobParameters();

		Thread thread = new Thread(this);
		try {
			thread.start();
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		logger.debug("Leaving");
	}

	public void reStatrJob(JobExecution jobExecution) {
		logger.debug("Entering");
		
		this.args = null;
		this.jobExecution = jobExecution;
		Thread thread = new Thread(this);
		try {
			thread.start();
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		logger.debug("Leaving");
	}

	@SuppressWarnings("serial")
	private void startJob() throws Exception {
		logger.debug("Entering");
		
		try {
			joblauncher.run(job, jobParameters).getId();

		} catch (JobExecutionAlreadyRunningException e) {
			logger.error(e.getMessage());
			throw new JobExecutionAlreadyRunningException(e.getMessage()) {};
		} catch (JobInstanceAlreadyCompleteException e) {
			logger.error(e.getMessage());
			throw new JobInstanceAlreadyCompleteException(e.getMessage()) {};
		} catch (JobParametersInvalidException e) {
			logger.error(e.getMessage());
			throw new JobParametersInvalidException(e.getMessage()) {};
		}
		logger.debug("Leaving");
	}

	@SuppressWarnings("serial")
	private void reStartJob() throws Exception {
		logger.debug("Entering");
		
		try {
			this.jobOperator.startNextInstance(this.job.getName());
			this.jobOperator.restart(jobExecution.getId());			
			
		} catch (JobRestartException e) {
			logger.error(e.getMessage());
			throw new JobRestartException(e.getMessage()) {};
		} catch (JobInstanceAlreadyCompleteException e) {
			logger.error(e.getMessage());
			throw new JobInstanceAlreadyCompleteException(e.getMessage()) {};
		} catch (JobParametersInvalidException e) {
			logger.error(e.getMessage());
			throw new JobParametersInvalidException(e.getMessage()) {};
		}
		logger.debug("Leaving");
	}

	public boolean resetStaleJob(JobExecution jobExecution) {
		logger.debug("Entering");
		
		if (jobExecution == null) {
			logger.debug("Leaving");
			return false;
		}

		jobExecution.setStatus(BatchStatus.STOPPED);
		jobExecution.setExitStatus(ExitStatus.STOPPED);
		jobExecution.setEndTime(DateUtility.getUtilDate());
		
		try {
			this.jobOperator.stop(jobExecution.getId());
		} catch (NoSuchJobExecutionException e) {	
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (JobExecutionNotRunningException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		this.jobRepository.update(jobExecution);
		logger.debug("Leaving");
		return true;
	}

	public void run() {
		logger.debug("Entering");

		try {
			this.jobStatus = null;
			if (this.args != null) {
				startJob();
			} else {
				reStartJob();
			}

		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				logger.error(e1.getMessage());
				this.jobStatus = e1.getMessage();
			}
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public void setSimpleJobExplorer(SimpleJobExplorer simpleJobExplorer) {
		this.simpleJobExplorer = simpleJobExplorer;
	}
	public SimpleJobExplorer getSimpleJobExplorer() {
		return simpleJobExplorer;
	}

}