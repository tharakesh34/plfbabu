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
 * FileName : AMZProcess.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 24-12-2017 *
 * 
 * Modified Date : 24-12-2017 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-12-2017 Pennant 0.1 * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.endofday.tasklet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.ProjectedAmortizationService;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.model.amortization.AmortizationQueuing;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.EODUtil;

public class AMZProcess implements Tasklet {
	private Logger logger = LogManager.getLogger(AMZProcess.class);

	private DataSource dataSource;
	private PlatformTransactionManager transactionManager;
	private ProjectedAmortizationDAO projectedAmortizationDAO;
	private ProjectedAmortizationService projectedAmortizationService;

	private static final String FINANCE_SQL = "Select FinID, CustID from AmortizationQueuing  Where ThreadID = ? and Progress = ?";

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date appDate = EODUtil.getDate("APP_DATE", context);

		logger.debug("START: Amortization On {}", appDate);

		Map<String, Object> stepExecutionContext = context.getStepContext().getStepExecutionContext();
		final int threadId = Integer.parseInt(stepExecutionContext.get(EodConstants.THREAD).toString());

		DataEngineStatus status = (DataEngineStatus) stepExecutionContext.get("amzProcess:" + String.valueOf(threadId));
		long processedCount = 1;
		long failedCount = 0;
		logger.info("process Statred by the Thread {} with date {}", threadId, appDate.toString());

		TransactionStatus txStatus = null;
		List<ProjectedAccrual> finProjAccList = null;
		List<ProjectedAmortization> incomeAMZList = null;

		List<Exception> exceptions = new ArrayList<Exception>(1);
		AmortizationQueuing amortizationQueuing;
		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		JdbcCursorItemReader<AmortizationQueuing> cursorItemReader = new JdbcCursorItemReader<AmortizationQueuing>();

		cursorItemReader.setSql(FINANCE_SQL);
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setRowMapper(new RowMapper<AmortizationQueuing>() {

			@Override
			public AmortizationQueuing mapRow(ResultSet rs, int rowNum) throws SQLException {
				AmortizationQueuing amz = new AmortizationQueuing();

				amz.setFinID(rs.getLong("FinID"));
				amz.setCustID(rs.getLong("CustID"));

				return amz;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, String.valueOf(threadId));
				ps.setString(2, String.valueOf(AmortizationConstants.PROGRESS_WAIT));
			}
		});

		cursorItemReader.open(context.getStepContext().getStepExecution().getExecutionContext());
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		Date amzMonth = (Date) context.getStepContext().getJobExecutionContext()
				.get(AmortizationConstants.AMZ_MONTHEND);
		Date amzMonthStart = DateUtil.getMonthStart(amzMonth);

		while ((amortizationQueuing = cursorItemReader.read()) != null) {
			status.setProcessedRecords(processedCount++);
			BatchUtil.setExecutionStatus(context, status);
			long finID = amortizationQueuing.getFinID();

			try {
				txStatus = this.transactionManager.getTransaction(txDef);

				// SKIP THIS STEP : update start and begin transaction
				// this.projectedAmortizationDAO.startEODForFinRef(finReference);

				FinEODEvent finEODEvent = new FinEODEvent();
				FinanceMain fm = this.projectedAmortizationService.getFinanceForIncomeAMZ(finID);

				// get income/expense details
				incomeAMZList = this.projectedAmortizationDAO.getIncomeAMZDetailsByRef(finID);

				// Actual Amortization Calculation and Saving (Table : ProjectedIncomeAMZ).
				if (!incomeAMZList.isEmpty()) {
					finEODEvent.setAppDate(appDate);
					finEODEvent.setEventFromDate(amzMonth);

					finEODEvent.setFinanceMain(fm);
					finEODEvent.setIncomeAMZList(incomeAMZList);

					if (!FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())
							|| fm.getClosedDate().compareTo(amzMonth) > 0) {

						// get future ACCRUALS
						finProjAccList = this.projectedAmortizationDAO.getFutureProjectedAccrualsByFinRef(finID,
								amzMonthStart);
						finEODEvent.setProjectedAccrualList(finProjAccList);
					}

					// Amortization Calculation and Saving
					this.projectedAmortizationService.processMonthEndIncomeAMZ(finEODEvent);
				}

				// update status and commit transaction
				this.projectedAmortizationDAO.updateStatus(finID, AmortizationConstants.PROGRESS_SUCCESS);
				this.transactionManager.commit(txStatus);
			} catch (Exception e) {
				status.setFailedRecords(failedCount++);
				logError(e);
				transactionManager.rollback(txStatus);
				exceptions.add(e);
				updateFailed(finID);
			}
		}
		cursorItemReader.close();

		if (CollectionUtils.isNotEmpty(exceptions)) {
			logger.warn(exceptions.get(0).getMessage());
			Exception exception = exceptions.get(0);
			exceptions.clear();
			throw exception;
		}

		logger.info("COMPLETE : Amortization On {}", appDate);
		return RepeatStatus.FINISHED;
	}

	public void updateFailed(long finID) {
		AmortizationQueuing amortizationQueuing = new AmortizationQueuing();

		amortizationQueuing.setFinID(finID);
		amortizationQueuing.setEndTime(DateUtil.getSysDate());

		// reset thread for reallocation and reset to "wait", to re run only failed cases.
		amortizationQueuing.setThreadId(0);
		amortizationQueuing.setProgress(AmortizationConstants.PROGRESS_WAIT);

		projectedAmortizationDAO.updateFailed(amortizationQueuing);
	}

	/**
	 * 
	 * @param exp
	 */
	private void logError(Exception exp) {
		logger.error("Cause {}", exp.getCause());
		logger.error("Message {}", exp.getMessage());
		logger.error("LocalizedMessage {}", exp.getLocalizedMessage());
		logger.error("StackTrace {}", exp);

	}

	/**
	 * @param fintype
	 * @return
	 */
	public final FinanceType getFinanceType(String fintype) {
		return FinanceConfigCache.getCacheFinanceType(StringUtils.trimToEmpty(fintype));
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Autowired
	public void setProjectedAmortizationService(ProjectedAmortizationService projectedAmortizationService) {
		this.projectedAmortizationService = projectedAmortizationService;
	}

	@Autowired
	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}
}