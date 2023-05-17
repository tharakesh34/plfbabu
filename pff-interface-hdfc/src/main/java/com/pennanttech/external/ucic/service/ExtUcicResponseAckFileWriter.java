package com.pennanttech.external.ucic.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;
import com.pennanttech.external.config.dao.ExtGenericDao;
import com.pennanttech.external.config.model.FileInterfaceConfig;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.ucic.dao.ExtUcicDao;
import com.pennanttech.external.util.TextFileUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicResponseAckFileWriter extends TextFileUtil implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(ExtUcicResponseAckFileWriter.class);

	private ExtUcicDao extUcicDao;
	private ExtGenericDao extGenericDao;

	public void processUcicResponseAckFile(Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);

		// Get main configuration for External Interfaces
		List<FileInterfaceConfig> mainConfig = extGenericDao.getExternalConfig();

		FileInterfaceConfig ucicAckConfig = getDataFromList(mainConfig, CONFIG_UCIC_ACK);
		FileInterfaceConfig ucicAckConfConfig = getDataFromList(mainConfig, CONFIG_UCIC_ACK_CONF);

		if (ucicAckConfig == null || ucicAckConfConfig == null) {
			logger.debug(
					"EXT_UCIC: No configuration found for type UCIC ack file. So returning without generating the ack file.");
			return;
		}

		String baseFilePath = App.getResourcePath(ucicAckConfig.getFileLocation());

		String fileName = ucicAckConfig.getFilePrepend()
				+ new SimpleDateFormat(ucicAckConfig.getDateFormat()).format(appDate)
				+ StringUtils.stripToEmpty(ucicAckConfig.getFilePostpend()) + ucicAckConfig.getFileExtension();

		String status = extUcicDao.executeUcicAckFileSP(fileName);

		if ("SUCCESS".equals(status)) {
			// Fetch request file from DB Server location and store it in client SFTP
			FileInterfaceConfig dbServerConfig = getDataFromList(mainConfig, CONFIG_PLF_DB_SERVER);

			if (dbServerConfig == null) {
				logger.debug("EXT_UCIC: DB Server config not found. So returning.");
				return;
			}

			String remoteFilePath = dbServerConfig.getFileSftpLocation();

			if (remoteFilePath == null || "".equals(remoteFilePath)) {
				logger.debug("EXT_UCIC: RemoteFilePath in config not found. So returning.");
				return;
			}

			try {
				FtpClient ftpClient = getftpClientConnection(dbServerConfig);
				ftpClient.download(remoteFilePath, baseFilePath, fileName);
			} catch (Exception e) {
				logger.debug("Unable to download file from DB Server to local path.");
				return;
			}

			if ("Y".equals(ucicAckConfig.getIsSftp())) {
				uploadFilesToClientLocation(appDate, ucicAckConfig, ucicAckConfConfig, baseFilePath, fileName);

			} else {
				// Request file is already downloaded to location, so we are generating complete file for the request
				// file
				writeCompleteFile(appDate, ucicAckConfig, ucicAckConfConfig);
			}
		} else {
			logger.debug("Error In Executing SP_UCIC_WRITE_ACK_FILE Procedure");
		}
		logger.debug(Literal.LEAVING);
	}

	private void uploadFilesToClientLocation(Date appDate, FileInterfaceConfig ucicAckConfig,
			FileInterfaceConfig ucicAckConfConfig, String baseFilePath, String fileName) throws Exception, IOException {
		FtpClient ftpClient;
		// Now upload file to SFTP of client location as per configuration
		File mainFile = new File(baseFilePath + File.separator + fileName);
		ftpClient = getftpClientConnection(ucicAckConfig);
		ftpClient.upload(mainFile, ucicAckConfig.getFileSftpLocation());

		// Now upload complete file to SFTP of client location as per configuration
		String completeFilePathWithName = writeCompleteFile(appDate, ucicAckConfig, ucicAckConfConfig);
		File completeFileToUpload = new File(completeFilePathWithName);
		ftpClient = getftpClientConnection(ucicAckConfig);
		ftpClient.upload(completeFileToUpload, ucicAckConfig.getFileSftpLocation());

		fileBackup(ucicAckConfig, mainFile, completeFileToUpload);
	}

	private void fileBackup(FileInterfaceConfig ucicAckConfig, File mainFile, File completeFileToUpload)
			throws IOException {
		logger.debug(Literal.ENTERING);

		String localBkpLocation = ucicAckConfig.getFileLocalBackupLocation();
		if (localBkpLocation == null || "".equals(localBkpLocation)) {
			logger.debug("EXT_UCIC: Local backup location not configured, so returning.");
			return;
		}

		String localBackupLocation = App.getResourcePath(ucicAckConfig.getFileLocalBackupLocation());

		File mainFileBkp = new File(localBackupLocation + File.separator + mainFile.getName());
		File completeFileBkp = new File(localBackupLocation + File.separator + completeFileToUpload.getName());

		Files.copy(mainFile, mainFileBkp);
		Files.copy(completeFileToUpload, completeFileBkp);

		logger.debug("EXT_UCIC:MainFile & Completefile backup Successful");
		logger.debug(Literal.LEAVING);
	}

	private String writeCompleteFile(Date appDate, FileInterfaceConfig ucicReqCompleteConfig,
			FileInterfaceConfig ucicAckConfConfig) throws Exception {
		String baseFilePath = App.getResourcePath(ucicAckConfConfig.getFileLocation());
		String completeFileName = baseFilePath + File.separator + ucicAckConfConfig.getFilePrepend()
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

	public void setExtGenericDao(ExtGenericDao extGenericDao) {
		this.extGenericDao = extGenericDao;
	}

}
