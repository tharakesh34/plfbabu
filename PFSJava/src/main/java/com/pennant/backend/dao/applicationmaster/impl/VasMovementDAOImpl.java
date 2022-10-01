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
 * * FileName : VasMovementDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-12-2011 * * Modified
 * Date : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.applicationmaster.VasMovementDAO;
import com.pennant.backend.model.finance.VasMovement;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>VasMovement model</b> class.<br>
 * 
 */
public class VasMovementDAOImpl extends SequenceDao<VasMovement> implements VasMovementDAO {
	private static Logger logger = LogManager.getLogger(VasMovementDAOImpl.class);

	public VasMovementDAOImpl() {
		super();
	}

	@Override
	public VasMovement getVasMovementById(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" VasMovementId, FinID, FinReference, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinAmount, FinStartDate, MaturityDate, CustCif, FinType");
		}

		sql.append(" From VasMovement");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				VasMovement vm = new VasMovement();

				vm.setVasMovementId(rs.getLong("VasMovementId"));
				vm.setFinID(rs.getLong("FinID"));
				vm.setFinReference(rs.getString("FinReference"));
				vm.setVersion(rs.getInt("Version"));
				vm.setLastMntBy(rs.getLong("LastMntBy"));
				vm.setLastMntOn(rs.getTimestamp("LastMntOn"));
				vm.setRecordStatus(rs.getString("RecordStatus"));
				vm.setRoleCode(rs.getString("RoleCode"));
				vm.setNextRoleCode(rs.getString("NextRoleCode"));
				vm.setTaskId(rs.getString("TaskId"));
				vm.setNextTaskId(rs.getString("NextTaskId"));
				vm.setRecordType(rs.getString("RecordType"));
				vm.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					vm.setFinAmount(rs.getBigDecimal("FinAmount"));
					vm.setFinStartdate(rs.getDate("FinStartDate"));
					vm.setMaturityDate(rs.getDate("MaturityDate"));
					vm.setCustCif(rs.getString("CustCif"));
					vm.setFinType(rs.getString("FinType"));
				}

				return vm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public void delete(VasMovement vm, String type) {
		StringBuilder sql = new StringBuilder("Delete From VasMovement");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index, vm.getFinID());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(VasMovement vm, String type) {
		if (vm.getId() <= 0) {
			vm.setId(getNextValue("SeqBMTCheckList"));
		}

		StringBuilder sql = new StringBuilder("Insert Into VasMovement");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (VasMovementId, FinID, FinReference");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, vm.getVasMovementId());
			ps.setLong(index++, vm.getFinID());
			ps.setString(index++, vm.getFinReference());
			ps.setInt(index++, vm.getVersion());
			ps.setLong(index++, vm.getLastMntBy());
			ps.setTimestamp(index++, vm.getLastMntOn());
			ps.setString(index++, vm.getRecordStatus());
			ps.setString(index++, vm.getRoleCode());
			ps.setString(index++, vm.getNextRoleCode());
			ps.setString(index++, vm.getTaskId());
			ps.setString(index++, vm.getNextTaskId());
			ps.setString(index++, vm.getRecordType());
			ps.setLong(index, vm.getWorkflowId());
		});

		return vm.getId();
	}

	@Override
	public void update(VasMovement vm, String type) {
		StringBuilder sql = new StringBuilder("Update VasMovement");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, vm.getVersion());
			ps.setLong(index++, vm.getLastMntBy());
			ps.setTimestamp(index++, vm.getLastMntOn());
			ps.setString(index++, vm.getRecordStatus());
			ps.setString(index++, vm.getRoleCode());
			ps.setString(index++, vm.getNextRoleCode());
			ps.setString(index++, vm.getTaskId());
			ps.setString(index++, vm.getNextTaskId());
			ps.setString(index++, vm.getRecordType());
			ps.setLong(index++, vm.getWorkflowId());
			ps.setLong(index++, vm.getFinID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, vm.getVersion() - 1);
			}

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}
}