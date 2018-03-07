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
 * FileName    		:  CostOfFundDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.applicationmaster.CostOfFundDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.CostOfFund;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>CostOfFund model</b> class.<br>
 * 
 */
public class CostOfFundDAOImpl extends BasisCodeDAO<CostOfFund> implements CostOfFundDAO {
	private static Logger	 logger	= Logger.getLogger(CostOfFundDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public CostOfFundDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record CostOfFunds details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CostOfFund
	 */
	@Override
	public CostOfFund getCostOfFundById(final String cofCode, String currency, Date cofEffDate, String type) {
		logger.debug(Literal.ENTERING);
		
		CostOfFund costOfFund = new CostOfFund();
		costOfFund.setCofCode(cofCode);
		costOfFund.setCurrency(currency);
		costOfFund.setCofEffDate(cofEffDate);

		StringBuilder selectSql = new StringBuilder("SELECT CofCode, Currency, CofEffDate, cofRate, DelExistingRates, Active,");
		if (type.contains("View")) {
			selectSql.append(" LovDescCofTypeName, ");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,LastMdfDate ");
		selectSql.append(" FROM CostOfFunds");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CofCode =:CofCode and CofEffDate=:CofEffDate and Currency=:Currency");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(costOfFund);
		RowMapper<CostOfFund> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CostOfFund.class);
		try {
			costOfFund = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			costOfFund = null;
		}
		
		logger.debug(Literal.LEAVING);
		return costOfFund;
	}
	
	@Override
	public boolean isDuplicateKey(String cofCode, Date cofEffDate, String currency, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "CofCode =:cofCode and CofEffDate=:cofEffDate and Currency=:currency";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CostOfFunds", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CostOfFunds_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CostOfFunds_Temp", "CostOfFunds" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("cofCode", cofCode);
		paramSource.addValue("cofEffDate", cofEffDate);
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
	public String save(CostOfFund costOfFund, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into CostOfFunds");
		sql.append(tableType.getSuffix());
		sql.append(" (CofCode, Currency, CofEffDate, cofRate, DelExistingRates, Active,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		sql.append(" NextTaskId, RecordType, WorkflowId, LastMdfDate )");
		sql.append(" Values(:CofCode, :Currency, :CofEffDate, :cofRate, :DelExistingRates, :Active,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId, :LastMdfDate)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(costOfFund);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return costOfFund.getId();
	}

	@Override
	public void update(CostOfFund costOfFund, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update CostOfFunds");
		sql.append(tableType.getSuffix());
		sql.append(" set cofRate = :cofRate,  DelExistingRates = :DelExistingRates, Active = :Active,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, LastMdfDate=:LastMdfDate ");
		sql.append(" where CofCode =:CofCode AND CofEffDate = :CofEffDate AND Currency = :Currency");
		/*sql.append(QueryUtil.getConcurrencyCondition(tableType));*/

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(costOfFund);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public void delete(CostOfFund costOfFund, TableType tableType ) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" delete from CostOfFunds");
		sql.append(tableType.getSuffix());
		sql.append(" where CofCode =:CofCode and CofEffDate = :CofEffDate and Currency = :Currency");
		/*sql.append(QueryUtil.getConcurrencyCondition(tableType));*/
		
		// Execute the SQL, binding the arguments.
	    logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(costOfFund);
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
	 * Common method for CostOfFunds to get the costOfFund and
	 * get the List of objects less than passed Effective CostOfFund Date
	 * 
	 * @param cofCode
	 * @param cofEffDate
	 * @param type
	 * @return
	 */
	private List<CostOfFund> getCostOfFundListByType(String cofCode, String currency, Date cofEffDate, String type) {
		logger.debug("Entering");
		CostOfFund costOfFund = new CostOfFund();
		costOfFund.setCofCode(cofCode);
		costOfFund.setCurrency(currency);
		costOfFund.setCofEffDate(cofEffDate);

		StringBuilder selectSql = new StringBuilder("SELECT CofCode,Currency,CofEffDate,cofRate,LastMdfDate ");
		selectSql.append(" FROM CostOfFunds");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CofCode =:CofCode and Currency =:Currency and CofEffDate <=:CofEffDate  Order by CofEffDate Desc");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(costOfFund);
		RowMapper<CostOfFund> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CostOfFund.class);
		
		List<CostOfFund> costOfFunds = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		
		logger.debug("Leaving");
		return costOfFunds;
	}

	public List<CostOfFund> getCostOfFundHistByType(String cofCode, String currency, Date cofEffDate) {
		logger.debug("Entering");
		CostOfFund costOfFund = new CostOfFund();
		costOfFund.setCofCode(cofCode);
		costOfFund.setCurrency(currency);
		costOfFund.setCofEffDate(cofEffDate);

		StringBuilder selectSql = new StringBuilder("select COFCODE, COFEFFDATE, COFRATE ");
		selectSql.append(" FROM CostOfFunds");
		selectSql.append(" Where cofCode = :CofCode AND Currency = :Currency ");
		selectSql.append(" AND cofeffdate >= (select max(CofEffDate) from COSTOFFUNDS ");
		selectSql.append(" Where cofCode = :CofCode AND Currency = :Currency AND CofEffDate <= :CofEffDate)");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(costOfFund);
		RowMapper<CostOfFund> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CostOfFund.class);
		
		List<CostOfFund> costOfFunds = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper); 
		
		logger.debug("Leaving");
		return costOfFunds;
	}

