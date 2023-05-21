package com.pennant.pff.letter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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
import com.pennanttech.pff.eod.step.StepUtil;

public class LetterGenerationTasklet implements Tasklet {
	private static Logger logger = LogManager.getLogger(LetterGenerationTasklet.class);

	private static final String START_MSG = "Letter Generation Process started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String FAILED_MSG = "Letter Generation Process failed on {} for the Presentment-ID {}";
	private static final String SUCCESS_MSG = "Letter Generation Process completed at {} for the APP_DATE {} with THREAD_ID {}";

	private BatchJobQueueDAO ebjqDAO;

	private LetterService letterService;

	private DataSourceTransactionManager transactionManager;

	protected static AtomicLong processedCount = new AtomicLong(0);
	protected static AtomicLong failedCount = new AtomicLong(0);

	public LetterGenerationTasklet(BatchJobQueueDAO ebjqDAO) {
		super();
		this.ebjqDAO = ebjqDAO;
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

		List<Exception> exceptions = new ArrayList<>(1);

		while (letterID != null) {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setId(queueID);
			jobQueue.setThreadId(threadID);

			try {
				jobQueue.setProgress(EodConstants.PROGRESS_IN_PROCESS);
				ebjqDAO.updateProgress(jobQueue);

				boolean status = generateLetter(letterID, appDate);

				if (status) {
					jobQueue.setProgress(EodConstants.PROGRESS_SUCCESS);
					ebjqDAO.updateProgress(jobQueue);
				} else {
					jobQueue.setProgress(EodConstants.PROGRESS_FAILED);
					ebjqDAO.updateProgress(jobQueue);
				}

				StepUtil.LETTER_GENERATION.setProcessedRecords(processedCount.incrementAndGet());
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);

				exceptions.add(e);

				StepUtil.LETTER_GENERATION.setProcessedRecords(failedCount.incrementAndGet());

				String errorMsg = ExceptionUtils.getStackTrace(e);

				if (errorMsg != null && errorMsg.length() > 2000) {
					errorMsg = errorMsg.substring(0, 1999);
				}

				strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
				logger.info(FAILED_MSG, strSysDate, letterID);

				jobQueue.setError(errorMsg);
				jobQueue.setProgress(EodConstants.PROGRESS_FAILED);

				ebjqDAO.updateProgress(jobQueue);

			}

			queueID = ebjqDAO.getNextValue();
			letterID = ebjqDAO.getIdBySequence(queueID);
		}

		if (!exceptions.isEmpty()) {
			throw exceptions.get(0);
		}

		String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		logger.info(SUCCESS_MSG, sysDate, strAppDate, threadID);

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}

	private boolean generateLetter(long letterID, Date appDate) {
		Letter letter = letterService.generate(letterID, appDate);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

		try {

			letterService.sendEmail(letter);

			transactionManager.commit(transactionStatus);
		} catch (Exception e) {
			transactionManager.rollback(transactionStatus);
			throw e;
		}

		return true;
	}

	@Autowired
	public void setLetterService(LetterService letterService) {
		this.letterService = letterService;
	}

	@Autowired
	public void setTransactionManager(DataSourceTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
