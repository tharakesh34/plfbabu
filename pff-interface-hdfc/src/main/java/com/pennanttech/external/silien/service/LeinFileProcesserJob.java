package com.pennanttech.external.silien.service;

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

import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.InterfaceErrorCodeUtil;
import com.pennanttech.external.silien.dao.ExtLienMarkingDAO;
import com.pennanttech.external.silien.model.LienFileStatus;
import com.pennanttech.external.silien.model.LienMarkDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class LeinFileProcesserJob extends AbstractJob
		implements InterfaceConstants, ErrorCodesConstants, ExtIntfConfigConstants {

	private static final Logger logger = LogManager.getLogger(LienFileReadingJob.class);
	private static final String FETCH_QUERY = "Select * from SILIEN_FILE_STATUS  Where STATUS = ?";
	private static final int SILIEN_RESP_NEGLECT_LINES = 13;
	private static final String RESPONSE_END = "--------------";
	private static final String CONTROL_SPACES = "      ";
	private static final String RESP_SUCCESS = "SUCCESS";
	private static final String RESP_REJECTED = "REJECTED";

	private ExtLienMarkingDAO externalLienMarkingDAO;
	private FileInterfaceConfig lienConfig;
	private DataSource extDataSource;
	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		{
			// Get all the required DAO's
			applicationContext = ApplicationContextProvider.getApplicationContext();
			externalLienMarkingDAO = applicationContext.getBean(ExtLienMarkingDAO.class);
			extDataSource = applicationContext.getBean("extDataSource", DataSource.class);

		}

		processSavedFiles();

	}

	private void processSavedFiles() {

		logger.debug(Literal.ENTERING);

		lienConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_LIEN_RESP);

		JdbcCursorItemReader<LienFileStatus> cursorItemReader = new JdbcCursorItemReader<LienFileStatus>();
		cursorItemReader.setDataSource(extDataSource);
		cursorItemReader.setFetchSize(1);
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
		try {
			while ((lienFileStatus = cursorItemReader.read()) != null) {
				String errCode = "";
				try {
					File file = new File(folderPath + File.separator + lienFileStatus.getFileName());

					// Mark file processing status as INPROCESS
					externalLienMarkingDAO.updateLienResponseFileStatus(lienFileStatus.getId(), INPROCESS, "", "");

					// Verify if file is valid or not
					boolean isValidFile = validateFile(file);
					if (isValidFile) {

						// get all the records from file in a list of objects
						List<LienMarkDetail> lienDataList = getFileRecords(file);
						if (lienDataList == null) {
							// Invalid file
							errCode = SL1000;
							externalLienMarkingDAO.updateLienResponseFileStatus(lienFileStatus.getId(), COMPLETED,
									errCode, InterfaceErrorCodeUtil.getErrorMessage(errCode));
							continue;
						}

						// Process each record and save to PLF
						for (LienMarkDetail lienMarkDetail : lienDataList) {

							// Validate Account number in the record
							if (lienMarkDetail.getAccNumber() == null || "".equals(lienMarkDetail.getAccNumber())) {
								logger.debug("EXT_SILIEN:Account number received is empty/null, So not Processing");
								continue;
							}

							// Validate Lien mark in the record
							if ("".equals(StringUtils.stripToEmpty(lienMarkDetail.getLienMark()))) {
								logger.debug("EXT_SILIEN:Lien Status received is empty/null, So not Processing");
								continue;
							}

							externalLienMarkingDAO.updateLienRecordStatus(lienMarkDetail);
						}

						// mark file processing status as completed
						externalLienMarkingDAO.updateLienResponseFileStatus(lienFileStatus.getId(), COMPLETED, "", "");

					} else {
						// mark file processing status as completed with error
						errCode = SL1001;
						externalLienMarkingDAO.updateLienResponseFileStatus(lienFileStatus.getId(), COMPLETED, errCode,
								InterfaceErrorCodeUtil.getErrorMessage(errCode));
						continue;
					}

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					// mark file processing status as completed with exception
					errCode = SL1002;
					externalLienMarkingDAO.updateLienResponseFileStatus(lienFileStatus.getId(), FAILED, errCode,
							e.getMessage());
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);

		} finally {
			if (cursorItemReader != null) {
				cursorItemReader.close();
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

}
