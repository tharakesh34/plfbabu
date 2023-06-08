package com.pennanttech.external.app.config.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.config.model.FileTransferConfig;
import com.pennanttech.external.app.config.model.InterfaceErrorCode;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtGenericDaoImpl implements ExtGenericDao {

	private static final Logger logger = LogManager.getLogger(ExtGenericDaoImpl.class);

	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	@Override
	public List<InterfaceErrorCode> fetchInterfaceErrorCodes() {
		logger.debug(Literal.ENTERING);
		String queryStr;

		List<InterfaceErrorCode> list = new ArrayList<InterfaceErrorCode>();

		queryStr = "SELECT ERROR_CODE,ERROR_MESSAGE FROM INTERFACE_ERROR_CODES";

		extNamedJdbcTemplate.getJdbcOperations().query(queryStr, rs -> {
			InterfaceErrorCode errorCode = new InterfaceErrorCode();
			errorCode.setErrorCode(rs.getString("ERROR_CODE"));
			errorCode.setErrorMessage(rs.getString("ERROR_MESSAGE"));
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
		resetSequences("SEQ_SILIEN", 1);
		resetSequences("SEQ_FINCON_GL", 1);
		resetSequences("SEQ_COLLECTION_RECEIPT", 1);
	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}

	@Override
	public List<FileInterfaceConfig> getExternalConfig() {
		logger.debug(Literal.ENTERING);

		String queryStr;

		List<FileInterfaceConfig> list = new ArrayList<FileInterfaceConfig>();
		queryStr = "SELECT INTERFACE_TYPE,NO_OF_RECORDS,FILE_LOCATION,HOLD_TYPE,FILE_PREPEND,FILE_POSTPEND,"
				+ "FILE_EXTENSION,DATE_FORMAT,SUCCESS_INDICATOR,FAIL_INDICATOR ,BACKUP_LOCATION,FIC_NAMES FROM FILE_INTERFACE_CONFIG";

		extNamedJdbcTemplate.getJdbcOperations().query(queryStr, rs -> {
			FileInterfaceConfig extConfig = new FileInterfaceConfig();
			extConfig.setInterfaceName(rs.getString("INTERFACE_TYPE"));
			extConfig.setNoOfRecords(rs.getLong("NO_OF_RECORDS"));
			extConfig.setFileLocation(rs.getString("FILE_LOCATION"));
			extConfig.setHodlType(rs.getInt("HOLD_TYPE"));
			extConfig.setFilePrepend(StringUtils.trimToEmpty(rs.getString("FILE_PREPEND")));
			extConfig.setFilePostpend(StringUtils.trimToEmpty(rs.getString("FILE_POSTPEND")));
			extConfig.setFileExtension(StringUtils.trimToEmpty(rs.getString("FILE_EXTENSION")));
			extConfig.setDateFormat(rs.getString("DATE_FORMAT"));
			extConfig.setSuccessIndicator(rs.getString("SUCCESS_INDICATOR"));
			extConfig.setFailIndicator(rs.getString("FAIL_INDICATOR"));
			extConfig.setFileLocalBackupLocation(rs.getString("BACKUP_LOCATION"));
			extConfig.setFicNames(rs.getString("FIC_NAMES"));
			list.add(extConfig);
		});
		logger.debug(Literal.LEAVING);
		return list;
	}

	@Override
	public List<FileTransferConfig> getFileTransferConfig() {
		logger.debug(Literal.ENTERING);

		String queryStr;

		List<FileTransferConfig> list = new ArrayList<FileTransferConfig>();
		queryStr = "SELECT FIC_NAME,ACCESS_KEY,SECRET_KEY,HOST_NAME,PORT,PRIVATE_KEY,SSE_ALGORITHM,PREFIX,SFTP_LOCATION,SFTP_BACKUP_LOCATION,PROTOCOL FROM FILE_TRANSFER_CONFIG";

		extNamedJdbcTemplate.getJdbcOperations().query(queryStr, rs -> {
			FileTransferConfig ftc = new FileTransferConfig();
			ftc.setFicName(rs.getString("FIC_NAME"));
			ftc.setAccessKey(rs.getString("ACCESS_KEY"));
			ftc.setSecretKey(rs.getString("SECRET_KEY"));
			ftc.setHostName(rs.getString("HOST_NAME"));
			ftc.setPort(rs.getInt("PORT"));
			ftc.setPrivateKey(rs.getString("PRIVATE_KEY"));
			ftc.setSseAlgo(rs.getString("SSE_ALGORITHM"));
			ftc.setSftpPrefix(rs.getString("PREFIX"));
			ftc.setSftpLocation(rs.getString("SFTP_LOCATION"));
			ftc.setSftpBackupLocation(rs.getString("SFTP_BACKUP_LOCATION"));
			ftc.setProtocol(rs.getString("PROTOCOL"));
			list.add(ftc);
		});
		logger.debug(Literal.LEAVING);
		return list;
	}

}
