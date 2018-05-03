package com.pennanttech.pff.bajaj.schedule.jobs;

import java.io.Serializable;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class MasterExtractSchedulerJob implements Job, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MasterExtractSchedulerJob.class);

	public MasterExtractSchedulerJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String configuration = (String) dataMap.get("configuration");
		DataSource dataSource = (DataSource) dataMap.get("dataSource");

		DataEngineExport export = new DataEngineExport(dataSource, 1000, App.DATABASE.name(), true, null);

		try {
			export.exportData(configuration);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}
}
