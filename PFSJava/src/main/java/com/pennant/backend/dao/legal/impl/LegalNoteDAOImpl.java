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
 * FileName    		:  LegalNoteDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-06-2018    														*
 *                                                                  						*
 * Modified Date    :  19-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.legal.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.legal.LegalNoteDAO;
import com.pennant.backend.model.legal.LegalNote;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>LegalNote</code> with set of CRUD
 * operations.
 */
public class LegalNoteDAOImpl extends BasisNextidDaoImpl<LegalNote> implements LegalNoteDAO {
	private static Logger logger = Logger.getLogger(LegalNoteDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public LegalNoteDAOImpl() {
		super();
	}

	@Override
	public LegalNote getLegalNote(long legalNoteId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalNoteId, legalId, code, description, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LegalNotes");
		sql.append(type);
		sql.append(" Where legalNoteId = :legalNoteId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		LegalNote legalNote = new LegalNote();
		legalNote.setLegalNoteId(legalNoteId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalNote);
		RowMapper<LegalNote> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalNote.class);

		try {
			legalNote = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			legalNote = null;
		}

		logger.debug(Literal.LEAVING);
		return legalNote;
	}

	@Override
	public List<LegalNote> getLegalNoteList(long legalId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalNoteId, legalId, code, description, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LegalNotes");
		sql.append(type);
		sql.append(" Where legalId = :legalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		LegalNote legalNote = new LegalNote();
		legalNote.setLegalId(legalId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalNote);
		RowMapper<LegalNote> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalNote.class);
		try {
			return this.namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
		} finally {
			sql = null;
		}
		return null;
	}

	@Override
	public String save(LegalNote legalNote, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into LegalNotes");
		sql.append(tableType.getSuffix());
		sql.append(" (legalNoteId, legalId, code, description, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :legalNoteId, :legalId, :code, :description, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (legalNote.getId() == Long.MIN_VALUE) {
			legalNote.setId(getNextidviewDAO().getNextId("SeqLegalNotes"));
			logger.debug("get NextID:" + legalNote.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalNote);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(legalNote.getLegalNoteId());
	}

	@Override
	public void update(LegalNote legalNote, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update LegalNotes");
		sql.append(tableType.getSuffix());
		sql.append("  set legalId = :legalId, code = :code, description = :description, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where legalNoteId = :legalNoteId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalNote);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(LegalNote legalNote, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from LegalNotes");
		sql.append(tableType.getSuffix());
		sql.append(" where legalNoteId = :legalNoteId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalNote);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
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
	public void deleteList(LegalNote legalNote, String tableType) {

		StringBuilder deleteSql = new StringBuilder("Delete From LegalNotes");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where legalId = :legalId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(legalNote);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
	}

	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
