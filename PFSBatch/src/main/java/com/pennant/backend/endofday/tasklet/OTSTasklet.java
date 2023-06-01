package com.pennant.backend.endofday.tasklet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
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

import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.settlement.model.FinSettlementHeader;
import com.pennant.pff.settlement.model.SettlementAllocationDetail;
import com.pennant.pff.settlement.service.SettlementService;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.eod.EODUtil;

public class OTSTasklet implements Tasklet {

	private Logger logger = LogManager.getLogger(OTSTasklet.class);

	private PlatformTransactionManager transactionManager;
	private DataSource dataSource;
	private SettlementService settlementService;

	private static final String QUEUE_QUERY = "Select SettlementID From OTS_Queue Where ThreadID = ? and Progress = ?";

	private static final String START_MSG = "OTS Process started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String FAILED_MSG = "OTS Process failed on {} for the FinReference {}";
	private static final String SUCCESS_MSG = "OTS Process completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String EXCEPTION_MSG = "OTS Process failed on {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_LOG = "Cause {}\nMessage {}\nLocalizedMessage {}\nStackTrace {}";

	public static AtomicLong processedCount = new AtomicLong(0);
	public static AtomicLong failedCount = new AtomicLong(0);

	public OTSTasklet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		EventProperties eventProperties = EODUtil.getEventProperties(EODUtil.EVENT_PROPERTIES, chunkContext);
		Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();

		if (!eventProperties.isAllowOTSOnEOD()) {
			return RepeatStatus.FINISHED;
		}

		Date appDate = eventProperties.getAppDate();

		final int threadId = Integer.parseInt(stepExecutionContext.get(EodConstants.THREAD).toString());

		String strSysDate = DateUtil.format(appDate, DateFormat.LONG_DATE_TIME);
		String strAppDate = DateUtil.formatToLongDate(appDate);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		TransactionStatus txStatus = null;

		List<Exception> exceptions = new ArrayList<>(1);

		logger.info(START_MSG, strSysDate, strAppDate);

		JdbcCursorItemReader<Long> itemReader = new JdbcCursorItemReader<>();
		itemReader.setSql(QUEUE_QUERY);
		itemReader.setDataSource(dataSource);
		itemReader.setRowMapper((rs, rowNum) -> rs.getLong(1));
		itemReader.setPreparedStatementSetter(ps -> {
			ps.setLong(1, threadId);
			ps.setInt(2, EodConstants.PROGRESS_WAIT);
		});

		itemReader.open(chunkContext.getStepContext().getStepExecution().getExecutionContext());

		Long id = null;
		while ((id = itemReader.read()) != null) {
			settlementService.updateProgress(id, EodConstants.PROGRESS_IN_PROCESS);

			FinSettlementHeader fsh = settlementService.getsettlementById(id);

			if (fsh == null) {
				continue;
			}

			fsh.setAppDate(appDate);

			BigDecimal balanceAmt = settlementService.getSettlementAountReceived(fsh.getFinID());

			boolean validSettlementProcess = false;
			boolean validSettlementCancellation = false;

			List<SettlementAllocationDetail> allocations = fsh.getSettlementAllocationDetails();
			if (balanceAmt.compareTo(fsh.getSettlementAmount()) >= 0 && CollectionUtils.isNotEmpty(allocations)) {
				settlementService.loadSettlementData(fsh);
				validSettlementProcess = settlementService.isValidSettlementProcess(fsh);
				validSettlementCancellation = !validSettlementProcess;
			}

			if (validSettlementCancellation || isValidSettlementCancellation(fsh)) {
				settlementService.loadSettlementData(fsh);
				validSettlementCancellation = true;
			}

			try {
				txStatus = transactionManager.getTransaction(txDef);

				if (validSettlementProcess) {
					settlementService.processSettlement(fsh);
				}

				if (validSettlementCancellation) {
					settlementService.processSettlementCancellation(fsh);
				}

				settlementService.updateProgress(id, EodConstants.PROGRESS_SUCCESS);
				transactionManager.commit(txStatus);
			} catch (Exception e) {
				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				exceptions.add(e);
				strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}

				settlementService.updateProgress(id, EodConstants.PROGRESS_FAILED);
				logger.info(FAILED_MSG, strSysDate, fsh.getFinID());
			} finally {
				txStatus = null;
			}
		}

		itemReader.close();

		if (!exceptions.isEmpty()) {
			String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
			logger.info(EXCEPTION_MSG, sysDate, strAppDate);

			throw exceptions.get(0);
		}

		String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		logger.info(SUCCESS_MSG, sysDate, strAppDate);

		return RepeatStatus.FINISHED;
	}

	private boolean isValidSettlementCancellation(FinSettlementHeader fsh) {
		return fsh.getAppDate().compareTo(fsh.getEndDate()) == 0 && fsh.getNoOfGraceDays() >= 0;
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
	public void setSettlementService(SettlementService settlementService) {
		this.settlementService = settlementService;
	}

}
