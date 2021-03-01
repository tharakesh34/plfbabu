package com.pennanttech.pff.schedule.jobs;

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

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.process.ExtractCustomerData;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class CustomerExtractJob implements Job, Serializable {
	private static final long serialVersionUID = 7243833242165622897L;
	private static final Logger logger = LogManager.getLogger(CustomerExtractJob.class);

	public static final String JOB_KEY = "PORTAL_CUSTOMER_EXTRACT_JOB";
	public static final String JOB_KEY_DESCRIPTION = "Customer extract Job";
	public static final String JOB_TRIGGER = "PORTAL_CUSTOMER_EXTRACT_JOB_TRIGGER";
	public static final String JOB_FREQUENCY = App.getProperty("customer.portal.extract.cron");

	private ExtractCustomerData extractCustomerData;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		String jobName = context.getJobDetail().getKey().getName();

		logger.debug(String.format("JOB: %s", jobName));

		try {
			getExtractCustomerData(context).processDownloadCustomers();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private ExtractCustomerData getExtractCustomerData(JobExecutionContext context) {
		if (extractCustomerData == null) {
			extractCustomerData = (ExtractCustomerData) context.getJobDetail().getJobDataMap()
					.get("extractCustomerData");
		}
		return extractCustomerData;
	}

	public static String getCronExpression() {
		String cronExpression = App.getProperty("customer.portal.extract.cron");

		if (StringUtils.isEmpty(cronExpression)) {
			cronExpression = JOB_FREQUENCY;
		}

		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(
					String.format("The cron expression %s for mandate process not valid.", cronExpression));
		}

		return cronExpression;
	}

}
