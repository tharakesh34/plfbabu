package com.pennanttech.external.presentment.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.app.util.ExtSFTPUtil;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.InterfaceErrorCodeUtil;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.external.presentment.model.ExtPresentment;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.ftp.SftpClient;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class FileFetchPresentmentRespJob extends AbstractJob
		implements InterfaceConstants, ErrorCodesConstants, ExtIntfConfigConstants {
	private static final Logger logger = LogManager.getLogger(FileFetchPresentmentRespJob.class);

	private ExtPresentmentDAO externalPresentmentDAO;

	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		try {

			applicationContext = ApplicationContextProvider.getApplicationContext();
			externalPresentmentDAO = applicationContext.getBean(ExtPresentmentDAO.class);
			readAndSaveFiles();

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

	}

	public void readAndSaveFiles() {
		logger.debug(Literal.ENTERING);

		processSIReposne();
		processIPDCReposne();
		processNACHReposne();

		logger.debug(Literal.LEAVING);
	}

	private void processSIReposne() {
		// For all type of interfaces configured, process the response files from the configured folder

		FileInterfaceConfig externalRespConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_SI_RESP);
		FileInterfaceConfig externalReqConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_SI_REQ);

		if (externalRespConfig != null && externalReqConfig != null) {
			processResponseFiles(externalRespConfig, externalReqConfig);
			externalRespConfig = null;
			externalReqConfig = null;
		}
	}

	private void processIPDCReposne() {
		// For all type of interfaces configured, process the response files from the configured folder

		FileInterfaceConfig externalRespConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_IPDC_RESP);
		FileInterfaceConfig externalReqConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_IPDC_REQ);

		if (externalRespConfig != null && externalReqConfig != null) {
			processResponseFiles(externalRespConfig, externalReqConfig);
			externalRespConfig = null;
			externalReqConfig = null;
		}
	}

	private void processNACHReposne() {
		// For all type of interfaces configured, process the response files from the configured folder

		FileInterfaceConfig externalRespConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_NACH_RESP);
		FileInterfaceConfig externalReqConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_NACH_REQ);

		if ("Y".equals(StringUtils.stripToEmpty(externalRespConfig.getIsSftp()))) {
			fetchResponseFilesFromSFTP(externalRespConfig);
		}

		if (externalRespConfig != null && externalReqConfig != null) {
			processResponseFiles(externalRespConfig, externalReqConfig);
			externalRespConfig = null;
			externalReqConfig = null;
		}
	}

	private void processResponseFiles(FileInterfaceConfig respConfig, FileInterfaceConfig reqConfig) {
		logger.debug(Literal.ENTERING);

		// Get Interface/Module wise folder path
		String folderPath = App.getResourcePath(respConfig.getFileLocation());

		if (folderPath == null || "".equals(folderPath)) {
			logger.debug("Invalid folder path, so returning.");
			return;
		}

		File dirPath = new File(folderPath);

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

		// load request folder file names as list of names
		List<String> requestFileNames = fetchRequestFiles(reqConfig);

		// Process each file individually
		for (File file : filesList) {

			// Check if the file is already saved in table for the particular interface/module
			boolean isRecordExist = externalPresentmentDAO.isFileProcessed(file.getName(),
					respConfig.getInterfaceName());

			if (isRecordExist) {
				logger.debug("EXT_FILE: File status already saved, so returning.");
				continue;
			}

			String fileName = file.getName();
			String filePrepend = respConfig.getFilePrepend();
			String fileExtension = respConfig.getFileExtension();
			String fileLocation = respConfig.getFileLocation();

			ExtPresentment extPresentment = new ExtPresentment();
			extPresentment.setModule(respConfig.getInterfaceName());
			extPresentment.setFileName(fileName);
			extPresentment.setFileLocation(fileLocation);

			if (fileName.startsWith(filePrepend) && fileName.endsWith(fileExtension)) {

				// Check with request file name for SI and INTERNAl
				// Split with , and _ and check request file name in request folder

				boolean isValid = true;

				if (CONFIG_IPDC_RESP.equals(respConfig.getInterfaceName())
						|| CONFIG_SI_RESP.equals(respConfig.getInterfaceName())) {

					if ((!fileName.startsWith(filePrepend.concat(respConfig.getSuccessIndicator()))
							|| !fileName.startsWith(filePrepend.concat(respConfig.getFailIndicator())))
							&& !fileName.endsWith(fileExtension)) {
						logger.debug("Error Code F407, Invalid response file. So not processing the response file.");
						continue;
					}

					String splitConstant = "";
					String fileType = "";

					if (fileName.startsWith(filePrepend.concat(respConfig.getSuccessIndicator()))
							&& fileName.endsWith(fileExtension)) {
						fileType = "S";
						splitConstant = ",";
					} else if (fileName.startsWith(filePrepend.concat(respConfig.getFailIndicator()))
							&& fileName.endsWith(fileExtension)) {
						fileType = "R";
						splitConstant = "_";
					}

					String[] fNameArray = StringUtils.split(fileName, splitConstant);
					if (fNameArray == null || fNameArray.length < 2) {
						logger.debug(
								"Error Code F409, Request file name is invalid. So not processing the response file.");
						continue;
					}

					String reqFileNameInRespFile = getReqFileNameFromRespFileName(fNameArray, fileExtension,
							respConfig.getInterfaceName(), fileType);

					isValid = requestFileNames.contains(reqFileNameInRespFile);

					if (!isValid) {
						logger.debug(
								"Error Code F401, Request file name is invalid. So not processing the response file.");
						continue;
					}

					// Added for reject file count validation
					if ("R".equals(fileType)) {
						boolean isValidRejectFile = validateRejectFile(file);

						if (!isValidRejectFile) {
							extPresentment.setErrorCode(F606);
							extPresentment.setErrorMessage(InterfaceErrorCodeUtil.getErrorMessage(F606));
						}

					}

				}

				// Add unprocessed files in to table
				if (extPresentment.getErrorCode() != null && !"".equals(extPresentment.getErrorCode())) {
					extPresentment.setStatus(UNPROCESSED); // set file record process as unprocessed
					extPresentment.setExtraction(FAILED); // set file extract process unprocessed
				} else {
					extPresentment.setStatus(UNPROCESSED); // set file record process as unprocessed
					extPresentment.setExtraction(UNPROCESSED); // set file extract process unprocessed
				}
				externalPresentmentDAO.saveExtPresentment(extPresentment);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private String getReqFileNameFromRespFileName(String[] fNameArray, String fileExtension, String interfaceName,
			String fileType) {
		logger.debug(Literal.ENTERING);
		String reqFileNameInRespFile = "";

		if (CONFIG_SI_RESP.equals(interfaceName) && "R".equals(fileType)) {

			if (fNameArray.length >= 3) {
				if (fNameArray[2].contains(fileExtension)) {
					reqFileNameInRespFile = fNameArray[1] + "_"
							+ fNameArray[2].substring(0, fNameArray[2].indexOf(fileExtension));
				} else {
					reqFileNameInRespFile = fNameArray[1] + "_" + fNameArray[2];
				}
			}
		} else if (CONFIG_SI_RESP.equals(interfaceName) && "S".equals(fileType)
				|| CONFIG_IPDC_RESP.equals(interfaceName) && "S".equals(fileType)
				|| CONFIG_IPDC_RESP.equals(interfaceName) && "R".equals(fileType)) {
			if (fNameArray[1].contains(fileExtension)) {
				reqFileNameInRespFile = fNameArray[1].substring(0, fNameArray[1].indexOf(fileExtension));
			} else {
				reqFileNameInRespFile = fNameArray[1];
			}
		}
		logger.debug(Literal.LEAVING);
		return reqFileNameInRespFile;
	}

	private List<String> fetchRequestFiles(FileInterfaceConfig reqConfig) {
		List<String> requestFileNames = new ArrayList<String>();
		if (reqConfig != null && (CONFIG_IPDC_REQ.equals(reqConfig.getInterfaceName())
				|| CONFIG_SI_REQ.equals(reqConfig.getInterfaceName()))) {
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
		}
		return requestFileNames;
	}

	public boolean validateRejectFile(File file) {
		logger.debug(Literal.ENTERING);
		try {
			RejectFileData cntData = new RejectFileData();
			readLastLine(file, cntData);
			long fileLines = cntData.getLinesCount();
			String data = cntData.getLastLine();
			long recSize = fileLines - 2;
			if (data != null && !"".equals(data)) {
				if (data.startsWith("F") && data.length() > 1) {
					int fileRecSize = 0;
					fileRecSize = Integer.parseInt(data.substring(1));
					if (fileRecSize == recSize) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	public void readLastLine(File file, RejectFileData cntData) throws Exception {
		logger.debug(Literal.ENTERING);
		String last = null, line;
		long cnt = 0;
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			while ((line = in.readLine()) != null) {
				cnt = cnt + 1;
				if (line != null) {
					last = line;
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		cntData.setLastLine(last);
		cntData.setLinesCount(cnt);
		logger.debug(Literal.LEAVING);
	}

	private void fetchResponseFilesFromSFTP(FileInterfaceConfig externalRespConfig) {
		logger.debug(Literal.ENTERING);
		if (!"".equals(StringUtils.stripToEmpty(externalRespConfig.getFileSftpLocation()))) {

			String host = externalRespConfig.getHostName();
			int port = externalRespConfig.getPort();
			String accessKey = externalRespConfig.getAccessKey();
			String secretKey = externalRespConfig.getSecretKey();

			FtpClient ftpClient = null;
			try {
				String remoteFilePath = externalRespConfig.getFileSftpLocation();

				if (remoteFilePath == null || "".equals(remoteFilePath)) {
					logger.debug("EXT_PRMNT:Invalid PRMNT resp remote folder path, so returning.");
					return;
				}

				String localFolderPath = App.getResourcePath(externalRespConfig.getFileLocation());

				if (localFolderPath == null || "".equals(localFolderPath)) {
					logger.debug("EXT_PRMNT:Invalid PRMNT resp local folder path, so returning.");
					return;
				}

				ExtSFTPUtil extSFTPUtil = new ExtSFTPUtil(externalRespConfig);
				ftpClient = extSFTPUtil.getSFTPConnection();

				// Get list of files in SFTP.
				List<String> fileNames = extSFTPUtil.getFileListFromSFTP(remoteFilePath);

				for (String fileName : fileNames) {
					ftpClient.download(remoteFilePath, localFolderPath, fileName);
					moveToBackup(externalRespConfig, localFolderPath, fileName);
					new SftpClient(host, port, accessKey, secretKey)
							.deleteFile(remoteFilePath + File.separator + fileName);
				}

			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			} finally {
				if (ftpClient != null) {
					ftpClient.disconnect();
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void moveToBackup(FileInterfaceConfig externalRespConfig, String localFolderPath, String fileName) {
		logger.debug(Literal.ENTERING);
		if ("".equals(StringUtils.stripToEmpty(externalRespConfig.getFileBackupLocation()))) {
			logger.debug("EXT_PRMNT: No configuration found for Backup path, so returning.");
			return;
		}

		FtpClient sftpClient = null;
		try {
			ExtSFTPUtil extSFTPUtil = new ExtSFTPUtil(externalRespConfig);
			sftpClient = extSFTPUtil.getSFTPConnection();
			sftpClient.upload(new File(localFolderPath + File.separator + fileName),
					externalRespConfig.getFileBackupLocation());
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		} finally {
			if (sftpClient != null) {
				sftpClient.disconnect();
			}
		}
		logger.debug(Literal.LEAVING);
	}

	class RejectFileData {
		private long linesCount;
		private String lastLine;

		public long getLinesCount() {
			return linesCount;
		}

		public void setLinesCount(long linesCount) {
			this.linesCount = linesCount;
		}

		public String getLastLine() {
			return lastLine;
		}

		public void setLastLine(String lastLine) {
			this.lastLine = lastLine;
		}

	}
}
