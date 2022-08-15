/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : PrepareIncomeAMZDetails.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 13-10-2018 *
 * 
 * Modified Date : 13-10-2018 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-10-2018 Pennant 0.1 * * * * * * * * *
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

import com.pennant.app.core.ProjectedAmortizationService;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class PrepareIncomeAMZDetails implements Tasklet {
	private Logger logger = LogManager.getLogger(PrepareIncomeAMZDetails.class);

	private ProjectedAmortizationService projectedAmortizationService;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = EODUtil.getDate("APP_VALUEDATE", context);

		logger.info("START : Prepare Income AMZ Details {}", valueDate);

		Date amzMonth = (Date) context.getStepContext().getJobExecutionContext()
				.get(AmortizationConstants.AMZ_MONTHEND);

		BatchUtil.setExecutionStatus(context, StepUtil.PREPARE_INCOME_AMZ_DETAILS);

		// Bulk inert new fees and expenses for all finances
		projectedAmortizationService.prepareAMZDetails(amzMonth, valueDate);

		logger.info("COMPLETE : Prepare Income AMZ Details on {}", valueDate);
		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setProjectedAmortizationService(ProjectedAmortizationService projectedAmortizationService) {
		this.projectedAmortizationService = projectedAmortizationService;
	}
}
