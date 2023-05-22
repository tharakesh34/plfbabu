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
 * * FileName : VehicleDealerDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-09-2011 * * Modified
 * Date : 29-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-09-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.amtmasters.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>VehicleDealer model</b> class.<br>
 */
public class VehicleDealerDAOImpl extends SequenceDao<VehicleDealer> implements VehicleDealerDAO {
	private static Logger logger = LogManager.getLogger(VehicleDealerDAOImpl.class);

	public VehicleDealerDAOImpl() {
		super();
	}

	/**
	 * get the Collection Tables List
	 */
	public List<VehicleDealer> getVehicleDealerList(String dealerType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DealerId, DealerType, DealerName, DealerTelephone, DealerFax, DealerAddress1");
		sql.append(", DealerAddress2, DealerAddress3, DealerAddress4, DealerCountry, DealerCity, DealerProvince");
		sql.append(", Email, POBox, ZipCode, Active, Emirates, CommisionPaidAt, Code, ShortCode, CalculationRule");
		sql.append(", PaymentMode, AccountNumber, AccountingSetId, PanNumber, UidNumber, TaxNumber");
		sql.append(", Fromprovince, Toprovince, AccountNo, AccountType, BankBranchID, Version");
		sql.append(", LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, SellerType, BranchCode, PinCodeId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCountry, LovDescCity, LovDescProvince, CalRuleDesc");
			sql.append(", AccountingSetCode, AccountingSetDesc, EmiratesDescription");
			sql.append(", FromprovinceName, ToprovinceName, BankBranchCode, BankBranchCodeName, BankName");
			sql.append(", BranchIFSCCode, BranchMICRCode, BranchCity, AreaName");
		}

