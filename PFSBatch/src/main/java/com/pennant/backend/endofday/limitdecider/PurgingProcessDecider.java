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
 * FileName : PurgingProcessDecider.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.endofday.limitdecider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.pennant.app.util.SysParamUtil;

public class PurgingProcessDecider implements JobExecutionDecider {

	private Logger logger = LogManager.getLogger(PurgingProcessDecider.class);

	public PurgingProcessDecider() {
	    super();
	}

	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		logger.debug("Entering");
		try {
			if ("Y".equalsIgnoreCase(SysParamUtil.getValueAsString("PURGING_PROCESS"))) {
				return new FlowExecutionStatus("YES");
			} else {
				return new FlowExecutionStatus("NO");
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return new FlowExecutionStatus("NO");

	}

}
