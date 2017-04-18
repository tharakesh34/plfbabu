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
 * FileName    		:  BeforeEOD.java													*                           
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
package com.pennant.backend.endofday.tasklet.ahb;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.SysParamUtil.Param;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;

public class StopAutoHunting implements Tasklet {
	private Logger	logger	= Logger.getLogger(StopAutoHunting.class);

	public StopAutoHunting() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date date = DateUtility.getValueDate();
		logger.debug("START: Request File Reading for Value Date: " + date);

		String status = SysParamUtil.getValueAsString(Param.AUTOHUNTING.getCode());

		// check Auto hunting status before starting job
		if (PennantConstants.AUTOHUNT_RUNNING.equals(status)) {
			SysParamUtil.updateParamDetails(Param.AUTOHUNTING.getCode(), PennantConstants.AUTOHUNT_BATCH);
			BatchUtil.setExecution(context, "WAIT", "Request stop auto hunting intiated.");
		}

		// wait for the stopped

		while (true) {
			
			status = SysParamUtil.getValueAsString(Param.AUTOHUNTING.getCode());
			if (PennantConstants.AUTOHUNT_BATCH.equals(status)) {
				BatchUtil.setExecution(context, "WAIT", "Waiting for closing of Auto hunting");
				continue;
			}

			if (PennantConstants.AUTOHUNT_STOPPED.equals(status)) {
				break;
			}

		}

		logger.debug("END: Request File Reading for Value Date: " + date);
		return RepeatStatus.FINISHED;

	}

}
