package com.pennanttech.external.ucic.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.google.common.io.Files;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.FileTransferUtil;
import com.pennanttech.external.app.util.InterfaceErrorCodeUtil;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.ucic.dao.ExtUcicDao;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicWeekFileService extends TextFileUtil
		implements InterfaceConstants, ExtIntfConfigConstants, ErrorCodesConstants {
	private static final Logger logger = LogManager.getLogger(ExtUcicWeekFileService.class);
	private ExtUcicDao extUcicDao;
	private FileInterfaceConfig ucicWeeklyConfig;

	public void processWeeklyFileRequest(Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);

		// Fetch UCIC weekly config from main configuration
		ucicWeeklyConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_UCIC_WEEKLY_FILE);

		if (ucicWeeklyConfig == null) {
			return;
		}

		String fileName = ucicWeeklyConfig.getFilePrepend()
				+ new SimpleDateFormat(ucicWeeklyConfig.getDateFormat()).format(appDate)
				+ ucicWeeklyConfig.getFileExtension();

		String baseFilePath = App.getResourcePath(ucicWeeklyConfig.getFileLocation());

		MapSqlParameterSource inPrams = new MapSqlParameterSource();
		inPrams.addValue("aFileName", fileName);

		// Generate Request file from database server
		String status = extUcicDao.executeSP(SP_UCIC_WRITE_WEEKLY_REQ_FILE, inPrams);

		if ("SUCCESS".equals(status)) {
			// Fetch request file from DB Server location and store it in client SFTP
			FileInterfaceConfig serverConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_PLF_DB_SERVER);

			// Now get remote file to local base location using SERVER config
			String remoteFilePath = serverConfig.getFileTransferConfig().getSftpLocation();
			if (remoteFilePath == null || "".equals(remoteFilePath)) {
				return;
			}
			FileTransferUtil fileTransferUtil = new FileTransferUtil(ucicWeeklyConfig);
			try {
				fileTransferUtil.downloadFromSFTP(fileName, baseFilePath);
			} catch (Exception e) {
				logger.debug(InterfaceErrorCodeUtil.getErrorMessage(UC1011));
				return;
			}

			if ("Y".equals(ucicWeeklyConfig.getFileTransfer())) {
				FileTransferUtil sFileTransferUtil = new FileTransferUtil(serverConfig);
				// Now upload file to SFTP of client location as per configuration
				File mainFile = new File(baseFilePath + File.separator + fileName);
				sFileTransferUtil.uploadToSFTP(baseFilePath, fileName);

				fileBackup(ucicWeeklyConfig, mainFile);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void fileBackup(FileInterfaceConfig ucicWeeklyConfig, File mainFile) throws IOException {
		logger.debug(Literal.ENTERING);

		String localBkpLocation = ucicWeeklyConfig.getFileLocalBackupLocation();
		if (localBkpLocation == null || "".equals(localBkpLocation)) {
			return;
		}
		String localBackupLocation = App.getResourcePath(ucicWeeklyConfig.getFileLocalBackupLocation());
		File mainFileBkp = new File(localBackupLocation + File.separator + mainFile.getName());
		Files.copy(mainFile, mainFileBkp);
		logger.debug(Literal.LEAVING);
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

}
