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
 * FileName    		:  CustomerIdentityDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.customermasters.CustomerIdentityDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerIdentity;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CustomerIdentity model</b> class.<br>
 * 
 */
public class CustomerIdentityDAOImpl extends BasisCodeDAO<CustomerIdentity> implements CustomerIdentityDAO {

	private static Logger logger = Logger.getLogger(CustomerIdentityDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new CustomerIdentity 
	 * @return CustomerIdentity
	 */
	@Override
	public CustomerIdentity getCustomerIdentity() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CustomerIdentity");
		CustomerIdentity customerIdentity= new CustomerIdentity();
		if (workFlowDetails!=null){
			customerIdentity.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return customerIdentity;
	}

	/**
	 * This method get the module from method getCustomerIdentity() and set the new record flag as true and return CustomerIdentity()   
	 * @return CustomerIdentity
	 */
	@Override
	public CustomerIdentity getNewCustomerIdentity() {
		logger.debug("Entering");
		CustomerIdentity customerIdentity = getCustomerIdentity();
		customerIdentity.setNewRecord(true);
		logger.debug("Leaving");
		return customerIdentity;
	}

	/**
	 * Fetch the Record  Customer Identity Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerIdentity
	 */
	@Override
	public CustomerIdentity getCustomerIdentityByID(final long id,String idType, String type) {
		logger.debug("Entering");
		CustomerIdentity customerIdentity = getCustomerIdentity();
		customerIdentity.setId(id);
		customerIdentity.setIdType(idType);
		
		StringBuilder selectSql = new StringBuilder("SELECT IdCustID,IdLocation, IdExpiresOn, IdIssuedOn,IdIssueCountry, IdRef, IdIssuedBy, IdType,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustRecordType,lovDescCustCIF, lovDescCustShrtName,lovDescIdTypeName, lovDescIdIssueCountryName, ");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  CustIdentities"+StringUtils.trimToEmpty(type));
		selectSql.append(" Where IdCustID =:idCustID AND IdType =:idType" );
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIdentity);
		RowMapper<CustomerIdentity> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIdentity.class);
		
		try{
			customerIdentity = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			customerIdentity = null;
		}
		logger.debug("Leaving");
		return customerIdentity;
	}
	
	/**
	 * This method initialize the Record.
	 * @param CustomerIdentity (customerIdentity)
 	 * @return CustomerIdentity
	 */
	@Override
	public void initialize(CustomerIdentity customerIdentity) {
		super.initialize(customerIdentity);
	}
	
	/**
	 * This method refresh the Record.
	 * @param CustomerIdentity (customerIdentity)
 	 * @return void
	 */
	@Override
	public void refresh(CustomerIdentity customerIdentity) {
		
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the CustIdentities or CustIdentities_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Customer Identity Details by key IdCustID
	 * 
	 * @param Customer Identity Details (customerIdentity)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(CustomerIdentity customerIdentity,String type) {
		logger.debug("Entering");
		
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder("Delete From CustIdentities" + StringUtils.trimToEmpty(type));
		deleteSql.append(" Where IdCustID =:IdCustID AND IdType =:IdType");
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIdentity);
		
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",customerIdentity.getIdCustID(),
						customerIdentity.getIdType(), customerIdentity.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",customerIdentity.getIdCustID(),
					customerIdentity.getIdType(), customerIdentity.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving delete Method");
	}
	
	/**
	 * This method insert new Records into CustIdentities or CustIdentities_Temp.
	 *
	 * save Customer Identity Details 
	 * 
	 * @param Customer Identity Details (customerIdentity)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerIdentity customerIdentity,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into CustIdentities" + StringUtils.trimToEmpty(type) );
		insertSql.append(" (IdCustID, IdType, IdIssuedBy, IdRef, IdIssueCountry, IdIssuedOn, IdExpiresOn, IdLocation," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:IdCustID, :IdType, :IdIssuedBy, :IdRef, :IdIssueCountry, :IdIssuedOn, :IdExpiresOn, :IdLocation," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIdentity);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return customerIdentity.getId();
	}
	
	/**
	 * This method updates the Record CustIdentities or CustIdentities_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Customer Identity Details by key IdCustID and Version
	 * 
	 * @param Customer Identity Details (customerIdentity)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(CustomerIdentity customerIdentity,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update CustIdentities" + StringUtils.trimToEmpty(type));
		updateSql.append(" Set IdCustID = :IdCustID, IdType = :IdType, IdIssuedBy = :IdIssuedBy, IdRef = :IdRef," );
		updateSql.append(" IdIssueCountry = :IdIssueCountry, IdIssuedOn = :IdIssuedOn, IdExpiresOn = :IdExpiresOn, IdLocation = :IdLocation" );
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append("RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where IdCustID =:IdCustID AND IdType =:IdType");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIdentity);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails= getError("41004",customerIdentity.getIdCustID(),customerIdentity.getIdType(), customerIdentity.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, long customerID,String idType, String userLanguage){
		String[][] parms= new String[2][1]; 
		
		parms[1][0] = String.valueOf(customerID);
		parms[1][1] = idType;

		parms[0][0] = PennantJavaUtil.getLabel("label_IdCustID")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_IdType")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}