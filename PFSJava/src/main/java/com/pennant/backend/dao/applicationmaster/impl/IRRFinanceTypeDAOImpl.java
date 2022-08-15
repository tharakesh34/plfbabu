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
 * * FileName : IRRFinanceTypeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2017 * *
 * Modified Date : 21-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-06-2017 PENNANT 0.1 * * 23-07-2018 Sai Krishna 0.2 bugs #492 Unable to save & submit * Loan Types * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.IRRFinanceTypeDAO;
import com.pennant.backend.model.applicationmaster.IRRFinanceType;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>IRRFinanceType</code> with set of CRUD operations.
 */
public class IRRFinanceTypeDAOImpl extends BasicDao<IRRFinanceType> implements IRRFinanceTypeDAO {
	private static Logger logger = LogManager.getLogger(IRRFinanceTypeDAOImpl.class);

	public IRRFinanceTypeDAOImpl() {
		super();
	}

	@Override
	public IRRFinanceType getIRRFinanceType(long iRRID, String finType, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" iRRID, finType, ");
		if (type.contains("View")) {
			sql.append("iRRID, finType, iRRID,finType,");
		}

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From IRRFinanceTypes");
		sql.append(type);
		sql.append(" Where iRRID = :iRRID AND finType = :finType");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		IRRFinanceType iRRFinanceType = new IRRFinanceType();
		iRRFinanceType.setIRRID(iRRID);
		iRRFinanceType.setFinType(finType);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(iRRFinanceType);
		RowMapper<IRRFinanceType> rowMapper = BeanPropertyRowMapper.newInstance(IRRFinanceType.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String save(IRRFinanceType iRRFinanceType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into IRRFinanceTypes");
		sql.append(tableType.getSuffix());
		sql.append(" (iRRID, finType, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :iRRID, :finType, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(iRRFinanceType);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(iRRFinanceType.getIRRID());
	}

	@Override
	public void update(IRRFinanceType iRRFinanceType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		// 23-07-2018 bugs #492 Unable to save & submit Loan Types due to updating the primary key in the update
		// statement.
		StringBuilder sql = new StringBuilder("update IRRFinanceTypes");
		sql.append(tableType.getSuffix());
		sql.append("  set");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where iRRID = :iRRID and finType = :finType ");
		// sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(iRRFinanceType);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(IRRFinanceType iRRFinanceType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from IRRFinanceTypes");
		sql.append(tableType.getSuffix());
		sql.append(" where iRRID = :iRRID ");
		// sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(iRRFinanceType);
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
	public List<IRRFinanceType> getIRRFinanceTypeByFinType(String finType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinType, IRRID, IrrCode, IrrCodeDesc"); // IrrCode is not available in main table
		sql.append(" from IRRFinanceTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinType = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, finType);
			}
		}, new RowMapper<IRRFinanceType>() {
			@Override
			public IRRFinanceType mapRow(ResultSet rs, int rowNum) throws SQLException {
				IRRFinanceType ft = new IRRFinanceType();

				ft.setFinType(rs.getString("FinType"));
				ft.setIRRID(rs.getLong("IRRID"));
				ft.setIrrCode(rs.getString("IrrCode"));
				ft.setIrrCodeDesc(rs.getString("IrrCodeDesc"));

				return ft;
			}
		});
	}

	@Override
	public List<IRRFinanceType> getIRRFinanceTypeList(String finType, String type) {
		logger.debug(Literal.ENTERING);

		IRRFinanceType iRRFinanceType = new IRRFinanceType();
		iRRFinanceType.setFinType(finType);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" iRRID, finType, ");
		if (type.contains("View")) {
			sql.append("iRRID, finType, iRRID,finType,");
		}

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From IRRFinanceTypes");
		sql.append(type);
		sql.append(" Where finType = :finType ");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(iRRFinanceType);
		RowMapper<IRRFinanceType> typeRowMapper = BeanPropertyRowMapper.newInstance(IRRFinanceType.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);

	}

	@Override
	public void deleteList(String finType, String type) {
		logger.debug("Entering");
		FinTypeVASProducts finTypeVASProducts = new FinTypeVASProducts();
		finTypeVASProducts.setFinType(finType);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" IRRFinanceTypes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeVASProducts);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
}
