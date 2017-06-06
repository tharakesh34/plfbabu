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

public class InsuranceDetailsDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger	logger	= Logger.getLogger(InsuranceDetailsDataMart.class);

	public InsuranceDetailsDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
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
		sql.append(" SELECT * from DM_INSURANCE_DETAILS_VIEW ");

		parmMap = new MapSqlParameterSource();

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Long>() {
			MapSqlParameterSource	map	= null;

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				String[] filterFields = new String[1];
				filterFields[0] = "AGREEMENTNO";
				while (rs.next()) {

					try {
						map = mapData(rs);
						saveOrUpdate(map, "DM_INSURANCE_DETAILS", destinationJdbcTemplate, filterFields);
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
		map.addValue("ASSETID", rs.getObject("ASSETID"));
		map.addValue("INSURANCE_TYPE", rs.getObject("INSURANCE_TYPE"));
		map.addValue("INSUR_PREMIUM", rs.getObject("INSUR_PREMIUM"));
		map.addValue("INSURANCE_RENEWAL_DATE", rs.getObject("INSURANCE_RENEWAL_DATE"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROCESSED_FLAG", rs.getObject("PROCESSED_FLAG"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("POLICY_TENURE", rs.getObject("POLICY_TENURE"));
		map.addValue("SUM_ASSURED", rs.getObject("SUM_ASSURED"));
		map.addValue("NOMINEE_NAME", rs.getObject("NOMINEE_NAME"));
		map.addValue("GOOD_HEALTH", rs.getObject("GOOD_HEALTH"));
		map.addValue("CRITICAL_ILLNESS_FLAG", rs.getObject("CRITICAL_ILLNESS_FLAG"));
		map.addValue("RELATIONSHIP", rs.getObject("RELATIONSHIP"));
		map.addValue("NOMINEE_ADDRESS", rs.getObject("NOMINEE_ADDRESS"));
		map.addValue("NOMINEE_RELATION", rs.getObject("NOMINEE_RELATION"));
		map.addValue("NOMINEE_DOB", rs.getObject("NOMINEE_DOB"));
		map.addValue("NOMINEE_AGE", rs.getObject("NOMINEE_AGE"));
		map.addValue("NOMINEE_CONTACTNO", rs.getObject("NOMINEE_CONTACTNO"));
		map.addValue("NOMINEE_NAME2", rs.getObject("NOMINEE_NAME2"));
		map.addValue("NOMINEE_ADDRESS2", rs.getObject("NOMINEE_ADDRESS2"));
		map.addValue("NOMINEE_RELATION2", rs.getObject("NOMINEE_RELATION2"));
		map.addValue("NOMINEE_DOB2", rs.getObject("NOMINEE_DOB2"));
		map.addValue("NOMINEE_AGE2", rs.getObject("NOMINEE_AGE2"));
		map.addValue("NOMINEE_CONTACTNO2", rs.getObject("NOMINEE_CONTACTNO2"));
		map.addValue("DFGH", rs.getObject("DFGH"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;
	}

}
