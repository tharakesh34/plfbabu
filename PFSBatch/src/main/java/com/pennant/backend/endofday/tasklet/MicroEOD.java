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
 * FileName : MicroEOD.java *
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
 * 26-04-2011 Pennant 0.1 * * 04-05-2018 Vinay 0.2 As discuss with Satya Naga Prasad * Micro EOD code changed * * * * *
 * * *
 ********************************************************************************************
 */
package com.pennant.backend.endofday.tasklet;

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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ProvisionConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.eod.EodService;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennant.pff.eod.cache.RuleConfigCache;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.eod.EODUtil;

public class MicroEOD implements Tasklet {
	private Logger logger = LogManager.getLogger(MicroEOD.class);

	private EodService eodService;
	private CustomerQueuingDAO customerQueuingDAO;
	private PlatformTransactionManager transactionManager;
	private DataSource dataSource;
	private CustomerDAO customerDAO;

	// ##_0.2
	private static final String CUSTOMER_SQL = "Select CustID, LoanExist, LimitRebuild from CustomerQueuing  Where ThreadID = ? and Progress= ?";

	public MicroEOD() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		EventProperties eventProperties = EODUtil.getEventProperties(EODUtil.EVENT_PROPERTIES, context);

		Map<String, Object> stepExecutionContext = context.getStepContext().getStepExecutionContext();

		Date appDate = eventProperties.getAppDate();
		final int threadId = Integer.parseInt(stepExecutionContext.get(EodConstants.THREAD).toString());

		String strAppDate = DateUtil.formatToLongDate(appDate);
		String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);

		logger.info("Micro EOD Start on {} for the application date {} with Thread ID {}", sysDate, strAppDate,
				threadId);

		DataEngineStatus status = (DataEngineStatus) stepExecutionContext.get("microEOD:" + String.valueOf(threadId));
		long processedCount = 1;
		long failedCount = 0;

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		JdbcCursorItemReader<CustomerQueuing> cursorItemReader = new JdbcCursorItemReader<CustomerQueuing>();
		cursorItemReader.setSql(CUSTOMER_SQL);
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setRowMapper((rs, rowNum) -> {
			CustomerQueuing cq = new CustomerQueuing();

			cq.setCustID(rs.getLong("CustID"));
			cq.setLoanExist(rs.getBoolean("LoanExist"));
			cq.setLimitRebuild(rs.getBoolean("LimitRebuild"));

			return cq;
		});
		cursorItemReader.setPreparedStatementSetter(ps -> {
			ps.setLong(1, threadId);
			ps.setInt(2, EodConstants.PROGRESS_WAIT);
		});

		// Get the Rules
		String provisionRule = null;
		String amzMethodRule = null;

		String provRule = eventProperties.getProvRule();

		if (provRule != null) {
			String moduleProvsn = RuleConstants.MODULE_PROVSN;
			String eventProvsn = RuleConstants.EVENT_PROVSN;

			provisionRule = RuleConfigCache.getCacheRuleCode(provRule, moduleProvsn, eventProvsn);
		}

		String methodRule = AmortizationConstants.AMZ_METHOD_RULE;
		amzMethodRule = RuleConfigCache.getCacheRuleCode(methodRule, methodRule, methodRule);
		// Load SMT Parameters here and set into custEODEvent to reduce the number of DB hits

		boolean customerProvision = false;
		Date provisionEffectiveDate = null;
		String provisionBooks = null;

		if (ImplementationConstants.ALLOW_NPA_PROVISION) {
			logger.info("NPA and Provisining Enabled");

			provisionBooks = eventProperties.getProvisionBooks();
			String npaTagging = eventProperties.getNpaTagging();

			if (ProvisionConstants.NPA_TAGGING_CUSTOMER.equals(npaTagging)) {
				customerProvision = true;
			}

			String provEffPostDate = eventProperties.getProvEffPostDate();

			if (PennantConstants.NO.equals(provEffPostDate)) {
				provisionEffectiveDate = eventProperties.getPostDate();
			}

			logger.info("ProvisionBooks  {}", provisionBooks);
			logger.info("NPA Tagging {}", npaTagging);
			logger.info("Customer Provision {}", customerProvision);
		}

		// to hold the exception till the process completed for all the customers
		List<Exception> exceptions = new ArrayList<Exception>(1);

		cursorItemReader.open(context.getStepContext().getStepExecution().getExecutionContext());

		CustomerQueuing customerQueuing;
		while ((customerQueuing = cursorItemReader.read()) != null) {
			status.setProcessedRecords(processedCount++);
			BatchUtil.setExecutionStatus(context, status);

			long custId = customerQueuing.getCustID();

			try {
				sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
				logger.info("Micro EOD started on {} for the customer ID {}", sysDate, custId);
				customerQueuingDAO.startEODForCID(custId, appDate);

				txStatus = transactionManager.getTransaction(txDef);

				CustEODEvent custEODEvent = new CustEODEvent();
				custEODEvent.setProvisionRule(provisionRule);
				custEODEvent.setAmzMethodRule(amzMethodRule);

				custEODEvent.setExecuteNPAaAndProvision(ImplementationConstants.ALLOW_NPA_PROVISION);
				custEODEvent.setProvisionBooks(provisionBooks);
				custEODEvent.setCustomerProvision(customerProvision);
				custEODEvent.setProvisionEffectiveDate(provisionEffectiveDate);
				custEODEvent.setEventProperties(eventProperties);

				if (customerQueuing.isLoanExist()) {
					customerQueuing.setLoanExist(false);
					Customer customer = customerDAO.getCustomerEOD(custId);
					custEODEvent.setCustomer(customer);
					custEODEvent.setEodDate(appDate);
					custEODEvent.setEodValueDate(appDate);

					eodService.doProcess(custEODEvent);
					eodService.doUpdate(custEODEvent, customerQueuing.isLimitRebuild());
				} else {
					logger.info("There is no active loans exists for the customer ID {}", custId);
					if (customerQueuing.isLimitRebuild()) {
						eodService.processCustomerRebuild(custId, true);
					}
				}

				logger.info("Updating the EOD status for the customer ID {}", custId);
				customerQueuingDAO.updateStatus(custId, EodConstants.PROGRESS_SUCCESS, appDate);

				transactionManager.commit(txStatus);
				sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
				logger.info("Micro EOD completed on {} for the customer ID {}", sysDate, custId);

				custEODEvent.getFinEODEvents().clear();

			} catch (Exception e) {
				status.setFailedRecords(failedCount++);
				logError(e);
				transactionManager.rollback(txStatus);
				exceptions.add(e);
				sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
				logger.info("Micro EOD failed on {} for the customer ID {}", sysDate, custId);
				updateFailed(custId);
			}
		}

		cursorItemReader.close();

		if (!exceptions.isEmpty()) {
			Exception exception = new Exception(exceptions.get(0));
			exceptions.clear();
			sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
			logger.info("Micro EOD failed on {}\n, Application Date {}\n Thread ID {}", sysDate, strAppDate, threadId);
			throw exception;
		}

		sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		logger.info("Micro EOD Completed on {},  for the application Date {} with Thread ID {}", sysDate, strAppDate,
				threadId);

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
		// reset thread for reallocation
		customerQueuing.setThreadId(0);
		// reset to "wait", to re run only failed cases.
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
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}
}
