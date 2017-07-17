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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeInsuranceDAO;
import com.pennant.backend.model.applicationmaster.FinTypeInsurances;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinanceType model</b> class.<br>
 * 
 */
public class FinTypeInsuranceDAOImpl extends BasisCodeDAO<FinTypeInsurances> implements FinTypeInsuranceDAO{

	private static Logger logger = Logger.getLogger(FinTypeInsuranceDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinTypeInsuranceDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceType
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinTypeInsurances getFinTypeInsurance(){
		logger.debug("Entering");
		FinTypeInsurances finTypeInsurance = new FinTypeInsurances("");
		logger.debug("Leaving");
		return finTypeInsurance;
	}

	/**
	 * This method get the module from method getFinanceType() and set the new record flag as true and
	 * return FinanceType()
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinTypeInsurances getNewFinTypeInsurance() {
		logger.debug("Entering");
		FinTypeInsurances finTypeInsurance = getFinTypeInsurance();
		finTypeInsurance.setNewRecord(true);
		logger.debug("Leaving");
		return finTypeInsurance;
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
	public List<FinTypeInsurances> getFinTypeInsuranceListByID(final String id, int moduleId, String type) {
		logger.debug("Entering");
		FinTypeInsurances finTypeInsurance = new FinTypeInsurances();
		finTypeInsurance.setId(id);
		finTypeInsurance.setModuleId(moduleId);

		StringBuilder selectSql = new StringBuilder("SELECT FinType,InsuranceType,PolicyType, DftPayType, CalType, ");
		selectSql.append(" AmountRule, Mandatory,AlwRateChange,ConstAmt,Percentage,CalculateOn,");
		if (type.contains("View")) {
			selectSql.append(" InsuranceTypeDesc,RuleCodeDesc,PolicyDesc,");
		}
		selectSql.append("Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId ");

		selectSql.append(" FROM FinTypeInsurances");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeInsurance);
		RowMapper<FinTypeInsurances> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeInsurances.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	/**
	 * Method for Fetching Mandatory Insurances allowed against Finance Type
	 */
	@Override
	public List<String> getFinTypeInsurances(final String finType, int moduleId) {
		logger.debug("Entering");
		
		List<String> mandatoryInsurances = null;
		StringBuilder selectSql = new StringBuilder("select policyType ");
		selectSql.append(" from FinTypeInsurances where FinType ='");
		selectSql.append(finType);
		selectSql.append("'  and Mandatory = 1 ");
		selectSql.append(" and ModuleId = " + moduleId);
		logger.debug("selectSql: " + selectSql.toString());

		try {
			mandatoryInsurances =  this.namedParameterJdbcTemplate.getJdbcOperations().queryForList(selectSql.toString(), String.class);
		} catch (Exception e) {
			mandatoryInsurances = new ArrayList<>();
			logger.error("Exception: ", e);
		}
		
		logger.debug("Leaving");
		return mandatoryInsurances;
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
	public FinTypeInsurances getFinTypeInsuranceByID(FinTypeInsurances finTypeInsurance, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT FinType,InsuranceType,PolicyType, DftPayType, CalType, ");
		selectSql.append(" AmountRule, Mandatory,AlwRateChange,ConstAmt,Percentage,CalculateOn,");
		if (type.contains("View")) {
			selectSql.append(" InsuranceTypeDesc,RuleCodeDesc,PolicyDesc,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");

		selectSql.append(" FROM FinTypeInsurances");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType AND InsuranceType = :InsuranceType AND ModuleId = :ModuleId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeInsurance);
		RowMapper<FinTypeInsurances> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeInsurances.class);

		try {
			finTypeInsurance = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finTypeInsurance = null;
		}
		logger.debug("Leaving");
		return finTypeInsurance;
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
	   public String save(FinTypeInsurances finTypeInsurance, String type) {
		logger.debug("Entering ");
		
		StringBuilder insertSql = new StringBuilder("Insert Into FinTypeInsurances" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (FinType, InsuranceType, PolicyType,DftPayType, CalType," );
		insertSql.append(" AmountRule, Mandatory,AlwRateChange,ConstAmt,Percentage,CalculateOn," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, ModuleId)" );
		insertSql.append(" Values(:FinType, :InsuranceType, :PolicyType, :DftPayType, :CalType," );
		insertSql.append(" :AmountRule, :Mandatory, :AlwRateChange, :ConstAmt, :Percentage, :CalculateOn," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :ModuleId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeInsurance);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return finTypeInsurance.getId();
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
	public void update(FinTypeInsurances finTypeInsurance, String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		
		StringBuilder updateSql = new StringBuilder("Update FinTypeInsurances" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set PolicyType = :PolicyType, DftPayType = :DftPayType,");
		updateSql.append(" CalType = :CalType,AmountRule = :AmountRule,");
		updateSql.append(" Mandatory = :Mandatory,AlwRateChange =:AlwRateChange,ConstAmt =:ConstAmt,Percentage =:Percentage,CalculateOn=:CalculateOn,");
		updateSql.append(" LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, ModuleId = :ModuleId" );
		updateSql.append(" Where FinType =:FinType And InsuranceType=:InsuranceType  And ModuleId = :ModuleId");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeInsurance);
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
	public void delete(FinTypeInsurances finTypeInsurance,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeInsurances");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where FinType =:FinType And InsuranceType =:InsuranceType And  ModuleId = :ModuleId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeInsurance);
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
	public void deleteByFinType(String finType, int moduleId, String type) {
		logger.debug("Entering");
		FinTypeInsurances finTypeInsurance = new FinTypeInsurances();
		finTypeInsurance.setFinType(finType);
		finTypeInsurance.setModuleId(moduleId);
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeInsurances");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType And ModuleId = :ModuleId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeInsurance);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}
	
	@Override
	public List<FinTypeInsurances> getFinTypeInsurances(String policyType, int moduleId, String type) {
		logger.debug("Entering");
		FinTypeInsurances finTypeInsurance = new FinTypeInsurances();
		finTypeInsurance.setPolicyType(policyType);
		finTypeInsurance.setModuleId(moduleId);

		StringBuilder selectSql = new StringBuilder("SELECT FinType,InsuranceType,PolicyType, DftPayType, CalType, ");
		selectSql.append(" AmountRule, Mandatory,ConstAmt,Percentage,CalculateOn,");
		if (type.contains("View")) {
			selectSql.append(" InsuranceTypeDesc,RuleCodeDesc,PolicyDesc,");
		}
		selectSql.append("Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");

		selectSql.append(" FROM FinTypeInsurances");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PolicyType = :PolicyType And ModuleId = :ModuleId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeInsurance);
		RowMapper<FinTypeInsurances> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeInsurances.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	@Override
	public int getFinTypeInsuranceByRuleCode(String ruleCode, String type) {
		logger.debug("Entering");
		FinTypeInsurances finTypeInsurance = new FinTypeInsurances();
		finTypeInsurance.setAmountRule(ruleCode);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinTypeInsurances");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AmountRule =:AmountRule");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeInsurance);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
	

}