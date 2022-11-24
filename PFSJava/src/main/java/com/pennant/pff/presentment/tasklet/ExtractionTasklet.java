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
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class ExtractionTasklet implements Tasklet {
	private final Logger logger = LogManager.getLogger(ExtractionTasklet.class);

	private PresentmentEngine presentmentEngine;
	private BatchJobQueueDAO ebjqDAO;
	private DataSourceTransactionManager transactionManager;
	private PresentmentDAO presentmentDAO;

	private static final String START_MSG = "Presentment extraction Process started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String FAILED_MSG = "Presentment extraction Process failed on {} for the Presentment-ID {}";
	private static final String SUCCESS_MSG = "Presentment extraction Process completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String EXCEPTION_MSG = "Presentment extraction Process failed on {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_LOG = "Cause {}\nMessage {}\n LocalizedMessage {}\nStackTrace {}";

	int successRecords;
	int processRecords;
	int failedRecords;

	public ExtractionTasklet(BatchJobQueueDAO ebjqDAO, PresentmentEngine presentmentEngine,
			DataSourceTransactionManager transactionManager, PresentmentDAO presentmentDAO) {
		super();
		this.ebjqDAO = ebjqDAO;
		this.presentmentEngine = presentmentEngine;
		this.transactionManager = transactionManager;
		this.presentmentDAO = presentmentDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		List<Exception> exceptions = new ArrayList<>(1);

		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
		Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();

		int threadID = Integer.parseInt(stepExecutionContext.get("THREAD_ID").toString());

		ebjqDAO.resetSequence();

		long queueID = ebjqDAO.getNextValue();
		long batchId = jobParameters.getLong("BATCH_ID");
		Long presentmentID = ebjqDAO.getIdBySequence(queueID);

		if (presentmentID == null) {
			return RepeatStatus.FINISHED;
		}

		Date appDate = SysParamUtil.getAppDate();
		String strAppDate = DateUtil.formatToLongDate(appDate);
		String strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);

		logger.info(START_MSG, strSysDate, strAppDate, threadID);

		PresentmentHeader ph = getPresentmentHeader(jobParameters);

		while (presentmentID != null) {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setBatchId(batchId);
			jobQueue.setId(queueID);
			jobQueue.setThreadId(threadID);
			++processRecords;
			jobQueue.setProcessedRecords(processRecords);

			try {
				jobQueue.setProgress(EodConstants.PROGRESS_IN_PROCESS);
				ebjqDAO.updateProgress(jobQueue);

				boolean status = extractPresentment(presentmentID, ph);

				presentmentDAO.updateBatch(jobQueue);

				if (status) {
					jobQueue.setProgress(EodConstants.PROGRESS_SUCCESS);
					ebjqDAO.updateProgress(jobQueue);

					++successRecords;
					jobQueue.setSuccessRecords(successRecords);
					presentmentDAO.updateBatch(jobQueue);
				} else {
					jobQueue.setProgress(EodConstants.PROGRESS_FAILED);
					ebjqDAO.updateProgress(jobQueue);

					++failedRecords;
					jobQueue.setFailedRecords(failedRecords);
					presentmentDAO.updateBatch(jobQueue);
				}

			} catch (Exception e) {
				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				if (!exceptions.isEmpty()) {
					exceptions.add(e);
				}
				strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
				logger.info(FAILED_MSG, strSysDate, presentmentID);

				jobQueue.setProgress(EodConstants.PROGRESS_FAILED);
				ebjqDAO.updateProgress(jobQueue);

				++failedRecords;
				jobQueue.setFailedRecords(failedRecords);
				presentmentDAO.updateBatch(jobQueue);
			}

			queueID = ebjqDAO.getNextValue();
			presentmentID = ebjqDAO.getIdBySequence(queueID);
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

	private boolean extractPresentment(Long presentmentID, PresentmentHeader ph) {
		try {

			PresentmentDetail pd = presentmentEngine.extract(presentmentID, ph);
			pd.setPresentmentType(ph.getPresentmentType());

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

			try {
				presentmentEngine.save(pd);
				transactionManager.commit(transactionStatus);
			} catch (Exception e) {
				transactionManager.rollback(transactionStatus);
				throw e;
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

}
