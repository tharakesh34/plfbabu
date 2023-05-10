/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : PFSParameterDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-07-2011 * * Modified
 * Date : 12-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.smtmasters.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

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
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return PFSParameter
	 */
	@Override
	public PFSParameter getPFSParameterById(final String id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" SysParmCode, SysParmDesc, SysParmType, SysParmMaint, SysParmValue, SysParmLength");
		sql.append(", SysParmDec, SysParmList, SysParmValdMod, SysParmDescription, Version, LastMntOn");
		sql.append(", LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId");
		sql.append(" From SMTParameters");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where SysParmCode = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
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
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record is not found in SMTParameters{} for the specified SysParmCode >> {}", type, id);
		}

		return null;
	}

	/**
	 * This method Deletes the Record from the SMTparameters or SMTparameters_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete System Parameter by key SysParmCode
	 * 
	 * @param System Parameter (pFSParameter)
	 * @param type   (String) ""/_Temp/_View
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
	 * @param System Parameter (pFSParameter)
	 * @param type   (String) ""/_Temp/_View
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
	 * @param System Parameter (pFSParameter)
	 * @param type   (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(PFSParameter pFSParameter, String type) {
		StringBuilder sql = new StringBuilder("Update SMTparameters");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set SysParmDesc = :SysParmDesc, ");
		sql.append(" SysParmType = :SysParmType, SysParmMaint = :SysParmMaint, ");
		sql.append(" SysParmValue = :SysParmValue, SysParmLength = :SysParmLength, ");
		sql.append(" SysParmDec = :SysParmDec, SysParmList = :SysParmList, ");
		sql.append(" SysParmValdMod = :SysParmValdMod, SysParmDescription = :SysParmDescription, ");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where SysParmCode =:SysParmCode");

		if (!type.endsWith("_Temp")) {
			sql.append(" AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pFSParameter);
		int recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void update(String sysParmCode, String sysParmValue, String type) {
		StringBuilder sql = new StringBuilder("Update SMTparameters");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set SysParmValue = ?");
		sql.append(" Where SysParmCode = ?");

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, sysParmValue);
			ps.setString(2, sysParmCode);

		});

		Timestamp updatedOn = new Timestamp(System.currentTimeMillis());

		saveLog(sysParmCode, sysParmValue, updatedOn);
	}

	private void saveLog(String sysParmCode, String sysParmValue, Timestamp updatedOn) {
		StringBuilder sql = new StringBuilder("Insert Into APP_DATES_LOG");
		sql.append(" (Code, Value, UpdatedOn)");
		sql.append(" Values (?, ?, ?)");

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, sysParmCode);
			ps.setString(2, sysParmValue);
			ps.setTimestamp(3, updatedOn);
		});

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

		RowMapper<PFSParameter> typeRowMapper = BeanPropertyRowMapper.newInstance(PFSParameter.class);
		List<PFSParameter> systemParms = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

		logger.debug("Leaving");
		return systemParms;
	}

	/**
	 * Method for get the list of Global Variable records
	 */
	public List<GlobalVariable> getGlobaVariables() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Code, Name, Value, Type");
		sql.append(" from GlobalVariables");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
		}, (rs, rowNum) -> {
			GlobalVariable gv = new GlobalVariable();

			gv.setId(rs.getLong("Id"));
			gv.setCode(rs.getString("Code"));
			gv.setName(rs.getString("Name"));
			gv.setValue(rs.getString("Value"));
			gv.setType(rs.getString("Type"));

			return gv;
		});

	}

}