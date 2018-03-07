package com.pennanttech.bajaj.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.model.customermasters.Customer;
import com.pennanttech.gcd.GcdCustomer;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.customer.CustomerProcedure;

public class CustomerCurdOperationProcess{
	private static final Logger logger = Logger.getLogger(CustomerCurdOperationProcess.class);

	private DataSource dataSource;
	private DataSource finOneDataSource;
	NamedParameterJdbcTemplate jdbcTemplate;

	public CustomerCurdOperationProcess(DataSource dataSource, DataSource finOneDataSource) {
		this.dataSource = dataSource;
		this.finOneDataSource = finOneDataSource;
		jdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
	}
	

	public void process() {
		logger.debug(Literal.ENTERING);
		if ("Y".equalsIgnoreCase((String)getSMTParameter("GCD_FINONE_SCHDLR_STATE", String.class))) {
			MapSqlParameterSource parmMap;
			StringBuilder sql = getSql();

			parmMap = new MapSqlParameterSource();
			parmMap.addValue("StatusFromFinnOne", "P");
			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					Map<String, Object> map;
					try {
						while (rs.next()) {
							map = getcustomerData(rs);
							callProcedure(map, rs.getObject("Custid"));
						}
					} catch (Exception e) {
						logger.debug(e.getMessage());
					}
				}

			});
		}
	}

	private StringBuilder getSql() {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select FinCustId, SourceSystem, CustomerName, constId, IndustryId, CategoryId, Spousename,");
		sql.append(" IndvCorpFlag, FName, MName, Lname, DOB, Sex,");
		sql.append(" IncomeSource, YearsOfCurrJob, DOI, MpAkerId, MakerDate, AuthId, AuthDate, AccType, ApCcocatg,");
		sql.append(" DateLastUpdate, NationalId, PassportNo, Nationality, PanNo, RegionId, BankType, EntityFlag,");
		sql.append(" ContactPerson, CustSearchId, SectorId, FraudFlag, FraudScore, EmiCardElig, AddressDetail,");
		sql.append(" BankDetail, NomineeName, NomineeAddress, NomineeRelationship, Field9, Field10,");
		sql.append(" InsertUpdateFlag, StatusFromFinnOne, RejectionReason, FinnCustId, SfdcCustomerId, BranchId,Custid,RequestSeq");
		sql.append(" FROM  GCDCUSTOMERS Where StatusFromFinnOne = :StatusFromFinnOne");
		return sql;
	}

	protected Map<String, Object> getcustomerData(ResultSet rs) throws Exception {

		Map<String, Object> map = new LinkedHashMap<>();

		map.put("P_FINN_CUSTID", rs.getObject("FinCustId"));
		map.put("P_SOURCE_SYSTEM", rs.getObject("SourceSystem"));
		map.put("P_CUSTOMERNAME", rs.getObject("CustomerName"));
		map.put("P_CONSTID", rs.getObject("constId"));
		map.put("P_INDUSTRYID", rs.getObject("IndustryId"));
		map.put("P_CATEGORYID", rs.getObject("CategoryId"));
		map.put("P_SPOUSENAME", rs.getObject("Spousename"));
		map.put("P_INDV_CORP_FLAG", rs.getObject("IndvCorpFlag"));
		map.put("P_FNAME", rs.getObject("FName"));
		map.put("P_MNAME", rs.getObject("MName"));
		map.put("P_LNAME", rs.getObject("Lname"));
		map.put("P_DOB", rs.getObject("DOB"));
		map.put("P_SEX", rs.getObject("Sex"));
		map.put("P_INCOME_SOURCE", rs.getObject("IncomeSource"));
		map.put("P_YEARS_CURR_JOB", rs.getObject("YearsOfCurrJob"));
		map.put("P_COR_DOI", rs.getObject("DOI"));
		map.put("P_MAKERID", rs.getObject("MpAkerId"));
		map.put("P_MAKERDATE", rs.getObject("MakerDate"));
		map.put("P_AUTHID", rs.getObject("AuthId"));
		map.put("P_AUTHDATE", rs.getObject("AuthDate"));
		map.put("P_ACCOTYPE", rs.getObject("AccType"));
		map.put("P_ACCOCATG", rs.getObject("ApCcocatg"));
		map.put("P_DATELASTUPDT", rs.getObject("DateLastUpdate"));
		map.put("P_NATIONALID", rs.getObject("NationalId"));
		map.put("P_PASSPORTNO", rs.getObject("PassportNo"));
		map.put("P_NATIONALITY", rs.getObject("Nationality"));
		map.put("P_PAN_NO", rs.getObject("PanNo"));
		map.put("P_REGIONID", rs.getObject("RegionId"));
		map.put("P_BANK_TYPE", rs.getObject("BankType"));
		map.put("P_ENTITYFLAG", rs.getObject("EntityFlag"));
		map.put("P_CONTACT_PERSON", rs.getObject("ContactPerson"));
		map.put("P_CUSTSEARCHID", rs.getObject("CustSearchId"));
		map.put("P_ECONOMIC_SEC_ID", rs.getObject("SectorId"));
		map.put("P_FRAUD_FLAG", rs.getObject("FraudFlag"));
		map.put("P_FRAUD_SCORE", rs.getObject("FraudScore"));
		map.put("P_EMI_CARD_ELIG", rs.getObject("EmiCardElig"));
		map.put("P_ADDRESS_DTL", rs.getObject("AddressDetail"));
		map.put("P_BANK_DTL", rs.getObject("BankDetail"));
		map.put("P_N_NAME", rs.getObject("NomineeName"));
		map.put("P_N_ADDRESS", rs.getObject("NomineeAddress"));
		map.put("P_N_RELATION", rs.getObject("NomineeRelationship"));
		map.put("P_N_FIELD9", rs.getObject("Field9"));
		map.put("P_N_FIELD10", rs.getObject("Field10"));
		map.put("P_INS_UPD_FLAG", rs.getObject("InsertUpdateFlag"));
		map.put("P_SUCCESS_REJECT", rs.getObject("StatusFromFinnOne"));
		map.put("P_REJECTION_REASON", rs.getObject("RejectionReason"));
		map.put("P_FINN_CUST_ID", rs.getObject("FinnCustId"));
		map.put("P_SFDC_CUSTOMERID", rs.getObject("SfdcCustomerId"));
		map.put("P_BRANCHID", rs.getObject("BranchId"));
		return map;
	}

	private void callProcedure(Map<String, Object> map, Object custId) {
		logger.debug(Literal.ENTERING);
		GcdCustomer gcdCustomer = new GcdCustomer();

		gcdCustomer.setCustId(Long.valueOf(custId.toString()));
		try {

			CustomerProcedure customerproc = new CustomerProcedure(this.finOneDataSource, "CREATE_CUSTOMER_IN_FINNONE");
			customerproc.setQueryTimeout(60);
			Map<String, Object> outParam = customerproc.execute(map);
			if (!outParam.isEmpty()) {
				gcdCustomer.setStatusFromFinnOne(String.valueOf(outParam.get("P_SUCCESS_REJECT")));
				gcdCustomer.setRejectionReason(String.valueOf(outParam.get("P_REJECTION_REASON")));
				gcdCustomer.setFinnCustId(String.valueOf(outParam.get("P_FINN_CUST_ID")));
				updateSuccessStatus(gcdCustomer);// updating success flag;
				updateCustomer(gcdCustomer);// updating the customer if success.
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			// updating to status P in case timeout,so that scheduler will pick next time.
			gcdCustomer.setStatusFromFinnOne("P");
			if (e instanceof QueryTimeoutException) {
				gcdCustomer.setRejectionReason("Request Timed Out");
				updateFailStatus(gcdCustomer);
			} else {
				// updating to status P in case of any other error logging the reason.
				gcdCustomer.setRejectionReason(StringUtils.substring(e.getMessage(), 0, 50));
				updateFailStatus(gcdCustomer);
			}

		}
		logger.debug(Literal.LEAVING);
	}

	public void updateFailStatus(GcdCustomer gcdCustomer) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE GCDCUSTOMERS SET StatusFromFinnOne = :StatusFromFinnOne, RejectionReason = :RejectionReason,IsSuccess = 0");
		updateSql.append(" Where CustId =:CustId and RequestSeq =:RequestSeq");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gcdCustomer);
		jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	public void updateSuccessStatus(GcdCustomer gcdCustomer) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(
				"UPDATE GCDCUSTOMERS SET StatusFromFinnOne = :StatusFromFinnOne, RejectionReason = :RejectionReason, FinnCustId = :FinnCustId, IsSuccess = 1");
		updateSql.append(" Where CustId = :CustId and RequestSeq =:RequestSeq");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gcdCustomer);
		jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	public void updateCustomer(GcdCustomer gcdCustomer) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustCoreBank(gcdCustomer.getFinnCustId());
		customer.setCustID(gcdCustomer.getCustId());
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE CUSTOMERS SET CustCoreBank = :CustCoreBank Where custID = :custID");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	protected Object getSMTParameter(String sysParmCode, Class<?> type) {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT SYSPARMVALUE FROM SMTPARAMETERS where SYSPARMCODE = :SYSPARMCODE");
		paramMap.addValue("SYSPARMCODE", sysParmCode);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramMap, type);
		} catch (Exception e) {
			logger.error("The parameter code " + sysParmCode + " not configured.");
		} finally {
			paramMap = null;
			sql = null;
		}
		return null;
	}
}
