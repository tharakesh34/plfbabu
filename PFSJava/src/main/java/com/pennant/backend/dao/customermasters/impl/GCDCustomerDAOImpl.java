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
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.GCDCustomerDAO;
import com.pennanttech.gcd.GcdCustomer;
import com.pennanttech.pff.core.Literal;

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