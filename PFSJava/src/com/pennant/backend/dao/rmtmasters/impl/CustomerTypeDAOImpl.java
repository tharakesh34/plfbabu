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
 * FileName    		:  CustomerTypeDAOImpl.java                                                   * 	  
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

package com.pennant.backend.dao.rmtmasters.impl;

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
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rmtmasters.CustomerTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CustomerType model</b> class.<br>
 * 
 */
public class CustomerTypeDAOImpl extends BasisCodeDAO<CustomerType> implements
		CustomerTypeDAO {

	private static Logger logger = Logger.getLogger(CustomerTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new CustomerType
	 * 
	 * @return CustomerType
	 */
	@Override
	public CustomerType getCustomerType() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil
				.getWorkFlowDetails("CustomerType");
		CustomerType customerType = new CustomerType();
		if (workFlowDetails != null) {
			customerType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return customerType;
	}

	/**
	 * This method get the module from method getCustomerType() and set the new
	 * record flag as true and return CustomerType()
	 * 
	 * @return CustomerType
	 */
	@Override
	public CustomerType getNewCustomerType() {
		logger.debug("Entering");
		CustomerType customerType = getCustomerType();
		customerType.setNewRecord(true);
		logger.debug("Leaving");
		return customerType;
	}

	/**
	 * Fetch the Record Customer Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerType
	 */
	@Override
	public CustomerType getCustomerTypeById(final String id, String type) {
		logger.debug("Entering");

		CustomerType customerType = new CustomerType();
		customerType.setId(id);

		StringBuilder selectSql = new StringBuilder("Select CustTypeCode, CustTypeCtg, CustTypeDesc,CustTypeIsActive," );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" From RMTCustTypes");
		selectSql.append(StringUtils.trimToEmpty(type) );
		selectSql.append(" Where CustTypeCode =:CustTypeCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				customerType);
		RowMapper<CustomerType> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerType.class);

		try {
			customerType = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			customerType = null;
		}
		logger.debug("Leaving");
		return customerType;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param CustomerType
	 *            (customerType)
	 * @return CustomerType
	 */
	@Override
	public void initialize(CustomerType customerType) {
		super.initialize(customerType);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param CustomerType
	 *            (customerType)
	 * @return void
	 */
	@Override
	public void refresh(CustomerType customerType) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTCustTypes or
	 * RMTCustTypes_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete Customer Types by key CustTypeCode
	 * 
	 * @param Customer
	 *            Types (customerType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(CustomerType customerType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTCustTypes");
		deleteSql.append(StringUtils.trimToEmpty(type)); 
		deleteSql.append(" Where CustTypeCode =:CustTypeCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				customerType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",customerType.getCustTypeCode(),
						customerType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails=getError("41006",customerType.getCustTypeCode(),
					customerType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTCustTypes or RMTCustTypes_Temp.
	 * 
	 * save Customer Types
	 * 
	 * @param Customer
	 *            Types (customerType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(CustomerType customerType, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into RMTCustTypes");
		insertSql.append(StringUtils.trimToEmpty(type)); 
		insertSql.append(" (CustTypeCode, CustTypeCtg, CustTypeDesc, CustTypeIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:CustTypeCode, :CustTypeCtg, :CustTypeDesc, :CustTypeIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerType.getId();
	}

	/**
	 * This method updates the Record RMTCustTypes or RMTCustTypes_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Customer Types by key CustTypeCode and Version
	 * 
	 * @param Customer
	 *            Types (customerType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(CustomerType customerType, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update RMTCustTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustTypeCode = :CustTypeCode, CustTypeCtg= :CustTypeCtg, CustTypeDesc = :CustTypeDesc," );
		updateSql.append(" CustTypeIsActive = :CustTypeIsActive," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId"); 
		updateSql.append(" Where CustTypeCode =:CustTypeCode");

		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails=getError("41004",customerType.getCustTypeCode(),
					customerType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId,String custTypeCode, String userLanguage){
		String[][] parms= new String[2][1]; 
		
		parms[1][0] = custTypeCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_CustTypeCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
				errorId, parms[0],parms[1]), userLanguage);
	}
}