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
 * * FileName : VASConfigurationDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-11-2016 * *
 * Modified Date : 29-11-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-11-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.configuration.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.configuration.VASConfigurationDAO;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASPremiumCalcDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>VASConfiguration model</b> class.<br>
 * 
 */

public class VASConfigurationDAOImpl extends BasicDao<VASConfiguration> implements VASConfigurationDAO {
	private static Logger logger = LogManager.getLogger(VASConfigurationDAOImpl.class);

	public VASConfigurationDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new VASConfiguration
	 * 
	 * @return VASConfiguration
	 */

	@Override
	public VASConfiguration getVASConfiguration() {
		logger.debug("Entering");
		logger.debug("Leaving");
		return new VASConfiguration();
	}

	/**
	 * This method get the module from method getVASConfiguration() and set the new record flag as true and return
	 * VASConfiguration()
	 * 
	 * @return VASConfiguration
	 */

	@Override
	public VASConfiguration getNewVASConfiguration() {
		logger.debug("Entering");
		VASConfiguration vASConfiguration = getVASConfiguration();
		vASConfiguration.setNewRecord(true);
		logger.debug("Leaving");
		return vASConfiguration;
	}

	@Override
	public List<VASConfiguration> getVASConfigurations(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ProductCode, ProductDesc, RecAgainst, FeeAccrued, FeeAccounting, AccrualAccounting");
		sql.append(", RecurringType, FreeLockPeriod, PreValidationReq, PostValidationReq, Active, Remarks");
		sql.append(", ProductType, VasFee, BatchId, AllowFeeToModify, ManufacturerId, PreValidation");
		sql.append(", PostValidation, FeeType, FlpCalculatedOn, ShortCode, ModeOfPayment, AllowFeeType");
		sql.append(", MedicalApplicable, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeAccountingName, AccrualAccountingName, FeeAccountingDesc");
			sql.append(", AccrualAccountingDesc, ProductTypeDesc, ProductCategory");
			sql.append(", ManufacturerName, FeeTypeCode, FeeTypeDesc, FileName");
		}

