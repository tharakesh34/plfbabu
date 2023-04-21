package com.pennanttech.external.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.config.InterfaceErrorCode;
import com.pennanttech.external.dao.ExtInterfaceDao;
import com.pennanttech.external.mandate.errors.ExtMandateError;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class ExtInterfaceDaoImpl implements ExtInterfaceDao {

	private static final Logger logger = LogManager.getLogger(ExtInterfaceDaoImpl.class);

	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	@Override
	public List<InterfaceErrorCode> fetchInterfaceErrorCodes() {
		logger.debug(Literal.ENTERING);
		String queryStr;

		List<InterfaceErrorCode> list = new ArrayList<InterfaceErrorCode>();

		queryStr = "SELECT ERROR_CODE,ERROR_MESSAGE,ERROR_DESC FROM INTERFACE_ERROR_CODES";

		extNamedJdbcTemplate.getJdbcOperations().query(queryStr, rs -> {
			InterfaceErrorCode errorCode = new InterfaceErrorCode();
			errorCode.setErrorCode(rs.getString("ERROR_CODE"));
			errorCode.setErrorMessage(rs.getString("ERROR_MESSAGE"));
			errorCode.setErrorTag(rs.getString("ERROR_DESC"));

			list.add(errorCode);
		});
		logger.debug(Literal.LEAVING);
		return list;
	}

	private void resetSequences(String seqName, long sequence) {
		switch (App.DATABASE) {
		case ORACLE:
		case MY_SQL:
			extNamedJdbcTemplate.getJdbcOperations()
					.execute("ALTER SEQUENCE " + seqName + " RESTART START WITH " + sequence);
			break;
		case POSTGRES:
			extNamedJdbcTemplate.getJdbcOperations().execute("ALTER SEQUENCE " + seqName + " RESTART WITH " + sequence);
			break;
		default:
			//
		}
	}

	@Override
	public void resetAllSequences() {
		resetSequences("SEQ_PRMNT_SI", 1);
		resetSequences("SEQ_PRMNT_SI_INTERNAL", 1);
		resetSequences("SEQ_PRMNT_ACH", 1);
		resetSequences("SEQ_PRMNT_PDC", 1);
	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}

	@Override
	public String getRemarkFromCode(String code) {
		String sql = "SELECT NAME FROM EXT_MANDATE_ERRORS WHERE CODE = ?";

		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, String.class, code);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return "";
	}

	@Override
	public List<ExtMandateError> getExtMandateErrors() {
		logger.debug(Literal.ENTERING);
		String queryStr;

		List<ExtMandateError> list = new ArrayList<ExtMandateError>();

		queryStr = "SELECT CODE,NAME FROM EXT_MANDATE_ERRORS WHERE STATUS = 1";

		extNamedJdbcTemplate.getJdbcOperations().query(queryStr, rs -> {
			ExtMandateError errorCode = new ExtMandateError();
			errorCode.setCode(rs.getString("CODE"));
			errorCode.setName(rs.getString("NAME"));
			list.add(errorCode);
		});
		logger.debug(Literal.LEAVING);
		return list;
	}

	@Override
	public List<ExternalConfig> getExternalConfig() {
		logger.debug(Literal.ENTERING);

		String queryStr;

		List<ExternalConfig> list = new ArrayList<ExternalConfig>();
		queryStr = "SELECT INTERFACE_TYPE,NO_OF_RECORDS,FILE_LOCATION,HOLD_TYPE,FILE_PREPEND,FILE_POSTPEND,"
				+ "FILE_EXTENSION,DATE_FORMAT,SUCCESS_INDICATOR,FAIL_INDICATOR " + ",BACKUP_LOCATION,ACCESS_KEY,"
				+ "SECRET_KEY,HOST_NAME,PORT,PRIVATE_KEY,SSE_ALGORITHM,PREFIX,"
				+ "SFTP_LOCATION,IS_SFTP,LOCAL_BACKUP_LOCATION,SFTP_BUCKET" + " FROM FILE_INTERFACE_CONFIG";

		extNamedJdbcTemplate.getJdbcOperations().query(queryStr, rs -> {
			ExternalConfig extConfig = new ExternalConfig();
			extConfig.setInterfaceName(rs.getString("INTERFACE_TYPE"));
			extConfig.setNoOfRecords(rs.getBigDecimal("NO_OF_RECORDS"));
			extConfig.setFileLocation(rs.getString("FILE_LOCATION"));
			extConfig.setHodlType(rs.getInt("HOLD_TYPE"));
			extConfig.setFilePrepend(StringUtils.trimToEmpty(rs.getString("FILE_PREPEND")));
			extConfig.setFilePostpend(StringUtils.trimToEmpty(rs.getString("FILE_POSTPEND")));
			extConfig.setFileExtension(StringUtils.trimToEmpty(rs.getString("FILE_EXTENSION")));
			extConfig.setDateFormat(rs.getString("DATE_FORMAT"));
			extConfig.setSuccessIndicator(rs.getString("SUCCESS_INDICATOR"));
			extConfig.setFailIndicator(rs.getString("FAIL_INDICATOR"));

			extConfig.setFileBackupLocation(rs.getString("BACKUP_LOCATION"));
			extConfig.setAccessKey(rs.getString("ACCESS_KEY"));
			extConfig.setSecretKey(rs.getString("SECRET_KEY"));
			extConfig.setHostName(rs.getString("HOST_NAME"));
			extConfig.setPort(rs.getInt("PORT"));
			extConfig.setPrivateKey(rs.getString("PRIVATE_KEY"));
			extConfig.setSseAlgo(rs.getString("SSE_ALGORITHM"));
			extConfig.setSftpPrefix(rs.getString("PREFIX"));
			extConfig.setFileSftpLocation(rs.getString("SFTP_LOCATION"));
			extConfig.setIsSftp(rs.getString("IS_SFTP"));
			extConfig.setFileLocalBackupLocation(rs.getString("LOCAL_BACKUP_LOCATION"));
			extConfig.setSftpBucketLocation(rs.getString("SFTP_BUCKET"));

			list.add(extConfig);
		});
		logger.debug(Literal.LEAVING);
		return list;
	}

}
