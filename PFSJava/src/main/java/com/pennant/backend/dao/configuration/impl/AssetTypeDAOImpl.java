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
 * FileName    		:  AssetTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2016    														*
 *                                                                  						*
 * Modified Date    :  14-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.configuration.impl;


import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.configuration.AssetTypeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.configuration.AssetType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
/**
 * DAO methods implementation for the <b>AssetType model</b> class.<br>
 * 
 */

public class AssetTypeDAOImpl extends BasisCodeDAO<AssetType> implements AssetTypeDAO {

	private static Logger logger = Logger.getLogger(AssetTypeDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


	public AssetTypeDAOImpl() {
		super();
	}


	/**
	 * This method set the Work Flow id based on the module name and return the new VASConfiguration 
	 * @return VASConfiguration
	 */
	@Override
	public AssetType getAssetType() {
		logger.debug("Entering");
		logger.debug("Leaving");
		return new AssetType();
	}


	/**
	 * This method get the module from method getVASConfiguration() and set the new record flag as true and return VASConfiguration()   
	 * @return VASConfiguration
	 */

	@Override
	public AssetType getNewAssetType() {
		logger.debug("Entering");
		AssetType aAssetType = getAssetType();
		aAssetType.setNewRecord(true);
		logger.debug("Leaving");
		return aAssetType;
	}
	/**
	 * Fetch the Record  AssetType details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return AssetType
	 */
	@Override
	public AssetType getAssetTypeById(final String id, String type) {
		logger.debug("Entering");
		AssetType assetType = new AssetType();
		assetType.setId(id);
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" assetType,assetDesc,remarks,active,preValidation,postValidation,");
		
		if(type.contains("View")){
			sql.append("");
		}	
		
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From AssetTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where AssetType =:AssetType");
		
		logger.debug("sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assetType);
		RowMapper<AssetType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AssetType.class);
		
		try{
			assetType = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			assetType = null;
		}
		logger.debug("Leaving");
		return assetType;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the AssetTypes or AssetTypes_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete AssetType by key AssetType
	 * 
	 * @param AssetType (assetType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(AssetType assetType,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder sql = new StringBuilder("Delete From AssetTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where AssetType =:AssetType");
	
		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assetType);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into AssetTypes or AssetTypes_Temp.
	 *
	 * save AssetType 
	 * 
	 * @param AssetType (assetType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(AssetType assetType,String type) {
		logger.debug("Entering");
		
		StringBuilder sql =new StringBuilder("Insert Into AssetTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (assetType,assetDesc,remarks,active,preValidation,postValidation,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" Values(");
		sql.append(" :assetType,:assetDesc,:remarks,:active,:preValidation,:postValidation,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("sql: " + sql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assetType);
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return assetType.getId();
	}
	
	/**
	 * This method updates the Record AssetTypes or AssetTypes_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update AssetType by key AssetType and Version
	 * 
	 * @param AssetType (assetType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(AssetType assetType,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	sql =new StringBuilder("Update AssetTypes");
		sql.append(StringUtils.trimToEmpty(type)); 
		sql.append(" Set assetType=:assetType, assetDesc=:assetDesc, remarks=:remarks,preValidation=:preValidation,postValidation=:postValidation,");
		sql.append(" active=:active,");
		sql.append(" Version=:Version , LastMntBy=:LastMntBy, LastMntOn=:LastMntOn, RecordStatus=:RecordStatus, RoleCode=:RoleCode, NextRoleCode=:NextRoleCode, TaskId=:TaskId, NextTaskId=:NextTaskId, RecordType=:RecordType, WorkflowId=:WorkflowId");
		sql.append(" Where AssetType =:AssetType");
		
		if (!type.endsWith("_Temp")){
			sql.append("  AND Version= :Version-1");
		}
		
		logger.debug("Sql: " + sql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assetType);
		recordCount = this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public int getAssignedAssets(String assetType) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append("Select  Count(*) from (SELECT   *  FROM   FinAssetTypes_Temp  UNION ALL ");
		sql.append("SELECT   *  FROM   FinAssetTypes ");
		sql.append("WHERE     NOT EXISTS (SELECT  1 FROM  FinAssetTypes_Temp WHERE  AssetType = FinAssetTypes.AssetType)) T ");
		sql.append("Where T.AssetType = :AssetType");

		source.addValue("AssetType", assetType);
		logger.debug("sql: " + sql.toString());

		try {
			return this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			assetType = null;
		}
		logger.debug("Leaving");
		return 0;
	}
}