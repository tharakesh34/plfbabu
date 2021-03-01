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
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>FinanceTaxDetail</code> with set of CRUD operations.
 */
public class FinanceTaxDetailDAOImpl extends BasicDao<FinanceTaxDetail> implements FinanceTaxDetailDAO {
	private static Logger logger = LogManager.getLogger(FinanceTaxDetailDAOImpl.class);

	public FinanceTaxDetailDAOImpl() {
		super();
	}

	@Override
	public FinanceTaxDetail getFinanceTaxDetail(String finReference, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, ApplicableFor, TaxCustId, TaxExempted, TaxNumber, AddrLine1, AddrLine2");
		sql.append(", AddrLine3, AddrLine4, Country, Province, City, PinCode, SezCertificateNo, SezValueDate");
		sql.append(", AddressDetail");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CountryName, ProvinceName, CityName, PinCodeName, CustCIF, CustShrtName");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId, PinCodeId");
		sql.append(" from FinTaxDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where finReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, (rs, rowNum) -> {
				FinanceTaxDetail td = new FinanceTaxDetail();

				td.setFinReference(rs.getString("FinReference"));
				td.setApplicableFor(rs.getString("ApplicableFor"));
				td.setTaxCustId(rs.getLong("TaxCustId"));
				td.setTaxExempted(rs.getBoolean("TaxExempted"));
				td.setTaxNumber(rs.getString("TaxNumber"));
				td.setAddrLine1(rs.getString("AddrLine1"));
				td.setAddrLine2(rs.getString("AddrLine2"));
				td.setAddrLine3(rs.getString("AddrLine3"));
				td.setAddrLine4(rs.getString("AddrLine4"));
				td.setCountry(rs.getString("Country"));
				td.setProvince(rs.getString("Province"));
				td.setCity(rs.getString("City"));
				td.setPinCode(rs.getString("PinCode"));
				td.setSezCertificateNo(rs.getString("SezCertificateNo"));
				td.setSezValueDate(rs.getTimestamp("SezValueDate"));
				td.setAddressDetail(rs.getString("AddressDetail"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					td.setCountryName(rs.getString("CountryName"));
					td.setProvinceName(rs.getString("ProvinceName"));
					td.setCityName(rs.getString("CityName"));
					td.setPinCodeName(rs.getString("PinCodeName"));
					td.setCustCIF(rs.getString("CustCIF"));
					td.setCustShrtName(rs.getString("CustShrtName"));
				}

				td.setVersion(rs.getInt("Version"));
				td.setLastMntOn(rs.getTimestamp("LastMntOn"));
				td.setLastMntBy(rs.getLong("LastMntBy"));
				td.setRecordStatus(rs.getString("RecordStatus"));
				td.setRoleCode(rs.getString("RoleCode"));
				td.setNextRoleCode(rs.getString("NextRoleCode"));
				td.setTaskId(rs.getString("TaskId"));
				td.setNextTaskId(rs.getString("NextTaskId"));
				td.setRecordType(rs.getString("RecordType"));
				td.setWorkflowId(rs.getLong("WorkflowId"));
				td.setPinCodeId((Long) rs.getObject("PinCodeId"));

				return td;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in FinTaxDetail{} table/view for the specified FinReference >> {}", type,
					finReference);
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
	public void update(FinanceTaxDetail ftd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FinTaxDetail");
		sql.append(tableType.getSuffix());
		sql.append(" set applicableFor = ?, TaxCustId= ?, TaxExempted = ?, TaxNumber = ?");
		sql.append(", AddrLine1 = ?, AddrLine2 = ?, AddrLine3 = ?, AddrLine4 = ?");
		sql.append(", Country = ?, Province = ?, City = ?, PinCode = ?, SezCertificateNo = ?");
		sql.append(", SezValueDate = ?, AddressDetail = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?, PinCodeId = ?");
		sql.append(" Where finReference = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.trace(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, ftd.getApplicableFor());
			ps.setLong(index++, ftd.getTaxCustId());
			ps.setBoolean(index++, ftd.isTaxExempted());
			ps.setString(index++, ftd.getTaxNumber());
			ps.setString(index++, ftd.getAddrLine1());
			ps.setString(index++, ftd.getAddrLine2());
			ps.setString(index++, ftd.getAddrLine3());
			ps.setString(index++, ftd.getAddrLine4());
			ps.setString(index++, ftd.getCountry());
			ps.setString(index++, ftd.getProvince());
			ps.setString(index++, ftd.getCity());
			ps.setString(index++, ftd.getPinCode());
			ps.setString(index++, ftd.getSezCertificateNo());
			ps.setDate(index++, JdbcUtil.getDate(ftd.getSezValueDate()));
			ps.setString(index++, ftd.getAddressDetail());
			ps.setTimestamp(index++, ftd.getLastMntOn());
			ps.setString(index++, ftd.getRecordStatus());
			ps.setString(index++, ftd.getRoleCode());
			ps.setString(index++, ftd.getNextRoleCode());
			ps.setString(index++, ftd.getTaskId());
			ps.setString(index++, ftd.getNextTaskId());
			ps.setString(index++, ftd.getRecordType());
			ps.setLong(index++, ftd.getWorkflowId());
			if ((Object) ftd.getPinCodeId() instanceof Long) {
				ps.setLong(index++, ftd.getPinCodeId());
			} else {
				ps.setNull(index++, Types.NULL);
			}

			ps.setString(index++, ftd.getFinReference());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index++, ftd.getLastMntOn());
			} else {
				ps.setInt(index++, ftd.getVersion() - 1);
			}

		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
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
