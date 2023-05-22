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
 * * FileName : CustomerAddresDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 * *
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
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>CustomerAddres model</b> class.<br>
 * 
 */
public class CustomerAddresDAOImpl extends SequenceDao<CustomerAddres> implements CustomerAddresDAO {
	private static Logger logger = LogManager.getLogger(CustomerAddresDAOImpl.class);

	public CustomerAddresDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer Address details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustomerAddres
	 */
	@Override
	public CustomerAddres getCustomerAddresById(final long id, String addType, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustAddressId, CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet");
		sql.append(", CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince, CustAddrPriority");
		sql.append(", CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom, TypeOfResidence, CustAddrLine3");
		sql.append(", CustAddrLine4, CustDistrict, PinCodeId");

		if (type.contains("View")) {
			sql.append(", LovDescCustAddrTypeName, LovDescCustAddrCityName, LovDescCustDistrictName");
			sql.append(", LovDescCustAddrProvinceName, LovDescCustAddrCountryName, LovDescCustAddrZip");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" from CustomerAddresses");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ? and CustAddrType = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CustomerAddres ca = new CustomerAddres();

				ca.setCustAddressId(rs.getLong("CustAddressId"));
				ca.setCustID(rs.getLong("CustID"));
				ca.setCustAddrType(rs.getString("CustAddrType"));
				ca.setCustAddrHNbr(rs.getString("CustAddrHNbr"));
				ca.setCustFlatNbr(rs.getString("CustFlatNbr"));
				ca.setCustAddrStreet(rs.getString("CustAddrStreet"));
				ca.setCustAddrLine1(rs.getString("CustAddrLine1"));
				ca.setCustAddrLine2(rs.getString("CustAddrLine2"));
				ca.setCustPOBox(rs.getString("CustPOBox"));
				ca.setCustAddrCity(rs.getString("CustAddrCity"));
				ca.setCustAddrProvince(rs.getString("CustAddrProvince"));
				ca.setCustAddrPriority(rs.getInt("CustAddrPriority"));
				ca.setCustAddrCountry(rs.getString("CustAddrCountry"));
				ca.setCustAddrZIP(rs.getString("CustAddrZIP"));
				ca.setCustAddrPhone(rs.getString("CustAddrPhone"));
				ca.setCustAddrFrom(rs.getTimestamp("CustAddrFrom"));
				ca.setTypeOfResidence(rs.getString("TypeOfResidence"));
				ca.setCustAddrLine3(rs.getString("CustAddrLine3"));
				ca.setCustAddrLine4(rs.getString("CustAddrLine4"));
				ca.setCustDistrict(rs.getString("CustDistrict"));
				ca.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));

				if (type.contains("View")) {
					ca.setLovDescCustAddrTypeName(rs.getString("LovDescCustAddrTypeName"));
					ca.setLovDescCustAddrCityName(rs.getString("LovDescCustAddrCityName"));
					ca.setLovDescCustAddrProvinceName(rs.getString("LovDescCustAddrProvinceName"));
					ca.setLovDescCustAddrCountryName(rs.getString("LovDescCustAddrCountryName"));
					ca.setLovDescCustAddrZip(rs.getString("LovDescCustAddrZip"));
					ca.setLovDescCustDistrictName(rs.getString("LovDescCustDistrictName"));
				}

				ca.setVersion(rs.getInt("Version"));
				ca.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ca.setLastMntBy(rs.getLong("LastMntBy"));
				ca.setRecordStatus(rs.getString("RecordStatus"));
				ca.setRoleCode(rs.getString("RoleCode"));
				ca.setNextRoleCode(rs.getString("NextRoleCode"));
				ca.setTaskId(rs.getString("TaskId"));
				ca.setNextTaskId(rs.getString("NextTaskId"));
				ca.setRecordType(rs.getString("RecordType"));
				ca.setWorkflowId(rs.getLong("WorkflowId"));

				return ca;
			}, id, addType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	/**
	 * Method For getting List of Customer related Addresses for Customer
	 */
	public List<CustomerAddres> getCustomerAddresByCustomer(final long custId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet, CustAddrLine1");
		sql.append(", CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince, CustAddrPriority, CustAddrCountry");
		sql.append(", CustAddrZIP, CustAddrPhone, CustAddrFrom, TypeOfResidence, CustAddrLine3, CustAddrLine4");
		sql.append(", CustDistrict, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, PinCodeId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCustAddrTypeName, LovDescCustAddrCityName, LovDescCustAddrProvinceName");
			sql.append(", LovDescCustAddrCountryName, LovDescCustAddrZip, LovDescCustDistrictName");
		}

		sql.append(" from CustomerAddresses");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, custId);
		}, (rs, rowNum) -> {
			CustomerAddres ca = new CustomerAddres();

			ca.setCustID(rs.getLong("CustID"));
			ca.setCustAddrType(rs.getString("CustAddrType"));
			ca.setCustAddrHNbr(rs.getString("CustAddrHNbr"));
			ca.setCustFlatNbr(rs.getString("CustFlatNbr"));
			ca.setCustAddrStreet(rs.getString("CustAddrStreet"));
			ca.setCustAddrLine1(rs.getString("CustAddrLine1"));
			ca.setCustAddrLine2(rs.getString("CustAddrLine2"));
			ca.setCustPOBox(rs.getString("CustPOBox"));
			ca.setCustAddrCity(rs.getString("CustAddrCity"));
			ca.setCustAddrProvince(rs.getString("CustAddrProvince"));
			ca.setCustAddrPriority(rs.getInt("CustAddrPriority"));
			ca.setCustAddrCountry(rs.getString("CustAddrCountry"));
			ca.setCustAddrZIP(rs.getString("CustAddrZIP"));
			ca.setCustAddrPhone(rs.getString("CustAddrPhone"));
			ca.setCustAddrFrom(rs.getTimestamp("CustAddrFrom"));
			ca.setTypeOfResidence(rs.getString("TypeOfResidence"));
			ca.setCustAddrLine3(rs.getString("CustAddrLine3"));
			ca.setCustAddrLine4(rs.getString("CustAddrLine4"));
			ca.setCustDistrict(rs.getString("CustDistrict"));
			ca.setVersion(rs.getInt("Version"));
			ca.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ca.setLastMntBy(rs.getLong("LastMntBy"));
			ca.setRecordStatus(rs.getString("RecordStatus"));
			ca.setRoleCode(rs.getString("RoleCode"));
			ca.setNextRoleCode(rs.getString("NextRoleCode"));
			ca.setTaskId(rs.getString("TaskId"));
			ca.setNextTaskId(rs.getString("NextTaskId"));
			ca.setRecordType(rs.getString("RecordType"));
			ca.setWorkflowId(rs.getLong("WorkflowId"));
			ca.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				ca.setLovDescCustAddrTypeName(rs.getString("LovDescCustAddrTypeName"));
				ca.setLovDescCustAddrCityName(rs.getString("LovDescCustAddrCityName"));
				ca.setLovDescCustAddrProvinceName(rs.getString("LovDescCustAddrProvinceName"));
				ca.setLovDescCustAddrCountryName(rs.getString("LovDescCustAddrCountryName"));
				ca.setLovDescCustAddrZip(rs.getString("LovDescCustAddrZip"));
				ca.setLovDescCustDistrictName(rs.getString("LovDescCustDistrictName"));
			}

			return ca;
		});

	}

	/**
	 * This method Deletes the Record from the CustomerAddresses or CustomerAddresses_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Customer Address by key CustID
	 * 
	 * @param Customer Address (customerAddres)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustomerAddres customerAddres, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerAddresses");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID AND CustAddrType =:custAddrType ");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);

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
	 * Method for Deletion of Customer Related List of CustomerAddress for the Customer
	 */
	public void deleteByCustomer(final long customerId, String type) {
		logger.debug("Entering");

		CustomerAddres customerAddres = new CustomerAddres();
		customerAddres.setId(customerId);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From CustomerAddresses");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CustomerAddresses or CustomerAddresses_Temp.
	 * 
	 * save Customer Address
	 * 
	 * @param Customer Address (customerAddres)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustomerAddres customerAddres, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		if (customerAddres.getCustAddressId() == Long.MIN_VALUE) {
			customerAddres.setCustAddressId(getNextValue("SeqCustomerAddresses"));
			logger.debug("get NextID:" + customerAddres.getCustAddressId());
		}
		insertSql.append("Insert Into CustomerAddresses");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustAddressId,CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet,");
		insertSql.append(
				" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCountry, CustAddrProvince, CustAddrPriority,");
		insertSql.append(" CustAddrCity, CustAddrZIP, CustAddrPhone,CustAddrFrom,TypeOfResidence,CustAddrLine3,");
		insertSql.append(" CustAddrLine4, CustDistrict, PinCodeId,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql
				.append(" Values(:CustAddressId,:CustID, :CustAddrType, :CustAddrHNbr, :CustFlatNbr, :CustAddrStreet,");
		insertSql.append(
				" :CustAddrLine1, :CustAddrLine2, :CustPOBox, :CustAddrCountry, :CustAddrProvince, :CustAddrPriority,");
		insertSql
				.append(" :CustAddrCity, :CustAddrZIP, :CustAddrPhone, :CustAddrFrom,:TypeOfResidence,:CustAddrLine3,");
		insertSql.append(" :CustAddrLine4,:CustDistrict, :PinCodeId,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerAddres.getId();
	}

	/**
	 * This method updates the Record CustomerAddresses or CustomerAddresses_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer Address by key CustID and Version
	 * 
	 * @param Customer Address (customerAddres)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustomerAddres customerAddres, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerAddresses");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustAddrHNbr = :CustAddrHNbr, CustFlatNbr = :CustFlatNbr,");
		updateSql.append(" CustAddrStreet = :CustAddrStreet, CustAddrLine1 = :CustAddrLine1,");
		updateSql.append(" CustAddrLine2 = :CustAddrLine2, CustPOBox = :CustPOBox,");
		updateSql.append(
				" CustAddrCountry = :CustAddrCountry, CustAddrProvince = :CustAddrProvince, CustAddrPriority = :CustAddrPriority, ");
		updateSql.append(" CustAddrCity = :CustAddrCity, CustAddrZIP = :CustAddrZIP,");
		updateSql.append(
				" CustAddrPhone = :CustAddrPhone,TypeOfResidence = :TypeOfResidence,CustAddrLine3=:CustAddrLine3,CustAddrLine4=:CustAddrLine4,");
		updateSql.append(" CustDistrict = :CustDistrict, PinCodeId = :PinCodeId,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType,");
		updateSql.append(" WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID AND CustAddrType =:custAddrType");

		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for get total number of records from BMTAddressTypes master table.<br>
	 * 
	 * @param addrType
	 * 
	 * @return Integer
	 */
	@Override
	public int getAddrTypeCount(String addrType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AddrTypeCode", addrType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM BMTAddressTypes");
		selectSql.append(" WHERE ");
		selectSql.append("AddrTypeCode= :AddrTypeCode");

		logger.debug("insertSql: " + selectSql.toString());
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	/**
	 * Method for get total number of records from BMTAddressTypes master table.<br>
	 * 
	 * @param addrType
	 * 
	 * @return Integer
	 */
	@Override
	public int getcustAddressCount(String addrType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustAddrType", addrType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM CustomerAddresses");
		selectSql.append(" WHERE ");
		selectSql.append("CustAddrType= :CustAddrType");

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
	public int getVersion(long id, String addrType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", id);
		source.addValue("CustAddrType", addrType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT Version FROM CustomerAddresses");

		selectSql.append(" WHERE CustId = :CustId AND CustAddrType = :CustAddrType");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public boolean isServiceable(long pinCodeId) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("pinCodeId", pinCodeId);

		StringBuilder selectSql = new StringBuilder("SELECT serviceable");
		selectSql.append(" From PinCodes");
		selectSql.append(" Where pinCodeId = :pinCodeId");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class) > 0;
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	/**
	 * Fetch the Record Customer Address details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustomerAddres
	 */
	@Override
	public CustomerAddres getHighPriorityCustAddr(final long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet, CustAddrLine1");
		sql.append(", CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince, CustAddrPriority, CustAddrCountry");
		sql.append(", CustAddrZIP, CustAddrPhone, CustAddrFrom, TypeOfResidence, CustAddrLine3, CustAddrLine4");
		sql.append(", CustDistrict, PinCodeId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCustAddrTypeName, LovDescCustAddrCityName, LovDescCustAddrProvinceName");
			sql.append(", LovDescCustAddrCountryName, LovDescCustAddrZip, LovDescCustDistrictName");
		}

		sql.append(" From CustomerAddresses");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("  Where CustID = ? and CustAddrPriority = ?");

		int priority = Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH);

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CustomerAddres ca = new CustomerAddres();

				ca.setCustID(rs.getLong("CustID"));
				ca.setCustAddrType(rs.getString("CustAddrType"));
				ca.setCustAddrHNbr(rs.getString("CustAddrHNbr"));
				ca.setCustFlatNbr(rs.getString("CustFlatNbr"));
				ca.setCustAddrStreet(rs.getString("CustAddrStreet"));
				ca.setCustAddrLine1(rs.getString("CustAddrLine1"));
				ca.setCustAddrLine2(rs.getString("CustAddrLine2"));
				ca.setCustPOBox(rs.getString("CustPOBox"));
				ca.setCustAddrCity(rs.getString("CustAddrCity"));
				ca.setCustAddrProvince(rs.getString("CustAddrProvince"));
				ca.setCustAddrPriority(rs.getInt("CustAddrPriority"));
				ca.setCustAddrCountry(rs.getString("CustAddrCountry"));
				ca.setCustAddrZIP(rs.getString("CustAddrZIP"));
				ca.setCustAddrPhone(rs.getString("CustAddrPhone"));
				ca.setCustAddrFrom(rs.getTimestamp("CustAddrFrom"));
				ca.setTypeOfResidence(rs.getString("TypeOfResidence"));
				ca.setCustAddrLine3(rs.getString("CustAddrLine3"));
				ca.setCustAddrLine4(rs.getString("CustAddrLine4"));
				ca.setCustDistrict(rs.getString("CustDistrict"));
				ca.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					ca.setLovDescCustAddrTypeName(rs.getString("LovDescCustAddrTypeName"));
					ca.setLovDescCustAddrCityName(rs.getString("LovDescCustAddrCityName"));
					ca.setLovDescCustAddrProvinceName(rs.getString("LovDescCustAddrProvinceName"));
					ca.setLovDescCustAddrCountryName(rs.getString("LovDescCustAddrCountryName"));
					ca.setLovDescCustAddrZip(rs.getString("LovDescCustAddrZip"));
					ca.setLovDescCustDistrictName(rs.getString("LovDescCustDistrictName"));
				}

				return ca;
			}, id, priority);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Record not found in CustomerAddresses{} table for the specified CustID >> {} and CustAddrPriority",
					type, id, priority);
		}

		return null;
	}

	@Override
	public String getCustHighPriorityAddr(final long id) {
		String sql = "Select CustAddrProvince From CustomerAddresses Where CustID = ? and CustAddrPriority = ?";

		int priority = Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH);

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, id, priority);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isExisiCustPincode(long id) {
		String sql = "Select Count(PinCodeId) From CustomerAddresses Where PinCodeId = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, id) > 0;
	}

	@Override
	public List<CustomerAddres> getCustomerAddresById(final long id, long priority) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustAddressId, CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet");
		sql.append(", CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince, CustAddrPriority");
		sql.append(", CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom, TypeOfResidence, CustAddrLine3");
		sql.append(", CustAddrLine4, CustDistrict, PinCodeId");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" From CustomerAddresses");
		sql.append(" Where CustID = ? and CustAddrPriority = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CustomerAddres ca = new CustomerAddres();

			ca.setCustAddressId(rs.getLong("CustAddressId"));
			ca.setCustID(rs.getLong("CustID"));
			ca.setCustAddrType(rs.getString("CustAddrType"));
			ca.setCustAddrHNbr(rs.getString("CustAddrHNbr"));
			ca.setCustFlatNbr(rs.getString("CustFlatNbr"));
			ca.setCustAddrStreet(rs.getString("CustAddrStreet"));
			ca.setCustAddrLine1(rs.getString("CustAddrLine1"));
			ca.setCustAddrLine2(rs.getString("CustAddrLine2"));
			ca.setCustPOBox(rs.getString("CustPOBox"));
			ca.setCustAddrCity(rs.getString("CustAddrCity"));
			ca.setCustAddrProvince(rs.getString("CustAddrProvince"));
			ca.setCustAddrPriority(rs.getInt("CustAddrPriority"));
			ca.setCustAddrCountry(rs.getString("CustAddrCountry"));
			ca.setCustAddrZIP(rs.getString("CustAddrZIP"));
			ca.setCustAddrPhone(rs.getString("CustAddrPhone"));
			ca.setCustAddrFrom(rs.getTimestamp("CustAddrFrom"));
			ca.setTypeOfResidence(rs.getString("TypeOfResidence"));
			ca.setCustAddrLine3(rs.getString("CustAddrLine3"));
			ca.setCustAddrLine4(rs.getString("CustAddrLine4"));
			ca.setCustDistrict(rs.getString("CustDistrict"));
			ca.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));
			ca.setVersion(rs.getInt("Version"));
			ca.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ca.setLastMntBy(rs.getLong("LastMntBy"));
			ca.setRecordStatus(rs.getString("RecordStatus"));
			ca.setRoleCode(rs.getString("RoleCode"));
			ca.setNextRoleCode(rs.getString("NextRoleCode"));
			ca.setTaskId(rs.getString("TaskId"));
			ca.setNextTaskId(rs.getString("NextTaskId"));
			ca.setRecordType(rs.getString("RecordType"));
			ca.setWorkflowId(rs.getLong("WorkflowId"));

			return ca;
		}, id, priority);
	}

	@Override
	public List<CustomerAddres> getCustomerAddresById(final long id, String addType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustAddressId, CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet");
		sql.append(", CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince, CustAddrPriority");
		sql.append(", CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom, TypeOfResidence, CustAddrLine3");
		sql.append(", CustAddrLine4, CustDistrict, PinCodeId");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" From CustomerAddresses");
		sql.append(" Where CustID = ? and CustAddrType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CustomerAddres ca = new CustomerAddres();

			ca.setCustAddressId(rs.getLong("CustAddressId"));
			ca.setCustID(rs.getLong("CustID"));
			ca.setCustAddrType(rs.getString("CustAddrType"));
			ca.setCustAddrHNbr(rs.getString("CustAddrHNbr"));
			ca.setCustFlatNbr(rs.getString("CustFlatNbr"));
			ca.setCustAddrStreet(rs.getString("CustAddrStreet"));
			ca.setCustAddrLine1(rs.getString("CustAddrLine1"));
			ca.setCustAddrLine2(rs.getString("CustAddrLine2"));
			ca.setCustPOBox(rs.getString("CustPOBox"));
			ca.setCustAddrCity(rs.getString("CustAddrCity"));
			ca.setCustAddrProvince(rs.getString("CustAddrProvince"));
			ca.setCustAddrPriority(rs.getInt("CustAddrPriority"));
			ca.setCustAddrCountry(rs.getString("CustAddrCountry"));
			ca.setCustAddrZIP(rs.getString("CustAddrZIP"));
			ca.setCustAddrPhone(rs.getString("CustAddrPhone"));
			ca.setCustAddrFrom(rs.getTimestamp("CustAddrFrom"));
			ca.setTypeOfResidence(rs.getString("TypeOfResidence"));
			ca.setCustAddrLine3(rs.getString("CustAddrLine3"));
			ca.setCustAddrLine4(rs.getString("CustAddrLine4"));
			ca.setCustDistrict(rs.getString("CustDistrict"));
			ca.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));
			ca.setVersion(rs.getInt("Version"));
			ca.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ca.setLastMntBy(rs.getLong("LastMntBy"));
			ca.setRecordStatus(rs.getString("RecordStatus"));
			ca.setRoleCode(rs.getString("RoleCode"));
			ca.setNextRoleCode(rs.getString("NextRoleCode"));
			ca.setTaskId(rs.getString("TaskId"));
			ca.setNextTaskId(rs.getString("NextTaskId"));
			ca.setRecordType(rs.getString("RecordType"));
			ca.setWorkflowId(rs.getLong("WorkflowId"));

			return ca;
		}, id, addType);
	}

}