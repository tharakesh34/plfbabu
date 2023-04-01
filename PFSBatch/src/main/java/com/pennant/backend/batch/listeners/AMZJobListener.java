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
 * FileName : AMZJobListener.java *
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
 * 13-10-2018 Satya 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.batch.listeners;

import java.util.Date;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.AmortizationConstants;
import com.pennanttech.pennapps.core.util.DateUtil;

public class AMZJobListener implements JobExecutionListener {

	public AMZJobListener() {
		super();
	}

	@Override
	public void beforeJob(JobExecution arg0) {
		// To Handle Restart case
		if (arg0.getExecutionContext().get(AmortizationConstants.AMZ_MONTHEND) == null) {

			// update amortization month
			Date prvAMZMonth = SysParamUtil.getValueAsDate(AmortizationConstants.AMZ_MONTHEND);

			Date amzMonth = DateUtil.addDays(prvAMZMonth, 1);
			amzMonth = DateUtil.getMonthEnd(amzMonth);

			arg0.getExecutionContext().put(AmortizationConstants.AMZ_MONTHEND, amzMonth);
			arg0.getExecutionContext().put("AMZ_PRVMONTHEND", prvAMZMonth);
		}
	}

	@Override
	public void afterJob(JobExecution arg0) {

	}
}