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
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinanceType model</b> class.<br>
 * 
 */
public class FinTypeAccountingDAOImpl extends BasisCodeDAO<FinTypeAccounting> implements FinTypeAccountingDAO {

	private static Logger logger = Logger.getLogger(FinTypeAccountingDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinTypeAccountingDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceType
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinTypeAccounting getFinTypeAccounting() {
		logger.debug("Entering");
		FinTypeAccounting finTypeAccounting = new FinTypeAccounting("");
		logger.debug("Leaving");
		return finTypeAccounting;
	}

	/**
	 * This method get the module from method getFinanceType() and set the new record flag as true and
	 * return FinanceType()
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinTypeAccounting getNewFinTypeAccounting() {
		logger.debug("Entering");
		FinTypeAccounting finTypeAccounting = getFinTypeAccounting();
		finTypeAccounting.setNewRecord(true);
		logger.debug("Leaving");
		return finTypeAccounting;
	}



	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *         (String)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return FinTypeAccounting List
	 */
	@Override
	public List<FinTypeAccounting> getFinTypeAccountingListByID(final String id, int moduleId, String type) {
		logger.debug("Entering");
		FinTypeAccounting finTypeAccounting = new FinTypeAccounting();
		finTypeAccounting.setId(id);
		finTypeAccounting.setModuleId(moduleId);

		StringBuilder selectSql = new StringBuilder("SELECT FinType, Event, AccountSetID, ");
		if (type.contains("View")) {
			selectSql.append(" lovDescEventAccountingName,lovDescAccountingName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");

		selectSql.append(" FROM FinTypeAccounting");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType And ModuleId = :ModuleId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		RowMapper<FinTypeAccounting> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeAccounting.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	
	@Override
	public List<FinTypeAccounting> getFinTypeAccountingByFinType(String finType, int moduleId) {
		logger.debug("Entering");
		FinTypeAccounting finTypeAccounting = new FinTypeAccounting();
		finTypeAccounting.setFinType(finType);
		finTypeAccounting.setModuleId(moduleId);
		StringBuilder selectSql = new StringBuilder("SELECT FinType, Event, AccountSetID ");
		selectSql.append(" FROM FinTypeAccounting");
		selectSql.append(" Where FinType = :FinType And ModuleId = :ModuleId");
		
		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		RowMapper<FinTypeAccounting> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeAccounting.class);
		
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
	 * @return FinTypeAccounting
	 */
	@Override
	public FinTypeAccounting getFinTypeAccountingByID(FinTypeAccounting finTypeAccounting, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT FinType, Event, AccountSetID, ");
		if (type.contains("View")) {
		selectSql.append(" lovDescEventAccountingName,lovDescAccountingName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");

		selectSql.append(" FROM FinTypeAccounting");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType And Event = :Event And ModuleId = :ModuleId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		RowMapper<FinTypeAccounting> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeAccounting.class);

		try {
			finTypeAccounting = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finTypeAccounting = null;
		}
		logger.debug("Leaving");
		return finTypeAccounting;
	}

	/**
	 * @param dataSource
	 *         the dataSource to set
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}


	/**
	 * This method insert new Records into FinTypeAccounting or FinTypeAccounting_Temp.
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
	public String save(FinTypeAccounting finTypeAccounting, String type) {
		logger.debug("Entering ");
		
		StringBuilder insertSql = new StringBuilder("Insert Into FinTypeAccounting" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (FinType, Event, AccountSetID," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, ModuleId)" );
		insertSql.append(" Values(:FinType, :Event, :AccountSetID," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :ModuleId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return finTypeAccounting.getId();
	}

	/**
	 * This method updates the Record FinTypeAccounting or FinTypeAccounting_Temp. if Record not updated
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
	public void update(FinTypeAccounting finTypeAccounting, String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		
		StringBuilder updateSql = new StringBuilder("Update FinTypeAccounting" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set AccountSetID = :AccountSetID,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, ModuleId = :ModuleId" );
		updateSql.append(" Where FinType=:FinType And Event=:Event And ModuleId = :ModuleId");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}
	
	
	/**
	 * This method Deletes the Record from the FinTypeAccounting or FinTypeAccounting_Temp. if Record not
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
	public void delete(FinTypeAccounting finTypeAccounting,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeAccounting");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where FinType = :FinType And Event = :Event And ModuleId = :ModuleId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
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
		FinTypeAccounting finTypeAccounting = new FinTypeAccounting();
		finTypeAccounting.setFinType(finType);
		finTypeAccounting.setModuleId(moduleId);
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeAccounting");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType And ModuleId = :ModuleId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}
	
	
	@Override
	public Long getAccountSetID(String finType, String event, int moduleId) {
		logger.debug("Entering");

		if(StringUtils.isEmpty(finType) || StringUtils.isEmpty(event)){
			logger.debug("Leaving");
			return Long.MIN_VALUE;
		}
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
		source.addValue("Event", event);
		source.addValue("ModuleId", moduleId);
		
		StringBuilder selectSql = new StringBuilder("SELECT AccountSetID  FROM FinTypeAccounting ");
		selectSql.append(" Where FinType = :FinType And Event = :Event And ModuleId = :ModuleId");

		logger.debug("selectSql: " + selectSql.toString());

		Long result = Long.MIN_VALUE;
		try {
			result =  this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return result;
	}

	@Override
	public List<String> getFinTypeAccounting(String event,Long accountSetId, int moduleId) {
		logger.debug("Entering");
		
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("Event", event);
		mapSqlParameterSource.addValue("AccountSetID", accountSetId);
		mapSqlParameterSource.addValue("ModuleId", moduleId);
		
		StringBuilder selectSql = new StringBuilder(" Select FinType FROM FinTypeAccounting");
		selectSql.append(" Where Event = :Event AND AccountSetID = :AccountSetID AND ModuleId = :ModuleId" );
		
		logger.debug("selectSql: " + selectSql.toString());
		
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), mapSqlParameterSource, String.class);
	}
	
	@Override
	public List<Long> getFinTypeAccounting(String fintype, List<String> events, int moduleId) {
		logger.debug("Entering");
		
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinType", fintype);
		mapSqlParameterSource.addValue("Event", events);
		mapSqlParameterSource.addValue("ModuleId", moduleId);
		
		StringBuilder selectSql = new StringBuilder(" Select AccountSetID FROM FinTypeAccounting");
		selectSql.append(" Where FinType = :FinType AND Event IN (:Event)  AND ModuleId = :ModuleId" );
		
		logger.debug("selectSql: " + selectSql.toString());
		
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), mapSqlParameterSource, Long.class);
	}
	
}