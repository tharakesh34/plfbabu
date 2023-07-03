package com.pennanttech.external.app.config.dao;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.pennanttech.pennapps.core.resource.Literal;

public class ExternalDao {

	private static final Logger logger = LogManager.getLogger(ExternalDao.class);

	private DataSource extDataSource;
	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	public void setExtDataSource(DataSource extDataSource) {
		this.extDataSource = extDataSource;
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}

	public String executeSP(String spName, MapSqlParameterSource in) {
		logger.debug(Literal.ENTERING);
		String status = "FAIL";
		try {

			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(extDataSource);
			jdbcCall.withProcedureName(spName);
			jdbcCall.execute(in);

			status = "SUCCESS";
			logger.info("Procedure Execution Completed.");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			status = "Error while executing Procedure";
		}
		logger.debug(Literal.LEAVING);
		return status;
	}

	public String executeSP(String spName) {
		logger.debug(Literal.ENTERING);
		String status = "FAIL";
		try {

			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(extDataSource);
			jdbcCall.withProcedureName(spName);
			jdbcCall.execute();
			status = "SUCCESS";
			logger.info("Procedure Execution Completed.");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			status = "Error while executing Procedure";
		}
		logger.debug(Literal.LEAVING);
		return status;
	}

	public long getSeqNumber(String tableName) {
		StringBuilder sql = new StringBuilder("select ").append(tableName).append(".NEXTVAL from DUAL");

		return extNamedJdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), Long.class);

	}
}