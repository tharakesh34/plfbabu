package com.pennanttech.external.services;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class ExternalServiceStatus {

	private static String sqlQry = "Select active from EXTINTERFACECONF where code = :ServiceCode ";
	private static NamedParameterJdbcTemplate jdbcTemplate;

	public static boolean isServiceActive(String serviceCode) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("ServiceCode", serviceCode);
		try {
			return jdbcTemplate.queryForObject(sqlQry, paramMap, Boolean.class);
		} catch (Exception e) {

		}
		return true;
	}

	public static void setDataSource(DataSource dataSource) {
		ExternalServiceStatus.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}
