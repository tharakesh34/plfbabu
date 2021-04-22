package com.pennanttech.pff.disbursement.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.disbursment.DefaultDisbursementResponse;

public class DisbAutoUploadService extends BasicDao<Object> {
	private static final Logger logger = LogManager.getLogger(DisbAutoUploadService.class);

	private DefaultDisbursementResponse defaultDisbursementResponse;
	private DataEngineConfig dataEngineConfig;
	private DataEngineStatus status = null;
	int port = 0;
	String hostName = "";
	String userName = "";
	String password = "";
	String remoteFile = "";

	public void uploadDisbursements() throws Exception {
		logger.debug(Literal.ENTERING);
		List<Configuration> configList = dataEngineConfig.getMenuList(true);
		for (Configuration configuration : configList) {
			String configName = configuration.getName();
			if (configName.startsWith("DISB_") && configName.endsWith("_IMPORT")) {
				status = dataEngineConfig.getLatestExecution(configName);
			}
		}

		long configId = dataEngineConfig.getConfigIdByName(status.getName());
		List<EventProperties> eventPropertiesList = getEventProperties(configId);
		for (EventProperties eventProperties : eventPropertiesList) {
			port = Integer.parseInt(eventProperties.getPort());
			hostName = eventProperties.getHostName();
			userName = eventProperties.getAccessKey();
			password = eventProperties.getSecretKey();
			remoteFile = eventProperties.getBucketName();
		}

		FTPClient ftpClient = new FTPClient();
		File file = null;
		try {
			ftpClient.connect(hostName, port);
			ftpClient.login(userName, password);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			file = new File("D:/test.txt");
			OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
			boolean status = ftpClient.retrieveFile(remoteFile, os);
			os.close();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		try {
			//LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			LoggedInUser loggedInUser = new LoggedInUser();
			getDefaultDisbursementResponse().processResponseFile(1000L, status, file, null, null, loggedInUser);

		} catch (Exception io) {
			logger.error(Literal.EXCEPTION, io);
		}
		logger.debug(Literal.LEAVING);
	}

	public List<EventProperties> getEventProperties(long configId) {
		RowMapper<EventProperties> rowMapper = null;
		MapSqlParameterSource parameterMap = null;
		StringBuilder sql = null;

		try {
			sql = new StringBuilder("Select * from  DATA_ENGINE_EVENT_PROPERTIES");
			sql.append(" Where config_id = :configId");

			parameterMap = new MapSqlParameterSource();
			parameterMap.addValue("configId", configId);

			rowMapper = BeanPropertyRowMapper.newInstance(EventProperties.class);

			return jdbcTemplate.query(sql.toString(), parameterMap, rowMapper);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return null;
	}

	public DataEngineConfig getDataEngineConfig() {
		return dataEngineConfig;
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	public DefaultDisbursementResponse getDefaultDisbursementResponse() {
		return defaultDisbursementResponse;
	}

	public void setDefaultDisbursementResponse(DefaultDisbursementResponse defaultDisbursementResponse) {
		this.defaultDisbursementResponse = defaultDisbursementResponse;
	}

}
