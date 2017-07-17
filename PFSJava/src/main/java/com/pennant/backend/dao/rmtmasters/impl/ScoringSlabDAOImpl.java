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
 * FileName    		:  ScoringSlabDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.rmtmasters.ScoringSlabDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>ScoringSlab model</b> class.<br>
 * 
 */

public class ScoringSlabDAOImpl extends BasisNextidDaoImpl<ScoringSlab> implements ScoringSlabDAO {

	private static Logger logger = Logger.getLogger(ScoringSlabDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ScoringSlabDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new ScoringSlab 
	 * @return ScoringSlab
	 */

	@Override
	public ScoringSlab getScoringSlab() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ScoringSlab");
		ScoringSlab scoringSlab= new ScoringSlab();
		if (workFlowDetails!=null){
			scoringSlab.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return scoringSlab;
	}

	/**
	 * This method get the module from method getScoringSlab() and set the new record flag as true and return ScoringSlab()   
	 * @return ScoringSlab
	 */

	@Override
	public ScoringSlab getNewScoringSlab() {
		logger.debug("Entering");
		ScoringSlab scoringSlab = getScoringSlab();
		scoringSlab.setNewRecord(true);
		logger.debug("Leaving");
		return scoringSlab;
	}

	/**
	 * Fetch the Record  Scoring Slab Details details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ScoringSlab
	 */
	@Override
	public ScoringSlab getScoringSlabById(final long id, String type) {
		logger.debug("Entering");
		ScoringSlab scoringSlab = new ScoringSlab();

		scoringSlab.setId(id);

		StringBuilder selectSql = new StringBuilder("Select ScoreGroupId, ScoringSlab, CreditWorthness");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
		selectSql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescScoreGroupCode");
		}
		selectSql.append(" From RMTScoringSlab");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ScoreGroupId =:ScoreGroupId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringSlab);
		RowMapper<ScoringSlab> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ScoringSlab.class);

		try{
			scoringSlab = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString()
					, beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			scoringSlab = null;
		}
		logger.debug("Leaving");
		return scoringSlab;
	}

	public List<ScoringSlab> getScoringSlabsByScoreGrpId(final long scoreGrpId, String type) {
		logger.debug("Entering");
		ScoringSlab scoringSlab = new ScoringSlab();
		scoringSlab.setScoreGroupId(scoreGrpId);
		List<ScoringSlab>  scoringSlabList;

		StringBuilder	selectSql =new StringBuilder("Select ScoreGroupId, ScoringSlab, CreditWorthness");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		selectSql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescScoreGroupCode");
		}
		selectSql.append(" From RMTScoringSlab");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ScoreGroupId =:ScoreGroupId ORDER BY ScoringSlab Desc ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringSlab);
		RowMapper<ScoringSlab> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ScoringSlab.class);

		try{
			scoringSlabList= this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			scoringSlabList = null;
		}
		logger.debug("Leaving");
		return scoringSlabList;
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTScoringSlab or RMTScoringSlab_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Scoring Slab Details by key ScoreGroupId
	 * 
	 * @param Scoring Slab Details (scoringSlab)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ScoringSlab scoringSlab,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From RMTScoringSlab");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ScoreGroupId =:ScoreGroupId AND ScoringSlab = :ScoringSlab");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringSlab);
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
	 * This method Deletes the Record from the RMTScoringSlab or RMTScoringSlab_Temp.
	 * delete Scoring Slab Details by key ScoreGroupId
	 * 
	 */

	public void delete(long scoreGroupId,String type) {
		logger.debug("Entering");
		ScoringSlab scoringSlab=new ScoringSlab();
		scoringSlab.setScoreGroupId(scoreGroupId);
		StringBuilder deleteSql = new StringBuilder("Delete From RMTScoringSlab");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ScoreGroupId =:ScoreGroupId");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringSlab);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	/**
	 * This method insert new Records into RMTScoringSlab or RMTScoringSlab_Temp.
	 * it fetches the available Sequence form SeqRMTScoringSlab by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Scoring Slab Details 
	 * 
	 * @param Scoring Slab Details (scoringSlab)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(ScoringSlab scoringSlab,String type) {
		logger.debug("Entering");
		if (scoringSlab.getId()==Long.MIN_VALUE){
			scoringSlab.setId(getNextidviewDAO().getNextId("SeqRMTScoringSlab"));
			logger.debug("get NextID:"+scoringSlab.getId());
		}

		StringBuilder insertSql =new StringBuilder("Insert Into RMTScoringSlab");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ScoreGroupId, ScoringSlab, CreditWorthness");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		insertSql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ScoreGroupId, :ScoringSlab, :CreditWorthness");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode");
		insertSql.append(", :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringSlab);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return scoringSlab.getId();
	}

	/**
	 * This method updates the Record RMTScoringSlab or RMTScoringSlab_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Scoring Slab Details by key ScoreGroupId and Version
	 * 
	 * @param Scoring Slab Details (scoringSlab)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(ScoringSlab scoringSlab,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update RMTScoringSlab");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set CreditWorthness = :CreditWorthness");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		updateSql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		updateSql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ScoreGroupId =:ScoreGroupId AND ScoringSlab = :ScoringSlab");

		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringSlab);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}