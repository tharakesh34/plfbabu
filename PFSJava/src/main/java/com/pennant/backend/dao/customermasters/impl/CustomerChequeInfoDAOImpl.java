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
 * * FileName : CustomerChequeInfoDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 * *
 * Modified Date : 06-05-2011 * * Description : * *
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

import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>CustomerChequeInfo model</b> class.<br>
 * 
 */
public class CustomerChequeInfoDAOImpl extends BasicDao<CustomerChequeInfo> implements CustomerChequeInfoDAO {
	private static Logger logger = LogManager.getLogger(CustomerChequeInfoDAOImpl.class);

	public CustomerChequeInfoDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer EMails details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustomerChequeInfo
	 */
	@Override
	public CustomerChequeInfo getCustomerChequeInfoById(final long id, int chequeSeq, String type) {
		CustomerChequeInfo customerChequeInfo = new CustomerChequeInfo();
		customerChequeInfo.setId(id);
		customerChequeInfo.setChequeSeq(chequeSeq);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT CustID, ChequeSeq, MonthYear, TotChequePayment, Salary, Debits, ReturnChequeAmt, ReturnChequeCount, Remarks,");
		if (type.contains("View")) {
			selectSql.append(" lovDescCustCIF,lovDescCustShrtName,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerChequeInfo");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID AND ChequeSeq = :ChequeSeq");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerChequeInfo);
		RowMapper<CustomerChequeInfo> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerChequeInfo.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Cheque details found in CustomerChequeInfo{} table/view for the specified CustID >> {} and ChequeSeq >> {}",
					type, id, chequeSeq);
		}

		return null;
	}

	/**
	 * Method to return the customer email based on given customer id
	 */
	@Override
	public List<CustomerChequeInfo> getChequeInfoByCustomer(final long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, ChequeSeq, MonthYear, TotChequePayment, Salary, Debits, ReturnChequeAmt");
		sql.append(", ReturnChequeCount, Remarks, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(" ");
		}

		sql.append(" from CustomerChequeInfo");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, id);
		}, (rs, rowNum) -> {
			CustomerChequeInfo cci = new CustomerChequeInfo();

			cci.setCustID(rs.getLong("CustID"));
			cci.setChequeSeq(rs.getInt("ChequeSeq"));
			cci.setMonthYear(rs.getTimestamp("MonthYear"));
			cci.setTotChequePayment(rs.getBigDecimal("TotChequePayment"));
			cci.setSalary(rs.getBigDecimal("Salary"));
			cci.setDebits(rs.getBigDecimal("Debits"));
			cci.setReturnChequeAmt(rs.getBigDecimal("ReturnChequeAmt"));
			cci.setReturnChequeCount(rs.getInt("ReturnChequeCount"));
			cci.setRemarks(rs.getString("Remarks"));
			cci.setVersion(rs.getInt("Version"));
			cci.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cci.setLastMntBy(rs.getLong("LastMntBy"));
			cci.setRecordStatus(rs.getString("RecordStatus"));
			cci.setRoleCode(rs.getString("RoleCode"));
			cci.setNextRoleCode(rs.getString("NextRoleCode"));
			cci.setTaskId(rs.getString("TaskId"));
			cci.setNextTaskId(rs.getString("NextTaskId"));
			cci.setRecordType(rs.getString("RecordType"));
			cci.setWorkflowId(rs.getLong("WorkflowId"));

			return cci;
		});
	}

	/**
	 * This method Deletes the Record from the CustomerChequeInfo or CustomerChequeInfo_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Customer EMails by key CustID
	 * 
	 * @param Customer EMails (customerChequeInfo)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerChequeInfo customerChequeInfo, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From CustomerChequeInfo");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID AND ChequeSeq = :ChequeSeq");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerChequeInfo);

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
	 * This method Deletes the Record from the CustomerChequeInfo or CustomerChequeInfo_Temp for the Customer.
	 * 
	 * @param Customer EMails (customerChequeInfo)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByCustomer(long custID, String type) {
		logger.debug("Entering");

		CustomerChequeInfo customerChequeInfo = new CustomerChequeInfo();
		customerChequeInfo.setCustID(custID);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerChequeInfo");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerChequeInfo);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CustomerChequeInfo or CustomerChequeInfo_Temp.
	 *
	 * save Customer EMails
	 * 
	 * @param Customer EMails (customerChequeInfo)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerChequeInfo customerChequeInfo, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerChequeInfo");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (CustID, ChequeSeq, MonthYear, TotChequePayment, Salary, Debits,  ReturnChequeAmt, ReturnChequeCount, Remarks,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:CustID, :ChequeSeq, :MonthYear, :TotChequePayment, :Salary, :Debits, :ReturnChequeAmt, :ReturnChequeCount, :Remarks,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerChequeInfo);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerChequeInfo.getId();
	}

	/**
	 * This method updates the Record CustomerChequeInfo or CustomerChequeInfo_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer EMails by key CustID and Version
	 * 
	 * @param Customer EMails (customerChequeInfo)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerChequeInfo customerChequeInfo, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerChequeInfo");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set MonthYear = :MonthYear, TotChequePayment = :TotChequePayment, Salary = :Salary, Debits = :Debits,");
		updateSql.append(
				" ReturnChequeAmt = :ReturnChequeAmt, ReturnChequeCount = :ReturnChequeCount, Remarks = :Remarks,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND ChequeSeq =:ChequeSeq ");
		if (!type.endsWith("_Temp")) {
			updateSql.append("AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerChequeInfo);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

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

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT Version FROM CustomerChequeInfo");
		selectSql.append(" WHERE CustId = :CustId AND ChequeSeq = :ChequeSeq");

		logger.debug("insertSql: " + selectSql.toString());

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}
}