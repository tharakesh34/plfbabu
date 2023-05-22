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
 * * FileName : TdsReceivableDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * * Modified
 * Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.tds.receivables.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.tds.receivables.TdsReceivableDAO;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>TdsReceivable</code> with set of CRUD operations.
 */
public class TdsReceivableDAOImpl extends SequenceDao<TdsReceivable> implements TdsReceivableDAO {
	private static Logger logger = LogManager.getLogger(TdsReceivableDAOImpl.class);

	public TdsReceivableDAOImpl() {
		super();
	}

	@Override
	public TdsReceivable getTdsReceivable(long id, TableType type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, TanID, CertificateNumber, CertificateDate, CertificateAmount, AssessmentYear");
		sql.append(", BalanceAmount, UtilizedAmount, Status");
		sql.append(", DateOfReceipt, CertificateQuarter, DocID, Version, LastMntOn, LastMntBy");

		if (StringUtils.containsIgnoreCase(type.getSuffix(), "view")) {
			sql.append(", TanNumber");
		}

		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From TDS_RECEIVABLES");
		sql.append(type.getSuffix());
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				TdsReceivable tdsReceivable = new TdsReceivable();

				tdsReceivable.setId(rs.getLong("Id"));
				tdsReceivable.setTanID(rs.getLong("TanID"));
				tdsReceivable.setCertificateNumber(rs.getString("CertificateNumber"));
				tdsReceivable.setCertificateDate(rs.getTimestamp("CertificateDate"));
				tdsReceivable.setCertificateAmount(rs.getBigDecimal("CertificateAmount"));
				tdsReceivable.setBalanceAmount(rs.getBigDecimal("BalanceAmount"));
				tdsReceivable.setUtilizedAmount(rs.getBigDecimal("UtilizedAmount"));
				tdsReceivable.setStatus(rs.getString("Status"));
				tdsReceivable.setAssessmentYear(rs.getString("AssessmentYear"));
				tdsReceivable.setDateOfReceipt(rs.getTimestamp("DateOfReceipt"));
				tdsReceivable.setCertificateQuarter(rs.getString("CertificateQuarter"));
				tdsReceivable.setDocID(rs.getLong("DocID"));
				tdsReceivable.setVersion(rs.getInt("Version"));
				tdsReceivable.setLastMntOn(rs.getTimestamp("LastMntOn"));
				tdsReceivable.setLastMntBy(rs.getLong("LastMntBy"));

				if (StringUtils.containsIgnoreCase(type.getSuffix(), "view")) {
					tdsReceivable.setTanNumber(rs.getString("TanNumber"));
				}

				tdsReceivable.setRecordStatus(rs.getString("RecordStatus"));
				tdsReceivable.setRoleCode(rs.getString("RoleCode"));
				tdsReceivable.setNextRoleCode(rs.getString("NextRoleCode"));
				tdsReceivable.setTaskId(rs.getString("TaskId"));
				tdsReceivable.setNextTaskId(rs.getString("NextTaskId"));
				tdsReceivable.setRecordType(rs.getString("RecordType"));
				tdsReceivable.setWorkflowId(rs.getLong("WorkflowId"));

				return tdsReceivable;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public boolean isDuplicateKey(long id, String certificateNumber, TableType tableType) {
		String sql;
		String whereClause = "CertificateNumber = ? and Id != ?";
		Object[] obj = new Object[] { certificateNumber, id };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("TDS_RECEIVABLES", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("TDS_RECEIVABLES_TEMP", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "TDS_RECEIVABLES_TEMP", "TDS_RECEIVABLES" }, whereClause);
			obj = new Object[] { certificateNumber, id, certificateNumber, id };
			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public String save(TdsReceivable tdsReceivable, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert");
		sql.append(" into TDS_RECEIVABLES");
		sql.append(tableType.getSuffix());
		sql.append("(Id, TanID, CertificateNumber, CertificateDate, CertificateAmount, AssessmentYear");
		sql.append(", DateOfReceipt, CertificateQuarter, DocID, UtilizedAmount, BalanceAmount, Status");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (tdsReceivable.getId() == Long.MIN_VALUE) {
			tdsReceivable.setId(getNextValue("SeqTDS_RECEIVABLES"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, tdsReceivable.getId());
				ps.setLong(index++, tdsReceivable.getTanID());
				ps.setString(index++, tdsReceivable.getCertificateNumber());
				ps.setDate(index++, JdbcUtil.getDate(tdsReceivable.getCertificateDate()));
				ps.setBigDecimal(index++, tdsReceivable.getCertificateAmount());
				ps.setString(index++, tdsReceivable.getAssessmentYear());
				ps.setDate(index++, JdbcUtil.getDate(tdsReceivable.getDateOfReceipt()));
				ps.setString(index++, tdsReceivable.getCertificateQuarter());
				ps.setLong(index++, tdsReceivable.getDocID());
				ps.setBigDecimal(index++, tdsReceivable.getUtilizedAmount());
				ps.setBigDecimal(index++, tdsReceivable.getBalanceAmount());
				ps.setString(index++, tdsReceivable.getStatus());
				ps.setInt(index++, tdsReceivable.getVersion());
				ps.setLong(index++, tdsReceivable.getLastMntBy());
				ps.setTimestamp(index++, tdsReceivable.getLastMntOn());
				ps.setString(index++, tdsReceivable.getRecordStatus());
				ps.setString(index++, tdsReceivable.getRoleCode());
				ps.setString(index++, tdsReceivable.getNextRoleCode());
				ps.setString(index++, tdsReceivable.getTaskId());
				ps.setString(index++, tdsReceivable.getNextTaskId());
				ps.setString(index++, tdsReceivable.getRecordType());
				ps.setLong(index, tdsReceivable.getWorkflowId());

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(tdsReceivable.getId());
	}

	@Override
	public void update(TdsReceivable tdsReceivable, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" TDS_RECEIVABLES");
		sql.append(tableType.getSuffix());
		sql.append(" Set TanID = ?, CertificateNumber = ?, CertificateDate = ?");
		sql.append(", CertificateAmount = ?, AssessmentYear = ?");
		sql.append(", DateOfReceipt = ?, CertificateQuarter = ?, DocID = ?");
		sql.append(", Status = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, tdsReceivable.getTanID());
			ps.setString(index++, tdsReceivable.getCertificateNumber());
			ps.setDate(index++, JdbcUtil.getDate(tdsReceivable.getCertificateDate()));
			ps.setBigDecimal(index++, tdsReceivable.getCertificateAmount());
			ps.setString(index++, tdsReceivable.getAssessmentYear());
			ps.setDate(index++, JdbcUtil.getDate(tdsReceivable.getDateOfReceipt()));
			ps.setString(index++, tdsReceivable.getCertificateQuarter());
			ps.setLong(index++, tdsReceivable.getDocID());
			ps.setString(index++, tdsReceivable.getStatus());
			ps.setTimestamp(index++, tdsReceivable.getLastMntOn());
			ps.setString(index++, tdsReceivable.getRecordStatus());
			ps.setString(index++, tdsReceivable.getRoleCode());
			ps.setString(index++, tdsReceivable.getNextRoleCode());
			ps.setString(index++, tdsReceivable.getTaskId());
			ps.setString(index++, tdsReceivable.getNextTaskId());
			ps.setString(index++, tdsReceivable.getRecordType());
			ps.setLong(index++, tdsReceivable.getWorkflowId());

			ps.setLong(index++, tdsReceivable.getId());
			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index, tdsReceivable.getPrevMntOn());
			} else {
				ps.setInt(index, tdsReceivable.getVersion() - 1);
			}

		});

	}

	@Override
	public void delete(TdsReceivable tdsReceivable, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete");
		sql.append(" From TDS_RECEIVABLES");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ?");
		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, tdsReceivable.getId()));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void updateReceivableBalances(TdsReceivable tdsReceivable) {
		String sql = "Update TDS_RECEIVABLES Set UtilizedAmount = ?, BalanceAmount = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;
			ps.setBigDecimal(index++, tdsReceivable.getUtilizedAmount());
			ps.setBigDecimal(index++, tdsReceivable.getBalanceAmount());

			ps.setLong(index, tdsReceivable.getId());
		});

	}

	@Override
	public String getStatus(String certificatenumber) {
		String sql = "Select Status From TDS_RECEIVABLES Where CertificateNumber = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, certificatenumber);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}
}
