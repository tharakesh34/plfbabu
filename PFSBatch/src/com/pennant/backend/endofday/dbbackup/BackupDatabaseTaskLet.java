package com.pennant.backend.endofday.dbbackup;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.util.BackupDatabase;

public class BackupDatabaseTaskLet implements Tasklet{
	private Logger logger = Logger.getLogger(BackupDatabaseTaskLet.class);

	private BackupDatabase backupDatabase;
    private boolean   beforeEod;
    private Date dateValueDate = null;
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		//Date Parameter List
		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());

    	logger.debug("START: Data Base Backup for Value Date: "+ dateValueDate);
		try {
			BatchUtil.setExecution(context, "INFO", "");
			String 	DB_BACK_UP_Status = getBackupDatabase().backupDatabase(isBeforeEod());
			
			if(!isBeforeEod() && !DB_BACK_UP_Status.equals("")){
			  context.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("DBBACKUP_STATUS", DB_BACK_UP_Status);
			} else if(!DB_BACK_UP_Status.equals("")) {
				throw new Exception(DB_BACK_UP_Status.split(",")[1]);
			}
		} catch (Exception e) {
			logger.error(e);
			throw e;

		}
    	logger.debug("COMPLETE: Data Base Backup for Value Date: "+ dateValueDate);

		return RepeatStatus.FINISHED;
	}

	public BackupDatabase getBackupDatabase() {
		return backupDatabase;
	}

	public void setBackupDatabase(BackupDatabase backupDatabase) {
		this.backupDatabase = backupDatabase;
	}

	public boolean isBeforeEod() {
		return beforeEod;
	}

	public void setBeforeEod(boolean beforeEod) {
		this.beforeEod = beforeEod;
	}
	
}
