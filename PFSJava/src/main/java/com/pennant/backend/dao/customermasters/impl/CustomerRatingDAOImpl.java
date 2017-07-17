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
 * FileName    		:  CustomerRatingDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.customermasters.CustomerRatingDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustomerRating model</b> class.<br>
 * 
 */
public class CustomerRatingDAOImpl extends BasisCodeDAO<CustomerRating> implements CustomerRatingDAO {

	private static Logger logger = Logger.getLogger(CustomerRatingDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerRatingDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  Customer Ratings details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerRating
	 */
	@Override
	public CustomerRating getCustomerRatingByID(final long id,String ratingType, String type) {
		logger.debug("Entering");
		CustomerRating customerRating = new CustomerRating();
		customerRating.setId(id);
		customerRating.setCustRatingType(ratingType);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID,CustRatingType, CustRatingCode, CustRating, ValueType," );
		if(type.contains("View")){
			selectSql.append(" lovDescCustRatingTypeName, lovDesccustRatingCodeDesc,lovDescCustRatingName, ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  CustomerRatings");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND CustRatingType = :custRatingType") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerRating);
		RowMapper<CustomerRating> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerRating.class);

		try{
			customerRating = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerRating = null;
		}
		logger.debug("Leaving");
		return customerRating;
	}

	/** 
	 * Method For getting List of Customer related Ratings for Customer
	 */
	public List<CustomerRating> getCustomerRatingByCustomer(final long id,String type) {
		logger.debug("Entering");
		CustomerRating customerRating = new CustomerRating();
		customerRating.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID,CustRatingType, CustRatingCode, CustRating, ValueType, " );
		if(type.contains("View")){
			selectSql.append(" lovDescCustRatingTypeName, lovDesccustRatingCodeDesc,lovDescCustRatingName, ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  CustomerRatings");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID ") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerRating);
		RowMapper<CustomerRating> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerRating.class);

		List<CustomerRating> customerRatings = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper); 
		logger.debug("Leaving");
		return customerRatings;
	}
	
	/** 
	 * Method For getting List of Customer related Ratings for Customer
	 */
	@Override
	public List<CustomerRating> getCustomerRatingByCustId(final long id, String type) {
		logger.debug("Entering");
		CustomerRating customerRating = new CustomerRating();
		customerRating.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select CustRatingType, CustRatingCode , LovDescCustRatingCodeDesc " );
		selectSql.append(" FROM  CustomerRatings");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND CustRatingCode != '' ") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerRating);
		RowMapper<CustomerRating> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerRating.class);

		List<CustomerRating> customerRatings = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper); 

		logger.debug("Leaving");
		return customerRatings;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the CustomerRatings or CustomerRatings_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Customer Ratings by key CustID
	 * 
	 * @param Customer Ratings (customerRating)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerRating customerRating,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From CustomerRatings" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where CustID =:CustID AND CustRatingType =:CustRatingType");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerRating);
		try{
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
	 * Method for Deletion of Customer Related List of CustomerRatings for the Customer
	 */
	public void deleteByCustomer(final long customerId,String type) {
		logger.debug("Entering");
		CustomerRating customerRating = new CustomerRating();
		customerRating.setId(customerId);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From CustomerRatings" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where CustID =:CustID ");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerRating);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CustomerRatings or CustomerRatings_Temp.
	 *
	 * save Customer Ratings 
	 * 
	 * @param Customer Ratings (customerRating)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerRating customerRating,String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerRatings" );
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, CustRatingType, CustRatingCode, CustRating, ValueType," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:CustID, :CustRatingType, :CustRatingCode, :CustRating, :ValueType, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerRating);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerRating.getId();
	}

	/**
	 * This method updates the Record CustomerRatings or CustomerRatings_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Customer Ratings by key CustID and Version
	 * 
	 * @param Customer Ratings (customerRating)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerRating customerRating,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerRatings" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set CustRatingCode = :CustRatingCode," );
		updateSql.append(" CustRating = :CustRating, ValueType = :ValueType ," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId" );
		updateSql.append(" Where CustID =:CustID and CustRatingType = :CustRatingType ");

		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerRating);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}