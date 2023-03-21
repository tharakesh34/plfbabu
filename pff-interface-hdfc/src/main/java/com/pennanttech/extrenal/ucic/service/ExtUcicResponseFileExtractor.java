package com.pennanttech.extrenal.ucic.service;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.extrenal.ucic.dao.ExtUcicDao;
import com.pennanttech.extrenal.ucic.model.ExtUcicData;
import com.pennanttech.extrenal.ucic.model.ExtUcicFile;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicResponseFileExtractor implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(ExtUcicResponseFileExtractor.class);

	private static final String FETCH_QUERY = "Select * from UCIC_RESP_FILES  Where STATUS = ? AND EXTRACTION = ?";

	private static final String UCIC_RESP_SPLITTER = ",";
	private static final int UCIC_RESP_NEGLECT_LINES = 2;
	private static final String UCIC_RESPONSE_END = "EOF";

	private ApplicationContext applicationContext;
	private ExtUcicDao extUcicDao;
	private DataSource dataSource;

	public void readFileAndExtracData() throws Exception {
		logger.debug(Literal.ENTERING);

		applicationContext = ApplicationContextProvider.getApplicationContext();
		dataSource = applicationContext.getBean("dataSource", DataSource.class);

		// Read 10 files at a time using file status = 0
		JdbcCursorItemReader<ExtUcicFile> cursorItemReader = new JdbcCursorItemReader<ExtUcicFile>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setFetchSize(10);
		cursorItemReader.setSql(FETCH_QUERY);
		cursorItemReader.setRowMapper(new RowMapper<ExtUcicFile>() {
			@Override
			public ExtUcicFile mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExtUcicFile ucicFile = new ExtUcicFile();
				ucicFile.setId(rs.getLong("ID"));
				ucicFile.setFileName(rs.getString("FILE_NAME"));
				ucicFile.setFileLocation(rs.getString("FILE_LOCATION"));
				return ucicFile;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, UNPROCESSED);// STATUS = UnProcessed-0
				ps.setLong(2, UNPROCESSED);// EXTRACTION = UnProcessed-0
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		ExtUcicFile ucicFile;

		while ((ucicFile = cursorItemReader.read()) != null) {
			try {
				String folderPath = App.getResourcePath(ucicFile.getFileLocation());
				File file = new File(folderPath + File.separator + ucicFile.getFileName());

				// Mark file processing status as INPROCESS
				extUcicDao.updateResponseFileProcessingFlag(ucicFile.getId(), INPROCESS);

				// get all the records from file in a list of objects
				List<ExtUcicData> ucicDataList = prepareDataFromFile(file, ucicFile.getId());

				// check if list is null
				if (ucicDataList == null || ucicDataList.isEmpty()) {
					// Invalid file
					extUcicDao.updateResponseFileProcessingFlag(ucicFile.getId(), COMPLETED);
					continue;
				}

				// Save records as bulk
				extUcicDao.saveResponseFileRecordsData(ucicDataList);

				// mark file extraction and processing status as completed
				extUcicDao.updateResponseFileExtractionFlag(ucicFile.getId(), COMPLETED, COMPLETED);

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		cursorItemReader.close();
		logger.debug(Literal.LEAVING);
	}

	private List<ExtUcicData> prepareDataFromFile(File file, long headerId) {
		logger.debug(Literal.ENTERING);
		int cnt = 0;
		List<ExtUcicData> dataList = new ArrayList<ExtUcicData>();
		try (Scanner sc = new Scanner(file)) {

			while (sc.hasNextLine()) {

				ExtUcicData data = null;
				String lineData = sc.nextLine();

				if (cnt == UCIC_RESP_NEGLECT_LINES) {// Consider record after 2 lines.

					if (lineData.contains(UCIC_RESPONSE_END)) { // End of the line for response data.
						return dataList;
					} else {
						data = prepareResponse(lineData);
						data.setId(headerId);
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

	private ExtUcicData prepareResponse(String lineData) {
		logger.debug(Literal.ENTERING);

		ExtUcicData detail = new ExtUcicData();

		String[] dataArray = lineData.toString().split(UCIC_RESP_SPLITTER);

		if (dataArray.length >= 1) {
			detail.setCustId(dataArray[0]);
		}

		if (dataArray.length >= 2) {
			detail.setUcicId(dataArray[1]);
		}

		logger.debug(Literal.LEAVING);
		return detail;
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

}
