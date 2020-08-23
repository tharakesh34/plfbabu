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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.smtmasters.PFSParameterDAO;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>PFSParameter model</b> class.<br>
 * 
 */
public class PFSParameterDAOImpl extends BasicDao<PFSParameter> implements PFSParameterDAO {
	private static Logger logger = LogManager.getLogger(PFSParameterDAOImpl.class);

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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" SysParmCode, SysParmDesc, SysParmType, SysParmMaint, SysParmValue, SysParmLength");
		sql.append(", SysParmDec, SysParmList, SysParmValdMod, SysParmDescription, Version, LastMntOn");
		sql.append(", LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId");
		sql.append(" from SMTparameters");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where SysParmCode = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id },
					new RowMapper<PFSParameter>() {
						@Override
						public PFSParameter mapRow(ResultSet rs, int rowNum) throws SQLException {
							PFSParameter smtp = new PFSParameter();

							smtp.setSysParmCode(rs.getString("SysParmCode"));
							smtp.setSysParmDesc(rs.getString("SysParmDesc"));
							smtp.setSysParmType(rs.getString("SysParmType"));
							smtp.setSysParmMaint(rs.getBoolean("SysParmMaint"));
							smtp.setSysParmValue(rs.getString("SysParmValue"));
							smtp.setSysParmLength(rs.getInt("SysParmLength"));
							smtp.setSysParmDec(rs.getInt("SysParmDec"));
							smtp.setSysParmList(rs.getString("SysParmList"));
							smtp.setSysParmValdMod(rs.getString("SysParmValdMod"));
							smtp.setSysParmDescription(rs.getString("SysParmDescription"));
							smtp.setVersion(rs.getInt("Version"));
							smtp.setLastMntOn(rs.getTimestamp("LastMntOn"));
							smtp.setLastMntBy(rs.getLong("LastMntBy"));
							smtp.setRecordStatus(rs.getString("RecordStatus"));
							smtp.setRoleCode(rs.getString("RoleCode"));
							smtp.setNextRoleCode(rs.getString("NextRoleCode"));
							smtp.setTaskId(rs.getString("TaskId"));
							smtp.setNextTaskId(rs.getString("NextTaskId"));
							smtp.setRecordType(rs.getString("RecordType"));
							smtp.setWorkflowId(rs.getLong("WorkflowId"));

							return smtp;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * This method Deletes the Record from the SMTparameters or SMTparameters_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete System Parameter by key SysParmCode
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

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

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
		insertSql.append(
				" (SysParmCode, SysParmDesc, SysParmType, SysParmMaint, SysParmValue, SysParmLength, SysParmDec,");
		insertSql.append(
				" SysParmList, SysParmValdMod, SysParmDescription,Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:SysParmCode, :SysParmDesc, :SysParmType, :SysParmMaint, :SysParmValue, :SysParmLength,");
		insertSql.append(" :SysParmDec, :SysParmList, :SysParmValdMod, :SysParmDescription,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
		return pFSParameter.getId();
	}

	/**
	 * This method updates the Record SMTparameters or SMTparameters_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update System Parameter by key SysParmCode and Version
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
		updateSql.append(" Set SysParmDesc = :SysParmDesc, ");
		updateSql.append(" SysParmType = :SysParmType, SysParmMaint = :SysParmMaint, ");
		updateSql.append(" SysParmValue = :SysParmValue, SysParmLength = :SysParmLength, ");
		updateSql.append(" SysParmDec = :SysParmDec, SysParmList = :SysParmList, ");
		updateSql.append(" SysParmValdMod = :SysParmValdMod, SysParmDescription = :SysParmDescription, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where SysParmCode =:SysParmCode");

		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method updates the Record SMTparameters or SMTparameters_Temp. update System Parameter value by key
	 * SysParmCode
	 * 
	 * @param sysParmCode
	 * @param sysParmValue
	 * @param type
	 */
	@Override
	public void update(String sysParmCode, String sysParmValue, String type) {
		logger.debug("Entering ");

		PFSParameter pFSParameter = new PFSParameter();
		pFSParameter.setSysParmCode(sysParmCode);
		pFSParameter.setSysParmValue(sysParmValue);

		StringBuilder updateSql = new StringBuilder("Update SMTparameters");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SysParmValue = :SysParmValue ");
		updateSql.append(" Where SysParmCode =:SysParmCode");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

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
		updateSql.append(" Set SysParmValue = :SysParmValue Where SysParmCode =:SysParmCode ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

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

		StringBuilder selectSql = new StringBuilder(" Select SysParmCode, SysParmDesc, ");
		selectSql.append(" SysParmType, SysParmMaint, SysParmValue, SysParmLength, SysParmDec, ");
		selectSql.append(" SysParmList, SysParmValdMod, SysParmDescription, ");
		selectSql.append(" Version , LastMntBy, LastMntOn From SMTparameters");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new PFSParameter());

		RowMapper<PFSParameter> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PFSParameter.class);
		List<PFSParameter> systemParms = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

		logger.debug("Leaving");
		return systemParms;
	}

	/**
	 * Method for get the list of Global Variable records
	 */
	public List<GlobalVariable> getGlobaVariables() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Code, Name, Value, Type");
		sql.append(" from GlobalVariables");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					// FIXME
				}
			}, new RowMapper<GlobalVariable>() {
				@Override
				public GlobalVariable mapRow(ResultSet rs, int rowNum) throws SQLException {
					GlobalVariable gv = new GlobalVariable();

					gv.setId(rs.getLong("Id"));
					gv.setCode(rs.getString("Code"));
					gv.setName(rs.getString("Name"));
					gv.setValue(rs.getString("Value"));
					gv.setType(rs.getString("Type"));

					return gv;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

}