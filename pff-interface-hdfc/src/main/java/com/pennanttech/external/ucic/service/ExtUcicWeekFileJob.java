package com.pennanttech.external.ucic.service;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.util.ApplicationContextProvider;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicWeekFileJob extends AbstractJob {
	private static final Logger logger = LogManager.getLogger(ExtUcicWeekFileJob.class);
	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		try {
			applicationContext = ApplicationContextProvider.getApplicationContext();
			ExtUcicWeekFileService extUcicWeekFileWriter = applicationContext.getBean("ucicWeeklyWritingService",
					ExtUcicWeekFileService.class);
			if (extUcicWeekFileWriter != null) {
				Date appDate = SysParamUtil.getAppDate();
				extUcicWeekFileWriter.processWeeklyFileRequest(appDate);
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}
}
