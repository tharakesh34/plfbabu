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
 * * FileName : VasMovementDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-12-2011 * *
 * Modified Date : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.applicationmaster.VasMovementDetailDAO;
import com.pennant.backend.model.finance.VasMovementDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>VasMovementDetail model</b> class.<br>
 * 
 */
public class VasMovementDetailDAOImpl extends BasicDao<VasMovementDetail> implements VasMovementDetailDAO {
	private static Logger logger = LogManager.getLogger(VasMovementDetailDAOImpl.class);

	public VasMovementDetailDAOImpl() {
		super();
	}

	@Override
	public List<VasMovementDetail> getVasMovementDetailById(final long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" VasMovementId, VasMovementDetailId, FinID, FinReference, VasReference, MovementDate");
		sql.append(", MovementAmt, VasProvider, VasProduct, VasAmount");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From VasMovementDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where VasMovementId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, id), (rs, rowNum) -> {
			VasMovementDetail vmd = new VasMovementDetail();

			vmd.setVasMovementId(rs.getLong("VasMovementId"));
			vmd.setVasMovementDetailId(rs.getLong("VasMovementDetailId"));
			vmd.setFinID(rs.getLong("FinID"));
			vmd.setFinReference(rs.getString("FinReference"));
			vmd.setVasReference(rs.getString("VasReference"));
			vmd.setMovementDate(rs.getDate("MovementDate"));
			vmd.setMovementAmt(rs.getBigDecimal("MovementAmt"));
			vmd.setVasProvider(rs.getString("VasProvider"));
			vmd.setVasProduct(rs.getString("VasProduct"));
			vmd.setVasAmount(rs.getBigDecimal("VasAmount"));
			vmd.setVersion(rs.getInt("Version"));
			vmd.setLastMntBy(rs.getLong("LastMntBy"));
			vmd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			vmd.setRecordStatus(rs.getString("RecordStatus"));
			vmd.setRoleCode(rs.getString("RoleCode"));
			vmd.setNextRoleCode(rs.getString("NextRoleCode"));
			vmd.setTaskId(rs.getString("TaskId"));
			vmd.setNextTaskId(rs.getString("NextTaskId"));
			vmd.setRecordType(rs.getString("RecordType"));
			vmd.setWorkflowId(rs.getLong("WorkflowId"));

			return vmd;
		});
	}

	public void delete(VasMovementDetail vmd, String type) {
		StringBuilder sql = new StringBuilder("Delete From VasMovementDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where VasMovementDetailId = ? and VasReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, vmd.getVasMovementDetailId());
				ps.setString(index, vmd.getVasReference());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	public void delete(long vasMovementDetailId, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Delete From VasMovementDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where VasMovementId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, vasMovementDetailId);
		});
	}

	@Override
	public long save(VasMovementDetail vmd, String type) {
		StringBuilder sql = new StringBuilder("Insert Into VasMovementDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (VasMovementId, VasMovementDetailId, FinID, FinReference, VasReference");
		sql.append(", MovementDate, MovementAmt, VasProvider, VasProduct, VasAmount");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, vmd.getVasMovementId());
			ps.setLong(index++, vmd.getVasMovementDetailId());
			ps.setLong(index++, vmd.getFinID());
			ps.setString(index++, vmd.getFinReference());
			ps.setString(index++, vmd.getVasReference());
			ps.setDate(index++, JdbcUtil.getDate(vmd.getMovementDate()));
			ps.setBigDecimal(index++, vmd.getMovementAmt());
			ps.setString(index++, vmd.getVasProvider());
			ps.setString(index++, vmd.getVasProduct());
			ps.setBigDecimal(index++, vmd.getVasAmount());
			ps.setInt(index++, vmd.getVersion());
			ps.setLong(index++, vmd.getLastMntBy());
			ps.setTimestamp(index++, vmd.getLastMntOn());
			ps.setString(index++, vmd.getRecordStatus());
			ps.setString(index++, vmd.getRoleCode());
			ps.setString(index++, vmd.getNextRoleCode());
			ps.setString(index++, vmd.getTaskId());
			ps.setString(index++, vmd.getNextTaskId());
			ps.setString(index++, vmd.getRecordType());
			ps.setLong(index, vmd.getWorkflowId());
		});

		return vmd.getId();
	}

	@Override
	public void update(VasMovementDetail vmd, String type) {
		StringBuilder sql = new StringBuilder("Update VasMovementDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set MovementDate = ?, MovementAmt = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where VasMovementDetailId = ? and VasMovementId = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(vmd.getMovementDate()));
			ps.setBigDecimal(index++, vmd.getMovementAmt());
			ps.setInt(index++, vmd.getVersion());
			ps.setLong(index++, vmd.getLastMntBy());
			ps.setTimestamp(index++, vmd.getLastMntOn());
			ps.setString(index++, vmd.getRecordStatus());
			ps.setString(index++, vmd.getRoleCode());
			ps.setString(index++, vmd.getNextRoleCode());
			ps.setString(index++, vmd.getTaskId());
			ps.setString(index++, vmd.getNextTaskId());
			ps.setString(index++, vmd.getRecordType());
			ps.setLong(index++, vmd.getWorkflowId());

			ps.setLong(index++, vmd.getVasMovementDetailId());
			ps.setLong(index++, vmd.getVasMovementId());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, vmd.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public BigDecimal getVasMovementDetailByRef(String finReference, Date finStartDate, Date finEndDate, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" sum(MovementAmt)");
		sql.append(" From VasMovementDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");
		sql.append(" and MovementDate >= ? and MovementDate <= ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finReference, finStartDate,
					finEndDate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}
}