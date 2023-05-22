package com.pennanttech.pff.refund.eod.tasklet;

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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.autorefund.service.AutoRefundService;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class AutoRefundTasklet implements Tasklet {
	private Logger logger = LogManager.getLogger(AutoRefundTasklet.class);

	private BatchJobQueueDAO ebjqDAO;
	private AutoRefundService autoRefundService;
	private PlatformTransactionManager transactionManager;

	private static final String START_MSG = "Auto Refund started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String SUCCESS_MSG = "Auto Refund completed at {} for the APP_DATE {} with THREAD_ID {}";

	public static final AtomicLong processedCount = new AtomicLong(0);
	public static final AtomicLong failedCount = new AtomicLong(0);

	public AutoRefundTasklet(BatchJobQueueDAO ebjqDAO) {
		super();
		this.ebjqDAO = ebjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		logger.debug(Literal.ENTERING);

		Map<String, Object> stepExecutionContext = context.getStepContext().getStepExecutionContext();

		int threadID = Integer.parseInt(stepExecutionContext.get("THREAD_ID").toString());

		long queueID = ebjqDAO.getNextValue();

		Long finID = ebjqDAO.getIdBySequence(queueID);

		if (finID == null) {
			return RepeatStatus.FINISHED;
		}

		Date appDate = SysParamUtil.getAppDate();

		String strAppDate = DateUtil.formatToLongDate(appDate);
		String strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);

		logger.info(START_MSG, strSysDate, strAppDate, threadID);

		List<Exception> exceptions = new ArrayList<>(1);

		while (finID != null) {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setId(queueID);
			jobQueue.setThreadId(threadID);

			try {
				jobQueue.setProgress(EodConstants.PROGRESS_IN_PROCESS);
				ebjqDAO.updateProgress(jobQueue);

				boolean status = processRefund(finID);

				if (status) {
					jobQueue.setProgress(EodConstants.PROGRESS_SUCCESS);
					ebjqDAO.updateProgress(jobQueue);
					StepUtil.AUTO_REFUND_PROCESS.setProcessedRecords(processedCount.incrementAndGet());
				} else {
					jobQueue.setProgress(EodConstants.PROGRESS_FAILED);
					ebjqDAO.updateProgress(jobQueue);
					StepUtil.AUTO_REFUND_PROCESS.setProcessedRecords(failedCount.incrementAndGet());
				}
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);

				exceptions.add(e);

				StepUtil.AUTO_REFUND_PROCESS.setProcessedRecords(failedCount.incrementAndGet());

				String errorMsg = ExceptionUtils.getStackTrace(e);

				if (errorMsg != null && errorMsg.length() > 2000) {
					errorMsg = errorMsg.substring(0, 1999);
				}

				jobQueue.setError(errorMsg);
				jobQueue.setProgress(EodConstants.PROGRESS_FAILED);

				ebjqDAO.updateProgress(jobQueue);
			}

			queueID = ebjqDAO.getNextValue();

			finID = ebjqDAO.getIdBySequence(queueID);
		}

		if (!exceptions.isEmpty()) {
			throw exceptions.get(0);
		}

		String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		logger.info(SUCCESS_MSG, sysDate, strAppDate, threadID);

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}

	private boolean processRefund(Long finID) {

		EventProperties ep = EODUtil.EVENT_PROPS;

		AutoRefundLoan arl = autoRefundService.getAutoRefundDetails(finID, ep);

		if (arl == null) {
			return true;
		}

		autoRefundService.executeRefund(arl);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

		try {
			autoRefundService.updateRefunds(arl);

			transactionManager.commit(transactionStatus);
		} catch (Exception e) {
			transactionManager.rollback(transactionStatus);
			throw e;
		}

		return true;
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Autowired
	public void setAutoRefundService(AutoRefundService autoRefundService) {
		this.autoRefundService = autoRefundService;
	}
}
