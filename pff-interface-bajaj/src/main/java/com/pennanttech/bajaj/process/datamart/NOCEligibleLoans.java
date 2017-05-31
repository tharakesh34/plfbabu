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

public class NOCEligibleLoans extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(NOCEligibleLoans.class);

	public NOCEligibleLoans(DataSource dataSource, long userId, Date valueDate, Date appDate, boolean logBatch) {
		super(dataSource, App.DATABASE.name(), userId, valueDate, logBatch);
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
		sql.append(" SELECT * from DM_NOC_ELIGIBLE_LOANS_VIEW ");

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
						saveOrUpdate(map, "DM_NOC_ELIGIBLE_LOANS", destinationJdbcTemplate, filterFields);
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

		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("LOANACCTNUM", rs.getObject("LOANACCTNUM"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("EMAIL", rs.getObject("EMAIL"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("PROCESSED_FLAG", rs.getObject("PROCESSED_FLAG"));

		return map;

	}
}
