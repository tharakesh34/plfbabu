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
 * FileName    		:  InterfaceMappingDAOImpl.java                                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-11-2017    														*
 *                                                                  						*
 * Modified Date    :     																	*	
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-11-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.interfacemapping.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.interfacemapping.InterfaceMappingDAO;
import com.pennant.backend.model.interfacemapping.InterfaceMapping;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class InterfaceMappingDAOImpl extends SequenceDao<InterfaceMapping> implements InterfaceMappingDAO {
	private static Logger logger = Logger.getLogger(InterfaceMappingDAOImpl.class);

	public InterfaceMappingDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record InterfaceMapping details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return InterfaceMapping
	 */
	@Override
	public InterfaceMapping getInterfaceMappingById(long id, String type) {
		logger.debug("Entering");
		InterfaceMapping interfaceMapping = new InterfaceMapping();
		interfaceMapping.setId(id);

		StringBuilder selectSql = new StringBuilder(
				"Select InterfaceMappingId,InterfaceId,InterfaceName,InterfaceField,MappingTable,MappingColumn,MappingValue,Active");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",MappingType, Module ");
		}
		selectSql.append(" From InterfaceMapping");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  InterfaceMappingId =:InterfaceMappingId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interfaceMapping);
		RowMapper<InterfaceMapping> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InterfaceMapping.class);

		try {
			interfaceMapping = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			interfaceMapping = null;
		}
		logger.debug("Leaving");

		return interfaceMapping;
	}

	/**
	 * This method updates the Record InterfaceMapping or InterfaceMapping_Temp.
	 * if Record not updated then throws DataAccessException with error 41004.
	 * update InterfaceMapping by key InterfaceMappingId and Version
	 * 
	 * @param InterfaceMapping
	 *            (interfaceMapping)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(InterfaceMapping interfaceMapping, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update InterfaceMapping");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set InterfaceName = :InterfaceName, InterfaceField = :InterfaceField, MappingTable = :MappingTable");
		updateSql.append(", MappingColumn = :MappingColumn, MappingValue = :MappingValue, Active = :Active");
		updateSql.append(
				", Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode");
		updateSql.append(
				" , NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where InterfaceMappingId =:InterfaceMappingId AND InterfaceId =:InterfaceId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interfaceMapping);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");

	}

	/**
	 * This method Deletes the Record from the InterfaceMapping or
	 * InterfaceMapping_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete InterfaceMapping by key
	 * InterfaceMappingId
	 * 
	 * @param InterfaceMapping
	 *            (interfaceMapping)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(InterfaceMapping interfaceMapping, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From InterfaceMapping");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where InterfaceMappingId =:InterfaceMappingId ");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interfaceMapping);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");

	}

	/**
	 * This method insert new Records into InterfaceMapping or
	 * InterfaceMapping_Temp. it fetches the available Sequence form
	 * InterfaceMapping by using getNextidviewDAO().getNextId() method.
	 * 
	 * save InterfaceMapping
	 * 
	 * @param InterfaceMapping
	 *            (interfaceMapping)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(InterfaceMapping interfaceMapping, String type) {
		logger.debug("Entering");

		if (interfaceMapping.getId() == Long.MIN_VALUE) {
			interfaceMapping.setId(getNextValue("SeqInterfaceMapping"));
			logger.debug("get NextID:" + interfaceMapping.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into InterfaceMapping");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (InterfaceMappingId,InterfaceId,InterfaceName,InterfaceField,MappingTable,MappingColumn,MappingValue,Active");
		insertSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:InterfaceMappingId,:InterfaceId,:InterfaceName,:InterfaceField,:MappingTable,:MappingColumn,:MappingValue,:Active");
		insertSql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interfaceMapping);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return interfaceMapping.getId();
	}

	/**
	 * method to return table column list
	 * 
	 * @param tableName
	 */
	@Override
	public List<String> getTableNameColumnsList(String tableName) {
		logger.debug("Entering");

		StringBuilder selectQry = new StringBuilder();
		if (com.pennanttech.pennapps.core.App.DATABASE == com.pennanttech.pennapps.core.App.Database.ORACLE) {
			selectQry.append("select COLUMN_NAME from USER_TAB_COLS where TABLE_NAME = :TABLE_NAME");
		}
		if (com.pennanttech.pennapps.core.App.DATABASE == com.pennanttech.pennapps.core.App.Database.SQL_SERVER) {
			selectQry.append("SELECT COLUMN_name FROM information_schema.columns WHERE  TABLE_NAME=:TABLE_NAME");
		}

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("TABLE_NAME", tableName);

		logger.debug("selectSql: " + selectQry);

		return this.jdbcTemplate.queryForList(selectQry.toString(), paramSource, String.class);
	}

	@Override
	public boolean isDuplicateKey(InterfaceMapping interfaceMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String whereClause;

		if (!StringUtils.isEmpty(interfaceMapping.getMappingValue())) {
			whereClause = "InterfaceId = :InterfaceId And MappingValue = :MappingValue";
		} else if (!StringUtils.isEmpty(interfaceMapping.getMappingTable())
				&& !StringUtils.isEmpty(interfaceMapping.getMappingColumn())) {
			whereClause = "InterfaceId = :InterfaceId And MappingTable = :MappingTable And MappingColumn = :MappingColumn";
		} else {
			whereClause = "InterfaceId = :InterfaceId And MappingTable is null And MappingColumn is null And MappingValue is null";
		}

		String sql;

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("InterfaceMapping", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("InterfaceMapping_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "InterfaceMapping_Temp", "InterfaceMapping" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("InterfaceId", interfaceMapping.getInterfaceId());
		paramSource.addValue("MappingTable", interfaceMapping.getMappingTable());
		paramSource.addValue("MappingValue", interfaceMapping.getMappingValue());
		paramSource.addValue("MappingColumn", interfaceMapping.getMappingColumn());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}