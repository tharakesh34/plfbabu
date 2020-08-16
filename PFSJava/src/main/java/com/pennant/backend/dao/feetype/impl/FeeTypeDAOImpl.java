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
 * FileName    		:  FeeTypeDAOImpl.java                                                  * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-01-2017    														*
 *                                                                  						*
 * Modified Date    :  03-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-01-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.feetype.impl;

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
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.model.finance.FeeType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>FeeType</code> with set of CRUD operations.
 */

public class FeeTypeDAOImpl extends SequenceDao<FeeType> implements FeeTypeDAO {
	private static Logger logger = Logger.getLogger(FeeTypeDAOImpl.class);

	public FeeTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record FeeType details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FeeType
	 */
	@Override
	public FeeType getFeeTypeById(final long id, String type) {
		logger.debug("Entering");

		FeeType feeType = new FeeType();
		feeType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(
				" Select feeTypeID, feeTypeCode, feeTypeDesc, active, manualAdvice,refundable, AdviseType, AccountSetId, TaxComponent, TaxApplicable,");
		if (type.contains("View")) {
			selectSql.append(" AccountSetCode, AccountSetCodeName, 	DueAcctSetCode, DueAcctSetCodeName, ");
		}
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,HostFeeTypeCode, AmortzReq, DueAccReq, DueAccSet,TdsReq");
		selectSql.append(" From FeeTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FeeTypeID =:FeeTypeID");

		logger.debug("sql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		RowMapper<FeeType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FeeType.class);

		try {
			feeType = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			feeType = null;
		}

		logger.debug("Leaving");
		return feeType;
	}

	@Override
	public boolean isDuplicateKey(long feeTypeID, String feeTypeCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "FeeTypeCode = :feeTypeCode and FeeTypeID != :feeTypeID";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("FeeTypes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("FeeTypes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "FeeTypes_Temp", "FeeTypes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("feeTypeID", feeTypeID);
		paramSource.addValue("feeTypeCode", feeTypeCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(FeeType feeType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into FeeTypes");
		sql.append(tableType.getSuffix());
		sql.append(
				" (feeTypeID, feeTypeCode, feeTypeDesc, manualAdvice, AdviseType, AccountSetId, active, TaxComponent, TaxApplicable,refundable,");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,HostFeeTypeCode,  AmortzReq, DueAccReq, DueAccSet,TdsReq)");
		sql.append(" values(");
		sql.append(
				" :feeTypeID, :feeTypeCode, :feeTypeDesc, :manualAdvice, :AdviseType, :AccountSetId, :active, :TaxComponent, :TaxApplicable, :refundable,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId,:HostFeeTypeCode, :AmortzReq ,:DueAccReq, :DueAccSet,:TdsReq)");

		// Get the identity sequence number.
		if (feeType.getId() == Long.MIN_VALUE) {
			feeType.setId(getNextId("SeqFeeTypes"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(feeType);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug("Leaving");
		return String.valueOf(feeType.getFeeTypeID());
	}

	@Override
	public void update(FeeType feeType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("update FeeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" set feeTypeCode=:feeTypeCode,feeTypeDesc=:feeTypeDesc,");
		sql.append(" active=:active,");
		sql.append(
				" manualAdvice = :manualAdvice, AdviseType = :AdviseType, AccountSetId = :AccountSetId, TaxComponent = :TaxComponent, TaxApplicable = :TaxApplicable,refundable =:refundable,");
		sql.append(
				" Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(
				" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType,");

		sql.append(
				" WorkflowId = :WorkflowId,HostFeeTypeCode=:HostFeeTypeCode, AmortzReq = :AmortzReq ,DueAccReq =:DueAccReq, DueAccSet =:DueAccSet,TdsReq =:TdsReq");

		sql.append(" where FeeTypeID =:FeeTypeID");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		int recordCount = jdbcTemplate.update(sql.toString(), beanParameters);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(FeeType feeType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete From FeeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" where FeeTypeID =:FeeTypeID");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Fetch the Record FeeType details by Fee code
	 * 
	 * @param feeTypeCode
	 *            (String)
	 * @return FeeType
	 */
	@Override
	public FeeType getApprovedFeeTypeByFeeCode(String feeTypeCode) {

		FeeType feeType = new FeeType();
		feeType.setFeeTypeCode(feeTypeCode);
		StringBuilder sql = new StringBuilder();

		sql.append("select FeeTypeID, FeeTypeCode, FeeTypeDesc, Active, ManualAdvice, AdviseType, AccountSetId");
		sql.append(
				", HostFeeTypeCode, AmortzReq, TaxApplicable, TaxComponent, Refundable, DueAccReq ,DueAccSet ,TdsReq");
		sql.append(" From FeeTypes");
		sql.append(" Where FeeTypeCode = :FeeTypeCode");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		RowMapper<FeeType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FeeType.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public int getAccountingSetIdCount(long accountSetId, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select Count(*) From FeeTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountSetId = :AccountSetId");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("AccountSetId", accountSetId);

		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}

		logger.debug("Leaving");

		return count;
	}

	@Override
	public long getFinFeeTypeIdByFeeType(String feeTypeCode, String type) {
		logger.debug("Entering");

		long feeTypeId = Long.MIN_VALUE;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT feeTypeID From FeeTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE FeeTypeCode = :FeeTypeCode ");

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FeeTypeCode", feeTypeCode);

		try {
			feeTypeId = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			feeTypeId = Long.MIN_VALUE;
		}

		logger.debug("Leaving");

		return feeTypeId;
	}

	/**
	 * Fetch the Record FeeType details by FeeTypeCode field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FeeType
	 */
	@Override
	public FeeType getTaxDetailByCode(final String feeTypeCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TaxComponent, TaxApplicable, AmortzReq, AccountSetId");
		sql.append(", FeeTypeCode, FeeTypeDesc, Refundable, TdsReq");
		sql.append(" From FeeTypes");
		sql.append(" Where FeeTypeCode = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), new Object[] { feeTypeCode },
					new RowMapper<FeeType>() {

						@Override
						public FeeType mapRow(ResultSet rs, int arg1) throws SQLException {
							FeeType f = new FeeType();

							f.setTaxComponent(rs.getString("TaxComponent"));
							f.setTaxApplicable(rs.getBoolean("TaxApplicable"));
							f.setAmortzReq(rs.getBoolean("AmortzReq"));
							f.setAccountSetId(rs.getLong("AccountSetId"));
							f.setFeeTypeCode(rs.getString("FeeTypeCode"));
							f.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
							f.setrefundable(rs.getBoolean("Refundable"));
							f.setTdsReq(rs.getBoolean("TdsReq"));

							return f;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public String getTaxCompByCode(String feeTypeCode) {
		logger.debug("Entering");

		String taxType = null;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT TaxComponent From FeeTypes");
		selectSql.append(" WHERE FeeTypeCode = :FeeTypeCode ");

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FeeTypeCode", feeTypeCode);

		try {
			taxType = this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			taxType = null;
		}

		logger.debug("Leaving");
		return taxType;
	}

	@Override
	public List<FeeType> getManualAdviseFeeType(int adviceType, String type) {
		logger.debug(Literal.ENTERING);

		FeeType feeType = new FeeType();
		feeType.setAdviseType(adviceType);

		StringBuilder selectSql = new StringBuilder("Select FeeTypeID, FeeTypeCode, FeeTypeDesc, Active,");
		selectSql.append(" ManualAdvice, AdviseType, AccountSetId, HostFeeTypeCode, AmortzReq, TaxApplicable, ");
		selectSql.append(" TaxComponent,refundable ,DueAccReq ,DueAccSet ,TdsReq  From FeeTypes");
		selectSql.append(type);
		selectSql.append(" Where AdviseType = :AdviseType AND ManualAdvice=1 AND Active=1");

		logger.debug("sql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		RowMapper<FeeType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FeeType.class);

		try {
			return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public long getFeeTypeId(String feeTypeCode) {
		logger.debug(Literal.ENTERING);

		long feeTypeId = Long.MIN_VALUE;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FeeTypeID From FeeTypes");
		selectSql.append(" WHERE FeeTypeCode = :FeeTypeCode ");

		logger.trace(Literal.SQL + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FeeTypeCode", feeTypeCode);

		try {
			feeTypeId = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
		}

		logger.debug(Literal.LEAVING);
		return feeTypeId;
	}

	@Override
	public boolean isFeeTypeAmortzReq(String feeTypeCode) {
		logger.debug(Literal.ENTERING);

		boolean isAmortzReq = false;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT AmortzReq From FeeTypes");
		selectSql.append(" WHERE FeeTypeCode = :FeeTypeCode ");

		logger.trace(Literal.SQL + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FeeTypeCode", feeTypeCode);

		try {
			isAmortzReq = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Boolean.class);
		} catch (EmptyResultDataAccessException e) {
		}

		logger.debug(Literal.LEAVING);
		return isAmortzReq;
	}

	@Override
	public List<FeeType> getAMZReqFeeTypes() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FeeTypeID, FeeTypeCode, FeeTypeDesc, Active, ManualAdvice, AdviseType, AccountSetId");
		sql.append(", HostFeeTypeCode, AmortzReq, TaxApplicable, TaxComponent, Refundable, DueAccReq");
		sql.append(", DueAccSet, TdsReq");
		sql.append(" from FeeTypes");
		sql.append(" Where AmortzReq = ? and Active = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setBoolean(1, true);
					ps.setBoolean(2, true);
				}
			}, new RowMapper<FeeType>() {
				@Override
				public FeeType mapRow(ResultSet rs, int rowNum) throws SQLException {
					FeeType fee = new FeeType();

					fee.setFeeTypeID(rs.getLong("FeeTypeID"));
					fee.setFeeTypeCode(rs.getString("FeeTypeCode"));
					fee.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
					fee.setActive(rs.getBoolean("Active"));
					fee.setManualAdvice(rs.getBoolean("ManualAdvice"));
					fee.setAdviseType(rs.getInt("AdviseType"));
					fee.setAccountSetId(rs.getLong("AccountSetId"));
					fee.setHostFeeTypeCode(rs.getString("HostFeeTypeCode"));
					fee.setAmortzReq(rs.getBoolean("AmortzReq"));
					fee.setTaxApplicable(rs.getBoolean("TaxApplicable"));
					fee.setTaxComponent(rs.getString("TaxComponent"));
					fee.setrefundable(rs.getBoolean("Refundable"));
					fee.setDueAccReq(rs.getBoolean("DueAccReq"));
					fee.setDueAccSet(rs.getLong("DueAccSet"));
					fee.setTdsReq(rs.getBoolean("TdsReq"));

					return fee;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

}