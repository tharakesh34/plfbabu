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

public class DisbursementDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(DisbursementDataMart.class);

	public DisbursementDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
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
		sql.append(" SELECT * from DM_DISB_DETAILS_DAILY_VIEW ");

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
						saveOrUpdate(map, "DM_DISB_DETAILS_DAILY", destinationJdbcTemplate, filterFields);
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

		map.addValue("DISBURSEDAMT", rs.getObject("DISBURSEDAMT"));
		map.addValue("DISB_STATUS", rs.getObject("DISB_STATUS"));
		map.addValue("FIRST_DUE_DATE", rs.getObject("FIRST_DUE_DATE"));
		map.addValue("GROSS_TENURE", rs.getObject("GROSS_TENURE"));
		map.addValue("NET_TENURE", rs.getObject("NET_TENURE"));
		map.addValue("MATURITYDATE", rs.getObject("MATURITYDATE"));
		map.addValue("EXPIRYDATE", rs.getObject("EXPIRYDATE"));
		map.addValue("NO_OF_ADV_INSTL", rs.getObject("NO_OF_ADV_INSTL"));
		map.addValue("ADV_EMI_AMT", rs.getObject("ADV_EMI_AMT"));
		map.addValue("EMI", rs.getObject("EMI"));
		map.addValue("REPAYMENT_MODE", rs.getObject("REPAYMENT_MODE"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("PROMOTIONID", rs.getObject("PROMOTIONID"));
		map.addValue("ICICI_LOMBARD", rs.getObject("ICICI_LOMBARD"));
		map.addValue("BAGIC", rs.getObject("BAGIC"));
		map.addValue("BALIC_CHARGES", rs.getObject("BALIC_CHARGES"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROCESSED_FLAG", rs.getObject("PROCESSED_FLAG"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("FEE", rs.getObject("FEE"));
		map.addValue("DEALER_SUBV", rs.getObject("DEALER_SUBV"));
		map.addValue("MANU_SUBV_DED", rs.getObject("MANU_SUBV_DED"));
		map.addValue("MANU_SUBV_NDED", rs.getObject("MANU_SUBV_NDED"));
		map.addValue("PREEMI", rs.getObject("PREEMI"));
		map.addValue("EXISTING_LANNO", rs.getObject("EXISTING_LANNO"));
		map.addValue("MORTGAGE_FEE", rs.getObject("MORTGAGE_FEE"));
		map.addValue("COMMITMENT_FEE", rs.getObject("COMMITMENT_FEE"));
		map.addValue("PROCESSING_FEE", rs.getObject("PROCESSING_FEE"));
		map.addValue("PRE_EMI_RECEIVABLE", rs.getObject("PRE_EMI_RECEIVABLE"));
		map.addValue("INSURANCE", rs.getObject("INSURANCE"));
		map.addValue("PAYMENTMODE", rs.getObject("PAYMENTMODE"));
		map.addValue("FREQ", rs.getObject("FREQ"));
		map.addValue("CHEQUENUM", rs.getObject("CHEQUENUM"));
		map.addValue("CUST_ACCT_NO", rs.getObject("CUST_ACCT_NO"));
		map.addValue("BANKNAME", rs.getObject("BANKNAME"));
		map.addValue("MICRCODE", rs.getObject("MICRCODE"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("EMI_CHARGE", rs.getObject("EMI_CHARGE"));
		map.addValue("PDC_CHARGE", rs.getObject("PDC_CHARGE"));
		map.addValue("IRR_PER", rs.getObject("IRR_PER"));
		map.addValue("FEE_WL", rs.getObject("FEE_WL"));
		map.addValue("ELC_CHARGE", rs.getObject("ELC_CHARGE"));
		map.addValue("CREDIT_VIDYA_FEES", rs.getObject("CREDIT_VIDYA_FEES"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;

	}
}
