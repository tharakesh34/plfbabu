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
 * FileName    		:  CustTypePANMappingDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2018    														*
 *                                                                  						*
 * Modified Date    :  30-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2018       Pennant	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.systemmasters.CustTypePANMappingDAO;
import com.pennant.backend.model.systemmasters.CustTypePANMapping;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>CustTypePANMapping</code> with set of CRUD operations.
 */
public class CustTypePANMappingDAOImpl extends SequenceDao<CustTypePANMapping> implements CustTypePANMappingDAO {
	private static Logger logger = LogManager.getLogger(CustTypePANMappingDAOImpl.class);

	public CustTypePANMappingDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record CustTypePANMapping Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustTypePANMapping
	 */
	@Override
	public CustTypePANMapping getCustTypePANMappingById(long mappingID, String type) {
		logger.debug(Literal.ENTERING);

		CustTypePANMapping custTypePANMapping = new CustTypePANMapping();
		custTypePANMapping.setMappingID(mappingID);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select MappingID, CustCategory, CustType, PANLetter, Active, Version, LastMntOn,");
		selectSql.append(" LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",CUSTTYPEDESC");
		}
		selectSql.append(" FROM  CustTypePANMapping");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where MappingID =:MappingID");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custTypePANMapping);
		RowMapper<CustTypePANMapping> typeRowMapper = BeanPropertyRowMapper.newInstance(CustTypePANMapping.class);

		try {
			custTypePANMapping = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			custTypePANMapping = null;
		}

		logger.debug(Literal.LEAVING);
		return custTypePANMapping;
	}

	@Override
	public boolean isDuplicateKey(long id, String custType, String pANLetter, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "CustType = :CustType and PANLetter = :PANLetter and MappingID != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CustTypePANMapping", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CustTypePANMapping_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CustTypePANMapping_Temp", "CustTypePANMapping" },
					whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("CustType", custType);
		paramSource.addValue("PANLetter", pANLetter);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(CustTypePANMapping custTypePANMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into CustTypePANMapping");
		sql.append(tableType.getSuffix());
		sql.append(" (MappingID, CustCategory, CustType, panLetter, Active, Version,");
		sql.append(" LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(" values (:MappingID, :CustCategory, :CustType, :panLetter, :Active, :Version,");
		sql.append(" :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (custTypePANMapping.getMappingID() <= 0) {
			custTypePANMapping.setMappingID(getNextValue("SeqCustTypePANMapping"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(custTypePANMapping);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(custTypePANMapping.getMappingID());
	}

	@Override
	public void update(CustTypePANMapping custTypePANMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update CustTypePANMapping");
		sql.append(tableType.getSuffix());
		sql.append(" set CustCategory = :CustCategory, CustType = :CustType, panLetter = :panLetter,");
		sql.append(" Active = :Active, Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where MappingID = :MappingID");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(custTypePANMapping);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(CustTypePANMapping custTypePANMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CustTypePANMapping");
		sql.append(tableType.getSuffix());
		sql.append(" where MappingID = :MappingID");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(custTypePANMapping);
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
	public CustTypePANMapping getApprovedPANMapping(CustTypePANMapping panMap, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MappingID, CustCategory, CustType, PANLetter, Active");
		sql.append(" From CustTypePANMapping");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustType = ? and CustCategory = ? and Active = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql.toString(),
					new Object[] { panMap.getCustType(), panMap.getCustCategory(), 1 }, (rs, i) -> {
						CustTypePANMapping custPanMap = new CustTypePANMapping();

						custPanMap.setMappingID(JdbcUtil.getLong(rs.getLong("MappingID")));
						custPanMap.setCustCategory(rs.getString("CustCategory"));
						custPanMap.setCustType(rs.getString("CustType"));
						custPanMap.setPanLetter(rs.getString("PANLetter"));
						custPanMap.setActive(rs.getBoolean("Active"));

						return custPanMap;
					});
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(
					"Record is not found in CustTypePANMapping{} for the specified CustType >> {}, CustCategory >> {}, Active >> 1",
					type, panMap.getCustType(), panMap.getCustCategory());
		}

		return null;
	}
}
