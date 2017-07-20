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
 * FileName    		:  CustomerEMailDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustomerEMail model</b> class.<br>
 * 
 */
public class CustomerEMailDAOImpl extends BasisCodeDAO<CustomerEMail> implements CustomerEMailDAO {

	private static Logger logger = Logger.getLogger(CustomerEMailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerEMailDAOImpl() {
		super();
	}
	

	/**
	 * Fetch the Record  Customer EMails details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerEMail
	 */
	@Override
	public CustomerEMail getCustomerEMailById(final long id, String typeCode,String type) {
		logger.debug("Entering");
		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setId(id);
		customerEMail.setCustEMailTypeCode(typeCode);
		StringBuilder selectSql = new StringBuilder();	
		selectSql.append(" SELECT CustID, CustEMail, CustEMailPriority, CustEMailTypeCode,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustEMailTypeCode,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerEMails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND CustEMailTypeCode = :custEMailTypeCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		RowMapper<CustomerEMail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerEMail.class);

		try{
			customerEMail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerEMail = null;
		}
		logger.debug("Leaving");
		return customerEMail;
	}

	/**
	 * Method to return the customer email based on given customer id
	 * */
	public List<CustomerEMail> getCustomerEmailByCustomer(final long id,String type) {
		logger.debug("Entering");
		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setId(id);
		
		StringBuilder selectSql = new StringBuilder();	
		selectSql.append(" SELECT CustID, CustEMail, CustEMailPriority, CustEMailTypeCode,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustEMailTypeCode,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerEMails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		RowMapper<CustomerEMail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerEMail.class);
		
		List<CustomerEMail> customerEMails = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		
		logger.debug("Leaving");
		return customerEMails;
	}	
	
	/**
	 * Method to return the customer email based on given customer id
	 */
	@Override
	public List<String> getCustEmailsByCustId(final long custId) {
		logger.debug("Entering");
		
		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setId(custId);
		
		StringBuilder selectSql = new StringBuilder();	
		selectSql.append(" SELECT CustEMail FROM  CustomerEMails ");
		selectSql.append(" Where CustID = :custID ORDER BY CustEMailPriority");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		RowMapper<String> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(String.class);
		
		
		List <String> custEmailsByIDs = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper); 
		logger.debug("Leaving");
		return custEmailsByIDs;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the CustomerEMails or CustomerEMails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Customer EMails by key CustID
	 * 
	 * @param Customer EMails (customerEMail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerEMail customerEMail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From CustomerEMails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID AND CustEMailTypeCode =:custEMailTypeCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);

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
	 * This method Deletes the Record from the CustomerEMails or CustomerEMails_Temp for the Customer.
	 * 
	 * @param Customer EMails (customerEMail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByCustomer(long custID,String type) {
		logger.debug("Entering");

		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setCustID(custID);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerEMails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CustomerEMails or CustomerEMails_Temp.
	 *
	 * save Customer EMails 
	 * 
	 * @param Customer EMails (customerEMail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerEMail customerEMail,String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerEMails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, CustEMailTypeCode, CustEMailPriority, CustEMail," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustEMailTypeCode, :CustEMailPriority, :CustEMail,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerEMail.getId();
	}

	/**
	 * This method updates the Record CustomerEMails or CustomerEMails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Customer EMails by key CustID and Version
	 * 
	 * @param Customer EMails (customerEMail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerEMail customerEMail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerEMails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustEMailPriority = :CustEMailPriority, CustEMail = :CustEMail,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND CustEMailTypeCode =:custEMailTypeCode ");
		if (!type.endsWith("_Temp")){
			updateSql.append("AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for get total number of records from BMTEMailTypes master table.<br>
	 * 
	 * @param EmailTypeCode
	 * 
	 * @return Integer
	 */
	@Override
	public int getEMailTypeCount(String typeCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("EmailTypeCode", typeCode);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM BMTEMailTypes");
		selectSql.append(" WHERE ");
		selectSql.append("EmailTypeCode= :EmailTypeCode");

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
	/**
	 * Fetch current version of the record.
	 * 
	 * @param id
	 * @param typeCode
	 * @return Integer
	 */
	@Override
	public int getVersion(long id, String typeCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", id);
		source.addValue("CustEMailTypeCode", typeCode);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM CustomerEMails");

		selectSql.append(" WHERE CustId = :CustId AND CustEMailTypeCode = :CustEMailTypeCode");

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