package com.pennanttech.external.collectionreceipt.service;

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

import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.FileTransferUtil;
import com.pennanttech.external.app.util.InterfaceErrorCodeUtil;
import com.pennanttech.external.collectionreceipt.dao.ExtCollectionReceiptDao;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class FetchFileCollectionReqJob extends AbstractJob
		implements InterfaceConstants, ExtIntfConfigConstants, ErrorCodesConstants {

	private static final Logger logger = LogManager.getLogger(FetchFileCollectionReqJob.class);

	private ExtCollectionReceiptDao extCollectionReceiptDao;
	private ApplicationContext applicationContext;

	private FileInterfaceConfig collectionReqConfig;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		// Get all the required DAO's
		applicationContext = ApplicationContextProvider.getApplicationContext();
		extCollectionReceiptDao = applicationContext.getBean("extCollectionReceiptDao", ExtCollectionReceiptDao.class);

		fetchRemoteFiles();

		logger.debug(Literal.LEAVING);
	}

	private void fetchRemoteFiles() {
		logger.debug(Literal.ENTERING);

		// Fetch Collection Receipt Request config from main configuration
		collectionReqConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_COLLECTION_REQ_CONF);

		if (collectionReqConfig == null) {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(CR1000));
			return;
		}

		String baseFilePath = collectionReqConfig.getFileLocation();

		if ("".equals(StringUtils.stripToEmpty(baseFilePath))) {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(CR1001));
			return;
		}

		FileTransferUtil fileTransferUtil = new FileTransferUtil(collectionReqConfig);

		String remoteFilePath = collectionReqConfig.getFileTransferConfig().getSftpLocation();

		if ("".equals(StringUtils.stripToEmpty(remoteFilePath))) {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(CR1002));
			return;
		}

		// Get list of files in SFTP.
		List<String> fileNames = fileTransferUtil.fetchFileNamesListFromSFTP();

		String localFolderPath = App.getResourcePath(baseFilePath);

		List<String> filteredFileNames = new ArrayList<>();
		for (String fileName : fileNames) {

			if (fileName.contains(".inproc")) {
				logger.debug(InterfaceErrorCodeUtil.getErrorMessage(CR1003));
				continue;
			}

			try {
				// Download file from remote location
				fileTransferUtil.downloadFromSFTP(fileName, localFolderPath);

				// Changing file to inproc in local folder
				String procName = fileName.substring(0, fileName.indexOf(collectionReqConfig.getFileExtension()));
				// Change Extension to .inproc
				procName = procName.concat(".inproc");

				Path srcPath = Paths.get(localFolderPath + File.separator + fileName);
				Path destPath = Paths.get(localFolderPath + File.separator + procName);
				Files.copy(srcPath, destPath, StandardCopyOption.COPY_ATTRIBUTES);

				// Push .inproc file to SFTP
				fileTransferUtil.uploadToSFTP(localFolderPath, procName);

				// Checksum for file

				// Delete original file in SFTP
				fileTransferUtil.deleteFileFromSFTP(fileName);
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
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(CR1004));
			return;
		}

		// Fetch the list of files from configured folder
		File filesList[] = dirPath.listFiles();

		if (filesList == null || filesList.length == 0) {
			// no files
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(CR1005));
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