		sql.append(" from VasStructure");
		sql.append(StringUtils.trimToEmpty(type));

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new RowMapper<VASConfiguration>() {
				@Override
				public VASConfiguration mapRow(ResultSet rs, int rowNum) throws SQLException {
					VASConfiguration vasStructure = new VASConfiguration();

					vasStructure.setProductCode(rs.getString("ProductCode"));
					vasStructure.setProductDesc(rs.getString("ProductDesc"));
					vasStructure.setRecAgainst(rs.getString("RecAgainst"));
					vasStructure.setFeeAccrued(rs.getBoolean("FeeAccrued"));
					vasStructure.setFeeAccounting(rs.getLong("FeeAccounting"));
					vasStructure.setAccrualAccounting(rs.getLong("AccrualAccounting"));
					vasStructure.setRecurringType(rs.getBoolean("RecurringType"));
					vasStructure.setFreeLockPeriod(rs.getInt("FreeLockPeriod"));
					vasStructure.setPreValidationReq(rs.getBoolean("PreValidationReq"));
					vasStructure.setPostValidationReq(rs.getBoolean("PostValidationReq"));
					vasStructure.setActive(rs.getBoolean("Active"));
					vasStructure.setRemarks(rs.getString("Remarks"));
					vasStructure.setProductType(rs.getString("ProductType"));
					vasStructure.setVasFee(rs.getBigDecimal("VasFee"));
					vasStructure.setBatchId(rs.getLong("BatchId"));
					vasStructure.setAllowFeeToModify(rs.getBoolean("AllowFeeToModify"));
					vasStructure.setManufacturerId(rs.getLong("ManufacturerId"));
					vasStructure.setPreValidation(rs.getString("PreValidation"));
					vasStructure.setPostValidation(rs.getString("PostValidation"));
					vasStructure.setFeeType(JdbcUtil.getLong(rs.getLong("FeeType")));
					vasStructure.setFlpCalculatedOn(rs.getString("FlpCalculatedOn"));
					vasStructure.setShortCode(rs.getString("ShortCode"));
					vasStructure.setModeOfPayment(rs.getString("ModeOfPayment"));
					vasStructure.setAllowFeeType(rs.getString("AllowFeeType"));
					vasStructure.setMedicalApplicable(rs.getBoolean("MedicalApplicable"));
					vasStructure.setVersion(rs.getInt("Version"));
					vasStructure.setLastMntBy(rs.getLong("LastMntBy"));
					vasStructure.setLastMntOn(rs.getTimestamp("LastMntOn"));
					vasStructure.setRecordStatus(rs.getString("RecordStatus"));
					vasStructure.setRoleCode(rs.getString("RoleCode"));
					vasStructure.setNextRoleCode(rs.getString("NextRoleCode"));
					vasStructure.setTaskId(rs.getString("TaskId"));
					vasStructure.setNextTaskId(rs.getString("NextTaskId"));
					vasStructure.setRecordType(rs.getString("RecordType"));
					vasStructure.setWorkflowId(rs.getLong("WorkflowId"));
					if (StringUtils.trimToEmpty(type).contains("View")) {
						vasStructure.setFeeAccountingName(rs.getString("FeeAccountingName"));
						vasStructure.setAccrualAccountingName(rs.getString("AccrualAccountingName"));
						vasStructure.setFeeAccountingDesc(rs.getString("FeeAccountingDesc"));
						vasStructure.setAccrualAccountingDesc(rs.getString("AccrualAccountingDesc"));
						vasStructure.setProductTypeDesc(rs.getString("ProductTypeDesc"));
						vasStructure.setProductCategory(rs.getString("ProductCategory"));
						vasStructure.setManufacturerName(rs.getString("ManufacturerName"));
						vasStructure.setFeeTypeCode(rs.getString("FeeTypeCode"));
						vasStructure.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
						vasStructure.setFileName(rs.getString("FileName"));
					}

					return vasStructure;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return new ArrayList<>();
	}

	@Override
	public VASConfiguration getVASConfigurationByCode(String productCode, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ProductCode, ProductDesc, RecAgainst, FeeAccrued, FeeAccounting, AccrualAccounting");
		sql.append(", RecurringType, FreeLockPeriod, PreValidationReq, PostValidationReq, Active, Remarks");
		sql.append(", ProductType, VasFee, BatchId, AllowFeeToModify, ManufacturerId, PreValidation");
		sql.append(", PostValidation, FeeType, FlpCalculatedOn, ShortCode, ModeOfPayment, AllowFeeType");
		sql.append(", MedicalApplicable, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeAccountingName, AccrualAccountingName, FeeAccountingDesc");
			sql.append(", AccrualAccountingDesc, ProductTypeDesc, ProductCategory");
			sql.append(", ManufacturerName, FeeTypeCode, FeeTypeDesc, FileName");
		}

		sql.append(" from VasStructure");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ProductCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<VASConfiguration>() {
				@Override
				public VASConfiguration mapRow(ResultSet rs, int rowNum) throws SQLException {
					VASConfiguration vasStructure = new VASConfiguration();

					vasStructure.setProductCode(rs.getString("ProductCode"));
					vasStructure.setProductDesc(rs.getString("ProductDesc"));
					vasStructure.setRecAgainst(rs.getString("RecAgainst"));
					vasStructure.setFeeAccrued(rs.getBoolean("FeeAccrued"));
					vasStructure.setFeeAccounting(rs.getLong("FeeAccounting"));
					vasStructure.setAccrualAccounting(rs.getLong("AccrualAccounting"));
					vasStructure.setRecurringType(rs.getBoolean("RecurringType"));
					vasStructure.setFreeLockPeriod(rs.getInt("FreeLockPeriod"));
					vasStructure.setPreValidationReq(rs.getBoolean("PreValidationReq"));
					vasStructure.setPostValidationReq(rs.getBoolean("PostValidationReq"));
					vasStructure.setActive(rs.getBoolean("Active"));
					vasStructure.setRemarks(rs.getString("Remarks"));
					vasStructure.setProductType(rs.getString("ProductType"));
					vasStructure.setVasFee(rs.getBigDecimal("VasFee"));
					vasStructure.setBatchId(rs.getLong("BatchId"));
					vasStructure.setAllowFeeToModify(rs.getBoolean("AllowFeeToModify"));
					vasStructure.setManufacturerId(rs.getLong("ManufacturerId"));
					vasStructure.setPreValidation(rs.getString("PreValidation"));
					vasStructure.setPostValidation(rs.getString("PostValidation"));
					vasStructure.setFeeType(JdbcUtil.getLong(rs.getLong("FeeType")));
					vasStructure.setFlpCalculatedOn(rs.getString("FlpCalculatedOn"));
					vasStructure.setShortCode(rs.getString("ShortCode"));
					vasStructure.setModeOfPayment(rs.getString("ModeOfPayment"));
					vasStructure.setAllowFeeType(rs.getString("AllowFeeType"));
					vasStructure.setMedicalApplicable(rs.getBoolean("MedicalApplicable"));
					vasStructure.setVersion(rs.getInt("Version"));
					vasStructure.setLastMntBy(rs.getLong("LastMntBy"));
					vasStructure.setLastMntOn(rs.getTimestamp("LastMntOn"));
					vasStructure.setRecordStatus(rs.getString("RecordStatus"));
					vasStructure.setRoleCode(rs.getString("RoleCode"));
					vasStructure.setNextRoleCode(rs.getString("NextRoleCode"));
					vasStructure.setTaskId(rs.getString("TaskId"));
					vasStructure.setNextTaskId(rs.getString("NextTaskId"));
					vasStructure.setRecordType(rs.getString("RecordType"));
					vasStructure.setWorkflowId(rs.getLong("WorkflowId"));
					if (StringUtils.trimToEmpty(type).contains("View")) {
						vasStructure.setFeeAccountingName(rs.getString("FeeAccountingName"));
						vasStructure.setAccrualAccountingName(rs.getString("AccrualAccountingName"));
						vasStructure.setFeeAccountingDesc(rs.getString("FeeAccountingDesc"));
						vasStructure.setAccrualAccountingDesc(rs.getString("AccrualAccountingDesc"));
						vasStructure.setProductTypeDesc(rs.getString("ProductTypeDesc"));
						vasStructure.setProductCategory(rs.getString("ProductCategory"));
						vasStructure.setManufacturerName(rs.getString("ManufacturerName"));
						vasStructure.setFeeTypeCode(rs.getString("FeeTypeCode"));
						vasStructure.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
						vasStructure.setFileName(rs.getString("FileName"));
					}

					return vasStructure;
				}
			}, productCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	/**
	 * This method Deletes the Record from the VasStructure or VasStructure_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete VASConfiguration by key ProductCode
	 * 
	 * @param VASConfiguration (vASConfiguration)
	 * @param type             (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(VASConfiguration vASConfiguration, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From VasStructure");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ProductCode =:ProductCode");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASConfiguration);
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
	 * This method insert new Records into VasStructure or VasStructure_Temp.
	 *
	 * save VASConfiguration
	 * 
	 * @param VASConfiguration (vASConfiguration)
	 * @param type             (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(VASConfiguration vASConfiguration, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into VasStructure");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (ProductCode, ProductDesc, RecAgainst, FeeAccrued, FeeAccounting, AccrualAccounting, RecurringType, FreeLockPeriod, PreValidationReq, PostValidationReq, Active, Remarks, ");
		insertSql.append("  ProductType, VasFee, AllowFeeToModify, ManufacturerId, PreValidation, PostValidation, ");
		insertSql.append(
				" FeeType, FLPCalculatedOn, ShortCode, ModeOfPayment, AllowFeeType, MedicalApplicable, BatchId,");
		insertSql.append(
				"  Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				"  Values(:ProductCode, :ProductDesc, :RecAgainst, :FeeAccrued, :FeeAccounting, :AccrualAccounting, :RecurringType, :FreeLockPeriod, :PreValidationReq, :PostValidationReq, :Active, :Remarks, ");
		insertSql.append(
				"  :ProductType, :VasFee, :AllowFeeToModify, :ManufacturerId, :PreValidation, :PostValidation, ");
		insertSql.append(
				" :FeeType, :FlpCalculatedOn, :ShortCode, :ModeOfPayment, :AllowFeeType, :MedicalApplicable, :BatchId,");
		insertSql.append(
				"  :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASConfiguration);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vASConfiguration.getId();
	}

	/**
	 * This method updates the Record VasStructure or VasStructure_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update VASConfiguration by key ProductCode and Version
	 * 
	 * @param VASConfiguration (vASConfiguration)
	 * @param type             (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(VASConfiguration vASConfiguration, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update VasStructure");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set ProductDesc = :ProductDesc, RecAgainst = :RecAgainst, FeeAccrued = :FeeAccrued, FeeAccounting = :FeeAccounting,");
		updateSql.append(
				"  AccrualAccounting = :AccrualAccounting, RecurringType = :RecurringType, FreeLockPeriod = :FreeLockPeriod,");
		updateSql.append(
				"	PreValidationReq = :PreValidationReq, PostValidationReq = :PostValidationReq, Active = :Active, Remarks = :Remarks,");
		updateSql.append(
				"  ProductType= :ProductType , VasFee = :VasFee, AllowFeeToModify = :AllowFeeToModify, ManufacturerId= :ManufacturerId, ");
		updateSql.append(
				"  ModeOfPayment= :ModeOfPayment , AllowFeeType = :AllowFeeType, MedicalApplicable = :MedicalApplicable, BatchId = :BatchId, ");
		updateSql.append(
				"  PreValidation = :PreValidation, PostValidation= :PostValidation, FeeType = :FeeType, FLPCalculatedOn = :FlpCalculatedOn, ShortCode = :ShortCode,");
		updateSql.append(" Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(
				"  RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ProductCode =:ProductCode");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASConfiguration);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/*
	 * Check whether the VASProducttype exists in VASRecording or not
	 */
	@Override
	public boolean isVASTypeExists(String productType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(
				" Select COUNT(*) from VasStructure T1 INNER JOIN VASRecording T2 On T1.ProductCode = T2.ProductCode");
		sql.append(" Where T1.ProductType = :ProductType ");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ProductType", productType);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0;
	}

	@Override
	public int getFeeAccountingCount(long feeAccountId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder("Select Count(*) From VasStructure");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FeeAccounting = :FeeAccounting");
		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FeeAccounting", feeAccountId);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public void savePremiumCalcDetails(List<VASPremiumCalcDetails> premiumCalcDetList, String tableType) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO VASPremiumCalcDetails");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" ( BatchId, ProductCode, ManufacturerId, CustomerAge, Gender,");
		sql.append("PolicyAge, PremiumPercentage, MinAmount, MaxAmount, LoanAge) ");
		sql.append("  VALUES(:BatchId, :ProductCode, :ManufacturerId, :CustomerAge, :Gender,");
		sql.append(" :PolicyAge, :PremiumPercentage, :MinAmount, :MaxAmount, :LoanAge) ");

