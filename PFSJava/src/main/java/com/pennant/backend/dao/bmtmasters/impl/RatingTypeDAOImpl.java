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
 * * FileName : RatingTypeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified
 * Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.bmtmasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.bmtmasters.RatingTypeDAO;
import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>RatingType model</b> class.<br>
 * 
 */
public class RatingTypeDAOImpl extends BasicDao<RatingType> implements RatingTypeDAO {
	private static Logger logger = LogManager.getLogger(RatingTypeDAOImpl.class);

	public RatingTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Rating Type details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return RatingType
	 */
	@Override
	public RatingType getRatingTypeById(final String id, String type) {
		logger.debug("Entering");
		RatingType ratingType = new RatingType();
		ratingType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select RatingType, RatingTypeDesc, ValueType, ValueLen, RatingIsActive,");
		selectSql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTRatingTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RatingType =:RatingType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ratingType);
		RowMapper<RatingType> typeRowMapper = BeanPropertyRowMapper.newInstance(RatingType.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the BMTRatingTypes or BMTRatingTypes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Rating Type by key RatingType
	 * 
	 * @param Rating Type (ratingType)
	 * @param type   (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(RatingType ratingType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BMTRatingTypes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where RatingType =:RatingType");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ratingType);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTRatingTypes or BMTRatingTypes_Temp.
	 * 
	 * save Rating Type
	 * 
	 * @param Rating Type (ratingType)
	 * @param type   (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(RatingType ratingType, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTRatingTypes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RatingType, RatingTypeDesc, ValueType, ValueLen, RatingIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:RatingType, :RatingTypeDesc, :ValueType, :ValueLen, :RatingIsActive,");
		insertSql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ratingType);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return ratingType.getId();
	}

	/**
	 * This method updates the Record BMTRatingTypes or BMTRatingTypes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Rating Type by key RatingType and Version
	 * 
	 * @param Rating Type (ratingType)
	 * @param type   (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(RatingType ratingType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTRatingTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set RatingTypeDesc = :RatingTypeDesc, ValueType = :ValueType,");
		updateSql.append(" ValueLen = :ValueLen, RatingIsActive = :RatingIsActive,");
		updateSql.append(
				" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(
				" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where RatingType =:RatingType");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ratingType);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}