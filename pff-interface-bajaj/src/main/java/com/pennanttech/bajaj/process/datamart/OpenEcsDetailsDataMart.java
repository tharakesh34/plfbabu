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

public class OpenEcsDetailsDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(OpenEcsDetailsDataMart.class);

	public OpenEcsDetailsDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
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
		sql.append(" SELECT * from DM_OPENECS_DETAILS_VIEW ");

		parmMap = new MapSqlParameterSource();

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {
			MapSqlParameterSource map = null;

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				String[] filterFields = new String[1];
				filterFields[0] = "DISBURSEMENTNO";
				while (rs.next()) {

					try {
						map = mapData(rs);
						saveOrUpdate(map, "DM_OPENECS_DETAILS", destinationJdbcTemplate, filterFields);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						String keyId = rs.getString("DISBURSEMENTNO");
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

		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("ECS_ID", rs.getObject("ECS_ID"));
		map.addValue("BANK_ID", rs.getObject("BANK_ID"));
		map.addValue("BANK_NAME", rs.getObject("BANK_NAME"));
		map.addValue("BANKBRANCHID", rs.getObject("BANKBRANCHID"));
		map.addValue("BANKID", rs.getObject("BANKID"));
		map.addValue("BANK_BRANCH_NAME", rs.getObject("BANK_BRANCH_NAME"));
		map.addValue("CITY", rs.getObject("CITY"));
		map.addValue("ACCTYPE", rs.getObject("ACCTYPE"));
		map.addValue("ACCNO", rs.getObject("ACCNO"));
		map.addValue("MAXLIMIT", rs.getObject("MAXLIMIT"));
		map.addValue("BALLIMIT", rs.getObject("BALLIMIT"));
		map.addValue("UTIL_LIMIT", rs.getObject("UTIL_LIMIT"));
		map.addValue("VALID_LIMIT", rs.getObject("VALID_LIMIT"));
		map.addValue("REPAY_MODE", rs.getObject("REPAY_MODE"));
		map.addValue("MICRCODE", rs.getObject("MICRCODE"));
		map.addValue("ACTIVE_FLAG", rs.getObject("ACTIVE_FLAG"));
		map.addValue("CITYID", rs.getObject("CITYID"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;

	}
}
