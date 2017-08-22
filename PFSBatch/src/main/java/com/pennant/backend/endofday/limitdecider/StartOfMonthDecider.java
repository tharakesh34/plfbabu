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
 * FileName    		:  EndOfMonthDecider.java													*                           
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
package com.pennant.backend.endofday.limitdecider;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class StartOfMonthDecider implements JobExecutionDecider {
	private static final Logger logger = Logger.getLogger(StartOfMonthDecider.class);

	private Date dateValueDate = null;
	private Date monthStartDate = null;

	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		logger.debug(Literal.ENTERING);
		try {
			dateValueDate = DateUtility.getAppDate();
			monthStartDate = DateUtility.getMonthStartDate(dateValueDate);
			String isDailyDownlaod = SysParamUtil.getValueAsString("GST_TAXDETAIL_DOWNLOAD");

			if (dateValueDate.compareTo(monthStartDate) == 0 || StringUtils.equalsIgnoreCase("Y", isDailyDownlaod)) {
				return new FlowExecutionStatus("StartOfMonth");
			}
			
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new FlowExecutionStatus("NotStartOfMonth");
	}

}
