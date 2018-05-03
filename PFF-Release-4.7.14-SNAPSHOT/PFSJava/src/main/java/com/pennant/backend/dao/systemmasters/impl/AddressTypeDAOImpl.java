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
 * FileName    		:  AddressTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.systemmasters.AddressTypeDAO;
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>AddressType model</b> class.<br>
 */
public class AddressTypeDAOImpl extends BasicDao<AddressType> implements AddressTypeDAO {
	private static Logger logger = Logger.getLogger(AddressTypeDAOImpl.class);

	public AddressTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Address Type details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AddressType
	 */
	@Override
	public AddressType getAddressTypeById(final String id, String type) {
		logger.debug("Entering");
		AddressType addressType = new AddressType();
		addressType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT AddrTypeCode, AddrTypeDesc, AddrTypePriority, AddrTypeFIRequired, AddrTypeIsActive," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTAddressTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AddrTypeCode =:AddrTypeCode") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(addressType);
		RowMapper<AddressType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AddressType.class);

		try {
			addressType = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			addressType = null;
		}
		logger.debug("Leaving");
		return addressType;
	}
	
	@Override
	public boolean isDuplicateKey(String addressTypeCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "AddrTypeCode = :addressTypeCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTAddressTypes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTAddressTypes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BMTAddressTypes_Temp", "BMTAddressTypes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("addressTypeCode", addressTypeCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(AddressType addressType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into BMTAddressTypes");
		sql.append(tableType.getSuffix());
		sql.append(" (AddrTypeCode, AddrTypeDesc, AddrTypePriority, AddrTypeFIRequired, AddrTypeIsActive," );
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		sql.append(" RecordType, WorkflowId)");
		sql.append(" values(:AddrTypeCode, :AddrTypeDesc, :AddrTypePriority, :AddrTypeFIRequired, :AddrTypeIsActive, " );
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		sql.append(" :RecordType, :WorkflowId)");
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(addressType);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return addressType.getId();
	}
	
	@Override
	public void update(AddressType addressType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update BMTAddressTypes");
		sql.append(tableType.getSuffix());
		sql.append(" set AddrTypeDesc = :AddrTypeDesc," );
		sql.append(" AddrTypePriority = :AddrTypePriority, AddrTypeFIRequired = :AddrTypeFIRequired, AddrTypeIsActive = :AddrTypeIsActive ," );
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		sql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		sql.append(" where AddrTypeCode =:AddrTypeCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(addressType);
		int recordCount  = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}


	@Override
	public void delete(AddressType addressType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append("delete from BMTAddressTypes");
		sql.append(tableType.getSuffix());
		sql.append(" where AddrTypeCode = :AddrTypeCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL +  sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(addressType);
		int recordCount = 0;
		
		try {
			recordCount = jdbcTemplate.update(sql.toString(),paramSource);
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
	public List<String> getFiRequiredCodes() {
		List<String> codes;
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("addrtypefirequired", 1);

		try {
			codes = jdbcTemplate.queryForList(
					"select addrtypeCode from BMTAddressTypes where addrtypefirequired=:addrtypefirequired",
					parameterSource, String.class);
		} catch (Exception e) {
			codes = new ArrayList<>();
		}

		return codes;
	}	
}