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

public class CoApplicantDetailsDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(CoApplicantDetailsDataMart.class);

	public CoApplicantDetailsDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
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
		sql.append(" SELECT * from DM_COAPPLICANT_DTLS_VIEW ");

		parmMap = new MapSqlParameterSource();

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Long>() {
			MapSqlParameterSource map = null;

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				String[] filterFields = new String[1];
				filterFields[0] = "CUSTOMERID";
				while (rs.next()) {

					try {
						map = mapData(rs);
						saveOrUpdate(map, "DM_COAPPLICANT_DTLS", destinationJdbcTemplate, filterFields);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						String keyId = rs.getString("CUSTOMERID");
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

		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("APPLICANT_TYPE", rs.getObject("APPLICANT_TYPE"));
		map.addValue("PAN_NO", rs.getObject("PAN_NO"));
		map.addValue("CUST_RELATION", rs.getObject("CUST_RELATION"));
		map.addValue("CUST_TYPE", rs.getObject("CUST_TYPE"));
		map.addValue("AGE", rs.getObject("AGE"));
		map.addValue("DOB", rs.getObject("DOB"));
		map.addValue("FNAME", rs.getObject("FNAME"));
		map.addValue("MNAME", rs.getObject("MNAME"));
		map.addValue("LNAME", rs.getObject("LNAME"));
		map.addValue("GENDER", rs.getObject("GENDER"));
		map.addValue("MARITAL_STATUS", rs.getObject("MARITAL_STATUS"));
		map.addValue("NO_OF_DEPENDENT", rs.getObject("NO_OF_DEPENDENT"));
		map.addValue("YRS_CURRENTJOB", rs.getObject("YRS_CURRENTJOB"));
		map.addValue("PREVIOUS_JOB_YEAR", rs.getObject("PREVIOUS_JOB_YEAR"));
		map.addValue("QUALIFICATION", rs.getObject("QUALIFICATION"));
		map.addValue("EMPLOYER_DESC", rs.getObject("EMPLOYER_DESC"));
		map.addValue("COMPANY_TYPE", rs.getObject("COMPANY_TYPE"));
		map.addValue("INDUSTRYID", rs.getObject("INDUSTRYID"));
		map.addValue("BUSINESS_NATURE", rs.getObject("BUSINESS_NATURE"));
		map.addValue("OCCUPATION_CODE", rs.getObject("OCCUPATION_CODE"));
		map.addValue("GUARDIAN", rs.getObject("GUARDIAN"));
		map.addValue("PROCESS_FLAG", rs.getObject("PROCESS_FLAG"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("EMP_TYPE", rs.getObject("EMP_TYPE"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("INCOME", rs.getObject("INCOME"));
		map.addValue("APPLID", rs.getObject("APPLID"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;

	}
}
