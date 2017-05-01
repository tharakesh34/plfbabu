package com.pennanttech.pff.bajaj.schedule.job;

import java.text.ParseException;

import javax.sql.DataSource;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.TriggerBuilder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.job.scheduler.AbstractJobScheduler;
import com.pennanttech.pff.core.job.scheduler.JobSchedulerDetails;

public class BajaJobScheduler extends AbstractJobScheduler {
	protected DataSource				dataSource;
	private NamedParameterJdbcTemplate	namedJdbcTemplate;
	
	
	public BajaJobScheduler() {
		super();
	}

	@Override
	public void addJobs() throws ParseException {
		JobSchedulerDetails jobDetails = null;

		String schduleTime = null;
		try {
			schduleTime = (String) getSMTParameter("AUTO_DISB_RES_FILE_SCHEDULE_TIME", String.class);
		} catch (Exception e) {
			throw new ParseException(schduleTime, 0);
		}

		jobDetails = new JobSchedulerDetails();
		jobDetails.setJobDetail(JobBuilder.newJob(AutoDisburseFileResponseJob.class).withIdentity("AUTO_DISB_RES_FILE", "AUTO_DISB_RES_FILE").withDescription("AUTO_DISB_RES_FILE").build());
		jobDetails.setTrigger(TriggerBuilder.newTrigger().withIdentity("AUTO_DISB_RES_FILE", "AUTO_DISB_RES_FILE").withDescription("Import batch files Trigger").withSchedule(CronScheduleBuilder.cronSchedule(schduleTime)).build());
		JOB_SCHEDULER_MAP.put("AUTO_DISB_RES_FILE", jobDetails);
	}

	private Object getSMTParameter(String sysParmCode, Class<?> type) throws Exception {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT SYSPARMVALUE FROM SMTPARAMETERS where SYSPARMCODE = :SYSPARMCODE");
		paramMap.addValue("SYSPARMCODE", sysParmCode);

		try {
			return namedJdbcTemplate.queryForObject(sql.toString(), paramMap, type);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("The parameter code " + sysParmCode + " not configured.");
		}
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}


}
