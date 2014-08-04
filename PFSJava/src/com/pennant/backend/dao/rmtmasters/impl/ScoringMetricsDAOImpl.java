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
 * FileName    		:  ScoringMetricsDAOImpl.java                                                   * 	  
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


import java.util.List;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rmtmasters.ScoringMetricsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>ScoringMetrics model</b> class.<br>
 * 
 */

public class ScoringMetricsDAOImpl extends BasisCodeDAO<ScoringMetrics> implements ScoringMetricsDAO {

	private static Logger logger = Logger.getLogger(ScoringMetricsDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new ScoringMetrics 
	 * @return ScoringMetrics
	 */

	@Override
	public ScoringMetrics getScoringMetrics() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ScoringMetrics");
		ScoringMetrics scoringMetrics= new ScoringMetrics();
		if (workFlowDetails!=null){
			scoringMetrics.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return scoringMetrics;
	}


	/**
	 * This method get the module from method getScoringMetrics() 
	 * and set the new record flag as true and return ScoringMetrics()   
	 * @return ScoringMetrics
	 */


	@Override
	public ScoringMetrics getNewScoringMetrics() {
		logger.debug("Entering");
		ScoringMetrics scoringMetrics = getScoringMetrics();
		scoringMetrics.setNewRecord(true);
		logger.debug("Leaving");
		return scoringMetrics;
	}

	/**
	 * Fetch the Record  Scoring Metrics Details details by key fields
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ScoringMetrics
	 */
	@Override
	public ScoringMetrics getScoringMetricsById(final String id, String type) {
		logger.debug("Entering");
		ScoringMetrics scoringMetrics = new ScoringMetrics();
		scoringMetrics.setId(0);

		StringBuilder selectSql = new StringBuilder("Select ScoreGroupId, ScoringId, CategoryType, ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescScoringCode,lovDescScoringCodeDesc,lovDescScoreMetricSeq, lovDescSQLRule, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTScoringMetrics");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ScoreGroupId =:ScoreGroupId and ScoringId=:ScoringId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);
		RowMapper<ScoringMetrics> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ScoringMetrics.class);

		try{
			scoringMetrics = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			scoringMetrics = null;
		}
		logger.debug("Leaving");
		return scoringMetrics;
	}
	/**
	 * This method returns the  List<ScoringMetrics>  by key field
	 */
	public List<ScoringMetrics> getScoringMetricsByScoreGrpId(final long scoreGrpId,String categoryType, String type) {
		logger.debug("Entering");
		ScoringMetrics scoringMetrics = new ScoringMetrics();
		scoringMetrics.setScoreGroupId(scoreGrpId);
		scoringMetrics.setCategoryType(categoryType);

		StringBuilder selectSql = new StringBuilder("Select ScoreGroupId, ScoringId, CategoryType, ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescScoringCode,lovDescScoringCodeDesc,lovDescScoreMetricSeq, lovDescSQLRule, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTScoringMetrics");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ScoreGroupId =:ScoreGroupId AND CategoryType =:CategoryType ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);
		RowMapper<ScoringMetrics> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ScoringMetrics.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * This method initialize the Record.
	 * @param ScoringMetrics (scoringMetrics)
	 * @return ScoringMetrics
	 */
	@Override
	public void initialize(ScoringMetrics scoringMetrics) {
		super.initialize(scoringMetrics);
	}
	/**
	 * This method refresh the Record.
	 * @param ScoringMetrics (scoringMetrics)
	 * @return void
	 */
	@Override
	public void refresh(ScoringMetrics scoringMetrics) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTScoringMetrics or RMTScoringMetrics_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Scoring Metrics Details by key ScoreGroupId and ScoringId
	 * 
	 * @param Scoring Metrics Details (scoringMetrics)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(ScoringMetrics scoringMetrics,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From RMTScoringMetrics");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ScoreGroupId =:ScoreGroupId AND ScoringId=:ScoringId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",scoringMetrics.getLovDescScoringCode()
						,scoringMetrics.getLovDescScoringGroupCode()
						,scoringMetrics.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",scoringMetrics.getLovDescScoringCode()
					,scoringMetrics.getLovDescScoringGroupCode()
					,scoringMetrics.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	/**
	 * This method delete the record by scoreGroupId
	 */
	public void delete(long  scoreGroupId,String type) {
		logger.debug("Entering");
		ScoringMetrics scoringMetrics=new ScoringMetrics();
		scoringMetrics.setScoreGroupId(scoreGroupId);
		
		StringBuilder deleteSql = new StringBuilder("Delete From RMTScoringMetrics");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ScoreGroupId =:ScoreGroupId");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	}
	/**
	 * This method insert new Records into RMTScoringMetrics or RMTScoringMetrics_Temp.
	 *
	 * save Scoring Metrics Details 
	 * 
	 * @param Scoring Metrics Details (scoringMetrics)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(ScoringMetrics scoringMetrics,String type) {
		logger.debug("Entering");

		StringBuilder insertSql =new StringBuilder("Insert Into RMTScoringMetrics");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ScoreGroupId, ScoringId, CategoryType, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode , " );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ScoreGroupId, :ScoringId, :CategoryType, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return String.valueOf(scoringMetrics.getId());
	}

	/**
	 * This method updates the Record RMTScoringMetrics or RMTScoringMetrics_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Scoring Metrics Details by key ScoreGroupId and Version
	 * 
	 * @param Scoring Metrics Details (scoringMetrics)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(ScoringMetrics scoringMetrics,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update RMTScoringMetrics");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ScoreGroupId = :ScoreGroupId, ScoringId = :ScoringId, CategoryType =:CategoryType, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ScoreGroupId =:ScoreGroupId AND  ScoringId = :ScoringId");

		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",String.valueOf(scoringMetrics.getLovDescScoringCode())
					,scoringMetrics.getLovDescScoringGroupCode()
					,scoringMetrics.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails  getError(String errorId, String ScoreMetricCode,String scoringGroupCode, String userLanguage){

		String[][] parms= new String[2][2];
		parms[1][0] = ScoreMetricCode;
		parms[1][1] = scoringGroupCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_ScoringMetricCode")+": "+parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_ScoreGroupCode")+": "+parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}


}