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
 * FileName    		:  IRRFinanceTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2017    														*
 *                                                                  						*
 * Modified Date    :  21-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.applicationmaster.FinIRRDetailsDAO;
import com.pennant.backend.model.finance.FinIRRDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>IRRFinanceType</code> with set of CRUD operations.
 */
public class FinIRRDetailsDAOImpl extends BasicDao<FinIRRDetails> implements FinIRRDetailsDAO {
	private static Logger logger = LogManager.getLogger(FinIRRDetailsDAOImpl.class);

	@Override
	public List<FinIRRDetails> getFinIRRList(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" IRRID, FinReference, IRR, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(", IRRCode, IrrCodeDesc");
		}

		sql.append(" from FinIRRDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
				}
			}, new RowMapper<FinIRRDetails>() {
				@Override
				public FinIRRDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinIRRDetails irr = new FinIRRDetails();

					irr.setiRRID(rs.getLong("IRRID"));
					irr.setFinReference(rs.getString("FinReference"));
					irr.setIRR(rs.getBigDecimal("IRR"));
					irr.setVersion(rs.getInt("Version"));
					irr.setLastMntBy(rs.getLong("LastMntBy"));
					irr.setLastMntOn(rs.getTimestamp("LastMntOn"));
					irr.setRecordStatus(rs.getString("RecordStatus"));
					irr.setRoleCode(rs.getString("RoleCode"));
					irr.setNextRoleCode(rs.getString("NextRoleCode"));
					irr.setTaskId(rs.getString("TaskId"));
					irr.setNextTaskId(rs.getString("NextTaskId"));
					irr.setRecordType(rs.getString("RecordType"));
					irr.setWorkflowId(rs.getLong("WorkflowId"));

					if (StringUtils.containsIgnoreCase(type, "View")) {
						irr.setiRRCode(rs.getString("IRRCode"));
						irr.setIrrCodeDesc(rs.getString("IrrCodeDesc"));
					}

					return irr;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public String save(FinIRRDetails entity, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into FinIRRDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (iRRID, finReference, iRR, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :iRRID, :finReference, :iRR,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(entity);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(entity.getiRRID());
	}

	@Override
	public void update(FinIRRDetails entity, TableType tableType) {
		logger.debug(Literal.ENTERING);
		StringBuilder updatesql = new StringBuilder("update FinIRRDetails");
		updatesql.append(tableType.getSuffix());
		updatesql.append(" set iRRID = :iRRID, ");
		updatesql.append(" finReference = :finReference, iRR = :iRR,");
		updatesql.append(" RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		updatesql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updatesql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updatesql.append(" where iRRID = :iRRID And finReference = :finReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + updatesql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(entity);
		int recordCount = jdbcTemplate.update(updatesql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(FinIRRDetails entity, TableType tableType) {
		StringBuilder deleteSql = new StringBuilder("Delete From FinIRRDetails");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where iRRID = :iRRID And finReference = :finReference");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(entity);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void deleteList(String finReference, TableType tableType) {
		logger.debug(Literal.ENTERING);

		FinIRRDetails irrFeeType = new FinIRRDetails();
		irrFeeType.setFinReference(finReference);

		StringBuilder sql = new StringBuilder("delete from FinIRRDetails");
		sql.append(tableType.getSuffix());
		sql.append(" where FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(irrFeeType);
		jdbcTemplate.update(sql.toString(), paramSource);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveList(List<FinIRRDetails> finIrrDetails, TableType tableType) {
		StringBuilder sql = new StringBuilder(" insert into FinIRRDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (iRRID, finReference, iRR, ");
		sql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :iRRID, :finReference, :iRR,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finIrrDetails.toArray());

		try {
			jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);

	}

}
