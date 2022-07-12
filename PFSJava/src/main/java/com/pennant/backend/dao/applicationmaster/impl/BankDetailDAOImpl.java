/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : BankDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified
 * Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>BankDetail model</b> class.<br>
 * 
 */
public class BankDetailDAOImpl extends BasicDao<BankDetail> implements BankDetailDAO {
	private static Logger logger = LogManager.getLogger(BankDetailDAOImpl.class);

	public BankDetailDAOImpl() {
		super();
	}

	@Override
	public BankDetail getBankDetailByIfsc(String ifsc) {
		BankDetail bankDetail = new BankDetail();
		bankDetail.setIfsc(ifsc);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select branch.branchdesc bankBranch,bank.bankname bankName from BankBranches branch");
		selectSql.append(" left join BMTBankDetail bank on");
		selectSql.append(" branch.bankcode=bank.bankcode where ifsc=:ifsc");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankDetail);
		RowMapper<BankDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(BankDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public BankDetail getBankDetailById(final String id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BankCode, BankName, BankShortCode, Active, AccNoLength, MinAccNoLength, Version");
		sql.append(", LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" from BMTBankDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				BankDetail bd = new BankDetail();

				bd.setBankCode(rs.getString("BankCode"));
				bd.setBankName(rs.getString("BankName"));
				bd.setBankShortCode(rs.getString("BankShortCode"));
				bd.setActive(rs.getBoolean("Active"));
				bd.setAccNoLength(rs.getInt("AccNoLength"));
				bd.setMinAccNoLength(rs.getInt("MinAccNoLength"));
				bd.setVersion(rs.getInt("Version"));
				bd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				bd.setLastMntBy(rs.getLong("LastMntBy"));
				bd.setRecordStatus(rs.getString("RecordStatus"));
				bd.setRoleCode(rs.getString("RoleCode"));
				bd.setNextRoleCode(rs.getString("NextRoleCode"));
				bd.setTaskId(rs.getString("TaskId"));
				bd.setNextTaskId(rs.getString("NextTaskId"));
				bd.setRecordType(rs.getString("RecordType"));
				bd.setWorkflowId(rs.getLong("WorkflowId"));

				return bd;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public boolean isDuplicateKey(String bankCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "BankCode = :bankCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTBankDetail", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTBankDetail_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BMTBankDetail_Temp", "BMTBankDetail" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("bankCode", bankCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(BankDetail bankDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into BMTBankDetail");
		sql.append(tableType.getSuffix());
		sql.append(" (BankCode, BankName, BankShortCode, Active,  AccNoLength, MinAccNoLength,");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(:BankCode, :BankName, :BankShortCode, :Active, :AccNoLength, :MinAccNoLength,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(bankDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return bankDetail.getId();
	}

	@Override
	public void update(BankDetail bankDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update BMTBankDetail");
		sql.append(tableType.getSuffix());
		sql.append(
				" set BankName = :BankName, BankShortCode = :BankShortCode, Active = :Active, AccNoLength = :AccNoLength, MinAccNoLength = :MinAccNoLength,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		sql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		sql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where BankCode =:BankCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(bankDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(BankDetail bankDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BMTBankDetail");
		sql.append(tableType.getSuffix());
		sql.append(" Where BankCode =:BankCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(bankDetail);
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
	public BankDetail getAccNoLengthByCode(String bankCode, String type) {
		logger.debug("Entering");

		BankDetail bankDetail = new BankDetail();
		bankDetail.setBankCode(bankCode);

		StringBuilder selectSql = new StringBuilder("Select AccNoLength, MinAccNoLength");

		selectSql.append(" From BMTBankDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankCode =:BankCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankDetail);
		RowMapper<BankDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(BankDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return bankDetail;
		}
	}

	@Override
	public String getBankCodeByName(String bankName) {
		logger.debug("Entering");

		BankDetail bankDetail = new BankDetail();
		bankDetail.setBankName(bankName);

		StringBuilder selectSql = new StringBuilder("Select BankCode");

		selectSql.append(" From BMTBankDetail");
		selectSql.append(" Where bankName =:bankName");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankDetail);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Ticket id:124998 return boolean value based on bankcode and active status exists in table .
	 * 
	 * @param bankCode
	 * @param type
	 * @param active
	 */
	@Override
	public boolean isBankCodeExits(String bankCode, String type, boolean active) {
		logger.debug("Entering");

		BankDetail bankDetail = new BankDetail();
		bankDetail.setBankCode(bankCode);
		bankDetail.setActive(active);

		StringBuilder selectSql = new StringBuilder("Select count(*) ");

		selectSql.append(" From BMTBankDetail");
		selectSql.append(type);
		selectSql.append(" Where BankCode =:BankCode and Active = :Active");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankDetail);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class) > 0;
	}

	@Override
	public boolean isBankCodeExits(String bankCode) {
		String sql = "Select BankCode From BMTBankDetail Where BankCode = ? and Active = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, String.class, bankCode, 1) != null;
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return false;
	}
}