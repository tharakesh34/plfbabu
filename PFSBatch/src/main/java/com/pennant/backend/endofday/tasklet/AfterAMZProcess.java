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
 * FileName    		:  BeforeAMZProcess.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  13-10-2018															*
 *                                                                  
 * Modified Date    :  13-10-2018															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2018       Pennant	                 0.1                                            * 
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.util.AmortizationConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.eod.EODUtil;

public class AfterAMZProcess implements Tasklet {
	private Logger logger = LogManager.getLogger(AfterAMZProcess.class);

	private ProjectedAmortizationDAO projectedAmortizationDAO;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date appDate = EODUtil.getDate("APP_DATE", context);

		logger.debug("START: After Amortization on  {}", appDate);

		Date amzMonth = (Date) context.getStepContext().getJobExecutionContext()
				.get(AmortizationConstants.AMZ_MONTHEND);

		// copy previous AMZ data from Working table to main table
		projectedAmortizationDAO.copyPrvProjAMZ();

		// create indexes on PROJECTEDINCOMEAMZ table
		projectedAmortizationDAO.createIndexProjIncomeAMZ();

		// update amortization month
		SysParamUtil.updateParamDetails(AmortizationConstants.AMZ_MONTHEND,
				DateUtil.format(amzMonth, DateFormat.FULL_DATE.getPattern()));

		logger.debug("COMPLETE : After Amortization on {}", appDate);
		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}
}
