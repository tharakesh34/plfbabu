package com.pennanttech.bajaj.process.datamart;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;

public class AddressDetailsDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger	logger	= Logger.getLogger(AddressDetailsDataMart.class);

	public AddressDetailsDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate);
	}

	@Override
	public void run() {
		processData();
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parmMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * from DM_ADDRESS_DETAILS_VIEW ");

		parmMap = new MapSqlParameterSource();

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {
			MapSqlParameterSource map = null;

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				String[] filterFields = new String[1];
				filterFields[0] = "ADDRESSID";
				while (rs.next()) {

					try {
						map = mapData(rs);
						saveOrUpdate(map, "DM_ADDRESS_DETAILS", destinationJdbcTemplate, filterFields);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						String keyId = rs.getString("ADDRESSID");
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
				return totalRecords;
			}
		});
		logger.debug(Literal.LEAVING);
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("ADDRESSID", rs.getObject("ADDRESSID"));
		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("STATEID", rs.getObject("STATEID"));
		map.addValue("REGIONID", rs.getObject("REGIONID"));
		map.addValue("CITY", rs.getObject("CITY"));
		map.addValue("STDISD", rs.getObject("STDISD"));
		map.addValue("MAILINGADDRESS", rs.getObject("MAILINGADDRESS"));
		map.addValue("ADDRESS1", rs.getObject("ADDRESS1"));
		map.addValue("ADDRESS2", rs.getObject("ADDRESS2"));
		map.addValue("ADDRESS3", rs.getObject("ADDRESS3"));
		map.addValue("ZIPCODE", rs.getObject("ZIPCODE"));
		map.addValue("COUNTRY", rs.getObject("COUNTRY"));
		map.addValue("ADDRESSTYPE", rs.getObject("ADDRESSTYPE"));
		map.addValue("APPLICANT_TYPE", rs.getObject("APPLICANT_TYPE"));
		map.addValue("PHONE1", rs.getObject("PHONE1"));
		map.addValue("PHONE2", rs.getObject("PHONE2"));
		map.addValue("MOBILE", rs.getObject("MOBILE"));
		map.addValue("EMAIL", rs.getObject("EMAIL"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROCESSFLAG", rs.getObject("PROCESSFLAG"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("AREA", rs.getObject("AREA"));
		map.addValue("LANDMARK", rs.getObject("LANDMARK"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;

	}
}
