package com.pennanttech.pff.knockoff.eod.tasklet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections4.CollectionUtils;
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
import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.extension.CustomerExtension;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.knockoff.model.ExcessKnockOff;
import com.pennanttech.pff.knockoff.service.ExcessKnockOffService;
import com.pennanttech.pff.refund.eod.tasklet.AutoRefundTasklet;

public class ExcessKnockOffTasklet implements Tasklet {
	private Logger logger = LogManager.getLogger(AutoRefundTasklet.class);

	private BatchJobQueueDAO ebjqDAO;
	private ExcessKnockOffService excessKnockOffService;
	private PlatformTransactionManager transactionManager;

	private static final String START_MSG = "Cross Loan KnockOff started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String SUCCESS_MSG = "Cross Loan KnockOff completed at {} for the APP_DATE {} with THREAD_ID {}";

	public static final AtomicLong processedCount = new AtomicLong(0);
	public static final AtomicLong failedCount = new AtomicLong(0);

	public ExcessKnockOffTasklet(BatchJobQueueDAO ebjqDAO) {
		super();
		this.ebjqDAO = ebjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		logger.debug(Literal.ENTERING);

		BatchUtil.setExecutionStatus(context, StepUtil.CROSS_LOAN_KNOCKOFF);

		Map<String, Object> stepExecutionContext = context.getStepContext().getStepExecutionContext();

		int threadID = Integer.parseInt(stepExecutionContext.get("THREAD_ID").toString());

		long queueID = ebjqDAO.getNextValue();

		String reference = null;

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			reference = ebjqDAO.getRefBySequence(queueID);
		} else {
			reference = String.valueOf(ebjqDAO.getIdBySequence(queueID));
		}

		if (reference == null || "null".equals(reference)) {
			return RepeatStatus.FINISHED;
		}

		Date appDate = SysParamUtil.getAppDate();

		String strAppDate = DateUtil.formatToLongDate(appDate);
		String strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);

		logger.info(START_MSG, strSysDate, strAppDate, threadID);

		List<Exception> exceptions = new ArrayList<>(1);

		while (reference != null) {
			BatchJobQueue jobQueue = new BatchJobQueue();
			jobQueue.setId(queueID);
			jobQueue.setThreadId(threadID);

			try {
				jobQueue.setProgress(EodConstants.PROGRESS_IN_PROCESS);
				ebjqDAO.updateProgress(jobQueue);

				CustomerCoreBank customerCoreBank = new CustomerCoreBank();
				if (CustomerExtension.CUST_CORE_BANK_ID) {
					customerCoreBank.setCustCoreBank(reference);
				} else {
					customerCoreBank.setCustID(Long.parseLong(reference));
				}

				boolean status = processKnockOff(customerCoreBank);

				if (status) {
					jobQueue.setProgress(EodConstants.PROGRESS_SUCCESS);
					ebjqDAO.updateProgress(jobQueue);
					StepUtil.CROSS_LOAN_KNOCKOFF.setProcessedRecords(processedCount.incrementAndGet());
				} else {
					jobQueue.setProgress(EodConstants.PROGRESS_FAILED);
					ebjqDAO.updateProgress(jobQueue);
					StepUtil.CROSS_LOAN_KNOCKOFF.setProcessedRecords(failedCount.incrementAndGet());
				}
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);

				exceptions.add(e);

				StepUtil.CROSS_LOAN_KNOCKOFF.setProcessedRecords(failedCount.incrementAndGet());

				String errorMsg = ExceptionUtils.getStackTrace(e);

				if (errorMsg != null && errorMsg.length() > 2000) {
					errorMsg = errorMsg.substring(0, 1999);
				}

				jobQueue.setError(errorMsg);
				jobQueue.setProgress(EodConstants.PROGRESS_FAILED);

				ebjqDAO.updateProgress(jobQueue);
			}

			queueID = ebjqDAO.getNextValue();

			if (CustomerExtension.CUST_CORE_BANK_ID) {
				reference = ebjqDAO.getRefBySequence(queueID);
			} else {
				reference = String.valueOf(ebjqDAO.getIdBySequence(queueID));

				if ("null".equals(reference)) {
					reference = null;
				}
			}
		}

		if (!exceptions.isEmpty()) {
			throw exceptions.get(0);
		}

		String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		logger.info(SUCCESS_MSG, sysDate, strAppDate, threadID);

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}

	private boolean processKnockOff(CustomerCoreBank customerCoreBank) {
		List<ExcessKnockOff> ekflist = excessKnockOffService.loadData(customerCoreBank);

		ekflist.forEach(l1 -> l1.setExcessKnockOffDetails(excessKnockOffService.getStageDataByID(l1.getId())));

		if (CollectionUtils.isEmpty(ekflist)) {
			return true;
		}

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		for (ExcessKnockOff ekf : ekflist) {
			if (ekf.getBalanceAmt().compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			long custID = ekf.getCustID();
			long finID = ekf.getFinID();
			String coreBankId = ekf.getCoreBankId();

			List<FinanceMain> fmList = excessKnockOffService.getLoansbyCustId(custID, coreBankId, finID);

			for (FinanceMain fm : fmList) {

				if (ekf.getBalanceAmt().compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}

				TransactionStatus txStatus = null;

				try {
					txStatus = transactionManager.getTransaction(txDef);

					excessKnockOffService.process(ekf, fm);

					transactionManager.commit(txStatus);

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				}
			}
		}

		return true;

	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Autowired
	public void setExcessKnockOffService(ExcessKnockOffService excessKnockOffService) {
		this.excessKnockOffService = excessKnockOffService;
	}

}