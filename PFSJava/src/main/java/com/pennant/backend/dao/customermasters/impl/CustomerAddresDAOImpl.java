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
 * FileName    		:  CustomerAddresDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustomerAddres model</b> class.<br>
 * 
 */
public class CustomerAddresDAOImpl extends BasisCodeDAO<CustomerAddres> implements CustomerAddresDAO {
	private static Logger logger = Logger.getLogger(CustomerAddresDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerAddresDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer Address details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerAddres
	 */
	@Override
	public CustomerAddres getCustomerAddresById(final long id, String addType, String type) {
		logger.debug("Entering");
		CustomerAddres customerAddres = new CustomerAddres();
		customerAddres.setId(id);
		customerAddres.setCustAddrType(addType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet," );
		selectSql.append(" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince,CustAddrPriority," );
		selectSql.append(" CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom,TypeOfResidence,CustAddrLine3,CustAddrLine4,CustDistrict,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustAddrTypeName, lovDescCustAddrCityName," );
			selectSql.append(" lovDescCustAddrProvinceName, lovDescCustAddrCountryName," );
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM CustomerAddresses");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND CustAddrType = :custAddrType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		RowMapper<CustomerAddres> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerAddres.class);

		try {
			customerAddres = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerAddres = null;
		}
		logger.debug("Leaving");
		return customerAddres;
	}

	/** 
	 * Method For getting List of Customer related Addresses for Customer
	 */
	public List<CustomerAddres> getCustomerAddresByCustomer(final long custId, String type) {
		logger.debug("Entering");
		CustomerAddres customerAddres = new CustomerAddres();
		customerAddres.setId(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet," );
		selectSql.append(" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince,CustAddrPriority," );
		selectSql.append(" CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom,TypeOfResidence,CustAddrLine3,CustAddrLine4,CustDistrict,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustAddrTypeName, lovDescCustAddrCityName," );
			selectSql.append(" lovDescCustAddrProvinceName, lovDescCustAddrCountryName,lovDescCustAddrZip," );
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM CustomerAddresses");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		RowMapper<CustomerAddres> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerAddres.class);

		List<CustomerAddres> customerAddresses = this.namedParameterJdbcTemplate.query(selectSql.toString(),beanParameters, typeRowMapper); 
		logger.debug("Leaving");
		return customerAddresses;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the CustomerAddresses or
	 * CustomerAddresses_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Customer Address by key
	 * CustID
	 * 
	 * @param Customer
	 *            Address (customerAddres)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerAddres customerAddres, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerAddresses");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID AND CustAddrType =:custAddrType ");
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);

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
	 * Method for Deletion of Customer Related List of CustomerAddress for the Customer
	 */
	public void deleteByCustomer(final long customerId,String type) {
		logger.debug("Entering");
		
		CustomerAddres customerAddres = new CustomerAddres();
		customerAddres.setId(customerId);
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerAddresses");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CustomerAddresses or
	 * CustomerAddresses_Temp.
	 * 
	 * save Customer Address
	 * 
	 * @param Customer
	 *            Address (customerAddres)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerAddres customerAddres, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into CustomerAddresses");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet," );
		insertSql.append(" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCountry, CustAddrProvince, CustAddrPriority," );
		insertSql.append(" CustAddrCity, CustAddrZIP, CustAddrPhone,CustAddrFrom,TypeOfResidence,CustAddrLine3,CustAddrLine4,CustDistrict,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId," );
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustAddrType, :CustAddrHNbr, :CustFlatNbr, :CustAddrStreet,");
		insertSql.append(" :CustAddrLine1, :CustAddrLine2, :CustPOBox, :CustAddrCountry, :CustAddrProvince, :CustAddrPriority,");
		insertSql.append(" :CustAddrCity, :CustAddrZIP, :CustAddrPhone, :CustAddrFrom,:TypeOfResidence,:CustAddrLine3,:CustAddrLine4,:CustDistrict,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerAddres.getId();
	}

	/**
	 * This method updates the Record CustomerAddresses or
	 * CustomerAddresses_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer Address by key
	 * CustID and Version
	 * 
	 * @param Customer
	 *            Address (customerAddres)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerAddres customerAddres, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerAddresses");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustAddrHNbr = :CustAddrHNbr, CustFlatNbr = :CustFlatNbr," );
		updateSql.append(" CustAddrStreet = :CustAddrStreet, CustAddrLine1 = :CustAddrLine1," );
		updateSql.append(" CustAddrLine2 = :CustAddrLine2, CustPOBox = :CustPOBox," );
		updateSql.append(" CustAddrCountry = :CustAddrCountry, CustAddrProvince = :CustAddrProvince, CustAddrPriority = :CustAddrPriority, " );
		updateSql.append(" CustAddrCity = :CustAddrCity, CustAddrZIP = :CustAddrZIP," );
		updateSql.append(" CustAddrPhone = :CustAddrPhone,TypeOfResidence = :TypeOfResidence,CustAddrLine3=:CustAddrLine3,CustAddrLine4=:CustAddrLine4,CustDistrict=:CustDistrict,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND CustAddrType =:custAddrType");

		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for get total number of records from BMTAddressTypes master table.<br>
	 * 
	 * @param addrType
	 * 
	 * @return Integer
	 */
	@Override
	public int getAddrTypeCount(String addrType) {
		logger.debug("Entering");
		
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("AddrTypeCode", addrType);
		
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM BMTAddressTypes");
		selectSql.append(" WHERE ");
		selectSql.append("AddrTypeCode= :AddrTypeCode");
		
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
	
	/**
	 * Method for get total number of records from BMTAddressTypes master table.<br>
	 * 
	 * @param addrType
	 * 
	 * @return Integer
	 */
	@Override
	public int getcustAddressCount(String addrType) {
		logger.debug("Entering");
		
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("CustAddrType", addrType);
		
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM CustomerAddresses");
		selectSql.append(" WHERE ");
		selectSql.append("CustAddrType= :CustAddrType");
		
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

	/**
	 * Fetch current version of the record.
	 * 
	 * @param id
	 * @param typeCode
	 * @return Integer
	 */
	@Override
	public int getVersion(long id, String addrType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", id);
		source.addValue("CustAddrType", addrType);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM CustomerAddresses");
		
		selectSql.append(" WHERE CustId = :CustId AND CustAddrType = :CustAddrType");

		logger.debug("insertSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}
	
	@Override
	public boolean isServiceable(String pinCode) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("pinCode", pinCode);
		
		StringBuilder selectSql = new StringBuilder("SELECT serviceable");
		selectSql.append(" From PinCodes_View ");
		selectSql.append(" Where pinCode=:pinCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		int rcdCount =  this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		
		logger.debug("Leaving");
		return rcdCount > 0 ? true : false;
	}
}