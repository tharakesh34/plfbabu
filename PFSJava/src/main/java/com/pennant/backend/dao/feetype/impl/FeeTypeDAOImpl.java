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
 * FileName    		:  FeeTypeDAOImpl.java                                                  * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-01-2017    														*
 *                                                                  						*
 * Modified Date    :  03-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-01-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.feetype.impl;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>FeeType model</b> class.<br>
 * 
 */

public class FeeTypeDAOImpl extends BasisNextidDaoImpl<FeeType> implements FeeTypeDAO {

	private static Logger				logger	= Logger.getLogger(FeeTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FeeTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record FeeType details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FeeType
	 */
	@Override
	public FeeType getFeeTypeById(final long id, String type) {
		logger.debug("Entering");
		FeeType feeType = new FeeType();
		feeType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select feeTypeID,feeTypeCode,feeTypeDesc,active,");
		selectSql
				.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FeeTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FeeTypeID =:FeeTypeID");

		logger.debug("sql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		RowMapper<FeeType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FeeType.class);

		try {
			feeType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			feeType = null;
		}
		logger.debug("Leaving");
		return feeType;
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
	 * This method Deletes the Record from the FeeTypes or FeeTypes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete FeeType by key FeeTypeID
	 * 
	 * @param FeeType
	 *            (feeType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FeeType feeType, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder sql = new StringBuilder();
		sql.append("Delete From FeeTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FeeTypeID =:FeeTypeID");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", feeType.getFeeTypeID(), feeType.getUserDetails()
						.getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006", feeType.getFeeTypeID(), feeType.getUserDetails()
					.getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FeeTypes or FeeTypes_Temp. it fetches the available Sequence form SeqFeeTypes
	 * by using getNextidviewDAO().getNextId() method.
	 *
	 * save FeeType
	 * 
	 * @param FeeType
	 *            (feeType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(FeeType feeType, String type) {
		logger.debug("Entering");
		if (feeType.getId() == Long.MIN_VALUE) {
			feeType.setId(getNextidviewDAO().getNextId("SeqFeeTypes"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into FeeTypes");
		sql.append(StringUtils.trimToEmpty(type) );
		sql.append(" (feeTypeID,feeTypeCode,feeTypeDesc,active,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(" :feeTypeID,:feeTypeCode,:feeTypeDesc,:active,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return feeType.getId();
	}

	/**
	 * This method updates the Record FeeTypes or FeeTypes_Temp. if Record not updated then throws DataAccessException
	 * with error 41004. update FeeType by key FeeTypeID and Version
	 * 
	 * @param FeeType
	 *            (feeType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	@Override
	public void update(FeeType feeType, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder sql = new StringBuilder();
		sql.append("Update FeeTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set feeTypeID=:feeTypeID,feeTypeCode=:feeTypeCode,feeTypeDesc=:feeTypeDesc,");
		sql.append(" active=:active,");
		sql.append(" Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where FeeTypeID =:FeeTypeID");

		if (!type.endsWith("_Temp")) {
			sql.append("  AND Version= :Version-1");
		}

		logger.debug("Sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeType);
		recordCount = this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", feeType.getFeeTypeID(), feeType.getUserDetails()
					.getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails getError(String errorId, long feeTypeID, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = String.valueOf(feeTypeID);
		parms[0][0] = PennantJavaUtil.getLabel("label_FeeTypeID") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]),
				userLanguage);
	}
}