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

public class WriteOffDetailsDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(WriteOffDetailsDataMart.class);

	public WriteOffDetailsDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
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
		sql.append(" SELECT * from DM_WRITEOFF_DETAILS_VIEW ");

		parmMap = new MapSqlParameterSource();

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {
			MapSqlParameterSource map = null;

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				String[] filterFields = new String[1];
				filterFields[0] = "AGREEMENTNO";
				while (rs.next()) {

					try {
						map = mapData(rs);
						saveOrUpdate(map, "DM_WRITEOFF_DETAILS", destinationJdbcTemplate, filterFields);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						String keyId = rs.getString("AGREEMENTNO");
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

		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("CHARGEOFF_DT", rs.getObject("CHARGEOFF_DT"));
		map.addValue("CHARGEOFF_FLG", rs.getObject("CHARGEOFF_FLG"));
		map.addValue("CHARGEOFF_REASON", rs.getObject("CHARGEOFF_REASON"));
		map.addValue("SETTLEMENT_LOSS", rs.getObject("SETTLEMENT_LOSS"));
		map.addValue("GROSS_WRITEOFF_AMT", rs.getObject("GROSS_WRITEOFF_AMT"));
		map.addValue("NET_WRITEOFF_AMT", rs.getObject("NET_WRITEOFF_AMT"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROCESSED_FLAG", rs.getObject("PROCESSED_FLAG"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("RECEIPT_ON_CHARGEOFF", rs.getObject("RECEIPT_ON_CHARGEOFF"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));

		return map;

	}
}
