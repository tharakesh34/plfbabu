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
 * FileName    		:  VehicleVersionDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.amtmasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.amtmasters.VehicleVersionDAO;
import com.pennant.backend.model.amtmasters.VehicleVersion;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>VehicleVersion model</b> class.<br>
 */
public class VehicleVersionDAOImpl extends SequenceDao<VehicleVersion> implements VehicleVersionDAO {
	private static Logger logger = Logger.getLogger(VehicleVersionDAOImpl.class);

	
	public VehicleVersionDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record  Vehicle Version Details details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VehicleVersion
	 */
	@Override
	public VehicleVersion getVehicleVersionById(final long id, String type) {
		logger.debug("Entering");
		VehicleVersion vehicleVersion = new VehicleVersion();
		vehicleVersion.setId(id);
		
		StringBuilder   selectSql = new StringBuilder  ("SELECT VehicleVersionId, VehicleModelId,");
		selectSql.append(" VehicleDoors, VehicleCc, VehicleVersionCode, VehicleCategory, ");
		if(type.contains("View")){
			selectSql.append("lovDescVehicleModelDesc,");
		}

		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  AMTVehicleVersion");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where VehicleVersionId =:VehicleVersionId ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleVersion);
		RowMapper<VehicleVersion> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VehicleVersion.class);

		try{
			vehicleVersion = this.jdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vehicleVersion = null;
		}
		logger.debug("Leaving");
		return vehicleVersion;
	}


	/**
	 * This method Deletes the Record from the AMTVehicleVersion or
	 * AMTVehicleVersion_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Vehicle Version Details by
	 * key VehicleVersionId
	 * 
	 * @param Vehicle
	 *            Version Details (vehicleVersion)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(VehicleVersion vehicleVersion, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From AMTVehicleVersion");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where VehicleVersionId =:VehicleVersionId ");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleVersion);
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
	 * This method insert new Records into AMTVehicleVersion or
	 * AMTVehicleVersion_Temp. it fetches the available Sequence form
	 * SeqAMTVehicleVersion by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Vehicle Version Details
	 * 
	 * @param Vehicle
	 *            Version Details (vehicleVersion)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(VehicleVersion vehicleVersion,String type) {
		logger.debug("Entering");
		if (vehicleVersion.getId()==Long.MIN_VALUE){
			vehicleVersion.setId(getNextId("SeqAMTVehicleVersion"));
			logger.debug("get NextID:"+vehicleVersion.getId());
		}
		StringBuilder insertSql = new StringBuilder();

		insertSql.append(" Insert Into AMTVehicleVersion");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(VehicleVersionId, VehicleModelId, VehicleVersionCode, VehicleDoors,VehicleCc, VehicleCategory, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values");
		insertSql.append("(:VehicleVersionId, :VehicleModelId, :VehicleVersionCode,:VehicleDoors,:VehicleCc, :VehicleCategory,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleVersion);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vehicleVersion.getId();
	}

	/**
	 * This method updates the Record AMTVehicleVersion or
	 * AMTVehicleVersion_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Vehicle Version Details by
	 * key VehicleVersionId and Version
	 * 
	 * @param Vehicle
	 *            Version Details (vehicleVersion)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(VehicleVersion vehicleVersion, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update AMTVehicleVersion");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set VehicleModelId = :VehicleModelId,");
		updateSql.append(" VehicleVersionCode = :VehicleVersionCode, VehicleDoors = :VehicleDoors, VehicleCc = :VehicleCc,");
		updateSql.append(" VehicleCategory = :VehicleCategory, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, ");
		updateSql.append(" NextRoleCode = :NextRoleCode,TaskId = :TaskId, ");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where VehicleVersionId =:VehicleVersionId");

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleVersion);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (!type.endsWith("_Temp")) {
			updateSql.append("AND Version= :Version-1");
		}
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Record  Vehicle Version Details details by VersionModel And VersionCode
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VehicleVersion
	 */
	@Override
    public VehicleVersion getVehicleVersionByType(VehicleVersion vehicleVersion, String type) {
		logger.debug("Entering");
		
		StringBuilder   selectSql = new StringBuilder  ("SELECT VehicleVersionId, VehicleModelId,");
		selectSql.append(" VehicleDoors, VehicleCc, VehicleVersionCode, VehicleCategory, ");
		if(type.contains("View")){
			selectSql.append("lovDescVehicleModelDesc,");
		}

		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  AMTVehicleVersion");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where VehicleVersionId != :VehicleVersionId AND VehicleModelId =:VehicleModelId AND VehicleVersionCode =:VehicleVersionCode  ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleVersion);
		RowMapper<VehicleVersion> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VehicleVersion.class);

		try{
			vehicleVersion = this.jdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vehicleVersion = null;
		}
		logger.debug("Leaving");
		return vehicleVersion;
	}
}