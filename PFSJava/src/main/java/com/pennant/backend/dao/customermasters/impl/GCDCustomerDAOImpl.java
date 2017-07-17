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

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.GCDCustomerDAO;
import com.pennanttech.gcd.GcdCustomer;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>CustomerIncome model</b> class.<br>
 * 
 */
public class GCDCustomerDAOImpl implements GCDCustomerDAO {
	private static Logger logger = Logger.getLogger(GCDCustomerDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public GCDCustomerDAOImpl() {
		super();
	}

	@Override
	public void save(GcdCustomer gcdCustomer) {
		logger.debug("Entering");
		try {
			StringBuilder insertSql = new StringBuilder();
			insertSql.append(" Insert Into GCDCUSTOMERS");
			insertSql.append(" (FinCustId, SourceSystem, CustomerName, ConstId, IndustryId, CategoryId, Spousename,");
			insertSql.append(" IndvCorpFlag, FName, MName, Lname, DOB, Sex,");
			insertSql.append(
					" IncomeSource, YearsOfCurrJob, DOI, MpAkerId, MakerDate, AuthId, AuthDate, AccType, ApCcocatg,");
			insertSql.append(
					" DateLastUpdate, NationalId, PassportNo, Nationality, PanNo, RegionId, BankType, EntityFlag,");
			insertSql.append(
					" ContactPerson, CustSearchId, SectorId, FraudFlag, FraudScore, EmiCardElig, AddressDetail,");
			insertSql.append(" BankDetail, NomineeName, NomineeAddress, NomineeRelationship, Field9, Field10,");
			insertSql.append(
					" InsertUpdateFlag, StatusFromFinnOne, RejectionReason, FinnCustId, SfdcCustomerId, BranchId)");

			insertSql.append(
					" Values(:FinCustId, :SourceSystem, :CustomerName, :ConstId, :IndustryId, :CategoryId, :Spousename,");
			insertSql.append(" :IndvCorpFlag, :FName, :MName, :Lname, :DOB, :Sex,");
			insertSql.append(" :IncomeSource, :YearsOfCurrJob, :DOI, :MpAkerId, :MakerDate, :AuthId,");
			insertSql.append(" :AuthDate, :AccType, :ApCcocatg, :DateLastUpdate, :NationalId, :PassportNo,");
			insertSql.append(" :Nationality, :PanNo, :RegionId, :BankType, :EntityFlag,");
			insertSql.append(" :ContactPerson, :CustSearchId, :SectorId, :FraudFlag, :FraudScore,");
			insertSql.append(
					" :EmiCardElig, :AddressDetail, :BankDetail, :NomineeName, :NomineeAddress, :NomineeRelationship,");
			insertSql.append(
					" :Field9, :Field10, :InsertUpdateFlag, :StatusFromFinnOne, :RejectionReason, :FinnCustId, :SfdcCustomerId, :BranchId)");

			logger.trace(Literal.SQL + insertSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gcdCustomer);
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			return;
		}
		logger.debug("Leaving");

	}
	
	@Override
	public GcdCustomer getgcdCustomerbyFinOneID(String finCustId) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder("Select * ");
		sql.append(" From GcdCustomers  Where FinCustId =:FinCustId");
		logger.debug("selectSql: " + sql.toString());

		RowMapper<GcdCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(GcdCustomer.class);

		source = new MapSqlParameterSource();
		source.addValue("FinCustId", finCustId);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		} finally {
			source = null;
			sql = null;
		}
		logger.debug("Leaving");
		return null;
	}
	
