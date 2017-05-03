package com.pennanttech.pff.bajaj.schedule.job;

import java.text.ParseException;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.TriggerBuilder;

import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.job.scheduler.AbstractJobScheduler;
import com.pennanttech.pff.core.job.scheduler.JobSchedulerDetails;

public class BajaJobScheduler extends AbstractJobScheduler {
	protected DataSource			dataSource;
	private static Configuration	conf;

	public BajaJobScheduler() {
		super();
	}

	@Override
	public void addJobs() throws ParseException {
		autoDisbursementJob();
	}

	private void autoDisbursementJob() throws ParseException {
		JobSchedulerDetails jobDetails = null;

		loadConfiguration("DISB_HDFC_IMPORT");
		
		if (conf == null) {
			return;
		}

		String schduleTime = null;
		try {
			schduleTime = conf.getCronExpression();
			BajajInterfaceConstants.autoDisbFileLoaction = conf.getUploadPath();
		} catch (Exception e) {
			throw new ParseException(schduleTime, 0);
		}
		
		if (StringUtils.trimToNull(schduleTime) != null) {
			jobDetails = new JobSchedulerDetails();
			jobDetails.setJobDetail(JobBuilder.newJob(AutoDisburseFileResponseJob.class)
					.withIdentity("AUTO_DISB_RES_FILE", "AUTO_DISB_RES_FILE").withDescription("AUTO_DISB_RES_FILE")
					.build());
			jobDetails.setTrigger(TriggerBuilder.newTrigger().withIdentity("AUTO_DISB_RES_FILE", "AUTO_DISB_RES_FILE")
					.withDescription("Auto disbursement response file trigger.")
					.withSchedule(CronScheduleBuilder.cronSchedule(schduleTime)).build());
			JOB_SCHEDULER_MAP.put("AUTO_DISB_RES_FILE", jobDetails);
		}
	}

	private void loadConfiguration(String configName) {
		DataEngineConfig datEngine;

		try {
			if (conf == null) {
				datEngine = new DataEngineConfig(dataSource);
				conf = datEngine.getConfigurationByName(configName);
			}
		} catch (Exception e) {
			logger.warn("Data engine configuration details not avilable for " + configName);
		} finally {
			datEngine = null;
		}

	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
