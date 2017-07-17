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
 * FileName    		:  CustomerPhoneNumberDAOImpl.java                                      * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustomerPhoneNumber model</b> class.<br>
 * 
 */
public class CustomerPhoneNumberDAOImpl extends BasisCodeDAO<CustomerPhoneNumber> implements CustomerPhoneNumberDAO {

	private static Logger logger = Logger.getLogger(CustomerPhoneNumberDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public CustomerPhoneNumberDAOImpl() {
		super();
	}


	/**
	 * Fetch the Record  Customer PhoneNumbers details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerPhoneNumber
	 */
	@Override
	public CustomerPhoneNumber getCustomerPhoneNumberByID(final long id,String typeCode,String type) {
		logger.debug("Entering");
		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setId(id);
		customerPhoneNumber.setPhoneTypeCode(typeCode);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  PhoneCustID, PhoneTypeCode," );
		selectSql.append(" PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority," );
		if(type.contains("View")){
			selectSql.append(" lovDescPhoneTypeCodeName, lovDescPhoneCountryName,PhoneRegex," );
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId " );
		selectSql.append(" FROM  CustomerPhoneNumbers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PhoneCustID =:PhoneCustID AND PhoneTypeCode=:PhoneTypeCode ") ; 
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		RowMapper<CustomerPhoneNumber> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerPhoneNumber.class);
		
		try{
			customerPhoneNumber = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerPhoneNumber = null;
		}
		logger.debug("Leaving");
		return customerPhoneNumber;
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the CustomerPhoneNumbers or CustomerPhoneNumbers_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Customer PhoneNumbers by key PhoneCustID
	 * 
	 * @param Customer PhoneNumbers (customerPhoneNumber)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerPhoneNumber customerPhoneNumber,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerPhoneNumbers" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where PhoneCustID =:PhoneCustID AND PhoneTypeCode =:PhoneTypeCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		
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
	 * This method insert new Records into CustomerPhoneNumbers or CustomerPhoneNumbers_Temp.
	 *
	 * save Customer PhoneNumbers 
	 * 
	 * @param Customer PhoneNumbers (customerPhoneNumber)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerPhoneNumber customerPhoneNumber,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerPhoneNumbers");
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:PhoneCustID, :PhoneTypeCode, :PhoneCountryCode,:PhoneAreaCode,:PhoneNumber,:PhoneTypePriority,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return customerPhoneNumber.getId();
	}
	
	/**
	 * This method updates the Record CustomerPhoneNumbers or CustomerPhoneNumbers_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Customer PhoneNumbers by key PhoneCustID and Version
	 * 
	 * @param Customer PhoneNumbers (customerPhoneNumber)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerPhoneNumber customerPhoneNumber,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerPhoneNumbers");
		updateSql.append( StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set PhoneCountryCode = :PhoneCountryCode, PhoneAreaCode = :PhoneAreaCode, PhoneTypePriority =:PhoneTypePriority," );
		updateSql.append(" PhoneNumber = :PhoneNumber," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId" );
		updateSql.append(" Where PhoneCustID =:PhoneCustID AND PhoneTypeCode=:PhoneTypeCode");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Getting List of Objects in Customers By Using CustID
	 */
	public List<CustomerPhoneNumber> getCustomerPhoneNumberByCustomer(final long id,String type) {
		logger.debug("Entering");
		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setPhoneCustID(id);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority," );
		if(type.contains("View")){
			selectSql.append(" lovDescPhoneTypeCodeName, lovDescPhoneCountryName,PhoneRegex," );
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  CustomerPhoneNumbers");
		selectSql.append(StringUtils.trimToEmpty(type) );
		selectSql.append(" Where PhoneCustID =:PhoneCustID ") ; 
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		RowMapper<CustomerPhoneNumber> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerPhoneNumber.class);
		
		List<CustomerPhoneNumber> customerPhoneNumbers = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		logger.debug("Leaving ");
		return customerPhoneNumbers;
	}
	
	public List<CustomerPhoneNumber> getCustomerPhoneNumberByCustomerPhoneType(final long id,String type, String phoneType) {
		logger.debug("Entering");
		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setPhoneCustID(id);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority," );
		if(type.contains("View")){
			selectSql.append(" lovDescPhoneTypeCodeName, lovDescPhoneCountryName," );
			selectSql.append(" lovDescCustRecordType ,lovDescCustCIF,lovDescCustShrtName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  CustomerPhoneNumbers");
		selectSql.append(StringUtils.trimToEmpty(type) );
		selectSql.append("  Where PhoneCustID =:PhoneCustID and PhoneTypeCode =:PhoneTypeCode") ; 
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		RowMapper<CustomerPhoneNumber> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerPhoneNumber.class);

		List<CustomerPhoneNumber> customerPhoneNumbers = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		logger.debug("Leaving ");
		return  customerPhoneNumbers;
	}
	
	/**
	 * This method Deletes the Record from the CustomerPhoneNumbers or CustomerPhoneNumbers_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Customer PhoneNumbers by key PhoneCustID
	 * 
	 * @param Customer PhoneNumbers (customerPhoneNumber)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByCustomer(final long id ,String type) {
		logger.debug("Entering");
		
		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setPhoneCustID(id);
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerPhoneNumbers" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where PhoneCustID =:PhoneCustID ");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	

	/**
	 * Fetch the Customer PhoneNumber By its CustPhoneId
	 * 
	 * @param id
	 * 
	 * 
	 * @return
	 */
	@Override
	public List<CustomerPhoneNumber> getCustomerPhoneNumberById(long id, String type) {
		logger.debug("Entering");
		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setPhoneCustID(id);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority," );
		if(type.contains("View")){
			selectSql.append(" lovDescPhoneTypeCodeName, lovDescPhoneCountryName," );
			selectSql.append(" lovDescCustRecordType ,lovDescCustCIF,lovDescCustShrtName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  CustomerPhoneNumbers");
		selectSql.append(StringUtils.trimToEmpty(type) );
		selectSql.append(" Where PhoneCustID =:PhoneCustID") ; 
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		RowMapper<CustomerPhoneNumber> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerPhoneNumber.class);
		
		List<CustomerPhoneNumber> customerPhoneNumbers = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		logger.debug("Leaving ");
		return  customerPhoneNumbers;
	}

	/**
	 * Fetch current version of the record.
	 * 
	 * @param id
	 * @param typeCode
	 * @return Integer
	 */
	@Override
	public int getVersion(long id, String phoneTypeCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PhoneCustId", id);
		source.addValue("PhoneTypeCode", phoneTypeCode);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM CustomerPhoneNumbers");
		
		selectSql.append(" WHERE PhoneCustId = :PhoneCustId AND PhoneTypeCode = :PhoneTypeCode");

		logger.debug("insertSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	/**
	 * Method for get total number of records from BMTPhoneTypes master table.<br>
	 * 
	 * @param phoneTypeCode
	 * 
	 * @return Integer
	 */
	@Override
	public int getPhoneTypeCodeCount(String phoneTypeCode) {
		logger.debug("Entering");
		
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("PhoneTypeCode", phoneTypeCode);
		
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM BMTPhoneTypes");
		selectSql.append(" WHERE ");
		selectSql.append("PhoneTypeCode= :PhoneTypeCode");
		
		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch(EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		
		return recordCount;
	}
	
}