package com.pennanttech.bajaj.services;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.pff.core.Literal;

public abstract class BajajServices {
	private static final Logger				logger	= Logger.getLogger(BajajServices.class);
	
	protected DataSource					dataSource;
	protected JdbcTemplate					jdbcTemplate;
	protected NamedParameterJdbcTemplate	namedJdbcTemplate;
	
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	protected Object getSMTParameter(String sysParmCode, Class<?> type) throws Exception {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT SYSPARMVALUE FROM SMTPARAMETERS where SYSPARMCODE = :SYSPARMCODE");
		paramMap.addValue("SYSPARMCODE", sysParmCode);

		try {
			return namedJdbcTemplate.queryForObject(sql.toString(), paramMap, type);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("The parameter code " + sysParmCode + " not configured.");
		}
	}

	protected int updateParameter(String sysParmCode, Object value) throws Exception {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("UPDATE SMTPARAMETERS SET SYSPARMVALUE = :SYSPARMVALUE where SYSPARMCODE = :SYSPARMCODE");
		paramMap.addValue("SYSPARMCODE", sysParmCode);
		paramMap.addValue("SYSPARMVALUE", value);

		try {
			return namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to update the " + sysParmCode + ".");
		}
	}
	
	protected Date getAppDate() throws Exception {
		String appDate = (String) getSMTParameter("APP_VALUEDATE", String.class);
		return DateUtil.parse(appDate, "yyyy-MM-dd"); //FIXME Deriving Application date should be from core.
	}
	
}
