package com.pennanttech.pff.knockoff.eod.tasklet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
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

import com.pennant.app.core.AutoKnockOffService;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.receipts.CrossLoanKnockOffDAO;
import com.pennant.backend.dao.receipts.CrossLoanTransferDAO;
import com.pennant.backend.model.autoknockoff.AutoKnockOffExcessDetails;
import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.finance.AutoKnockOffExcess;
import com.pennant.backend.model.finance.CrossLoanKnockOff;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.knockoff.ExcessKnockOffUtil;
import com.pennanttech.pff.knockoff.model.ExcessKnockOff;
import com.pennanttech.pff.knockoff.service.ExcessKnockOffService;

public class ExcessKnockOffTasklet implements Tasklet {
	private Logger logger = LogManager.getLogger(ExcessKnockOffTasklet.class);

	private PlatformTransactionManager transactionManager;
	private DataSource dataSource;
	private ExcessKnockOffService excessKnockOffService;
	private AutoKnockOffService autoKnockOffService;
	private CrossLoanKnockOffDAO crossLoanKnockOffDAO;
	private CrossLoanTransferDAO crossLoanTransferDAO;

	private static final String QUEUE_QUERY = "Select CustID, CoreBankId From Cross_Loan_KnockOff_Queue Where ThreadID = ? and Progress= ?";

	private static final String START_MSG = "Cross Loan KnockOff started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String FAILED_MSG = "Cross Loan KnockOff failed on {} for the FinReference {}";
	private static final String SUCCESS_MSG = "Cross Loan KnockOff completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String EXCEPTION_MSG = "Cross Loan KnockOff failed on {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_LOG = "Cause {}\nMessage {}\n LocalizedMessage {}\nStackTrace {}";

	public static AtomicLong processedCount = new AtomicLong(0);
	public static AtomicLong failedCount = new AtomicLong(0);

	public ExcessKnockOffTasklet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		logger.debug(Literal.ENTERING);

		Map<String, Object> stepExecutionContext = context.getStepContext().getStepExecutionContext();

		Date appDate = SysParamUtil.getAppDate();
		final int threadId = Integer.parseInt(stepExecutionContext.get(EodConstants.THREAD).toString());

		String strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		String strAppDate = DateUtil.formatToLongDate(appDate);

		logger.info(START_MSG, strSysDate, strAppDate, threadId);

		BatchUtil.setExecutionStatus(context, StepUtil.CROSS_LOAN_KNOCKOFF);

		JdbcCursorItemReader<CustomerCoreBank> itemReader = new JdbcCursorItemReader<>();
		itemReader.setSql(QUEUE_QUERY);
		itemReader.setDataSource(dataSource);
		itemReader.setRowMapper((rs, rowNum) -> {
			CustomerCoreBank cd = new CustomerCoreBank();
			cd.setCustCoreBank(rs.getString("CoreBankId"));
			cd.setCustID(rs.getLong("CustID"));
			return cd;
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

		CustomerCoreBank customerCoreBank = null;

		while ((customerCoreBank = itemReader.read()) != null) {
			excessKnockOffService.updateProgress(customerCoreBank, EodConstants.PROGRESS_IN_PROCESS);
			List<ExcessKnockOff> ekflist = excessKnockOffService.loadData(customerCoreBank);
			ekflist.forEach(
					l1 -> l1.setExcessKnockOffDetails(excessKnockOffService.getStageDataByID(l1.getId())));

			if (CollectionUtils.isEmpty(ekflist)) {
				continue;
			}

			try {

				for (ExcessKnockOff ekf : ekflist) {
					if (ekf.getBalanceAmt().compareTo(BigDecimal.ZERO) <= 0) {
						break;
					}

					long custID = ekf.getCustID();
					long finID = ekf.getFinID();
					String coreBankId = ekf.getCoreBankId();

					List<FinanceMain> fmList = excessKnockOffService.getLoansbyCustId(custID, coreBankId, finID);

					try {
						txStatus = transactionManager.getTransaction(txDef);
						processCrossKnockOff(ekf, fmList);
					} catch (Exception e) {
						if (txStatus != null) {
							transactionManager.rollback(txStatus);
						}
						throw e;
					} finally {
						txStatus = null;
					}
				}

				excessKnockOffService.updateProgress(customerCoreBank, EodConstants.PROGRESS_SUCCESS);

				StepUtil.CROSS_LOAN_KNOCKOFF.setProcessedRecords(processedCount.incrementAndGet());

			} catch (Exception e) {
				StepUtil.CROSS_LOAN_KNOCKOFF.setFailedRecords(failedCount.incrementAndGet());

				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);
				exceptions.add(e);

				strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
				logger.info(FAILED_MSG, strSysDate);

				excessKnockOffService.updateProgress(customerCoreBank, EodConstants.PROGRESS_FAILED);
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

		BatchUtil.setExecutionStatus(context, StepUtil.CROSS_LOAN_KNOCKOFF);

		return RepeatStatus.FINISHED;
	}

	private void processCrossKnockOff(ExcessKnockOff ekf, List<FinanceMain> fmList) {
		for (FinanceMain fm : fmList) {
			if (ekf.getBalanceAmt().compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			AutoKnockOffExcess ake = ExcessKnockOffUtil.getExcessKnockOff(ekf, fm);
			ake.setCrossLoanAutoKnockOff(true);

			ekf.getExcessKnockOffDetails()
					.forEach(ekod -> ake.getExcessDetails().add(ExcessKnockOffUtil.getKnockOffDetails(ekod)));

			autoKnockOffService.process(ake);

			List<AutoKnockOffExcessDetails> excessDetails = ake.getExcessDetails();
			List<CrossLoanKnockOff> clkoList = new ArrayList<>();

			for (AutoKnockOffExcessDetails ako : excessDetails) {
				if (ako.getReceiptID() > 0) {
					CrossLoanKnockOff clko = ExcessKnockOffUtil.getCrossLoanKnockOff(ako, ekf, fm);
					clko.setCrossLoanTransfer(ExcessKnockOffUtil.getCrossLoanTransfer(ako, ekf, fm));
					clko.getCrossLoanTransfer().setReceiptId(ako.getReceiptID());
					clko.setReceiptID(ako.getReceiptID());
					clko.setValueDate(clko.getCrossLoanTransfer().getValueDate());
					clko.setTransferID(crossLoanTransferDAO.save(clko.getCrossLoanTransfer(), ""));
					clkoList.add(clko);
				}
			}

			if (CollectionUtils.isNotEmpty(clkoList)) {
				crossLoanKnockOffDAO.saveCrossLoanHeader(clkoList, "");
			}

			ekf.setBalanceAmt(ekf.getBalanceAmt().subtract(ake.getTotalUtilizedAmnt()));
		}

		logger.debug(Literal.LEAVING);

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
	public void setExcessKnockOffService(ExcessKnockOffService excessKnockOffService) {
		this.excessKnockOffService = excessKnockOffService;
	}

	@Autowired
	public void setAutoKnockOffService(AutoKnockOffService autoKnockOffService) {
		this.autoKnockOffService = autoKnockOffService;
	}

	@Autowired
	public void setCrossLoanKnockOffDAO(CrossLoanKnockOffDAO crossLoanKnockOffDAO) {
		this.crossLoanKnockOffDAO = crossLoanKnockOffDAO;
	}

	@Autowired
	public void setCrossLoanTransferDAO(CrossLoanTransferDAO crossLoanTransferDAO) {
		this.crossLoanTransferDAO = crossLoanTransferDAO;
	}

}