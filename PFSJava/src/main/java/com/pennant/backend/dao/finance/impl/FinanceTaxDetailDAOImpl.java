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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

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

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, ApplicableFor, TaxCustId, TaxExempted, TaxNumber, AddrLine1, AddrLine2");
		sql.append(", AddrLine3, AddrLine4, Country, Province, City, PinCode, SezCertificateNo, SezValueDate");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CountryName, ProvinceName, CityName, PinCodeName, CustCIF, CustShrtName");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", pinCodeId");
		sql.append(" From FinTaxDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where finReference = ?");

		logger.trace(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<FinanceTaxDetail>() {
						@Override
						public FinanceTaxDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinanceTaxDetail ftd = new FinanceTaxDetail();

							ftd.setFinReference(rs.getString("FinReference"));
							ftd.setApplicableFor(rs.getString("ApplicableFor"));
							ftd.setTaxCustId(rs.getLong("TaxCustId"));
							ftd.setTaxExempted(rs.getBoolean("TaxExempted"));
							ftd.setTaxNumber(rs.getString("TaxNumber"));
							ftd.setAddrLine1(rs.getString("AddrLine1"));
							ftd.setAddrLine2(rs.getString("AddrLine2"));
							ftd.setAddrLine3(rs.getString("AddrLine3"));
							ftd.setAddrLine4(rs.getString("AddrLine4"));
							ftd.setCountry(rs.getString("Country"));
							ftd.setProvince(rs.getString("Province"));
							ftd.setCity(rs.getString("City"));
							ftd.setPinCode(rs.getString("PinCode"));
							ftd.setSezCertificateNo(rs.getString("SezCertificateNo"));
							ftd.setSezValueDate(rs.getTimestamp("SezValueDate"));
							ftd.setCountryName(rs.getString("CountryName"));
							ftd.setProvinceName(rs.getString("ProvinceName"));
							ftd.setCityName(rs.getString("CityName"));
							ftd.setPinCodeName(rs.getString("PinCodeName"));
							ftd.setCustCIF(rs.getString("CustCIF"));
							ftd.setCustShrtName(rs.getString("CustShrtName"));
							ftd.setVersion(rs.getInt("Version"));
							ftd.setLastMntOn(rs.getTimestamp("LastMntOn"));
							ftd.setLastMntBy(rs.getLong("LastMntBy"));
							ftd.setRecordStatus(rs.getString("RecordStatus"));
							ftd.setRoleCode(rs.getString("RoleCode"));
							ftd.setNextRoleCode(rs.getString("NextRoleCode"));
							ftd.setTaskId(rs.getString("TaskId"));
							ftd.setNextTaskId(rs.getString("NextTaskId"));
							ftd.setRecordType(rs.getString("RecordType"));
							ftd.setWorkflowId(rs.getLong("WorkflowId"));

							return ftd;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public String save(FinanceTaxDetail financeTaxDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into FinTaxDetail");
		sql.append(tableType.getSuffix());
		sql.append("(finReference, applicableFor,TaxCustId, taxExempted, taxNumber, addrLine1, addrLine2, ");
		sql.append(
				"addrLine3, addrLine4, country, province, city, pinCode, sezCertificateNo , sezValueDate , AddressDetail, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,");
		sql.append(" pinCodeId)");
		sql.append(" values(");
		sql.append(" :finReference, :applicableFor,:TaxCustId, :taxExempted, :taxNumber, :addrLine1, :addrLine2, ");
		sql.append(
				" :addrLine3, :addrLine4, :country, :province, :city, :pinCode,  :sezCertificateNo , :sezValueDate , :AddressDetail, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, ");
		sql.append(":pinCodeId)");

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
		sql.append(
				" city = :city, pinCode = :pinCode, sezCertificateNo = :sezCertificateNo , sezValueDate = :sezValueDate, AddressDetail = :AddressDetail, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, pinCodeId = :pinCodeId");
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
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

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
		/*
		 * if (recordCount == 0) { throw new ConcurrencyException(); }
		 */

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

	@Override
	public List<FinanceTaxDetail> getGSTNumberAndCustCIF(long taxCustId, String taxNumber, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TaxCustId, TaxNumber");
		sql.append(" From FinTaxDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where TaxCustId <> ? And TaxNumber = ?");
		logger.debug("selectSql: " + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, taxCustId);
					ps.setString(index++, taxNumber);
				}
			}, new RowMapper<FinanceTaxDetail>() {
				@Override
				public FinanceTaxDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceTaxDetail gd = new FinanceTaxDetail();

					gd.setTaxCustId(rs.getLong("TaxCustId"));
					gd.setTaxNumber(rs.getString("TaxNumber"));

					return gd;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
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