/*
	@Override
	public void callStoredProcedure(GcdCustomer gcdCustomer) {
		// SqlParameterSource inParam = new MapSqlParameterSource();
		MapSqlParameterSource inParam = new MapSqlParameterSource();
		inParam.addValue("P_FINN_CUSTID", gcdCustomer.getFinCustId());
		inParam.addValue("P_SOURCE_SYSTEM", gcdCustomer.getSourceSystem());
		inParam.addValue("P_CUSTOMERNAME", gcdCustomer.getCustomerName());
		inParam.addValue("P_CONSTID", gcdCustomer.getConstId());
		inParam.addValue("P_INDUSTRYID", gcdCustomer.getIndustryId());
		inParam.addValue("P_CATEGORYID", gcdCustomer.getCategoryId());
		inParam.addValue("P_SPOUSENAME", gcdCustomer.getSpousename());
		inParam.addValue("P_INDV_CORP_FLAG", gcdCustomer.getIndvCorpFlag());
		inParam.addValue("P_FNAME", gcdCustomer.getfName());
		inParam.addValue("P_MNAME", gcdCustomer.getmName());
		inParam.addValue("P_LNAME", gcdCustomer.getLname());
		inParam.addValue("P_DOB", gcdCustomer.getDOB());
		inParam.addValue("P_SEX", gcdCustomer.getSex());
		inParam.addValue("P_P_INCOME_SOURCE", gcdCustomer.getIncomeSource());
		inParam.addValue("P_YEARS_CURR_JOB", gcdCustomer.getYearsOfCurrJob());
		inParam.addValue("P_COR_DOI", gcdCustomer.getDOI());
		inParam.addValue("P_MP_AKERID", gcdCustomer.getMpAkerId());
		inParam.addValue("P_MAKERDATE", gcdCustomer.getMakerDate());
		inParam.addValue("P_P_AUTHID", gcdCustomer.getAuthId());
		inParam.addValue("P_AUTHDATE", gcdCustomer.getAuthDate());
		inParam.addValue("P_ACCOTYPE", gcdCustomer.getAccType());
		inParam.addValue("P_AP_CCOCATG", gcdCustomer.getApCcocatg());
		inParam.addValue("P_DATELASTUPDT", gcdCustomer.getDateLastUpdate());
		inParam.addValue("P_P_NATIONALID", gcdCustomer.getNationalId());
		inParam.addValue("P_PASSPORTNO", gcdCustomer.getPassportNo());
		inParam.addValue("P_NATIONALITY", gcdCustomer.getNationality());
		inParam.addValue("P_PP_AN_NO", gcdCustomer.getPanNo());
		inParam.addValue("P_REGIONID", gcdCustomer.getRegionId());
		inParam.addValue("P_BANK_TYPE", gcdCustomer.getBankType());
		inParam.addValue("P_ENTITYFLAG", gcdCustomer.getEntityFlag());
		inParam.addValue("P_CONTACT_PERSON", gcdCustomer.getContactPerson());
		inParam.addValue("P_CUSTSEARCHID", gcdCustomer.getCustSearchId());
		inParam.addValue("P_ECONOMIC_SEC_ID", gcdCustomer.getSectorId());
		inParam.addValue("P_FRAUD_FLAG", gcdCustomer.getFraudFlag());
		inParam.addValue("P_FRAUD_SCORE", gcdCustomer.getFraudScore());
		inParam.addValue("P_EMI_CARD_ELIG", gcdCustomer.getEmiCardElig());
		inParam.addValue("P_ADDRESS_DTL", gcdCustomer.getAddressDetail());
		inParam.addValue("P_BANK_DTL", gcdCustomer.getBankDetail());
		inParam.addValue("P_N_NAME", gcdCustomer.getNomineeName());
		inParam.addValue("P_N_ADDRESS", gcdCustomer.getNomineeAddress());
		inParam.addValue("P_N_RELATION", gcdCustomer.getNomineeRelationship());
		inParam.addValue("P_N_FIELD9", gcdCustomer.getField9());
		inParam.addValue("P_N_FIELD10", gcdCustomer.getField10());
		inParam.addValue("P_INS_UPD_FLAG", gcdCustomer.getInsertUpdateFlag());
		inParam.addValue("P_SUCCESS_REJECT", gcdCustomer.getStatusFromFinnOne());
		inParam.addValue("P_REJECTION_REASON", gcdCustomer.getRejectionReason());
		inParam.addValue("P_FINN_CUST_ID", gcdCustomer.getFinnCustId());
		inParam.addValue("P_SFDC_CUSTOMERID", gcdCustomer.getSfdcCustomerId());
		inParam.addValue("P_BRANCHID", gcdCustomer.getBranchId());
		final String procedure = "{call GCD_CUSTOMER_INSERT_UPDATE(:P_FINN_CUSTID, :P_SOURCE_SYSTEM, :P_CUSTOMERNAME, :P_CONSTID, :P_INDUSTRYID, :P_CATEGORYID, :P_SPOUSENAME, :P_INDV_CORP_FLAG, :P_FNAME, :P_MNAME, :P_LNAME,"
				+ " :P_DOB, :P_SEX, :P_P_INCOME_SOURCE, :P_YEARS_CURR_JOB, :P_COR_DOI, :P_MP_AKERID, :P_MAKERDATE, :P_P_AUTHID, :P_AUTHDATE, :P_ACCOTYPE, :P_AP_CCOCATG,"
				+ " :P_DATELASTUPDT, :P_P_NATIONALID, :P_PASSPORTNO, :P_NATIONALITY, :P_PP_AN_NO, :P_REGIONID, :P_BANK_TYPE, :P_ENTITYFLAG, :P_CONTACT_PERSON, :P_CUSTSEARCHID,"
				+ " :P_ECONOMIC_SEC_ID, :P_FRAUD_FLAG, :P_FRAUD_SCORE, :P_EMI_CARD_ELIG, :P_ADDRESS_DTL, :P_BANK_DTL, :P_N_NAME, :P_N_ADDRESS, :P_N_RELATION, :P_N_FIELD9,"
				+ " :P_N_FIELD10, :P_INS_UPD_FLAG, :P_SUCCESS_REJECT, :P_REJECTION_REASON, :P_FINN_CUST_ID, :P_SFDC_CUSTOMERID, :P_BRANCHID)}"; // not
																																				// using

		logger.trace(Literal.SQL + procedure.toString());
		try {
			Map<String, Object> outParam = simpleJdbcCall.execute(inParam);

			System.out.println((String) outParam.get("P_SUCCESS_REJECT")); // result from Sproc
			System.out.println((String) outParam.get("P_REJECTION_REASON")); // result from Sproc
			System.out.println((String) outParam.get("P_FINN_CUST_ID"));// result from Sproc

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
		updateSql
				.append("UPDATE GCDCUSTOMERS SET StatusFromFinnOne = :StatusFromFinnOne, RejectionReason = :RejectionReason, FinnCustId = :FinnCustId, IsSuccess = 1");
		updateSql.append(" Where ConstId = :ConstId");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gcdCustomer);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}*/

	@Override
	public List<GcdCustomer> fetchFailedGcdCustomers() {
		logger.debug("Entering");

		List<GcdCustomer> list = new ArrayList<GcdCustomer>();
		GcdCustomer gcdCustomer = new GcdCustomer();

		StringBuilder sql = new StringBuilder();
		sql.append(" Select");
		sql.append(" FinCustId, SourceSystem, CustomerName, IndustryId, CategoryId, Spousename,");
		sql.append(" IndvCorpFlag, FName, MName, Lname, DOB, Sex,");
		sql.append(" IncomeSource, YearsOfCurrJob, DOI, MpAkerId, MakerDate, AuthId, AuthDate, AccType, ApCcocatg,");
		sql.append(" DateLastUpdate, NationalId, PassportNo, Nationality, PanNo, RegionId, BankType, EntityFlag,");
		sql.append(" ContactPerson, CustSearchId, SectorId, FraudFlag, FraudScore, EmiCardElig, AddressDetail,");
		sql.append(" BankDetail, NomineeName, NomineeAddress, NomineeRelationship, Field9, Field10,");
		sql.append(" InsertUpdateFlag, StatusFromFinnOne, RejectionReason, FinnCustId, SfdcCustomerId, BranchId");
		sql.append(" FROM  GCDCUSTOMERS");
		sql.append(" Where IsSuccess = 0");

		logger.debug("selectSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gcdCustomer);

		try {
			list = this.namedParameterJdbcTemplate.queryForList(sql.toString(), beanParameters, GcdCustomer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			list = null;
		}
		logger.debug("Leaving");
		return list;
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}



}