package com.pennanttech.external.ucic.service;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtInterfaceDao;
import com.pennanttech.external.ucic.dao.ExtUcicDao;
import com.pennanttech.external.ucic.model.ExtUcicFile;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.ftp.SftpClient;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicResponseFileProcessor implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(ExtUcicResponseFileProcessor.class);

	private static final String FETCH_QUERY = "Select * from UCIC_RESP_FILES  Where STATUS = ? AND EXTRACTION = ?";

	private ApplicationContext applicationContext;
	private ExtUcicDao extUcicDao;
	private DataSource dataSource;
	private ExtInterfaceDao extInterfaceDao;

	public void readFileAndExtracData() throws Exception {
		logger.debug(Literal.ENTERING);

		applicationContext = ApplicationContextProvider.getApplicationContext();
		dataSource = applicationContext.getBean("dataSource", DataSource.class);

		// Get main configuration for External Interfaces
		List<ExternalConfig> mainConfig = extInterfaceDao.getExternalConfig();
		// Get Response file and complete file configuration
		ExternalConfig ucicDBServerConfig = getDataFromList(mainConfig, CONFIG_PLF_DB_SERVER);

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

				// Connect to SFTP..
				FtpClient ftpClient = null;
				String host = ucicDBServerConfig.getHostName();
				int port = ucicDBServerConfig.getPort();
				String accessKey = ucicDBServerConfig.getAccessKey();
				String secretKey = ucicDBServerConfig.getSecretKey();
				try {
					ftpClient = new SftpClient(host, port, accessKey, secretKey);
				} catch (Exception e) {
					e.printStackTrace();
					logger.debug("Unable to connect to SFTP.");
				}

				// Get configured remote path to save file to DB server location
				String remoteFilePath = ucicDBServerConfig.getFileSftpLocation();

				// Upload the response file to DB Server to read by ORACLE Database
				ftpClient.upload(file, remoteFilePath);

				// Now Run SP here to read file by ORACLE
				String stat = extUcicDao.executeUcicResponseFileSP(file.getName());

				// check if list is null
				if ("SUCCESS".equals(stat)) {
					// mark file extraction and processing status as completed
					extUcicDao.updateResponseFileExtractionFlag(ucicFile.getId(), COMPLETED, COMPLETED);
					continue;
				} else {
					// mark file extraction and processing status as completed
					extUcicDao.updateResponseFileExtractionFlag(ucicFile.getId(), FAILED, FAILED);
					continue;
				}

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		cursorItemReader.close();
		logger.debug(Literal.LEAVING);
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

	public void setExtInterfaceDao(ExtInterfaceDao extInterfaceDao) {
		this.extInterfaceDao = extInterfaceDao;
	}

}
