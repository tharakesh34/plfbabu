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
 * FileName    		:  PhoneTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.systemmasters.PhoneTypeDAO;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>PhoneType model</b> class.<br>
 * 
 */
public class PhoneTypeDAOImpl extends BasisCodeDAO<PhoneType> implements PhoneTypeDAO {

	private static Logger logger = Logger.getLogger(PhoneTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public PhoneTypeDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Phone Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return PhoneType
	 */
	@Override
	public PhoneType getPhoneTypeById(final String id, String type) {
		logger.debug(Literal.ENTERING);
		PhoneType phoneType = new PhoneType();
		phoneType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select PhoneTypeCode, PhoneTypeDesc, PhoneTypeRegex, PhoneTypePriority, PhoneTypeIsActive,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTPhoneTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PhoneTypeCode =:PhoneTypeCode");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(phoneType);
		RowMapper<PhoneType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PhoneType.class);

		try {
			phoneType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			phoneType = null;
		}
		logger.debug(Literal.LEAVING);
		return phoneType;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTPhoneTypes or
	 * BMTPhoneTypes_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete Phone Types by key PhoneTypeCode
	 * 
	 * @param Phone
	 *            Types (phoneType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(PhoneType phoneType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append(" Delete From BMTPhoneTypes");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where PhoneTypeCode =:PhoneTypeCode");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(phoneType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
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
	 * This method insert new Records into BMTPhoneTypes or BMTPhoneTypes_Temp.
	 * 
	 * save Phone Types
	 * 
	 * @param Phone
	 *            Types (phoneType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(PhoneType phoneType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTPhoneTypes");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (PhoneTypeCode, PhoneTypeDesc, PhoneTypeRegex, PhoneTypePriority, PhoneTypeIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:PhoneTypeCode, :PhoneTypeDesc, :PhoneTypeRegex,:PhoneTypePriority, :PhoneTypeIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(phoneType);
		try{
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return phoneType.getId();
	}

	/**
	 * This method updates the Record BMTPhoneTypes or BMTPhoneTypes_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Phone Types by key PhoneTypeCode and Version
	 * 
	 * @param Phone
	 *            Types (phoneType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(PhoneType phoneType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTPhoneTypes");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set PhoneTypeDesc = :PhoneTypeDesc,");
		updateSql.append(" PhoneTypeRegex =:PhoneTypeRegex,PhoneTypePriority = :PhoneTypePriority, PhoneTypeIsActive = :PhoneTypeIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where PhoneTypeCode =:PhoneTypeCode");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL +  updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(phoneType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(String phoneTypeCode, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "PhoneTypeCode =:PhoneTypeCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTPhoneTypes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTPhoneTypes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BMTPhoneTypes_Temp", "BMTPhoneTypes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("PhoneTypeCode", phoneTypeCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
}