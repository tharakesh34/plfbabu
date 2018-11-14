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
 * FileName    		:  QueryDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-05-2018    														*
 *                                                                  						*
 * Modified Date    :  09-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-05-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.loanquery.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

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
	private static Logger logger = Logger.getLogger(QueryDetailDAOImpl.class);

	public QueryDetailDAOImpl() {
		super();
	}

	@Override
	public QueryDetail getQueryDetail(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, finReference, categoryId, qryNotes, assignedRole, notifyTo, ");
		sql.append(
				" status, Coalesce(raisedBy,0) raisedBy, raisedOn, responsNotes, Coalesce(responseBy,0) responseBy, responseOn, ");
		sql.append(" closerNotes, Coalesce(closerBy,0) closerBy, closerOn, module, reference, Version");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			// sql.append(" code, description,usrLogin " );
			sql.append(" ,categorycode, categoryDescription,usrLogin, ");
			sql.append(" responseUser, closerUser ");
		}
		sql.append(" From QUERYDETAIL");
		sql.append(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		QueryDetail queryDetail = new QueryDetail();
		queryDetail.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(queryDetail);
		RowMapper<QueryDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(QueryDetail.class);

		try {
			queryDetail = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			queryDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return queryDetail;
	}

	@Override
	public String save(QueryDetail queryDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into QUERYDETAIL");
		// sql.append(tableType.getSuffix());
		sql.append("(id, finReference, categoryId, qryNotes, assignedRole, notifyTo, ");
		sql.append("status, raisedBy, raisedOn, Version, LastMntBy,WorkflowId, Module, Reference)");
		sql.append(" values(");
		sql.append(" :id, :finReference, :categoryId, :qryNotes, :assignedRole, :notifyTo, ");
		sql.append(" :status, :raisedBy, :raisedOn, :Version, :LastMntBy, :WorkflowId, :Module, :Reference)");

		if (queryDetail.getId() == Long.MIN_VALUE) {
			queryDetail.setId(getNextValue("SeqQUERYDETAIL"));
			logger.debug("get NextID:" + queryDetail.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(queryDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(queryDetail.getId());
	}

	@Override
	public void update(QueryDetail queryDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update QUERYDETAIL");
		// sql.append(tableType.getSuffix());
		sql.append("  set status = :status, raisedBy= :raisedBy, qryNotes = :qryNotes, responsNotes = :responsNotes, ");
		sql.append(" responseBy = :responseBy, responseOn = :responseOn, closerNotes = :closerNotes, ");
		sql.append(
				" closerBy = :closerBy, closerOn = :closerOn, Version = :Version, Module = :Module, Reference = :Reference");
		sql.append(" where id = :id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(queryDetail);
		jdbcTemplate.update(sql.toString(), paramSource);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(QueryDetail queryDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from QUERYDETAIL");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(queryDetail);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
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
	public List<QueryDetail> getQueryMgmtList(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		List<QueryDetail> queryDetails = new ArrayList<QueryDetail>();

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT status ");

		sql.append(" From QUERYDETAIL");
		sql.append(type);
		sql.append(" Where finReference = :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		QueryDetail queryDetail = new QueryDetail();
		queryDetail.setFinReference(finReference);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(queryDetail);
		RowMapper<QueryDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(QueryDetail.class);

		try {
			queryDetails = this.jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			queryDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return queryDetails;
	}

	@Override
	public List<QueryDetail> getQueryMgmtListByRef(String reference, String type) {
		logger.debug(Literal.ENTERING);

		List<QueryDetail> queryDetails = new ArrayList<QueryDetail>();

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT status ");

		sql.append(" From QUERYDETAIL");
		sql.append(type);
		sql.append(" Where Reference = :Reference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		QueryDetail queryDetail = new QueryDetail();
		queryDetail.setReference(reference);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(queryDetail);
		RowMapper<QueryDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(QueryDetail.class);

		try {
			queryDetails = this.jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			queryDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return queryDetails;
	}

	@Override
	public List<QueryDetail> getQueryMgmtListForAgreements(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		List<QueryDetail> queryDetails = new ArrayList<QueryDetail>();

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, finReference, categoryId, qryNotes, assignedRole, notifyTo, ");
		sql.append(
				" status, Coalesce(raisedBy,0) raisedBy, raisedOn, responsNotes, Coalesce(responseBy,0) responseBy, responseOn, ");
		sql.append(" closerNotes, Coalesce(closerBy,0) closerBy, closerOn,");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			// sql.append(" code, description,usrLogin " );
			sql.append(" categorycode, categoryDescription,usrLogin, ");
			sql.append(" responseUser, closerUser ");
		}
		sql.append(" From QUERYDETAIL");
		sql.append(type);
		sql.append(" Where finReference = :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		QueryDetail queryDetail = new QueryDetail();
		queryDetail.setFinReference(finReference);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(queryDetail);
		RowMapper<QueryDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(QueryDetail.class);

		try {
			queryDetails = this.jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			queryDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return queryDetails;
	}

	@Override
	public List<QueryDetail> getUnClosedQurysForGivenRole(String finReference, String assignedRole) {
		logger.debug(Literal.ENTERING);

		List<QueryDetail> queryDetails = new ArrayList<QueryDetail>();

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ID, FINREFERENCE, CATEGORYID, QRYNOTES, ASSIGNEDROLE, ");
		sql.append(" NOTIFYTO, STATUS, RAISEDBY, RAISEDON, VERSION, LASTMNTBY,WORKFLOWID, MODULE, REFERENCE ");
		sql.append(" From QUERYDETAIL");
		sql.append(" Where FinReference = :FinReference AND AssignedRole = :AssignedRole ");
		sql.append("  AND ( Status != :ClosedStatus AND Status!= :ResolvedStatus) ");
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);
		parameterSource.addValue("AssignedRole", assignedRole);
		parameterSource.addValue("ClosedStatus", "Close");
		parameterSource.addValue("ResolvedStatus", "Resolve");

		RowMapper<QueryDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(QueryDetail.class);

		try {
			queryDetails = this.jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return queryDetails;
	}

}
