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
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.mail.MailTemplateDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>MailTemplate model</b> class.<br>
 * 
 */

public class MailTemplateDAOImpl extends BasisNextidDaoImpl<MailTemplate> implements MailTemplateDAO {

	private static Logger logger = Logger.getLogger(MailTemplateDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new MailTemplate 
	 * @return MailTemplate
	 */

	@Override
	public MailTemplate getMailTemplate() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("MailTemplate");
		MailTemplate mailTemplate= new MailTemplate();
		if (workFlowDetails!=null){
			mailTemplate.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return mailTemplate;
	}


	/**
	 * This method get the module from method getMailTemplate() and set the new record flag as true and return MailTemplate()   
	 * @return MailTemplate
	 */


	@Override
	public MailTemplate getNewMailTemplate() {
		logger.debug("Entering");
		MailTemplate mailTemplate = getMailTemplate();
		mailTemplate.setNewRecord(true);
		logger.debug("Leaving");
		return mailTemplate;
	}

	/**
	 * Fetch the Record  Mail Template details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return MailTemplate
	 */
	@Override
	public MailTemplate getMailTemplateById(final long id, String templateFor, String type) {
		logger.debug("Entering");
		MailTemplate mailTemplate = getMailTemplate();
		
		mailTemplate.setId(id);
		mailTemplate.setTemplateFor(templateFor);
		
		StringBuilder selectSql = new StringBuilder("Select TemplateId, TemplateFor, Module, TemplateCode, TemplateDesc, SmsTemplate, SmsContent, EmailTemplate, EmailContent, EmailFormat, EmailSubject, EmailSendTo, TurnAroundTime, Repeat, Active");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			//selectSql.append(",lovDescTemplateTypeName,lovDescEmailFormatName");
		}
		selectSql.append(" From Templates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where TemplateId =:TemplateId AND TemplateFor=:TemplateFor ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
		RowMapper<MailTemplate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MailTemplate.class);
		
		try{
			mailTemplate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			mailTemplate = null;
		}
		logger.debug("Leaving");
		return mailTemplate;
	}
	
	/**
	 * This method initialise the Record.
	 * @param MailTemplate (mailTemplate)
 	 * @return MailTemplate
	 */
	@Override
	public void initialize(MailTemplate mailTemplate) {
		super.initialize(mailTemplate);
	}
	/**
	 * This method refresh the Record.
	 * @param MailTemplate (mailTemplate)
 	 * @return void
	 */
	@Override
	public void refresh(MailTemplate mailTemplate) {
		
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
	@SuppressWarnings("serial")
	public void delete(MailTemplate mailTemplate,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From Templates");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where TemplateId =:TemplateId AND TemplateFor=:TemplateFor");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",mailTemplate.getTemplateCode() ,mailTemplate.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",mailTemplate.getTemplateCode() ,mailTemplate.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
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
		
		StringBuilder insertSql =new StringBuilder("Insert Into Templates");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (TemplateId,  TemplateFor, Module, TemplateCode, TemplateDesc, SmsTemplate, SmsContent, EmailTemplate, EmailContent, EmailFormat, EmailSubject, EmailSendTo, TurnAroundTime, Repeat, Active");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:TemplateId, :TemplateFor, :Module, :TemplateCode, :TemplateDesc, :SmsTemplate, :SmsContent, :EmailTemplate, :EmailContent, :EmailFormat, :EmailSubject, :EmailSendTo, :TurnAroundTime, :Repeat, :Active");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
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
	
	@SuppressWarnings("serial")
	@Override
	public void update(MailTemplate mailTemplate,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update Templates");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set TemplateId = :TemplateId, TemplateFor=:TemplateFor, Module=:Module, TemplateCode = :TemplateCode, TemplateDesc = :TemplateDesc, SmsTemplate = :SmsTemplate, SmsContent = :SmsContent,");
		updateSql.append(" EmailTemplate = :EmailTemplate, EmailContent = :EmailContent, EmailFormat = :EmailFormat, EmailSubject = :EmailSubject, EmailSendTo= :EmailSendTo,TurnAroundTime=:TurnAroundTime, Repeat = :Repeat, Active = :Active");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where TemplateId =:TemplateId AND TemplateFor=:TemplateFor");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailTemplate);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",mailTemplate.getTemplateCode() ,mailTemplate.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String TemplateCode, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = TemplateCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_TemplateCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}