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
 * FileName    		:  MicroEOD.java										*                           
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.core.CustEODEvent;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.eod.EodService;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;

public class MicroEOD implements Tasklet {

	private Logger						logger	= Logger.getLogger(MicroEOD.class);
	private EodService					eodService;
	private CustomerQueuingDAO			customerQueuingDAO;
	private PlatformTransactionManager	transactionManager;

	public MicroEOD() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Micro EOD On : " + valueDate);
		int threadId = (int) context.getStepContext().getStepExecutionContext().get(EodConstants.THREAD);
		logger.info("process Statred by the Thread : " + threadId + " with date " + valueDate.toString());

		int chunkSize = 10;

		while (true) {
			int countForProcess = customerQueuingDAO.startEODForCID(valueDate, chunkSize, threadId);

			if (countForProcess > 0) {
				processCustChunks(threadId, valueDate);
			} else {
				break;
			}

			logger.debug("COMPLETE: Micro EOD On :" + valueDate);
		}


		return RepeatStatus.FINISHED;
	}

	public void processCustChunks(int threadId, Date valueDate) {

		List<CustEODEvent> custEODEvents = new ArrayList<CustEODEvent>(1);
		List<Customer> customers = customerQueuingDAO.getCustForProcess(threadId);

		for (int i = 0; i < customers.size(); i++) {
			CustEODEvent custEODEvent = new CustEODEvent();
			custEODEvent.setCustomer(customers.get(i));

			custEODEvent.setEodDate(valueDate);
			custEODEvent.setEodValueDate(valueDate);
			custEODEvents.add(custEODEvent);

			try {
				eodService.doProcess(custEODEvent, valueDate);
				custEODEvents.set(i, custEODEvent);
			} catch (Exception e) {
				custEODEvent.setEodSuccess(false);
			}
		}
		
		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setReadOnly(true);
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus txStatus = null;

		//BEGIN TRANSACTION
		txStatus = transactionManager.getTransaction(txDef);

		for (int i = 0; i < custEODEvents.size(); i++) {
			CustEODEvent custEODEvent = custEODEvents.get(i);
			if (!custEODEvent.isEodSuccess()) {
				updateFailed(threadId, custEODEvent.getCustomer().getCustID());
			} else {

				//update customer EOD
				try {
					eodService.getLoadFinanceData().updateFinEODEvents(custEODEvent);

					//receipt postings
					if (custEODEvent.isCheckPresentment()) {
						eodService.getReceiptPaymentService().processrReceipts(custEODEvent);
					}

					//customer Date update
					eodService.getLoadFinanceData().updateCustomerDate(custEODEvent.getCustomer().getCustID(), valueDate);
					
					updateCustQueueStatus(threadId, custEODEvent.getCustomer().getCustID(), EodConstants.PROGRESS_SUCCESS, false);

				} catch (Exception e) {
					transactionManager.rollback(txStatus);
					updateFailed(threadId, custEODEvent.getCustomer().getCustID());
				}

				//clear data after the process
				custEODEvent.getFinEODEvents().clear();
				custEODEvent = null;
			}
		}

		//COMMIT THE TRANSACTION
		transactionManager.commit(txStatus);

	}

	public void updateCustQueueStatus(int threadId, long custId, int progress, boolean start) {
		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custId);
		customerQueuing.setThreadId(threadId);
		customerQueuing.setStartTime(DateUtility.getSysDate());
		customerQueuing.setEndTime(DateUtility.getSysDate());
		customerQueuing.setProgress(progress);
		customerQueuingDAO.update(customerQueuing, start);
	}

	public void updateFailed(int threadId, long custId) {
		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custId);
		customerQueuing.setEndTime(DateUtility.getSysDate());
		//reset thread for reallocation
		customerQueuing.setThreadId(0);
		customerQueuing.setProgress(EodConstants.PROGRESS_WAIT);
		customerQueuingDAO.updateFailed(customerQueuing);
	}

	public void setEodService(EodService eodService) {
		this.eodService = eodService;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

}
