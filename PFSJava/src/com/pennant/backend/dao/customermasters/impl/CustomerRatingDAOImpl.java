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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerRatingDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CustomerRating model</b> class.<br>
 * 
 */
public class CustomerRatingDAOImpl extends BasisCodeDAO<CustomerRating> implements CustomerRatingDAO {

	private static Logger logger = Logger.getLogger(CustomerRatingDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new CustomerRating 
	 * @return CustomerRating
	 */
	@Override
	public CustomerRating getCustomerRating() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CustomerRating");
		CustomerRating customerRating= new CustomerRating();
		if (workFlowDetails!=null){
			customerRating.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return customerRating;
	}

	/**
	 * This method get the module from method getCustomerRating() and set the
	 * new record flag as true and return CustomerRating()
	 * 
	 * @return CustomerRating
	 */
	@Override
	public CustomerRating getNewCustomerRating() {
		logger.debug("Entering");
		CustomerRating customerRating = getCustomerRating();
		customerRating.setNewRecord(true);
		logger.debug("Leaving");
		return customerRating;
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
			selectSql.append(" lovDescCustRecordType , lovDescCustCIF, lovDescCustShrtName," );
			selectSql.append(" lovDescCustRatingTypeName, lovDesccustRatingCodeDesc,lovDescCustRatingName, ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  CustomerRatings"+StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND CustRatingType = :custRatingType") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerRating);
		RowMapper<CustomerRating> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerRating.class);

		try{
			customerRating = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
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
			selectSql.append(" lovDescCustRecordType , lovDescCustCIF, lovDescCustShrtName," );
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

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
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

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}

	/**
	 * This method initialize the Record.
	 * @param CustomerRating (customerRating)
	 * @return CustomerRating
	 */
	@Override
	public void initialize(CustomerRating customerRating) {
		super.initialize(customerRating);
	}

	/**
	 * This method refresh the Record.
	 * @param CustomerRating (customerRating)
	 * @return void
	 */
	@Override
	public void refresh(CustomerRating customerRating) {

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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails= getError("41003", customerRating.getCustID(),
						customerRating.getCustRatingType(), customerRating.getUserDetails().getUsrLanguage()); 
				throw new DataAccessException(errorDetails.getError()) {};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", customerRating.getCustID(), 
					customerRating.getCustRatingType(), customerRating.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
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
	@SuppressWarnings("serial")
	@Override
	public void update(CustomerRating customerRating,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerRatings" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set CustID = :CustID, CustRatingType = :CustRatingType, CustRatingCode = :CustRatingCode," );
		updateSql.append(" CustRating = :CustRating, ValueType = :ValueType ," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId" );
		updateSql.append(" Where CustID =:CustID and CustRatingType = :CustRatingType ");

		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerRating);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails= getError("41004", customerRating.getCustID(), 
					customerRating.getCustRatingType(), customerRating.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, long customerID,String ratingType, String userLanguage){
		String[][] parms= new String[2][2]; 
		
		parms[1][0] = String.valueOf(customerID);
		parms[1][1] = ratingType;

		parms[0][0] = PennantJavaUtil.getLabel("label_CustID")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_CustRatingType")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

}