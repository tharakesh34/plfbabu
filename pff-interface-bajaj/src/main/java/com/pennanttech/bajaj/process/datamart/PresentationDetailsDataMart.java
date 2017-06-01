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

public class PresentationDetailsDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(PresentationDetailsDataMart.class);

	public PresentationDetailsDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
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
		sql.append(" SELECT * from DM_PRESENTATION_DETAILS_VIEW ");

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
						saveOrUpdate(map, "DM_PRESENTATION_DETAILS", destinationJdbcTemplate, filterFields);
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
		map.addValue("EMI", rs.getObject("EMI"));
		map.addValue("REPAY_TYPE", rs.getObject("REPAY_TYPE"));
		map.addValue("DEPOSITED_DATE", rs.getObject("DEPOSITED_DATE"));
		map.addValue("CREDIT_STATUS", rs.getObject("CREDIT_STATUS"));
		map.addValue("RETURN_CODE", rs.getObject("RETURN_CODE"));
		map.addValue("RETURN_REASON", rs.getObject("RETURN_REASON"));
		map.addValue("REMARKS", rs.getObject("REMARKS"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("PROCESSDATE", rs.getObject("PROCESSDATE"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("EMI_NO", rs.getObject("EMI_NO"));
		map.addValue("CUSTOMER_BANK_NAME", rs.getObject("CUSTOMER_BANK_NAME"));
		map.addValue("BOM_BOUNCE_BUCKET", rs.getObject("BOM_BOUNCE_BUCKET"));
		map.addValue("MICR_CODE", rs.getObject("MICR_CODE"));
		map.addValue("STATUS_UPDT_DATE", rs.getObject("STATUS_UPDT_DATE"));
		map.addValue("CUST_BANK_AC_NO", rs.getObject("CUST_BANK_AC_NO"));
		map.addValue("CUSTOMER_BANK_BRANCH", rs.getObject("CUSTOMER_BANK_BRANCH"));
		map.addValue("CHEQUESNO", rs.getObject("CHEQUESNO"));
		map.addValue("CHEQUEDATE", rs.getObject("CHEQUEDATE"));
		map.addValue("FEMI_FLAG", rs.getObject("FEMI_FLAG"));
		map.addValue("HOLD_IGNORE_CODE", rs.getObject("HOLD_IGNORE_CODE"));
		map.addValue("HOLD_IGNORE_REASON", rs.getObject("HOLD_IGNORE_REASON"));
		map.addValue("DEST_ACC_HOLDER", rs.getObject("DEST_ACC_HOLDER"));
		map.addValue("PDCID", rs.getObject("PDCID"));
		map.addValue("BBRANCHID", rs.getObject("BBRANCHID"));

		return map;

	}
}
