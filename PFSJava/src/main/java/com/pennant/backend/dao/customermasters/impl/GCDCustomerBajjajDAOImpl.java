/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CustomerIncomeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters.impl;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.GCDCustomerBajjajDAO;
import com.pennanttech.gcd.GcdCustomer;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>CustomerIncome model</b> class.<br>
 * 
 */
public class GCDCustomerBajjajDAOImpl implements GCDCustomerBajjajDAO {
	private static Logger logger = Logger.getLogger(GCDCustomerBajjajDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private SimpleJdbcCall simpleJdbcCall;

	public GCDCustomerBajjajDAOImpl() {
		super();
		initSimpleJdbcCall();
	}

	@Override
	public void callStoredProcedure(GcdCustomer gcdCustomer) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource inParam = new MapSqlParameterSource();
		
		inParam.addValue("P_FINN_CUSTID", gcdCustomer.getFinCustId());
		logger.debug("P_FINN_CUSTID------------->"+gcdCustomer.getFinCustId());
		
		inParam.addValue("P_SOURCE_SYSTEM", gcdCustomer.getSourceSystem());
		logger.debug("P_SOURCE_SYSTEM------------->"+gcdCustomer.getSourceSystem());
		
		inParam.addValue("P_CUSTOMERNAME", gcdCustomer.getCustomerName());
		logger.debug("P_CUSTOMERNAME------------->"+gcdCustomer.getCustomerName());
		
		inParam.addValue("P_CONSTID", gcdCustomer.getConstId());
		logger.debug("P_CONSTID------------->"+gcdCustomer.getConstId());
		
		inParam.addValue("P_INDUSTRYID", gcdCustomer.getIndustryId());
		logger.debug("P_INDUSTRYID------------->"+gcdCustomer.getIndustryId());
		
		inParam.addValue("P_CATEGORYID", gcdCustomer.getCategoryId());
		logger.debug("P_CATEGORYID------------->"+gcdCustomer.getCategoryId());
		
		inParam.addValue("P_SPOUSENAME", gcdCustomer.getSpousename());
		logger.debug("P_SPOUSENAME------------->"+gcdCustomer.getSpousename());
		
		inParam.addValue("P_INDV_CORP_FLAG", gcdCustomer.getIndvCorpFlag());
		logger.debug("P_INDV_CORP_FLAG------------->"+gcdCustomer.getIndvCorpFlag());
		
		inParam.addValue("P_FNAME", gcdCustomer.getfName());
		logger.debug("P_FNAME------------->"+gcdCustomer.getfName());
		
		inParam.addValue("P_MNAME", gcdCustomer.getmName());
		logger.debug("P_MNAME------------->"+gcdCustomer.getmName());
		
		inParam.addValue("P_LNAME", gcdCustomer.getLname());
		logger.debug("P_LNAME------------->"+gcdCustomer.getLname());
		
		inParam.addValue("P_DOB", gcdCustomer.getDOB());
		logger.debug("P_DOB------------->"+gcdCustomer.getDOB());
		
		inParam.addValue("P_SEX", gcdCustomer.getSex());
		logger.debug("P_SEX------------->"+gcdCustomer.getSex());
		
		inParam.addValue("P_P_INCOME_SOURCE", gcdCustomer.getIncomeSource());
		logger.debug("P_P_INCOME_SOURCE------------->"+gcdCustomer.getIncomeSource());
		
		inParam.addValue("P_YEARS_CURR_JOB", gcdCustomer.getYearsOfCurrJob());
		logger.debug("P_YEARS_CURR_JOB------------->"+gcdCustomer.getYearsOfCurrJob());
		
		inParam.addValue("P_COR_DOI", gcdCustomer.getDOI());
		logger.debug("P_COR_DOI------------->"+gcdCustomer.getDOI());
		
		inParam.addValue("P_MP_AKERID", gcdCustomer.getMpAkerId());
		logger.debug("P_MP_AKERID------------->"+gcdCustomer.getMpAkerId());
		
		inParam.addValue("P_MAKERDATE", gcdCustomer.getMakerDate());
		logger.debug("P_MAKERDATE------------->"+gcdCustomer.getMakerDate());
		
		inParam.addValue("P_P_AUTHID", gcdCustomer.getAuthId());
		logger.debug("P_P_AUTHID------------->"+gcdCustomer.getAuthId());
		
		inParam.addValue("P_AUTHDATE", gcdCustomer.getAuthDate());
		logger.debug("P_AUTHDATE------------->"+gcdCustomer.getAuthDate());
		
		inParam.addValue("P_ACCOTYPE", gcdCustomer.getAccType());
		logger.debug("P_ACCOTYPE------------->"+gcdCustomer.getAccType());
		
		inParam.addValue("P_AP_CCOCATG", gcdCustomer.getApCcocatg());
		logger.debug("P_AP_CCOCATG------------->"+gcdCustomer.getApCcocatg());
		
		inParam.addValue("P_DATELASTUPDT", gcdCustomer.getDateLastUpdate());
		logger.debug("P_DATELASTUPDT------------->"+gcdCustomer.getDateLastUpdate());
		
		inParam.addValue("P_P_NATIONALID", gcdCustomer.getNationalId());
		logger.debug("P_P_NATIONALID------------->"+gcdCustomer.getNationalId());
		
		inParam.addValue("P_PASSPORTNO", gcdCustomer.getPassportNo());
		logger.debug("P_PASSPORTNO------------->"+gcdCustomer.getPassportNo());
		
		inParam.addValue("P_NATIONALITY", gcdCustomer.getNationality());
		logger.debug("P_NATIONALITY------------->"+gcdCustomer.getNationality());
		
		inParam.addValue("P_PP_AN_NO", gcdCustomer.getPanNo());
		logger.debug("P_PP_AN_NO------------->"+gcdCustomer.getPanNo());
		
		inParam.addValue("P_REGIONID", gcdCustomer.getRegionId());
		logger.debug("P_REGIONID------------->"+gcdCustomer.getRegionId());
		
		inParam.addValue("P_BANK_TYPE", gcdCustomer.getBankType());
		logger.debug("P_BANK_TYPE------------->"+gcdCustomer.getBankType());
		
		inParam.addValue("P_ENTITYFLAG", gcdCustomer.getEntityFlag());
		logger.debug("P_ENTITYFLAG------------->"+gcdCustomer.getEntityFlag());
		
		inParam.addValue("P_CONTACT_PERSON", gcdCustomer.getContactPerson());
		logger.debug("P_CONTACT_PERSON------------->"+gcdCustomer.getContactPerson());
		
		inParam.addValue("P_CUSTSEARCHID", gcdCustomer.getCustSearchId());
		logger.debug("P_CUSTSEARCHID------------->"+gcdCustomer.getCustSearchId());
		
		inParam.addValue("P_ECONOMIC_SEC_ID", gcdCustomer.getSectorId());
		logger.debug("P_ECONOMIC_SEC_ID------------->"+gcdCustomer.getSectorId());
		
		inParam.addValue("P_FRAUD_FLAG", gcdCustomer.getFraudFlag());
		logger.debug("P_FRAUD_FLAG------------->"+gcdCustomer.getFraudFlag());
		
		inParam.addValue("P_FRAUD_SCORE", gcdCustomer.getFraudScore());
		logger.debug("P_FRAUD_SCORE------------->"+gcdCustomer.getFraudScore());
		
		inParam.addValue("P_EMI_CARD_ELIG", gcdCustomer.getEmiCardElig());
		logger.debug("P_EMI_CARD_ELIG------------->"+gcdCustomer.getEmiCardElig());
		
		inParam.addValue("P_ADDRESS_DTL", gcdCustomer.getAddressDetail());
		logger.debug("P_ADDRESS_DTL------------->"+gcdCustomer.getAddressDetail());
		
		inParam.addValue("P_BANK_DTL", gcdCustomer.getBankDetail());
		logger.debug("P_BANK_DTL------------->"+gcdCustomer.getBankDetail());
		
		inParam.addValue("P_N_NAME", gcdCustomer.getNomineeName());
		logger.debug("P_N_NAME------------->"+gcdCustomer.getNomineeName());
		
		inParam.addValue("P_N_ADDRESS", gcdCustomer.getNomineeAddress());
		logger.debug("P_N_ADDRESS------------->"+gcdCustomer.getNomineeAddress());
		
		inParam.addValue("P_N_RELATION", gcdCustomer.getNomineeRelationship());
		logger.debug("P_N_RELATION------------->"+gcdCustomer.getNomineeRelationship());
		
		inParam.addValue("P_N_FIELD9", gcdCustomer.getField9());
		logger.debug("P_N_FIELD9------------->"+gcdCustomer.getField9());
		
		inParam.addValue("P_N_FIELD10", gcdCustomer.getField10());
		logger.debug("P_N_FIELD10------------->"+gcdCustomer.getField10());
		
		inParam.addValue("P_INS_UPD_FLAG", gcdCustomer.getInsertUpdateFlag());
		logger.debug("P_INS_UPD_FLAG------------->"+gcdCustomer.getInsertUpdateFlag());
		
		inParam.addValue("P_SUCCESS_REJECT", gcdCustomer.getStatusFromFinnOne());
		logger.debug("P_SUCCESS_REJECT------------->"+gcdCustomer.getStatusFromFinnOne());
		
		inParam.addValue("P_REJECTION_REASON", gcdCustomer.getRejectionReason());
		logger.debug("P_REJECTION_REASON------------->"+gcdCustomer.getRejectionReason());
		
		inParam.addValue("P_FINN_CUST_ID", gcdCustomer.getFinnCustId());
		logger.debug("P_FINN_CUST_ID------------->"+gcdCustomer.getFinnCustId());
		
		inParam.addValue("P_SFDC_CUSTOMERID", gcdCustomer.getSfdcCustomerId());
		logger.debug("P_SFDC_CUSTOMERID------------->"+gcdCustomer.getSfdcCustomerId());
		
		inParam.addValue("P_BRANCHID", gcdCustomer.getBranchId());
		logger.debug("P_BRANCHID------------->"+gcdCustomer.getBranchId());
		
		final String procedure = "{call CREATE_CUSTOMER_IN_FINNONE(:P_FINN_CUSTID, :P_SOURCE_SYSTEM, :P_CUSTOMERNAME, :P_CONSTID, :P_INDUSTRYID, :P_CATEGORYID, :P_SPOUSENAME, :P_INDV_CORP_FLAG, :P_FNAME, :P_MNAME, :P_LNAME,"
				+ " :P_DOB, :P_SEX, :P_P_INCOME_SOURCE, :P_YEARS_CURR_JOB, :P_COR_DOI, :P_MP_AKERID, :P_MAKERDATE, :P_P_AUTHID, :P_AUTHDATE, :P_ACCOTYPE, :P_AP_CCOCATG,"
				+ " :P_DATELASTUPDT, :P_P_NATIONALID, :P_PASSPORTNO, :P_NATIONALITY, :P_PP_AN_NO, :P_REGIONID, :P_BANK_TYPE, :P_ENTITYFLAG, :P_CONTACT_PERSON, :P_CUSTSEARCHID,"
				+ " :P_ECONOMIC_SEC_ID, :P_FRAUD_FLAG, :P_FRAUD_SCORE, :P_EMI_CARD_ELIG, :P_ADDRESS_DTL, :P_BANK_DTL, :P_N_NAME, :P_N_ADDRESS, :P_N_RELATION, :P_N_FIELD9,"
				+ " :P_N_FIELD10, :P_INS_UPD_FLAG, :P_SUCCESS_REJECT, :P_REJECTION_REASON, :P_FINN_CUST_ID, :P_SFDC_CUSTOMERID, :P_BRANCHID)}"; // not

		logger.trace(Literal.SQL + procedure.toString());
		try {
			Map<String, Object> outParam = simpleJdbcCall.execute(inParam);
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
		this.namedParameterJdbcTemplate.update(updateSql.toString(), parameterSource);
		logger.debug("Leaving");
	}

	public void updateSuccessStatus(GcdCustomer gcdCustomer) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(
				"UPDATE GCDCUSTOMERS SET StatusFromFinnOne = :StatusFromFinnOne, RejectionReason = :RejectionReason, FinnCustId = :FinnCustId, IsSuccess = 1");
		updateSql.append(" Where ConstId = :ConstId");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gcdCustomer);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	private void initSimpleJdbcCall() {
		this.simpleJdbcCall = new SimpleJdbcCall(getDataSource()).withProcedureName("CREATE_CUSTOMER_IN_FINNONE");
	}

	private DriverManagerDataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		/*dataSource.setDriverClassName(SysParamUtil.getValueAsString("GCD_CUSTOMER_DRIVER"));
		dataSource.setUrl(SysParamUtil.getValueAsString("GCD_CUSTOMER_URL"));
		dataSource.setUsername(SysParamUtil.getValueAsString("GCD_CUSTOMER_USERNAME"));
		dataSource.setPassword(SysParamUtil.getValueAsString("GCD_CUSTOMER_PWD"));
		*/
		
		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		dataSource.setUrl("jdbc:oracle:thin:@192.168.1.19:1521:orcl");
		dataSource.setUsername("PLFDEV");
		dataSource.setPassword("pff123");
		
		return dataSource;
	}
}