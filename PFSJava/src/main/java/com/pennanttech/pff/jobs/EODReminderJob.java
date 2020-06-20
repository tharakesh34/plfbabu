package com.pennanttech.pff.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.EODService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class EODReminderJob implements Job {
	private static final Logger logger = LogManager.getLogger(EODReminderJob.class);

	public static final String JOB_KEY = "EOD_REMINDER_JOB";
	public static final String JOB_KEY_DESCRIPTION = "EOD Reminder Job";
	public static final String JOB_TRIGGER = "EOD_REMINDER_JOB_TRIGGER";

	private EODService eodService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		getEODService(context).sendReminderNotification();
	}

	private EODService getEODService(JobExecutionContext context) {
		if (eodService == null) {
			eodService = (EODService) context.getJobDetail().getJobDataMap().get("eodService");
		}
		return eodService;
	}
}
