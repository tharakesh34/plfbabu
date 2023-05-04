package com.pennanttech.pff.autowriteoff.eod.tasklet;

import java.sql.Timestamp;
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
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.autowriteoff.model.AutoWriteOffLoan;
import com.pennanttech.pff.autowriteoff.service.AutoWriteOffService;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.receipt.constants.Allocation;

public class AutoWriteOffClacTaskLet implements Tasklet {
	private Logger logger = LogManager.getLogger(AutoWriteOffClacTaskLet.class);

	private PlatformTransactionManager transactionManager;
	private DataSource dataSource;
	private AutoWriteOffService autoWriteOffService;
	private FeeTypeDAO feeTypeDAO;

	private static final String QUEUE_QUERY = "Select FinID From Auto_Write_Off_Calc_Queue Where ThreadID = ? and Progress = ?";

	private static final String START_MSG = "Auto WriteOff started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String FAILED_MSG = "Auto WriteOff failed on {} for the FinReference {}";
	private static final String SUCCESS_MSG = "Auto WriteOff completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String EXCEPTION_MSG = "Auto WriteOff failed on {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_LOG = "Cause {}\nMessage {}\nLocalizedMessage {}\nStackTrace {}";

	public static AtomicLong processedCount = new AtomicLong(0);
	public static AtomicLong failedCount = new AtomicLong(0);

	public AutoWriteOffClacTaskLet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Map<String, Object> stepExecutionContext = context.getStepContext().getStepExecutionContext();

		Date appDate = SysParamUtil.getAppDate();
		Date monthEnd = DateUtil.getMonthEnd(appDate);
		boolean isMonthEnd = appDate.compareTo(monthEnd) == 0;
		if (!isMonthEnd) {
			return RepeatStatus.FINISHED;
		}
		FeeType feeType = feeTypeDAO.getTaxDetailByCode(Allocation.ODC);
		String schdMthd = SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD);
		final int threadId = Integer.parseInt(stepExecutionContext.get(EodConstants.THREAD).toString());

		String strSysDate = DateUtil.format(appDate, DateFormat.LONG_DATE_TIME);
		String strAppDate = DateUtil.formatToLongDate(appDate);

		logger.info(START_MSG, strSysDate, strAppDate, threadId);

		BatchUtil.setExecutionStatus(context, StepUtil.AUTOWRITEOFF_CALC);

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
		long finID = 0;

		while ((finID = itemReader.read()) != 0) {
			try {
				txStatus = transactionManager.getTransaction(txDef);

				autoWriteOffService.updateProgress(finID, EodConstants.PROGRESS_IN_PROCESS);

				// Receipt Creation
				AutoWriteOffLoan awl = autoWriteOffService.processReceipts(finID, appDate, feeType, schdMthd);
				if (awl == null) {

					// Write off Setting of Loans
					String finRef = autoWriteOffService.prepareWriteOff(finID, appDate);

					// Update Status of Write off Queue record
					autoWriteOffService.updateProgress(finID, EodConstants.PROGRESS_SUCCESS);

					awl = new AutoWriteOffLoan();
					awl.setFinID(finID);
					awl.setFinReference(finRef);
					awl.setExecutionDate(new Timestamp(System.currentTimeMillis()));
					awl.setCode("AWL_000");
					awl.setStatus("S");
					autoWriteOffService.insertlog(awl);

					transactionManager.commit(txStatus);
				} else {
					awl.setExecutionDate(new Timestamp(System.currentTimeMillis()));
					awl.setStatus("S");

					autoWriteOffService.updateProgress(finID, EodConstants.PROGRESS_SUCCESS);
					autoWriteOffService.insertlog(awl);

					transactionManager.commit(txStatus);
				}

				StepUtil.AUTOWRITEOFF_CALC.setProcessedRecords(processedCount.incrementAndGet());

			} catch (Exception e) {

				StepUtil.AUTOWRITEOFF_CALC.setFailedRecords(failedCount.incrementAndGet());

				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}

				exceptions.add(e);
				strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);

				logger.info(FAILED_MSG, strSysDate, finID);

				autoWriteOffService.updateProgress(finID, EodConstants.PROGRESS_FAILED);
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

		BatchUtil.setExecutionStatus(context, StepUtil.AUTOWRITEOFF_CALC);

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
	public void setAutoWriteOffService(AutoWriteOffService autoWriteOffService) {
		this.autoWriteOffService = autoWriteOffService;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

}