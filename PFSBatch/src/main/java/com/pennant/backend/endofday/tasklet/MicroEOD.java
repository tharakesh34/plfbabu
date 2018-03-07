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
 * FileName    		:  MicroEOD.java														*                           
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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
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

	private Logger						logger		= Logger.getLogger(MicroEOD.class);
	private EodService					eodService;
	private CustomerQueuingDAO			customerQueuingDAO;
	private PlatformTransactionManager	transactionManager;
	private DataSource					dataSource;

	private static final String			customerSQL	= "Select CustID from CustomerQueuing  Where ThreadID = ? and Progress= ?";

	public MicroEOD() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date appDate = DateUtility.getAppDate();
		logger.debug("START: Micro EOD On : " + appDate);

		final int threadId = (int) context.getStepContext().getStepExecutionContext().get(EodConstants.THREAD);
		logger.info("process Statred by the Thread : " + threadId + " with date " + appDate.toString());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		JdbcCursorItemReader<CustomerQueuing> cursorItemReader = new JdbcCursorItemReader<CustomerQueuing>();
		cursorItemReader.setSql(customerSQL);
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setRowMapper(ParameterizedBeanPropertyRowMapper.newInstance(CustomerQueuing.class));
		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, threadId);
				ps.setInt(2, EodConstants.PROGRESS_WAIT);
			}
		});

		//to hold the exception till the process completed for all the customers
		List<Exception> exceptions = new ArrayList<Exception>(1);

		cursorItemReader.open(context.getStepContext().getStepExecution().getExecutionContext());
		
		CustomerQueuing customerQueuing = new CustomerQueuing();
		while ((customerQueuing = cursorItemReader.read()) != null) {
			
			long custId = customerQueuing.getCustID();
			
			try {
				//update start
				customerQueuingDAO.startEODForCID(custId);

				// begin transaction
				txStatus = transactionManager.getTransaction(txDef);

				CustEODEvent custEODEvent = new CustEODEvent();
				if (customerQueuing.isLoanExist()) {
					customerQueuing.setLoanExist(false);
					Customer customer = eodService.getLoadFinanceData().getCustomerDAO().getCustomerEOD(custId);
					custEODEvent.setCustomer(customer);
					custEODEvent.setEodDate(appDate);
					custEODEvent.setEodValueDate(appDate);
					
					eodService.doProcess(custEODEvent);
					eodService.doUpdate(custEODEvent, customerQueuing.isLimitRebuild());
				} else {
					if (customerQueuing.isLimitRebuild()) {
						eodService.processCustomerRebuild(custId, true);
					}
				}
				
				//update  end
				customerQueuingDAO.updateStatus(custId, EodConstants.PROGRESS_SUCCESS);
				
				//commit
				transactionManager.commit(txStatus);

				custEODEvent.getFinEODEvents().clear();
				custEODEvent = null;

			} catch (Exception e) {
				logError(e);
				transactionManager.rollback(txStatus);
				exceptions.add(e);
				updateFailed(custId);
			}
			//clear data after the process
			customerQueuing = null;
		}
		cursorItemReader.close();

		if (!exceptions.isEmpty()) {
			Exception exception = new Exception(exceptions.get(0));
			exceptions.clear();
			exceptions=null;
			throw exception;
		}

		logger.debug("COMPLETE: Micro EOD On :" + appDate);

		return RepeatStatus.FINISHED;
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

	public void updateFailed(long custId) {
		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custId);
		customerQueuing.setEndTime(DateUtility.getSysDate());
		//reset thread for reallocation
		customerQueuing.setThreadId(0);
		//reset to "wait", to re run only failed cases.
		customerQueuing.setProgress(EodConstants.PROGRESS_WAIT);
		customerQueuingDAO.updateFailed(customerQueuing);
	}

	private void logError(Exception exp) {
		logger.error("Cause : " + exp.getCause());
		logger.error("Message : " + exp.getMessage());
		logger.error("LocalizedMessage : " + exp.getLocalizedMessage());
		logger.error("StackTrace : ", exp);

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

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
