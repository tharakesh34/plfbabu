package com.pennant.pff.presentment.tasklet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.eventproperties.service.EventPropertiesService;
import com.pennant.backend.eventproperties.service.impl.EventPropertiesServiceImpl.EventType;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class SuccessResponseTasklet implements Tasklet {
	private final Logger logger = LogManager.getLogger(SuccessResponseTasklet.class);

	private static final String START_MSG = "Presentment success response process started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String FAILED_MSG = "Presentment success response process failed on {} for the Presentment-ID {}";
	private static final String SUCCESS_MSG = "Presentment success response process completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String EXCEPTION_MSG = "Presentment success response process failed on {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_LOG = "Cause {}\nMessage {}\n LocalizedMessage {}\nStackTrace {}";

	private static final String SUCCESS = RepayConstants.PEXC_SUCCESS;
	private static final String FAILURE = RepayConstants.PEXC_FAILURE;

	private PresentmentEngine presentmentEngine;
	private BatchJobQueueDAO ebjqDAO;
	private DataSourceTransactionManager transactionManager;
	private EventPropertiesService eventPropertiesService;

	public SuccessResponseTasklet(BatchJobQueueDAO ebjqDAO, PresentmentEngine presentmentEngine,
			DataSourceTransactionManager transactionManager, EventPropertiesService eventPropertiesService) {
		super();
		this.ebjqDAO = ebjqDAO;
		this.presentmentEngine = presentmentEngine;
		this.transactionManager = transactionManager;
		this.eventPropertiesService = eventPropertiesService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		List<Exception> exceptions = new ArrayList<>(1);

		Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();

		int threadID = Integer.parseInt(stepExecutionContext.get("THREAD_ID").toString());

		long queueID = ebjqDAO.getNextValue();

		Long responseID = ebjqDAO.getIdBySequence(queueID);

		if (responseID == null) {
			return RepeatStatus.FINISHED;
		}

		EventProperties eventProperties = eventPropertiesService
				.getEventProperties(EventType.PRESENTMENT_RESPONSE_UPLOAD);
		Date appDate = SysParamUtil.getAppDate();

		String strAppDate = DateUtil.formatToLongDate(appDate);
		String strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);

		logger.info(START_MSG, strSysDate, strAppDate, threadID);

		while (responseID != null) {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setId(queueID);
			jobQueue.setThreadId(threadID);

			try {
				jobQueue.setProgress(EodConstants.PROGRESS_IN_PROCESS);
				ebjqDAO.updateProgress(jobQueue);

				boolean status = processResponse(responseID, appDate, eventProperties);

				if (status) {
					jobQueue.setProgress(EodConstants.PROGRESS_SUCCESS);
					ebjqDAO.updateProgress(jobQueue);
				} else {
					jobQueue.setProgress(EodConstants.PROGRESS_FAILED);
					ebjqDAO.updateProgress(jobQueue);
				}

				presentmentEngine.updateResposeStatus(responseID, "", "", EodConstants.PROGRESS_SUCCESS);
			} catch (Exception e) {
				String errorMessage = e.getMessage();
				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				if (exceptions.isEmpty()) {
					exceptions.add(e);
				}

				strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
				logger.info(FAILED_MSG, strSysDate, responseID);

				jobQueue.setProgress(EodConstants.PROGRESS_FAILED);
				ebjqDAO.updateProgress(jobQueue);

				presentmentEngine.updateResposeStatus(responseID, FAILURE, errorMessage, EodConstants.PROGRESS_FAILED);
			}

			queueID = ebjqDAO.getNextValue();
			responseID = ebjqDAO.getIdBySequence(queueID);
		}

		if (!exceptions.isEmpty()) {
			String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
			logger.info(EXCEPTION_MSG, sysDate, strAppDate, threadID);

			throw exceptions.get(0);
		}

		String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		logger.info(SUCCESS_MSG, sysDate, strAppDate, threadID);

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}

	private boolean processResponse(long responseID, Date appDate, EventProperties eventProperties) {
		PresentmentDetail pd = presentmentEngine.getPresentmenForResponse(responseID);
		pd.setAppDate(appDate);
		pd.setEventProperties(eventProperties);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

		try {
			presentmentEngine.processResponse(pd);
			transactionManager.commit(transactionStatus);
		} catch (AppException e) {
			transactionManager.rollback(transactionStatus);
			return false;
		} catch (Exception e) {
			transactionManager.rollback(transactionStatus);
			throw e;
		}

		return true;
	}

}
