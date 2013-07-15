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
 * FileName    		:  StatisticsSheduler.java													*                           
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
package com.pennant.app.util;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

import com.pennant.backend.service.dashboard.DetailStatisticsService;
import com.sun.xml.internal.ws.developer.Stateful;

@Stateful
public class StatisticsSheduler implements Job,Serializable {

	private static final long serialVersionUID = 4716466545490087546L;
	private final static Logger logger = Logger.getLogger(StatisticsSheduler.class);
	private static DetailStatisticsService detailStatisticsService;

	@Override
	public  synchronized void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("Entering ");
		logger.debug("Entering HashCode:"+this.hashCode());
		
		JobKey jobKey = context.getJobDetail().getKey();

		context.toString();
		logger.debug("--->Executing Audit Statistics fetching job");
		if(jobKey.getName().equalsIgnoreCase("AUDIT_STATISTICS")){
			try {
				getDetailStatisticsService().saveOrUpdate();
			} catch (Exception e) {
				logger.error(e.toString());
				try {
					logger.debug("Audit statistics schedular is going into standby mode ,jobs execution Paused until it starts again");
					context.getScheduler().standby();
				} catch (SchedulerException se) {
					logger.error(se.toString());
				}
			}
		}
		logger.debug("Leaving HashCode:"+this.hashCode());
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public  static DetailStatisticsService getDetailStatisticsService() {
		return detailStatisticsService;
	}
	public void setDetailStatisticsService(
			DetailStatisticsService detailStatisticsService) {
		StatisticsSheduler.detailStatisticsService = detailStatisticsService;
	}
}
