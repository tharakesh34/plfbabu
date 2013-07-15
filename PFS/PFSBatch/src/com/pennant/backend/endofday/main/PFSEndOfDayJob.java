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

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennant.app.util.DateUtility;

public class PFSEndOfDayJob {
	
	@SuppressWarnings("serial")
	public static void main(String[] args) throws Exception {

		JobParametersBuilder builder = null;  
		JobParameters jobParameters = null;  

		//Building Job Context Id based on DateTime Format
		builder = new JobParametersBuilder();  
		if(args.length >0){
			builder.addString("Date", args[0]);
		} else {
			builder.addString("Date", DateUtility.formatDate(new Date(), "ddMMyyyyHHmmss"));
		}
		
		jobParameters = builder.toJobParameters();  

		//Loading End Of Day XML file into Context
		//ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("PradeepPFSEndOfDayJob.xml");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("PFSBDEodJob.xml");
		JobLauncher joblauncher = (JobLauncher)context.getBean("jobLauncher");
		Job job = (Job)context.getBean("PradeepPFSEndOfDayJob");
		
		try {
			
			//Running Defined job in XML
			joblauncher.run(job, jobParameters);
			
		} catch (JobExecutionAlreadyRunningException e) {
			throw new JobExecutionAlreadyRunningException(e.getMessage()) {};
		} catch (JobRestartException e) {
			throw new JobRestartException(e.getMessage()) {};
		} catch (JobInstanceAlreadyCompleteException e) {
			throw new JobInstanceAlreadyCompleteException(e.getMessage()) {};
		} catch (JobParametersInvalidException e) {
			throw new JobParametersInvalidException(e.getMessage()) {};
		}
		
	}
	
}