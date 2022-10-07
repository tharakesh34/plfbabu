package com.pennanttech.external.services;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.pennapps.core.resource.Message;

public class ExternalServiceStatus {
	private static Logger logger = LogManager.getLogger(ExternalServiceStatus.class);

	private static String sqlQry = "Select active from EXTINTERFACECONF where code = :ServiceCode ";
	private static NamedParameterJdbcTemplate jdbcTemplate;

	public static boolean isServiceActive(String serviceCode) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("ServiceCode", serviceCode);
		try {
			return jdbcTemplate.queryForObject(sqlQry, paramMap, Boolean.class);
		} catch (Exception e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return true;
	}

	public static void setDataSource(DataSource dataSource) {
		ExternalServiceStatus.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}
