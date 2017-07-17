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
 * FileName    		:  LimitStructureDAOImpl.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.limit.impl;


import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.limit.LimitStructureDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>LimitStructure model</b> class.<br>
 * 
 */

public class LimitStructureDAOImpl extends BasisCodeDAO<LimitStructure> implements LimitStructureDAO {

	private static Logger logger = Logger.getLogger(LimitStructureDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new LimitStructure 
	 * @return LimitStructure
	 */

	@Override
	public LimitStructure getLimitStructure() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LimitStructure");
		LimitStructure limitStructure = new LimitStructure();
		if (workFlowDetails != null){
			limitStructure.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return limitStructure;
	}


	/**
	 * This method get the module from method getLimitStructure() and set the new record flag as true and return LimitStructure()   
	 * @return LimitStructure
	 */


	@Override
	public LimitStructure getNewLimitStructure() {
		logger.debug("Entering");
		LimitStructure limitStructure = getLimitStructure();
		limitStructure.setNewRecord(true);
		logger.debug("Leaving");
		return limitStructure;
	}

	/**
	 * Fetch the Record  Limit Structure details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LimitStructure
	 */
	@Override
	public LimitStructure getLimitStructureById(final String id, String type) {
		logger.debug("Entering");
		LimitStructure limitStructure = getLimitStructure();
		
		limitStructure.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select StructureCode, StructureName,Active,LimitCategory,ShowLimitsIn");
		selectSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From LimitStructure");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where StructureCode =:StructureCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitStructure);
		RowMapper<LimitStructure> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitStructure.class);
		
		try {
			limitStructure = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			limitStructure = null;
		}
		logger.debug("Leaving");
		return limitStructure;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the LimitStructure or LimitStructure_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Limit Structure by key StructureCode
	 * 
	 * @param Limit Structure (limitStructure)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(LimitStructure limitStructure,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From LimitStructure");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where StructureCode =:StructureCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitStructure);
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
	 * This method insert new Records into LimitStructure or LimitStructure_Temp.
	 *
	 * save Limit Structure 
	 * 
	 * @param Limit Structure (limitStructure)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(LimitStructure limitStructure,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into LimitStructure");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (StructureCode, StructureName, Active,LimitCategory,ShowLimitsIn");
		insertSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:StructureCode, :StructureName, :Active,:LimitCategory, :ShowLimitsIn");
		insertSql.append(", :Version ,:CreatedBy, :CreatedOn, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitStructure);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return limitStructure.getId();
	}
	
	/**
	 * This method updates the Record LimitStructure or LimitStructure_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Limit Structure by key StructureCode and Version
	 * 
	 * @param Limit Structure (limitStructure)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(LimitStructure limitStructure,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update LimitStructure");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set StructureName = :StructureName, Active =:Active,LimitCategory =:LimitCategory, ShowLimitsIn = :ShowLimitsIn");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where StructureCode =:StructureCode");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitStructure);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for get total count of limit structure records.
	 * 
	 * @param structureCode
	 * @param tableType
	 * 
	 * @return integer
	 */
	@Override
	public int getLimitStructureCountById(String structureCode, String tableType) {
		logger.debug("Entering");
		
		LimitStructure limitStructure = new LimitStructure();
		limitStructure.setStructureCode(structureCode);
		
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) FROM LimitStructure ");
		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" WHERE StructureCode = :StructureCode");
		
		logger.debug("selectSql: " + selectSql.toString());

		int recordCount = 0;
		try {
			SqlParameterSource beanParams = new BeanPropertySqlParameterSource(limitStructure);
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParams, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");
		return recordCount;
	}
}