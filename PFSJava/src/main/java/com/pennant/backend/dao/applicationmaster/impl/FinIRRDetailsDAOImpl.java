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
 * 21-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

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
	public List<FinIRRDetails> getFinIRRList(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" IRRID, FinID, FinReference, IRR, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(", IRRCode, IrrCodeDesc");
		}

		sql.append(" From FinIRRDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);
		}, (rs, rowNum) -> {
			FinIRRDetails irr = new FinIRRDetails();

			irr.setiRRID(rs.getLong("IRRID"));
			irr.setFinID(rs.getLong("FinID"));
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
		});
	}

	@Override
	public String save(FinIRRDetails irr, TableType tableType) {
		String sql = getInsertQuery(tableType);

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.update(sql, ps -> setPreparedStatement(irr, ps));
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(irr.getiRRID());
	}

	@Override
	public void update(FinIRRDetails irr, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update FinIRRDetails");
		sql.append(tableType.getSuffix());
		sql.append(" Set IRRID = ?, FinID = ?, FinReference = ?, IRR = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where IRRID = ? And FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			setPreparedStatement(irr, ps);
			ps.setLong(15, irr.getiRRID());
			ps.setLong(16, irr.getFinID());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public void delete(FinIRRDetails entity, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From FinIRRDetails");
		sql.append(tableType.getSuffix());
		sql.append(" Where IRRID = ? And FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, entity.getiRRID());
			ps.setLong(index, entity.getFinID());
		});
	}

	@Override
	public void deleteList(long finID, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from FinIRRDetails");
		sql.append(tableType.getSuffix());
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, finID));
	}

	@Override
	public void saveList(List<FinIRRDetails> irr, TableType tableType) {
		String sql = getInsertQuery(tableType);

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					FinIRRDetails fid = irr.get(i);

					setPreparedStatement(fid, ps);
				}

				@Override
				public int getBatchSize() {
					return irr.size();
				}
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	private String getInsertQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into FinIRRDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (IRRID, FinID, FinReference, IRR");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");
		return sql.toString();
	}

	private void setPreparedStatement(FinIRRDetails irr, PreparedStatement ps) throws SQLException {
		int index = 1;

		ps.setLong(index++, irr.getiRRID());
		ps.setLong(index++, irr.getFinID());
		ps.setString(index++, irr.getFinReference());
		ps.setBigDecimal(index++, irr.getIRR());
		ps.setInt(index++, irr.getVersion());
		ps.setLong(index++, irr.getLastMntBy());
		ps.setTimestamp(index++, irr.getLastMntOn());
		ps.setString(index++, irr.getRecordStatus());
		ps.setString(index++, irr.getRoleCode());
		ps.setString(index++, irr.getNextRoleCode());
		ps.setString(index++, irr.getTaskId());
		ps.setString(index++, irr.getNextTaskId());
		ps.setString(index++, irr.getRecordType());
		ps.setLong(index, irr.getWorkflowId());
	}

}
