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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.google.common.io.Files;
import com.pennanttech.external.app.config.dao.ExternalDao;
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

public class ExtUcicResponseAckFileWriter extends TextFileUtil
		implements InterfaceConstants, ExtIntfConfigConstants, ErrorCodesConstants {

	private static final Logger logger = LogManager.getLogger(ExtUcicResponseAckFileWriter.class);

	private ExtUcicDao extUcicDao;

	private ExternalDao externalDao;

	public void processUcicResponseAckFile(Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);

		FileInterfaceConfig ucicAckConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_UCIC_ACK);
		FileInterfaceConfig ucicAckConfConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_UCIC_ACK_CONF);

		if (ucicAckConfig == null || ucicAckConfConfig == null) {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(UC1010));
			return;
		}

		String baseFilePath = App.getResourcePath(ucicAckConfig.getFileLocation());

		String fileName = ucicAckConfig.getFilePrepend()
				+ new SimpleDateFormat(ucicAckConfig.getDateFormat()).format(appDate)
				+ StringUtils.stripToEmpty(ucicAckConfig.getFilePostpend()) + ucicAckConfig.getFileExtension();

		MapSqlParameterSource inPrams = new MapSqlParameterSource();
		inPrams.addValue("aFileName", fileName);

		String status = externalDao.executeSP(SP_UCIC_WRITE_ACK_FILE, inPrams);

		if ("SUCCESS".equals(status)) {
			// Fetch request file from DB Server location and store it in client SFTP
			FileInterfaceConfig dbServerConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_PLF_DB_SERVER);

			if (dbServerConfig == null) {
				logger.debug(InterfaceErrorCodeUtil.getErrorMessage(UC1007));
				return;
			}
			String remoteFilePath = dbServerConfig.getFileTransferConfig().getSftpLocation();

			if (remoteFilePath == null || "".equals(remoteFilePath)) {
				logger.debug(InterfaceErrorCodeUtil.getErrorMessage(UC1008));
				return;
			}

			try {
				FileTransferUtil fileTransferUtil = new FileTransferUtil(dbServerConfig);
				fileTransferUtil.downloadFromSFTP(fileName, baseFilePath);
			} catch (Exception e) {
				logger.debug(InterfaceErrorCodeUtil.getErrorMessage(UC1011));
				return;
			}

			if ("Y".equals(ucicAckConfig.getFileTransfer())) {
				uploadFilesToClientLocation(appDate, ucicAckConfig, ucicAckConfConfig, baseFilePath, fileName);

			} else {
				// Request file is already downloaded to location, so we are generating complete file for the request
				// file
				writeCompleteFile(appDate, ucicAckConfig, ucicAckConfConfig);
			}
		} else {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(UC1012));
		}
		logger.debug(Literal.LEAVING);
	}

	private void uploadFilesToClientLocation(Date appDate, FileInterfaceConfig ucicAckConfig,
			FileInterfaceConfig ucicAckConfConfig, String baseFilePath, String fileName) throws Exception, IOException {
		// Now upload file to SFTP of client location as per configuration
		File mainFile = new File(baseFilePath + File.separator + fileName);
		FileTransferUtil fileTransferUtil = new FileTransferUtil(ucicAckConfig);
		fileTransferUtil.uploadToSFTP(baseFilePath, fileName);
		// Now upload complete file to SFTP of client location as per configuration
		String completeFilePathWithName = writeCompleteFile(appDate, ucicAckConfig, ucicAckConfConfig);
		File completeFileToUpload = new File(completeFilePathWithName);
		fileTransferUtil.uploadToSFTP(baseFilePath, completeFilePathWithName);
		fileBackup(ucicAckConfig, mainFile, completeFileToUpload);
	}

	private void fileBackup(FileInterfaceConfig ucicAckConfig, File mainFile, File completeFileToUpload)
			throws IOException {
		logger.debug(Literal.ENTERING);

		String localBkpLocation = ucicAckConfig.getFileLocalBackupLocation();
		if (localBkpLocation == null || "".equals(localBkpLocation)) {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(UC1013));
			return;
		}

		String localBackupLocation = App.getResourcePath(ucicAckConfig.getFileLocalBackupLocation());

		File mainFileBkp = new File(localBackupLocation + File.separator + mainFile.getName());
		File completeFileBkp = new File(localBackupLocation + File.separator + completeFileToUpload.getName());

		Files.copy(mainFile, mainFileBkp);
		Files.copy(completeFileToUpload, completeFileBkp);
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

	public void setExternalDao(ExternalDao externalDao) {
		this.externalDao = externalDao;
	}

}
