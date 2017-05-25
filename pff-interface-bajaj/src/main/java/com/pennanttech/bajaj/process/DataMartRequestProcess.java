package com.pennanttech.bajaj.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;

public class DataMartRequestProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(DataMartRequestProcess.class);

	public DataMartRequestProcess(DataSource dataSource, long userId, Date valueDate) {
		super(dataSource, App.DATABASE.name(), userId, valueDate);
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		try {
			prosessData1();
		} catch (Exception e) {
			// TODO Exception
		}

		logger.debug(Literal.LEAVING);
	}

	private List<String> prosessData1() throws Exception {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
		try {
			return jdbcTemplate.query(sql.toString(), paramMap, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					String id = null;
					Map<String, Object> rowMap = rowMapper.mapRow(rs, rowNum);
					// Add more columns not there in source query
					// Curd Operations use the methods in DataEngine for curd operations. methods like
					// saveOrUpdate, save, update, delete
					rowMap = null;
					return id;// Based on our requirement
				}
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			paramMap = null;
			sql = null;
		}
		logger.debug(Literal.ENTERING);
		return null;
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;
	}
}
