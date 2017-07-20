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
 * FileName    		:  FinanceTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountDAO;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinanceType model</b> class.<br>
 * 
 */
public class FinTypeAccountDAOImpl extends BasisCodeDAO<FinTypeAccount> implements FinTypeAccountDAO {

	private static Logger logger = Logger.getLogger(FinTypeAccountDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinTypeAccountDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceType
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinTypeAccount getFinTypeAccount() {
		logger.debug("Entering");
		FinTypeAccount finTypeAccount = new FinTypeAccount("");
		logger.debug("Leaving");
		return finTypeAccount;
	}

	/**
	 * This method get the module from method getFinanceType() and set the new record flag as true and
	 * return FinanceType()
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinTypeAccount getNewFinTypeAccount() {
		logger.debug("Entering");
		FinTypeAccount finTypeAccount = getFinTypeAccount();
		finTypeAccount.setNewRecord(true);
		logger.debug("Leaving");
		return finTypeAccount;
	}



	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *         (String)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public List<FinTypeAccount> getFinTypeAccountListByID(final String id, String type) {
		logger.debug("Entering");
		FinTypeAccount finTypeAccount = new FinTypeAccount();
		finTypeAccount.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT FinType,FinCcy, Event, AlwManualEntry, ");
		selectSql.append(" AlwCustomerAccount, AccountReceivable, CustAccountTypes, DefaultAccNum,");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" FROM FinTypeAccount");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccount);
		RowMapper<FinTypeAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeAccount.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	
	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *         (String)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinTypeAccount getFinTypeAccountByID(FinTypeAccount finTypeAccount, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT FinType,FinCcy, Event, AlwManualEntry, ");
		selectSql.append(" AlwCustomerAccount, AccountReceivable, CustAccountTypes, DefaultAccNum,");
		if (type.contains("View")) {
		selectSql.append(" FinCcyName,FinFormatter,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" FROM FinTypeAccount");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType And FinCcy = :FinCcy And Event = :Event");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccount);
		RowMapper<FinTypeAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeAccount.class);

		try {
			finTypeAccount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finTypeAccount = null;
		}
		logger.debug("Leaving");
		return finTypeAccount;
	}

	/**
	 * @param dataSource
	 *         the dataSource to set
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}


	/**
	 * This method insert new Records into RMTFinanceTypes or RMTFinanceTypes_Temp.
	 * 
	 * save Finance Types
	 * 
	 * @param Finance
	 *         Types (financeType)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FinTypeAccount finTypeAccount, String type) {
		logger.debug("Entering ");
		
		StringBuilder insertSql = new StringBuilder("Insert Into FinTypeAccount" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (FinType, FinCcy, Event, AlwManualEntry," );
		insertSql.append(" AlwCustomerAccount, AccountReceivable, CustAccountTypes, DefaultAccNum," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:FinType, :FinCcy, :Event, :AlwManualEntry," );
		insertSql.append(" :AlwCustomerAccount, :AccountReceivable, :CustAccountTypes, :DefaultAccNum," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccount);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return finTypeAccount.getId();
	}

	/**
	 * This method updates the Record RMTFinanceTypes or RMTFinanceTypes_Temp. if Record not updated
	 * then throws DataAccessException with error 41004. update Finance Types by key FinType and
	 * Version
	 * 
	 * @param Finance
	 *         Types (financeType)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinTypeAccount finTypeAccount, String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		
		StringBuilder updateSql = new StringBuilder("Update FinTypeAccount" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set AlwManualEntry = :AlwManualEntry,AlwCustomerAccount = :AlwCustomerAccount,");
		updateSql.append(" AccountReceivable = :AccountReceivable,CustAccountTypes = :CustAccountTypes,");
		updateSql.append(" DefaultAccNum = :DefaultAccNum, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where FinType =:FinType and FinCcy=:FinCcy and Event=:Event ");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccount);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}
	
	
	/**
	 * This method Deletes the Record from the RMTFinanceTypes or RMTFinanceTypes_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Finance Types by key FinType
	 * 
	 * @param Finance
	 *         Types (financeType)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinTypeAccount finTypeAccount,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeAccount");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where FinType =:FinType And FinCcy =:FinCcy And Event =:Event");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccount);
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
	 * This method initialize the Record.
	 * 
	 * @param FinanceType
	 *         (financeType)
	 * @return FinanceType
	 */
	
	@Override
	public void deleteByFinType(String finType, String type) {
		logger.debug("Entering");
		FinTypeAccount finTypeAccount = new FinTypeAccount();
		finTypeAccount.setFinType(finType);
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeAccount");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccount);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}
	
}