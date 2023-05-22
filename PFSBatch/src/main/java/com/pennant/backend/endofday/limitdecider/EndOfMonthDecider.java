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
 * FileName : EndOfMonthDecider.java *
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

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.EODUtil;

public class EndOfMonthDecider implements JobExecutionDecider {
	private static final Logger logger = LogManager.getLogger(EndOfMonthDecider.class);

	private EODConfigDAO eodConfigDAO;

	public EndOfMonthDecider() {
		super();
	}

	public EODConfig getEodConfig() {
		try {
			List<EODConfig> list = eodConfigDAO.getEODConfig();
			if (!list.isEmpty()) {
				return list.get(0);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return null;
	}

	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		logger.debug(Literal.ENTERING);
		Object object = jobExecution.getExecutionContext().get(EODUtil.EVENT_PROPERTIES);

		Date valueDate = null;
		int amzPostingEvent = 0;
		boolean eomOnEOD = false;

		if (object == null) {
			valueDate = (Date) jobExecution.getExecutionContext().get("APP_VALUEDATE");
			amzPostingEvent = SysParamUtil.getValueAsInt(AccountConstants.AMZ_POSTING_EVENT);
			eomOnEOD = SysParamUtil.isAllowed(SMTParameterConstants.EOM_ON_EOD);
		} else {
			EventProperties eventProperties = (EventProperties) object;
			valueDate = eventProperties.getAppValueDate();
			amzPostingEvent = eventProperties.getAmzPostingEvent();
			eomOnEOD = eventProperties.isEomOnEOD();
		}

		try {

			boolean monthEnd = false;

			if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_MTH_END) {
				if (valueDate.compareTo(DateUtil.getMonthEnd(valueDate)) == 0) {
					monthEnd = true;
				}
			} else if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_EXT_MTH_END) {
				if (getEodConfig() != null && getEodConfig().isInExtMnth()) {
					if (getEodConfig().getMnthExtTo().compareTo(valueDate) == 0) {
						monthEnd = true;
					}
				}
			}

			/* If month start date then only it should run */
			if (monthEnd || eomOnEOD) {
				return new FlowExecutionStatus("EndOfMonth");
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new FlowExecutionStatus("NotEndOfMonth");
	}
}
