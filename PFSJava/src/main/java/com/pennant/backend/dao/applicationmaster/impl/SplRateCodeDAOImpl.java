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
 * * FileName : SplRateCodeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified
 * Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.Date;
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

import com.pennant.backend.dao.applicationmaster.SplRateCodeDAO;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>SplRateCode model</b> class.<br>
 * 
 */
public class SplRateCodeDAOImpl extends BasicDao<SplRateCode> implements SplRateCodeDAO {
	private static Logger logger = LogManager.getLogger(SplRateCodeDAOImpl.class);

	public SplRateCodeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Special Rate Codes details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return SplRateCode
	 */
	@Override
	public SplRateCode getSplRateCodeById(final String id, String type) {
		logger.debug("Entering");
		SplRateCode splRateCode = new SplRateCode();
		splRateCode.setId(id);

		StringBuilder selectSql = new StringBuilder("Select SRType, SRTypeDesc, SRIsActive,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTSplRateCodes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SRType =:SRType ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(splRateCode);
		RowMapper<SplRateCode> typeRowMapper = BeanPropertyRowMapper.newInstance(SplRateCode.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public List<BaseRate> getBaseRateHistByType(String bRType, String currency, Date bREffDate) {
		logger.debug("Entering");
		BaseRate baseRate = new BaseRate();
		baseRate.setBRType(bRType);
		baseRate.setCurrency(currency);
		baseRate.setBREffDate(bREffDate);

		StringBuilder selectSql = new StringBuilder("select BRTYPE, BREFFDATE, BRRATE ");
		selectSql.append(" FROM RMTBaseRates");
		selectSql.append(" Where brtype = :BRType AND Currency = :Currency AND RecordStatus = 'Approved' ");
		selectSql.append(" AND breffdate >= (select max(BREffDate) from RMTBASERATES ");
		selectSql.append(
				" Where brtype = :BRType AND Currency = :Currency AND RecordStatus = 'Approved' AND breffdate <= :BREffDate");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRate);
		RowMapper<BaseRate> typeRowMapper = BeanPropertyRowMapper.newInstance(BaseRate.class);

		List<BaseRate> baseRates = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

		logger.debug("Leaving");
		return baseRates;
	}

	/**
	 * This method Deletes the Record from the RMTSplRateCodes or RMTSplRateCodes_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Special Rate Codes by key SRType
	 * 
	 * @param Special Rate Codes (splRateCode)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(SplRateCode splRateCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder(" Delete From RMTSplRateCodes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SRType =:SRType");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(splRateCode);

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
	 * This method insert new Records into RMTSplRateCodes or RMTSplRateCodes_Temp.
	 *
	 * save Special Rate Codes
	 * 
	 * @param Special Rate Codes (splRateCode)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(SplRateCode splRateCode, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into RMTSplRateCodes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SRType, SRTypeDesc, SRIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:SRType, :SRTypeDesc, :SRIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(splRateCode);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return splRateCode.getId();
	}

	/**
	 * This method updates the Record RMTSplRateCodes or RMTSplRateCodes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Special Rate Codes by key SRType and Version
	 * 
	 * @param Special Rate Codes (splRateCode)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(SplRateCode splRateCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder updateSql = new StringBuilder("Update RMTSplRateCodes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SRTypeDesc = :SRTypeDesc, SRIsActive = :SRIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where SRType =:SRType");

		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(splRateCode);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}