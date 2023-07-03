package com.pennanttech.external.gst.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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
import com.pennanttech.external.gst.dao.ExtGSTDao;
import com.pennanttech.external.gst.model.GSTCompHeader;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class FetchFileGSTRespJob extends AbstractJob
		implements InterfaceConstants, ErrorCodesConstants, ExtIntfConfigConstants {

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

		ExtGSTDao extGSTDao = applicationContext.getBean(ExtGSTDao.class);

		// Get Response file and complete file configuration
		FileInterfaceConfig respConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_GST_RESP);
		FileInterfaceConfig respDoneConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_GST_RESP_DONE);

		if (respConfig == null || respDoneConfig == null) {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(GS1004));
			return;
		}

		String localFolderPath = App.getResourcePath(respConfig.getFileLocation());

		if (localFolderPath == null || "".equals(localFolderPath)) {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(GS1005));
			return;
		}

		// Check if file is in SFTP location, then get the file.
		if ("Y".equals(StringUtils.stripToEmpty(respConfig.getFileTransfer()))) {
			FileTransferUtil fileTransferUtil = new FileTransferUtil(respConfig);
			// Get list of files in SFTP.
			List<String> fileNames = fileTransferUtil.fetchFileNamesListFromSFTP();
			for (String fileName : fileNames) {
				fileTransferUtil.downloadFromSFTP(fileName, localFolderPath);
			}
		}

		File dirPath = new File(localFolderPath);

		if (!dirPath.isDirectory()) {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(GS1006));
			return;
		}

		// Fetch the list of files from configured folder
		File[] filesList = dirPath.listFiles();

		if (filesList == null || filesList.length == 0) {
			// no files
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(GS1007));
			return;
		}

		// load response folder file names as list of names
		List<String> respFileNames = fetchRespFiles(respConfig);
		List<String> doneFilesList = new ArrayList<>();
		// Process each file individually
		for (File file : filesList) {
			String fileName = file.getName();
			if (fileName.endsWith(respDoneConfig.getFileExtension())) {
				String lastStr = fileName.substring(0, fileName.indexOf(respDoneConfig.getFileExtension()));
				doneFilesList.add(lastStr);
			}
		}

		if (doneFilesList.isEmpty()) {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(GS1008));
			return;
		}

		for (String doneFileName : doneFilesList) {

			if (respFileNames.contains(doneFileName)) {
				// Check if the file is already saved in table
				boolean isRecordExist = extGSTDao.isFileProcessed(doneFileName);
				if (!isRecordExist) {
					// Get response file
					String respFolderPath = App.getResourcePath(respConfig.getFileLocation());

					File responseFile = new File(respFolderPath + File.separator + doneFileName);

					// Validating file Header and Footer. If not valid, mark file as 'File format mismatch'
					boolean isValidFile = validateFile(responseFile);

					GSTCompHeader header = new GSTCompHeader();

					header.setFileName(doneFileName);
					header.setFileLocation(respConfig.getFileLocation());

					if (!isValidFile) {
						header.setStatus(FAILED);
						header.setExtraction(UNPROCESSED);
						header.setErrorCode(GS1000);
						header.setErrorMessage(InterfaceErrorCodeUtil.getErrorMessage(GS1000));
						extGSTDao.saveResponseFile(header);
					}

					// Add unprocessed files in to table
					header.setStatus(UNPROCESSED);
					header.setExtraction(UNPROCESSED);
					extGSTDao.saveResponseFile(header);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean validateFile(File file) {
		logger.debug(Literal.ENTERING);
		try {
			FileData cntData = new FileData();
			readLastLine(file, cntData);
			long fileLines = cntData.getLinesCount();
			String data = cntData.getLastLine();
			long recSize = fileLines - 1;
			if (data != null && !"".equals(data) && (data.startsWith("EOF") && data.length() > 1)) {
				String[] params = Pattern.compile("\\|").split(data);
				int fileRecSize = 0;
				fileRecSize = Integer.parseInt(params[1]);
				if (fileRecSize == recSize) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	public void readLastLine(File file, FileData cntData) throws Exception {
		logger.debug(Literal.ENTERING);
		String last = null;
		String line = null;
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

	private List<String> fetchRespFiles(FileInterfaceConfig reqConfig) {
		List<String> respFileNames = new ArrayList<>();
		String reqFolderPath = App.getResourcePath(reqConfig.getFileLocation());
		if (reqFolderPath != null && !"".equals(reqFolderPath)) {
			File reqDirPath = new File(reqFolderPath);
			if (reqDirPath.isDirectory()) {
				// Fetch the list of request files from configured folder
				File[] filesList = reqDirPath.listFiles();
				if (filesList != null && filesList.length > 0) {
					for (File file : filesList) {
						respFileNames.add(file.getName());
					}
				}
			}
		}
		return respFileNames;
	}

	class FileData {
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