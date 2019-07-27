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
 * FileName    		:  PrepareAmortizationQueue.java										*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  18-05-2018															*
 *                                                                  
 * Modified Date    :  18-05-2018															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-05-2018       Pennant	                 0.1                                            * 
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

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.BatchUtil;

public class PrepareAmortizationQueue implements Tasklet {

	private Logger logger = Logger.getLogger(PrepareAmortizationQueue.class);

	private ProjectedAmortizationDAO projectedAmortizationDAO;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		Date appDate = DateUtility.getAppDate();
		logger.debug("START : Prepare Amortization Queue on : " + appDate);

		Date amzMonth = (Date) context.getStepContext().getJobExecutionContext()
				.get(AmortizationConstants.AMZ_MONTHEND);

		// log and truncate previous data
		projectedAmortizationDAO.logAmortizationQueuing();
		projectedAmortizationDAO.delete();

		// prepare AMZ Queue
		int count = projectedAmortizationDAO.prepareAmortizationQueue(amzMonth, false);

		// copy previous AMZ data into working table and truncate main table
		projectedAmortizationDAO.truncateAndInsertProjAMZ(amzMonth);

		BatchUtil.setExecution(context, "TOTAL", String.valueOf(count));
		BatchUtil.setExecution(context, "PROCESSED", String.valueOf(count));

		logger.debug("COMPLETE : Prepare Amortization Queue on : " + appDate);
		return RepeatStatus.FINISHED;
	}

	// setters / getters

	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}
}