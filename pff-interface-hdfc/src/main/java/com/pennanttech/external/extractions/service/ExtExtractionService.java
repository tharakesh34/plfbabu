package com.pennanttech.external.extractions.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtInterfaceDao;
import com.pennanttech.external.extractions.dao.ExtExtractionDao;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.ftp.SftpClient;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtExtractionService implements InterfaceConstants, ExtractionConstants {

	private static final Logger logger = LogManager.getLogger(ExtExtractionService.class);

	private ExtExtractionDao extExtractionDao;
	private ExtInterfaceDao extInterfaceDao;

	private ExternalConfig extractionConfig;

	private ApplicationContext applicationContext;

	public ExtExtractionService() {
		applicationContext = ApplicationContextProvider.getApplicationContext();
		extExtractionDao = applicationContext.getBean(ExtExtractionDao.class);
		extInterfaceDao = applicationContext.getBean(ExtInterfaceDao.class);
	}

	public String procesExtraction(String extractName) {
		logger.debug(Literal.ENTERING);

		if (extractName == null || "".equals(extractName)) {
			logger.debug("Ext_Warning: Configuration received as NULL/EMPTY for Extraction. So returning.");
			return "Error";
		}

		String spName = "";
		if (extractName.equals(CONFIG_FINCON_GL)) {
			spName = EXTRACT_FINCON_GL_SP;
		} else if (extractName.equals(CONFIG_ALM)) {
			spName = EXTRACT_ALM_SP;
		} else if (extractName.equals(CONFIG_RPMS)) {
			spName = EXTRACT_RPMS_SP;
		} else if (extractName.equals(CONFIG_BASEL_ONE)) {
			spName = EXTRACT_BASEL_ONE_SP;
		} else if (extractName.equals(CONFIG_BASEL_TWO)) {
			spName = EXTRACT_BASEL_TWO_SP;
		}

		if ("".equals(spName)) {
			logger.debug("Ext_Warning: SP name not found for Extraction. So returning.");
			return "Error";
		}

		String status = extExtractionDao.executeExtractionSp(spName);

		logger.debug(Literal.LEAVING);

		return status;

	}

	public void processRequestFile(String extractName) {
		logger.debug(Literal.ENTERING);

		Date appDate = SysParamUtil.getAppDate();

		if (appDate == null) {
			logger.debug("Ext_Warning: App Date null. So returning.");
			return;
		}

		// Get main configuration for External Interfaces
		List<ExternalConfig> mainConfig = extInterfaceDao.getExternalConfig();

		extractionConfig = getDataFromList(mainConfig, extractName);

		String fileName = extractionConfig.getFilePrepend()
				+ new SimpleDateFormat(extractionConfig.getDateFormat()).format(appDate)
				+ extractionConfig.getFileExtension();

		String status = extExtractionDao.executeRequestFileSp(fileName);

		if ("SUCCESS".equals(status)) {

			String baseFilePath = App.getResourcePath(extractionConfig.getFileLocation());

			if (baseFilePath == null || "".equals(baseFilePath)) {
				logger.debug("Ext_Warning: Local file path not found for config. So returning.");
				return;
			}

			// Fetch request file from DB Server location to local and the upload it in client SFTP
			ExternalConfig serverConfig = getDataFromList(mainConfig, CONFIG_PLF_DB_SERVER);

			if (serverConfig == null) {
				logger.debug("Ext_Warning: DB Server config not found. So returning.");
				return;
			}

			FtpClient ftpClient = null;
			String host = serverConfig.getHostName();
			int port = serverConfig.getPort();
			String accessKey = serverConfig.getAccessKey();
			String secretKey = serverConfig.getSecretKey();
			try {
				ftpClient = new SftpClient(host, port, accessKey, secretKey);
			} catch (Exception e) {
				logger.debug("Unable to connect to SFTP.");
				return;
			}
			// Now get remote file to local base location using SERVER config
			String remoteFilePath = serverConfig.getFileSftpLocation();
			try {
				ftpClient.download(remoteFilePath, baseFilePath, fileName);
			} catch (Exception e) {
				logger.debug("Unable to download file from DB Server to local path.");
				return;
			}

			if ("Y".equals(extractionConfig.getIsSftp())) {
				// Now upload file to SFTP of client location as per configuration
				File mainFile = new File(baseFilePath + File.separator + fileName);
				ftpClient.upload(mainFile, extractionConfig.getFileSftpLocation());
				mainFile.delete();
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void setExtExtractionDao(ExtExtractionDao extExtractionDao) {
		this.extExtractionDao = extExtractionDao;
	}

	public void setExtInterfaceDao(ExtInterfaceDao extInterfaceDao) {
		this.extInterfaceDao = extInterfaceDao;
	}

	public void setExtractionConfig(ExternalConfig extractionConfig) {
		this.extractionConfig = extractionConfig;
	}

}
