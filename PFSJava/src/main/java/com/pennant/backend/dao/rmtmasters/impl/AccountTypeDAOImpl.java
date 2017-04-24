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
 * FileName    		:  AccountTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>AccountType model</b> class.<br>
 * 
 */
public class AccountTypeDAOImpl extends BasisCodeDAO<AccountType> implements
					AccountTypeDAO {
	
	private static Logger logger = Logger.getLogger(AccountTypeDAOImpl.class);
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public AccountTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Account Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AccountType
	 */
	@Override
	public AccountType getAccountTypeById(final String id, String type) {
		logger.debug("Entering");

		AccountType accountType = new AccountType();
		accountType.setId(id);

		StringBuilder selectSql = new StringBuilder("Select  AcType, AcTypeDesc, AcPurpose, AcTypeGrpId, AcHeadCode,");
		selectSql.append(" InternalAc, CustSysAc, AcTypeIsActive, AssertOrLiability, OnBalanceSheet, AllowOverDraw , ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, AcLmtCategory, ProfitCenterID, CostCenterID" );
		if(type.contains("View")){
			selectSql.append(",GroupCode,  GroupDescription, costCenterDesc, profitCenterDesc,CostCenterCode,ProfitCenterCode");
		}
		selectSql.append(" From RMTAccountTypes" );
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AcType =:AcType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				accountType);
		RowMapper<AccountType> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AccountType.class);

		try {
			accountType = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			accountType = null;
		}
		logger.debug("Leaving");
		return accountType;
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
	 * This method Deletes the Record from the RMTAccountTypes or
	 * RMTAccountTypes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Account Types by key AcType
	 * 
	 * @param Account
	 *            Types (accountType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(AccountType accountType, String type) {
		logger.debug("Entering");

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTAccountTypes");
		deleteSql.append(StringUtils.trimToEmpty(type) ); 
		deleteSql.append(" Where AcType =:AcType");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				accountType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
					beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",accountType.getAcType(),
						accountType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails=  getError("41006",accountType.getAcType(),
					accountType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTAccountTypes or
	 * RMTAccountTypes_Temp.
	 * 
	 * save Account Types
	 * 
	 * @param Account
	 *            Types (accountType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(AccountType accountType, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTAccountTypes" );
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AcType, AcTypeDesc, AcPurpose, AcTypeGrpId, AcHeadCode," );
		insertSql.append(" InternalAc, CustSysAc, AcTypeIsActive, AcLmtCategory, AssertOrLiability, OnBalanceSheet, AllowOverDraw ,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, ProfitCenterID, CostCenterID)");
		insertSql.append(" Values(:AcType, :AcTypeDesc, :AcPurpose,  :AcTypeGrpId, :AcHeadCode, " );
		insertSql.append(" :InternalAc, :CustSysAc,:AcTypeIsActive, :AcLmtCategory, :AssertOrLiability, :OnBalanceSheet, :AllowOverDraw ," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :ProfitCenterID, :CostCenterID)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				accountType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return accountType.getId();
	}

	/**
	 * This method updates the Record RMTAccountTypes or RMTAccountTypes_Temp.
	 * if Record not updated then throws DataAccessException with error 41004.
	 * update Account Types by key AcType and Version
	 * 
	 * @param Account
	 *            Types (accountType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(AccountType accountType, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update RMTAccountTypes");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set AcTypeDesc = :AcTypeDesc, AcPurpose = :AcPurpose,  AcTypeGrpId = :AcTypeGrpId," );
		updateSql.append(" AcHeadCode = :AcHeadCode, InternalAc = :InternalAc, AcLmtCategory=:AcLmtCategory," );
		updateSql.append(" CustSysAc = :CustSysAc, AcTypeIsActive = :AcTypeIsActive, AssertOrLiability = :AssertOrLiability, OnBalanceSheet = :OnBalanceSheet, AllowOverDraw = :AllowOverDraw ,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode,TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, ProfitCenterID = :ProfitCenterID, CostCenterID = :CostCenterID" );
		updateSql.append(" Where AcType =:AcType");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				accountType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),
				beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails=  getError("41004",accountType.getAcType(),
					accountType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Fetch the Record Account Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AccountType
	 */
	@Override
	public List<ValueLabel> getAccountTypeDesc(final List<String> acTypeList) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select AcType Label, AcTypeDesc Value From RMTAccountTypes" );
		selectSql.append(" Where AcType IN(:acTypeList)");
		
		Map<String, List<String>> params = new HashMap<String, List<String>>();
		params.put("acTypeList", acTypeList);

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ValueLabel> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ValueLabel.class);


		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), params, typeRowMapper);
	}

	
	private ErrorDetails  getError(String errorId,String accountType, String userLanguage){
		String[][] parms= new String[2][1]; 
		
		parms[1][0] = accountType;
		parms[0][0] = PennantJavaUtil.getLabel("label_AcType")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

	@Override
	public int getgetAccountTypeByProfit(long profitCenterID, String type) {
		AccountType accountType = new AccountType();
		accountType.setProfitCenterID(profitCenterID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From RMTAccountTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ProfitCenterID =:ProfitCenterID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountType);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
	
	@Override
	public int getgetAccountTypeByCost(long costCenterID, String type) {
		AccountType accountType = new AccountType();
		accountType.setCostCenterID(costCenterID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From RMTAccountTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CostCenterID =:CostCenterID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountType);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
}
	