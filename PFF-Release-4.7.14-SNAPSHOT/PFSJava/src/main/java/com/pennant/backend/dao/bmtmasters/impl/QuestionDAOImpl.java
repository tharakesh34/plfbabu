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
 * FileName    		:  QuestionDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-11-2011    														*
 *                                                                  						*
 * Modified Date    :  21-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-11-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.bmtmasters.QuestionDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.bmtmasters.Question;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>Question model</b> class.<br>
 * 
 */

public class QuestionDAOImpl extends BasisNextidDaoImpl<Question> implements QuestionDAO {

	private static Logger logger = Logger.getLogger(QuestionDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new Question 
	 * @return Question
	 */

	public QuestionDAOImpl() {
		super();
	}
	
	@Override
	public Question getQuestion() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Question");
		Question question= new Question();
		if (workFlowDetails!=null){
			question.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return question;
	}


	/**
	 * This method get the module from method getQuestion() and set the new record flag as true and return Question()   
	 * @return Question
	 */


	@Override
	public Question getNewQuestion() {
		logger.debug("Entering");
		Question question = getQuestion();
		question.setNewRecord(true);
		logger.debug("Leaving");
		return question;
	}

	/**
	 * Fetch the Record  Question Details details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Question
	 */
	@Override
	public Question getQuestionById(final long id, String type) {
		logger.debug("Entering");
		Question question = new Question();
		
		question.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select QuestionId, QuestionDesc, AnswerA, AnswerB, AnswerC, AnswerD, CorrectAnswer, QuestionIsActive");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTQuestion");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where QuestionId =:QuestionId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(question);
		RowMapper<Question> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Question.class);
		
		try{
			question = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			question = null;
		}
		logger.debug("Leaving");
		return question;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the BMTQuestion or BMTQuestion_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Question Details by key QuestionId
	 * 
	 * @param Question Details (question)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Question question,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From BMTQuestion");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where QuestionId =:QuestionId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(question);
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
	 * This method insert new Records into BMTQuestion or BMTQuestion_Temp.
	 * it fetches the available Sequence form SeqBMTQuestion by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Question Details 
	 * 
	 * @param Question Details (question)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(Question question,String type) {
		logger.debug("Entering");
		if (question.getId()==Long.MIN_VALUE){
			question.setId(getNextidviewDAO().getNextId("SeqBMTQuestion"));
			logger.debug("get NextID:"+question.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into BMTQuestion");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (QuestionId, QuestionDesc, AnswerA, AnswerB, AnswerC, AnswerD, CorrectAnswer, QuestionIsActive");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:QuestionId, :QuestionDesc, :AnswerA, :AnswerB, :AnswerC, :AnswerD, :CorrectAnswer, :QuestionIsActive");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(question);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return question.getId();
	}
	
	/**
	 * This method updates the Record BMTQuestion or BMTQuestion_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Question Details by key QuestionId and Version
	 * 
	 * @param Question Details (question)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(Question question,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update BMTQuestion");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set QuestionDesc = :QuestionDesc, AnswerA = :AnswerA, AnswerB = :AnswerB, AnswerC = :AnswerC, AnswerD = :AnswerD, CorrectAnswer = :CorrectAnswer, QuestionIsActive = :QuestionIsActive");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where QuestionId =:QuestionId");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(question);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}