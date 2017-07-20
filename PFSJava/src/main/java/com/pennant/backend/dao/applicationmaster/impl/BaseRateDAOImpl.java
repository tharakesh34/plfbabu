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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

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
		logger.debug(Literal.ENTERING);
		
		BaseRate baseRate = new BaseRate();
		baseRate.setBRType(bRType);
		baseRate.setCurrency(currency);
		baseRate.setBREffDate(bREffDate);

		StringBuilder selectSql = new StringBuilder("SELECT BRType, Currency, BREffDate, BRRate, DelExistingRates, BRTypeIsActive,");
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
		
		logger.debug(Literal.LEAVING);
		return baseRate;
	}
	
	@Override
	public boolean isDuplicateKey(String bRType, Date bREffDate, String currency, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "BRType =:bRType and BREffDate=:bREffDate and Currency=:currency";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTBaseRates", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTBaseRates_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTBaseRates_Temp", "RMTBaseRates" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("bRType", bRType);
		paramSource.addValue("bREffDate", bREffDate);
		paramSource.addValue("currency", currency);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public String save(BaseRate baseRate, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into RMTBaseRates");
		sql.append(tableType.getSuffix());
		sql.append(" (BRType, Currency, BREffDate, BRRate, DelExistingRates, BRTypeIsActive,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		sql.append(" NextTaskId, RecordType, WorkflowId, LastMdfDate )");
		sql.append(" Values(:BRType, :Currency, :BREffDate, :BRRate, :DelExistingRates, :BRTypeIsActive,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId, :LastMdfDate)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(baseRate);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return baseRate.getId();
	}

	@Override
	public void update(BaseRate baseRate, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update RMTBaseRates");
		sql.append(tableType.getSuffix());
		sql.append(" set BRRate = :BRRate,  DelExistingRates = :DelExistingRates, BRTypeIsActive = :BRTypeIsActive,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, LastMdfDate=:LastMdfDate ");
		sql.append(" where BRType =:BRType AND BREffDate = :BREffDate AND Currency = :Currency");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(baseRate);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public void delete(BaseRate baseRate, TableType tableType ) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" delete from RMTBaseRates");
		sql.append(tableType.getSuffix());
		sql.append(" where BRType =:BRType and BREffDate = :BREffDate and Currency = :Currency");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
	    logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(baseRate);
		int recordCount = 0;
		
		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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
		
		List<BaseRate> baseRates = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		
		logger.debug("Leaving");
		return baseRates;
	}

	public List<BaseRate> getBaseRateHistByType(String bRType, String currency, Date bREffDate) {
		logger.debug("Entering");
		BaseRate baseRate = new BaseRate();
		baseRate.setBRType(bRType);
		baseRate.setCurrency(currency);
		baseRate.setBREffDate(bREffDate);

		StringBuilder selectSql = new StringBuilder("select BRTYPE, BREFFDATE, BRRATE ");
		selectSql.append(" FROM RMTBaseRates");
		selectSql.append(" Where brtype = :BRType AND Currency = :Currency ");
		selectSql.append(" AND breffdate >= (select max(BREffDate) from RMTBASERATES ");
		selectSql.append(" Where brtype = :BRType AND Currency = :Currency AND breffdate <= :BREffDate)");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRate);
		RowMapper<BaseRate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BaseRate.class);
		
		List<BaseRate> baseRates = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper); 
		
		logger.debug("Leaving");
		return baseRates;
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
		
		List<BaseRate> baseRates = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper); 
		logger.debug("Leaving");
		return baseRates;
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
			throw new DependencyFoundException(e);
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
}