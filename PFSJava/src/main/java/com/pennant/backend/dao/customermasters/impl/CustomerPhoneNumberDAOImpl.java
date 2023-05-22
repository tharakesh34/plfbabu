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
 * * FileName : CustomerPhoneNumberDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
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

import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>CustomerPhoneNumber model</b> class.<br>
 * 
 */
public class CustomerPhoneNumberDAOImpl extends BasicDao<CustomerPhoneNumber> implements CustomerPhoneNumberDAO {
	private static Logger logger = LogManager.getLogger(CustomerPhoneNumberDAOImpl.class);

	public CustomerPhoneNumberDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer PhoneNumbers details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustomerPhoneNumber
	 */
	@Override
	public CustomerPhoneNumber getCustomerPhoneNumberByID(final long id, String typeCode, String type) {
		logger.debug("Entering");
		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setId(id);
		customerPhoneNumber.setPhoneTypeCode(typeCode);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  PhoneCustID, PhoneTypeCode,");
		selectSql.append(" PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority,");
		if (type.contains("View")) {
			selectSql.append(" lovDescPhoneTypeCodeName, lovDescPhoneCountryName,PhoneRegex,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerPhoneNumbers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PhoneCustID =:PhoneCustID AND PhoneTypeCode=:PhoneTypeCode ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		RowMapper<CustomerPhoneNumber> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerPhoneNumber.class);

		try {
			customerPhoneNumber = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Record not found in CustomerPhoneNumbers{} for the specified PhoneCustID >> {} and PhoneTypeCode",
					type, id, typeCode);
			customerPhoneNumber = null;
		}
		logger.debug("Leaving");
		return customerPhoneNumber;
	}

	/**
	 * This method Deletes the Record from the CustomerPhoneNumbers or CustomerPhoneNumbers_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete Customer PhoneNumbers by key PhoneCustID
	 * 
	 * @param Customer PhoneNumbers (customerPhoneNumber)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerPhoneNumber customerPhoneNumber, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerPhoneNumbers");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PhoneCustID =:PhoneCustID AND PhoneTypeCode =:PhoneTypeCode");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);

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
	 * This method insert new Records into CustomerPhoneNumbers or CustomerPhoneNumbers_Temp.
	 *
	 * save Customer PhoneNumbers
	 * 
	 * @param Customer PhoneNumbers (customerPhoneNumber)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerPhoneNumber customerPhoneNumber, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerPhoneNumbers");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:PhoneCustID, :PhoneTypeCode, :PhoneCountryCode,:PhoneAreaCode,:PhoneNumber,:PhoneTypePriority,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerPhoneNumber.getId();
	}

	/**
	 * This method updates the Record CustomerPhoneNumbers or CustomerPhoneNumbers_Temp. if Record not updated then
	 * throws DataAccessException with error 41004. update Customer PhoneNumbers by key PhoneCustID and Version
	 * 
	 * @param Customer PhoneNumbers (customerPhoneNumber)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerPhoneNumber customerPhoneNumber, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerPhoneNumbers");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set PhoneCountryCode = :PhoneCountryCode, PhoneAreaCode = :PhoneAreaCode, PhoneTypePriority =:PhoneTypePriority,");
		updateSql.append(" PhoneNumber = :PhoneNumber,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType,");
		updateSql.append(" WorkflowId = :WorkflowId");
		updateSql.append(" Where PhoneCustID =:PhoneCustID AND PhoneTypeCode=:PhoneTypeCode");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Getting List of Objects in Customers By Using CustID
	 */
	public List<CustomerPhoneNumber> getCustomerPhoneNumberByCustomer(final long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber, PhoneTypePriority");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescPhoneTypeCodeName, LovDescPhoneCountryName, PhoneRegex");
		}

