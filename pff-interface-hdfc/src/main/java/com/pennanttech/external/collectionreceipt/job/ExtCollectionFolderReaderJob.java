package com.pennanttech.external.collectionreceipt.job;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import com.pennanttech.external.collectionreceipt.dao.ExtCollectionReceiptDao;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;
import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.config.model.FileInterfaceConfig;
import com.pennanttech.external.config.model.InterfaceErrorCode;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtGenericDao;
import com.pennanttech.external.util.ExtSFTPUtil;
import com.pennanttech.external.util.InterfaceErrorCodeUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtCollectionFolderReaderJob extends AbstractJob implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(ExtCollectionFolderReaderJob.class);

	private ExtGenericDao extInterfaceDao;
	private ExtCollectionReceiptDao extCollectionReceiptDao;
	private ApplicationContext applicationContext;

	private FileInterfaceConfig collectionReqConfig;

	String COLLECTION_REQ_CONFIG_MISSING = "Ext_Warning: No configuration found for type Collection receipt interface. So returning without generating the request file.";
	String COLLECTION_REQ_BASE_FILE_PATH_MISSING = "Ext_Warning: No configuration found for type Collection receipt baseFilePath. So returning without generating the request file.";
	String COLLECTION_REQ_REMOTE_FILE_PATH_MISSING = "Ext_Warning: No configuration found for type Collection receipt remoteFilePath. So returning without generating the request file.";

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		// Get all the required DAO's
		applicationContext = ApplicationContextProvider.getApplicationContext();
		extInterfaceDao = applicationContext.getBean(ExtGenericDao.class);
		extCollectionReceiptDao = applicationContext.getBean("extCollectionReceiptDao", ExtCollectionReceiptDao.class);

		fetchRemoteFiles();

		logger.debug(Literal.LEAVING);
	}

	private void fetchRemoteFiles() {
		logger.debug(Literal.ENTERING);

		// Get main configuration for External Interfaces
		List<FileInterfaceConfig> mainConfig = extInterfaceDao.getExternalConfig();

		// Fetch Collection Receipt Request config from main configuration
		collectionReqConfig = getDataFromList(mainConfig, CONFIG_COLLECTION_REQ_CONF);

		// get error codes handy
		if (InterfaceErrorCodeUtil.getInstance().getInterfaceErrorsList().isEmpty()) {
			List<InterfaceErrorCode> interfaceErrorsList = extInterfaceDao.fetchInterfaceErrorCodes();
			InterfaceErrorCodeUtil.getInstance().setInterfaceErrorsList(interfaceErrorsList);
		}

		if (collectionReqConfig == null) {
			logger.debug(COLLECTION_REQ_CONFIG_MISSING);
			return;
		}

		String baseFilePath = collectionReqConfig.getFileLocation();

		if ("".equals(StringUtils.stripToEmpty(baseFilePath))) {
			logger.debug(COLLECTION_REQ_BASE_FILE_PATH_MISSING);
			return;
		}

		String remoteFilePath = collectionReqConfig.getFileSftpLocation();

		if ("".equals(StringUtils.stripToEmpty(remoteFilePath))) {
			logger.debug(COLLECTION_REQ_REMOTE_FILE_PATH_MISSING);
			return;
		}

		ExtSFTPUtil requestSFTPUtil = new ExtSFTPUtil(collectionReqConfig);

		// Get list of files in SFTP.
		List<String> fileNames = requestSFTPUtil.getFileListFromSFTP(remoteFilePath);

		String localFolderPath = App.getResourcePath(baseFilePath);

		List<String> filteredFileNames = new ArrayList<>();
		for (String fileName : fileNames) {

			if (fileName.contains(".inproc")) {
				logger.debug("EXT_WARN: File is having extension inproc, so returning.");
				continue;
			}

			try {
				// Download file from remote location
				FtpClient ftpClient = requestSFTPUtil.getSFTPConnection();
				ftpClient.download(remoteFilePath, localFolderPath, fileName);

				// Changing file to inproc in local folder
				String procName = fileName.substring(0, fileName.indexOf(collectionReqConfig.getFileExtension()));
				// Change Extension to .inproc
				procName = procName.concat(".inproc");

				Path srcPath = Paths.get(localFolderPath + File.separator + fileName);
				Path destPath = Paths.get(localFolderPath + File.separator + procName);
				Files.copy(srcPath, destPath, StandardCopyOption.COPY_ATTRIBUTES);

				// Push .inproc file to SFTP
				FtpClient sftpClient = requestSFTPUtil.getSFTPConnection();
				sftpClient.upload(localFolderPath, collectionReqConfig.getFileSftpLocation(), procName);

				// Checksum for file

				// Delete original file in SFTP
				String remFilePath = collectionReqConfig.getFileSftpLocation();
				requestSFTPUtil.deleteFile(remFilePath + "/" + fileName);
				filteredFileNames.add(fileName);
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			}

		}

		if (filteredFileNames.size() > 0) {
			saveFetchedFiles();
		}

		logger.debug(Literal.LEAVING);
	}

	private void saveFetchedFiles() {
		logger.debug(Literal.ENTERING);
		String localFolderPath = App.getResourcePath(collectionReqConfig.getFileLocation());
		File dirPath = new File(localFolderPath);

		if (!dirPath.isDirectory()) {
			logger.debug("Invalid folder directory path, so returning.");
			return;
		}

		// Fetch the list of files from configured folder
		File filesList[] = dirPath.listFiles();

		if (filesList == null || filesList.length == 0) {
			// no files
			logger.debug("No files found in the folder, so returning.");
			return;
		}

		// Process each file individually
		for (File file : filesList) {

			boolean isFileFound = extCollectionReceiptDao.isFileFound(file.getName());// Bean to get two results
			if (isFileFound) {
				// Check if the file is already processed in table
				// boolean isFileProcessed = extCollectionReceiptDao.isFileProcessed(fileName, COMPLETED);
				//
				// if (isFileProcessed) {
				// logger.debug("EXT_FILE: File already processed, so returning.");
				// continue;
				// }
				continue;
			}

			if (file.getName().contains(".inproc")) {
				continue;
			}

			CollReceiptHeader extCollectionHeader = new CollReceiptHeader();
			extCollectionHeader.setRequestFileName(file.getName());
			extCollectionHeader.setRequestFileLocation(collectionReqConfig.getFileLocation());
			extCollectionHeader.setStatus(UNPROCESSED);
			extCollectionHeader.setExtraction(UNPROCESSED);
			extCollectionHeader.setWriteResponse(UNPROCESSED);
			extCollectionHeader.setRespFileStatus(UNPROCESSED);
			extCollectionHeader.setErrorCode("");
			extCollectionHeader.setErrorMessage("");
			extCollectionReceiptDao.saveFile(extCollectionHeader);
		}
		logger.debug(Literal.LEAVING);
	}

}
