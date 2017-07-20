/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : CustomerEmploymentDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 *
 * * Modified Date : 06-05-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
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

import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustomerEmploymentDetail model</b> class.<br>
 * 
 */
public class CustomerEmploymentDetailDAOImpl extends BasisNextidDaoImpl<CustomerEmploymentDetail> implements
		CustomerEmploymentDetailDAO {

	private static Logger				logger	= Logger.getLogger(CustomerEmploymentDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public CustomerEmploymentDetailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer Employment Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerEmploymentDetail
	 */
	@Override
	public CustomerEmploymentDetail getCustomerEmploymentDetailByCustEmpId(long custEmpId, String type) {
		logger.debug("Entering");
		CustomerEmploymentDetail customerEmploymentDetail = new CustomerEmploymentDetail();
		customerEmploymentDetail.setCustEmpId(custEmpId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT CustEmpId,CustID, CustEmpName, CustEmpDept, CustEmpDesg,");
		selectSql.append(" CustEmpType, CustEmpFrom, CustEmpTo,CurrentEmployer,");
		if (type.contains("View")) {
			selectSql.append(" lovDescCustEmpDesgName, lovDescCustEmpDeptName,");
			selectSql.append(" lovDescCustEmpTypeName,lovDesccustEmpName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  CustomerEmpDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustEmpId = :CustEmpId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEmploymentDetail);
		RowMapper<CustomerEmploymentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerEmploymentDetail.class);

		try {
			customerEmploymentDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			customerEmploymentDetail = null;
		}
		logger.debug("Leaving");
		return customerEmploymentDetail;
	}

	@Override
	public int getCustomerEmploymentByCustEmpName(final long id, long custEmpName, long custEmpId, String type) {
		logger.debug("Entering");
		CustomerEmploymentDetail customerEmploymentDetail = new CustomerEmploymentDetail();
		customerEmploymentDetail.setCustEmpId(custEmpId);
		customerEmploymentDetail.setId(id);
		customerEmploymentDetail.setCustEmpName(custEmpName);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From CustomerEmpDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID and CustEmpName=:CustEmpName and CustEmpId != :CustEmpId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEmploymentDetail);

		try {
			int custEmployment = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					Integer.class);
			logger.debug("Leaving");
			return custEmployment;
		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		}
	}

	/**
	 * Method to return the customer details based on given customer id.
	 */
	public CustomerEmploymentDetail isEmployeeExistWithCustID(final long id, String type) {
		logger.debug("Entering");

		CustomerEmploymentDetail customerEmploymentDetail = new CustomerEmploymentDetail();
		customerEmploymentDetail.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select CustID From CustomerEmpDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID =:custID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEmploymentDetail);
		RowMapper<CustomerEmploymentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerEmploymentDetail.class);

		try {
			customerEmploymentDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerEmploymentDetail = null;
		}
		logger.debug("Leaving");
		return customerEmploymentDetail;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the CustomerEmpDetails or CustomerEmpDetails_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Customer Employment Details by key CustID
	 * 
	 * @param Customer
	 *            Employment Details (customerEmploymentDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerEmploymentDetail customerEmploymentDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerEmpDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustEmpId=:CustEmpId ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEmploymentDetail);

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
	 * This method insert new Records into CustomerEmpDetails or CustomerEmpDetails_Temp.
	 * 
	 * save Customer Employment Details
	 * 
	 * @param Customer
	 *            Employment Details (customerEmploymentDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerEmploymentDetail customerEmploymentDetail, String type) {
		logger.debug("Entering");
		if (customerEmploymentDetail.getCustEmpId() == Long.MIN_VALUE) {
			customerEmploymentDetail.setCustEmpId(getNextidviewDAO().getNextId("SeqCustomerEmpDetails"));
			logger.debug("get NextID:" + customerEmploymentDetail.getCustEmpId());
		}
		StringBuilder insertSql = new StringBuilder();

		insertSql.append(" Insert Into CustomerEmpDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustEmpId,CustID, CustEmpName, CustEmpFrom, CustEmpTo, CustEmpDesg,");
		insertSql.append(" CustEmpDept, CustEmpType,CurrentEmployer,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustEmpId, :CustID, :CustEmpName, :CustEmpFrom, :CustEmpTo, :CustEmpDesg,");
		insertSql.append("	:CustEmpDept, :CustEmpType, :CurrentEmployer,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEmploymentDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerEmploymentDetail.getId();
	}

	/**
	 * This method updates the Record CustomerEmpDetails or CustomerEmpDetails_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer Employment Details by key CustID and Version
	 * 
	 * @param Customer
	 *            Employment Details (customerEmploymentDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerEmploymentDetail customerEmploymentDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerEmpDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustEmpDesg = :CustEmpDesg, CustEmpDept = :CustEmpDept,");
		updateSql.append(" CustEmpType = :CustEmpType,CustEmpFrom = :CustEmpFrom, CurrentEmployer =:CurrentEmployer,");
		updateSql.append(" CustEmpTo = :CustEmpTo , Version = :Version ,");
		updateSql.append(" LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, ");
		updateSql.append(" NextTaskId = :NextTaskId,RecordType = :RecordType,");
		updateSql.append("  WorkflowId = :WorkflowId");
		updateSql.append(" Where CustEmpId =:CustEmpId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEmploymentDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving Update Method");
	}


	@Override
	public List<CustomerEmploymentDetail> getCustomerEmploymentDetailsByID(long id, String type) {
		logger.debug("Entering");
		CustomerEmploymentDetail customerEmploymentDetail = new CustomerEmploymentDetail();
		customerEmploymentDetail.setId(id);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT CustEmpId,CustID, CustEmpName, CustEmpDept, CustEmpDesg,");
		selectSql.append(" CustEmpType, CustEmpFrom, CustEmpTo,CurrentEmployer,");
		if (type.contains("View")) {
			selectSql.append(" lovDescCustEmpDesgName, lovDescCustEmpDeptName,");
			selectSql.append(" lovDescCustEmpTypeName,lovDesccustEmpName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  CustomerEmpDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEmploymentDetail);
		RowMapper<CustomerEmploymentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerEmploymentDetail.class);

		List<CustomerEmploymentDetail> custEmploymentDetails = this.namedParameterJdbcTemplate.query(
				selectSql.toString(), beanParameters, typeRowMapper);

		logger.debug("Leaving");
		return custEmploymentDetails;

	}

	@Override
	public void deleteByCustomer(long custID, String tableType) {
		logger.debug("Entering");
		CustomerEmploymentDetail customerEmploymentDetail = new CustomerEmploymentDetail();
		customerEmploymentDetail.setId(custID);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerEmpDetails");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where CustID =:CustID ");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEmploymentDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	/**
	 * Fetch current version of the record.
	 * 
	 * @param custID
	 * @param empName
	 * @return Integer
	 */
	@Override
	public int getVersion(long custID, long empName) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", custID);
		source.addValue("CustEmpName", empName);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM CustomerEmpDetails");

		selectSql.append(" WHERE CustId = :CustId AND CustEmpName = :CustEmpName");

		logger.debug("insertSql: " + selectSql.toString());

		int returnRcds = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class); 
		logger.debug("Leaving");
		return returnRcds;
	}

}