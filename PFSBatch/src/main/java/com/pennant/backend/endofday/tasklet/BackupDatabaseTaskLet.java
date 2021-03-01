package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.eod.util.BackupDatabase;
import com.pennanttech.pff.eod.EODUtil;

public class BackupDatabaseTaskLet implements Tasklet {
	private Logger logger = LogManager.getLogger(BackupDatabaseTaskLet.class);

	private BackupDatabase backupDatabase;
	private boolean beforeEod;
	private Date dateValueDate = null;

	public BackupDatabaseTaskLet() {
		//
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		//Date Parameter List
		dateValueDate = EODUtil.getDate("APP_VALUEDATE", context);

		logger.info("START: Data Base Backup for Value Date {}", dateValueDate);
		try {
			String dbBackUpStatus = getBackupDatabase().backupDatabase(isBeforeEod());

			if (!isBeforeEod() && StringUtils.isNotEmpty(dbBackUpStatus)) {
				context.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
						.put("DBBACKUP_STATUS", dbBackUpStatus);
			} else if (StringUtils.isNotEmpty(dbBackUpStatus)) {
				throw new Exception(dbBackUpStatus.split(",")[1]);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;

		}
		logger.debug("COMPLETE: Data Base Backup for Value Date: " + dateValueDate);

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
