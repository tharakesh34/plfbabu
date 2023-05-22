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
 * * FileName : PromotionDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-03-2017 * * Modified
 * Date : 21-03-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-03-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.rmtmasters.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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

import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>Promotion model</b> class.<br>
 * 
 */

public class PromotionDAOImpl extends SequenceDao<Promotion> implements PromotionDAO {
	private static Logger logger = LogManager.getLogger(PromotionDAOImpl.class);

	public PromotionDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Promotion details by key field
	 * 
	 * @param promotionCode (String)
	 * @param type          (String) ""/_Temp/_View
	 * @return Promotion
	 */
	@Override
	public Promotion getPromotionById(final String promotionCode, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where PromotionCode = ?");

		logger.trace(Literal.SQL + sql.toString());

		PromotionRowMapper rowMapper = new PromotionRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, promotionCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the Promotions or Promotions_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Promotion by key PromotionCode
	 * 
	 * @param Promotion (promotion)
	 * @param type      (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Promotion promotion, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder deletSql = new StringBuilder();
		int recordCount = 0;

		deletSql.append("Delete From Promotions");
		deletSql.append(StringUtils.trimToEmpty(type));
		deletSql.append(" Where PromotionCode = :PromotionCode");

		logger.debug(Literal.SQL + deletSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		try {
			recordCount = this.jdbcTemplate.update(deletSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into Promotions or Promotions_Temp.
	 * 
	 * save Promotion
	 * 
	 * @param Promotion (promotion)
	 * @param type      (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Promotion promotion, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		if (promotion.getPromotionId() == Long.MIN_VALUE) {
			promotion.setPromotionId(getNextValue("SeqPromotions"));
			logger.debug("get NextID:" + promotion.getPromotionId());
		}

		sql.append("Insert Into Promotions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(PromotionId, promotionCode, promotionDesc, finType, startDate, endDate, finIsDwPayRequired");
		sql.append(" , downPayRule, actualInterestRate, finBaseRate, finSplRate, finMargin, applyRpyPricing");
		sql.append(" , rpyPricingMethod, finMinTerm, finMaxTerm, finMinAmount, finMaxAmount, finMinRate, finMaxRate");
		sql.append(" , active, referenceID , openBalOnPV , tenor , advEMITerms , pftDaysBasis, subventionRate");
		sql.append(" , taxApplicable, cashBackFromDealer, cashBackToCustomer, specialScheme, remarks, cbFrmMnf");
		sql.append(" , mnfCbToCust, dlrCbToCust, cbPyt, dbd, mbd, dbdPerc, dbdPercCal, dbdRtnd, mbdRtnd");
		sql.append(" , knckOffDueAmt, dbdFeeTypId, mbdFeeTypId, dbdAndMbdFeeTypId, Version, LastMntBy, LastMntOn");
		sql.append(" , RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(" :PromotionId, :promotionCode, :promotionDesc, :finType, :startDate, :endDate");
		sql.append(" , :finIsDwPayRequired, :downPayRule, :actualInterestRate, :finBaseRate, :finSplRate, :finMargin");
		sql.append(" , :applyRpyPricing, :rpyPricingMethod, :finMinTerm, :finMaxTerm, :finMinAmount, :finMaxAmount");
		sql.append(" , :finMinRate, :finMaxRate, :active, :referenceID, :openBalOnPV, :tenor, :advEMITerms");
		sql.append(" , :pftDaysBasis, :subventionRate, :taxApplicable, :cashBackFromDealer, :cashBackToCustomer");
		sql.append(" , :specialScheme, :remarks, :cbFrmMnf, :mnfCbToCust, :dlrCbToCust, :cbPyt, :dbd, :mbd, :dbdPerc");
		sql.append(
				" , :dbdPercCal, :dbdRtnd, :mbdRtnd, :knckOffDueAmt, :dbdFeeTypId, :mbdFeeTypId, :dbdAndMbdFeeTypId");
		sql.append(
				" , :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId");
		sql.append(" , :RecordType, :WorkflowId)");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);

		return promotion.getPromotionCode();
	}

	/**
	 * This method updates the Record Promotions or Promotions_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Promotion by key PromotionCode and Version
	 * 
	 * @param Promotion (promotion)
	 * @param type      (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(Promotion promotion, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		int recordCount = 0;

		sql.append("Update Promotions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set PromotionId = :PromotionId, promotionCode = :promotionCode");
		sql.append(", promotionDesc = :promotionDesc, finType = :finType, startDate = :startDate");
		sql.append(", endDate = :endDate, finIsDwPayRequired = :finIsDwPayRequired, downPayRule = :downPayRule");
		sql.append(", actualInterestRate=:actualInterestRate, finBaseRate=:finBaseRate, finSplRate = :finSplRate");
		sql.append(", finMargin = :finMargin, applyRpyPricing = :applyRpyPricing");
		sql.append(", rpyPricingMethod = :rpyPricingMethod, finMinTerm = :finMinTerm, finMaxTerm = :finMaxTerm");
		sql.append(", finMinAmount = :finMinAmount, finMaxAmount = :finMaxAmount, finMinRate = :finMinRate");
		sql.append(", finMaxRate = :finMaxRate, active = :active, referenceID = :referenceID");
		sql.append(", openBalOnPV = :openBalOnPV , tenor = :tenor, advEMITerms = :advEMITerms");
		sql.append(", pftDaysBasis = :pftDaysBasis, subventionRate = :subventionRate, taxApplicable = :taxApplicable");
		sql.append(", cashBackFromDealer = :cashBackFromDealer, cashBackToCustomer = :cashBackToCustomer");
		sql.append(", specialScheme = :specialScheme, remarks = :remarks,  cbFrmMnf=:cbFrmMnf");
		sql.append(", mnfCbToCust = :mnfCbToCust, dlrCbToCust = :dlrCbToCust, cbPyt = :cbPyt, dbd=:dbd");
		sql.append(", mbd = :mbd, dbdPerc = :dbdPerc, dbdPercCal = :dbdPercCal, dbdRtnd = :dbdRtnd");
		sql.append(", mbdRtnd = :mbdRtnd, knckOffDueAmt = :knckOffDueAmt, dbdFeeTypId = :dbdFeeTypId");
		sql.append(", mbdFeeTypId = :mbdFeeTypId, dbdAndMbdFeeTypId = :dbdAndMbdFeeTypId,  Version = :Version");
		sql.append(", LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus");
		sql.append(", RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId");
		sql.append(", NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where PromotionCode = :PromotionCode and PromotionId = :PromotionId");

		if (!type.endsWith("_Temp")) {
			sql.append("  AND Version= :Version-1");
		}

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public int getPromtionCodeCount(String promotionCode, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder("Select Count(*) From Promotions");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PromotionCode = :PromotionCode");
		logger.debug(Literal.SQL + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PromotionCode", promotionCode);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	/**
	 * Fetch record count of product
	 * 
	 * @param productCode
	 * @return Integer
	 */
	@Override
	public int getFinanceTypeCountById(String finType) {
		logger.debug(Literal.ENTERING);

		Promotion promotion = new Promotion();
		promotion.setFinType(finType);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(FinType) ");
		selectSql.append(" From Promotions Where FinType =:FinType");

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * Fetch the Promotions Based on the finType
	 * 
	 * @param productCode
	 */
	@Override
	public List<Promotion> getPromotionsByFinType(String finType, String type) {
		logger.debug(Literal.ENTERING);

		Promotion promotion = new Promotion();
		promotion.setFinType(finType);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT FinType, FinTypeDesc, PromotionCode, PromotionDesc");
		selectSql.append(" From Promotions");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType");

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		RowMapper<Promotion> typeRowMapper = BeanPropertyRowMapper.newInstance(Promotion.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public int getPromotionByRuleCode(long ruleId, String type) {
		logger.debug(Literal.ENTERING);
		Promotion promotion = new Promotion();
		promotion.setDownPayRule(ruleId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From Promotions");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DownPayRule =:DownPayRule");

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public Promotion getPromotionByCode(String promotionCode, String type) {
		logger.debug(Literal.ENTERING);

		Promotion promotion = new Promotion();
		promotion.setPromotionCode(promotionCode);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT PromotionId, promotionCode, promotionDesc, finType, startDate, endDate");
		sql.append(", finIsDwPayRequired, downPayRule, actualInterestRate, finBaseRate, finSplRate, finMargin");
		sql.append(", applyRpyPricing, rpyPricingMethod, finMinTerm, finMaxTerm, finMinAmount, finMaxAmount");
		sql.append(", finMinRate, finMaxRate, active, referenceID, openBalOnPV, tenor, advEMITerms, pftDaysBasis");
		sql.append(", subventionRate, taxApplicable, cashBackFromDealer, cashBackToCustomer, specialScheme, remarks");
		sql.append(", cbFrmMnf, mnfCbToCust, dlrCbToCust, cbPyt, dbd, mbd, dbdPerc, dbdPercCal, dbdRtnd, mbdRtnd");
		sql.append(", knckOffDueAmt, dbdFeeTypId, mbdFeeTypId, dbdAndMbdFeeTypId");
		if (type.contains("View")) {
			sql.append(",finCcy, FinTypeDesc, DownPayRuleCode, DownPayRuleDesc, RpyPricingCode, RpyPricingDesc");
			sql.append(", productCategory");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From Promotions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PromotionCode = :PromotionCode");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		RowMapper<Promotion> typeRowMapper = BeanPropertyRowMapper.newInstance(Promotion.class);

		try {
			promotion = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			promotion = null;
		}

		logger.debug(Literal.LEAVING);

		return promotion;
	}

	@Override
	public Promotion getActiveSchemeForTxn(final String promotionCode, Date valueDate) {
		Promotion promotion = new Promotion();
		promotion.setPromotionCode(promotionCode);
		promotion.setStartDate(valueDate);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT PromotionId, promotionCode, promotionDesc, finType, startDate, endDate");
		sql.append(", finIsDwPayRequired, downPayRule, actualInterestRate, finBaseRate, finSplRate");
		sql.append(", finMargin, applyRpyPricing, rpyPricingMethod, finMinTerm, finMaxTerm, finMinAmount");
		sql.append(", finMaxAmount, finMinRate, finMaxRate, active, referenceID, openBalOnPV, tenor, advEmiTerms");
		sql.append(", pftDaysBasis, subventionRate, taxApplicable, cashBackFromDealer, cashBackToCustomer");
		sql.append(", specialScheme, remarks, cbFrmMnf, mnfCbToCust, dlrCbToCust, cbPyt, dbd, mbd, dbdPerc");
		sql.append(", dbdPercCal, dbdRtnd, mbdRtnd, knckOffDueAmt, dbdFeeTypId, mbdFeeTypId, dbdAndMbdFeeTypId");
		sql.append(" From Promotions Where PromotionCode = :PromotionCode");
		sql.append(" AND StartDate <= :StartDate AND EndDate >= :StartDate AND Active = 1");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		RowMapper<Promotion> typeRowMapper = BeanPropertyRowMapper.newInstance(Promotion.class);

		try {
			promotion = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.debug("Promotion Code: " + promotionCode + " NOT FOUND");
			promotion = null;
		}

		return promotion;
	}

	@Override
	public long getPromotionalReferenceId() {
		return getNextValue("SeqPromotionScheme");
	}

	/**
	 * Fetch the Record Promotion details by key field
	 * 
	 * @param promotionId (long)
	 * @param type        (String) ""/_Temp/_View
	 * @return Promotion
	 */
	@Override
	public Promotion getPromotionById(long promotionId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where PromotionId = ?");

		logger.trace(Literal.SQL + sql.toString());

		PromotionRowMapper rowMapper = new PromotionRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, promotionId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the Record Promotion details by key field
	 * 
	 * @param referenceId (long)
	 * @param type        (String) ""/_Temp/_View
	 * @return Promotion
	 */
	@Override
	public Promotion getPromotionByReferenceId(long referenceId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT PromotionId, promotionCode, promotionDesc, finType, startDate, endDate");
		sql.append(", finIsDwPayRequired, downPayRule, actualInterestRate, finBaseRate, finSplRate, finMargin");
		sql.append(", applyRpyPricing, rpyPricingMethod, finMinTerm, finMaxTerm, finMinAmount, finMaxAmount");
		sql.append(", finMinRate, finMaxRate, active, referenceID, openBalOnPV, tenor, advEMITerms, pftDaysBasis");
		sql.append(", subventionRate, taxApplicable, cashBackFromDealer, cashBackToCustomer, specialScheme, remarks");
		sql.append(", cbFrmMnf, mnfCbToCust, dlrCbToCust, cbPyt, dbd, mbd, dbdPerc, dbdPercCal, dbdRtnd, mbdRtnd");
		sql.append(", knckOffDueAmt, dbdFeeTypId, mbdFeeTypId, dbdAndMbdFeeTypId");
		if (type.contains("View")) {
			sql.append(", finCcy, FinTypeDesc, DownPayRuleCode, DownPayRuleDesc, RpyPricingCode, RpyPricingDesc");
			sql.append(", productCategory");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From Promotions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReferenceID = :ReferenceID");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceID", referenceId);
		RowMapper<Promotion> typeRowMapper = BeanPropertyRowMapper.newInstance(Promotion.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PromotionId, PromotionCode, PromotionDesc, FinType, StartDate, EndDate, FinIsDwPayRequired");
		sql.append(", DownPayRule, ActualInterestRate, FinBaseRate, FinSplRate, FinMargin, ApplyRpyPricing");
		sql.append(", RpyPricingMethod, FinMinTerm, FinMaxTerm, FinMinAmount, FinMaxAmount, FinMinRate");
		sql.append(", FinMaxRate, Active, ReferenceID, OpenBalOnPV, Tenor, AdvEMITerms, PftDaysBasis");
		sql.append(", SubventionRate, TaxApplicable, CashBackFromDealer, CashBackToCustomer, SpecialScheme");
		sql.append(", Remarks, CbFrmMnf, MnfCbToCust, DlrCbToCust, CbPyt, Dbd, Mbd, DbdPerc, DbdPercCal");
		sql.append(", DbdRtnd, MbdRtnd, KnckOffDueAmt, DbdFeeTypId, MbdFeeTypId, DbdAndMbdFeeTypId");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinCcy, FinTypeDesc, DownPayRuleCode, DownPayRuleDesc");
			sql.append(", RpyPricingCode, RpyPricingDesc, ProductCategory");
		}

		sql.append(" from Promotions");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private class PromotionRowMapper implements RowMapper<Promotion> {
		private String type;

		private PromotionRowMapper(String type) {
			this.type = type;
		}

		@Override
		public Promotion mapRow(ResultSet rs, int rowNum) throws SQLException {
			Promotion pc = new Promotion();

			pc.setPromotionId(rs.getLong("PromotionId"));
			pc.setPromotionCode(rs.getString("PromotionCode"));
			pc.setPromotionDesc(rs.getString("PromotionDesc"));
			pc.setFinType(rs.getString("FinType"));
			pc.setStartDate(rs.getTimestamp("StartDate"));
			pc.setEndDate(rs.getTimestamp("EndDate"));
			pc.setFinIsDwPayRequired(rs.getBoolean("FinIsDwPayRequired"));
			pc.setDownPayRule(rs.getLong("DownPayRule"));
			pc.setActualInterestRate(rs.getBigDecimal("ActualInterestRate"));
			pc.setFinBaseRate(rs.getString("FinBaseRate"));
			pc.setFinSplRate(rs.getString("FinSplRate"));
			pc.setFinMargin(rs.getBigDecimal("FinMargin"));
			pc.setApplyRpyPricing(rs.getBoolean("ApplyRpyPricing"));
			pc.setRpyPricingMethod(rs.getLong("RpyPricingMethod"));
			pc.setFinMinTerm(rs.getInt("FinMinTerm"));
			pc.setFinMaxTerm(rs.getInt("FinMaxTerm"));
			pc.setFinMinAmount(rs.getBigDecimal("FinMinAmount"));
			pc.setFinMaxAmount(rs.getBigDecimal("FinMaxAmount"));
			pc.setFinMinRate(rs.getBigDecimal("FinMinRate"));
			pc.setFinMaxRate(rs.getBigDecimal("FinMaxRate"));
			pc.setActive(rs.getBoolean("Active"));
			pc.setReferenceID(rs.getLong("ReferenceID"));
			pc.setOpenBalOnPV(rs.getBoolean("OpenBalOnPV"));
			pc.setTenor(rs.getInt("Tenor"));
			pc.setAdvEMITerms(rs.getInt("AdvEMITerms"));
			pc.setPftDaysBasis(rs.getString("PftDaysBasis"));
			pc.setSubventionRate(rs.getBigDecimal("SubventionRate"));
			pc.setTaxApplicable(rs.getBoolean("TaxApplicable"));
			pc.setCashBackFromDealer(rs.getInt("CashBackFromDealer"));
			pc.setCashBackToCustomer(rs.getInt("CashBackToCustomer"));
			pc.setSpecialScheme(rs.getBoolean("SpecialScheme"));
			pc.setRemarks(rs.getString("Remarks"));
			pc.setCbFrmMnf(rs.getInt("CbFrmMnf"));
			pc.setMnfCbToCust(rs.getInt("MnfCbToCust"));
			pc.setDlrCbToCust(rs.getInt("DlrCbToCust"));
			pc.setCbPyt(rs.getString("CbPyt"));
			pc.setDbd(rs.getBoolean("Dbd"));
			pc.setMbd(rs.getBoolean("Mbd"));
			pc.setDbdPerc(rs.getBigDecimal("DbdPerc"));
			pc.setDbdPercCal(rs.getString("DbdPercCal"));
			pc.setDbdRtnd(rs.getBoolean("DbdRtnd"));
			pc.setMbdRtnd(rs.getBoolean("MbdRtnd"));
			pc.setKnckOffDueAmt(rs.getBoolean("KnckOffDueAmt"));
			pc.setDbdFeeTypId(rs.getLong("DbdFeeTypId"));
			pc.setMbdFeeTypId(rs.getLong("MbdFeeTypId"));
			pc.setDbdAndMbdFeeTypId(rs.getLong("DbdAndMbdFeeTypId"));
			pc.setVersion(rs.getInt("Version"));
			pc.setLastMntOn(rs.getTimestamp("LastMntOn"));
			pc.setLastMntBy(rs.getLong("LastMntBy"));
			pc.setRecordStatus(rs.getString("RecordStatus"));
			pc.setRoleCode(rs.getString("RoleCode"));
			pc.setNextRoleCode(rs.getString("NextRoleCode"));
			pc.setTaskId(rs.getString("TaskId"));
			pc.setNextTaskId(rs.getString("NextTaskId"));
			pc.setRecordType(rs.getString("RecordType"));
			pc.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				pc.setFinCcy(rs.getString("FinCcy"));
				pc.setFinTypeDesc(rs.getString("FinTypeDesc"));
				pc.setDownPayRuleCode(rs.getString("DownPayRuleCode"));
				pc.setDownPayRuleDesc(rs.getString("DownPayRuleDesc"));
				pc.setRpyPricingCode(rs.getString("RpyPricingCode"));
				pc.setRpyPricingDesc(rs.getString("RpyPricingDesc"));
				pc.setProductCategory(rs.getString("ProductCategory"));
			}

			return pc;
		}

	}

	@Override
	public void updatePromotion(Promotion promotion) {
		logger.debug(Literal.ENTERING);

		StringBuilder updateSql = new StringBuilder();
		promotion.setActive(false);

		updateSql.append(" Update Promotions");
		updateSql.append(" set active=:active, LastMntOn=:LastMntOn ");
		updateSql.append(" Where PromotionCode =:PromotionCode and ReferenceID <> :ReferenceID and Active = 1");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);

		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Fetch the Record Promotion details by key field
	 * 
	 * @param promotionCode (String)
	 * @param type          (String) ""/_Temp/_View
	 * @return Promotion
	 */
	@Override
	public Promotion getPromotion(Promotion promotion, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where PromotionCode = ? AND ReferenceID = ?");

		logger.trace(Literal.SQL + sql.toString());

		PromotionRowMapper rowMapper = new PromotionRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, promotion.getPromotionCode(),
					promotion.getReferenceID());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Promotion getPromotionForLMSEvent(String code) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PromotionCode, PromotionDesc, FinIsDwPayRequired, DownPayRule, ActualInterestRate, FinBaseRate");
		sql.append(", FinSplRate, FinMargin, ApplyRpyPricing, RpyPricingMethod, FinMinTerm, FinMaxTerm");
		sql.append(", FinMinAmount, FinMaxAmount, FinMinRate, FinMaxRate");
		sql.append(" From Promotions");
		sql.append(" Where PromotionCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Promotion p = new Promotion();

				p.setPromotionCode(rs.getString("PromotionCode"));
				p.setPromotionDesc(rs.getString("PromotionDesc"));
				p.setFinIsDwPayRequired(rs.getBoolean("FinIsDwPayRequired"));
				p.setDownPayRule(rs.getLong("DownPayRule"));
				p.setActualInterestRate(rs.getBigDecimal("ActualInterestRate"));
				p.setFinBaseRate(rs.getString("FinBaseRate"));
				p.setFinSplRate(rs.getString("FinSplRate"));
				p.setFinMargin(rs.getBigDecimal("FinMargin"));
				p.setApplyRpyPricing(rs.getBoolean("ApplyRpyPricing"));
				p.setRpyPricingMethod(rs.getLong("RpyPricingMethod"));
				p.setFinMinTerm(rs.getInt("FinMinTerm"));
				p.setFinMaxTerm(rs.getInt("FinMaxTerm"));
				p.setFinMinAmount(rs.getBigDecimal("FinMinAmount"));
				p.setFinMaxAmount(rs.getBigDecimal("FinMaxAmount"));
				p.setFinMinRate(rs.getBigDecimal("FinMinRate"));
				p.setFinMaxRate(rs.getBigDecimal("FinMaxRate"));

				return p;
			}, code);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}