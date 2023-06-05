package com.pennanttech.external.ucic.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ExtSFTPUtil;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.InterfaceErrorCodeUtil;
import com.pennanttech.external.ucic.dao.ExtUcicDao;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicResponseFileReader implements InterfaceConstants, ErrorCodesConstants, ExtIntfConfigConstants {

	private static final Logger logger = LogManager.getLogger(ExtUcicResponseFileReader.class);

	private ExtUcicDao extUcicDao;

	private static final int UCIC_RESP_NEGLECT_LINES = 2;
	private static final String UCIC_RESPONSE_END = "EOF";

	public void readFolderForFiles() {
		logger.debug(Literal.ENTERING);

		// Get Response file and complete file configuration
		FileInterfaceConfig ucicRespConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_UCIC_RESP);
		FileInterfaceConfig ucicRespCompleteConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_UCIC_RESP_COMPLETE);

		if (ucicRespConfig == null || ucicRespCompleteConfig == null) {
			logger.debug(
					"EXT_UCIC: No configuration found for type UCIC response. So returning without reading the folder.");
			return;
		}

		String localFolderPath = App.getResourcePath(ucicRespConfig.getFileLocation());

		if (localFolderPath == null || "".equals(localFolderPath)) {
			logger.debug("EXT_UCIC:Invalid UCIC resp folder path, so returning.");
			return;
		}

		// Check if file is in SFTP location, then get the file.
		if ("Y".equals(StringUtils.stripToEmpty(ucicRespConfig.getIsSftp()))) {

			String remoteFilePath = ucicRespConfig.getFileSftpLocation();
			// Get list of files in SFTP.
			ExtSFTPUtil extSFTPUtil = new ExtSFTPUtil(ucicRespConfig);
			List<String> fileNames = extSFTPUtil.getFileListFromSFTP(remoteFilePath);

			for (String fileName : fileNames) {
				FtpClient ftpClient = extSFTPUtil.getSFTPConnection();
				ftpClient.download(remoteFilePath, localFolderPath, fileName);
			}
		}

		File dirPath = new File(localFolderPath);

		if (!dirPath.isDirectory()) {
			logger.debug("Invalid  UCIC resp folder directory path, so returning.");
			return;
		}

		// Fetch the list of files from configured folder
		File filesList[] = dirPath.listFiles();

		if (filesList == null || filesList.length == 0) {
			// no files
			logger.debug("No files found in the folder, so returning.");
			return;
		}

		// load response folder file names as list of names
		List<String> respFileNames = fetchRespFiles(ucicRespConfig);
		List<String> completeFilesList = new ArrayList<String>();
		// Process each file individually
		for (File file : filesList) {
			String fileName = file.getName();
			if (fileName.startsWith(ucicRespCompleteConfig.getFilePrepend())) {
				int prependLength = StringUtils.stripToEmpty(ucicRespCompleteConfig.getFilePrepend()).length();
				String lastStr = fileName.substring(prependLength,
						fileName.indexOf(ucicRespCompleteConfig.getFileExtension()));
				completeFilesList.add(lastStr);
			}
		}

		if (completeFilesList.isEmpty()) {
			logger.debug("No complete files found in the folder, so returning.");
			return;
		}

		for (String completeFileDate : completeFilesList) {
			String respFileName = ucicRespConfig.getFilePrepend().concat(completeFileDate)
					.concat(ucicRespConfig.getFileExtension());
			if (respFileNames.contains(respFileName)) {
				// Check if the file is already saved in table
				boolean isRecordExist = extUcicDao.isFileProcessed(respFileName);
				if (!isRecordExist) {

					// Get response file
					String respFolderPath = App.getResourcePath(ucicRespConfig.getFileLocation());

					File responseFile = new File(respFolderPath + File.separator + respFileName);

					// get all the records from file in a list of objects
					List<String> fileRecordsList = prepareDataFromFile(responseFile);

					// Validating file Header and Footer. If not valid, mark file as 'File format mismatch'
					boolean isValidFile = validateFile(responseFile, fileRecordsList.size());

					// Add unprocessed files in to table
					if (isValidFile) {
						extUcicDao.saveResponseFile(respFileName, ucicRespConfig.getFileLocation(), UNPROCESSED, "",
								"");
					} else {
						// Add Failed file in to table with error code and error message
						extUcicDao.saveResponseFile(respFileName, ucicRespConfig.getFileLocation(), FAILED, F607,
								InterfaceErrorCodeUtil.getErrorMessage(F607));
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private List<String> prepareDataFromFile(File file) {
		logger.debug(Literal.ENTERING);
		int cnt = 0;
		List<String> dataList = new ArrayList<String>();
		try (Scanner sc = new Scanner(file)) {

			while (sc.hasNextLine()) {

				/* ExtUcicData data = null; */
				String lineData = sc.nextLine();

				if (cnt == UCIC_RESP_NEGLECT_LINES) {// Consider record after 2 lines.

					if (lineData.contains(UCIC_RESPONSE_END)) { // End of the line for response data.
						return dataList;
					} else {
						dataList.add(lineData);
					}

				} else {
					cnt = cnt + 1;
				}
			}

			return dataList;
		} catch (Exception e) {
			logger.debug("Exception caught {}" + e);
		}
		logger.debug(Literal.LEAVING);
		return dataList;
	}

	private boolean validateFile(File file, int recordsCount) {
		logger.debug(Literal.ENTERING);
		int cnt = 0;
		boolean returnVal1 = false;
		boolean returnVal2 = false;
		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String lineData = sc.nextLine();

				if (cnt < UCIC_RESP_NEGLECT_LINES) {
					if (cnt == 0) {
						if (lineData.startsWith("HDR")) {
							String[] strs = lineData.split("\\|");
							if (strs != null && strs.length >= 3) {
								if (strs[1] != null && !"".equals(strs[1])) {
									long totalRecordsMentionedInFile = Long.parseLong(strs[1]);
									if (totalRecordsMentionedInFile == recordsCount) {
										returnVal1 = true;
									}
								}
							}
						}
					}
					cnt = cnt + 1;
				} else {
					if (lineData.contains(UCIC_RESPONSE_END)) {
						returnVal2 = true;
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Exception caught {}" + e);
		}
		logger.debug(Literal.LEAVING);
		return returnVal1 && returnVal2;
	}

	private List<String> fetchRespFiles(FileInterfaceConfig reqConfig) {
		List<String> respFileNames = new ArrayList<String>();
		String reqFolderPath = App.getResourcePath(reqConfig.getFileLocation());
		if (reqFolderPath != null && !"".equals(reqFolderPath)) {
			File reqDirPath = new File(reqFolderPath);
			if (reqDirPath.isDirectory()) {
				// Fetch the list of request files from configured folder
				File filesList[] = reqDirPath.listFiles();
				if (filesList != null && filesList.length > 0) {
					for (File file : filesList) {
						respFileNames.add(file.getName());
					}
				}
			}
		}
		return respFileNames;
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}
}