		sql.append(" from CustomerPhoneNumbers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PhoneCustID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, id);
		}, (rs, rowNum) -> {
			CustomerPhoneNumber pno = new CustomerPhoneNumber();

			pno.setPhoneCustID(rs.getLong("PhoneCustID"));
			pno.setPhoneTypeCode(rs.getString("PhoneTypeCode"));
			pno.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
			pno.setPhoneAreaCode(rs.getString("PhoneAreaCode"));
			pno.setPhoneNumber(rs.getString("PhoneNumber"));
			pno.setPhoneTypePriority(rs.getInt("PhoneTypePriority"));
			pno.setVersion(rs.getInt("Version"));
			pno.setLastMntBy(rs.getLong("LastMntBy"));
			pno.setLastMntOn(rs.getTimestamp("LastMntOn"));
			pno.setRecordStatus(rs.getString("RecordStatus"));
			pno.setRoleCode(rs.getString("RoleCode"));
			pno.setNextRoleCode(rs.getString("NextRoleCode"));
			pno.setTaskId(rs.getString("TaskId"));
			pno.setNextTaskId(rs.getString("NextTaskId"));
			pno.setRecordType(rs.getString("RecordType"));
			pno.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				pno.setLovDescPhoneTypeCodeName(rs.getString("LovDescPhoneTypeCodeName"));
				pno.setLovDescPhoneCountryName(rs.getString("LovDescPhoneCountryName"));
				pno.setPhoneRegex(rs.getString("PhoneRegex"));
			}

