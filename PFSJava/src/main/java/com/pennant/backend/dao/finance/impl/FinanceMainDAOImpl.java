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
 *
 * FileName : FinanceMainDAOImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 24-12-2017 *
 * 
 * Modified Date : 24-12-2017 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-12-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.zkoss.util.resource.Labels;

import com.pennant.app.core.CustEODEvent;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.LoanPendingData;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennant.backend.model.finance.FinCustomerDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceMainExtension;
import com.pennant.backend.model.finance.FinanceStatusEnquiry;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.UserPendingCases;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.extension.CustomerExtension;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.dms.model.DMSQueue;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.FinanceUtil;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 */
public class FinanceMainDAOImpl extends BasicDao<FinanceMain> implements FinanceMainDAO {
	private static Logger logger = LogManager.getLogger(FinanceMainDAOImpl.class);

	public FinanceMainDAOImpl() {
		super();
	}

	@Override
	public FinanceMain getFinanceMain(boolean isWIF) {
		String wfModuleName = "";

		if (isWIF) {
			wfModuleName = "WIFFinanceMain";
		} else {
			wfModuleName = "FinanceMain";
		}

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails(wfModuleName);
		FinanceMain fm = new FinanceMain();
		if (workFlowDetails != null) {
			fm.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		return fm;
	}

	@Override
	public List<String> getFinanceWorlflowFirstTaskOwners(String event, String moduleName) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FirstTaskOwner");
		sql.append(" From WorkFlowDetails");
		sql.append(" where WorkFlowType in (Select distinct WorkFlowType From LMTFinanceWorkFlowDef");

		Object[] objects = null;

		if (StringUtils.isNotBlank(event)) {
			sql.append(" where FinEvent = ?)");
			sql.append(" and WorkFlowActive = ?");
			objects = new Object[] { event, 1 };
		} else {
			sql.append(") and WorkFlowActive = ?");
			objects = new Object[] { 1 };
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForList(sql.toString(), String.class, objects);
	}

	@Override
	public FinanceMain getFinanceMain(long finID, String nextRoleCode, String type) {
		StringBuilder sql = new StringBuilder(getFinMainAllQuery(type, false));
		sql.append(" Where FinID = ? and NextRoleCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinanceMainRowMapper rowMapper = new FinanceMainRowMapper(type, false);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID, nextRoleCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinanceMainByRef(String finReference, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder(getFinMainAllQuery(type, isWIF));
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinanceMainRowMapper rowMapper = new FinanceMainRowMapper(type, isWIF);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinanceMainById(long finID, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder(getFinMainAllQuery(type, isWIF));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinanceMainRowMapper rowMapper = new FinanceMainRowMapper(type, isWIF);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getDisbursmentFinMainById(long finID, TableType tableType) {
		StringBuilder sql = getDisbursementFmQuery(tableType);
		sql.append(" Where fm.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new DIsbursementFMRowMapper(), finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getDisbursmentFinMainById(String finReference, TableType tableType) {
		StringBuilder sql = getDisbursementFmQuery(tableType);
		sql.append(" Where fm.FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new DIsbursementFMRowMapper(), finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private StringBuilder getDisbursementFmQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinCcy, fm.FinType, fm.CustID, fm.FinStartDate, fm.FinBranch");
		sql.append(", fm.FinReference, fm.MaturityDate, fm.FeeChargeAmt, fm.DownPayment");
		sql.append(", fm.DeductFeeDisb, fm.BpiAmount, fm.FinIsActive");
		sql.append(", fm.BpiTreatment, fm.QuickDisb, fm.InstBasedSchd, fm.FinAssetValue, fm.FinCurrAssetValue");
		sql.append(", c.CustCIF, c.CustShrtName, ft.alwMultiPartyDisb, ft.FinTypeDesc");
		sql.append(", PromotionCode, e.EntityCode");
		sql.append(" From FinanceMain");
		sql.append(tableType.getSuffix());
		sql.append(" fm");
		sql.append(" Inner Join RMTFinanceTypes ft On fm.FinType = ft.FinType");
		sql.append(" Inner Join Customers c On fm.CustID = c.CustID");
		sql.append(" Inner Join SmtDivisionDetail d On d.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e on e.EntityCode = d.EntityCode");
		return sql;
	}

	private class DIsbursementFMRowMapper implements RowMapper<FinanceMain> {

		@Override
		public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setFinType(rs.getString("FinType"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			fm.setDownPayment(rs.getBigDecimal("DownPayment"));
			fm.setDeductFeeDisb(rs.getBigDecimal("DeductFeeDisb"));
			fm.setBpiAmount(rs.getBigDecimal("BpiAmount"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setBpiTreatment(rs.getString("BpiTreatment"));
			fm.setQuickDisb(rs.getBoolean("QuickDisb"));
			fm.setInstBasedSchd(rs.getBoolean("InstBasedSchd"));
			fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setLovDescCustCIF(rs.getString("CustCIF"));
			fm.setLovDescCustShrtName(rs.getString("CustShrtName"));
			fm.setAlwMultiDisb(rs.getBoolean("AlwMultiPartyDisb"));
			fm.setLovDescFinTypeName(rs.getString("FinTypeDesc"));
			fm.setEntityCode(rs.getString("EntityCode"));
			fm.setLovDescEntityCode(rs.getString("EntityCode"));
			fm.setPromotionCode(rs.getString("PromotionCode"));

			return fm;
		}

	}

	@Override
	public FinanceMain getFinanceMainForPftCalc(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinType, CustID, FinAmount, DownPayment, FeeChargeAmt, GrcPeriodEndDate");
		sql.append(", NextRepayPftDate, NextRepayRvwDate, FinIsActive, ProfitDaysBasis, FinStartDate");
		sql.append(", FinAssetValue, LastRepayPftDate, LastRepayRvwDate, FinCurrAssetValue, MaturityDate");
		sql.append(", FinStatus, FinStsReason, InitiateUser");
		sql.append(", ClosingStatus, LastRepayDate, NextRepayDate, PromotionCode, PastduePftCalMthd");
		sql.append(", PastduePftMargin, InstBasedSchd, SchdVersion");
		sql.append(" From FinanceMain");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinType(rs.getString("FinType"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setFinAmount(rs.getBigDecimal("FinAmount"));
				fm.setDownPayment(rs.getBigDecimal("DownPayment"));
				fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
				fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
				fm.setNextRepayPftDate(rs.getDate("NextRepayPftDate"));
				fm.setNextRepayRvwDate(rs.getDate("NextRepayRvwDate"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
				fm.setLastRepayPftDate(rs.getDate("LastRepayPftDate"));
				fm.setLastRepayRvwDate(rs.getDate("LastRepayRvwDate"));
				fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setFinStatus(rs.getString("FinStatus"));
				fm.setFinStsReason(rs.getString("FinStsReason"));
				fm.setInitiateUser(rs.getLong("InitiateUser"));
				fm.setClosingStatus(rs.getString("ClosingStatus"));
				fm.setLastRepayDate(rs.getDate("LastRepayDate"));
				fm.setNextRepayDate(rs.getDate("NextRepayDate"));
				fm.setPromotionCode(rs.getString("PromotionCode"));
				fm.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
				fm.setPastduePftMargin(rs.getBigDecimal("PastduePftMargin"));
				fm.setInstBasedSchd(rs.getBoolean("InstBasedSchd"));
				fm.setSchdVersion(rs.getInt("SchdVersion"));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinanceMainForRpyCancel(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ft.FinType, FinID, FinReference, CustID, GrcPeriodEndDate, NextRepayPftDate, NextRepayRvwDate");
		sql.append(", FinStatus, FinAmount, FeeChargeAmt, FinRepaymentAmount, fm.FinCcy, FinBranch");
		sql.append(", ProfitDaysBasis, FinStartDate, FinAssetValue, LastRepayPftDate, LastRepayRvwDate");
		sql.append(", FinCurrAssetValue, MaturityDate, PromotionCode, e.EntityCode, WriteoffLoan, SchdVersion");
		sql.append(" From Financemain fm");
		sql.append(" inner join RmtFinanceTypes ft on ft.Fintype = fm.Fintype");
		sql.append(" inner join SmtDivisionDetail d on d.DivisionCode = ft.FinDivision");
		sql.append(" inner join entity e on e.EntityCode = d.EntityCode");
		sql.append("  Where FinID = ?");

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinType(rs.getString("FinType"));
				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
				fm.setNextRepayPftDate(rs.getDate("NextRepayPftDate"));
				fm.setNextRepayRvwDate(rs.getDate("NextRepayRvwDate"));
				fm.setFinStatus(rs.getString("FinStatus"));
				fm.setFinAmount(rs.getBigDecimal("FinAmount"));
				fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
				fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
				fm.setFinCcy(rs.getString("FinCcy"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
				fm.setLastRepayPftDate(rs.getDate("LastRepayPftDate"));
				fm.setLastRepayRvwDate(rs.getDate("LastRepayRvwDate"));
				fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setPromotionCode(rs.getString("PromotionCode"));
				fm.setEntityCode(rs.getString("EntityCode"));
				fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
				fm.setSchdVersion(rs.getInt("SchdVersion"));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record is not found in Financemain for the specified FinID >> {}", finID);
		}

		return null;
	}

	@Override
	public FinanceMain getFMForVAS(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, FinID, FinReference, FinType, FinBranch, FinCcy");
		sql.append(" From Financemain");
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setCustID(rs.getLong("CustID"));
				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinType(rs.getString("FinType"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setFinCcy(rs.getString("FinCcy"));

				return fm;

			}, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinanceMainForBatch(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, GrcPeriodEndDate, FinRepaymentAmount");
		sql.append(", FinCommitmentRef, FinLimitRef, FinCcy, FinBranch");
		sql.append(", CustID, FinAmount, FeeChargeAmt, DownPayment, DownPayBank, DownPaySupl");
		sql.append(", FinType, FinStartDate, GraceTerms, NumberOfTerms, NextGrcPftDate");
		sql.append(", NextRepayDate, LastRepayPftDate, NextRepayPftDate, ProductCategory, FinCategory");
		sql.append(", LastRepayRvwDate, NextRepayRvwDate, FinAssetValue, FinCurrAssetValue, FinRepayMethod");
		sql.append(", RepayFrq, ClosingStatus, DueBucket, CalRoundingMode, RoundingTarget, RecordType");
		sql.append(", Version, ProfitDaysBasis, FinStatus, FinStsReason, PastduePftCalMthd, PastduePftMargin");
		sql.append(", InitiateUser, MaturityDate, MinDownPayPerc, PromotionCode, FinIsActive, SanBsdSchdle");
		sql.append(", PromotionSeqId, SvAmount, CbAmount, EmployeeName, SchdVersion, OverdraftTxnChrgReq");
		sql.append(", OverdraftCalcChrg, OverdraftChrgAmtOrPerc, OverdraftChrCalOn, FinType, LastRepayDate");
		sql.append(", LastRepayCpzDate From Financemain");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
				fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
				fm.setFinCommitmentRef(rs.getString("FinCommitmentRef"));
				fm.setFinLimitRef(rs.getString("FinLimitRef"));
				fm.setFinCcy(rs.getString("FinCcy"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setFinAmount(rs.getBigDecimal("FinAmount"));
				fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
				fm.setDownPayment(rs.getBigDecimal("DownPayment"));
				fm.setDownPayBank(rs.getBigDecimal("DownPayBank"));
				fm.setDownPaySupl(rs.getBigDecimal("DownPaySupl"));
				fm.setFinType(rs.getString("FinType"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setGraceTerms(rs.getInt("GraceTerms"));
				fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
				fm.setNextGrcPftDate(rs.getDate("NextGrcPftDate"));
				fm.setNextRepayDate(rs.getDate("NextRepayDate"));
				fm.setLastRepayPftDate(rs.getDate("LastRepayPftDate"));
				fm.setNextRepayPftDate(rs.getDate("NextRepayPftDate"));
				fm.setProductCategory(rs.getString("ProductCategory"));
				fm.setFinCategory(rs.getString("FinCategory"));
				fm.setLastRepayRvwDate(rs.getDate("LastRepayRvwDate"));
				fm.setNextRepayRvwDate(rs.getDate("NextRepayRvwDate"));
				fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
				fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
				fm.setFinRepayMethod(rs.getString("FinRepayMethod"));
				fm.setRepayFrq(rs.getString("RepayFrq"));
				fm.setClosingStatus(rs.getString("ClosingStatus"));
				fm.setDueBucket(rs.getInt("DueBucket"));
				fm.setCalRoundingMode(rs.getString("CalRoundingMode"));
				fm.setRoundingTarget(rs.getInt("RoundingTarget"));
				fm.setRecordType(rs.getString("RecordType"));
				fm.setVersion(rs.getInt("Version"));
				fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
				fm.setFinStatus(rs.getString("FinStatus"));
				fm.setFinStsReason(rs.getString("FinStsReason"));
				fm.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
				fm.setPastduePftMargin(rs.getBigDecimal("PastduePftMargin"));
				fm.setInitiateUser(rs.getLong("InitiateUser"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setMinDownPayPerc(rs.getBigDecimal("MinDownPayPerc"));
				fm.setPromotionCode(rs.getString("PromotionCode"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
				fm.setPromotionSeqId(JdbcUtil.getLong(rs.getObject("PromotionSeqId")));
				fm.setSvAmount(rs.getBigDecimal("SvAmount"));
				fm.setCbAmount(rs.getBigDecimal("CbAmount"));
				fm.setEmployeeName(rs.getString("EmployeeName"));
				fm.setSchdVersion(rs.getInt("SchdVersion"));
				fm.setOverdraftTxnChrgReq(rs.getBoolean("OverdraftTxnChrgReq"));
				fm.setOverdraftCalcChrg(rs.getString("OverdraftCalcChrg"));
				fm.setOverdraftChrgAmtOrPerc(rs.getBigDecimal("OverdraftChrgAmtOrPerc"));
				fm.setOverdraftChrCalOn(rs.getString("OverdraftChrCalOn"));
				fm.setFinType(rs.getString("FinType"));
				fm.setLastRepayDate(rs.getDate("LastRepayDate"));
				fm.setLastRepayCpzDate(rs.getDate("LastRepayCpzDate"));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<Long> getFinanceMainListByBatch(final Date curBD, final Date nextBD, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID");
		sql.append(" From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinStartDate >= ? and MaturityDate < ?");

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setDate(index++, JdbcUtil.getDate(curBD));
			ps.setDate(index, JdbcUtil.getDate(nextBD));
		}, (rs, rowNum) -> {
			return rs.getLong("FinID");
		});
	}

	@Override
	public long save(FinanceMain fm, TableType tableType, boolean wif) {
		StringBuilder sql = new StringBuilder("insert into");
		if (wif) {
			sql.append(" WIFFinanceMain");
		} else {
			sql.append(" FinanceMain");
		}
		sql.append(tableType.getSuffix());
		sql.append("(FinID, FinReference, GraceTerms, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod, GraceBaseRate");
		sql.append(", GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw, GrcPftRvwFrq");
		sql.append(", NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate, RepaySpecialRate");
		sql.append(", RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate, AllowRepayRvw");
		sql.append(", RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate, MaturityDate");
		sql.append(", CpzAtGraceEnd, DownPayment, DownPayBank, DownPaySupl, ReqRepayAmount, TotalProfit");
		sql.append(", TotalCpz, TotalGrossPft, TotalGracePft, TotalGraceCpz, TotalGrossGrcPft, TotalRepayAmt");
		sql.append(", GrcRateBasis, RepayRateBasis, FinType, FinRemarks, FinCcy, ScheduleMethod, FinContractDate");
		sql.append(", ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay, FinStartDate");
		sql.append(", FinAmount, FinRepaymentAmount, CustID, Defferments, PlanDeferCount, FinBranch, FinSourceID");
		sql.append(", AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange, AvailedDefFrqChange");
		sql.append(", RecalType, FinIsActive, FinAssetValue, LastRepayDate");
		sql.append(", LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd");
		sql.append(", GrcMargin, RepayMargin, FinCommitmentRef, FinLimitRef, FinCurrAssetValue");
		sql.append(", ClosingStatus, FinApprovedDate");
		sql.append(", DedupFound, SkipDedup, Blacklisted, GrcProfitDaysBasis, StepFinance, StepPolicy");
		sql.append(", AlwManualSteps, NoOfSteps, StepType, AnualizedPercRate, EffectiveRateOfReturn, FinRepayPftOnFrq");
		sql.append(", LinkedFinRef, GrcMinRate, GrcMaxRate, GrcMaxAmount, RpyMinRate, RpyMaxRate, ManualSchedule");
		sql.append(", MinDownPayPerc, TDSApplicable");
		sql.append(", AlwBPI, BpiTreatment, PlanEMIHAlw, PlanEMIHMethod, PlanEMIHMaxPerYear");
		sql.append(", PlanEMIHMax, PlanEMIHLockPeriod, PlanEMICpz, CalRoundingMode, RoundingTarget, AlwMultiDisb");
		sql.append(", FinRepayMethod, FeeChargeAmt, BpiAmount, DeductFeeDisb, RvwRateApplFor, SchCalOnRvw");
		sql.append(", PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin, FinCategory");
		sql.append(", ProductCategory, AdvanceEMI, BpiPftDaysBasis, FixedTenorRate, FixedRateTenor, BusinessVertical");
		sql.append(", GrcAdvType, GrcAdvTerms, AdvType, AdvTerms, AdvStage, AllowDrawingPower, AllowRevolving");
		sql.append(", AppliedLoanAmt, FinIsRateRvwAtGrcEnd");
		sql.append(", OverdraftTxnChrgReq, OverdraftCalcChrg, OverdraftChrgAmtOrPerc, OverdraftChrCalOn");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn ");
		if (!wif) {
			sql.append(", InvestmentRef, MigratedFinance, ScheduleMaintained, ScheduleRegenerated");
			sql.append(", CustDSR, LimitValid, OverrideLimit, FinPurpose, FinStatus, FinStsReason");
			sql.append(", InitiateUser, DeviationApproval");
			sql.append(", FinPreApprovedRef, MandateID, JointAccount, JointCustId ");
			sql.append(", RcdMaintainSts, NextUserId, Priority");
			sql.append(", InitiateDate, AccountsOfficer, ApplicationNo, DsaCode, DroplineFrq");
			sql.append(", FirstDroplineDate, PftServicingODLimit, ReferralId, EmployeeName, DmaCode, SalesDepartment");
			sql.append(", QuickDisb, WifReference, UnPlanEMIHLockPeriod, UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi");
			sql.append(", MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, ReAgeBucket, DueBucket");
			sql.append(", EligibilityMethod, samplingRequired, legalRequired, connector, ProcessAttributes");
			sql.append(", PromotionCode, TdsPercentage, TdsStartDate, TdsEndDate, TdsLimitAmt");
			sql.append(", VanReq, VanCode, SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount");
			sql.append(", AlwGrcAdj, EndGrcPeriodAftrFullDisb, AutoIncGrcEndDate, PlanEMIHAlwInGrace, SchdVersion");
			sql.append(", SubVentionFrom, ManufacturerDealerId, Escrow, CustBankId");
			sql.append(", ManualSchdType, Isra, SanctionedDate");
			// HL
			sql.append(", FinOcrRequired, ReqLoanAmt, ReqLoanTenor, OfferProduct, OfferAmount");
			sql.append(", CustSegmentation, BaseProduct, ProcessType, BureauTimeSeries");
			sql.append(", CampaignName, ExistingLanRefNo, LeadSource, PoSource , Rsa, Verification");
			sql.append(", SourcingBranch, SourChannelCategory, AsmName, OfferId");
			sql.append(", Pmay, parentRef, loanSplitted, AlwLoanSplit, InstBasedSchd, AllowSubvention");
			sql.append(", TdsType, NoOfGrcSteps, CalcOfSteps, StepsAppliedFor, SecurityMandateID");
		}
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?");
		if (!wif) {
			sql.append(", ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			// HL
			sql.append(", ?, ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ? , ?, ?");
			sql.append(", ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");

		}
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setLong(++index, fm.getFinID());
				ps.setString(++index, fm.getFinReference());
				ps.setInt(++index, fm.getGraceTerms());
				ps.setInt(++index, fm.getNumberOfTerms());
				ps.setDate(++index, JdbcUtil.getDate(fm.getGrcPeriodEndDate()));
				ps.setBoolean(++index, fm.isAllowGrcPeriod());
				ps.setString(++index, fm.getGraceBaseRate());
				ps.setString(++index, fm.getGraceSpecialRate());
				ps.setBigDecimal(++index, fm.getGrcPftRate());
				ps.setString(++index, fm.getGrcPftFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextGrcPftDate()));
				ps.setBoolean(++index, fm.isAllowGrcPftRvw());
				ps.setString(++index, fm.getGrcPftRvwFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextGrcPftRvwDate()));
				ps.setBoolean(++index, fm.isAllowGrcCpz());
				ps.setString(++index, fm.getGrcCpzFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextGrcCpzDate()));
				ps.setString(++index, fm.getRepayBaseRate());
				ps.setString(++index, fm.getRepaySpecialRate());
				ps.setBigDecimal(++index, fm.getRepayProfitRate());
				ps.setString(++index, fm.getRepayFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextRepayDate()));
				ps.setString(++index, fm.getRepayPftFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextRepayPftDate()));
				ps.setBoolean(++index, fm.isAllowRepayRvw());
				ps.setString(++index, fm.getRepayRvwFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextRepayRvwDate()));
				ps.setBoolean(++index, fm.isAllowRepayCpz());
				ps.setString(++index, fm.getRepayCpzFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextRepayCpzDate()));
				ps.setDate(++index, JdbcUtil.getDate(fm.getMaturityDate()));
				ps.setBoolean(++index, fm.isCpzAtGraceEnd());
				ps.setBigDecimal(++index, fm.getDownPayment());
				ps.setBigDecimal(++index, fm.getDownPayBank());
				ps.setBigDecimal(++index, fm.getDownPaySupl());
				ps.setBigDecimal(++index, fm.getReqRepayAmount());
				ps.setBigDecimal(++index, fm.getTotalProfit());
				ps.setBigDecimal(++index, fm.getTotalCpz());
				ps.setBigDecimal(++index, fm.getTotalGrossPft());
				ps.setBigDecimal(++index, fm.getTotalGracePft());
				ps.setBigDecimal(++index, fm.getTotalGraceCpz());
				ps.setBigDecimal(++index, fm.getTotalGrossGrcPft());
				ps.setBigDecimal(++index, fm.getTotalRepayAmt());
				ps.setString(++index, fm.getGrcRateBasis());
				ps.setString(++index, fm.getRepayRateBasis());
				ps.setString(++index, fm.getFinType());
				ps.setString(++index, fm.getFinRemarks());
				ps.setString(++index, fm.getFinCcy());
				ps.setString(++index, fm.getScheduleMethod());
				ps.setDate(++index, JdbcUtil.getDate(fm.getFinContractDate()));
				ps.setString(++index, fm.getProfitDaysBasis());
				ps.setDate(++index, JdbcUtil.getDate(fm.getReqMaturity()));
				ps.setInt(++index, fm.getCalTerms());
				ps.setDate(++index, JdbcUtil.getDate(fm.getCalMaturity()));
				ps.setBigDecimal(++index, fm.getFirstRepay());
				ps.setBigDecimal(++index, fm.getLastRepay());
				ps.setDate(++index, JdbcUtil.getDate(fm.getFinStartDate()));
				ps.setBigDecimal(++index, fm.getFinAmount());
				ps.setBigDecimal(++index, fm.getFinRepaymentAmount());
				ps.setLong(++index, fm.getCustID());
				ps.setInt(++index, fm.getDefferments());
				ps.setInt(++index, fm.getPlanDeferCount());
				ps.setString(++index, fm.getFinBranch());
				ps.setString(++index, fm.getFinSourceID());
				ps.setInt(++index, fm.getAllowedDefRpyChange());
				ps.setInt(++index, fm.getAvailedDefRpyChange());
				ps.setInt(++index, fm.getAllowedDefFrqChange());
				ps.setInt(++index, fm.getAvailedDefFrqChange());
				ps.setString(++index, fm.getRecalType());
				ps.setBoolean(++index, fm.isFinIsActive());
				ps.setBigDecimal(++index, fm.getFinAssetValue());
				ps.setDate(++index, JdbcUtil.getDate(fm.getLastRepayDate()));
				ps.setDate(++index, JdbcUtil.getDate(fm.getLastRepayPftDate()));
				ps.setDate(++index, JdbcUtil.getDate(fm.getLastRepayRvwDate()));
				ps.setDate(++index, JdbcUtil.getDate(fm.getLastRepayCpzDate()));
				ps.setBoolean(++index, fm.isAllowGrcRepay());
				ps.setString(++index, fm.getGrcSchdMthd());
				ps.setBigDecimal(++index, fm.getGrcMargin());
				ps.setBigDecimal(++index, fm.getRepayMargin());
				ps.setString(++index, fm.getFinCommitmentRef());
				ps.setString(++index, fm.getFinLimitRef());
				ps.setBigDecimal(++index, fm.getFinCurrAssetValue());
				ps.setString(++index, fm.getClosingStatus());
				ps.setDate(++index, JdbcUtil.getDate(fm.getFinApprovedDate()));
				ps.setBoolean(++index, fm.isDedupFound());
				ps.setBoolean(++index, fm.isSkipDedup());
				ps.setBoolean(++index, fm.isBlacklisted());
				ps.setString(++index, fm.getGrcProfitDaysBasis());
				ps.setBoolean(++index, fm.isStepFinance());
				ps.setString(++index, fm.getStepPolicy());
				ps.setBoolean(++index, fm.isAlwManualSteps());
				ps.setInt(++index, fm.getNoOfSteps());
				ps.setString(++index, fm.getStepType());
				ps.setBigDecimal(++index, fm.getAnualizedPercRate());
				ps.setBigDecimal(++index, fm.getEffectiveRateOfReturn());
				ps.setBoolean(++index, fm.isFinRepayPftOnFrq());
				ps.setString(++index, fm.getLinkedFinRef());
				ps.setBigDecimal(++index, fm.getGrcMinRate());
				ps.setBigDecimal(++index, fm.getGrcMaxRate());
				ps.setBigDecimal(++index, fm.getGrcMaxAmount());
				ps.setBigDecimal(++index, fm.getRpyMinRate());
				ps.setBigDecimal(++index, fm.getRpyMaxRate());
				ps.setBoolean(++index, fm.isManualSchedule());
				ps.setBigDecimal(++index, fm.getMinDownPayPerc());
				ps.setBoolean(++index, fm.isTDSApplicable());
				ps.setBoolean(++index, fm.isAlwBPI());
				ps.setString(++index, fm.getBpiTreatment());
				ps.setBoolean(++index, fm.isPlanEMIHAlw());
				ps.setString(++index, fm.getPlanEMIHMethod());
				ps.setInt(++index, fm.getPlanEMIHMaxPerYear());
				ps.setInt(++index, fm.getPlanEMIHMax());
				ps.setInt(++index, fm.getPlanEMIHLockPeriod());
				ps.setBoolean(++index, fm.isPlanEMICpz());
				ps.setString(++index, fm.getCalRoundingMode());
				ps.setInt(++index, fm.getRoundingTarget());
				ps.setBoolean(++index, fm.isAlwMultiDisb());
				ps.setString(++index, fm.getFinRepayMethod());
				ps.setBigDecimal(++index, fm.getFeeChargeAmt());
				ps.setBigDecimal(++index, fm.getBpiAmount());
				ps.setBigDecimal(++index, fm.getDeductFeeDisb());
				ps.setString(++index, fm.getRvwRateApplFor());
				ps.setString(++index, fm.getSchCalOnRvw());
				ps.setString(++index, fm.getPastduePftCalMthd());
				ps.setString(++index, fm.getDroppingMethod());
				ps.setBoolean(++index, fm.isRateChgAnyDay());
				ps.setBigDecimal(++index, fm.getPastduePftMargin());
				ps.setString(++index, fm.getFinCategory());
				ps.setString(++index, fm.getProductCategory());
				ps.setBigDecimal(++index, fm.getAdvanceEMI());
				ps.setString(++index, fm.getBpiPftDaysBasis());
				ps.setBigDecimal(++index, fm.getFixedTenorRate());
				ps.setInt(++index, fm.getFixedRateTenor());
				ps.setObject(++index, fm.getBusinessVertical());
				ps.setString(++index, fm.getGrcAdvType());
				ps.setInt(++index, fm.getGrcAdvTerms());
				ps.setString(++index, fm.getAdvType());
				ps.setInt(++index, fm.getAdvTerms());
				ps.setString(++index, fm.getAdvStage());
				ps.setBoolean(++index, fm.isAllowDrawingPower());
				ps.setBoolean(++index, fm.isAllowRevolving());
				ps.setBigDecimal(++index, fm.getAppliedLoanAmt());
				ps.setBoolean(++index, fm.isFinIsRateRvwAtGrcEnd());
				ps.setBoolean(++index, fm.isOverdraftTxnChrgReq());
				ps.setString(++index, fm.getOverdraftCalcChrg());
				ps.setBigDecimal(++index, fm.getOverdraftChrgAmtOrPerc());
				ps.setString(++index, fm.getOverdraftChrCalOn());
				ps.setObject(++index, fm.getCreatedBy());
				ps.setTimestamp(++index, fm.getCreatedOn());
				ps.setObject(++index, fm.getApprovedBy());
				ps.setTimestamp(++index, fm.getApprovedOn());
				if (!wif) {
					ps.setString(++index, fm.getInvestmentRef());
					ps.setBoolean(++index, fm.isMigratedFinance());
					ps.setBoolean(++index, fm.isScheduleMaintained());
					ps.setBoolean(++index, fm.isScheduleRegenerated());
					ps.setBigDecimal(++index, fm.getCustDSR());
					ps.setBoolean(++index, fm.isLimitValid());
					ps.setBoolean(++index, fm.isOverrideLimit());
					ps.setString(++index, fm.getFinPurpose());
					ps.setString(++index, fm.getFinStatus());
					ps.setString(++index, fm.getFinStsReason());
					ps.setLong(++index, fm.getInitiateUser());
					ps.setBoolean(++index, fm.isDeviationApproval());
					ps.setString(++index, fm.getFinPreApprovedRef());
					ps.setObject(++index, fm.getMandateID());
					ps.setBoolean(++index, fm.isJointAccount());
					ps.setLong(++index, fm.getJointCustId());
					ps.setString(++index, fm.getRcdMaintainSts());
					ps.setString(++index, fm.getNextUserId());
					ps.setInt(++index, fm.getPriority());
					ps.setDate(++index, JdbcUtil.getDate(fm.getInitiateDate()));
					ps.setLong(++index, fm.getAccountsOfficer());
					ps.setString(++index, fm.getApplicationNo());
					ps.setString(++index, fm.getDsaCode());
					ps.setString(++index, fm.getDroplineFrq());
					ps.setDate(++index, JdbcUtil.getDate(fm.getFirstDroplineDate()));
					ps.setBoolean(++index, fm.isPftServicingODLimit());
					ps.setString(++index, fm.getReferralId());
					ps.setString(++index, fm.getEmployeeName());
					ps.setString(++index, fm.getDmaCode());
					ps.setString(++index, fm.getSalesDepartment());
					ps.setBoolean(++index, fm.isQuickDisb());
					ps.setString(++index, fm.getWifReference());
					ps.setInt(++index, fm.getUnPlanEMIHLockPeriod());
					ps.setBoolean(++index, fm.isUnPlanEMICpz());
					ps.setBoolean(++index, fm.isReAgeCpz());
					ps.setInt(++index, fm.getMaxUnplannedEmi());
					ps.setInt(++index, fm.getMaxReAgeHolidays());
					ps.setInt(++index, fm.getAvailedUnPlanEmi());
					ps.setInt(++index, fm.getAvailedReAgeH());
					ps.setInt(++index, fm.getReAgeBucket());
					ps.setInt(++index, fm.getDueBucket());
					ps.setLong(++index, fm.getEligibilityMethod());
					ps.setBoolean(++index, fm.isSamplingRequired());
					ps.setBoolean(++index, fm.isLegalRequired());
					ps.setLong(++index, fm.getConnector());
					ps.setString(++index, fm.getProcessAttributes());
					ps.setString(++index, fm.getPromotionCode());
					ps.setBigDecimal(++index, fm.getTdsPercentage());
					ps.setDate(++index, JdbcUtil.getDate(fm.getTdsStartDate()));
					ps.setDate(++index, JdbcUtil.getDate(fm.getTdsEndDate()));
					ps.setBigDecimal(++index, fm.getTdsLimitAmt());
					ps.setBoolean(++index, fm.isVanReq());
					ps.setString(++index, fm.getVanCode());
					ps.setBoolean(++index, fm.isSanBsdSchdle());
					ps.setObject(++index, fm.getPromotionSeqId());
					ps.setBigDecimal(++index, fm.getSvAmount());
					ps.setBigDecimal(++index, fm.getCbAmount());
					ps.setBoolean(++index, fm.isAlwGrcAdj());
					ps.setBoolean(++index, fm.isEndGrcPeriodAftrFullDisb());
					ps.setBoolean(++index, fm.isAutoIncGrcEndDate());
					ps.setBoolean(++index, fm.isPlanEMIHAlwInGrace());
					ps.setInt(++index, fm.getSchdVersion());
					ps.setString(++index, fm.getSubVentionFrom());
					ps.setObject(++index, fm.getManufacturerDealerId());
					ps.setBoolean(++index, fm.isEscrow());
					ps.setObject(++index, fm.getCustBankId());

					ps.setString(++index, fm.getManualSchdType());
					ps.setBoolean(++index, fm.isIsra());
					ps.setDate(++index, JdbcUtil.getDate(fm.getSanctionedDate()));

					// HL
					ps.setBoolean(++index, fm.isFinOcrRequired());
					ps.setBigDecimal(++index, fm.getReqLoanAmt());
					ps.setInt(++index, fm.getReqLoanTenor());
					ps.setString(++index, fm.getOfferProduct());
					ps.setBigDecimal(++index, fm.getOfferAmount());
					ps.setString(++index, fm.getCustSegmentation());
					ps.setString(++index, fm.getBaseProduct());
					ps.setString(++index, fm.getProcessType());
					ps.setString(++index, fm.getBureauTimeSeries());
					ps.setString(++index, fm.getCampaignName());
					ps.setString(++index, fm.getExistingLanRefNo());
					ps.setString(++index, fm.getLeadSource());
					ps.setString(++index, fm.getPoSource());
					ps.setBoolean(++index, fm.isRsa());
					ps.setString(++index, fm.getVerification());
					ps.setString(++index, fm.getSourcingBranch());
					ps.setString(++index, fm.getSourChannelCategory());
					ps.setObject(++index, fm.getAsmName());
					ps.setString(++index, fm.getOfferId());
					ps.setBoolean(++index, fm.isPmay());
					ps.setString(++index, fm.getParentRef());
					ps.setBoolean(++index, fm.isLoanSplitted());
					ps.setBoolean(++index, fm.isAlwLoanSplit());
					ps.setBoolean(++index, fm.isInstBasedSchd());
					ps.setBoolean(++index, fm.isAllowSubvention());
					ps.setString(++index, fm.getTdsType());
					ps.setInt(++index, fm.getNoOfGrcSteps());
					ps.setString(++index, fm.getCalcOfSteps());
					ps.setString(++index, fm.getStepsAppliedFor());
					ps.setObject(++index, fm.getSecurityMandateID());
				}
				ps.setInt(++index, fm.getVersion());
				ps.setLong(++index, fm.getLastMntBy());
				ps.setTimestamp(++index, fm.getLastMntOn());
				ps.setString(++index, fm.getRecordStatus());
				ps.setString(++index, fm.getRoleCode());
				ps.setString(++index, fm.getNextRoleCode());
				ps.setString(++index, fm.getTaskId());
				ps.setString(++index, fm.getNextTaskId());
				ps.setString(++index, fm.getRecordType());
				ps.setLong(++index, fm.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return fm.getFinID();
	}

	@Override
	public void update(FinanceMain financeMain, TableType tableType, boolean wif) {
		StringBuilder sql = new StringBuilder("Update");
		if (wif) {
			sql.append(" WIFFinanceMain");
		} else {
			sql.append(" FinanceMain");
		}
		sql.append(tableType.getSuffix());
		sql.append(" Set NumberOfTerms = :NumberOfTerms,GraceTerms=:GraceTerms, ");
		sql.append(" GrcPeriodEndDate = :GrcPeriodEndDate, AllowGrcPeriod = :AllowGrcPeriod,");
		sql.append(" GraceBaseRate = :GraceBaseRate, GraceSpecialRate = :GraceSpecialRate,");
		sql.append(" GrcPftRate = :GrcPftRate, GrcPftFrq = :GrcPftFrq,");
		sql.append(" NextGrcPftDate = :NextGrcPftDate, AllowGrcPftRvw = :AllowGrcPftRvw,");
		sql.append(" GrcPftRvwFrq = :GrcPftRvwFrq, NextGrcPftRvwDate = :NextGrcPftRvwDate,");
		sql.append(" AllowGrcCpz = :AllowGrcCpz, GrcCpzFrq = :GrcCpzFrq, NextGrcCpzDate = :NextGrcCpzDate,");
		sql.append(" RepayBaseRate = :RepayBaseRate, RepaySpecialRate = :RepaySpecialRate,");
		sql.append(" RepayProfitRate = :RepayProfitRate, RepayFrq = :RepayFrq, NextRepayDate = :NextRepayDate,");
		sql.append(" RepayPftFrq = :RepayPftFrq, NextRepayPftDate = :NextRepayPftDate,");
		sql.append(" AllowRepayRvw = :AllowRepayRvw, RepayRvwFrq = :RepayRvwFrq,");
		sql.append(" NextRepayRvwDate = :NextRepayRvwDate, AllowRepayCpz = :AllowRepayCpz,");
		sql.append(" RepayCpzFrq = :RepayCpzFrq, NextRepayCpzDate = :NextRepayCpzDate,");
		sql.append(" MaturityDate = :MaturityDate, CpzAtGraceEnd = :CpzAtGraceEnd, DownPayment = :DownPayment,");
		sql.append(" DownPayBank=:DownPayBank, DownPaySupl=:DownPaySupl, ReqRepayAmount = :ReqRepayAmount,");
		sql.append(" TotalProfit = :TotalProfit,TotalCpz= :TotalCpz, TotalGrossPft = :TotalGrossPft,");
		sql.append(" TotalGracePft= :TotalGracePft,TotalGraceCpz= :TotalGraceCpz,TotalGrossGrcPft= :TotalGrossGrcPft,");
		sql.append(
				" TotalRepayAmt= :TotalRepayAmt, GrcRateBasis = :GrcRateBasis, RepayRateBasis = :RepayRateBasis, FinType = :FinType,");
		sql.append(" FinRemarks = :FinRemarks, FinCcy = :FinCcy, ScheduleMethod = :ScheduleMethod,");
		sql.append(" ProfitDaysBasis = :ProfitDaysBasis, ReqMaturity = :ReqMaturity, CalTerms = :CalTerms,");
		sql.append(" CalMaturity = :CalMaturity, FirstRepay = :FirstRepay, LastRepay = :LastRepay,");
		sql.append(" FinStartDate = :FinStartDate, FinAmount = :FinAmount,FinContractDate= :FinContractDate,");
		sql.append(" FinRepaymentAmount = :FinRepaymentAmount, CustID = :CustID,Defferments = :Defferments,");
		sql.append(" PlanDeferCount= :PlanDeferCount, FinBranch =:FinBranch, FinSourceID= :FinSourceID,");
		sql.append(" AllowedDefRpyChange= :AllowedDefRpyChange, AvailedDefRpyChange= :AvailedDefRpyChange,");
		sql.append(" AllowedDefFrqChange= :AllowedDefFrqChange, AvailedDefFrqChange= :AvailedDefFrqChange,");
		sql.append(" RecalType=:RecalType, FinIsActive= :FinIsActive,FinAssetValue= :FinAssetValue,");
		sql.append(" LastRepayDate= :LastRepayDate,LastRepayPftDate= :LastRepayPftDate,");
		sql.append(
				" LastRepayRvwDate= :LastRepayRvwDate, LastRepayCpzDate= :LastRepayCpzDate,AllowGrcRepay= :AllowGrcRepay,");
		sql.append(" GrcSchdMthd= :GrcSchdMthd, GrcMargin= :GrcMargin,RepayMargin= :RepayMargin,");
		sql.append(
				" FinCommitmentRef= :FinCommitmentRef, FinLimitRef=:FinLimitRef, FinCurrAssetValue= :FinCurrAssetValue,");
		sql.append(" ClosingStatus= :ClosingStatus, FinApprovedDate= :FinApprovedDate,");
		sql.append(
				" AnualizedPercRate =:AnualizedPercRate , EffectiveRateOfReturn =:EffectiveRateOfReturn , FinRepayPftOnFrq =:FinRepayPftOnFrq ,");
		sql.append(
				" GrcProfitDaysBasis = :GrcProfitDaysBasis, StepFinance = :StepFinance, StepPolicy = :StepPolicy, StepType = :StepType,");
		sql.append(
				" AlwManualSteps = :AlwManualSteps, NoOfSteps = :NoOfSteps, ManualSchedule=:ManualSchedule , LinkedFinRef=:LinkedFinRef, ");
		sql.append(
				" GrcMinRate=:GrcMinRate, GrcMaxRate=:GrcMaxRate ,GrcMaxAmount=:GrcMaxAmount, RpyMinRate=:RpyMinRate, RpyMaxRate=:RpyMaxRate, ");
		sql.append(" MinDownPayPerc=:MinDownPayPerc, TDSApplicable=:TDSApplicable,AlwBPI=:AlwBPI , ");
		sql.append(
				" BpiTreatment=:BpiTreatment , PlanEMIHAlw=:PlanEMIHAlw , PlanEMIHMethod=:PlanEMIHMethod , PlanEMIHMaxPerYear=:PlanEMIHMaxPerYear , ");
		sql.append(
				" PlanEMIHMax=:PlanEMIHMax , PlanEMIHLockPeriod=:PlanEMIHLockPeriod , PlanEMICpz=:PlanEMICpz , CalRoundingMode=:CalRoundingMode ,RoundingTarget=:RoundingTarget, AlwMultiDisb=:AlwMultiDisb, FeeChargeAmt=:FeeChargeAmt, BpiAmount=:BpiAmount, DeductFeeDisb=:DeductFeeDisb, ");
		sql.append(
				"RvwRateApplFor =:RvwRateApplFor, SchCalOnRvw =:SchCalOnRvw,PastduePftCalMthd=:PastduePftCalMthd,DroppingMethod=:DroppingMethod,RateChgAnyDay=:RateChgAnyDay,PastduePftMargin=:PastduePftMargin,  FinCategory=:FinCategory, ProductCategory=:ProductCategory, AllowDrawingPower =:AllowDrawingPower, AllowRevolving =:AllowRevolving, AppliedLoanAmt =:AppliedLoanAmt, CbAmount =:CbAmount, FinIsRateRvwAtGrcEnd =:FinIsRateRvwAtGrcEnd,");
		sql.append(" OverdraftTxnChrgReq = :OverdraftTxnChrgReq, OverdraftCalcChrg = :OverdraftCalcChrg");
		sql.append(", OverdraftChrgAmtOrPerc = :OverdraftChrgAmtOrPerc, OverdraftChrCalOn = :OverdraftChrCalOn,");
		if (!wif) {
			sql.append(
					" DroplineFrq= :DroplineFrq,FirstDroplineDate = :FirstDroplineDate,PftServicingODLimit = :PftServicingODLimit,");
			sql.append(
					" MigratedFinance = :MigratedFinance,ScheduleMaintained = :ScheduleMaintained, ScheduleRegenerated = :ScheduleRegenerated,");
			sql.append(
					" LimitValid= :LimitValid, OverrideLimit= :OverrideLimit,FinPurpose=:FinPurpose, DeviationApproval=:DeviationApproval,FinPreApprovedRef=:FinPreApprovedRef, MandateID=:MandateID, ");
			sql.append(" FinStatus=:FinStatus , FinStsReason=:FinStsReason, InitiateUser=:InitiateUser,");
			sql.append(" CustDSR=:CustDSR, JointAccount=:JointAccount, JointCustId=:JointCustId, ");
			sql.append(" RcdMaintainSts=:RcdMaintainSts, FinRepayMethod=:FinRepayMethod, ");
			sql.append(" NextUserId=:NextUserId, Priority=:Priority, InitiateDate= :InitiateDate, ");
			sql.append(
					" AccountsOfficer =:AccountsOfficer,DsaCode = :DsaCode, ApplicationNo=:ApplicationNo, ReferralId =:ReferralId ,EmployeeName =:EmployeeName, DmaCode =:DmaCode , SalesDepartment =:SalesDepartment , QuickDisb =:QuickDisb , WifReference =:WifReference ,");
			sql.append(
					" UnPlanEMIHLockPeriod=:UnPlanEMIHLockPeriod , UnPlanEMICpz=:UnPlanEMICpz, ReAgeCpz=:ReAgeCpz, MaxUnplannedEmi=:MaxUnplannedEmi, MaxReAgeHolidays=:MaxReAgeHolidays ,");
			sql.append(
					" AvailedUnPlanEmi=:AvailedUnPlanEmi, AvailedReAgeH=:AvailedReAgeH,ReAgeBucket=:ReAgeBucket,EligibilityMethod=:EligibilityMethod,samplingRequired=:samplingRequired,legalRequired=:legalRequired,connector=:connector, ProcessAttributes=:ProcessAttributes,");
			sql.append(
					" TDSPercentage = :TdsPercentage, TdsStartDate = :TdsStartDate, TdsEndDate = :TdsEndDate, TdsLimitAmt = :TdsLimitAmt");
			sql.append(", VanReq =:VanReq, VanCode =:VanCode, PlanEMIHAlwInGrace = :PlanEMIHAlwInGrace");
			sql.append(
					", SubVentionFrom = :SubVentionFrom , ManufacturerDealerId = :ManufacturerDealerId, Escrow = :Escrow, CustBankId =:CustBankId");

			// HL
			sql.append(", FinOcrRequired = :FinOcrRequired, ReqLoanAmt = :ReqLoanAmt, ReqLoanTenor = :ReqLoanTenor");
			sql.append(", OfferProduct = :OfferProduct, OfferAmount = :OfferAmount");
			sql.append(
					", CustSegmentation = :CustSegmentation, BaseProduct = :BaseProduct, ProcessType = :ProcessType");
			sql.append(", BureauTimeSeries = :BureauTimeSeries,  CampaignName = :CampaignName");
			sql.append(", ExistingLanRefNo = :ExistingLanRefNo, LeadSource = :LeadSource, PoSource = :PoSource");
			sql.append(", Rsa = :Rsa,  Verification = :Verification, SourcingBranch = :SourcingBranch");
			sql.append(", SourChannelCategory = :SourChannelCategory, AsmName = :AsmName, OfferId = :OfferId");
			sql.append(", AlwGrcAdj = :AlwGrcAdj, EndGrcPeriodAftrFullDisb = :EndGrcPeriodAftrFullDisb");
			sql.append(", AutoIncGrcEndDate = :AutoIncGrcEndDate, Pmay = :Pmay ");
			sql.append(", InvestmentRef=:InvestmentRef, ParentRef=:ParentRef,LoanSplitted=:LoanSplitted ");
			sql.append(
					", AlwLoanSplit = :AlwLoanSplit, InstBasedSchd=:InstBasedSchd, TdsType = :TdsType, NoOfGrcSteps =:NoOfGrcSteps,");
			sql.append("CalcOfSteps =:CalcOfSteps, StepsAppliedFor =:StepsAppliedFor,");
			sql.append(" ManualSchdType = :ManualSchdType, Isra = :Isra, SanctionedDate = :SanctionedDate,");
		}
		sql.append(
				" AdvanceEMI = :AdvanceEMI, BpiPftDaysBasis = :BpiPftDaysBasis, FixedTenorRate=:FixedTenorRate, FixedRateTenor=:FixedRateTenor");
		sql.append(
				", GrcAdvType = :GrcAdvType, GrcAdvTerms = :GrcAdvTerms, AdvType = :AdvType, AdvTerms = :AdvTerms, AdvStage = :AdvStage");
		sql.append(", PromotionCode = :PromotionCode, SanBsdSchdle=:SanBsdSchdle ");

		if (!financeMain.isFinIsActive()) {
			financeMain.setClosedDate(FinanceUtil.deriveClosedDate(financeMain));
			sql.append(", ClosedDate = :ClosedDate ");
		}

		sql.append(", Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where FinID = :FinID");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeMain);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public void delete(FinanceMain fm, TableType tableType, boolean wif, boolean finalize) {
		StringBuilder sql = new StringBuilder("delete From");
		if (wif) {
			sql.append(" WIFFinanceMain");
		} else {
			sql.append(" FinanceMain");
		}
		sql.append(tableType.getSuffix());
		sql.append(" Where FinID = ?");

		if (tableType == TableType.MAIN_TAB || !finalize) {
			sql.append(QueryUtil.getConcurrencyClause(tableType));
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = 0;

		try {
			recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				ps.setLong(index++, fm.getFinID());
				if (tableType == TableType.MAIN_TAB || !finalize) {
					if (tableType == TableType.TEMP_TAB) {
						ps.setTimestamp(index, fm.getPrevMntOn());
					} else {
						ps.setInt(index, fm.getVersion() - 1);
					}
				}
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public boolean isFinReferenceExists(String finReference, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select count(FinID)");

		if (isWIF) {
			sql.append(" From WIFFinanceMain");
		} else {
			sql.append(" From FinanceMain");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finReference) > 0;
	}

	@Override
	public List<BigDecimal> getActualPftBal(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TotalProfit, TotalCpz");
		sql.append(" From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinanceMain result = null;
		try {
			result = this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setTotalProfit(rs.getBigDecimal("TotalProfit"));
				fm.setTotalCpz(rs.getBigDecimal("TotalCpz"));

				return fm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		List<BigDecimal> list = new ArrayList<BigDecimal>();
		if (result != null) {
			list.add(result.getTotalProfit());
			list.add(result.getTotalCpz());
		} else {
			list.add(BigDecimal.ZERO);
			list.add(BigDecimal.ZERO);
		}

		return list;
	}

	@Override
	public List<FinanceEnquiry> getFinanceDetailsByCustId(long custId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinBranch, FinType, FinCcy, ScheduleMethod, ProfitDaysBasis");
		sql.append(", FinStartDate, NumberOfTerms, CustID, FinAmount, GrcPeriodEndDate, MaturityDate");
		sql.append(", FinRepaymentAmount, FinIsActive, AllowGrcPeriod, LovDescFinTypeName, LovDescCustCIF");
		sql.append(", LovDescCustShrtName, LovDescFinBranchName, Blacklisted, LovDescFinScheduleOn");
		sql.append(", FeeChargeAmt, ClosingStatus, CustTypeCtg, GraceTerms, LovDescFinDivision");
		sql.append(", LovDescProductCodeName, Defferments, SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(" From FinanceEnquiry_View");
		sql.append(" Where CustID = ?");
		sql.append(" and (ClosingStatus is null or ClosingStatus != 'C')");
		sql.append(" order by FinType, FinCcy ");
		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, custId);
		}, (rs, rowNum) -> {
			FinanceEnquiry fm = new FinanceEnquiry();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setScheduleMethod(rs.getString("ScheduleMethod"));
			fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
			fm.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
			fm.setLovDescCustCIF(rs.getString("LovDescCustCIF"));
			fm.setLovDescCustShrtName(rs.getString("LovDescCustShrtName"));
			fm.setLovDescFinBranchName(rs.getString("LovDescFinBranchName"));
			fm.setBlacklisted(rs.getBoolean("Blacklisted"));
			fm.setLovDescFinScheduleOn(rs.getString("LovDescFinScheduleOn"));
			fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));
			fm.setCustTypeCtg(rs.getString("CustTypeCtg"));
			fm.setGraceTerms(rs.getInt("GraceTerms"));
			fm.setLovDescFinDivision(rs.getString("lovDescFinDivision"));
			fm.setLovDescProductCodeName(rs.getString("lovDescProductCodeName"));
			fm.setDefferments(rs.getInt("Defferments"));
			fm.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
			fm.setPromotionSeqId(rs.getInt("PromotionSeqId"));
			fm.setSvAmount(rs.getBigDecimal("SvAmount"));
			fm.setCbAmount(rs.getBigDecimal("CbAmount"));
			fm.setCreatedOn(rs.getTimestamp("CreatedBy"));
			fm.setCreatedBy(JdbcUtil.getLong(rs.getObject("CreatedOn")));
			fm.setApprovedOn(rs.getTimestamp("ApprovedBy"));
			fm.setApprovedBy(JdbcUtil.getLong(rs.getObject("ApprovedOn")));

			return fm;
		});
	}

	@Override
	public void updateCustCIF(long custID, long finID) {
		String sql = "Update FinanceMain_Temp Set CustID = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, custID);
			ps.setLong(2, finID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateNPA(long finID, boolean undernpa) {
		String sql = "Update FinanceMain Set UnderNpa = ? Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setBoolean(1, undernpa);
			ps.setLong(2, finID);
		});
	}

	@Override
	public void updateFinBlackListStatus(long finID) {
		String sql = "Update FinanceMain Set Blacklisted = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			ps.setBoolean(1, true);
			ps.setLong(2, finID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinanceSummary getFinanceProfitDetails(long finID) {
		/* Removed the View FINANCEPROFITENQUIRY_VIEW, and the same is handled here */

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference,fm.FinBranch, fm.FinType, fm.FinCcy");
		sql.append(", fm.NumberOfTerms, fm.MaturityDate, fm.FinStartDate, fm.LastRepayDate");
		sql.append(", fpd.CustCIF, fpd.NoOdInst, fpd.NoPaidInst, fm.RepayProfitRate");
		sql.append(", fpd.ODPrincipal, fpd.ODProfit, fm.DownPayment");
		sql.append(", fm.DownPayBank, fm.DownPaySupl, fpd.TotalPriBal, fpd.PftAccrueSusp");
		sql.append(", fpd.TotalPriPaid, fpd.TotalPftPaid, fpd.TotalPriBal, fpd.TotalPftBal");
		sql.append(", (fpd.TotalPriPaid + fpd.TotalPriBal) TotalPriSchd");
		sql.append(", (fpd.TotalPftPaid + fpd.TotalPftBal) TotalPftSchd");
		sql.append(", (fpd.TotalPriPaid + fpd.TotalPriBal + fpd.TotalPftPaid + fpd.TotalPftBal) TotalOriginal");
		sql.append(", (fpd.TotalPriPaid + fpd.TotalPriBal - fpd.TdSchdPri) OutStandPrincipal");
		sql.append(", (fpd.TotalPftPaid + fpd.TotalPftBal - fpd.TdSchdPft) OutStandProfit");
		sql.append(", (fpd.TotalPriPaid + fpd.TotalPftPaid) TotalPaid");
		sql.append(", (fpd.TotalPriBal + fpd.TotalPftBal) TotalUnPaid");
		sql.append(", (fpd.TotalPftPaid + fpd.PftAccrued) EarnedProfit");
		sql.append(", (fpd.TotalPftBal - fpd.PftAccrued) UnEarnedProfit");
		sql.append(", (fpd.ODPrincipal + fpd.ODProfit) TotalOverDue");
		sql.append(", (fpd.TotalPriPaid + fpd.TotalPriBal - fpd.TdSchdPri + fpd.TotalPftPaid");
		sql.append(" + fpd.TotalPftBal - fpd.TdSchdPft) TotalOutStanding");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApprovedName");
		sql.append(" From FinanceMain fm");
		sql.append(" Join FinPftDetails fpd on fm.FinID =  fpd.FinID");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = fm.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = fm.ApprovedBy");
		sql.append(" Where fm.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceSummary fs = new FinanceSummary();

				fs.setFinID(rs.getLong("FinID"));
				fs.setFinReference(rs.getString("FinReference"));
				fs.setFinBranch(rs.getString("FinBranch"));
				fs.setFinType(rs.getString("FinType"));
				fs.setFinCcy(rs.getString("FinCcy"));
				fs.setNumberOfTerms(rs.getLong("NumberOfTerms"));
				fs.setMaturityDate(JdbcUtil.getDate(rs.getDate("MaturityDate")));
				fs.setFinStartDate(JdbcUtil.getDate(rs.getDate("FinStartDate")));
				fs.setFinLastRepayDate(JdbcUtil.getDate(rs.getDate("LastRepayDate")));
				fs.setCustCIF(rs.getString("CustCIF"));
				fs.setOverDueInstlments(rs.getLong("NoOdInst"));
				fs.setPaidInstlments(rs.getLong("NoPaidInst"));
				fs.setFinRate(rs.getBigDecimal("RepayProfitRate"));
				fs.setOverDuePrincipal(rs.getBigDecimal("ODPrincipal"));
				fs.setOverDueProfit(rs.getBigDecimal("ODProfit"));
				fs.setTotalDownPayment(rs.getBigDecimal("DownPayment"));
				fs.setDownPaymentToBank(rs.getBigDecimal("DownPayBank"));
				fs.setDownPaymentToSpplier(rs.getBigDecimal("DownPaySupl"));
				fs.setUnPaidPrincipal(rs.getBigDecimal("TotalPriBal"));
				fs.setProfitSuspended(rs.getBigDecimal("PftAccrueSusp"));
				fs.setSchdPriPaid(rs.getBigDecimal("TotalPriPaid"));
				fs.setSchdPftPaid(rs.getBigDecimal("TotalPftPaid"));
				fs.setCurrentFinanceAmount(rs.getBigDecimal("TotalPriBal"));
				fs.setUnPaidProfit(rs.getBigDecimal("TotalPftBal"));
				fs.setTotalPriSchd(rs.getBigDecimal("TotalPriSchd"));
				fs.setTotalPftSchd(rs.getBigDecimal("TotalPftSchd"));
				fs.setTotalOriginal(rs.getBigDecimal("TotalOriginal"));
				fs.setOutStandPrincipal(rs.getBigDecimal("OutStandPrincipal"));
				fs.setOutStandProfit(rs.getBigDecimal("OutStandProfit"));
				fs.setTotalPaid(rs.getBigDecimal("TotalPaid"));
				fs.setTotalUnPaid(rs.getBigDecimal("TotalUnPaid"));
				fs.setEarnedProfit(rs.getBigDecimal("EarnedProfit"));
				fs.setTotalOverDue(rs.getBigDecimal("TotalOverDue"));
				fs.setOverDueInstlementPft(rs.getBigDecimal("TotalOverDue"));
				fs.setTotalOutStanding(rs.getBigDecimal("TotalOutStanding"));
				fs.setCreatedName(rs.getString("CreatedName"));
				fs.setApprovedName(rs.getString("ApprovedName"));

				return fs;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public Boolean saveRejectFinanceDetails(FinanceMain fm) {
		saveRejectFinanace(fm);

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO RejectDocumentDetails SELECT * FROM  DocumentDetails_Temp WHERE ReferenceId = ?");

		long finID = fm.getFinID();

		doReject(sql.toString(), finID);

		copyTableById("FinAgreementDetail_Temp", "RejectFinAgreementDetail", finID);
		copyTableById("FinanceEligibilityDetail", "RejectFinanceEligibilityDetail", finID);
		copyTableById("FinanceScoreHeader", "RejectFinanceScoreHeader", finID);
		copyTableById("FinDisbursementDetails_Temp", "RejectFinDisbursementdetails", finID);
		copyTableById("FinRepayinstruction_Temp", "RejectFinRepayinstruction", finID);
		copyTableById("FinScheduledetails_Temp", "RejectFinScheduledetails", finID);
		copyTableById("FinDedupDetail", "RejectFinDedupDetail", finID);
		copyTableById("FinBlackListDetail", "RejectFinBlackListDetail", finID);
		copyTableById("FinODPenaltyRates_Temp", "RejectFinODPenaltyRates", finID);
		copyTableById("FinFeeCharges_Temp", "RejectFinFeeCharges", finID);

		sql = new StringBuilder();
		sql.append("INSERT INTO RejectFinanceScoreDetail");
		sql.append(" SELECT D.HeaderID, D.SubGroupID, D.RuleId, D.MaxScore, D.ExecScore");
		sql.append(" FROM FinanceScoreDetail D");
		sql.append(" INNER JOIN RejectFinanceScoreHeader H ON D.HeaderID = H.HeaderId ");
		sql.append(" WHERE FinID = ?");

		doReject(sql.toString(), finID);
		sql.delete(0, sql.length());

		return true;
	}

	private void copyTableById(String fromTable, String toTable, long finID) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO ");
		sql.append(toTable);
		sql.append(" SELECT * FROM ");
		sql.append(fromTable);
		sql.append(" WHERE FinID = ?");

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setLong(1, finID);
		});
	}

	private void doReject(String sql, long finID) {
		logger.debug(Literal.SQL + sql);
		this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, finID);
		});
	}

	public void saveFinanceSnapshot(FinanceMain fm) {
		copyTableById("FinScheduledetails", "FinScheduleDetails_Log", fm.getFinID());
	}

	public String saveRejectFinanace(FinanceMain fm) {
		StringBuilder sql = new StringBuilder("Insert Into ");
		sql.append(" RejectFinanceMain ");
		sql.append(" (FinID, FinReference, GraceTerms, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod");
		sql.append(", GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw");
		sql.append(", GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate");
		sql.append(", RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate");
		sql.append(", AllowRepayRvw, RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate");
		sql.append(", MaturityDate, CpzAtGraceEnd, DownPayment, DownPayBank, DownPaySupl, ReqRepayAmount");
		sql.append(", TotalProfit, TotalCpz, TotalGrossPft, TotalGracePft, TotalGraceCpz, TotalGrossGrcPft");
		sql.append(", TotalRepayAmt, GrcRateBasis, RepayRateBasis, FinType, FinRemarks, FinCcy, ScheduleMethod");
		sql.append(", FinContractDate, ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay");
		sql.append(", LastRepay, FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments, PlanDeferCount");
		sql.append(", FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange");
		sql.append(", AvailedDefFrqChange, RecalType, FinIsActive, FinAssetValue, LastRepayDate, LastRepayPftDate");
		sql.append(", LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd, GrcMargin, RepayMargin");
		sql.append(", FinCommitmentRef, FinLimitRef, FinCurrAssetValue, ClosingStatus, FinApprovedDate");
		sql.append(", DedupFound, SkipDedup, Blacklisted, GrcProfitDaysBasis, StepFinance, StepPolicy");
		sql.append(", StepType, AlwManualSteps, NoOfSteps, ManualSchedule, AnualizedPercRate, EffectiveRateOfReturn");
		sql.append(", FinRepayPftOnFrq, LinkedFinRef, GrcMinRate, GrcMaxRate, GrcMaxAmount, RpyMinRate");
		sql.append(", RpyMaxRate, InvestmentRef, MigratedFinance, ScheduleMaintained, ScheduleRegenerated");
		sql.append(", CustDSR, FeeChargeAmt, BpiAmount, DeductFeeDisb, LimitValid, OverrideLimit, FinPurpose");
		sql.append(", DeviationApproval, FinPreApprovedRef, MandateID, FinStatus, FinStsReason, initiateUser");
		sql.append(", JointAccount, JointCustId, RcdMaintainSts, FinRepayMethod, AlwBPI, BpiTreatment");
		sql.append(", PlanEMIHAlw, PlanEMIHMethod, PlanEMIHMaxPerYear, PlanEMIHMax, PlanEMIHLockPeriod");
		sql.append(", PlanEMICpz, CalRoundingMode, RoundingTarget, AlwMultiDisb, NextUserId, Priority");
		sql.append(", DsaCode, MinDownPayPerc, InitiateDate, TDSApplicable, AccountsOfficer, ApplicationNo");
		sql.append(", ReferralId, DmaCode, SalesDepartment, QuickDisb, WifReference, UnPlanEMIHLockPeriod");
		sql.append(", UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi, MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH");
		sql.append(", RvwRateApplFor, SchCalOnRvw, PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin");
		sql.append(", FinCategory, ProductCategory, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, RejectStatus, RejectReason");
		sql.append(", DueBucket, AdvanceEMI, BpiPftDaysBasis, FixedTenorRate, FixedRateTenor, ProcessAttributes");
		sql.append(", SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount, ReqLoanAmt, ReqLoanTenor, FinOcrRequired");
		sql.append(", OfferProduct, OfferAmount, CustSegmentation, BaseProduct, ProcessType, BureauTimeSeries");
		sql.append(", CampaignName, ExistingLanRefNo, LeadSource, PoSource, Rsa, Verification, SourcingBranch");
		sql.append(", SourChannelCategory, AsmName, OfferId, AlwLoanSplit, AlwGrcAdj, EndGrcPeriodAftrFullDisb");
		sql.append(", AutoIncGrcEndDate, TdsType, SchdVersion, ManualSchdType, CreatedBy, CreatedOn, ApprovedBy");
		sql.append(", ApprovedOn)");
		sql.append(" Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 0;

				ps.setLong(++index, fm.getFinID());
				ps.setString(++index, fm.getFinReference());
				ps.setInt(++index, fm.getGraceTerms());
				ps.setInt(++index, fm.getNumberOfTerms());
				ps.setDate(++index, JdbcUtil.getDate(fm.getGrcPeriodEndDate()));
				ps.setBoolean(++index, fm.isAllowGrcPeriod());
				ps.setString(++index, fm.getGraceBaseRate());
				ps.setString(++index, fm.getGraceSpecialRate());
				ps.setBigDecimal(++index, fm.getGrcPftRate());
				ps.setString(++index, fm.getGrcPftFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextGrcPftDate()));
				ps.setBoolean(++index, fm.isAllowGrcPftRvw());
				ps.setString(++index, fm.getGrcPftRvwFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextGrcPftRvwDate()));
				ps.setBoolean(++index, fm.isAllowGrcCpz());
				ps.setString(++index, fm.getGrcCpzFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextGrcCpzDate()));
				ps.setString(++index, fm.getRepayBaseRate());
				ps.setString(++index, fm.getRepaySpecialRate());
				ps.setBigDecimal(++index, fm.getRepayProfitRate());
				ps.setString(++index, fm.getRepayFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextRepayDate()));
				ps.setString(++index, fm.getRepayPftFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextRepayPftDate()));
				ps.setBoolean(++index, fm.isAllowRepayRvw());
				ps.setString(++index, fm.getRepayRvwFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextRepayRvwDate()));
				ps.setBoolean(++index, fm.isAllowRepayCpz());
				ps.setString(++index, fm.getRepayCpzFrq());
				ps.setDate(++index, JdbcUtil.getDate(fm.getNextRepayCpzDate()));
				ps.setDate(++index, JdbcUtil.getDate(fm.getMaturityDate()));
				ps.setBoolean(++index, fm.isCpzAtGraceEnd());
				ps.setBigDecimal(++index, fm.getDownPayment());
				ps.setBigDecimal(++index, fm.getDownPayBank());
				ps.setBigDecimal(++index, fm.getDownPaySupl());
				ps.setBigDecimal(++index, fm.getReqRepayAmount());
				ps.setBigDecimal(++index, fm.getTotalProfit());
				ps.setBigDecimal(++index, fm.getTotalCpz());
				ps.setBigDecimal(++index, fm.getTotalGrossPft());
				ps.setBigDecimal(++index, fm.getTotalGracePft());
				ps.setBigDecimal(++index, fm.getTotalGraceCpz());
				ps.setBigDecimal(++index, fm.getTotalGrossGrcPft());
				ps.setBigDecimal(++index, fm.getTotalRepayAmt());
				ps.setString(++index, fm.getGrcRateBasis());
				ps.setString(++index, fm.getRepayRateBasis());
				ps.setString(++index, fm.getFinType());
				ps.setString(++index, fm.getFinRemarks());
				ps.setString(++index, fm.getFinCcy());
				ps.setString(++index, fm.getScheduleMethod());
				ps.setDate(++index, JdbcUtil.getDate(fm.getFinContractDate()));
				ps.setString(++index, fm.getProfitDaysBasis());
				ps.setDate(++index, JdbcUtil.getDate(fm.getReqMaturity()));
				ps.setInt(++index, fm.getCalTerms());
				ps.setDate(++index, JdbcUtil.getDate(fm.getCalMaturity()));
				ps.setBigDecimal(++index, fm.getFirstRepay());
				ps.setBigDecimal(++index, fm.getLastRepay());
				ps.setDate(++index, JdbcUtil.getDate(fm.getFinStartDate()));
				ps.setBigDecimal(++index, fm.getFinAmount());
				ps.setBigDecimal(++index, fm.getFinRepaymentAmount());
				ps.setLong(++index, fm.getCustID());
				ps.setInt(++index, fm.getDefferments());
				ps.setInt(++index, fm.getPlanDeferCount());
				ps.setString(++index, fm.getFinBranch());
				ps.setString(++index, fm.getFinSourceID());
				ps.setInt(++index, fm.getAllowedDefRpyChange());
				ps.setInt(++index, fm.getAvailedDefRpyChange());
				ps.setInt(++index, fm.getAllowedDefFrqChange());
				ps.setInt(++index, fm.getAvailedDefFrqChange());
				ps.setString(++index, fm.getRecalType());
				ps.setBoolean(++index, fm.isFinIsActive());
				ps.setBigDecimal(++index, fm.getFinAssetValue());
				ps.setDate(++index, JdbcUtil.getDate(fm.getLastRepayDate()));
				ps.setDate(++index, JdbcUtil.getDate(fm.getLastRepayPftDate()));
				ps.setDate(++index, JdbcUtil.getDate(fm.getLastRepayRvwDate()));
				ps.setDate(++index, JdbcUtil.getDate(fm.getLastRepayCpzDate()));
				ps.setBoolean(++index, fm.isAllowGrcRepay());
				ps.setString(++index, fm.getGrcSchdMthd());
				ps.setBigDecimal(++index, fm.getGrcMargin());
				ps.setBigDecimal(++index, fm.getRepayMargin());
				ps.setString(++index, fm.getFinCommitmentRef());
				ps.setString(++index, fm.getFinLimitRef());
				ps.setBigDecimal(++index, fm.getFinCurrAssetValue());
				ps.setString(++index, fm.getClosingStatus());
				ps.setDate(++index, JdbcUtil.getDate(fm.getFinApprovedDate()));
				ps.setBoolean(++index, fm.isDedupFound());
				ps.setBoolean(++index, fm.isSkipDedup());
				ps.setBoolean(++index, fm.isBlacklisted());
				ps.setString(++index, fm.getGrcProfitDaysBasis());
				ps.setBoolean(++index, fm.isStepFinance());
				ps.setString(++index, fm.getStepPolicy());
				ps.setString(++index, fm.getStepType());
				ps.setBoolean(++index, fm.isAlwManualSteps());
				ps.setInt(++index, fm.getNoOfSteps());
				ps.setBoolean(++index, fm.isManualSchedule());
				ps.setBigDecimal(++index, fm.getAnualizedPercRate());
				ps.setBigDecimal(++index, fm.getEffectiveRateOfReturn());
				ps.setBoolean(++index, fm.isFinRepayPftOnFrq());
				ps.setString(++index, fm.getLinkedFinRef());
				ps.setBigDecimal(++index, fm.getGrcMinRate());
				ps.setBigDecimal(++index, fm.getGrcMaxRate());
				ps.setBigDecimal(++index, fm.getGrcMaxAmount());
				ps.setBigDecimal(++index, fm.getRpyMinRate());
				ps.setBigDecimal(++index, fm.getRpyMaxRate());
				ps.setString(++index, fm.getInvestmentRef());
				ps.setBoolean(++index, fm.isMigratedFinance());
				ps.setBoolean(++index, fm.isScheduleMaintained());
				ps.setBoolean(++index, fm.isScheduleRegenerated());
				ps.setBigDecimal(++index, fm.getCustDSR());
				ps.setBigDecimal(++index, fm.getFeeChargeAmt());
				ps.setBigDecimal(++index, fm.getBpiAmount());
				ps.setBigDecimal(++index, fm.getDeductFeeDisb());
				ps.setBoolean(++index, fm.isLimitValid());
				ps.setBoolean(++index, fm.isOverrideLimit());
				ps.setString(++index, fm.getFinPurpose());
				ps.setBoolean(++index, fm.isDeviationApproval());
				ps.setString(++index, fm.getFinPreApprovedRef());
				ps.setLong(++index, fm.getMandateID());
				ps.setString(++index, fm.getFinStatus());
				ps.setString(++index, fm.getFinStsReason());
				ps.setLong(++index, fm.getInitiateUser());
				ps.setBoolean(++index, fm.isJointAccount());
				ps.setLong(++index, fm.getJointCustId());
				ps.setString(++index, fm.getRcdMaintainSts());
				ps.setString(++index, fm.getFinRepayMethod());
				ps.setBoolean(++index, fm.isAlwBPI());
				ps.setString(++index, fm.getBpiTreatment());
				ps.setBoolean(++index, fm.isPlanEMIHAlw());
				ps.setString(++index, fm.getPlanEMIHMethod());
				ps.setInt(++index, fm.getPlanEMIHMaxPerYear());
				ps.setInt(++index, fm.getPlanEMIHMax());
				ps.setInt(++index, fm.getPlanEMIHLockPeriod());
				ps.setBoolean(++index, fm.isPlanEMICpz());
				ps.setString(++index, fm.getCalRoundingMode());
				ps.setInt(++index, fm.getRoundingTarget());
				ps.setBoolean(++index, fm.isAlwMultiDisb());
				ps.setString(++index, fm.getNextUserId());
				ps.setInt(++index, fm.getPriority());
				ps.setString(++index, fm.getDsaCode());
				ps.setBigDecimal(++index, fm.getMinDownPayPerc());
				ps.setDate(++index, JdbcUtil.getDate(fm.getInitiateDate()));
				ps.setBoolean(++index, fm.isTDSApplicable());
				ps.setLong(++index, fm.getAccountsOfficer());
				ps.setString(++index, fm.getApplicationNo());
				ps.setString(++index, fm.getReferralId());
				ps.setString(++index, fm.getDmaCode());
				ps.setString(++index, fm.getSalesDepartment());
				ps.setBoolean(++index, fm.isQuickDisb());
				ps.setString(++index, fm.getWifReference());
				ps.setInt(++index, fm.getUnPlanEMIHLockPeriod());
				ps.setBoolean(++index, fm.isUnPlanEMICpz());
				ps.setBoolean(++index, fm.isReAgeCpz());
				ps.setInt(++index, fm.getMaxUnplannedEmi());
				ps.setInt(++index, fm.getMaxReAgeHolidays());
				ps.setInt(++index, fm.getAvailedUnPlanEmi());
				ps.setInt(++index, fm.getAvailedReAgeH());
				ps.setString(++index, fm.getRvwRateApplFor());
				ps.setString(++index, fm.getSchCalOnRvw());
				ps.setString(++index, fm.getPastduePftCalMthd());
				ps.setString(++index, fm.getDroppingMethod());
				ps.setBoolean(++index, fm.isRateChgAnyDay());
				ps.setBigDecimal(++index, fm.getPastduePftMargin());
				ps.setString(++index, fm.getFinCategory());
				ps.setString(++index, fm.getProductCategory());
				ps.setInt(++index, fm.getVersion());
				ps.setLong(++index, fm.getLastMntBy());
				ps.setTimestamp(++index, fm.getLastMntOn());
				ps.setString(++index, fm.getRecordStatus());
				ps.setString(++index, fm.getRoleCode());
				ps.setString(++index, fm.getNextRoleCode());
				ps.setString(++index, fm.getTaskId());
				ps.setString(++index, fm.getNextTaskId());
				ps.setString(++index, fm.getRecordType());
				ps.setLong(++index, fm.getWorkflowId());
				ps.setString(++index, fm.getRejectStatus());
				ps.setString(++index, fm.getRejectReason());
				ps.setInt(++index, fm.getDueBucket());
				ps.setBigDecimal(++index, fm.getAdvanceEMI());
				ps.setString(++index, fm.getBpiPftDaysBasis());
				ps.setBigDecimal(++index, fm.getFixedTenorRate());
				ps.setInt(++index, fm.getFixedRateTenor());
				ps.setString(++index, fm.getProcessAttributes());
				ps.setBoolean(++index, fm.isSanBsdSchdle());
				ps.setLong(++index, fm.getPromotionSeqId());
				ps.setBigDecimal(++index, fm.getSvAmount());
				ps.setBigDecimal(++index, fm.getCbAmount());
				ps.setBigDecimal(++index, fm.getReqLoanAmt());
				ps.setInt(++index, fm.getReqLoanTenor());
				ps.setBoolean(++index, fm.isFinOcrRequired());
				ps.setString(++index, fm.getOfferProduct());
				ps.setBigDecimal(++index, fm.getOfferAmount());
				ps.setString(++index, fm.getCustSegmentation());
				ps.setString(++index, fm.getBaseProduct());
				ps.setString(++index, fm.getProcessType());
				ps.setString(++index, fm.getBureauTimeSeries());
				ps.setString(++index, fm.getCampaignName());
				ps.setString(++index, fm.getExistingLanRefNo());
				ps.setString(++index, fm.getLeadSource());
				ps.setString(++index, fm.getPoSource());
				ps.setBoolean(++index, fm.isRsa());
				ps.setString(++index, fm.getVerification());
				ps.setString(++index, fm.getSourcingBranch());
				ps.setString(++index, fm.getSourChannelCategory());
				ps.setLong(++index, fm.getAsmName());
				ps.setString(++index, fm.getOfferId());
				ps.setBoolean(++index, fm.isAlwLoanSplit());
				ps.setBoolean(++index, fm.isAlwGrcAdj());
				ps.setBoolean(++index, fm.isEndGrcPeriodAftrFullDisb());
				ps.setBoolean(++index, fm.isAutoIncGrcEndDate());
				ps.setString(++index, fm.getTdsType());
				ps.setInt(++index, fm.getSchdVersion());
				ps.setString(++index, fm.getManualSchdType());
				ps.setLong(++index, fm.getCreatedBy());
				ps.setTimestamp(++index, fm.getCreatedOn());
				ps.setLong(++index, fm.getApprovedBy());
				ps.setTimestamp(++index, fm.getApprovedOn());
			}
		});

		return fm.getFinReference();
	}

	@Override
	public List<FinanceSummary> getFinExposureByCustId(long custId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinCommitmentRef, CmtTitle, CustID");
		sql.append(", NumberOfTerms, FinStartDate, FinType, TotalOriginal, CmtAmount, CmtAvailable");
		sql.append(", CmtExpiryDate, TotalOutStanding, MaturityDate, TotalRepayAmt, FinStatus");
		sql.append(" From CustFinanceExposure_View");
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinanceSummary fm = new FinanceSummary();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinCommitmentRef(rs.getString("FinCommitmentRef"));
			fm.setCmtTitle(rs.getString("CmtTitle"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinType(rs.getString("FinType"));
			fm.setTotalOriginal(rs.getBigDecimal("TotalOriginal"));
			fm.setCmtAmount(rs.getBigDecimal("CmtAmount"));
			fm.setCmtAvailable(rs.getBigDecimal("CmtAvailable"));
			fm.setCmtExpiryDate(rs.getDate("CmtExpiryDate"));
			fm.setTotalOutStanding(rs.getBigDecimal("TotalOutStanding"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setTotalRepayAmt(rs.getBigDecimal("TotalRepayAmt"));
			fm.setFinStatus(rs.getString("FinStatus"));

			return fm;

		}, custId);
	}

	@Override
	public String getNextRoleCodeByRef(long finID) {
		String sql = "Select NextRoleCode From FinanceMain_Temp Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, String.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateNextUserId(List<Long> finIDList, String oldUserId, String newUserId, boolean isManualAssignment) {
		MapSqlParameterSource finRefMap = new MapSqlParameterSource();

		for (Long finID : finIDList) {
			finRefMap.addValue("FinID", finID);
			finRefMap.addValue("USER_ID", oldUserId);
			finRefMap.addValue("NEW_USER_ID", newUserId);

			StringBuilder sql = new StringBuilder("Update FinanceMain_Temp");
			if (isManualAssignment) {
				sql.append(
						" SET NextUserId= (CASE WHEN NextRoleCode NOT like '%,%' AND COALESCE(NextUserId, ' ') LIKE (' ') THEN :NEW_USER_ID");
				sql.append(" WHEN NextRoleCode like '%,%' AND COALESCE(NextUserId, ' ') LIKE (' ') THEN :NEW_USER_ID ");
				if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.POSTGRES) {
					sql.append(
							" WHEN NextRoleCode like '%,%' AND NOT COALESCE(NextUserId, ' ') LIKE (' ') THEN NextUserId||','||:NEW_USER_ID END) ");
				} else {
					sql.append(
							" WHEN NextRoleCode like '%,%' AND NOT COALESCE(NextUserId, ' ') LIKE (' ') THEN NextUserId+','+:NEW_USER_ID END) ");
				}
			} else {
				sql.append(" SET NextUserId = REPLACE(NextUserId, :USER_ID, :NEW_USER_ID)");
			}
			sql.append(" Where FinID = :FinID");

			logger.debug(Literal.SQL + sql.toString());

			this.jdbcTemplate.update(sql.toString(), finRefMap);
		}
	}

	@Override
	public void updateDeviationApproval(FinanceMain fm, boolean rejected, String type) {
		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set DeviationApproval = ?");
		if (rejected) {
			sql.append(", NextTaskId = ?, NextRoleCode = ?, NextUserId = ?");
		}
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setBoolean(index++, fm.isDeviationApproval());

			if (rejected) {
				ps.setString(index++, fm.getNextTaskId());
				ps.setString(index++, fm.getNextRoleCode());
				ps.setString(index++, fm.getNextUserId());
			}

			ps.setLong(index, fm.getFinID());
		});
	}

	@Override
	public List<FinanceMain> getFinanceRefByPriority() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinId, FinReference, LastMntBy, NextUserId, NextRoleCode, Priority");
		sql.append(" From FinanceMain_Temp");
		sql.append(" Where FinReference Not In (Select Reference From MailLog) AND Priority != 0");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinId"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setLastMntBy(rs.getLong("LastMntBy"));
			fm.setNextUserId(rs.getString("NextUserId"));
			fm.setNextRoleCode(rs.getString("NextRoleCode"));
			fm.setPriority(rs.getInt("Priority"));

			return fm;
		});
	}

	@Override
	public void updatePaymentInEOD(FinanceMain fm) {
		StringBuilder sql = new StringBuilder("Update FinanceMain Set");
		sql.append(" FinStatus = ?, FinStsReason = ?, FinIsActive = ?, ClosingStatus = ?, FinRepaymentAmount = ?");

		if (!fm.isFinIsActive()) {
			fm.setClosedDate(FinanceUtil.deriveClosedDate(fm));

			sql.append(", ClosedDate = ?");
		}

		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fm.getFinStatus());
			ps.setString(index++, fm.getFinStsReason());
			ps.setBoolean(index++, fm.isFinIsActive());
			ps.setString(index++, fm.getClosingStatus());
			ps.setBigDecimal(index++, fm.getFinRepaymentAmount());

			if (!fm.isFinIsActive()) {
				ps.setDate(index++, JdbcUtil.getDate(fm.getClosedDate()));
			}

			ps.setLong(index, fm.getFinID());
		});
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public String getApprovedRepayMethod(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select FinRepayMethod");
		sql.append(" From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where finID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return StringUtils.trimToNull(this.jdbcOperations.queryForObject(sql.toString(), String.class, finID));
	}

	@Override
	public void updateMaturity(long finID, String closingStatus, boolean finIsActive, Date closedDate) {
		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(" Set FinIsActive = ?, ClosingStatus = ?");

		if (!finIsActive) {
			sql.append(", ClosedDate = ?");
		}
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setBoolean(index++, finIsActive);
			ps.setString(index++, closingStatus);

			if (!finIsActive) {
				ps.setDate(index++, JdbcUtil.getDate(FinanceUtil.deriveClosedDate(closedDate)));
			}

			ps.setLong(index, finID);
		});
	}

	@Override
	public List<String> getScheduleEffectModuleList(boolean schdChangeReq) {
		String sql = "Select ModuleName FROM ScheduleEffectModule Where SchdCanModify = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForList(sql, String.class, schdChangeReq);
	}

	@Override
	public List<FinanceMain> getFinanceMainbyCustId(final long custID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, CustID, FinAmount, FinType, FinCcy, WriteoffLoan");
		sql.append(" From FinanceMain");
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, custID);
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

			return fm;
		});
	}

	@Override
	public int getFinanceCountById(long finID, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select count(FinID)");

		if (!isWIF) {
			sql.append(" From FinanceMain");
		} else {
			sql.append(" From WIFFinanceMain");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? And FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID, 1);
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	@Override
	public Long getFinIDByFinReference(String finReference, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select FinID");

		if (!isWIF) {
			sql.append(" From FinanceMain");
		} else {
			sql.append(" From WIFFinanceMain");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ? and FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, finReference, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getFinCountByCustId(long custID) {
		String sql = "Select count(FinID) From FinanceMain Where CustID = ? And FinIsActive = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, custID, 1);
	}

	@Override
	public int getFinanceCountByMandateId(long mandateID) {
		String sql = "Select count(FinID) From FinanceMain Where MandateID = ? And FinIsActive = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, mandateID, 1);
	}

	@Override
	public Long getFinIDForMandate(String finReference, long mandateID) {
		String sql = "Select FinID From FinanceMain Where FinReference = ? And MandateID = ? And FinIsActive = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, finReference, mandateID, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int loanMandateSwapping(long finID, long newMandateID, String repayMethod, String type,
			boolean securityMandate) {

		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));

		sql.append(" Set");
		if (securityMandate) {
			sql.append(" SecurityMandateID = ?");
		} else {
			sql.append(" MandateID = ?");
		}

		if (StringUtils.isNotBlank(repayMethod)) {
			sql.append(", FinRepayMethod = ?");
		}

		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, newMandateID);

			if (StringUtils.isNotBlank(repayMethod)) {
				ps.setString(index++, repayMethod);
			}

			ps.setLong(index, finID);

		});

		return recordCount;

	}

	@Override
	public FinanceMain getFinanceDetailsForService1(String finReference, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, GrcPeriodEndDate, MaturityDate");
		sql.append(", AllowGrcPeriod, RepayFrq, FinStartDate, CustID, FinType");

		if (isWIF) {
			sql.append(" From WIFFinanceMain");
		} else {
			sql.append(" From FinanceMain");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
				fm.setRepayFrq(rs.getString("RepayFrq"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setFinType(rs.getString("FinType"));

				return fm;

			}, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinanceDetailsForService(long finID, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, GrcPeriodEndDate, MaturityDate");
		sql.append(", AllowGrcPeriod, RepayFrq, FinStartDate, CustID");

		if (isWIF) {
			sql.append(" From WIFFinanceMain");
		} else {
			sql.append(" From FinanceMain");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
				fm.setRepayFrq(rs.getString("RepayFrq"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setCustID(rs.getLong("CustID"));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int updateFinanceBasicDetails(FinanceMain fm, String type) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set DsaCode = ?, AccountsOfficer = ?, ReferralId = ?");
		sql.append(", SalesDepartment = ?, DmaCode = ?, OverdraftTxnChrgReq = ?");
		sql.append(", OverdraftCalcChrg = ?, OverdraftChrgAmtOrPerc = ?, OverdraftChrCalOn = ?");

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(fm.getProductCategory())) {
			sql.append(", FinAssetValue = ?, NumberOfTerms = ?, RepayFrq = ?");
		}
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, fm.getDsaCode());
			ps.setLong(index++, fm.getAccountsOfficer());
			ps.setString(index++, fm.getReferralId());
			ps.setString(index++, fm.getSalesDepartment());
			ps.setString(index++, fm.getDmaCode());
			ps.setBoolean(index++, fm.isOverdraftTxnChrgReq());
			ps.setString(index++, fm.getOverdraftCalcChrg());
			ps.setBigDecimal(index++, fm.getOverdraftChrgAmtOrPerc());
			ps.setString(index++, fm.getOverdraftChrCalOn());

			if (FinanceConstants.PRODUCT_ODFACILITY.equals(fm.getProductCategory())) {
				ps.setBigDecimal(index++, fm.getFinAssetValue());
				ps.setInt(index++, fm.getNumberOfTerms());
				ps.setString(index++, fm.getRepayFrq());
			}

			ps.setLong(index, fm.getFinID());
		});
	}

	@Override
	public List<String> getUsersLoginList(List<String> nextRoleCodes) {
		List<Long> userIds = new ArrayList<>();
		if (nextRoleCodes != null) {
			for (String id : nextRoleCodes) {
				userIds.add(Long.valueOf(id));
			}
		}

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("usrid", userIds);

		StringBuilder sql = new StringBuilder("Select usrlogin From secusers");
		sql.append(" Where usrid IN (:usrid) ");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcTemplate.queryForList(sql.toString(), mapSqlParameterSource, String.class);
	}

	@Override
	public FinanceMain getFinanceMainParms(long FinID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, Defferments, PlanDeferCount, AllowedDefRpyChange, AvailedDefRpyChange");
		sql.append(", AvailedDefFrqChange, FinIsActive, AlwBPI, BpiTreatment, PlanEMIHAlw, PlanEMIHAlwInGrace");
		sql.append(", PlanEMIHMethod, PlanEMIHMaxPerYear, PlanEMIHMax, PlanEMIHLockPeriod, PlanEMICpz, AlwMultiDisb");
		sql.append(", UnPlanEMIHLockPeriod, UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi, MaxReAgeHolidays");
		sql.append(", AvailedUnPlanEmi, AvailedReAgeH, PromotionCode, AllowedDefFrqChange, WriteoffLoan");
		sql.append(" From FinanceMain_View");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setDefferments(rs.getInt("Defferments"));
				fm.setDefferments(rs.getInt("Defferments"));
				fm.setPlanDeferCount(rs.getInt("PlanDeferCount"));
				fm.setAllowedDefRpyChange(rs.getInt("AllowedDefRpyChange"));
				fm.setAvailedDefRpyChange(rs.getInt("AvailedDefRpyChange"));
				fm.setAllowedDefFrqChange(rs.getInt("AllowedDefFrqChange"));
				fm.setAvailedDefFrqChange(rs.getInt("AvailedDefFrqChange"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setAlwBPI(rs.getBoolean("AlwBPI"));
				fm.setBpiTreatment(rs.getString("BpiTreatment"));
				fm.setPlanEMIHAlw(rs.getBoolean("PlanEMIHAlw"));
				fm.setPlanEMIHAlwInGrace(rs.getBoolean("PlanEMIHAlwInGrace"));
				fm.setPlanEMIHMethod(rs.getString("PlanEMIHMethod"));
				fm.setPlanEMIHMaxPerYear(rs.getInt("PlanEMIHMaxPerYear"));
				fm.setPlanEMIHMax(rs.getInt("PlanEMIHMax"));
				fm.setPlanEMIHLockPeriod(rs.getInt("PlanEMIHLockPeriod"));
				fm.setPlanEMICpz(rs.getBoolean("PlanEMICpz"));
				fm.setAlwMultiDisb(rs.getBoolean("AlwMultiDisb"));
				fm.setUnPlanEMIHLockPeriod(rs.getInt("UnPlanEMIHLockPeriod"));
				fm.setUnPlanEMICpz(rs.getBoolean("UnPlanEMICpz"));
				fm.setReAgeCpz(rs.getBoolean("ReAgeCpz"));
				fm.setMaxUnplannedEmi(rs.getInt("MaxUnplannedEmi"));
				fm.setMaxReAgeHolidays(rs.getInt("MaxReAgeHolidays"));
				fm.setAvailedUnPlanEmi(rs.getInt("AvailedUnPlanEmi"));
				fm.setAvailedReAgeH(rs.getInt("AvailedReAgeH"));
				fm.setPromotionCode(rs.getString("PromotionCode"));
				fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

				return fm;
			}, FinID);

		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinanceMain> getFinanceByCustId(long custId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.FinAmount, fm.FinType, fm.FinCcy, fm.FinAssetValue");
		sql.append(", fm.NumberOfTerms, fm.MaturityDate, fm.finStatus, fm.FinStartDate");
		sql.append(", fm.FirstRepay, ft.FinCategory lovDescFinProduct, fm.ClosingStatus");
		sql.append(", fm.RecordStatus, fm.ProductCategory, fm.FinBranch, fm.FinApprovedDate, fm.FinIsActive");
		sql.append(", fm.WriteoffLoan");
		sql.append(" From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type)).append(" fm");
		sql.append(" Inner Join RMTFinanceTypes ft On fm.FinType = ft.FinType");
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, custId);
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setFinStatus(rs.getString("finStatus"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFirstRepay(rs.getBigDecimal("FirstRepay"));
			fm.setLovDescFinProduct(rs.getString("lovDescFinProduct"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));
			fm.setRecordStatus(rs.getString("RecordStatus"));
			fm.setProductCategory(rs.getString("ProductCategory"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinApprovedDate(rs.getDate("FinApprovedDate"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

			return fm;
		});
	}

	@Override
	public List<FinanceMain> getFinanceByCollateralRef(String collateralRef) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.FinAmount, fm.FinType, fm.FinCcy");
		sql.append(", fm.ClosingStatus, fm.FinAssetValue, fm.NumberOfTerms, fm.MaturityDate, fm.Finstatus");
		sql.append(", fm.FinStartDate, fm.FirstRepay, ft.FinCategory");
		sql.append(", ca.CollateralRef, fm.WriteoffLoan");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join CollateralAssignment ca On CA.Reference = fm.FinReference");
		sql.append(" Inner Join RMTFinanceTypes ft ON ft.FinType = fm.FinType");
		sql.append(" Where CollateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, collateralRef);
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));
			fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setFinStatus(rs.getString("Finstatus"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFirstRepay(rs.getBigDecimal("FirstRepay"));
			fm.setFinCategory(rs.getString("FinCategory"));
			fm.setLovDescFinProduct(rs.getString("FinCategory"));
			// fm.setCollateralRef(rs.getString("CollateralRef"));
			fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

			return fm;
		});
	}

	@Override
	public List<Long> getFinReferencesByMandateId(long mandateId) {
		String sql = "Select FinID From FinanceMain Where MandateID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForList(sql, Long.class, mandateId);
	}

	@Override
	public List<Long> getFinIDList(String custCIF, String closingStatus) {
		StringBuilder sql = new StringBuilder("Select FinID");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner join CUstomers c on c.CustID = fm.CustID");
		sql.append(" Where CustCIF = ?");

		Object[] objects = new Object[] { custCIF };

		if (StringUtils.isBlank(closingStatus)) {
			sql.append(" and ClosingStatus is null");
		} else {
			sql.append(" and ClosingStatus = ?");
			objects = new Object[] { custCIF, closingStatus };
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, objects);
	}

	@Override
	public BigDecimal getFinAssetValue(long finID) {
		String sql = "Select FinAssetValue From FinanceMain Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
	}

	@Override
	public String getFinBranch(long finID) {
		String sql = "Select FinBranch From FinanceMain Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, String.class, finID);
	}

	public List<FinanceMain> getFinMainsForEODByCustId(Customer customer) {
		long custID = customer.getCustID();
		String corBankID = customer.getCustCoreBank();

		StringBuilder sql = getFinSelectQueryForEod();

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" Where c.CustCoreBank = ? and fm.FinIsActive = ?");
		} else {
			sql.append(" Where fm.CustID = ? and fm.FinIsActive = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		FinRowMapperForEod rowMapper = new FinRowMapperForEod();

		return this.jdbcOperations.query(sql.toString(), ps -> {

			if (CustomerExtension.CUST_CORE_BANK_ID) {
				ps.setString(1, corBankID);
			} else {
				ps.setLong(1, custID);
			}

			ps.setBoolean(2, true);
		}, rowMapper);

	}

	@Override
	public FinanceMain getFinMainsForEODByFinRef(long finID, boolean isActive) {
		StringBuilder sql = getFinSelectQueryForEod();
		sql.append(" Where fm.FinID = ? and fm.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinRowMapperForEod rowMapper = new FinRowMapperForEod();
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID, isActive);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateFinanceInEOD(FinanceMain financeMain, List<String> updateFields, boolean rateRvw) {
		StringBuilder sql = new StringBuilder("Update FinanceMain Set ");

		if (!updateFields.isEmpty()) {

			for (int i = 0; i < updateFields.size(); i++) {
				sql.append(updateFields.get(i));
				sql.append(" = :");
				sql.append(updateFields.get(i));
				if (i != updateFields.size() - 1) {
					sql.append(" ,");
				}
			}

			if (rateRvw) {
				sql.append(" ,");
			}
		}

		if (rateRvw) {
			// profit related fields for rate review
			sql.append(" TotalGracePft = :TotalGracePft, TotalGraceCpz = :TotalGraceCpz ");
			sql.append(" ,TotalGrossGrcPft = :TotalGrossGrcPft, TotalProfit = :TotalProfit ");
			sql.append(" ,TotalCpz = :TotalCpz, TotalGrossPft = :TotalGrossPft ");
			sql.append(" ,TotalRepayAmt = :TotalRepayAmt, FinRepaymentAmount = :FinRepaymentAmount ");
		}

		sql.append(" Where FinID = :FinID");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
	}

	@Override
	public List<FinanceMain> getBYCustIdForLimitRebuild(final long id, boolean orgination) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  FinID, FinReference, GrcPeriodEndDate, AllowGrcPeriod, GraceBaseRate, GraceSpecialRate");
		sql.append(", GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw, GrcPftRvwFrq, NextGrcPftRvwDate");
		sql.append(", AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate, RepaySpecialRate, RepayProfitRate");
		sql.append(", RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate, AllowRepayRvw, RepayRvwFrq");
		sql.append(", NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate, MaturityDate");
		sql.append(", CpzAtGraceEnd, GrcRateBasis, RepayRateBasis, FinType, FinCcy, ProfitDaysBasis");
		sql.append(", FirstRepay, LastRepay, ScheduleMethod, DownPayment, FinStartDate, FinAmount");
		sql.append(", CustID, FinBranch, FinSourceID, RecalType, FinIsActive, LastRepayDate, LastRepayPftDate");
		sql.append(", LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd, GrcMargin, RepayMargin");
		sql.append(", ClosingStatus, FinRepayPftOnFrq, GrcProfitDaysBasis, GrcMinRate, GrcMaxRate");
		sql.append(", GrcMaxAmount, RpyMinRate, RpyMaxRate, ManualSchedule, CalRoundingMode, RvwRateApplFor");
		sql.append(", SchCalOnRvw, FinAssetValue, FinCurrAssetValue, PastduePftCalMthd, DroppingMethod");
		sql.append(", RateChgAnyDay, PastduePftMargin, FinRepayMethod, MigratedFinance, ScheduleMaintained");
		sql.append(", ScheduleRegenerated, MandateID, FinStatus, FinStsReason");
		sql.append(", PromotionCode, FinCategory, ProductCategory, ReAgeBucket, TDSApplicable");
		sql.append(", SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount, TdsType, WriteoffLoan, ManualSchdType");

		if (orgination) {
			sql.append(", 1 LimitValid");
		}
		sql.append(" FROM FinanceMain");
		if (orgination) {
			sql.append(TableType.TEMP_TAB.getSuffix());
		}
		sql.append(" Where CustID= ?");
		if (orgination) {
			if (App.DATABASE == Database.ORACLE) {
				sql.append(" AND RcdMaintainSts IS NULL ");
			} else {
				sql.append(" AND RcdMaintainSts = '' ");
			}
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, id);
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();
			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
			fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
			fm.setGraceBaseRate(rs.getString("GraceBaseRate"));
			fm.setGraceSpecialRate(rs.getString("GraceSpecialRate"));
			fm.setGrcPftRate(rs.getBigDecimal("GrcPftRate"));
			fm.setGrcPftFrq(rs.getString("GrcPftFrq"));
			fm.setNextGrcPftDate(rs.getDate("NextGrcPftDate"));
			fm.setAllowGrcPftRvw(rs.getBoolean("AllowGrcPftRvw"));
			fm.setGrcPftRvwFrq(rs.getString("GrcPftRvwFrq"));
			fm.setNextGrcPftRvwDate(rs.getDate("NextGrcPftRvwDate"));
			fm.setAllowGrcCpz(rs.getBoolean("AllowGrcCpz"));
			fm.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
			fm.setNextGrcCpzDate(rs.getDate("NextGrcCpzDate"));
			fm.setRepayBaseRate(rs.getString("RepayBaseRate"));
			fm.setRepaySpecialRate(rs.getString("RepaySpecialRate"));
			fm.setRepayProfitRate(rs.getBigDecimal("RepayProfitRate"));
			fm.setRepayFrq(rs.getString("RepayFrq"));
			fm.setNextRepayDate(rs.getDate("NextRepayDate"));
			fm.setRepayPftFrq(rs.getString("RepayPftFrq"));
			fm.setNextRepayPftDate(rs.getDate("NextRepayPftDate"));
			fm.setAllowRepayRvw(rs.getBoolean("AllowRepayRvw"));
			fm.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
			fm.setNextRepayRvwDate(rs.getDate("NextRepayRvwDate"));
			fm.setAllowRepayCpz(rs.getBoolean("AllowRepayCpz"));
			fm.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
			fm.setNextRepayCpzDate(rs.getDate("NextRepayCpzDate"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setCpzAtGraceEnd(rs.getBoolean("CpzAtGraceEnd"));
			fm.setGrcRateBasis(rs.getString("GrcRateBasis"));
			fm.setRepayRateBasis(rs.getString("RepayRateBasis"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
			fm.setFirstRepay(rs.getBigDecimal("FirstRepay"));
			fm.setLastRepay(rs.getBigDecimal("LastRepay"));
			fm.setScheduleMethod(rs.getString("ScheduleMethod"));
			fm.setDownPayment(rs.getBigDecimal("DownPayment"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinSourceID(rs.getString("FinSourceID"));
			fm.setRecalType(rs.getString("RecalType"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setLastRepayDate(rs.getDate("LastRepayDate"));
			fm.setLastRepayPftDate(rs.getDate("LastRepayPftDate"));
			fm.setLastRepayRvwDate(rs.getDate("LastRepayRvwDate"));
			fm.setLastRepayCpzDate(rs.getDate("LastRepayCpzDate"));
			fm.setAllowGrcRepay(rs.getBoolean("AllowGrcRepay"));
			fm.setGrcSchdMthd(rs.getString("GrcSchdMthd"));
			fm.setGrcMargin(rs.getBigDecimal("GrcMargin"));
			fm.setRepayMargin(rs.getBigDecimal("RepayMargin"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));
			fm.setFinRepayPftOnFrq(rs.getBoolean("FinRepayPftOnFrq"));
			fm.setGrcProfitDaysBasis(rs.getString("GrcProfitDaysBasis"));
			fm.setGrcMinRate(rs.getBigDecimal("GrcMinRate"));
			fm.setGrcMaxRate(rs.getBigDecimal("GrcMaxRate"));
			fm.setGrcMaxAmount(rs.getBigDecimal("GrcMaxAmount"));
			fm.setRpyMinRate(rs.getBigDecimal("RpyMinRate"));
			fm.setRpyMaxRate(rs.getBigDecimal("RpyMaxRate"));
			fm.setManualSchedule(rs.getBoolean("ManualSchedule"));
			fm.setCalRoundingMode(rs.getString("CalRoundingMode"));
			fm.setRvwRateApplFor(rs.getString("RvwRateApplFor"));
			fm.setSchCalOnRvw(rs.getString("SchCalOnRvw"));
			fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
			fm.setDroppingMethod(rs.getString("DroppingMethod"));
			fm.setRateChgAnyDay(rs.getBoolean("RateChgAnyDay"));
			fm.setPastduePftMargin(rs.getBigDecimal("PastduePftMargin"));
			fm.setFinRepayMethod(rs.getString("FinRepayMethod"));
			fm.setMigratedFinance(rs.getBoolean("MigratedFinance"));
			fm.setScheduleMaintained(rs.getBoolean("ScheduleMaintained"));
			fm.setScheduleRegenerated(rs.getBoolean("ScheduleRegenerated"));
			fm.setMandateID(JdbcUtil.getLong(rs.getObject("MandateID")));
			fm.setFinStatus(rs.getString("FinStatus"));
			fm.setFinStsReason(rs.getString("FinStsReason"));
			fm.setPromotionCode(rs.getString("PromotionCode"));
			fm.setFinCategory(rs.getString("FinCategory"));
			fm.setProductCategory(rs.getString("ProductCategory"));
			fm.setReAgeBucket(rs.getInt("ReAgeBucket"));
			fm.setTDSApplicable(rs.getBoolean("TDSApplicable"));
			fm.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
			fm.setPromotionSeqId(JdbcUtil.getLong(rs.getObject("PromotionSeqId")));
			fm.setSvAmount(rs.getBigDecimal("SvAmount"));
			fm.setCbAmount(rs.getBigDecimal("CbAmount"));
			fm.setTdsType(rs.getString("TdsType"));
			fm.setManualSchdType(rs.getString("ManualSchdType"));
			if (orgination) {
				fm.setLimitValid(rs.getBoolean("LimitValid"));
			}
			return fm;
		});
	}

	@Override
	public FinanceMain getFinanceBasicDetailByRef(long finID, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.MaturityDate, fm.ProfitDaysBasis");
		sql.append(", ft.RateChgAnyDay, fm.ProductCategory");

		if (isWIF) {
			sql.append(" From WIFFinanceMain fm");
		} else {
			sql.append(" From FinanceMain fm");
		}
		sql.append(" Inner Join RMTfinanceTypes ft On ft.FinType = fm.FinType ");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
				fm.setRateChgAnyDay(rs.getBoolean("RateChgAnyDay"));
				fm.setProductCategory(rs.getString("ProductCategory"));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateFinMandateId(Long mandateId, long finID, String type) {
		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(type);
		sql.append(" Set MandateId = ? Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), mandateId, finID);
	}

	@Override
	public long getMandateIdByRef(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select MandateId From FinanceMain");
		sql.append(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return Long.MIN_VALUE;
		}
	}

	@Override
	public int getFinanceCountById(long finID) {
		String sql = "Select count(FinID) From FinanceMain Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID);
	}

	@Override
	public boolean isAppNoExists(String applicationNo, TableType tableType) {
		Object[] parameters = new Object[] { applicationNo, 1 };

		String sql = new String();
		String whereClause = " ApplicationNo = ? and FinIsActive = ?";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("FinanceMain", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("FinanceMain_Temp", whereClause);
			break;
		default:
			parameters = new Object[] { applicationNo, 1, applicationNo, 1 };
			sql = QueryUtil.getCountQuery(new String[] { "FinanceMain_Temp", "FinanceMain" }, whereClause);
			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, parameters) > 0;

	}

	@Override
	public String getApplicationNoById(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select ApplicationNo From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isFinTypeExistsInFinanceMain(String finType, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select count(*) FROM FINANCEMAIN");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where FinType = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finType) > 0;
	}

	@Override
	public List<FinanceMain> getFinancesByExpenseType(String finType, Date finApprovalStartDate,
			Date finApprovalEndDate) {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.FinAssetValue, fm.FinCurrAssetValue, fm.FinCcy");
		sql.append(", fm.FinBranch, fm.FinType, e.EntityCode");
		sql.append(" From FinanceMain fm, SMTDivisionDetail e");
		sql.append(" inner join  RMTFinanceTypes ft on e.DivisionCode = ft.FinDivision and ft.FinType= ?");
		sql.append(" WHERE  fm.FinType = ? And (ClosingStatus is null or ClosingStatus <> ?)");
		sql.append(" And fm.FinApprovedDate >= ? And fm.FinApprovedDate <= ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, finType);
			ps.setString(index++, finType);
			ps.setString(index++, "C");
			ps.setDate(index++, JdbcUtil.getDate(finApprovalStartDate));
			ps.setDate(index, JdbcUtil.getDate(finApprovalEndDate));
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinType(rs.getString("FinType"));
			fm.setEntityCode(rs.getString("EntityCode"));

			return fm;
		});

	}

	@Override
	public boolean isLoanPurposeExits(String loanPurposeCode, String type) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("finpurpose", loanPurposeCode);

		StringBuilder sql = new StringBuilder();
		sql.append("Select count(FinID) From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where Finpurpose = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, loanPurposeCode) > 0;
	}

	@Override
	public String getEarlyPayMethodsByFinRefernce(long finID) {
		StringBuilder sql = new StringBuilder("Select AlwEarlyPayMethods From RMTFinanceTypes");
		sql.append(" Where FinType = (Select FinType From FinanceMain Where FinID = ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<LoanPendingData> getCustomerODLoanDetails(long userID) {
		StringBuilder sql = new StringBuilder("SELECT fm.FinID, FinReference, FM.CustID, CM.CustCIF CustCIF");
		sql.append(", CM.CustShrtName CustShrtName, CD.CustDocTitle PANNumber, CP.PhoneNumber, FM.RoleCode");
		sql.append(" From FinanceMain_Temp FM");
		sql.append(" left JOIN Customers CM ON CM.CustID = FM.CUSTID");
		sql.append(" left join customerdocuments CD ON CD.CustID = CM.CUSTID AND CUSTDOCCATEGORY='PPAN'");
		sql.append(" left join CustomerPhonenumbers CP ON CP.PhoneCustID = CM.CUSTID AND PhoneTypeCode='MOBILE'");
		sql.append(" Where InitiateUser = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, userID);
		}, (rs, rowNum) -> {
			LoanPendingData pd = new LoanPendingData();

			pd.setFinID(rs.getLong("FinID"));
			pd.setFinReference(rs.getString("FinReference"));
			pd.setCustID(rs.getLong("CustID"));
			pd.setCustCIF(rs.getString("CustCIF"));
			pd.setCustShrtName(rs.getString("CustShrtName"));
			pd.setpANNumber(rs.getString("PANNumber"));
			pd.setPhoneNumber(rs.getString("PhoneNumber"));
			pd.setCurrentRole(rs.getString("RoleCode"));

			return pd;
		});

	}

	public int getActiveCount(String finType, long custID) {
		String sql = "Select count(FinID) From FinanceMain Where FinType = ? And CUSTID = ? And FinIsActive = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finType, custID, 1);
	}

	@Override
	public int getODLoanCount(String finType, long custID) {
		String sql = "Select count(FinID) From FinanceMain Where FinType = ? AND CUSTID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finType, custID);
	}

	@Override
	public List<FinanceMain> getUnApprovedFinances() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinType, AutoRejectionDays, FinID, FinReference, FinStartDate");
		sql.append(" From FinanceMain_Temp fm");
		sql.append(" Inner join RMTFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" where ft.AutoRejectionDays > ? and fm.RecordType = ? and fm.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, 0);
			ps.setString(index++, "NEW");
			ps.setBoolean(index, true);

		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinType(rs.getString("FinType"));
			fm.setAutoRejectionDays(rs.getInt("AutoRejectionDays"));
			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));

			return fm;
		});

	}

	@Override
	public void updateNextUserId(long finID, String nextUserId) {
		String sql = "Update FinanceMain_Temp Set NextUserId = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, nextUserId, finID);
	}

	@Override
	public String getNextUserId(long finID) {
		String sql = "Select NextUserId From FinanceMain_Temp Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, String.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public FinanceMain getEntityNEntityDesc(long finID, String type, boolean wif) {
		StringBuilder sql = new StringBuilder("Select e.EntityCode, e.EntityDesc");
		if (wif) {
			sql.append(" From WifFinanceMain");
		} else {
			sql.append(" From FinanceMain");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" fm");

		sql.append(" Inner Join RMTFinanceTypes ft On ft.FinType = fm.FinType");
		sql.append(" Inner Join SMTDivisionDetail dd On dd.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e On e.EntityCode = dd.EntityCode");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setEntityCode(rs.getString("EntityCode"));
				fm.setEntityDesc(rs.getString("EntityDesc"));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getClosingStatus(long finID, TableType tempTab, boolean wif) {
		StringBuilder sql = new StringBuilder("Select ClosingStatus, WriteoffLoan");
		if (wif) {
			sql.append(" From WifFinanceMain");
		} else {
			sql.append(" From FinanceMain");
		}
		sql.append(StringUtils.trimToEmpty(tempTab.getSuffix()));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setClosingStatus(rs.getString("ClosingStatus"));
				fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Date getClosedDateByFinRef(long finID) {
		String sql = "Select ClosedDate From FinanceMain Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Date.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Long getFinID(String finReference, String entity, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select FinID From FinanceMain");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" fm");
		sql.append(" Inner Join RMTFinanceTypes ft On ft.FinType = fm.FinType");
		sql.append(" Inner Join SMTDivisionDetail dd On dd.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e On e.EntityCode = dd.EntityCode");
		sql.append(" Where fm.FinReference = ? and e.EntityCode = ?");

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql.toString(), Long.class, finReference, entity);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDeveloperFinance(long finID, String type, boolean wif) {
		StringBuilder sql = new StringBuilder("Select ft.DEVELOPERFINANCE");
		if (wif) {
			sql.append(" From WifFinanceMain fm");
		} else {
			sql.append(" From FinanceMain fm");
		}
		sql.append(" Inner Join RmTFinanceTypes ft On ft.FinType = fm.FinType");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Boolean.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	// FIXME to custom RowMapper
	@Override
	public FinanceMain getFinanceDetailsByFinRefence(String finReference) {
		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Select ");
		sql.append(" custId, FinReference, LastMntOn, FinStartDate,");
		sql.append(" RecordStatus, FinAmount, FinAssetValue, finIsActive");
		sql.append(" From FinanceMain_view");
		sql.append(" Where FinReference = :FinReference");
		logger.debug(Literal.SQL + sql.toString());

		source.addValue("FinReference", finReference);

		RowMapper<FinanceMain> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getFinanceType(long finID, TableType tabeType) {
		StringBuilder sql = new StringBuilder("Select FinType");
		sql.append(" From FinanceMain");
		sql.append(tabeType.getSuffix());
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getFinanceType(String finReference, TableType tabeType) {
		StringBuilder sql = new StringBuilder("Select FinType");
		sql.append(" From FinanceMain");
		sql.append(tabeType.getSuffix());
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateFinAssetValue(FinanceMain fm) {
		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(" Set LastMntBy = ?, LastMntOn = ?, FinAssetValue = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fm.getLastMntBy());
			ps.setTimestamp(index++, fm.getLastMntOn());
			ps.setBigDecimal(index++, fm.getFinAssetValue());
			ps.setLong(index, fm.getFinID());
		});
	}

	@Override
	public FinanceMain getFinanceForAssignments(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinStartDate, MaturityDate, FinCurrAssetValue, FinAssetValue, AlwFlexi");
		sql.append(", FinType, FinIsActive, EntityCode, FinCcy, CustID, FinBranch, PromotionCode, PromotionSeqId");
		sql.append(", SvAmount, CbAmount, AssignmentId");
		sql.append(" From FinanceMain");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
				fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
				fm.setAlwFlexi(rs.getBoolean("AlwFlexi"));
				fm.setFinType(rs.getString("FinType"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setEntityCode(rs.getString("EntityCode"));
				fm.setFinCcy(rs.getString("FinCcy"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setPromotionCode(rs.getString("PromotionCode"));
				fm.setPromotionSeqId(JdbcUtil.getLong(rs.getObject("PromotionSeqId")));
				fm.setSvAmount(rs.getBigDecimal("SvAmount"));
				fm.setCbAmount(rs.getBigDecimal("CbAmount"));
				fm.setAssignmentId(JdbcUtil.getLong(rs.getObject("AssignmentId")));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	// FIXME Remove order by clause and handle in code.
	@Override
	public List<FinanceMain> getFinListForIncomeAMZ(Date curMonthStart) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinStartDate, FinApprovedDate, ClosingStatus, FinIsActive, ClosedDate");
		sql.append(", WriteoffLoan From FinanceMain");
		sql.append(" Where MaturityDate >= ? Order by FinID");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setDate(index, JdbcUtil.getDate(curMonthStart));
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinApprovedDate(rs.getDate("FinApprovedDate"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setClosedDate(rs.getDate("ClosedDate"));
			fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

			return fm;
		});
	}

	@Override
	public void updateAssignmentId(long finID, long assignmentId) {
		String sql = "Update FinanceMain Set AssignmentId = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, assignmentId);
			ps.setLong(2, finID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	private Map<String, Object> getGLSubHeadCodes(long finID, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinReference, fm.FinBranch, bv.Code, dd.EntityCode");
		sql.append(", c.SubCategory, pc.PCCityName, ft.FinCollateralReq, ft.FinDivision");
		sql.append(" From FinanceMain");
		sql.append(tableType.getSuffix());
		sql.append(" fm");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Inner Join RMTBranches br on br.BranchCode = fm.FinBranch");
		sql.append(" Inner Join RMTProvincevsCity pc on pc.PcCity = br.BranchCity");
		sql.append(" Left Join Business_Vertical bv on bv.ID = fm.BusinessVertical");
		sql.append(" Inner Join SMTDivisionDetail dd on dd.DivisionCode = ft.FinDivision");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		final Map<String, Object> map = new HashMap<>();

		return this.jdbcOperations.query(sql.toString(), (ResultSet rs) -> {
			while (rs.next()) {
				map.put("FINREFERENCE", rs.getString("FinReference"));
				map.put("FINBRANCH", rs.getString("FinBranch"));
				map.put("ENTITYCODE", rs.getString("EntityCode"));
				map.put("BUSINESSVERTICAL", rs.getString("Code"));
				map.put("EMPTYPE", rs.getString("SubCategory"));
				map.put("BRANCHCITY", rs.getString("PCCityName"));
				map.put("FINCOLLATERALREQ", rs.getBoolean("FinCollateralReq"));
				map.put("FINDIVISION", rs.getString("FinDivision"));
				map.put("ALWFLEXI", false);
				map.put("BTLOAN", null);
			}
			return map;
		}, finID);
	}

	@Override
	public Map<String, Object> getGLSubHeadCodes(long finID) {
		Map<String, Object> glSubHeadCodes = getGLSubHeadCodes(finID, TableType.MAIN_TAB);

		if (MapUtils.isEmpty(glSubHeadCodes)) {
			glSubHeadCodes = getGLSubHeadCodes(finID, TableType.TEMP_TAB);
		}

		return glSubHeadCodes;
	}

	@Override
	public int getCountByBlockedFinances(long finID) {
		String sql = "Select count(FinID) From BlockedFinances Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID);
	}

	@Override
	public void updateFromReceipt(FinanceMain fm, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FinanceMain set");
		sql.append(" StepFinance = ?,  AccountsOfficer = ?, PlanEMIHMax = ?");
		sql.append(", RepayCpzFrq = ?, WorkflowId = ?, UnPlanEMIHLockPeriod = ?");
		sql.append(", NextRoleCode = ?, GrcPftRate = ?, AllowGrcRepay = ?, GrcCpzFrq = ?");
		sql.append(", LastRepayPftDate = ?, NextGrcPftDate = ?, RecalType = ?, AllowedDefRpyChange = ?");
		sql.append(", MinDownPayPerc = ?, CustDSR = ?, RvwRateApplFor = ?, RateChgAnyDay = ?");
		sql.append(", RcdMaintainSts = ?, MaxReAgeHolidays = ?");
		sql.append(", CustID = ?, CalTerms = ?, FinAmount = ?, UnPlanEMICpz = ?");
		sql.append(", PlanDeferCount = ?, PlanEMIHAlw = ?, PlanEMIHAlwInGrace = ?, CalMaturity = ?");
		sql.append(", DsaCode = ?, Defferments = ?, BpiAmount = ?, ApplicationNo = ?, LegalRequired = ?");
		sql.append(", RoleCode = ?, FinPurpose = ?, FinCurrAssetValue = ?, FinStsReason = ?, NextTaskId = ?");
		sql.append(", GrcRateBasis = ?, LastRepayRvwDate = ?, RecordStatus = ?, TotalGraceCpz = ?");
		sql.append(", RpyMaxRate = ?, RpyMinRate = ?, AlwMultiDisb = ?, NextRepayCpzDate = ?, FinRepaymentAmount = ?");
		sql.append(", DmaCode = ?, ReqRepayAmount = ?, MaxUnplannedEmi = ?");
		sql.append(", ScheduleRegenerated = ?, FixedRateTenor = ?, GrcProfitDaysBasis = ?");
		sql.append(", DownPayBank = ?, DroplineFrq = ?, FinStatus = ?, NumberOfTerms = ?");
		sql.append(", OverrideLimit = ?, Version = ?, ScheduleMethod = ?, FirstRepay = ?");
		sql.append(", RepayProfitRate = ?, FinIsActive = ?, GrcPeriodEndDate = ?");
		sql.append(", RepayRvwFrq = ?, FinLimitRef = ?, ProductCategory = ?");
		sql.append(", FinRepayPftOnFrq = ?, EmployeeName = ?, TDSApplicable = ?");
		sql.append(", Connector = ?, AvailedDefRpyChange = ?, ScheduleMaintained = ?, LastRepayDate = ?");
		sql.append(", LastRepay = ?, ReqMaturity = ?, PastduePftMargin = ?, RepayFrq = ?, TotalCpz = ?");
		sql.append(", TotalGrossPft = ?, FinRepayMethod = ?, AllowRepayRvw = ?");
		sql.append(", TotalProfit = ?, AvailedUnPlanEmi = ?, FinCategory = ?, GrcPftRvwFrq = ?, FinCommitmentRef = ?");
		sql.append(", TotalGrossGrcPft = ?, DownPaySupl = ?, AllowGrcPeriod = ?, SalesDepartment = ?");
		sql.append(", PlanEMICpz = ?, GrcMaxRate = ?, FirstDroplineDate = ?, MandateID = ?");
		sql.append(", AvailedDefFrqChange = ?, CpzAtGraceEnd = ?, StepPolicy = ?, AllowGrcCpz = ?");
		sql.append(", NextRepayPftDate = ?, AllowGrcPftRvw = ?, NextGrcPftRvwDate = ?, FinContractDate = ?");
		sql.append(", InvestmentRef = ?, PromotionCode = ?, FinPreApprovedRef = ?, LimitValid = ?");
		sql.append(", NoOfSteps = ?, RoundingTarget = ?");
		sql.append(", LastRepayCpzDate = ?, NextGrcCpzDate = ?, PlanEMIHMethod = ?, AdvanceEMI = ?");
		sql.append(", AnualizedPercRate = ?, FinCcy = ?, TaskId = ?, BpiPftDaysBasis = ?, StepType = ?");
		sql.append(", WifReference = ?, InitiateDate = ?, FinApprovedDate = ?");
		sql.append(", BpiTreatment = ?, NextRepayDate = ?, DroppingMethod = ?, RepayBaseRate = ?");
		sql.append(", SchCalOnRvw = ?, AvailedReAgeH = ?, FeeChargeAmt = ?, MigratedFinance = ?");
		sql.append(", FixedTenorRate = ?, LastMntOn = ?, GrcMaxAmount = ?, PlanEMIHLockPeriod = ?");
		sql.append(", InitiateUser = ?, RepayPftFrq = ?, ProfitDaysBasis = ?, JointAccount = ?, FinBranch = ?");
		sql.append(", GrcPftFrq = ?, FinSourceID = ?, EligibilityMethod = ?, CalRoundingMode = ?");
		sql.append(", DeductFeeDisb = ?, FinType = ?, EffectiveRateOfReturn = ?, GraceTerms = ?");
		sql.append(", DownPayment = ?, FinAssetValue = ?, FinRemarks = ?");
		sql.append(", Priority = ?, AllowedDefFrqChange = ?, DeviationApproval = ?, ManualSchedule = ?");
		sql.append(", GraceBaseRate = ?, ReferralId = ?, SamplingRequired = ?, AlwBPI = ?, AlwManualSteps = ?");
		sql.append(", GrcMargin = ?, RepayMargin = ?, RepayRateBasis = ?, LinkedFinRef = ?, TotalRepayAmt = ?");
		sql.append(", JointCustId = ?, TotalGracePft = ?, RepaySpecialRate = ?");
		sql.append(", RecordType = ?, ReAgeCpz = ?, GraceSpecialRate = ?, AllowRepayCpz = ?");
		sql.append(", NextRepayRvwDate = ?, PlanEMIHMaxPerYear = ?, GrcSchdMthd = ?, LastMntBy = ?");
		sql.append(", NextUserId = ?, FinStartDate = ?, MaturityDate = ?, ClosingStatus = ?");
		sql.append(", PftServicingODLimit = ?, QuickDisb = ?, GrcMinRate = ?, PastduePftCalMthd = ?");
		sql.append(", ReAgeBucket = ?, TdsType = ?, WriteoffLoan = ?, DueBucket = ?");

		if (!fm.isFinIsActive()) {
			fm.setClosedDate(FinanceUtil.deriveClosedDate(fm));
			sql.append(", ClosedDate = ?");
		}

		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBoolean(index++, fm.isStepFinance());
			ps.setLong(index++, fm.getAccountsOfficer());
			ps.setInt(index++, fm.getPlanEMIHMax());
			ps.setString(index++, fm.getRepayCpzFrq());
			ps.setLong(index++, fm.getWorkflowId());
			ps.setInt(index++, fm.getUnPlanEMIHLockPeriod());
			ps.setString(index++, fm.getNextRoleCode());
			ps.setBigDecimal(index++, fm.getGrcPftRate());
			ps.setBoolean(index++, fm.isAllowGrcRepay());
			ps.setString(index++, fm.getGrcCpzFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getLastRepayPftDate()));
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextGrcPftDate()));
			ps.setString(index++, fm.getRecalType());
			ps.setInt(index++, fm.getAllowedDefRpyChange());
			ps.setBigDecimal(index++, fm.getMinDownPayPerc());
			ps.setBigDecimal(index++, fm.getCustDSR());
			ps.setString(index++, fm.getRvwRateApplFor());
			ps.setBoolean(index++, fm.isRateChgAnyDay());
			ps.setString(index++, fm.getRcdMaintainSts());
			ps.setInt(index++, fm.getMaxReAgeHolidays());
			ps.setLong(index++, fm.getCustID());
			ps.setInt(index++, fm.getCalTerms());
			ps.setBigDecimal(index++, fm.getFinAmount());
			ps.setBoolean(index++, fm.isUnPlanEMICpz());
			ps.setInt(index++, fm.getPlanDeferCount());
			ps.setBoolean(index++, fm.isPlanEMIHAlw());
			ps.setBoolean(index++, fm.isPlanEMIHAlwInGrace());
			ps.setDate(index++, JdbcUtil.getDate(fm.getCalMaturity()));
			ps.setString(index++, fm.getDsaCode());
			ps.setInt(index++, fm.getDefferments());
			ps.setBigDecimal(index++, fm.getBpiAmount());
			ps.setString(index++, fm.getApplicationNo());
			ps.setBoolean(index++, fm.isLegalRequired());
			ps.setString(index++, fm.getRoleCode());
			ps.setString(index++, fm.getFinPurpose());
			ps.setBigDecimal(index++, fm.getFinCurrAssetValue());
			ps.setString(index++, fm.getFinStsReason());
			ps.setString(index++, fm.getNextTaskId());
			ps.setString(index++, fm.getGrcRateBasis());
			ps.setDate(index++, JdbcUtil.getDate(fm.getLastRepayRvwDate()));
			ps.setString(index++, fm.getRecordStatus());
			ps.setBigDecimal(index++, fm.getTotalGraceCpz());
			ps.setBigDecimal(index++, fm.getRpyMaxRate());
			ps.setBigDecimal(index++, fm.getRpyMinRate());
			ps.setBoolean(index++, fm.isAlwMultiDisb());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayCpzDate()));
			ps.setBigDecimal(index++, fm.getFinRepaymentAmount());
			ps.setString(index++, fm.getDmaCode());
			ps.setBigDecimal(index++, fm.getReqRepayAmount());
			ps.setInt(index++, fm.getMaxUnplannedEmi());
			ps.setBoolean(index++, fm.isScheduleRegenerated());
			ps.setInt(index++, fm.getFixedRateTenor());
			ps.setString(index++, fm.getGrcProfitDaysBasis());
			ps.setBigDecimal(index++, fm.getDownPayBank());
			ps.setString(index++, fm.getDroplineFrq());
			ps.setString(index++, fm.getFinStatus());
			ps.setInt(index++, fm.getNumberOfTerms());
			ps.setBoolean(index++, fm.isOverrideLimit());
			ps.setInt(index++, fm.getVersion());
			ps.setString(index++, fm.getScheduleMethod());
			ps.setBigDecimal(index++, fm.getFirstRepay());
			ps.setBigDecimal(index++, fm.getRepayProfitRate());
			ps.setBoolean(index++, fm.isFinIsActive());
			ps.setDate(index++, JdbcUtil.getDate(fm.getGrcPeriodEndDate()));
			ps.setString(index++, fm.getRepayRvwFrq());
			ps.setString(index++, fm.getFinLimitRef());
			ps.setString(index++, fm.getProductCategory());
			ps.setBoolean(index++, fm.isFinRepayPftOnFrq());
			ps.setString(index++, fm.getEmployeeName());
			ps.setBoolean(index++, fm.isTDSApplicable());
			ps.setLong(index++, fm.getConnector());
			ps.setInt(index++, fm.getAvailedDefRpyChange());
			ps.setBoolean(index++, fm.isScheduleMaintained());
			ps.setDate(index++, JdbcUtil.getDate(fm.getLastRepayDate()));
			ps.setBigDecimal(index++, fm.getLastRepay());
			ps.setDate(index++, JdbcUtil.getDate(fm.getReqMaturity()));
			ps.setBigDecimal(index++, fm.getPastduePftMargin());
			ps.setString(index++, fm.getRepayFrq());
			ps.setBigDecimal(index++, fm.getTotalCpz());
			ps.setBigDecimal(index++, fm.getTotalGrossPft());
			ps.setString(index++, fm.getFinRepayMethod());
			ps.setBoolean(index++, fm.isAllowRepayRvw());
			ps.setBigDecimal(index++, fm.getTotalProfit());
			ps.setInt(index++, fm.getAvailedUnPlanEmi());
			ps.setString(index++, fm.getFinCategory());
			ps.setString(index++, fm.getGrcPftRvwFrq());
			ps.setString(index++, fm.getFinCommitmentRef());
			ps.setBigDecimal(index++, fm.getTotalGrossGrcPft());
			ps.setBigDecimal(index++, fm.getDownPaySupl());
			ps.setBoolean(index++, fm.isAllowGrcPeriod());
			ps.setString(index++, fm.getSalesDepartment());
			ps.setBoolean(index++, fm.isPlanEMICpz());
			ps.setBigDecimal(index++, fm.getGrcMaxRate());
			ps.setDate(index++, JdbcUtil.getDate(fm.getFirstDroplineDate()));
			ps.setObject(index++, fm.getMandateID());
			ps.setInt(index++, fm.getAvailedDefFrqChange());
			ps.setBoolean(index++, fm.isCpzAtGraceEnd());
			ps.setString(index++, fm.getStepPolicy());
			ps.setBoolean(index++, fm.isAllowGrcCpz());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayPftDate()));
			ps.setBoolean(index++, fm.isAllowGrcPftRvw());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextGrcPftRvwDate()));
			ps.setDate(index++, JdbcUtil.getDate(fm.getFinContractDate()));
			ps.setString(index++, fm.getInvestmentRef());
			ps.setString(index++, fm.getPromotionCode());
			ps.setString(index++, fm.getFinPreApprovedRef());
			ps.setBoolean(index++, fm.isLimitValid());
			ps.setInt(index++, fm.getNoOfSteps());
			ps.setInt(index++, fm.getRoundingTarget());
			ps.setDate(index++, JdbcUtil.getDate(fm.getLastRepayCpzDate()));
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextGrcCpzDate()));
			ps.setString(index++, fm.getPlanEMIHMethod());
			ps.setBigDecimal(index++, fm.getAdvanceEMI());
			ps.setBigDecimal(index++, fm.getAnualizedPercRate());
			ps.setString(index++, fm.getFinCcy());
			ps.setString(index++, fm.getTaskId());
			ps.setString(index++, fm.getBpiPftDaysBasis());
			ps.setString(index++, fm.getStepType());
			ps.setString(index++, fm.getWifReference());
			ps.setDate(index++, JdbcUtil.getDate(fm.getInitiateDate()));
			ps.setDate(index++, JdbcUtil.getDate(fm.getFinApprovedDate()));
			ps.setString(index++, fm.getBpiTreatment());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayDate()));
			ps.setString(index++, fm.getDroppingMethod());
			ps.setString(index++, fm.getRepayBaseRate());
			ps.setString(index++, fm.getSchCalOnRvw());
			ps.setInt(index++, fm.getAvailedReAgeH());
			ps.setBigDecimal(index++, fm.getFeeChargeAmt());
			ps.setBoolean(index++, fm.isMigratedFinance());
			ps.setBigDecimal(index++, fm.getFixedTenorRate());
			ps.setTimestamp(index++, fm.getLastMntOn());
			ps.setBigDecimal(index++, fm.getGrcMaxAmount());
			ps.setInt(index++, fm.getPlanEMIHLockPeriod());
			ps.setLong(index++, fm.getInitiateUser());
			ps.setString(index++, fm.getRepayPftFrq());
			ps.setString(index++, fm.getProfitDaysBasis());
			ps.setBoolean(index++, fm.isJointAccount());
			ps.setString(index++, fm.getFinBranch());
			ps.setString(index++, fm.getGrcPftFrq());
			ps.setString(index++, fm.getFinSourceID());
			ps.setLong(index++, fm.getEligibilityMethod());
			ps.setString(index++, fm.getCalRoundingMode());
			ps.setBigDecimal(index++, fm.getDeductFeeDisb());
			ps.setString(index++, fm.getFinType());
			ps.setBigDecimal(index++, fm.getEffectiveRateOfReturn());
			ps.setInt(index++, fm.getGraceTerms());
			ps.setBigDecimal(index++, fm.getDownPayment());
			ps.setBigDecimal(index++, fm.getFinAssetValue());
			ps.setString(index++, fm.getFinRemarks());
			ps.setInt(index++, fm.getPriority());
			ps.setInt(index++, fm.getAllowedDefFrqChange());
			ps.setBoolean(index++, fm.isDeviationApproval());
			ps.setBoolean(index++, fm.isManualSchedule());
			ps.setString(index++, fm.getGraceBaseRate());
			ps.setString(index++, fm.getReferralId());
			ps.setBoolean(index++, fm.isSamplingRequired());
			ps.setBoolean(index++, fm.isAlwBPI());
			ps.setBoolean(index++, fm.isAlwManualSteps());
			ps.setBigDecimal(index++, fm.getGrcMargin());
			ps.setBigDecimal(index++, fm.getRepayMargin());
			ps.setString(index++, fm.getRepayRateBasis());
			ps.setString(index++, fm.getLinkedFinRef());
			ps.setBigDecimal(index++, fm.getTotalRepayAmt());
			ps.setLong(index++, fm.getJointCustId());
			ps.setBigDecimal(index++, fm.getTotalGracePft());
			ps.setString(index++, fm.getRepaySpecialRate());
			ps.setString(index++, fm.getRecordType());
			ps.setBoolean(index++, fm.isReAgeCpz());
			ps.setString(index++, fm.getGraceSpecialRate());
			ps.setBoolean(index++, fm.isAllowRepayCpz());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayRvwDate()));
			ps.setInt(index++, fm.getPlanEMIHMaxPerYear());
			ps.setString(index++, fm.getGrcSchdMthd());
			ps.setLong(index++, fm.getLastMntBy());
			ps.setString(index++, fm.getNextUserId());
			ps.setDate(index++, JdbcUtil.getDate(fm.getFinStartDate()));
			ps.setDate(index++, JdbcUtil.getDate(fm.getMaturityDate()));
			ps.setString(index++, fm.getClosingStatus());
			ps.setBoolean(index++, fm.isPftServicingODLimit());
			ps.setBoolean(index++, fm.isQuickDisb());
			ps.setBigDecimal(index++, fm.getGrcMinRate());
			ps.setString(index++, fm.getPastduePftCalMthd());
			ps.setInt(index++, fm.getReAgeBucket());
			ps.setString(index++, fm.getTdsType());
			ps.setBoolean(index++, fm.isWriteoffLoan());
			ps.setInt(index++, fm.getDueBucket());

			if (!fm.isFinIsActive()) {
				ps.setDate(index++, JdbcUtil.getDate(fm.getClosedDate()));
			}

			ps.setLong(index, fm.getFinID());

		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinanceMain isFlexiLoan(long finID) {
		String sql = "Select FinID, FinReference, FinType, AlwFlexi, FlexiType From FinanceMain Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();
				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinType(rs.getString("FinType"));
				fm.setAlwFlexi(rs.getBoolean("AlwFlexi"));
				// fm.setFlexiType(rs.getString("FlexiType"));

				return fm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isFinReferenceExitsinLQ(long finID, TableType tempTab, boolean wif) {
		StringBuilder sql = new StringBuilder("Select count(FinID)");
		if (wif) {
			sql.append(" From WifFinanceMain");
		} else {
			sql.append(" From FinanceMain");
		}
		sql.append(StringUtils.trimToEmpty(tempTab.getSuffix()));
		sql.append(" Where FinID = ? and RcdMaintainSts is null");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID) > 0;
	}

	@Override
	public List<FinanceMain> getFinanceMainForLinkedLoans(long custId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Distinct FinID, FinReference, FinType, FinStartDate, FinStatus, FinCcy");
		sql.append(", FinCurrAssetValue, FeeChargeAmt, FinRepaymentAmount");
		sql.append(" From FinanceMain");
		sql.append(" Where CustId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinStatus(rs.getString("FinStatus"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));

			return fm;

		}, custId);
	}

	@Override
	public List<FinanceMain> getFinanceMainForLinkedLoans(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" distinct fm.FinID, fm.FinReference, fm.FinType, fm.FinStartDate, fm.FinStatus, fm.FinCcy");
		sql.append(", fm.FinCurrAssetValue, fm.FeeChargeAmt, fm.FinRepaymentAmount");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join CollateralAssignment ca On ca.Reference = fm.FinReference");
		sql.append(" Where ca.CollateralRef In (select CollateralRef From CollateralAssignment");
		sql.append(" Where Reference = ?)");
		sql.append(" Union");
		sql.append(" Select distinct fm.FinID, fm.FinReference, fm.FinType, fm.FinStartDate, fm.FinStatus, fm.FinCcy");
		sql.append(", fm.FinCurrAssetValue, fm.FeeChargeAmt, fm.FinRepaymentAmount");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join LinkedFinances lf on lf.LinkedReference = fm.FinReference");
		sql.append(" Where lf.FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, finReference);
			ps.setString(2, finReference);
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinStatus(rs.getString("FinStatus"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));

			return fm;
		});
	}

	@Override
	public Map<String, Object> getGSTDataMap(long finID, TableType tableType) {
		Map<String, Object> map = new HashMap<>();

		StringBuilder sql = new StringBuilder();
		sql.append("select fm.FinID, fm.FinReference, fm.FinCCY, fm.FinBranch, cu.CustDftBranch CustBranch");
		sql.append(", ca.CustAddrProvince CustProvince, ca.CustAddrCountry CustCountry, cu.ResidentialStatus");
		sql.append(", cu.CustResidentialSts CustResidentialSts");

		if (TableType.MAIN_TAB == tableType) {
			sql.append(" From FinanceMain fm");
			sql.append(" inner join Customers cu on cu.CustId = fm.CustId");
			sql.append(" inner join CustomerAddresses ca on ca.CustId = cu.CustId");
		} else if (TableType.TEMP_TAB == tableType) {
			sql.append(" From FinanceMain_Temp fm");
			sql.append(" inner join Customers cu on cu.CustId = fm.CustId");
			sql.append(" inner join CustomerAddresses ca on ca.CustId = cu.CustId");
		} else if (TableType.VIEW == tableType) {
			sql.append(" From FinanceMain_View fm");
			sql.append(" inner join Customers_View cu on cu.CustId = fm.CustId");
			sql.append(" inner join CustomerAddresses_View ca on ca.CustId = cu.CustId");
		}

		sql.append(" and CustAddrPriority = ?");
		sql.append(" Where fm.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			map.put("FinID", rs.getLong("FinID"));
			map.put("FinReference", rs.getString("FinReference"));
			map.put("FinCCY", rs.getString("FinCCY"));
			map.put("FinBranch", rs.getString("FinBranch"));
			map.put("CustBranch", rs.getString("CustBranch"));
			map.put("CustProvince", rs.getString("CustProvince"));
			map.put("CustCountry", rs.getString("CustCountry"));
			map.put("ResidentialStatus", rs.getString("ResidentialStatus"));
			map.put("CustResidentialSts", rs.getString("CustResidentialSts"));
			return map;
		}, Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH), finID);

		return map;
	}

	@Override
	public Map<String, Object> getCustGSTDataMap(long custId, TableType tableType) {
		Map<String, Object> map = new HashMap<>();

		StringBuilder sql = new StringBuilder();
		sql.append("select cu.CustDftBranch CustBranch");
		sql.append(", ca.CustAddrProvince CustProvince, ca.CustAddrCountry CustCountry, cu.ResidentialStatus");
		sql.append(", cu.CustResidentialSts CustResidentialSts");

		if (TableType.MAIN_TAB == tableType) {
			sql.append(" From Customers cu");
			sql.append(" inner join CustomerAddresses ca on ca.CustId = cu.CustId");
		} else if (TableType.TEMP_TAB == tableType) {
			sql.append(" From Customers_Temp cu");
			sql.append(" inner join CustomerAddresses_Temp ca on ca.CustId = cu.CustId");
		} else if (TableType.VIEW == tableType) {
			sql.append(" From Customers_View cu");
			sql.append(" inner join CustomerAddresses_View ca on ca.CustId = cu.CustId");
		}

		sql.append(" Where cu.CustId = ? and custaddrpriority = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			map.put("CustBranch", rs.getString("CustBranch"));
			map.put("CustProvince", rs.getString("CustProvince"));
			map.put("CustCountry", rs.getString("CustCountry"));
			map.put("ResidentialStatus", rs.getString("ResidentialStatus"));
			map.put("CustResidentialSts", rs.getString("CustResidentialSts"));
			return map;
		}, custId, Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH));

		return map;
	}

	@Override
	public boolean isFinActive(long finID) {
		String sql = "Select FinisActive From FinanceMain Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Boolean.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public String getFinanceMainByRcdMaintenance(long finID) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select RcdMaintainSts From (");
		sql.append(" Select RcdMaintainSts From FinanceMain_Temp Where FinID = ?");
		sql.append(" Union All");
		sql.append(" Select RcdMaintainSts From FinanceMain Where FinID = ?");
		sql.append(" and not exists (Select 1 From FinanceMain_Temp Where FinID = FinanceMain.FinID)");
		sql.append(" ) fm");

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, finID, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getRcdMaintenanceByRef(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select RcdMaintainSts, MaturityDate, WriteoffLoan From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setRcdMaintainSts(rs.getString("RcdMaintainSts"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void deleteFinreference(FinanceMain fm, TableType tableType, boolean wifi, boolean finilize) {
		StringBuilder sql = new StringBuilder("Delete");
		if (wifi) {
			sql.append(" From WIFFinanceMain");
		} else {
			sql.append(" From FinanceMain");
		}
		sql.append(tableType.getSuffix());
		sql.append(" where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = 0;

		try {
			recordCount = jdbcOperations.update(sql.toString(), fm.getFinID());
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinanceMain getFinanceMainByOldFinReference(String oldFinReference, boolean active) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, PlanDeferCount, AllowedDefRpyChange");
		sql.append(", AvailedDefRpyChange, AllowedDefFrqChange, AvailedDefFrqChange, FinIsActive, PromotionCode");
		sql.append(", OldFinReference, FinType, FinCategory, FinBranch, CustID, PromotionCode");
		sql.append(" From FinanceMain");
		sql.append(" Where OldFinReference = ? AND FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setPlanDeferCount(rs.getInt("PlanDeferCount"));
				fm.setAllowedDefRpyChange(rs.getInt("AllowedDefRpyChange"));
				fm.setAvailedDefRpyChange(rs.getInt("AvailedDefRpyChange"));
				fm.setAllowedDefFrqChange(rs.getInt("AllowedDefFrqChange"));
				fm.setAvailedDefFrqChange(rs.getInt("AvailedDefFrqChange"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setPromotionCode(rs.getString("PromotionCode"));
				fm.setOldFinReference(rs.getString("OldFinReference"));
				fm.setFinType(rs.getString("FinType"));
				fm.setFinCategory(rs.getString("FinCategory"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setPromotionCode(rs.getString("PromotionCode"));

				return fm;

			}, oldFinReference, active);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinanceMain> getFinancesByFinApprovedDate(Date finApprovalStartDate, Date finApprovalEndDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinType, FinCcy, CustID, FinBranch");
		sql.append(", FinStartDate, FinApprovedDate, MaturityDate, FinAssetValue, FinCurrAssetValue, FinAmount");
		sql.append(", FinCategory, ProductCategory, FinStatus");
		sql.append(", CalRoundingMode, RoundingTarget, ProfitDaysBasis");
		sql.append(", ClosingStatus, FinIsActive, EntityCode, WriteoffLoan");
		sql.append(" From FinanceMain");
		sql.append(" WHERE FinApprovedDate >= ? And FinApprovedDate <= ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setDate(1, JdbcUtil.getDate(finApprovalStartDate));
			ps.setDate(2, JdbcUtil.getDate(finApprovalEndDate));
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinApprovedDate(rs.getDate("FinApprovedDate"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setFinCategory(rs.getString("FinCategory"));
			fm.setProductCategory(rs.getString("ProductCategory"));
			fm.setFinStatus(rs.getString("FinStatus"));
			fm.setCalRoundingMode(rs.getString("CalRoundingMode"));
			fm.setRoundingTarget(rs.getInt("RoundingTarget"));
			fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setEntityCode(rs.getString("EntityCode"));
			fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

			return fm;
		});
	}

	@Override
	public FinanceMain getFinanceForIncomeAMZ(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinType, CustID, ClosingStatus, FinIsActive, MaturityDate, ClosedDate");
		sql.append(", WriteoffLoan From FinanceMain");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinType(rs.getString("FinType"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setClosingStatus(rs.getString("ClosingStatus"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setClosedDate(rs.getDate("ClosedDate"));
				fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinanceMain> getFinListForAMZ(Date monthEndDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinType, FinCcy, CustID, FinBranch, ");
		sql.append(" FinStartDate, FinApprovedDate, MaturityDate, FinAssetValue, FinCurrAssetValue, FinAmount, ");
		sql.append(" FinCategory, ProductCategory, FinStatus, ");
		sql.append(" CalRoundingMode, RoundingTarget, ProfitDaysBasis, ");
		sql.append(" ClosingStatus, FinIsActive, EntityCode, ClosedDate, WriteoffLoan");
		sql.append(" From FinanceMain");
		sql.append(" Where MaturityDate >= ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setDate(1, JdbcUtil.getDate(DateUtil.getMonthStart(monthEndDate)));
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinApprovedDate(rs.getDate("FinApprovedDate"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setFinCategory(rs.getString("FinCategory"));
			fm.setProductCategory(rs.getString("ProductCategory"));
			fm.setFinStatus(rs.getString("FinStatus"));
			fm.setCalRoundingMode(rs.getString("CalRoundingMode"));
			fm.setRoundingTarget(rs.getInt("RoundingTarget"));
			fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setEntityCode(rs.getString("EntityCode"));
			fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));

			return fm;
		});
	}

	@Override
	public int getCountByFinReference(long finID, boolean active) {
		String sql = "Select count(FinID) From FinanceMain Where FinID = ? And FinIsActive = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID, active);
	}

	@Override
	public int getCountByOldFinReference(String hostReference) {
		String sql = "Select Count(FinID) From Financemain_Extension Where Hostreference = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, hostReference);
	}

	@Override
	public long getLoanWorkFlowIdByFinRef(String finReference, String type) {
		StringBuilder sql = new StringBuilder("Select WorkflowId");
		sql.append(" From FinanceMain");
		sql.append(type);
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public String getLovDescEntityCode(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select LovDescEntityCode From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void saveHostRef(FinanceMainExtension fm) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinanceMain_Extension");
		sql.append(" (FinId, Finreference, Hostreference, OldHostReference)");
		sql.append(" values (?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fm.getFinId());
			ps.setString(index++, fm.getFinreference());
			ps.setString(index++, fm.getHostreference());
			ps.setString(index, fm.getOldhostreference());
		});
	}

	@Override
	public FinanceMain getFinanceMainByHostReference(String oldFinReference, boolean active) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, PlanDeferCount, SvAmount, CbAmount, AllowedDefRpyChange");
		sql.append(", AvailedDefRpyChange, AllowedDefFrqChange, FinAmount, DownPayment, FinStartDate");
		sql.append(", PromotionSeqId, AvailedDefFrqChange, FinIsActive, PromotionCode, OldFinReference");
		sql.append(", FinCategory, FinType, FinBranch, MandateID, CustID");
		sql.append(" From FinanceMain");
		sql.append(" Where FinID = (Select FinID From Financemain_Extension Where HostReference = ?)");
		sql.append(" and FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setPlanDeferCount(rs.getInt("PlanDeferCount"));
				fm.setSvAmount(rs.getBigDecimal("SvAmount"));
				fm.setCbAmount(rs.getBigDecimal("CbAmount"));
				fm.setAllowedDefRpyChange(rs.getInt("AllowedDefRpyChange"));
				fm.setAvailedDefRpyChange(rs.getInt("AvailedDefRpyChange"));
				fm.setAllowedDefFrqChange(rs.getInt("AllowedDefFrqChange"));
				fm.setFinAmount(rs.getBigDecimal("FinAmount"));
				fm.setDownPayment(rs.getBigDecimal("DownPayment"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setPromotionSeqId(JdbcUtil.getLong(rs.getObject("PromotionSeqId")));
				fm.setAvailedDefFrqChange(rs.getInt("AvailedDefFrqChange"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setPromotionCode(rs.getString("PromotionCode"));
				fm.setOldFinReference(rs.getString("OldFinReference"));
				fm.setFinCategory(rs.getString("FinCategory"));
				fm.setFinType(rs.getString("FinType"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setMandateID(JdbcUtil.getLong(rs.getObject("MandateID")));
				fm.setCustID(JdbcUtil.getLong(rs.getObject("CustID")));

				return fm;

			}, oldFinReference, active);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getCountByExternalReference(String oldHostReference) {
		String sql = "Select count(ID) From Financemain_Extension Where OldHostreference = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, oldHostReference);
	}

	@Override
	public int getCountByOldHostReference(String oldHostReference) {
		StringBuilder sql = new StringBuilder("Select Count(FinID)");
		sql.append(" From Financemain Where FinID = (Select FinID From FinanceMain_Extension");
		sql.append(" Where OldHostReference = ?)  AND FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, oldHostReference, 0);
	}

	@Override
	public void updateRejectFinanceMain(FinanceMain fm, TableType tableType, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Update Financemain");
		sql.append(tableType.getSuffix());
		sql.append(" Set Approved = ?, ProcessAttributes = ?, FinIsActive = ?, NextTaskId = ?");
		sql.append(", RecordStatus = ?, NextRoleCode = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fm.getApproved());
			ps.setString(index++, fm.getProcessAttributes());
			ps.setBoolean(index++, fm.isFinIsActive());
			ps.setString(index++, fm.getNextTaskId());
			ps.setString(index++, fm.getRecordStatus());
			ps.setString(index++, fm.getNextRoleCode());
			ps.setLong(index, fm.getFinID());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinanceMain getUserActions(String finReference) {
		String sql = "Select FinType, NextRoleCode From FinanceMain_Temp Where FinReference = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinType(rs.getString("FinType"));
				fm.setNextRoleCode(rs.getString("NextRoleCode"));

				return fm;

			}, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinanceDetailsForInsurance(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinType, Finccy");
		sql.append(" From FinanceMain");

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinType(rs.getString("FinType"));
				fm.setFinCcy(rs.getString("Finccy"));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	// FIXME to custom RowMapper
	@Override
	public List<FinanceMain> getFinMainListBySQLQueryRule(String whereClause, String type) {
		StringBuilder sql = new StringBuilder("Select FinID, FinReference,GraceTerms, NumberOfTerms");
		sql.append(", GrcPeriodEndDate, GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate");
		sql.append(", AllowGrcPftRvw,GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate");
		sql.append(", RepayBaseRate,RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq");
		sql.append(", AllowRepayRvw,RepayRvwFrq,NextRepayRvwDate,AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate");
		sql.append(", MaturityDate, CpzAtGraceEnd,DownPayment, ReqRepayAmount, TotalProfit, DownPayBank");
		sql.append(", TotalCpz,TotalGrossPft,TotalGracePft,TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt");
		sql.append(", NextRepayPftDate,GrcRateBasis,RepayRateBasis, FinType,FinRemarks, FinCcy, ScheduleMethod");
		sql.append(", ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,DownPaySupl");
		sql.append(", FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments, PlanDeferCount");
		sql.append(", FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange");
		sql.append(", AvailedDefFrqChange, RecalType, FinAssetValue, FinIsActive");
		sql.append(", LastRepayDate, LastRepayPftDate,LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay");
		sql.append(", GrcMargin, RepayMargin, FinCommitmentRef, FinCurrAssetValue");
		sql.append(", ClosingStatus, FinApprovedDate");
		sql.append(", AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq , GrcProfitDaysBasis");
		sql.append(", LinkedFinRef, GrcMinRate, GrcMaxRate , RpyMinRate, RpyMaxRate,GrcSchdMthd, StepPolicy");
		sql.append(", ManualSchedule");
		sql.append(", MinDownPayPerc,TDSApplicable, FeeChargeAmt");
		sql.append(", PlanEMIHMethod, PlanEMIHMaxPerYear, PlanEMIHMax, PlanEMIHLockPeriod , PlanEMICpz");
		sql.append(", DeductFeeDisb,RvwRateApplFor, SchCalOnRvw,PastduePftCalMthd,DroppingMethod,RateChgAnyDay");
		sql.append(", InvestmentRef, RcdMaintainSts,FinRepayMethod");
		sql.append(", MigratedFinance,ScheduleMaintained,ScheduleRegenerated,CustDSR,JointAccount,JointCustId");
		sql.append(", Blacklisted,OverrideLimit,FinPurpose,FinStatus,FinStsReason,InitiateUser");
		sql.append(", NextUserId, Priority, AlwManualSteps");
		sql.append(", InitiateDate,AccountsOfficer,DsaCode");
		sql.append(", ReferralId, DmaCode, SalesDepartment, QuickDisb, WifReference, UnPlanEMIHLockPeriod");
		sql.append(", MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, PromotionCode, ApplicationNo, AlwBPI");
		sql.append(", CalRoundingMode , AlwMultiDisb, BpiAmount, PastduePftMargin,FinCategory,ProductCategory");
		sql.append(", DeviationApproval,FinPreApprovedRef,MandateID,FirstDroplineDate,PftServicingODLimit");
		sql.append(", UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi,BpiTreatment, PlanEMIHAlw, PlanEMIHAlwInGrace");
		sql.append(", StepType, DroplineFrq, NoOfSteps");
		sql.append(", StepFinance, FinContractDate, TdsType, WriteoffLoan");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescFinTypeName, lovDescFinBranchName");
			sql.append(", lovDescAccruedTillLBD, lovDescFinScheduleOn, CostOfFunds, ARFSuspendSubvention");
			sql.append(", SubventionTillMonths, AllowSuspendSubvention, SuspendBucket,SubventionApplicable");
			sql.append(", LovDescStepPolicyName,CustStsDescription, lovDescAccountsOfficer,DsaCodeDesc");
			sql.append(", ReAgeBucket, FinLimitRef, ReferralIdDesc, DmaCodeDesc, SalesDepartmentDesc");
			sql.append(", CustNationality, CustParentCountry, CustGenderCode, CustIsStaff, CustAddrProvince");
			sql.append(", FinDivision, CustIndustry, CustCtgCode, CustDftBranch, CustEmpSts, CustMaritalSts");
			sql.append(", CustRiskCountry, CustSector,CustSegment, CustSubSector, CustSubSegment, CustTypeCode");
		}

		if (type.equals("_LCFTView")) {
			sql.append(", 1 LimitValid");
		}

		sql.append(" From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" " + whereClause);
		sql.append(" AND FinIsActive = 1");

		if (type.equals("_LCFTView")) {

			if (App.DATABASE == Database.ORACLE) {
				sql.append(" AND RcdMaintainSts IS NULL ");
			} else {
				sql.append(" AND RcdMaintainSts = '' ");
			}
		}

		logger.debug(Literal.SQL + sql.toString());
		RowMapper<FinanceMain> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceMain.class);

		return this.jdbcTemplate.query(sql.toString(), new MapSqlParameterSource(), typeRowMapper);
	}

	@Override
	public FinanceMain getFinanceMainDetails(long finID) {
		StringBuilder sql = new StringBuilder();
		sql.append("select T1.FinID, T1.FinReference, T1.FinIsActive, T1.FinStartDate, T1.FinBranch");
		sql.append(", T36.EntityCode, T7.FinDivision");
		sql.append(" FROM FINANCEMAIN T1");
		sql.append(" JOIN RMTFINANCETYPES T7 ON T1.FINTYPE = T7.FINTYPE");
		sql.append(" LEFT JOIN RMTBRANCHES T12 ON T1.FINBRANCH = T12.BRANCHCODE");
		sql.append(" LEFT JOIN FINPFTDETAILS T17 ON T17.FinID = T1.FinID");
		sql.append(" LEFT JOIN STEPPOLICYHEADER T24 ON T1.STEPPOLICY = T24.POLICYCODE");
		sql.append(" LEFT JOIN BMTCUSTSTATUSCODES T27 ON T1.FINSTATUS = T27.CUSTSTSCODE");
		sql.append(" LEFT JOIN AMTVEHICLEDEALER T28 ON T1.ACCOUNTSOFFICER = T28.DEALERID");
		sql.append(" LEFT JOIN RELATIONSHIPOFFICERS T30 ON T1.DSACODE = T30.ROFFICERCODE");
		sql.append(" LEFT JOIN RELATIONSHIPOFFICERS T33 ON T1.REFERRALID = T33.ROFFICERCODE");
		sql.append(" LEFT JOIN RELATIONSHIPOFFICERS T34 ON T1.DMACODE = T34.ROFFICERCODE");
		sql.append(" LEFT JOIN RMTGENDEPARTMENTS T35 ON T1.SALESDEPARTMENT = T35.GENDEPARTMENT");
		sql.append(" LEFT JOIN SMTDIVISIONDETAIL T36 ON T7.FINDIVISION = T36.DIVISIONCODE");
		sql.append(" LEFT JOIN RELATIONSHIPOFFICERS T37 ON T1.EMPLOYEENAME = T37.ROFFICERCODE");
		sql.append(" LEFT JOIN RMTLOVFIELDDETAIL T38 ON T1.ELIGIBILITYMETHOD = T38.FIELDCODEID");
		sql.append(" AND T38.FIELDCODE = ?");
		sql.append(" LEFT JOIN LOANPURPOSES T39 ON T1.FINPURPOSE = T39.LOANPURPOSECODE");
		sql.append(" LEFT JOIN AMTVEHICLEDEALER T40 ON T1.CONNECTOR = T40.DEALERID");
		sql.append(" LEFT JOIN BUSINESS_VERTICAL BV ON BV.ID = T1.BUSINESSVERTICAL");
		sql.append(" Where T1.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setEntityCode(rs.getString("EntityCode"));
				fm.setLovDescEntityCode(rs.getString("EntityCode"));
				fm.setLovDescFinDivision(rs.getString("FinDivision"));

				return fm;
			}, "ELGMETHOD", finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isFinExistsByPromotionSeqID(long referenceId) {
		String sql = "Select count(FinID) From FinanceMain_View Where PromotionSeqID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, referenceId) > 0;
	}

	@Override
	public boolean isRepayFrqExists(String brType) {
		String sql = "Select count(FinID) From FinanceMain_View Where RepayBaseRate = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, brType) > 0;
	}

	@Override
	public boolean isGrcRepayFrqExists(String brType) {
		String sql = "Select count(FinID) From FinanceMain_View Where GrcPftRvwFrq = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, brType) > 0;
	}

	@Override
	public Date getFinStartDate(long finID) {
		String sql = "Select FinStartdate From FinanceMain Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Date.class, finID);
	}

	// FIXME to custom RowMapper
	@Override
	public FinanceMain getFinanceMain(long finId, String[] columns, String type) {
		String field = String.join(", ", columns).substring(0);

		StringBuilder sql = new StringBuilder("select ");
		sql.append(field);
		sql.append(" From");
		sql.append(" FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = :FinID");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinID", finId);

		RowMapper<FinanceMain> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinanceEnquiry> getAllFinanceDetailsByCustId(long custId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinBranch, FinType, FinCcy, ScheduleMethod, ProfitDaysBasis");
		sql.append(", FinStartDate, NumberOfTerms, CustID, FinAmount, GrcPeriodEndDate, MaturityDate");
		sql.append(", FinRepaymentAmount, FinIsActive, AllowGrcPeriod, LovDescFinTypeName, LovDescCustCIF");
		sql.append(", LovDescCustShrtName, LovDescFinBranchName, Blacklisted, LovDescFinScheduleOn");
		sql.append(", FeeChargeAmt, ClosingStatus, CustTypeCtg, GraceTerms, lovDescFinDivision");
		sql.append(", LovDescProductCodeName, Defferments, FinRepayMethod, MandateID");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(" From FinanceEnquiry_View");
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, custId);
		}, (rs, rowNum) -> {
			FinanceEnquiry fm = new FinanceEnquiry();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setScheduleMethod(rs.getString("ScheduleMethod"));
			fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
			fm.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
			fm.setLovDescCustCIF(rs.getString("LovDescCustCIF"));
			fm.setLovDescCustShrtName(rs.getString("LovDescCustShrtName"));
			fm.setLovDescFinBranchName(rs.getString("LovDescFinBranchName"));
			fm.setBlacklisted(rs.getBoolean("Blacklisted"));
			fm.setLovDescFinScheduleOn(rs.getString("LovDescFinScheduleOn"));
			fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));
			fm.setCustTypeCtg(rs.getString("CustTypeCtg"));
			fm.setGraceTerms(rs.getInt("GraceTerms"));
			fm.setLovDescFinDivision(rs.getString("lovDescFinDivision"));
			fm.setLovDescProductCodeName(rs.getString("lovDescProductCodeName"));
			fm.setDefferments(rs.getInt("Defferments"));
			fm.setFinRepayMethod(rs.getString("FinRepayMethod"));
			fm.setMandateID(rs.getLong("MandateID"));
			fm.setCreatedOn(rs.getTimestamp("CreatedBy"));
			fm.setCreatedBy(JdbcUtil.getLong(rs.getObject("CreatedOn")));
			fm.setApprovedOn(rs.getTimestamp("ApprovedBy"));
			fm.setApprovedBy(JdbcUtil.getLong(rs.getObject("ApprovedOn")));

			return fm;
		});

	}

	private StringBuilder getFinSelectQueryForEod() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.GrcPeriodEndDate, fm.AllowGrcPeriod, fm.GraceBaseRate");
		sql.append(", fm.GraceSpecialRate, fm.GrcPftRate, fm.GrcPftFrq, fm.NextGrcPftDate, fm.AllowGrcPftRvw");
		sql.append(", fm.GrcPftRvwFrq, fm.NextGrcPftRvwDate, fm.AllowGrcCpz, fm.GrcCpzFrq, fm.NextGrcCpzDate");
		sql.append(", fm.RepayBaseRate, fm.RepaySpecialRate, fm.RepayProfitRate, fm.RepayFrq, fm.NextRepayDate");
		sql.append(", fm.RepayPftFrq, fm.NextRepayPftDate, fm.AllowRepayRvw, fm.RepayRvwFrq, fm.RepayRvwFrq");
		sql.append(", fm.NextRepayRvwDate, fm.AllowRepayCpz, fm.RepayCpzFrq, fm.NextRepayCpzDate, fm.MaturityDate");
		sql.append(", fm.CpzAtGraceEnd, fm.GrcRateBasis, fm.RepayRateBasis, fm.FinType, fm.FinCcy, fm.ProfitDaysBasis");
		sql.append(", fm.FirstRepay, fm.LastRepay, fm.ScheduleMethod, fm.FinStartDate, fm.FinAmount, fm.CustID");
		sql.append(", fm.FinBranch, fm.FinSourceID, fm.RecalType, fm.FinIsActive, fm.LastRepayDate");
		sql.append(", fm.LastRepayPftDate, fm.LastRepayRvwDate, fm.LastRepayCpzDate, fm.AllowGrcRepay, fm.GrcSchdMthd");
		sql.append(", fm.GrcMargin, fm.RepayMargin, fm.ClosingStatus, fm.FinRepayPftOnFrq, fm.GrcProfitDaysBasis");
		sql.append(", fm.GrcMinRate, fm.GrcMaxRate, fm.GrcMaxAmount, fm.RpyMinRate, fm.RpyMaxRate, fm.ManualSchedule");
		sql.append(", fm.CalRoundingMode, fm.RoundingTarget, fm.RvwRateApplFor, fm.SchCalOnRvw, fm.PastduePftCalMthd");
		sql.append(", fm.DroppingMethod, fm.RateChgAnyDay, fm.PastduePftMargin, fm.FinRepayMethod, fm.MigratedFinance");
		sql.append(", fm.ScheduleMaintained, fm.ScheduleRegenerated, fm.MandateID, fm.FinStatus, fm.DueBucket");
		sql.append(", fm.FinStsReason");
		sql.append(", fm.PromotionCode, fm.FinCategory, fm.ProductCategory, fm.ReAgeBucket");
		sql.append(", fm.TDSApplicable, fm.TdsType");
		sql.append(", fm.BpiTreatment, fm.FinRepaymentAmount, fm.GrcAdvType, fm.AdvType, fm.SanBsdSchdle");
		sql.append(", fm.AutoIncGrcEndDate, fm.Version, fm.LastMntOn, fm.ReferralId, fm.GraceTerms, fm.AlwMultiDisb");
		sql.append(", fm.NumberOfTerms, fm.PromotionSeqId, fm.SvAmount, fm.CbAmount, fm.EmployeeName");
		sql.append(", fm.FinAssetValue, fm.FinCurrAssetValue, fm.AlwGrcAdj, fm.EndGrcPeriodAftrFullDisb");
		sql.append(", fm.WriteoffLoan, fm.SchdVersion, fm.NumberOfTerms, fm.ManualSchdType");
		sql.append(", fm.OverdraftTxnChrgReq, fm.OverdraftCalcChrg, fm.OverdraftChrgAmtOrPerc, fm.OverdraftChrCalOn");
		sql.append(", fm.StepFinance, fm.AlwManualSteps, fm.CalcOfSteps, fm.NoOfGrcSteps, sdd.EntityCode");
		sql.append(", fm.UnderSettlement, fm.UnderNpa");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join SmtDivisionDetail sdd on sdd.DivisionCode = ft.FinDivision");

		return sql;
	}

	public class FinRowMapperForEod implements RowMapper<FinanceMain> {

		@Override
		public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {

			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
			fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
			fm.setGraceBaseRate(rs.getString("GraceBaseRate"));
			fm.setGraceSpecialRate(rs.getString("GraceSpecialRate"));
			fm.setGrcPftRate(rs.getBigDecimal("GrcPftRate"));
			fm.setGrcPftFrq(rs.getString("GrcPftFrq"));
			fm.setNextGrcPftDate(rs.getDate("NextGrcPftDate"));
			fm.setAllowGrcPftRvw(rs.getBoolean("AllowGrcPftRvw"));
			fm.setGrcPftRvwFrq(rs.getString("GrcPftRvwFrq"));
			fm.setNextGrcPftRvwDate(rs.getDate("NextGrcPftRvwDate"));
			fm.setAllowGrcCpz(rs.getBoolean("AllowGrcCpz"));
			fm.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
			fm.setNextGrcCpzDate(rs.getDate("NextGrcCpzDate"));
			fm.setRepayBaseRate(rs.getString("RepayBaseRate"));
			fm.setRepaySpecialRate(rs.getString("RepaySpecialRate"));
			fm.setRepayProfitRate(rs.getBigDecimal("RepayProfitRate"));
			fm.setRepayFrq(rs.getString("RepayFrq"));
			fm.setNextRepayDate(rs.getDate("NextRepayDate"));
			fm.setRepayPftFrq(rs.getString("RepayPftFrq"));
			fm.setNextRepayPftDate(rs.getDate("NextRepayPftDate"));
			fm.setAllowRepayRvw(rs.getBoolean("AllowRepayRvw"));
			fm.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
			fm.setNextRepayRvwDate(rs.getDate("NextRepayRvwDate"));
			fm.setAllowRepayCpz(rs.getBoolean("AllowRepayCpz"));
			fm.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
			fm.setNextRepayCpzDate(rs.getDate("NextRepayCpzDate"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setCpzAtGraceEnd(rs.getBoolean("CpzAtGraceEnd"));
			fm.setGrcRateBasis(rs.getString("GrcRateBasis"));
			fm.setRepayRateBasis(rs.getString("RepayRateBasis"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
			fm.setFirstRepay(rs.getBigDecimal("FirstRepay"));
			fm.setLastRepay(rs.getBigDecimal("LastRepay"));
			fm.setScheduleMethod(rs.getString("ScheduleMethod"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinSourceID(rs.getString("FinSourceID"));
			fm.setRecalType(rs.getString("RecalType"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setLastRepayDate(rs.getDate("LastRepayDate"));
			fm.setLastRepayPftDate(rs.getDate("LastRepayPftDate"));
			fm.setLastRepayRvwDate(rs.getDate("LastRepayRvwDate"));
			fm.setLastRepayCpzDate(rs.getDate("LastRepayCpzDate"));
			fm.setAllowGrcRepay(rs.getBoolean("AllowGrcRepay"));
			fm.setGrcSchdMthd(rs.getString("GrcSchdMthd"));
			fm.setGrcMargin(rs.getBigDecimal("GrcMargin"));
			fm.setRepayMargin(rs.getBigDecimal("RepayMargin"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));
			fm.setFinRepayPftOnFrq(rs.getBoolean("FinRepayPftOnFrq"));
			fm.setGrcProfitDaysBasis(rs.getString("GrcProfitDaysBasis"));
			fm.setGrcMinRate(rs.getBigDecimal("GrcMinRate"));
			fm.setGrcMaxRate(rs.getBigDecimal("GrcMaxRate"));
			fm.setGrcMaxAmount(rs.getBigDecimal("GrcMaxAmount"));
			fm.setRpyMinRate(rs.getBigDecimal("RpyMinRate"));
			fm.setRpyMaxRate(rs.getBigDecimal("RpyMaxRate"));
			fm.setManualSchedule(rs.getBoolean("ManualSchedule"));
			fm.setCalRoundingMode(rs.getString("CalRoundingMode"));
			fm.setRoundingTarget(rs.getInt("RoundingTarget"));
			fm.setRvwRateApplFor(rs.getString("RvwRateApplFor"));
			fm.setSchCalOnRvw(rs.getString("SchCalOnRvw"));
			fm.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
			fm.setDroppingMethod(rs.getString("DroppingMethod"));
			fm.setRateChgAnyDay(rs.getBoolean("RateChgAnyDay"));
			fm.setPastduePftMargin(rs.getBigDecimal("PastduePftMargin"));
			fm.setFinRepayMethod(rs.getString("FinRepayMethod"));
			fm.setMigratedFinance(rs.getBoolean("MigratedFinance"));
			fm.setScheduleMaintained(rs.getBoolean("ScheduleMaintained"));
			fm.setScheduleRegenerated(rs.getBoolean("ScheduleRegenerated"));
			fm.setMandateID(JdbcUtil.getLong(rs.getObject("MandateID")));
			fm.setFinStatus(rs.getString("FinStatus"));
			fm.setDueBucket(rs.getInt("DueBucket"));
			fm.setFinStsReason(rs.getString("FinStsReason"));
			fm.setPromotionCode(rs.getString("PromotionCode"));
			fm.setFinCategory(rs.getString("FinCategory"));
			fm.setProductCategory(rs.getString("ProductCategory"));
			fm.setReAgeBucket(rs.getInt("ReAgeBucket"));
			fm.setTDSApplicable(rs.getBoolean("TDSApplicable"));
			fm.setTdsType(rs.getString("TdsType"));
			fm.setBpiTreatment(rs.getString("BpiTreatment"));
			fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
			fm.setGrcAdvType(rs.getString("GrcAdvType"));
			fm.setAdvType(rs.getString("AdvType"));
			fm.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
			fm.setPromotionSeqId(JdbcUtil.getLong(rs.getObject("PromotionSeqId")));
			fm.setSvAmount(rs.getBigDecimal("SvAmount"));
			fm.setCbAmount(rs.getBigDecimal("CbAmount"));
			fm.setEmployeeName(rs.getString("EmployeeName"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setAlwGrcAdj(rs.getBoolean("AlwGrcAdj"));
			fm.setEndGrcPeriodAftrFullDisb(rs.getBoolean("EndGrcPeriodAftrFullDisb"));
			fm.setAutoIncGrcEndDate(rs.getBoolean("AutoIncGrcEndDate"));
			fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			fm.setVersion(rs.getInt("Version"));
			fm.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fm.setReferralId(rs.getString("ReferralId"));
			fm.setGraceTerms(rs.getInt("GraceTerms"));
			fm.setAlwMultiDisb(rs.getBoolean("Alwmultidisb"));
			fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
			fm.setSchdVersion(rs.getInt("SchdVersion"));
			fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			fm.setManualSchdType(rs.getString("ManualSchdType"));
			fm.setOverdraftTxnChrgReq(rs.getBoolean("OverdraftTxnChrgReq"));
			fm.setOverdraftCalcChrg(rs.getString("OverdraftCalcChrg"));
			fm.setOverdraftChrgAmtOrPerc(rs.getBigDecimal("OverdraftChrgAmtOrPerc"));
			fm.setOverdraftChrCalOn(rs.getString("OverdraftChrCalOn"));
			fm.setStepFinance(rs.getBoolean("StepFinance"));
			fm.setAlwManualSteps(rs.getBoolean("AlwManualSteps"));
			fm.setCalcOfSteps(rs.getString("CalcOfSteps"));
			fm.setNoOfGrcSteps(rs.getInt("NoOfGrcSteps"));
			fm.setEntityCode(rs.getString("EntityCode"));
			fm.setUnderSettlement(rs.getBoolean("UnderSettlement"));
			fm.setUnderNpa(rs.getBoolean("UnderNpa"));

			return fm;

		}

	}

	@Override
	public void updateCustChange(long newCustId, long mandateId, long finID, String type) {
		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(type);
		sql.append(" Set CustID = ?, MandateID = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setLong(1, newCustId);
			ps.setLong(2, mandateId);
			ps.setLong(3, finID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<UserPendingCases> getUserPendingCasesDetails(long usrId, String roleCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.RecordStatus");
		sql.append(", fm.RoleCode, sr.RoleDesc, fm.FinType, fm.InitiateDate");
		sql.append(", cu.CustShrtName, cu.PhoneNumber");
		sql.append(" FROM Financemain_Temp fm");
		sql.append(" Inner Join Secroles sr On sr.Rolecd = fm.NextRoleCode");
		sql.append(" Inner Join SecUsers su on su.UsrId = fm.LastMntBy");
		sql.append(" Inner Join Customers cu On cu.CustID = fm.CustID");
		sql.append(" Where fm.LastMntBy = ? and fm.NextRoleCode = ?");

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			UserPendingCases pc = new UserPendingCases();

			pc.setFinID(rs.getLong("FinID"));
			pc.setFinReference(rs.getString("FinReference"));
			pc.setRecordStatus(rs.getString("RecordStatus"));
			pc.setRoleCode(rs.getString("RoleCode"));
			pc.setRoleDesc(rs.getString("RoleDesc"));
			pc.setFinType(rs.getString("FinType"));
			pc.setInitiateDate(rs.getDate("InitiateDate"));
			pc.setCustShrtName(rs.getString("CustShrtName"));
			pc.setPhoneNumber(rs.getString("PhoneNumber"));

			return pc;

		}, usrId, roleCode);
	}

	private String getFinMainAllQuery(String type, boolean wif) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod, GraceBaseRate");
		sql.append(", GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw, GrcPftRvwFrq");
		sql.append(", NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate, RepaySpecialRate");
		sql.append(", RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate, AllowRepayRvw");
		sql.append(", RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate");
		sql.append(", MaturityDate, CpzAtGraceEnd, DownPayment, GraceFlatAmount, ReqRepayAmount, TotalProfit");
		sql.append(", TotalCpz, TotalGrossPft, TotalGrossGrcPft, TotalGracePft, TotalGraceCpz, GrcRateBasis");
		sql.append(", RepayRateBasis, FinType, FinRemarks, FinCcy, ScheduleMethod, ProfitDaysBasis");
		sql.append(", ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay, FinStartDate, FinAmount");
		sql.append(", FinRepaymentAmount, CustID, Defferments, PlanDeferCount, FinBranch, FinSourceID");
		sql.append(", AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange, AvailedDefFrqChange");
		sql.append(", RecalType, FinAssetValue, FinIsActive, Version");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, MinDownPayPerc, LastRepayDate, LastRepayPftDate, LastRepayRvwDate");
		sql.append(", LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd, GrcMargin, RepayMargin, FinCurrAssetValue");
		sql.append(", FinCommitmentRef, FinContractDate ");
		sql.append(", TotalRepayAmt, FinApprovedDate, FeeChargeAmt");
		sql.append(", FinRepayPftOnFrq, AnualizedPercRate, EffectiveRateOfReturn, DownPayBank, DownPaySupl");
		sql.append(", GraceTerms, GrcProfitDaysBasis, StepFinance, StepType, StepPolicy, AlwManualSteps");
		sql.append(", NoOfSteps, LinkedFinRef, GrcMinRate, GrcMaxRate, GrcMaxAmount, RpyMinRate, RpyMaxRate");
		sql.append(", ManualSchedule");
		sql.append(", TDSApplicable, AlwBPI, BpiTreatment, PlanEMIHAlw");
		sql.append(", PlanEMIHMethod, PlanEMIHMaxPerYear, PlanEMIHMax, PlanEMIHLockPeriod, PlanEMICpz");
		sql.append(", CalRoundingMode, RoundingTarget, AlwMultiDisb, BpiAmount, DeductFeeDisb, RvwRateApplFor");
		sql.append(", SchCalOnRvw, PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin");
		sql.append(", FinCategory, ProductCategory, AdvanceEMI, BpiPftDaysBasis, FixedTenorRate, FixedRateTenor");
		sql.append(", GrcAdvType, GrcAdvTerms, AdvType, AdvTerms, AdvStage, AllowDrawingPower, AllowRevolving");
		sql.append(", SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount, AppliedLoanAmt");
		sql.append(", FinIsRateRvwAtGrcEnd, ClosingStatus, WriteoffLoan, Restructure");
		sql.append(", OverdraftTxnChrgReq, OverdraftCalcChrg, OverdraftChrgAmtOrPerc, OverdraftChrCalOn");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");

		if (!wif) {
			sql.append(", DmaCode, TdsPercentage, FinStsReason, Connector, samplingRequired, LimitApproved");
			sql.append(", NextUserId, VanCode, FinLimitRef");
			sql.append(", legalRequired, CreditInsAmt, Blacklisted, FinRepayMethod, FirstDroplineDate");
			sql.append(", CustDSR, AccountsOfficer, QuickDisb, UnPlanEMICpz");
			sql.append(", ReAgeCpz, AvailedReAgeH, SalesDepartment, DroplineFrq");
			sql.append(", PromotionCode, TdsLimitAmt, MigratedFinance, MaxReAgeHolidays");
			sql.append(", WifReference, UnPlanEMIHLockPeriod, TdsEndDate, Priority, Discrepancy, DeviationApproval");
			sql.append(", ScheduleMaintained, FinPurpose, ScheduleRegenerated, SecurityCollateral, RcdMaintainSts");
			sql.append(", MaxUnplannedEmi, DsaCode, ReferralId, InitiateDate, ProcessAttributes");
			sql.append(", VanReq, InvestmentRef, FinPreApprovedRef, EmployeeName, OverrideLimit, TdsStartDate");
			sql.append(", MandateID, SecurityMandateID");
			sql.append(", LimitValid, ApplicationNo, EligibilityMethod, PftServicingODLimit");
			sql.append(", BusinessVertical, ReAgeBucket, JointCustId, InitiateUser, Approved");
			sql.append(", JointAccount, FinStatus, AvailedUnPlanEmi, PlanEMIHAlwInGrace, SchdVersion");
			sql.append(", SubVentionFrom, ManufacturerDealerId, Escrow, CustBankId");

			// HL
			sql.append(", ReqLoanAmt, ReqLoanTenor, FinOcrRequired, OfferProduct, OfferAmount, CustSegmentation");
			sql.append(", BaseProduct, ProcessType, BureauTimeSeries, CampaignName, ExistingLanRefNo, OfferId");
			sql.append(", LeadSource, PoSource, Rsa, Verification, SourcingBranch, SourChannelCategory, AsmName");
			sql.append(", AlwGrcAdj, EndGrcPeriodAftrFullDisb, AutoIncGrcEndDate,InstBasedSchd, ParentRef");
			sql.append(", AlwLoanSplit, LoanSplitted, Pmay, AllowSubvention, TdsType, NoOfGrcSteps");
			sql.append(", CalcOfSteps, StepsAppliedFor");
			sql.append(", ManualSchdType, Isra, SanctionedDate");
			sql.append(", UnderSettlement, UnderNpa");
		}

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescFinTypeName, LovDescFinMaxAmt, LovDescFinMinAmount, LovDescFinBranchName");

			if (StringUtils.trimToEmpty(type).contains("AView")) {
				sql.append(", LovDescCustCIF");
			}

			if (!wif) {
				sql.append(", LovDescFinScheduleOn, LovDescAccruedTillLBD, CustStsDescription");
				sql.append(", LovDescSourceCity, LovDescFinDivision, FinBranchProvinceCode, LovDescStepPolicyName");
				sql.append(", LovDescAccountsOfficer, DsaCodeDesc, ReferralIdDesc, EmployeeNameDesc, DmaCodeDesc");
				sql.append(", SalesDepartmentDesc, LovDescEntityCode, LovEligibilityMethod");
				sql.append(", LovDescEligibilityMethod, LovDescFinPurposeName, ConnectorCode");
				sql.append(", ConnectorDesc, BusinessVerticalCode, BusinessVerticalDesc, LovDescSourcingBranch");
				sql.append(", EmployeeName, ManufacturerDealerName, ManufacturerDealerCode");
				sql.append(", CustAcctNumber, CustAcctHolderName");
			}
		}

		if (!wif) {
			sql.append(" From FinanceMain");
		} else {
			sql.append(" From WIFFinanceMain");
		}

		sql.append(StringUtils.trimToEmpty(type));
		return sql.toString();
	}

	private class FinanceMainRowMapper implements RowMapper<FinanceMain> {
		private String type;
		private boolean wIf;

		private FinanceMainRowMapper(String type, boolean wIf) {
			this.type = type;
			this.wIf = wIf;
		}

		@Override
		public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
			fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
			fm.setGraceBaseRate(rs.getString("GraceBaseRate"));
			fm.setGraceSpecialRate(rs.getString("GraceSpecialRate"));
			fm.setGrcPftRate(rs.getBigDecimal("GrcPftRate"));
			fm.setGrcPftFrq(rs.getString("GrcPftFrq"));
			fm.setNextGrcPftDate(rs.getDate("NextGrcPftDate"));
			fm.setAllowGrcPftRvw(rs.getBoolean("AllowGrcPftRvw"));
			fm.setGrcPftRvwFrq(rs.getString("GrcPftRvwFrq"));
			fm.setNextGrcPftRvwDate(rs.getDate("NextGrcPftRvwDate"));
			fm.setAllowGrcCpz(rs.getBoolean("AllowGrcCpz"));
			fm.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
			fm.setNextGrcCpzDate(rs.getDate("NextGrcCpzDate"));
			fm.setRepayBaseRate(rs.getString("RepayBaseRate"));
			fm.setRepaySpecialRate(rs.getString("RepaySpecialRate"));
			fm.setRepayProfitRate(rs.getBigDecimal("RepayProfitRate"));
			fm.setRepayFrq(rs.getString("RepayFrq"));
			fm.setNextRepayDate(rs.getDate("NextRepayDate"));
			fm.setRepayPftFrq(rs.getString("RepayPftFrq"));
			fm.setNextRepayPftDate(rs.getDate("NextRepayPftDate"));
			fm.setAllowRepayRvw(rs.getBoolean("AllowRepayRvw"));
			fm.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
			fm.setNextRepayRvwDate(rs.getDate("NextRepayRvwDate"));
			fm.setAllowRepayCpz(rs.getBoolean("AllowRepayCpz"));
			fm.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
			fm.setNextRepayCpzDate(rs.getDate("NextRepayCpzDate"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setCpzAtGraceEnd(rs.getBoolean("CpzAtGraceEnd"));
			fm.setDownPayment(rs.getBigDecimal("DownPayment"));
			// fm.setGraceFlatAmount(rs.getBigDecimal("GraceFlatAmount")); //(Not available in Bean)
			fm.setReqRepayAmount(rs.getBigDecimal("ReqRepayAmount"));
			fm.setTotalProfit(rs.getBigDecimal("TotalProfit"));
			fm.setTotalCpz(rs.getBigDecimal("TotalCpz"));
			fm.setTotalGrossPft(rs.getBigDecimal("TotalGrossPft"));
			fm.setTotalGrossGrcPft(rs.getBigDecimal("TotalGrossGrcPft"));
			fm.setTotalGracePft(rs.getBigDecimal("TotalGracePft"));
			fm.setTotalGraceCpz(rs.getBigDecimal("TotalGraceCpz"));
			fm.setGrcRateBasis(rs.getString("GrcRateBasis"));
			fm.setRepayRateBasis(rs.getString("RepayRateBasis"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinRemarks(rs.getString("FinRemarks"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setScheduleMethod(rs.getString("ScheduleMethod"));
			fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
			fm.setReqMaturity(rs.getDate("ReqMaturity"));
			fm.setCalTerms(rs.getInt("CalTerms"));
			fm.setCalMaturity(rs.getDate("CalMaturity"));
			fm.setFirstRepay(rs.getBigDecimal("FirstRepay"));
			fm.setLastRepay(rs.getBigDecimal("LastRepay"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setDefferments(rs.getInt("Defferments"));
			fm.setPlanDeferCount(rs.getInt("PlanDeferCount"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinSourceID(rs.getString("FinSourceID"));
			fm.setAllowedDefRpyChange(rs.getInt("AllowedDefRpyChange"));
			fm.setAvailedDefRpyChange(rs.getInt("AvailedDefRpyChange"));
			fm.setAllowedDefFrqChange(rs.getInt("AllowedDefFrqChange"));
			fm.setAvailedDefFrqChange(rs.getInt("AvailedDefFrqChange"));
			fm.setRecalType(rs.getString("RecalType"));
			fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setVersion(rs.getInt("Version"));
			fm.setLastMntBy(rs.getLong("LastMntBy"));
			fm.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fm.setRecordStatus(rs.getString("RecordStatus"));
			fm.setRoleCode(rs.getString("RoleCode"));
			fm.setNextRoleCode(rs.getString("NextRoleCode"));
			fm.setTaskId(rs.getString("TaskId"));
			fm.setNextTaskId(rs.getString("NextTaskId"));
			fm.setRecordType(rs.getString("RecordType"));
			fm.setWorkflowId(rs.getLong("WorkflowId"));
			fm.setMinDownPayPerc(rs.getBigDecimal("MinDownPayPerc"));
			fm.setLastRepayDate(rs.getDate("LastRepayDate"));
			fm.setLastRepayPftDate(rs.getDate("LastRepayPftDate"));
			fm.setLastRepayRvwDate(rs.getDate("LastRepayRvwDate"));
			fm.setLastRepayCpzDate(rs.getDate("LastRepayCpzDate"));
			fm.setAllowGrcRepay(rs.getBoolean("AllowGrcRepay"));
			fm.setGrcSchdMthd(rs.getString("GrcSchdMthd"));
			fm.setGrcMargin(rs.getBigDecimal("GrcMargin"));
			fm.setRepayMargin(rs.getBigDecimal("RepayMargin"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setFinCommitmentRef(rs.getString("FinCommitmentRef"));
			fm.setFinContractDate(rs.getDate("FinContractDate"));
			fm.setTotalRepayAmt(rs.getBigDecimal("TotalRepayAmt"));
			fm.setFinApprovedDate(rs.getDate("FinApprovedDate"));
			fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			fm.setFinRepayPftOnFrq(rs.getBoolean("FinRepayPftOnFrq"));
			fm.setAnualizedPercRate(rs.getBigDecimal("AnualizedPercRate"));
			fm.setEffectiveRateOfReturn(rs.getBigDecimal("EffectiveRateOfReturn"));
			fm.setDownPayBank(rs.getBigDecimal("DownPayBank"));
			fm.setDownPaySupl(rs.getBigDecimal("DownPaySupl"));
			fm.setGraceTerms(rs.getInt("GraceTerms"));
			fm.setGrcProfitDaysBasis(rs.getString("GrcProfitDaysBasis"));
			fm.setStepFinance(rs.getBoolean("StepFinance"));
			fm.setStepType(rs.getString("StepType"));
			fm.setStepPolicy(rs.getString("StepPolicy"));
			fm.setAlwManualSteps(rs.getBoolean("AlwManualSteps"));
			fm.setNoOfSteps(rs.getInt("NoOfSteps"));
			fm.setLinkedFinRef(rs.getString("LinkedFinRef"));
			fm.setGrcMinRate(rs.getBigDecimal("GrcMinRate"));
			fm.setGrcMaxRate(rs.getBigDecimal("GrcMaxRate"));
			fm.setGrcMaxAmount(rs.getBigDecimal("GrcMaxAmount"));
			fm.setRpyMinRate(rs.getBigDecimal("RpyMinRate"));
			fm.setRpyMaxRate(rs.getBigDecimal("RpyMaxRate"));
			fm.setManualSchedule(rs.getBoolean("ManualSchedule"));
			fm.setTDSApplicable(rs.getBoolean("TDSApplicable"));
			fm.setAlwBPI(rs.getBoolean("AlwBPI"));
			fm.setBpiTreatment(rs.getString("BpiTreatment"));
			fm.setPlanEMIHAlw(rs.getBoolean("PlanEMIHAlw"));
			fm.setPlanEMIHMethod(rs.getString("PlanEMIHMethod"));
			fm.setPlanEMIHMaxPerYear(rs.getInt("PlanEMIHMaxPerYear"));
			fm.setPlanEMIHMax(rs.getInt("PlanEMIHMax"));
			fm.setPlanEMIHLockPeriod(rs.getInt("PlanEMIHLockPeriod"));
			fm.setPlanEMICpz(rs.getBoolean("PlanEMICpz"));
			fm.setCalRoundingMode(rs.getString("CalRoundingMode"));
			fm.setRoundingTarget(rs.getInt("RoundingTarget"));
			fm.setAlwMultiDisb(rs.getBoolean("AlwMultiDisb"));
			fm.setBpiAmount(rs.getBigDecimal("BpiAmount"));
			fm.setDeductFeeDisb(rs.getBigDecimal("DeductFeeDisb"));
			fm.setRvwRateApplFor(rs.getString("RvwRateApplFor"));
			fm.setSchCalOnRvw(rs.getString("SchCalOnRvw"));
			fm.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
			fm.setDroppingMethod(rs.getString("DroppingMethod"));
			fm.setRateChgAnyDay(rs.getBoolean("RateChgAnyDay"));
			fm.setPastduePftMargin(rs.getBigDecimal("PastduePftMargin"));
			fm.setFinCategory(rs.getString("FinCategory"));
			fm.setProductCategory(rs.getString("ProductCategory"));
			fm.setAdvanceEMI(rs.getBigDecimal("AdvanceEMI"));
			fm.setBpiPftDaysBasis(rs.getString("BpiPftDaysBasis"));
			fm.setFixedTenorRate(rs.getBigDecimal("FixedTenorRate"));
			fm.setFixedRateTenor(rs.getInt("FixedRateTenor"));
			fm.setGrcAdvType(rs.getString("GrcAdvType"));
			fm.setGrcAdvTerms(rs.getInt("GrcAdvTerms"));
			fm.setAdvType(rs.getString("AdvType"));
			fm.setAdvTerms(rs.getInt("AdvTerms"));
			fm.setAdvStage(rs.getString("AdvStage"));
			fm.setAllowDrawingPower(rs.getBoolean("AllowDrawingPower"));
			fm.setAllowRevolving(rs.getBoolean("AllowRevolving"));
			fm.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
			fm.setPromotionSeqId(JdbcUtil.getLong(rs.getObject("PromotionSeqId")));
			fm.setSvAmount(rs.getBigDecimal("SvAmount"));
			fm.setCbAmount(rs.getBigDecimal("CbAmount"));
			fm.setAppliedLoanAmt(rs.getBigDecimal("AppliedLoanAmt"));
			fm.setFinIsRateRvwAtGrcEnd(rs.getBoolean("FinIsRateRvwAtGrcEnd"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));
			fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
			fm.setRestructure(rs.getBoolean("Restructure"));
			fm.setRestructure(rs.getBoolean("Restructure"));
			fm.setOverdraftTxnChrgReq(rs.getBoolean("OverdraftTxnChrgReq"));
			fm.setOverdraftCalcChrg(rs.getString("OverdraftCalcChrg"));
			fm.setOverdraftChrgAmtOrPerc(rs.getBigDecimal("OverdraftChrgAmtOrPerc"));
			fm.setOverdraftChrCalOn(rs.getString("OverdraftChrCalOn"));
			fm.setCreatedBy(rs.getLong("CreatedBy"));
			fm.setCreatedOn(rs.getTimestamp("CreatedOn"));
			fm.setApprovedBy(rs.getLong("ApprovedBy"));
			fm.setApprovedOn(rs.getTimestamp("ApprovedOn"));

			if (!wIf) {
				fm.setDmaCode(rs.getString("DmaCode"));
				fm.setTdsPercentage(rs.getBigDecimal("TdsPercentage"));
				fm.setFinStsReason(rs.getString("FinStsReason"));
				fm.setConnector(rs.getLong("Connector"));
				fm.setSamplingRequired(rs.getBoolean("samplingRequired"));
				// fm.setLimitApproved(rs.getString("LimitApproved")); //(Not available in Bean)
				fm.setNextUserId(rs.getString("NextUserId"));
				fm.setVanCode(rs.getString("VanCode"));
				fm.setFinLimitRef(rs.getString("FinLimitRef"));
				fm.setAvailedUnPlanEmi(rs.getInt("AvailedUnPlanEmi"));
				fm.setLegalRequired(rs.getBoolean("legalRequired"));
				// fm.setCreditInsAmt(rs.getBigDecimal("CreditInsAmt")); //(Not available in Bean)
				fm.setBlacklisted(rs.getBoolean("Blacklisted"));
				fm.setFinRepayMethod(rs.getString("FinRepayMethod"));
				fm.setFirstDroplineDate(rs.getDate("FirstDroplineDate"));
				fm.setCustDSR(rs.getBigDecimal("CustDSR"));
				fm.setAccountsOfficer(rs.getLong("AccountsOfficer"));
				fm.setQuickDisb(rs.getBoolean("QuickDisb"));
				fm.setUnPlanEMICpz(rs.getBoolean("UnPlanEMICpz"));
				fm.setReAgeCpz(rs.getBoolean("ReAgeCpz"));
				fm.setAvailedReAgeH(rs.getInt("AvailedReAgeH"));
				fm.setSalesDepartment(rs.getString("SalesDepartment"));
				fm.setDroplineFrq(rs.getString("DroplineFrq"));
				fm.setPromotionCode(rs.getString("PromotionCode"));
				fm.setTdsLimitAmt(rs.getBigDecimal("TdsLimitAmt"));
				fm.setMigratedFinance(rs.getBoolean("MigratedFinance"));
				fm.setMaxReAgeHolidays(rs.getInt("MaxReAgeHolidays"));
				fm.setWifReference(rs.getString("WifReference"));
				fm.setUnPlanEMIHLockPeriod(rs.getInt("UnPlanEMIHLockPeriod"));
				fm.setTdsEndDate(rs.getDate("TdsEndDate"));
				fm.setPriority(rs.getInt("Priority"));
				// fm.setDiscrepancy(rs.getString("Discrepancy")); //(Not available in Bean)
				fm.setDeviationApproval(rs.getBoolean("DeviationApproval"));
				fm.setScheduleMaintained(rs.getBoolean("ScheduleMaintained"));
				fm.setFinPurpose(rs.getString("FinPurpose"));
				fm.setScheduleRegenerated(rs.getBoolean("ScheduleRegenerated"));
				// fm.setSecurityCollateral(rs.getString("SecurityCollateral")); //(Not available in Bean)
				fm.setRcdMaintainSts(rs.getString("RcdMaintainSts"));
				fm.setMaxUnplannedEmi(rs.getInt("MaxUnplannedEmi"));
				fm.setDsaCode(rs.getString("DsaCode"));
				fm.setReferralId(rs.getString("ReferralId"));
				fm.setInitiateDate(rs.getDate("InitiateDate"));
				fm.setProcessAttributes(rs.getString("ProcessAttributes"));
				fm.setVanReq(rs.getBoolean("VanReq"));
				fm.setInvestmentRef(rs.getString("InvestmentRef"));
				fm.setFinPreApprovedRef(rs.getString("FinPreApprovedRef"));
				fm.setEmployeeName(rs.getString("EmployeeName"));
				fm.setOverrideLimit(rs.getBoolean("OverrideLimit"));
				fm.setTdsStartDate(rs.getDate("TdsStartDate"));
				fm.setMandateID(JdbcUtil.getLong(rs.getObject("MandateID")));
				fm.setSecurityMandateID(JdbcUtil.getLong(rs.getObject("SecurityMandateID")));
				fm.setLimitValid(rs.getBoolean("LimitValid"));
				fm.setApplicationNo(rs.getString("ApplicationNo"));
				fm.setEligibilityMethod(rs.getLong("EligibilityMethod"));
				fm.setPftServicingODLimit(rs.getBoolean("PftServicingODLimit"));
				fm.setBusinessVertical(JdbcUtil.getLong(rs.getObject("BusinessVertical")));
				fm.setReAgeBucket(rs.getInt("ReAgeBucket"));
				fm.setJointCustId(rs.getLong("JointCustId"));
				fm.setInitiateUser(rs.getLong("InitiateUser"));
				fm.setApproved(rs.getString("Approved"));
				fm.setJointAccount(rs.getBoolean("JointAccount"));
				fm.setFinStatus(rs.getString("FinStatus"));
				fm.setPlanEMIHAlwInGrace(rs.getBoolean("PlanEMIHAlwInGrace"));
				fm.setSchdVersion(rs.getInt("SchdVersion"));
				fm.setSubVentionFrom(rs.getString("SubVentionFrom"));
				fm.setManufacturerDealerId(JdbcUtil.getLong(rs.getObject("ManufacturerDealerId")));
				fm.setEscrow(rs.getBoolean("Escrow"));
				fm.setCustBankId(JdbcUtil.getLong(rs.getObject("CustBankId")));

				// HL
				fm.setReqLoanAmt(rs.getBigDecimal("ReqLoanAmt"));
				fm.setReqLoanTenor(rs.getInt("ReqLoanTenor"));
				fm.setFinOcrRequired(rs.getBoolean("FinOcrRequired"));
				fm.setOfferProduct(rs.getString("OfferProduct"));
				fm.setOfferAmount(rs.getBigDecimal("OfferAmount"));
				fm.setCustSegmentation(rs.getString("CustSegmentation"));
				fm.setBaseProduct(rs.getString("BaseProduct"));
				fm.setProcessType(rs.getString("ProcessType"));
				fm.setBureauTimeSeries(rs.getString("BureauTimeSeries"));
				fm.setCampaignName(rs.getString("CampaignName"));
				fm.setExistingLanRefNo(rs.getString("ExistingLanRefNo"));
				fm.setOfferId(rs.getString("OfferId"));
				fm.setLeadSource(rs.getString("LeadSource"));
				fm.setPoSource(rs.getString("PoSource"));
				fm.setRsa(rs.getBoolean("rsa"));
				fm.setVerification(rs.getString("Verification"));
				fm.setSourcingBranch(rs.getString("SourcingBranch"));
				fm.setSourChannelCategory(rs.getString("SourChannelCategory"));
				fm.setAsmName(JdbcUtil.getLong(rs.getObject("AsmName")));
				fm.setAlwGrcAdj(rs.getBoolean("AlwGrcAdj"));
				fm.setEndGrcPeriodAftrFullDisb(rs.getBoolean("EndGrcPeriodAftrFullDisb"));
				fm.setAutoIncGrcEndDate(rs.getBoolean("AutoIncGrcEndDate"));
				fm.setInstBasedSchd(rs.getBoolean("InstBasedSchd"));
				fm.setParentRef(rs.getString("ParentRef"));
				fm.setAlwLoanSplit(rs.getBoolean("AlwLoanSplit"));
				fm.setLoanSplitted(rs.getBoolean("LoanSplitted"));
				fm.setPmay(rs.getBoolean("Pmay"));
				fm.setAllowSubvention(rs.getBoolean("AllowSubvention"));
				fm.setTdsType(rs.getString("TdsType"));
				fm.setNoOfGrcSteps(rs.getInt("NoOfGrcSteps"));
				fm.setCalcOfSteps(rs.getString("CalcOfSteps"));
				fm.setStepsAppliedFor(rs.getString("StepsAppliedFor"));
				fm.setManualSchdType(rs.getString("ManualSchdType"));
				fm.setIsra(rs.getBoolean("Isra"));
				fm.setSanctionedDate(rs.getDate("SanctionedDate"));
				fm.setUnderSettlement(rs.getBoolean("UnderSettlement"));
				fm.setUnderNpa(rs.getBoolean("UnderNpa"));
			}

			if (StringUtils.trimToEmpty(type).contains("View")) {
				fm.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
				// fm.setLovDescFinMaxAmt(rs.getBigDecimal("LovDescFinMaxAmt")); //(Not available in Bean)
				// fm.setLovDescFinMinAmount(rs.getBigDecimal("LovDescFinMinAmount")); //(Not available in Bean)
				fm.setLovDescFinBranchName(rs.getString("LovDescFinBranchName"));

				if (StringUtils.trimToEmpty(type).contains("AView")) {
					fm.setLovDescCustCIF(rs.getString("LovDescCustCIF"));
				}

				if (!wIf) {
					fm.setLovDescFinScheduleOn(rs.getString("LovDescFinScheduleOn"));
					fm.setLovDescAccruedTillLBD(rs.getBigDecimal("LovDescAccruedTillLBD"));
					fm.setCustStsDescription(rs.getString("CustStsDescription"));
					fm.setLovDescSourceCity(rs.getString("LovDescSourceCity"));
					fm.setLovDescFinDivision(rs.getString("LovDescFinDivision"));
					fm.setFinBranchProvinceCode(rs.getString("FinBranchProvinceCode"));
					fm.setLovDescStepPolicyName(rs.getString("LovDescStepPolicyName"));
					fm.setLovDescAccountsOfficer(rs.getString("LovDescAccountsOfficer"));
					fm.setDsaCodeDesc(rs.getString("DsaCodeDesc"));
					fm.setReferralIdDesc(rs.getString("ReferralIdDesc"));
					fm.setEmployeeNameDesc(rs.getString("EmployeeNameDesc"));
					fm.setDmaCodeDesc(rs.getString("DmaCodeDesc"));
					fm.setSalesDepartmentDesc(rs.getString("SalesDepartmentDesc"));
					fm.setEntityCode(rs.getString("LovDescEntityCode"));
					fm.setLovDescEntityCode(rs.getString("LovDescEntityCode"));
					fm.setLovEligibilityMethod(rs.getString("LovEligibilityMethod"));
					fm.setLovDescEligibilityMethod(rs.getString("LovDescEligibilityMethod"));
					fm.setLovDescFinPurposeName(rs.getString("LovDescFinPurposeName"));
					fm.setConnectorCode(rs.getString("ConnectorCode"));
					fm.setConnectorDesc(rs.getString("ConnectorDesc"));
					fm.setBusinessVerticalCode(rs.getString("BusinessVerticalCode"));
					fm.setBusinessVerticalDesc(rs.getString("BusinessVerticalDesc"));
					// HL
					fm.setLovDescSourcingBranch(rs.getString("LovDescSourcingBranch"));
					fm.setEmployeeName(rs.getString("EmployeeName"));
					fm.setManufacturerDealerName(rs.getString("ManufacturerDealerName"));
					fm.setManufacturerDealerCode(rs.getString("ManufacturerDealerCode"));
					fm.setCustAcctNumber(rs.getString("CustAcctNumber"));
					fm.setCustAcctHolderName(rs.getString("custAcctHolderName"));

				}
			}

			return fm;
		}
	}

	@Override
	public Long getCustomerIdByFin(String finReference) {
		StringBuilder sql = new StringBuilder("Select distinct CustId");
		sql.append(" From (");
		sql.append(" Select CustId From FinanceMain_Temp Where FinReference = ?");
		sql.append(" Union all");
		sql.append(" Select CustId From FinanceMain Where FinReference = ?");
		sql.append(") T");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, finReference, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinDetailsForHunter(String leadId, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select fm.FinID, fm.FinReference, fm.FinType");
		sql.append(" From FinanceMain").append(type).append(" fm");
		sql.append(" Inner Join RMTFinanceTypes ft ON ft.FinType = fm.FinType");
		sql.append(" Where fm.OfferId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinType(rs.getString("FinType"));

				return fm;

			}, leadId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	// FIXME to custom RowMapper
	@Override
	public List<FinanceMain> getFinanceByInvReference(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select * From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = :FinID");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinID", finID);

		RowMapper<FinanceMain> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceMain.class);

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);

	}

	@Override
	public List<Long> getInvestmentFinRef(String investmentRef, String type) {
		StringBuilder sql = new StringBuilder("Select FinID From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where InvestmentRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, investmentRef);
	}

	@Override
	public List<Long> getParentRefifAny(String parentRef, String type, boolean isFromAgr) {
		StringBuilder sql = new StringBuilder("Select FinID From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));

		Object[] objects = null;

		if (isFromAgr) {
			objects = new Object[] { parentRef, parentRef };
			sql.append(" Where ParentRef = ? or InvestmentRef = ?");
		} else {
			objects = new Object[] { parentRef };
			sql.append(" Where ParentRef = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, objects);

	}

	@Override
	public void updatePmay(long finID, boolean pmay, String type) {
		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(type);
		sql.append(" Set PMay = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setBoolean(1, pmay);
			ps.setLong(2, finID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinCustomerDetails getDetailsByOfferID(String offerID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustShrtName, CustCif, fm.FinID, fm.Finreference, fm.OfferId");
		sql.append(" From Customers cu");
		sql.append(" Inner Join (");
		sql.append(" Select OfferId, FinID, Finreference, CustID From FinanceMain");
		sql.append(" Union All");
		sql.append(" Select OfferId, FinID, Finreference, CustID From FinanceMain_Temp");
		sql.append(") fm on fm.CustID = cu.CustID");
		sql.append(" Where OfferId = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinCustomerDetails cd = null;

		SqlRowSet rowSet = this.jdbcOperations.queryForRowSet(sql.toString(), offerID);
		if (rowSet != null) {
			cd = new FinCustomerDetails();
			while (rowSet.next()) {
				FinCustomerDetails.Category category = cd.new Category();
				cd.setFinID(rowSet.getLong("FinID"));
				cd.setFinReference(rowSet.getString("Finreference"));
				category.setName(rowSet.getString("CustShrtName"));
				category.setCategory("Primary");
				category.setCif(rowSet.getString("CustCif"));
				cd.getCif().add(category);
			}
			setJointAccountDetails(cd);
		}

		return cd;
	}

	private void setJointAccountDetails(FinCustomerDetails cd) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustShrtName, CustCif, fm.FinID, fm.Finreference");
		sql.append(" From Customers cu");
		sql.append(" Inner Join (");
		sql.append(" Select FinID, Finreference, CustID From FinJointAccountDetails");
		sql.append(" Union All");
		sql.append(" Select FinID, Finreference, CustID From FinJointAccountDetails_Temp");
		sql.append(") fm on fm.CustID = cu.CustID");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		SqlRowSet rowSet = this.jdbcOperations.queryForRowSet(sql.toString(), cd.getFinID());
		if (rowSet != null) {
			while (rowSet.next()) {
				FinCustomerDetails.Category category = cd.new Category();
				category.setCif(rowSet.getString("custcif"));
				category.setCategory("Co-Applicant");
				category.setName(rowSet.getString("custshrtname"));
				cd.getCif().add(category);
			}
		}
	}

	@Override
	public DMSQueue getOfferIdByFin(DMSQueue dmsQueue) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" OfferId, ApplicationNo");
		sql.append(" From (");
		sql.append(" Select fm.OfferId, fm.FinID, fm.ApplicationNo From FinanceMain_Temp fm");
		sql.append(" Union All");
		sql.append(" Select fm.OfferId, fm.FinID, fm.ApplicationNo From FinanceMain fm");
		sql.append(" Where Not (Exists (Select 1 From FinanceMain_Temp");
		sql.append(" Where FinanceMain_Temp.FinID = fm.FinID))");
		sql.append(") T where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {

				dmsQueue.setOfferId(rs.getString("OfferId"));
				dmsQueue.setApplicationNo(rs.getString("ApplicationNo"));

				return dmsQueue;

			}, dmsQueue.getFinID());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getEHFinanceMain(final long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod, GrcPftFrq, AllowGrcPftRvw");
		sql.append(", GrcPftRvwFrq, AllowGrcCpz, GrcCpzFrq, RepayFrq, RepayPftFrq, AllowRepayRvw, RepayRvwFrq");
		sql.append(", AllowRepayCpz, RepayCpzFrq, MaturityDate, CpzAtGraceEnd, TotalProfit, TotalCpz");
		sql.append(", TotalGrossPft, TotalGrossGrcPft, TotalGracePft, TotalGraceCpz, GrcRateBasis");
		sql.append(", RepayRateBasis, FinType, ScheduleMethod, ProfitDaysBasis, FinStartDate, FinAmount");
		sql.append(", CustID, FinBranch, FinSourceID, RecalType, Version, AllowGrcRepay, GrcSchdMthd");
		sql.append(", FinRepayPftOnFrq, FinStatus, GraceTerms, FinRepayMethod, GrcProfitDaysBasis");
		sql.append(", TDSApplicable, DroplineFrq, FirstDroplineDate, AlwBPI, BpiTreatment, CalRoundingMode");
		sql.append(", RoundingTarget, MaxUnplannedEmi, AvailedUnPlanEmi, BpiAmount, DroppingMethod");
		sql.append(", FinCategory, ProductCategory, BpiPftDaysBasis,FinCcy,AdvTerms, AdvStage,AdvType, AdvanceEMI");
		sql.append(", FinIsActive, LastRepayRvwDate, PastduePftCalMthd, FinAssetValue, DueBucket, TdsType");
		sql.append(", WriteoffLoan");
		sql.append(" From FinanceMain");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
				fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
				fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
				fm.setGrcPftFrq(rs.getString("GrcPftFrq"));
				fm.setAllowGrcPftRvw(rs.getBoolean("AllowGrcPftRvw"));
				fm.setGrcPftRvwFrq(rs.getString("GrcPftRvwFrq"));
				fm.setAllowGrcCpz(rs.getBoolean("AllowGrcCpz"));
				fm.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
				fm.setRepayFrq(rs.getString("RepayFrq"));
				fm.setRepayPftFrq(rs.getString("RepayPftFrq"));
				fm.setAllowRepayRvw(rs.getBoolean("AllowRepayRvw"));
				fm.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
				fm.setAllowRepayCpz(rs.getBoolean("AllowRepayCpz"));
				fm.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setCpzAtGraceEnd(rs.getBoolean("CpzAtGraceEnd"));
				fm.setTotalProfit(rs.getBigDecimal("TotalProfit"));
				fm.setTotalCpz(rs.getBigDecimal("TotalCpz"));
				fm.setTotalGrossPft(rs.getBigDecimal("TotalGrossPft"));
				fm.setTotalGrossGrcPft(rs.getBigDecimal("TotalGrossGrcPft"));
				fm.setTotalGracePft(rs.getBigDecimal("TotalGracePft"));
				fm.setTotalGraceCpz(rs.getBigDecimal("TotalGraceCpz"));
				fm.setGrcRateBasis(rs.getString("GrcRateBasis"));
				fm.setRepayRateBasis(rs.getString("RepayRateBasis"));
				fm.setFinType(rs.getString("FinType"));
				fm.setScheduleMethod(rs.getString("ScheduleMethod"));
				fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setFinAmount(rs.getBigDecimal("FinAmount"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setFinSourceID(rs.getString("FinSourceID"));
				fm.setRecalType(rs.getString("RecalType"));
				fm.setVersion(rs.getInt("Version"));
				fm.setAllowGrcRepay(rs.getBoolean("AllowGrcRepay"));
				fm.setGrcSchdMthd(rs.getString("GrcSchdMthd"));
				fm.setFinRepayPftOnFrq(rs.getBoolean("FinRepayPftOnFrq"));
				fm.setFinStatus(rs.getString("FinStatus"));
				fm.setGraceTerms(rs.getInt("GraceTerms"));
				fm.setFinRepayMethod(rs.getString("FinRepayMethod"));
				fm.setGrcProfitDaysBasis(rs.getString("GrcProfitDaysBasis"));
				fm.setTDSApplicable(rs.getBoolean("TDSApplicable"));
				fm.setDroplineFrq(rs.getString("DroplineFrq"));
				fm.setFirstDroplineDate(rs.getDate("FirstDroplineDate"));
				fm.setAlwBPI(rs.getBoolean("AlwBPI"));
				fm.setBpiTreatment(rs.getString("BpiTreatment"));
				fm.setCalRoundingMode(rs.getString("CalRoundingMode"));
				fm.setRoundingTarget(rs.getInt("RoundingTarget"));
				fm.setMaxUnplannedEmi(rs.getInt("MaxUnplannedEmi"));
				fm.setAvailedUnPlanEmi(rs.getInt("AvailedUnPlanEmi"));
				fm.setBpiAmount(rs.getBigDecimal("BpiAmount"));
				fm.setDroppingMethod(rs.getString("DroppingMethod"));
				fm.setFinCategory(rs.getString("FinCategory"));
				fm.setProductCategory(rs.getString("ProductCategory"));
				fm.setBpiPftDaysBasis(rs.getString("BpiPftDaysBasis"));
				fm.setFinCcy(rs.getString("FinCcy"));
				fm.setAdvTerms(rs.getInt("AdvTerms"));
				fm.setAdvStage(rs.getString("AdvStage"));
				fm.setAdvType(rs.getString("AdvType"));
				fm.setAdvanceEMI(rs.getBigDecimal("AdvanceEMI"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setLastRepayRvwDate(rs.getDate("LastRepayRvwDate"));
				fm.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
				fm.setDueBucket(rs.getInt("DueBucket"));
				fm.setTdsType(rs.getString("TdsType"));
				fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
				return fm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	// FIXME to PreparedStatement
	@Override
	public void updateEHFinanceMain(FinanceMain financeMain) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FinanceMain");
		sql.append(" Set NumberOfTerms = :NumberOfTerms,GraceTerms=:GraceTerms, ");
		sql.append(" GrcPeriodEndDate = :GrcPeriodEndDate, NextGrcPftDate = :NextGrcPftDate,");
		sql.append(" NextGrcPftRvwDate = :NextGrcPftRvwDate, NextGrcCpzDate = :NextGrcCpzDate,");
		sql.append(" NextRepayDate = :NextRepayDate, NextRepayPftDate = :NextRepayPftDate,");
		sql.append(" NextRepayRvwDate = :NextRepayRvwDate, NextRepayCpzDate = :NextRepayCpzDate, ");
		sql.append(" MaturityDate = :MaturityDate, TotalProfit = :TotalProfit,TotalCpz= :TotalCpz, ");
		sql.append(" TotalGrossPft = :TotalGrossPft, TotalGracePft= :TotalGracePft, TotalGraceCpz= :TotalGraceCpz, ");
		sql.append(" TotalGrossGrcPft= :TotalGrossGrcPft, TotalRepayAmt= :TotalRepayAmt, ");
		sql.append(" CalTerms = :CalTerms, CalMaturity = :CalMaturity, FirstRepay = :FirstRepay, ");
		sql.append(" LastRepay = :LastRepay, ");
		sql.append(" LastRepayDate= :LastRepayDate,LastRepayPftDate= :LastRepayPftDate,");
		sql.append(" LastRepayRvwDate= :LastRepayRvwDate, LastRepayCpzDate= :LastRepayCpzDate, ");
		sql.append(" AnualizedPercRate =:AnualizedPercRate , EffectiveRateOfReturn =:EffectiveRateOfReturn , ");
		sql.append(" ScheduleMaintained = :ScheduleMaintained, ScheduleRegenerated = :ScheduleRegenerated, ");
		sql.append(" MaxUnplannedEmi=:MaxUnplannedEmi, AvailedUnPlanEmi=:AvailedUnPlanEmi, TdsType=:TdsType");
		sql.append(", Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(" where FinID = :FinID");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeMain);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinanceMain getFinBasicDetails(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.CustID, fm.FinCcy, fm.FinBranch, fm.FinType, fm.ScheduleMethod");
		sql.append(", fm.ProfitDaysBasis, fm.GrcPeriodEndDate, fm.AllowGrcPeriod, fm.ProductCategory, fm.FinCategory");
		sql.append(", cu.CustCIF, cu.CustShrtName, ClosingStatus");
		sql.append(", ft.FinDivision, UnderSettlement, RcdMaintainSts");
		sql.append(" From FinanceMain").append(type).append(" fm");
		sql.append(" Inner Join RMTFinanceTypes ft On ft.FinType = fm.FinType");
		sql.append(" Inner Join Customers cu ON cu.CustID = fm.CustID");
		sql.append(" Where fm.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setFinCcy(rs.getString("FinCcy"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setFinType(rs.getString("FinType"));
				fm.setScheduleMethod(rs.getString("ScheduleMethod"));
				fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
				fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
				fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
				fm.setProductCategory(rs.getString("ProductCategory"));
				fm.setFinCategory(rs.getString("FinCategory"));
				fm.setLovDescCustCIF(rs.getString("CustCIF"));
				fm.setLovDescCustShrtName(rs.getString("CustShrtName"));
				fm.setClosingStatus(rs.getString("ClosingStatus"));
				fm.setLovDescFinDivision(rs.getString("FinDivision"));
				fm.setUnderSettlement(rs.getBoolean("UnderSettlement"));
				fm.setRcdMaintainSts(rs.getString("RcdMaintainSts"));

				return fm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateDeductFeeDisb(FinanceMain fm, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(tableType.getSuffix());
		sql.append(" set DeductFeeDisb = ?, LastMntOn = ?");
		sql.append(" Where FinID = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, fm.getDeductFeeDisb());
			ps.setTimestamp(index++, fm.getLastMntOn());
			ps.setLong(index++, fm.getFinID());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index, fm.getPrevMntOn());
			} else {
				ps.setInt(index, fm.getVersion());
			}

		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinanceMain getFinanceMain(long finID, String[] columns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserPendingCases> getUserPendingCasesDetails(String userLogin, String roleCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getClosedDate(long finID) {
		String sql = "Select ClosedDate From FinanceMain Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Date.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateTdsApplicable(FinanceMain fm) {
		String sql = "Update FinanceMain Set TDSApplicable = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, fm.isTDSApplicable(), fm.getFinID());

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public boolean isPmayApplicable(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Pmay From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Boolean.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public void updateRepaymentAmount(FinanceMain fm) {
		String sql = "Update FinanceMain Set FinRepaymentAmount = ?, FinIsActive = ?, ClosingStatus = ?, FinStatus = ?, FinStsReason = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, fm.getFinRepaymentAmount());
			ps.setBoolean(index++, fm.isFinIsActive());
			ps.setString(index++, fm.getClosingStatus());
			ps.setString(index++, fm.getFinStatus());
			ps.setString(index++, fm.getFinStsReason());
			ps.setLong(index, fm.getFinID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateRestructure(long finID, boolean restructure) {
		String sql = "Update FinanceMain Set Restructure = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setBoolean(1, restructure);
			ps.setLong(2, finID);
		});
	}

	@Override
	public Map<String, Object> getGSTDataMapForDealer(long dealerId) {
		String sql = "Select DealerProvince, DealerCountry From AMTVehicleDealer_Aview Where DealerId = ?";

		logger.debug(Literal.SQL + sql);

		Map<String, Object> map = new HashMap<>();

		jdbcOperations.query(sql, ps -> {
			ps.setLong(1, dealerId);
		}, (rs, i) -> {

			map.put("CustProvince", rs.getString("DealerProvince"));
			map.put("CustCountry", rs.getString("DealerCountry"));

			return map;
		});

		return map;
	}

	@Override
	public void updateWriteOffStatus(long finID, boolean writeoffLoan) {
		String sql = "Update FinanceMain Set WriteoffLoan = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setBoolean(1, writeoffLoan);
			ps.setLong(2, finID);
		});
	}

	@Override
	public void updateMaintainceStatus(long finID, String rcdMaintainSts) {
		String sql = "Update FinanceMain Set RcdMaintainSts = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, rcdMaintainSts);
			ps.setLong(index, finID);

		});
	}

	@Override
	public void updateMaintainceStatus(String finReference, String rcdMaintainSts) {
		String sql = "Update FinanceMain Set RcdMaintainSts = ? Where FinReference = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, rcdMaintainSts);
			ps.setString(index, finReference);

		});
	}

	@Override
	public String getFinCategory(String finReference) {
		String sql = "Select FinCategory From FinanceMain Where FinReference = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public List<Long> getChildFinRefByParentRef(String parentRef) {
		String sql = "Select FinID From FinanceMain_Temp Where ParentRef = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.query(sql, ps -> {
			ps.setString(1, parentRef);
		}, (rs, rowNum) -> {
			return rs.getLong(1);
		});
	}

	@Override
	public void updateChildFinance(List<FinanceMain> list, String type) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FinanceMain");
		sql.append(type);
		sql.append(" Set FinIsActive = ?, ClosedDate = ?, ClosingStatus = ?");
		sql.append(", RcdMaintainSts = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, WorkflowId = ?, RecordType = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql);

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceMain fm = list.get(i);

				int index = 1;

				ps.setBoolean(index++, fm.isFinIsActive());
				ps.setDate(index++, JdbcUtil.getDate(fm.getClosedDate()));
				ps.setString(index++, fm.getClosingStatus());
				ps.setString(index++, fm.getRcdMaintainSts());
				ps.setString(index++, fm.getRoleCode());
				ps.setString(index++, fm.getNextRoleCode());
				ps.setString(index++, fm.getTaskId());
				ps.setString(index++, fm.getNextTaskId());
				ps.setLong(index++, fm.getWorkflowId());
				ps.setString(index++, fm.getRecordType());
				ps.setLong(index, fm.getFinID());

			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});

	}

	@Override
	public void updateRejectFinanceMain(List<FinanceMain> list, String type) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FinanceMain");
		sql.append(type);
		sql.append(" Set FinIsActive = ?,  RecordStatus = ?,  NextRoleCode = ?");
		sql.append(", NextTaskId = ?,  approved = ?,  ProcessAttributes = ?");
		sql.append(" where FinID = ?");

		logger.debug(Literal.SQL + sql);

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceMain fm = list.get(i);

				int index = 1;

				ps.setBoolean(index++, fm.isFinIsActive());
				ps.setString(index++, fm.getRecordStatus());
				ps.setString(index++, fm.getNextRoleCode());
				ps.setString(index++, fm.getNextTaskId());
				ps.setString(index++, fm.getApproved());
				ps.setString(index++, fm.getProcessAttributes());
				ps.setLong(index, fm.getFinID());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});

	}

	@Override
	public void updateSchdVersion(FinanceMain fm, boolean isPresentment) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" FinanceMain set");
		sql.append(" SchdVersion = SchdVersion + ?");
		sql.append(" Where FinID = ?");

		if (!isPresentment) {
			sql.append(" and SchdVersion = ?");
		}

		logger.debug(Literal.SQL + sql);

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			ps.setInt(1, 1);
			ps.setLong(2, fm.getFinID());
			if (!isPresentment) {
				ps.setInt(3, fm.getSchdVersion());
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException(Labels.getLabel("FINSRV_Validation"));
		}
	}

	@Override
	public int getSchdVersion(long finID) {
		String sql = "Select SchdVersion From FinanceMain Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public FinanceMain getFinMainLinkedFinancesByFinRef(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinId, FinReference, CustshrtName, FinType, FinIsActive, EntityCode from (");
		sql.append(" Select F.FinId, F.FinReference, C.CustShrtName ");
		sql.append(",F.FinType, F.FinIsActive, SD.EntityCode");
		sql.append(" From FinanceMain_Temp f");
		sql.append(" Inner Join Customers c ON f.custid = c.custid");
		sql.append(" Inner Join RMTFinanceTypes ft ON ft.fintype = f.fintype");
		sql.append(" Inner Join SMTDivisionDetail sd ON ft.findivision = sd.divisioncode");
		sql.append(" UNION ALL");
		sql.append(" Select F.FinId, F.FinReference, C.CustShrtName ");
		sql.append(", F.FinType, F.FinIsActive, SD.EntityCode");
		sql.append(" From FinanceMain f ");
		sql.append(" Inner Join Customers c ON f.custid = c.custid");
		sql.append(" Inner Join RMTFinanceTypes ft ON ft.fintype = f.fintype");
		sql.append(" Inner Join SMTDivisionDetail sd ON ft.findivision = sd.divisioncode");
		sql.append(" Where not (exists ( SELECT 1  FROM financemain_temp WHERE financemain_temp.finID = f.finID))");
		sql.append(") t Where FinId = ?");
		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinReference(rs.getString("FinReference"));
				fm.setLovDescCustShrtName(rs.getString("CustshrtName"));
				fm.setFinType(rs.getString("FinType"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setEntityCode(rs.getString("EntityCode"));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private String getBasicFieldsQuery(String tableType, boolean isFinReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, fm.FinType, fm.FinCategory, CustID, EntityCode, FinDivision");
		sql.append(", FinBranch, FinAmount, fm.FinCcy, FinPurpose, FinStartDate");
		sql.append(", fm.QuickDisb, FinAssetValue, FinCurrAssetValue");
		sql.append(", fm.FinIsActive, RcdMaintainSts, ClosingStatus, MaturityDate, CalMaturity");
		sql.append(", fm.RecordStatus, fm.RecordType, fm.RoleCode, fm.NextRoleCode, fm.WorkflowId");
		sql.append(" From FinanceMain").append(tableType).append(" fm");
		sql.append(" Inner Join RmtFinanceTypes ft On ft.FinType = fm.FinType");
		sql.append(" Inner Join SMTDivisionDetail dd On dd.DivisionCode = ft.FinDivision");

		if (isFinReference) {
			sql.append(" Where FinReference = ?");
		} else {
			sql.append(" Where FinID = ?");
		}
		return sql.toString();
	}

	private String getBasicFieldsQuery(TableType tableType, boolean isFinReference) {
		StringBuilder sql = new StringBuilder();

		switch (tableType) {
		case MAIN_TAB:
		case AVIEW:
			sql.append(getBasicFieldsQuery("", isFinReference));
			break;
		case TEMP_TAB:
		case TVIEW:
			sql.append(getBasicFieldsQuery("_Temp", isFinReference));
			break;
		case BOTH_TAB:
		case VIEW:
			sql.append("Select * From (");
			sql.append(getBasicFieldsQuery("_Temp", isFinReference));
			sql.append(" Union All ");
			sql.append(getBasicFieldsQuery("", isFinReference));
			sql.append(" and not exists (Select 1 From FinanceMain_Temp Where FinanceMain_Temp.FinID = fm.FinID)");
			sql.append(" ) fm");
			break;
		default:
			sql.append(getBasicFieldsQuery(tableType.getSuffix(), isFinReference));
			break;
		}

		return sql.toString();
	}

	@Override
	public FinanceMain getFinanceMain(String finReference) {
		String sql = getBasicFieldsQuery(TableType.BOTH_TAB, true);

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, new FinanceMainRM(), finReference, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinanceMain(String finReference, TableType tableType) {
		String sql = getBasicFieldsQuery(tableType, true);

		logger.debug(Literal.SQL + sql);

		Object[] parameters = null;
		if (tableType == TableType.VIEW || tableType == TableType.BOTH_TAB) {
			parameters = new Object[] { finReference, finReference };
		} else {
			parameters = new Object[] { finReference };
		}

		try {
			return this.jdbcOperations.queryForObject(sql, new FinanceMainRM(), parameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinanceMain(long finID) {
		String sql = getBasicFieldsQuery(TableType.BOTH_TAB, false);

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, new FinanceMainRM(), finID, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinanceMain(long finID, TableType tableType) {
		String sql = getBasicFieldsQuery(tableType, false);

		logger.debug(Literal.SQL + sql);

		Object[] parameters = null;
		if (tableType == TableType.VIEW || tableType == TableType.BOTH_TAB) {
			parameters = new Object[] { finID, finID };
		} else {
			parameters = new Object[] { finID };
		}

		try {
			return this.jdbcOperations.queryForObject(sql, new FinanceMainRM(), parameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public Long getFinID(String finReference) {
		StringBuilder sql = new StringBuilder("Select FinID From (");
		sql.append(" Select FinID From FinanceMain_Temp Where FinReference = ?");
		sql.append(" Union All");
		sql.append(" Select FinID From FinanceMain Where FinReference = ?");
		sql.append(" and not exists (Select 1 From FinanceMain_Temp Where FinID = FinanceMain.FinID)");
		sql.append(" ) fm");

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, finReference, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public Long getActiveFinID(String finReference) {
		StringBuilder sql = new StringBuilder("Select FinID From (");
		sql.append(" Select FinID From FinanceMain_Temp fm Where FinReference = ? and FinIsActive = ?");
		sql.append(" Union All");
		sql.append(" Select FinID From FinanceMain fm Where FinReference = ? and FinIsActive = ?");
		sql.append(" and not exists (Select 1 From FinanceMain_Temp Where FinID = fm.FinID)");
		sql.append(" ) fm");

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, finReference, 1, finReference, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public Long getFinID(String finReference, TableType tableType) {
		Object[] object = new Object[] { finReference };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
		case AVIEW:
			sql.append(" Select FinID From FinanceMain fm Where FinReference = ?");
			break;
		case TEMP_TAB:
		case TVIEW:
			sql.append(" Select FinID From FinanceMain_Temp fm Where FinReference = ?");
			break;
		case BOTH_TAB:
		case VIEW:
			object = new Object[] { finReference, finReference };

			sql.append("Select FinID From (");
			sql.append(" Select FinID From FinanceMain_Temp fm Where FinReference = ?");
			sql.append(" Union All");
			sql.append(" Select FinID From FinanceMain fm Where FinReference = ?");
			sql.append(" and not exists (Select 1 From FinanceMain_Temp Where FinID = fm.FinID)");
			sql.append(" ) fm");
			break;
		default:
			break;
		}

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, object);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public FinanceMain getBasicDetails(String finReference, TableType tableType) {
		Object[] object = new Object[] { finReference };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select FinID, CustID, MaturityDate");
			sql.append(" From FinanceMain fm Where FinReference = ?");
			break;
		case TEMP_TAB:
			sql.append(" Select FinID, CustID, MaturityDate");
			sql.append("  From FinanceMain_Temp fm Where FinReference = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { finReference, finReference };

			sql.append("Select FinID, CustID, MaturityDate From (");
			sql.append(" Select FinID, CustID, MaturityDate");
			sql.append("  From FinanceMain_Temp fm Where FinReference = ?");
			sql.append(" Union All");
			sql.append(" Select FinID, CustID, MaturityDate");
			sql.append("  From FinanceMain fm Where FinReference = ?");
			sql.append(" and not exists (Select 1 From FinanceMain_Temp Where FinID = fm.FinID)");
			sql.append(" ) fm");
			break;
		default:
			break;
		}

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();
				fm.setFinID(rs.getLong("FinID"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));

				return fm;
			}, object);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public Long getActiveFinID(String finReference, TableType tableType) {
		Object[] object = new Object[] { finReference, 1 };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
		case AVIEW:
			sql.append(" Select FinID From FinanceMain fm Where FinReference = ? and FinIsActive = ?");
			break;
		case TEMP_TAB:
		case TVIEW:
			sql.append(" Select FinID From FinanceMain_Temp fm Where FinReference = ? and FinIsActive = ?");
			break;
		case BOTH_TAB:
		case VIEW:
			object = new Object[] { finReference, 1, finReference, 1 };

			sql.append("Select FinID From (");
			sql.append(" Select FinID From FinanceMain_Temp fm Where FinReference = ? and FinIsActive = ?");
			sql.append(" Union All");
			sql.append(" Select FinID From FinanceMain fm Where FinReference = ? and FinIsActive = ?");
			sql.append(" and not exists (Select 1 From FinanceMain_Temp Where FinID = fm.FinID)");
			sql.append(" ) fm");
			break;
		default:
			break;
		}

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, object);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public Long getActiveWIFFinID(String finReference, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID From WIFFinanceMain").append(tableType.getSuffix());
		sql.append(" Where FinReference = ? and FinIsActive = ?");

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, finReference, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private class FinanceMainRM implements RowMapper<FinanceMain> {
		private FinanceMainRM() {
			super();
		}

		@Override
		public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinCategory(rs.getString("FinCategory"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setEntityCode(rs.getString("EntityCode"));
			fm.setLovDescEntityCode(rs.getString("EntityCode"));
			fm.setLovDescFinDivision(rs.getString("FinDivision"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setFinPurpose(rs.getString("FinPurpose"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));
			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setCalMaturity(rs.getDate("CalMaturity"));
			fm.setQuickDisb(rs.getBoolean("QuickDisb"));
			fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setRecordStatus(rs.getString("RecordStatus"));
			fm.setRecordType(rs.getString("RecordType"));
			fm.setRcdMaintainSts(rs.getString("RcdMaintainSts"));
			fm.setRoleCode(rs.getString("RoleCode"));
			fm.setNextRoleCode(rs.getString("NextRoleCode"));
			fm.setCustID(rs.getLong("WorkflowId"));

			return fm;
		}

	}

	@Override
	public FinanceStatusEnquiry getLoanStatusDetailsByFinReference(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FM.FinID, FM.FinReference, FP.CurODDays, FP.TotalPriBal, FM.ClosingStatus");
		sql.append(", FP.TotalPriBal, FP.ODPrincipal, FP.ODPrincipal, FP.ODProfit");
		sql.append(", FP.LPPTilllBD, FP.BounceAMt, FP.ReceivableAdvise");
		sql.append(", FP.ExcessAmt, FP.MaturityDate, FP.PenaltyDue");
		sql.append(" From FinanceMain FM");
		sql.append(" Inner Join FinPftDetails FP ON FM.FinID = FP.FinID");
		sql.append(" Where FM.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rownum) -> {
				FinanceStatusEnquiry fse = new FinanceStatusEnquiry();

				fse.setFinReference(rs.getString("FinReference"));
				fse.setCurODDays(rs.getInt("CurODDays"));
				fse.setOutStandPrincipal(rs.getBigDecimal("TotalPriBal"));
				fse.setClosingStatus(rs.getString("ClosingStatus"));
				fse.setPos(rs.getBigDecimal("TotalPriBal").add(rs.getBigDecimal("ODPrincipal")));
				fse.setOdprincipal(rs.getBigDecimal("ODPrincipal"));
				fse.setOdprofit(rs.getBigDecimal("ODProfit"));
				fse.setTotChagrOved(getTotalCharges(rs));
				fse.setExcessAmt(rs.getBigDecimal("ExcessAmt"));
				fse.setMaturityDate(rs.getDate("MaturityDate"));
				fse.setPenaltyDue(rs.getBigDecimal("PenaltyDue"));

				return fse;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private BigDecimal getTotalCharges(ResultSet rs) throws SQLException {
		BigDecimal lppTillLastBusinessDate = rs.getBigDecimal("LPPTilllBD");
		BigDecimal bounceAmt = rs.getBigDecimal("BounceAMt");
		BigDecimal receivableAdvise = rs.getBigDecimal("ReceivableAdvise");

		if (lppTillLastBusinessDate == null) {
			lppTillLastBusinessDate = BigDecimal.ZERO;
		}

		if (bounceAmt == null) {
			bounceAmt = BigDecimal.ZERO;
		}

		if (receivableAdvise == null) {
			receivableAdvise = BigDecimal.ZERO;
		}

		return lppTillLastBusinessDate.add(bounceAmt).add(receivableAdvise);
	}

	@Override
	public String getFinCategoryByFinType(String finType) {
		String sql = "Select FinCategory From RMTFinanceTypes Where FinType = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, String.class, finType);
	}

	@Override
	public int getCustomerBankCountById(Long bankId, long custId) {
		String sql = "Select Count(BankId) From CustomerBankInfo Where BankId = ? and CustId = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> rs.getInt(1), bankId, custId);
		} catch (EmptyResultDataAccessException dae) {
			return 0;
		}

	}

	@Override
	public FinanceMain getRejectFinanceMainByRef(String finReference) {
		String sql = "Select FinID, FinReference, TaskId, RoleCode, RecordType From RejectFinanceMain Where  FinReference = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setTaskId(rs.getString("TaskId"));
				fm.setRoleCode(rs.getString("RoleCode"));
				fm.setRecordType(rs.getString("RecordType"));

				return fm;
			}, finReference);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinanceMainForAdviseUpload(String finReference) {
		String sql = "Select FinID, FinStartDate, FinIsActive, MaturityDate From FinanceMain Where FinReference = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(finReference);
				fm.setFinStartDate(JdbcUtil.getDate(rs.getDate("FinStartDate")));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setMaturityDate(JdbcUtil.getDate(rs.getDate("MaturityDate")));

				return fm;
			}, finReference);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinanceMainForLMSEvent(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  FinID, fm.FinReference, fm.FinID, fm.CustID, fm.FinBranch, fm.FinType, fm.FinAmount");
		sql.append(", fm.FinAssetValue, fm.FinCurrAssetValue, fm.DeductFeeDisb, fm.FeeChargeAmt");
		sql.append(", fm.FinStartDate, fm.FinCategory, fm.ApplicationNo, fm.ReferralId, fm.SalesDepartment");
		sql.append(", fm.RepayFrq, fm.MaturityDate, fm.ClosedDate");
		sql.append(", fm.GraceTerms, fm.NumberOfTerms, fm.GrcPeriodEndDate");
		sql.append(", fm.GrcPftFrq, fm.NextGrcCpzDate, fm.GrcCpzFrq, fm.GrcPftRvwFrq, fm.GraceBaseRate");
		sql.append(", fm.GraceSpecialRate, fm.GrcMargin, fm.GrcPftRate, fm.GrcSchdMthd, fm.RepayBaseRate");
		sql.append(", fm.RepaySpecialRate, fm.RepayMargin, fm.RepayProfitRate, fm.RepayPftFrq, fm.RepayCpzFrq");
		sql.append(", fm.ReqRepayAmount, fm.ScheduleMethod, fm.ProfitDaysBasis, fm.FinRepayMethod, fm.MandateID");
		sql.append(", fm.BpiTreatment, fm.BpiAmount, fm.NoOfSteps, fm.StepType, fm.ReqMaturity, fm.CalTerms");
		sql.append(", fm.CalMaturity, fm.ClosingStatus, fm.FinPurpose, fm.CustDSR, fm.JointCustId");
		sql.append(", fm.Version, fm.LastMntBy, fm.LastMntOn, fm.InitiateUser, fm.InitiateDate, fm.BusinessVertical");
		sql.append(", fm.AllowGrcPftRvw, fm.AllowGrcCpz, fm.RepayRvwFrq, fm.BpiPftDaysBasis, fm.FinCcy, fm.RpyMinRate");
		sql.append(", fm.CalRoundingMode, fm.RoundingTarget, fm.FinRepaymentAmount, fm.TotalRepayAmt, fm.FirstRepay");
		sql.append(", fm.TotalGracePft, fm.TotalProfit, fm.AdvanceEMI, fm.RecalType, fm.PlanEMIHMethod");
		sql.append(", fm.NextGrcPftRvwDate, fm.GrcProfitDaysBasis, fm.ProductCategory, fm.DroplineFrq");
		sql.append(", fm.DownPayment, fm.NextUserId, fm.NextTaskId, fm.ParentRef, fm.AdvType, fm.DownPayBank");
		sql.append(", fm.TotalGrossPft, fm.RepayRateBasis, fm.AllowGrcPeriod, fm.GrcMaxAmount, fm.PlanEMIHMaxPerYear");
		sql.append(", fm.PlanEMIHMax, fm.PlanEMIHLockPeriod, fm.GrcMinRate, fm.SanBsdSchdle, fm.LastRepayDate");
		sql.append(", fm.FinOcrRequired, fm.LastRepayCpzDate, fm.LastRepayPftDate, fm.LastRepayRvwDate");
		sql.append(", fm.NextGrcPftDate, fm.NextRepayPftDate, fm.AllowRepayRvw, fm.AdvTerms, fm.TotalGraceCpz");
		sql.append(", fm.TotalCpz, fm.StepFinance, fm.RvwRateApplFor, fm.FixedRateTenor, fm.NextRepayDate");
		sql.append(", fm.FixedTenorRate, fm.GrcRateBasis, fm.AllowRepayCpz, fm.ScheduleMaintained");
		sql.append(", fm.TotalGrossGrcPft, fm.AdvStage, fm.FirstDroplineDate, fm.QuickDisb, fm.AllowGrcRepay");
		sql.append(", fm.TDSApplicable, fm.AlwBPI, fm.AlwManualSteps, fm.FinIsActive, fm.ScheduleRegenerated");
		sql.append(", fm.JointAccount, fm.AlwMultiDisb, fm.AllowSubvention, fm.CpzAtGraceEnd");
		sql.append(", fm.FinRepayPftOnFrq, fm.PlanEMIHAlw, fm.DeviationApproval, fm.PlanEMICpz");
		sql.append(", fm.PlanEMIHAlwInGrace, fm.FinIsRateRvwAtGrcEnd, fm.ManualSchedule, fm.ReAgeCpz, fm.UnPlanEMICpz");
		sql.append(", fm.RecordType, fm.FinContractDate, fm.FinApprovedDate, fm.FinCommitmentRef, fm.SvAmount");
		sql.append(", fm.CbAmount, fm.FinStatus, fm.FinStsReason, fm.PastduePftCalMthd, fm.PastduePftMargin");
		sql.append(", fm.EligibilityMethod, fm.PromotionCode, fm.SchdVersion");
		sql.append(", e.EntityCode, fm.DmaCode, fm.MigratedFinance, fm.MaxReAgeHolidays");
		sql.append(", fm.LinkedFinRef, fm.WifReference, fm.UnPlanEMIHLockPeriod, fm.NextRepayCpzDate");
		sql.append(", fm.Priority, fm.PlanDeferCount, fm.Connector, fm.AllowedDefFrqChange, fm.RecordStatus");
		sql.append(", fm.RpyMaxRate, fm.AllowedDefRpyChange, fm.FinLimitRef, fm.SamplingRequired, fm.TdsType");
		sql.append(", fm.FinSourceID, fm.RcdMaintainSts, fm.MaxUnplannedEmi, fm.AvailedUnPlanEmi, fm.TaskId");
		sql.append(", fm.FinRemarks, fm.AnualizedPercRate, fm.OverrideLimit, fm.DsaCode");
		sql.append(", fm.NextRepayRvwDate, fm.SchCalOnRvw, fm.Defferments, fm.StepPolicy, fm.WorkflowId");
		sql.append(", fm.AccountsOfficer, fm.MinDownPayPerc, fm.GrcMaxRate, fm.WriteoffLoan");
		sql.append(", fm.InvestmentRef, fm.FinPreApprovedRef, fm.LastRepay, fm.EmployeeName, fm.EffectiveRateOfReturn");
		sql.append(", fm.RoleCode, fm.DownPaySupl, fm.RateChgAnyDay, fm.AvailedReAgeH, fm.NextRoleCode, fm.LimitValid");
		sql.append(", fm.AvailedDefRpyChange, fm.DroppingMethod, fm.PftServicingODLimit");
		sql.append(", fm.ReAgeBucket, fm.AvailedDefFrqChange, fm.LegalRequired, ft.FinTypeDesc, b.BranchDesc");
		sql.append(", fm.UnderSettlement, fm.UnderNpa");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join RMTFinanceTypes ft On fm.FinType = ft.FinType");
		sql.append(" Inner Join Customers c On fm.CustID = c.CustID");
		sql.append(" Inner Join SmtDivisionDetail d On d.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e on e.EntityCode = d.EntityCode");
		sql.append(" Inner Join RMTBranches b on b.BranchCode = fm.FinBranch");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setFinType(rs.getString("FinType"));
				fm.setFinAmount(rs.getBigDecimal("FinAmount"));
				fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
				fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
				fm.setDeductFeeDisb(rs.getBigDecimal("DeductFeeDisb"));
				fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setFinCategory(rs.getString("FinCategory"));
				fm.setApplicationNo(rs.getString("ApplicationNo"));
				fm.setReferralId(rs.getString("ReferralId"));
				fm.setSalesDepartment(rs.getString("SalesDepartment"));
				fm.setRepayFrq(rs.getString("RepayFrq"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setClosedDate(rs.getDate("ClosedDate"));
				fm.setGraceTerms(rs.getInt("GraceTerms"));
				fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
				fm.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
				fm.setGrcPftFrq(rs.getString("GrcPftFrq"));
				fm.setNextGrcCpzDate(rs.getDate("NextGrcCpzDate"));
				fm.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
				fm.setGrcPftRvwFrq(rs.getString("GrcPftRvwFrq"));
				fm.setGraceBaseRate(rs.getString("GraceBaseRate"));
				fm.setGraceSpecialRate(rs.getString("GraceSpecialRate"));
				fm.setGrcMargin(rs.getBigDecimal("GrcMargin"));
				fm.setGrcPftRate(rs.getBigDecimal("GrcPftRate"));
				fm.setGrcSchdMthd(rs.getString("GrcSchdMthd"));
				fm.setRepayBaseRate(rs.getString("RepayBaseRate"));
				fm.setRepaySpecialRate(rs.getString("RepaySpecialRate"));
				fm.setRepayMargin(rs.getBigDecimal("RepayMargin"));
				fm.setRepayProfitRate(rs.getBigDecimal("RepayProfitRate"));
				fm.setRepayPftFrq(rs.getString("RepayPftFrq"));
				fm.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
				fm.setReqRepayAmount(rs.getBigDecimal("ReqRepayAmount"));
				fm.setScheduleMethod(rs.getString("ScheduleMethod"));
				fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
				fm.setFinRepayMethod(rs.getString("FinRepayMethod"));
				fm.setMandateID(rs.getLong("MandateID"));
				fm.setBpiTreatment(rs.getString("BpiTreatment"));
				fm.setBpiAmount(rs.getBigDecimal("BpiAmount"));
				fm.setNoOfSteps(rs.getInt("NoOfSteps"));
				fm.setStepType(rs.getString("StepType"));
				fm.setReqMaturity(rs.getDate("ReqMaturity"));
				fm.setCalTerms(rs.getInt("CalTerms"));
				fm.setCalMaturity(rs.getDate("CalMaturity"));
				fm.setClosingStatus(rs.getString("ClosingStatus"));
				fm.setFinPurpose(rs.getString("FinPurpose"));
				fm.setCustDSR(rs.getBigDecimal("CustDSR"));
				fm.setJointCustId(rs.getLong("JointCustId"));
				fm.setVersion(rs.getInt("Version"));
				fm.setLastMntBy(rs.getLong("LastMntBy"));
				fm.setLastMntOn(rs.getTimestamp("LastMntOn"));
				fm.setInitiateUser(rs.getLong("InitiateUser"));
				fm.setInitiateDate(rs.getDate("InitiateDate"));
				fm.setBusinessVertical(rs.getLong("BusinessVertical"));
				fm.setAllowGrcPftRvw(rs.getBoolean("AllowGrcPftRvw"));
				fm.setAllowGrcCpz(rs.getBoolean("AllowGrcCpz"));
				fm.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
				fm.setBpiPftDaysBasis(rs.getString("BpiPftDaysBasis"));
				fm.setFinCcy(rs.getString("FinCcy"));
				fm.setRpyMinRate(rs.getBigDecimal("RpyMinRate"));
				fm.setCalRoundingMode(rs.getString("CalRoundingMode"));
				fm.setRoundingTarget(rs.getInt("RoundingTarget"));
				fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
				fm.setTotalRepayAmt(rs.getBigDecimal("TotalRepayAmt"));
				fm.setFirstRepay(rs.getBigDecimal("FirstRepay"));
				fm.setTotalGracePft(rs.getBigDecimal("TotalGracePft"));
				fm.setTotalProfit(rs.getBigDecimal("TotalProfit"));
				fm.setAdvanceEMI(rs.getBigDecimal("AdvanceEMI"));
				fm.setRecalType(rs.getString("RecalType"));
				fm.setPlanEMIHMethod(rs.getString("PlanEMIHMethod"));
				fm.setNextGrcPftRvwDate(rs.getDate("NextGrcPftRvwDate"));
				fm.setGrcProfitDaysBasis(rs.getString("GrcProfitDaysBasis"));
				fm.setProductCategory(rs.getString("ProductCategory"));
				fm.setDroplineFrq(rs.getString("DroplineFrq"));
				fm.setDownPayment(rs.getBigDecimal("DownPayment"));
				fm.setNextUserId(rs.getString("NextUserId"));
				fm.setNextTaskId(rs.getString("NextTaskId"));
				fm.setParentRef(rs.getString("ParentRef"));
				fm.setAdvType(rs.getString("AdvType"));
				fm.setDownPayBank(rs.getBigDecimal("DownPayBank"));
				fm.setTotalGrossPft(rs.getBigDecimal("TotalGrossPft"));
				fm.setRepayRateBasis(rs.getString("RepayRateBasis"));
				fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
				fm.setGrcMaxAmount(rs.getBigDecimal("GrcMaxAmount"));
				fm.setPlanEMIHMaxPerYear(rs.getInt("PlanEMIHMaxPerYear"));
				fm.setPlanEMIHMax(rs.getInt("PlanEMIHMax"));
				fm.setPlanEMIHLockPeriod(rs.getInt("PlanEMIHLockPeriod"));
				fm.setGrcMinRate(rs.getBigDecimal("GrcMinRate"));
				fm.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
				fm.setLastRepayDate(rs.getDate("LastRepayDate"));
				fm.setFinOcrRequired(rs.getBoolean("FinOcrRequired"));
				fm.setLastRepayCpzDate(rs.getDate("LastRepayCpzDate"));
				fm.setLastRepayPftDate(rs.getDate("LastRepayPftDate"));
				fm.setLastRepayRvwDate(rs.getDate("LastRepayRvwDate"));
				fm.setNextGrcPftDate(rs.getDate("NextGrcPftDate"));
				fm.setNextRepayPftDate(rs.getDate("NextRepayPftDate"));
				fm.setAllowRepayRvw(rs.getBoolean("AllowRepayRvw"));
				fm.setAdvTerms(rs.getInt("AdvTerms"));
				fm.setTotalGraceCpz(rs.getBigDecimal("TotalGraceCpz"));
				fm.setTotalCpz(rs.getBigDecimal("TotalCpz"));
				fm.setStepFinance(rs.getBoolean("StepFinance"));
				fm.setRvwRateApplFor(rs.getString("RvwRateApplFor"));
				fm.setFixedRateTenor(rs.getInt("FixedRateTenor"));
				fm.setNextRepayDate(rs.getDate("NextRepayDate"));
				fm.setFixedTenorRate(rs.getBigDecimal("FixedTenorRate"));
				fm.setGrcRateBasis(rs.getString("GrcRateBasis"));
				fm.setAllowRepayCpz(rs.getBoolean("AllowRepayCpz"));
				fm.setScheduleMaintained(rs.getBoolean("ScheduleMaintained"));
				fm.setTotalGrossGrcPft(rs.getBigDecimal("TotalGrossGrcPft"));
				fm.setAdvStage(rs.getString("AdvStage"));
				fm.setFirstDroplineDate(rs.getDate("FirstDroplineDate"));
				fm.setQuickDisb(rs.getBoolean("QuickDisb"));
				fm.setAllowGrcRepay(rs.getBoolean("AllowGrcRepay"));
				fm.setTDSApplicable(rs.getBoolean("TDSApplicable"));
				fm.setAlwBPI(rs.getBoolean("AlwBPI"));
				fm.setAlwManualSteps(rs.getBoolean("AlwManualSteps"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setScheduleRegenerated(rs.getBoolean("ScheduleRegenerated"));
				fm.setJointAccount(rs.getBoolean("JointAccount"));
				fm.setAlwMultiDisb(rs.getBoolean("AlwMultiDisb"));
				fm.setAllowSubvention(rs.getBoolean("AllowSubvention"));
				fm.setCpzAtGraceEnd(rs.getBoolean("CpzAtGraceEnd"));
				fm.setFinRepayPftOnFrq(rs.getBoolean("FinRepayPftOnFrq"));
				fm.setPlanEMIHAlw(rs.getBoolean("PlanEMIHAlw"));
				fm.setDeviationApproval(rs.getBoolean("DeviationApproval"));
				fm.setPlanEMICpz(rs.getBoolean("PlanEMICpz"));
				fm.setPlanEMIHAlwInGrace(rs.getBoolean("PlanEMIHAlwInGrace"));
				fm.setFinIsRateRvwAtGrcEnd(rs.getBoolean("FinIsRateRvwAtGrcEnd"));
				fm.setManualSchedule(rs.getBoolean("ManualSchedule"));
				fm.setReAgeCpz(rs.getBoolean("ReAgeCpz"));
				fm.setUnPlanEMICpz(rs.getBoolean("UnPlanEMICpz"));
				fm.setRecordType(rs.getString("RecordType"));
				fm.setFinContractDate(rs.getDate("FinContractDate"));
				fm.setFinApprovedDate(rs.getDate("FinApprovedDate"));
				fm.setFinCommitmentRef(rs.getString("FinCommitmentRef"));
				fm.setSvAmount(rs.getBigDecimal("SvAmount"));
				fm.setCbAmount(rs.getBigDecimal("CbAmount"));
				fm.setFinStatus(rs.getString("FinStatus"));
				fm.setFinStsReason(rs.getString("FinStsReason"));
				fm.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
				fm.setPastduePftMargin(rs.getBigDecimal("PastduePftMargin"));
				fm.setEligibilityMethod(rs.getLong("EligibilityMethod"));
				fm.setPromotionCode(rs.getString("PromotionCode"));
				fm.setSchdVersion(rs.getInt("SchdVersion"));
				fm.setEntityCode(rs.getString("EntityCode"));
				fm.setLovDescEntityCode(rs.getString("EntityCode"));
				fm.setDmaCode(rs.getString("DmaCode"));
				fm.setMigratedFinance(rs.getBoolean("MigratedFinance"));
				fm.setMaxReAgeHolidays(rs.getInt("MaxReAgeHolidays"));
				fm.setLinkedFinRef(rs.getString("LinkedFinRef"));
				fm.setWifReference(rs.getString("WifReference"));
				fm.setUnPlanEMIHLockPeriod(rs.getInt("UnPlanEMIHLockPeriod"));
				fm.setNextRepayCpzDate(rs.getDate("NextRepayCpzDate"));
				fm.setPriority(rs.getInt("Priority"));
				fm.setPlanDeferCount(rs.getInt("PlanDeferCount"));
				fm.setConnector(rs.getLong("Connector"));
				fm.setAllowedDefFrqChange(rs.getInt("AllowedDefFrqChange"));
				fm.setRecordStatus(rs.getString("RecordStatus"));
				fm.setRpyMaxRate(rs.getBigDecimal("RpyMaxRate"));
				fm.setAllowedDefRpyChange(rs.getInt("AllowedDefRpyChange"));
				fm.setFinLimitRef(rs.getString("FinLimitRef"));
				fm.setSamplingRequired(rs.getBoolean("SamplingRequired"));
				fm.setTdsType(rs.getString("TdsType"));
				fm.setFinSourceID(rs.getString("FinSourceID"));
				fm.setRcdMaintainSts(rs.getString("RcdMaintainSts"));
				fm.setMaxUnplannedEmi(rs.getInt("MaxUnplannedEmi"));
				fm.setAvailedUnPlanEmi(rs.getInt("AvailedUnPlanEmi"));
				fm.setTaskId(rs.getString("TaskId"));
				fm.setFinRemarks(rs.getString("FinRemarks"));
				fm.setAnualizedPercRate(rs.getBigDecimal("AnualizedPercRate"));
				fm.setOverrideLimit(rs.getBoolean("OverrideLimit"));
				fm.setDsaCode(rs.getString("DsaCode"));
				fm.setNextRepayRvwDate(rs.getDate("NextRepayRvwDate"));
				fm.setSchCalOnRvw(rs.getString("SchCalOnRvw"));
				fm.setDefferments(rs.getInt("Defferments"));
				fm.setStepPolicy(rs.getString("StepPolicy"));
				fm.setWorkflowId(rs.getLong("WorkflowId"));
				fm.setAccountsOfficer(rs.getLong("AccountsOfficer"));
				fm.setMinDownPayPerc(rs.getBigDecimal("MinDownPayPerc"));
				fm.setGrcMaxRate(rs.getBigDecimal("GrcMaxRate"));
				fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
				fm.setInvestmentRef(rs.getString("InvestmentRef"));
				fm.setFinPreApprovedRef(rs.getString("FinPreApprovedRef"));
				fm.setLastRepay(rs.getBigDecimal("LastRepay"));
				fm.setEmployeeName(rs.getString("EmployeeName"));
				fm.setEffectiveRateOfReturn(rs.getBigDecimal("EffectiveRateOfReturn"));
				fm.setRoleCode(rs.getString("RoleCode"));
				fm.setDownPaySupl(rs.getBigDecimal("DownPaySupl"));
				fm.setRateChgAnyDay(rs.getBoolean("RateChgAnyDay"));
				fm.setAvailedReAgeH(rs.getInt("AvailedReAgeH"));
				fm.setNextRoleCode(rs.getString("NextRoleCode"));
				fm.setLimitValid(rs.getBoolean("LimitValid"));
				fm.setAvailedDefRpyChange(rs.getInt("AvailedDefRpyChange"));
				fm.setDroppingMethod(rs.getString("DroppingMethod"));
				fm.setPftServicingODLimit(rs.getBoolean("PftServicingODLimit"));
				fm.setReAgeBucket(rs.getInt("ReAgeBucket"));
				fm.setAvailedDefFrqChange(rs.getInt("AvailedDefFrqChange"));
				fm.setLegalRequired(rs.getBoolean("LegalRequired"));
				fm.setLovDescFinTypeName(rs.getString("FinTypeDesc"));
				fm.setLovDescFinBranchName(rs.getString("BranchDesc"));
				fm.setUnderSettlement(rs.getBoolean("UnderSettlement"));
				fm.setUnderNpa(rs.getBoolean("UnderNpa"));

				return fm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getLovDescFinDivisionByReference(String finReference) {
		StringBuilder sql = new StringBuilder("Select ft.FinDivision");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Where fm.FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), String.class, finReference);
	}

	@Override
	public Map<String, Object> getExtendedFields(String reference, String tableName) {
		StringBuilder sql = new StringBuilder("Select * from ");
		sql.append(tableName);
		sql.append(" where  Reference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForMap(sql.toString(), reference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new HashMap<>();
	}

	@Override
	public FinanceMain getFinanceMainByReference(String finReference, boolean active) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, PlanDeferCount, SvAmount, CbAmount, AllowedDefRpyChange, AvailedDefRpyChange");
		sql.append(", AllowedDefFrqChange, AvailedDefFrqChange, FinAmount, DownPayment, FinStartDate");
		sql.append(", PromotionSeqId, FinIsActive, PromotionCode, FinCategory, FinType, FinBranch, MandateId, CustID");
		sql.append(" From FinanceMain");
		sql.append(" Where FinReference = ? and FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinReference(rs.getString("FinReference"));
				fm.setPlanDeferCount(rs.getInt("PlanDeferCount"));
				fm.setSvAmount(rs.getBigDecimal("SvAmount"));
				fm.setCbAmount(rs.getBigDecimal("CbAmount"));
				fm.setAllowedDefRpyChange(rs.getInt("AllowedDefRpyChange"));
				fm.setAvailedDefRpyChange(rs.getInt("AvailedDefRpyChange"));
				fm.setAllowedDefFrqChange(rs.getInt("AllowedDefFrqChange"));
				fm.setAvailedDefFrqChange(rs.getInt("AvailedDefFrqChange"));
				fm.setFinAmount(rs.getBigDecimal("FinAmount"));
				fm.setDownPayment(rs.getBigDecimal("DownPayment"));
				fm.setFinStartDate(JdbcUtil.getDate(rs.getDate("FinStartDate")));
				fm.setPromotionSeqId(rs.getLong("PromotionSeqId"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setPromotionCode(rs.getString("PromotionCode"));
				fm.setFinCategory(rs.getString("FinCategory"));
				fm.setFinType(rs.getString("FinType"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setMandateID(rs.getLong("MandateId"));
				fm.setCustID(rs.getLong("CustID"));

				return fm;
			}, finReference, active);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isOverDraft(String finReference) {
		String sql = "Select Count(FinReference) From FinanceMain Where FinReference = ? and ProductCategory = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> rs.getInt(1), finReference,
					FinanceConstants.PRODUCT_ODFACILITY) > 0;
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}

	}

	@Override
	public String getEntityCodeByRef(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" sd.EntityCode");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join RMTFinanceTypes ft ON ft.FinType = fm.FinType");
		sql.append(" Inner Join SMTDivisionDetail sd ON sd.DivisionCode = ft.FinDivision");
		sql.append(" Where fm.FinReference = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				return rs.getString(1);
			}, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Long getCustomerIdByFinRef(String finReference) {
		String sql = "Select CustId from FinanceMain_view where FinReference = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public Long getCustomerIdByFinID(long finID) {
		String sql = "Select CustId from FinanceMain_view where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public void updateFinanceForGraceEndInEOD(FinanceMain financeMain) {
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update FinanceMain Set");
		updateSql.append(" GrcPeriodEndDate = :GrcPeriodEndDate, AllowGrcPeriod = :AllowGrcPeriod, ");
		updateSql.append("  GraceBaseRate = :GraceBaseRate, GraceSpecialRate = :GraceSpecialRate, ");
		updateSql.append("  GrcPftRate = :GrcPftRate, GrcPftFrq = :GrcPftFrq, ");
		updateSql.append("  NextGrcPftDate = :NextGrcPftDate, AllowGrcPftRvw = :AllowGrcPftRvw, ");
		updateSql.append("  GrcPftRvwFrq = :GrcPftRvwFrq, NextGrcPftRvwDate = :NextGrcPftRvwDate, ");
		updateSql.append("  AllowGrcCpz = :AllowGrcCpz, GrcCpzFrq = :GrcCpzFrq, ");
		updateSql.append("  NextGrcCpzDate = :NextGrcCpzDate, RepayBaseRate = :RepayBaseRate, ");
		updateSql.append("  RepaySpecialRate = :RepaySpecialRate, RepayProfitRate = :RepayProfitRate, ");
		updateSql.append("  RepayFrq = :RepayFrq, NextRepayDate = :NextRepayDate, ");
		updateSql.append("  RepayPftFrq = :RepayPftFrq, NextRepayPftDate = :NextRepayPftDate, ");
		updateSql.append("  AllowRepayRvw = :AllowRepayRvw, RepayRvwFrq = :RepayRvwFrq, ");
		updateSql.append("  NextRepayRvwDate = :NextRepayRvwDate, AllowRepayCpz = :AllowRepayCpz, ");
		updateSql.append("  RepayCpzFrq = :RepayCpzFrq, NextRepayCpzDate = :NextRepayCpzDate, ");
		updateSql.append("  MaturityDate = :MaturityDate, CpzAtGraceEnd = :CpzAtGraceEnd, ");
		updateSql.append("  GrcRateBasis = :GrcRateBasis, RepayRateBasis = :RepayRateBasis, ");
		updateSql.append("  FinType = :FinType, FinCcy = :FinCcy, ");
		updateSql.append("  ProfitDaysBasis = :ProfitDaysBasis, FirstRepay = :FirstRepay, ");
		updateSql.append("  LastRepay = :LastRepay, ScheduleMethod = :ScheduleMethod, ");
		updateSql.append("  FinStartDate = :FinStartDate, FinAmount = :FinAmount, ");
		updateSql.append("  CustID = :CustID, FinBranch = :FinBranch, ");
		updateSql.append("  FinSourceID = :FinSourceID, RecalType = :RecalType, ");
		updateSql.append("  FinIsActive = :FinIsActive, LastRepayDate = :LastRepayDate, ");
		updateSql.append("  LastRepayPftDate = :LastRepayPftDate, LastRepayRvwDate = :LastRepayRvwDate, ");
		updateSql.append("  LastRepayCpzDate = :LastRepayCpzDate, AllowGrcRepay = :AllowGrcRepay, ");
		updateSql.append("  GrcSchdMthd = :GrcSchdMthd, GrcMargin = :GrcMargin, ");
		updateSql.append("  RepayMargin = :RepayMargin, ClosingStatus = :ClosingStatus, ");
		updateSql.append("  FinRepayPftOnFrq = :FinRepayPftOnFrq, GrcProfitDaysBasis = :GrcProfitDaysBasis, ");
		updateSql.append("  GrcMinRate = :GrcMinRate, GrcMaxRate = :GrcMaxRate, ");
		updateSql.append("  GrcMaxAmount = :GrcMaxAmount, RpyMinRate = :RpyMinRate, ");
		updateSql.append("  RpyMaxRate = :RpyMaxRate, ManualSchedule = :ManualSchedule, ");
		updateSql.append("  CalRoundingMode = :CalRoundingMode, RoundingTarget = :RoundingTarget, ");
		updateSql.append("  RvwRateApplFor = :RvwRateApplFor, SchCalOnRvw = :SchCalOnRvw, ");
		updateSql.append("  PastduePftCalMthd = :PastduePftCalMthd, DroppingMethod = :DroppingMethod, ");
		updateSql.append("  RateChgAnyDay = :RateChgAnyDay, PastduePftMargin = :PastduePftMargin, ");
		updateSql.append("  FinRepayMethod = :FinRepayMethod, MigratedFinance = :MigratedFinance, ");
		updateSql.append("  ScheduleMaintained = :ScheduleMaintained, ScheduleRegenerated = :ScheduleRegenerated, ");
		updateSql.append("  MandateID = :MandateID, FinStatus = :FinStatus, ");
		updateSql.append("  DueBucket = :DueBucket, FinStsReason = :FinStsReason, ");
		updateSql.append("  PromotionCode = :PromotionCode, FinCategory = :FinCategory, ");
		updateSql.append("  ProductCategory = :ProductCategory, ReAgeBucket = :ReAgeBucket, ");
		updateSql.append("  TDSApplicable = :TDSApplicable, BpiTreatment = :BpiTreatment, ");
		updateSql.append("  FinRepaymentAmount = :FinRepaymentAmount, GrcAdvType = :GrcAdvType, ");
		updateSql.append("  AdvType = :AdvType, SanBsdSchdle = :SanBsdSchdle, ");
		updateSql.append("  PromotionSeqId = :PromotionSeqId, SvAmount = :SvAmount, ");
		updateSql.append("  CbAmount = :CbAmount, EmployeeName = :EmployeeName, FinAssetValue = :FinAssetValue, ");
		updateSql.append("  FinCurrAssetValue = :FinCurrAssetValue, AlwGrcAdj = :AlwGrcAdj, ");
		updateSql.append(
				"  EndGrcPeriodAftrFullDisb = :EndGrcPeriodAftrFullDisb, AutoIncGrcEndDate = :AutoIncGrcEndDate, ");
		updateSql.append("  Version = :Version, LastMntOn = :LastMntOn, ");
		updateSql.append("  AlwMultiDisb = :AlwMultiDisb, ");
		updateSql.append("  ReferralId = :ReferralId, GraceTerms = :GraceTerms, ");
		updateSql.append("  WriteoffLoan = :WriteoffLoan, SchdVersion = :SchdVersion, ");
		updateSql.append("  ManualSchdType = :ManualSchdType, NumberOfTerms = :NumberOfTerms, ");
		updateSql.append("  StepFinance = :StepFinance, AlwManualSteps = :AlwManualSteps, ");
		updateSql.append("  CalcOfSteps = :CalcOfSteps, NoOfGrcSteps = :NoOfGrcSteps, ");
		updateSql.append("  NoOfSteps = :NoOfSteps, StepsAppliedFor = :StepsAppliedFor ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<FinanceMain> getForFinanceExposer(long custId) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select MaturityDate, FinStartDate, FinCcy, TotalRepayAmt from (");
		sql.append(" Select CustId, MaturityDate, FinStartDate, FinCcy, TotalRepayAmt");
		sql.append(" From Financemain_Temp Where CustID = ?");
		sql.append(" Union all");
		sql.append(" Select CustId, MaturityDate, FinStartDate, FinCcy, TotalRepayAmt");
		sql.append(" From Financemain Where CustID = ? and FinIsActive = ?");
		sql.append(" and not exists (select 1 from Financemain_Temp where FinReference = Financemain.FinReference)");
		sql.append(") fm");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setMaturityDate(rs.getDate("MaturityDate"));
			fm.setFinStartDate(rs.getDate("FinStartDate"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setTotalRepayAmt(rs.getBigDecimal("TotalRepayAmt"));

			return fm;
		}, custId, custId, 1);
	}

	@Override
	public int getBucketByFinStatus(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" dc.DueDays");
		sql.append(" From DPDBucketsConfig dc");
		sql.append(" Inner Join DPDBuckets db on db.BucketID = dc.BucketID");
		sql.append(" Inner Join FinanceMain fm on fm.FinStatus = db.BucketCode");
		sql.append(" Where fm.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), Integer.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public List<FinanceMain> getFinanceMainActiveList(Date fromDate, Date toDate, String finType) {
		logger.debug(Literal.ENTERING);

		List<FinanceMain> finMains;
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference,CalRoundingMode,RoundingTarget,FinCcy,FinType,cs.CustShrtName lovDescCustShrtName");
		sql.append(" From FinanceMain fm");
		sql.append(" inner join customers cs on cs.custid = fm.custid");
		sql.append(
				" where FinisActive =:FinisActive and FinStartDate <=:FinStartDate  and MaturityDate >= :MaturityDate");
		if (finType != null) {
			sql.append(" and finType =:finType ");
		}
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinisActive", true);
		source.addValue("FinStartDate", fromDate);
		source.addValue("MaturityDate", toDate);
		if (finType != null) {
			source.addValue("finType", finType);
		}
		try {
			RowMapper<FinanceMain> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceMain.class);
			finMains = jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			return new ArrayList<>();
		}
		logger.debug(Literal.LEAVING);
		return finMains;
	}

	public String getOrgFinCategory(String finReference) {
		String sql = "Select FinCategory From FinanceMain_Temp Where FinReference = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinanceMain getFinanceMain(String finReference, String entity) {
		StringBuilder sql = new StringBuilder("Select fm.FinID, fm.FinReference, fm.FinIsActive, fm.CustId");
		sql.append(", fm.WriteoffLoan, fm.Fintype, fm.RcdMaintainSts, fm.MaturityDate ");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join RMTFinanceTypes ft On ft.FinType = fm.FinType");
		sql.append(" Inner Join SMTDivisionDetail dd On dd.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e On e.EntityCode = dd.EntityCode");
		sql.append(" Where fm.FinReference = ? and e.EntityCode = ?");

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setCustID(rs.getLong("CustId"));
				fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
				fm.setFinType(rs.getString("Fintype"));
				fm.setRcdMaintainSts(rs.getString("RcdMaintainSts"));
				fm.setMaturityDate(JdbcUtil.getDate(rs.getDate("MaturityDate")));

				return fm;
			}, finReference, entity);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Date getMaturityDate(String finReference) {
		String sql = "Select MaturityDate From FinanceMain Where FinReference = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Date.class, finReference);
	}

	@Override
	public FinanceMain getEntityByRef(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, fm.FinBranch, e.EntityCode, e.EntityDesc");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join RMTFinanceTypes ft On ft.FinType = fm.FinType");
		sql.append(" Inner Join SMTDivisionDetail dd On dd.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e On e.EntityCode = dd.EntityCode");
		sql.append(" Where fm.FinReference = ? ");

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setEntityCode(rs.getString("EntityCode"));
				fm.setEntityDesc(rs.getString("EntityDesc"));
				return fm;
			}, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<AutoRefundLoan> getAutoRefunds(CustEODEvent cee) {
		Date eodDate = cee.getEodDate();
		EventProperties ep = cee.getEventProperties();
		Customer customer = cee.getCustomer();
		StringBuilder sql = new StringBuilder("Select * From (");
		sql.append(" Select fm.FinID, fm.FinReference, ft.MaxAutoRefund, ft.MinAutoRefund");
		sql.append(", fm.FinRepayMethod, fm.FinIsActive, fpd.CurOdDays, fm.FinCcy, fm.WriteOffLoan");
		sql.append(", h.HoldStatus, fm.FinType, e.EntityCode");
		sql.append(" From Financemain fm");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join SmtDivisionDetail d On d.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e on e.EntityCode = d.EntityCode");
		sql.append(" Inner Join FinPftDetails fpd on fpd.FinID = fm.FinID");
		sql.append(" Left Join Fin_Hold_Details h on fm.FinID = h.FinID");
		sql.append(" Where fm.FinIsActive = ?");

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" and c.CustCoreBank = ? ");
		} else {
			sql.append(" and c.CustID = ? ");
		}
		sql.append(" and ft.AllowAutoRefund = ?");

		sql.append(" Union All");

		sql.append(" Select fm.FinID, fm.FinReference, ft.MaxAutoRefund, ft.MinAutoRefund");
		sql.append(", fm.FinRepayMethod, fm.FinIsActive, fpd.CurOdDays, fm.FinCcy, fm.WriteOffLoan");
		sql.append(", h.HoldStatus, fm.FinType, e.EntityCode");
		sql.append(" From Financemain fm");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join SmtDivisionDetail d On d.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e on e.EntityCode = d.EntityCode");
		sql.append(" Inner Join FinPftDetails fpd on fpd.FinID = fm.FinID");
		sql.append(" Left Join Fin_Hold_Details h on fm.FinID = h.FinID");
		sql.append(" Where fm.FinIsActive = ? and fm.ClosedDate <= ?");

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" and c.CustCoreBank = ? ");
		} else {
			sql.append(" and c.CustID = ? ");
		}
		sql.append(" and ft.AllowAutoRefund = ?");

		sql.append(" ) T");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setBoolean(++index, true);
			if (CustomerExtension.CUST_CORE_BANK_ID) {
				ps.setString(++index, customer.getCustCoreBank());
			} else {
				ps.setLong(++index, customer.getCustID());
			}
			ps.setBoolean(++index, true);

			ps.setBoolean(++index, false);
			ps.setDate(++index, JdbcUtil.getDate(DateUtil.addDays(eodDate, -ep.getAutoRefundDaysForClosed())));
			if (CustomerExtension.CUST_CORE_BANK_ID) {
				ps.setString(++index, customer.getCustCoreBank());
			} else {
				ps.setLong(++index, customer.getCustID());
			}
			ps.setBoolean(++index, true);

		}, (rs, rowNum) -> {
			AutoRefundLoan arl = new AutoRefundLoan();

			arl.setFinID(rs.getLong("FinID"));
			arl.setFinReference(rs.getString("FinReference"));
			arl.setMaxRefundAmt(rs.getBigDecimal("MaxAutoRefund"));
			arl.setMinRefundAmt(rs.getBigDecimal("MinAutoRefund"));
			arl.setFinRepayMethod(rs.getString("FinRepayMethod"));
			arl.setFinIsActive(rs.getBoolean("FinIsActive"));
			arl.setDpdDays(rs.getInt("CurOdDays"));
			arl.setFinCcy(rs.getString("FinCcy"));
			arl.setWriteOffLoan(rs.getBoolean("WriteOffLoan"));
			arl.setHoldStatus(rs.getString("HoldStatus"));
			arl.setFinType(rs.getString("FinType"));
			arl.setEntityCode(rs.getString("EntityCode"));

			return arl;
		});
	}

	@Override
	public void updateSettlementFlag(long finID, boolean isUnderSettlement) {
		String sql = "Update FinanceMain Set UnderSettlement = ? Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, isUnderSettlement, finID);
	}

	@Override
	public FinanceMain getFinanceMainForExcessTransfer(long finId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinId, FinReference, FinBranch, PromotionCode, FinType, FinCcy, CustId");
		sql.append(" From FinanceMain");
		sql.append(" Where FinId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinId"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinCcy(rs.getString("FinCCy"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setPromotionCode(rs.getString("PromotionCode"));
			fm.setCustID(rs.getLong("CustId"));

			return fm;
		}, finId);
	}

	@Override
	public List<Long> getFinIDsByCustomer(CustomerCoreBank customerCoreBank) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinId From FinanceMain fm");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			sql.append(" Where c.CustCoreBank = ? ");
		} else {
			sql.append(" Where c.CustID = ? ");
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			if (CustomerExtension.CUST_CORE_BANK_ID) {
				ps.setString(++index, customerCoreBank.getCustCoreBank());
			} else {
				ps.setLong(++index, customerCoreBank.getCustID());
			}

		}, (rs, rowNum) -> {
			return rs.getLong("FinId");
		});
	}

	@Override
	public List<FinanceMain> getFinDetailsByFinType(String finType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, FinReference, LastMntOn, FinStartDate, RecordStatus");
		sql.append(", FinAmount, FinAssetValue, FinType, FinIsActive");
		sql.append(" From FinanceMain_Temp fm Where FinType = ?");
		sql.append(" Union All");
		sql.append(" CustID, FinReference, LastMntOn, FinStartDate, RecordStatus");
		sql.append(", FinAmount, FinAssetValue, FinType, FinIsActive");
		sql.append(" From FinanceMain fm Where FinType = ?");
		sql.append(" and not exists (Select 1 From FinanceMain_Temp Where FinID = fm.FinID)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setCustID(rs.getLong("CustID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fm.setFinStartDate(JdbcUtil.getDate(rs.getDate("FinStartDate")));
			fm.setRecordStatus(rs.getString("RecordStatus"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));

			return fm;
		}, finType);
	}

	@Override
	public int updateFinRepayMethod(long finID, String finRepayMethod) {
		String sql = "Update FinanceMain Set FinRepayMethod = ?, MandateId = ? Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, finRepayMethod);
			ps.setInt(2, 0);
			ps.setLong(3, finID);
		});
	}

	@Override
	public List<Long> getByCustShrtName(String custShrtName, TableType tableType) {
		Object[] object = new Object[] { 1, custShrtName + "%" };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select FinID");
			sql.append(" From FinanceMain fm");
			sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
			sql.append(" Where fm.FinIsActive = ? and c.CustShrtName like ?");
			break;
		case TEMP_TAB:
			object = new Object[] { custShrtName + "%", custShrtName + "%" };
			sql.append(" Select FinID");
			sql.append(" From FinanceMain_Temp fm");
			sql.append(" Inner Join");
			sql.append(" (Select CustID From (");
			sql.append(" Select CustID From Customers_Temp c Where CustShrtName like ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c Where CustShrtName like ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
			sql.append(" )) c on c.CustID = fm.CustID");
			break;
		case BOTH_TAB:
			object = new Object[] { 1, custShrtName + "%", custShrtName + "%", custShrtName + "%" };
			sql.append(" Select FinID");
			sql.append(" From FinanceMain fm");
			sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
			sql.append(" Where fm.FinIsActive = ? and c.CustShrtName like ?");
			sql.append(" Union All");
			sql.append(" Select FinID");
			sql.append(" From FinanceMain_Temp fm");
			sql.append(" Inner Join");
			sql.append(" (Select CustID From (");
			sql.append(" Select CustID From Customers_Temp c Where CustShrtName like ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c Where CustShrtName like ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
			sql.append(" )) c on c.CustID = fm.CustID");
			sql.append(" Where not exists (Select 1 From FinanceMain_Temp Where FinID = fm.FinID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<Long> list = new ArrayList<>();

		SqlRowSet rowSet = this.jdbcOperations.queryForRowSet(sql.toString(), object);

		while (rowSet.next()) {

			if (list.size() == 50) {
				break;
			}

			list.add(rowSet.getLong("FinID"));
		}

		return list;
	}

	@Override
	public List<Long> getByPANNumber(String custCRCPR, TableType tableType) {
		Object[] object = new Object[] { 1, custCRCPR };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select FinID");
			sql.append(" From FinanceMain fm");
			sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
			sql.append(" Where fm.FinIsActive = ? and c.CustCRCPR = ?");
			break;
		case TEMP_TAB:
			object = new Object[] { custCRCPR, custCRCPR };
			sql.append(" Select FinID");
			sql.append(" From FinanceMain_Temp fm");
			sql.append(" Inner Join");
			sql.append(" (Select CustID From (");
			sql.append(" Select CustID From Customers_Temp c Where CustCRCPR = ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c Where CustCRCPR = ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
			sql.append(" )) c on c.CustID = fm.CustID");
			break;
		case BOTH_TAB:
			object = new Object[] { 1, custCRCPR, custCRCPR, custCRCPR };
			sql.append(" Select FinID");
			sql.append(" From FinanceMain fm");
			sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
			sql.append(" Where fm.FinIsActive = ? and c.CustCRCPR = ?");
			sql.append(" Union All");
			sql.append(" Select FinID");
			sql.append(" From FinanceMain_Temp fm");
			sql.append(" Inner Join");
			sql.append(" (Select CustID From (");
			sql.append(" Select CustID From Customers_Temp c Where CustCRCPR = ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c Where CustCRCPR = ?");
			sql.append(" and not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
			sql.append(" )) c on c.CustID = fm.CustID");
			sql.append(" Where not exists (Select 1 From FinanceMain_Temp Where FinID = fm.FinID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, object);
	}

	@Override
	public List<Long> getByAccountNumber(String accNumber) {
		String sql = "Select fm.FinID From FinanceMain fm Inner Join Mandates m on m.MandateID = fm.MandateID Where fm.FinIsActive = ? and m.AccNumber = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForList(sql, Long.class, 1, accNumber);

	}

	@Override
	public List<Long> getByPhoneNumber(String phoneNumber, TableType tableType) {
		Object[] object = new Object[] { phoneNumber, 1 };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select FinID");
			sql.append(" From FinanceMain fm");
			sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
			sql.append(" Inner Join CustomerPhoneNumbers cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Where fm.FinIsActive = ? ");
			break;
		case TEMP_TAB:
			object = new Object[] { phoneNumber, phoneNumber };
			sql.append(" Select FinID");
			sql.append(" From FinanceMain_Temp fm");
			sql.append(" Inner Join");
			sql.append(" (Select CustID From (");
			sql.append(" Select CustID From Customers_Temp c");
			sql.append(" Inner Join CustomerPhoneNumbers_Temp cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c");
			sql.append(" Inner Join CustomerPhoneNumbers cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Where not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
			sql.append(" )) c on c.CustID = fm.CustID");
			break;
		case BOTH_TAB:
			object = new Object[] { phoneNumber, 1, phoneNumber, phoneNumber };
			sql.append(" Select FinID");
			sql.append(" From FinanceMain fm");
			sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
			sql.append(" Inner Join CustomerPhoneNumbers cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Where fm.FinIsActive = ?");
			sql.append(" Union All");
			sql.append(" Select FinID");
			sql.append(" From FinanceMain_Temp fm");
			sql.append(" Inner Join");
			sql.append(" (Select CustID From (");
			sql.append(" Select CustID From Customers_Temp c");
			sql.append(" Inner Join CustomerPhoneNumbers_Temp cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Union All");
			sql.append(" Select CustID From Customers c");
			sql.append(" Inner Join CustomerPhoneNumbers cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Where not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
			sql.append(" )) c on c.CustID = fm.CustID");
			sql.append(" Where not exists (Select 1 From FinanceMain_Temp Where FinID = fm.FinID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, object);
	}

	@Override
	public List<Long> getByCustShrtNameAndPhoneNumber(String custShrtName, String phoneNumber, TableType tableType) {
		Object[] object = new Object[] { custShrtName, phoneNumber, 1 };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select distinct FinID");
			sql.append(" From FinanceMain fm");
			sql.append(" Inner Join Customers c on c.CustID = fm.CustID and c.CustShrtName = ?");
			sql.append(" Inner Join CustomerPhoneNumbers cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Where fm.FinIsActive = ? ");
			break;
		case TEMP_TAB:
			object = new Object[] { phoneNumber, phoneNumber, custShrtName };
			sql.append(" Select distinct FinID");
			sql.append(" From FinanceMain_Temp fm");
			sql.append(" Inner Join");
			sql.append(" (Select CustID, CustShrtName From (");
			sql.append(" Select CustID, CustShrtName From Customers_Temp c");
			sql.append(" Inner Join CustomerPhoneNumbers_Temp cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Union All");
			sql.append(" Select CustID, CustShrtName From Customers c");
			sql.append(" Inner Join CustomerPhoneNumbers cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Where not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
			sql.append(" )) c on c.CustID = fm.CustID and c.CustShrtName = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { custShrtName, phoneNumber, 1, phoneNumber, phoneNumber, custShrtName };
			sql.append(" Select distinct FinID");
			sql.append(" From FinanceMain fm");
			sql.append(" Inner Join Customers c on c.CustID = fm.CustID and c.CustShrtName = ?");
			sql.append(" Inner Join CustomerPhoneNumbers cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Where fm.FinIsActive = ?");
			sql.append(" Union All");
			sql.append(" Select FinID");
			sql.append(" From FinanceMain_Temp fm");
			sql.append(" Inner Join");
			sql.append(" (Select CustID, CustShrtName From (");
			sql.append(" Select CustID, CustShrtName From Customers_Temp c");
			sql.append(" Inner Join CustomerPhoneNumbers_Temp cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Union All");
			sql.append(" Select CustID, CustShrtName From Customers c");
			sql.append(" Inner Join CustomerPhoneNumbers cp on cp.PhoneCustID = c.CustID and cp.PhoneNumber = ?");
			sql.append(" Where not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
			sql.append(" )) c on c.CustID = fm.CustID  and c.CustShrtName = ?");
			sql.append(" Where not exists (Select 1 From FinanceMain_Temp Where FinID = fm.FinID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, object);
	}

	@Override
	public List<Long> getByCustShrtNameAndDateOfBirth(String custShrtName, Date dateOfBirth, TableType tableType) {
		Object[] object = new Object[] { custShrtName, dateOfBirth, 1 };

		StringBuilder sql = new StringBuilder();
		switch (tableType) {
		case MAIN_TAB:
			sql.append(" Select distinct FinID");
			sql.append(" From FinanceMain fm");
			sql.append(" Inner Join Customers c on c.CustID = fm.CustID and c.CustShrtName = ? and c.CustDOB = ?");
			sql.append(" Where fm.FinIsActive = ? ");
			break;
		case TEMP_TAB:
			object = new Object[] { custShrtName, dateOfBirth };
			sql.append(" Select distinct FinID");
			sql.append(" From FinanceMain_Temp fm");
			sql.append(" Inner Join");
			sql.append(" (Select CustID, CustShrtName, CustDOB From (");
			sql.append(" Select CustID, CustShrtName, CustDOB From Customers_Temp c");
			sql.append(" Union All");
			sql.append(" Select CustID, CustShrtName, CustDOB From Customers c");
			sql.append(" Where not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
			sql.append(" )) c on c.CustID = fm.CustID and c.CustShrtName = ?  and c.CustDOB = ?");
			break;
		case BOTH_TAB:
			object = new Object[] { custShrtName, dateOfBirth, 1, custShrtName, dateOfBirth };
			sql.append(" Select distinct FinID");
			sql.append(" From FinanceMain fm");
			sql.append(" Inner Join Customers c on c.CustID = fm.CustID and c.CustShrtName = ? and c.CustDOB = ?");
			sql.append(" Where fm.FinIsActive = ?");
			sql.append(" Union All");
			sql.append(" Select FinID");
			sql.append(" From FinanceMain_Temp fm");
			sql.append(" Inner Join");
			sql.append(" (Select CustID, CustShrtName, CustDOB From (");
			sql.append(" Select CustID, CustShrtName, CustDOB From Customers_Temp c");
			sql.append(" Union All");
			sql.append(" Select CustID, CustShrtName, CustDOB From Customers c");
			sql.append(" Where not exists (Select 1 From Customers_Temp Where CustID = c.CustID)");
			sql.append(" )) c on c.CustID = fm.CustID  and c.CustShrtName = ? and c.CustDOB = ?");
			sql.append(" Where not exists (Select 1 From FinanceMain_Temp Where FinID = fm.FinID)");
		default:
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, object);
	}

	@Override
	public List<Long> getByCustShrtNameAndEMIAmount(String customerName, BigDecimal repayAmount) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select distinct fs.FinID");
		sql.append(" From FinScheduleDetails fs");
		sql.append(" Inner Join FinanceMain fm On fm.finID = fs.finID");
		sql.append(" Inner Join Customers cu on cu.CustID = fm.CustID");
		sql.append(" Where fm.FinIsActive = ? and fs.RepayAmount = ? and cu.CustShrtName = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, 1, repayAmount, customerName);
	}

	@Override
	public Date getMaturityDatebyFinID(long finID) {
		String sql = "Select MaturityDate From FinanceMain Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Date.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

}