		sql.append(" from AMTVehicleDealer");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DealerType = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index, dealerType);
			}
		}, new RowMapper<VehicleDealer>() {
			@Override
			public VehicleDealer mapRow(ResultSet rs, int rowNum) throws SQLException {
				VehicleDealer vd = new VehicleDealer();

				vd.setDealerId(rs.getLong("DealerId"));
				vd.setDealerType(rs.getString("DealerType"));
				vd.setDealerName(rs.getString("DealerName"));
				vd.setDealerTelephone(rs.getString("DealerTelephone"));
				vd.setDealerFax(rs.getString("DealerFax"));
				vd.setDealerAddress1(rs.getString("DealerAddress1"));
				vd.setDealerAddress2(rs.getString("DealerAddress2"));
				vd.setDealerAddress3(rs.getString("DealerAddress3"));
				vd.setDealerAddress4(rs.getString("DealerAddress4"));
				vd.setDealerCountry(rs.getString("DealerCountry"));
				vd.setDealerCity(rs.getString("DealerCity"));
				vd.setDealerProvince(rs.getString("DealerProvince"));
				vd.setEmail(rs.getString("Email"));
				vd.setPOBox(rs.getString("POBox"));
				vd.setZipCode(rs.getString("ZipCode"));
				vd.setActive(rs.getBoolean("Active"));
				vd.setEmirates(rs.getString("Emirates"));
				vd.setCommisionPaidAt(rs.getString("CommisionPaidAt"));
				vd.setCode(rs.getString("Code"));
				vd.setShortCode(rs.getString("ShortCode"));
				vd.setCalculationRule(rs.getString("CalculationRule"));
				vd.setPaymentMode(rs.getString("PaymentMode"));
				vd.setAccountNumber(rs.getString("AccountNumber"));
				vd.setAccountingSetId(JdbcUtil.getLong(rs.getObject("AccountingSetId")));
				vd.setPanNumber(rs.getString("PanNumber"));
				vd.setUidNumber(rs.getString("UidNumber"));
				vd.setTaxNumber(rs.getString("TaxNumber"));
				vd.setFromprovince(rs.getString("Fromprovince"));
				vd.setToprovince(rs.getString("Toprovince"));
				vd.setAccountNo(rs.getString("AccountNo"));
				vd.setAccountType(rs.getString("AccountType"));
				vd.setBankBranchID(JdbcUtil.getLong(rs.getObject("BankBranchID")));
				vd.setVersion(rs.getInt("Version"));
				vd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				vd.setLastMntBy(rs.getLong("LastMntBy"));
				vd.setRecordStatus(rs.getString("RecordStatus"));
				vd.setRoleCode(rs.getString("RoleCode"));
				vd.setNextRoleCode(rs.getString("NextRoleCode"));
				vd.setTaskId(rs.getString("TaskId"));
				vd.setNextTaskId(rs.getString("NextTaskId"));
				vd.setRecordType(rs.getString("RecordType"));
				vd.setWorkflowId(rs.getLong("WorkflowId"));
				vd.setSellerType(rs.getString("SellerType"));
				vd.setBranchCode(rs.getString("BranchCode"));
				vd.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					vd.setLovDescCountry(rs.getString("LovDescCountry"));
					vd.setLovDescCity(rs.getString("LovDescCity"));
					vd.setLovDescProvince(rs.getString("LovDescProvince"));
					vd.setCalRuleDesc(rs.getString("CalRuleDesc"));
					vd.setAccountingSetCode(rs.getString("AccountingSetCode"));
					vd.setAccountingSetDesc(rs.getString("AccountingSetDesc"));
					vd.setEmiratesDescription(rs.getString("EmiratesDescription"));
					vd.setFromprovinceName(rs.getString("FromprovinceName"));
					vd.setToprovinceName(rs.getString("ToprovinceName"));
					vd.setBankBranchCode(rs.getString("BankBranchCode"));
					vd.setBankBranchCodeName(rs.getString("BankBranchCodeName"));
					vd.setBankName(rs.getString("BankName"));
					vd.setBranchIFSCCode(rs.getString("BranchIFSCCode"));
					vd.setBranchMICRCode(rs.getString("BranchMICRCode"));
					vd.setBranchCity(rs.getString("BranchCity"));
					vd.setAreaName(rs.getString("AreaName"));
				}

				return vd;
			}
		});
	}

	/**
	 * Fetch the Record Vehicle Dealer details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return VehicleDealer
	 */
	@Override
	public VehicleDealer getVehicleDealerById(final long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DealerId, DealerType, DealerName, DealerTelephone, DealerFax, DealerAddress1");
		sql.append(", DealerAddress2, DealerAddress3, DealerAddress4, DealerCountry, DealerCity, DealerProvince");
		sql.append(", Email, POBox, ZipCode, Active, Emirates, CommisionPaidAt, Code, ProductCtg, ShortCode");
		sql.append(", CalculationRule, PaymentMode, AccountNumber, AccountingSetId, PanNumber, UidNumber");
		sql.append(", TaxNumber, Fromprovince, Toprovince, AccountNo, AccountType, BankBranchID");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId, SellerType, BranchCode, PinCodeId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescCountry, LovDescCity");
			sql.append(", LovDescProvince, CalRuleDesc, AccountingSetCode, AccountingSetDesc, EmiratesDescription");
			sql.append(", ProductCtgDesc, FromprovinceName, ToprovinceName, BankBranchCode, BankBranchCodeName");
			sql.append(", BankName, BranchIFSCCode, BranchMICRCode, BranchCity, AreaName");
		}

		sql.append(" from AMTVehicleDealer");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DealerId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				VehicleDealer vd = new VehicleDealer();

				vd.setDealerId(rs.getLong("DealerId"));
				vd.setDealerType(rs.getString("DealerType"));
				vd.setDealerName(rs.getString("DealerName"));
				vd.setDealerTelephone(rs.getString("DealerTelephone"));
				vd.setDealerFax(rs.getString("DealerFax"));
				vd.setDealerAddress1(rs.getString("DealerAddress1"));
				vd.setDealerAddress2(rs.getString("DealerAddress2"));
				vd.setDealerAddress3(rs.getString("DealerAddress3"));
				vd.setDealerAddress4(rs.getString("DealerAddress4"));
				vd.setDealerCountry(rs.getString("DealerCountry"));
				vd.setDealerCity(rs.getString("DealerCity"));
				vd.setDealerProvince(rs.getString("DealerProvince"));
				vd.setEmail(rs.getString("Email"));
				vd.setPOBox(rs.getString("POBox"));
				vd.setZipCode(rs.getString("ZipCode"));
				vd.setActive(rs.getBoolean("Active"));
				vd.setEmirates(rs.getString("Emirates"));
				vd.setCommisionPaidAt(rs.getString("CommisionPaidAt"));
				vd.setCode(rs.getString("Code"));
				vd.setProductCtg(rs.getString("ProductCtg"));
				vd.setShortCode(rs.getString("ShortCode"));
				vd.setCalculationRule(rs.getString("CalculationRule"));
				vd.setPaymentMode(rs.getString("PaymentMode"));
				vd.setAccountNumber(rs.getString("AccountNumber"));
				vd.setAccountingSetId(JdbcUtil.getLong(rs.getObject("AccountingSetId")));
				vd.setPanNumber(rs.getString("PanNumber"));
				vd.setUidNumber(rs.getString("UidNumber"));
				vd.setTaxNumber(rs.getString("TaxNumber"));
				vd.setFromprovince(rs.getString("Fromprovince"));
				vd.setToprovince(rs.getString("Toprovince"));
				vd.setAccountNo(rs.getString("AccountNo"));
				vd.setAccountType(rs.getString("AccountType"));
				vd.setBankBranchID(JdbcUtil.getLong(rs.getObject("BankBranchID")));
				vd.setVersion(rs.getInt("Version"));
				vd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				vd.setLastMntBy(rs.getLong("LastMntBy"));
				vd.setRecordStatus(rs.getString("RecordStatus"));
				vd.setRoleCode(rs.getString("RoleCode"));
				vd.setNextRoleCode(rs.getString("NextRoleCode"));
				vd.setTaskId(rs.getString("TaskId"));
				vd.setNextTaskId(rs.getString("NextTaskId"));
				vd.setRecordType(rs.getString("RecordType"));
				vd.setWorkflowId(rs.getLong("WorkflowId"));
				vd.setSellerType(rs.getString("SellerType"));
				vd.setBranchCode(rs.getString("BranchCode"));
				vd.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					vd.setLovDescCountry(rs.getString("LovDescCountry"));
					vd.setLovDescCity(rs.getString("LovDescCity"));
					vd.setLovDescProvince(rs.getString("LovDescProvince"));
					vd.setCalRuleDesc(rs.getString("CalRuleDesc"));
					vd.setAccountingSetCode(rs.getString("AccountingSetCode"));
					vd.setAccountingSetDesc(rs.getString("AccountingSetDesc"));
					vd.setEmiratesDescription(rs.getString("EmiratesDescription"));
					vd.setProductCtgDesc(rs.getString("ProductCtgDesc"));
					vd.setFromprovinceName(rs.getString("FromprovinceName"));
					vd.setToprovinceName(rs.getString("ToprovinceName"));
					vd.setBankBranchCode(rs.getString("BankBranchCode"));
					vd.setBankBranchCodeName(rs.getString("BankBranchCodeName"));
					vd.setBankName(rs.getString("BankName"));
					vd.setBranchIFSCCode(rs.getString("BranchIFSCCode"));
					vd.setBranchMICRCode(rs.getString("BranchMICRCode"));
					vd.setBranchCity(rs.getString("BranchCity"));
					vd.setAreaName(rs.getString("AreaName"));
				}

				return vd;

			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public VehicleDealer getOverDraftVehicleDealerById(String type) {
		logger.debug("Entering");
		VehicleDealer vehicleDealer = new VehicleDealer();
		vehicleDealer.setDealerType("SOPT");
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT dealername,dealercity FROM AMTVehicleDealer_AView");
		selectSql.append(
				" Where DealerType =:DealerType AND Active = 1 ORDER BY DealerName ASC,DealerCity ASC LIMIT 10");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		RowMapper<VehicleDealer> typeRowMapper = BeanPropertyRowMapper.newInstance(VehicleDealer.class);

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("DealerType", "SOPT");

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the AMTVehicleDealer or AMTVehicleDealer_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Vehicle Dealer by key DealerId
	 * 
	 * @param Vehicle Dealer (vehicleDealer)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(VehicleDealer vehicleDealer, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From AMTVehicleDealer");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DealerId =:DealerId");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
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
	 * This method insert new Records into AMTVehicleDealer or AMTVehicleDealer_Temp. it fetches the available Sequence
	 * form SeqAMTVehicleDealer by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Vehicle Dealer
	 * 
	 * @param Vehicle Dealer (vehicleDealer)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(VehicleDealer vehicleDealer, String type) {
		logger.debug("Entering");
		if (vehicleDealer.getId() == Long.MIN_VALUE) {
			vehicleDealer.setId(getNextValue("SeqAMTVehicleDealer"));
			logger.debug("get NextValue:" + vehicleDealer.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into AMTVehicleDealer");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(DealerId,DealerType, DealerName,DealerTelephone,DealerFax,DealerAddress1,DealerAddress2, ");
		insertSql.append("DealerAddress3,DealerAddress4,DealerCountry,DealerCity,DealerProvince,");
		insertSql.append(
				"Email,POBox,ZipCode,Active,Emirates,CommisionPaidAt,CalculationRule,PaymentMode,AccountNumber,");
		insertSql.append(
				"AccountingSetId,Code,ProductCtg,ShortCode,PANNumber,UIDNumber,TaxNumber,fromprovince,toprovince,");
		insertSql.append(
				"AccountNo,AccountType,BankBranchID, PinCodeId, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,SellerType, BranchCode)");

		insertSql.append(" Values(:DealerId,:DealerType, :DealerName,:DealerTelephone,:DealerFax,:DealerAddress1,");
		insertSql
				.append(" :DealerAddress2,:DealerAddress3,:DealerAddress4,:DealerCountry,:DealerCity,:DealerProvince,");
		insertSql.append(
				" :Email,:POBox,:ZipCode,:Active,:Emirates,:CommisionPaidAt,:CalculationRule,:PaymentMode,:AccountNumber,:AccountingSetId,:Code ,:ProductCtg,:ShortCode, ");
		insertSql.append(
				" :panNumber,:uidNumber,:taxNumber,:fromprovince,:toprovince,:accountNo,:accountType,:bankBranchID, :PinCodeId,:Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId,:SellerType, :BranchCode)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vehicleDealer.getId();
	}

	/**
	 * This method updates the Record AMTVehicleDealer or AMTVehicleDealer_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Vehicle Dealer by key DealerId and Version
	 * 
	 * @param Vehicle Dealer (vehicleDealer)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(VehicleDealer vehicleDealer, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update AMTVehicleDealer");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DealerType = :DealerType, DealerName = :DealerName, ");
		updateSql.append("DealerTelephone =:DealerTelephone,DealerFax = :DealerFax,DealerAddress1 = :DealerAddress1,");
		updateSql.append(
				"DealerAddress2 = :DealerAddress2,DealerAddress3 = :DealerAddress3,DealerAddress4 = :DealerAddress4,");
		updateSql.append(" DealerCountry = :DealerCountry,DealerCity = :DealerCity,DealerProvince = :DealerProvince,");
		updateSql.append(
				" Email = :Email,POBox = :POBox,ZipCode = :ZipCode,Active = :Active,Emirates = :Emirates,CommisionPaidAt = :CommisionPaidAt,");
		updateSql.append(
				"CalculationRule = :CalculationRule,PaymentMode = :PaymentMode,AccountNumber = :AccountNumber,AccountingSetId = :AccountingSetId,Code = :Code, ProductCtg = :ProductCtg,ShortCode =:ShortCode, ");

		updateSql.append(
				" PANNumber = :panNumber ,UIDNumber = :uidNumber ,TaxNumber =:taxNumber,fromprovince =:fromprovince,toprovince = :toprovince, AccountNo =:accountNo, ");
		updateSql.append(
				"AccountType = :accountType,BankBranchID = :bankBranchID, PinCodeId = :pinCodeId, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(
				" RecordType = :RecordType, WorkflowId = :WorkflowId,SellerType = :SellerType, BranchCode=:BranchCode ");
		updateSql.append(" Where DealerId =:DealerId");

		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public boolean SearchName(String dealerName, String dealerType) {
		logger.debug("Entering");

		String searchQuery = "select count(dealerName) from AMTVehicleDealer_View where dealerName=:dealerName and dealerType=:dealerType";

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("dealerName", dealerName);
		parameterSource.addValue("dealerType", dealerType);

		return jdbcTemplate.queryForObject(searchQuery, parameterSource, Integer.class) > 0;
	}

	/**
	 * Fetch the Record VehicleDealer details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return count
	 */
	@Override
	public int getVehicleDealerByType(final String dealerType, String name, long id, String type) {
		logger.debug("Entering");
		VehicleDealer vehicleDealer = new VehicleDealer();

		vehicleDealer.setDealerType(dealerType);
		vehicleDealer.setDealerName(name);
		vehicleDealer.setDealerId(id);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From AMTVehicleDealer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DealerType =:DealerType AND DealerName =:DealerName AND DealerId !=:DealerId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public int getVASManufactureCode(String dealerName, String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ManufacturerName", dealerName);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From VASStructure");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ManufacturerName =:ManufacturerName ");

		logger.debug("selectSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public List<VehicleDealer> getVehicleDealerById(List<Long> dealerIds) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DealerName, DealerCity, DealerId, Code");
		sql.append(" From AMTVehicleDealer");
		sql.append(" Where DealerId IN(");

		int i = 0;

		while (i < dealerIds.size()) {
			sql.append(" ?,");
			i++;
		}

		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			for (Long id : dealerIds) {
				ps.setLong(index++, id);
			}

		}, (rs, rowNum) -> {
			VehicleDealer vd = new VehicleDealer();

			vd.setCode(rs.getString("Code"));
			vd.setDealerName(rs.getString("DealerName"));
			vd.setDealerCity(rs.getString("DealerCity"));
			vd.setDealerId(rs.getLong("DealerId"));

			return vd;
		});

	}

	@Override
	public int getVehicleDealerByCode(String code, String dealerType, long id, String type) {
		logger.debug("Entering");
		VehicleDealer vehicleDealer = new VehicleDealer();

		vehicleDealer.setDealerType(dealerType);
		vehicleDealer.setDealerId(id);
		vehicleDealer.setCode(code);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From AMTVehicleDealer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DealerType =:DealerType AND Code =:Code AND DealerId !=:DealerId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public VehicleDealer getDealerShortCodes(String shortCode) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder(
				" Select AV.SHORTCODE DealerShortCode, VS.SHORTCODE ProductShortCode from AMTVEHICLEDEALER AV");
		sql.append(" INNER JOIN VASSTRUCTURE VS ON  AV.DEALERID = VS.MANUFACTURERID");
		sql.append("  Where VS.PRODUCTCODE = :PRODUCTCODE");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PRODUCTCODE", shortCode);

		RowMapper<VehicleDealer> typeRowMapper = BeanPropertyRowMapper.newInstance(VehicleDealer.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
	}

	@Override
	public VehicleDealer getDealerShortCode(long providerId) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("Select SHORTCODE DealerShortCode  from AMTVEHICLEDEALER");
		sql.append(" Where DEALERID = :DEALERID");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DEALERID", providerId);

		RowMapper<VehicleDealer> typeRowMapper = BeanPropertyRowMapper.newInstance(VehicleDealer.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
	}

	@Override
	public VehicleDealer getVehicleDealerById(String code, String dealerType, String type) {

		logger.debug("Entering");
		VehicleDealer vehicleDealer = new VehicleDealer();

		vehicleDealer.setDealerType(dealerType);
		vehicleDealer.setCode(code);
		vehicleDealer.setActive(true);
		StringBuilder selectSql = new StringBuilder("SELECT DealerId");
		selectSql.append(" From AMTVehicleDealer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DealerType =:DealerType AND Code =:Code AND Active =:Active");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		RowMapper<VehicleDealer> typeRowMapper = BeanPropertyRowMapper.newInstance(VehicleDealer.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<VehicleDealer> getVehicleDealerBranchCodes(String dealerType, String type) {
		logger.debug("Entering");
		VehicleDealer vehicleDealer = new VehicleDealer();
		vehicleDealer.setDealerType(dealerType);

		StringBuilder selectSql = new StringBuilder("SELECT dealername,branchcode ");
		selectSql.append("from AMTVehicleDealer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where DealerType=:DealerType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		RowMapper<VehicleDealer> typeRowMapper = BeanPropertyRowMapper.newInstance(VehicleDealer.class);
		List<VehicleDealer> vehicleDealerBranchCodeList = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return vehicleDealerBranchCodeList;
	}

	@Override
	public VehicleDealer getVehicleDealerById(Long id, String dealerType, String type) {
		logger.debug(Literal.ENTERING);

		VehicleDealer vehicleDealer = new VehicleDealer();
		vehicleDealer.setDealerType(dealerType);
		vehicleDealer.setDealerId(id);
		vehicleDealer.setActive(true);
		StringBuilder selectSql = new StringBuilder("SELECT DealerId, DealerName ,DealerCity");
		selectSql.append(" From AMTVehicleDealer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DealerId =:DealerId AND DealerType =:DealerType AND Active =:Active");

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		RowMapper<VehicleDealer> typeRowMapper = BeanPropertyRowMapper.newInstance(VehicleDealer.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getApprovedVehicleDealerCountById(long dealerId, String delarType) {
		String sql = "Select Coalesce(Count(*), 0) From AMTVehicleDealer Where DealerType = ? and DealerId = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, delarType, dealerId);
	}

	@Override
	public boolean isValidDealer(long dealerId) {
		String sql = "Select Coalesce(Count(DealerId), 0) From AMTVehicleDealer Where DealerId = ? and DealerType = ? and Active = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, dealerId, VASConsatnts.VASAGAINST_PARTNER, 1) > 0;
	}

}