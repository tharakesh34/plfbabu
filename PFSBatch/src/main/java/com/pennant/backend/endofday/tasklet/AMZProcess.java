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
 * FileName    		:  AMZProcess.java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  24-12-2017															*
 *                                                                  
 * Modified Date    :  24-12-2017															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-12-2017       Pennant	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.ProjectedAmortizationService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.model.amortization.AmortizationQueuing;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.cache.util.FinanceConfigCache;

public class AMZProcess implements Tasklet {

	private Logger logger = Logger.getLogger(AMZProcess.class);

	private DataSource dataSource;
	private PlatformTransactionManager transactionManager;

	private ProjectedAmortizationDAO projectedAmortizationDAO;
	private ProjectedAmortizationService projectedAmortizationService;

	private static final String financeSQL = "Select FinReference, CustID from AmortizationQueuing  Where ThreadID = :ThreadId and Progress = :Progress";

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		Date appDate = DateUtility.getAppDate();
		logger.debug("START : Amortization On : " + appDate);

		final int threadId = (int) context.getStepContext().getStepExecutionContext().get(AmortizationConstants.THREAD);
		logger.info("AMZ Process Statred by the Thread : " + threadId + " with date " + appDate.toString());

		TransactionStatus txStatus = null;
		List<ProjectedAccrual> finProjAccList = null;
		List<ProjectedAmortization> incomeAMZList = null;

		List<Exception> exceptions = new ArrayList<Exception>(1);
		AmortizationQueuing amortizationQueuing = new AmortizationQueuing();
		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		JdbcCursorItemReader<AmortizationQueuing> cursorItemReader = new JdbcCursorItemReader<AmortizationQueuing>();

		cursorItemReader.setSql(financeSQL);
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setRowMapper(ParameterizedBeanPropertyRowMapper.newInstance(AmortizationQueuing.class));

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, threadId);
				ps.setInt(2, AmortizationConstants.PROGRESS_WAIT);
			}
		});

		cursorItemReader.open(context.getStepContext().getStepExecution().getExecutionContext());
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		Date amzMonth = (Date) context.getStepContext().getJobExecutionContext()
				.get(AmortizationConstants.AMZ_MONTHEND);
		Date amzMonthStart = DateUtility.getMonthStart(amzMonth);

		while ((amortizationQueuing = cursorItemReader.read()) != null) {

			String finReference = amortizationQueuing.getFinReference();

			try {

				txStatus = this.transactionManager.getTransaction(txDef);

				// SKIP THIS STEP : update start and begin transaction
				// this.projectedAmortizationDAO.startEODForFinRef(finReference);

				FinEODEvent finEODEvent = new FinEODEvent();
				FinanceMain finMain = this.projectedAmortizationService.getFinanceForIncomeAMZ(finReference);

				// get income/expense details
				incomeAMZList = this.projectedAmortizationDAO.getIncomeAMZDetailsByRef(finReference);

				// Actual Amortization Calculation and Saving (Table : ProjectedIncomeAMZ).
				if (!incomeAMZList.isEmpty()) {

					finEODEvent.setAppDate(appDate);
					finEODEvent.setEventFromDate(amzMonth);

					finEODEvent.setFinanceMain(finMain);
					finEODEvent.setIncomeAMZList(incomeAMZList);

					if (!StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_CANCELLED)
							|| finMain.getClosedDate().compareTo(amzMonth) > 0) {

						// get future ACCRUALS
						finProjAccList = this.projectedAmortizationDAO
								.getFutureProjectedAccrualsByFinRef(finMain.getFinReference(), amzMonthStart);
						finEODEvent.setProjectedAccrualList(finProjAccList);
					}

					// Amortization Calculation and Saving
					this.projectedAmortizationService.processMonthEndIncomeAMZ(finEODEvent);
				}

				// update status and commit transaction
				this.projectedAmortizationDAO.updateStatus(finReference, AmortizationConstants.PROGRESS_SUCCESS);
				this.transactionManager.commit(txStatus);

				finEODEvent = null;
				incomeAMZList = null;
				finProjAccList = null;

			} catch (Exception e) {
				logError(e);
				transactionManager.rollback(txStatus);
				exceptions.add(e);
				updateFailed(finReference);
			}

			//clear data after the process
			amortizationQueuing = null;
		}
		cursorItemReader.close();

		if (!exceptions.isEmpty()) {
			Exception exception = new Exception(exceptions.get(0));
			exceptions.clear();
			exceptions = null;
			throw exception;
		}

		logger.debug("COMPLETE : Amortization On : " + appDate);
		return RepeatStatus.FINISHED;
	}

	// helpers

	/**
	 * 
	 * @param finReference
	 */
	public void updateFailed(String finReference) {

		AmortizationQueuing amortizationQueuing = new AmortizationQueuing();

		amortizationQueuing.setFinReference(finReference);
		amortizationQueuing.setEndTime(DateUtility.getSysDate());

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
		logger.error("Cause : " + exp.getCause());
		logger.error("Message : " + exp.getMessage());
		logger.error("LocalizedMessage : " + exp.getLocalizedMessage());
		logger.error("StackTrace : ", exp);

	}

	/**
	 * @param fintype
	 * @return
	 */
	public final FinanceType getFinanceType(String fintype) {
		return FinanceConfigCache.getCacheFinanceType(StringUtils.trimToEmpty(fintype));

	}

	// getters / setters

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setProjectedAmortizationService(ProjectedAmortizationService projectedAmortizationService) {
		this.projectedAmortizationService = projectedAmortizationService;
	}

	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}
}