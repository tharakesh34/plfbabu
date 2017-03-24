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
 * FileName    		:  NextBussinessDateUpdation.java										*                           
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

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.core.DateService;
import com.pennant.app.util.DateUtility;

public class NextBussinessDateUpdation implements Tasklet {

	private Logger		logger	= Logger.getLogger(NextBussinessDateUpdation.class);

	private DateService	dateService;

	public NextBussinessDateUpdation() {

	}

	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getValueDate();

		logger.debug("START: Updation of Next Bussiness Date on Value Date: " + valueDate);
		getDateService().doUpdatebeforeEod(true);

		logger.debug("COMPLETE: Updation of Next Bussiness Date on Value Date: " + valueDate);
		return RepeatStatus.FINISHED;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public DateService getDateService() {
		return dateService;
	}

	public void setDateService(DateService dateService) {
		this.dateService = dateService;
	}

}
