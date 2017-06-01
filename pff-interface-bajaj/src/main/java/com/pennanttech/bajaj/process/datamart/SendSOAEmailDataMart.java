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

public class SendSOAEmailDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(SendSOAEmailDataMart.class);

	public SendSOAEmailDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
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
		sql.append(" SELECT * from DM_SEND_SOA_EMAIL_VIEW ");

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
						saveOrUpdate(map, "DM_SEND_SOA_EMAIL", destinationJdbcTemplate, filterFields);
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

		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("EMAILID", rs.getObject("EMAILID"));
		map.addValue("AGREEMENTID", rs.getObject("AGREEMENTID"));
		map.addValue("PROCESSID", rs.getObject("PROCESSID"));
		map.addValue("GENERATION_DATE", rs.getObject("GENERATION_DATE"));
		map.addValue("PROCESSED", rs.getObject("PROCESSED"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("GROUPID", rs.getObject("GROUPID"));
		map.addValue("TOTAL_LAN", rs.getObject("TOTAL_LAN"));
		map.addValue("TOTAL_CLOSED_LAN", rs.getObject("TOTAL_CLOSED_LAN"));
		map.addValue("TOTAL_ACTIVE_LAN", rs.getObject("TOTAL_ACTIVE_LAN"));
		map.addValue("SWIPE_FLAG", rs.getObject("SWIPE_FLAG"));
		map.addValue("EMI_CARD_NO", rs.getObject("EMI_CARD_NO"));
		map.addValue("DISBURSEMENT_DATE", rs.getObject("DISBURSEMENT_DATE"));
		map.addValue("SUPPLIERID", rs.getObject("SUPPLIERID"));
		map.addValue("SUPPLIERDESC", rs.getObject("SUPPLIERDESC"));
		map.addValue("AMT_FIN", rs.getObject("AMT_FIN"));
		map.addValue("EMI", rs.getObject("EMI"));
		map.addValue("NEXT_EMI_DUE_DATE", rs.getObject("NEXT_EMI_DUE_DATE"));
		map.addValue("CHEQUE_BOUNCE_CHARGE", rs.getObject("CHEQUE_BOUNCE_CHARGE"));

		return map;

	}
}
