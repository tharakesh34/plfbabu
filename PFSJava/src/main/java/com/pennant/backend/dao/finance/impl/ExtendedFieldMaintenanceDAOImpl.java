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
 * * FileName : ExtendedFieldMaintenanceDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-01-2021 *
 * * Modified Date : * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 15-01-2021 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.ExtendedFieldMaintenanceDAO;
import com.pennant.backend.model.finance.ExtendedFieldMaintenance;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 */
public class ExtendedFieldMaintenanceDAOImpl extends SequenceDao<ExtendedFieldMaintenance>
		implements ExtendedFieldMaintenanceDAO {

	public ExtendedFieldMaintenanceDAOImpl() {
		super();
	}

	@Override
	public ExtendedFieldMaintenance getExtendedFieldMaintenanceByFinRef(String reference, String type) {

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" id, reference, type, event, Version, LastMntBy, RecordStatus, ");
		sql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from Extended_Field_Mnt");
		sql.append(type);
		sql.append(" Where reference = :reference");

		// Execute the SQL, binding the arguments.
		logger.debug(Literal.SQL + sql.toString());

		ExtendedFieldMaintenance extendedFieldMaintenance = new ExtendedFieldMaintenance();
		extendedFieldMaintenance.setReference(reference);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(extendedFieldMaintenance);
		RowMapper<ExtendedFieldMaintenance> rowMapper = BeanPropertyRowMapper
				.newInstance(ExtendedFieldMaintenance.class);

		try {
			extendedFieldMaintenance = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			extendedFieldMaintenance = null;
		}
		return extendedFieldMaintenance;
	}

	@Override
	public boolean isDuplicateKey(String finReference, TableType tableType) {

		// Prepare the SQL.
		String sql;
		String whereClause = "Reference = :Reference";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Extended_Field_Mnt", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Extended_Field_Mnt_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Extended_Field_Mnt_Temp", "Extended_Field_Mnt" },
					whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.debug(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("FinReference", finReference);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}
		return exists;
	}

	@Override
	public void save(ExtendedFieldMaintenance extFieldsMaint, TableType tableType) {

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into Extended_Field_Mnt");
		sql.append(tableType.getSuffix());
		sql.append(" (id, reference, type, event, version, lastMntBy, lastMntOn, taskId, nextTaskId, recordStatus, ");
		sql.append(" recordType, roleCode, nextRoleCode, workflowId)");
		sql.append(" values(");
		sql.append(
				" :id, :reference, :type, :event, :version, :lastMntBy, :lastMntOn, :taskId, :nextTaskId, :recordStatus, ");
		sql.append(" :recordType, :roleCode, :nextRoleCode, :workflowId)");

		// Execute the SQL, binding the arguments.
		logger.debug(Literal.SQL + sql.toString());

		try {
			logger.debug(Literal.SQL + sql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extFieldsMaint);
			this.jdbcTemplate.update(sql.toString(), beanParameters);

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

	}

	@Override
	public void update(ExtendedFieldMaintenance extFieldsMaint, TableType tableType) {

		int recordCount = 0;
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update Extended_Field_Mnt");
		sql.append(tableType.getSuffix());
		sql.append(" set reference = :reference, type = :type, event = :event,");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordStatus = :RecordStatus,");
		sql.append(" RecordType = :RecordType, RoleCode = :RoleCode, ");
		sql.append(" NextRoleCode = :NextRoleCode, WorkflowId = :WorkflowId");
		sql.append(" where Reference = :Reference and Id = :Id");

		if (!tableType.toString().startsWith("TEMP")) {
			sql.append("  AND Version= :Version-1");
		}
		// Execute the SQL, binding the arguments.
		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extFieldsMaint);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		// Check for the concurrency failure.
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(ExtendedFieldMaintenance extendedFieldMaintenance, TableType tableType) {
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From Extended_Field_Mnt");
		deleteSql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		deleteSql.append(" Where Reference =:Reference");
		logger.debug(Literal.SQL + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldMaintenance);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
	}

	@Override
	public void save(FinanceMain financeMain) {

		ExtendedFieldMaintenance extendedFieldMaintenance = prepareExtendedFldsMaintanance(financeMain);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into Extended_Field_Mnt");
		sql.append(" (id, reference, type, event, version, lastMntBy, lastMntOn, taskId, nextTaskId, recordStatus, ");
		sql.append(" recordType, roleCode, nextRoleCode, workflowId)");
		sql.append(" values(");
		sql.append(
				" :id, :reference, :type, :event, :version, :lastMntBy, :lastMntOn, :taskId, :nextTaskId, :recordStatus, ");
		sql.append(" :recordType, :roleCode, :nextRoleCode, :workflowId)");

		// Execute the SQL, binding the arguments.
		logger.debug(Literal.SQL + sql.toString());

		try {
			logger.debug(Literal.SQL + sql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldMaintenance);
			this.jdbcTemplate.update(sql.toString(), beanParameters);

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

	}

	private ExtendedFieldMaintenance prepareExtendedFldsMaintanance(FinanceMain financeMain) {// FIXME pls move this
																								// data preparation to
																								// service
		ExtendedFieldMaintenance extFldMntnance = new ExtendedFieldMaintenance();

		extFldMntnance.setId(getNextValue("SeqExtendedFieldMaintenance"));
		extFldMntnance.setReference(financeMain.getFinReference());
		extFldMntnance.setType(financeMain.getFinType());
		extFldMntnance.setEvent(FinServiceEvent.ORG);
		extFldMntnance.setVersion(financeMain.getVersion());
		extFldMntnance.setLastMntBy(financeMain.getLastMntBy());
		extFldMntnance.setLastMntOn(financeMain.getLastMntOn());
		extFldMntnance.setTaskId("");
		extFldMntnance.setNextTaskId("");
		extFldMntnance.setRecordType("");
		extFldMntnance.setRoleCode("");
		extFldMntnance.setNextRoleCode("");
		extFldMntnance.setRecordStatus(financeMain.getRecordStatus());
		extFldMntnance.setWorkflowId(0);
		return extFldMntnance;
	}
}
