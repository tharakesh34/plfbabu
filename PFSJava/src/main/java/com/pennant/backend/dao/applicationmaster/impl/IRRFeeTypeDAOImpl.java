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
 * FileName    		:  IRRFeeTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2017    														*
 *                                                                  						*
 * Modified Date    :  21-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster.impl;

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
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.IRRFeeTypeDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.applicationmaster.IRRFeeType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>IRRFeeType</code> with set of CRUD
 * operations.
 */
public class IRRFeeTypeDAOImpl extends BasisNextidDaoImpl<IRRFeeType> implements IRRFeeTypeDAO {
	private static Logger logger = Logger.getLogger(IRRFeeTypeDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public IRRFeeTypeDAOImpl() {
		super();
	}

	@Override
	public IRRFeeType getIRRFeeType(long iRRID, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" iRRID, feeTypeID, feePercentage, ");
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From IRRFeeTypes");
		sql.append(type);
		sql.append(" Where iRRID = :iRRID AND feeTypeID = :feeTypeID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		IRRFeeType iRRFeeType = new IRRFeeType();
		iRRFeeType.setIRRID(iRRID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(iRRFeeType);
		RowMapper<IRRFeeType> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IRRFeeType.class);

		try {
			iRRFeeType = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			iRRFeeType = null;
		}

		logger.debug(Literal.LEAVING);
		return iRRFeeType;
	}

	@Override
	public List<IRRFeeType> getIRRFeeTypeList(long iRRID, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" iRRID, feeTypeID, feePercentage,");
		if (type.contains("View")) {
			sql.append(" feeTypeCode, feeTypeDesc,");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From IRRFeeTypes");
		sql.append(type);
		sql.append(" Where iRRID = :iRRID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		IRRFeeType iRRFeeType = new IRRFeeType();
		iRRFeeType.setIRRID(iRRID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(iRRFeeType);
		RowMapper<IRRFeeType> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IRRFeeType.class);

		List<IRRFeeType> feeTypes = namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);

		logger.debug(Literal.LEAVING);
		return feeTypes;
	}

	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Records from the CollateralThirdParty or
	 * CollateralThirdParty_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete CollateralThirdParty Details
	 * by key reference
	 * 
	 * @param CollateralThirdParty
	 *            Details (collateralThirdParty)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteList(IRRFeeType irrFeeType, TableType type) {
		logger.debug(Literal.ENTERING);

		StringBuilder deleteSql = new StringBuilder("Delete From IRRFeeTypes");
		deleteSql.append(StringUtils.trimToEmpty(type.getSuffix()));
		deleteSql.append(" Where  iRRID = :IRRID ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(irrFeeType);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	public String save(IRRFeeType irrFeeType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into IRRFeeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" (IRRID, feeTypeID, feePercentage, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :iRRID, :feeTypeID, :feePercentage, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(irrFeeType);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(irrFeeType.getIRRID());

	}

	public void update(IRRFeeType irrFeeType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update IRRFeeTypes");
		sql.append(tableType.getSuffix());
		sql.append("  set feePercentage = :FeePercentage, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where iRRID = :iRRID AND feeTypeID = :FeeTypeID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(irrFeeType);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	public void delete(IRRFeeType irrFeeType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from IRRFeeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" where iRRID = :iRRID AND feeTypeID = :FeeTypeID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(irrFeeType);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

}
