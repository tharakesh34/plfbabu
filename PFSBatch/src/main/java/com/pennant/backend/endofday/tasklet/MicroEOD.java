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
 * 04-05-2018		Vinay					 0.2         As discuss with Satya Naga Prasad  *
 * 														 Micro EOD code changed             *  
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
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.SnapshotService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.eod.EodService;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.dataengine.model.DataEngineStatus;

public class MicroEOD implements Tasklet {
	private Logger logger = LogManager.getLogger(MicroEOD.class);

	private EodService eodService;
	private RuleDAO ruleDAO;
	private CustomerQueuingDAO customerQueuingDAO;
	private PlatformTransactionManager transactionManager;
	private DataSource dataSource;
	private CustomerDAO customerDAO;
	private SnapshotService snapshotService;

	// ##_0.2
	private static final String CUSTOMER_SQL = "Select CustID, LoanExist, LimitRebuild from CustomerQueuing  Where ThreadID = ? and Progress= ?";

	public MicroEOD() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date appDate = SysParamUtil.getAppDate();
		logger.debug("START: Micro EOD On {}", appDate);

		Map<String, Object> stepExecutionContext = context.getStepContext().getStepExecutionContext();
		final int threadId = Integer.parseInt(stepExecutionContext.get(EodConstants.THREAD).toString());

		DataEngineStatus status = (DataEngineStatus) stepExecutionContext.get("microEOD:" + String.valueOf(threadId));
		long processedCount = 1;
		long failedCount = 0;
		logger.info("process Statred by the Thread {} with date {}", threadId, appDate.toString());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		JdbcCursorItemReader<CustomerQueuing> cursorItemReader = new JdbcCursorItemReader<CustomerQueuing>();
		cursorItemReader.setSql(CUSTOMER_SQL);
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setRowMapper(ParameterizedBeanPropertyRowMapper.newInstance(CustomerQueuing.class));
		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, threadId);
				ps.setInt(2, EodConstants.PROGRESS_WAIT);
			}
		});

		// Get the Rules 
		String provisionRule = null;
		String amzMethodRule = null;

		String provRule = SysParamUtil.getValueAsString("PROVISION_RULE");
		if (provRule != null) {
			provisionRule = ruleDAO.getAmountRule(provRule, RuleConstants.MODULE_PROVSN, RuleConstants.EVENT_PROVSN);
		}

		amzMethodRule = ruleDAO.getAmountRule(AmortizationConstants.AMZ_METHOD_RULE,
				AmortizationConstants.AMZ_METHOD_RULE, AmortizationConstants.AMZ_METHOD_RULE);

		//to hold the exception till the process completed for all the customers
		List<Exception> exceptions = new ArrayList<Exception>(1);

		cursorItemReader.open(context.getStepContext().getStepExecution().getExecutionContext());

		CustomerQueuing customerQueuing = new CustomerQueuing();
		while ((customerQueuing = cursorItemReader.read()) != null) {
			status.setProcessedRecords(processedCount++);
			BatchUtil.setExecutionStatus(context, status);

			long custId = customerQueuing.getCustID();

			try {
				//update start
				customerQueuingDAO.startEODForCID(custId);

				// begin transaction
				txStatus = transactionManager.getTransaction(txDef);

				CustEODEvent custEODEvent = new CustEODEvent();
				custEODEvent.setProvisionRule(provisionRule);
				custEODEvent.setAmzMethodRule(amzMethodRule);

				if (customerQueuing.isLoanExist()) {
					customerQueuing.setLoanExist(false);
					Customer customer = customerDAO.getCustomerEOD(custId);
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
				snapshotService.doSnapshotPreparation(appDate, custId);

				//commit
				transactionManager.commit(txStatus);

				custEODEvent.getFinEODEvents().clear();
				custEODEvent = null;

			} catch (Exception e) {
				status.setFailedRecords(failedCount++);
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
			exceptions = null;
			throw exception;
		}

		logger.debug("COMPLETE: Micro EOD On {}", appDate);

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
		logger.error("Cause {}", exp.getCause());
		logger.error("Message {}", exp.getMessage());
		logger.error("LocalizedMessage {}", exp.getLocalizedMessage());
		logger.error("StackTrace {}", exp);

	}

	@Autowired
	public void setEodService(EodService eodService) {
		this.eodService = eodService;
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Autowired
	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Autowired
	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setSnapshotService(SnapshotService snapshotService) {
		this.snapshotService = snapshotService;
	}

}
