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
 * FileName    		:  FinanceCheckListReferenceDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-12-2011    														*
 *                                                                  						*
 * Modified Date    :  08-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.lmtmasters.impl;


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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinanceCheckListReference model</b> class.<br>
 * 
 */

public class FinanceCheckListReferenceDAOImpl extends BasisCodeDAO<FinanceCheckListReference> 
implements FinanceCheckListReferenceDAO {

	private static Logger logger = Logger.getLogger(FinanceCheckListReferenceDAOImpl.class);

	public FinanceCheckListReferenceDAOImpl() {
		super();
	}
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceCheckListReference 
	 * @return FinanceCheckListReference
	 */

	@Override
	public FinanceCheckListReference getFinanceCheckListReference() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinanceCheckListReference");
		FinanceCheckListReference financeCheckListReference= new FinanceCheckListReference();
		if (workFlowDetails!=null){
			financeCheckListReference.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return financeCheckListReference;
	}


	/**
	 * This method get the module from method getFinanceCheckListReference()
	 *  and set the new record flag as true and return FinanceCheckListReference()   
	 * @return FinanceCheckListReference
	 */


	@Override
	public FinanceCheckListReference getNewFinanceCheckListReference() {
		logger.debug("Entering");
		FinanceCheckListReference financeCheckListReference = getFinanceCheckListReference();
		financeCheckListReference.setNewRecord(true);
		logger.debug("Leaving");
		return financeCheckListReference;
	}

	/**
	 * Fetch the Record  Finance Check List Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceCheckListReference
	 */
	@Override
	public FinanceCheckListReference getFinanceCheckListReferenceById(final String id,long questionId
			,long answer ,String type) {
		logger.debug("Entering");
		FinanceCheckListReference financeCheckListReference = new FinanceCheckListReference();

		financeCheckListReference.setId(id);
		financeCheckListReference.setQuestionId(questionId);
		financeCheckListReference.setAnswer(answer);

		StringBuilder selectSql = new StringBuilder("Select FinReference, QuestionId, Answer,Remarks");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		selectSql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescQuesDesc, lovDescAnswerDesc ");
		}
		selectSql.append(" From FinanceCheckListRef");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference and  QuestionId=:QuestionId and Answer=:Answer");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCheckListReference);
		RowMapper<FinanceCheckListReference> typeRowMapper = ParameterizedBeanPropertyRowMapper
		.newInstance(FinanceCheckListReference.class);

		try{
			financeCheckListReference = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString()
					, beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeCheckListReference = null;
		}
		logger.debug("Leaving");
		return financeCheckListReference;
	}

	/**
	 * Fetch the Record  Finance Check List Details details by key field
	 * 
	 * @param finReference (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceCheckListReference
	 */
	@Override
	public List<FinanceCheckListReference> getCheckListByFinRef(final String finReference,String showStageCheckListIds, String type) {
		logger.debug("Entering");
		
		FinanceCheckListReference financeCheckListReference = new FinanceCheckListReference();
		financeCheckListReference.setId(finReference);
		List<FinanceCheckListReference>  finCheckListRefList= null;
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, QuestionId, Answer,Remarks");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,lovDescQuesDesc, lovDescAnswerDesc ");
		}
		selectSql.append(" From FinanceCheckListRef");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference"); 
		
		if(StringUtils.isNotBlank(showStageCheckListIds)){
			selectSql.append(" AND QuestionId IN("+showStageCheckListIds+") "); 
		}
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCheckListReference);
		RowMapper<FinanceCheckListReference> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceCheckListReference.class);

		try{
			finCheckListRefList= this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);		
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finCheckListRefList = null;
		}
		logger.debug("Leaving");
		return finCheckListRefList;
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the FinanceCheckListRef or FinanceCheckListRef_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Check List Details by key FinReference
	 * 
	 * @param Finance Check List Details (financeCheckListReference)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinanceCheckListReference financeCheckListReference,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinanceCheckListRef");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference and QuestionId=:QuestionId and Answer=:Answer");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCheckListReference);
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
	 * This method deletes all the records with  finReference condition
	 */
	public void delete(String  finReference,String type) {
		logger.debug("Entering");
		FinanceCheckListReference financeCheckListReference=new FinanceCheckListReference();
		financeCheckListReference.setFinReference(finReference);
		StringBuilder deleteSql = new StringBuilder("Delete From FinanceCheckListRef");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCheckListReference);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinanceCheckListRef or FinanceCheckListRef_Temp.
	 *
	 * save Finance Check List Details 
	 * 
	 * @param Finance Check List Details (financeCheckListReference)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FinanceCheckListReference financeCheckListReference,String type) {
		logger.debug("Entering");

		StringBuilder insertSql =new StringBuilder("Insert Into FinanceCheckListRef");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, QuestionId, Answer,Remarks");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		insertSql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :QuestionId, :Answer,:Remarks");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		insertSql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCheckListReference);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeCheckListReference.getId();
	}

	/**
	 * This method updates the Record FinanceCheckListRef or FinanceCheckListRef_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Finance Check List Details by key FinReference and Version
	 * 
	 * @param Finance Check List Details (financeCheckListReference)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinanceCheckListReference financeCheckListReference,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinanceCheckListRef");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set Remarks=:Remarks, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		updateSql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId");
		updateSql.append(", NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference AND QuestionId = :QuestionId AND Answer =:Answer");

		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCheckListReference);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}