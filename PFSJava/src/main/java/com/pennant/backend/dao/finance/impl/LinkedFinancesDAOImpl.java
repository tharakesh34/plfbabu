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
 * * FileName : LinkedFinancesDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.LinkedFinancesDAO;
import com.pennant.backend.model.finance.LinkedFinances;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>LinkedFinances model</b> class.<br>
 */
public class LinkedFinancesDAOImpl extends SequenceDao<LinkedFinances> implements LinkedFinancesDAO {
	private static Logger logger = LogManager.getLogger(LinkedFinancesDAOImpl.class);

	public LinkedFinancesDAOImpl() {
		super();
	}

	@Override
	public void deleteByLinkedReference(String linkedFinReference, long finID, String type) {
		StringBuilder sql = new StringBuilder("Delete From LinkedFinances");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and LinkedReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index, linkedFinReference);
		});
	}

	@Override
	public void delete(long finID, String type) {
		StringBuilder sql = new StringBuilder("Delete From LinkedFinances");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, finID);
		});
	}

	@Override
	public long save(LinkedFinances lnkdFinance, String type) {
		StringBuilder sql = getInsertSqlQuery(type);

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> setInsertParameterizedFields(lnkdFinance, ps));

		return lnkdFinance.getId();
	}

	public void saveList(List<LinkedFinances> linFinList, String type) {
		StringBuilder sql = getInsertSqlQuery(type);

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					LinkedFinances lnkdFinance = linFinList.get(i);
					setInsertParameterizedFields(lnkdFinance, ps);
				}

				@Override
				public int getBatchSize() {
					return linFinList.size();
				}
			});
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void update(LinkedFinances lnkdFinance, String type) {
		StringBuilder sql = getUpdateSqlQuery(type);

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(),
				ps -> setUpdateParameterizedFields(lnkdFinance, ps));

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateList(List<LinkedFinances> linFinList, String type) {
		StringBuilder sql = getUpdateSqlQuery(type);

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					LinkedFinances lnkdFinance = linFinList.get(i);
					setUpdateParameterizedFields(lnkdFinance, ps);
				}

				@Override
				public int getBatchSize() {
					return linFinList.size();
				}
			});
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<LinkedFinances> getFinIsLinkedActive(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.FinIsActive, T2.LinkedReference, T2.FinID, T2.FinReference, T2.Status");
		sql.append(" From Financemain T1");
		sql.append(" Inner Join Linkedfinances T2 on T1.FinReference = T2.LinkedReference");
		sql.append(" Where T2.FinReference = ?");
		sql.append(" UNION");
		sql.append(" Select");
		sql.append(" T1.FinIsActive, T2.LinkedReference, T2.FinID, T2.FinReference, T2.Status");
		sql.append(" From Financemain T1");
		sql.append(" Inner Join Linkedfinances T2 on T1.FinReference = T2.FinReference");
		sql.append(" Where T2.LinkedReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, finReference);
			ps.setString(2, finReference);
		}, (rs, i) -> {
			LinkedFinances lf = new LinkedFinances();

			lf.setFinIsActive(rs.getBoolean(1));
			lf.setLinkedReference(rs.getString(2));
			lf.setFinID(rs.getLong(3));
			lf.setFinReference(rs.getString(4));
			lf.setStatus(rs.getString(5));

			return lf;
		});
	}

	@Override
	public List<LinkedFinances> getLinkedFinancesByFinRef(String finReference, String type) {
		StringBuilder sql = getSelectSqlQuery(type);
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index, finReference);
		}, (rs, i) -> getRowMapper(type, rs));
	}

	@Override
	public LinkedFinances getLinkedFinancesByLinkRef(String linkedReference, long finID, String type) {
		StringBuilder sql = getSelectSqlQuery(type);
		sql.append(" Where FinID = ? and LinkedReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> getRowMapper(type, rs), finID,
				linkedReference);
	}

	@Override
	public List<LinkedFinances> getLinkedFinancesByFin(String linkedRef, String type) {
		StringBuilder sql = getSelectSqlQuery(type);
		sql.append(" Where LinkedReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setString(1, linkedRef),
				(rs, i) -> getRowMapper(type, rs));
	}

	@Override
	public List<String> getFinReferences(String reference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Distinct LinkedReference From LinkedFinances");
		sql.append(" Where FinReference = ?");
		sql.append(" Union");
		sql.append(" Select");
		sql.append(" Distinct FinReference From LinkedFinances");
		sql.append(" Where LinkedReference = ?");

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, reference);
			ps.setString(2, reference);
		}, (rs, i) -> rs.getString(1));
	}

	private StringBuilder getInsertSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" LinkedFinances");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (ID, FinID, FinReference, LinkedReference, Status");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		return sql;
	}

	private StringBuilder getUpdateSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Update LinkedFinances");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set");
		sql.append(" Status = ?, Version = ? , LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId= ?, NextTaskId= ?, RecordType= ?, WorkflowId= ?");
		sql.append(" Where FinID = ? and LinkedReference = ?");
		return sql;
	}

	private StringBuilder getSelectSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, FinID, FinReference, LinkedReference, Status");
		if (type.contains("View")) {
			sql.append(", CustShrtName, LinkedFinType");
		}
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LinkedFinances");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private void setInsertParameterizedFields(LinkedFinances lnkdFinance, PreparedStatement ps) throws SQLException {
		int index = 1;

		if (lnkdFinance.getID() == 0 || lnkdFinance.getID() == Long.MIN_VALUE) {
			lnkdFinance.setID(getNextValue("SeqLinkedFinances"));
		}

		ps.setLong(index++, lnkdFinance.getID());
		ps.setLong(index++, lnkdFinance.getFinID());
		ps.setString(index++, lnkdFinance.getFinReference());
		ps.setString(index++, lnkdFinance.getLinkedReference());
		ps.setString(index++, lnkdFinance.getStatus());
		ps.setInt(index++, lnkdFinance.getVersion());
		ps.setLong(index++, lnkdFinance.getLastMntBy());
		ps.setTimestamp(index++, lnkdFinance.getLastMntOn());
		ps.setString(index++, lnkdFinance.getRecordStatus());
		ps.setString(index++, lnkdFinance.getRoleCode());
		ps.setString(index++, lnkdFinance.getNextRoleCode());
		ps.setString(index++, lnkdFinance.getTaskId());
		ps.setString(index++, lnkdFinance.getNextTaskId());
		ps.setString(index++, lnkdFinance.getRecordType());
		ps.setLong(index, lnkdFinance.getWorkflowId());
	}

	private void setUpdateParameterizedFields(LinkedFinances lnkdFinance, PreparedStatement ps) throws SQLException {
		int index = 1;

		ps.setString(index++, lnkdFinance.getStatus());
		ps.setInt(index++, lnkdFinance.getVersion());
		ps.setLong(index++, lnkdFinance.getLastMntBy());
		ps.setTimestamp(index++, lnkdFinance.getLastMntOn());
		ps.setString(index++, lnkdFinance.getRecordStatus());
		ps.setString(index++, lnkdFinance.getRoleCode());
		ps.setString(index++, lnkdFinance.getNextRoleCode());
		ps.setString(index++, lnkdFinance.getTaskId());
		ps.setString(index++, lnkdFinance.getNextTaskId());
		ps.setString(index++, lnkdFinance.getRecordType());
		ps.setLong(index++, lnkdFinance.getWorkflowId());

		ps.setLong(index++, lnkdFinance.getFinID());
		ps.setString(index, lnkdFinance.getLinkedReference());
	}

	private LinkedFinances getRowMapper(String type, ResultSet rs) throws SQLException {
		LinkedFinances lf = new LinkedFinances();
		lf.setID(rs.getLong("ID"));
		lf.setFinID(rs.getLong("FinID"));
		lf.setFinReference(rs.getString("FinReference"));
		lf.setLinkedReference(rs.getString("LinkedReference"));
		lf.setStatus(rs.getString("Status"));

		if (type.contains("View")) {
			lf.setCustShrtName(rs.getString("CustShrtName"));
			lf.setLinkedFinType(rs.getString("LinkedFinType"));
		}

		lf.setVersion(rs.getInt("Version"));
		lf.setLastMntBy(rs.getLong("LastMntBy"));
		lf.setLastMntOn(rs.getTimestamp("LastMntOn"));
		lf.setRecordStatus(rs.getString("RecordStatus"));
		lf.setRoleCode(rs.getString("RoleCode"));
		lf.setNextRoleCode(rs.getString("NextRoleCode"));
		lf.setTaskId(rs.getString("TaskId"));
		lf.setNextTaskId(rs.getString("NextTaskId"));
		lf.setRecordType(rs.getString("RecordType"));
		lf.setWorkflowId(rs.getLong("WorkflowId"));

		return lf;
	}
}