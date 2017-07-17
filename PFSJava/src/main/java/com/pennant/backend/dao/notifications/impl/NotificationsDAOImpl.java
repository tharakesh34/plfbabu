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
 * FileName    		:  NotificationsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.notifications.impl;

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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.model.MailData;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.notifications.NotificationsDAO;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>Notifications model</b> class.<br>
 * 
 */
public class NotificationsDAOImpl extends BasisNextidDaoImpl<Notifications> implements NotificationsDAO {

	private static Logger logger = Logger.getLogger(NotificationsDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public NotificationsDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Notifications Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Notifications
	 */
	@Override
	public Notifications getNotificationsById(final String ruleCode, String type) {
		logger.debug("Entering");
		Notifications notifications = new Notifications();
		notifications.setRuleCode(ruleCode);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select RuleId, RuleCode, RuleModule,TemplateType, RuleCodeDesc,RuleTemplate, ActualBlockTemplate," );
		selectSql.append("  RuleReciepent, ActualBlockReciepent, RuleAttachment, ActualBlockAtachment," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" , TemplateTypeFields, RuleReciepentFields, RuleAttachmentFields" );
		selectSql.append(" FROM  Notifications");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleCode =:RuleCode") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notifications);
		RowMapper<Notifications> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notifications.class);

		try {
			notifications = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			notifications = null;
		}
		logger.debug("Leaving");
		return notifications;
	}

	/**
	 * Fetch the Record Notifications Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Notifications
	 */
	@Override
	public Notifications getNotifications(String ruleCode, String ruleModule, String type) {
		logger.debug("Entering");
		Notifications notifications = new Notifications();
		notifications.setRuleCode(ruleCode);
		notifications.setRuleModule(ruleModule);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select RuleId, RuleCode, RuleModule,TemplateType, RuleCodeDesc, RuleTemplate, ActualBlockTemplate," );
		selectSql.append(" RuleReciepent, ActualBlockReciepent, RuleAttachment, ActualBlockAtachment," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" , TemplateTypeFields, RuleReciepentFields, RuleAttachmentFields" );
		selectSql.append(" FROM  Notifications");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleCode =:RuleCode AND  RuleModule=:RuleModule") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notifications);
		RowMapper<Notifications> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notifications.class);

