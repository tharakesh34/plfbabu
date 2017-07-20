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
 * FileName    		:  CustomerChequeInfoDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustomerChequeInfo model</b> class.<br>
 * 
 */
public class CustomerChequeInfoDAOImpl extends BasisCodeDAO<CustomerChequeInfo> implements CustomerChequeInfoDAO {

	private static Logger logger = Logger.getLogger(CustomerChequeInfoDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerChequeInfoDAOImpl() {
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
	 * @return CustomerChequeInfo
	 */
	@Override
	public CustomerChequeInfo getCustomerChequeInfoById(final long id, int chequeSeq,String type) {
		logger.debug("Entering");
		CustomerChequeInfo customerChequeInfo = new CustomerChequeInfo();
		customerChequeInfo.setId(id);
		customerChequeInfo.setChequeSeq(chequeSeq);
		StringBuilder selectSql = new StringBuilder();	
		selectSql.append(" SELECT CustID, ChequeSeq, MonthYear, TotChequePayment, Salary, Debits, ReturnChequeAmt, ReturnChequeCount, Remarks,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustCIF,lovDescCustShrtName,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerChequeInfo");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND ChequeSeq = :ChequeSeq");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerChequeInfo);
		RowMapper<CustomerChequeInfo> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerChequeInfo.class);

		try{
			customerChequeInfo = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerChequeInfo = null;
		}
		logger.debug("Leaving");
		return customerChequeInfo;
	}

	/**
	 * Method to return the customer email based on given customer id
	 * */
	@Override
	public List<CustomerChequeInfo> getChequeInfoByCustomer(final long id,String type) {
		logger.debug("Entering");
		CustomerChequeInfo customerChequeInfo = new CustomerChequeInfo();
		customerChequeInfo.setId(id);
		
		StringBuilder selectSql = new StringBuilder();	
		selectSql.append(" SELECT CustID, ChequeSeq, MonthYear, TotChequePayment, Salary, Debits, ReturnChequeAmt, ReturnChequeCount, Remarks,");
		if(type.contains("View")){
			selectSql.append(" ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerChequeInfo");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerChequeInfo);
		RowMapper<CustomerChequeInfo> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustomerChequeInfo.class);
		
		List<CustomerChequeInfo> custCheques = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		logger.debug("Leaving");
		return custCheques;
	}	
	
	
	/**
	 * This method Deletes the Record from the CustomerChequeInfo or CustomerChequeInfo_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Customer EMails by key CustID
	 * 
	 * @param Customer EMails (customerChequeInfo)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerChequeInfo customerChequeInfo,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From CustomerChequeInfo");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID AND ChequeSeq = :ChequeSeq");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerChequeInfo);

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
	 * This method Deletes the Record from the CustomerChequeInfo or CustomerChequeInfo_Temp for the Customer.
	 * 
	 * @param Customer EMails (customerChequeInfo)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByCustomer(long custID,String type) {
		logger.debug("Entering");

		CustomerChequeInfo customerChequeInfo = new CustomerChequeInfo();
		customerChequeInfo.setCustID(custID);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerChequeInfo");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerChequeInfo);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CustomerChequeInfo or CustomerChequeInfo_Temp.
	 *
	 * save Customer EMails 
	 * 
	 * @param Customer EMails (customerChequeInfo)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerChequeInfo customerChequeInfo,String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerChequeInfo");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, ChequeSeq, MonthYear, TotChequePayment, Salary, Debits,  ReturnChequeAmt, ReturnChequeCount, Remarks," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :ChequeSeq, :MonthYear, :TotChequePayment, :Salary, :Debits, :ReturnChequeAmt, :ReturnChequeCount, :Remarks," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerChequeInfo);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerChequeInfo.getId();
	}

	/**
	 * This method updates the Record CustomerChequeInfo or CustomerChequeInfo_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Customer EMails by key CustID and Version
	 * 
	 * @param Customer EMails (customerChequeInfo)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerChequeInfo customerChequeInfo,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerChequeInfo");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set MonthYear = :MonthYear, TotChequePayment = :TotChequePayment, Salary = :Salary, Debits = :Debits,");
		updateSql.append(" ReturnChequeAmt = :ReturnChequeAmt, ReturnChequeCount = :ReturnChequeCount, Remarks = :Remarks," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND ChequeSeq =:ChequeSeq ");
		if (!type.endsWith("_Temp")){
			updateSql.append("AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerChequeInfo);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch current version of the record.
	 * 
	 * @param id
	 * @param chequeSeq
	 * @return Integer
	 */
	@Override
	public int getVersion(long id, int chequeSeq) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", id);
		source.addValue("ChequeSeq", chequeSeq);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM CustomerChequeInfo");

		selectSql.append(" WHERE CustId = :CustId AND ChequeSeq = :ChequeSeq");

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