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
 * * FileName : HoldDisbursementDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-10-2018 * *
 * Modified Date : 09-10-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-10-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.HoldDisbursementDAO;
import com.pennant.backend.model.finance.HoldDisbursement;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>HoldDisbursement</code> with set of CRUD operations.
 */
public class HoldDisbursementDAOImpl extends BasicDao<HoldDisbursement> implements HoldDisbursementDAO {
	private static Logger logger = LogManager.getLogger(HoldDisbursementDAOImpl.class);

	public HoldDisbursementDAOImpl() {
		super();
	}

	@Override
	public HoldDisbursement getHoldDisbursement(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, Hold, TotalLoanAmt, DisbursedAmount, HoldLimitAmount, Remarks");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From HoldDisbursement");
		sql.append(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				HoldDisbursement hd = new HoldDisbursement();

				hd.setFinID(rs.getLong("FinID"));
				hd.setFinReference(rs.getString("FinReference"));
				hd.setHold(rs.getBoolean("Hold"));
				hd.setTotalLoanAmt(rs.getBigDecimal("TotalLoanAmt"));
				hd.setDisbursedAmount(rs.getBigDecimal("DisbursedAmount"));
				hd.setHoldLimitAmount(rs.getBigDecimal("HoldLimitAmount"));
				hd.setRemarks(rs.getString("Remarks"));
				hd.setVersion(rs.getInt("Version"));
				hd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				hd.setLastMntBy(rs.getLong("LastMntBy"));
				hd.setRecordStatus(rs.getString("RecordStatus"));
				hd.setRoleCode(rs.getString("RoleCode"));
				hd.setNextRoleCode(rs.getString("NextRoleCode"));
				hd.setTaskId(rs.getString("TaskId"));
				hd.setNextTaskId(rs.getString("NextTaskId"));
				hd.setRecordType(rs.getString("RecordType"));
				hd.setWorkflowId(rs.getLong("WorkflowId"));

				return hd;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String save(HoldDisbursement hd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into HoldDisbursement");
		sql.append(tableType.getSuffix());
		sql.append(" (FinID, FinReference, Hold, TotalLoanAmt, DisbursedAmount, HoldLimitAmount, Remarks");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(" )");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, hd.getFinID());
				ps.setString(index++, hd.getFinReference());
				ps.setBoolean(index++, hd.isHold());
				ps.setBigDecimal(index++, hd.getTotalLoanAmt());
				ps.setBigDecimal(index++, hd.getDisbursedAmount());
				ps.setBigDecimal(index++, hd.getHoldLimitAmount());
				ps.setString(index++, hd.getRemarks());
				ps.setInt(index++, hd.getVersion());
				ps.setLong(index++, hd.getLastMntBy());
				ps.setTimestamp(index++, hd.getLastMntOn());
				ps.setString(index++, hd.getRecordStatus());
				ps.setString(index++, hd.getRoleCode());
				ps.setString(index++, hd.getNextRoleCode());
				ps.setString(index++, hd.getTaskId());
				ps.setString(index++, hd.getNextTaskId());
				ps.setString(index++, hd.getRecordType());
				ps.setLong(index++, hd.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(hd.getFinReference());
	}

	@Override
	public void update(HoldDisbursement hd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update HoldDisbursement");
		sql.append(tableType.getSuffix());
		sql.append(" Set Hold = ?, TotalLoanAmt = ?, DisbursedAmount = ?, HoldLimitAmount = ?, Remarks = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBoolean(index++, hd.isHold());
			ps.setBigDecimal(index++, hd.getTotalLoanAmt());
			ps.setBigDecimal(index++, hd.getDisbursedAmount());
			ps.setBigDecimal(index++, hd.getHoldLimitAmount());
			ps.setString(index++, hd.getRemarks());
			ps.setTimestamp(index++, hd.getLastMntOn());
			ps.setString(index++, hd.getRecordStatus());
			ps.setString(index++, hd.getRoleCode());
			ps.setString(index++, hd.getNextRoleCode());
			ps.setString(index++, hd.getTaskId());
			ps.setString(index++, hd.getNextTaskId());
			ps.setString(index++, hd.getRecordType());
			ps.setLong(index++, hd.getWorkflowId());
			ps.setLong(index++, hd.getFinID());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index++, hd.getPrevMntOn());
			} else {
				ps.setInt(index++, hd.getVersion() - 1);
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(HoldDisbursement hd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From HoldDisbursement");
		sql.append(tableType.getSuffix());
		sql.append(" Where FinID = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, hd.getFinID());

				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(index++, hd.getPrevMntOn());
				} else {
					ps.setInt(index++, hd.getVersion() - 1);
				}
			});

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

	}

	@Override
	public boolean isDuplicateKey(long finID, TableType tableType) {
		String sql;
		String whereClause = "FinID = ?";

		Object[] obj = new Object[] { finID };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("HOLDDISBURSEMENT", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("HOLDDISBURSEMENT_TEMP", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "HOLDDISBURSEMENT", "HOLDDISBURSEMENT_TEMP" }, whereClause);
			obj = new Object[] { finID, finID };
			break;
		}

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public boolean isholdDisbursementProcess(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select Count(FinID) From HoldDisbursement");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and Hold = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID, 1) > 0;
	}
}
