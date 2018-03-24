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
 * FileName    		:  CurrencyDAOImpl.java                                                   * 	  
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>Currency model</b> class.<br>
 * 
 */
public class CurrencyDAOImpl extends BasicDao<Currency> implements CurrencyDAO {
	private static Logger logger = Logger.getLogger(CurrencyDAOImpl.class);
	
	public CurrencyDAOImpl() {
		super();
	}


	/**
	 * Fetch the Record  Currency details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Currency
	 */
	@Override
	public Currency getCurrencyById(final String id, String type) {
		logger.debug("Entering ");
		Currency currency = new Currency();
		currency.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT CcyCode, CcyNumber, CcyDesc, CcySwiftCode," );
		selectSql.append(" CcyEditField, CcyMinorCcyUnits, CcyDrRateBasisCode," );
		selectSql.append(" CcyCrRateBasisCode, CcyIsIntRounding, CcySpotRate, CcyIsReceprocal," );
		selectSql.append(" CcyUserRateBuy, CcyUserRateSell, CcyIsMember, CcyIsGroup," );
		selectSql.append(" CcyIsAlwForLoans, CcyIsAlwForDepo, CcyIsAlwForAc, CcyIsActive,");
		selectSql.append(" CcyMinorCcyDesc, CcySymbol," );
		if(type.contains("View")){
			selectSql.append(" lovDescCcyDrRateBasisCodeName, lovDescCcyCrRateBasisCodeName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  RMTCurrencies");
		selectSql.append(StringUtils.trimToEmpty(type) );
		selectSql.append("  Where CcyCode =:CcyCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		RowMapper<Currency> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(Currency.class);

		try{
			currency = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			currency = null;
		}
		logger.debug("Leaving ");
		return currency;
	}
	
	/**
	 * Fetch the Record  Currency details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Currency
	 */
	@Override
	public Currency getCurrencyByCode(final String id) {
		logger.debug("Entering ");
		Currency currency = new Currency();
		currency.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT CcyCode, CcyNumber, CcyEditField" );
		selectSql.append(" FROM  RMTCurrencies");
		selectSql.append("  Where CcyCode =:CcyCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		RowMapper<Currency> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Currency.class);

		try{
			currency = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			currency = null;
		}
		logger.debug("Leaving ");
		return currency;
	}
	
	
	/**
	 * Fetch the Record  Currency details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ValueLabel
	 */
	@Override
	public String getCurrencyById(final String id) {
		logger.debug("Entering ");
		String ccyCode = null;
		Currency currency = new Currency();
		currency.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT CcyNumber FROM  RMTCurrencies");
		selectSql.append(" Where CcyCode =:CcyCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);

		try{
			ccyCode = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);	
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			ccyCode = "";
		}
		logger.debug("Leaving ");
		return ccyCode;
	}

	@Override
	public boolean isDuplicateKey(String ccyCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "CcyCode = :ccyCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTCurrencies", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTCurrencies_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTCurrencies_Temp", "RMTCurrencies" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("ccyCode", ccyCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public String save(Currency currency, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into RMTCurrencies" );
		sql.append(tableType.getSuffix());
		sql.append(" (CcyCode, CcyNumber, CcyDesc, CcySwiftCode, CcyEditField," );
		sql.append(" CcyMinorCcyUnits, CcyDrRateBasisCode, CcyCrRateBasisCode," );
		sql.append(" CcyIsIntRounding, CcySpotRate, CcyIsReceprocal, CcyUserRateBuy," );
		sql.append(" CcyUserRateSell, CcyIsMember, CcyIsGroup, CcyIsAlwForLoans, CcyIsAlwForDepo," );
		sql.append(" CcyIsAlwForAc, CcyIsActive, CcyMinorCcyDesc, CcySymbol," );
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(:CcyCode, :CcyNumber, :CcyDesc, :CcySwiftCode, :CcyEditField," );
		sql.append(" :CcyMinorCcyUnits, :CcyDrRateBasisCode, :CcyCrRateBasisCode," );
		sql.append(" :CcyIsIntRounding, :CcySpotRate, :CcyIsReceprocal, :CcyUserRateBuy," );
		sql.append(" :CcyUserRateSell, :CcyIsMember, :CcyIsGroup, :CcyIsAlwForLoans," );
		sql.append(" :CcyIsAlwForDepo, :CcyIsAlwForAc, :CcyIsActive, :CcyMinorCcyDesc, :CcySymbol,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(currency);
		
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return currency.getId();
	}
	
	@Override
	public void update(Currency currency, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update RMTCurrencies");
		sql.append(tableType.getSuffix());
		sql.append(" set CcyNumber = :CcyNumber, CcyDesc = :CcyDesc,");
		sql.append(" CcySwiftCode = :CcySwiftCode, CcyEditField = :CcyEditField,");
		sql.append(" CcyMinorCcyUnits =:CcyMinorCcyUnits,CcyDrRateBasisCode = :CcyDrRateBasisCode,");
		sql.append(" CcyCrRateBasisCode = :CcyCrRateBasisCode,CcyIsIntRounding = :CcyIsIntRounding,");
		sql.append(" CcySpotRate = :CcySpotRate, CcyIsReceprocal = :CcyIsReceprocal,");
		sql.append(" CcyUserRateBuy = :CcyUserRateBuy, CcyUserRateSell = :CcyUserRateSell,");
		sql.append(" CcyIsMember = :CcyIsMember, CcyIsGroup = :CcyIsGroup,");
		sql.append(" CcyIsAlwForLoans = :CcyIsAlwForLoans, CcyIsAlwForDepo = :CcyIsAlwForDepo,");
		sql.append(" CcyIsAlwForAc = :CcyIsAlwForAc, CcyIsActive = :CcyIsActive,");
		sql.append(" CcyMinorCcyDesc = :CcyMinorCcyDesc, CcySymbol = :CcySymbol ,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where CcyCode =:CcyCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		int recordCount = jdbcTemplate.update(sql.toString(), beanParameters);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public void delete(Currency currency, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" delete from RMTCurrencies");
		sql.append(tableType.getSuffix());
		sql.append(" where CcyCode =:CcyCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
	    logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(currency);
		int recordCount = 0;
		
		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
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
	 * Method for Checking Currency having Unique CcyCode,CcyNumber and SwiftCode or Not
	 */
	@Override
	public boolean getUniqueCurrencyByID(Currency currency,boolean ccyNum, boolean swiftCode) {
		logger.debug("Entering ");
		
		String whereCond = "";
		if(ccyNum){
			whereCond = " Where  CcyNumber =:CcyNumber";
		}else if(swiftCode){
			whereCond = " Where CcySwiftCode =:CcySwiftCode"; 
		}

		StringBuilder selectSql = new StringBuilder("SELECT CcyCode, CcyNumber, CcyDesc, CcySwiftCode ");
		selectSql.append(" FROM  RMTCurrencies_View ");
		selectSql.append(whereCond);

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		RowMapper<Currency> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(Currency.class);

		List<Currency> list = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		if(list !=null && list.size()>0){
			logger.debug("Leaving ");
			return true;
		}
		logger.debug("Leaving ");
		return false;
	}
	
	/**
	 * Fetch the Record  Currency details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ValueLabel
	 */
	@Override
	public List<Currency> getCurrencyList() {
		logger.debug("Entering ");

		StringBuilder selectSql = new StringBuilder("SELECT CcyCode, CcyDesc, CcyNumber, CcyEditField,");
		selectSql.append(" CcySpotRate, CcyIsReceprocal, CcyUserRateSell, CcyUserRateBuy, CcyMinorCcyUnits,");
		selectSql.append(" CcyMinorCcyDesc, CcySymbol");
		selectSql.append(" FROM  RMTCurrencies");
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<Currency> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Currency.class);

		List<Currency> currencies = this.jdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper); 
		logger.debug("Leaving");
		return currencies;
	}
	
	@Override
	public Currency getCurrency(String ccy) {
	//	logger.debug("Entering ");
		
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("CcyCode", ccy);
		StringBuilder selectSql = new StringBuilder("SELECT CcyCode, CcyDesc, CcyNumber, CcyEditField,");
		selectSql.append(" CcySpotRate, CcyIsReceprocal, CcyUserRateSell, CcyUserRateBuy, CcyMinorCcyUnits,");
		selectSql.append(" CcyMinorCcyDesc, CcySymbol");
		selectSql.append(" FROM  RMTCurrencies where CcyCode = :CcyCode");
	//	logger.debug("selectSql: " + selectSql.toString()); 
		RowMapper<Currency> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Currency.class);
		
		Currency currencies = this.jdbcTemplate.queryForObject(selectSql.toString(),source, typeRowMapper); 
		//logger.debug("Leaving");
		return currencies;
	}

	@Override
    public List<Currency> getCurrencyList(List<String> asList) {
		logger.debug("Entering ");
		
		Map<String, Object> namedParameters=new HashMap<String, Object>();
		namedParameters.put("CCYList", asList);

		StringBuilder selectSql = new StringBuilder("SELECT CcyCode, CcyNumber, " );
		selectSql.append(" CcyEditField, CcyMinorCcyUnits, CcySpotRate, CcyIsReceprocal " );
		selectSql.append(" FROM  RMTCurrencies");
		selectSql.append("  Where CcyCode IN(:CCYList)");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<Currency> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Currency.class);

		logger.debug("Leaving ");
		return this.jdbcTemplate.query(selectSql.toString(), namedParameters, typeRowMapper);	
    }


	@Override
	public boolean isExistsCurrencyCode(String ccyCode) {
	logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CcyCode", ccyCode);
		
		StringBuilder selectSql = new StringBuilder("SELECT CcyCode FROM RMTCurrencies");
		selectSql.append(" WHERE CcyCode=:CcyCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		
		try {
			RowMapper<Currency> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Currency.class);
			Currency currencies = jdbcTemplate.queryForObject(selectSql.toString(),source, typeRowMapper); 
			if(currencies != null) {
				return true;
			}
		} catch(EmptyResultDataAccessException ex) {
			logger.warn("Exception: ", ex);
			return false;
		}
		return false;
	}
}