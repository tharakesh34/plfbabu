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
 * FileName    		:  CustomerBalanceSheetDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  07-12-2011    														*
 *                                                                  						*
 * Modified Date    :  07-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.customermasters.impl;


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

import com.pennant.backend.dao.customermasters.CustomerBalanceSheetDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustomerBalanceSheet model</b> class.<br>
 * 
 */
public class CustomerBalanceSheetDAOImpl extends BasisCodeDAO<CustomerBalanceSheet> 
			implements CustomerBalanceSheetDAO {

	private static Logger logger = Logger.getLogger(CustomerBalanceSheetDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerBalanceSheetDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record  Customer Balance Sheet Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerBalanceSheet
	 */
	@Override
	public CustomerBalanceSheet getCustomerBalanceSheetById(final String financialYear,
			long custId, String type) {
		logger.debug("Entering");
		
		CustomerBalanceSheet customerBalanceSheet = new CustomerBalanceSheet();
		customerBalanceSheet.setId(financialYear);
		customerBalanceSheet.setCustId(custId);
		
		StringBuilder selectSql = new StringBuilder("Select CustId, FinancialYear, TotalAssets,");
		selectSql.append(" TotalLiabilities, NetProfit, NetSales, NetIncome, OperatingProfit," );
		selectSql.append(" CashFlow, BookValue, MarketValue,");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescCustCIF, lovDescCustRecordType , lovDescCustShrtName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From CustomerBalanceSheet");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustId =:CustId AND FinancialYear =:FinancialYear");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBalanceSheet);
		RowMapper<CustomerBalanceSheet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerBalanceSheet.class);
		
		try{
			customerBalanceSheet = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerBalanceSheet = null;
		}
		logger.debug("Leaving");
		return customerBalanceSheet;
	}
	
	/**
	 * Fetch the Records  Customer Balance Sheet List Details by CustomerID
	 * 
	 * @param id (long)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerBalanceSheet
	 */
	@Override
	public List<CustomerBalanceSheet> getBalanceSheetsByCustomer(final long id, String type) {
		logger.debug("Entering");
		CustomerBalanceSheet customerBalanceSheet = new CustomerBalanceSheet();
		customerBalanceSheet.setCustId(id);
		
		StringBuilder selectSql = new StringBuilder("Select CustId, FinancialYear, TotalAssets,");
		selectSql.append(" TotalLiabilities, NetProfit, NetSales, NetIncome, OperatingProfit," );
		selectSql.append(" CashFlow, BookValue, MarketValue,");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescCustCIF, lovDescCustRecordType , lovDescCustShrtName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From CustomerBalanceSheet");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustId =:CustId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBalanceSheet);
		RowMapper<CustomerBalanceSheet> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerBalanceSheet.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the CustomerBalanceSheet or CustomerBalanceSheet_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Customer Balance Sheet Details by key CustId
	 * 
	 * @param Customer Balance Sheet Details (customerBalanceSheet)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerBalanceSheet customerBalanceSheet,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From CustomerBalanceSheet");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustId =:CustId AND FinancialYear =:FinancialYear");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBalanceSheet);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method Deletes the Records from the CustomerBalanceSheet or CustomerBalanceSheet_Temp.
	 * delete Customer Balance Sheet Details List by key CustId
	 * 
	 * @param CustomerID (long)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * 
	 */
	public void deleteByCustomer(final long id,String type) {
		logger.debug("Entering");
		CustomerBalanceSheet customerBalanceSheet = new CustomerBalanceSheet();
		customerBalanceSheet.setCustId(id);
		
		StringBuilder deleteSql = new StringBuilder("Delete From CustomerBalanceSheet");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustId =:CustId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBalanceSheet);
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
	}
	
	/**
	 * This method insert new Records into CustomerBalanceSheet or CustomerBalanceSheet_Temp.
	 *
	 * save Customer Balance Sheet Details 
	 * 
	 * @param Customer Balance Sheet Details (customerBalanceSheet)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(CustomerBalanceSheet customerBalanceSheet,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder(" Insert Into CustomerBalanceSheet");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustId, FinancialYear, TotalAssets, TotalLiabilities, NetProfit, NetSales,");
		insertSql.append(" NetIncome, OperatingProfit, CashFlow, BookValue, MarketValue,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustId, :FinancialYear, :TotalAssets, :TotalLiabilities, :NetProfit,");
		insertSql.append(" :NetSales, :NetIncome, :OperatingProfit, :CashFlow, :BookValue, :MarketValue,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBalanceSheet);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return customerBalanceSheet.getId();
	}
	
	/**
	 * This method updates the Record CustomerBalanceSheet or CustomerBalanceSheet_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Customer Balance Sheet Details by key CustId and Version
	 * 
	 * @param Customer Balance Sheet Details (customerBalanceSheet)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerBalanceSheet customerBalanceSheet,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update CustomerBalanceSheet");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set TotalAssets = :TotalAssets, TotalLiabilities = :TotalLiabilities," );
		updateSql.append(" NetProfit = :NetProfit, NetSales = :NetSales, NetIncome = :NetIncome,");
		updateSql.append(" OperatingProfit = :OperatingProfit, CashFlow = :CashFlow," );
		updateSql.append(" BookValue = :BookValue, MarketValue = :MarketValue,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CustId =:CustId AND FinancialYear =:FinancialYear");
		
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerBalanceSheet);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}