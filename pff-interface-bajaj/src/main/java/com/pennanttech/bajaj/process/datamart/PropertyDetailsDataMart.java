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

public class PropertyDetailsDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger	logger	= Logger.getLogger(PropertyDetailsDataMart.class);

	public PropertyDetailsDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
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
		sql.append(" SELECT * from DM_PROPERTY_DTL_VIEW");

		parmMap = new MapSqlParameterSource();

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {
			MapSqlParameterSource map = null;

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				String[] filterFields = new String[1];
				filterFields[0] = "PROPERTYID";
				while (rs.next()) {

					try {
						map = mapData(rs);
						saveOrUpdate(map, "DM_PROPERTY_DTL", destinationJdbcTemplate, filterFields);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						String keyId = rs.getString("PROPERTYID");
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

		map.addValue("PROPERTYID", rs.getObject("PROPERTYID"));
		map.addValue("CITY", rs.getObject("CITY"));
		map.addValue("ADDRESS1", rs.getObject("ADDRESS1"));
		map.addValue("ADDRESS2", rs.getObject("ADDRESS2"));
		map.addValue("ADDRESS3", rs.getObject("ADDRESS3"));
		map.addValue("CITY1", rs.getObject("CITY1"));
		map.addValue("STATE", rs.getObject("STATE"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROPERTY_TYPE", rs.getObject("PROPERTY_TYPE"));
		map.addValue("PROPERTY_DESC", rs.getObject("PROPERTY_DESC"));
		map.addValue("PROPERTY_VALUE", rs.getObject("PROPERTY_VALUE"));
		map.addValue("ZIPCODE", rs.getObject("ZIPCODE"));
		map.addValue("PROCESSFLAG", rs.getObject("PROCESSFLAG"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));


		return map;

	}
}
