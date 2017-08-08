package com.pennanttech.bajaj.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.gcd.GcdCustomer;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.App;

public class CustomerCurdOperationProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(CustomerCurdOperationProcess.class);

	public CustomerCurdOperationProcess(DataSource dataSource, long userId, Date valueDate) {
		super(dataSource, App.DATABASE.name(), userId, false, valueDate);

	}

	private static SimpleJdbcCall simpleJdbcCall = null;

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);
		if (simpleJdbcCall == null) {
			prepairJdbcTemplate();
		}
		MapSqlParameterSource parmMap;
		StringBuilder sql = getSql();

		parmMap = new MapSqlParameterSource();
		parmMap.addValue("IsSuccess", 0);
		parameterJdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Long>() {

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					MapSqlParameterSource map = null;
					processedCount++;
					try {
						map = mapData(rs);
						callProcedure(map);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.ENTERING);
						failedCount++;
					} finally {
						logger.debug(successCount + " record inserted out of " + processedCount + "in GCDCustomer");
					}
				}
				return totalRecords;
			}

		});
	}

	private StringBuilder getSql() {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select FinCustId, SourceSystem, CustomerName, constId, IndustryId, CategoryId, Spousename,");
		sql.append(" IndvCorpFlag, FName, MName, Lname, DOB, Sex,");
		sql.append(" IncomeSource, YearsOfCurrJob, DOI, MpAkerId, MakerDate, AuthId, AuthDate, AccType, ApCcocatg,");
		sql.append(" DateLastUpdate, NationalId, PassportNo, Nationality, PanNo, RegionId, BankType, EntityFlag,");
		sql.append(" ContactPerson, CustSearchId, SectorId, FraudFlag, FraudScore, EmiCardElig, AddressDetail,");
		sql.append(" BankDetail, NomineeName, NomineeAddress, NomineeRelationship, Field9, Field10,");
		sql.append(" InsertUpdateFlag, StatusFromFinnOne, RejectionReason, FinnCustId, SfdcCustomerId, BranchId");
		sql.append(" FROM  GCDCUSTOMERS Where IsSuccess = :IsSuccess");
		return sql;
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {

		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("P_FINN_CUSTID", rs.getObject("FinCustId"));
		map.addValue("P_SOURCE_SYSTEM", rs.getObject("SourceSystem"));
		map.addValue("P_CUSTOMERNAME", rs.getObject("CustomerName"));
		map.addValue("P_CONSTID", rs.getObject("constId"));
		map.addValue("P_INDUSTRYID", rs.getObject("IndustryId"));
		map.addValue("P_CATEGORYID", rs.getObject("CategoryId"));
		map.addValue("P_SPOUSENAME", rs.getObject("Spousename"));
		map.addValue("P_INDV_CORP_FLAG", rs.getObject("IndvCorpFlag"));
		map.addValue("P_FNAME", rs.getObject("FName"));
		map.addValue("P_MNAME", rs.getObject("MName"));
		map.addValue("P_LNAME", rs.getObject("Lname"));
		map.addValue("P_DOB", rs.getObject("DOB"));
		map.addValue("P_SEX", rs.getObject("Sex"));
		map.addValue("P_P_INCOME_SOURCE", rs.getObject("IncomeSource"));
		map.addValue("P_YEARS_CURR_JOB", rs.getObject("YearsOfCurrJob"));
		map.addValue("P_COR_DOI", rs.getObject("DOI"));
		map.addValue("P_MP_AKERID", rs.getObject("MpAkerId"));
		map.addValue("P_MAKERDATE", rs.getObject("MakerDate"));
		map.addValue("P_P_AUTHID", rs.getObject("AuthId"));
		map.addValue("P_AUTHDATE", rs.getObject("AuthDate"));
		map.addValue("P_ACCOTYPE", rs.getObject("AccType"));
		map.addValue("P_AP_CCOCATG", rs.getObject("ApCcocatg"));
		map.addValue("P_DATELASTUPDT", rs.getObject("DateLastUpdate"));
		map.addValue("P_P_NATIONALID", rs.getObject("NationalId"));
		map.addValue("P_PASSPORTNO", rs.getObject("PassportNo"));
		map.addValue("P_NATIONALITY", rs.getObject("Nationality"));
		map.addValue("P_PP_AN_NO", rs.getObject("PanNo"));
		map.addValue("P_REGIONID", rs.getObject("RegionId"));
		map.addValue("P_BANK_TYPE", rs.getObject("BankType"));
		map.addValue("P_ENTITYFLAG", rs.getObject("EntityFlag"));
		map.addValue("P_CONTACT_PERSON", rs.getObject("ContactPerson"));
		map.addValue("P_CUSTSEARCHID", rs.getObject("CustSearchId"));
		map.addValue("P_ECONOMIC_SEC_ID", rs.getObject("SectorId"));
		map.addValue("P_FRAUD_FLAG", rs.getObject("FraudFlag"));
		map.addValue("P_FRAUD_SCORE", rs.getObject("FraudScore"));
		map.addValue("P_EMI_CARD_ELIG", rs.getObject("EmiCardElig"));
		map.addValue("P_ADDRESS_DTL", rs.getObject("AddressDetail"));
		map.addValue("P_BANK_DTL", rs.getObject("BankDetail"));
		map.addValue("P_N_NAME", rs.getObject("NomineeName"));
		map.addValue("P_N_ADDRESS", rs.getObject("NomineeAddress"));
		map.addValue("P_N_RELATION", rs.getObject("NomineeRelationship"));
		map.addValue("P_N_FIELD9", rs.getObject("Field9"));
		map.addValue("P_N_FIELD10", rs.getObject("Field10"));
		map.addValue("P_INS_UPD_FLAG", rs.getObject("InsertUpdateFlag"));
		map.addValue("P_SUCCESS_REJECT", rs.getObject("StatusFromFinnOne"));
		map.addValue("P_REJECTION_REASON", rs.getObject("RejectionReason"));
		map.addValue("P_FINN_CUST_ID", rs.getObject("FinnCustId"));
		map.addValue("P_SFDC_CUSTOMERID", rs.getObject("SfdcCustomerId"));
		map.addValue("P_BRANCHID", rs.getObject("BranchId"));
		return map;
	}

	private void callProcedure(MapSqlParameterSource map) {
		logger.debug(Literal.ENTERING);
		GcdCustomer gcdCustomer = new GcdCustomer();
		try {
			Map<String, Object> outParam = simpleJdbcCall.execute(map);
			gcdCustomer.setConstId(Long.parseLong(String.valueOf(map.getValue("P_CONSTID"))));
			if (!outParam.isEmpty()) {
				gcdCustomer.setStatusFromFinnOne(String.valueOf(outParam.get("P_SUCCESS_REJECT")));
				gcdCustomer.setRejectionReason(String.valueOf(outParam.get("P_REJECTION_REASON")));
				gcdCustomer.setFinnCustId(String.valueOf(outParam.get("P_FINN_CUST_ID")));
				updateSuccessStatus(gcdCustomer);// updating success flag;
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			updateFailStatus(gcdCustomer.getConstId()); // updating failure flag
		}
		logger.debug(Literal.LEAVING);
	}

	public void updateFailStatus(long constId) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE GCDCUSTOMERS SET IsSuccess = 0");
		updateSql.append(" Where ConstId =:ConstId");

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("ConstId", constId);
		jdbcTemplate.update(updateSql.toString(), parameterSource);
		logger.debug("Leaving");
	}

	public void updateSuccessStatus(GcdCustomer gcdCustomer) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(
				"UPDATE GCDCUSTOMERS SET StatusFromFinnOne = :StatusFromFinnOne, RejectionReason = :RejectionReason, FinnCustId = :FinnCustId, IsSuccess = 1");
		updateSql.append(" Where ConstId = :ConstId");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gcdCustomer);
		jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	private void prepairJdbcTemplate() {
		if (config.isLocalDB()) {
			simpleJdbcCall = new SimpleJdbcCall(dataSource).withProcedureName("GCD_CUSTOMER_INSERT_UPDATE");
		} else {
			simpleJdbcCall = new SimpleJdbcCall(getDestinationDataSource())
					.withProcedureName("CREATE_CUSTOMER_IN_FINNONE");
		}
	}

	private DriverManagerDataSource getDestinationDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(config.getDriverClass());
		dataSource.setUrl(config.getUrl());
		dataSource.setUsername(config.getUserName());
		dataSource.setPassword(EncryptionUtil.decrypt(config.getPassword()));
		return dataSource;
	}

}
