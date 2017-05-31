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

public class ReschDetailsDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(ReschDetailsDataMart.class);

	public ReschDetailsDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate, boolean logBatch) {
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
		sql.append(" SELECT * from DM_RESCH_DETAILS_DAILY_VIEW ");

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
						saveOrUpdate(map, "DM_RESCH_DETAILS_DAILY", destinationJdbcTemplate, filterFields);
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
		map.addValue("DISB_STATUS", rs.getObject("DISB_STATUS"));
		map.addValue("GROSS_TENURE", rs.getObject("GROSS_TENURE"));
		map.addValue("NET_TENURE", rs.getObject("NET_TENURE"));
		map.addValue("MATURITYDATE", rs.getObject("MATURITYDATE"));
		map.addValue("EXPIRYDATE", rs.getObject("EXPIRYDATE"));
		map.addValue("EMI", rs.getObject("EMI"));
		map.addValue("REPAYMENT_MODE", rs.getObject("REPAYMENT_MODE"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROCESSED_FLAG", rs.getObject("PROCESSED_FLAG"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("FREQ", rs.getObject("FREQ"));
		map.addValue("LOAN_STATUS", rs.getObject("LOAN_STATUS"));
		map.addValue("CLOSUREDATE", rs.getObject("CLOSUREDATE"));
		map.addValue("CUST_ACCT_NO", rs.getObject("CUST_ACCT_NO"));
		map.addValue("BANKNAME", rs.getObject("BANKNAME"));
		map.addValue("MICRCODE", rs.getObject("MICRCODE"));
		map.addValue("CUST_BANK_BRANCH", rs.getObject("CUST_BANK_BRANCH"));
		map.addValue("CUST_BANK_CITY", rs.getObject("CUST_BANK_CITY"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("PDCID", rs.getObject("PDCID"));
		map.addValue("PCFLAG", rs.getObject("PCFLAG"));
		map.addValue("TIE_UP", rs.getObject("TIE_UP"));
		map.addValue("MARGIN", rs.getObject("MARGIN"));
		map.addValue("SPECIALMARGIN", rs.getObject("SPECIALMARGIN"));
		map.addValue("FIXEDTENOR", rs.getObject("FIXEDTENOR"));
		map.addValue("CEEFFECTIVEDATE", rs.getObject("CEEFFECTIVEDATE"));
		map.addValue("EFF_RATE", rs.getObject("EFF_RATE"));
		map.addValue("PLRRATE", rs.getObject("PLRRATE"));
		map.addValue("TIE_UP_WITH", rs.getObject("TIE_UP_WITH"));
		map.addValue("DATE_OF_CLOSURE", rs.getObject("DATE_OF_CLOSURE"));
		map.addValue("PDCMS_SEQ_GENERATED_DATE", rs.getObject("PDCMS_SEQ_GENERATED_DATE"));
		map.addValue("INSTRUMENT_DATA_ENTRY_DATE", rs.getObject("INSTRUMENT_DATA_ENTRY_DATE"));
		map.addValue("PAYMENT_AUTHORIZATION_DATE", rs.getObject("PAYMENT_AUTHORIZATION_DATE"));

		return map;

	}
}
