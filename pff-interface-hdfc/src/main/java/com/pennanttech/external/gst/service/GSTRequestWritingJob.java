package com.pennanttech.external.gst.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.EXTIFConfigConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class GSTRequestWritingJob extends AbstractJob implements InterfaceConstants, EXTIFConfigConstants {
	private static final Logger logger = LogManager.getLogger(GSTRequestWritingJob.class);

	private FileInterfaceConfig gstCompReqConfig;
	private FileInterfaceConfig gstCompReqDoneConfig;
	private ExtGSTService extGSTService;

	String GST_REQ_CONFIG_MISSING = "Ext_GST: GST request file configuraton not found.";
	String GST_REQ_BASE_FILE_PATH_MISSING = "Ext_GST: GST request file base path configuraton not found.";

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		// Get the App Date
		Date appDate = SysParamUtil.getAppDate();

		// Fetch Configurations for request
		gstCompReqConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_GST_REQ);
		gstCompReqDoneConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_GST_REQ_DONE);

		// validate request configurations
		if (gstCompReqConfig == null || gstCompReqDoneConfig == null) {
			logger.debug(GST_REQ_CONFIG_MISSING);
			return;
		}

		// check if base path exists or not
		String baseFilePath = App.getResourcePath(gstCompReqConfig.getFileLocation());
		if ("".equals(StringUtils.stripToEmpty(baseFilePath))) {
			logger.debug(GST_REQ_BASE_FILE_PATH_MISSING);
			return;
		}

		// extract GST vouchers and prepare request file writing
		extGSTService.processRequestFile(gstCompReqConfig, gstCompReqDoneConfig, appDate);
		logger.debug(Literal.LEAVING);
	}

}
