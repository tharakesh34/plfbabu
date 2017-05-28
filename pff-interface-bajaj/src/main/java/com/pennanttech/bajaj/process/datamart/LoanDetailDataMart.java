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

public class LoanDetailDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(LoanDetailDataMart.class);

	public LoanDetailDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
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
		sql.append(" SELECT * from DM_LOAN_DETAILS_DAILY_VIEW ");

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
						saveOrUpdate(map, "DM_LOAN_DETAILS_DAILY", destinationJdbcTemplate, filterFields);
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
		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("CUSTOMER_YIELD", rs.getObject("CUSTOMER_YIELD"));
		map.addValue("STATUS", rs.getObject("STATUS"));
		map.addValue("NPA_STAGE", rs.getObject("NPA_STAGE"));
		map.addValue("LMS_BUCKET", rs.getObject("LMS_BUCKET"));
		map.addValue("COLL_BUCKET", rs.getObject("COLL_BUCKET"));
		map.addValue("INSURANCE_APPLIED_FLG", rs.getObject("INSURANCE_APPLIED_FLG"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROCESSED_FLAG", rs.getObject("PROCESSED_FLAG"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("CLOSUREDATE", rs.getObject("CLOSUREDATE"));
		map.addValue("TOPUP_AMT", rs.getObject("TOPUP_AMT"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("PDCID", rs.getObject("PDCID"));
		map.addValue("PCFLAG", rs.getObject("PCFLAG"));
		map.addValue("OD_FLAG", rs.getObject("OD_FLAG"));
		map.addValue("MARGIN", rs.getObject("MARGIN"));
		map.addValue("SPECIALMARGIN", rs.getObject("SPECIALMARGIN"));
		map.addValue("FIXEDTENOR", rs.getObject("FIXEDTENOR"));
		map.addValue("CEEFFECTIVEDATE", rs.getObject("CEEFFECTIVEDATE"));
		map.addValue("EFF_RATE", rs.getObject("EFF_RATE"));
		map.addValue("PLRRATE", rs.getObject("PLRRATE"));
		map.addValue("PARTY_CODE", rs.getObject("PARTY_CODE"));
		map.addValue("PARTY_NAME", rs.getObject("PARTY_NAME"));
		map.addValue("ZONE", rs.getObject("ZONE"));
		map.addValue("COLLECTION_CENTRE", rs.getObject("COLLECTION_CENTRE"));
		map.addValue("VIRTUAL_ACCOUNT_NUMBER", rs.getObject("VIRTUAL_ACCOUNT_NUMBER"));
		map.addValue("INSTALLMENT_TYPE", rs.getObject("INSTALLMENT_TYPE"));
		map.addValue("COMPANYTYPE", rs.getObject("COMPANYTYPE"));
		map.addValue("FIANANCE_CHARGES", rs.getObject("FIANANCE_CHARGES"));
		map.addValue("FILENO", rs.getObject("FILENO"));
		map.addValue("NO_OF_PDCS", rs.getObject("NO_OF_PDCS"));
		map.addValue("LIFEINSURANCE", rs.getObject("LIFEINSURANCE"));
		map.addValue("SHORTRECEIVED", rs.getObject("SHORTRECEIVED"));
		map.addValue("IN_FAVOUR_OFF", rs.getObject("IN_FAVOUR_OFF"));
		map.addValue("MKTGID", rs.getObject("MKTGID"));
		map.addValue("PRE_EMI_INT_500071", rs.getObject("PRE_EMI_INT_500071"));
		map.addValue("LOAN_PURPOSE_DTL", rs.getObject("LOAN_PURPOSE_DTL"));
		map.addValue("LOAN_PURPOSE_DESC", rs.getObject("LOAN_PURPOSE_DESC"));
		map.addValue("LOGIN_FEES", rs.getObject("LOGIN_FEES"));
		map.addValue("VC_REFERRAL_CD", rs.getObject("VC_REFERRAL_CD"));
		map.addValue("VC_REFERRAL_NAME", rs.getObject("VC_REFERRAL_NAME"));
		map.addValue("PROC_FEES2", rs.getObject("PROC_FEES2"));
		map.addValue("INSTRUMENT_TYPE", rs.getObject("INSTRUMENT_TYPE"));
		map.addValue("LAN_BARCODE", rs.getObject("LAN_BARCODE"));
		map.addValue("INTSTART_DATE_REGULAR", rs.getObject("INTSTART_DATE_REGULAR"));
		map.addValue("BPI_RECEIVABLE", rs.getObject("BPI_RECEIVABLE"));
		map.addValue("BPI_PAYABLE", rs.getObject("BPI_PAYABLE"));
		map.addValue("OPEN_FACILITY_FLAG", rs.getObject("OPEN_FACILITY_FLAG"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;

	}

}
