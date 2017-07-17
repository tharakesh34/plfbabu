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
 * FileName    		:  CustomerIncomeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustomerIncome model</b> class.<br>
 * 
 */
public class CustomerIncomeDAOImpl extends BasisCodeDAO<CustomerIncome> implements CustomerIncomeDAO {
	private static Logger logger = Logger.getLogger(CustomerIncomeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerIncomeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer Incomes details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerIncome
	 */
	public CustomerIncome getCustomerIncomeById(CustomerIncome customerIncome, String type) {
		logger.debug("Entering");
 	 
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID,  CustIncome, CustIncomeType, IncomeExpense, Category, Margin, JointCust,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustIncomeTypeName, lovDescCategoryName, " );
			selectSql.append(" lovDescCustCIF, lovDescCustShrtName,ToCcy, ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  CustomerIncomes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND CustIncomeType = :custIncomeType ");
		selectSql.append(" AND IncomeExpense = :IncomeExpense AND Category=:Category AND JointCust = :JointCust");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerIncome.class);

		try {
			customerIncome = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerIncome = null;
		}
		logger.debug("Leaving");
		return customerIncome;
	}

	/**
	 * Fetch the Records Customer Incomes details by key field
	 * 
	 * @param customerId
	 *            (long)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerIncome
	 */
	@Override
	public List<CustomerIncome> getCustomerIncomeByCustomer(final long id, boolean isWIF, String type) {
		logger.debug("Entering");
		
		CustomerIncome customerIncome = new CustomerIncome();
		customerIncome.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID,  CustIncome, CustIncomeType,IncomeExpense,Category,Margin, JointCust,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustIncomeTypeName,lovDescCategoryName, " );
			if(!isWIF){
				selectSql.append(" lovDescCustCIF, lovDescCustShrtName,ToCcy, ");
			}
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		if(isWIF){
			selectSql.append(" FROM  WIFCustomerIncomes");
		}else{
			selectSql.append(" FROM  CustomerIncomes");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID =:CustID ");

		logger.debug("selectSql: "+ selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),beanParameters, typeRowMapper);
	}
	
	@Override
	public BigDecimal getTotalIncomeByCustomer(long custId) {
		logger.debug("Entering");
		
		CustomerIncome customerIncome = new CustomerIncome();
		customerIncome.setCustID(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT SUM(CustIncome)  FROM  CustomerIncomes_AView Where custID = :custID ");

		logger.debug("selectSql: "+ selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);
		logger.debug("Leaving");
		BigDecimal totalIncome = BigDecimal.ZERO;
		try {
			totalIncome = new BigDecimal(this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),beanParameters, BigDecimal.class).toString());
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			totalIncome = BigDecimal.ZERO;
		}
		return totalIncome;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the CustomerIncomes or
	 * CustomerIncomes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Customer Incomes by key
	 * CustID
	 * 
	 * @param Customer
	 *            Incomes (customerIncome)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerIncome customerIncome, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerIncomes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID AND CustIncomeType =:CustIncomeType " );
		deleteSql.append(" AND IncomeExpense =:IncomeExpense  AND Category =:Category AND JointCust = :JointCust");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Records from the CustomerIncomes or
	 * CustomerIncomes_Temp if records Existed in table.
	 * 
	 * @param customerId (long)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByCustomer(final long customerId, String type, boolean isWIF) {
		logger.debug("Entering");
		
		CustomerIncome customerIncome = new CustomerIncome();
		customerIncome.setId(customerId);		
		
		StringBuilder deleteSql = new StringBuilder();
		if(!isWIF){
			deleteSql.append(" Delete From CustomerIncomes");
			deleteSql.append(StringUtils.trimToEmpty(type));
		}else{
			deleteSql.append(" Delete From WIFCustomerIncomes");
		}
		deleteSql.append(" Where CustID =:CustID ");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);

		this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CustomerIncomes or
	 * CustomerIncomes_Temp.
	 * 
	 * save Customer Incomes
	 * 
	 * @param Customer
	 *            Incomes (customerIncome)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(CustomerIncome customerIncome, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerIncomes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, CustIncomeType, CustIncome, IncomeExpense, Category,Margin, JointCust, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustIncomeType, :CustIncome, :IncomeExpense,:Category,:Margin, :JointCust, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerIncome.getId();
	}
	
	/**
	 * This method insert new Records into CustomerIncomes or
	 * CustomerIncomes_Temp.
	 * 
	 * save Customer Incomes
	 * 
	 * @param Customer
	 *            Incomes (customerIncome)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void saveBatch(List<CustomerIncome> customerIncome, String type, boolean isWIF) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		if(!isWIF){
			insertSql.append(" Insert Into CustomerIncomes");
			insertSql.append(StringUtils.trimToEmpty(type));
		}else{
			insertSql.append(" Insert Into WIFCustomerIncomes");
		}
		
		insertSql.append(" (CustID, CustIncomeType, CustIncome, IncomeExpense, Category,Margin, JointCust, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustIncomeType, :CustIncome, :IncomeExpense,:Category,:Margin, :JointCust, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerIncome.toArray());
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
	}

	/**
	 * This method updates the Record CustomerIncomes or CustomerIncomes_Temp.
	 * if Record not updated then throws DataAccessException with error 41004.
	 * update Customer Incomes by key CustID and Version
	 * 
	 * @param Customer
	 *            Incomes (customerIncome)
	 * @param type,
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerIncome customerIncome, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update CustomerIncomes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustIncome = :CustIncome,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND CustIncomeType =:CustIncomeType " );
		updateSql.append(" AND IncomeExpense =:IncomeExpense AND Category =:Category AND JointCust = :JointCust");
		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch current version of the record.
	 * 
	 * @param customerIncome
	 * @return Integer
	 */
	@Override
	public int getVersion(CustomerIncome customerIncome) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", customerIncome.getCustID());
		source.addValue("IncomeExpense", customerIncome.getIncomeExpense());
		source.addValue("CustIncomeType", customerIncome.getCustIncomeType());
		source.addValue("JointCust", customerIncome.isJointCust());
		source.addValue("Category", customerIncome.getCategory());

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM CustomerIncomes");
		selectSql.append(" WHERE ");
		selectSql.append("CustID= :CustID AND IncomeExpense= :IncomeExpense AND CustIncomeType= :CustIncomeType AND Category =:Category AND JointCust= :JointCust");

		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount;
	}


}