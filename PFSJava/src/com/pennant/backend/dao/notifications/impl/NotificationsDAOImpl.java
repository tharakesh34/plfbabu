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
import com.pennant.backend.dao.notifications.NotificationsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Notifications model</b> class.<br>
 * 
 */
public class NotificationsDAOImpl extends BasisCodeDAO<Notifications> implements NotificationsDAO {

	private static Logger logger = Logger.getLogger(NotificationsDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new Notifications
	 * 
	 * @return Notifications
	 */
	@Override
	public Notifications getNotifications() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Notifications");
		Notifications notifications = new Notifications();
		if (workFlowDetails != null) {
			notifications.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return notifications;
	}

	/**
	 * This method get the module from method getNotifications() and set the new
	 * record flag as true and return Notifications()
	 * 
	 * @return Notifications
	 */
	@Override
	public Notifications getNewNotifications() {
		logger.debug("Entering");
		Notifications notifications = getNotifications();
		notifications.setNewRecord(true);
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
	public Notifications getNotificationsById(final String ruleCode, String type) {
		logger.debug("Entering");
		Notifications notifications = new Notifications();
		notifications.setRuleCode(ruleCode);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select RuleCode, RuleModule, RuleCodeDesc,RuleTemplate, ActualBlockTemplate," );
		selectSql.append("  RuleReciepent, ActualBlockReciepent, RuleAttachment, ActualBlockAtachment," );
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  Notifications");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleCode =:RuleCode") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notifications);
		RowMapper<Notifications> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notifications.class);

		try {
			notifications = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
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

		selectSql.append(" Select RuleCode, RuleModule, RuleCodeDesc, RuleTemplate, ActualBlockTemplate," );
		selectSql.append(" RuleReciepent, ActualBlockReciepent, RuleAttachment, ActualBlockAtachment," );
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  Notifications");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleCode =:RuleCode AND  RuleModule=:RuleModule") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notifications);
		RowMapper<Notifications> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notifications.class);

		try {
			notifications = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			notifications = null;
		}
		logger.debug("Leaving");
		return notifications;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param Notifications
	 *            (notifications)
	 * @return Notifications
	 */
	@Override
	public void initialize(Notifications notifications) {
		super.initialize(notifications);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Notifications
	 *            (notifications)
	 * @return void
	 */
	@Override
	public void refresh(Notifications notifications) {

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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails= getError("41004", notifications.getRuleCode(), 
						notifications.getRuleModule(), notifications.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", notifications.getRuleCode(), 
					notifications.getRuleModule(), notifications.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
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
	public String save(Notifications notifications, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into Notifications");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RuleCode, RuleModule, RuleCodeDesc, RuleTemplate, ActualBlockTemplate,");
		insertSql.append(" RuleReciepent, ActualBlockReciepent, RuleAttachment, ActualBlockAtachment," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:RuleCode, :RuleModule, :RuleCodeDesc, :RuleTemplate, :ActualBlockTemplate," );
		insertSql.append(" :RuleReciepent, :ActualBlockReciepent, :RuleAttachment, :ActualBlockAtachment," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notifications);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return notifications.getRuleCode();
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
	@SuppressWarnings("serial")
	@Override
	public void update(Notifications notifications, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update Notifications");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set RuleCode = :RuleCode," );
		updateSql.append(" RuleModule = :RuleModule, RuleCodeDesc = :RuleCodeDesc , RuleTemplate = :RuleTemplate, ActualBlockTemplate = :ActualBlockTemplate," );
		updateSql.append(" RuleReciepent = :RuleReciepent , ActualBlockReciepent = :ActualBlockReciepent, RuleAttachment = :RuleAttachment, ActualBlockAtachment = :ActualBlockAtachment, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append("  Where RuleCode =:RuleCode ");
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notifications);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails= getError("41003", notifications.getRuleCode(), 
					notifications.getRuleModule(), notifications.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
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
	@SuppressWarnings("serial")
	@Override
	public List<Notifications> getNotificationsByModule(String module, String type) {
		logger.debug("Entering");

		logger.debug("Entering");
		Notifications notifications = new Notifications();
		notifications.setRuleModule(module);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select RuleCode, RuleModule, RuleCodeDesc,RuleTemplate, ActualBlockTemplate," );
		selectSql.append("  RuleReciepent, ActualBlockReciepent, RuleAttachment, ActualBlockAtachment," );
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  Notifications");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleModule =:RuleModule") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notifications);
		logger.debug("Entering");
		RowMapper<Notifications> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notifications.class);
		List<Notifications> notificationsList =  this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		return notificationsList;
	}

	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String ruleCode,String ruleModule, String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = ruleCode;
		parms[1][1] = ruleModule;

		parms[0][0] = PennantJavaUtil.getLabel("label_MailConfig_RuleCode")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_MailConfig_RuleModule")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}