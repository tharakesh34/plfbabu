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
package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.DateService;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class BeforeEOD implements Tasklet {
	private Logger logger = LogManager.getLogger(BeforeEOD.class);

	public BeforeEOD() {
		super();
	}

	private DateService dateService;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = SysParamUtil.getAppValueDate();
		logger.info("START Before EOD On {}", valueDate);

		dateService.doUpdatebeforeEod(true);

		StepUtil.BEFORE_EOD.setTotalRecords(1);
		StepUtil.BEFORE_EOD.setProcessedRecords(1);
		BatchUtil.setExecutionStatus(context, StepUtil.BEFORE_EOD);

		dateService.loadEODConfig();
		logger.debug("COMPLET Before EOD On {}", valueDate);
		return RepeatStatus.FINISHED;

	}

	@Autowired
	public void setDateService(DateService dateService) {
		this.dateService = dateService;
	}
}
