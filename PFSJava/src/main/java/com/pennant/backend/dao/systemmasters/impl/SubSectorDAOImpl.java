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
 * FileName    		:  SubSectorDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.systemmasters.SubSectorDAO;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>SubSector model</b> class.<br>
 * 
 */
public class SubSectorDAOImpl extends BasicDao<SubSector> implements SubSectorDAO {
	private static Logger logger = LogManager.getLogger(SubSectorDAOImpl.class);

	public SubSectorDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Sub Sectors details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return SubSector
	 */
	@Override
	public SubSector getSubSectorById(final String id, String subSectorCode, String type) {
		logger.debug(Literal.ENTERING);

		SubSector subSector = new SubSector();
		subSector.setId(id);
		subSector.setSubSectorCode(subSectorCode);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT SectorCode, SubSectorCode, SubSectorDesc, SubSectorIsActive,");
		if (type.contains("View")) {
			selectSql.append("lovDescSectorCodeName,");
		}
		selectSql.append(" Version, LastMntBy , LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From BMTSubSectors");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SectorCode=:sectorCode AND SubSectorCode=:subSectorCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subSector);
		RowMapper<SubSector> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SubSector.class);

		try {
			subSector = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			subSector = null;
		}

		logger.debug(Literal.LEAVING);
		return subSector;
	}

	@Override
	public SubSector getSubSectorBySubSectorCode(String subSectorCode, String type) {
		logger.debug(Literal.ENTERING);

		SubSector subSector = new SubSector();
		subSector.setSubSectorCode(subSectorCode);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT SectorCode, SubSectorCode, SubSectorDesc, SubSectorIsActive,");
		if (type.contains("View")) {
			selectSql.append("lovDescSectorCodeName,");
		}
		selectSql.append(" Version, LastMntBy , LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From BMTSubSectors");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SubSectorCode=:subSectorCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subSector);
		RowMapper<SubSector> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SubSector.class);

		try {
			subSector = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			subSector = null;
		}

		logger.debug(Literal.LEAVING);
		return subSector;
	}

	@Override
	public boolean isDuplicateKey(String sectorCode, String subSectorCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "SectorCode = :sectorCode and SubSectorCode = :subSectorCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTSubSectors", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTSubSectors_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BMTSubSectors_Temp", "BMTSubSectors" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("sectorCode", sectorCode);
		paramSource.addValue("subSectorCode", subSectorCode);
		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(SubSector subSector, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into BMTSubSectors");
		sql.append(tableType.getSuffix());
		sql.append(" (SectorCode, SubSectorCode, SubSectorDesc, SubSectorIsActive,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(" values(:SectorCode, :SubSectorCode, :SubSectorDesc, :SubSectorIsActive,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(subSector);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return subSector.getId();
	}

	@Override
	public void update(SubSector subSector, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update BMTSubSectors");
		sql.append(tableType.getSuffix());
		sql.append(" set SubSectorDesc = :SubSectorDesc,");
		sql.append(" SubSectorIsActive = :SubSectorIsActive,");
		sql.append(
				" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		sql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		sql.append(" where SectorCode =:SectorCode AND SubSectorCode=:subSectorCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(subSector);
		int recordCount = this.jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(SubSector subSector, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" delete from BMTSubSectors");
		sql.append(tableType.getSuffix());
		sql.append(" where SectorCode =:SectorCode AND SubSectorCode=:subSectorCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(subSector);
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

}