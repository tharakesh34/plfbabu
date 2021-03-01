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
 * FileName    		:  VASProviderAccDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-09-2018    														*
 *                                                                  						*
 * Modified Date    :  24-09-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-09-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.systemmasters.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.systemmasters.VASProviderAccDetailDAO;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>VASProviderAccDetail</code> with set of CRUD operations.
 */
public class VASProviderAccDetailDAOImpl extends SequenceDao<VASProviderAccDetail> implements VASProviderAccDetailDAO {
	private static Logger logger = LogManager.getLogger(VASProviderAccDetailDAOImpl.class);

	public VASProviderAccDetailDAOImpl() {
		super();
	}

	@Override
	public VASProviderAccDetail getVASProviderAccDetail(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, providerId,entityCode, paymentMode, bankBranchID, accountNumber, receivableAdjustment, ");
		sql.append(" reconciliationAmount, active, partnerBankId, ");
		if ("_view".equalsIgnoreCase(type)) {
			sql.append(
					"entityDesc, providerDesc, branchDesc,bankName,ifscCode,micrCode,bankCode, partnerBankCode, partnerBankName,");
		}

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From VASProviderAccDetail");
		sql.append(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		VASProviderAccDetail vASProviderAccDetail = new VASProviderAccDetail();
		vASProviderAccDetail.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(vASProviderAccDetail);
		RowMapper<VASProviderAccDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VASProviderAccDetail.class);

		try {
			vASProviderAccDetail = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			vASProviderAccDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return vASProviderAccDetail;
	}

	@Override
	public boolean isDuplicateKey(long id, long providerId, String entityCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "providerId = :ProviderId AND entityCode = :EntityCode AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("VASProviderAccDetail", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("VASProviderAccDetail_temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "VASProviderAccDetail_Temp", "VASProviderAccDetail" },
					whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("ProviderId", providerId);
		paramSource.addValue("EntityCode", entityCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(VASProviderAccDetail vASProviderAccDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into VASProviderAccDetail");
		sql.append(tableType.getSuffix());
		sql.append("(id, providerId, entityCode,paymentMode, bankBranchID,accountNumber, receivableAdjustment, ");
		sql.append(" reconciliationAmount, active, partnerBankId,");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(
				" :id, :providerId,:entityCode, :paymentMode, :bankBranchID, :accountNumber, :receivableAdjustment, ");
		sql.append(" :reconciliationAmount,:active, :partnerBankId,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (vASProviderAccDetail.getId() == Long.MIN_VALUE) {
			vASProviderAccDetail.setId(getNextValue("seqVASProviderAccDetail"));
			logger.debug("get NextID:" + vASProviderAccDetail.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(vASProviderAccDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(vASProviderAccDetail.getId());
	}

	@Override
	public void update(VASProviderAccDetail vASProviderAccDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update VASProviderAccDetail");
		sql.append(tableType.getSuffix());
		sql.append(
				"  set providerId = :providerId,entityCode =:entityCode, paymentMode = :paymentMode, bankBranchID = :bankBranchID,");
		sql.append(
				" accountNumber = :accountNumber, receivableAdjustment = :receivableAdjustment, reconciliationAmount = :reconciliationAmount ");
		sql.append(",active = :active, partnerBankId = :partnerBankId, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(vASProviderAccDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(VASProviderAccDetail vASProviderAccDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from VASProviderAccDetail");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(vASProviderAccDetail);
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
	public VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId, String entityCode, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT id, providerId,entityCode, paymentMode, bankBranchID, accountNumber,");
		sql.append(" receivableAdjustment, reconciliationAmount, active, partnerBankId, ");

		if (type.contains("view")) {
			sql.append(
					"entityDesc, providerDesc, branchDesc,bankName,ifscCode,micrCode,bankCode, partnerBankCode, partnerBankName,");
			sql.append(" branchCity, branchCode, ");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, ");
		sql.append(" RecordType, WorkflowId From VASProviderAccDetail");
		sql.append(type);
		sql.append(" Where ProviderId = :ProviderId AND EntityCode = :EntityCode");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ProviderId", providerId);
		source.addValue("EntityCode", entityCode);

		RowMapper<VASProviderAccDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VASProviderAccDetail.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT id, providerId,entityCode, paymentMode, bankBranchID, accountNumber,");
		sql.append(" receivableAdjustment, reconciliationAmount, active, partnerBankId, ");

		if (type.contains("view")) {
			sql.append(
					"entityDesc, providerDesc, branchDesc,bankName,ifscCode,micrCode,bankCode, partnerBankCode, partnerBankName,");
			sql.append(" branchCity, branchCode, ");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, ");
		sql.append(" RecordType, WorkflowId From VASProviderAccDetail");
		sql.append(type);
		sql.append(" Where ProviderId = :ProviderId");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ProviderId", providerId);

		VASProviderAccDetail vASProviderAccDetail = new VASProviderAccDetail();
		RowMapper<VASProviderAccDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VASProviderAccDetail.class);
		try {
			vASProviderAccDetail = jdbcTemplate.queryForObject(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			vASProviderAccDetail = null;
		}
		logger.debug(Literal.LEAVING);
		return vASProviderAccDetail;
	}
}
