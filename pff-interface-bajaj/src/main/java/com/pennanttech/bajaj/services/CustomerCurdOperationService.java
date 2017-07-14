package com.pennanttech.bajaj.services;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.bajaj.process.CustomerCurdOperationProcess;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.CustomerCurdOperations;

public class CustomerCurdOperationService extends BajajService implements CustomerCurdOperations {
	private static final Logger	logger	= Logger.getLogger(CustomerCurdOperationService.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		try {
			processData(new Long(1000));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void processData(Object... params) throws Exception {
		CustomerCurdOperationProcess service = new CustomerCurdOperationProcess(dataSource, new Long(1000), getValueDate());
		service.process("CUSTOMER_CURD_OPERATIONS");
	}
}
