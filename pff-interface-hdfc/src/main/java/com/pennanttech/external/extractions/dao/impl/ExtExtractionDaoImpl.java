package com.pennanttech.external.extractions.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.external.extractions.dao.ExtExtractionDao;
import com.pennanttech.external.extractions.service.ExtractionConstants;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class ExtExtractionDaoImpl extends SequenceDao implements ExtExtractionDao, ExtractionConstants {
	private static final Logger logger = LogManager.getLogger(ExtExtractionDaoImpl.class);

	private NamedParameterJdbcTemplate mainNamedJdbcTemplate;
	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	public ExtExtractionDaoImpl() {
		super();
	}

	@Override
	public long getSeqNumber(String tableName) {
		setDataSource(extNamedJdbcTemplate.getJdbcTemplate().getDataSource());
		return getNextValue(tableName);
	}

	@Override
	public String executeSp(String spName) {
		logger.info("SP Execution Started.");
		String status = "FAIL";
		try {
			mainNamedJdbcTemplate.getJdbcOperations().call(new CallableStatementCreator() {

				@Override
				public CallableStatement createCallableStatement(Connection connection) throws SQLException {

					CallableStatement callableStatement = connection.prepareCall("{ call " + spName + "() }");
					return callableStatement;

				}
			}, new ArrayList<SqlParameter>());
			status = "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			status = "Error In Calling Procedure";
		}
		logger.info("SP Execution Completed.");
		return status;
	}

	public String executeSp(String spName, String fileName) {
		logger.info("File writing SP Execution Started");
		String status = "FAIL";
		try {
			this.mainNamedJdbcTemplate.getJdbcOperations().call(new CallableStatementCreator() {

				@Override
				public CallableStatement createCallableStatement(Connection connection) throws SQLException {

					CallableStatement callableStatement = connection
							.prepareCall("{ call " + spName + "('" + fileName + "') }");
					return callableStatement;

				}
			}, new ArrayList<SqlParameter>());

			status = "SUCCESS";
		} catch (Exception e) {
			logger.error("Exception: Error while executing the Procedure ", e);
			status = "Error In Executing Procedure";
		}
		logger.info("File writing SP Execution Completed");
		return status;
	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}

	public void setMainDataSource(DataSource mainDataSource) {
		this.mainNamedJdbcTemplate = new NamedParameterJdbcTemplate(mainDataSource);
	}

}