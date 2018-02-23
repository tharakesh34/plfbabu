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
 * FileName    		:  ErrorDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2016    														*
 *                                                                  						*
 * Modified Date    :  05-05-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.errordetail.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.errordetail.ErrorDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>ErrorDetail model</b> class.<br>
 * 
 */

public class ErrorDetailDAOImpl extends BasisCodeDAO<ErrorDetail> implements ErrorDetailDAO {
	private static Logger log = LogManager.getLogger(ErrorDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ErrorDetailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Error Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ErrorDetail
	 */
	@Override
	public ErrorDetail getErrorDetailById(final String id, String type) {
		log.debug(Literal.ENTERING);
		ErrorDetail errorDetail = new ErrorDetail();
		errorDetail.setId(id);

		StringBuilder sql = new StringBuilder();

		sql.append("Select Code, Language, Severity, Message, ExtendedMessage");
		sql.append(", Version ,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		sql.append(" From ErrorDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Code =:Code");

		log.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(errorDetail);
		RowMapper<ErrorDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ErrorDetail.class);

		try {
			errorDetail = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			log.warn(Literal.EXCEPTION, e);
			errorDetail = null;
		}
		log.debug(Literal.LEAVING);
		return errorDetail;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the ErrorDetails or
	 * ErrorDetails_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete Error Detail by key ErrorCode
	 * 
	 * @param Error
	 *            Detail (errorDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ErrorDetail errorDetail, String type) {
		log.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Delete From ErrorDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Code =:Code");
		log.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(errorDetail);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		log.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into ErrorDetails or ErrorDetails_Temp.
	 *
	 * save Error Detail
	 * 
	 * @param Error
	 *            Detail (errorDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(ErrorDetail errorDetail, String type) {
		log.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into ErrorDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Code, Language, Severity, Message, ExtendedMessage");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:Code, :Language, :Severity, :Message, :ExtendedMessage");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		log.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(errorDetail);
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		log.debug(Literal.LEAVING);
		return errorDetail.getId();
	}

	/**
	 * This method updates the Record ErrorDetails or ErrorDetails_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Error Detail by key ErrorCode and Version
	 * 
	 * @param Error
	 *            Detail (errorDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(ErrorDetail errorDetail, String type) {
		log.debug(Literal.ENTERING);
		int recordCount = 0;
		StringBuilder sql = new StringBuilder("Update ErrorDetails");
		sql.append(StringUtils.trimToEmpty(type));

		sql.append(" Set Language=:Language, Severity=:Severity, Message =:Message, ExtendedMessage =:ExtendedMessage");
		sql.append(", Version=:Version, LastMntBy=:LastMntBy, LastMntOn=:LastMntOn, RecordStatus=:RecordStatus");
		sql.append(", RoleCode = :RoleCode, NextRoleCode=:NextRoleCode, TaskId=:TaskId, NextTaskId=:NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where Code=:Code");

		if (!type.endsWith("_Temp")) {
			sql.append("  AND Version=:Version-1");
		}

		log.debug("updateSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(errorDetail);
		recordCount = this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		log.debug(Literal.LEAVING);
	}

	@Override
	public ErrorDetail getErrorDetail(String code) {
		log.debug(String.format("Error code: %s", code));

		StringBuilder sql = new StringBuilder();
		sql.append("select Code, Language, Severity, Message");
		sql.append(", ExtendedMessage from ErrorDetails where Code =:Code");

		log.trace(Literal.SQL + sql);
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("Code", code);

		RowMapper<ErrorDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ErrorDetail.class);
		List<ErrorDetail> errorList = namedParameterJdbcTemplate.query(sql.toString(), namedParameters, typeRowMapper);
		if (errorList == null || errorList.isEmpty()) {
			return null;
		}

		return errorList.get(0);
	}

}