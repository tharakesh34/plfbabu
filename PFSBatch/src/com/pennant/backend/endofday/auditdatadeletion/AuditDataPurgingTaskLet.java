package com.pennant.backend.endofday.auditdatadeletion;


import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.util.DataPurgingProcess;

public class AuditDataPurgingTaskLet implements Tasklet{
	private Logger logger = Logger.getLogger(AuditDataPurgingTaskLet.class);

	private DataPurgingProcess dataPurgingProcess;
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		//Date Parameter List
		Date	dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
		
		logger.debug("START: Audit Data Purging for Value Date: "+ dateValueDate);
		try {
			BatchUtil.setExecution(context, "INFO", "");
			String 	auditPurgingStatus = getDataPurgingProcess().executeAuditDataPurging();
			context.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("AUDITPURGING_STATUS", auditPurgingStatus);
		} catch (Exception e) {
			logger.error("Audit Data Purging Failed "+e);
			throw e;
		}
		logger.debug("COMPLETE: Audit Data Purging for Value Date: "+ dateValueDate);
		return RepeatStatus.FINISHED;
	}

	public DataPurgingProcess getDataPurgingProcess() {
		return dataPurgingProcess;
	}
	public void setDataPurgingProcess(DataPurgingProcess dataPurgingProcess) {
		this.dataPurgingProcess = dataPurgingProcess;
	}


}
