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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>CustomerExtLiability model</b> class.<br>
 * 
 */
public class CustomerExtLiabilityDAOImpl extends BasisCodeDAO<CustomerExtLiability> implements CustomerExtLiabilityDAO {

	private static Logger logger = Logger.getLogger(CustomerExtLiabilityDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
			selectSql.append(" lovDescBankName,lovDescFinType,lovDescCustCIF,lovDescCustShrtName,");
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
			selectSql.append(" lovDescBankName,lovDescFinType,lovDescCustCIF,lovDescCustShrtName,");
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
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails= getError("41004", customerExtLiability.getCustID(),
						String.valueOf(customerExtLiability.getLiabilitySeq()), customerExtLiability.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", customerExtLiability.getCustID(), 
					String.valueOf(customerExtLiability.getLiabilitySeq()), customerExtLiability.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
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
	@SuppressWarnings("serial")
	@Override
	public void update(CustomerExtLiability customerExtLiability,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerExtLiability");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustID = :CustID, LiabilitySeq = :LiabilitySeq," );
		updateSql.append(" FinDate = :FinDate, FinType = :FinType, BankName = :BankName, OriginalAmount = :OriginalAmount,");
		updateSql.append(" InstalmentAmount = :InstalmentAmount, OutStandingBal = :OutStandingBal, FinStatus = :FinStatus,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND LiabilitySeq = :LiabilitySeq ");
		if (!type.endsWith("_TEMP")){
			updateSql.append("AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerExtLiability);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41003", customerExtLiability.getCustID(), 
					String.valueOf(customerExtLiability.getLiabilitySeq()), customerExtLiability.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, long customerId,String emailTypeCode, String userLanguage){
		String[][] parms= new String[2][2]; 
		
		parms[1][0] = String.valueOf(customerId);
		parms[1][1] = emailTypeCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_CustID")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_LiabilitySeq")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
				errorId, parms[0],parms[1]), userLanguage);
	}

}