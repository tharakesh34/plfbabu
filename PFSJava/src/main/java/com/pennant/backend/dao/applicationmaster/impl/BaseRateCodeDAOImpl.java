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
 * * FileName : BaseRateCodeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified
 * Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.BaseRateCodeDAO;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>BaseRateCode model</b> class.<br>
 */
public class BaseRateCodeDAOImpl extends BasicDao<BaseRateCode> implements BaseRateCodeDAO {
	private static Logger logger = LogManager.getLogger(BaseRateCodeDAOImpl.class);

	public BaseRateCodeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Base Rate Codes details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return BaseRateCode
	 */
	@Override
	public BaseRateCode getBaseRateCodeById(final String id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BRType, BRTypeDesc, BRTypeIsActive, BRRepayRvwFrq, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from RMTBaseRateCodes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BRType = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				BaseRateCode brc = new BaseRateCode();

				brc.setBRType(rs.getString("BRType"));
				brc.setBRTypeDesc(rs.getString("BRTypeDesc"));
				brc.setbRTypeIsActive(rs.getBoolean("BRTypeIsActive"));
				brc.setbRRepayRvwFrq(rs.getString("BRRepayRvwFrq"));
				brc.setVersion(rs.getInt("Version"));
				brc.setLastMntBy(rs.getLong("LastMntBy"));
				brc.setLastMntOn(rs.getTimestamp("LastMntOn"));
				brc.setRecordStatus(rs.getString("RecordStatus"));
				brc.setRoleCode(rs.getString("RoleCode"));
				brc.setNextRoleCode(rs.getString("NextRoleCode"));
				brc.setTaskId(rs.getString("TaskId"));
				brc.setNextTaskId(rs.getString("NextTaskId"));
				brc.setRecordType(rs.getString("RecordType"));
				brc.setWorkflowId(rs.getLong("WorkflowId"));

				return brc;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public boolean isDuplicateKey(String bRType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "BRType = :bRType";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTBaseRateCodes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTBaseRateCodes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTBaseRateCodes_Temp", "RMTBaseRateCodes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("bRType", bRType);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(BaseRateCode baseRateCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into RMTBaseRateCodes");
		sql.append(tableType.getSuffix());
		sql.append(" (BRType, BRTypeDesc, BRTypeIsActive, BRRepayRvwFrq,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(
				" values(:BRType, :BRTypeDesc, :BRTypeIsActive, :BRRepayRvwFrq, :Version , :LastMntBy, :LastMntOn,:RecordStatus,");
		sql.append(" :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(baseRateCode);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return baseRateCode.getId();
	}

	@Override
	public void update(BaseRateCode baseRateCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update RMTBaseRateCodes");
		sql.append(tableType.getSuffix());
		sql.append(" set BRTypeDesc = :BRTypeDesc, BRTypeIsActive = :BRTypeIsActive, BRRepayRvwFrq = :BRRepayRvwFrq,");
		sql.append(" Version = :Version ,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where BRType =:BRType ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(baseRateCode);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(BaseRateCode baseRateCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from RMTBaseRateCodes");
		sql.append(tableType.getSuffix());
		sql.append(" where BRType =:BRType");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(baseRateCode);
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
}