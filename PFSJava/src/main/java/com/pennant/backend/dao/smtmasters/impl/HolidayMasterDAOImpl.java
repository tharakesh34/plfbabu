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
 * FileName    		:  HolidayMasterDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-07-2011    														*
 *                                                                  						*
 * Modified Date    :  11-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-07-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.smtmasters.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

import com.pennant.app.util.BusinessCalendar;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.smtmasters.HolidayMasterDAO;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>HolidayMaster model</b> class.<br>
 * 
 */
public class HolidayMasterDAOImpl extends BasisCodeDAO<HolidayMaster> implements HolidayMasterDAO {
	private static Logger logger = Logger.getLogger(HolidayMasterDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public HolidayMasterDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Holiday Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return HolidayMaster
	 * 
	 * @throws EmptyResultDataAccessException
	 */
	@Override
	public HolidayMaster getHolidayMasterByID(final String id, final BigDecimal year, String type) {
		logger.debug("Entering");
		HolidayMaster holidayMaster = new HolidayMaster();
		holidayMaster.setId(id);
		holidayMaster.setHolidayYear(year);

		StringBuilder selectListSql = new StringBuilder(
				"Select HolidayCode, HolidayYear,HolidayCategory, HolidayType, Holidays,");
		selectListSql.append(" HolidayCodeDesc,HolidayDesc1, HolidayDesc2, HolidayDesc3, ");
		selectListSql
				.append(" Version , LastMntBy, LastMntOn,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		selectListSql.append(" From SMTHolidayMaster");
		selectListSql.append(StringUtils.trimToEmpty(type));
		selectListSql.append(" Where HolidayCode =:HolidayCode AND HolidayYear =:HolidayYear ");

		logger.debug("selectListSql: " + selectListSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(holidayMaster);
		RowMapper<HolidayMaster> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(HolidayMaster.class);

		try {
			holidayMaster = this.namedParameterJdbcTemplate.queryForObject(selectListSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			holidayMaster = null;
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return holidayMaster;
	}

	/**
	 * This method to Fetch the records by Code and Year
	 * 
	 * @param holidayCode
	 *            (String)
	 * 
	 * @param year
	 *            (BigDecimal)
	 * 
	 * @return List
	 * 
	 */
	@Override
	public List<HolidayMaster> getHolidayMasterByCategory(String holidayCategory, final BigDecimal year, String type) {
		logger.debug("Entering");
		HolidayMaster holidayMaster = new HolidayMaster();
		holidayMaster.setHolidayYear(year);
		holidayMaster.setHolidayCategory(holidayCategory);

		StringBuilder selectListSql = new StringBuilder(
				"Select HolidayCode, HolidayCategory, HolidayYear, HolidayType,");
		selectListSql.append(" Holidays, HolidayDesc1, HolidayDesc2, HolidayDesc3, ");
		selectListSql
				.append(" Version , LastMntBy, LastMntOn,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		selectListSql.append(" From SMTHolidayMaster");
		selectListSql.append(StringUtils.trimToEmpty(type));
		selectListSql
				.append(" Where HolidayYear in (0,:HolidayYear) AND HolidayCategory = :HolidayCategory ORDER BY HolidayType ");

		logger.debug("selectListSql: " + selectListSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(holidayMaster);
		RowMapper<HolidayMaster> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(HolidayMaster.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectListSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method to Fetch the records by Code and Year
	 * 
	 * @param holidayCode
	 *            (String)
	 * 
	 * @param year
	 *            (BigDecimal)
	 * 
	 * @return List
	 * 
	 */
	@Override
	public List<HolidayMaster> getHolidayMasterCodeYear(final String holidayCode, final BigDecimal year, String type) {
		logger.debug("Entering");
		HolidayMaster holidayMaster = new HolidayMaster();
		holidayMaster.setHolidayCode(holidayCode);
		holidayMaster.setHolidayYear(year);

		StringBuilder selectListSql = new StringBuilder(
				"Select HolidayCode, HolidayCategory, HolidayYear, HolidayType,");
		selectListSql.append(" Holidays, HolidayDesc1, HolidayDesc2, HolidayDesc3, ");
		selectListSql
				.append(" Version , LastMntBy, LastMntOn,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		selectListSql.append(" From SMTHolidayMaster");
		selectListSql.append(StringUtils.trimToEmpty(type));
		selectListSql
				.append(" Where HolidayCode =:HolidayCode AND HolidayYear in (0,:HolidayYear) ORDER BY HolidayType ");

		logger.debug("selectListSql: " + selectListSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(holidayMaster);
		RowMapper<HolidayMaster> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(HolidayMaster.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectListSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method to fetch the Holidays List by code
	 * 
	 * @param holidayCode
	 *            (String)
	 * 
	 * @return List
	 * 
	 */
	// Fetch the records by Code and Year
	@Override
	public List<HolidayMaster> getHolidayMasterCode(final String holidayCode) {
		logger.debug("Entering");

		Date curBussDate = DateUtility.getAppDate();
		int holidayYear = DateUtility.getYear(curBussDate);

		HolidayMaster holidayMaster = new HolidayMaster();
		holidayMaster.setHolidayCode(holidayCode);
		holidayMaster.setHolidayYear(new BigDecimal(holidayYear));

		StringBuilder selectListSql = new StringBuilder("Select HolidayCode,HolidayCategory, HolidayYear, HolidayType,");
		selectListSql.append(" Holidays, HolidayDesc1, HolidayDesc2, HolidayDesc3, ");
		selectListSql
				.append(" Version , LastMntBy, LastMntOn,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		selectListSql.append(" From SMTHolidayMaster");
		selectListSql
				.append(" Where HolidayCode =:HolidayCode AND (HolidayYear >= :HolidayYear-1 AND HolidayYear <= :HolidayYear+1) ORDER BY HolidayYear asc");

		logger.debug("selectListSql: " + selectListSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(holidayMaster);
		RowMapper<HolidayMaster> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(HolidayMaster.class);
		
		List<HolidayMaster> holidayMasters = this.namedParameterJdbcTemplate.query(selectListSql.toString(), beanParameters, typeRowMapper); 
		
		logger.debug("Leaving");
		return holidayMasters;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the SMTHolidayMaster or
	 * SMTHolidayMaster_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Holiday Details by key
	 * HolidayCode
	 * 
	 * @param Holiday
	 *            Details (holidayMaster)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(HolidayMaster holidayMaster, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From SMTHolidayMaster");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where HolidayCode =:HolidayCode AND HolidayYear = :HolidayYear");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(holidayMaster);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// added code to clear the holidayCache
		BusinessCalendar.clearHolidayCache(holidayMaster.getHolidayCode(), type);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into SMTHolidayMaster or
	 * SMTHolidayMaster_Temp.
	 * 
	 * save Holiday Details
	 * 
	 * @param Holiday
	 *            Details (holidayMaster)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return String
	 * 
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(HolidayMaster holidayMaster, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into SMTHolidayMaster");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (HolidayCode, HolidayCodeDesc, HolidayYear, HolidayType,");
		insertSql.append(" Holidays, HolidayDesc1,HolidayDesc2, HolidayDesc3, HolidayCategory, ");
		insertSql
				.append(" Version , LastMntBy, LastMntOn,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,RecordType, WorkflowId)");
		insertSql.append(" Values(:HolidayCode, :HolidayCodeDesc, :HolidayYear, :HolidayType,");
		insertSql.append(" :Holidays, :HolidayDesc1, :HolidayDesc2, :HolidayDesc3, :HolidayCategory, ");
		insertSql
				.append(" :Version , :LastMntBy, :LastMntOn,:RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,:RecordType, :WorkflowId )");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(holidayMaster);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return holidayMaster.getId();
	}

	/**
	 * This method updates the Record SMTHolidayMaster or SMTHolidayMaster_Temp.
	 * if Record not updated then throws DataAccessException with error 41004.
	 * update Holiday Details by key HolidayCode and Version
	 * 
	 * @param Holiday
	 *            Details (holidayMaster)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(HolidayMaster holidayMaster, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update SMTHolidayMaster");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set HolidayCodeDesc = :HolidayCodeDesc,Holidays = :Holidays,");
		updateSql.append(" HolidayDesc1 = :HolidayDesc1, HolidayDesc2 = :HolidayDesc2,");
		updateSql.append(" HolidayDesc3 = :HolidayDesc3, HolidayCategory = :HolidayCategory, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql
				.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");

		updateSql.append(" Where HolidayCode =:HolidayCode AND HolidayYear = :HolidayYear ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(holidayMaster);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		// added code to clear the holidayCache
		BusinessCalendar.clearHolidayCache(holidayMaster.getHolidayCode(), type);
		logger.debug("Leaving");
	}

}