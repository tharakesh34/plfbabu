package com.pennant.eod.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


public class DataPurgingProcess {

	private Logger logger = Logger.getLogger(DataPurgingProcess.class);

	private DataSource dataSource;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public DataPurgingProcess() {
		
	}
	
	public String executeAuditDataPurging() {
		logger.info("Audit Data Purging Started");
		final StringBuilder builder = new StringBuilder("{ call SP_AuditDataPurging(?) }");
		String status ="";
		logger.debug("selectSql: " + builder.toString());

		try{		
			status = (String) this.namedParameterJdbcTemplate.getJdbcOperations().execute(
					new CallableStatementCreator() {
						public CallableStatement createCallableStatement(Connection con) throws SQLException{
							CallableStatement cs = con.prepareCall(builder.toString());
							cs.registerOutParameter(1, Types.VARCHAR); 

							return cs;
						}
					},
					new CallableStatementCallback<Object>() {
						public Object doInCallableStatement(CallableStatement cs) throws SQLException{
							cs.execute();
							return  cs.getString(1);
						}
					}
					);
		}catch (Exception e) {
			logger.error("Exception: Audit Data Purging Failed ", e);
			status = "Error In Calling Procedure";
		}
		logger.info("Audit Data Purging Completed");
		return status;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
