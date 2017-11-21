package com.pennanttech.pff.bajaj.schedule.jobs;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.bajaj.services.CustomerCurdOperationService;
import com.pennanttech.pennapps.core.resource.Literal;

public class CustomerCrudOperationJob implements Job, Serializable {
	private static final Logger				logger				= Logger.getLogger(CustomerCrudOperationJob.class);

	private static final long				serialVersionUID	= 1L;

	private static CustomerCurdOperationService	customerCurdOperationService;

	public CustomerCrudOperationJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		customerCurdOperationService.execute(context);
		logger.debug(Literal.LEAVING);
	}

	public static void setCustomerCurdOperationService(CustomerCurdOperationService customerCurdOperationService) {
		CustomerCrudOperationJob.customerCurdOperationService = customerCurdOperationService;
	}

	

}
