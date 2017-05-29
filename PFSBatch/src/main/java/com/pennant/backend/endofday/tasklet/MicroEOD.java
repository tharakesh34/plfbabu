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
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.core.CustEODEvent;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.eod.EodService;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;

public class MicroEOD implements Tasklet {

	private Logger						logger		= Logger.getLogger(MicroEOD.class);
	private EodService					eodService;
	private CustomerQueuingDAO			customerQueuingDAO;
	private PlatformTransactionManager	transactionManager;
	private DataSource					dataSource;

	private static final String			customerSQL	= "Select CUST.CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode, CustDftBranch,"
															+ " CustPOB, CustCOB, CustGroupID, CustSts, CustStsChgDate, CustIsStaff, CustIndustry,CustSector,"
															+ " CustSubSector, CustEmpSts, CustSegment, CustSubSegment, CustParentCountry,CustResdCountry,"
															+ " CustRiskCountry, CustNationality, SalariedCustomer, custSuspSts,custSuspDate, custSuspTrigger,"
															+ " CustAppDate FROM  Customers CUST INNER JOIN CustomerQueuing CQ ON CUST.CustID = CQ.CustID "
															+ " Where ThreadID = :ThreadId and Progress=:Progress";

	public MicroEOD() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Micro EOD On : " + valueDate);

		final int threadId = (int) context.getStepContext().getStepExecutionContext().get(EodConstants.THREAD);
		logger.info("process Statred by the Thread : " + threadId + " with date " + valueDate.toString());

		int chunkSize = SysParamUtil.getValueAsInt(SMTParameterConstants.EOD_CHUNK_SIZE);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		JdbcCursorItemReader<Customer> cursorItemReader = new JdbcCursorItemReader<Customer>();
		cursorItemReader.setSql(customerSQL);
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setRowMapper(new BeanPropertyRowMapper<Customer>(Customer.class));
		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, threadId);
				ps.setInt(2, EodConstants.PROGRESS_WAIT);
			}
		});

		//for transaction 
		int count = 0;
		boolean trnsactionCompleted = true;

		cursorItemReader.open(context.getStepContext().getStepExecution().getExecutionContext());
		Customer customer;
		while ((customer = cursorItemReader.read()) != null) {
			long custID = 0;
			try {

				custID = customer.getCustID();
				//update start
				customerQueuingDAO.startEODForCID(custID);

				CustEODEvent custEODEvent = new CustEODEvent();
				custEODEvent.setCustomer(customer);
				custEODEvent.setEodDate(valueDate);
				custEODEvent.setEodValueDate(valueDate);

				eodService.doProcess(custEODEvent, valueDate);

				if (count == 0) {
					//BEGIN TRANSACTION
					txStatus = transactionManager.getTransaction(txDef);
					trnsactionCompleted = false;
				}

				//update customer EOD
				eodService.getLoadFinanceData().updateFinEODEvents(custEODEvent);
				//receipt postings
				if (custEODEvent.isCheckPresentment()) {
					eodService.getReceiptPaymentService().processrReceipts(custEODEvent);
				}
				//customer Date update
				String newCustStatus = null;
				if (custEODEvent.isUpdCustomer()) {
					newCustStatus = custEODEvent.getCustomer().getCustSts();
				}

				eodService.getLoadFinanceData().updateCustomerDate(custID, valueDate, newCustStatus);
				//update  end
				customerQueuingDAO.updateSucess(custID);
				count++;

				if (count == chunkSize) {
					//COMMIT THE TRANSACTION
					transactionManager.commit(txStatus);
					count = 0;//To Create new transaction
					trnsactionCompleted = true;
				}

				custEODEvent.getFinEODEvents().clear();
				custEODEvent = null;

			} catch (Exception e) {
				transactionManager.rollback(txStatus);
				count = 0;//To Create new transaction
				trnsactionCompleted = true;
				updateFailed(threadId, custID);
			}
			//clear data after the process
			customer = null;
		}

		if (!trnsactionCompleted) {
			//COMMIT THE TRANSACTION
			transactionManager.commit(txStatus);
		}

		cursorItemReader.close();

		logger.debug("COMPLETE: Micro EOD On :" + valueDate);

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

	public void updateFailed(int threadId, long custId) {
		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custId);
		customerQueuing.setEndTime(DateUtility.getSysDate());
		//reset thread for reallocation
		customerQueuing.setThreadId(0);
		customerQueuing.setProgress(EodConstants.PROGRESS_FAILED);
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

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
