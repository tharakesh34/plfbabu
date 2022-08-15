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
 * * FileName : CustomerStatusCodeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>CustomerStatusCode model</b> class.<br>
 * 
 */
public class CustomerStatusCodeDAOImpl extends BasicDao<CustomerStatusCode> implements CustomerStatusCodeDAO {
	private static Logger logger = LogManager.getLogger(CustomerStatusCodeDAOImpl.class);

	public CustomerStatusCodeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer Status Codes details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustomerStatusCode
	 */
	@Override
	public CustomerStatusCode getCustomerStatusCodeById(final String id, String type) {
		logger.debug("Entering");
		CustomerStatusCode customerStatusCode = new CustomerStatusCode();
		customerStatusCode.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT CustStsCode, CustStsDescription, DueDays, SuspendProfit,CustStsIsActive,");
		if (type.contains("View")) {
			selectSql.append("");
		}
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  BMTCustStatusCodes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustStsCode =:CustStsCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerStatusCode);
		RowMapper<CustomerStatusCode> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerStatusCode.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the BMTCustStatusCodes or BMTCustStatusCodes_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Customer Status Codes by key CustStsCode
	 * 
	 * @param Customer Status Codes (customerStatusCode)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(CustomerStatusCode customerStatusCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BMTCustStatusCodes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustStsCode =:CustStsCode");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerStatusCode);

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
	 * This method insert new Records into BMTCustStatusCodes or BMTCustStatusCodes_Temp.
	 * 
	 * save Customer Status Codes
	 * 
	 * @param Customer Status Codes (customerStatusCode)
	 * @param type     (String) ""/_Temp/_View
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
		insertSql.append(" (CustStsCode, CustStsDescription, DueDays, SuspendProfit, CustStsIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:CustStsCode, :CustStsDescription, :DueDays, :SuspendProfit,:CustStsIsActive, ");
		insertSql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerStatusCode);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerStatusCode.getId();
	}

	/**
	 * This method updates the Record BMTCustStatusCodes or BMTCustStatusCodes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer Status Codes by key CustStsCode and Version
	 * 
	 * @param Customer Status Codes (customerStatusCode)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerStatusCode customerStatusCode, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTCustStatusCodes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustStsDescription = :CustStsDescription,");
		updateSql.append(" DueDays=:DueDays, SuspendProfit=:SuspendProfit,CustStsIsActive = :CustStsIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CustStsCode =:CustStsCode ");
		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerStatusCode);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for get Finance Profit Suspend status
	 */
	@Override
	public boolean getFinanceSuspendStatus(int curODDays) {
		logger.debug("Entering");

		boolean suspendProfit = false;
		CustomerStatusCode customerStatusCode = new CustomerStatusCode();
		customerStatusCode.setDueDays(curODDays);
		StringBuilder selectSql = new StringBuilder("SELECT Count(CustStsCode) ");
		selectSql.append(" FROM  BMTCustStatusCodes");
		selectSql.append(" Where DueDays <=:DueDays AND SuspendProfit = 1");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerStatusCode);

		int suspendCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		if (suspendCount > 0) {
			suspendProfit = true;
		}
		logger.debug("Leaving");
		return suspendProfit;
	}

	/**
	 * Method for get Finance Profit Suspend status
	 */
	@Override
	public String getFinanceStatus(String finReference, boolean isCurFinStatus) {
		logger.debug("Entering");

		FinODDetails odDetails = new FinODDetails();
		odDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" Select CustStsCode FROM (SELECT CustStsCode, ");
		selectSql.append(" row_number() over (order by DueDays DESC) row_num  FROM BMTCustStatusCodes ");
		selectSql.append(" WHERE DueDays <= (Select COALESCE(Max(FinCurODDays), 0) from FinODDetails ");
		selectSql.append(" WHERE FinReference = :FinReference ");
		if (isCurFinStatus) {
			selectSql.append(" AND FinCurODAmt != 0 ");
		}
		selectSql.append("  ))T WHERE row_num <= 1 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(odDetails);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public CustomerStatusCode getCustStatusByMinDueDays(String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustStsCode, CustStsDescription, DueDays, SuspendProfit, CustStsIsActive, Version");
		sql.append(", LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" from BMTCustStatusCodes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DueDays = (");
		sql.append("Select COALESCE(MIN(DueDays),0) from BMTCustStatusCodes)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] {},
					new RowMapper<CustomerStatusCode>() {
						@Override
						public CustomerStatusCode mapRow(ResultSet rs, int rowNum) throws SQLException {
							CustomerStatusCode sc = new CustomerStatusCode();

							sc.setCustStsCode(rs.getString("CustStsCode"));
							sc.setCustStsDescription(rs.getString("CustStsDescription"));
							sc.setDueDays(rs.getInt("DueDays"));
							sc.setSuspendProfit(rs.getBoolean("SuspendProfit"));
							sc.setCustStsIsActive(rs.getBoolean("CustStsIsActive"));
							sc.setVersion(rs.getInt("Version"));
							sc.setLastMntOn(rs.getTimestamp("LastMntOn"));
							sc.setLastMntBy(rs.getLong("LastMntBy"));
							sc.setRecordStatus(rs.getString("RecordStatus"));
							sc.setRoleCode(rs.getString("RoleCode"));
							sc.setNextRoleCode(rs.getString("NextRoleCode"));
							sc.setTaskId(rs.getString("TaskId"));
							sc.setNextTaskId(rs.getString("NextTaskId"));
							sc.setRecordType(rs.getString("RecordType"));
							sc.setWorkflowId(rs.getLong("WorkflowId"));

							return sc;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}