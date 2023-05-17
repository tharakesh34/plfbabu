package com.pennanttech.external.silien.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import com.pennanttech.external.app.config.dao.ExtGenericDao;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.silien.dao.ExtLienMarkingDAO;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class LienFileReadingJob extends AbstractJob implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(LienFileReadingJob.class);

	private ExtLienMarkingDAO externalLienMarkingDAO;
	private FileInterfaceConfig lienConfig;
	private FileInterfaceConfig lienReqConfig;
	private ExtGenericDao extGenericDao;
	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		{
			// Get all the required DAO's
			applicationContext = ApplicationContextProvider.getApplicationContext();
			externalLienMarkingDAO = applicationContext.getBean(ExtLienMarkingDAO.class);
			extGenericDao = applicationContext.getBean(ExtGenericDao.class);

		}

		processSILienMarkingResponse();
		logger.debug(Literal.LEAVING);
	}

	public void processSILienMarkingResponse() {
		logger.debug(Literal.ENTERING);

		List<FileInterfaceConfig> mainConfig = extGenericDao.getExternalConfig();

		lienConfig = getDataFromList(mainConfig, CONFIG_LIEN_RESP);
		lienReqConfig = getDataFromList(mainConfig, CONFIG_LIEN_REQ);

		// Check if SILIEN config is configured
		if (lienConfig == null) {
			logger.debug(
					"Ext_Warning: No configuration found for type LIEN. So returning without processing response file.");
			return;
		}

		try {
			fetchAllFilesAndSave();

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void fetchAllFilesAndSave() {
		logger.debug(Literal.ENTERING);
		// Get response folder configured
		String folderPath = App.getResourcePath(lienConfig.getFileLocation());

		if (folderPath == null || "".equals(folderPath)) {
			logger.debug("Invalid folder path, so returning.");
			return;
		}

		File dirPath = new File(folderPath);

		if (!dirPath.isDirectory()) {
			logger.debug("Invalid folder directory path, so returning.");
			return;
		}

		File filesList[] = dirPath.listFiles();
		if (filesList == null || filesList.length == 0) {
			// no files
			logger.debug("No files found in the folder, so returning.");
			return;
		}

		// load request folder file names as list of names
		List<String> requestFileNames = fetchRequestFiles(lienReqConfig);

		for (File file : filesList) {
			String fileName = file.getName();
			String filePrepend = lienConfig.getFilePrepend();
			String fileExtension = lienConfig.getFileExtension();

			// Verify if the file is already processed or not
			if (!externalLienMarkingDAO.isFileProcessed(file.getName())) {
				// Insert new file into table as unprocessed

				if (!(fileName.startsWith(filePrepend.concat(lienConfig.getSuccessIndicator()))
						|| fileName.startsWith(filePrepend.concat(lienConfig.getFailIndicator())))
						|| !fileName.endsWith(fileExtension)) {
					logger.debug(
							"Error Code F507, Invalid SI LIEN response file. So not processing the response file.");
					continue;
				}

				String[] fNameArray = StringUtils.split(fileName, ",");

				if (fNameArray == null || fNameArray.length < 2) {
					logger.debug(
							"Error Code F509,Lien Request file name is invalid. So not processing the response file.");
					continue;
				}

				String reqFileNameInRespFile = getReqFileNameFromRespFileName(fNameArray, fileExtension);
				boolean isValid = requestFileNames.contains(reqFileNameInRespFile);

				if (!isValid) {
					logger.debug(
							"Error Code F510,Lien Request file name not found in response file. So not processing the response file.");
					continue;
				}
				externalLienMarkingDAO.insertSILienResponseFileStatus(file.getName(), UNPROCESSED);

			}
		}

		logger.debug(Literal.LEAVING);
	}

	private List<String> fetchRequestFiles(FileInterfaceConfig reqConfig) {
		List<String> requestFileNames = new ArrayList<String>();
		String reqFolderPath = App.getResourcePath(reqConfig.getFileLocation());
		if (reqFolderPath != null && !"".equals(reqFolderPath)) {
			File reqDirPath = new File(reqFolderPath);
			if (reqDirPath.isDirectory()) {
				// Fetch the list of request files from configured folder
				File filesList[] = reqDirPath.listFiles();
				if (filesList != null && filesList.length > 0) {
					for (File file : filesList) {
						requestFileNames.add(file.getName());
					}
				}
			}
		}
		return requestFileNames;
	}

	private String getReqFileNameFromRespFileName(String[] fNameArray, String fileExtension) {
		logger.debug(Literal.ENTERING);
		String reqFileNameInRespFile = "";

		if (fNameArray[1].contains(fileExtension)) {
			reqFileNameInRespFile = fNameArray[1].substring(0, fNameArray[1].indexOf(fileExtension));
		} else {
			reqFileNameInRespFile = fNameArray[1];
		}
		logger.debug(Literal.LEAVING);
		return reqFileNameInRespFile;
	}
}
