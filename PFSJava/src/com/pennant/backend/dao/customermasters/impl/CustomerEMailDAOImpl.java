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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CustomerEMail model</b> class.<br>
 * 
 */
public class CustomerEMailDAOImpl extends BasisCodeDAO<CustomerEMail> implements CustomerEMailDAO {

	private static Logger logger = Logger.getLogger(CustomerEMailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new CustomerEMail 
	 * @return CustomerEMail
	 */
	@Override
	public CustomerEMail getCustomerEMail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CustomerEMail");
		CustomerEMail customerEMail= new CustomerEMail();
		if (workFlowDetails!=null){
			customerEMail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return customerEMail;
	}

	/**
	 * This method get the module from method getCustomerEMail() and set the new
	 * record flag as true and return CustomerEMail()
	 * 
	 * @return CustomerEMail
	 */
	@Override
	public CustomerEMail getNewCustomerEMail() {
		logger.debug("Entering");
		CustomerEMail customerEMail = getCustomerEMail();
		customerEMail.setNewRecord(true);
		logger.debug("Leaving");
		return customerEMail;
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
			selectSql.append(" lovDescCustCIF, lovDescCustShrtName,lovDescCustEMailTypeCode,");
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
			selectSql.append(" lovDescCustCIF, lovDescCustShrtName,lovDescCustEMailTypeCode,");
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
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
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
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}	

	/**
	 * This method initialize the Record.
	 * @param CustomerEMail (customerEMail)
	 * @return CustomerEMail
	 */
	@Override
	public void initialize(CustomerEMail customerEMail) {
		super.initialize(customerEMail);
	}
	
	/**
	 * This method refresh the Record.
	 * @param CustomerEMail (customerEMail)
	 * @return void
	 */
	@Override
	public void refresh(CustomerEMail customerEMail) {

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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails= getError("41004", customerEMail.getCustID(),
						customerEMail.getCustEMailTypeCode(), customerEMail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", customerEMail.getCustID(), 
					customerEMail.getCustEMailTypeCode(), customerEMail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
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
	@SuppressWarnings("serial")
	@Override
	public void update(CustomerEMail customerEMail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerEMails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustID = :CustID, CustEMailTypeCode = :CustEMailTypeCode," );
		updateSql.append(" CustEMailPriority = :CustEMailPriority, CustEMail = :CustEMail,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND CustEMailTypeCode =:custEMailTypeCode ");
		if (!type.endsWith("_TEMP")){
			updateSql.append("AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41003", customerEMail.getCustID(), 
					customerEMail.getCustEMailTypeCode(), customerEMail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, long customerId,String emailTypeCode, String userLanguage){
		String[][] parms= new String[2][2]; 
		
		parms[1][0] = String.valueOf(customerId);
		parms[1][1] = emailTypeCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_CustID")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_CustEMailTypeCode")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
				errorId, parms[0],parms[1]), userLanguage);
	}

}