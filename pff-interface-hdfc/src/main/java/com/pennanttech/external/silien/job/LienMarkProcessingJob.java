package com.pennanttech.external.silien.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.silien.service.LienMarkingService;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class LienMarkProcessingJob extends AbstractJob implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(LienMarkProcessingJob.class);

	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		try {

			applicationContext = ApplicationContextProvider.getApplicationContext();

			LienMarkingService lienMarkingService = applicationContext.getBean("lienMarkingService",
					LienMarkingService.class);

			lienMarkingService.processLienRecords();

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}
}
