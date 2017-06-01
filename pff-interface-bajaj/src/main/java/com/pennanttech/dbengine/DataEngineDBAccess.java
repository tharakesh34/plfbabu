package com.pennanttech.dbengine;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.dataengine.DataAccess;
import com.pennanttech.dataengine.model.DataEngineStatus;

public class DataEngineDBAccess extends DataAccess {
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public DataEngineDBAccess(DataSource appDataSource, String dataBase, DataEngineStatus executionStatus) {
		this.database = dataBase;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(appDataSource);
	}
	
	protected void saveBatchLog(MapSqlParameterSource source, String sql) throws Exception {
		this.jdbcTemplate.update(sql.toString(), source);
	}
	
	protected String getApplicationDate() throws Exception {
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuffer sql = new StringBuffer();

		sql.append(" Select SysParmValue from SMTparameters Where SysParmCode = :SysParmCode ");
		source.addValue("SysParmCode", "APP_DATE");

		return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
	}
}
