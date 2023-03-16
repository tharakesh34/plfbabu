package com.pennanttech.external.ucic.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.config.ExtErrorCodes;
import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.config.InterfaceErrorCode;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtInterfaceDao;
import com.pennanttech.external.fileutil.TextFileUtil;
import com.pennanttech.external.ucic.dao.ExtUcicDao;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.ftp.SftpClient;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicRequestFile extends TextFileUtil implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(ExtUcicRequestFile.class);
	private ExtUcicDao extUcicDao;
	private ExtInterfaceDao extInterfaceDao;
	private ExternalConfig ucicReqConfig;
	private ExternalConfig ucicReqCompleteConfig;

	public void processUcicRequestFile(Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);
		// get error codes handy
		if (ExtErrorCodes.getInstance().getInterfaceErrorsList().isEmpty()) {
			List<InterfaceErrorCode> interfaceErrorsList = extInterfaceDao.fetchInterfaceErrorCodes();
			ExtErrorCodes.getInstance().setInterfaceErrorsList(interfaceErrorsList);
		}

		// Get main configuration for External Interfaces
		List<ExternalConfig> mainConfig = extInterfaceDao.getExternalConfig();

		// Fetch UCIC configs from main configuration
		ucicReqConfig = getDataFromList(mainConfig, CONFIG_UCIC_REQ);
		ucicReqCompleteConfig = getDataFromList(mainConfig, CONFIG_UCIC_REQ_COMPLETE);

		if (ucicReqConfig == null || ucicReqCompleteConfig == null) {
			logger.debug(
					"Ext_Warning: No configuration found for type UCIC request file generation. So returning without generating the request file.");
			return;
		}

		String fileName = ucicReqConfig.getFilePrepend()
				+ new SimpleDateFormat(ucicReqConfig.getDateFormat()).format(appDate)
				+ ucicReqConfig.getFileExtension();

		String baseFilePath = App.getResourcePath(ucicReqConfig.getFileLocation());

		// Generate Request file from database server
		String status = extUcicDao.executeUcicRequestFileSP(fileName);

		if ("SUCCESS".equals(status)) {
			// Fetch request file from DB Server location and store it in client SFTP
			ExternalConfig serverConfig = getDataFromList(mainConfig, CONFIG_UCIC_PLF_SERVER);
			FtpClient ftpClient = null;
			String host = serverConfig.getHostName();
			int port = serverConfig.getPort();
			String accessKey = serverConfig.getAccessKey();
			String secretKey = serverConfig.getSecretKey();
			try {
				ftpClient = new SftpClient(host, port, accessKey, secretKey);
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("Unable to connect to SFTP.");
			}
			// Now get remote file to local base location using SERVER config
			String remoteFilePath = serverConfig.getFileSftpLocation();
			try {
				ftpClient.download(remoteFilePath, baseFilePath, fileName);
			} catch (Exception e) {
				logger.debug("Unable to download file from DB Server to local path.");
				return;
			}

			if ("Y".equals(ucicReqConfig.getIsSftp())) {

				// Now upload file to SFTP of client location as per configuration
				File mainFile = new File(baseFilePath + File.separator + fileName);
				ftpClient.upload(mainFile, ucicReqConfig.getFileSftpLocation());

				// Now upload complete file to SFTP of client location as per configuration
				String completeFilePathWithName = writeCompleteFile(appDate);
				File completeFileToUpload = new File(completeFilePathWithName);
				ftpClient.upload(completeFileToUpload, ucicReqConfig.getFileSftpLocation());
			} else {
				// Request file is already downloaded to location, so we are generating complete file for the request
				// file
				writeCompleteFile(appDate);
			}

		}

		logger.debug(Literal.LEAVING);
	}

	private String writeCompleteFile(Date appDate) throws Exception {
		String baseFilePath = App.getResourcePath(ucicReqCompleteConfig.getFileLocation());
		String completeFileName = baseFilePath + File.separator + ucicReqCompleteConfig.getFilePrepend()
				+ new SimpleDateFormat(ucicReqCompleteConfig.getDateFormat()).format(appDate)
				+ ucicReqCompleteConfig.getFileExtension();

		List<StringBuilder> emptyList = new ArrayList<StringBuilder>();
		emptyList.add(new StringBuilder(""));
		super.writeDataToFile(completeFileName, emptyList);
		return completeFileName;
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

	public void setExtInterfaceDao(ExtInterfaceDao extInterfaceDao) {
		this.extInterfaceDao = extInterfaceDao;
	}

}
