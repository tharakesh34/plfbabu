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
 * FileName    		:  AgreementFieldsDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;


import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.finance.FinPlanEmiHolidayDAO;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;

/**
 * DAO methods implementation for the <b>AgreementFieldDetails model</b> class.<br>
 */
public class FinPlanEmiHolidayDAOImpl implements FinPlanEmiHolidayDAO {

	private static Logger logger = Logger.getLogger(FinPlanEmiHolidayDAOImpl.class);
	
	public FinPlanEmiHolidayDAOImpl() {
		super();
	}
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * Method for Fetching List of Planned EMI Holiday Months
	 */
	@Override
	public List<Integer> getPlanEMIHMonthsByRef(String finReference, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT PlanEMIHMonth FROM FinPlanEMIHMonths");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		List<Integer> planEMIHMonths = this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), source, Integer.class);
		
		logger.debug("Leaving");
		return planEMIHMonths;
	}

	/**
	 * Method for fetching lIst of Planned EMI Holiday Dates
	 */
	@Override
	public List<Date> getPlanEMIHDatesByRef(String finReference, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT PlanEMIHDate FROM FinPlanEMIHDates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		List<Date> planEMIHDates = this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), source, Date.class);
		
		logger.debug("Leaving");
		return planEMIHDates;
	}

	/**
	 * Method for Deletion of Plan EMI Holiday months by Key : Finance Reference
	 */
	@Override
	public void deletePlanEMIHMonths(String finReference, String type) {
		logger.debug("Entering");
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinPlanEMIHMonths");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		
		logger.debug("Leaving");
	}

	/**
	 * Method for saving List of Planned EMI Holiday Months by Frequency Method
	 */
	@Override
	public void savePlanEMIHMonths(List<FinPlanEmiHoliday> planEMIHMonths, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into FinPlanEMIHMonths");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, PlanEMIHMonth)");
		insertSql.append(" Values(:FinReference, :PlanEMIHMonth)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(planEMIHMonths.toArray());
		try {
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		} catch(Exception e) {
			logger.error("Exception", e);
			throw e;
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Method for Deletion of Plan EMI Holiday Dates by Key : Finance Reference
	 */
	@Override
	public void deletePlanEMIHDates(String finReference, String type) {
		logger.debug("Entering");
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinPlanEMIHDates");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		
		logger.debug("Leaving");
	}

	/**
	 * Method for Saving list of Planned EMI Holiday Dates by Ad-hoc Method
	 */
	@Override
	public void savePlanEMIHDates(List<FinPlanEmiHoliday> planEMIHDates, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into FinPlanEMIHDates");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, PlanEMIHDate)");
		insertSql.append(" Values(:FinReference, :PlanEMIHDate)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(planEMIHDates.toArray());
		try {
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		} catch(Exception e) {
			logger.error("Exception", e);
			throw e;
		}
		
		logger.debug("Leaving");
	}

}