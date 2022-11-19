package com.pennant.pff.presentment.tasklet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.eventproperties.service.EventPropertiesService;
import com.pennant.backend.eventproperties.service.impl.EventPropertiesServiceImpl.EventType;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class ApprovalTasklet implements Tasklet {
	private final Logger logger = LogManager.getLogger(ExtractionTasklet.class);

	private PresentmentEngine presentmentEngine;
	private BatchJobQueueDAO approvalBatchJobQueueDAO;
	private DataSourceTransactionManager transactionManager;

	private static final String START_MSG = "Presentment approval Process started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String FAILED_MSG = "Presentment approval Process failed on {} for the CustId {}";
	private static final String SUCCESS_MSG = "Presentment approval Process completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String EXCEPTION_MSG = "Presentment approval Process failed on {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_LOG = "Cause {}\nMessage {}\n LocalizedMessage {}\nStackTrace {}";

	private EventPropertiesService eventPropertiesService;
	private EventProperties eventProperties;

	public ApprovalTasklet(BatchJobQueueDAO approvalBatchJobQueueDAO, PresentmentEngine presentmentEngine,
			DataSourceTransactionManager transactionManager) {
		super();
		this.approvalBatchJobQueueDAO = approvalBatchJobQueueDAO;
		this.presentmentEngine = presentmentEngine;
		this.transactionManager = transactionManager;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		List<Exception> exceptions = new ArrayList<>(1);

		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
		Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();

		int threadID = Integer.parseInt(stepExecutionContext.get("THREAD_ID").toString());

		approvalBatchJobQueueDAO.resetSequence();

		long queueID = approvalBatchJobQueueDAO.getNextValue();

		Long presentmentID = approvalBatchJobQueueDAO.getIdBySequence(queueID);

		if (presentmentID == null) {
			return RepeatStatus.FINISHED;
		}

		eventProperties = eventPropertiesService.getEventProperties(EventType.EOD);

		Date appDate = SysParamUtil.getAppDate();
		String strAppDate = DateUtil.formatToLongDate(appDate);
		String strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);

		logger.info(START_MSG, strSysDate, strAppDate, threadID);

		PresentmentHeader ph = getPresentmentHeader(jobParameters);

		ph.setAppDate(appDate);

		while (presentmentID != null) {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setId(queueID);
			jobQueue.setThreadId(threadID);

			try {

				jobQueue.setProgress(EodConstants.PROGRESS_IN_PROCESS);
				approvalBatchJobQueueDAO.updateProgress(jobQueue);

				boolean status = approvePresentment(presentmentID, ph);

				if (status) {
					jobQueue.setProgress(EodConstants.PROGRESS_SUCCESS);
					approvalBatchJobQueueDAO.updateProgress(jobQueue);
				} else {
					jobQueue.setProgress(EodConstants.PROGRESS_FAILED);
					approvalBatchJobQueueDAO.updateProgress(jobQueue);
				}

			} catch (Exception e) {
				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				exceptions.add(e);

				strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
				logger.info(FAILED_MSG, strSysDate, presentmentID);

				jobQueue.setProgress(EodConstants.PROGRESS_FAILED);
				approvalBatchJobQueueDAO.updateProgress(jobQueue);

				break;
			}

			queueID = approvalBatchJobQueueDAO.getNextValue();
			presentmentID = approvalBatchJobQueueDAO.getIdBySequence(queueID);
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

	private boolean approvePresentment(Long presentmentID, PresentmentHeader ph) {
		try {

			PresentmentDetail pd = presentmentEngine.getPresentmenToPost(presentmentID);

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

			try {
				pd.setAppDate(ph.getAppDate());
				pd.setEventProperties(eventProperties);

				presentmentEngine.approve(pd);
				transactionManager.commit(transactionStatus);
			} catch (Exception e) {
				transactionManager.rollback(transactionStatus);
			}

		} catch (Exception e) {
			throw e;
		}

		return true;
	}

	private PresentmentHeader getPresentmentHeader(JobParameters jobParameters) {
		long batchId = jobParameters.getLong("BATCH_ID");

		PresentmentHeader ph = new PresentmentHeader();

		ph.setBatchID(batchId);
		ph.setAppDate(jobParameters.getDate("AppDate"));
		ph.setPresentmentType(jobParameters.getString("PresentmentType"));

		String automation = jobParameters.getString("AUTOMATION");

		if (automation.equals("Y")) {
			ph.setAutoExtract(true);
		} else {
			ph.setAutoExtract(false);

			ph.setMandateType(jobParameters.getString("MandateType"));
			ph.setEmandateSource(jobParameters.getString("EmandateSource"));
			ph.setLoanType(jobParameters.getString("LoanType"));
			ph.setEntityCode(jobParameters.getString("EntityCode"));
			ph.setFinBranch(jobParameters.getString("FinBranch"));
			ph.setFromDate(jobParameters.getDate("FromDate"));
			ph.setToDate(jobParameters.getDate("ToDate"));
			ph.setDueDate(jobParameters.getDate("DueDate"));
			ph.setBpiPaidOnInstDate(Boolean.valueOf(jobParameters.getString("BpiPaidOnInstDate")));
			ph.setGroupByBank(Boolean.valueOf(jobParameters.getString("GroupByBank")));
			ph.setGroupByPartnerBank(ImplementationConstants.GROUP_BATCH_BY_PARTNERBANK);
		}

		return ph;
	}

	public void setEventPropertiesService(EventPropertiesService eventPropertiesService) {
		this.eventPropertiesService = eventPropertiesService;
	}

}
