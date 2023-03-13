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

public class ExtUcicResponseJob extends AbstractJob {
	private static final Logger logger = LogManager.getLogger(ExtUcicResponseJob.class);
	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		try {
			applicationContext = ApplicationContextProvider.getApplicationContext();

			ExtUcicResponseFolderReader extUcicResponseFolderReader = applicationContext
					.getBean("extUcicResponseFolderReader", ExtUcicResponseFolderReader.class);

			ExtUcicResponseFileExtractor extUcicResponseFileExtractor = applicationContext
					.getBean("extUcicResponseFileExtractor", ExtUcicResponseFileExtractor.class);

			ExtUcicResponseProcessor ucicResponseProcessor = applicationContext.getBean("extUcicResponseProcessor",
					ExtUcicResponseProcessor.class);

			ExtUcicResponseAckFileWriter responseAckFileWriter = applicationContext
					.getBean("extUcicResponseAckFileWriter", ExtUcicResponseAckFileWriter.class);

			if (extUcicResponseFolderReader != null) {
				extUcicResponseFolderReader.readFolderForFiles();
			}

			if (extUcicResponseFileExtractor != null) {
				extUcicResponseFileExtractor.readFileAndExtracData();
			}

			if (ucicResponseProcessor != null) {
				ucicResponseProcessor.processExtractedRecords();
			}

			if (responseAckFileWriter != null) {
				Date appDate = SysParamUtil.getAppDate();
				responseAckFileWriter.processUcicResponseAckFile(appDate);
			}

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

}