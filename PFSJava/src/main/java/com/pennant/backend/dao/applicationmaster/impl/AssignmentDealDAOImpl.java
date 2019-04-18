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
 * FileName    		:  AssignmentDealDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2018    														*
 *                                                                  						*
 * Modified Date    :  12-09-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.AssignmentDealDAO;
import com.pennant.backend.model.applicationmaster.AssignmentDeal;
import com.pennant.backend.model.applicationmaster.AssignmentDealExcludedFee;
import com.pennant.backend.model.applicationmaster.AssignmentDealLoanType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>AssignmentDeal</code> with set of CRUD operations.
 */
public class AssignmentDealDAOImpl extends SequenceDao<AssignmentDeal> implements AssignmentDealDAO {
	private static Logger logger = Logger.getLogger(AssignmentDealDAOImpl.class);

	public AssignmentDealDAOImpl() {
		super();
	}

	@Override
	public AssignmentDeal getAssignmentDeal(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, code, description, partnerCode, partnerCodeName, partnerCodeDesc, active, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From AssignmentDeal");
		sql.append(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AssignmentDeal assignmentDeal = new AssignmentDeal();
		assignmentDeal.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignmentDeal);
		RowMapper<AssignmentDeal> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AssignmentDeal.class);

		try {
			assignmentDeal = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			assignmentDeal = null;
		}

