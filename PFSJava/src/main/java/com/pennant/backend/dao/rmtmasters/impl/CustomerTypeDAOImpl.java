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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rmtmasters.CustomerTypeDAO;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>CustomerType model</b> class.<br>
 * 
 */
public class CustomerTypeDAOImpl extends BasisCodeDAO<CustomerType> implements CustomerTypeDAO {

	private static Logger logger = Logger.getLogger(CustomerTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerTypeDAOImpl() {
		super();
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
		logger.debug(Literal.ENTERING);

		CustomerType customerType = new CustomerType();
		customerType.setId(id);

		StringBuilder selectSql = new StringBuilder("Select CustTypeCode, CustTypeCtg, CustTypeDesc,CustTypeIsActive,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTCustTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustTypeCode =:CustTypeCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerType);
		RowMapper<CustomerType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerType.class);

		try {
			customerType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			customerType = null;
		}
		logger.debug("Leaving");
		return customerType;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTCustTypes or RMTCustTypes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Customer Types by key CustTypeCode
	 * 
	 * @param Customer
	 *            Types (customerType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(CustomerType customerType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTCustTypes");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where CustTypeCode =:CustTypeCode");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
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
	public String save(CustomerType customerType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder("Insert Into RMTCustTypes");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (CustTypeCode, CustTypeCtg, CustTypeDesc, CustTypeIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustTypeCode, :CustTypeCtg, :CustTypeDesc, :CustTypeIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerType);
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return customerType.getId();
	}

	/**
	 * This method updates the Record RMTCustTypes or RMTCustTypes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer Types by key CustTypeCode and Version
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
	public void update(CustomerType customerType, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update RMTCustTypes");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set CustTypeCtg= :CustTypeCtg, CustTypeDesc = :CustTypeDesc,");
		updateSql.append(" CustTypeIsActive = :CustTypeIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CustTypeCode =:CustTypeCode");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * validate customer type code against the category code.
	 * 
	 * @param custTypeCode
	 * @param custCtgCode
	 * @return Integer
	 */
	@Override
	public int validateTypeAndCategory(String custTypeCode, String custCtgCode) {
		logger.debug(Literal.ENTERING);

		CustomerType customerType = new CustomerType();
		customerType.setId(custTypeCode);
		customerType.setCustTypeCtg(custCtgCode);

		StringBuilder selectSql = new StringBuilder("Select COUNT(*)  From RMTCustTypes ");
		selectSql.append(" Where CustTypeCode =:CustTypeCode AND CustTypeCtg= :CustTypeCtg");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerType);

		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			recordCount = 0;
		}
		logger.debug(Literal.LEAVING);
		return recordCount;
	}

	@Override
	public boolean isDuplicateKey(String customerTypeCode, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "CustTypeCode =:CustTypeCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTCustTypes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTCustTypes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTCustTypes_Temp", "RMTCustTypes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CustTypeCode", customerTypeCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}