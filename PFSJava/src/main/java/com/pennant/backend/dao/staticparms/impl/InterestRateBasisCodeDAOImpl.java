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
 * FileName    		:  InterestRateBasisCodeDAOImpl.java                                                   * 	  
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

package com.pennant.backend.dao.staticparms.impl;

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
import com.pennant.backend.dao.staticparms.InterestRateBasisCodeDAO;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>InterestRateBasisCode model</b> class.<br>
 * 
 */
public class InterestRateBasisCodeDAOImpl extends BasisCodeDAO<InterestRateBasisCode> implements InterestRateBasisCodeDAO {

	private static Logger logger = Logger.getLogger(InterestRateBasisCodeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public InterestRateBasisCodeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Interest Rate Basis Codes details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return InterestRateBasisCode
	 */
	@Override
	public InterestRateBasisCode getInterestRateBasisCodeById(final String id,String type) {
		logger.debug("Entering");
		InterestRateBasisCode interestRateBasisCode = new InterestRateBasisCode();
		interestRateBasisCode.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select IntRateBasisCode, IntRateBasisDesc, IntRateBasisIsActive,");
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTIntRateBasisCodes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where IntRateBasisCode =:IntRateBasisCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interestRateBasisCode);
		RowMapper<InterestRateBasisCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(InterestRateBasisCode.class);

		try {
			interestRateBasisCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			interestRateBasisCode = null;
		}
		logger.debug("Leaving");
		return interestRateBasisCode;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTIntRateBasisCodes or
	 * BMTIntRateBasisCodes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Interest Rate Basis Codes by
	 * key IntRateBasisCode
	 * 
	 * @param Interest
	 *            Rate Basis Codes (interestRateBasisCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(InterestRateBasisCode interestRateBasisCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BMTIntRateBasisCodes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where IntRateBasisCode =:IntRateBasisCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interestRateBasisCode);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTIntRateBasisCodes or
	 * BMTIntRateBasisCodes_Temp.
	 * 
	 * save Interest Rate Basis Codes
	 * 
	 * @param Interest
	 *            Rate Basis Codes (interestRateBasisCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(InterestRateBasisCode interestRateBasisCode, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTIntRateBasisCodes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (IntRateBasisCode, IntRateBasisDesc, IntRateBasisIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:IntRateBasisCode, :IntRateBasisDesc, :IntRateBasisIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interestRateBasisCode);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return interestRateBasisCode.getId();
	}

	/**
	 * This method updates the Record BMTIntRateBasisCodes or
	 * BMTIntRateBasisCodes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Interest Rate Basis Codes by
	 * key IntRateBasisCode and Version
	 * 
	 * @param Interest
	 *            Rate Basis Codes (interestRateBasisCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(InterestRateBasisCode interestRateBasisCode, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTIntRateBasisCodes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set IntRateBasisDesc = :IntRateBasisDesc,");
		updateSql.append(" IntRateBasisIsActive = :IntRateBasisIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where IntRateBasisCode =:IntRateBasisCode");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interestRateBasisCode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}