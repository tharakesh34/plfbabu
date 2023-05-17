package com.pennanttech.external.ucic.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;
import com.pennanttech.external.app.config.dao.ExtGenericDao;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.config.model.InterfaceErrorCode;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.InterfaceErrorCodeUtil;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.ucic.dao.ExtUcicDao;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicWeekFileService extends TextFileUtil implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(ExtUcicWeekFileService.class);
	private ExtUcicDao extUcicDao;
	private ExtGenericDao extGenericDao;
	private FileInterfaceConfig ucicWeeklyConfig;

	public void processWeeklyFileRequest(Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);
		// get error codes handy
		if (InterfaceErrorCodeUtil.getInstance().getInterfaceErrorsList().isEmpty()) {
			List<InterfaceErrorCode> interfaceErrorsList = extGenericDao.fetchInterfaceErrorCodes();
			InterfaceErrorCodeUtil.getInstance().setInterfaceErrorsList(interfaceErrorsList);
		}

		// Get main configuration for External Interfaces
		List<FileInterfaceConfig> mainConfig = extGenericDao.getExternalConfig();

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
			FtpClient ftpClient = null;
			// Fetch request file from DB Server location and store it in client SFTP
			FileInterfaceConfig serverConfig = getDataFromList(mainConfig, CONFIG_PLF_DB_SERVER);

			// Now get remote file to local base location using SERVER config
			String remoteFilePath = serverConfig.getFileSftpLocation();
			if (remoteFilePath == null || "".equals(remoteFilePath)) {
				logger.debug(
						"EXT_UCIC: No configuration found for type UCIC Weekly request file. So returning without generating the request file.");
				return;
			}

			ftpClient = getftpClientConnection(ucicWeeklyConfig);
			try {
				ftpClient.download(remoteFilePath, baseFilePath, fileName);
			} catch (Exception e) {
				logger.debug("Unable to download file from DB Server to local path.");
				return;
			}

			if ("Y".equals(ucicWeeklyConfig.getIsSftp())) {
				ftpClient = getftpClientConnection(serverConfig);
				// Now upload file to SFTP of client location as per configuration
				File mainFile = new File(baseFilePath + File.separator + fileName);
				ftpClient.upload(mainFile, ucicWeeklyConfig.getFileSftpLocation());

				fileBackup(ucicWeeklyConfig, mainFile);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void fileBackup(FileInterfaceConfig ucicWeeklyConfig, File mainFile) throws IOException {
		logger.debug(Literal.ENTERING);

		String localBkpLocation = ucicWeeklyConfig.getFileLocalBackupLocation();
		if (localBkpLocation == null || "".equals(localBkpLocation)) {
			logger.debug("EXT_UCIC: Local backup location not configured, so returning.");
			return;
		}
		String localBackupLocation = App.getResourcePath(ucicWeeklyConfig.getFileLocalBackupLocation());
		File mainFileBkp = new File(localBackupLocation + File.separator + mainFile.getName());
		Files.copy(mainFile, mainFileBkp);
		logger.debug("EXT_UCIC: AckFile backup Successful");
		logger.debug(Literal.LEAVING);
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

	public void setExtGenericDao(ExtGenericDao extGenericDao) {
		this.extGenericDao = extGenericDao;
	}

}
