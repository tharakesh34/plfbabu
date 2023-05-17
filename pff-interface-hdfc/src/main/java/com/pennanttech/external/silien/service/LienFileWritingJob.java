package com.pennanttech.external.silien.service;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class LienFileWritingJob extends AbstractJob implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(LienFileWritingJob.class);

	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		try {
			applicationContext = ApplicationContextProvider.getApplicationContext();

			LienFileWritingService lienFileWritingService = applicationContext.getBean("lienFileWritingService",
					LienFileWritingService.class);

			if (lienFileWritingService != null) {
				Date appDate = SysParamUtil.getAppDate();
				lienFileWritingService.processSILienMarkingRequest(appDate);
			}

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

}
