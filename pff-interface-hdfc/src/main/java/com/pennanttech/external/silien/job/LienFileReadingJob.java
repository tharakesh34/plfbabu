package com.pennanttech.external.silien.job;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.config.ExtErrorCodes;
import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.config.InterfaceErrorCode;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtInterfaceDao;
import com.pennanttech.external.silien.dao.ExtLienMarkingDAO;
import com.pennanttech.external.silien.model.LienFileStatus;
import com.pennanttech.external.silien.model.LienMarkDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class LienFileReadingJob extends AbstractJob implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(LienFileReadingJob.class);

	private static final String FETCH_QUERY = "Select * from SILIEN_FILE_STATUS  Where STATUS = ?";

	private static final int SILIEN_RESP_NEGLECT_LINES = 13;
	private static final String RESPONSE_END = "--------------";
	private static final String CONTROL_SPACES = "      ";
	private static final String RESP_SUCCESS = "SUCCESS";
	private static final String RESP_REJECTED = "REJECTED";

	private ExtLienMarkingDAO externalLienMarkingDAO;
	private ExternalConfig lienConfig;
	private ExternalConfig lienReqConfig;

	private DataSource dataSource;
	private ExtInterfaceDao extInterfaceDao;
	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		{
			// Get all the required DAO's
			applicationContext = ApplicationContextProvider.getApplicationContext();
			externalLienMarkingDAO = applicationContext.getBean(ExtLienMarkingDAO.class);
			extInterfaceDao = applicationContext.getBean(ExtInterfaceDao.class);
			dataSource = applicationContext.getBean("dataSource", DataSource.class);

		}

		processSILienMarkingResponse();
		logger.debug(Literal.LEAVING);
	}

	public void processSILienMarkingResponse() {
		logger.debug(Literal.ENTERING);

		// get error codes handy
		if (ExtErrorCodes.getInstance().getInterfaceErrorsList().isEmpty()) {
			List<InterfaceErrorCode> interfaceErrorsList = extInterfaceDao.fetchInterfaceErrorCodes();
			ExtErrorCodes.getInstance().setInterfaceErrorsList(interfaceErrorsList);
		}

		List<ExternalConfig> mainConfig = extInterfaceDao.getExternalConfig();

		lienConfig = getDataFromList(mainConfig, CONFIG_LIEN_RESP);
		lienReqConfig = getDataFromList(mainConfig, CONFIG_LIEN_REQ);

		// Check if SILIEN config is configured
		if (lienConfig == null) {
			logger.debug(
					"Ext_Warning: No configuration found for type LIEN. So returning without processing response file.");
			return;
		}

		try {
			// Fetch all files from Configured folder and save names in a table
			fetchAllFilesAndSave();
			// Process each file saved in the table
			processSavedFiles();

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

	private void processSavedFiles() throws Exception {
		logger.debug(Literal.ENTERING);

		// Read 10 files at a time using file status = 0
		JdbcCursorItemReader<LienFileStatus> cursorItemReader = new JdbcCursorItemReader<LienFileStatus>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setFetchSize(10);
		cursorItemReader.setSql(FETCH_QUERY);
		cursorItemReader.setRowMapper(new RowMapper<LienFileStatus>() {
			@Override
			public LienFileStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
				LienFileStatus extPresentment = new LienFileStatus();
				extPresentment.setId(rs.getLong("ID"));
				extPresentment.setFileName(rs.getString("FILE_NAME"));
				extPresentment.setStatus(rs.getInt("STATUS"));
				return extPresentment;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, UNPROCESSED);// STATUS = UnProcessed-0
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		String folderPath = App.getResourcePath(lienConfig.getFileLocation());

		LienFileStatus lienFileStatus;

		while ((lienFileStatus = cursorItemReader.read()) != null) {
			try {
				File file = new File(folderPath + File.separator + lienFileStatus.getFileName());

				// Mark file processing status as INPROCESS
				externalLienMarkingDAO.updateLienResponseFileStatus(lienFileStatus.getId(), INPROCESS, "", "");

				// Verify if file is valid or not
				boolean isValidFile = validateFile(file);

				// Validate file
				if (isValidFile) {

					// get all the records from file in a list of objects
					List<LienMarkDetail> lienDataList = getFileRecords(file);

					// check if list is null
					if (lienDataList == null) {
						// Invalid file
						InterfaceErrorCode interfaceErrorCode = getErrorFromList(
								ExtErrorCodes.getInstance().getInterfaceErrorsList(), F603);
						externalLienMarkingDAO.updateLienResponseFileStatus(lienFileStatus.getId(), COMPLETED,
								interfaceErrorCode.getErrorCode(), interfaceErrorCode.getErrorMessage());
						continue;
					}

					// Process each record and save to PLF
					for (LienMarkDetail lienMarkDetail : lienDataList) {

						// Validate Account number in the record
						if (lienMarkDetail.getAccNumber() == null || "".equals(lienMarkDetail.getAccNumber())) {
							InterfaceErrorCode interfaceErrorCode = getErrorFromList(
									ExtErrorCodes.getInstance().getInterfaceErrorsList(), F600);
							externalLienMarkingDAO.updateLienRecordStatus(lienMarkDetail.getAccNumber(), FILE_WRITTEN,
									interfaceErrorCode.getErrorCode(), interfaceErrorCode.getErrorMessage());
							continue;
						}

						// Validate Lien mark in the record
						if (lienMarkDetail.getLienMark() == null || "".equals(lienMarkDetail.getLienMark())) {
							InterfaceErrorCode interfaceErrorCode = getErrorFromList(
									ExtErrorCodes.getInstance().getInterfaceErrorsList(), F601);
							externalLienMarkingDAO.updateLienRecordStatus(lienMarkDetail.getAccNumber(), FILE_WRITTEN,
									interfaceErrorCode.getErrorCode(), interfaceErrorCode.getErrorMessage());
							continue;
						}

						externalLienMarkingDAO.updateLienInterfaceStatus(lienMarkDetail);
					}

					// mark file processing status as completed
					externalLienMarkingDAO.updateLienResponseFileStatus(lienFileStatus.getId(), COMPLETED, "", "");

				} else {
					// mark file processing status as completed with error
					InterfaceErrorCode interfaceErrorCode = getErrorFromList(
							ExtErrorCodes.getInstance().getInterfaceErrorsList(), F602);
					externalLienMarkingDAO.updateLienResponseFileStatus(lienFileStatus.getId(), COMPLETED,
							interfaceErrorCode.getErrorCode(), interfaceErrorCode.getErrorMessage());
					continue;
				}

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				// mark file processing status as completed with exception
				InterfaceErrorCode interfaceErrorCode = getErrorFromList(
						ExtErrorCodes.getInstance().getInterfaceErrorsList(), F604);
				externalLienMarkingDAO.updateLienResponseFileStatus(lienFileStatus.getId(), COMPLETED,
						interfaceErrorCode.getErrorCode(), e.getMessage());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean validateFile(File file) {
		logger.debug(Literal.ENTERING);
		String fileName = file.getName();
		String filePrepend = lienConfig.getFilePrepend();
		String fileExtension = lienConfig.getFileExtension();
		if (fileName.startsWith(filePrepend) && fileName.endsWith(fileExtension)) {
			return true;
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	private List<LienMarkDetail> getFileRecords(File file) {
		logger.debug(Literal.ENTERING);
		String fileName = file.getName();
		String filePrepend = lienConfig.getFilePrepend();
		String fileExtension = lienConfig.getFileExtension();

		if (fileName.startsWith(filePrepend.concat(lienConfig.getSuccessIndicator()))
				&& fileName.endsWith(fileExtension)) {
			// Parse success records
			return prepareDataFromFile(file, true);
		}

		if (fileName.startsWith(filePrepend.concat(lienConfig.getFailIndicator()))
				&& fileName.endsWith(fileExtension)) {
			// Parse rejected records
			return prepareDataFromFile(file, false);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private List<LienMarkDetail> prepareDataFromFile(File file, boolean isSucessFile) {
		logger.debug(Literal.ENTERING);
		int cnt = 0;
		List<LienMarkDetail> dataList = new ArrayList<LienMarkDetail>();
		try (Scanner sc = new Scanner(file)) {

			while (sc.hasNextLine()) {

				LienMarkDetail data = null;
				String lineData = sc.nextLine();

				if (cnt == SILIEN_RESP_NEGLECT_LINES) {// Consider record after 13 lines.

					if (lineData.contains(RESPONSE_END)) { // End of the line for response data.
						return dataList;
					} else {

						if (isSucessFile) {
							data = prepareSuccessResponse(lineData);
						} else {
							data = prepareRejectResponse(lineData);
						}

						dataList.add(data);
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

	private LienMarkDetail prepareSuccessResponse(String lineData) {
		logger.debug(Literal.ENTERING);

		LienMarkDetail detail = new LienMarkDetail();

		detail.setInterfaceStatus(RESP_SUCCESS);

		String[] dataArray = lineData.toString().split(CONTROL_SPACES);

		if (dataArray.length >= 1) {
			detail.setAccNumber(dataArray[0]);
		}

		if (dataArray.length >= 2) {
			detail.setLienMark(dataArray[1]);
		}

		logger.debug(Literal.LEAVING);
		return detail;
	}

	private LienMarkDetail prepareRejectResponse(String lineData) {
		logger.debug(Literal.ENTERING);

		LienMarkDetail detail = new LienMarkDetail();

		detail.setInterfaceStatus(RESP_REJECTED);

		String[] dataArray = lineData.toString().split(CONTROL_SPACES);

		if (dataArray.length >= 1) {
			detail.setAccNumber(dataArray[0]);
		}

		if (dataArray.length >= 2) {
			detail.setLienMark(dataArray[1]);
		}

		if (dataArray.length >= 9) {
			detail.setInterfaceReason(dataArray[8]);
		}

		logger.debug(Literal.LEAVING);
		return detail;
	}

	private List<String> fetchRequestFiles(ExternalConfig reqConfig) {
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
