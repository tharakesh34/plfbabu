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
 * FileName    		:  IncomeAmortization.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  24-12-2017															*
 *                                                                  
 * Modified Date    :  24-12-2017															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-12-2017       Pennant	                 0.1                                            * 
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

import com.pennant.app.eod.incomeamortization.IncomeAmortizationProcess;
import com.pennant.app.util.DateUtility;

public class IncomeAmortization implements Tasklet {

	private Logger logger = Logger.getLogger(IncomeAmortization.class);

	private IncomeAmortizationProcess incomeAmortizationProcess;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START : Income Amortization On : " + valueDate);

		incomeAmortizationProcess.projectedIncomeAmortization();

		logger.debug("COMPLETE : Income Amortization On : " + valueDate);
		return RepeatStatus.FINISHED;
	}

	// getters / setters

	public void setIncomeAmortizationProcess(IncomeAmortizationProcess incomeAmortizationProcess) {
		this.incomeAmortizationProcess = incomeAmortizationProcess;
	}
}