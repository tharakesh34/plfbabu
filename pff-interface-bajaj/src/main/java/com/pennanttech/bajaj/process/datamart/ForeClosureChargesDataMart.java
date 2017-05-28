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

public class ForeClosureChargesDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(ForeClosureChargesDataMart.class);

	public ForeClosureChargesDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
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
		sql.append(" SELECT * from FORECLOSURECHARGES_VIEW");

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
						saveOrUpdate(map, "FORECLOSURECHARGES", destinationJdbcTemplate, filterFields);
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
		map.addValue("BAL_PRINCIPAL", rs.getObject("BAL_PRINCIPAL"));
		map.addValue("RESEDUL_VALUE", rs.getObject("RESEDUL_VALUE"));
		map.addValue("INSTALLMENTS", rs.getObject("INSTALLMENTS"));
		map.addValue("ADVICES", rs.getObject("ADVICES"));
		map.addValue("PENALTY", rs.getObject("PENALTY"));
		map.addValue("INTREST_ON_TERMINATION", rs.getObject("INTREST_ON_TERMINATION"));
		map.addValue("FLOATINTREST_ON_TERMINATIO", rs.getObject("FLOATINTREST_ON_TERMINATIO"));
		map.addValue("OVER_DUES", rs.getObject("OVER_DUES"));
		map.addValue("CURRENT_OVERDUES", rs.getObject("CURRENT_OVERDUES"));
		map.addValue("CURRENT_WAVEOFFAMT", rs.getObject("CURRENT_WAVEOFFAMT"));
		map.addValue("OVER_DISTANCE_CHARGES", rs.getObject("OVER_DISTANCE_CHARGES"));
		map.addValue("INTREST_ON_TERMINATION_PER", rs.getObject("INTREST_ON_TERMINATION_PER"));
		map.addValue("INTREST_ACCRUALS", rs.getObject("INTREST_ACCRUALS"));
		map.addValue("EXCESS_AMOUNT", rs.getObject("EXCESS_AMOUNT"));
		map.addValue("EXCESS_REFUND", rs.getObject("EXCESS_REFUND"));
		map.addValue("ADVICE", rs.getObject("ADVICE"));
		map.addValue("REBATE", rs.getObject("REBATE"));
		map.addValue("ADVINSTL", rs.getObject("ADVINSTL"));
		map.addValue("EXCESSPRINPMNT", rs.getObject("EXCESSPRINPMNT"));
		map.addValue("SDAMT", rs.getObject("SDAMT"));
		map.addValue("SDINT", rs.getObject("SDINT"));
		map.addValue("EXCESS_INTREST_RATE", rs.getObject("EXCESS_INTREST_RATE"));
		map.addValue("VAT_ON_FORECLOSURE", rs.getObject("VAT_ON_FORECLOSURE"));
		map.addValue("UNDER_DISTANCE_CHARGES", rs.getObject("UNDER_DISTANCE_CHARGES"));
		map.addValue("NET_PAYBALE", rs.getObject("NET_PAYBALE"));
		map.addValue("WAIVEOFFAMOUNT", rs.getObject("WAIVEOFFAMOUNT"));
		map.addValue("ACTIVITY", rs.getObject("ACTIVITY"));
		map.addValue("AUTHORIZEDON", rs.getObject("AUTHORIZEDON"));
		map.addValue("COMMITMENT_FEE", rs.getObject("COMMITMENT_FEE"));
		map.addValue("ORIGINATION_FEE", rs.getObject("ORIGINATION_FEE"));
		map.addValue("PRE_EMI", rs.getObject("PRE_EMI"));
		map.addValue("BUSINESS_DATE", rs.getObject("BUSINESS_DATE"));
		map.addValue("CHEQUEID", rs.getObject("CHEQUEID"));
		map.addValue("STATUS", rs.getObject("STATUS"));
		map.addValue("INTEREST_WAIVE_OFF", rs.getObject("INTEREST_WAIVE_OFF"));
		map.addValue("BALANCE_PRIN_WAIVE_OFF", rs.getObject("BALANCE_PRIN_WAIVE_OFF"));
		map.addValue("INSTALLMET_INT_WAIVE_OFF", rs.getObject("INSTALLMET_INT_WAIVE_OFF"));
		map.addValue("WOFF_CURRMONTH_INT", rs.getObject("WOFF_CURRMONTH_INT"));
		map.addValue("WOFF_OVERDUE_CHARGE", rs.getObject("WOFF_OVERDUE_CHARGE"));
		map.addValue("WOFF_CHQBOUNCE_CHARGES", rs.getObject("WOFF_CHQBOUNCE_CHARGES"));
		map.addValue("WOFF_OTHERS", rs.getObject("WOFF_OTHERS"));
		map.addValue("INSTALLMENT_PRIN_WAIVE_OFF", rs.getObject("INSTALLMENT_PRIN_WAIVE_OFF"));
		map.addValue("WOFF_PARKING_CHARGES", rs.getObject("WOFF_PARKING_CHARGES"));
		map.addValue("TOT_OTHER_REPO_CHARGES", rs.getObject("TOT_OTHER_REPO_CHARGES"));
		map.addValue("WOFF_OTHER_REPO_CHARGES", rs.getObject("WOFF_OTHER_REPO_CHARGES"));
		map.addValue("TOT_REPOSESSION_CHARGES", rs.getObject("TOT_REPOSESSION_CHARGES"));
		map.addValue("WOFF_REPOSESSION_CHARGES", rs.getObject("WOFF_REPOSESSION_CHARGES"));
		map.addValue("TOT_PARKING_CHARGES", rs.getObject("TOT_PARKING_CHARGES"));

		return map;

	}
}
