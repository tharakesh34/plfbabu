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
 * FileName    		:  SplRateDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.applicationmaster.SplRateDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>SplRate model</b> class.<br>
 * 
 */
public class SplRateDAOImpl extends BasisCodeDAO<SplRate> implements SplRateDAO {
	private static Logger logger = Logger.getLogger(SplRateDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public SplRateDAOImpl() {
		super();
	}
	

	/**
	 * Fetch the Record Special Rates details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return SplRate
	 */
	@Override
	public SplRate getSplRateById(final String id,Date date, String type) {
		logger.debug("Entering");
		SplRate splRate = new SplRate();
		splRate.setId(id);
		splRate.setSREffDate(date);

		StringBuilder selectSql = new StringBuilder("SELECT SRType, SREffDate, SRRate, DelExistingRates, LastMdfDate, " );
		if(type.contains("View")){
			selectSql.append(" lovDescSRTypeName, ");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn,RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId  ");
		selectSql.append(" FROM RMTSplRates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SRType =:SRType AND SREffDate=:SREffDate");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(splRate);
		RowMapper<SplRate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SplRate.class);

		try {
			splRate = this.namedParameterJdbcTemplate.queryForObject(
					 selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			splRate = null;
		}
		logger.debug("Leaving");
		return splRate;
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
	 * This method Deletes the Record from the RMTSplRates or RMTSplRates_Temp.
	 * if Record not deleted then throws DataAccessException with error 41003.
	 * delete Special Rates by key SRType
	 * 
	 * @param Special
	 *            Rates (splRate)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(SplRate splRate, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTSplRates");
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where SRType =:SRType AND SREffDate=:SREffDate");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(splRate);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
					beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTSplRates or RMTSplRates_Temp.
	 * 
	 * save Special Rates
	 * 
	 * @param Special
	 *            Rates (splRate)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(SplRate splRate, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into RMTSplRates");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SRType, SREffDate, SRRate, DelExistingRates," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId,LastMdfDate)");
		insertSql.append(" Values(:SRType, :SREffDate, :SRRate, :DelExistingRates," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId,:LastMdfDate)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(splRate);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record RMTSplRates or RMTSplRates_Temp. if Record
	 * not updated then throws DataAccessException with error 41004. update
	 * Special Rates by key SRType and Version
	 * 
	 * @param Special
	 *            Rates (splRate)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 */
	@Override
	public void update(SplRate splRate, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder("Update RMTSplRates");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SRRate = :SRRate, DelExistingRates = :DelExistingRates,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, LastMdfDate=:LastMdfDate");
		updateSql.append(" Where SRType =:SRType AND SREffDate=:SREffDate");

		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(splRate);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * To get base rate value using base rate code and effective date is less
	 * than passed date
	 */
	public boolean getSplRateListById(String sRType, Date sREffDate, String type) {
		logger.debug("Entering");
		SplRate splRate = new SplRate();
		splRate.setSRType(sRType);
		splRate.setSREffDate(sREffDate);

		List<SplRate> splRateList = getSplRateByType(sRType, sREffDate, type);

		if (splRateList.size() > 0) {
			SplRate rate = splRateList.get(0);
			if (rate.getSREffDate().equals(splRate.getSREffDate())) {
				splRateList.remove(0);
			}
		}

		logger.debug("Leaving");
		if (splRateList.size() > 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * Common Method for getting list<SplRate> of Objects Using id and date with type of table
	 * @param id
	 * @param date
	 * @param type
	 * @return
	 */
	private List<SplRate> getSplRateByType(final String id, Date date,String type) {
		logger.debug("Entering");
		SplRate splRate = new SplRate();
		splRate.setId(id);
		splRate.setSREffDate(date);

		StringBuilder selectSql = new StringBuilder("SELECT  SRType, SREffDate, SRRate, LastMdfDate ");
		selectSql.append(" FROM RMTSplRates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SRType =:SRType AND SREffDate <=:SREffDate Order by SREffDate Desc");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(splRate);
		RowMapper<SplRate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SplRate.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	public List<SplRate> getSplRateHistByType(String sRType, Date sREffDate) {
		logger.debug("Entering");
		
		SplRate splRate = new SplRate();
		splRate.setSRType(sRType);
		splRate.setSREffDate(sREffDate);

		StringBuilder selectSql = new StringBuilder("select SRTYPE, SREFFDATE, SRRATE ");
		selectSql.append(" FROM RMTSplRates");
		selectSql.append(" Where srtype = :SRType ");
		selectSql.append(" AND sreffdate >= (select max(SREffDate) from RMTSPLRATES ");
		selectSql.append(" Where srtype = :SRType AND sreffdate <= :SREffDate)");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(splRate);
		RowMapper<SplRate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SplRate.class);
		
		List<SplRate> splRates = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper); 
		
		logger.debug("Leaving");
		return splRates;
	}

	@Override
	public List<SplRate> getSRListByMdfDate(Date effDate,String type) {
		logger.debug("Entering");
		
		SplRate splRate = new SplRate();
		StringBuilder selectSql = new StringBuilder("SELECT SRType, SREffDate, SRRate, LastMdfDate");
		selectSql.append(" FROM RMTSplRates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LastMdfDate ='");
		selectSql.append(effDate);
		selectSql.append('\'');
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(splRate);
		RowMapper<SplRate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SplRate.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * To get Special rate value using special rate code and effective date is less than
	 * or equal passed date
	 */
	public SplRate getSplRateByID(final String id, Date date) {
		logger.debug("Entering");
		SplRate splRate = null;
		
		List<SplRate>  splRates = getSplRateByType(id, date, "");
		if(splRates.size()>0){
			splRate = splRates.get(0);
		}

		logger.debug("Leaving");
		return splRate;
	}

	/**
	 * This method Deletes the Record from the RMTSplRates
	 * If Record not deleted then throws DataAccessException
	 * with error 41003. delete SplRates greater than effective date
	 * 
	 * @param SplRate
	 *            (splRate)
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteByEffDate(SplRate splRate, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTSplRates");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SRType =:SRType and  SREffDate > :SREffDate");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(splRate);

		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch record count of special rate code.
	 * 
	 * @param repaySpecialRate
	 * @param type (table type)
	 * @return Integer
	 */
	@Override
	public int getSpecialRateCountById(String repaySpecialRate, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("SRType", repaySpecialRate);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From  RMTSplRates ");
		selectSql.append(" WHERE SRType = :SRType");
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
}