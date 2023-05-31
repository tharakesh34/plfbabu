package com.pennanttech.external.ucic.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.external.ucic.model.ExtUcicCust;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicDaoImpl extends SequenceDao<ExtUcicCust> implements ExtUcicDao {
	private static final Logger logger = LogManager.getLogger(ExtUcicDaoImpl.class);
	private NamedParameterJdbcTemplate mainNamedJdbcTemplate;

	public ExtUcicDaoImpl() {
		super();
	}

	@Override
	public long getSeqNumber(String tableName) {
		setDataSource(mainNamedJdbcTemplate.getJdbcTemplate().getDataSource());
		return getNextValue(tableName);
	}

	@Override
	public void updateRecordProcessingFlagAndFileStatus(ExtUcicCust customer, int process_flag, int file_status) {
		logger.debug(Literal.ENTERING);
		String queryStr = "UPDATE UCIC_CUSTOMERS SET PROCESS_FLAG = ?,FILE_STATUS = ? WHERE CUSTID = ? AND FINREFERENCE = ?";
		logger.debug(Literal.SQL + queryStr);
		mainNamedJdbcTemplate.getJdbcOperations().update(queryStr.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, process_flag);
			ps.setLong(index++, file_status);
			ps.setLong(index++, customer.getCustId());
			ps.setString(index, customer.getFinreference());
		});
		logger.debug(Literal.LEAVING);

	}

	@Override
	public boolean isFileProcessed(String fileName) {
		String sql = "Select count(1) from UCIC_RESP_FILES Where FILE_NAME= ?";
		logger.debug(Literal.SQL + sql);
		try {
			return mainNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, fileName) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public void saveResponseFile(String fileName, String fileLocation, int fileStatus, String errorCode,
			String errorMessage) {
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder("INSERT INTO UCIC_RESP_FILES");
		sql.append(" (FILE_NAME,FILE_LOCATION,STATUS,CREATED_DATE,ERROR_CODE,ERROR_MESSAGE)");
		sql.append(" VALUES (?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		mainNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, fileName);
			ps.setString(index++, fileLocation);
			ps.setLong(index++, fileStatus);
			ps.setTimestamp(index++, curTimeStamp);
			ps.setString(index++, errorCode);
			ps.setString(index, errorMessage);
		});

	}

	@Override
	public void updateResponseFileProcessingFlag(long id, int status, String errorCode, String errorMessage) {
		String sql = "UPDATE UCIC_RESP_FILES SET STATUS = ?,ERROR_CODE = ? ,ERROR_MESSAGE =? Where ID= ? ";
		logger.debug(Literal.SQL + sql);
		mainNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setInt(index++, status);
			ps.setString(index++, errorCode);
			ps.setString(index++, errorMessage);
			ps.setLong(index, id);

		});
		logger.debug(Literal.LEAVING);
	}

	@Override
	public String executeDataExtractionFromSP() {
		logger.info("Extracting UCIC data");
		String status = "FAIL";
		try {
			mainNamedJdbcTemplate.getJdbcOperations().call(new CallableStatementCreator() {

				@Override
				public CallableStatement createCallableStatement(Connection connection) throws SQLException {

					CallableStatement callableStatement = connection.prepareCall("{ call SP_EXTRACT_UCIC_DATA() }");
					return callableStatement;

				}
			}, new ArrayList<SqlParameter>());

			status = "SUCCESS";
		} catch (Exception e) {
			logger.error("Exception: Extraction of UCIC Cust Data failed ", e);
			status = "Error In Calling Procedure";
		}
		logger.info("Extracting UCIC data Completed");
		return status;
	}

	@Override
	public String executeUcicRequestFileSP(String fileName) {
		String status = "FAIL";
		try {
			mainNamedJdbcTemplate.getJdbcOperations().call(new CallableStatementCreator() {

				@Override
				public CallableStatement createCallableStatement(Connection connection) throws SQLException {

					CallableStatement callableStatement = connection
							.prepareCall("{ call SP_UCIC_WRITE_REQUEST_FILE('" + fileName + "') }");
					return callableStatement;

				}
			}, new ArrayList<SqlParameter>());

			status = "SUCCESS";
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			status = "Error In Calling Procedure";
		}
		logger.info("UCIC request file writing Completed.");
		return status;
	}

	@Override
	public String executeUcicWeeklyRequestFileSP(String fileName) {
		String status = "FAIL";
		try {
			mainNamedJdbcTemplate.getJdbcOperations().call(new CallableStatementCreator() {

				@Override
				public CallableStatement createCallableStatement(Connection connection) throws SQLException {

					CallableStatement callableStatement = connection
							.prepareCall("{ call SP_UCIC_WRITE_WEEKLY_REQUEST_FILE('" + fileName + "') }");
					return callableStatement;

				}
			}, new ArrayList<SqlParameter>());

			status = "SUCCESS";
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			status = "Error In Calling Procedure";
		}
		logger.info("UCIC request file writing Completed.");
		return status;
	}

	@Override
	public String executeUcicResponseFileSP(String fileName) {
		String status = "FAIL";
		try {
			mainNamedJdbcTemplate.getJdbcOperations().call(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection connection) throws SQLException {
					CallableStatement callableStatement = connection
							.prepareCall("{ call SP_READ_UCIC_RESP_FILE('" + fileName + "') }");
					return callableStatement;
				}
			}, new ArrayList<SqlParameter>());
			status = "SUCCESS";
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			status = "Error In Calling Procedure";
		}
		logger.info("UCIC request file writing Completed.");
		return status;
	}

	@Override
	public String executeUcicAckFileSP(String fileName) {
		String status = "FAIL";
		try {
			mainNamedJdbcTemplate.getJdbcOperations().call(new CallableStatementCreator() {

				@Override
				public CallableStatement createCallableStatement(Connection connection) throws SQLException {

					CallableStatement callableStatement = connection
							.prepareCall("{ call SP_UCIC_WRITE_ACK_FILE('" + fileName + "') }");
					return callableStatement;

				}
			}, new ArrayList<SqlParameter>());

			status = "SUCCESS";
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			status = "Error In Calling Procedure";
		}
		logger.info("UCIC request file writing Completed.");
		return status;
	}

	@Override
	public int updateAckForFile(String fileName, int ackStatus) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE UCIC_RESP_FILES SET ACK_STATUS=? WHERE FILE_NAME = ?");

		logger.debug(Literal.SQL + sql.toString());

		return mainNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			ps.setLong(1, ackStatus);
			ps.setString(2, fileName);
		});

	}

	public void setMainDataSource(DataSource extDataSource) {
		this.mainNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}
}
