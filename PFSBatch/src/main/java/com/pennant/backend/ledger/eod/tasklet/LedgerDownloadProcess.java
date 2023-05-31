package com.pennant.backend.ledger.eod.tasklet;

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

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.external.LedgerDownloadService;
import com.pennanttech.pff.ledger.dao.LedgerDownloadDAO;
import com.pennanttech.pff.ledger.model.LedgerDownload;

public class LedgerDownloadProcess implements Tasklet {
	private Logger logger = LogManager.getLogger(LedgerDownloadProcess.class);

	private PlatformTransactionManager transactionManager;
	private DataSource dataSource;
	private LedgerDownloadService ledgerDownloadService;
	private LedgerDownloadDAO ledgerDownloadDAO;

	private static final String QUEUE_QUERY = "Select LinkedTranID From OGL_TRANSACTIONS_QUEUE Where ThreadID = ? and Progress= ?";
	private static final String START_MSG = "GL Download started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String FAILED_MSG = "GL Download failed on {} for the FinReference {}";
	private static final String SUCCESS_MSG = "GL Download completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String EXCEPTION_MSG = "GL Download failed on {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_LOG = "Cause {}\nMessage {}\n LocalizedMessage {}\nStackTrace {}";

	public static final AtomicLong processedCount = new AtomicLong(0);
	public static final AtomicLong failedCount = new AtomicLong(0);

	public LedgerDownloadProcess() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		if (ledgerDownloadService == null || ledgerDownloadDAO == null || !ledgerDownloadService.isMultiThread()) {
			logger.info("Implementations are not defined / Mutli-thread is not available");
			return RepeatStatus.FINISHED;
		}

		Map<String, Object> stepExecutionContext = context.getStepContext().getStepExecutionContext();

		Date appDate = SysParamUtil.getAppDate();
		appDate = DateUtil.addDays(appDate, -1);

		final int threadId = Integer.parseInt(stepExecutionContext.get(EodConstants.THREAD).toString());

		String strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		String strAppDate = DateUtil.formatToLongDate(appDate);

		logger.info(START_MSG, strSysDate, strAppDate, threadId);

		BatchUtil.setExecutionStatus(context, StepUtil.LEDGER_DOWNLOAD);

		JdbcCursorItemReader<Long> itemReader = new JdbcCursorItemReader<>();
		itemReader.setSql(QUEUE_QUERY);
		itemReader.setDataSource(dataSource);
		itemReader.setRowMapper((rs, rowNum) -> rs.getLong("LinkedTranID"));
		itemReader.setPreparedStatementSetter(ps -> {
			ps.setLong(1, threadId);
			ps.setInt(2, EodConstants.PROGRESS_WAIT);
		});

		itemReader.open(context.getStepContext().getStepExecution().getExecutionContext());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		List<Exception> exceptions = new ArrayList<>(1);

		Long linkedTranID = null;

		while ((linkedTranID = itemReader.read()) != null) {
			try {
				ledgerDownloadDAO.updateProgress(linkedTranID, EodConstants.PROGRESS_IN_PROCESS);

				List<LedgerDownload> list = ledgerDownloadService.process(linkedTranID, appDate);

				txStatus = transactionManager.getTransaction(txDef);

				ledgerDownloadDAO.save(list, "");

				ledgerDownloadDAO.updateProgress(linkedTranID, EodConstants.PROGRESS_SUCCESS);

				transactionManager.commit(txStatus);

				StepUtil.LEDGER_DOWNLOAD.setProcessedRecords(processedCount.incrementAndGet());
			} catch (Exception e) {
				StepUtil.LEDGER_DOWNLOAD.setFailedRecords(failedCount.incrementAndGet());

				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}

				exceptions.add(e);

				strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
				logger.info(FAILED_MSG, strSysDate, linkedTranID);

				ledgerDownloadDAO.updateProgress(linkedTranID, EodConstants.PROGRESS_FAILED);

				break;
			} finally {
				if (txStatus != null) {
					txStatus = null;
				}
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

		BatchUtil.setExecutionStatus(context, StepUtil.LEDGER_DOWNLOAD);

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

	@Autowired(required = false)
	public void setLedgerDownloadService(LedgerDownloadService ledgerDownloadService) {
		this.ledgerDownloadService = ledgerDownloadService;
	}

	@Autowired(required = false)
	public void setLedgerDownloadDAO(LedgerDownloadDAO ledgerDownloadDAO) {
		this.ledgerDownloadDAO = ledgerDownloadDAO;
	}
}
