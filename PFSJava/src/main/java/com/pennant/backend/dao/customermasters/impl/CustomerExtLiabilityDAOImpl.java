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
 * FileName    		:  CustomerExtLiabilityDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustomerExtLiability model</b> class.<br>
 * 
 */
public class CustomerExtLiabilityDAOImpl extends BasisCodeDAO<CustomerExtLiability> implements CustomerExtLiabilityDAO {

	private static Logger logger = Logger.getLogger(CustomerExtLiabilityDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerExtLiabilityDAOImpl() {
		super();
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * Fetch the Record  Customer EMails details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerExtLiability
	 */
	@Override
	public CustomerExtLiability getCustomerExtLiabilityById(final long id, int liabilitySeq,String type) {
		logger.debug("Entering");
		CustomerExtLiability customerExtLiability = new CustomerExtLiability();
		customerExtLiability.setId(id);
		customerExtLiability.setLiabilitySeq(liabilitySeq);
		StringBuilder selectSql = new StringBuilder();	
		selectSql.append(" SELECT CustID, LiabilitySeq, FinDate, FinType, BankName,  ");
		selectSql.append(" OriginalAmount, InstalmentAmount, OutStandingBal, FinStatus, ");
		if(type.contains("View")){
			selectSql.append(" lovDescBankName,lovDescFinType,lovDescFinStatus,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerExtLiability");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND LiabilitySeq = :LiabilitySeq");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerExtLiability);
		RowMapper<CustomerExtLiability> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerExtLiability.class);

		try{
			customerExtLiability = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerExtLiability = null;
		}
		logger.debug("Leaving");
		return customerExtLiability;
	}

	/**
	 * Method to return the customer email based on given customer id
	 * */
	@Override
	public List<CustomerExtLiability> getExtLiabilityByCustomer(final long id,String type) {
		logger.debug("Entering");
		CustomerExtLiability customerExtLiability = new CustomerExtLiability();
		customerExtLiability.setId(id);
		
		StringBuilder selectSql = new StringBuilder();	
		selectSql.append(" SELECT CustID, LiabilitySeq, FinDate, FinType, BankName,  ");
		selectSql.append(" OriginalAmount, InstalmentAmount, OutStandingBal, FinStatus, ");
		if(type.contains("View")){
			selectSql.append(" lovDescBankName,lovDescFinType,lovDescFinStatus,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerExtLiability");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerExtLiability);
		RowMapper<CustomerExtLiability> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerExtLiability.class);
		
		List <CustomerExtLiability> custExtLiabilities = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		logger.debug("Leaving");
		return custExtLiabilities;
	}	
	
	
	/**
	 * This method Deletes the Record from the CustomerExtLiability or CustomerExtLiability_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Customer EMails by key CustID
	 * 
	 * @param Customer EMails (customerExtLiability)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerExtLiability customerExtLiability,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From CustomerExtLiability");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID AND LiabilitySeq = :LiabilitySeq");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerExtLiability);

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
	 * This method Deletes the Record from the CustomerExtLiability or CustomerExtLiability_Temp for the Customer.
	 * 
	 * @param Customer EMails (customerExtLiability)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByCustomer(long custID,String type) {
		logger.debug("Entering");

		CustomerExtLiability customerExtLiability = new CustomerExtLiability();
		customerExtLiability.setCustID(custID);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerExtLiability");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerExtLiability);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CustomerExtLiability or CustomerExtLiability_Temp.
	 *
	 * save Customer EMails 
	 * 
	 * @param Customer EMails (customerExtLiability)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerExtLiability customerExtLiability,String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerExtLiability");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, LiabilitySeq, FinDate, FinType, BankName, OriginalAmount, InstalmentAmount, OutStandingBal, FinStatus," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :LiabilitySeq, :FinDate, :FinType, :BankName, :OriginalAmount, :InstalmentAmount, :OutStandingBal, :FinStatus,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerExtLiability);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerExtLiability.getId();
	}

	/**
	 * This method updates the Record CustomerExtLiability or CustomerExtLiability_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Customer EMails by key CustID and Version
	 * 
	 * @param Customer EMails (customerExtLiability)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerExtLiability customerExtLiability,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerExtLiability");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinDate = :FinDate, FinType = :FinType, BankName = :BankName, OriginalAmount = :OriginalAmount,");
		updateSql.append(" InstalmentAmount = :InstalmentAmount, OutStandingBal = :OutStandingBal, FinStatus = :FinStatus,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND LiabilitySeq = :LiabilitySeq ");
		if (!type.endsWith("_Temp")){
			updateSql.append("AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerExtLiability);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	

	/**
	 * Method for get total number of records from BMTBankDetail master table.<br>
	 * 
	 * @param bankCode
	 * 
	 * @return Integer
	 */
	@Override
	public int getBankNameCount(String bankCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BankCode", bankCode);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM BMTBankDetail");
		selectSql.append(" WHERE ");
		selectSql.append("BankCode= :BankCode");

		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount;
	}

	/**
	 * Method for get total number of records from OtherBankFinanceType master table.<br>
	 * 
	 * @param finType
	 * 
	 * @return Integer
	 */
	@Override
	public int getFinTypeCount(String finType) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM OtherBankFinanceType");
		selectSql.append(" WHERE ");
		selectSql.append("FinType= :FinType");
		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount;
	}
	
	/**
	 * Method for get total number of records from BMTCustStatusCodes master table.<br>
	 * 
	 * @param finStatus
	 * 
	 * @return Integer
	 */
	@Override
	public int getFinStatusCount(String finStatus) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustStsCode", finStatus);
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM BMTCustStatusCodes");
		selectSql.append(" WHERE ");
		selectSql.append("CustStsCode= :CustStsCode");
		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
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
	public int getVersion(long id, int liabilitySeq) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", id);
		source.addValue("LiabilitySeq", liabilitySeq);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM CustomerExtLiability");

		selectSql.append(" WHERE CustId = :CustId AND LiabilitySeq = :LiabilitySeq");

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
	@Override
	public int getCustomerExtLiabilityByBank(String bankCode, String type) {
		CustomerExtLiability customerExtLiability = new CustomerExtLiability();
		customerExtLiability.setBankName(bankCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From CustomerExtLiability");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankName =:BankName");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerExtLiability);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
}