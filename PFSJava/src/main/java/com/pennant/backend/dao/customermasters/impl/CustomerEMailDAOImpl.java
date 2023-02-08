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
 * * FileName : CustomerEMailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 * * Modified
 * Date : 06-05-2011 * * Description : * *
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

import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>CustomerEMail model</b> class.<br>
 * 
 */
public class CustomerEMailDAOImpl extends BasicDao<CustomerEMail> implements CustomerEMailDAO {
	private static Logger logger = LogManager.getLogger(CustomerEMailDAOImpl.class);

	public CustomerEMailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer EMails details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustomerEMail
	 */
	@Override
	public CustomerEMail getCustomerEMailById(final long id, String typeCode, String type) {
		logger.debug(Literal.ENTERING);
		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setId(id);
		customerEMail.setCustEMailTypeCode(typeCode);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CustID, CustEMail, CustEMailPriority, CustEMailTypeCode, DomainCheck ");
		if (type.contains("View")) {
			sql.append(", lovDescCustEMailTypeCode");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  CustomerEMails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = :custID AND CustEMailTypeCode = :custEMailTypeCode");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		RowMapper<CustomerEMail> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerEMail.class);

		try {
			customerEMail = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"EMail details not found in CustomerEMails{} table/view for the specified CustID >> {} and CustEMailTypeCode",
					type, id, typeCode);
			customerEMail = null;
		}
		logger.debug(Literal.LEAVING);
		return customerEMail;
	}

	/**
	 * Method to return the customer email based on given customer id
	 */
	public List<CustomerEMail> getCustomerEmailByCustomer(final long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustEMail, CustEMailPriority, CustEMailTypeCode, DomainCheck, Version");
		sql.append(", LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", lovDescCustEMailTypeCode");
		}

		sql.append(" from CustomerEMails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {

			int index = 1;
			ps.setLong(index, id);
		}, (rs, rowNum) -> {
			CustomerEMail ce = new CustomerEMail();

			ce.setCustID(rs.getLong("CustID"));
			ce.setCustEMail(rs.getString("CustEMail"));
			ce.setCustEMailPriority(rs.getInt("CustEMailPriority"));
			ce.setCustEMailTypeCode(rs.getString("CustEMailTypeCode"));
			ce.setDomainCheck(rs.getString("DomainCheck"));
			ce.setVersion(rs.getInt("Version"));
			ce.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ce.setLastMntBy(rs.getLong("LastMntBy"));
			ce.setRecordStatus(rs.getString("RecordStatus"));
			ce.setRoleCode(rs.getString("RoleCode"));
			ce.setNextRoleCode(rs.getString("NextRoleCode"));
			ce.setTaskId(rs.getString("TaskId"));
			ce.setNextTaskId(rs.getString("NextTaskId"));
			ce.setRecordType(rs.getString("RecordType"));
			ce.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				ce.setLovDescCustEMailTypeCode(rs.getString("LovDescCustEMailTypeCode"));
			}

			return ce;
		});
	}

	/**
	 * Method to return the customer email based on given customer id
	 */
	@Override
	public List<String> getCustEmailsByCustId(final long custId) {
		logger.debug("Entering");

		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setId(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustEMail FROM  CustomerEMails ");
		selectSql.append(" Where CustID = :custID ORDER BY CustEMailPriority");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		RowMapper<String> typeRowMapper = BeanPropertyRowMapper.newInstance(String.class);

		List<String> custEmailsByIDs = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		logger.debug("Leaving");
		return custEmailsByIDs;
	}

	/**
	 * This method Deletes the Record from the CustomerEMails or CustomerEMails_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Customer EMails by key CustID
	 * 
	 * @param Customer EMails (customerEMail)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerEMail customerEMail, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From CustomerEMails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID AND CustEMailTypeCode =:custEMailTypeCode");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);

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
	 * This method Deletes the Record from the CustomerEMails or CustomerEMails_Temp for the Customer.
	 * 
	 * @param Customer EMails (customerEMail)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByCustomer(long custID, String type) {
		logger.debug("Entering");

		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setCustID(custID);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerEMails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CustomerEMails or CustomerEMails_Temp.
	 *
	 * save Customer EMails
	 * 
	 * @param Customer EMails (customerEMail)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerEMail customerEMail, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert Into CustomerEMails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (CustID, CustEMailTypeCode, CustEMailPriority, CustEMail, DomainCheck");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:CustID, :CustEMailTypeCode, :CustEMailPriority, :CustEMail, :DomainCheck");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return customerEMail.getId();
	}

	/**
	 * This method updates the Record CustomerEMails or CustomerEMails_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer EMails by key CustID and Version
	 * 
	 * @param Customer EMails (customerEMail)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerEMail customerEMail, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerEMails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustEMailPriority = :CustEMailPriority, CustEMail = :CustEMail");
		updateSql.append(", DomainCheck = :DomainCheck, Version = :Version , LastMntBy = :LastMntBy");
		updateSql.append(", LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode");
		updateSql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		updateSql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CustID =:CustID AND CustEMailTypeCode =:custEMailTypeCode ");
		if (!type.endsWith("_Temp")) {
			updateSql.append("AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for get total number of records from BMTEMailTypes master table.<br>
	 * 
	 * @param EmailTypeCode
	 * 
	 * @return Integer
	 */
	@Override
	public int getEMailTypeCount(String typeCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("EmailTypeCode", typeCode);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM BMTEMailTypes");
		selectSql.append(" WHERE ");
		selectSql.append("EmailTypeCode= :EmailTypeCode");

		logger.debug("insertSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	/**
	 * Fetch current version of the record.
	 * 
	 * @param id
	 * @param typeCode
	 * @return Integer
	 */
	@Override
	public int getVersion(long id, String typeCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", id);
		source.addValue("CustEMailTypeCode", typeCode);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT Version FROM CustomerEMails");

		selectSql.append(" WHERE CustId = :CustId AND CustEMailTypeCode = :CustEMailTypeCode");

		logger.debug("insertSql: " + selectSql.toString());

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public List<CustomerEMail> getCustIDByEmail(String email, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select CustID, CustEMail, CustEMailPriority, CustEMailTypeCode,DomainCheck ");
		if (type.contains("View")) {
			sql.append(", lovDescCustEMailTypeCode");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  CustomerEMails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustEMail = :CustEMail");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("CustEMail", email);

		RowMapper<CustomerEMail> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerEMail.class);

		return this.jdbcTemplate.query(sql.toString(), mapSqlParameterSource, typeRowMapper);
	}

	@Override
	public List<String> getCustEmailsByCustomerId(long custId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustEMail FROM  CustomerEMails ");
		selectSql.append(" Where CustID = :custID ORDER BY CustEMailPriority");
		source.addValue("custID", custId);

		logger.debug("selectSql: " + selectSql.toString());

		List<String> custEmailsByIDs = this.jdbcTemplate.queryForList(selectSql.toString(), source, String.class);
		logger.debug("Leaving");

		return custEmailsByIDs;
	}

}