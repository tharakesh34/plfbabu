package com.pennanttech.external.ucic.service;

import java.io.File;
import java.text.SimpleDateFormat;
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

public class ExtUcicWeekFileService extends TextFileUtil implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(ExtUcicWeekFileService.class);
	private ExtUcicDao extUcicDao;
	private ExtInterfaceDao extInterfaceDao;
	private ExternalConfig ucicWeeklyConfig;

	public void processWeeklyFileRequest(Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);
		// get error codes handy
		if (ExtErrorCodes.getInstance().getInterfaceErrorsList().isEmpty()) {
			List<InterfaceErrorCode> interfaceErrorsList = extInterfaceDao.fetchInterfaceErrorCodes();
			ExtErrorCodes.getInstance().setInterfaceErrorsList(interfaceErrorsList);
		}

		// Get main configuration for External Interfaces
		List<ExternalConfig> mainConfig = extInterfaceDao.getExternalConfig();

		// Fetch UCIC weekly config from main configuration
		ucicWeeklyConfig = getDataFromList(mainConfig, CONFIG_UCIC_WEEKLY_FILE);

		if (ucicWeeklyConfig == null) {
			logger.debug(
					"Ext_Warning: No configuration found for type UCIC Weekly request file. So returning without generating the request file.");
			return;
		}

		String fileName = ucicWeeklyConfig.getFilePrepend()
				+ new SimpleDateFormat(ucicWeeklyConfig.getDateFormat()).format(appDate)
				+ ucicWeeklyConfig.getFileExtension();

		String baseFilePath = App.getResourcePath(ucicWeeklyConfig.getFileLocation());

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

			if ("Y".equals(ucicWeeklyConfig.getIsSftp())) {

				// Now upload file to SFTP of client location as per configuration
				File mainFile = new File(baseFilePath + File.separator + fileName);
				ftpClient.upload(mainFile, ucicWeeklyConfig.getFileSftpLocation());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

	public void setExtInterfaceDao(ExtInterfaceDao extInterfaceDao) {
		this.extInterfaceDao = extInterfaceDao;
	}

}
