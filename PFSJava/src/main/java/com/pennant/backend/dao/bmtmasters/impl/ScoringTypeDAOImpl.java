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
 * FileName    		:  ScoringTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-11-2011    														*
 *                                                                  						*
 * Modified Date    :  08-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.bmtmasters.impl;


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

import com.pennant.backend.dao.bmtmasters.ScoringTypeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.bmtmasters.ScoringType;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>ScoringType model</b> class.<br>
 * 
 */

public class ScoringTypeDAOImpl extends BasisCodeDAO<ScoringType> implements ScoringTypeDAO {

	private static Logger logger = Logger.getLogger(ScoringTypeDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public ScoringTypeDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new ScoringType 
	 * @return ScoringType
	 */

	@Override
	public ScoringType getScoringType() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ScoringType");
		ScoringType scoringType= new ScoringType();
		if (workFlowDetails!=null){
			scoringType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return scoringType;
	}


	/**
	 * This method get the module from method getScoringType() and set the new record flag as true and return ScoringType()   
	 * @return ScoringType
	 */


	@Override
	public ScoringType getNewScoringType() {
		logger.debug("Entering");
		ScoringType scoringType = getScoringType();
		scoringType.setNewRecord(true);
		logger.debug("Leaving");
		return scoringType;
	}

	/**
	 * Fetch the Record  SCoring Type Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ScoringType
	 */
	@Override
	public ScoringType getScoringTypeById(final String id, String type) {
		logger.debug("Entering");
		ScoringType scoringType = new ScoringType();		
		scoringType.setId(id);		
		StringBuilder selectSql = new StringBuilder("Select ScoType, ScoDesc");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" From BMTScoringType");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ScoType =:ScoType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringType);
		RowMapper<ScoringType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ScoringType.class);
		
		try{
			scoringType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			scoringType = null;
		}
		logger.debug("Leaving");
		return scoringType;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the BMTScoringType or BMTScoringType_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete SCoring Type Detail by key ScoType
	 * 
	 * @param SCoring Type Detail (scoringType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ScoringType scoringType,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From BMTScoringType");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ScoType =:ScoType");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringType);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into BMTScoringType or BMTScoringType_Temp.
	 *
	 * save SCoring Type Detail 
	 * 
	 * @param SCoring Type Detail (scoringType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(ScoringType scoringType,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into BMTScoringType");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ScoType, ScoDesc");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ScoType, :ScoDesc");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return scoringType.getId();
	}
	
	/**
	 * This method updates the Record BMTScoringType or BMTScoringType_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update SCoring Type Detail by key ScoType and Version
	 * 
	 * @param SCoring Type Detail (scoringType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(ScoringType scoringType,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update BMTScoringType");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ScoDesc = :ScoDesc");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ScoType =:ScoType");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}