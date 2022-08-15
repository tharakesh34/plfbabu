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
 * FileName : NextBussinessDateUpdation.java *
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
package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class PrepareCustomerQueue implements Tasklet {
	private Logger logger = LogManager.getLogger(PrepareCustomerQueue.class);

	private CustomerQueuingDAO customerQueuingDAO;
	private ProjectedAmortizationDAO projectedAmortizationDAO;

	public PrepareCustomerQueue() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		EventProperties eventProperties = EODUtil.getEventProperties(EODUtil.EVENT_PROPERTIES, context);
		Date valueDate = eventProperties.getAppValueDate();

		BatchUtil.setExecutionStatus(context, StepUtil.PREPARE_CUSTOMER_QUEUE);

		/* ACCRUALS calculation for Amortization */
		if (eventProperties.isMonthEndAccCallReq()) {
			/* Delete Future ACCRUAL Records before customer queuing */
			if (valueDate.compareTo(DateUtil.getMonthEnd(valueDate)) == 0 || eventProperties.isEomOnEOD()) {
				if (eventProperties.isCalAccrualFromStart()) {
					projectedAmortizationDAO.deleteAllProjAccruals();
				} else {
					Date monthStart = DateUtil.getMonthStart(valueDate);
					projectedAmortizationDAO.deleteFutureProjAccruals(monthStart);
				}
			}
		}

		/* Clear CustomerQueueing */
		logger.info("Deleting customer queueing from previous run...");
		this.customerQueuingDAO.delete();
		logger.info("Preparing customer Queueing for current run...");
		int count = customerQueuingDAO.prepareCustomerQueueByLoanCount(valueDate);

		/* Update the LimitRebuild flag as true, if the Limit Structure has been changed */
		customerQueuingDAO.updateLimitRebuild();

		StepUtil.PREPARE_CUSTOMER_QUEUE.setTotalRecords(count);
		StepUtil.PREPARE_CUSTOMER_QUEUE.setProcessedRecords(count);

		logger.info("Customer queueing preparation compled.");
		logger.info("Total customer queue {}", count);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	@Autowired
	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}
}
