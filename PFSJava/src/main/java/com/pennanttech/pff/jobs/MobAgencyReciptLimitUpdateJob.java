package com.pennanttech.pff.jobs;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.backend.service.finance.NonLanReceiptService;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class MobAgencyReciptLimitUpdateJob implements Job, Serializable {
	private static final long serialVersionUID = -6372080104335048313L;
	private static final Logger logger = LogManager.getLogger(MobAgencyReciptLimitUpdateJob.class);

	public static final String JOB_KEY = "MOB_AGENCY_RECEIPT_LIMIT_UPDATE";
	public static final String JOB_KEY_DESCRIPTION = "Updating Collection Agency Limit for Receipt Source Mobile";
	public static final String JOB_TRIGGER = "MOB_AGENCY_RECEIPT_LIMIT_UPDATE";
	public static final String JOB_FREQUENCY = "0 0/1 * 1/1 * ? *";
	public static final String JOB_ENABLED = App.getProperty("non.lan.receipt.mob.agency.limit.update");

	private NonLanReceiptService nonLanReceiptService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		String jobName = context.getJobDetail().getKey().getName();
		logger.debug(String.format("JOB: %s", jobName));

		try {
			if ("Y".equalsIgnoreCase(JOB_ENABLED)) {
				getNonLanReceiptService(context).processCollectionAPILog();
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private NonLanReceiptService getNonLanReceiptService(JobExecutionContext context) {
		if (nonLanReceiptService == null) {
			nonLanReceiptService = (NonLanReceiptService) context.getJobDetail().getJobDataMap()
					.get("nonLanReceiptService");
		}
		return nonLanReceiptService;
	}

	public static String getCronExpression() {
		String cronExpression = App.getProperty("non.lan.receipt.mob.limit.update.cron");

		if (StringUtils.isEmpty(cronExpression)) {
			cronExpression = JOB_FREQUENCY;
		}

		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(
					String.format("The cron expression %s for receipt source mobile is not valid.", cronExpression));
		}

		return cronExpression;
	}

}
