package com.pennanttech.external.presentment.job;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.config.ExtErrorCodes;
import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.config.InterfaceErrorCode;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtInterfaceDao;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.external.presentment.model.ExtPresentment;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtPresentmentFolderReaderJob extends AbstractJob implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(ExtPresentmentFolderReaderJob.class);

	private ExtPresentmentDAO externalPresentmentDAO;
	private ExtInterfaceDao extInterfaceDao;

	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		try {

			applicationContext = ApplicationContextProvider.getApplicationContext();
			externalPresentmentDAO = applicationContext.getBean(ExtPresentmentDAO.class);
			extInterfaceDao = applicationContext.getBean(ExtInterfaceDao.class);
			readAndSaveFiles();

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

	}

	public void readAndSaveFiles() {
		logger.debug(Literal.ENTERING);

		// Fetch Interface configuration
		List<ExternalConfig> listConfig = extInterfaceDao.getExternalConfig();

		processSIReposne(listConfig);
		processIPDCReposne(listConfig);
		processNACHReposne(listConfig);

		logger.debug(Literal.LEAVING);
	}

	private void processSIReposne(List<ExternalConfig> listConfig) {
		// For all type of interfaces configured, process the response files from the configured folder
		ExternalConfig externalRespConfig = getDataFromList(listConfig, CONFIG_SI_RESP);
		ExternalConfig externalReqConfig = getDataFromList(listConfig, CONFIG_SI_REQ);

		if (externalRespConfig != null && externalReqConfig != null) {
			processResponseFiles(externalRespConfig, externalReqConfig);
			externalRespConfig = null;
			externalReqConfig = null;
		}
	}

	private void processIPDCReposne(List<ExternalConfig> listConfig) {
		// For all type of interfaces configured, process the response files from the configured folder
		ExternalConfig externalRespConfig = getDataFromList(listConfig, CONFIG_IPDC_RESP);
		ExternalConfig externalReqConfig = getDataFromList(listConfig, CONFIG_IPDC_REQ);

		if (externalRespConfig != null && externalReqConfig != null) {
			processResponseFiles(externalRespConfig, externalReqConfig);
			externalRespConfig = null;
			externalReqConfig = null;
		}
	}

	private void processNACHReposne(List<ExternalConfig> listConfig) {
		// For all type of interfaces configured, process the response files from the configured folder
		ExternalConfig externalRespConfig = getDataFromList(listConfig, CONFIG_NACH_RESP);
		ExternalConfig externalReqConfig = getDataFromList(listConfig, CONFIG_NACH_REQ);

		if (externalRespConfig != null && externalReqConfig != null) {
			processResponseFiles(externalRespConfig, externalReqConfig);
			externalRespConfig = null;
			externalReqConfig = null;
		}
	}

	private void processResponseFiles(ExternalConfig respConfig, ExternalConfig reqConfig) {
		logger.debug(Literal.ENTERING);

		// get error codes handy
		if (ExtErrorCodes.getInstance().getInterfaceErrorsList().isEmpty()) {
			List<InterfaceErrorCode> interfaceErrorsList = extInterfaceDao.fetchInterfaceErrorCodes();
			ExtErrorCodes.getInstance().setInterfaceErrorsList(interfaceErrorsList);
		}

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

				boolean isValid = false;

				if (CONFIG_IPDC_REQ.equals(reqConfig.getInterfaceName())
						|| CONFIG_SI_REQ.equals(reqConfig.getInterfaceName())) {

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
							reqConfig.getInterfaceName(), fileType);

					isValid = requestFileNames.contains(reqFileNameInRespFile);

					// Added for reject file count validation
					if ("R".equals(fileType)) {
						String fileData = App.getResourcePath(extPresentment.getFileLocation()) + File.separator
								+ extPresentment.getFileName();

						boolean isValidRejectFile = validateRejectFile(fileData);

						if (!isValidRejectFile) {
							InterfaceErrorCode interfaceErrorCode = getErrorFromList(
									ExtErrorCodes.getInstance().getInterfaceErrorsList(), F606);

							extPresentment.setStatus(UNPROCESSED); // set file record process as unprocessed
							extPresentment.setExtraction(FAILED); // set file extract process unprocessed
							extPresentment.setErrorCode(interfaceErrorCode.getErrorCode());
							extPresentment.setErrorMessage(interfaceErrorCode.getErrorMessage());
							externalPresentmentDAO.saveRejectResponseFile(extPresentment);
							continue;
						}

					}

				} else if (CONFIG_NACH_REQ.equals(reqConfig.getInterfaceName())) {
					isValid = true;
				}

				if (!isValid) {
					logger.debug("Error Code F401, Request file name is invalid. So not processing the response file.");
					continue;
				}

				// Add unprocessed files in to table
				extPresentment.setStatus(UNPROCESSED); // set file record process as unprocessed
				extPresentment.setExtraction(UNPROCESSED); // set file extract process unprocessed
				externalPresentmentDAO.saveResponseFile(extPresentment);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private String getReqFileNameFromRespFileName(String[] fNameArray, String fileExtension, String interfaceName,
			String fileType) {
		logger.debug(Literal.ENTERING);
		String reqFileNameInRespFile = "";

		if (CONFIG_SI_REQ.equals(interfaceName) && "R".equals(fileType)) {

			if (fNameArray.length >= 3) {
				if (fNameArray[2].contains(fileExtension)) {
					reqFileNameInRespFile = fNameArray[1] + "_"
							+ fNameArray[2].substring(0, fNameArray[2].indexOf(fileExtension));
				} else {
					reqFileNameInRespFile = fNameArray[1] + "_" + fNameArray[2];
				}
			}
		} else if (CONFIG_SI_REQ.equals(interfaceName) && "S".equals(fileType)
				|| CONFIG_IPDC_REQ.equals(interfaceName) && "S".equals(fileType)
				|| CONFIG_IPDC_REQ.equals(interfaceName) && "R".equals(fileType)) {
			if (fNameArray[1].contains(fileExtension)) {
				reqFileNameInRespFile = fNameArray[1].substring(0, fNameArray[1].indexOf(fileExtension));
			} else {
				reqFileNameInRespFile = fNameArray[1];
			}
		}
		logger.debug(Literal.LEAVING);
		return reqFileNameInRespFile;
	}

	private List<String> fetchRequestFiles(ExternalConfig reqConfig) {
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

	private boolean validateRejectFile(String filePath) {
		logger.debug(Literal.ENTERING);
		try {

			long fileLines = countLineInFile(filePath);
			logger.debug("EXT_VR: fileLines:" + fileLines);
			long recSize = fileLines - 2;
			logger.debug("EXT_VR: recSize" + recSize);
			File file = new File(filePath);
			logger.debug("EXT_VR: file" + file.getName());
			String data = readLastLine(file);
			logger.debug("EXT_VR: Data" + data);
			if (data != null && !"".equals(data)) {
				logger.debug("EXT_VR: IF");
				if (data.startsWith("F") && data.length() > 1) {
					logger.debug("EXT_VR: IFF");
					int fileRecSize = 0;
					fileRecSize = Integer.parseInt(data.substring(1));
					logger.debug("EXT_VR: IFF fileRecSize:" + fileRecSize);
					if (fileRecSize == recSize) {
						logger.debug("EXT_VR: IFF IF TRUE");
						return true;
					}
				} else {
					logger.debug("EXT_VR: IFE");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	private long countLineInFile(String fileName) {
		logger.debug(Literal.ENTERING);
		long lines = 0;
		try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean endsWithoutNewLine = false;
			while ((readChars = is.read(c)) != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n')
						++count;
				}
				endsWithoutNewLine = (c[readChars - 1] != '\n');
			}
			if (endsWithoutNewLine) {
				++count;
			}
			lines = count;
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
		return lines;
	}

	public String readLastLine(File file) throws Exception {
		logger.debug(Literal.ENTERING);
		String last = null, line;
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			while ((line = in.readLine()) != null) {
				if (line != null) {
					last = line;
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		} finally {
		}
		logger.debug(Literal.LEAVING);
		return last;
	}
}
