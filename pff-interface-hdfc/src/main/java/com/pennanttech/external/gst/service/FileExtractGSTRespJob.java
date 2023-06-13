package com.pennanttech.external.gst.service;

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
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.gst.dao.ExtGSTDao;
import com.pennanttech.external.gst.model.GSTCompDetail;
import com.pennanttech.external.gst.model.GSTCompHeader;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class FileExtractGSTRespJob extends AbstractJob implements InterfaceConstants, ErrorCodesConstants {

	private static final Logger logger = LogManager.getLogger(FileExtractGSTRespJob.class);
	private static final String GST_COMP_RESPONSE_START = "G";
	private static final String FETCH_QUERY = "Select * from GSTHEADER  Where STATUS=? AND EXTRACTION=?";

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
		DataSource dataSource = applicationContext.getBean("extDataSource", DataSource.class);
		ExtGSTDao extGSTDao = applicationContext.getBean(ExtGSTDao.class);

		// Fetch 10 files using extraction status = 0
		JdbcCursorItemReader<GSTCompHeader> cursorItemReader = new JdbcCursorItemReader<GSTCompHeader>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setFetchSize(1);
		cursorItemReader.setSql(FETCH_QUERY);
		cursorItemReader.setRowMapper(new RowMapper<GSTCompHeader>() {
			@Override
			public GSTCompHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
				GSTCompHeader compHeader = new GSTCompHeader();
				compHeader.setId(rs.getLong("ID"));
				compHeader.setStatus(rs.getInt("STATUS"));
				compHeader.setExtraction(rs.getInt("EXTRACTION"));
				compHeader.setFileName(rs.getString("FILE_NAME"));
				compHeader.setFileLocation(rs.getString("FILE_LOCATION"));
				compHeader.setCreatedDate(rs.getDate("CREATED_DATE"));
				return compHeader;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, UNPROCESSED);
				ps.setLong(2, UNPROCESSED);
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		GSTCompHeader header;
		try {
			while ((header = cursorItemReader.read()) != null) {

				// update the extract state as processing
				header.setStatus(INPROCESS);
				extGSTDao.updateFileStatus(header);

				String filePath = App.getResourcePath(header.getFileLocation()) + File.separator + header.getFileName();

				File file = new File(filePath);

				List<GSTCompDetail> detailList = new ArrayList<>();
				try (Scanner sc = new Scanner(file)) {
					// Read file line by line
					while (sc.hasNextLine()) {
						String lineData = sc.nextLine();

						if (!lineData.trim().startsWith(GST_COMP_RESPONSE_START)) {
							continue;
						}

						GSTCompDetail detail = new GSTCompDetail();
						detail.setHeaderId(header.getId());
						detail.setRecord(lineData);
						detail.setStatus(UNPROCESSED);
						detailList.add(detail);

						if (detailList.size() == BULK_RECORD_COUNT) {
							// save bulk records at a time..
							extGSTDao.saveExtGSTCompRecordsData(detailList);
							detailList.clear();
						}
					}
					if (!detailList.isEmpty()) {
						// save records remaining after bulk insert
						extGSTDao.saveExtGSTCompRecordsData(detailList);
						detailList.clear();
					}
					// update the file extraction as completed
					header.setStatus(COMPLETED);
					header.setExtraction(COMPLETED);
					extGSTDao.updateFileStatus(header);
				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					// update the file extraction as completed
					header.setStatus(EXCEPTION);
					header.setExtraction(FAILED);
					header.setErrorCode(GS1002);
					header.setErrorMessage(e.getMessage());
					extGSTDao.updateFileStatus(header);
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

}
