package com.pennanttech.pff.provision.eod.tasklet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.provision.ProvisionReversalStage;
import com.pennanttech.pff.provision.model.Provision;
import com.pennanttech.pff.provision.service.ProvisionService;

public class ProvisionClacTaskLet implements Tasklet {
	private Logger logger = LogManager.getLogger(ProvisionClacTaskLet.class);

	private PlatformTransactionManager transactionManager;
	private DataSource dataSource;
	private ProvisionService provisionService;

	private static final String QUEUE_QUERY = "Select FinReference From Provision_Calc_Queue Where ThreadID = ? and Progress = ?";

	private static final String START_MSG = "Provision Calculation/Reversal started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String FAILED_MSG = "Provision Calculation/Reversal failed on {} for the FinReference {}";
	private static final String SUCCESS_MSG = "Provision Calculation/Reversal completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String EXCEPTION_MSG = "Provision Calculation/Reversal failed on {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_LOG = "Cause {}\nMessage {}\nLocalizedMessage {}\nStackTrace {}";

	public static AtomicLong processedCount = new AtomicLong(0);
	public static AtomicLong failedCount = new AtomicLong(0);

	public ProvisionClacTaskLet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Map<String, Object> stepExecutionContext = context.getStepContext().getStepExecutionContext();

		Date appDate = SysParamUtil.getAppDate();

		Date monthStart = DateUtil.getMonthStart(appDate);
		Date monthEnd = DateUtil.getMonthEnd(appDate);

		boolean isMonthStart = appDate.compareTo(monthStart) == 0;
		boolean isMonthEnd = appDate.compareTo(monthEnd) == 0;

		if (!isMonthStart && !isMonthEnd) {
			return RepeatStatus.FINISHED;
		}

		final int threadId = Integer.parseInt(stepExecutionContext.get(EodConstants.THREAD).toString());

		String strSysDate = DateUtil.format(appDate, DateFormat.LONG_DATE_TIME);
		String strAppDate = DateUtil.formatToLongDate(appDate);

		logger.info(START_MSG, strSysDate, strAppDate, threadId);

		BatchUtil.setExecutionStatus(context, StepUtil.PROVISION_CALC);

		JdbcCursorItemReader<String> itemReader = new JdbcCursorItemReader<>();
		itemReader.setSql(QUEUE_QUERY);
		itemReader.setDataSource(dataSource);
		itemReader.setRowMapper((rs, rowNum) -> {
			return rs.getString("FinReference");
		});
		itemReader.setPreparedStatementSetter(ps -> {
			ps.setLong(1, threadId);
			ps.setInt(2, EodConstants.PROGRESS_WAIT);
		});

		itemReader.open(context.getStepContext().getStepExecution().getExecutionContext());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		List<Exception> exceptions = new ArrayList<>(1);
		boolean reversalReq = ImplementationConstants.PROVISION_REVERSAL_REQ;
		boolean reveralOnSOM = ImplementationConstants.PROVISION_REVERSAL_STAGE == ProvisionReversalStage.SOM;
		boolean reveralOnEOM = ImplementationConstants.PROVISION_REVERSAL_STAGE == ProvisionReversalStage.EOM;

		String finReference = null;

		while ((finReference = itemReader.read()) != null) {
			try {
				provisionService.updateProgress(finReference, EodConstants.PROGRESS_IN_PROCESS);

				Long linkedTranId = provisionService.getLinkedTranId(finReference);

				if (isMonthStart) {
					if (reversalReq && reveralOnSOM && linkedTranId != null) {
						txStatus = transactionManager.getTransaction(txDef);

						if (ImplementationConstants.PROVISION_POSTINGS_REQ) {
							provisionService.doReversal(linkedTranId);
						}

						provisionService.updateProgress(finReference, EodConstants.PROGRESS_SUCCESS);

						transactionManager.commit(txStatus);
					}

					StepUtil.PROVISION_CALC.setProcessedRecords(processedCount.incrementAndGet());
					continue;
				}

				Provision provision = provisionService.getProvision(finReference, appDate);

				if (provision != null) {
					txStatus = transactionManager.getTransaction(txDef);

					if (ImplementationConstants.PROVISION_POSTINGS_REQ) {
						if (reversalReq && reveralOnEOM && linkedTranId != null) {
							provisionService.doReversal(linkedTranId);
						}

						provisionService.doPost(provision);
					}

					if (provision.getId() == null) {
						provisionService.save(provision);
					} else {
						provisionService.update(provision);
					}

					provisionService.updateProgress(finReference, EodConstants.PROGRESS_SUCCESS);

					transactionManager.commit(txStatus);
				}

				StepUtil.PROVISION_CALC.setProcessedRecords(processedCount.incrementAndGet());
			} catch (Exception e) {
				StepUtil.PROVISION_CALC.setFailedRecords(failedCount.incrementAndGet());

				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}

				exceptions.add(e);
				strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);

				logger.info(FAILED_MSG, strSysDate, finReference);

				provisionService.updateProgress(finReference, EodConstants.PROGRESS_FAILED);
			} finally {
				txStatus = null;
			}
		}

		itemReader.close();

		if (!exceptions.isEmpty()) {
			String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
			logger.info(EXCEPTION_MSG, sysDate, strAppDate, threadId);

			throw exceptions.get(0);
		}

		String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		logger.info(SUCCESS_MSG, sysDate, strAppDate, threadId);

		BatchUtil.setExecutionStatus(context, StepUtil.PROVISION_CALC);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Autowired
	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}

}