		logger.debug("insertSql: " + sql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(premiumCalcDetList.toArray());

		this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<VASPremiumCalcDetails> getPremiumCalcDetails(String productCode, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BatchId, ProductCode, ManufacturerId, CustomerAge, Gender");
		sql.append(", PolicyAge, PremiumPercentage, MinAmount, MaxAmount, LoanAge");
		sql.append(" from VASPremiumCalcDetails");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where ProductCode = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index, productCode);
			}
		}, new RowMapper<VASPremiumCalcDetails>() {
			@Override
			public VASPremiumCalcDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
				VASPremiumCalcDetails vas = new VASPremiumCalcDetails();

				vas.setBatchId(rs.getLong("BatchId"));
				vas.setProductCode(rs.getString("ProductCode"));
				vas.setManufacturerId(rs.getLong("ManufacturerId"));
				vas.setCustomerAge(rs.getInt("CustomerAge"));
				vas.setGender(rs.getString("Gender"));
				vas.setPolicyAge(rs.getInt("PolicyAge"));
				vas.setPremiumPercentage(rs.getBigDecimal("PremiumPercentage"));
				vas.setMinAmount(rs.getBigDecimal("MinAmount"));
				vas.setMaxAmount(rs.getBigDecimal("MaxAmount"));
				vas.setLoanAge(rs.getInt("LoanAge"));

				return vas;
			}
		});
	}

	@Override
	public void deletePremiumCalcDetails(String productCode, String tableType) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From VASPremiumCalcDetails");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where ProductCode =:ProductCode");

		logger.debug("deleteSql: " + deleteSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ProductCode", productCode);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), source);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
}