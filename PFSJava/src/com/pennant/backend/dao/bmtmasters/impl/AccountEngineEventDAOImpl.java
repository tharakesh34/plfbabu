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
 * FileName    		:  AccountEngineEventDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.bmtmasters.AccountEngineEventDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>AccountEngineEvent model</b> class.<br>
 * 
 */
public class AccountEngineEventDAOImpl extends BasisCodeDAO<AccountEngineEvent>	implements AccountEngineEventDAO {

	private static Logger logger = Logger.getLogger(AccountEngineEventDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new AccountEngineEvent
	 * 
	 * @return AccountEngineEvent
	 */
	@Override
	public AccountEngineEvent getAccountEngineEvent() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("AccountEngineEvent");
		AccountEngineEvent accountEngineEvent = new AccountEngineEvent();
		if (workFlowDetails != null) {
			accountEngineEvent.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return accountEngineEvent;
	}

	/**
	 * This method get the module from method getAccountEngineEvent() and set
	 * the new record flag as true and return AccountEngineEvent()
	 * 
	 * @return AccountEngineEvent
	 */
	@Override
	public AccountEngineEvent getNewAccountEngineEvent() {
		logger.debug("Entering");
		AccountEngineEvent accountEngineEvent = getAccountEngineEvent();
		accountEngineEvent.setNewRecord(true);
		logger.debug("Leaving");
		return accountEngineEvent;
	}

	/**
	 * Fetch the Record Accounting Engine Event details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AccountEngineEvent
	 */
	@Override
	public AccountEngineEvent getAccountEngineEventById(final String id, String type) {
		logger.debug("Entering");
		AccountEngineEvent accountEngineEvent = new AccountEngineEvent();
		accountEngineEvent.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select AEEventCode, AEEventCodeDesc," );
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTAEEvents");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AEEventCode =:AEEventCode") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountEngineEvent);
		RowMapper<AccountEngineEvent> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AccountEngineEvent.class);

		try {
			accountEngineEvent = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			accountEngineEvent = null;
		}
		logger.debug("Leaving");
		return accountEngineEvent;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param AccountEngineEvent
	 *            (accountEngineEvent)
	 * @return AccountEngineEvent
	 */
	@Override
	public void initialize(AccountEngineEvent accountEngineEvent) {
		super.initialize(accountEngineEvent);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param AccountEngineEvent
	 *            (accountEngineEvent)
	 * @return void
	 */
	@Override
	public void refresh(AccountEngineEvent accountEngineEvent) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTAEEvents or BMTAEEvents_Temp.
	 * if Record not deleted then throws DataAccessException with error 41003.
	 * delete Accounting Engine Event by key AEEventCode
	 * 
	 * @param Accounting
	 *            Engine Event (accountEngineEvent)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(AccountEngineEvent accountEngineEvent, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BMTAEEvents");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where AEEventCode =:AEEventCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountEngineEvent);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", accountEngineEvent.getAEEventCode(), 
					accountEngineEvent.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", accountEngineEvent.getAEEventCode(), 
					accountEngineEvent.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTAEEvents or BMTAEEvents_Temp.
	 * 
	 * save Accounting Engine Event
	 * 
	 * @param Accounting
	 *            Engine Event (accountEngineEvent)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(AccountEngineEvent accountEngineEvent, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTAEEvents");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AEEventCode, AEEventCodeDesc," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:AEEventCode, :AEEventCodeDesc, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountEngineEvent);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return accountEngineEvent.getId();
	}

	/**
	 * This method updates the Record BMTAEEvents or BMTAEEvents_Temp. if Record
	 * not updated then throws DataAccessException with error 41004. update
	 * Accounting Engine Event by key AEEventCode and Version
	 * 
	 * @param Accounting
	 *            Engine Event (accountEngineEvent)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(AccountEngineEvent accountEngineEvent, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTAEEvents");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set AEEventCode = :AEEventCode, AEEventCodeDesc = :AEEventCodeDesc," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where AEEventCode =:AEEventCode ");
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountEngineEvent);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),	beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails= getError("41003", accountEngineEvent.getAEEventCode(), 
					accountEngineEvent.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String aEEventCode,String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = aEEventCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_AEEventCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}