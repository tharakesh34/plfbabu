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

public class ApplicantDetailsDataMart extends DatabaseDataEngine implements Runnable {
	private static final Logger logger = Logger.getLogger(ApplicantDetailsDataMart.class);

	public ApplicantDetailsDataMart(DataSource dataSource, long userId, Date valueDate, Date appDate) {
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
		sql.append(" SELECT * from DM_APPLICANT_DETAILS_VIEW ");

		parmMap = new MapSqlParameterSource();

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {
			MapSqlParameterSource map = null;

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				String[] filterFields = new String[1];
				filterFields[0] = "CUSTOMERID";
				while (rs.next()) {

					try {
						map = mapData(rs);
						saveOrUpdate(map, "DM_APPLICANT_DETAILS", destinationJdbcTemplate, filterFields);
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
		map.addValue("CUST_TYPE", rs.getObject("CUST_TYPE"));
		map.addValue("PANNO", rs.getObject("PANNO"));
		map.addValue("ADDRESS1", rs.getObject("ADDRESS1"));
		map.addValue("ADDRESS2", rs.getObject("ADDRESS2"));
		map.addValue("ADDRESS3", rs.getObject("ADDRESS3"));
		map.addValue("ADDRESS4", rs.getObject("ADDRESS4"));
		map.addValue("CITY", rs.getObject("CITY"));
		map.addValue("STATE", rs.getObject("STATE"));
		map.addValue("COUNTRY", rs.getObject("COUNTRY"));
		map.addValue("ZIPCODE", rs.getObject("ZIPCODE"));
		map.addValue("ADDRESSTYPE", rs.getObject("ADDRESSTYPE"));
		map.addValue("EMAIL", rs.getObject("EMAIL"));
		map.addValue("PHONE1", rs.getObject("PHONE1"));
		map.addValue("PHONE2", rs.getObject("PHONE2"));
		map.addValue("MOBILE", rs.getObject("MOBILE"));
		map.addValue("FAX", rs.getObject("FAX"));
		map.addValue("EXISTING_CUST_FLAG", rs.getObject("EXISTING_CUST_FLAG"));
		map.addValue("INDIV_CORP_FLAG", rs.getObject("INDIV_CORP_FLAG"));
		map.addValue("AGE", rs.getObject("AGE"));
		map.addValue("DOB", rs.getObject("DOB"));
		map.addValue("FNAME", rs.getObject("FNAME"));
		map.addValue("MNAME", rs.getObject("MNAME"));
		map.addValue("LNAME", rs.getObject("LNAME"));
		map.addValue("GENDER", rs.getObject("GENDER"));
		map.addValue("MARITAL_STATUS", rs.getObject("MARITAL_STATUS"));
		map.addValue("NO_OF_DEPENDENT", rs.getObject("NO_OF_DEPENDENT"));
		map.addValue("YEARS_CURRENT_JOB", rs.getObject("YEARS_CURRENT_JOB"));
		map.addValue("YEARS_PREV_JOB", rs.getObject("YEARS_PREV_JOB"));
		map.addValue("QUALIFICATION", rs.getObject("QUALIFICATION"));
		map.addValue("RESIDENCETYPE", rs.getObject("RESIDENCETYPE"));
		map.addValue("YEARS_CURR_RESI", rs.getObject("YEARS_CURR_RESI"));
		map.addValue("EMPLOYER_DESC", rs.getObject("EMPLOYER_DESC"));
		map.addValue("COMPANY_TYPE", rs.getObject("COMPANY_TYPE"));
		map.addValue("INDUSTRYID", rs.getObject("INDUSTRYID"));
		map.addValue("NATURE_OF_BUSINESS", rs.getObject("NATURE_OF_BUSINESS"));
		map.addValue("EMPLOYMENT_TYPE", rs.getObject("EMPLOYMENT_TYPE"));
		map.addValue("EMPDESG", rs.getObject("EMPDESG"));
		map.addValue("OCCUPATION", rs.getObject("OCCUPATION"));
		map.addValue("ANNUAL_INCOME", rs.getObject("ANNUAL_INCOME"));
		map.addValue("GUARDIAN", rs.getObject("GUARDIAN"));
		map.addValue("BUSINESSDATE", rs.getObject("BUSINESSDATE"));
		map.addValue("PROCESSED_FLAG", rs.getObject("PROCESSED_FLAG"));
		map.addValue("PROCESS_DATE", rs.getObject("PROCESS_DATE"));
		map.addValue("SEGMENTS", rs.getObject("SEGMENTS"));
		map.addValue("CUSTOMERNAME", rs.getObject("CUSTOMERNAME"));
		map.addValue("CONTACT_PERSON_NAME", rs.getObject("CONTACT_PERSON_NAME"));
		map.addValue("CONSTITUTION", rs.getObject("CONSTITUTION"));
		map.addValue("CUST_BANK_NAME", rs.getObject("CUST_BANK_NAME"));
		map.addValue("CUST_BANK_BRANCH", rs.getObject("CUST_BANK_BRANCH"));
		map.addValue("EMI_CARD_LIMIT", rs.getObject("EMI_CARD_LIMIT"));
		map.addValue("EMI_CARD_ACCEPT_FLAG", rs.getObject("EMI_CARD_ACCEPT_FLAG"));
		map.addValue("EMI_CARD_SWIPE_FLAG", rs.getObject("EMI_CARD_SWIPE_FLAG"));
		map.addValue("EMI_CARD_ELIG", rs.getObject("EMI_CARD_ELIG"));
		map.addValue("EMI_CARD_NO", rs.getObject("EMI_CARD_NO"));
		map.addValue("BANK_ECS_MANDATE", rs.getObject("BANK_ECS_MANDATE"));
		map.addValue("OPEN_ECS_AVLB", rs.getObject("OPEN_ECS_AVLB"));
		map.addValue("OPEN_ECS_DATE", rs.getObject("OPEN_ECS_DATE"));
		map.addValue("BUSINESS_YEAR", rs.getObject("BUSINESS_YEAR"));
		map.addValue("TITLE", rs.getObject("TITLE"));
		map.addValue("COMP_NAME", rs.getObject("COMP_NAME"));
		map.addValue("YEARS_CURR_JOB", rs.getObject("YEARS_CURR_JOB"));
		map.addValue("GRADE", rs.getObject("GRADE"));
		map.addValue("FAMILY_CODE", rs.getObject("FAMILY_CODE"));
		map.addValue("MINOR", rs.getObject("MINOR"));
		map.addValue("GUARDIAN_NEW", rs.getObject("GUARDIAN_NEW"));
		map.addValue("UCIN_NO", rs.getObject("UCIN_NO"));
		map.addValue("PREFERRED_ELIGIBILITY", rs.getObject("PREFERRED_ELIGIBILITY"));
		map.addValue("PREFERRED_CARD_ACCEPTANCE", rs.getObject("PREFERRED_CARD_ACCEPTANCE"));
		map.addValue("PREFERRED_CARD_LIMIT", rs.getObject("PREFERRED_CARD_LIMIT"));
		map.addValue("CUST_BRANCHID", rs.getObject("CUST_BRANCHID"));
		map.addValue("BATCH_ID", rs.getObject("BATCH_ID"));

		return map;

	}

}
