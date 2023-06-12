package com.pennanttech.external.gst.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.InterfaceErrorCodeUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class FileWriteGSTReqJob extends AbstractJob
		implements InterfaceConstants, ExtIntfConfigConstants, ErrorCodesConstants {
	private static final Logger logger = LogManager.getLogger(FileWriteGSTReqJob.class);

	private FileInterfaceConfig gstCompReqConfig;
	private FileInterfaceConfig gstCompReqDoneConfig;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

		ExtGSTService extGSTService = applicationContext.getBean(ExtGSTService.class);

		// Get the App Date
		Date appDate = SysParamUtil.getAppDate();

		// Fetch Configurations for request
		gstCompReqConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_GST_REQ);
		gstCompReqDoneConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_GST_REQ_DONE);

		// validate request configurations
		if (gstCompReqConfig == null || gstCompReqDoneConfig == null) {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(GS1009));
			return;
		}

		// check if base path exists or not
		String baseFilePath = App.getResourcePath(gstCompReqConfig.getFileLocation());
		if ("".equals(StringUtils.stripToEmpty(baseFilePath))) {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(GS1010));
			return;
		}

		// extract GST vouchers and prepare request file writing
		extGSTService.processRequestFile(gstCompReqConfig, gstCompReqDoneConfig, appDate);
		logger.debug(Literal.LEAVING);
	}

}
