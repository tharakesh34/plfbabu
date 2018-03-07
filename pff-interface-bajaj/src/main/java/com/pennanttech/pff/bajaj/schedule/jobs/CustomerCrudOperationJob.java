package com.pennanttech.pff.bajaj.schedule.jobs;

import java.io.Serializable;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.bajaj.process.CustomerCurdOperationProcess;
import com.pennanttech.pennapps.core.resource.Literal;

public class CustomerCrudOperationJob implements Job, Serializable {
	private static final Logger				logger				= Logger.getLogger(CustomerCrudOperationJob.class);

	private static final long				serialVersionUID	= 1L;

	public CustomerCrudOperationJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		DataSource dataSource =  (DataSource)context.getJobDetail().getJobDataMap().get("dataSource");
		DataSource finOneDatasource =  (DataSource)context.getJobDetail().getJobDataMap().get("finOneDatasource");
		
		CustomerCurdOperationProcess service = new CustomerCurdOperationProcess(dataSource, finOneDatasource);
		try {
			service.process();
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		
		logger.debug(Literal.LEAVING);
	}
}
