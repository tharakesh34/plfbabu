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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CustomerIncome model</b> class.<br>
 * 
 */
public class CustomerIncomeDAOImpl extends BasisCodeDAO<CustomerIncome> implements CustomerIncomeDAO {

	private static Logger logger = Logger.getLogger(CustomerIncomeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new CustomerIncome
	 * 
	 * @return CustomerIncome
	 */

	@Override
	public CustomerIncome getCustomerIncome() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerIncome");
		CustomerIncome customerIncome = new CustomerIncome();
		if (workFlowDetails != null) {
			customerIncome.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return customerIncome;
	}

	/**
	 * This method get the module from method getCustomerIncome() and set the
	 * new record flag as true and return CustomerIncome()
	 * 
	 * @return CustomerIncome
	 */

	@Override
	public CustomerIncome getNewCustomerIncome() {
		logger.debug("Entering");
		CustomerIncome customerIncome = getCustomerIncome();
		customerIncome.setNewRecord(true);
		logger.debug("Leaving");
		return customerIncome;
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
	public CustomerIncome getCustomerIncomeById(final long id,String incomeType, String country, String type) {
		logger.debug("Entering");
		CustomerIncome customerIncome = getCustomerIncome();
		customerIncome.setId(id);
		customerIncome.setCustIncomeType(incomeType);
		customerIncome.setCustIncomeCountry(country);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID,  CustIncomeCountry, CustIncome, CustIncomeType,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustIncomeTypeName, lovDescCustIncomeCountryName," );
			selectSql.append(" lovDescCustRecordType,lovDescCustCIF, lovDescCustShrtName,lovDescCcyEditField,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  CustomerIncomes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND CustIncomeType = :custIncomeType ");
		selectSql.append(" AND CustIncomeCountry = :custIncomeCountry ");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerIncome.class);

		try {
			customerIncome = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
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
	public List<CustomerIncome> getCustomerIncomeByCustomer(final long id,String type) {
		logger.debug("Entering");
		CustomerIncome customerIncome = getCustomerIncome();
		customerIncome.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustIncomeCountry, CustIncome, CustIncomeType,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustIncomeTypeName, lovDescCustIncomeCountryName," );
			selectSql.append(" lovDescCustRecordType,lovDescCustCIF, lovDescCustShrtName,lovDescCcyEditField, ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  CustomerIncomes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID ");

		logger.debug("selectSql: "+ selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),beanParameters, typeRowMapper);
	}
	
	@Override
	public BigDecimal getTotalIncomeByCustomer(long custId) {
		logger.debug("Entering");
		
		CustomerIncome customerIncome = getCustomerIncome();
		customerIncome.setCustID(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT SUM(CustIncome)  FROM  CustomerIncomes_AView Where custID = :custID ");

		logger.debug("selectSql: "+ selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);
		logger.debug("Leaving");
		BigDecimal totalIncome = new BigDecimal(0);
		try {
			totalIncome = new BigDecimal(this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),beanParameters, BigDecimal.class).toString());
		} catch (Exception e) {
			totalIncome = new BigDecimal(0);
		}
		return totalIncome;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param CustomerIncome
	 *            (customerIncome)
	 * @return CustomerIncome
	 */
	@Override
	public void initialize(CustomerIncome customerIncome) {
		super.initialize(customerIncome);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param CustomerIncome
	 *            (customerIncome)
	 * @return void
	 */
	@Override
	public void refresh(CustomerIncome customerIncome) {

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
	@SuppressWarnings("serial")
	public void delete(CustomerIncome customerIncome, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerIncomes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID AND CustIncomeType =:CustIncomeType " );
		deleteSql.append(" AND CustIncomeCountry =:CustIncomeCountry ");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",customerIncome.getCustID(),
						customerIncome.getCustIncomeType(),customerIncome.getCustIncomeCountry(), 
						customerIncome.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",customerIncome.getCustID(),
					customerIncome.getCustIncomeType(),customerIncome.getCustIncomeCountry(),
					customerIncome.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
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
	public void deleteByCustomer(final long customerId, String type) {
		logger.debug("Entering");
		
		CustomerIncome customerIncome = getCustomerIncome();
		customerIncome.setId(customerId);		
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerIncomes");
		deleteSql.append(StringUtils.trimToEmpty(type));
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
		insertSql.append(" (CustID, CustIncomeType, CustIncome, CustIncomeCountry," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustIncomeType, :CustIncome, :CustIncomeCountry,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerIncome.getId();
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
	@SuppressWarnings("serial")
	@Override
	public void update(CustomerIncome customerIncome, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update CustomerIncomes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustID = :CustID, CustIncomeType = :CustIncomeType, CustIncome = :CustIncome,");
		updateSql.append(" CustIncomeCountry = :CustIncomeCountry,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND CustIncomeType =:CustIncomeType " );
		updateSql.append(" AND CustIncomeCountry =:CustIncomeCountry");
		if (!type.endsWith("_TEMP")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41003",customerIncome.getCustID(),
					customerIncome.getCustIncomeType(),customerIncome.getCustIncomeCountry(),
					customerIncome.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails  getError(String errorId, long customerID,
			String incomeType,String incomeCountry, String userLanguage){
		
		String[][] parms= new String[2][3]; 

		parms[1][0] = String.valueOf(customerID);
		parms[1][1] = incomeType;
		parms[1][2] = incomeCountry;

		parms[0][0] = PennantJavaUtil.getLabel("label_CustID")+ ":" + parms[1][0] +
						PennantJavaUtil.getLabel("label_CustIncomeType")+ ":" + parms[1][1];
		parms[0][1]= PennantJavaUtil.getLabel("label_CustIncomeCountry")+ ":" + parms[1][2];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}


}