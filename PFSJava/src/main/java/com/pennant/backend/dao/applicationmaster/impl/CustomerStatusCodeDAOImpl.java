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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.finance.FinODDetails;
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
			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<CustomerStatusCode>() {
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