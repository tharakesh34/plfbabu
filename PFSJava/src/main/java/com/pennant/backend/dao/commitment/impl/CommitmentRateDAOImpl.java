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
 * FileName    		:  CommitmentRateDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-12-2016    														*
 *                                                                  						*
 * Modified Date    :  22-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.commitment.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.pennant.backend.dao.commitment.CommitmentRateDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.commitment.CommitmentRate;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
/**
 * DAO methods implementation for the <b>CommitmentRate model</b> class.<br>
 * 
 */

public class CommitmentRateDAOImpl extends BasisCodeDAO<CommitmentRate> implements CommitmentRateDAO {

	private static Logger logger = Logger.getLogger(CommitmentRateDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


	public CommitmentRateDAOImpl() {
		super();
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new CommitmentRate 
	 * @return CommitmentRate
	 */
	@Override
	public CommitmentRate getCommitmentRate() {
		logger.debug("Entering");

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CommitmentRate");
		CommitmentRate commitmentRate= new CommitmentRate();
		if (workFlowDetails != null) {
			commitmentRate.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		logger.debug("Leaving");
		return commitmentRate;
	}

	/**
	 * This method get the module from method getCommitmentRate() and set the
	 * new record flag as true and return CommitmentRate()
	 * 
	 * @return CommitmentRate
	 */
	@Override
	public CommitmentRate getNewCommitmentRate() {
		logger.debug("Entering");

		CommitmentRate commitmentRate = getCommitmentRate();
		commitmentRate.setNewRecord(true);

		logger.debug("Leaving");
		return commitmentRate;
	}

	/**
	 * Fetch the Record  CommitmentRate details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CommitmentRate
	 */
	@Override
	public CommitmentRate getCommitmentRateById(String cmtReference, final String id, String type) {
		logger.debug("Entering");

		CommitmentRate commitmentRate = new CommitmentRate();
		commitmentRate.setCmtReference(cmtReference);
		commitmentRate.setId(id);

		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" CmtReference, CmtRvwFrq, CmtBaseRate, CmtMargin, CmtSpecialRate, CmtActualRate,CmtCalculatedRate, ");
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );

		if(type.contains("View")){
			sql.append(", CmtBaseRateName");
		}	
		sql.append(" From CommitmentRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CmtReference = :CmtReference AND CmtRvwFrq = :CmtRvwFrq");

		logger.debug("sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentRate);
		RowMapper<CommitmentRate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CommitmentRate.class);

		try{
			commitmentRate = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			commitmentRate = null;
		}

		logger.debug("Leaving");
		return commitmentRate;
	}

	/**
	 * Fetch the Record  CommitmentRate details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CommitmentRate
	 */
	@Override
	public List<CommitmentRate> getCommitmentRatesByCmtRef(final String cmtReference, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" CmtReference, CmtRvwFrq, CmtBaseRate, CmtMargin, CmtSpecialRate, CmtActualRate,CmtCalculatedRate, ");
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );

		if(type.contains("View")){
			sql.append(", CmtBaseRateName");
		}	
		sql.append(" From CommitmentRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CmtReference = :CmtReference");

		logger.debug("sql: " + sql.toString());

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("CmtReference", cmtReference);

		RowMapper<CommitmentRate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CommitmentRate.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(sql.toString(),parameterMap, typeRowMapper);
	}

	/**
	 * This method Deletes the Record from the CommitmentRates or CommitmentRates_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete CommitmentRate by key CmtRvwFrq
	 * 
	 * @param CommitmentRate (commitmentRate)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(CommitmentRate commitmentRate, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Delete From CommitmentRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CmtReference = :CmtReference AND CmtRvwFrq = :CmtRvwFrq");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentRate);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",commitmentRate.getId() ,commitmentRate.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006",commitmentRate.getId() ,commitmentRate.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}

		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CommitmentRates or CommitmentRates_Temp.
	 *
	 * save CommitmentRate 
	 * 
	 * @param CommitmentRate (commitmentRate)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(CommitmentRate commitmentRate,String type) {
		logger.debug("Entering");

		StringBuilder sql =new StringBuilder("Insert Into CommitmentRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (CmtReference, CmtRvwFrq, CmtBaseRate, CmtMargin, CmtSpecialRate, CmtActualRate,CmtCalculatedRate,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId) ");
		sql.append(" Values(:CmtReference,:CmtRvwFrq,:CmtBaseRate,:CmtMargin, :CmtSpecialRate, :CmtActualRate,:CmtCalculatedRate,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId) ");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentRate);
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");
		return commitmentRate.getId();
	}

	/**
	 * This method updates the Record CommitmentRates or CommitmentRates_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update CommitmentRate by key CmtRvwFrq and Version
	 * 
	 * @param CommitmentRate (commitmentRate)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	@Override
	public void update(CommitmentRate commitmentRate,String type) {
		logger.debug("Entering");

		int recordCount = 0;
		StringBuilder	sql =new StringBuilder("Update CommitmentRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set CmtReference = :CmtReference, CmtRvwFrq=:CmtRvwFrq, CmtBaseRate=:CmtBaseRate,");
		sql.append(" CmtMargin=:CmtMargin, CmtSpecialRate = :CmtSpecialRate, CmtActualRate=:CmtActualRate, CmtCalculatedRate=:CmtCalculatedRate,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		sql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where CmtReference = :CmtReference AND CmtRvwFrq = :CmtRvwFrq");

		if (!type.endsWith("_Temp")){
			sql.append("  AND Version= :Version-1");
		}

		logger.debug("Sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentRate);
		recordCount = this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",commitmentRate.getId() ,commitmentRate.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * 
	 */
	@Override
	public void deleteByCmtReference(String cmtReference, String type) {
		logger.debug("Entering");

		CommitmentRate commitmentRate = new CommitmentRate();
		commitmentRate.setCmtReference(cmtReference);

		StringBuilder sql = new StringBuilder("Delete From CommitmentRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CmtReference = :CmtReference");

		logger.debug("deleteSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentRate);
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param errorId
	 * @param CmtRvwFrq
	 * @param userLanguage
	 * @return
	 */
	private ErrorDetails  getError(String errorId, String cmtRvwFrq, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = cmtRvwFrq;
		parms[0][0] = PennantJavaUtil.getLabel("label_CmtRvwFrq")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}