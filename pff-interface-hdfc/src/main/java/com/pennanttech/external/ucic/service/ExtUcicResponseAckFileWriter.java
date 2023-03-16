package com.pennanttech.external.ucic.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtInterfaceDao;
import com.pennanttech.external.fileutil.TextFileUtil;
import com.pennanttech.external.ucic.dao.ExtUcicDao;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.ftp.SftpClient;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicResponseAckFileWriter extends TextFileUtil implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(ExtUcicResponseAckFileWriter.class);

	private ExtUcicDao extUcicDao;
	private ExtInterfaceDao extInterfaceDao;

	public void processUcicResponseAckFile(Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);

		// Get main configuration for External Interfaces
		List<ExternalConfig> mainConfig = extInterfaceDao.getExternalConfig();

		ExternalConfig ucicAckConfig = getDataFromList(mainConfig, CONFIG_UCIC_ACK);
		ExternalConfig ucicAckConfConfig = getDataFromList(mainConfig, CONFIG_UCIC_ACK_CONF);

		if (ucicAckConfig == null || ucicAckConfConfig == null) {
			logger.debug(
					"Ext_Warning: No configuration found for type UCIC ack file. So returning without generating the ack file.");
			return;
		}

		String baseFilePath = App.getResourcePath(ucicAckConfig.getFileLocation());

		String fileName = ucicAckConfig.getFilePrepend()
				+ new SimpleDateFormat(ucicAckConfig.getDateFormat()).format(appDate)
				+ StringUtils.stripToEmpty(ucicAckConfig.getFilePostpend()) + ucicAckConfig.getFileExtension();

		String status = extUcicDao.executeUcicAckFileSP(fileName);

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
			String remoteFilePath = serverConfig.getFileSftpLocation();
			try {
				ftpClient.download(remoteFilePath, baseFilePath, fileName);
			} catch (Exception e) {
				logger.debug("Unable to download file from DB Server to local path.");
				return;
			}

			if ("Y".equals(ucicAckConfig.getIsSftp())) {

				// Now upload file to SFTP of client location as per configuration
				File mainFile = new File(baseFilePath + File.separator + fileName);
				ftpClient.upload(mainFile, ucicAckConfig.getFileSftpLocation());

				// Now upload complete file to SFTP of client location as per configuration
				String completeFilePathWithName = writeCompleteFile(appDate, ucicAckConfig, ucicAckConfConfig);
				File completeFileToUpload = new File(completeFilePathWithName);
				ftpClient.upload(completeFileToUpload, ucicAckConfig.getFileSftpLocation());
			} else {
				// Request file is already downloaded to location, so we are generating complete file for the request
				// file
				writeCompleteFile(appDate, ucicAckConfig, ucicAckConfConfig);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private String writeCompleteFile(Date appDate, ExternalConfig ucicReqCompleteConfig,
			ExternalConfig ucicAckConfConfig) throws Exception {
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

	public void setExtInterfaceDao(ExtInterfaceDao extInterfaceDao) {
		this.extInterfaceDao = extInterfaceDao;
	}

}
