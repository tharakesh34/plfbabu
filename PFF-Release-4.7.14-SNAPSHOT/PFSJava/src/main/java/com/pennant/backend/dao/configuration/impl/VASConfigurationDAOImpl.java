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
 * FileName    		:  VASConfigurationDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-11-2016    														*
 *                                                                  						*
 * Modified Date    :  29-11-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-11-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.configuration.impl;


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

import com.pennant.backend.dao.configuration.VASConfigurationDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
 
/**
 * DAO methods implementation for the <b>VASConfiguration model</b> class.<br>
 * 
 */

public class VASConfigurationDAOImpl extends BasisCodeDAO<VASConfiguration> implements VASConfigurationDAO {

	private static Logger logger = Logger.getLogger(VASConfigurationDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	
	public VASConfigurationDAOImpl() {
		super();
	}


	/**
	 * This method set the Work Flow id based on the module name and return the new VASConfiguration 
	 * @return VASConfiguration
	 */

	@Override
	public VASConfiguration getVASConfiguration() {
		logger.debug("Entering");
		logger.debug("Leaving");
		return new VASConfiguration();
	}


	/**
	 * This method get the module from method getVASConfiguration() and set the new record flag as true and return VASConfiguration()   
	 * @return VASConfiguration
	 */


	@Override
	public VASConfiguration getNewVASConfiguration() {
		logger.debug("Entering");
		VASConfiguration vASConfiguration = getVASConfiguration();
		vASConfiguration.setNewRecord(true);
		logger.debug("Leaving");
		return vASConfiguration;
	}

	/**
	 * Fetch the Record  VASConfiguration details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return VASConfiguration
	 */
	@Override
	public VASConfiguration getVASConfigurationByCode(String productCode, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder("Select ProductCode, ProductDesc, RecAgainst, FeeAccrued, FeeAccounting, AccrualAccounting,");
		sql.append(" RecurringType, FreeLockPeriod, PreValidationReq, PostValidationReq, Active, Remarks, ProductType, VasFee,");
		sql.append("  AllowFeeToModify, ManufacturerId, PreValidation, PostValidation,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(" ,FeeAccountingName, AccrualAccountingName, FeeAccountingDesc, AccrualAccountingDesc");
			sql.append(" ,ProductTypeDesc, ProductCategory, ManufacturerName");
		}
		sql.append(" From VasStructure");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ProductCode =:ProductCode");
		logger.debug("selectSql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("ProductCode", productCode);
		RowMapper<VASConfiguration> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VASConfiguration.class);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		} finally {
			source = null;
			sql = null;
		}
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the VasStructure or VasStructure_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete VASConfiguration by key ProductCode
	 * 
	 * @param VASConfiguration (vASConfiguration)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(VASConfiguration vASConfiguration,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From VasStructure");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ProductCode =:ProductCode");
	
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASConfiguration);
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
	 * This method insert new Records into VasStructure or VasStructure_Temp.
	 *
	 * save VASConfiguration 
	 * 
	 * @param VASConfiguration (vASConfiguration)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(VASConfiguration vASConfiguration,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into VasStructure");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ProductCode, ProductDesc, RecAgainst, FeeAccrued, FeeAccounting, AccrualAccounting, RecurringType, FreeLockPeriod, PreValidationReq, PostValidationReq, Active, Remarks, ");
		insertSql.append("  ProductType, VasFee, AllowFeeToModify, ManufacturerId, PreValidation, PostValidation, ");
		insertSql.append("  Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append("  Values(:ProductCode, :ProductDesc, :RecAgainst, :FeeAccrued, :FeeAccounting, :AccrualAccounting, :RecurringType, :FreeLockPeriod, :PreValidationReq, :PostValidationReq, :Active, :Remarks, ");
		insertSql.append("  :ProductType, :VasFee, :AllowFeeToModify, :ManufacturerId, :PreValidation, :PostValidation, ");
		insertSql.append("  :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASConfiguration);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vASConfiguration.getId();
	}
	
	/**
	 * This method updates the Record VasStructure or VasStructure_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update VASConfiguration by key ProductCode and Version
	 * 
	 * @param VASConfiguration (vASConfiguration)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(VASConfiguration vASConfiguration,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update VasStructure");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ProductDesc = :ProductDesc, RecAgainst = :RecAgainst, FeeAccrued = :FeeAccrued, FeeAccounting = :FeeAccounting,");
		updateSql.append("  AccrualAccounting = :AccrualAccounting, RecurringType = :RecurringType, FreeLockPeriod = :FreeLockPeriod,");
		updateSql.append("	PreValidationReq = :PreValidationReq, PostValidationReq = :PostValidationReq, Active = :Active, Remarks = :Remarks,");
		updateSql.append("  ProductType= :ProductType , VasFee = :VasFee, AllowFeeToModify = :AllowFeeToModify, ManufacturerId= :ManufacturerId, ");
		updateSql.append("  PreValidation = :PreValidation, PostValidation= :PostValidation, Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append("  RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ProductCode =:ProductCode");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASConfiguration);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/*
	 * Check whether the VASProducttype exists in VASRecording or not
	 */
	@Override
	public boolean isVASTypeExists(String productType) {
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from VasStructure T1 INNER JOIN VASRecording T2 On T1.ProductCode = T2.ProductCode");
		sql.append(" Where T1.ProductType = :ProductType ");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("ProductType", productType);
		try {
			if (this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return false;
	}
	
	@Override
	public int getFeeAccountingCount(long feeAccountId, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select Count(*) From VasStructure");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FeeAccounting = :FeeAccounting");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FeeAccounting", feeAccountId);

		try {
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}

		logger.debug("Leaving");

		return count;
	}
	
}