	/**
	 * To get base rate value using base rate code and effective date is less
	 * than passed date
	 */
	public CostOfFund getCostOfFundByType(final String cofCode, String currency,  Date cofEffDate) {
		logger.debug("Entering");
		CostOfFund costOfFund = null;

		List<CostOfFund> costOfFunds = getCostOfFundListByType(cofCode, currency, cofEffDate, "");
		if (costOfFunds.size() > 0) {
			costOfFund = costOfFunds.get(0);
		}

		logger.debug("Leaving");
		return costOfFund;
	}

	/**
	 * To get base rate value using base rate code and effective date is less
	 * than passed date
	 */
	public boolean getCostOfFundListById(String cofCode, String currency, Date cofEffDate, String type) {
		logger.debug("Entering");
		CostOfFund costOfFund = new CostOfFund();
		costOfFund.setCofCode(cofCode);
		costOfFund.setCurrency(currency);
		costOfFund.setCofEffDate(cofEffDate);

		List<CostOfFund> costOfFundList = getCostOfFundListByType(cofCode, currency, cofEffDate, type);

		if (costOfFundList.size() > 0) {
			CostOfFund rate = costOfFundList.get(0);
			if (rate.getCofEffDate().equals(costOfFund.getCofEffDate())) {
				costOfFundList.remove(0);
			}
		}

		logger.debug("Leaving");

		if (costOfFundList.size() > 0) {
			return false;
		}
		return true;
	}	

	@Override
	public List<CostOfFund> getBSRListByMdfDate(Date effDate, String type) {
		logger.debug("Entering");
		
		CostOfFund costOfFund =new CostOfFund();
		StringBuilder selectSql = new StringBuilder("SELECT CofCode,Currency,CofEffDate,cofRate,LastMdfDate ");
		selectSql.append(" FROM CostOfFunds");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LastMdfDate='");
		selectSql.append(effDate);
		selectSql.append('\'');
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(costOfFund);
		RowMapper<CostOfFund> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CostOfFund.class);
		
		List<CostOfFund> costOfFunds = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper); 
		logger.debug("Leaving");
		return costOfFunds;
	}
	
	/**
	 * This method Deletes the Record from the CostOfFunds
	 * If Record not deleted then throws DataAccessException
	 * with error 41003. delete CostOfFunds greater than effective date
	 * 
	 * @param CostOfFunds
	 *            (costOfFund)
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByEffDate(CostOfFund costOfFund, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder(" Delete From CostOfFunds");
		deleteSql.append(StringUtils.trimToEmpty(type)); 
		deleteSql.append(" Where CofCode =:CofCode and Currency = :Currency and CofEffDate > :CofEffDate");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(costOfFund);

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
	 * @param cofCode
	 * @param currency
	 * @param type
	 * 
	 * @return Integer
	 */
	@Override
	public int getCostOfFundCountById(String cofCode, String currency, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CofCode", cofCode);
		source.addValue("Currency", currency);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(CofCode) From  CostOfFunds ");
		selectSql.append(" WHERE CofCode = :CofCode AND Currency = :Currency");
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