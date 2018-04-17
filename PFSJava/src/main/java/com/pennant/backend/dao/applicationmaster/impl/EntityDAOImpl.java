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
 * FileName    		:  EntityDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-06-2017    														*
 *                                                                  						*
 * Modified Date    :  15-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-06-2017       PENNANT	                 0.1                                            * 
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

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>Entity</code> with set of CRUD operations.
 */
public class EntityDAOImpl extends BasisCodeDAO<Entity> implements EntityDAO {
	private static Logger				logger	= Logger.getLogger(EntityDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public EntityDAOImpl() {
		super();
	}
	
	@Override
	public Entity getEntity(String entityCode,String type) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" entityCode, entityDesc, pANNumber, country, stateCode, cityCode, ");
		sql.append(" pinCode,entityAddrLine1,entityAddrLine2,entityAddrHNbr,entityFlatNbr,entityAddrStreet,entityPOBox,active,gstinAvailable,");
		if(type.contains("View")){
			sql.append(" countryname,ProvinceName,CItyName,pincodename," );
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,cINNumber" );
		
		sql.append(" From Entity");
		sql.append(type);
		sql.append(" Where entityCode = :entityCode");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		Entity entity = new Entity();
		entity.setEntityCode(entityCode);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(entity);
		RowMapper<Entity> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Entity.class);

		try {
			entity = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			entity = null;
		}

		logger.debug(Literal.LEAVING);
		return entity;
	}		
	
	@Override
	public String save(Entity entity,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into Entity");
		sql.append(tableType.getSuffix());
		sql.append("(entityCode, entityDesc, pANNumber, country, stateCode, cityCode, ");
		sql.append(" pinCode,entityAddrLine1,entityAddrLine2,entityAddrHNbr,entityFlatNbr,entityAddrStreet,entityPOBox, active, gstinAvailable, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,cINNumber)" );
		sql.append(" values(");
		sql.append(" :entityCode, :entityDesc, :pANNumber, :country, :stateCode, :cityCode, ");
		sql.append(" :pinCode,:entityAddrLine1,:entityAddrLine2,:entityAddrHNbr,:entityFlatNbr,:entityAddrStreet,:entityPOBox,:active, :gstinAvailable,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId,:cINNumber)");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(entity);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(entity.getEntityCode());
	}	

	@Override
	public void update(Entity entity,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update Entity" );
		sql.append(tableType.getSuffix());
		sql.append("  set entityDesc = :entityDesc, pANNumber = :pANNumber, country = :country, ");
		sql.append(" stateCode = :stateCode, cityCode = :cityCode, pinCode = :pinCode,entityAddrLine1=:entityAddrLine1,entityAddrLine2=:entityAddrLine2,");
		sql.append("entityAddrHNbr=:entityAddrHNbr,entityFlatNbr=:entityFlatNbr,entityAddrStreet=:entityAddrStreet,entityPOBox=:entityPOBox,");
		sql.append(" active = :active, gstinAvailable = :gstinAvailable,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId,cINNumber =:cINNumber");
		sql.append(" where entityCode = :entityCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(entity);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(Entity entity, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from Entity");
		sql.append(tableType.getSuffix());
		sql.append(" where entityCode = :entityCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(entity);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	@Override
	public boolean  count(String entityCode, String pANNumber, TableType tableType){
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = null;
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		if(StringUtils.isNotBlank(entityCode)) {
			whereClause = "entityCode = :entityCode " ;
			paramSource.addValue("entityCode", entityCode);
		} else if(StringUtils.isNotBlank(pANNumber)) {
			whereClause = "pANNumber = :pANNumber " ;
			paramSource.addValue("pANNumber", pANNumber);
		}

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Entity", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Entity_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Entity_Temp", "Entity" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public boolean panNumberExist(String pANNumber, String entityCode, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "pANNumber = :pANNumber AND entityCode = :entityCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Entity", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Entity_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Entity_Temp", "Entity" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("pANNumber", pANNumber);
		paramSource.addValue("entityCode", entityCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
}	