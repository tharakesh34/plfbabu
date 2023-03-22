package com.pennanttech.pff.npa.eod.tasklet;

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
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.npa.model.AssetClassSetupHeader;
import com.pennanttech.pff.npa.model.AssetClassification;
import com.pennanttech.pff.npa.service.AssetClassificationService;

public class EffAssetClassificationTaskLet implements Tasklet {
	private Logger logger = LogManager.getLogger(EffAssetClassificationTaskLet.class);

	private PlatformTransactionManager transactionManager;
	private DataSource dataSource;
	private AssetClassificationService assetClassificationService;

	private static final String QUEUE_QUERY = "Select FinID From Asset_Classification_Queue Where ThreadID = ? and Progress = ?";

	public static AtomicLong processedCount = new AtomicLong(0);
	public static AtomicLong failedCount = new AtomicLong(0);

	private static final String START_MSG = "Effective Asset Classification started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String FAILED_MSG = "Effective Asset Classification failed on {} for the FinReference {}";
	private static final String SUCCESS_MSG = "Effective Asset Classification completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String EXCEPTION_MSG = "Effective Asset Classification failed on {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_LOG = "Cause {}\nMessage {}\nLocalizedMessage {}\nStackTrace {}";

	public EffAssetClassificationTaskLet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Map<String, Object> stepExecutionContext = context.getStepContext().getStepExecutionContext();

		Date appDate = SysParamUtil.getAppDate();

		final int threadId = Integer.parseInt(stepExecutionContext.get(EodConstants.THREAD).toString());

		String strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		String strAppDate = DateUtil.formatToLongDate(appDate);

		logger.info(START_MSG, strSysDate, strAppDate, threadId);

		Map<String, AssetClassSetupHeader> header = assetClassificationService.getAssetClassSetups();

		BatchUtil.setExecutionStatus(context, StepUtil.EFF_NPA_CLASSIFICATION);

		JdbcCursorItemReader<Long> itemReader = new JdbcCursorItemReader<>();
		itemReader.setSql(QUEUE_QUERY);
		itemReader.setDataSource(dataSource);

		itemReader.setRowMapper((rs, rowNum) -> {
			return rs.getLong("FinID");
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

		Long finID = null;
		while ((finID = itemReader.read()) != null) {
			try {
				assetClassificationService.updateProgress(finID, EodConstants.PROGRESS_IN_PROCESS);

				AssetClassification npa = assetClassificationService.getNpaDetails(finID);

				if (npa == null) {
					throw new AppException("Data is not found in NPA Loan Info for the FinID :" + finID);
				}

				boolean effNpaStage = npa.isEffNpaStage();

				npa.setAssetClassSetup(header);
				assetClassificationService.setLoanInfo(npa);
				assetClassificationService.setEffClassification(npa);

				boolean npaChange = false;
				if (npa.isEffNpaStage() && npa.getLinkedTranID() == null) {
					npaChange = true;
					npa.setFinID(finID);
					npa.setFinReference(npa.getFinReference());
					assetClassificationService.setLoanInfo(npa);
				}

				AssetClassification napMovement = assetClassificationService.getNpaMovemnt(finID);

				Long npaMovemntId = assetClassificationService.getNpaMovemntId(finID);

				txStatus = transactionManager.getTransaction(txDef);

				assetClassificationService.doPostNpaChange(npa, npaMovemntId);

				assetClassificationService.updateClassification(npa);

				if (napMovement == null || isClassficationChange(napMovement, npa)) {
					npa.setClassDate(appDate);
					assetClassificationService.saveNpaMovement(npa);
				}

				if (npaMovemntId == null && npa.isEffNpaStage()) {
					npa.setNpaTaggedDate(appDate);
					assetClassificationService.saveNpaTaggingMovement(npa);
				} else if (npaMovemntId != null && !npa.isEffNpaStage()) {
					npa.setNpaUnTaggedDate(appDate);
					assetClassificationService.updateNpaMovement(npaMovemntId, npa);
				}

				assetClassificationService.updateProgress(finID, EodConstants.PROGRESS_SUCCESS);

				transactionManager.commit(txStatus);

				StepUtil.EFF_NPA_CLASSIFICATION.setProcessedRecords(processedCount.incrementAndGet());
			} catch (Exception e) {
				StepUtil.EFF_NPA_CLASSIFICATION.setFailedRecords(failedCount.incrementAndGet());

				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}

				exceptions.add(e);

				strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
				logger.info(FAILED_MSG, strSysDate, finID);

				assetClassificationService.updateProgress(finID, EodConstants.PROGRESS_FAILED);
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

		return RepeatStatus.FINISHED;
	}

	private boolean isClassficationChange(AssetClassification a1, AssetClassification a2) {
		return a1.getNpaClassID() != a2.getNpaClassID() || a1.getEffNpaClassID() != a2.getEffNpaClassID();
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
	public void setAssetClassificationService(AssetClassificationService assetClassificationService) {
		this.assetClassificationService = assetClassificationService;
	}

}
