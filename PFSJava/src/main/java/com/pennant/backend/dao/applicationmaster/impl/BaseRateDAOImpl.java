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
 * FileName    		:  BaseRateDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>BaseRate model</b> class.<br>
 * 
 */
public class BaseRateDAOImpl extends BasisCodeDAO<BaseRate> implements BaseRateDAO {
	private static Logger	 logger	= Logger.getLogger(BaseRateDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public BaseRateDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record BaseRates details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BaseRate
	 */
	@Override
	public BaseRate getBaseRateById(final String bRType, String currency, Date bREffDate, String type) {
		logger.debug("Entering");
		BaseRate baseRate = new BaseRate();
		baseRate.setBRType(bRType);
		baseRate.setCurrency(currency);
		baseRate.setBREffDate(bREffDate);

		StringBuilder selectSql = new StringBuilder("SELECT BRType, Currency, BREffDate, BRRate, DelExistingRates,");
		if (type.contains("View")) {
			selectSql.append(" LovDescBRTypeName, ");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,LastMdfDate ");
		selectSql.append(" FROM RMTBaseRates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BRType =:BRType and BREffDate=:BREffDate and Currency=:Currency");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRate);
		RowMapper<BaseRate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BaseRate.class);
		try {
			baseRate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			baseRate = null;
		}
		logger.debug("Leaving");
		return baseRate;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTBaseRates or
	 * RMTBaseRates_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete BaseRates by key BRType
	 * 
	 * @param BaseRates
	 *            (baseRate)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(BaseRate baseRate, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder(" Delete From RMTBaseRates");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BRType =:BRType and BREffDate = :BREffDate and Currency = :Currency");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRate);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", baseRate.getBRType(),
						baseRate.getBREffDate(), baseRate.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006", baseRate.getBRType(),
					baseRate.getBREffDate(), baseRate.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTBaseRates or RMTBaseRates_Temp.
	 * 
	 * save BaseRates
	 * 
	 * @param BaseRates
	 *            (baseRate)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(BaseRate baseRate, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into RMTBaseRates");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BRType, Currency, BREffDate, BRRate, DelExistingRates,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId, LastMdfDate )");
		insertSql.append(" Values(:BRType, :Currency, :BREffDate, :BRRate, :DelExistingRates,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId, :LastMdfDate)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRate);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record RMTBaseRates or RMTBaseRates_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update BaseRates by key BRType and Version
	 * 
	 * @param BaseRates
	 *            (baseRate)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(BaseRate baseRate, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update RMTBaseRates");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set BRRate = :BRRate,  DelExistingRates = :DelExistingRates,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, LastMdfDate=:LastMdfDate ");
		updateSql.append(" Where BRType =:BRType AND BREffDate = :BREffDate AND Currency = :Currency");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRate);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", baseRate.getBRType(), 
					baseRate.getBREffDate(), baseRate.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving Update Method");
	}

	/**
	 * Common method for BaseRates to get the baseRate and
	 * get the List of objects less than passed Effective BaseRate Date
	 * 
	 * @param bRType
	 * @param bREffDate
	 * @param type
	 * @return
	 */
	private List<BaseRate> getBaseRateListByType(String bRType, String currency, Date bREffDate, String type) {
		logger.debug("Entering");
		BaseRate baseRate = new BaseRate();
		baseRate.setBRType(bRType);
		baseRate.setCurrency(currency);
		baseRate.setBREffDate(bREffDate);

		StringBuilder selectSql = new StringBuilder("SELECT BRType,Currency,BREffDate,BRRate,LastMdfDate ");
		selectSql.append(" FROM RMTBaseRates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BRType =:BRType and Currency =:Currency and BREffDate <=:BREffDate  Order by BREffDate Desc");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRate);
		RowMapper<BaseRate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BaseRate.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * To get base rate value using base rate code and effective date is less
	 * than passed date
	 */
	public BaseRate getBaseRateByType(final String bRType, String currency,  Date bREffDate) {
		logger.debug("Entering");
		BaseRate baseRate = null;

		List<BaseRate> baseRates = getBaseRateListByType(bRType, currency, bREffDate, "");
		if (baseRates.size() > 0) {
			baseRate = baseRates.get(0);
		}

		logger.debug("Leaving");
		return baseRate;
	}

	/**
	 * To get base rate value using base rate code and effective date is less
	 * than passed date
	 */
	public boolean getBaseRateListById(String bRType, String currency, Date bREffDate, String type) {
		logger.debug("Entering");
		BaseRate baseRate = new BaseRate();
		baseRate.setBRType(bRType);
		baseRate.setCurrency(currency);
		baseRate.setBREffDate(bREffDate);

		List<BaseRate> baseRateList = getBaseRateListByType(bRType, currency, bREffDate, type);

		if (baseRateList.size() > 0) {
			BaseRate rate = baseRateList.get(0);
			if (rate.getBREffDate().equals(baseRate.getBREffDate())) {
				baseRateList.remove(0);
			}
		}

		logger.debug("Leaving");

		if (baseRateList.size() > 0) {
			return false;
		}
		return true;
	}

	@Override
	public List<BaseRate> getBSRListByMdfDate(Date effDate, String type) {
		logger.debug("Entering");
		
		BaseRate baseRate =new BaseRate();
		StringBuilder selectSql = new StringBuilder("SELECT BRType,Currency,BREffDate,BRRate,LastMdfDate ");
		selectSql.append(" FROM RMTBaseRates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LastMdfDate='");
		selectSql.append(effDate);
		selectSql.append('\'');
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRate);
		RowMapper<BaseRate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BaseRate.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	/**
	 * This method Deletes the Record from the RMTBaseRates
	 * If Record not deleted then throws DataAccessException
	 * with error 41003. delete BaseRates greater than effective date
	 * 
	 * @param BaseRates
	 *            (baseRate)
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void deleteByEffDate(BaseRate baseRate, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTBaseRates");
		deleteSql.append(StringUtils.trimToEmpty(type)); 
		deleteSql.append(" Where BRType =:BRType and Currency = :Currency and BREffDate > :BREffDate");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRate);

		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006", baseRate.getBRType(),
					baseRate.getBREffDate(), baseRate.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for count number of records with specified base code and currency
	 * 
	 * @param bRType
	 * @param currency
	 * @param type
	 * 
	 * @return Integer
	 */
	@Override
	public int getBaseRateCountById(String bRType, String currency, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BRType", bRType);
		source.addValue("Currency", currency);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From  RMTBaseRates ");
		selectSql.append(" WHERE BRType = :BRType AND Currency = :Currency");
		logger.debug("selectSql: " + selectSql.toString());

		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn("Warning", dae);
			recordCount = 0;
		}

		logger.debug("Leaving");
		return recordCount;
	}

	private ErrorDetails getError(String errorId, String baseRateType, Date effDate, String userLanguage) {
		String[][] parms = new String[2][2];

		parms[1][0] = baseRateType;
		parms[1][1] = DateUtility.formatToShortDate(effDate);

		parms[0][0] = PennantJavaUtil.getLabel("label_BRType") + ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_BREffDate") + ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0], parms[1]), userLanguage);
	}
}