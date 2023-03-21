package com.pennanttech.extrenal.ucic.service;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicRequestJob extends AbstractJob {
	private static final Logger logger = LogManager.getLogger(ExtUcicRequestJob.class);
	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		try {
			applicationContext = ApplicationContextProvider.getApplicationContext();
			ExtUcicRequestData extUcicRequestData = applicationContext.getBean("extUcicRequestData",
					ExtUcicRequestData.class);
			ExtUcicRequestFile extUcicRequestFile = applicationContext.getBean("extUcicRequestFile",
					ExtUcicRequestFile.class);
			Date appDate = SysParamUtil.getAppDate();
			if (extUcicRequestData != null) {
				extUcicRequestData.fetchAndProcessCustomers(appDate);
			}
			if (extUcicRequestFile != null) {
				extUcicRequestFile.processUcicRequestFile(appDate);
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

}
