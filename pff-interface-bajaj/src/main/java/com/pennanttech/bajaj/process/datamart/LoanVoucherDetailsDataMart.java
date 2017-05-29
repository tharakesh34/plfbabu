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

public class LoanVoucherDetailsDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(LoanVoucherDetailsDataMart.class);

	public LoanVoucherDetailsDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
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
		sql.append(" SELECT * from DM_LOAN_VOUCHER_DETAILS_VIEW");

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
						saveOrUpdate(map, "DM_LOAN_VOUCHER_DETAILS", destinationJdbcTemplate, filterFields);
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

		map.addValue("MODULEID CHAR()", rs.getObject("MODULEID CHAR()"));
		map.addValue("STAGEID CHAR()", rs.getObject("STAGEID CHAR()"));
		map.addValue("LEA_VOUCHERID CHAR()", rs.getObject("LEA_VOUCHERID CHAR()"));
		map.addValue("FA_VOUCHERID", rs.getObject("FA_VOUCHERID"));
		map.addValue("VOUCHERTYPE", rs.getObject("VOUCHERTYPE"));
		map.addValue("VOUCHERDATE", rs.getObject("VOUCHERDATE"));
		map.addValue("VALUEDATE", rs.getObject("VALUEDATE"));
		map.addValue("BRANCHID", rs.getObject("BRANCHID"));
		map.addValue("BRANCH_CODE", rs.getObject("BRANCH_CODE"));
		map.addValue("BRANCHDESC", rs.getObject("BRANCHDESC"));
		map.addValue("BUSINESS_AREA", rs.getObject("BUSINESS_AREA"));
		map.addValue("PROFIT_CENTRE", rs.getObject("PROFIT_CENTRE"));
		map.addValue("PRODUCT_FLAG", rs.getObject("PRODUCT_FLAG"));
		map.addValue("SCHEMEID", rs.getObject("SCHEMEID"));
		map.addValue("SCHEMEDESC", rs.getObject("SCHEMEDESC"));
		map.addValue("ASSIGNMENT", rs.getObject("ASSIGNMENT"));
		map.addValue("AGREEMENTID", rs.getObject("AGREEMENTID"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("AGREEMENTDATE", rs.getObject("AGREEMENTDATE"));
		map.addValue("DISBURSALDATE", rs.getObject("DISBURSALDATE"));
		map.addValue("LOAN_STATUS", rs.getObject("LOAN_STATUS"));
		map.addValue("NPA_STAGEID", rs.getObject("NPA_STAGEID"));
		map.addValue("FINNONE_GLID", rs.getObject("FINNONE_GLID"));
		map.addValue("GROUPGLDESC", rs.getObject("GROUPGLDESC"));
		map.addValue("SAPGL_CODE", rs.getObject("SAPGL_CODE"));
		map.addValue("COST_CENTRE", rs.getObject("COST_CENTRE"));
		map.addValue("DRAMT", rs.getObject("DRAMT"));
		map.addValue("CRAMT", rs.getObject("CRAMT"));
		map.addValue("DRCR_FLAG", rs.getObject("DRCR_FLAG"));
		map.addValue("DRCR_AMT", rs.getObject("DRCR_AMT"));
		map.addValue("NARRATION", rs.getObject("NARRATION"));
		map.addValue("CHEQUEID", rs.getObject("CHEQUEID"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROCESSDATE", rs.getObject("PROCESSDATE"));
		map.addValue("SEGMENT", rs.getObject("SEGMENT"));
		map.addValue("PROCESSED_FLAG", rs.getObject("PROCESSED_FLAG"));

		return map;

	}
}
