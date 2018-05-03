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
 * FileName    		:  AccountsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-01-2012    														*
 *                                                                  						*
 * Modified Date    :  02-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-01-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.accounts.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.accounts.AccountsDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>Accounts model</b> class.<br>
 * 
 */
public class AccountsDAOImpl extends BasisCodeDAO<Accounts> implements AccountsDAO {
	private static Logger logger = Logger.getLogger(AccountsDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public AccountsDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new Accounts 
	 * @return Accounts
	 */
	@Override
	public Accounts getAccounts() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Accounts");
		Accounts accounts= new Accounts();
		if (workFlowDetails!=null){
			accounts.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return accounts;
	}

	/**
	 * This method get the module from method getAcounts() and 
	 * set the new record flag as true and return Accounts()   
	 * @return Accounts
	 */
	@Override
	public Accounts getNewAccounts() {
		logger.debug("Entering");
		Accounts accounts = getAccounts();
		accounts.setNewRecord(true);
		logger.debug("Leaving");
		return accounts;
	}

	/**
	 * Fetch the Record  Account Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Accounts
	 */
	@Override
	public Accounts getAccountsById(final String id, String type) {
		logger.debug("Entering");
		
		Accounts accounts = new Accounts();
		accounts.setAccountId(id);
		
		StringBuilder selectSql = new StringBuilder("Select AccountId, AcCcy, AcType, AcBranch, AcCustId, AcFullName, ");
		selectSql.append("AcShortName, AcPurpose, InternalAc, CustSysAc,");
		selectSql.append("ShadowBal, AcBalance, AcOpenDate,AcCloseDate, AcLastCustTrnDate, AcLastSysTrnDate, AcActive, AcBlocked,"); 
		selectSql.append(" AcClosed, HostAcNumber");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescCustCIF,lovDescBranchCodeName,lovDescCurrency,lovDescAccTypeDesc, lovDescFinFormatter");
		}
		selectSql.append(" From Accounts");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountId =:AccountId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accounts);
		RowMapper<Accounts> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Accounts.class);
		
		try{
			accounts = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			accounts = null;
		}
		logger.debug("Leaving");
		return accounts;
	}
	
	/**
	 * Fetch the Record  Account Details List by key field
	 * 
	 * @param AcPurpose (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return List<Accounts>
	 */
	@Override
	public List<Accounts> getAccountsByAcPurpose(final String acPurpose, String type) {
		logger.debug("Entering");
		
		Accounts accounts = new Accounts();
		accounts.setAcPurpose(acPurpose);
		
		StringBuilder selectSql = new StringBuilder("Select AcType ");
		selectSql.append(" From RMTAccountTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AcPurpose =:AcPurpose");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accounts);
		RowMapper<Accounts> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Accounts.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the Accounts or Accounts_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Account Details by key AccointId
	 * 
	 * @param Account Details (accounts)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(Accounts accounts,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From Accounts");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where AccountId =:AccountId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accounts);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into Accounts or Accounts_Temp.
	 *
	 * save Account Details 
	 * 
	 * @param Account Details (accounts)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(Accounts accounts,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into Accounts");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AccountId, AcCcy, AcType, AcBranch, AcCustId, AcFullName, AcShortName");
		insertSql.append(", AcPurpose, InternalAc, CustSysAc");
		insertSql.append(", ShadowBal, AcBalance, AcOpenDate,AcCloseDate, AcLastCustTrnDate, AcLastSysTrnDate, AcActive" );
		insertSql.append(", AcBlocked, AcClosed, HostAcNumber");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode" );
		insertSql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:AccountId, :AcCcy, :AcType, :AcBranch, :AcCustId, :AcFullName" );
		insertSql.append(", :AcShortName, :AcPurpose, :InternalAc, :CustSysAc");
		insertSql.append(", :ShadowBal, :AcBalance, :AcOpenDate,:AcCloseDate, :AcLastCustTrnDate, :AcLastSysTrnDate");
		insertSql.append(", :AcActive, :AcBlocked, :AcClosed, :HostAcNumber");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId");
		insertSql.append(", :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accounts);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return accounts.getAccountId();
	}
	
	/**
	 * This method insert new Records into Accounts or Accounts_Temp.
	 *
	 * save Account Details 
	 * 
	 * @param Account Details (accounts)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void saveList(List<Accounts> accountList,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into Accounts");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AccountId, AcCcy, AcType, AcBranch, AcCustId, AcFullName, AcShortName");
		insertSql.append(", AcPurpose, InternalAc, CustSysAc");
		insertSql.append(", ShadowBal, AcBalance, AcOpenDate,AcCloseDate, AcLastCustTrnDate, AcLastSysTrnDate, AcActive" );
		insertSql.append(", AcBlocked, AcClosed, HostAcNumber");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode" );
		insertSql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:AccountId, :AcCcy, :AcType, :AcBranch, :AcCustId, :AcFullName" );
		insertSql.append(", :AcShortName, :AcPurpose, :InternalAc, :CustSysAc");
		insertSql.append(", :ShadowBal, :AcBalance, :AcOpenDate,:AcCloseDate, :AcLastCustTrnDate, :AcLastSysTrnDate");
		insertSql.append(", :AcActive, :AcBlocked, :AcClosed, :HostAcNumber");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId");
		insertSql.append(", :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	
	/**
	 * This method updates the Record Accounts or Accounts_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Account Details by key AccointId and Version
	 * 
	 * @param Account Details (accounts)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(Accounts accounts,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update Accounts");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set AcCcy = :AcCcy, AcType = :AcType" ); 
		updateSql.append(", AcBranch = :AcBranch, AcCustId = :AcCustId, AcFullName = :AcFullName" ); 
		updateSql.append(", AcShortName = :AcShortName, AcPurpose = :AcPurpose, InternalAc = :InternalAc" ); 
		updateSql.append(", CustSysAc = :CustSysAc, ShadowBal = :ShadowBal,  AcBalance = :AcBalance" ); 
		updateSql.append(", AcOpenDate = :AcOpenDate,AcCloseDate=:AcCloseDate, AcLastCustTrnDate = :AcLastCustTrnDate" ); 
		updateSql.append(", AcLastSysTrnDate = :AcLastSysTrnDate, AcActive = :AcActive, AcBlocked = :AcBlocked" ); 
		updateSql.append(", AcClosed = :AcClosed, HostAcNumber = :HostAcNumber, Version = :Version , LastMntBy = :LastMntBy");
		updateSql.append(", LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode" ); 
		updateSql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where AccountId =:AccountId");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accounts);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method updates the Record Accounts or Accounts_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Account Details by key AccointId and Version
	 * 
	 * @param Account Details (accounts)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void updateList(List<Accounts> accountList,String type) {
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder("Update Accounts");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set AcCcy = :AcCcy, AcType = :AcType" ); 
		updateSql.append(", AcBranch = :AcBranch, AcCustId = :AcCustId, AcFullName = :AcFullName" ); 
		updateSql.append(", AcShortName = :AcShortName, AcPurpose = :AcPurpose, InternalAc = :InternalAc" ); 
		updateSql.append(", CustSysAc = :CustSysAc, ShadowBal = :ShadowBal,  AcBalance = :AcBalance" ); 
		updateSql.append(", AcOpenDate = :AcOpenDate,AcCloseDate=:AcCloseDate, AcLastCustTrnDate = :AcLastCustTrnDate" ); 
		updateSql.append(", AcLastSysTrnDate = :AcLastSysTrnDate, AcActive = :AcActive, AcBlocked = :AcBlocked" ); 
		updateSql.append(", AcClosed = :AcClosed, HostAcNumber = :HostAcNumber, Version = :Version , LastMntBy = :LastMntBy");
		updateSql.append(", LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode" ); 
		updateSql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where AccountId =:AccountId");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
    @Override
    public void updateAccrualBalance() {
		logger.debug("Entering");
		
		Accounts accounts = new Accounts();
		accounts.setShadowBal(BigDecimal.ZERO);
		
		StringBuilder	updateSql =new StringBuilder("Update Accounts");
		updateSql.append(" Set ShadowBal = :ShadowBal ");
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accounts);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
    }

	@Override
	public boolean saveOrUpdate(Accounts account, String type) {

		int recordCount = 0;

		//PREPARE BOTH UPDATE. and Insert Statements and make available for exception handling
		StringBuilder	updateSql =new StringBuilder("Update Accounts Set ");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" ShadowBal = (ShadowBal + :ShadowBal), " ); 
		updateSql.append(" AcBalance = (AcBalance + :AcBalance) " ); 
		updateSql.append(" Where AccountId =:AccountId");

		StringBuilder insertSql =new StringBuilder("Insert Into Accounts");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AccountId, AcCcy, AcType, AcBranch, AcCustId, AcFullName, AcShortName");
		insertSql.append(", AcPurpose, InternalAc, CustSysAc");
		insertSql.append(", ShadowBal, AcBalance, AcOpenDate,AcCloseDate, AcLastCustTrnDate, AcLastSysTrnDate, AcActive" );
		insertSql.append(", AcBlocked, AcClosed, HostAcNumber");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode" );
		insertSql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:AccountId, :AcCcy, :AcType, :AcBranch, :AcCustId, :AcFullName" );
		insertSql.append(", :AcShortName, :AcPurpose, :InternalAc, :CustSysAc");
		insertSql.append(", :ShadowBal, :AcBalance, :AcOpenDate,:AcCloseDate, :AcLastCustTrnDate, :AcLastSysTrnDate");
		insertSql.append(", :AcActive, :AcBlocked, :AcClosed, :HostAcNumber");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId");
		insertSql.append(", :NextTaskId, :RecordType, :WorkflowId)");


		//TRY UPDATE.
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(account);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount > 0) {
			return true;
		}

		//UPDATE FAILS TRY INSERT
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
			return true;
		} catch (DuplicateKeyException e) {
			//Due to huge transactions hit record j=has been created between update and insert statements. SO update now
			recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

			if (recordCount > 0) {
				return true;
			}

		}
		
		return false;
	}

}