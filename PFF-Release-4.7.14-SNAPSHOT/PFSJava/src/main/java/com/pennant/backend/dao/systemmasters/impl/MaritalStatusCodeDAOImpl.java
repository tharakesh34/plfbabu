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
 * FileName    		:  MaritalStatusCodeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.systemmasters.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.MaritalStatusCodeDAO;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>MaritalStatusCode model</b> class.<br>
 * 
 */
public class MaritalStatusCodeDAOImpl extends BasisCodeDAO<MaritalStatusCode> implements MaritalStatusCodeDAO {
	private static Logger logger = Logger.getLogger(MaritalStatusCodeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public MaritalStatusCodeDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record MaritalStatus Codes details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return MaritalStatusCode
	 */
	@Override
	public MaritalStatusCode getMaritalStatusCodeById(final String id,String type) {
		logger.debug(Literal.ENTERING);
		MaritalStatusCode maritalStatusCode = new MaritalStatusCode();
		maritalStatusCode.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("Select MaritalStsCode, MaritalStsDesc, MaritalStsIsActive,SystemDefault,");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTMaritalStatusCodes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where MaritalStsCode =:MaritalStsCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(maritalStatusCode);
		RowMapper<MaritalStatusCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MaritalStatusCode.class);

		try {
			maritalStatusCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			maritalStatusCode = null;
		}
		logger.debug(Literal.LEAVING);
		return maritalStatusCode;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTMaritalStatusCodes or
	 * BMTMaritalStatusCodes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete MaritalStatus Codes by key
	 * MaritalStsCode
	 * 
	 * @param Marital
	 *            Status Codes (maritalStatusCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(MaritalStatusCode maritalStatusCode, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTMaritalStatusCodes");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where MaritalStsCode =:MaritalStsCode");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(maritalStatusCode);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into BMTMaritalStatusCodes or
	 * BMTMaritalStatusCodes_Temp.
	 * 
	 * save MaritalStatus Codes
	 * 
	 * @param Marital
	 *            Status Codes (maritalStatusCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(MaritalStatusCode maritalStatusCode, TableType tableType) {
		logger.debug(Literal.ENTERING);
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTMaritalStatusCodes");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (MaritalStsCode, MaritalStsDesc, MaritalStsIsActive,SystemDefault,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:MaritalStsCode, :MaritalStsDesc, :MaritalStsIsActive, :SystemDefault,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.trace(Literal.SQL + insertSql.toString());		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(maritalStatusCode);
		try{
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return maritalStatusCode.getId();
	}

	/**
	 * This method updates the Record BMTMaritalStatusCodes or
	 * BMTMaritalStatusCodes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update MaritalStatus Codes by key
	 * MaritalStsCode and Version
	 * 
	 * @param Marital
	 *            Status Codes (maritalStatusCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(MaritalStatusCode maritalStatusCode, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTMaritalStatusCodes");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set  MaritalStsDesc = :MaritalStsDesc, MaritalStsIsActive = :MaritalStsIsActive, SystemDefault=:SystemDefault,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where MaritalStsCode =:MaritalStsCode");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL +  updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(maritalStatusCode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Fetch the count of system default values by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Gender
	 */
	@Override
	public String getSystemDefaultCount(String maritalStsCode) {
		logger.debug("Entering");
		MaritalStatusCode maritalStatusCode = new MaritalStatusCode();
		maritalStatusCode.setMaritalStsCode(maritalStsCode);
		maritalStatusCode.setSystemDefault(true);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT MaritalStsCode FROM  BMTMaritalStatusCodes_View ");
		selectSql.append(" Where MaritalStsCode != :MaritalStsCode and SystemDefault = :SystemDefault");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(maritalStatusCode);
		String dftMaritalStsCode = "";
		try {
			dftMaritalStsCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
        } catch (Exception e) {
        	logger.warn("Exception: ", e);
        	dftMaritalStsCode = "";
        }
		logger.debug("Leaving");
		return dftMaritalStsCode;

	}

	@Override
	public boolean isDuplicateKey(String maritalStsCode, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "MaritalStsCode =:maritalStsCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTMaritalStatusCodes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTMaritalStatusCodes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BMTMaritalStatusCodes_Temp", "BMTMaritalStatusCodes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("maritalStsCode", maritalStsCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	
}