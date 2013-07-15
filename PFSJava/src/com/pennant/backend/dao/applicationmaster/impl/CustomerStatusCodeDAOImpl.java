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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CustomerStatusCode model</b> class.<br>
 * 
 */
public class CustomerStatusCodeDAOImpl extends BasisCodeDAO<CustomerStatusCode>	implements CustomerStatusCodeDAO {

	private static Logger logger = Logger.getLogger(CustomerStatusCodeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new CustomerStatusCode
	 * 
	 * @return CustomerStatusCode
	 */
	@Override
	public CustomerStatusCode getCustomerStatusCode() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerStatusCode");
		CustomerStatusCode customerStatusCode = new CustomerStatusCode();
		if (workFlowDetails != null) {
			customerStatusCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return customerStatusCode;
	}

	/**
	 * This method get the module from method getCustomerStatusCode() and set the
	 * new record flag as true and return CustomerStatusCode()
	 * 
	 * @return CustomerStatusCode
	 */
	@Override
	public CustomerStatusCode getNewCustomerStatusCode() {
		logger.debug("Entering");
		CustomerStatusCode customerStatusCode = getCustomerStatusCode();
		customerStatusCode.setNewRecord(true);
		logger.debug("Leaving");
		return customerStatusCode;
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
		CustomerStatusCode customerStatusCode = getCustomerStatusCode();
		customerStatusCode.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("SELECT CustStsCode, CustStsDescription, CustStsIsActive,CustStsOrder," );
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
			logger.error(e);
			customerStatusCode = null;
		}
		logger.debug("Leaving");
		return customerStatusCode;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param CustomerStatusCode
	 *            (customerStatusCode)
	 * @return CustomerStatusCode
	 */
	@Override
	public void initialize(CustomerStatusCode customerStatusCode) {
		super.initialize(customerStatusCode);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param CustomerStatusCode
	 *            (customerStatusCode)
	 * @return void
	 */
	@Override
	public void refresh(CustomerStatusCode customerStatusCode) {

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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails= getError("41004", customerStatusCode.getCustStsCode(), customerStatusCode.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", customerStatusCode.getCustStsCode(), customerStatusCode.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
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
		insertSql.append(" (CustStsCode, CustStsDescription, CustStsIsActive,CustStsOrder," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:CustStsCode, :CustStsDescription, :CustStsIsActive, :CustStsOrder, " );
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
	@SuppressWarnings("serial")
	@Override
	public void update(CustomerStatusCode customerStatusCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTCustStatusCodes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustStsCode = :CustStsCode, CustStsDescription = :CustStsDescription," );
		updateSql.append(" CustStsIsActive = :CustStsIsActive,CustStsOrder = :CustStsOrder ," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where CustStsCode =:CustStsCode ");
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerStatusCode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails= getError("41003", customerStatusCode.getCustStsCode(), customerStatusCode.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
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
	private ErrorDetails  getError(String errorId, String custStsCode,String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = custStsCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_CustStsCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}