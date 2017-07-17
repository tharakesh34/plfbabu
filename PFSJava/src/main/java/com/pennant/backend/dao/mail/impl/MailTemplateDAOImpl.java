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
 * FileName    		:  MailTemplateDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2012    														*
 *                                                                  						*
 * Modified Date    :  04-10-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.mail.impl;

import java.util.ArrayList;
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
import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>MailTemplate model</b> class.<br>
 * 
 */
public class MailTemplateDAOImpl extends BasisNextidDaoImpl<MailTemplate> implements MailTemplateDAO {
	private static Logger logger = Logger.getLogger(MailTemplateDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public MailTemplateDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Mail Template details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return MailTemplate
	 */
	@Override
	public MailTemplate getMailTemplateById(final long id, String type) {
		logger.debug("Entering");
		MailTemplate mailTemplate = new MailTemplate();
		
		mailTemplate.setId(id);
		
		StringBuilder selectSql = new StringBuilder(" Select TemplateId, TemplateFor, Module, TemplateCode, " );
		selectSql.append(" TemplateDesc, SmsTemplate, SmsContent, EmailTemplate, EmailContent, EmailFormat, " );
		selectSql.append(" EmailSubject, EmailSendTo, TurnAroundTime, Repeat, Active, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From Templates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where TemplateId =:TemplateId ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
		RowMapper<MailTemplate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MailTemplate.class);
		
		try{
			mailTemplate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), 
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			mailTemplate = null;
		}

		logger.debug("Leaving");
		return mailTemplate;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the Templates or Templates_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Mail Template by key TemplateCode
	 * 
	 * @param Mail Template (mailTemplate)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(MailTemplate mailTemplate,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From Templates");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where TemplateId =:TemplateId ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
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
	 * This method insert new Records into Templates or Templates_Temp.
	 *
	 * save Mail Template 
	 * 
	 * @param Mail Template (mailTemplate)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(MailTemplate mailTemplate,String type) {
		logger.debug("Entering");
		if (mailTemplate.getId() == Long.MIN_VALUE) {
			mailTemplate.setId(getNextidviewDAO().getNextId("SeqMailTemplate"));
			logger.debug("get NextID:" + mailTemplate.getId());
		}
		
		StringBuilder insertSql =new StringBuilder(" Insert Into Templates");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (TemplateId,  TemplateFor, Module, TemplateCode, TemplateDesc, SmsTemplate, SmsContent, " );
		insertSql.append(" EmailTemplate, EmailContent, EmailFormat, EmailSubject, EmailSendTo, TurnAroundTime, Repeat, Active, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:TemplateId, :TemplateFor, :Module, :TemplateCode, :TemplateDesc, :SmsTemplate, :SmsContent, " );
		insertSql.append(" :EmailTemplate, :EmailContent, :EmailFormat, :EmailSubject, :EmailSendTo, :TurnAroundTime, :Repeat, :Active,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return mailTemplate.getId();
	}
	
	/**
	 * This method updates the Record Templates or Templates_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Mail Template by key TemplateCode and Version
	 * 
	 * @param Mail Template (mailTemplate)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(MailTemplate mailTemplate,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update Templates");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set TemplateFor=:TemplateFor, Module=:Module, TemplateCode = :TemplateCode, " );
		updateSql.append(" TemplateDesc = :TemplateDesc, SmsTemplate = :SmsTemplate, SmsContent = :SmsContent, ");
		updateSql.append(" EmailTemplate = :EmailTemplate, EmailContent = :EmailContent, EmailFormat = :EmailFormat, " );
		updateSql.append(" EmailSubject = :EmailSubject, EmailSendTo= :EmailSendTo,TurnAroundTime=:TurnAroundTime, " );
		updateSql.append(" Repeat = :Repeat, Active = :Active ,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, " );
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, " );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where TemplateId =:TemplateId ");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	// TODO: Need to be checked whether this is required or not
	public List<MailTemplate> getMailTemplates(){
		List<MailTemplate> mailTemplateList = new ArrayList<MailTemplate>();
		MailTemplate mailTemplate = new MailTemplate();
		StringBuilder selectSql = new StringBuilder(" Select TemplateId, TemplateFor, Module, TemplateCode, " );
		selectSql.append(" TemplateDesc, SmsTemplate, SmsContent, EmailTemplate, EmailContent, EmailFormat, " );
		selectSql.append(" EmailSubject, EmailSendTo, TurnAroundTime, Repeat, Active, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From Templates");
		selectSql.append(" Where EmailTemplate = 1 ");
		
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
		RowMapper<MailTemplate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MailTemplate.class);
		
		try{
			mailTemplateList = this.namedParameterJdbcTemplate.query(selectSql.toString(), 
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving");
		return mailTemplateList;
	}
	
	@Override
	public int getMailTemplateByCode(String templateCode, long id, String type) {
		MailTemplate mailTemplate = new MailTemplate();
		mailTemplate.setTemplateCode(templateCode);
		mailTemplate.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From Templates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where TemplateCode =:TemplateCode AND TemplateId !=:TemplateId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

}