			return pno;
		});

	}

	public List<CustomerPhoneNumber> getCustomerPhoneNumberByCustomerPhoneType(final long id, String type,
			String phoneType) {
		logger.debug("Entering");
		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setPhoneCustID(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT  PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority,");
		if (type.contains("View")) {
			selectSql.append(" lovDescPhoneTypeCodeName, lovDescPhoneCountryName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  CustomerPhoneNumbers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append("  Where PhoneCustID =:PhoneCustID and PhoneTypeCode =:PhoneTypeCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		RowMapper<CustomerPhoneNumber> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerPhoneNumber.class);

		List<CustomerPhoneNumber> customerPhoneNumbers = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving ");
		return customerPhoneNumbers;
	}

	/**
	 * This method Deletes the Record from the CustomerPhoneNumbers or CustomerPhoneNumbers_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete Customer PhoneNumbers by key PhoneCustID
	 * 
	 * @param Customer PhoneNumbers (customerPhoneNumber)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByCustomer(final long id, String type) {
		logger.debug("Entering");

		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setPhoneCustID(id);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerPhoneNumbers");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PhoneCustID =:PhoneCustID ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Customer PhoneNumber By its CustPhoneId
	 * 
	 * @param id
	 * 
	 * 
	 * @return
	 */
	@Override
	public List<CustomerPhoneNumber> getCustomerPhoneNumberById(long id, String type) {
		logger.debug("Entering");
		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setPhoneCustID(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT  PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority,");
		if (type.contains("View")) {
			selectSql.append(" lovDescPhoneTypeCodeName, lovDescPhoneCountryName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  CustomerPhoneNumbers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PhoneCustID =:PhoneCustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		RowMapper<CustomerPhoneNumber> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerPhoneNumber.class);

		List<CustomerPhoneNumber> customerPhoneNumbers = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving ");
		return customerPhoneNumbers;
	}

	/**
	 * Fetch current version of the record.
	 * 
	 * @param id
	 * @param typeCode
	 * @return Integer
	 */
	@Override
	public int getVersion(long id, String phoneTypeCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PhoneCustId", id);
		source.addValue("PhoneTypeCode", phoneTypeCode);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT Version FROM CustomerPhoneNumbers");
		selectSql.append(" WHERE PhoneCustId = :PhoneCustId AND PhoneTypeCode = :PhoneTypeCode");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	/**
	 * Method for get total number of records from BMTPhoneTypes master table.<br>
	 * 
	 * @param phoneTypeCode
	 * 
	 * @return Integer
	 */
	@Override
	public int getPhoneTypeCodeCount(String phoneTypeCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PhoneTypeCode", phoneTypeCode);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM BMTPhoneTypes");
		selectSql.append(" WHERE ");
		selectSql.append("PhoneTypeCode= :PhoneTypeCode");

		logger.debug(Literal.SQL + selectSql.toString());
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public List<CustomerPhoneNumber> getCustIDByPhoneNumber(String phoneNumber, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append(" Select PhoneCustID, PhoneTypeCode,");
		sql.append(" PhoneCountryCode, PhoneAreaCode, PhoneNumber, PhoneTypePriority");
		if (type.contains("View")) {
			sql.append(", lovDescPhoneTypeCodeName, lovDescPhoneCountryName, PhoneRegex");
		}
		sql.append(" ,Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(" ,TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  CustomerPhoneNumbers");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PhoneNumber =:PhoneNumber");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("PhoneNumber", phoneNumber);

		RowMapper<CustomerPhoneNumber> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerPhoneNumber.class);

		return this.jdbcTemplate.query(sql.toString(), mapSqlParameterSource, typeRowMapper);
	}

	@Override
	public String getCustomerPhoneNumberByCustId(long custID) {
		String sql = "Select PhoneNumber From CustomerPhoneNumbers Where PhoneCustID = ? and PhoneTypePriority = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, String.class, custID,
				Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH));
	}

	@Override
	public List<Customer> getCustomersByPhoneNum(String phoneNum) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" C.CustID, C.CustCIF,C.CustShrtName ");
		sql.append(" From Customers C");
		sql.append(" Inner Join CustomerPhoneNumbers CP on CP.PhoneCustID = C.CustID");
		sql.append("  Where CP.PhoneNumber = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			Customer customer = new Customer();

			customer.setCustID(rs.getLong("CustID"));
			customer.setCustCIF(rs.getString("CustCIF"));
			customer.setCustShrtName(rs.getString("CustShrtName"));

			return customer;
		}, phoneNum);
	}

	@Override
	public List<CustomerPhoneNumber> getCustomerPhoneNumberByID(final long id, long phoneTypePriority) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PhoneCustID, PhoneTypeCode");
		sql.append(", PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CustomerPhoneNumbers");
		sql.append(" Where PhoneCustID = ? and PhoneTypePriority = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CustomerPhoneNumber custphone = new CustomerPhoneNumber();

			custphone.setPhoneCustID(rs.getLong("PhoneCustID"));
			custphone.setPhoneTypeCode(rs.getString("PhoneTypeCode"));
			custphone.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
			custphone.setPhoneAreaCode(rs.getString("PhoneAreaCode"));
			custphone.setPhoneNumber(rs.getString("PhoneNumber"));
			custphone.setPhoneTypePriority((int) rs.getLong("PhoneTypePriority"));
			custphone.setVersion(rs.getInt("Version"));
			custphone.setLastMntBy(rs.getLong("LastMntBy"));
			custphone.setLastMntOn(rs.getTimestamp("LastMntOn"));
			custphone.setRecordStatus(rs.getString("RecordStatus"));
			custphone.setRoleCode(rs.getString("RoleCode"));
			custphone.setNextRoleCode(rs.getString("NextRoleCode"));
			custphone.setTaskId(rs.getString("TaskId"));
			custphone.setNextTaskId(rs.getString("NextTaskId"));
			custphone.setRecordType(rs.getString("RecordType"));
			custphone.setWorkflowId(rs.getLong("WorkflowId"));

			return custphone;
		}, id, phoneTypePriority);
	}

	@Override
	public List<CustomerPhoneNumber> getCustomerPhoneNumberByID(final long id, String typeCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PhoneCustID, PhoneTypeCode,");
		sql.append(" PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority,");
		sql.append(" lovDescPhoneTypeCodeName, lovDescPhoneCountryName,PhoneRegex,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" From CustomerPhoneNumbers");
		sql.append(" Where PhoneCustID = ? and PhoneTypeCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CustomerPhoneNumber custphone = new CustomerPhoneNumber();

			custphone.setPhoneCustID(rs.getLong("PhoneCustID"));
			custphone.setPhoneTypeCode(rs.getString("PhoneTypeCode"));
			custphone.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
			custphone.setPhoneAreaCode(rs.getString("PhoneAreaCode"));
			custphone.setPhoneNumber(rs.getString("PhoneNumber"));
			custphone.setPhoneTypePriority((int) rs.getLong("PhoneTypePriority"));
			custphone.setVersion(rs.getInt("Version"));
			custphone.setLastMntBy(rs.getLong("LastMntBy"));
			custphone.setLastMntOn(rs.getTimestamp("LastMntOn"));
			custphone.setRecordStatus(rs.getString("RecordStatus"));
			custphone.setRoleCode(rs.getString("RoleCode"));
			custphone.setNextRoleCode(rs.getString("NextRoleCode"));
			custphone.setTaskId(rs.getString("TaskId"));
			custphone.setNextTaskId(rs.getString("NextTaskId"));
			custphone.setRecordType(rs.getString("RecordType"));
			custphone.setWorkflowId(rs.getLong("WorkflowId"));

			return custphone;
		}, id, typeCode);
	}

	@Override
	public String getPhoneNumberByCustID(long custID) {
		String sql = "Select PhoneNumber from CustomerPhoneNumbers Where PhoneCustID = ? and PhoneTypePriority = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, custID, 5);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}