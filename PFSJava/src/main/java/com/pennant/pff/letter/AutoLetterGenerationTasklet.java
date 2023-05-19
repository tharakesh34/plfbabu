package com.pennant.pff.letter;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.letter.Letter;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.letter.service.LetterService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class AutoLetterGenerationTasklet implements Tasklet {
	private static Logger logger = LogManager.getLogger(AutoLetterGenerationTasklet.class);

	private static final String START_MSG = "AutoLetterGeneration success response process started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String FAILED_MSG = "AutoLetterGeneration success response process failed on {} for the Presentment-ID {}";
	private static final String SUCCESS_MSG = "AutoLetterGeneration success response process completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_LOG = "Cause {}\nMessage {}\n LocalizedMessage {}\nStackTrace {}";

	private BatchJobQueueDAO ebjqDAO;
	private DataSourceTransactionManager transactionManager;
	private AutoLetterGenerationEngine autoLetterGenerationEngine;
	private LetterService letterService;

	public AutoLetterGenerationTasklet(BatchJobQueueDAO ebjqDAO, DataSourceTransactionManager transactionManager) {
		super();
		this.ebjqDAO = ebjqDAO;
		this.transactionManager = transactionManager;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();

		int threadID = Integer.parseInt(stepExecutionContext.get("THREAD_ID").toString());

		long queueID = ebjqDAO.getNextValue();

		Long letterID = ebjqDAO.getIdBySequence(queueID);

		if (letterID == null) {
			return RepeatStatus.FINISHED;
		}

		Date appDate = SysParamUtil.getAppDate();

		String strAppDate = DateUtil.formatToLongDate(appDate);
		String strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);

		logger.info(START_MSG, strSysDate, strAppDate, threadID);

		while (letterID != null) {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setId(queueID);
			jobQueue.setThreadId(threadID);

			try {
				jobQueue.setProgress(EodConstants.PROGRESS_IN_PROCESS);
				ebjqDAO.updateProgress(jobQueue);

				boolean status = generateLetter(letterID);

				if (status) {
					jobQueue.setProgress(EodConstants.PROGRESS_SUCCESS);
					ebjqDAO.updateProgress(jobQueue);
				} else {
					jobQueue.setProgress(EodConstants.PROGRESS_FAILED);
					ebjqDAO.updateProgress(jobQueue);
				}
			} catch (Exception e) {
				String errorMsg = ExceptionUtils.getStackTrace(e);

				if (errorMsg != null && errorMsg.length() > 2000) {
					errorMsg = errorMsg.substring(0, 1999);
				}

				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
				logger.info(FAILED_MSG, strSysDate, letterID);

				jobQueue.setError(errorMsg);
				jobQueue.setProgress(EodConstants.PROGRESS_FAILED);

				ebjqDAO.updateProgress(jobQueue);

				autoLetterGenerationEngine.updateResponse(letterID, e);
			}

			queueID = ebjqDAO.getNextValue();
			letterID = ebjqDAO.getIdBySequence(queueID);
		}

		String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		logger.info(SUCCESS_MSG, sysDate, strAppDate, threadID);

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}

	private boolean generateLetter(long letterID) {
		Letter letter = letterService.generate(letterID);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

		try {

			transactionManager.commit(transactionStatus);
		} catch (Exception e) {
			transactionManager.rollback(transactionStatus);
			throw e;
		}

		return true;
	}

	@Autowired
	public void setEbjqDAO(BatchJobQueueDAO ebjqDAO) {
		this.ebjqDAO = ebjqDAO;
	}

}
