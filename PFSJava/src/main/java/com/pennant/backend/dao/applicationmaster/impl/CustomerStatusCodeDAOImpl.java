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
 * FileName    		:  CustomerStatusCodeDAOImpl.java                                       * 	  
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

import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustomerStatusCode model</b> class.<br>
 * 
 */
public class CustomerStatusCodeDAOImpl extends BasisCodeDAO<CustomerStatusCode>	implements CustomerStatusCodeDAO {
	private static Logger logger = Logger.getLogger(CustomerStatusCodeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerStatusCodeDAOImpl() {
		super();
	}
	

	/**
	 * Fetch the Record Customer Status Codes details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerStatusCode
	 */
	@Override
	public CustomerStatusCode getCustomerStatusCodeById(final String id, String type) {
		logger.debug("Entering");
		CustomerStatusCode customerStatusCode = new CustomerStatusCode();
		customerStatusCode.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("SELECT CustStsCode, CustStsDescription, DueDays, SuspendProfit,CustStsIsActive," );
		if(type.contains("View")){
			selectSql.append("");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTCustStatusCodes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustStsCode =:CustStsCode") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerStatusCode);
		RowMapper<CustomerStatusCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerStatusCode.class);

		try {
			customerStatusCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			customerStatusCode = null;
		}
		logger.debug("Leaving");
		return customerStatusCode;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTCustStatusCodes or
	 * BMTCustStatusCodes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Customer Status Codes by key
	 * CustStsCode
	 * 
	 * @param Customer
	 *            Status Codes (customerStatusCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(CustomerStatusCode customerStatusCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTCustStatusCodes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustStsCode =:CustStsCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerStatusCode);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTCustStatusCodes or
	 * BMTCustStatusCodes_Temp.
	 * 
	 * save Customer Status Codes
	 * 
	 * @param Customer
	 *            Status Codes (customerStatusCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(CustomerStatusCode customerStatusCode, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTCustStatusCodes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustStsCode, CustStsDescription, DueDays, SuspendProfit, CustStsIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:CustStsCode, :CustStsDescription, :DueDays, :SuspendProfit,:CustStsIsActive, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerStatusCode);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerStatusCode.getId();
	}

	/**
	 * This method updates the Record BMTCustStatusCodes or
	 * BMTCustStatusCodes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer Status Codes by key
	 * CustStsCode and Version
	 * 
	 * @param Customer
	 *            Status Codes (customerStatusCode)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerStatusCode customerStatusCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTCustStatusCodes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustStsDescription = :CustStsDescription," );
		updateSql.append(" DueDays=:DueDays, SuspendProfit=:SuspendProfit,CustStsIsActive = :CustStsIsActive," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where CustStsCode =:CustStsCode ");
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerStatusCode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for get Finance Profit Suspend status
	 */
	@Override
    public boolean getFinanceSuspendStatus(int curODDays) {
		logger.debug("Entering");
		
		boolean suspendProfit = false;
		CustomerStatusCode customerStatusCode = new CustomerStatusCode();
		customerStatusCode.setDueDays(curODDays);
		StringBuilder selectSql = new StringBuilder("SELECT Count(CustStsCode) " );
		selectSql.append(" FROM  BMTCustStatusCodes");
		selectSql.append(" Where DueDays <=:DueDays AND SuspendProfit = 1") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerStatusCode);

		int suspendCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		if(suspendCount > 0){
			suspendProfit = true;
		}
		logger.debug("Leaving");
		return suspendProfit;
    }
	
	/**
	 * Method for get Finance Profit Suspend status
	 */
	@Override
	public String getFinanceStatus(String finReference, boolean isCurFinStatus) {
		logger.debug("Entering");
		
		FinODDetails odDetails = new FinODDetails();
		odDetails.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder(" Select CustStsCode FROM (SELECT CustStsCode, ");
		selectSql.append(" row_number() over (order by DueDays DESC) row_num  FROM BMTCustStatusCodes " );
		selectSql.append(" WHERE DueDays <= (Select COALESCE(Max(FinCurODDays), 0) from FinODDetails " );
		selectSql.append(" WHERE FinReference = :FinReference " );
		if(isCurFinStatus){
			selectSql.append(" AND FinCurODAmt != 0 " );
		}
		selectSql.append("  ))T WHERE row_num <= 1 " );
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(odDetails);
		
		String custStsCode = null;
		try {
			custStsCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,String.class);
        } catch (Exception e) {
        	logger.warn("Exception: ", e);
        	custStsCode = null;
        }
		
		logger.debug("Leaving");
		return custStsCode;
	}

	@Override
	public CustomerStatusCode getCustStatusByMinDueDays(String type) {
		logger.debug("Entering");
		CustomerStatusCode customerStatusCode = new CustomerStatusCode();
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("SELECT CustStsCode, CustStsDescription, DueDays, SuspendProfit,CustStsIsActive," );
		
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTCustStatusCodes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DueDays =( SELECT COALESCE(MIN(DueDays),0) from BMTCustStatusCodes)") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerStatusCode);
		RowMapper<CustomerStatusCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerStatusCode.class);

		try {
			customerStatusCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			customerStatusCode = null;
		}
		logger.debug("Leaving");
		return customerStatusCode;
	}
}