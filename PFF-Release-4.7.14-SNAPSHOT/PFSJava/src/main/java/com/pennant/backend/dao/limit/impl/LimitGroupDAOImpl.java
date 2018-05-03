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
 * FileName    		:  LimitGroupDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.limit.LimitGroupDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.limit.LimitGroup;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>LimitGroup model</b> class.<br>
 * 
 */
public class LimitGroupDAOImpl extends BasisCodeDAO<LimitGroup> implements LimitGroupDAO {

	private static Logger logger = Logger.getLogger(LimitGroupDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new LimitGroup 
	 * @return LimitGroup
	 */
	@Override
	public LimitGroup getLimitGroup() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LimitGroup");
		LimitGroup limitGroup = new LimitGroup();
		if (workFlowDetails != null) {
			limitGroup.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return limitGroup;
	}


	/**
	 * This method get the module from method getLimitGroup() and set the new record flag as true and return LimitGroup()   
	 * @return LimitGroup
	 */
	@Override
	public LimitGroup getNewLimitGroup() {
		logger.debug("Entering");
		LimitGroup limitGroup = getLimitGroup();
		limitGroup.setNewRecord(true);
		logger.debug("Leaving");
		return limitGroup;
	}

	/**
	 * Fetch the Record  Limit Group details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LimitGroup
	 */
	@Override
	public LimitGroup getLimitGroupById(final String id, String type) {
		logger.debug("Entering");
		LimitGroup limitGroup = getLimitGroup();
		
		limitGroup.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select LimitCategory,GroupCode, GroupName,Active,GroupOf");
		selectSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" From LimitGroup");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GroupCode =:GroupCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroup);
		RowMapper<LimitGroup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitGroup.class);
		
		try {
			limitGroup = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			limitGroup = null;
		}
		logger.debug("Leaving");
		return limitGroup;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the LimitGroup or LimitGroup_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Limit Group by key GroupCode
	 * 
	 * @param Limit Group (limitGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(LimitGroup limitGroup,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From LimitGroup");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where GroupCode =:GroupCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroup);
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
	 * This method insert new Records into LimitGroup or LimitGroup_Temp.
	 *
	 * save Limit Group 
	 * 
	 * @param Limit Group (limitGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(LimitGroup limitGroup,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into LimitGroup");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LimitCategory,GroupCode, GroupName,Active,GroupOf");
		insertSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:LimitCategory,:GroupCode, :GroupName,:Active,:GroupOf");
		insertSql.append(", :Version ,:CreatedBy, :CreatedOn, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroup);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return limitGroup.getId();
	}
	
	/**
	 * This method updates the Record LimitGroup or LimitGroup_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Limit Group by key GroupCode and Version
	 * 
	 * @param Limit Group (limitGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(LimitGroup limitGroup,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql = new StringBuilder("Update LimitGroup");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set LimitCategory =:LimitCategory, GroupName = :GroupName,Active =:Active, GroupOf =:GroupOf");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where GroupCode =:GroupCode");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroup);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
}