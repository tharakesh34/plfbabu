package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.eod.util.DataPurgingProcess;
import com.pennanttech.pff.eod.EODUtil;

public class AuditDataPurging implements Tasklet {
	private Logger logger = LogManager.getLogger(AuditDataPurging.class);

	private DataPurgingProcess dataPurgingProcess;

	public AuditDataPurging() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date dateValueDate = EODUtil.getDate("APP_VALUEDATE", context);

		logger.debug("START: Audit Data Purging for Value Date: " + dateValueDate);
		try {
			String auditPurgingStatus = getDataPurgingProcess().executeAuditDataPurging();
			context.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
					.put("AUDITPURGING_STATUS", auditPurgingStatus);
		} catch (Exception e) {
			logger.error("Exception: Audit Data Purging Failed ", e);
			context.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
					.put("AUDITPURGING_STATUS", e);
		}
		logger.debug("COMPLETE: Audit Data Purging for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	public DataPurgingProcess getDataPurgingProcess() {
		return dataPurgingProcess;
	}

	public void setDataPurgingProcess(DataPurgingProcess dataPurgingProcess) {
		this.dataPurgingProcess = dataPurgingProcess;
	}
}
