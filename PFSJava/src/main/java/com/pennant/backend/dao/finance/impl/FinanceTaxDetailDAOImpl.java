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
 * FileName    		:  FinanceTaxDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-06-2017    														*
 *                                                                  						*
 * Modified Date    :  17-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>FinanceTaxDetail</code> with set of CRUD operations.
 */
public class FinanceTaxDetailDAOImpl extends BasicDao<FinanceTaxDetail> implements FinanceTaxDetailDAO {
	private static Logger logger = Logger.getLogger(FinanceTaxDetailDAOImpl.class);

	public FinanceTaxDetailDAOImpl() {
		super();
	}

	@Override
	public FinanceTaxDetail getFinanceTaxDetail(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" finReference, applicableFor, TaxCustId, taxExempted, taxNumber, addrLine1, addrLine2");
		sql.append(", addrLine3, addrLine4, country, province, city, pinCode, sezCertificateNo , sezValueDate");
		if (type.contains("View")) {
			sql.append(", countryName, provinceName, cityName, pinCodeName, custCIF, custShrtName");
		}

		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From FinTaxDetail");
		sql.append(type);
		sql.append(" Where finReference = :finReference");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("finReference", finReference);

		RowMapper<FinanceTaxDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceTaxDetail.class);

		try {
			logger.debug(Literal.LEAVING);
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public String save(FinanceTaxDetail financeTaxDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into FinTaxDetail");
		sql.append(tableType.getSuffix());
		sql.append("(finReference, applicableFor,TaxCustId, taxExempted, taxNumber, addrLine1, addrLine2, ");
		sql.append("addrLine3, addrLine4, country, province, city, pinCode, sezCertificateNo , sezValueDate ,");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :finReference, :applicableFor,:TaxCustId, :taxExempted, :taxNumber, :addrLine1, :addrLine2, ");
		sql.append(" :addrLine3, :addrLine4, :country, :province, :city, :pinCode,  :sezCertificateNo , :sezValueDate ,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeTaxDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(financeTaxDetail.getFinReference());
	}

	@Override
	public void update(FinanceTaxDetail financeTaxDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update FinTaxDetail");
		sql.append(tableType.getSuffix());
		sql.append(
				"  set applicableFor = :applicableFor,TaxCustId= :TaxCustId, taxExempted = :taxExempted, taxNumber = :taxNumber, ");
		sql.append(" addrLine1 = :addrLine1, addrLine2 = :addrLine2, addrLine3 = :addrLine3, ");
		sql.append(" addrLine4 = :addrLine4, country = :country, province = :province, ");
		sql.append(" city = :city, pinCode = :pinCode, sezCertificateNo = :sezCertificateNo , sezValueDate = :sezValueDate,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where finReference = :finReference ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeTaxDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(FinanceTaxDetail financeTaxDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from FinTaxDetail");
		sql.append(tableType.getSuffix());
		sql.append(" where finReference = :finReference ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeTaxDetail);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public int getGSTNumberCount(long taxCustId, String taxNumber, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select count(TaxNumber) From FinTaxDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where TaxCustId <> :TaxCustId And TaxNumber = :TaxNumber");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("TaxCustId", taxCustId);
		source.addValue("TaxNumber", taxNumber);

		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");

		return count;
	}

	public boolean isReferenceExists(String finReference, String custCif) {
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from FinTaxDetail_View ");
		sql.append(" Where FinReference = :FinReference AND  CustCif = :CustCif ");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("CustCif", custCif);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return false;
	}

	@Override
	public void deleteFinTaxDetails(FinanceTaxDetail financeTaxDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from FinTaxDetail");
		sql.append(tableType.getSuffix());
		sql.append(" where finReference = :finReference ");
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeTaxDetail);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public int getFinTaxDetailsCount(String finReference) {

		logger.debug(Literal.ENTERING);

		int recordCount = 0;

		StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM fintaxdetail_temp ");
		countQuery.append("WHERE finReference = :finReference");
		logger.trace(Literal.SQL + countQuery.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("finReference", finReference);

		try {
			recordCount = jdbcTemplate.queryForObject(countQuery.toString(), paramSource, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug(Literal.LEAVING);

		return recordCount;
	}

}
