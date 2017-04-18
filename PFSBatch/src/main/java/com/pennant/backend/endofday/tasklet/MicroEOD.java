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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.eod.EodThread;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;

public class MicroEOD implements Tasklet, ApplicationContextAware {

	private Logger					logger	= Logger.getLogger(MicroEOD.class);

	private ApplicationContext		applicationContext;
	private CustomerQueuingDAO		customerQueuingDAO;
	private ThreadPoolTaskExecutor	threadPoolTaskExecutor;

	public MicroEOD() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getValueDate();
		logger.debug("START: Micro EOD On : " + valueDate);

		int threadCount = SysParamUtil.getValueAsInt("EOD_THREAD_COUNT");

		ExecutionContext executionContext = context.getStepContext().getStepExecution().getJobExecution()
				.getExecutionContext();

		String stata = (String) executionContext.get(EodConstants.MICRO_EOD);
		if (StringUtils.equals(stata, EodConstants.STATUS_FAILED)) {
			executionContext.put(EodConstants.MICRO_EOD, null);
			threadPoolTaskExecutor.destroy();
			throw new RuntimeException();
		} else if (!StringUtils.equals(stata, EodConstants.STATUS_STARTED)) {
			customerQueuingDAO.updateFailedThread(valueDate);
		}

		if (threadPoolTaskExecutor == null) {
			createThreadPool(threadCount);
		}

		for (int i = 1; i <= threadCount; i++) {
			EodThread eodThread = (EodThread) applicationContext.getBean("eodThread");
			eodThread.setThreadId("Thread" + i);
			eodThread.setEodDate(valueDate);
			threadPoolTaskExecutor.execute(eodThread);
		}
		logger.debug("COMPLETE: Micro EOD On :" + valueDate);
		return RepeatStatus.FINISHED;
	}

	private void createThreadPool(int threadCount) {
		threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setMaxPoolSize(threadCount + 1);
		threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		threadPoolTaskExecutor.initialize();
		threadPoolTaskExecutor.setThreadGroupName("PFFEOD");
		threadPoolTaskExecutor.setThreadNamePrefix("PFFEOD-");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

}
