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
 * FileName    		:  VehicleDealerDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.amtmasters.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>VehicleDealer model</b> class.<br>
 */
public class VehicleDealerDAOImpl extends SequenceDao<VehicleDealer> implements VehicleDealerDAO {
	private static Logger logger = Logger.getLogger(VehicleDealerDAOImpl.class);

	public VehicleDealerDAOImpl() {
		super();
	}

	/*
	 * @Override public List<VehicleDealer> getVehicleDealerList(String type) { logger.debug("Entering");
	 * List<VehicleDealer> vehicleDealer = new ArrayList<VehicleDealer>(); StringBuilder selectSql = new
	 * StringBuilder(); selectSql.
	 * append("SELECT DealerId,DealerType, DealerName,DealerTelephone,DealerFax,DealerAddress1,DealerAddress2, ");
	 * selectSql.append(
	 * "DealerAddress3,DealerAddress4,DealerCountry,DealerCity,DealerProvince,Email,POBox,ZipCode,Active,Emirates,CommisionPaidAt,Code,"
	 * ); selectSql.append("CalculationRule,PaymentMode,AccountNumber,AccountingSetId,");
	 * selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, " );
	 * selectSql.append(" NextRoleCode,TaskId, NextTaskId, RecordType, WorkflowId,SellerType ");
	 * selectSql.append(",LovDescCountry,LovDescCity,LovDescProvince,");
	 * selectSql.append("CalRuleDesc,AccountingSetCode,AccountingSetDesc,EmiratesDescription");
	 * selectSql.append(" FROM  AMTVehicleDealer"); selectSql.append(StringUtils.trimToEmpty(type));
	 * logger.debug("selectSql: " + selectSql.toString()); SqlParameterSource beanParameters = new
	 * BeanPropertySqlParameterSource(vehicleDealer); RowMapper<VehicleDealer> typeRowMapper =
	 * ParameterizedBeanPropertyRowMapper .newInstance(VehicleDealer.class);
	 * 
	 * try { vehicleDealer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper); }
	 * catch (EmptyResultDataAccessException e) { logger.warn("Exception: ", e); vehicleDealer = null; }
	 * logger.debug("Leaving"); return vehicleDealer;
	 * 
	 * }
	 */

	/**
	 * get the Collection Tables List
	 */
	public List<VehicleDealer> getVehicleDealerList(String dealerType, String type) {
		logger.debug("Entering");
		VehicleDealer vehicleDealer = new VehicleDealer();
		vehicleDealer.setDealerType(dealerType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				"SELECT DealerId,DealerType, DealerName,DealerTelephone,DealerFax,DealerAddress1,DealerAddress2, ");
		selectSql.append(
				"DealerAddress3,DealerAddress4,DealerCountry,DealerCity,DealerProvince,Email,POBox,ZipCode,Active,Emirates,CommisionPaidAt,Code,ShortCode,");
		selectSql.append(
				"CalculationRule,PaymentMode,AccountNumber,AccountingSetId,PANNumber,UIDNumber,TaxNumber,FromProvince,ToProvince,AccountNo,AccountType,BankBranchID,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode,TaskId, NextTaskId, RecordType, WorkflowId,SellerType, BranchCode ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",LovDescCountry,LovDescCity,LovDescProvince,");
			selectSql.append("CalRuleDesc,AccountingSetCode,AccountingSetDesc,EmiratesDescription,");
			selectSql.append(
					"fromprovinceName,toprovinceName,bankBranchCode,bankBranchCodeName,bankName,branchIFSCCode,branchMICRCode,branchCity");
		}
		selectSql.append(" FROM  AMTVehicleDealer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DealerType =:DealerType ");
		logger.debug("selectSql: " + selectSql.toString());

		// MapSqlParameterSource source = new MapSqlParameterSource();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);

		/*
		 * StringBuilder selectSql = new
		 * StringBuilder("select TABLE_NAME TableName, Status, ERROR_DESC ErrorMessage, EFFECTED_COUNT InsertCount" );
		 * selectSql.append(" From COLLECTIONS_TABLES");
		 */

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<VehicleDealer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VehicleDealer.class);
		List<VehicleDealer> collectionsVehicleDealerList = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");

