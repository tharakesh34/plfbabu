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
 * FileName    		:  PSLDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-06-2018    														*
 *                                                                  						*
 * Modified Date    :  20-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.psl.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.psl.PSLDetailDAO;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>PSLDetail</code> with set of CRUD operations.
 */
public class PSLDetailDAOImpl extends BasicDao<PSLDetail> implements PSLDetailDAO {
	private static Logger logger = LogManager.getLogger(PSLDetailDAOImpl.class);

	public PSLDetailDAOImpl() {
		super();
	}

	@Override
	public PSLDetail getPSLDetail(String finReference, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, CategoryCode, WeakerSection, LandHolding, LandArea, Sector, Amount");
		sql.append(", SubCategory, Purpose, EndUse, LoanPurpose, EligibleAmount, Version, LastMntOn");
		sql.append(", LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", WeakerSectionName, PurposeName, EndUseName");
		}

		sql.append(" from PSLDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where finReference = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, (rs, rowNum) -> {
				PSLDetail psl = new PSLDetail();

				psl.setFinReference(rs.getString("FinReference"));
				psl.setCategoryCode(rs.getString("CategoryCode"));
				psl.setWeakerSection(rs.getString("WeakerSection"));
				psl.setLandHolding(rs.getString("LandHolding"));
				psl.setLandArea(rs.getString("LandArea"));
				psl.setSector(rs.getString("Sector"));
				psl.setAmount(rs.getDouble("Amount"));
				psl.setSubCategory(rs.getString("SubCategory"));
				psl.setPurpose(rs.getString("Purpose"));
				psl.setEndUse(rs.getString("EndUse"));
				psl.setLoanPurpose(rs.getString("LoanPurpose"));
				psl.setEligibleAmount(rs.getBigDecimal("EligibleAmount"));
				psl.setVersion(rs.getInt("Version"));
				psl.setLastMntOn(rs.getTimestamp("LastMntOn"));
				psl.setLastMntBy(rs.getLong("LastMntBy"));
				psl.setRecordStatus(rs.getString("RecordStatus"));
				psl.setRoleCode(rs.getString("RoleCode"));
				psl.setNextRoleCode(rs.getString("NextRoleCode"));
				psl.setTaskId(rs.getString("TaskId"));
				psl.setNextTaskId(rs.getString("NextTaskId"));
				psl.setRecordType(rs.getString("RecordType"));
				psl.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					psl.setWeakerSectionName(rs.getString("WeakerSectionName"));
					psl.setPurposeName(rs.getString("PurposeName"));
					psl.setEndUseName(rs.getString("EndUseName"));
				}

				return psl;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in PSLDetail{} table/view for the specified FinReference >> {}", type,
					finReference);
		}

		return null;
	}

	@Override
	public String save(PSLDetail pSLDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into PSLDetail");
		sql.append(tableType.getSuffix());
		sql.append("(finReference, categoryCode, weakerSection, landHolding, landArea, sector, ");
		sql.append(" amount, subCategory, purpose, endUse,");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, LoanPurpose, EligibleAmount)");
		sql.append(" values(");
		sql.append(" :finReference, :categoryCode, :weakerSection, :landHolding, :landArea, :sector, ");
		sql.append(" :amount, :subCategory, :purpose, :endUse, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :LoanPurpose, :EligibleAmount)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pSLDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(pSLDetail.getFinReference());
	}

	@Override
	public void update(PSLDetail pSLDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update PSLDetail");
		sql.append(tableType.getSuffix());
		sql.append("  set categoryCode = :categoryCode, weakerSection = :weakerSection, landHolding = :landHolding, ");
		sql.append(" landArea = :landArea, sector = :sector, amount = :amount, ");
		sql.append(" subCategory = :subCategory, purpose = :purpose, endUse = :endUse, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(
				" RecordType = :RecordType, WorkflowId = :WorkflowId, LoanPurpose = :LoanPurpose, EligibleAmount = :EligibleAmount");
		sql.append(" where finReference = :finReference ");
		//	sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pSLDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PSLDetail pSLDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from PSLDetail");
		sql.append(tableType.getSuffix());
		sql.append(" where finReference = :finReference ");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pSLDetail);
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
	public String getPslCategoryCodes(String pslcategory) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select code");
		sql.append(" From Pslcategory");
		sql.append(" Where code = ?");
		String categoryCode = "";

		logger.trace(Literal.SQL + sql.toString());
		try {
			categoryCode = this.jdbcOperations.queryForObject(sql.toString(), new Object[] { pslcategory },
					new RowMapper<String>() {

						@Override
						public String mapRow(ResultSet rs, int rowNum) throws SQLException {

							return rs.getString(1);
						}
					});
		} catch (EmptyResultDataAccessException dae) {
			return null;

		}
		logger.debug(Literal.LEAVING);
		return categoryCode;
	}

	@Override
	public int getWeakerSection(String weakerSectionCode) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("select count(*)");
		sql.append(" From PSLWeakerSection");
		sql.append(" Where code = :code");

		logger.debug("sql: " + sql.toString());
		source.addValue("code", weakerSectionCode);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);

		} catch (EmptyResultDataAccessException dae) {
			logger.debug(Literal.EXCEPTION, dae);
			return 0;
		}
	}

	@Override
	public int getEndUseCode(String endCode, String purposeCode) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("select count(*)");
		sql.append(" From pslenduse");
		sql.append(" Where code = :code and purposeCode = :purposeCode");

		logger.debug("sql: " + sql.toString());
		source.addValue("code", endCode);
		source.addValue("purposeCode", purposeCode);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);

		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;

		}
	}

	@Override
	public int getPurposeCount(String code, String categoryCode) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("select count(*)");
		sql.append(" From pslPurpose");
		sql.append(" Where code = :code and categoryCode = :categoryCode");
		logger.trace(Literal.SQL + sql.toString());

		source.addValue("code", code);
		source.addValue("categoryCode", categoryCode);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);

		} catch (EmptyResultDataAccessException e) {
			logger.debug(Literal.EXCEPTION, e);
			return 0;

		}
	}

}