		try {
			notifications = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
			this.namedParameterJdbcTemplate.queryForMap(selectSql.toString(), beanParameters);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			notifications = null;
		}
		logger.debug("Leaving");
		return notifications;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTNotificationss or
	 * BMTNotificationss_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete Notifications Details by key RuleCode
	 * 
	 * @param Notifications
	 *            Details (notifications)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Notifications notifications, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql =new StringBuilder();
		deleteSql.append("Delete From Notifications");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  RuleCode =:RuleCode ");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notifications);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),	beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTNotificationss or BMTNotificationss_Temp.
	 * 
	 * save Notifications Details
	 * 
	 * @param Notifications
	 *            Details (notifications)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(Notifications notifications, String type) {
		logger.debug("Entering");
		
		if (notifications.getId() == Long.MIN_VALUE) {
			notifications.setId(getNextidviewDAO().getNextId("SeqNotifications"));
			logger.debug("get NextID:" + notifications.getId());
		}
		
		StringBuilder insertSql = new StringBuilder("Insert Into Notifications");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RuleId, RuleCode, RuleModule, TemplateType,RuleCodeDesc, RuleTemplate, ActualBlockTemplate,");
		insertSql.append(" RuleReciepent, ActualBlockReciepent, RuleAttachment, ActualBlockAtachment," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId,");
		insertSql.append(" TemplateTypeFields, RuleReciepentFields, RuleAttachmentFields)");
		insertSql.append(" Values(:RuleId, :RuleCode, :RuleModule, :TemplateType, :RuleCodeDesc, :RuleTemplate, :ActualBlockTemplate," );
		insertSql.append(" :RuleReciepent, :ActualBlockReciepent, :RuleAttachment, :ActualBlockAtachment," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId, ");
		insertSql.append(" :TemplateTypeFields , :RuleReciepentFields, :RuleAttachmentFields)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notifications);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return notifications.getRuleId();
	}

	/**
	 * This method updates the Record Notifications or Notifications_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Notifications Details by key RuleCode and Version
	 * 
	 * @param Notifications
	 *            Details (notifications)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(Notifications notifications, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update Notifications");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set RuleId=:RuleId," );
		updateSql.append(" RuleModule = :RuleModule,TemplateType=:TemplateType, RuleCodeDesc = :RuleCodeDesc , RuleTemplate = :RuleTemplate, ActualBlockTemplate = :ActualBlockTemplate," );
		updateSql.append(" RuleReciepent = :RuleReciepent , ActualBlockReciepent = :ActualBlockReciepent, RuleAttachment = :RuleAttachment, ActualBlockAtachment = :ActualBlockAtachment, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId," );
		updateSql.append(" TemplateTypeFields = :TemplateTypeFields, RuleReciepentFields = :RuleReciepentFields, RuleAttachmentFields = :RuleAttachmentFields" );
		updateSql.append("  Where RuleCode =:RuleCode ");
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notifications);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method updates the Record Notifications or Notifications_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Notifications Details by key RuleCode and Version
	 * 
	 * @param Notifications
	 *            Details (notifications)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public List<Notifications> getNotificationsByModule(String module, String type) {
		logger.debug("Entering");

		Notifications notifications = new Notifications();
		notifications.setRuleModule(module);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select TemplateType, RuleTemplate, RuleReciepent, RuleAttachment " );
		selectSql.append(" , TemplateTypeFields, RuleReciepentFields, RuleAttachmentFields" );
		selectSql.append(" FROM  Notifications");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleModule =:RuleModule") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notifications);
		RowMapper<Notifications> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notifications.class);
		List<Notifications> notificationsList =  this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return notificationsList;
	}

	/**
	 * Method for Fetching Notifications List using by Rule ID List
	 */
	@Override
    public List<Notifications> getNotificationsByRuleIdList(List<Long> notificationIdList, String type) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RuleIdList", notificationIdList);

		StringBuilder selectSql = new StringBuilder(" Select TemplateType, RuleTemplate, RuleReciepent, RuleAttachment " );
		selectSql.append(" , TemplateTypeFields, RuleReciepentFields, RuleAttachmentFields" );
		selectSql.append(" FROM  Notifications");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleId IN (:RuleIdList) ") ;

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<Notifications> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notifications.class);
		List<Notifications> notificationsList =  this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		logger.debug("Leaving");
		return notificationsList;
    }
	
	/**
	 * Method for get Template Id's List based on the TemplateType
	 */
	@Override
    public List<Long> getTemplateIds(String templateType) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("TemplateType", templateType);
		
		StringBuilder selectSql = new StringBuilder("Select RuleId ");
		selectSql.append(" From Notifications ");
		selectSql.append(" Where TemplateType =:TemplateType ");

		logger.debug("selectSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), source, Long.class);
    }

	
	@Override
	public List<MailData> getMailData(String mailName) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
	
		StringBuilder selectSql = new StringBuilder(" Select Id, MailName, MailTrigger, MailTo, MailSubject, " );
		selectSql.append(" MailBody, MailAttachment, MailAttachmentName, MailData ");
		selectSql.append(" FROM  MailConfiguration where MailName = '"+ mailName +"'");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<MailData> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MailData.class);
		List<MailData> mailData =  this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		logger.debug("Leaving");
		return mailData;
		
	}

	@Override
	public Map<String, Object> mergeFields(String query) {
		logger.debug("Entering");
		Map<String, Object> mergeData = new HashMap<String, Object>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			mergeData = this.namedParameterJdbcTemplate.queryForMap(query ,paramMap);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return mergeData;
	}

	@Override
	public int triggerMail(String query) {
		int value = 0;
		Object object = new Object();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(object);
		try {
			value = this.namedParameterJdbcTemplate.queryForObject(query, beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return value;
	}

}