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
 * * FileName : FinChangeCustomerDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-11-2019 * *
 * Modified Date : 20-11-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-11-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.FinChangeCustomerDAO;
import com.pennant.backend.model.finance.FinChangeCustomer;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>FinChangeCustomer</code> with set of CRUD operations.
 */
public class FinChangeCustomerDAOImpl extends SequenceDao<FinChangeCustomer> implements FinChangeCustomerDAO {
	private static Logger logger = LogManager.getLogger(FinChangeCustomerDAOImpl.class);

	public FinChangeCustomerDAOImpl() {
		super();
	}

	@Override
	public FinChangeCustomer getFinChangeCustomerById(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, FinReference, OldCustId, CoApplicantId");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (type.contains("_View")) {
			sql.append(", CustCategory, CustCif, JcustCif");
		}

		sql.append(" From FinChangeCustomer");
		sql.append(type);
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				FinChangeCustomer fcc = new FinChangeCustomer();

				fcc.setId(rs.getLong("Id"));
				fcc.setFinID(rs.getLong("FinID"));
				fcc.setFinReference(rs.getString("FinReference"));
				fcc.setOldCustId(rs.getLong("OldCustId"));
				fcc.setCoApplicantId(rs.getLong("CoApplicantId"));
				fcc.setVersion(rs.getInt("Version"));
				fcc.setLastMntOn(rs.getTimestamp("LastMntOn"));
				fcc.setLastMntBy(rs.getLong("LastMntBy"));
				fcc.setRecordStatus(rs.getString("RecordStatus"));
				fcc.setRoleCode(rs.getString("RoleCode"));
				fcc.setNextRoleCode(rs.getString("NextRoleCode"));
				fcc.setTaskId(rs.getString("TaskId"));
				fcc.setNextTaskId(rs.getString("NextTaskId"));
				fcc.setRecordType(rs.getString("RecordType"));
				fcc.setWorkflowId(rs.getLong("WorkflowId"));

				if (type.contains("_View")) {
					fcc.setCustCategory(rs.getString("CustCategory"));
					fcc.setCustCif(rs.getString("CustCif"));
					fcc.setJcustCif(rs.getString("JcustCif"));
				}

				return fcc;

			}, id);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public String save(FinChangeCustomer fcc, TableType tableType) {
		if (fcc.getId() <= 0) {
			fcc.setId(getNextValue("SeqChangeCustomer"));
		}
		StringBuilder sql = new StringBuilder("Insert Into FinChangeCustomer");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, FinID, FinReference, OldCustId, CoApplicantId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, fcc.getId());
				ps.setLong(index++, fcc.getFinID());
				ps.setString(index++, fcc.getFinReference());
				ps.setLong(index++, fcc.getOldCustId());
				ps.setLong(index++, fcc.getCoApplicantId());
				ps.setInt(index++, fcc.getVersion());
				ps.setLong(index++, fcc.getLastMntBy());
				ps.setTimestamp(index++, fcc.getLastMntOn());
				ps.setString(index++, fcc.getRecordStatus());
				ps.setString(index++, fcc.getRoleCode());
				ps.setString(index++, fcc.getNextRoleCode());
				ps.setString(index++, fcc.getTaskId());
				ps.setString(index++, fcc.getNextTaskId());
				ps.setString(index++, fcc.getRecordType());
				ps.setLong(index++, fcc.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(fcc.getFinReference());
	}

	@Override
	public void update(FinChangeCustomer fcc, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update FinChangeCustomer");
		sql.append(tableType.getSuffix());
		sql.append(" Set FinID = ?, FinReference = ?, OldCustId = ?, CoApplicantId = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ? ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, fcc.getFinID());
			ps.setString(index, fcc.getFinReference());
			ps.setLong(index, fcc.getOldCustId());
			ps.setLong(index, fcc.getCoApplicantId());
			ps.setTimestamp(index++, fcc.getLastMntOn());
			ps.setString(index++, fcc.getRecordStatus());
			ps.setString(index++, fcc.getRoleCode());
			ps.setString(index++, fcc.getNextRoleCode());
			ps.setString(index++, fcc.getTaskId());
			ps.setString(index++, fcc.getNextTaskId());
			ps.setString(index++, fcc.getRecordType());
			ps.setLong(index++, fcc.getWorkflowId());
			ps.setLong(index, fcc.getId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(FinChangeCustomer fcc, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From FinChangeCustomer");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ?");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, fcc.getId()));

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void deleteByReference(long finID) {
		String sql = "Delete from FinChangeCustomer Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> ps.setLong(1, finID));
	}

	@Override
	public boolean isDuplicateKey(long id, String finReference, TableType tableType) {
		return false;
	}

	@Override
	public boolean isFinReferenceProcess(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select Count(FinID) From FinChangeCustomer");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID) > 0;
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return false;
	}

}
