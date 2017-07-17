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
 * FileName    		:  CustomerCategoryDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.applicationmaster.CustomerCategoryDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustomerCategory model</b> class.<br>
 * 
 */
public class CustomerCategoryDAOImpl extends BasisCodeDAO<CustomerCategory>	implements CustomerCategoryDAO {

	private static Logger logger = Logger.getLogger(CustomerCategoryDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerCategoryDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer Categories details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerCategory
	 */
	@Override
	public CustomerCategory getCustomerCategoryById(final String id, String type) {
		logger.debug("Entering");
		CustomerCategory customerCategory = new CustomerCategory();
		customerCategory.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("SELECT CustCtgCode, CustCtgDesc,CustCtgType, CustCtgIsActive," );
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTCustCategories");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustCtgCode =:CustCtgCode") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerCategory);
		RowMapper<CustomerCategory> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerCategory.class);

		try {
			customerCategory = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			customerCategory = null;
		}
		logger.debug("Leaving");
		return customerCategory;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTCustCategories or
	 * BMTCustCategories_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Customer Categories by key
	 * CustCtgCode
	 * 
	 * @param Customer
	 *            Categories (customerCategory)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(CustomerCategory customerCategory, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTCustCategories");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustCtgCode =:CustCtgCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerCategory);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTCustCategories or
	 * BMTCustCategories_Temp.
	 * 
	 * save Customer Categories
	 * 
	 * @param Customer
	 *            Categories (customerCategory)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(CustomerCategory customerCategory, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTCustCategories");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustCtgCode, CustCtgDesc,CustCtgType, CustCtgIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:CustCtgCode, :CustCtgDesc, :CustCtgType,:CustCtgIsActive, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerCategory);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerCategory.getId();
	}

	/**
	 * This method updates the Record BMTCustCategories or
	 * BMTCustCategories_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer Categories by key
	 * CustCtgCode and Version
	 * 
	 * @param Customer
	 *            Categories (customerCategory)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerCategory customerCategory, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTCustCategories");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustCtgDesc = :CustCtgDesc,CustCtgType = :CustCtgType,");
		updateSql.append(" CustCtgIsActive = :CustCtgIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where CustCtgCode =:CustCtgCode ");
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerCategory);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}