package com.pennanttech.pff.bajaj.schedule.jobs;

import java.text.ParseException;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.TriggerBuilder;

import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.job.scheduler.AbstractJobScheduler;
import com.pennanttech.pff.core.job.scheduler.JobSchedulerDetails;

public class BajajJobScheduler extends AbstractJobScheduler {
	private static final Logger	logger		= Logger.getLogger(BajajJobScheduler.class);

	protected DataSource		dataSource;
	private DataEngineConfig	datEngine	= null;

	public BajajJobScheduler() {
		super();
	}

	@Override
	public void addJobs() throws ParseException {
		logger.debug(Literal.ENTERING);
		
		//autoDisbursementJob();
		
		impsDisbursementRespJob();
		posidexCustomerUpdateRespJob();

		logger.debug(Literal.LEAVING);
	}

	private void autoDisbursementJob() {
		logger.debug(Literal.ENTERING);

		// Auto Disbrsement response file 
		Configuration configuration = loadConfiguration("DISB_HDFC_IMPORT");
		if (configuration != null) {
			String schduleTime = null;
			try {
				schduleTime = configuration.getCronExpression();
				BajajInterfaceConstants.autoDisbFileLoaction = configuration.getUploadPath();
			} catch (Exception e) {
				logger.warn("Disbursement response file reading scheduler not started.");
			}

			if (StringUtils.trimToNull(schduleTime) != null) {
				JobSchedulerDetails jobDetails = new JobSchedulerDetails();
				jobDetails.setJobDetail(JobBuilder.newJob(DisbursementResponseJob.class)
						.withIdentity("AUTO_DISB_RES_JOB", "AUTO_DISB_RES_FILE")
						.withDescription("Auto Disbrsement response file job").build());
				jobDetails.setTrigger(TriggerBuilder.newTrigger()
						.withIdentity("AUTO_DISB_RES_FILE_TRIGGER", "AUTO_DISB_RES_FILE")
						.withDescription("Auto disbursement response file trigger.")
						.withSchedule(CronScheduleBuilder.cronSchedule(schduleTime)).build());
				JOB_SCHEDULER_MAP.put("AUTO_DISB_RES_FILE", jobDetails);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void impsDisbursementRespJob() {
		logger.debug(Literal.ENTERING);

		Configuration configuration = loadConfiguration("DISB_IMPS_RESPONSE");
		if (configuration != null) {
			String schduleTime = null;
			try {
				schduleTime = configuration.getCronExpression();
			} catch (Exception e) {
				logger.warn("Disbursement IMPS response scheduler not started.");
			}

			if (StringUtils.trimToNull(schduleTime) != null) {
				JobSchedulerDetails jobDetails = new JobSchedulerDetails();
				jobDetails.setJobDetail(JobBuilder.newJob(DisbrsementImpsResponseJob.class)
						.withIdentity("DISB_IMPS_RES_JOB", "DISB_IMPS_RESPONSE")
						.withDescription("Disbrsement imps response job").build());
				jobDetails.setTrigger(TriggerBuilder.newTrigger()
						.withIdentity("DISB_IMPS_RES_JOB_TRIGGER", "DISB_IMPS_RESPONSE")
						.withDescription("Disbrsement imps response job trigger.")
						.withSchedule(CronScheduleBuilder.cronSchedule(schduleTime)).build());
				JOB_SCHEDULER_MAP.put("DISB_IMPS_RES_JOB", jobDetails);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void posidexCustomerUpdateRespJob() {
		logger.debug(Literal.ENTERING);

		Configuration configuration = loadConfiguration("POSIDEX_CUSTOMER_UPDATE_RESPONSE");
		if (configuration != null) {
			String schduleTime = null;
			try {
				schduleTime = configuration.getCronExpression();
			} catch (Exception e) {
				logger.warn("Posidex customer update response scheduler not started.");
			}

			if (StringUtils.trimToNull(schduleTime) != null) {
				JobSchedulerDetails jobDetails = new JobSchedulerDetails();
				jobDetails.setJobDetail(JobBuilder.newJob(PosidexCustomerUpdateResponseJob.class)
						.withIdentity("POSIDEX_RES_JOB", "POSIDEX_CUSTOMER_RESPONSE")
						.withDescription("Posidex customer update response job trigger.").build());
				jobDetails.setTrigger(TriggerBuilder.newTrigger()
						.withIdentity("POSIDEX_RES_JOB_TRIGGER", "POSIDEX_CUSTOMER_RESPONSE")
						.withDescription("Posidex customer update response job trigger.")
						.withSchedule(CronScheduleBuilder.cronSchedule(schduleTime)).build());
				JOB_SCHEDULER_MAP.put("POSIDEX_RES_JOB", jobDetails);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private Configuration loadConfiguration(String configName) {
		logger.debug(Literal.ENTERING);
		Configuration configuration = null;
		try {
			if (datEngine == null) {
				datEngine = new DataEngineConfig(dataSource);
			}
			configuration = datEngine.getConfigurationByName(configName);
		} catch (Exception e) {
			logger.warn("Data engine configuration details not avilable for " + configName);
		}
		logger.debug(Literal.LEAVING);
		return configuration;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
