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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>CustomerEmploymentDetail model</b> class.<br>
 * 
 */
public class CustomerEmploymentDetailDAOImpl extends SequenceDao<CustomerEmploymentDetail>
		implements CustomerEmploymentDetailDAO {
	private static Logger logger = LogManager.getLogger(CustomerEmploymentDetailDAOImpl.class);

	public CustomerEmploymentDetailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer Employment Details details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustomerEmploymentDetail
	 */
	@Override
	public CustomerEmploymentDetail getCustomerEmploymentDetailByCustEmpId(long custEmpId, String type) {
		logger.debug("Entering");
		CustomerEmploymentDetail customerEmploymentDetail = new CustomerEmploymentDetail();
		customerEmploymentDetail.setCustEmpId(custEmpId);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT CustEmpId,CustID, CustEmpName, CustEmpDept, CustEmpDesg");
		sql.append(", CustEmpType, CustEmpFrom, CustEmpTo,CurrentEmployer,CompanyName");
		if (type.contains("View")) {
			sql.append(", LovDescEmpCategory, lovDescCustEmpDesgName, LovDescCustEmpDeptName");
			sql.append(", LovDescCustEmpTypeName, LovDesccustEmpName, LovDescEmpIndustry");
		}
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  CustomerEmpDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustEmpId = :CustEmpId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEmploymentDetail);
		RowMapper<CustomerEmploymentDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(CustomerEmploymentDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getCustomerEmploymentByCustEmpName(final long id, Long custEmpName, long custEmpId, String type) {
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

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
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
		RowMapper<CustomerEmploymentDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(CustomerEmploymentDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the CustomerEmpDetails or CustomerEmpDetails_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Customer Employment Details by key CustID
	 * 
	 * @param Customer Employment Details (customerEmploymentDetail)
	 * @param type     (String) ""/_Temp/_View
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
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
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
	 * @param Customer Employment Details (customerEmploymentDetail)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerEmploymentDetail customerEmploymentDetail, String type) {
		logger.debug("Entering");
		if (customerEmploymentDetail.getCustEmpId() == Long.MIN_VALUE) {
			customerEmploymentDetail.setCustEmpId(getNextValue("SeqCustomerEmpDetails"));
			logger.debug("get NextID:" + customerEmploymentDetail.getCustEmpId());
		}
		StringBuilder insertSql = new StringBuilder();

		insertSql.append(" Insert Into CustomerEmpDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustEmpId,CustID, CustEmpName, CustEmpFrom, CustEmpTo, CustEmpDesg,");
		insertSql.append(" CustEmpDept, CustEmpType,CurrentEmployer,companyName,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustEmpId, :CustID, :CustEmpName, :CustEmpFrom, :CustEmpTo, :CustEmpDesg,");
		insertSql.append("	:CustEmpDept, :CustEmpType, :CurrentEmployer,:CompanyName,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEmploymentDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerEmploymentDetail.getId();
	}

	/**
	 * This method updates the Record CustomerEmpDetails or CustomerEmpDetails_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer Employment Details by key CustID and Version
	 * 
	 * @param Customer Employment Details (customerEmploymentDetail)
	 * @param type     (String) ""/_Temp/_View
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
		updateSql.append(" Set CustEmpDesg = :CustEmpDesg, CustEmpDept = :CustEmpDept, CustEmpName = :CustEmpName,");
		updateSql.append(" CustEmpType = :CustEmpType,CustEmpFrom = :CustEmpFrom, CurrentEmployer =:CurrentEmployer,");
		updateSql.append(" CustEmpTo = :CustEmpTo ,companyName=:CompanyName, Version = :Version ,");
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
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving Update Method");
	}

	@Override
	public List<CustomerEmploymentDetail> getCustomerEmploymentDetailsByID(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustEmpId, CustID, CustEmpName, CustEmpDept, CustEmpDesg, CustEmpType, CustEmpFrom");
		sql.append(", CustEmpTo, CurrentEmployer, CompanyName, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCustEmpDesgName, LovDescCustEmpDeptName, LovDescCustEmpTypeName");
			sql.append(", LovDesccustEmpName, LovDescEmpCategory, LovDescEmpIndustry");
		}

		sql.append(" from CustomerEmpDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, id);
		}, (rs, rowNum) -> {
			CustomerEmploymentDetail emp = new CustomerEmploymentDetail();

			emp.setCustEmpId(rs.getLong("CustEmpId"));
			emp.setCustID(rs.getLong("CustID"));
			emp.setCustEmpName(JdbcUtil.getLong(rs.getObject("CustEmpName")));
			emp.setCustEmpDept(rs.getString("CustEmpDept"));
			emp.setCustEmpDesg(rs.getString("CustEmpDesg"));
			emp.setCustEmpType(rs.getString("CustEmpType"));
			emp.setCustEmpFrom(rs.getTimestamp("CustEmpFrom"));
			emp.setCustEmpTo(rs.getTimestamp("CustEmpTo"));
			emp.setCurrentEmployer(rs.getBoolean("CurrentEmployer"));
			emp.setCompanyName(rs.getString("CompanyName"));
			emp.setVersion(rs.getInt("Version"));
			emp.setLastMntBy(rs.getLong("LastMntBy"));
			emp.setLastMntOn(rs.getTimestamp("LastMntOn"));
			emp.setRecordStatus(rs.getString("RecordStatus"));
			emp.setRoleCode(rs.getString("RoleCode"));
			emp.setNextRoleCode(rs.getString("NextRoleCode"));
			emp.setTaskId(rs.getString("TaskId"));
			emp.setNextTaskId(rs.getString("NextTaskId"));
			emp.setRecordType(rs.getString("RecordType"));
			emp.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				emp.setLovDescCustEmpDesgName(rs.getString("LovDescCustEmpDesgName"));
				emp.setLovDescCustEmpDeptName(rs.getString("LovDescCustEmpDeptName"));
				emp.setLovDescCustEmpTypeName(rs.getString("LovDescCustEmpTypeName"));
				emp.setLovDesccustEmpName(rs.getString("LovDesccustEmpName"));
				emp.setLovDescEmpCategory(rs.getString("LovDescEmpCategory"));
				emp.setLovDescEmpIndustry(rs.getString("LovDescEmpIndustry"));
			}

			return emp;
		});
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
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
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
	public int getVersion(long custID, long custEmpId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", custID);
		source.addValue("CustEmpId", custEmpId);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT Version FROM CustomerEmpDetails");
		sql.append(" WHERE CustId = :CustId AND CustEmpId = :CustEmpId");

		logger.trace(Literal.SQL + sql.toString());
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}
}