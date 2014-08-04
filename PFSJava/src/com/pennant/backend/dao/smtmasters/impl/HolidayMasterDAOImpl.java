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

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.smtmasters.HolidayMasterDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>HolidayMaster model</b> class.<br>
 * 
 */

public class HolidayMasterDAOImpl extends BasisCodeDAO<HolidayMaster> implements
		HolidayMasterDAO {

	private static Logger logger = Logger.getLogger(HolidayMasterDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new HolidayMaster
	 * 
	 * @return HolidayMaster
	 */
	@Override
	public HolidayMaster getHolidayMaster() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil
				.getWorkFlowDetails("HolidayMaster");
		HolidayMaster holidayMaster = new HolidayMaster();
		if (workFlowDetails != null) {
			holidayMaster.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return holidayMaster;
	}

	/**
	 * This method get the module from method getHolidayMaster() and set the new
	 * record flag as true and return HolidayMaster()
	 * 
	 * @return HolidayMaster
	 */
	@Override
	public HolidayMaster getNewHolidayMaster() {
		logger.debug("Entering");
		HolidayMaster holidayMaster = getHolidayMaster();
		holidayMaster.setNewRecord(true);
		logger.debug("Leaving");
		return holidayMaster;
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
	public HolidayMaster getHolidayMasterByID(final String id,
			final BigDecimal year, final String holidayType, String type) {
		logger.debug("Entering");
		HolidayMaster holidayMaster = new HolidayMaster();
		holidayMaster.setId(id);
		holidayMaster.setHolidayYear(year);
		holidayMaster.setHolidayType(holidayType);

		StringBuilder selectListSql = new StringBuilder("Select HolidayCode, HolidayYear, HolidayType, Holidays,");
		selectListSql.append(" HolidayCodeDesc,HolidayDesc1, HolidayDesc2, HolidayDesc3, ");
		selectListSql.append(" Version , LastMntBy, LastMntOn ");
		selectListSql.append(" From SMTHolidayMaster");
		selectListSql.append(StringUtils.trimToEmpty(type));
		selectListSql.append(" Where HolidayCode =:HolidayCode AND HolidayYear =:HolidayYear AND HolidayType =:HolidayType ");

		logger.debug("selectListSql: " + selectListSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				holidayMaster);
		RowMapper<HolidayMaster> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(HolidayMaster.class);

		try {
			holidayMaster = this.namedParameterJdbcTemplate.queryForObject(
					selectListSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			holidayMaster = null;
			logger.error(e);
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
	public List<HolidayMaster> getHolidayMasterCodeYear(
			final String holidayCode, final BigDecimal year, String type) {
		logger.debug("Entering");
		HolidayMaster holidayMaster = new HolidayMaster();
		holidayMaster.setHolidayCode(holidayCode);
		holidayMaster.setHolidayYear(year);

		StringBuilder selectListSql = new StringBuilder("Select HolidayCode, HolidayYear, HolidayType,");
		selectListSql.append(" Holidays, HolidayDesc1, HolidayDesc2, HolidayDesc3, ");
		selectListSql.append(" Version , LastMntBy, LastMntOn ");
		selectListSql.append(" From SMTHolidayMaster");
		selectListSql.append(StringUtils.trimToEmpty(type));
		selectListSql.append(" Where HolidayCode =:HolidayCode AND HolidayYear in (0,:HolidayYear) ORDER BY HolidayType ");

		logger.debug("selectListSql: " + selectListSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				holidayMaster);
		RowMapper<HolidayMaster> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(HolidayMaster.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectListSql.toString(),
				beanParameters, typeRowMapper);
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
		
		Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		int holidayYear = DateUtility.getYear(curBussDate);
		
		HolidayMaster holidayMaster = new HolidayMaster();
		holidayMaster.setHolidayCode(holidayCode);
		holidayMaster.setHolidayYear(new BigDecimal(holidayYear));
		
		StringBuilder selectListSql = new StringBuilder("Select HolidayCode, HolidayYear, HolidayType,");
		selectListSql.append(" Holidays, HolidayDesc1, HolidayDesc2, HolidayDesc3, ");
		selectListSql.append(" Version , LastMntBy, LastMntOn ");
		selectListSql.append(" From SMTHolidayMaster");
		selectListSql.append(" Where HolidayCode =:HolidayCode AND (HolidayYear >= :HolidayYear-1 AND HolidayYear <= :HolidayYear+1) ORDER BY HolidayYear asc");

		logger.debug("selectListSql: " + selectListSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				holidayMaster);
		RowMapper<HolidayMaster> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(HolidayMaster.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectListSql.toString(),
				beanParameters, typeRowMapper);
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param HolidayMaster
	 *            (holidayMaster)
	 * @return HolidayMaster
	 */
	@Override
	public void initialize(HolidayMaster holidayMaster) {
		super.initialize(holidayMaster);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param HolidayMaster
	 *            (holidayMaster)
	 * @return void
	 */
	@Override
	public void refresh(HolidayMaster holidayMaster) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
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
	@SuppressWarnings("serial")
	public void delete(HolidayMaster holidayMaster, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From SMTHolidayMaster");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where HolidayCode =:HolidayCode AND HolidayYear = :HolidayYear AND HolidayType = :HolidayType");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				holidayMaster);
		
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
					beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",
						holidayMaster.getHolidayCode(),
						holidayMaster.getHolidayYear(),
						holidayMaster.getHolidayType(), holidayMaster
								.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",
					holidayMaster.getHolidayCode(),
					holidayMaster.getHolidayYear(),
					holidayMaster.getHolidayType(), holidayMaster
							.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
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
		
		StringBuilder insertSql =new StringBuilder("Insert Into SMTHolidayMaster");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (HolidayCode, HolidayCodeDesc, HolidayYear, HolidayType,");
		insertSql.append(" Holidays, HolidayDesc1,HolidayDesc2, HolidayDesc3, ");
		insertSql.append(" Version , LastMntBy, LastMntOn)");
		insertSql.append(" Values(:HolidayCode, :HolidayCodeDesc, :HolidayYear, :HolidayType,");
		insertSql.append(" :Holidays, :HolidayDesc1, :HolidayDesc2, :HolidayDesc3,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn )");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				holidayMaster);
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
	@SuppressWarnings("serial")
	@Override
	public void update(HolidayMaster holidayMaster, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder("Update SMTHolidayMaster");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set HolidayCodeDesc = :HolidayCodeDesc,Holidays = :Holidays,");
		updateSql.append( " HolidayDesc1 = :HolidayDesc1, HolidayDesc2 = :HolidayDesc2,");
		updateSql.append( " HolidayDesc3 = :HolidayDesc3, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		updateSql.append(" Where HolidayCode =:HolidayCode AND HolidayYear = :HolidayYear AND HolidayType = :HolidayType");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				holidayMaster);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),
				beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41003",
					holidayMaster.getHolidayCode(),
					holidayMaster.getHolidayYear(),
					holidayMaster.getHolidayType(), holidayMaster
							.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails getError(String errorId, String holidayCode,BigDecimal holidayYear,
			String holidayType, String userLanguage) {
		String[][] parms = new String[2][3];
		
		parms[1][0] = holidayCode;
		parms[1][1]= holidayYear.toString();
		parms[1][2] = holidayType;
		
		parms[0][0] = PennantJavaUtil.getLabel("label_HolidayMasterDialog_HolidayCode") + ":"
				+ parms[1][0] + PennantJavaUtil.getLabel("label_HolidayMasterDialog_HolidayYear") + ":"
				+ parms[1][1];
		
		parms[0][1] = PennantJavaUtil.getLabel("label_HolidayMasterDialog_HolidayType") + ":"
		+ parms[1][2];
		
		return ErrorUtil.getErrorDetail(new ErrorDetails(
				PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]),
				userLanguage);
	}

}