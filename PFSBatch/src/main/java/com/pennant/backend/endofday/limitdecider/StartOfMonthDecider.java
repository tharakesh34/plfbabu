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
 * FileName : StartOfMonthDecider.java *
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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class StartOfMonthDecider implements JobExecutionDecider {
	private static final Logger logger = LogManager.getLogger(StartOfMonthDecider.class);

	private EODConfigDAO eodConfigDAO;

	public StartOfMonthDecider() {
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
		Date valueDate = (Date) jobExecution.getExecutionContext().get("APP_VALUEDATE");
		try {

			boolean monthStart = false;
			int amzPostingEvent = SysParamUtil.getValueAsInt(AccountConstants.AMZ_POSTING_EVENT);
			if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_MTH_END) {
				if (valueDate.compareTo(DateUtil.getMonthStart(valueDate)) == 0) {
					monthStart = true;
				}
			} else if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_EXT_MTH_END) {
				if (getEodConfig() != null && getEodConfig().isInExtMnth()) {
					if (getEodConfig().getMnthExtTo().compareTo(valueDate) == 0) {
						monthStart = true;
					}
				}
			}

			// if month start date then only it should run
			if (monthStart || StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("SOM_ON_EOD"))) {
				return new FlowExecutionStatus("StartOfMonth");
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new FlowExecutionStatus("NotStartOfMonth");
	}

	@Autowired
	public void setEodConfigDAO(EODConfigDAO eodConfigDAO) {
		this.eodConfigDAO = eodConfigDAO;
	}

}
