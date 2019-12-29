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
 * FileName    		:  NextBussinessDateUpdation.java										*                           
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

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.dao.CustomerQueuingDAO;
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
		Date valueDate = SysParamUtil.getAppValueDate();
		logger.info("START: Prepare Customer Queue On {}", valueDate);
		BatchUtil.setExecutionStatus(context, StepUtil.PREPARE_CUSTOMER_QUEUE);

		/* Delete Future ACCRUAL Records before customer queuing */

		/* ACCRUALS calculation for Amortization */
		if (SysParamUtil.isAllowed(AmortizationConstants.MONTHENDACC_CALREQ)) {
			if (valueDate.compareTo(DateUtility.getMonthEnd(valueDate)) == 0 || SysParamUtil.isAllowed("EOM_ON_EOD")) {
				if (SysParamUtil.isAllowed("MONTHENDACC_FROMFINSTARTDATE")) {
					projectedAmortizationDAO.deleteAllProjAccruals();
				} else {
					Date monthStart = DateUtility.getMonthStart(valueDate);
					projectedAmortizationDAO.deleteFutureProjAccruals(monthStart);
				}
			}
		}

		/* Clear CustomerQueueing */
		this.customerQueuingDAO.delete();
		int count = customerQueuingDAO.prepareCustomerQueue(valueDate);

		/* Update the LimitRebuild flag as true, if the Limit Structure has been changed */
		customerQueuingDAO.updateLimitRebuild();

		StepUtil.PREPARE_CUSTOMER_QUEUE.setTotalRecords(count);
		StepUtil.PREPARE_CUSTOMER_QUEUE.setProcessedRecords(count);

		logger.info("COMPLETE: Prepare Customer Queue On {}", valueDate);
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
