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

public class LoanWiseRepayScheduleDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(LoanWiseRepayScheduleDataMart.class);

	public LoanWiseRepayScheduleDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, valueDate);
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
		sql.append(" SELECT * from DM_LOANWISE_CHARGE_DTLS_VIEW");

		parmMap = new MapSqlParameterSource();

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {
			MapSqlParameterSource map = null;

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				String[] filterFields = new String[1];
				filterFields[0] = "AGREEMENTID";
				while (rs.next()) {

					try {
						map = mapData(rs);
						saveOrUpdate(map, "DM_LOANWISE_REPAYSCHEDULE", destinationJdbcTemplate, filterFields);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						String keyId = rs.getString("AGREEMENTID");
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

		map.addValue("AGREEMENTID", rs.getObject("AGREEMENTID"));
		map.addValue("TXNADVICEID", rs.getObject("TXNADVICEID"));
		map.addValue("CHARGEID", rs.getObject("CHARGEID"));
		map.addValue("CHARGECODEID", rs.getObject("CHARGECODEID"));
		map.addValue("CHARGEDESC", rs.getObject("CHARGEDESC"));
		map.addValue("CHARGEAMT", rs.getObject("CHARGEAMT"));
		map.addValue("STATUS", rs.getObject("STATUS"));
		map.addValue("AMTINPROCESS", rs.getObject("AMTINPROCESS"));
		map.addValue("TXNADJUSTEDAMT", rs.getObject("TXNADJUSTEDAMT"));
		map.addValue("ADVICEAMT", rs.getObject("ADVICEAMT"));
		map.addValue("ADVICEDATE", rs.getObject("ADVICEDATE"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROCESSDATE", rs.getObject("PROCESSDATE"));
		map.addValue("PROCESSED_FLAG", rs.getObject("PROCESSED_FLAG"));
		map.addValue("SEGMENT", rs.getObject("SEGMENT"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));
		return map;

	}
}
