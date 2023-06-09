package com.pennanttech.external.app.config.dao;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.pennanttech.pennapps.core.resource.Literal;

public class ExternalDao {

	private static final Logger logger = LogManager.getLogger(ExternalDao.class);

	DataSource extDataSource;

	public void setExtDataSource(DataSource extDataSource) {
		this.extDataSource = extDataSource;
	}

	public String executeSP(String spName, MapSqlParameterSource in) {
		String status = "FAIL";
		try {

			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(extDataSource);
			jdbcCall.withProcedureName(spName);
			jdbcCall.execute(in);

			status = "SUCCESS";
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			status = "Error In Calling Procedure";
		}
		logger.info("UCIC request file writing Completed.");
		return status;
	}

	public String executeSP(String spName) {
		String status = "FAIL";
		try {

			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(extDataSource);
			jdbcCall.withProcedureName(spName);
			jdbcCall.execute();

			status = "SUCCESS";
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			status = "Error In Calling Procedure";
		}
		logger.info("UCIC request file writing Completed.");
		return status;
	}

}
