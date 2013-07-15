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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CustomerAddres model</b> class.<br>
 * 
 */
public class CustomerAddresDAOImpl extends BasisCodeDAO<CustomerAddres> implements CustomerAddresDAO {

	private static Logger logger = Logger.getLogger(CustomerAddresDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new CustomerAddres
	 * 
	 * @return CustomerAddres
	 */
	@Override
	public CustomerAddres getCustomerAddres() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerAddres");
		CustomerAddres customerAddres = new CustomerAddres();
		if (workFlowDetails != null) {
			customerAddres.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return customerAddres;
	}

	/**
	 * This method get the module from method getCustomerAddres() and set the
	 * new record flag as true and return CustomerAddres()
	 * 
	 * @return CustomerAddres
	 */
	@Override
	public CustomerAddres getNewCustomerAddres() {
		logger.debug("Entering");
		CustomerAddres customerAddres = getCustomerAddres();
		customerAddres.setNewRecord(true);
		logger.debug("Leaving");
		return customerAddres;
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
		CustomerAddres customerAddres = getCustomerAddres();
		customerAddres.setId(id);
		customerAddres.setCustAddrType(addType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet," );
		selectSql.append(" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince," );
		selectSql.append(" CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustAddrTypeName, lovDescCustAddrCityName," );
			selectSql.append(" lovDescCustAddrProvinceName, lovDescCustAddrCountryName," );
			selectSql.append(" lovDescCustRecordType, lovDescCustCIF, lovDescCustShrtName,");
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
		CustomerAddres customerAddres = getCustomerAddres();
		customerAddres.setId(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet," );
		selectSql.append(" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince," );
		selectSql.append(" CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustAddrTypeName, lovDescCustAddrCityName," );
			selectSql.append(" lovDescCustAddrProvinceName, lovDescCustAddrCountryName," );
			selectSql.append(" lovDescCustRecordType, lovDescCustCIF, lovDescCustShrtName,");
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

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),beanParameters, typeRowMapper);
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param CustomerAddres
	 *            (customerAddres)
	 * @return CustomerAddres
	 */
	@Override
	public void initialize(CustomerAddres customerAddres) {
		super.initialize(customerAddres);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param CustomerAddres
	 *            (customerAddres)
	 * @return void
	 */
	@Override
	public void refresh(CustomerAddres customerAddres) {

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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails = getError("41003", customerAddres.getCustID(),
						customerAddres.getCustAddrType(), customerAddres.getUserDetails().getUsrLanguage()); 
				throw new DataAccessException(errorDetails.getError()) { };
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", customerAddres.getCustID(),
					customerAddres.getCustAddrType(), customerAddres.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Deletion of Customer Related List of CustomerAddress for the Customer
	 */
	public void deleteByCustomer(final long customerId,String type) {
		logger.debug("Entering");
		
		CustomerAddres customerAddres = getCustomerAddres();
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
		insertSql.append(" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCountry, CustAddrProvince," );
		insertSql.append(" CustAddrCity, CustAddrZIP, CustAddrPhone,CustAddrFrom,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId," );
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustAddrType, :CustAddrHNbr, :CustFlatNbr, :CustAddrStreet,");
		insertSql.append(" :CustAddrLine1, :CustAddrLine2, :CustPOBox, :CustAddrCountry, :CustAddrProvince,");
		insertSql.append(" :CustAddrCity, :CustAddrZIP, :CustAddrPhone, :CustAddrFrom,");
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
	@SuppressWarnings("serial")
	@Override
	public void update(CustomerAddres customerAddres, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerAddresses");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustID = :CustID, CustAddrType = :CustAddrType," );
		updateSql.append(" CustAddrHNbr = :CustAddrHNbr, CustFlatNbr = :CustFlatNbr," );
		updateSql.append(" CustAddrStreet = :CustAddrStreet, CustAddrLine1 = :CustAddrLine1," );
		updateSql.append(" CustAddrLine2 = :CustAddrLine2, CustPOBox = :CustPOBox," );
		updateSql.append(" CustAddrCountry = :CustAddrCountry, CustAddrProvince = :CustAddrProvince," );
		updateSql.append(" CustAddrCity = :CustAddrCity, CustAddrZIP = :CustAddrZIP," );
		updateSql.append(" CustAddrPhone = :CustAddrPhone,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND CustAddrType =:custAddrType");

		if (!type.endsWith("_TEMP")) {
			updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails= getError("41004", customerAddres.getCustID(), 
					customerAddres.getCustAddrType(), customerAddres.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, long customerID,String addrType, String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = String.valueOf(customerID);
		parms[1][1] = addrType;

		parms[0][0] = PennantJavaUtil.getLabel("label_CustID")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_CustAddrType")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
				errorId, parms[0],parms[1]), userLanguage);
	}
}