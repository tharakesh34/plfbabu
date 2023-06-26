package com.pennanttech.external.extractions.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.app.config.dao.ExternalDao;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.FileTransferUtil;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class UCICExtractionService extends TextFileUtil implements InterfaceConstants, ExtIntfConfigConstants {

	private static final Logger logger = LogManager.getLogger(UCICExtractionService.class);
	private ExternalDao externalDao;

	public void processExtraction() {
		logger.debug(Literal.ENTERING);

		try {

			String resp = externalDao.executeSP(SP_EXTRACT_UCIC_DATA);

			if (resp != null && "SUCCESS".equals(resp)) {
				logger.debug("Successfully extracted customers data.");
			} else {
				logger.debug("Customers data extraction Unsuccessful :" + resp);
				return;
			}

			Date appDate = SysParamUtil.getAppDate();

			// Fetch UCIC configs from main configuration
			FileInterfaceConfig ucicReqConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_UCIC_REQ);
			FileInterfaceConfig ucicReqCompleteConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_UCIC_REQ_COMPLETE);

			if (ucicReqConfig == null || ucicReqCompleteConfig == null) {
				return;
			}

			String baseFilePath = App.getResourcePath(ucicReqConfig.getFileLocation());
			if (baseFilePath == null || "".equals(baseFilePath)) {
				return;
			}

			String fileName = ucicReqConfig.getFilePrepend()
					+ new SimpleDateFormat(ucicReqConfig.getDateFormat()).format(appDate)
					+ ucicReqConfig.getFileExtension();

			MapSqlParameterSource inPrams = new MapSqlParameterSource();
			inPrams.addValue("aFileName", fileName);

			// Generate Request file from database server
			String status = externalDao.executeSP(SP_UCIC_WRITE_REQ_FILE, inPrams);

			if (!"SUCCESS".equals(status)) {
				return;
			}

			// Fetch request file from DB Server location and store it in client SFTP
			FileInterfaceConfig serverConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_PLF_DB_SERVER);
			String remoteFilePath = null;

			if (serverConfig == null) {
				return;
			}

			// Now get remote file to local base location using SERVER config
			remoteFilePath = serverConfig.getFileTransferConfig().getSftpLocation();
			if (remoteFilePath == null || "".equals(remoteFilePath)) {
				return;
			}

			// Create FTP connection
			FileTransferUtil fileTransferUtil = new FileTransferUtil(serverConfig);
			// Download file from DB server to local location
			fileTransferUtil.downloadFromSFTP(fileName, baseFilePath);

			logger.debug("File Download Sucessful from DB Server to local path");

			// Uploading to HDFC SFTP
			if ("Y".equals(ucicReqConfig.getFileTransfer())) {
				FileTransferUtil sftpServerConfig = new FileTransferUtil(serverConfig);
				sftpServerConfig.uploadToSFTP(baseFilePath, fileName);
			}

			// since no exception write and share the complete file

			String baseFilePathCompleted = App.getResourcePath(ucicReqCompleteConfig.getFileLocation());
			String completeFileName = baseFilePathCompleted + File.separator + ucicReqCompleteConfig.getFilePrepend()
					+ new SimpleDateFormat(ucicReqCompleteConfig.getDateFormat()).format(appDate)
					+ ucicReqCompleteConfig.getFileExtension();

			List<StringBuilder> emptyList = new ArrayList<StringBuilder>();
			emptyList.add(new StringBuilder(""));
			super.writeDataToFile(completeFileName, emptyList);
			fileTransferUtil.uploadToSFTP(baseFilePath, completeFileName);
		} catch (Exception e) {
			logger.debug("Unable to download file from DB Server to local path.", e);
			return;
		}

		logger.debug(Literal.LEAVING);
	}

	public void setExternalDao(ExternalDao externalDao) {
		this.externalDao = externalDao;
	}
}
