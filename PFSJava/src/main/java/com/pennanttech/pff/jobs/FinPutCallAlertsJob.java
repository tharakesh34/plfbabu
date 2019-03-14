package com.pennanttech.pff.jobs;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.backend.service.finance.covenant.impl.PutCallAlerts;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class FinPutCallAlertsJob implements Job {
	static final String JOB_KEY = "FIN_PUT_CALL_ALERTS_JOB";
	static final String JOB_TRIGGER = "FIN_PUT_CALL_ALERTS_JOB_TRIGGER";
	private static final String CRON_EXPRESSION = "0 * 4 ? * * *";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		PutCallAlerts putCallAlerts = SpringBeanUtil.getBean(PutCallAlerts.class);

		putCallAlerts.sendAlerts();
	}

	public static String getCronExpression() {
		String cronExpression = App.getProperty("fin.put.call.alerts.cron.expression");

		if (StringUtils.isEmpty(cronExpression)) {
			cronExpression = CRON_EXPRESSION;
		}

		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(
					String.format("The cron expression %s for covenants alerts not valid.", cronExpression));
		}

		return cronExpression;
	}
}
