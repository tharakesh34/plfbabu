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
 * FileName    		:  ScoringGroupDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-12-2011    														*
 *                                                                  						*
 * Modified Date    :  05-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters.impl;


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

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.rmtmasters.ScoringGroupDAO;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>ScoringGroup model</b> class.<br>
 * 
 */

public class ScoringGroupDAOImpl extends BasisNextidDaoImpl<ScoringGroup> implements ScoringGroupDAO {

	private static Logger logger = Logger.getLogger(ScoringGroupDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public ScoringGroupDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record  Scoring Group Detail details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ScoringGroup
	 */
	@Override
	public ScoringGroup getScoringGroupById(final long id, String type) {
		logger.debug("Entering");
		ScoringGroup scoringGroup = new ScoringGroup();
		
		scoringGroup.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select ScoreGroupId, ScoreGroupCode, ScoreGroupName, CategoryType, MinScore, IsOverride, OverrideScore");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From RMTScoringGroup");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ScoreGroupId =:ScoreGroupId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringGroup);
		RowMapper<ScoringGroup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ScoringGroup.class);
		
		try{
			scoringGroup = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			scoringGroup = null;
		}
		logger.debug("Leaving");
		return scoringGroup;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the RMTScoringGroup or RMTScoringGroup_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Scoring Group Detail by key ScoreGroupId
	 * 
	 * @param Scoring Group Detail (scoringGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ScoringGroup scoringGroup,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From RMTScoringGroup");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ScoreGroupId =:ScoreGroupId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringGroup);
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
	 * This method insert new Records into RMTScoringGroup or RMTScoringGroup_Temp.
	 * it fetches the available Sequence form SeqRMTScoringGroup by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Scoring Group Detail 
	 * 
	 * @param Scoring Group Detail (scoringGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(ScoringGroup scoringGroup,String type) {
		logger.debug("Entering");
		if (scoringGroup.getId()==Long.MIN_VALUE){
			scoringGroup.setId(getNextidviewDAO().getNextId("SeqRMTScoringGroup"));
			logger.debug("get NextID:"+scoringGroup.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into RMTScoringGroup");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ScoreGroupId, ScoreGroupCode, ScoreGroupName,CategoryType, MinScore, IsOverride, OverrideScore");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ScoreGroupId, :ScoreGroupCode, :ScoreGroupName, :CategoryType, :MinScore, :IsOverride, :OverrideScore");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringGroup);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return scoringGroup.getId();
	}
	
	/**
	 * This method updates the Record RMTScoringGroup or RMTScoringGroup_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Scoring Group Detail by key ScoreGroupId and Version
	 * 
	 * @param Scoring Group Detail (scoringGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(ScoringGroup scoringGroup,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update RMTScoringGroup");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ScoreGroupCode = :ScoreGroupCode, ScoreGroupName = :ScoreGroupName,CategoryType=:CategoryType, MinScore = :MinScore, IsOverride = :IsOverride, OverrideScore = :OverrideScore");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ScoreGroupId =:ScoreGroupId");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringGroup);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
}