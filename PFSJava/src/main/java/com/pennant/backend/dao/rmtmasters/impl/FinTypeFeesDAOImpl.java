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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinanceType model</b> class.<br>
 * 
 */
public class FinTypeFeesDAOImpl extends BasisCodeDAO<FinTypeFees> implements FinTypeFeesDAO {

	private static Logger logger = Logger.getLogger(FinTypeFeesDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinTypeFeesDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceType
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinTypeFees getFinTypeFees() {
		logger.debug("Entering");
		FinTypeFees finTypeFees = new FinTypeFees("");
		logger.debug("Leaving");
		return finTypeFees;
	}

	/**
	 * This method get the module from method getFinanceType() and set the new record flag as true and
	 * return FinanceType()
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinTypeFees getNewFinTypeFees() {
		logger.debug("Entering");
		FinTypeFees finTypeFees = getFinTypeFees();
		finTypeFees.setNewRecord(true);
		logger.debug("Leaving");
		return finTypeFees;
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
	public List<FinTypeFees> getFinTypeFeesListByID(final String id, int moduleId, String type) {
		logger.debug("Entering");
		FinTypeFees finTypeFees = new FinTypeFees();
		finTypeFees.setId(id);
		finTypeFees.setModuleId(moduleId);

		StringBuilder selectSql = new StringBuilder("SELECT FinType, OriginationFee, FinEvent, FeeTypeID, FeeOrder,");
		selectSql.append(" FeeScheduleMethod, CalculationType, RuleCode, Amount, Percentage, CalculateOn, AlwDeviation,");
		selectSql.append(" MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, Active,");
		if (type.contains("View")) {
			selectSql.append(" FeeTypeCode, FeeTypeDesc, RuleDesc,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");

		selectSql.append(" FROM FinTypeFees");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType And ModuleId = :ModuleId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);
		RowMapper<FinTypeFees> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeFees.class);
		
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
	public List<FinTypeFees> getFinTypeFeesList(String finType, String finEvent, String type, boolean origination, int moduleId) {
		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource=new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinType", finType);
		mapSqlParameterSource.addValue("FinEvent", finEvent);
		mapSqlParameterSource.addValue("Origination", origination);
		mapSqlParameterSource.addValue("ModuleId", moduleId);
		
		StringBuilder selectSql = new StringBuilder("SELECT FinType, OriginationFee, FinEvent, FeeTypeID, FeeOrder,");
		selectSql.append(" FeeScheduleMethod, CalculationType, RuleCode, Amount, Percentage, CalculateOn, AlwDeviation,");
		selectSql.append(" MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, Active,");
		if (type.contains("View")) {
			selectSql.append(" FeeTypeCode, FeeTypeDesc, RuleDesc,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");
		
		selectSql.append(" FROM FinTypeFees");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType AND FinEvent = :FinEvent AND Active = 1");
		selectSql.append(" AND OriginationFee = :Origination  AND ModuleId = :ModuleId");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinTypeFees> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeFees.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
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
	public List<FinTypeFees> getFinTypeFeesList(String finType, List<String> finEvents, String type, int moduleId) {
		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource=new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinType", finType);
		mapSqlParameterSource.addValue("FinEvent", finEvents);
		mapSqlParameterSource.addValue("ModuleId", moduleId);
		
		StringBuilder selectSql = new StringBuilder("SELECT FinType, OriginationFee, FinEvent, FeeTypeID, FeeOrder,");
		selectSql.append(" FeeScheduleMethod, CalculationType, RuleCode, Amount, Percentage, CalculateOn, AlwDeviation,");
		selectSql.append(" MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, Active,");
		if (type.contains("View")) {
			selectSql.append(" FeeTypeCode, FeeTypeDesc, RuleDesc,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");
		
		selectSql.append(" FROM FinTypeFees");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType AND FinEvent IN (:FinEvent) AND ModuleId = :ModuleId ");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinTypeFees> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeFees.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
	}
	
	@Override
	public List<FinTypeFees> getFinTypeFeeCodes(String finType, int moduleId) {
		logger.debug("Entering");
		
		MapSqlParameterSource mapSqlParameterSource=new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinType", finType);
		mapSqlParameterSource.addValue("ModuleId", moduleId);

		StringBuilder selectSql = new StringBuilder("SELECT T2.FeeTypeCode,T2.FeeTypeDesc,T1.AlwDeviation  From FinTypeFees T1");
		selectSql.append("  INNER JOIN FeeTypes T2 ON T1.FeeTypeID = T2.FeeTypeID  ");
		selectSql.append(" Where T1.OriginationFee = 1 AND T1.FinType = :FinType AND AND T1.ModuleId = :ModuleId");
		

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinTypeFees> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeFees.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
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
	public FinTypeFees getFinTypeFeesByID(FinTypeFees finTypeFees, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT FinType, OriginationFee, FinEvent, FeeTypeID, FeeOrder,");
		selectSql.append(" FeeScheduleMethod, CalculationType, RuleCode, Amount, Percentage, CalculateOn, AlwDeviation,");
		selectSql.append(" MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, Active,");
		if (type.contains("View")) {
			selectSql.append(" FeeTypeCode, FeeTypeDesc, RuleDesc,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");

		selectSql.append(" FROM FinTypeFees");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType And OriginationFee = :OriginationFee And FinEvent = :FinEvent And FeeTypeID = :FeeTypeID And ModuleId = :ModuleId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);
		RowMapper<FinTypeFees> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeFees.class);

		try {
			finTypeFees = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finTypeFees = null;
		}
		logger.debug("Leaving");
		return finTypeFees;
	}


	/**
	 * This method refresh the Record.
	 * 
	 * @param FinanceType
	 *         (financeType)
	 * @return void
	 */
	@Override
	public void refresh(FinTypeFees finTypeFees) {

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
	public String save(FinTypeFees finTypeFees, String type) {
		logger.debug("Entering ");
		
		StringBuilder insertSql = new StringBuilder("Insert Into FinTypeFees" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (FinType, OriginationFee, FinEvent, FeeTypeID, FeeOrder," );
		insertSql.append(" FeeScheduleMethod, CalculationType, RuleCode, Amount, Percentage," );
		insertSql.append(" CalculateOn, AlwDeviation, MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, Active," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, ModuleId)" );
		insertSql.append(" Values(:FinType, :OriginationFee, :FinEvent, :FeeTypeID, :FeeOrder," );
		insertSql.append(" :FeeScheduleMethod, :CalculationType, :RuleCode, :Amount, :Percentage," );
		insertSql.append(" :CalculateOn, :AlwDeviation, :MaxWaiverPerc, :AlwModifyFee, :AlwModifyFeeSchdMthd, :Active," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :ModuleId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return finTypeFees.getId();
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
	public void update(FinTypeFees finTypeFees, String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		
		StringBuilder updateSql = new StringBuilder("Update FinTypeFees" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set FeeOrder = :FeeOrder,FeeScheduleMethod = :FeeScheduleMethod, CalculationType = :CalculationType, ");
		updateSql.append(" RuleCode = :RuleCode, Amount = :Amount,Percentage = :Percentage, CalculateOn = :CalculateOn, AlwDeviation = :AlwDeviation, ");
		updateSql.append(" MaxWaiverPerc = :MaxWaiverPerc,AlwModifyFee = :AlwModifyFee, AlwModifyFeeSchdMthd = :AlwModifyFeeSchdMthd, Active = :Active,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, ModuleId = :ModuleId" );
		updateSql.append(" Where FinType =:FinType and OriginationFee=:OriginationFee and FinEvent=:FinEvent and FeeTypeID=:FeeTypeID And ModuleId = :ModuleId");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);
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
	public void delete(FinTypeFees finTypeFees,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeFees");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where FinType =:FinType And OriginationFee =:OriginationFee And FinEvent =:FinEvent And FeeTypeID =:FeeTypeID And ModuleId  = :ModuleId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);
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
	public void deleteByFinType(String finType, String type, int moduleId) {
		logger.debug("Entering");
		FinTypeFees finTypeFees = new FinTypeFees();
		finTypeFees.setFinType(finType);
		finTypeFees.setModuleId(moduleId);
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeFees");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType And ModuleId = :ModuleId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);
		
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}
	
	@Override
	public List<FinTypeFees> getFinTypeFeesList(String finEvent,List<String> finTypes, int moduleId) {
		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource=new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinEvent", finEvent);
		mapSqlParameterSource.addValue("FinType", finTypes);
		mapSqlParameterSource.addValue("ModuleId", moduleId);
		
		StringBuilder selectSql = new StringBuilder("SELECT FinType, OriginationFee, FinEvent, FeeTypeID, Active, FeeTypeCode");
		selectSql.append(" FROM FinTypeFees_AView");
		selectSql.append(" Where FinType IN (:FinType) AND FinEvent = :FinEvent AND ModuleId = :ModuleId");
		
		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinTypeFees> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeFees.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
	}
	
	@Override
	public int getFinTypeFeesByRuleCode(String ruleCode, String type) {
		logger.debug("Entering");
		FinTypeFees finTypeFees = new FinTypeFees();
		finTypeFees.setRuleCode(ruleCode);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinTypeFees");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleCode =:RuleCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
}