		return collectionsVehicleDealerList;
	}

	/**
	 * Fetch the Record Vehicle Dealer details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VehicleDealer
	 */
	@Override
	public VehicleDealer getVehicleDealerById(final long id, String type) {
		logger.debug("Entering");
		VehicleDealer vehicleDealer = new VehicleDealer();
		vehicleDealer.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(
				"SELECT DealerId,DealerType, DealerName,DealerTelephone,DealerFax,DealerAddress1,DealerAddress2, ");
		selectSql.append(
				"DealerAddress3,DealerAddress4,DealerCountry,DealerCity,DealerProvince,Email,POBox,ZipCode,Active,Emirates,CommisionPaidAt,Code,ProductCtg,ShortCode,");
		selectSql.append(
				"CalculationRule,PaymentMode,AccountNumber,AccountingSetId,PANNumber,UIDNumber,TaxNumber,FromProvince,ToProvince,AccountNo,AccountType,BankBranchID,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode,TaskId, NextTaskId, RecordType, WorkflowId,SellerType, BranchCode");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",LovDescCountry,LovDescCity,LovDescProvince,");
			selectSql.append("CalRuleDesc,AccountingSetCode,AccountingSetDesc,EmiratesDescription,ProductCtgDesc,");
			selectSql.append(
					"fromprovinceName,toprovinceName,bankBranchCode,bankBranchCodeName,bankName,branchIFSCCode,branchMICRCode,branchCity");
		}
		selectSql.append(" FROM  AMTVehicleDealer");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DealerId =:DealerId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vehicleDealer);
		RowMapper<VehicleDealer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VehicleDealer.class);

		try {
			vehicleDealer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vehicleDealer = null;
		}
		logger.debug("Leaving");
		return vehicleDealer;
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
		RowMapper<VehicleDealer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VehicleDealer.class);

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("DealerType", "SOPT");

		try {
			vehicleDealer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);

		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vehicleDealer = null;
		}
		logger.debug("Leaving");
		return vehicleDealer;
	}

	/**
	 * This method Deletes the Record from the AMTVehicleDealer or AMTVehicleDealer_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Vehicle Dealer by key DealerId
	 * 
	 * @param Vehicle
	 *            Dealer (vehicleDealer)
	 * @param type
	 *            (String) ""/_Temp/_View
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
	 * @param Vehicle
	 *            Dealer (vehicleDealer)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(VehicleDealer vehicleDealer, String type) {
		logger.debug("Entering");
		if (vehicleDealer.getId() == Long.MIN_VALUE) {
			vehicleDealer.setId(getNextId("SeqAMTVehicleDealer"));
			logger.debug("get NextID:" + vehicleDealer.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into AMTVehicleDealer");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append("(DealerId,DealerType, DealerName,DealerTelephone,DealerFax,DealerAddress1,DealerAddress2, ");
		insertSql.append("DealerAddress3,DealerAddress4,DealerCountry,DealerCity,DealerProvince,");
		insertSql.append(
				"Email,POBox,ZipCode,Active,Emirates,CommisionPaidAt,CalculationRule,PaymentMode,AccountNumber,AccountingSetId,Code,ProductCtg,ShortCode,PANNumber,UIDNumber,TaxNumber,fromprovince,toprovince,");
		insertSql
				.append("AccountNo,AccountType,BankBranchID ,Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,SellerType, BranchCode)");

		insertSql.append(" Values(:DealerId,:DealerType, :DealerName,:DealerTelephone,:DealerFax,:DealerAddress1,");
		insertSql
				.append(" :DealerAddress2,:DealerAddress3,:DealerAddress4,:DealerCountry,:DealerCity,:DealerProvince,");
		insertSql.append(
				" :Email,:POBox,:ZipCode,:Active,:Emirates,:CommisionPaidAt,:CalculationRule,:PaymentMode,:AccountNumber,:AccountingSetId,:Code ,:ProductCtg,:ShortCode, ");
		insertSql.append(
				" :panNumber,:uidNumber,:taxNumber,:fromprovince,:toprovince,:accountNo,:accountType,:bankBranchID,:Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,");
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
	 * @param Vehicle
	 *            Dealer (vehicleDealer)
	 * @param type
	 *            (String) ""/_Temp/_View
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
				"AccountType = :accountType,BankBranchID = :bankBranchID,Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
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
		logger.debug(" Inside searchname");
		boolean status = false;
		try {
			String searchQuery = "select count(dealerName) from AMTVehicleDealer_View where dealerName=:dealerName and dealerType=:dealerType";

			MapSqlParameterSource parameterSource = new MapSqlParameterSource();
			parameterSource.addValue("dealerName", dealerName);
			parameterSource.addValue("dealerType", dealerType);

			int count = jdbcTemplate.queryForObject(searchQuery, parameterSource, Integer.class);
			logger.debug(" dealer name  : " + count);
			if (count != 0) {
				status = true;
			}
		} catch (IncorrectResultSizeDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");

		return status;
	}

	/**
	 * Fetch the Record VehicleDealer details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return count
	 */
	@Override
	public int getVehicleDealerByType(final String dealerType, String name, long id, String type) {
		logger.debug("Entering");
		VehicleDealer vehicleDealer = new VehicleDealer();

		vehicleDealer.setDealerType(dealerType);
		;
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
		int count;

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From VASStructure");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ManufacturerName =:ManufacturerName ");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
		logger.debug("Leaving");
		return count;
	}

	@Override
	public List<VehicleDealer> getVehicleDealerById(List<Long> dealerIds) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Select DealerName, DealerCity, DealerId,Code From AMTVehicleDealer ");
		sql.append(" WHERE DealerId IN(:dealerIds) ");
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("dealerIds", dealerIds);
		try {
			RowMapper<VehicleDealer> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(VehicleDealer.class);
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);

		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return null;
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

		RowMapper<VehicleDealer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VehicleDealer.class);
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

		RowMapper<VehicleDealer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VehicleDealer.class);
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
		RowMapper<VehicleDealer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VehicleDealer.class);

		try {
			vehicleDealer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vehicleDealer = null;
		}
		logger.debug("Leaving");
		return vehicleDealer;

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
		RowMapper<VehicleDealer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VehicleDealer.class);
		List<VehicleDealer> vehicleDealerBranchCodeList = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return vehicleDealerBranchCodeList;
	}

}