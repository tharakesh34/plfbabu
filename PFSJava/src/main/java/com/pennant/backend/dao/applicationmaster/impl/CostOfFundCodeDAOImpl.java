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
 * * FileName : CostOfFundCodeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.CostOfFundCodeDAO;
import com.pennant.backend.model.applicationmaster.CostOfFundCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>CostOfFundCode model</b> class.<br>
 */
public class CostOfFundCodeDAOImpl extends BasicDao<CostOfFundCode> implements CostOfFundCodeDAO {
	private static Logger logger = LogManager.getLogger(CostOfFundCodeDAOImpl.class);

	public CostOfFundCodeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Base Rate Codes details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CostOfFundCode
	 */
	@Override
	public CostOfFundCode getCostOfFundCodeById(final String id, String type) {
		logger.debug(Literal.ENTERING);

		CostOfFundCode costOfFundCode = new CostOfFundCode();
		costOfFundCode.setId(id);

		StringBuilder selectSql = new StringBuilder("Select CofCode, CofDesc, Active,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From CostOfFundCodes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CofCode =:CofCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(costOfFundCode);
		RowMapper<CostOfFundCode> typeRowMapper = BeanPropertyRowMapper.newInstance(CostOfFundCode.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(String cofCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "CofCode = :cofCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CostOfFundCodes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CostOfFundCodes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CostOfFundCodes_Temp", "CostOfFundCodes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("cofCode", cofCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(CostOfFundCode costOfFundCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into CostOfFundCodes");
		sql.append(tableType.getSuffix());
		sql.append(" (CofCode, CofDesc, Active,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(:CofCode, :CofDesc, :Active, :Version , :LastMntBy, :LastMntOn,:RecordStatus,");
		sql.append(" :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(costOfFundCode);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return costOfFundCode.getId();
	}

	@Override
	public void update(CostOfFundCode costOfFundCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update CostOfFundCodes");
		sql.append(tableType.getSuffix());
		sql.append(" set CofDesc = :CofDesc, Active = :Active,");
		sql.append(" Version = :Version ,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where CofCode =:CofCode ");
		/* sql.append(QueryUtil.getConcurrencyCondition(tableType)); */

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(costOfFundCode);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(CostOfFundCode costOfFundCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CostOfFundCodes");
		sql.append(tableType.getSuffix());
		sql.append(" where CofCode =:CofCode");
		/* sql.append(QueryUtil.getConcurrencyCondition(tableType)); */

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(costOfFundCode);
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
	public boolean isIdExists(String cofCode) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" Select COUNT(CofCode) from CostOfFunds");
		sql.append(" Where CofCode = :CofCode ");
		logger.debug("Sql: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CofCode", cofCode);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0;
	}
}