		logger.debug(Literal.LEAVING);
		return assignmentDeal;
	}

	public AssignmentDealLoanType getAssignmentDealLoanType(long dealId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, dealId, LoanTypeCode, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From AssignmentDealLoanType");
		sql.append(type);
		sql.append(" Where dealId = :dealId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AssignmentDealLoanType assignmentDealLoanType = new AssignmentDealLoanType();
		assignmentDealLoanType.setId(dealId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignmentDealLoanType);
		try {
			return (AssignmentDealLoanType) this.jdbcTemplate.queryForList(sql.toString(), paramSource, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			assignmentDealLoanType = null;
		}

		logger.debug(Literal.LEAVING);
		return assignmentDealLoanType;
	}

	public AssignmentDealExcludedFee getAssignmentDealExcludedFee(long dealId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, dealId, FeeTypeId, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From AssignmentDealLoanType");
		sql.append(type);
		sql.append(" Where dealId = :dealId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AssignmentDealExcludedFee assignmentDealExcludedFee = new AssignmentDealExcludedFee();
		assignmentDealExcludedFee.setId(dealId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignmentDealExcludedFee);
		try {
			return (AssignmentDealExcludedFee) this.jdbcTemplate.queryForList(sql.toString(), paramSource, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			assignmentDealExcludedFee = null;
		}

		logger.debug(Literal.LEAVING);
		return assignmentDealExcludedFee;
	}

	@Override
	public String save(AssignmentDeal assignmentDeal, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into AssignmentDeal");
		sql.append(tableType);
		sql.append("( id, code, description, partnerCode, active, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :Id, :code, :description, :partnerCode, :active, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (assignmentDeal.getId() == Long.MIN_VALUE) {
			assignmentDeal.setId(getNextValue("SeqAssignmentDeal"));
			logger.debug("get NextID:" + assignmentDeal.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignmentDeal);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (Exception e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(assignmentDeal.getId());
	}

	public String saveLoanType(AssignmentDealLoanType assignmentDealLoanType, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into AssignmentDealLoanType");
		sql.append(tableType);
		sql.append("( id, dealId, LoanTypeCode, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append("  :Id, :dealId, :LoanTypeCode, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (assignmentDealLoanType.getId() == Long.MIN_VALUE) {
			assignmentDealLoanType.setId(getNextValue("SeqAssignmentDeal"));
			logger.debug("get NextID:" + assignmentDealLoanType.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignmentDealLoanType);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (Exception e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(assignmentDealLoanType.getId());
	}

	public String saveExcludedFee(AssignmentDealExcludedFee assignmentDealExcludedFee, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into AssignmentDealExcludedFee");
		sql.append(tableType);
		sql.append("( id, dealId, FeeTypeId, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append("  :Id, :dealId, :FeeTypeId, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (assignmentDealExcludedFee.getId() == Long.MIN_VALUE) {
			assignmentDealExcludedFee.setId(getNextValue("SeqAssignmentDeal"));
			logger.debug("get NextID:" + assignmentDealExcludedFee.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignmentDealExcludedFee);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (Exception e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(assignmentDealExcludedFee.getId());
	}

	@Override
	public void update(AssignmentDeal assignmentDeal, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update AssignmentDeal");
		sql.append(tableType);
		sql.append("  set code = :code, description = :description, partnerCode = :partnerCode, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignmentDeal);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	public void updateLoanType(AssignmentDealLoanType assignmentDealLoanType, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update AssignmentDealLoanType");
		sql.append(tableType);
		sql.append("  set dealId = :dealId, LoanTypeCode = :LoanTypeCode, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where Id = :id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignmentDealLoanType);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	public void updateExcludedFee(AssignmentDealExcludedFee assignmentDealExcludedFee, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update AssignmentDealExcludedFee");
		sql.append(tableType);
		sql.append("  set dealId = :dealId, FeeTypeId = :FeeTypeId, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where Id = :Id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignmentDealExcludedFee);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	public void deleteExcludedFee(AssignmentDealExcludedFee assignmentDealExcFee, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from AssignmentDealExcludedFee");
		sql.append(tableType);
		sql.append(" where Id = :Id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignmentDealExcFee);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		/*
		 * if (recordCount == 0) { throw new ConcurrencyException(); }
		 */

		logger.debug(Literal.LEAVING);
	}

	public void deleteLoanType(AssignmentDealLoanType assignmentDealLoanType, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from AssignmentDealLoanType");
		sql.append(tableType);
		sql.append(" where Id = :Id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignmentDealLoanType);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		/*
		 * if (recordCount == 0) { throw new ConcurrencyException(); }
		 */
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<AssignmentDealLoanType> getAssignmentDealLoanTypeList(long dealId, String type) {

		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("dealId", dealId);
		RowMapper<AssignmentDealLoanType> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AssignmentDealLoanType.class);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, dealId, LoanTypeCode, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From AssignmentDealLoanType");
		sql.append(type);
		sql.append(" Where dealId = :dealId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);

	}

	public List<AssignmentDealExcludedFee> getAssignmentDealExcludedFeeList(long dealId, String type) {

		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("dealId", dealId);
		RowMapper<AssignmentDealExcludedFee> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AssignmentDealExcludedFee.class);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, dealId, FeeTypeId, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From AssignmentDealExcludedFee");
		sql.append(type);
		sql.append(" Where dealId = :dealId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);

	}

	/**
	 * Method for Deletion List of Document Details in temp table
	 * 
	 * @param documentDetailList
	 * @param type
	 */
	public void deleteExcFeeList(List<AssignmentDealExcludedFee> list, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From AssignmentDealExcludedFee");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DealId =:DealId ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(list.toArray());
		this.jdbcTemplate.batchUpdate(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Deletion List of Document Details in temp table
	 * 
	 * @param documentDetailList
	 * @param type
	 */
	public void deleteLoanTypeList(List<AssignmentDealLoanType> list, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From AssignmentDealLoanType");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DealId =:DealId ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(list.toArray());
		this.jdbcTemplate.batchUpdate(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	public void delete(AssignmentDeal assignmentDeal, String tableType) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From AssignmentDeal");
		deleteSql.append(tableType);
		deleteSql.append(" Where Id = :Id ");

		logger.trace(Literal.SQL + deleteSql.toString());

		MapSqlParameterSource sqlParameter = new MapSqlParameterSource();
		sqlParameter.addValue("Id", assignmentDeal.getId());

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), sqlParameter);
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
	public boolean isDuplicateKey(long id, String code, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "code = :code AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("AssignmentDeal", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("AssignmentDeal_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "AssignmentDeal_Temp", "AssignmentDeal" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("code", code);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public int getMappedAssignmentDeals(long partnerCode) {

		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select COUNT(*) from AssignmentDeal");
		sql.append(" where partnerCode = :partnerCode ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("partnerCode", partnerCode);
		try {
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.queryForObject(sql.toString(), paramSource, Integer.class);

		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}

	@Override
	public List<AssignmentDealExcludedFee> getApprovedAssignmentDealExcludedFeeList(long dealId) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DealId", dealId);
		RowMapper<AssignmentDealExcludedFee> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AssignmentDealExcludedFee.class);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" Id, DealId, FeeTypeId, FeeTypeCode, FeeTypeCode,");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From AssDealExcludedFee_AView"); //TODO if you change table name change View name also
		sql.append(" Where DealId = :DealId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);

	}
}
