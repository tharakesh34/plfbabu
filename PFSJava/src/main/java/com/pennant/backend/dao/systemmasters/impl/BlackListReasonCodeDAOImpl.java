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
 * FileName    		:  BlackListReasonCodeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.BlackListReasonCodeDAO;
import com.pennant.backend.model.systemmasters.BlackListReasonCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>BlackListReasonCode model</b> class.<br>
 * 
 */
public class BlackListReasonCodeDAOImpl extends BasisCodeDAO<BlackListReasonCode> implements BlackListReasonCodeDAO {

	private static Logger logger = Logger.getLogger(BlackListReasonCodeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public BlackListReasonCodeDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Black List Reasons details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BlackListReasonCode
	 */
	@Override
	public BlackListReasonCode getBlackListReasonCodeById(final String id, String type) {
		logger.debug("Entering");
		BlackListReasonCode blackListReasonCode = new BlackListReasonCode();
		blackListReasonCode.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("Select BLRsnCode, BLRsnDesc, BLIsActive," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTBlackListRsnCodes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  BLRsnCode =:BLRsnCode") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blackListReasonCode);
		RowMapper<BlackListReasonCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BlackListReasonCode.class);

		try {
			blackListReasonCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,	typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			blackListReasonCode = null;
		}
		logger.debug("Leaving");
		return blackListReasonCode;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTBlackListRsnCodes or
	 * BMTBlackListRsnCodes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Black List Reasons by key
	 * BLRsnCode
	 * 
	 * @param Black
	 *            List Reasons (blackListReasonCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(BlackListReasonCode blackListReasonCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTBlackListRsnCodes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BLRsnCode =:BLRsnCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blackListReasonCode);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTBlackListRsnCodes or
	 * BMTBlackListRsnCodes_Temp.
	 * 
	 * save Black List Reasons
	 * 
	 * @param Black
	 *            List Reasons (blackListReasonCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(BlackListReasonCode blackListReasonCode, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTBlackListRsnCodes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BLRsnCode, BLRsnDesc, BLIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, " );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:BLRsnCode, :BLRsnDesc, :BLIsActive, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blackListReasonCode);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return blackListReasonCode.getId();
	}

	/**
	 * This method updates the Record BMTBlackListRsnCodes or
	 * BMTBlackListRsnCodes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Black List Reasons by key
	 * BLRsnCode and Version
	 * 
	 * @param Black
	 *            List Reasons (blackListReasonCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(BlackListReasonCode blackListReasonCode, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTBlackListRsnCodes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set BLRsnDesc = :BLRsnDesc, BLIsActive = :BLIsActive," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where BLRsnCode =:BLRsnCode ");
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blackListReasonCode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}