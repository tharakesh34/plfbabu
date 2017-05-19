package com.pennanttech.bajaj.services;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.pff.core.services.ResponseService;

public class DisbursementResponseImpsService extends BajajService {
	private static final Logger		logger				= Logger.getLogger(DisbursementResponseImpsService.class);

	@Autowired
	private ResponseService	disbursementResponseService;
	
	public DisbursementResponseImpsService() {
		super();
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
	}
		
}