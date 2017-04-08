package com.pennanttech.dbengine;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.dataengine.DataAccess;
import com.pennanttech.dataengine.model.DataEngineStatus;

public class DataEngineDBAccess extends DataAccess {
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public DataEngineDBAccess(DataSource appDataSource, String dataBase, DataEngineStatus executionStatus) {
		super(appDataSource, executionStatus);
		this.database = dataBase;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(appDataSource);
	}
	
	protected void saveBatchLog(MapSqlParameterSource source, String sql) throws Exception {
		this.jdbcTemplate.update(sql.toString(), source);
	}
}
