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
 * FileName    		:  PFSParameterDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-07-2011    														*
 *                                                                  						*
 * Modified Date    :  12-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-07-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.smtmasters.impl;

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
import com.pennant.backend.dao.smtmasters.PFSParameterDAO;
import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>PFSParameter model</b> class.<br>
 * 
 */
public class PFSParameterDAOImpl extends BasisCodeDAO<PFSParameter> implements PFSParameterDAO {

	private static Logger logger = Logger.getLogger(PFSParameterDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public PFSParameterDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record System Parameter details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return PFSParameter
	 */
	@Override
	public PFSParameter getPFSParameterById(final String id, String type) {
		//logger.debug("Entering");

		PFSParameter parameter = new PFSParameter();
		parameter.setId(id);

		StringBuilder sql = new StringBuilder("SELECT SysParmCode, SysParmDesc, ");
		sql.append(" SysParmType, SysParmMaint, SysParmValue, SysParmLength, SysParmDec, ");
		sql.append(" SysParmList, SysParmValdMod, SysParmDescription, ");
		sql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode," );
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId " );
		sql.append(" FROM  SMTparameters");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where SysParmCode =:SysParmCode ");

		//logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(parameter);
		RowMapper<PFSParameter> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PFSParameter.class);

		try {
			parameter = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			parameter = null;
		}

		//logger.debug("Leaving");
		return parameter;
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
	 * This method Deletes the Record from the SMTparameters or
	 * SMTparameters_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete System Parameter by key SysParmCode
	 * 
	 * @param System
	 *            Parameter (pFSParameter)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(PFSParameter pFSParameter, String type) {
		logger.debug("Entering ");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From SMTparameters");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SysParmCode =:SysParmCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				pFSParameter);
	
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
					beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method insert new Records into SMTparameters or SMTparameters_Temp.
	 * 
	 * save System Parameter
	 * 
	 * @param System
	 *            Parameter (pFSParameter)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(PFSParameter pFSParameter, String type) {
		logger.debug("Entering ");
		
		StringBuilder insertSql = new StringBuilder("Insert Into SMTparameters");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SysParmCode, SysParmDesc, SysParmType, SysParmMaint, SysParmValue, SysParmLength, SysParmDec,");
		insertSql.append(" SysParmList, SysParmValdMod, SysParmDescription,Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:SysParmCode, :SysParmDesc, :SysParmType, :SysParmMaint, :SysParmValue, :SysParmLength,");
		insertSql.append(" :SysParmDec, :SysParmList, :SysParmValdMod, :SysParmDescription,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," ); 
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
		return pFSParameter.getId();
	}

	/**
	 * This method updates the Record SMTparameters or SMTparameters_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update System Parameter by key SysParmCode and Version
	 * 
	 * @param System
	 *            Parameter (pFSParameter)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(PFSParameter pFSParameter, String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		StringBuilder updateSql = new StringBuilder("Update SMTparameters");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SysParmDesc = :SysParmDesc, " );
		updateSql.append(" SysParmType = :SysParmType, SysParmMaint = :SysParmMaint, " );
		updateSql.append(" SysParmValue = :SysParmValue, SysParmLength = :SysParmLength, " );
		updateSql.append(" SysParmDec = :SysParmDec, SysParmList = :SysParmList, " );
		updateSql.append(" SysParmValdMod = :SysParmValdMod, SysParmDescription = :SysParmDescription, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where SysParmCode =:SysParmCode");

		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * This method updates the Record SMTparameters or SMTparameters_Temp. 
	 * update System Parameter value by key SysParmCode 
	 * @param sysParmCode
	 * @param sysParmValue
	 * @param type
	 */
	@Override
	public void update(String sysParmCode, String sysParmValue,String type) {
		logger.debug("Entering ");
		
		PFSParameter pFSParameter=new PFSParameter();
		pFSParameter.setSysParmCode(sysParmCode);
		pFSParameter.setSysParmValue(sysParmValue);
		
		StringBuilder updateSql = new StringBuilder("Update SMTparameters");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SysParmValue = :SysParmValue " );
		updateSql.append(" Where SysParmCode =:SysParmCode");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving ");
	}

	/**
	 * Method for Updating Parameter Value
	 */
	@Override
    public void updateParmValue(PFSParameter pFSParameter) {
		int recordCount = 0;
		logger.debug("Entering ");
		StringBuilder updateSql = new StringBuilder("Update SMTparameters");
		updateSql.append(" Set SysParmValue = :SysParmValue Where SysParmCode =:SysParmCode " );

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}

	
	
	/**
	 * Method for getting the List of Static PFSParameters list
	 */
	public List<PFSParameter> getAllPFSParameter() {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder(" Select SysParmCode, SysParmDesc, " );
		selectSql.append(" SysParmType, SysParmMaint, SysParmValue, SysParmLength, SysParmDec, " );
		selectSql.append(" SysParmList, SysParmValdMod, SysParmDescription, ");
		selectSql.append(" Version , LastMntBy, LastMntOn From SMTparameters");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new PFSParameter());
		
		RowMapper<PFSParameter> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PFSParameter.class);
		List<PFSParameter> systemParms = this.namedParameterJdbcTemplate.query(selectSql.toString(),beanParameters, typeRowMapper);  

		logger.debug("Leaving");
		return systemParms;
	}
	
	/**
	 * Method for get the list of Global Variable records
	 */
	public List<GlobalVariable> getGlobaVariables(){
		logger.debug("Entering");
		StringBuilder selectQry = new StringBuilder("Select  varCode,varName,varValue,varType " );
		selectQry.append(" from GlobalVariable" );
		logger.debug("selectSql: " + selectQry.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new GlobalVariable());
		RowMapper<GlobalVariable> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(GlobalVariable.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectQry.toString(),beanParameters, typeRowMapper);
	}
	
}