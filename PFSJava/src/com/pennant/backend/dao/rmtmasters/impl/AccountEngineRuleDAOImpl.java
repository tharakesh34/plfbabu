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
 * FileName    		:  AccountEngineRuleDAOImpl.java                                                   * 	  
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

package com.pennant.backend.dao.rmtmasters.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
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
import com.pennant.backend.dao.rmtmasters.AccountEngineRuleDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.AccountEngineRule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>AccountEngineRule model</b> class.<br>
 * 
 */
public class AccountEngineRuleDAOImpl extends
		BasisNextidDaoImpl<AccountEngineRule> implements AccountEngineRuleDAO {

	private static Logger logger = Logger.getLogger(AccountEngineRuleDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new AccountEngineRule
	 * 
	 * @return AccountEngineRule
	 */
	@Override
	public AccountEngineRule getAccountEngineRule() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil
				.getWorkFlowDetails("AccountEngineRule");
		AccountEngineRule accountEngineRule = new AccountEngineRule();
		if (workFlowDetails != null) {
			accountEngineRule.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return accountEngineRule;
	}

	/**
	 * This method get the module from method getAccountEngineRule() and set the
	 * new record flag as true and return AccountEngineRule()
	 * 
	 * @return AccountEngineRule
	 */
	@Override
	public AccountEngineRule getNewAccountEngineRule() {
		logger.debug("Entering");
		AccountEngineRule accountEngineRule = getAccountEngineRule();
		accountEngineRule.setNewRecord(true);
		logger.debug("Leaving");
		return accountEngineRule;
	}

	/**
	 * Fetch the Record Accounting Engine Rules details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AccountEngineRule
	 */
	@Override
	public AccountEngineRule getAccountEngineRuleById(final long id, String type) {
		logger.debug("Entering");
		AccountEngineRule accountEngineRule = getAccountEngineRule();
		accountEngineRule.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select AERuleId, AEEvent,  AERule," );
		selectSql.append(" AERuleDesc, AEIsSysDefault,");
		if(type.contains("View")){
			selectSql.append(" lovDescAEEventName ,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn,RecordStatus, RoleCode," );
		selectSql.append(" NextRoleCode, TaskId,NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTAERules");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AERuleId =:AERuleId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				accountEngineRule);
		RowMapper<AccountEngineRule> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AccountEngineRule.class);

		try {
			accountEngineRule = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			accountEngineRule = null;
		}
		logger.debug("Leaving");
		return accountEngineRule;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param AccountEngineRule
	 *            (accountEngineRule)
	 * @return AccountEngineRule
	 */
	@Override
	public void initialize(AccountEngineRule accountEngineRule) {
		super.initialize(accountEngineRule);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param AccountEngineRule
	 *            (accountEngineRule)
	 * @return void
	 */
	@Override
	public void refresh(AccountEngineRule accountEngineRule) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTAERules or RMTAERules_Temp. if
	 * Record not deleted then throws DataAccessException with error 41003.
	 * delete Accounting Engine Rules by key AEEvent
	 * 
	 * @param Accounting
	 *            Engine Rules (accountEngineRule)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(AccountEngineRule accountEngineRule, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTAERules");
		deleteSql.append(StringUtils.trimToEmpty(type) ); 
		deleteSql.append(" Where AERuleId =:AERuleId");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				accountEngineRule);
		
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
					beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003", accountEngineRule.getAEEvent(),
						accountEngineRule.getAERule(), accountEngineRule.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", accountEngineRule.getAEEvent(),
					accountEngineRule.getAERule(), accountEngineRule.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTAERules or RMTAERules_Temp.
	 * 
	 * save Accounting Engine Rules
	 * 
	 * @param Accounting
	 *            Engine Rules (accountEngineRule)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(AccountEngineRule accountEngineRule, String type) {
		logger.debug("Entering");
		
		if (accountEngineRule.getId() == 0
				|| accountEngineRule.getId() == Long.MIN_VALUE) {
			accountEngineRule.setId(getNextidviewDAO().getNextId(
					"Seq" + PennantJavaUtil.getTabelMap("AccountEngineRule")));
		}
		StringBuilder insertSql = new StringBuilder("Insert Into RMTAERules");
		insertSql.append(StringUtils.trimToEmpty(type)); 
		insertSql.append(" (AERuleId,AEEvent, AERule, AERuleDesc, AEIsSysDefault,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:AERuleId,:AEEvent, :AERule, :AERuleDesc, :AEIsSysDefault," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				accountEngineRule);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return accountEngineRule.getId();
	}

	/**
	 * This method updates the Record RMTAERules or RMTAERules_Temp. if Record
	 * not updated then throws DataAccessException with error 41004. update
	 * Accounting Engine Rules by key AEEvent and Version
	 * 
	 * @param Accounting
	 *            Engine Rules (accountEngineRule)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(AccountEngineRule accountEngineRule, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update RMTAERules");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set AERuleId =:AERuleId , AEEvent = :AEEvent, AERule = :AERule," );
		updateSql.append(" AERuleDesc = :AERuleDesc, AEIsSysDefault = :AEIsSysDefault," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" ); 
		updateSql.append(" Where AERuleId =:AERuleId ");

		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				accountEngineRule);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),
				beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails=  getError("41004", accountEngineRule.getAEEvent(),
					accountEngineRule.getAERule(), accountEngineRule.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Fetch the Record Accounting Engine Rules details by key field & IsSysDefault=true
	 */
	public AccountEngineRule getAccountEngineRuleBySysDflt(
			AccountEngineRule accountEngineRule,String type,boolean idExists){

		logger.debug("Entering");
		AccountEngineRule aAccountEngineRule = getNewAccountEngineRule();
		try {
			BeanUtils.copyProperties(aAccountEngineRule, accountEngineRule);
		} catch (Exception e) {
			logger.error(e);
		}
		aAccountEngineRule.setAEIsSysDefault(true);
		
		StringBuilder selectSql = new StringBuilder("Select AERuleId, AEEvent,AERule,AERuleDesc");
		selectSql.append(" From RMTAERules"+StringUtils.trim(type));
		selectSql.append(" Where AEEvent =:AEEvent AND AEIsSysDefault=:AEIsSysDefault " );
		if(idExists){
			selectSql.append(" AND AERuleId !=:AERuleId");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				aAccountEngineRule);
		RowMapper<AccountEngineRule> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AccountEngineRule.class);

		try {
			aAccountEngineRule = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);
			//accountEngineRule.setAEIsSysDefault(sysDefault);
		} catch (EmptyResultDataAccessException e) {
			logger.debug("Leaving");
			aAccountEngineRule = null;
			return aAccountEngineRule;
		}
		logger.debug("Leaving");
		return aAccountEngineRule;
	}
	
	
	
	@Override
	public List<AccountEngineRule> getListAERuleBySysDflt(String type){
		logger.debug("Entering");		
		StringBuilder selectSql = new StringBuilder("Select AERuleId, AEEvent,AERule,AERuleDesc");
		selectSql.append(" From RMTAERules"+StringUtils.trim(type));
		selectSql.append(" Where AEEvent in ('ADDDBSP','ADDDBSF','ADDDBSN','AMZ','AMZSUSP','M_NONAMZ','M_AMZ','RATCHG','REPAY', 'EARLYPAY','EARLYSTL','WRITEOFF')");
		selectSql.append("  AND AEIsSysDefault=1");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new	AccountEngineRule());
		RowMapper<AccountEngineRule> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AccountEngineRule.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	
	private ErrorDetails  getError(String errorId,String event, String rule,String userLanguage){
		String[][] parms= new String[2][2]; 
		
		parms[1][0] = event;
		parms[1][1] = rule;

		parms[0][0] = PennantJavaUtil.getLabel("label_AEEvent")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_AERule")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

}