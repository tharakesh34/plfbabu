package com.pennanttech.external.ucic.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.FileTransferUtil;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.ucic.dao.ExtUcicDao;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicRequestFile extends TextFileUtil implements InterfaceConstants, ExtIntfConfigConstants {
	private static final Logger logger = LogManager.getLogger(ExtUcicRequestFile.class);
	private ExtUcicDao extUcicDao;
	private FileInterfaceConfig ucicReqConfig;
	private FileInterfaceConfig ucicReqCompleteConfig;

	public void processUcicRequestFile(Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);

		// Fetch UCIC configs from main configuration
		ucicReqConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_UCIC_REQ);
		ucicReqCompleteConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_UCIC_REQ_COMPLETE);

		if (ucicReqConfig == null || ucicReqCompleteConfig == null) {
			logger.debug(
					"Ext_Warning: No configuration found for type UCIC request file generation. So returning without generating the request file.");
			return;
		}

		String fileName = ucicReqConfig.getFilePrepend()
				+ new SimpleDateFormat(ucicReqConfig.getDateFormat()).format(appDate)
				+ ucicReqConfig.getFileExtension();

		String baseFilePath = App.getResourcePath(ucicReqConfig.getFileLocation());
		if (baseFilePath == null || "".equals(baseFilePath)) {
			logger.debug("EXT_UCIC: baseFilePath Configuration not found, so returning.");
			return;
		}

		// Generate Request file from database server
		String status = extUcicDao.executeUcicRequestFileSP(fileName);

		if ("SUCCESS".equals(status)) {

			String remoteFilePath = null;

			// Fetch request file from DB Server location and store it in client SFTP
			FileInterfaceConfig serverConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_PLF_DB_SERVER);

			if (serverConfig == null) {
				logger.debug("EXT_UCIC: CONFIG_PLF_DB_SERVER Configuration not found, so returning.");
				return;
			}

			// Now get remote file to local base location using SERVER config
			remoteFilePath = serverConfig.getFileTransferConfig().getSftpLocation();
			if (remoteFilePath == null || "".equals(remoteFilePath)) {
				logger.debug("EXT_UCIC: remoteFilePath in Configuration not found, so returning.");
				return;
			}

			// Create FTP connection
			FileTransferUtil fileTransferUtil = new FileTransferUtil(serverConfig);

			try {
				// Download file from DB server to local location
				fileTransferUtil.downloadFromSFTP(fileName, baseFilePath);
				logger.debug("File Download Sucessful from DB Server to local path");
			} catch (Exception e) {
				logger.debug("Unable to download file from DB Server to local path.", e);
				return;
			}

			// Uploading to HDFC SFTP
			if ("Y".equals(ucicReqConfig.getFileTransfer())) {
				uploadToClientLocation(appDate, fileName, baseFilePath);
			} else {
				// Request file is already downloaded to local location,
				// so we are generating complete file for the request file
				writeCompleteFile(appDate);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void uploadToClientLocation(Date appDate, String fileName, String baseFilePath) {
		FileInterfaceConfig serverConfig;
		serverConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_UCIC_REQ);
		if (serverConfig == null) {
			logger.debug("EXT_UCIC: CONFIG_UCIC_REQ Configuration not found, so returning.");
			return;
		}
		FileTransferUtil fileTransferUtil = new FileTransferUtil(serverConfig);
		try {
			// Now upload file to SFTP of client location as per configuration
			File mainFile = new File(baseFilePath + File.separator + fileName);
			fileTransferUtil.uploadToSFTP(baseFilePath, fileName);
			logger.debug("EXT_UCIC:ReqFile upload Successful to Destination");

			// Now upload complete file to SFTP of client location as per configuration
			String completeFilePathWithName = writeCompleteFile(appDate);
			File completeFileToUpload = new File(completeFilePathWithName);
			fileTransferUtil.uploadToSFTP(baseFilePath, completeFilePathWithName);
			logger.debug("EXT_UCIC:Completefile upload Sucessful to Destination");
			// Deleting mainFile and CompleteFile post uploading
			fileBackup(serverConfig, mainFile, completeFileToUpload);
		} catch (Exception e) {
			logger.debug("EXT_UCIC:Unable to upload files from local path to destination.", e);
			return;
		}
	}

	private void fileBackup(FileInterfaceConfig serverConfig, File mainFile, File completeFileToUpload)
			throws IOException {
		logger.debug(Literal.ENTERING);

		String localBkpLocation = serverConfig.getFileLocalBackupLocation();
		if (localBkpLocation == null || "".equals(localBkpLocation)) {
			logger.debug("EXT_UCIC: Local backup location not configured, so returning.");
			return;
		}

		String localBackupLocation = App.getResourcePath(serverConfig.getFileLocalBackupLocation());

		File mainFileBkp = new File(localBackupLocation + File.separator + mainFile.getName());
		File completeFileBkp = new File(localBackupLocation + File.separator + completeFileToUpload.getName());

		Files.copy(mainFile, mainFileBkp);
		Files.copy(completeFileToUpload, completeFileBkp);

		logger.debug("EXT_UCIC:MainFile & Completefile backup Successful");
		logger.debug(Literal.LEAVING);
	}

	protected void fileDeletion(FileInterfaceConfig serverConfig, File mainFile, File completeFileToUpload)
			throws IOException {
		logger.debug(Literal.ENTERING);
		FileTransferUtil fileTransferUtil = new FileTransferUtil(serverConfig);
		fileTransferUtil.deleteFileFromSFTP(mainFile.getName());
		fileTransferUtil.deleteFileFromSFTP(completeFileToUpload.getName());
		logger.debug("MainFile & Completefile deletion Successful");
		logger.debug(Literal.LEAVING);
	}

	private String writeCompleteFile(Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);
		String baseFilePath = App.getResourcePath(ucicReqCompleteConfig.getFileLocation());
		String completeFileName = baseFilePath + File.separator + ucicReqCompleteConfig.getFilePrepend()
				+ new SimpleDateFormat(ucicReqCompleteConfig.getDateFormat()).format(appDate)
				+ ucicReqCompleteConfig.getFileExtension();

		List<StringBuilder> emptyList = new ArrayList<StringBuilder>();
		emptyList.add(new StringBuilder(""));
		super.writeDataToFile(completeFileName, emptyList);
		logger.debug(Literal.LEAVING);
		return completeFileName;
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

}
