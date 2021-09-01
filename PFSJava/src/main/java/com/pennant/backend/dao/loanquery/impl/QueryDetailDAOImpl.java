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
 * * FileName : QueryDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-05-2018 * * Modified
 * Date : 09-05-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-05-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.loanquery.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.loanquery.QueryDetailDAO;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>QueryDetail</code> with set of CRUD operations.
 */
public class QueryDetailDAOImpl extends SequenceDao<QueryDetail> implements QueryDetailDAO {
	private static Logger logger = LogManager.getLogger(QueryDetailDAOImpl.class);

	public QueryDetailDAOImpl() {
		super();
	}

	@Override
	public QueryDetail getQueryDetail(long id, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		QueryDetailRowMapper rowMapper = new QueryDetailRowMapper(type);

		try {
			return jdbcOperations.queryForObject(sql.toString(), rowMapper, id);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public String save(QueryDetail qd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into QueryDetail");
		sql.append(" (Id, FinID, FinReference, CategoryId, QryNotes, AssignedRole, NotifyTo");
		sql.append(", Status, RaisedBy, RaisedOn, Version, LastMntBy, WorkflowId, Module, Reference");
		sql.append(", RaisedUsrRole)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (qd.getId() == Long.MIN_VALUE) {
			qd.setId(getNextValue("SeqQueryDetail"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, qd.getId());
				ps.setLong(index++, qd.getFinID());
				ps.setString(index++, qd.getFinReference());
				ps.setLong(index++, qd.getCategoryId());
				ps.setString(index++, qd.getQryNotes());
				ps.setString(index++, qd.getAssignedRole());
				ps.setString(index++, qd.getNotifyTo());
				ps.setString(index++, qd.getStatus());
				ps.setLong(index++, qd.getRaisedBy());
				ps.setTimestamp(index++, qd.getRaisedOn());
				ps.setInt(index++, qd.getVersion());
				ps.setLong(index++, qd.getLastMntBy());
				ps.setLong(index++, qd.getWorkflowId());
				ps.setString(index++, qd.getModule());
				ps.setString(index++, qd.getReference());
				ps.setString(index++, qd.getRaisedUsrRole());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(qd.getId());
	}

	@Override
	public void update(QueryDetail qd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update QueryDetail");
		sql.append(" Set Status = ?, RaisedBy = ?, QryNotes = ?, ResponsNotes = ?");
		sql.append(", ResponseBy = ?, ResponseOn = ?, CloserNotes = ?");
		sql.append(", CloserBy = ?, CloserOn = ?, Version = ?, Module = ?, Reference = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, qd.getStatus());
			ps.setLong(index++, qd.getRaisedBy());
			ps.setString(index++, qd.getQryNotes());
			ps.setString(index++, qd.getResponsNotes());
			ps.setLong(index++, qd.getResponseBy());
			ps.setTimestamp(index++, qd.getResponseOn());
			ps.setString(index++, qd.getCloserNotes());
			ps.setLong(index++, qd.getCloserBy());
			ps.setTimestamp(index++, qd.getCloserOn());
			ps.setInt(index++, qd.getVersion());
			ps.setString(index++, qd.getModule());
			ps.setString(index++, qd.getReference());

			ps.setLong(index++, qd.getId());
		});
	}

	// FIXME:PrevMntOn is not Available
	@Override
	public void delete(QueryDetail queryDetail, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From QueryDetail");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = :Id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(queryDetail);

		try {
			int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

	}

	@Override
	public List<QueryDetail> getQueryMgmtList(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select Status, RaisedUsrRole");
		sql.append(" From QueryDetail");
		sql.append(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
		}, (rs, rowNum) -> {
			QueryDetail qd = new QueryDetail();

			qd.setStatus(rs.getString("Status"));
			qd.setRaisedUsrRole(rs.getString("RaisedUsrRole"));

			return qd;
		});

	}

	@Override
	public List<QueryDetail> getQueryMgmtListByRef(String reference, String type) {
		StringBuilder sql = new StringBuilder("Select Status");
		sql.append(" From QueryDetail");
		sql.append(type);
		sql.append(" Where Reference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, reference);
		}, (rs, rowNum) -> {
			QueryDetail qd = new QueryDetail();

			qd.setStatus(rs.getString("Status"));

			return qd;
		});
	}

	@Override
	public List<QueryDetail> getQueryMgmtListForAgreements(long finID, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		QueryDetailRowMapper rowMapper = new QueryDetailRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
		}, rowMapper);

	}

