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
 * FileName    		:  WeekendMasterDAOImpl.java                                                   * 	  
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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.smtmasters.WeekendMasterDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>WeekendMaster model</b> class.<br>
 * 
 */
public class WeekendMasterDAOImpl extends BasisCodeDAO<WeekendMaster> implements
		WeekendMasterDAO {

	private static Logger logger = Logger.getLogger(WeekendMasterDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new WeekendMaster
	 * 
	 * @return WeekendMaster
	 */
	@Override
	public WeekendMaster getWeekendMaster() {
		logger.debug("Entering ");
		// WorkFlowDetails
		// workFlowDetails=WorkFlowUtil.getWorkFlowDetails("WeekendMaster");
		WeekendMaster weekendMaster = new WeekendMaster();
		/*
		 * if (workFlowDetails!=null){
		 * weekendMaster.setWorkflowId(workFlowDetails.getWorkFlowId()); }
		 */
		logger.debug("Leaving ");
		return weekendMaster;
	}

	/**
	 * This method get the module from method getWeekendMaster() and set the new
	 * record flag as true and return WeekendMaster()
	 * 
	 * @return WeekendMaster
	 */
	@Override
	public WeekendMaster getNewWeekendMaster() {
		logger.debug("Entering");
		WeekendMaster weekendMaster = getWeekendMaster();
		weekendMaster.setNewRecord(true);
		logger.debug("Leaving");
		return weekendMaster;
	}

	/**
	 * Fetch the Record Weekend Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return WeekendMaster
	 */
	@Override
	public WeekendMaster getWeekendMasterByID(final String id, String type) {
		logger.debug("Entering");
		WeekendMaster weekendMaster = new WeekendMaster();
		weekendMaster.setId(id);
		
		StringBuilder selectListSql = new StringBuilder("Select WeekendCode, WeekendDesc, Weekend, ");
		selectListSql.append(" Version , LastMntBy, LastMntOn ");
		selectListSql.append(" From SMTWeekendMaster");
		selectListSql.append(StringUtils.trimToEmpty(type));
		selectListSql.append(" Where WeekendCode = :WeekendCode ");

		logger.debug("selectListSql: " + selectListSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				weekendMaster);
		RowMapper<WeekendMaster> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(WeekendMaster.class);

		try {
			weekendMaster = this.namedParameterJdbcTemplate.queryForObject(
					selectListSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			weekendMaster = null;
		}
		logger.debug("Leaving");
		return weekendMaster;
	}

	@Override
	public WeekendMaster getWeekendMasterByCode(final String weekendCode) {
		logger.debug("Entering");
		WeekendMaster weekendMaster = new WeekendMaster();
		weekendMaster.setWeekendCode(weekendCode);

		StringBuilder selectListSql = new StringBuilder("Select WeekendCode, WeekendDesc, Weekend, ");
		selectListSql.append(" Version , LastMntBy, LastMntOn ");
		selectListSql.append(" From SMTWeekendMaster");
		selectListSql.append(" Where WeekendCode in ('GEN',:WeekendCode) ");

		logger.debug("selectListSql: " + selectListSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				weekendMaster);
		RowMapper<WeekendMaster> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(WeekendMaster.class);

		List<WeekendMaster> list = this.namedParameterJdbcTemplate.query(
				selectListSql.toString(), beanParameters, typeRowMapper);
		if (list == null || list.size() == 0) {
			weekendMaster = null;
		} else {

			WeekendMaster master = null;
			weekendMaster = null;

			for (int i = 0; i < list.size(); i++) {

				if (list.get(i).getWeekendCode().equalsIgnoreCase("GEN")) {
					master = list.get(i);
				}

				if (list.get(i).getWeekendCode().equalsIgnoreCase(weekendCode)) {
					weekendMaster = list.get(i);
				}
			}

			if (weekendMaster == null) {
				return master;
			}

		}
		logger.debug("Leaving");
		return weekendMaster;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param WeekendMaster
	 *            (weekendMaster)
	 * @return WeekendMaster
	 */
	@Override
	public void initialize(WeekendMaster weekendMaster) {
		super.initialize(weekendMaster);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param WeekendMaster
	 *            (weekendMaster)
	 * @return void
	 */
	@Override
	public void refresh(WeekendMaster weekendMaster) {

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
	 * This method Deletes the Record from the SMTWeekendMaster or
	 * SMTWeekendMaster_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Weekend Details by key
	 * WeekendCode
	 * 
	 * @param Weekend
	 *            Details (weekendMaster)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(WeekendMaster weekendMaster, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From SMTWeekendMaster");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where WeekendCode =:WeekendCode");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				weekendMaster);
		
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
					beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003",
						weekendMaster.getId(), weekendMaster.getUserDetails()
								.getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",
					weekendMaster.getId(), weekendMaster.getUserDetails()
							.getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into SMTWeekendMaster or
	 * SMTWeekendMaster_Temp.
	 * 
	 * save Weekend Details
	 * 
	 * @param Weekend
	 *            Details (weekendMaster)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(WeekendMaster weekendMaster, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into SMTWeekendMaster");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (WeekendCode, WeekendDesc, Weekend, ");
		insertSql.append(" Version , LastMntBy, LastMntOn)");
		insertSql.append(" Values(:WeekendCode, :WeekendDesc, :Weekend,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn )");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				weekendMaster);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return weekendMaster.getId();
	}

	/**
	 * This method updates the Record SMTWeekendMaster or SMTWeekendMaster_Temp.
	 * if Record not updated then throws DataAccessException with error 41004.
	 * update Weekend Details by key WeekendCode and Version
	 * 
	 * @param Weekend
	 *            Details (weekendMaster)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(WeekendMaster weekendMaster, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder("Update SMTWeekendMaster");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set WeekendCode = :WeekendCode, WeekendDesc = :WeekendDesc, Weekend = :Weekend, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		updateSql.append(" Where WeekendCode =:WeekendCode");
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				weekendMaster);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),
				beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004",
					weekendMaster.getId(), weekendMaster.getUserDetails()
							.getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails getError(String errorId, 
			String weekendCode, String userLanguage) {
		
		String[][] parms = new String[2][2];
		
		parms[1][0] = weekendCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_WeekendCode") + ":" + parms[1][0];
		
		return ErrorUtil.getErrorDetail(new ErrorDetails(
				PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]),
				userLanguage);
	}
}