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
 * FileName    		:  MasterMappingDAOImpl.java                                         * 	  
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

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.interfacemapping.MasterMappingDAO;
import com.pennant.backend.model.interfacemapping.MasterMapping;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

public class MasterMappingDAOImpl extends BasisNextidDaoImpl<MasterMapping> implements MasterMappingDAO {
	private static Logger				logger	= Logger.getLogger(MasterMappingDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public MasterMappingDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record MasterMapping details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return MasterMapping
	 */
	@Override
	public List<MasterMapping> getMasterMappingDetails(long interfaceMappingId, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("InterfaceMappingId", interfaceMappingId);

		StringBuilder selectSql = new StringBuilder("Select MasterMappingId, InterfaceMappingId, PLFValue, InterfaceValue");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From MasterMapping");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  InterfaceMappingId = :InterfaceMappingId");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<MasterMapping> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MasterMapping.class);

		List<MasterMapping> mappings = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

		logger.debug("Leaving");

		return mappings;
	}

	/**
	 * This method updates the Record MasterMapping or MasterMapping_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update MasterMapping by key MasterMappingId and Version
	 * 
	 * @param MasterMapping
	 *            (MasterMapping)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(MasterMapping masterMapping, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update MasterMapping");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set PlfValue = :PlfValue,");
		updateSql.append(" InterfaceValue = :InterfaceValue, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where MasterMappingId = :MasterMappingId And InterfaceMappingId = :InterfaceMappingId ");
		
		/*if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}*/
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(masterMapping);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the MasterMapping or MasterMapping_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete MasterMapping by key MasterMappingId
	 * 
	 * @param MasterMapping
	 *            (MasterMapping)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(MasterMapping masterMapping, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder("Delete From MasterMapping");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  MasterMappingId =:MasterMappingId and InterfaceMappingId =:InterfaceMappingId ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(masterMapping);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");

	}

	/**
	 * This method insert new Records into MasterMapping or MasterMapping_Temp. it fetches the available Sequence form
	 * MasterMapping by using getNextidviewDAO().getNextId() method.
	 * 
	 * save MasterMapping
	 * 
	 * @param MasterMapping
	 *            (MasterMapping)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(MasterMapping masterMapping, String tableType) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();

		if (masterMapping.getMasterMappingId() == Long.MIN_VALUE) {
			masterMapping.setMasterMappingId(getNextidviewDAO().getNextId("SeqMasterMapping"));
			logger.debug("get NextID:" + masterMapping.getMasterMappingId());
		}
		insertSql.append("Insert Into MasterMapping");
		insertSql.append(StringUtils.trimToEmpty(tableType));
		insertSql.append(" (MasterMappingId,InterfaceMappingId,PlfValue,InterfaceValue");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		insertSql.append(", RecordType, WorkflowId)");
		insertSql.append(" Values(:MasterMappingId,:InterfaceMappingId, :PlfValue , :InterfaceValue");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId ");
		insertSql.append(", :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(masterMapping);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return masterMapping.getId();
	}

	@Override
	public void delete(long checkListId, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From MasterMapping");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where InterfaceMappingId =:InterfaceMappingId");

		logger.debug("deleteSql: " + deleteSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("InterfaceMappingId", checkListId);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
		
	
	}
	
	/**
	 * method to return list of String based on tablename and value 
	 * @param tableName
	 * @param value
	 */
	
	@Override
	public List<String> getMappings(String tableName, String value) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectQry = new StringBuilder();
		selectQry.append("select ");
		selectQry.append(value);
		selectQry.append(" from ");
		selectQry.append(tableName);
		
		logger.debug("selectSql: " + selectQry);
		
		return this.namedParameterJdbcTemplate.queryForList(selectQry.toString(), source, String.class);
	}
	
	/**
	 * TO Set DataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
