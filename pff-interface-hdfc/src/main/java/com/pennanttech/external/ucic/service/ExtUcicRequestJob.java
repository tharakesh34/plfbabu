package com.pennanttech.external.ucic.service;

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
		String custDataExtrctstatus = null;
		try {

			applicationContext = ApplicationContextProvider.getApplicationContext();

			// Get the data extractor service
			ExtUcicDataExtractor extUcicDataExtractor = applicationContext.getBean("extUcicExtractData",
					ExtUcicDataExtractor.class);

			// Get the request file writer service
			ExtUcicRequestFile extUcicRequestFile = applicationContext.getBean("extUcicRequestFile",
					ExtUcicRequestFile.class);

			if (extUcicDataExtractor != null) {
				custDataExtrctstatus = extUcicDataExtractor.extractCustomerData();
			}
			if (custDataExtrctstatus.equals("SUCCESS")) {
				Date appDate = SysParamUtil.getAppDate();
				if (extUcicRequestFile != null) {
					extUcicRequestFile.processUcicRequestFile(appDate);
				}
			} else {
				logger.debug("Customers data extraction Unsuccessful :" + custDataExtrctstatus);
			}

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

}