	@Override
	public List<QueryDetail> getUnClosedQurysForGivenRole(long finID, String assignedRole) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, FinReference, CategoryId, QryNotes, AssignedRole");
		sql.append(" NotifyTo, Status, RaisedBy, RaisedOn, Version, LastmntBy, WorkFlowId, Module, Reference");
		sql.append(" From QueryDetail");
		sql.append(" Where FinID = ? and AssignedRole = ?");
		sql.append(" and (Status ! = ? and Status ! = ?) ");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index++, assignedRole);
			ps.setString(index++, "Close");
			ps.setString(index++, "Resolve");
		}, (rs, rowNum) -> {
			QueryDetail qd = new QueryDetail();

			qd.setFinID(rs.getLong("Id"));
			qd.setFinID(rs.getLong("FinID"));
			qd.setFinReference(rs.getString("FinReference"));
			qd.setCategoryId(rs.getLong("CategoryId"));
			qd.setQryNotes(rs.getString("QryNotes"));
			qd.setAssignedRole(rs.getString("AssignedRole"));
			qd.setNotifyTo(rs.getString("NotifyTo"));
			qd.setStatus(rs.getString("Status"));
			qd.setRaisedBy(rs.getLong("RaisedBy"));
			qd.setRaisedOn(rs.getTimestamp("RaisedOn"));
			qd.setVersion(rs.getInt("Version"));
			qd.setLastMntBy(rs.getLong("LastMntBy"));
			qd.setWorkflowId(rs.getLong("WorkflowId"));
			qd.setModule(rs.getString("Module"));
			qd.setReference(rs.getString("Reference"));

			return qd;
		});

	}

	@Override
	public List<QueryDetail> getQueryListByReference(String Reference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, FinReference, CategoryId, QryNotes, AssignedRole");
		sql.append(", NotifyTo, Status, RaisedBy, RaisedOn, Version, LastmntBy, WorkFlowId, Module, Reference");
		sql.append(" From QueryDetail");
		sql.append(" Where Reference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, Reference);
		}, (rs, rowNum) -> {
			QueryDetail qd = new QueryDetail();

			qd.setFinID(rs.getLong("Id"));
			qd.setFinID(rs.getLong("FinID"));
			qd.setFinReference(rs.getString("FinReference"));
			qd.setCategoryId(rs.getLong("CategoryId"));
			qd.setQryNotes(rs.getString("QryNotes"));
			qd.setAssignedRole(rs.getString("AssignedRole"));
			qd.setNotifyTo(rs.getString("NotifyTo"));
			qd.setStatus(rs.getString("Status"));
			qd.setRaisedBy(rs.getLong("RaisedBy"));
			qd.setRaisedOn(rs.getTimestamp("RaisedOn"));
			qd.setVersion(rs.getInt("Version"));
			qd.setLastMntBy(rs.getLong("LastMntBy"));
			qd.setWorkflowId(rs.getLong("WorkflowId"));
			qd.setModule(rs.getString("Module"));
			qd.setReference(rs.getString("Reference"));

			return qd;

		});
	}

	@Override
	public Long getCustIdByQuery(long queryId) {
		StringBuilder sql = new StringBuilder("Select Distinct CustId From (");
		sql.append(" Select c.CustId, qd.Id From Querydetail_Temp qd");
		sql.append(" Inner Join Financemain_Temp fm on fm.FinID = qd.FinID");
		sql.append(" Inner Join Customers_Temp c on c.CustId = fm.CustId");
		sql.append(" Union All");
		sql.append(" Select c.CustId, qd.Id From Querydetail qd");
		sql.append(" Inner Join Financemain fm on fm.FinID = qd.FinID");
		sql.append(" Inner Join Customers c on c.CustId = fm.CustId) T Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, queryId);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	private StringBuilder sqlSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, FinReference, CategoryId, QryNotes, AssignedRole, NotifyTo");
		sql.append(", Status, Coalesce(RaisedBy, 0) RaisedBy, RaisedOn, ResponsNotes");
		sql.append(", Coalesce(ResponseBy, 0) ResponseBy, ResponseOn");
		sql.append(", CloserNotes, Coalesce(CloserBy,0) CloserBy, CloserOn, Module, Reference, Version");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CategoryCode, CategoryDescription, UsrLogin");
			sql.append(", ResponseUser, CloserUser");
		}

		sql.append(" From QueryDetail");
		sql.append(type);

		return sql;
	}

	private class QueryDetailRowMapper implements RowMapper<QueryDetail> {
		private String type;

		private QueryDetailRowMapper(String type) {
			this.type = type;
		}

		@Override
		public QueryDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			QueryDetail qd = new QueryDetail();

			qd.setFinID(rs.getLong("Id"));
			qd.setFinID(rs.getLong("FinID"));
			qd.setFinReference(rs.getString("FinReference"));
			qd.setCategoryId(rs.getLong("CategoryId"));
			qd.setQryNotes(rs.getString("QryNotes"));
			qd.setAssignedRole(rs.getString("AssignedRole"));
			qd.setNotifyTo(rs.getString("NotifyTo"));
			qd.setStatus(rs.getString("Status"));
			qd.setRaisedBy(rs.getLong("RaisedBy"));
			qd.setRaisedOn(rs.getTimestamp("RaisedOn"));
			qd.setResponsNotes(rs.getString("ResponsNotes"));
			qd.setResponseBy(rs.getLong("ResponseBy"));
			qd.setResponseOn(rs.getTimestamp("ResponseOn"));
			qd.setCloserNotes(rs.getString("CloserNotes"));
			qd.setCloserBy(rs.getLong("CloserBy"));
			qd.setCloserOn(rs.getTimestamp("CloserOn"));
			qd.setModule(rs.getString("Module"));
			qd.setReference(rs.getString("Reference"));
			qd.setVersion(rs.getInt("Version"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				qd.setCategoryCode(rs.getString("CategoryCode"));
				qd.setCategoryDescription(rs.getString("CategoryDescription"));
				qd.setUsrLogin(rs.getString("UsrLogin"));
				qd.setResponseUser(rs.getString("ResponseUser"));
				qd.setCloserUser(rs.getString("CloserUser"));
			}

			return qd;

		}
	}
}
