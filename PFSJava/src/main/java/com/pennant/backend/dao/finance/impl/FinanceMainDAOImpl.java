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
 *
 * FileName    		:  FinanceMainDAOImpl.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  24-12-2017															*
 *                                                                  
 * Modified Date    :  24-12-2017															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-12-2017       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.LoanPendingData;
import com.pennant.backend.model.ddapayments.DDAPayments;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.BulkDefermentChange;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.FinCustomerDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceMainExtension;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.RolledoverFinanceDetail;
import com.pennant.backend.model.finance.UserPendingCases;
import com.pennant.backend.model.reports.AvailCommitment;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.model.DMSQueue;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 */
public class FinanceMainDAOImpl extends BasicDao<FinanceMain> implements FinanceMainDAO {
	private static Logger logger = LogManager.getLogger(FinanceMainDAOImpl.class);

	public FinanceMainDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceMain
	 * 
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMain(boolean isWIF) {
		String wfModuleName = "";

		if (isWIF) {
			wfModuleName = "WIFFinanceMain";
		} else {
			wfModuleName = "FinanceMain";
		}

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails(wfModuleName);
		FinanceMain financeMain = new FinanceMain();
		if (workFlowDetails != null) {
			financeMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		return financeMain;
	}

	/**
	 * This method get the module from method getFinanceMain() and set the new record flag as true and return
	 * FinanceMain()
	 * 
	 * @return FinanceMain
	 */

	@Override
	public FinanceMain getNewFinanceMain(boolean isWIF) {
		logger.debug("Entering");
		FinanceMain financeMain = getFinanceMain(isWIF);
		financeMain.setNewRecord(true);
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * Method to get all the first task owner roles of the work flow's which are used for finance.
	 */
	@Override
	public List<String> getFinanceWorlflowFirstTaskOwners(String event, String moduleName) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("WorkFlowActive", 1);
		source.addValue("FinEvent", event);
		source.addValue("ModuleName", event);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select FirstTaskOwner from WorkFlowDetails");
		selectSql.append(" where  WorkFlowType in ( select DISTINCT WorkFlowType from  LMTFinanceWorkFlowDef");
		if (StringUtils.isNotBlank(event)) {
			selectSql.append(" where FinEvent=:FinEvent )");
		}
		selectSql.append(" and WorkFlowActive= :WorkFlowActive ");
		logger.debug("selectSql: " + selectSql.toString());
		List<String> firstTaskOwnerList = new ArrayList<String>();

		try {
			firstTaskOwnerList = this.jdbcTemplate.queryForList(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.debug(e);
		}

		logger.debug("Leaving");
		return firstTaskOwnerList;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMain(final String finReference, String nextRoleCode, String type) {
		StringBuilder sql = new StringBuilder(getFinMainAllQuery(type, false));
		sql.append(" Where FinReference = ? And NextRoleCode = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinanceMainRowMapper rowMapper = new FinanceMainRowMapper(type, false);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, nextRoleCode },
					rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"There is no loans exists in FinanceMain{} table/ view for the specified FinReference >> {} And NextRoleCode >> {}",
					type, finReference, nextRoleCode);
		}

		return null;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainById(final String finReference, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder(getFinMainAllQuery(type, isWIF));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinanceMainRowMapper rowMapper = new FinanceMainRowMapper(type, isWIF);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getDisbursmentFinMainById(final String finReference, TableType tableType) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = null;

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.FinCcy, t1.FinType, t1.CustID, t1.FinStartDate, t1.FinBranch");
		sql.append(", t1.FinReference, t1.MaturityDate, t1.FeeChargeAmt, t1.DownPayment");
		sql.append(", t1.DeductFeeDisb, t1.BpiAmount, t1.DeductInsDisb, t1.FinIsActive");
		sql.append(", t1.BpiTreatment, t1.QuickDisb, t1.FinAssetValue, t1.FinCurrAssetValue");
		sql.append(", t3.CustCIF, t3.CustShrtName, t2.alwMultiPartyDisb, t2.FinTypeDesc");
		sql.append(" from FinanceMain");
		sql.append(tableType.getSuffix());
		sql.append(" t1 INNER JOIN RMTFinanceTypes t2 ON t1.FinType=t2.FinType ");
		sql.append(" INNER JOIN Customers t3 ON t1.CustID = t3.CustID ");
		sql.append(" Where T1.FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());
		try {
			fm = this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<FinanceMain>() {
						@Override
						public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinanceMain fm = new FinanceMain();

							fm.setFinCcy(rs.getString("FinCcy"));
							fm.setFinType(rs.getString("FinType"));
							fm.setCustID(rs.getLong("CustID"));
							fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
							fm.setFinBranch(rs.getString("FinBranch"));
							fm.setFinReference(rs.getString("FinReference"));
							fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
							fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
							fm.setDownPayment(rs.getBigDecimal("DownPayment"));
							fm.setDeductFeeDisb(rs.getBigDecimal("DeductFeeDisb"));
							fm.setBpiAmount(rs.getBigDecimal("BpiAmount"));
							fm.setDeductInsDisb(rs.getBigDecimal("DeductInsDisb"));
							fm.setFinIsActive(rs.getBoolean("FinIsActive"));
							fm.setBpiTreatment(rs.getString("BpiTreatment"));
							fm.setQuickDisb(rs.getBoolean("QuickDisb"));
							fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
							fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
							fm.setLovDescCustCIF(rs.getString("CustCIF"));
							fm.setLovDescCustShrtName(rs.getString("CustShrtName"));
							fm.setAlwMultiDisb(rs.getBoolean("AlwMultiPartyDisb"));
							fm.setLovDescFinTypeName(rs.getString("FinTypeDesc"));

							return fm;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return fm;

	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainForPftCalc(final String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinType, CustID, FinAmount, DownPayment, FeeChargeAmt, GrcPeriodEndDate");
		sql.append(", NextRepayPftDate, NextRepayRvwDate, FinIsActive, ProfitDaysBasis, FinStartDate");
		sql.append(", FinAssetValue, LastRepayPftDate, LastRepayRvwDate, FinCurrAssetValue, MaturityDate");
		sql.append(", FinStatus, FinStsReason, InitiateUser, BankName, Iban, AccountType, DdaReferenceNo");
		sql.append(", ClosingStatus, LastRepayDate, NextRepayDate, PromotionCode, PastduePftCalMthd");
		sql.append(", PastduePftMargin,instbasedschd");
		sql.append(" from FinanceMain");
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<FinanceMain>() {
						@Override
						public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinanceMain fm = new FinanceMain();

							fm.setFinReference(rs.getString("FinReference"));
							fm.setFinType(rs.getString("FinType"));
							fm.setCustID(rs.getLong("CustID"));
							fm.setFinAmount(rs.getBigDecimal("FinAmount"));
							fm.setDownPayment(rs.getBigDecimal("DownPayment"));
							fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
							fm.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
							fm.setNextRepayPftDate(rs.getTimestamp("NextRepayPftDate"));
							fm.setNextRepayRvwDate(rs.getTimestamp("NextRepayRvwDate"));
							fm.setFinIsActive(rs.getBoolean("FinIsActive"));
							fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
							fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
							fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
							fm.setLastRepayPftDate(rs.getTimestamp("LastRepayPftDate"));
							fm.setLastRepayRvwDate(rs.getTimestamp("LastRepayRvwDate"));
							fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
							fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
							fm.setFinStatus(rs.getString("FinStatus"));
							fm.setFinStsReason(rs.getString("FinStsReason"));
							fm.setInitiateUser(rs.getLong("InitiateUser"));
							fm.setBankName(rs.getString("BankName"));
							fm.setIban(rs.getString("Iban"));
							fm.setAccountType(rs.getString("AccountType"));
							fm.setDdaReferenceNo(rs.getString("DdaReferenceNo"));
							fm.setClosingStatus(rs.getString("ClosingStatus"));
							fm.setLastRepayDate(rs.getTimestamp("LastRepayDate"));
							fm.setNextRepayDate(rs.getTimestamp("NextRepayDate"));
							fm.setPromotionCode(rs.getString("PromotionCode"));
							fm.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
							fm.setPastduePftMargin(rs.getBigDecimal("PastduePftMargin"));
							fm.setInstBasedSchd(rs.getBoolean("instbasedschd"));

							return fm;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainForRpyCancel(final String id) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ft.FinType, FinReference, CustID, GrcPeriodEndDate, NextRepayPftDate, NextRepayRvwDate");
		sql.append(", FinStatus, FinAmount, FeeChargeAmt, FinRepaymentAmount, fm.FinCcy, FinBranch");
		sql.append(", ProfitDaysBasis, FinStartDate, FinAssetValue, LastRepayPftDate, LastRepayRvwDate");
		sql.append(", FinCurrAssetValue, MaturityDate, PromotionCode, e.EntityCode");
		sql.append(" from Financemain fm");
		sql.append(" inner join RmtFinanceTypes ft on ft.Fintype = fm.Fintype");
		sql.append(" inner join SmtDivisionDetail d on d.DivisionCode = ft.FinDivision");
		sql.append(" inner join entity e on e.EntityCode = d.EntityCode");
		sql.append("  Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id },
					new RowMapper<FinanceMain>() {
						@Override
						public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinanceMain fm = new FinanceMain();

							fm.setFinType(rs.getString("FinType"));
							fm.setFinReference(rs.getString("FinReference"));
							fm.setCustID(rs.getLong("CustID"));
							fm.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
							fm.setNextRepayPftDate(rs.getTimestamp("NextRepayPftDate"));
							fm.setNextRepayRvwDate(rs.getTimestamp("NextRepayRvwDate"));
							fm.setFinStatus(rs.getString("FinStatus"));
							fm.setFinAmount(rs.getBigDecimal("FinAmount"));
							fm.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
							fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
							fm.setFinCcy(rs.getString("FinCcy"));
							fm.setFinBranch(rs.getString("FinBranch"));
							fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
							fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
							fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
							fm.setLastRepayPftDate(rs.getTimestamp("LastRepayPftDate"));
							fm.setLastRepayRvwDate(rs.getTimestamp("LastRepayRvwDate"));
							fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
							fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
							fm.setPromotionCode(rs.getString("PromotionCode"));
							fm.setEntityCode(rs.getString("EntityCode"));

							return fm;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param finReference
	 *            (String)
	 * 
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainForBatch(final String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, GrcPeriodEndDate, FinRepaymentAmount, DisbAccountId, RepayAccountId");
		sql.append(", FinAccount, FinCustPftAccount, FinCommitmentRef, FinLimitRef, FinCcy, FinBranch");
		sql.append(", CustID, FinAmount, FeeChargeAmt, DownPayment, DownPayBank, DownPaySupl, DownPayAccount");
		sql.append(", SecurityDeposit, FinType, FinStartDate, GraceTerms, NumberOfTerms, NextGrcPftDate");
		sql.append(", NextRepayDate, LastRepayPftDate, NextRepayPftDate, ProductCategory, FinCategory");
		sql.append(", LastRepayRvwDate, NextRepayRvwDate, FinAssetValue, FinCurrAssetValue, FinRepayMethod");
		sql.append(", RepayFrq, ClosingStatus, DueBucket, CalRoundingMode, RoundingTarget, RecordType");
		sql.append(", Version, ProfitDaysBasis, FinStatus, FinStsReason, PastduePftCalMthd, PastduePftMargin");
		sql.append(", InitiateUser, BankName, Iban, AccountType, DdaReferenceNo, MaturityDate");
		sql.append(", feeAccountId, MinDownPayPerc, PromotionCode, FinIsActive, SanBsdSchdle, PromotionSeqId");
		sql.append(", SvAmount, CbAmount, EmployeeName");
		sql.append(" from Financemain");
		sql.append("  Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<FinanceMain>() {
						@Override
						public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinanceMain fm = new FinanceMain();

							fm.setFinReference(rs.getString("FinReference"));
							fm.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
							fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
							fm.setDisbAccountId(rs.getString("DisbAccountId"));
							fm.setRepayAccountId(rs.getString("RepayAccountId"));
							fm.setFinAccount(rs.getString("FinAccount"));
							fm.setFinCustPftAccount(rs.getString("FinCustPftAccount"));
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
							fm.setDownPayAccount(rs.getString("DownPayAccount"));
							fm.setSecurityDeposit(rs.getBigDecimal("SecurityDeposit"));
							fm.setFinType(rs.getString("FinType"));
							fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
							fm.setGraceTerms(rs.getInt("GraceTerms"));
							fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
							fm.setNextGrcPftDate(rs.getTimestamp("NextGrcPftDate"));
							fm.setNextRepayDate(rs.getTimestamp("NextRepayDate"));
							fm.setLastRepayPftDate(rs.getTimestamp("LastRepayPftDate"));
							fm.setNextRepayPftDate(rs.getTimestamp("NextRepayPftDate"));
							fm.setProductCategory(rs.getString("ProductCategory"));
							fm.setFinCategory(rs.getString("FinCategory"));
							fm.setLastRepayRvwDate(rs.getTimestamp("LastRepayRvwDate"));
							fm.setNextRepayRvwDate(rs.getTimestamp("NextRepayRvwDate"));
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
							fm.setBankName(rs.getString("BankName"));
							fm.setIban(rs.getString("Iban"));
							fm.setAccountType(rs.getString("AccountType"));
							fm.setDdaReferenceNo(rs.getString("DdaReferenceNo"));
							fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
							fm.setFeeAccountId(rs.getString("feeAccountId"));
							fm.setMinDownPayPerc(rs.getBigDecimal("MinDownPayPerc"));
							fm.setPromotionCode(rs.getString("PromotionCode"));
							fm.setFinIsActive(rs.getBoolean("FinIsActive"));
							fm.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
							fm.setPromotionSeqId(rs.getLong("PromotionSeqId"));
							fm.setSvAmount(rs.getBigDecimal("SvAmount"));
							fm.setCbAmount(rs.getBigDecimal("CbAmount"));
							fm.setEmployeeName(rs.getString("EmployeeName"));

							return fm;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;

	}

	/**
	 * Fetch the Records Finance Main Detail details by key field
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@Override
	public List<String> getFinanceMainListByBatch(final Date curBD, final Date nextBD, String type) {
		logger.debug(Literal.ENTERING);

		List<String> finReference = null;

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference");
		sql.append(" From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinStartDate >= ? and MaturityDate < ?");

		logger.trace(Literal.SQL + sql.toString());
		try {
			finReference = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setDate(index++, JdbcUtil.getDate(curBD));
					ps.setDate(index++, JdbcUtil.getDate(nextBD));
				}
			}, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("FinReference");
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return finReference;
	}

	/**
	 * This method insert new Records into FinanceMain or FinanceMain_Temp.
	 * 
	 * save Finance Main Detail
	 * 
	 * @param Finance
	 *            Main Detail (financeMain)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	public String saveInvestmentFinance(FinanceMain financeMain, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");

		insertSql.append(" FinanceMain");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, InvestmentRef , FinType, FinCcy, FinBranch, FinAmount, FinStartDate,");
		insertSql.append(" MaturityDate, CustID, RepayProfitRate , TotalRepayAmt ,");
		insertSql.append(" TotalProfit, ProfitDaysBasis, ScheduleMethod, disbAccountId, repayAccountId,");
		insertSql.append(" LastRepayDate, LastRepayPftDate, LastRepayRvwDate , LastRepayCpzDate ,");
		insertSql.append(" GraceTerms, NumberOfTerms, AllowGrcPeriod, AllowGrcPftRvw , AllowGrcCpz ,");
		insertSql.append(" AllowRepayRvw, AllowRepayCpz, CpzAtGraceEnd , CalTerms ,");
		insertSql.append(" Defferments, PlanDeferCount, AllowedDefRpyChange , AvailedDefRpyChange ,");
		insertSql.append(" AllowedDefFrqChange, AvailedDefFrqChange, FinIsActive , AllowGrcRepay ,");
		insertSql.append(" FinRepayPftOnFrq , ");
		insertSql.append(" MigratedFinance, ScheduleMaintained, ScheduleRegenerated , Blacklisted ,");
		insertSql
				.append(" GrcProfitDaysBasis, StepFinance , StepPolicy, StepType, AlwManualSteps, NoOfSteps,DsaCode, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(
				" NextTaskId, NextUserId, Priority, RecordType, WorkflowId, feeAccountId, MinDownPayPerc, TDSApplicable,DroplineFrq,FirstDroplineDate,PftServicingODLimit, PromotionCode)");

		insertSql.append(
				" Values(:FinReference, :InvestmentRef, :FinType, :FinCcy, :FinBranch, :FinAmount, :FinStartDate,");
		insertSql.append(" :MaturityDate, :CustID, :RepayProfitRate , :TotalRepayAmt ,");
		insertSql.append(" :TotalProfit, :ProfitDaysBasis, :ScheduleMethod, :DisbAccountId, :RepayAccountId,");
		insertSql.append(" :LastRepayDate, :LastRepayPftDate, :LastRepayRvwDate, :LastRepayCpzDate,");
		insertSql.append(" :GraceTerms, :NumberOfTerms, :AllowGrcPeriod, :AllowGrcPftRvw, :AllowGrcCpz,");
		insertSql.append(" :AllowRepayRvw, :AllowRepayCpz, :CpzAtGraceEnd, :CalTerms,");
		insertSql.append(" :Defferments, :PlanDeferCount, :AllowedDefRpyChange, :AvailedDefRpyChange,");
		insertSql.append(" :AllowedDefFrqChange, :AvailedDefFrqChange, :FinIsActive , :AllowGrcRepay,");
		insertSql.append(" :FinRepayPftOnFrq , ");
		insertSql.append(" :MigratedFinance, :ScheduleMaintained, :ScheduleRegenerated, :Blacklisted,");
		insertSql.append(
				" :GrcProfitDaysBasis, :StepFinance, :StepPolicy, :StepType, :AlwManualSteps, :NoOfSteps,:DsaCode, ");
		insertSql.append(" :Version ,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(
				" :NextTaskId, :NextUserId, :Priority, :RecordType,:WorkflowId, :feeAccountId, :minDownPayPerc, :TDSApplicable,:DroplineFrq,:FirstDroplineDate,:PftServicingODLimit, :PromotionCode)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeMain.getId();
	}

	@Override
	public String save(FinanceMain fm, TableType tableType, boolean wif) {
		StringBuilder sql = new StringBuilder("insert into");
		if (wif) {
			sql.append(" WIFFinanceMain");
		} else {
			sql.append(" FinanceMain");
		}
		sql.append(tableType.getSuffix());
		sql.append("(FinReference, GraceTerms, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod, GraceBaseRate");
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
		sql.append(", RecalType, FinIsActive, FinAssetValue, disbAccountId, repayAccountId, LastRepayDate");
		sql.append(", LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd");
		sql.append(", GrcMargin, RepayMargin, FinCommitmentRef, FinLimitRef, DepreciationFrq, FinCurrAssetValue");
		sql.append(", NextDepDate, LastDepDate, FinAccount, FinCustPftAccount, ClosingStatus, FinApprovedDate");
		sql.append(", DedupFound, SkipDedup, Blacklisted, GrcProfitDaysBasis, StepFinance, StepPolicy");
		sql.append(", AlwManualSteps, NoOfSteps, StepType, AnualizedPercRate, EffectiveRateOfReturn, FinRepayPftOnFrq");
		sql.append(", LinkedFinRef, GrcMinRate, GrcMaxRate, GrcMaxAmount, RpyMinRate, RpyMaxRate, ManualSchedule");
		sql.append(", TakeOverFinance, GrcAdvBaseRate, GrcAdvMargin, GrcAdvPftRate, RpyAdvBaseRate, RpyAdvMargin");
		sql.append(", RpyAdvPftRate, SupplementRent, IncreasedCost, feeAccountId, MinDownPayPerc, TDSApplicable");
		sql.append(", InsuranceAmt, AlwBPI, BpiTreatment, PlanEMIHAlw, PlanEMIHMethod, PlanEMIHMaxPerYear");
		sql.append(", PlanEMIHMax, PlanEMIHLockPeriod, PlanEMICpz, CalRoundingMode, RoundingTarget, AlwMultiDisb");
		sql.append(", FinRepayMethod, FeeChargeAmt, BpiAmount, DeductFeeDisb, RvwRateApplFor, SchCalOnRvw");
		sql.append(", PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin, FinCategory");
		sql.append(", ProductCategory, AdvanceEMI, BpiPftDaysBasis, FixedTenorRate, FixedRateTenor, BusinessVertical");
		sql.append(", GrcAdvType, GrcAdvTerms, AdvType, AdvTerms, AdvStage, AllowDrawingPower, AllowRevolving");
		sql.append(", appliedLoanAmt, FinIsRateRvwAtGrcEnd");
		if (!wif) {
			sql.append(", InvestmentRef, MigratedFinance, ScheduleMaintained, ScheduleRegenerated");
			sql.append(", CustDSR, LimitValid, OverrideLimit, FinPurpose, FinStatus, FinStsReason");
			sql.append(", InitiateUser, BankName, Iban, AccountType, DdaReferenceNo, DeviationApproval");
			sql.append(", FinPreApprovedRef, MandateID, JointAccount, JointCustId, DownPayAccount, SecurityDeposit");
			sql.append(", RcdMaintainSts, FinCancelAc, NextUserId, Priority, RolloverFrq, NextRolloverDate");
			sql.append(", ShariaStatus, InitiateDate, MMAId, AccountsOfficer, ApplicationNo, DsaCode, DroplineFrq");
			sql.append(", FirstDroplineDate, PftServicingODLimit, ReferralId, EmployeeName, DmaCode, SalesDepartment");
			sql.append(", QuickDisb, WifReference, UnPlanEMIHLockPeriod, UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi");
			sql.append(", MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, ReAgeBucket, DueBucket");
			sql.append(", EligibilityMethod, samplingRequired, legalRequired, connector, ProcessAttributes");
			sql.append(", PromotionCode, TdsPercentage, TdsStartDate, TdsEndDate, TdsLimitAmt");
			sql.append(", VanReq, VanCode, SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount");
			sql.append(", AlwGrcAdj, EndGrcPeriodAftrFullDisb, AutoIncGrcEndDate, PlanEMIHAlwInGrace");
			// HL
			sql.append(", FinOcrRequired, ReqLoanAmt, ReqLoanTenor, OfferProduct, OfferAmount");
			sql.append(", CustSegmentation, BaseProduct, ProcessType, BureauTimeSeries");
			sql.append(", CampaignName, ExistingLanRefNo, LeadSource, PoSource , Rsa, Verification");
			sql.append(", SourcingBranch, SourChannelCategory, AsmName, OfferId");
			sql.append(", Pmay, AlwGrcAdj, EndGrcPeriodAftrFullDisb, AutoIncGrcEndDate");
			sql.append(", parentRef, loanSplitted, AlwLoanSplit, InstBasedSchd");

		}
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		if (!wif) {
			sql.append(", ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			// HL
			sql.append(", ?, ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ? , ?, ?");
			sql.append(", ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ?");
			sql.append(", ?, ?, ?, ?");

		}
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, fm.getFinReference());
				ps.setInt(index++, fm.getGraceTerms());
				ps.setInt(index++, fm.getNumberOfTerms());
				ps.setDate(index++, JdbcUtil.getDate(fm.getGrcPeriodEndDate()));
				ps.setBoolean(index++, fm.isAllowGrcPeriod());
				ps.setString(index++, fm.getGraceBaseRate());
				ps.setString(index++, fm.getGraceSpecialRate());
				ps.setBigDecimal(index++, fm.getGrcPftRate());
				ps.setString(index++, fm.getGrcPftFrq());
				ps.setDate(index++, JdbcUtil.getDate(fm.getNextGrcPftDate()));
				ps.setBoolean(index++, fm.isAllowGrcPftRvw());
				ps.setString(index++, fm.getGrcPftRvwFrq());
				ps.setDate(index++, JdbcUtil.getDate(fm.getNextGrcPftRvwDate()));
				ps.setBoolean(index++, fm.isAllowGrcCpz());
				ps.setString(index++, fm.getGrcCpzFrq());
				ps.setDate(index++, JdbcUtil.getDate(fm.getNextGrcCpzDate()));
				ps.setString(index++, fm.getRepayBaseRate());
				ps.setString(index++, fm.getRepaySpecialRate());
				ps.setBigDecimal(index++, fm.getRepayProfitRate());
				ps.setString(index++, fm.getRepayFrq());
				ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayDate()));
				ps.setString(index++, fm.getRepayPftFrq());
				ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayPftDate()));
				ps.setBoolean(index++, fm.isAllowRepayRvw());
				ps.setString(index++, fm.getRepayRvwFrq());
				ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayRvwDate()));
				ps.setBoolean(index++, fm.isAllowRepayCpz());
				ps.setString(index++, fm.getRepayCpzFrq());
				ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayCpzDate()));
				ps.setDate(index++, JdbcUtil.getDate(fm.getMaturityDate()));
				ps.setBoolean(index++, fm.isCpzAtGraceEnd());
				ps.setBigDecimal(index++, fm.getDownPayment());
				ps.setBigDecimal(index++, fm.getDownPayBank());
				ps.setBigDecimal(index++, fm.getDownPaySupl());
				ps.setBigDecimal(index++, fm.getReqRepayAmount());
				ps.setBigDecimal(index++, fm.getTotalProfit());
				ps.setBigDecimal(index++, fm.getTotalCpz());
				ps.setBigDecimal(index++, fm.getTotalGrossPft());
				ps.setBigDecimal(index++, fm.getTotalGracePft());
				ps.setBigDecimal(index++, fm.getTotalGraceCpz());
				ps.setBigDecimal(index++, fm.getTotalGrossGrcPft());
				ps.setBigDecimal(index++, fm.getTotalRepayAmt());
				ps.setString(index++, fm.getGrcRateBasis());
				ps.setString(index++, fm.getRepayRateBasis());
				ps.setString(index++, fm.getFinType());
				ps.setString(index++, fm.getFinRemarks());
				ps.setString(index++, fm.getFinCcy());
				ps.setString(index++, fm.getScheduleMethod());
				ps.setDate(index++, JdbcUtil.getDate(fm.getFinContractDate()));
				ps.setString(index++, fm.getProfitDaysBasis());
				ps.setDate(index++, JdbcUtil.getDate(fm.getReqMaturity()));
				ps.setInt(index++, fm.getCalTerms());
				ps.setDate(index++, JdbcUtil.getDate(fm.getCalMaturity()));
				ps.setBigDecimal(index++, fm.getFirstRepay());
				ps.setBigDecimal(index++, fm.getLastRepay());
				ps.setDate(index++, JdbcUtil.getDate(fm.getFinStartDate()));
				ps.setBigDecimal(index++, fm.getFinAmount());
				ps.setBigDecimal(index++, fm.getFinRepaymentAmount());
				ps.setLong(index++, JdbcUtil.setLong(fm.getCustID()));
				ps.setInt(index++, fm.getDefferments());
				ps.setInt(index++, fm.getPlanDeferCount());
				ps.setString(index++, fm.getFinBranch());
				ps.setString(index++, fm.getFinSourceID());
				ps.setInt(index++, fm.getAllowedDefRpyChange());
				ps.setInt(index++, fm.getAvailedDefRpyChange());
				ps.setInt(index++, fm.getAllowedDefFrqChange());
				ps.setInt(index++, fm.getAvailedDefFrqChange());
				ps.setString(index++, fm.getRecalType());
				ps.setBoolean(index++, fm.isFinIsActive());
				ps.setBigDecimal(index++, fm.getFinAssetValue());
				ps.setString(index++, fm.getDisbAccountId());
				ps.setString(index++, fm.getRepayAccountId());
				ps.setDate(index++, JdbcUtil.getDate(fm.getLastRepayDate()));
				ps.setDate(index++, JdbcUtil.getDate(fm.getLastRepayPftDate()));
				ps.setDate(index++, JdbcUtil.getDate(fm.getLastRepayRvwDate()));
				ps.setDate(index++, JdbcUtil.getDate(fm.getLastRepayCpzDate()));
				ps.setBoolean(index++, fm.isAllowGrcRepay());
				ps.setString(index++, fm.getGrcSchdMthd());
				ps.setBigDecimal(index++, fm.getGrcMargin());
				ps.setBigDecimal(index++, fm.getRepayMargin());
				ps.setString(index++, fm.getFinCommitmentRef());
				ps.setString(index++, fm.getFinLimitRef());
				ps.setString(index++, fm.getDepreciationFrq());
				ps.setBigDecimal(index++, fm.getFinCurrAssetValue());
				ps.setDate(index++, JdbcUtil.getDate(fm.getNextDepDate()));
				ps.setDate(index++, JdbcUtil.getDate(fm.getLastDepDate()));
				ps.setString(index++, fm.getFinAccount());
				ps.setString(index++, fm.getFinCustPftAccount());
				ps.setString(index++, fm.getClosingStatus());
				ps.setDate(index++, JdbcUtil.getDate(fm.getFinApprovedDate()));
				ps.setBoolean(index++, fm.isDedupFound());
				ps.setBoolean(index++, fm.isSkipDedup());
				ps.setBoolean(index++, fm.isBlacklisted());
				ps.setString(index++, fm.getGrcProfitDaysBasis());
				ps.setBoolean(index++, fm.isStepFinance());
				ps.setString(index++, fm.getStepPolicy());
				ps.setBoolean(index++, fm.isAlwManualSteps());
				ps.setInt(index++, fm.getNoOfSteps());
				ps.setString(index++, fm.getStepType());
				ps.setBigDecimal(index++, fm.getAnualizedPercRate());
				ps.setBigDecimal(index++, fm.getEffectiveRateOfReturn());
				ps.setBoolean(index++, fm.isFinRepayPftOnFrq());
				ps.setString(index++, fm.getLinkedFinRef());
				ps.setBigDecimal(index++, fm.getGrcMinRate());
				ps.setBigDecimal(index++, fm.getGrcMaxRate());
				ps.setBigDecimal(index++, fm.getGrcMaxAmount());
				ps.setBigDecimal(index++, fm.getRpyMinRate());
				ps.setBigDecimal(index++, fm.getRpyMaxRate());
				ps.setBoolean(index++, fm.isManualSchedule());
				ps.setBoolean(index++, fm.isTakeOverFinance());
				ps.setString(index++, fm.getGrcAdvBaseRate());
				ps.setBigDecimal(index++, fm.getGrcAdvMargin());
				ps.setBigDecimal(index++, fm.getGrcAdvPftRate());
				ps.setString(index++, fm.getRpyAdvBaseRate());
				ps.setBigDecimal(index++, fm.getRpyAdvMargin());
				ps.setBigDecimal(index++, fm.getRpyAdvPftRate());
				ps.setBigDecimal(index++, fm.getSupplementRent());
				ps.setBigDecimal(index++, fm.getIncreasedCost());
				ps.setString(index++, fm.getFeeAccountId());
				ps.setBigDecimal(index++, fm.getMinDownPayPerc());
				ps.setBoolean(index++, fm.isTDSApplicable());
				ps.setBigDecimal(index++, fm.getInsuranceAmt());
				ps.setBoolean(index++, fm.isAlwBPI());
				ps.setString(index++, fm.getBpiTreatment());
				ps.setBoolean(index++, fm.isPlanEMIHAlw());
				ps.setString(index++, fm.getPlanEMIHMethod());
				ps.setInt(index++, fm.getPlanEMIHMaxPerYear());
				ps.setInt(index++, fm.getPlanEMIHMax());
				ps.setInt(index++, fm.getPlanEMIHLockPeriod());
				ps.setBoolean(index++, fm.isPlanEMICpz());
				ps.setString(index++, fm.getCalRoundingMode());
				ps.setInt(index++, fm.getRoundingTarget());
				ps.setBoolean(index++, fm.isAlwMultiDisb());
				ps.setString(index++, fm.getFinRepayMethod());
				ps.setBigDecimal(index++, fm.getFeeChargeAmt());
				ps.setBigDecimal(index++, fm.getBpiAmount());
				ps.setBigDecimal(index++, fm.getDeductFeeDisb());
				ps.setString(index++, fm.getRvwRateApplFor());
				ps.setString(index++, fm.getSchCalOnRvw());
				ps.setString(index++, fm.getPastduePftCalMthd());
				ps.setString(index++, fm.getDroppingMethod());
				ps.setBoolean(index++, fm.isRateChgAnyDay());
				ps.setBigDecimal(index++, fm.getPastduePftMargin());
				ps.setString(index++, fm.getFinCategory());
				ps.setString(index++, fm.getProductCategory());
				ps.setBigDecimal(index++, fm.getAdvanceEMI());
				ps.setString(index++, fm.getBpiPftDaysBasis());
				ps.setBigDecimal(index++, fm.getFixedTenorRate());
				ps.setInt(index++, fm.getFixedRateTenor());
				ps.setLong(index++, JdbcUtil.setLong(fm.getBusinessVertical()));
				ps.setString(index++, fm.getGrcAdvType());
				ps.setInt(index++, fm.getGrcAdvTerms());
				ps.setString(index++, fm.getAdvType());
				ps.setInt(index++, fm.getAdvTerms());
				ps.setString(index++, fm.getAdvStage());
				ps.setBoolean(index++, fm.isAllowDrawingPower());
				ps.setBoolean(index++, fm.isAllowRevolving());
				ps.setBigDecimal(index++, fm.getAppliedLoanAmt());
				ps.setBoolean(index++, fm.isFinIsRateRvwAtGrcEnd());
				if (!wif) {
					ps.setString(index++, fm.getInvestmentRef());
					ps.setBoolean(index++, fm.isMigratedFinance());
					ps.setBoolean(index++, fm.isScheduleMaintained());
					ps.setBoolean(index++, fm.isScheduleRegenerated());
					ps.setBigDecimal(index++, fm.getCustDSR());
					ps.setBoolean(index++, fm.isLimitValid());
					ps.setBoolean(index++, fm.isOverrideLimit());
					ps.setString(index++, fm.getFinPurpose());
					ps.setString(index++, fm.getFinStatus());
					ps.setString(index++, fm.getFinStsReason());
					ps.setLong(index++, JdbcUtil.setLong(fm.getInitiateUser()));
					ps.setString(index++, fm.getBankName());
					ps.setString(index++, fm.getIban());
					ps.setString(index++, fm.getAccountType());
					ps.setString(index++, fm.getDdaReferenceNo());
					ps.setBoolean(index++, fm.isDeviationApproval());
					ps.setString(index++, fm.getFinPreApprovedRef());
					ps.setLong(index++, JdbcUtil.setLong(fm.getMandateID()));
					ps.setBoolean(index++, fm.isJointAccount());
					ps.setLong(index++, JdbcUtil.setLong(fm.getJointCustId()));
					ps.setString(index++, fm.getDownPayAccount());
					ps.setBigDecimal(index++, fm.getSecurityDeposit());
					ps.setString(index++, fm.getRcdMaintainSts());
					ps.setString(index++, fm.getFinCancelAc());
					ps.setString(index++, fm.getNextUserId());
					ps.setInt(index++, fm.getPriority());
					ps.setString(index++, fm.getRolloverFrq());
					ps.setDate(index++, JdbcUtil.getDate(fm.getNextRolloverDate()));
					ps.setString(index++, fm.getShariaStatus());
					ps.setDate(index++, JdbcUtil.getDate(fm.getInitiateDate()));
					ps.setLong(index++, JdbcUtil.setLong(fm.getMMAId()));
					ps.setLong(index++, JdbcUtil.setLong(fm.getAccountsOfficer()));
					ps.setString(index++, fm.getApplicationNo());
					ps.setString(index++, fm.getDsaCode());
					ps.setString(index++, fm.getDroplineFrq());
					ps.setDate(index++, JdbcUtil.getDate(fm.getFirstDroplineDate()));
					ps.setBoolean(index++, fm.isPftServicingODLimit());
					ps.setString(index++, fm.getReferralId());
					ps.setString(index++, fm.getEmployeeName());
					ps.setString(index++, fm.getDmaCode());
					ps.setString(index++, fm.getSalesDepartment());
					ps.setBoolean(index++, fm.isQuickDisb());
					ps.setString(index++, fm.getWifReference());
					ps.setInt(index++, fm.getUnPlanEMIHLockPeriod());
					ps.setBoolean(index++, fm.isUnPlanEMICpz());
					ps.setBoolean(index++, fm.isReAgeCpz());
					ps.setInt(index++, fm.getMaxUnplannedEmi());
					ps.setInt(index++, fm.getMaxReAgeHolidays());
					ps.setInt(index++, fm.getAvailedUnPlanEmi());
					ps.setInt(index++, fm.getAvailedReAgeH());
					ps.setInt(index++, fm.getReAgeBucket());
					ps.setInt(index++, fm.getDueBucket());
					ps.setLong(index++, JdbcUtil.setLong(fm.getEligibilityMethod()));
					ps.setBoolean(index++, fm.isSamplingRequired());
					ps.setBoolean(index++, fm.isLegalRequired());
					ps.setLong(index++, JdbcUtil.setLong(fm.getConnector()));
					ps.setString(index++, fm.getProcessAttributes());
					ps.setString(index++, fm.getPromotionCode());
					ps.setBigDecimal(index++, fm.getTdsPercentage());
					ps.setDate(index++, JdbcUtil.getDate(fm.getTdsStartDate()));
					ps.setDate(index++, JdbcUtil.getDate(fm.getTdsEndDate()));
					ps.setBigDecimal(index++, fm.getTdsLimitAmt());
					ps.setBoolean(index++, fm.isVanReq());
					ps.setString(index++, fm.getVanCode());
					ps.setBoolean(index++, fm.isSanBsdSchdle());
					ps.setLong(index++, JdbcUtil.setLong(fm.getPromotionSeqId()));
					ps.setBigDecimal(index++, fm.getSvAmount());
					ps.setBigDecimal(index++, fm.getCbAmount());
					ps.setBoolean(index++, fm.isAlwGrcAdj());
					ps.setBoolean(index++, fm.isEndGrcPeriodAftrFullDisb());
					ps.setBoolean(index++, fm.isAutoIncGrcEndDate());
					ps.setBoolean(index++, fm.isPlanEMIHAlwInGrace());

					// HL
					ps.setBoolean(index++, fm.isFinOcrRequired());
					ps.setBigDecimal(index++, fm.getReqLoanAmt());
					ps.setInt(index++, fm.getReqLoanTenor());
					ps.setString(index++, fm.getOfferProduct());
					ps.setBigDecimal(index++, fm.getOfferAmount());
					ps.setString(index++, fm.getCustSegmentation());
					ps.setString(index++, fm.getBaseProduct());
					ps.setString(index++, fm.getProcessType());
					ps.setString(index++, fm.getBureauTimeSeries());
					ps.setString(index++, fm.getCampaignName());
					ps.setString(index++, fm.getExistingLanRefNo());
					ps.setString(index++, fm.getLeadSource());
					ps.setString(index++, fm.getPoSource());
					ps.setBoolean(index++, fm.isRsa());
					ps.setString(index++, fm.getVerification());
					ps.setString(index++, fm.getSourcingBranch());
					ps.setString(index++, fm.getSourChannelCategory());
					ps.setLong(index++, fm.getAsmName());
					ps.setString(index++, fm.getOfferId());
					ps.setBoolean(index++, fm.isPmay());
					ps.setBoolean(index++, fm.isAlwGrcAdj());
					ps.setBoolean(index++, fm.isEndGrcPeriodAftrFullDisb());
					ps.setBoolean(index++, fm.isAutoIncGrcEndDate());
					ps.setString(index++, fm.getParentRef());
					ps.setBoolean(index++, fm.isLoanSplitted());
					ps.setBoolean(index++, fm.isAlwLoanSplit());
					ps.setBoolean(index++, fm.isInstBasedSchd());

				}
				ps.setInt(index++, fm.getVersion());
				ps.setLong(index++, JdbcUtil.setLong(fm.getLastMntBy()));
				ps.setTimestamp(index++, fm.getLastMntOn());
				ps.setString(index++, fm.getRecordStatus());
				ps.setString(index++, fm.getRoleCode());
				ps.setString(index++, fm.getNextRoleCode());
				ps.setString(index++, fm.getTaskId());
				ps.setString(index++, fm.getNextTaskId());
				ps.setString(index++, fm.getRecordType());
				ps.setLong(index++, JdbcUtil.setLong(fm.getWorkflowId()));
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return fm.getId();
	}

	@Override
	public void update(FinanceMain financeMain, TableType tableType, boolean wif) {
		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update ");
		if (wif) {
			sql.append(" WIFFinanceMain");
		} else {
			sql.append(" FinanceMain");
		}
		sql.append(tableType.getSuffix());
		sql.append(" set NumberOfTerms = :NumberOfTerms,GraceTerms=:GraceTerms, ");
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
		sql.append(" disbAccountId= :disbAccountId, repayAccountId= :repayAccountId,");
		sql.append(" LastRepayDate= :LastRepayDate,LastRepayPftDate= :LastRepayPftDate,");
		sql.append(
				" LastRepayRvwDate= :LastRepayRvwDate, LastRepayCpzDate= :LastRepayCpzDate,AllowGrcRepay= :AllowGrcRepay,");
		sql.append(" GrcSchdMthd= :GrcSchdMthd, GrcMargin= :GrcMargin,RepayMargin= :RepayMargin,");
		sql.append(
				" FinCommitmentRef= :FinCommitmentRef, FinLimitRef=:FinLimitRef, DepreciationFrq= :DepreciationFrq, FinCurrAssetValue= :FinCurrAssetValue,");
		sql.append(" NextDepDate= :NextDepDate, LastDepDate= :LastDepDate,FinAccount=:FinAccount, ");
		sql.append(
				" FinCustPftAccount=:FinCustPftAccount,ClosingStatus= :ClosingStatus, FinApprovedDate= :FinApprovedDate,");
		sql.append(
				" AnualizedPercRate =:AnualizedPercRate , EffectiveRateOfReturn =:EffectiveRateOfReturn , FinRepayPftOnFrq =:FinRepayPftOnFrq ,");
		sql.append(
				" GrcProfitDaysBasis = :GrcProfitDaysBasis, StepFinance = :StepFinance, StepPolicy = :StepPolicy, StepType = :StepType,");
		sql.append(
				" AlwManualSteps = :AlwManualSteps, NoOfSteps = :NoOfSteps, ManualSchedule=:ManualSchedule , TakeOverFinance=:TakeOverFinance ,LinkedFinRef=:LinkedFinRef, ");
		sql.append(
				" GrcMinRate=:GrcMinRate, GrcMaxRate=:GrcMaxRate ,GrcMaxAmount=:GrcMaxAmount, RpyMinRate=:RpyMinRate, RpyMaxRate=:RpyMaxRate, ");
		sql.append(" GrcAdvBaseRate=:GrcAdvBaseRate ,GrcAdvMargin=:GrcAdvMargin , ");
		sql.append(
				" GrcAdvPftRate=:GrcAdvPftRate ,RpyAdvBaseRate=:RpyAdvBaseRate ,RpyAdvMargin=:RpyAdvMargin ,RpyAdvPftRate=:RpyAdvPftRate ,");
		sql.append(" SupplementRent=:SupplementRent , IncreasedCost=:IncreasedCost , ");
		sql.append(
				" FeeAccountId=:FeeAccountId , MinDownPayPerc=:MinDownPayPerc, TDSApplicable=:TDSApplicable,InsuranceAmt=:InsuranceAmt, AlwBPI=:AlwBPI , ");
		sql.append(
				" BpiTreatment=:BpiTreatment , PlanEMIHAlw=:PlanEMIHAlw , PlanEMIHMethod=:PlanEMIHMethod , PlanEMIHMaxPerYear=:PlanEMIHMaxPerYear , ");
		sql.append(
				" PlanEMIHMax=:PlanEMIHMax , PlanEMIHLockPeriod=:PlanEMIHLockPeriod , PlanEMICpz=:PlanEMICpz , CalRoundingMode=:CalRoundingMode ,RoundingTarget=:RoundingTarget, AlwMultiDisb=:AlwMultiDisb, FeeChargeAmt=:FeeChargeAmt, BpiAmount=:BpiAmount, DeductFeeDisb=:DeductFeeDisb, ");
		sql.append(
				"RvwRateApplFor =:RvwRateApplFor, SchCalOnRvw =:SchCalOnRvw,PastduePftCalMthd=:PastduePftCalMthd,DroppingMethod=:DroppingMethod,RateChgAnyDay=:RateChgAnyDay,PastduePftMargin=:PastduePftMargin,  FinCategory=:FinCategory, ProductCategory=:ProductCategory, AllowDrawingPower =:AllowDrawingPower, AllowRevolving =:AllowRevolving, AppliedLoanAmt =:AppliedLoanAmt, CbAmount =:CbAmount, FinIsRateRvwAtGrcEnd =:FinIsRateRvwAtGrcEnd,");
		if (!wif) {
			sql.append(
					" DroplineFrq= :DroplineFrq,FirstDroplineDate = :FirstDroplineDate,PftServicingODLimit = :PftServicingODLimit,");
			sql.append(
					" MigratedFinance = :MigratedFinance,ScheduleMaintained = :ScheduleMaintained, ScheduleRegenerated = :ScheduleRegenerated,FinCancelAc=:FinCancelAc,");
			sql.append(
					" LimitValid= :LimitValid, OverrideLimit= :OverrideLimit,FinPurpose=:FinPurpose, DeviationApproval=:DeviationApproval,FinPreApprovedRef=:FinPreApprovedRef, MandateID=:MandateID, ");
			sql.append(
					" FinStatus=:FinStatus , FinStsReason=:FinStsReason, InitiateUser=:InitiateUser, BankName=:BankName, Iban=:Iban, AccountType=:AccountType,  DdaReferenceNo=:DdaReferenceNo,");
			sql.append(" CustDSR=:CustDSR, JointAccount=:JointAccount, JointCustId=:JointCustId, ");
			sql.append(
					" DownPayAccount=:DownPayAccount,  SecurityDeposit = :SecurityDeposit, RcdMaintainSts=:RcdMaintainSts, FinRepayMethod=:FinRepayMethod, ");
			sql.append(
					" NextUserId=:NextUserId, Priority=:Priority, RolloverFrq=:RolloverFrq, NextRolloverDate=:NextRolloverDate, ShariaStatus = :ShariaStatus,InitiateDate= :InitiateDate, ");
			sql.append(
					" MMAId =:MMAId,AccountsOfficer =:AccountsOfficer,DsaCode = :DsaCode, ApplicationNo=:ApplicationNo, ReferralId =:ReferralId ,EmployeeName =:EmployeeName, DmaCode =:DmaCode , SalesDepartment =:SalesDepartment , QuickDisb =:QuickDisb , WifReference =:WifReference ,");
			sql.append(
					" UnPlanEMIHLockPeriod=:UnPlanEMIHLockPeriod , UnPlanEMICpz=:UnPlanEMICpz, ReAgeCpz=:ReAgeCpz, MaxUnplannedEmi=:MaxUnplannedEmi, MaxReAgeHolidays=:MaxReAgeHolidays ,");
			sql.append(
					" AvailedUnPlanEmi=:AvailedUnPlanEmi, AvailedReAgeH=:AvailedReAgeH,ReAgeBucket=:ReAgeBucket,EligibilityMethod=:EligibilityMethod,samplingRequired=:samplingRequired,legalRequired=:legalRequired,connector=:connector, ProcessAttributes=:ProcessAttributes,");
			sql.append(
					" TDSPercentage = :TdsPercentage, TdsStartDate = :TdsStartDate, TdsEndDate = :TdsEndDate, TdsLimitAmt = :TdsLimitAmt");
			sql.append(", VanReq =:VanReq, VanCode =:VanCode, PlanEMIHAlwInGrace = :PlanEMIHAlwInGrace");

			//HL
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
			sql.append(", AlwLoanSplit = :AlwLoanSplit, InstBasedSchd=:InstBasedSchd, ");
		}
		sql.append(
				"AdvanceEMI = :AdvanceEMI, BpiPftDaysBasis = :BpiPftDaysBasis, FixedTenorRate=:FixedTenorRate, FixedRateTenor=:FixedRateTenor");
		sql.append(
				", GrcAdvType = :GrcAdvType, GrcAdvTerms = :GrcAdvTerms, AdvType = :AdvType, AdvTerms = :AdvTerms, AdvStage = :AdvStage");
		sql.append(", PromotionCode = :PromotionCode, SanBsdSchdle=:SanBsdSchdle ");

		// For InActive Loans, Update Loan Closed Date
		if (!financeMain.isFinIsActive()) {
			if (financeMain.getClosedDate() == null) {
				financeMain.setClosedDate(SysParamUtil.getAppDate());
			}
			sql.append(", ClosedDate = :ClosedDate ");
		}

		sql.append(", Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where FinReference = :FinReference");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeMain);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public void delete(FinanceMain financeMain, TableType tableType, boolean wif, boolean finalize) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from");
		if (wif) {
			sql.append(" WIFFinanceMain");
		} else {
			sql.append(" FinanceMain");
		}
		sql.append(tableType.getSuffix());
		// TODO: Srikanth to check the Reject Loan API case.
		sql.append(" where FinReference = :FinReference");
		if (tableType == TableType.MAIN_TAB || !finalize) {
			sql.append(QueryUtil.getConcurrencyCondition(tableType));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeMain);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method updates the Record FinanceMain or FinanceMain_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Main Detail by key FinReference and Version
	 * 
	 * @param Finance
	 *            Main Detail (financeMain)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	public void updateInvestmentFinance(FinanceMain financeMain, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update ");

		updateSql.append(" FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinType = :FinType,  FinCcy = :FinCcy, FinBranch = :FinBranch,");
		updateSql.append(" FinAmount = :FinAmount, FinStartDate = :FinStartDate, ");
		updateSql.append(" MaturityDate = :MaturityDate, CustID = :CustID,");
		updateSql.append(" RepayProfitRate = :RepayProfitRate, TotalRepayAmt= :TotalRepayAmt, ");
		updateSql.append(" TotalProfit = :TotalProfit, ProfitDaysBasis= :ProfitDaysBasis, ");
		updateSql.append(" ScheduleMethod = :ScheduleMethod, ");
		updateSql.append(" DisbAccountId = :DisbAccountId, RepayAccountId= :RepayAccountId, ");
		updateSql.append(" LastRepayDate = :LastRepayDate, LastRepayPftDate = :LastRepayPftDate, ");
		updateSql.append(" LastRepayRvwDate = :LastRepayRvwDate, LastRepayCpzDate = :LastRepayCpzDate, ");
		updateSql.append(" NumberOfTerms = :NumberOfTerms, GraceTerms=:GraceTerms, AllowGrcPeriod = :AllowGrcPeriod, ");
		updateSql.append(" AllowGrcPftRvw = :AllowGrcPftRvw, AllowGrcCpz = :AllowGrcCpz, ");
		updateSql.append(" AllowRepayRvw = :AllowRepayRvw, AllowRepayCpz = :AllowRepayCpz, ");
		updateSql.append(" CpzAtGraceEnd = :CpzAtGraceEnd, CalTerms = :CalTerms, ");
		updateSql.append(" Defferments = :Defferments, PlanDeferCount = :PlanDeferCount, ");
		updateSql.append(" AllowedDefRpyChange = :AllowedDefRpyChange, AvailedDefRpyChange = :AvailedDefRpyChange, ");
		updateSql.append(" AllowedDefFrqChange = :AllowedDefFrqChange, AvailedDefFrqChange = :AvailedDefFrqChange, ");
		updateSql.append(" FinIsActive = :FinIsActive, AllowGrcRepay = :AllowGrcRepay, ");
		updateSql.append(" FinRepayPftOnFrq = :FinRepayPftOnFrq, ");
		updateSql.append(" MigratedFinance = :MigratedFinance, ScheduleMaintained = :ScheduleMaintained, ");
		updateSql.append(
				" ScheduleRegenerated = :ScheduleRegenerated, Blacklisted = :Blacklisted, GrcProfitDaysBasis = :GrcProfitDaysBasis,");
		updateSql.append(
				" StepFinance = :StepFinance, StepPolicy = :StepPolicy, AlwManualSteps = :AlwManualSteps, NoOfSteps = :NoOfSteps, StepType = :StepType, DsaCode = :DsaCode, ");
		updateSql.append(
				" DroplineFrq= :DroplineFrq,FirstDroplineDate = :FirstDroplineDate,PftServicingODLimit = :PftServicingODLimit,");
		updateSql.append(" Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, NextUserId=:NextUserId, Priority=:Priority, MinDownPayPerc=:MinDownPayPerc");
		updateSql.append(", PromotionCode = :PromotionCode");
		updateSql.append(" Where FinReference =:FinReference");
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to return the customer details based on given customer id.
	 */
	public boolean isFinReferenceExists(final String id, String type, boolean isWIF) {
		logger.debug("Entering");

		FinanceMain fm = null;

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference");

		if (isWIF) {
			sql.append(" From WIFFinanceMain");
		} else {
			sql.append(" From FinanceMain");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());
		try {
			fm = this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id }, new RowMapper<FinanceMain>() {
				@Override
				public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceMain fm = new FinanceMain();

					fm.setFinReference(rs.getString("FinReference"));

					return fm;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		if (fm == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void listUpdate(ArrayList<FinanceMain> financeMain, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set NumberOfTerms = :NumberOfTerms, GraceTerms=:GraceTerms, ");
		updateSql.append(" GrcPeriodEndDate = :GrcPeriodEndDate, AllowGrcPeriod = :AllowGrcPeriod,");
		updateSql.append(" GraceBaseRate = :GraceBaseRate, GraceSpecialRate = :GraceSpecialRate,");
		updateSql.append(" GrcPftRate = :GrcPftRate, GrcPftFrq = :GrcPftFrq,");
		updateSql.append(" NextGrcPftDate = :NextGrcPftDate, AllowGrcPftRvw = :AllowGrcPftRvw,");
		updateSql.append(" GrcPftRvwFrq = :GrcPftRvwFrq, NextGrcPftRvwDate = :NextGrcPftRvwDate,");
		updateSql.append(" AllowGrcCpz = :AllowGrcCpz, GrcCpzFrq = :GrcCpzFrq, NextGrcCpzDate = :NextGrcCpzDate,");
		updateSql.append(" RepayBaseRate = :RepayBaseRate, RepaySpecialRate = :RepaySpecialRate,");
		updateSql.append(" RepayProfitRate = :RepayProfitRate, RepayFrq = :RepayFrq, NextRepayDate = :NextRepayDate,");
		updateSql.append(" RepayPftFrq = :RepayPftFrq, NextRepayPftDate = :NextRepayPftDate,");
		updateSql.append(" AllowRepayRvw = :AllowRepayRvw, RepayRvwFrq = :RepayRvwFrq,");
		updateSql.append(" NextRepayRvwDate = :NextRepayRvwDate, AllowRepayCpz = :AllowRepayCpz,");
		updateSql.append(" RepayCpzFrq = :RepayCpzFrq, NextRepayCpzDate = :NextRepayCpzDate,");
		updateSql.append(" MaturityDate = :MaturityDate, CpzAtGraceEnd = :CpzAtGraceEnd, DownPayment = :DownPayment,");
		updateSql.append(
				" DownPayBank=:DownPayBank, DownPaySupl=:DownPaySupl, DownPayAccount=:DownPayAccount,  SecurityDeposit = :SecurityDeposit,");
		updateSql.append(
				" ReqRepayAmount = :ReqRepayAmount, TotalProfit = :TotalProfit,TotalCpz= :TotalCpz, TotalGrossPft = :TotalGrossPft,");
		updateSql.append(
				" TotalGracePft= :TotalGracePft,TotalGraceCpz= :TotalGraceCpz,TotalGrossGrcPft= :TotalGrossGrcPft,");
		updateSql.append(
				" TotalRepayAmt= :TotalRepayAmt, GrcRateBasis = :GrcRateBasis, RepayRateBasis = :RepayRateBasis, FinType = :FinType,");
		updateSql.append(" FinRemarks = :FinRemarks, FinCcy = :FinCcy, ScheduleMethod = :ScheduleMethod,");
		updateSql.append(" ProfitDaysBasis = :ProfitDaysBasis, ReqMaturity = :ReqMaturity, CalTerms = :CalTerms,");
		updateSql.append(" CalMaturity = :CalMaturity, FirstRepay = :FirstRepay, LastRepay = :LastRepay,");
		updateSql.append(" FinStartDate = :FinStartDate, FinAmount = :FinAmount,FinContractDate=:FinContractDate,");
		updateSql.append(" FinRepaymentAmount = :FinRepaymentAmount, CustID = :CustID,Defferments = :Defferments, ");
		updateSql.append(" PlanDeferCount= :PlanDeferCount, FinBranch =:FinBranch, FinSourceID= :FinSourceID,");
		updateSql.append(" AllowedDefRpyChange= :AllowedDefRpyChange, AvailedDefRpyChange= :AvailedDefRpyChange,");
		updateSql.append(" AllowedDefFrqChange= :AllowedDefFrqChange, AvailedDefFrqChange= :AvailedDefFrqChange,");
		updateSql.append(" RecalType=:RecalType, FinIsActive= :FinIsActive,FinAssetValue= :FinAssetValue,");
		updateSql.append(" disbAccountId= :disbAccountId, repayAccountId= :repayAccountId,FinPurpose=:FinPurpose,");
		updateSql.append(
				" LastRepayDate= :LastRepayDate,LastRepayPftDate= :LastRepayPftDate, FinStatus=:FinStatus, FinStsReason=:FinStsReason,");
		updateSql.append(
				" InitiateUser=:InitiateUser, LastRepayRvwDate= :LastRepayRvwDate, LastRepayCpzDate= :LastRepayCpzDate,AllowGrcRepay= :AllowGrcRepay,");
		updateSql.append(
				" BankName=:BankName, Iban=:Iban, AccountType=:AccountType, DdaReferenceNo=:DdaReferenceNo, GrcSchdMthd= :GrcSchdMthd, GrcMargin= :GrcMargin, RepayMargin= :RepayMargin,");
		updateSql.append(
				" FinCommitmentRef= :FinCommitmentRef, FinLimitRef=:FinLimitRef, DepreciationFrq= :DepreciationFrq, FinCurrAssetValue= :FinCurrAssetValue,");
		updateSql.append(
				" NextDepDate= :NextDepDate, LastDepDate= :LastDepDate,FinAccount=:FinAccount,FinCancelAc=:FinCancelAc,");
		updateSql.append(" FinCustPftAccount= :FinCustPftAccount, ClosingStatus= :ClosingStatus, ");
		updateSql.append(
				" FinApprovedDate= :FinApprovedDate, FeeChargeAmt=:FeeChargeAmt, BpiAmount=:BpiAmount, DeductFeeDisb=:DeductFeeDisb, LimitValid= :LimitValid, OverrideLimit= :OverrideLimit,");
		updateSql.append(" AnualizedPercRate =:AnualizedPercRate , EffectiveRateOfReturn =:EffectiveRateOfReturn , ");
		updateSql.append(" FinRepayPftOnFrq =:FinRepayPftOnFrq , CustDSR=:CustDSR, ");
		updateSql.append(
				" JointAccount=:JointAccount,JointCustId=:JointCustId, DownPayAccount=:DownPayAccount,  SecurityDeposit =:SecurityDeposit, RcdMaintainSts=:RcdMaintainSts,");
		updateSql.append(" FinRepayMethod=:FinRepayMethod, ");
		updateSql.append(
				" GrcProfitDaysBasis = :GrcProfitDaysBasis, StepFinance = :StepFinance, StepPolicy = :StepPolicy, StepType = :StepType,");
		updateSql.append(
				" AlwManualSteps = :AlwManualSteps, NoOfSteps = :NoOfSteps,  ManualSchedule=:ManualSchedule , TakeOverFinance=:TakeOverFinance ,");
		updateSql.append(" LinkedFinRef=:LinkedFinRef,");
		updateSql.append(
				" GrcMinRate=:GrcMinRate, GrcMaxRate=:GrcMaxRate ,GrcMaxAmount=:GrcMaxAmount, RpyMinRate=:RpyMinRate, RpyMaxRate=:RpyMaxRate, ");
		updateSql.append(" GrcAdvBaseRate=:GrcAdvBaseRate ,GrcAdvMargin=:GrcAdvMargin , ");
		updateSql.append(
				" GrcAdvPftRate=:GrcAdvPftRate ,RpyAdvBaseRate=:RpyAdvBaseRate ,RpyAdvMargin=:RpyAdvMargin ,RpyAdvPftRate=:RpyAdvPftRate ,");
		updateSql.append(" SupplementRent=:SupplementRent , IncreasedCost=:IncreasedCost , RolloverFrq=:RolloverFrq, ");
		updateSql.append(" NextRolloverDate=:NextRolloverDate, ShariaStatus = :ShariaStatus, DsaCode = :DsaCode, ");
		updateSql.append(
				" DroplineFrq= :DroplineFrq,FirstDroplineDate = :FirstDroplineDate,PftServicingODLimit = :PftServicingODLimit, AlwBPI=:AlwBPI , ");
		updateSql.append(
				" BpiTreatment=:BpiTreatment , PlanEMIHAlw=:PlanEMIHAlw , PlanEMIHAlwInGrace=:PlanEMIHAlwInGrace,PlanEMIHMethod=:PlanEMIHMethod , PlanEMIHMaxPerYear=:PlanEMIHMaxPerYear , ");
		updateSql.append(
				" PlanEMIHMax=:PlanEMIHMax , PlanEMIHLockPeriod=:PlanEMIHLockPeriod , PlanEMICpz=:PlanEMICpz , CalRoundingMode=:CalRoundingMode ,RoundingTarget=:RoundingTarget, AlwMultiDisb=:AlwMultiDisb, ApplicationNo=:ApplicationNo,");
		updateSql.append(
				" ReferralId =:ReferralId , DmaCode =:DmaCode , SalesDepartment =:SalesDepartment , QuickDisb =:QuickDisb , WifReference =:WifReference ,");
		updateSql.append(
				" UnPlanEMIHLockPeriod=:UnPlanEMIHLockPeriod , UnPlanEMICpz=:UnPlanEMICpz, ReAgeCpz=:ReAgeCpz, MaxUnplannedEmi=:MaxUnplannedEmi, MaxReAgeHolidays=:MaxReAgeHolidays ,AvailedUnPlanEmi=:AvailedUnPlanEmi, AvailedReAgeH=:AvailedReAgeH, ReAgeBucket=:ReAgeBucket, FinCategory=:FinCategory, ProductCategory=:ProductCategory, ");
		updateSql.append(
				" Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, NextUserId=:NextUserId, Priority=:Priority, RecordType = :RecordType, WorkflowId = :WorkflowId, MinDownPayPerc=:MinDownPayPerc");
		updateSql.append(
				" PromotionCode = :PromotionCode, AdvanceEMI = :AdvanceEMI, BpiPftDaysBasis= :BpiPftDaysBasis, FixedTenorRate=:FixedTenorRate, FixedRateTenor=:FixedRateTenor");
		updateSql.append(", SanBsdSchdle=:SanBsdSchdle");
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(financeMain.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void updateFinAccounts(String finReference, String finAccount) {
		int recordCount = 0;

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setFinAccount(finAccount);

		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinAccount =:FinAccount ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for get the Actual Profit Amount
	 * 
	 * @throws ClassNotFoundException
	 * @throws DataAccessException
	 */
	@Override
	public List<BigDecimal> getActualPftBal(String finReference, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TotalProfit, TotalCpz");
		sql.append(" From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());
		try {
			financeMain = this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<FinanceMain>() {
						@Override
						public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinanceMain fm = new FinanceMain();

							fm.setTotalProfit(rs.getBigDecimal("TotalProfit"));
							fm.setTotalCpz(rs.getBigDecimal("TotalCpz"));

							return fm;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
		List<BigDecimal> list = new ArrayList<BigDecimal>();
		if (financeMain != null) {
			list.add(financeMain.getTotalProfit());
			list.add(financeMain.getTotalCpz());
		} else {
			list.add(BigDecimal.ZERO);
			list.add(BigDecimal.ZERO);
		}

		return list;
	}

	@Override
	public void updateRepaymentAmount(String finReference, BigDecimal finAmount, BigDecimal repaymentAmount,
			String finStatus, String finStsReason, boolean isCancelProc, boolean pftFullyPaid) {
		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setFinRepaymentAmount(repaymentAmount);
		financeMain.setFinStatus(finStatus);
		financeMain.setFinStsReason(finStsReason);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinRepaymentAmount =:FinRepaymentAmount ");
		if (finAmount.subtract(repaymentAmount).compareTo(BigDecimal.ZERO) <= 0) {
			if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCIP)) {
				financeMain.setFinIsActive(false);
				financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
				updateSql.append(" , FinIsActive = :FinIsActive, ClosingStatus =:ClosingStatus ");
			} else if (pftFullyPaid
					&& ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCPI)) {
				financeMain.setFinIsActive(false);
				financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
				updateSql.append(" , FinIsActive = :FinIsActive, ClosingStatus =:ClosingStatus ");
			}
		} else if (isCancelProc) {
			financeMain.setFinIsActive(true);
			financeMain.setClosingStatus(null);
			updateSql.append(" , FinIsActive = :FinIsActive, ClosingStatus =:ClosingStatus ");
		}

		// For InActive Loans, Update Loan Closed Date
		if (!financeMain.isFinIsActive()) {
			financeMain.setClosedDate(SysParamUtil.getAppDate());
			updateSql.append(", ClosedDate = :ClosedDate ");
		}

		updateSql.append(" , FinStatus = :FinStatus , FinStsReason = :FinStsReason ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	/**
	 * Method for get the Finance Details and FinanceShedule Details
	 */
	@Override
	public List<FinanceEnquiry> getFinanceDetailsByCustId(long custId) {
		logger.debug(Literal.ENTERING);

		List<FinanceEnquiry> finEnquiry = null;

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinBranch, FinType, FinCcy, ScheduleMethod, ProfitDaysBasis");
		sql.append(", FinStartDate, NumberOfTerms, CustID, FinAmount, GrcPeriodEndDate, MaturityDate");
		sql.append(", FinRepaymentAmount, FinIsActive, AllowGrcPeriod, LovDescFinTypeName, LovDescCustCIF");
		sql.append(", LovDescCustShrtName, LovDescFinBranchName, Blacklisted, LovDescFinScheduleOn");
		sql.append(", FeeChargeAmt, ClosingStatus, CustTypeCtg, GraceTerms, LovDescFinDivision");
		sql.append(", LovDescProductCodeName, Defferments, SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount");
		sql.append(" from FinanceEnquiry_View");
		sql.append(" Where CustID = ?");
		sql.append(" and (ClosingStatus is null or ClosingStatus != 'C')");
		sql.append(" ORDER BY FinType, FinCcy ");
		logger.trace(Literal.SQL + sql.toString());
		try {
			finEnquiry = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, custId);
				}
			}, new RowMapper<FinanceEnquiry>() {
				@Override
				public FinanceEnquiry mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceEnquiry finEnquiry = new FinanceEnquiry();

					finEnquiry.setFinReference(rs.getString("FinReference"));
					finEnquiry.setFinBranch(rs.getString("FinBranch"));
					finEnquiry.setFinType(rs.getString("FinType"));
					finEnquiry.setFinCcy(rs.getString("FinCcy"));
					finEnquiry.setScheduleMethod(rs.getString("ScheduleMethod"));
					finEnquiry.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
					finEnquiry.setFinStartDate(rs.getTimestamp("FinStartDate"));
					finEnquiry.setNumberOfTerms(rs.getInt("NumberOfTerms"));
					finEnquiry.setCustID(rs.getLong("CustID"));
					finEnquiry.setFinAmount(rs.getBigDecimal("FinAmount"));
					finEnquiry.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
					finEnquiry.setMaturityDate(rs.getTimestamp("MaturityDate"));
					finEnquiry.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
					finEnquiry.setFinIsActive(rs.getBoolean("FinIsActive"));
					finEnquiry.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
					finEnquiry.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
					finEnquiry.setLovDescCustCIF(rs.getString("LovDescCustCIF"));
					finEnquiry.setLovDescCustShrtName(rs.getString("LovDescCustShrtName"));
					finEnquiry.setLovDescFinBranchName(rs.getString("LovDescFinBranchName"));
					finEnquiry.setBlacklisted(rs.getBoolean("Blacklisted"));
					finEnquiry.setLovDescFinScheduleOn(rs.getString("LovDescFinScheduleOn"));
					finEnquiry.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
					finEnquiry.setClosingStatus(rs.getString("ClosingStatus"));
					finEnquiry.setCustTypeCtg(rs.getString("CustTypeCtg"));
					finEnquiry.setGraceTerms(rs.getInt("GraceTerms"));
					finEnquiry.setLovDescFinDivision(rs.getString("lovDescFinDivision"));
					finEnquiry.setLovDescProductCodeName(rs.getString("lovDescProductCodeName"));
					finEnquiry.setDefferments(rs.getInt("Defferments"));
					finEnquiry.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
					finEnquiry.setPromotionSeqId(rs.getInt("PromotionSeqId"));
					finEnquiry.setSvAmount(rs.getBigDecimal("SvAmount"));
					finEnquiry.setCbAmount(rs.getBigDecimal("CbAmount"));

					return finEnquiry;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return finEnquiry;

	}

	@Override
	public void updateCustCIF(long custID, String finReference) {
		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setCustID(custID);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain_Temp");
		updateSql.append(" Set CustID =:CustID ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateFinBlackListStatus(String finReference) {
		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setBlacklisted(true);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set Blacklisted =:Blacklisted ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<String> getFinanceReferenceList() {
		logger.debug(Literal.ENTERING);
		List<String> fm = null;
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference");
		sql.append(" From FinanceMain");
		logger.trace(Literal.SQL + sql.toString());
		try {
			fm = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					// FIXME
				}
			}, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {

					return rs.getString("FinReference");
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return fm;

	}

	@Override
	public FinanceSummary getFinanceProfitDetails(String finRef) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select * from FinanceProfitEnquiry_View");
		sql.append(" where FinReference = :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("FinReference", finRef);

		RowMapper<FinanceSummary> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceSummary.class);

		FinanceSummary summary = new FinanceSummary();
		try {
			summary = this.jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return summary;
	}

	/**
	 * Method for fetching List Of IJARAH Finance for Bulk Rate Change
	 */
	@Override
	public List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate) {
		logger.debug("Entering");

		BulkProcessDetails changeFinance = new BulkProcessDetails();
		changeFinance.setLovDescEventFromDate(fromDate);
		changeFinance.setLovDescEventToDate(toDate);

		StringBuilder selectSql = new StringBuilder(" SELECT FinReference, FinType, ");
		selectSql.append(" FinCcy, ScheduleMethod, ProfitDaysBasis, CustCIF, FinBranch, ");
		selectSql.append(" ProductCode,  MIN(SchDate) EventFromDate, MAX(SchDate) EventToDate ");
		selectSql.append(
				" FROM IjarahFinance_View WHERE SchDate BETWEEN :LovDescEventFromDate AND :LovDescEventToDate ");
		selectSql.append(
				" GROUP BY FinReference ,FinType ,FinCcy ,ScheduleMethod , ProfitDaysBasis ,CustCIF ,FinBranch ,ProductCode ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(changeFinance);
		RowMapper<BulkProcessDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BulkProcessDetails.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for Fetch List of Finance for Bulk Deferment Process
	 */
	@Override
	public List<BulkDefermentChange> getBulkDefermentFinList(Date fromDate, Date toDate) {
		logger.debug("Entering");

		BulkDefermentChange changeFinance = new BulkDefermentChange();
		changeFinance.setEventFromDate(fromDate);
		changeFinance.setEventToDate(toDate);

		StringBuilder selectSql = new StringBuilder(" SELECT FinReference, FinType, ");
		selectSql.append(" FinCcy, ScheduleMethod, ProfitDaysBasis, CustCIF, FinBranch, ");
		selectSql.append(" ProductCode, EventFromDate ");
		selectSql.append(
				" FROM BulkDefermentFinance_View WHERE EventFromDate BETWEEN :EventFromDate AND :EventToDate )");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(changeFinance);
		RowMapper<BulkDefermentChange> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BulkDefermentChange.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Reject Finance Details Saving For Reinstance of Finance Process
	 */
	public Boolean saveRejectFinanceDetails(FinanceMain financeMain) {
		logger.debug("Entering");

		saveRejectFinanace(financeMain);

		saveRejectedChildDetail(
				"INSERT INTO RejectDocumentDetails SELECT * FROM  DocumentDetails_Temp WHERE ReferenceId = :FinReference",
				financeMain);
		saveFinanceDetails("FinAgreementDetail_Temp", "RejectFinAgreementDetail", financeMain);
		saveFinanceDetails("FinanceEligibilityDetail", "RejectFinanceEligibilityDetail", financeMain);
		saveFinanceDetails("FinanceScoreHeader", "RejectFinanceScoreHeader", financeMain);
		saveFinanceDetails("FinContributorHeader_Temp", "RejectFinContributorHeader", financeMain);
		saveFinanceDetails("FinContributorDetail_Temp", "RejectFinContributorDetail", financeMain);
		saveFinanceDetails("FinDisbursementDetails_Temp", "RejectFinDisbursementdetails", financeMain);
		saveFinanceDetails("FinRepayinstruction_Temp", "RejectFinRepayinstruction", financeMain);
		saveFinanceDetails("FinScheduledetails_Temp", "RejectFinScheduledetails", financeMain);
		saveFinanceDetails("FinDedupDetail", "RejectFinDedupDetail", financeMain);
		saveFinanceDetails("FinBlackListDetail", "RejectFinBlackListDetail", financeMain);
		saveFinanceDetails("FinPoliceCaseDetail", "RejectFinPoliceCaseDetail", financeMain);
		saveFinanceDetails("FinODPenaltyRates_Temp", "RejectFinODPenaltyRates", financeMain);
		saveFinanceDetails("FinFeeCharges_Temp", "RejectFinFeeCharges", financeMain);

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(
				" INSERT INTO RejectFinanceScoreDetail SELECT D.HeaderID,  D.SubGroupID, D.RuleId, D.MaxScore, D.ExecScore ");
		insertSql
				.append(" FROM FinanceScoreDetail D INNER JOIN RejectFinanceScoreHeader H ON D.HeaderID = H.HeaderId ");
		insertSql.append(" WHERE FinReference = :FinReference ");
		saveRejectedChildDetail(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		return true;
	}

	private void saveFinanceDetails(String fromTable, String toTable, FinanceMain financeMain) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" INSERT INTO ");
		insertSql.append(toTable);
		insertSql.append(" SELECT * FROM ");
		insertSql.append(fromTable);
		insertSql.append(" WHERE FinReference = :FinReference ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	private void saveRejectedChildDetail(String insertSql, FinanceMain financeMain) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.jdbcTemplate.update(insertSql, beanParameters);
		logger.debug("Leaving");
	}

	/***
	 * Method to save finance detail snap shot.
	 * 
	 * @param financeMain
	 */
	public void saveFinanceSnapshot(FinanceMain financeMain) {
		logger.debug("Entering");
		saveFinanceDetails("FinScheduledetails", "FinScheduleDetails_Log", financeMain);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinanceMain or FinanceMain_Temp.
	 * 
	 * save Finance Main Detail
	 * 
	 * @param Finance
	 *            Main Detail (financeMain)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public String saveRejectFinanace(FinanceMain financeMain) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" RejectFinanceMain ");
		insertSql.append(" (FinReference, GraceTerms,  NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		insertSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		insertSql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		insertSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		insertSql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		insertSql.append(
				" MaturityDate, CpzAtGraceEnd,DownPayment, DownPayBank, DownPaySupl, ReqRepayAmount, TotalProfit,");
		insertSql.append(" TotalCpz,TotalGrossPft,TotalGracePft, TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		insertSql.append("  GrcRateBasis, RepayRateBasis,FinType,FinRemarks, FinCcy, ScheduleMethod,FinContractDate,");
		insertSql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,");
		insertSql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments,PlanDeferCount,");
		insertSql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		insertSql.append(
				" AvailedDefFrqChange, RecalType, FinIsActive,FinAssetValue, disbAccountId, repayAccountId, FinCancelAc, ");
		insertSql.append(
				" LastRepayDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd,");
		insertSql.append(" GrcMargin, RepayMargin, FinCommitmentRef, FinLimitRef, DepreciationFrq, FinCurrAssetValue,");
		insertSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount, ClosingStatus, FinApprovedDate, ");
		insertSql.append(" DedupFound,SkipDedup,Blacklisted,");
		insertSql.append(
				" GrcProfitDaysBasis, StepFinance , StepPolicy, StepType, AlwManualSteps, NoOfSteps, ManualSchedule , TakeOverFinance ,");
		insertSql.append(" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq, ");
		insertSql.append(" LinkedFinRef, ");
		insertSql.append(" GrcMinRate, GrcMaxRate,GrcMaxAmount, RpyMinRate, RpyMaxRate,  ");
		insertSql.append(
				" GrcAdvBaseRate ,GrcAdvMargin ,GrcAdvPftRate ,RpyAdvBaseRate ,RpyAdvMargin ,RpyAdvPftRate , SupplementRent, IncreasedCost, ");
		insertSql.append(" InvestmentRef, MigratedFinance, ScheduleMaintained, ScheduleRegenerated,CustDSR,");
		insertSql.append(
				" FeeChargeAmt, BpiAmount, DeductFeeDisb, LimitValid, OverrideLimit,FinPurpose,DeviationApproval,FinPreApprovedRef,MandateID,FinStatus, FinStsReason, initiateUser, BankName, Iban, AccountType, DdaReferenceNo, ");
		insertSql.append(
				" JointAccount,JointCustId,DownPayAccount, SecurityDeposit, RcdMaintainSts,FinRepayMethod, AlwBPI , BpiTreatment , PlanEMIHAlw , ");
		insertSql.append(
				" PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax , PlanEMIHLockPeriod , PlanEMICpz , CalRoundingMode ,RoundingTarget, AlwMultiDisb, ");
		insertSql.append(" NextUserId, ");
		insertSql.append(
				" Priority, RolloverFrq, NextRolloverDate, ShariaStatus, DsaCode, feeAccountId,MinDownPayPerc, MMAId, InitiateDate,TDSApplicable,AccountsOfficer,ApplicationNo,");
		insertSql.append(
				" ReferralId, DmaCode, SalesDepartment, QuickDisb, WifReference, UnPlanEMIHLockPeriod , UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi, MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, RvwRateApplFor ,SchCalOnRvw,PastduePftCalMthd,DroppingMethod,RateChgAnyDay,PastduePftMargin,  FinCategory, ProductCategory,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(
				" NextTaskId, RecordType, WorkflowId, RejectStatus, RejectReason, DueBucket, AdvanceEMI , BpiPftDaysBasis, FixedTenorRate,FixedRateTenor,ProcessAttributes, SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount");

		// HL
		insertSql.append(", ReqLoanAmt, ReqLoanTenor, FinOcrRequired, OfferProduct, OfferAmount");
		insertSql.append(", CustSegmentation, BaseProduct, ProcessType, BureauTimeSeries, CampaignName");
		insertSql.append(", ExistingLanRefNo, LeadSource, PoSource, Rsa, Verification, SourcingBranch");
		insertSql.append(", SourChannelCategory, AsmName, OfferId, AlwLoanSplit");
		insertSql.append(", AlwGrcAdj, EndGrcPeriodAftrFullDisb, AutoIncGrcEndDate)");

		insertSql.append(" Values(:FinReference,:GraceTerms, :NumberOfTerms, :GrcPeriodEndDate, :AllowGrcPeriod,");
		insertSql.append(" :GraceBaseRate, :GraceSpecialRate,:GrcPftRate,:GrcPftFrq,:NextGrcPftDate,:AllowGrcPftRvw,");
		insertSql.append(" :GrcPftRvwFrq,:NextGrcPftRvwDate,:AllowGrcCpz,:GrcCpzFrq,:NextGrcCpzDate,:RepayBaseRate,");
		insertSql
				.append(" :RepaySpecialRate,:RepayProfitRate,:RepayFrq,:NextRepayDate,:RepayPftFrq,:NextRepayPftDate,");
		insertSql.append(
				" :AllowRepayRvw,:RepayRvwFrq,:NextRepayRvwDate,:AllowRepayCpz,:RepayCpzFrq,:NextRepayCpzDate,");
		insertSql.append(
				" :MaturityDate,:CpzAtGraceEnd,:DownPayment, :DownPayBank, :DownPaySupl, :ReqRepayAmount,:TotalProfit,");
		insertSql.append(" :TotalCpz,:TotalGrossPft,:TotalGracePft,:TotalGraceCpz,:TotalGrossGrcPft, :TotalRepayAmt,");
		insertSql.append(
				" :GrcRateBasis,:RepayRateBasis, :FinType,:FinRemarks,:FinCcy,:ScheduleMethod,:FinContractDate,");
		insertSql.append(" :ProfitDaysBasis,:ReqMaturity,:CalTerms,:CalMaturity,:FirstRepay,:LastRepay,");
		insertSql.append(" :FinStartDate,:FinAmount,:FinRepaymentAmount,:CustID,:Defferments,:PlanDeferCount,");
		insertSql
				.append(" :FinBranch, :FinSourceID, :AllowedDefRpyChange, :AvailedDefRpyChange, :AllowedDefFrqChange,");
		insertSql.append(
				" :AvailedDefFrqChange, :RecalType, :FinIsActive,:FinAssetValue, :disbAccountId, :repayAccountId,:FinCancelAc, ");
		insertSql.append(
				" :LastRepayDate, :LastRepayPftDate, :LastRepayRvwDate, :LastRepayCpzDate,:AllowGrcRepay, :GrcSchdMthd,");
		insertSql.append(
				" :GrcMargin, :RepayMargin, :FinCommitmentRef, :FinLimitRef, :DepreciationFrq, :FinCurrAssetValue,");
		insertSql.append(
				" :NextDepDate, :LastDepDate, :FinAccount, :FinCustPftAccount, :ClosingStatus , :FinApprovedDate, ");
		insertSql.append(" :DedupFound,:SkipDedup,:Blacklisted,");
		insertSql.append(
				" :GrcProfitDaysBasis, :StepFinance , :StepPolicy, :StepType, :AlwManualSteps, :NoOfSteps, :ManualSchedule , :TakeOverFinance , ");
		insertSql.append(" :AnualizedPercRate , :EffectiveRateOfReturn , :FinRepayPftOnFrq, ");
		insertSql.append(" :LinkedFinRef, ");
		insertSql.append(" :GrcMinRate, :GrcMaxRate ,:GrcMaxAmount, :RpyMinRate, :RpyMaxRate, ");
		insertSql.append(
				" :GrcAdvBaseRate ,:GrcAdvMargin ,:GrcAdvPftRate ,:RpyAdvBaseRate ,:RpyAdvMargin ,:RpyAdvPftRate ,:SupplementRent, :IncreasedCost,  ");
		insertSql.append("  :InvestmentRef, :MigratedFinance, :ScheduleMaintained, :ScheduleRegenerated, :CustDSR,  ");
		insertSql.append(
				" :FeeChargeAmt, :BpiAmount, :DeductFeeDisb, :LimitValid, :OverrideLimit, :FinPurpose,:DeviationApproval,:FinPreApprovedRef,:MandateID,:FinStatus, :FinStsReason, :InitiateUser, :BankName, :Iban, :AccountType, :DdaReferenceNo,");
		insertSql.append(
				" :JointAccount,:JointCustId , :DownPayAccount,  :SecurityDeposit, :RcdMaintainSts,:FinRepayMethod, :AlwBPI , :BpiTreatment , :PlanEMIHAlw , ");
		insertSql.append(
				" :PlanEMIHMethod , :PlanEMIHMaxPerYear , :PlanEMIHMax , :PlanEMIHLockPeriod , :PlanEMICpz , :CalRoundingMode ,:RoundingTarget, :AlwMultiDisb, ");
		insertSql.append(" :NextUserId, ");
		insertSql.append(
				" :Priority,:RolloverFrq, :NextRolloverDate, :ShariaStatus, :DsaCode,:feeAccountId,:MinDownPayPerc,:MMAId,:InitiateDate,:TDSApplicable,:AccountsOfficer, :ApplicationNo,");
		insertSql.append(
				" :ReferralId, :DmaCode, :SalesDepartment, :QuickDisb, :WifReference, :UnPlanEMIHLockPeriod , :UnPlanEMICpz, :ReAgeCpz, :MaxUnplannedEmi, :MaxReAgeHolidays, :AvailedUnPlanEmi, :AvailedReAgeH,:RvwRateApplFor, :SchCalOnRvw,:PastduePftCalMthd,:DroppingMethod,:RateChgAnyDay,:PastduePftMargin, :FinCategory, :ProductCategory,");
		insertSql.append(" :Version ,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(
				" :NextTaskId,:RecordType,:WorkflowId, :RejectStatus, :RejectReason, :DueBucket, :AdvanceEMI , :BpiPftDaysBasis, :FixedTenorRate, :FixedRateTenor, :ProcessAttributes, :SanBsdSchdle, :PromotionSeqId, :SvAmount, :CbAmount");

		// HL
		insertSql.append(", :ReqLoanAmt, :ReqLoanTenor, :FinOcrRequired, :OfferProduct, :OfferAmount");
		insertSql.append(", :CustSegmentation, :BaseProduct, :ProcessType, :BureauTimeSeries, :CampaignName");
		insertSql.append(", :ExistingLanRefNo, :LeadSource, :PoSource, :Rsa, :Verification, :SourcingBranch");
		insertSql.append(", :SourChannelCategory, :AsmName, :OfferId, :AlwLoanSplit");
		insertSql.append(", :AlwGrcAdj, :EndGrcPeriodAftrFullDisb, :AutoIncGrcEndDate)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeMain.getId();
	}

	/**
	 * Fetch the Records Finance Main Detail details by key field
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@Override
	public List<AvailFinance> getFinanceDetailByCmtRef(String cmtRef, long custId) {
		logger.debug("Entering");
		AvailCommitment commitment = new AvailCommitment();
		commitment.setCmtReference(cmtRef);
		commitment.setCustId(custId);

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinReference , FinType,FinCcy , FinAmount,TotalPftSchd, DrawnPrinciple , OutStandingBal, ");
		selectSql.append(
				" CcySpotRate , LastRepay , MaturityDate , ProfitRate , RepayFrq , Status, CcyEditField, FinDivision , FinDivisionDesc");
		selectSql.append(" from AvailFinance_View ");
		selectSql.append(" Where FinCommitmentRef =:CmtReference AND OutStandingBal > 0 ");
		if (custId != 0) {
			selectSql.append(" AND CustId =:CustId ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);
		RowMapper<AvailFinance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AvailFinance.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Fetch the Records/Existing Finance Main Detail details by customer ID
	 */
	@Override
	public List<FinanceSummary> getFinExposureByCustId(long custId) {
		logger.debug("Entering");
		FinanceSummary summary = new FinanceSummary();
		summary.setCustID(custId);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference , FinCommitmentRef , CmtTitle , CustID , ");
		selectSql.append(" NumberOfTerms , FinStartDate , FinType , TotalOriginal , CmtAmount , CmtAvailable , ");
		selectSql.append(" CmtExpiryDate , TotalOutStanding , MaturityDate , TotalRepayAmt , FinStatus ");
		selectSql.append(" from CustFinanceExposure_View ");
		selectSql.append(" Where CustID =:CustID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(summary);
		RowMapper<FinanceSummary> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceSummary.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for Updation of Effective Rate of Return for Finance
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@Override
	public void updateFinanceERR(String finReference, Date lastRepayDate, Date lastRepayPftDate,
			BigDecimal effectiveRate, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setEffectiveRateOfReturn(effectiveRate);
		financeMain.setLastRepayDate(lastRepayDate);
		financeMain.setLastRepayPftDate(lastRepayPftDate);

		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder(" Update FinanceMain ");
		updateSql.append(
				" SET EffectiveRateOfReturn =:EffectiveRateOfReturn, LastRepayDate =:LastRepayDate, LastRepayPftDate=:LastRepayPftDate ");
		updateSql.append(" Where FinReference =:FinReference ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateFinancePriority() {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("update FinanceMain_Temp");
		sql.append(" set Priority = Priority + 1");
		sql.append(" where Priority != 3");
		if (App.DATABASE == Database.ORACLE) {
			sql.append(" and (sysdate - lastmnton) * 24 * 60 > :TIMEINTERVAL");
		} else {
			sql.append(" and DATEDIFF(MINUTE, lastmnton, getdate()) > :TIMEINTERVAL");
		}
		logger.debug("updateSql: " + sql.toString());

		// Get the time interval system parameter for queue priority
		int timeInterval = SysParamUtil.getValueAsInt("QUEUEPRIORITY_TIMEINTERVAL");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("TIMEINTERVAL", timeInterval);

		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * Method for Approval process for LPO Approval Agreement
	 */
	@Override
	public void updateApprovalStatus(String finReference, String approvalStatus) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("ApprovalStatus", approvalStatus);

		StringBuilder sql = new StringBuilder("UPDATE FinanceMain_Temp");
		sql.append(" set Approved = :ApprovalStatus");
		sql.append(" where FinReference = :FinReference");
		logger.debug("updateSql: " + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug("Leaving");
	}

	@Override
	public String getNextRoleCodeByRef(String finReference, String type) {
		logger.debug("Entering");

		String nextRoleCode = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder("SELECT NextRoleCode From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference");
		logger.debug("selectSql: " + sql.toString());

		try {
			nextRoleCode = this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			nextRoleCode = null;
		}

		logger.debug("Leaving");
		return nextRoleCode;
	}

	@Override
	public void updateNextUserId(List<String> finRefList, String oldUserId, String newUserId,
			boolean isManualAssignment) {

		logger.debug("Entering");

		Map<String, String> finRefMap = new HashMap<String, String>();
		for (String reference : finRefList) {
			finRefMap.put("FinReference", reference);
			finRefMap.put("USER_ID", oldUserId);
			finRefMap.put("NEW_USER_ID", newUserId);

			StringBuilder updateSql = new StringBuilder("Update FinanceMain_Temp");
			if (isManualAssignment) {
				updateSql.append(
						" SET NextUserId= (CASE WHEN NextRoleCode NOT like '%,%' AND COALESCE(NextUserId, ' ') LIKE (' ') THEN :NEW_USER_ID");
				updateSql.append(
						" WHEN NextRoleCode like '%,%' AND COALESCE(NextUserId, ' ') LIKE (' ') THEN :NEW_USER_ID ");
				if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.POSTGRES) {
					updateSql.append(
							" WHEN NextRoleCode like '%,%' AND NOT COALESCE(NextUserId, ' ') LIKE (' ') THEN NextUserId||','||:NEW_USER_ID END) ");
				} else {
					updateSql.append(
							" WHEN NextRoleCode like '%,%' AND NOT COALESCE(NextUserId, ' ') LIKE (' ') THEN NextUserId+',");
					updateSql.append(StringUtils.trimToEmpty(newUserId));
					updateSql.append("' END) ");
				}
			} else {
				updateSql.append(" SET NextUserId = REPLACE(NextUserId, :USER_ID, :NEW_USER_ID)");
			}
			updateSql.append(" Where FinReference =:FinReference");
			logger.debug("updateSql: " + updateSql.toString());

			this.jdbcTemplate.update(updateSql.toString(), finRefMap);
		}

		logger.debug("Leaving");
	}

	@Override
	public void updateDeviationApproval(FinanceMain financeMain, boolean rejected, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DeviationApproval =:DeviationApproval");
		if (rejected) {
			updateSql.append(" ,NextTaskId=:NextTaskId, NextRoleCode = :NextRoleCode, NextUserId = :NextUserId");
		}
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Method for fetch finance details based on priority
	 */
	@Override
	public List<FinanceMain> getFinanceRefByPriority() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select ");
		sql.append(" FinReference, LastMntBy, NextUserId, NextRoleCode, Priority");
		sql.append(" from FinanceMain_Temp");
		sql.append(" where FinReference NOT IN (Select Reference from MailLog) AND Priority != 0");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {

				}
			}, new RowMapper<FinanceMain>() {
				@Override
				public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceMain fm = new FinanceMain();

					fm.setFinReference(rs.getString("FinReference"));
					fm.setLastMntBy(rs.getLong("LastMntBy"));
					fm.setNextUserId(rs.getString("NextUserId"));
					fm.setNextRoleCode(rs.getString("NextRoleCode"));
					fm.setPriority(rs.getInt("Priority"));

					return fm;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * Method for fetch unprocessed finance details
	 */
	@Override
	public List<FinanceMain> getFinanceRefByValueDate(Date appDate, int maxAllowedDays) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", appDate);
		source.addValue("MaxAllowedDays", maxAllowedDays);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference,LastMntBy,NextUserId ,NextRoleCode, Priority FROM FinanceMain_Temp ");
		selectSql.append(" where FinReference in(Select Reference from ( ");

		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" Select Reference, DATEDIFF(dd, MAX(ValueDate), :AppDate)");
			selectSql.append("  maxValueDate from MailLog group by Reference) T where maxValueDate =:MaxAllowedDays)");
		} else if (App.DATABASE == Database.ORACLE) {
			selectSql.append(" Select Reference, TRUNC(:AppDate) - TO_DATE(MAX(ValueDate), 'dd-MM-yy') ");
			selectSql.append("  maxValueDate from MailLog group by Reference) T where maxValueDate =:MaxAllowedDays)");
		}

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	/**
	 * Method for fetch finance details based on GrcEnd Date
	 */
	@Override
	public List<FinanceMain> getFinGraceDetails(Date grcEnd, int allowedDays) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("GraceEndDate", grcEnd);
		source.addValue("AllowedDays", allowedDays);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				"SELECT GrcPeriodEndDate, CustID,FinReference,LastMntBy,NextUserId ,NextRoleCode, Priority FROM FinanceMain WHERE ");

		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" (DATEDIFF(dd, GrcPeriodEndDate, :GraceEndDate)) =:AllowedDays ");
		} else if (App.DATABASE == Database.ORACLE) {
			selectSql.append(
					" SELECT Reference, (TRUNC(:GraceEndDate) - TO_DATE(MAX(GrcPeriodEndDate), 'dd-MM-yy') =:AllowedDays ");
		}
		selectSql.append(" AND FinReference NOT IN(SELECT Reference FROM MailLog ) ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	/**
	 * Method for Fetching List of Limit Reference Details utilized for Finances in Rollover Functionality
	 */
	@Override
	public List<String> getRollOverLimitRefList() {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT DISTINCT FinLimitRef FROM FinanceMain WHERE NextRolloverDate IS NOT NULL ");
		selectSql.append(" AND FinReference NOT IN (SELECT FinReference From RolledOverFinanceDetail_View)  ");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		return this.jdbcTemplate.queryForList(selectSql.toString(), source, String.class);
	}

	/**
	 * Method for Fetching List of Limit Reference Details utilized for Finances in Rollover Functionality
	 */
	@Override
	public List<String> getRollOverFinTypeList(String limitRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinLimitRef", limitRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT DISTINCT FinType FROM FinanceMain WHERE NextRolloverDate IS NOT NULL ");
		selectSql.append(
				" AND FinReference NOT IN (SELECT FinReference From RolledOverFinanceDetail_View) AND FinLimitRef=:FinLimitRef  ");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		return this.jdbcTemplate.queryForList(selectSql.toString(), source, String.class);
	}

	/**
	 * Method for Fetching List of Rollover Date Details utilized for Finances in Rollover Functionality
	 */
	@Override
	public List<Date> getRollOverDateList(String limitRef, String finType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinLimitRef", limitRef);
		source.addValue("FinType", finType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT DISTINCT MaturityDate FROM FinanceMain WHERE NextRolloverDate IS NOT NULL ");
		selectSql.append(" AND FinReference NOT IN (SELECT FinReference From RolledOverFinanceDetail_View) ");
		selectSql.append(" AND FinLimitRef=:FinLimitRef AND FinType=:FinType ");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		return this.jdbcTemplate.queryForList(selectSql.toString(), source, Date.class);
	}

	/**
	 * Method for Fetching List of Finances on Same Rollover Date
	 */
	@Override
	public List<RolledoverFinanceDetail> getFinanceList(String limitRef, String finType, Date rolloverDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinLimitRef", limitRef);
		source.addValue("FinType", finType);
		source.addValue("MaturityDate", rolloverDate);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT F.FinReference, F.FinstartDate StartDate, F.FinAmount, F.EffectiveRateOfReturn ProfitRate, F.TotalProfit, ");
		selectSql.append(
				" S.RolloverAmount, F.NextRolloverDate RolloverDate, C.CcyEditField Formatter, P.TotalPriBal, P.TotalPftBal, F.FinPurpose  ");
		selectSql.append(
				" FROM FinanceMain F INNER JOIN FinScheduleDetails S ON F.FinReference = S.FinReference AND F.MaturityDate = S.SchDate ");
		selectSql.append(
				" INNER JOIN RMTCurrencies C ON C.CcyCode = F.FinCcy INNER JOIN FinPftDetails P ON P.FinReference = F.FinReference ");
		selectSql.append(
				" WHERE F.NextRolloverDate IS NOT NULL AND S.RolloverAmount > 0 AND F.FinReference NOT IN (SELECT FinReference From RolledOverFinanceDetail_View) ");
		selectSql.append(" AND F.FinLimitRef=:FinLimitRef AND F.FinType=:FinType AND F.MaturityDate=:MaturityDate ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<RolledoverFinanceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(RolledoverFinanceDetail.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public FinanceMain getFinanceMainByRef(String reference, String type, boolean isRejectFinance) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(reference);
		StringBuilder selectSql = new StringBuilder("select  *  FROM ");
		/*
		 * StringBuilder selectSql = new StringBuilder("select  FinReference, GrcPeriodEndDate, FinRepaymentAmount," );
		 * selectSql.
		 * append(" DisbAccountid, RepayAccountid, FinAccount, FinCustPftAccount, FinCommitmentRef, FinLimitRef," );
		 * selectSql. append(
		 * " FinCcy, FinBranch, CustId, FinAmount, FeeChargeAmt, DownPayment, DownPayBank, DownPaySupl, DownPayAccount, SecurityDeposit, FinType, "
		 * ); selectSql. append(
		 * " FinStartDate,GraceTerms, NumberOfTerms, NextGrcPftDate, NextRepayDate, LastRepayPftDate, NextRepayPftDate, "
		 * ); selectSql. append(" LastRepayRvwDate, NextRepayRvwDate, FinAssetValue, FinCurrAssetValue,FinRepayMethod, "
		 * ); selectSql. append(" RecordType, Version, ProfitDaysBasis , FeeChargeAmt, FinStatus, FinStsReason," );
		 * selectSql.
		 * append(" InitiateUser, BankName, Iban, AccountType, DdaReferenceNo, SecurityDeposit, MaturityDate " );
		 */
		if (isRejectFinance) {
			selectSql.append(" RejectFinancemain");
		} else {
			selectSql.append(" Financemain");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			financeMain = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * Method for Fetch DDA Payment Initiation details
	 * 
	 * @param repayMethod
	 * @param appDate
	 * @return List<FinanceMain>
	 */
	@Override
	public List<DDAPayments> getDDAPaymentsList(String repayMethod, Date appDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinRepayMethod", repayMethod);
		source.addValue("SchDate", appDate);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT T1.FinReference, T1.FinRepaymentAmount, T1.DDAReferenceNo, T1.RepayAccountId, T2.CustCIF, T3.SchDate");
		selectSql.append(" FROM FinanceMain T1 INNER JOIN Customers T2 ON T1.CustID = T2.CustID");
		selectSql.append(" INNER JOIN FinScheduleDetails T3 ON T1.FinReference = T3.FinReference");
		selectSql.append(" WHERE T1.FinRepayMethod =:FinRepayMethod AND T3.SchDate =:SchDate");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<DDAPayments> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DDAPayments.class);
		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return null;
		}
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param finReference
	 *            (String)
	 * 
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainForManagerCheque(final String finReference, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(finReference);

		StringBuilder selectSql = new StringBuilder("select  FinReference, GrcPeriodEndDate, FinRepaymentAmount,");
		selectSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		selectSql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		selectSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		selectSql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		selectSql.append(
				" MaturityDate, CpzAtGraceEnd,DownPayment, GraceFlatAmount, ReqRepayAmount, TotalProfit, DownPayBank, DownPaySupl, ");
		selectSql.append(" TotalCpz,TotalGrossPft,TotalGracePft,TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		selectSql.append(" GrcRateBasis, RepayRateBasis, FinType,FinRemarks, FinCcy, ScheduleMethod,");
		selectSql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,");
		selectSql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments, ");
		selectSql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		selectSql
				.append(" AvailedDefFrqChange, RecalType, FinAssetValue, disbAccountId, repayAccountId, FinIsActive, ");
		selectSql.append(
				" LastRepayDate, LastRepayPftDate,LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd,");
		selectSql.append(
				" GrcMargin, RepayMargin, FinCommitmentRef, FinLimitRef, DepreciationFrq, FinCurrAssetValue,FinContractDate,");
		selectSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount,");
		selectSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount,");
		selectSql.append(" ClosingStatus, FinApprovedDate, ");
		selectSql.append(
				" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq, FeeChargeAmt, BpiAmount, DeductFeeDisb, PromotionCode, SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(", lovDescFinTypeName, lovDescCustCIF, ");
			selectSql.append(
					" lovDescCustShrtName, LovDescCustFName, lovDescCustLName, lovDescFinBranchName, lovDescFinancingAmount ");
		}
		selectSql.append(" FROM Financemain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			financeMain = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}

	@Override
	public void updateRepaymentAmount(String finReference, BigDecimal repaymentAmount) {
		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setFinRepaymentAmount(repaymentAmount);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinRepaymentAmount =:FinRepaymentAmount ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateStatus(String finReference, String status, String statusReason) {

		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinStatus(status);
		financeMain.setFinStsReason(statusReason);
		financeMain.setFinReference(finReference);
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinStatus = :FinStatus, FinStsReason = :FinStsReason ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updatePaymentInEOD(FinanceMain financeMain) {
		int recordCount = 0;
		StringBuilder sql = new StringBuilder("Update FinanceMain Set");
		sql.append(" FinStatus = ?, FinStsReason =  ?,");
		sql.append(" FinIsActive =  ?, ClosingStatus =  ?,");
		sql.append(" FinRepaymentAmount =  ?");

		EventProperties eventProperties = financeMain.getEventProperties();
		if (!financeMain.isFinIsActive()) {
			if (eventProperties.isParameterLoaded()) {
				financeMain.setClosedDate(eventProperties.getAppDate());
			} else {
				financeMain.setClosedDate(SysParamUtil.getAppDate());
			}
			sql.append(", ClosedDate = ?");
		}

		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, financeMain.getFinStatus());
				ps.setString(index++, financeMain.getFinStsReason());
				ps.setBoolean(index++, financeMain.isFinIsActive());
				ps.setString(index++, financeMain.getClosingStatus());
				ps.setBigDecimal(index++, financeMain.getFinRepaymentAmount());

				if (!financeMain.isFinIsActive()) {
					ps.setDate(index++, JdbcUtil.getDate(financeMain.getClosedDate()));
				}

				// append fields of where condition.
				ps.setString(index++, financeMain.getFinReference());
			});

		} catch (Exception e) {
			throw e;
		}

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	/**
	 * Method for Fetching Approved Repayment method
	 */
	@Override
	public String getApprovedRepayMethod(String finReference, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinRepayMethod FROM FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE FinReference=:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		String repayMethod = this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		if (StringUtils.isBlank(repayMethod)) {
			repayMethod = null;
		}
		logger.debug("Leaving");
		return repayMethod;
	}

	/**
	 * @param AccountNo
	 * @return
	 */
	@Override
	public String getCurrencyByAccountNo(String accountNo) {
		logger.debug("Entering");
		FinanceMain financeMain = new FinanceMain();
		financeMain.setRepayAccountId(accountNo);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select FinCcy from FinanceMain fm inner join SecondaryAccounts sa on ");
		selectSql.append(" fm.FinReference=sa.FinReference where fm.RepayAccountId= :RepayAccountId ");
		selectSql.append(" or sa.AccountNumber = :RepayAccountId group by FinCcy ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		String finCcy = null;
		try {
			finCcy = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finCcy = null;
		}
		financeMain = null;
		logger.debug("Leaving");
		return finCcy;
	}

	@Override
	public void updateMaturity(String finReference, String closingStatus, boolean finIsActive, Date closedDate) {
		logger.debug("Entering");
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setClosingStatus(closingStatus);
		financeMain.setFinIsActive(finIsActive);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinIsActive = :FinIsActive, ClosingStatus = :ClosingStatus ");

		// For InActive Loans, Update Loan Closed Date
		if (!financeMain.isFinIsActive()) {
			if (closedDate == null) {
				financeMain.setClosedDate(SysParamUtil.getAppDate());
			} else {
				financeMain.setClosedDate(closedDate);
			}
			updateSql.append(", ClosedDate = :ClosedDate ");
		}
		updateSql.append(" Where FinReference = :FinReference ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void updateFinPftMaturity(String finReference, String closingStatus, boolean finIsActive) {
		logger.debug("Entering");
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setClosingStatus(closingStatus);
		financeMain.setFinIsActive(finIsActive);

		StringBuilder updateSql = new StringBuilder("Update finpftdetails ");
		updateSql.append(" Set FinIsActive = :FinIsActive, ClosingStatus = :ClosingStatus ");
		updateSql.append(" Where FinReference = :FinReference ");
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<String> getScheduleEffectModuleList(boolean schdChangeReq) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("schdChangeReq", schdChangeReq);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT ModuleName FROM ScheduleEffectModule WHERE SchdCanModify =:schdChangeReq ");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		return this.jdbcTemplate.queryForList(selectSql.toString(), source, String.class);
	}

	/**
	 * updates finance sequence table
	 */
	@Override
	public boolean updateSeqNumber(long oldNumber, long newNumber) {
		logger.debug("Entering");
		boolean valUpdated = false;
		try {
			MapSqlParameterSource maSqlParameterSource = new MapSqlParameterSource();
			maSqlParameterSource.addValue("oldNumber", oldNumber);
			maSqlParameterSource.addValue("newNumber", newNumber);

			String updateSql = "UPDATE  SeqFinReference  SET Seqno = :newNumber Where Seqno = :oldNumber";
			int result = this.jdbcTemplate.update(updateSql, maSqlParameterSource);
			if (result == 1) {
				valUpdated = true;
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return valUpdated;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public List<FinanceMain> getFinanceMainbyCustId(final long id) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, CustID, FinAmount, FinType, FinCcy");
		sql.append(" from FinanceMain");
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, id);
				}
			}, new RowMapper<FinanceMain>() {
				@Override
				public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceMain fm = new FinanceMain();

					fm.setFinReference(rs.getString("FinReference"));
					fm.setCustID(rs.getLong("CustID"));
					fm.setFinAmount(rs.getBigDecimal("FinAmount"));
					fm.setFinType(rs.getString("FinType"));
					fm.setFinCcy(rs.getString("FinCcy"));

					return fm;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * Method for fetch total number of records i.e count
	 * 
	 * @param finReference
	 * @param type
	 * @param isWIF
	 */
	@Override
	public int getFinanceCountById(String finReference, String type, boolean isWIF) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");

		if (!isWIF) {
			selectSql.append(" From FinanceMain");
		} else {
			selectSql.append(" From WIFFinanceMain");
		}

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND FinIsActive = 1");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}

	@Override
	public int getFinCountByCustId(long custID) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setCustID(custID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");

		selectSql.append(" From FinanceMain");

		selectSql.append(" Where CustID =:CustID AND FinIsActive = 1");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}

	@Override
	public int getFinanceCountByMandateId(long mandateID) {
		logger.debug("Entering");
		FinanceMain financeMain = new FinanceMain();
		financeMain.setMandateID(mandateID);
		financeMain.setFinIsActive(true);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where MandateID =:MandateID AND FinIsActive=:FinIsActive");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}

	/**
	 * Method for getCount for mandate and FinRef in finance main
	 * 
	 * @param finReference
	 * @param mandateID
	 * 
	 */
	@Override
	public int getFinanceCountById(String finReference, long mandateID) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setMandateID(mandateID);
		financeMain.setFinIsActive(true);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where MandateID =:MandateID AND FinReference=:FinReference  AND FinIsActive=:FinIsActive");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			return 0;
		}
	}

	/**
	 * Method for Update old MandateId With New MandateId
	 * 
	 * @param finReference
	 * @param newMandateID
	 * 
	 */
	@Override
	public int loanMandateSwapping(String finReference, long newMandateID, String repayMethod, String type) {
		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setMandateID(newMandateID);
		financeMain.setFinRepayMethod(repayMethod);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));

		updateSql.append(" Set MandateID =:MandateID ");
		if (StringUtils.isNotBlank(repayMethod)) {
			updateSql.append(" ,FinRepayMethod =:FinRepayMethod");
		}
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");

		return recordCount;

	}

	/**
	 * Method for fetch Finance details for Finance Maintenance module
	 * 
	 * @param finReference
	 * @param type
	 * @param isWIF
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceDetailsForService(String finReference, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, GrcPeriodEndDate, MaturityDate");
		sql.append(", AllowGrcPeriod, RepayFrq, FinStartDate, CustID");

		if (isWIF) {
			sql.append(" From WIFFinanceMain");
		} else {
			sql.append(" From FinanceMain");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<FinanceMain>() {
						@Override
						public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinanceMain fm = new FinanceMain();

							fm.setFinReference(rs.getString("FinReference"));
							fm.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
							fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
							fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
							fm.setRepayFrq(rs.getString("RepayFrq"));
							fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
							fm.setCustID(rs.getLong("CustID"));

							return fm;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.ENTERING);
		return null;
	}

	/**
	 * Method for Update Basic Finance details
	 * 
	 * @param financeMain
	 * @param type
	 * @return Integer
	 */
	@Override
	public int updateFinanceBasicDetails(FinanceMain financeMain, String type) {
		logger.debug("Entering");

		int recordCount = 0;

		StringBuilder updateSql = new StringBuilder("Update  ");
		updateSql.append("FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set  DsaCode = :DsaCode , AccountsOfficer = :AccountsOfficer, ReferralId = :ReferralId, EmployeeName = :EmployeeName, ");
		updateSql.append(" SalesDepartment = :SalesDepartment , DmaCode = :DmaCode");
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		try {
			recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			recordCount = 0;
		}
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
		}
		logger.debug("Leaving");
		return recordCount;
	}

	@Override
	public List<String> getUsersLoginList(List<String> nextRoleCodes) {
		logger.debug("Entering");

		List<Long> userIds = new ArrayList<>();
		if (nextRoleCodes != null) {
			for (String id : nextRoleCodes) {
				userIds.add(Long.valueOf(id));
			}
		}

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("usrid", userIds);

		StringBuilder selectSql = new StringBuilder("Select  usrlogin from secusers");
		selectSql.append(" Where usrid IN (:usrid) ");

		logger.debug("selectSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForList(selectSql.toString(), mapSqlParameterSource, String.class);
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainParms(final String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append("  Defferments, PlanDeferCount, AllowedDefRpyChange, AvailedDefRpyChange");
		sql.append(
				", AvailedDefFrqChange, FinIsActive, AlwBPI, BpiTreatment, PlanEMIHAlw, PlanEMIHAlwInGrace, PlanEMIHMethod");
		sql.append(", PlanEMIHMaxPerYear, PlanEMIHMax, PlanEMIHLockPeriod, PlanEMICpz, AlwMultiDisb");
		sql.append(", UnPlanEMIHLockPeriod, UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi, MaxReAgeHolidays");
		sql.append(", AvailedUnPlanEmi, AvailedReAgeH, PromotionCode, AllowedDefFrqChange");
		sql.append(" from FinanceMain_View");
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<FinanceMain>() {
						@Override
						public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinanceMain fm = new FinanceMain();

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

							return fm;
						}
					});

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Method to get Finance related data.
	 * 
	 * @param custId
	 */
	@Override
	public List<FinanceMain> getFinanceByCustId(long custId, String type) {
		logger.debug(Literal.ENTERING);

		List<FinanceMain> fm = null;

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinReference, fm.FinAmount, fm.FinType, fm.FinCcy, fm.FinAssetValue");
		sql.append(", fm.NumberOfTerms, fm.MaturityDate, fm.finStatus, fm.FinStartDate");
		sql.append(", fm.FirstRepay, ft.FinCategory lovDescFinProduct, fm.ClosingStatus");
		sql.append(", fm.RecordStatus, fm.ProductCategory, fm.FinBranch, fm.FinApprovedDate, fm.FinIsActive");
		sql.append(" from FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" fm INNER JOIN RMTFinanceTypes ft ON fm.FinType = ft.FinType ");
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());
		try {
			fm = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, custId);
				}
			}, new RowMapper<FinanceMain>() {
				@Override
				public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceMain fm = new FinanceMain();

					fm.setFinReference(rs.getString("FinReference"));
					fm.setFinAmount(rs.getBigDecimal("FinAmount"));
					fm.setFinType(rs.getString("FinType"));
					fm.setFinCcy(rs.getString("FinCcy"));
					fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
					fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
					fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
					fm.setFinStatus(rs.getString("finStatus"));
					fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
					fm.setFirstRepay(rs.getBigDecimal("FirstRepay"));
					fm.setLovDescFinProduct(rs.getString("lovDescFinProduct"));
					fm.setClosingStatus(rs.getString("ClosingStatus"));
					fm.setRecordStatus(rs.getString("RecordStatus"));
					fm.setProductCategory(rs.getString("ProductCategory"));
					fm.setFinBranch(rs.getString("FinBranch"));
					fm.setFinApprovedDate(rs.getTimestamp("FinApprovedDate"));
					fm.setFinIsActive(rs.getBoolean("FinIsActive"));

					return fm;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return fm;
	}

	/**
	 * Method to get Finance related data.
	 * 
	 * @param collateralRef
	 */
	@Override
	public List<FinanceMain> getFinanceByCollateralRef(String collateralRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		StringBuilder selectSql = new StringBuilder(
				"SELECT FM.FinReference, FM.FinAmount, FM.FinType, FM.FinCcy,FM.ClosingStatus,");
		selectSql.append(
				" FM.FinAssetValue, FM.NumberOfTerms, FM.MaturityDate, FM.Finstatus,FM.FinStartDate, FM.FirstRepay,");
		selectSql.append(" FT.FinCategory lovDescFinProduct, CA.CollateralRef From FinanceMain FM INNER JOIN ");
		selectSql.append(" CollateralAssignment CA On FM.FinReference = CA.Reference INNER JOIN ");
		selectSql.append(" RMTFinanceTypes FT ON FM.FinType = FT.FinType");
		selectSql.append(" Where CollateralRef =:CollateralRef");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		List<FinanceMain> financeMainList = new ArrayList<FinanceMain>();
		try {
			financeMainList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return financeMainList;
	}

	/**
	 * Method to get FinanceReferences by Given MandateId.
	 * 
	 * @param mandateId
	 */
	@Override
	public List<String> getFinReferencesByMandateId(long mandateId) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setMandateID(mandateId);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference ");
		selectSql.append(" From FinanceMain");

		selectSql.append(" Where MandateID =:MandateID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		List<String> finReferencesList = new ArrayList<String>();
		try {
			finReferencesList = this.jdbcTemplate.queryForList(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return finReferencesList;
	}

	/**
	 * Method to get FinanceReferences by Given custId.
	 * 
	 * @param custId
	 * @param finActiveStatus
	 */
	@Override
	public List<String> getFinReferencesByCustID(long custId, String finActiveStatus) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setCustID(custId);
		financeMain.setClosingStatus(finActiveStatus);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference ");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where CustID =:CustID AND");
		if (StringUtils.isBlank(finActiveStatus)) {
			selectSql.append(" ClosingStatus is null");
		} else {
			selectSql.append(" ClosingStatus =:ClosingStatus");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		List<String> finReferencesList = new ArrayList<String>();
		try {
			finReferencesList = this.jdbcTemplate.queryForList(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return finReferencesList;
	}

	/***
	 * Method to get the finassetValue for the comparison with teh current asset value in the odMaintenance
	 */
	@Override
	public BigDecimal getFinAssetValue(String finReference) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder("SELECT FinAssetValue ");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where FinReference =:FinReference");
		logger.debug("selectSql: " + selectSql.toString());
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);
	}

	/***
	 * Method to get the Loan branch against the Reference
	 */
	@Override
	public String getFinBranch(String finReference) {
		StringBuilder sql = new StringBuilder("Select FinBranch");
		sql.append(" From FinanceMain");
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, String.class);
	}

	@Override
	public BigDecimal getTotalMaxRepayAmount(long mandateId, String finReference) {
		logger.debug(Literal.ENTERING);
		// FIXME Need to convert this sum of, max of logic to java .since it is
		// not supported by Postgresql
		// // Prepare the SQL.
		// StringBuilder sql = new StringBuilder("select
		// coalesce(sum(max(RepayAmount)), 0) from FinScheduleDetails_View");
		// sql.append(" where FinReference in (select FinReference from
		// FinanceMain_View ");
		// sql.append(" where MandateId = :MandateId and FinIsActive = 1 and
		// FinReference != :FinReference)");
		// sql.append(" group by FinReference");
		//
		// // Execute the SQL, binding the arguments.
		// logger.trace(Literal.SQL + sql);
		// MapSqlParameterSource paramSource = new MapSqlParameterSource();
		// paramSource.addValue("MandateId", mandateId);
		// paramSource.addValue("FinReference", finReference);
		//
		// logger.debug(Literal.LEAVING);
		// return jdbcTemplate.queryForObject(sql.toString(), paramSource,
		// BigDecimal.class);
		return BigDecimal.ZERO;
	}

	@Override
	public void updateBucketStatus(String finReference, String status, int bucket, String statusReason) {

		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinStatus(status);
		financeMain.setFinStsReason(statusReason);
		financeMain.setFinReference(finReference);
		financeMain.setDueBucket(bucket);
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinStatus = :FinStatus, FinStsReason = :FinStsReason, DueBucket=:DueBucket ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Method for get the Finance Details and FinanceShedule Details
	 */
	@Override
	public List<FinanceMain> getFinMainsForEODByCustId(long custId, boolean isActive) {
		StringBuilder sql = getFinSelectQueryForEod();
		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(EodConstants.SQL_NOLOCK);
		}
		sql.append(" where CustID = ?");

		if (isActive) {
			sql.append(" and FinIsActive = ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		FinRowMapperForEod rowMapper = new FinRowMapperForEod();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, custId);
			if (isActive) {
				ps.setBoolean(2, isActive);
			}
		}, rowMapper);

	}

	/**
	 * Method for get the Finance Details by Finance Reference
	 */
	@Override
	public FinanceMain getFinMainsForEODByFinRef(String finReference, boolean isActive) {
		StringBuilder sql = getFinSelectQueryForEod();
		sql.append(" where FinReference = ? and FinIsActive = ?");
		logger.trace(Literal.SQL + sql.toString());

		try {
			FinRowMapperForEod rowMapper = new FinRowMapperForEod();

			return this.jdbcTemplate.getJdbcOperations().queryForObject(sql.toString(),
					new Object[] { finReference, isActive }, rowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		return null;
	}

	@Override
	public int getFinanceMainByBank(String bankCode, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setBankName(bankCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankName =:BankName");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
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

		sql.append(" Where FinReference =:FinReference");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public List<FinanceMain> getBYCustIdForLimitRebuild(final long id, boolean orgination) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append("  FinReference, GrcPeriodEndDate, AllowGrcPeriod, GraceBaseRate, GraceSpecialRate");
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
		sql.append(", ScheduleRegenerated, MandateID, FinStatus, FinStsReason, BankName, Iban, AccountType");
		sql.append(", DdaReferenceNo, PromotionCode, FinCategory, ProductCategory, ReAgeBucket, TDSApplicable");
		sql.append(", SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount");

		if (orgination) {
			sql.append(" , 1 LimitValid  ");
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

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, id);
				}
			}, new RowMapper<FinanceMain>() {
				@Override
				public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceMain fm = new FinanceMain();

					fm.setFinReference(rs.getString("FinReference"));
					fm.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
					fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
					fm.setGraceBaseRate(rs.getString("GraceBaseRate"));
					fm.setGraceSpecialRate(rs.getString("GraceSpecialRate"));
					fm.setGrcPftRate(rs.getBigDecimal("GrcPftRate"));
					fm.setGrcPftFrq(rs.getString("GrcPftFrq"));
					fm.setNextGrcPftDate(rs.getTimestamp("NextGrcPftDate"));
					fm.setAllowGrcPftRvw(rs.getBoolean("AllowGrcPftRvw"));
					fm.setGrcPftRvwFrq(rs.getString("GrcPftRvwFrq"));
					fm.setNextGrcPftRvwDate(rs.getTimestamp("NextGrcPftRvwDate"));
					fm.setAllowGrcCpz(rs.getBoolean("AllowGrcCpz"));
					fm.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
					fm.setNextGrcCpzDate(rs.getTimestamp("NextGrcCpzDate"));
					fm.setRepayBaseRate(rs.getString("RepayBaseRate"));
					fm.setRepaySpecialRate(rs.getString("RepaySpecialRate"));
					fm.setRepayProfitRate(rs.getBigDecimal("RepayProfitRate"));
					fm.setRepayFrq(rs.getString("RepayFrq"));
					fm.setNextRepayDate(rs.getTimestamp("NextRepayDate"));
					fm.setRepayPftFrq(rs.getString("RepayPftFrq"));
					fm.setNextRepayPftDate(rs.getTimestamp("NextRepayPftDate"));
					fm.setAllowRepayRvw(rs.getBoolean("AllowRepayRvw"));
					fm.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
					fm.setNextRepayRvwDate(rs.getTimestamp("NextRepayRvwDate"));
					fm.setAllowRepayCpz(rs.getBoolean("AllowRepayCpz"));
					fm.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
					fm.setNextRepayCpzDate(rs.getTimestamp("NextRepayCpzDate"));
					fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
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
					fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
					fm.setFinAmount(rs.getBigDecimal("FinAmount"));
					fm.setCustID(rs.getLong("CustID"));
					fm.setFinBranch(rs.getString("FinBranch"));
					fm.setFinSourceID(rs.getString("FinSourceID"));
					fm.setRecalType(rs.getString("RecalType"));
					fm.setFinIsActive(rs.getBoolean("FinIsActive"));
					fm.setLastRepayDate(rs.getTimestamp("LastRepayDate"));
					fm.setLastRepayPftDate(rs.getTimestamp("LastRepayPftDate"));
					fm.setLastRepayRvwDate(rs.getTimestamp("LastRepayRvwDate"));
					fm.setLastRepayCpzDate(rs.getTimestamp("LastRepayCpzDate"));
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
					fm.setMandateID(rs.getLong("MandateID"));
					fm.setFinStatus(rs.getString("FinStatus"));
					fm.setFinStsReason(rs.getString("FinStsReason"));
					fm.setBankName(rs.getString("BankName"));
					fm.setIban(rs.getString("Iban"));
					fm.setAccountType(rs.getString("AccountType"));
					fm.setDdaReferenceNo(rs.getString("DdaReferenceNo"));
					fm.setPromotionCode(rs.getString("PromotionCode"));
					fm.setFinCategory(rs.getString("FinCategory"));
					fm.setProductCategory(rs.getString("ProductCategory"));
					fm.setReAgeBucket(rs.getInt("ReAgeBucket"));
					fm.setTDSApplicable(rs.getBoolean("TDSApplicable"));
					fm.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
					fm.setPromotionSeqId(rs.getLong("PromotionSeqId"));
					fm.setSvAmount(rs.getBigDecimal("SvAmount"));
					fm.setCbAmount(rs.getBigDecimal("CbAmount"));
					if (orgination) {
						fm.setLimitValid(rs.getBoolean("LimitValid"));
					}
					return fm;
				}
			});

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);

		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();

	}

	@Override
	public FinanceMain getFinanceBasicDetailByRef(String finReference, boolean isWIF) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder(
				"SELECT FM.FinReference, FM.MaturityDate, FT.Ratechganyday, FM.ProductCategory ");

		if (isWIF) {
			selectSql.append(" From WIFFinanceMain FM");
		} else {
			selectSql.append(" From FinanceMain FM");
		}
		selectSql.append(" inner join RMTfinanceTypes FT on FM.FinType = FT.FinType ");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		FinanceMain financeMain = null;
		try {
			financeMain = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * 
	 * 
	 */
	@Override
	public void updateFinMandateId(long mandateId, String finReference, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("MandateId", mandateId);

		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(type);
		sql.append(" set MandateId =:MandateId");
		sql.append(" where FinReference =:FinReference");
		logger.debug("updateSql: " + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * Method for fetch existing mandate id by reference.
	 * 
	 * @param finReference
	 * @param type
	 * @return mandateId
	 */
	@Override
	public long getMandateIdByRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder("SELECT MandateId From FinanceMain");
		selectSql.append(type);
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		long mandateId = Long.MIN_VALUE;
		try {
			mandateId = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			mandateId = Long.MIN_VALUE;
		}
		logger.debug(Literal.LEAVING);
		return mandateId;
	}

	/**
	 * Method for fetch total number of records i.e count
	 * 
	 * @param finReference
	 */
	@Override
	public int getFinanceCountById(String finReference) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");
		selectSql.append(" From FinanceMain_AView");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}

	/**
	 * Method for check Application number already Exists or not
	 * 
	 * @param applicationNo
	 * @param finType
	 */
	@Override
	public boolean isAppNoExists(String applicationNo, TableType tableType) {
		logger.debug("Entering");

		String selectSql = new String();
		String whereClause = " ApplicationNo =:ApplicationNo AND FinIsActive = :FinIsActive";
		switch (tableType) {
		case MAIN_TAB:
			selectSql = QueryUtil.getCountQuery("FinanceMain", whereClause);
			break;
		case TEMP_TAB:
			selectSql = QueryUtil.getCountQuery("FinanceMain_Temp", whereClause);
			break;
		default:
			selectSql = QueryUtil.getCountQuery(new String[] { "FinanceMain_Temp", "FinanceMain" }, whereClause);
			break;
		}

		logger.debug("selectSql: " + selectSql.toString());
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + selectSql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("ApplicationNo", applicationNo);
		paramSource.addValue("FinIsActive", 1);

		Integer count = jdbcTemplate.queryForObject(selectSql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}
		return exists;
	}

	@Override
	public String getApplicationNoById(String finReference, String type) {
		StringBuilder sql = new StringBuilder("Select ApplicationNo From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL, sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("ApplicationNo not found in FinanceMain{} for the specified FinReference >> {}", type,
					finReference);
		}

		return null;
	}

	/**
	 * Method to get FinanceReferences by Given custId.
	 * 
	 * @param custId
	 */
	@Override
	public List<String> getFinReferencesByCustID(long custID) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setCustID(custID);
		financeMain.setFinIsActive(true);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference ");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where CustID =:CustID AND FinIsActive = :FinIsActive");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		List<String> finReferencesList = new ArrayList<String>();
		try {
			finReferencesList = this.jdbcTemplate.queryForList(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return finReferencesList;
	}

	/**
	 * Method for get total number of records from FinanceMain master table.<br>
	 * 
	 * @param divisionCode
	 * 
	 * @return Boolean
	 */
	@Override
	public boolean isFinTypeExistsInFinanceMain(String finType, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FINTYPE", finType);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM FINANCEMAIN");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE FINTYPE= :FINTYPE");

		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount > 0 ? true : false;
	}

	@Override
	public List<FinanceMain> getFinancesByExpenseType(String finType, Date finApprovalStartDate,
			Date finApprovalEndDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinReference, fm.FinAssetValue, fm.FinCurrAssetValue, fm.FinCcy");
		sql.append(", fm.FinBranch, fm.FinType, e.EntityCode");
		sql.append(" from FinanceMain fm,SMTDivisionDetail e");
		sql.append(" inner join  RMTFinanceTypes ft on e.DivisionCode = ft.FinDivision and ft.FinType= ?");
		sql.append(" WHERE  fm.FinType = ? And (ClosingStatus is null or ClosingStatus <> ?)");
		sql.append(" And fm.FinApprovedDate >= ? And fm.FinApprovedDate <= ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finType);
					ps.setString(index++, finType);
					ps.setString(index++, "C");
					ps.setDate(index++, JdbcUtil.getDate(finApprovalStartDate));
					ps.setDate(index++, JdbcUtil.getDate(finApprovalEndDate));
				}
			}, new RowMapper<FinanceMain>() {
				@Override
				public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceMain fm = new FinanceMain();

					fm.setFinReference(rs.getString("FinReference"));
					fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
					fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
					fm.setFinCcy(rs.getString("FinCcy"));
					fm.setFinBranch(rs.getString("FinBranch"));
					fm.setFinType(rs.getString("FinType"));
					fm.setEntityCode(rs.getString("EntityCode"));

					return fm;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * Method for get total number of records from FinanceMain master table.<br>
	 * 
	 * @param loanPurposeCode
	 * 
	 * @return Boolean
	 */
	@Override
	public boolean isLoanPurposeExits(String loanPurposeCode, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("finpurpose", loanPurposeCode);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM FINANCEMAIN");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE finpurpose= :finpurpose");

		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount > 0 ? true : false;
	}

	@Override
	public String getEarlyPayMethodsByFinRefernce(String finReference) {
		logger.debug("Entering");

		String alwEarlyPayMethods = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		StringBuilder sql = new StringBuilder("Select AlwEarlyPayMethods from RMTFinanceTypes");
		sql.append(" Where FinType = (Select FinType from FinanceMain Where FinReference = :FinReference)");
		logger.debug("selectSql: " + sql.toString());

		try {
			alwEarlyPayMethods = this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			alwEarlyPayMethods = null;
		}

		logger.debug("Leaving");

		return alwEarlyPayMethods;
	}

	@Override
	public FinanceMain getDMFinanceMainByRef(String finReference, String type) {
		// Copied getFinanceMainById and removed unwanted/calculated fields

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("");
		selectSql.append("SELECT FinReference,GraceTerms, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		selectSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq, AllowGrcPftRvw,");
		selectSql.append(" GrcPftRvwFrq, AllowGrcCpz, GrcCpzFrq, RepayBaseRate,");
		selectSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, RepayPftFrq, ");
		selectSql.append(" AllowRepayRvw,RepayRvwFrq, AllowRepayCpz, RepayCpzFrq, ");
		selectSql.append(" MaturityDate, CpzAtGraceEnd, ReqRepayAmount, TotalProfit, ");
		selectSql.append(" TotalCpz,TotalGrossPft,TotalGracePft,TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		selectSql.append(" FinType, FinRemarks, FinCcy, ScheduleMethod,");
		selectSql.append(" ProfitDaysBasis, FirstRepay, LastRepay,");
		selectSql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, ");
		selectSql.append(" FinBranch, FinSourceID, ");
		selectSql.append(" RecalType, FinAssetValue, FinIsActive, ");
		selectSql.append(" AllowGrcRepay, GrcSchdMthd,");
		selectSql.append(" GrcMargin, RepayMargin, FinCurrAssetValue, FinContractDate,");
		selectSql.append(" ClosingStatus, FinApprovedDate, ");
		selectSql.append(" AnualizedPercRate,  FinRepayPftOnFrq , GrcProfitDaysBasis, ");
		selectSql.append(" LinkedFinRef,");
		selectSql.append(" RpyMinRate, RpyMaxRate, TDSApplicable, FeeChargeAmt, InsuranceAmt, ");
		selectSql.append(" AlwBPI, BpiTreatment, BpiAmount,");
		selectSql.append(" CalRoundingMode, RoundingTarget, AlwMultiDisb,");
		selectSql.append(" DeductFeeDisb, RvwRateApplFor, SchCalOnRvw, PastduePftCalMthd, ");
		selectSql.append(" RateChgAnyDay, PastduePftMargin,  FinCategory, ProductCategory,");
		selectSql.append(
				" LastMntBy, LastMntOn, FinRepayMethod, ManualSchedule, ScheduleMaintained, ScheduleRegenerated, ");
		selectSql.append(" JointAccount, JointCustId, MandateID, ");
		selectSql.append(" LimitValid, OverrideLimit, FinPurpose, FinStatus, FinStsReason, InitiateUser, ");
		selectSql.append(" BankName, Iban, AccountType, DdaReferenceNo, ");
		selectSql.append(" AccountsOfficer, DsaCode,");
		selectSql.append(" ReferralId, DmaCode, SalesDepartment, QuickDisb, ");
		selectSql.append(" PromotionCode, ApplicationNo, SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount");

		// Fields Required based on source data
		/*
		 * //Planned EMI selectSql.
		 * append(" PlanEMIHAlw, PlanEMIHMethod, PlanEMIHMaxPerYear, PlanEMIHMax, PlanEMIHLockPeriod, PlanEMICpz," );
		 * 
		 * //Unplanned EMI selectSql. append(" UnPlanEMIHLockPeriod , UnPlanEMICpz, MaxUnplannedEmi, ");
		 * 
		 * //Reage selectSql. append(" ReAgeCpz, MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, " );
		 * 
		 * //Drolpline Loan selectSql. append("DroppingMethod, DroplineFrq,FirstDroplineDate,PftServicingODLimit, " );
		 */

		selectSql.append(" From FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("FinReference", finReference);

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}

		return null;

	}

	@Override
	public List<String> getFinanceReferenceList(String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT FinReference From FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource("");
		logger.debug("Leaving");
		return this.jdbcTemplate.queryForList(selectSql.toString(), beanParameters, String.class);
	}

	@Override
	public List<LoanPendingData> getCustomerODLoanDetails(long userID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT FinReference, FM.CustID ,CM.CustCIF CustCIF,");
		selectSql.append(" CM.CustShrtName CustShrtName,CD.CustDocTitle PANNumber,CP.PhoneNumber");
		selectSql.append(" From FinanceMain_Temp FM left JOIN Customers CM ON CM.CustID = FM.CUSTID");
		selectSql.append(" left join customerdocuments CD ON CD.CustID = CM.CUSTID AND CUSTDOCCATEGORY='PPAN'");
		selectSql.append(
				"left join CustomerPhonenumbers CP ON CP.PhoneCustID = CM.CUSTID AND PhoneTypeCode='MOBILE' Where InitiateUser=:InitiateUser");

		source.addValue("InitiateUser", userID);
		RowMapper<LoanPendingData> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LoanPendingData.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

	}

	public int getActiveCount(String finType, long custID) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinType(finType);
		financeMain.setCustID(custID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");

		selectSql.append(" From FinanceMain");

		selectSql.append(" Where FinType =:FinType AND CUSTID =:custID AND FinIsActive = 1");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}

	@Override
	public int getODLoanCount(String finType, long custID) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinType(finType);
		financeMain.setCustID(custID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");

		selectSql.append(" From FinanceMain");

		selectSql.append(" Where FinType =:FinType  AND CUSTID =:custID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}

	@Override
	public List<FinanceMain> getUnApprovedFinances() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinType, AutoRejectionDays, FinReference, FinStartDate");
		sql.append(" From FinanceMain_Temp fm");
		sql.append(" Inner join RMTFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" where ft.AutoRejectionDays > ? and fm.RecordType = ? and fm.FinIsActive = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, 0);
			ps.setString(index++, "NEW");
			ps.setBoolean(index++, true);

		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinType(rs.getString("FinType"));
			fm.setAutoRejectionDays(rs.getInt("AutoRejectionDays"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinStartDate(rs.getTimestamp("FinStartDate"));

			return fm;
		});

	}

	@Override
	public void updateNextUserId(String finReference, String nextUserId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("NextUserId", nextUserId);

		StringBuilder sql = new StringBuilder("update FinanceMain_Temp");
		sql.append(" set NextUserId = :NextUserId");
		sql.append(" where FinReference = :FinReference");
		logger.debug(Literal.SQL + sql.toString());

		jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public String getNextUserId(String finReference) {
		logger.debug(Literal.ENTERING);
		String nextUserId = null;

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder("select NextUserId from FinanceMain_Temp");
		sql.append(" where FinReference = :FinReference");
		logger.debug(Literal.SQL + sql.toString());

		try {
			nextUserId = jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return nextUserId;
	}

	public FinanceMain getEntityNEntityDesc(String finreference, String type, boolean wif) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT T4.ENTITYCODE, T4.ENTITYDESC From");
		if (wif) {
			sql.append(" WifFinanceMain");
		} else {
			sql.append(" FinanceMain");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" T1");

		sql.append(" INNER JOIN RMTFINANCETYPES T2 ON T1.FINTYPE = T2.FINTYPE");
		sql.append(" INNER JOIN SMTDIVISIONDETAIL T3 ON T2.FINDIVISION=T3.DIVISIONCODE");
		sql.append(" INNER JOIN ENTITY T4 ON T4.ENTITYCODE = T3.ENTITYCODE");

		sql.append(" Where FinReference = :FinReference");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finreference);
		try {
			RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public FinanceType getFinTypeDetailsByFinreferene(String finReference, String type, boolean wif) {
		logger.debug("Entering");

		FinanceType financeType = new FinanceType();
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		StringBuilder sql = new StringBuilder(
				"SELECT FT.DEVELOPERFINANCE,FT.FinTypeClassification,FT.FinScheduleOn,FT.AlwEarlyPayMethods From ");
		if (wif) {
			sql.append("WifFinanceMain FM");
		} else {
			sql.append("FinanceMain FM");
		}
		sql.append(" Inner Join RmTFinanceTypes FT on FM.FinType = FT.FinType");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference");
		logger.debug("selectSql: " + sql.toString());

		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);
		try {
			financeType = this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			financeType = new FinanceType();
		}

		logger.debug("Leaving");
		return financeType;

	}

	/**
	 * getting closing status for perticular loan reference
	 * 
	 * @param finReference
	 * @param tempTab
	 * @param wif
	 */
	@Override
	public String getClosingStatus(String finReference, TableType tempTab, boolean wif) {
		logger.debug("Entering");

		String closingStaus = null;

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder("SELECT closingstatus From ");
		if (wif) {
			sql.append("WifFinanceMain");
		} else {
			sql.append("FinanceMain");
		}
		sql.append(StringUtils.trimToEmpty(tempTab.getSuffix()));
		sql.append(" Where FinReference = :FinReference");

		logger.debug("selectSql: " + sql.toString());

		try {
			closingStaus = this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			closingStaus = null;
		}

		logger.debug("Leaving");
		return closingStaus;
	}

	@Override
	public Date getClosedDateByFinRef(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ClosedDate");
		sql.append(" From FinanceMain");
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, Date.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * //### 18-07-2018 Ticket ID : 124998,receipt upload
	 */
	@Override
	public long getPartnerBankIdByReference(String finReference, String paymentMode, String fundingAc, String type,
			String purpose, boolean wif) {
		logger.debug("Entering");
		long partnerBankId = 0;

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("Purpose", purpose);
		source.addValue("PARTNERBANKCODE", fundingAc);
		source.addValue("PAYMENTMODE", paymentMode);

		StringBuilder sql = new StringBuilder("SELECT FP.PARTNERBANKID From ");
		if (wif) {
			sql.append("WifFinanceMain");
		} else {
			sql.append("FinanceMain");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" FM ");

		sql.append(" INNER JOIN FINTYPEPARTNERBANKS FP ON FP.FINTYPE =FM.FINTYPE ");
		sql.append(" INNER JOIN PARTNERBANKS PB ON PB.PARTNERBANKID =FP.PARTNERBANKID ");

		sql.append(" Where FM.FinReference = :FinReference");
		sql.append(" and FP.PURPOSE = :Purpose");
		sql.append(" and PB.PARTNERBANKCODE = :PARTNERBANKCODE");
		sql.append(" and FP.PAYMENTMODE = :PAYMENTMODE");

		logger.debug("selectSql: " + sql.toString());

		try {
			partnerBankId = this.jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			partnerBankId = 0;
		}

		logger.debug("Leaving");
		return partnerBankId;
	}

	/**
	 * //### 12-07-2018 Ticket ID : 12499
	 * 
	 * check whether loan reference with entity code is present
	 * 
	 * @param finReference
	 * @param type
	 * @param entity
	 */
	@Override
	public boolean isFinReferenceExitsWithEntity(String finReference, String type, String entity) {
		logger.debug("Entering");

		int entityCount = 0;

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FINREFERENCE", finReference);
		source.addValue("ENTITYCODE", entity);

		StringBuilder sql = new StringBuilder("SELECT COUNT(t4.ENTITYCODE) FROM ");
		sql.append("FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" t1");

		sql.append(" inner JOIN");
		sql.append(" RMTFinanceTypes  T2 ON T1.FinType = T2.FinType  inner JOIN");
		sql.append(" SMTDIVISIONDETAIL T3 ON T2.FinDivision=T3.DIVISIONCODE inner join");
		sql.append(" ENTITY t4 on t4.entitycode = t4.entitycode");

		sql.append(" Where t1.FINREFERENCE = :FINREFERENCE");
		sql.append(" and t4.ENTITYCODE = :ENTITYCODE");

		logger.debug("selectSql: " + sql.toString());

		try {
			entityCount = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			entityCount = 0;
		}

		if (entityCount > 0) {
			return true;
		}

		logger.debug("Leaving");
		return false;
	}

	/**
	 * 
	 * check whether loan reference is developer or not
	 * 
	 * @param finReference
	 * @param type
	 * @param wif
	 */
	@Override
	public boolean isDeveloperFinance(String finReference, String type, boolean wif) {

		logger.debug("Entering");

		boolean isdeveloperFinance = false;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		StringBuilder sql = new StringBuilder("SELECT ft.DEVELOPERFINANCE From ");
		if (wif) {
			sql.append("WifFinanceMain FM");
		} else {
			sql.append("FinanceMain FM");
		}
		sql.append(" Inner Join RmTFinanceTypes FT on FM.FinType = FT.FinType");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference");
		logger.debug("selectSql: " + sql.toString());

		try {
			isdeveloperFinance = this.jdbcTemplate.queryForObject(sql.toString(), source, Boolean.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			isdeveloperFinance = false;
		}

		logger.debug("Leaving");
		return isdeveloperFinance;

	}

	@Override
	public FinanceMain getFinanceDetailsByFinRefence(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder("SELECT * From ");
		sql.append("FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference");
		sql.append(" ORDER BY FinReference ASC,FinType ASC");
		logger.debug(Literal.SQL + sql.toString());

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			financeMain = this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.log(Level.ERROR, e.getCause(), e);
		}

		logger.debug(Literal.LEAVING);

		return financeMain;
	}

	@Override
	public List<String> getFinanceMainbyCustId(long custId, String type) {

		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setCustID(custId);
		financeMain.setFinIsActive(true);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference ");
		selectSql.append(" From FinanceMain");
		if (StringUtils.isNotBlank(type)) {
			selectSql.append(type);
		}
		selectSql.append(" Where CustID =:CustID AND FinIsActive = :FinIsActive");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		List<String> finReferencesList = new ArrayList<String>();
		try {
			finReferencesList = this.jdbcTemplate.queryForList(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return finReferencesList;

	}

	@Override
	public String getFinanceTypeFinReference(String finReference, String type) {

		logger.debug("Entering");

		String financeType = null;

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder("SELECT FinType From ");
		sql.append("FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference");

		logger.debug("selectSql: " + sql.toString());

		try {
			financeType = this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			financeType = null;
		}

		logger.debug("Leaving");
		return financeType;

	}

	@Override
	public void updateFinAssetValue(FinanceMain finMain) {

		StringBuilder sql = new StringBuilder("Update FinanceMain SET");
		sql.append(" LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, FinAssetValue = :FinAssetValue ");
		sql.append(" Where FinReference = :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finMain);

		jdbcTemplate.update(sql.toString(), paramSource);
	}

	@Override
	public FinanceMain getFinanceForAssignments(String finReference) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT FinReference, FinStartDate, MaturityDate, FinCurrAssetValue, FinAssetValue, AlwFlexi, FinType, FinIsActive, EntityCode, FinCcy, CustID, FinBranch, PromotionCode, PromotionSeqId, SvAmount, CbAmount, AssignmentId");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);

		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		return null;
	}

	@Override
	public List<FinanceMain> getFinListForIncomeAMZ(Date curMonthStart) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinStartDate, FinApprovedDate, ClosingStatus, FinIsActive, ClosedDate");
		sql.append(" from FinanceMain");
		sql.append(" Where MaturityDate >= ?");
		sql.append(" ORDER BY FinReference ");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setDate(index++, JdbcUtil.getDate(curMonthStart));
				}
			}, new RowMapper<FinanceMain>() {
				@Override
				public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceMain fm = new FinanceMain();

					fm.setFinReference(rs.getString("FinReference"));
					fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
					fm.setFinApprovedDate(rs.getTimestamp("FinApprovedDate"));
					fm.setClosingStatus(rs.getString("ClosingStatus"));
					fm.setFinIsActive(rs.getBoolean("FinIsActive"));
					fm.setClosedDate(rs.getTimestamp("ClosedDate"));

					return fm;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.ENTERING, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void updateAssignmentId(String finReference, long assignmentId) {
		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setAssignmentId(assignmentId);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set AssignmentId = :AssignmentId ");
		updateSql.append(" Where FinReference = :FinReference");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public Map<String, Object> getGLSubHeadCodes(String finRef) {
		final Map<String, Object> map = new HashMap<>();
		try {
			StringBuilder selectSql = selectGLSubHeadCodes();
			selectSql.append(" FROM GL_SubHeadCodes_View Where FINREFERENCE = ?");
			return this.jdbcOperations.query(selectSql.toString(), new Object[] { finRef }, (ResultSet rs) -> {
				while (rs.next()) {
					map.put("FINREFERENCE", rs.getString("FINREFERENCE"));
					map.put("ENTITYCODE", rs.getString("ENTITYCODE"));
					map.put("ALWFLEXI", rs.getBoolean("ALWFLEXI"));
					map.put("FINBRANCH", rs.getString("FINBRANCH"));
					map.put("BTLOAN", rs.getString("BTLOAN"));
					map.put("BUSINESSVERTICAL", rs.getString("BUSINESSVERTICAL"));
					map.put("EMPTYPE", rs.getString("EMPTYPE"));
					map.put("BRANCHCITY", rs.getString("BRANCHCITY"));
					map.put("FINCOLLATERALREQ", rs.getBoolean("FINCOLLATERALREQ"));
					map.put("FINDIVISION", rs.getString("FINDIVISION"));
				}
				return map;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records not found in GL_SubHeadCodes_View for the specified FinReference {}", finRef);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return map;
	}

	private StringBuilder selectGLSubHeadCodes() {
		StringBuilder selectSql = new StringBuilder("Select");
		selectSql.append(" FINREFERENCE, ENTITYCODE, ALWFLEXI, FINBRANCH, BTLOAN, BUSINESSVERTICAL");
		selectSql.append(", EMPTYPE, BRANCHCITY, FINCOLLATERALREQ, FINDIVISION");
		return selectSql;

	}

	@Override
	public int getCountByBlockedFinances(String finReference) {
		logger.debug("Entering");

		int count = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		StringBuilder sql = new StringBuilder("Select Count(*) from BlockedFinances");
		sql.append(" Where FinReference = :FinReference ");
		logger.debug("selectSql: " + sql.toString());

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			count = 0;
		}

		logger.debug("Leaving");
		return count;
	}

	@Override
	public void updateFromReceipt(FinanceMain financeMain, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update ");
		sql.append(" FinanceMain");
		sql.append(tableType.getSuffix());
		sql.append(" Set");
		sql.append(" BankName = :BankName");
		sql.append(", StepFinance = :StepFinance");
		sql.append(", RepayAccountId = :RepayAccountId");
		sql.append(", AccountsOfficer = :AccountsOfficer");
		// sql.append(", DevName = :DevName");
		sql.append(", PlanEMIHMax = :PlanEMIHMax");
		sql.append(", ShariaStatus = :ShariaStatus");
		sql.append(", RepayCpzFrq = :RepayCpzFrq");
		sql.append(", WorkflowId = :WorkflowId");
		sql.append(", UnPlanEMIHLockPeriod = :UnPlanEMIHLockPeriod");
		sql.append(", NextRoleCode = :NextRoleCode");
		sql.append(", GrcPftRate = :GrcPftRate");
		sql.append(", AllowGrcRepay = :AllowGrcRepay");
		sql.append(", GrcCpzFrq = :GrcCpzFrq");
		sql.append(", NextRolloverDate = :NextRolloverDate");
		sql.append(", LastRepayPftDate = :LastRepayPftDate");
		sql.append(", NextGrcPftDate = :NextGrcPftDate");
		sql.append(", RecalType = :RecalType");
		sql.append(", AllowedDefRpyChange = :AllowedDefRpyChange");
		// sql.append(", Rsa = :Rsa");
		sql.append(", MinDownPayPerc = :MinDownPayPerc");
		sql.append(", CustDSR = :CustDSR");
		sql.append(", RvwRateApplFor = :RvwRateApplFor");
		sql.append(", RateChgAnyDay = :RateChgAnyDay");
		sql.append(", DownPayAccount = :DownPayAccount");
		sql.append(", RcdMaintainSts = :RcdMaintainSts");
		sql.append(", MaxReAgeHolidays = :MaxReAgeHolidays");
		sql.append(", FinAccount = :FinAccount");
		sql.append(", DepreciationFrq = :DepreciationFrq");
		sql.append(", CustID = :CustID");
		// sql.append(", CofRate = :CofRate");
		sql.append(", RpyAdvPftRate = :RpyAdvPftRate");
		sql.append(", CalTerms = :CalTerms");
		// sql.append(", DropLineCalcOn = :DropLineCalcOn");
		// sql.append(", ReqloanTenor = :ReqloanTenor");
		sql.append(", FinAmount = :FinAmount");
		sql.append(", UnPlanEMICpz = :UnPlanEMICpz");
		sql.append(", PlanDeferCount = :PlanDeferCount");
		sql.append(", PlanEMIHAlw = :PlanEMIHAlw");
		sql.append(", PlanEMIHAlwInGrace = :PlanEMIHAlwInGrace");
		sql.append(", CalMaturity = :CalMaturity");
		sql.append(", DsaCode = :DsaCode");
		sql.append(", Defferments = :Defferments");
		// sql.append(", ProjectName = :ProjectName");
		sql.append(", BpiAmount = :BpiAmount");
		sql.append(", ApplicationNo = :ApplicationNo");
		sql.append(", LegalRequired = :LegalRequired");
		sql.append(", RoleCode = :RoleCode");
		sql.append(", FinPurpose = :FinPurpose");
		sql.append(", FinCurrAssetValue = :FinCurrAssetValue");
		sql.append(", FinStsReason = :FinStsReason");
		sql.append(", NextTaskId = :NextTaskId");
		// sql.append(", SourChannelCategory = :SourChannelCategory");
		sql.append(", GrcRateBasis = :GrcRateBasis");
		sql.append(", LastRepayRvwDate = :LastRepayRvwDate");
		sql.append(", RecordStatus = :RecordStatus");
		// sql.append(", ReqLoanAmt = :ReqLoanAmt");
		sql.append(", TotalGraceCpz = :TotalGraceCpz");
		sql.append(", RpyMaxRate = :RpyMaxRate");
		// sql.append(", AdvEMITerms = :AdvEMITerms");
		sql.append(", RpyMinRate = :RpyMinRate");
		sql.append(", AlwMultiDisb = :AlwMultiDisb");
		sql.append(", NextRepayCpzDate = :NextRepayCpzDate");
		sql.append(", FinRepaymentAmount = :FinRepaymentAmount");
		sql.append(", GrcAdvPftRate = :GrcAdvPftRate");
		sql.append(", DmaCode = :DmaCode");
		sql.append(", ReqRepayAmount = :ReqRepayAmount");
		sql.append(", MaxUnplannedEmi = :MaxUnplannedEmi");
		sql.append(", ScheduleRegenerated = :ScheduleRegenerated");
		sql.append(", GrcAdvMargin = :GrcAdvMargin");
		sql.append(", FixedRateTenor = :FixedRateTenor");
		sql.append(", DisbAccountId = :DisbAccountId");
		sql.append(", GrcProfitDaysBasis = :GrcProfitDaysBasis");
		sql.append(", DownPayBank = :DownPayBank");
		sql.append(", LastDepDate = :LastDepDate");
		sql.append(", DroplineFrq = :DroplineFrq");
		sql.append(", FinStatus = :FinStatus");
		sql.append(", NumberOfTerms = :NumberOfTerms");
		// sql.append(", AlwAutoRateRvw = :AlwAutoRateRvw");
		sql.append(", OverrideLimit = :OverrideLimit");
		sql.append(", Version = :Version");
		sql.append(", ScheduleMethod = :ScheduleMethod");
		sql.append(", RpyAdvBaseRate = :RpyAdvBaseRate");
		// sql.append(", PromotionSeqId = :PromotionSeqId");
		// sql.append(", SvAmount = :SvAmount");
		// sql.append(", CbAmount = :CbAmount");
		// sql.append(", BaseProduct = :BaseProduct");
		sql.append(", FirstRepay = :FirstRepay");
		sql.append(", RepayProfitRate = :RepayProfitRate");
		sql.append(", FinIsActive = :FinIsActive");
		sql.append(", GrcPeriodEndDate = :GrcPeriodEndDate");
		// sql.append(", Psl = :Psl");
		// sql.append(", BureauTimeSeries = :BureauTimeSeries");
		sql.append(", RpyAdvMargin = :RpyAdvMargin");
		sql.append(", RepayRvwFrq = :RepayRvwFrq");
		sql.append(", FinLimitRef = :FinLimitRef");
		sql.append(", ProductCategory = :ProductCategory");
		sql.append(", FinCustPftAccount = :FinCustPftAccount");
		sql.append(", FinRepayPftOnFrq = :FinRepayPftOnFrq");
		sql.append(", EmployeeName = :EmployeeName");
		sql.append(", TDSApplicable = :TDSApplicable");
		sql.append(", IncreasedCost = :IncreasedCost");
		// sql.append(", EndGrcPeriodAftrFullDisb = :EndGrcPeriodAftrFullDisb");
		sql.append(", Connector = :Connector");
		sql.append(", AvailedDefRpyChange = :AvailedDefRpyChange");
		sql.append(", ScheduleMaintained = :ScheduleMaintained");
		sql.append(", LastRepayDate = :LastRepayDate");
		sql.append(", LastRepay = :LastRepay");
		sql.append(", ReqMaturity = :ReqMaturity");
		// sql.append(", PMay = :PMay");
		sql.append(", PastduePftMargin = :PastduePftMargin");
		sql.append(", RepayFrq = :RepayFrq");
		sql.append(", TotalCpz = :TotalCpz");
		sql.append(", TotalGrossPft = :TotalGrossPft");
		sql.append(", SecurityDeposit = :SecurityDeposit");
		sql.append(", FinRepayMethod = :FinRepayMethod");
		sql.append(", AllowRepayRvw = :AllowRepayRvw");
		// sql.append(", TypeOfFacility = :TypeOfFacility");
		sql.append(", TotalProfit = :TotalProfit");
		// sql.append(", AlwStrtPrdHday = :AlwStrtPrdHday");
		sql.append(", AvailedUnPlanEmi = :AvailedUnPlanEmi");
		sql.append(", FinCategory = :FinCategory");
		sql.append(", GrcPftRvwFrq = :GrcPftRvwFrq");
		sql.append(", FinCommitmentRef = :FinCommitmentRef");
		sql.append(", TotalGrossGrcPft = :TotalGrossGrcPft");
		sql.append(", DownPaySupl = :DownPaySupl");
		sql.append(", AllowGrcPeriod = :AllowGrcPeriod");
		sql.append(", SalesDepartment = :SalesDepartment");
		sql.append(", PlanEMICpz = :PlanEMICpz");
		sql.append(", GrcMaxRate = :GrcMaxRate");
		sql.append(", FirstDroplineDate = :FirstDroplineDate");
		sql.append(", Iban = :Iban");
		sql.append(", MandateID = :MandateID");
		sql.append(", AvailedDefFrqChange = :AvailedDefFrqChange");
		// sql.append(", StrtprdCpzMethod = :StrtprdCpzMethod");
		sql.append(", CpzAtGraceEnd = :CpzAtGraceEnd");
		sql.append(", StepPolicy = :StepPolicy");
		sql.append(", AllowGrcCpz = :AllowGrcCpz");
		// sql.append(", AlwFlexi = :AlwFlexi");
		// sql.append(", CofCode = :CofCode");
		sql.append(", NextRepayPftDate = :NextRepayPftDate");
		sql.append(", AllowGrcPftRvw = :AllowGrcPftRvw");
		// sql.append(", AllowSubvention = :AllowSubvention");
		sql.append(", NextGrcPftRvwDate = :NextGrcPftRvwDate");
		sql.append(", FinContractDate = :FinContractDate");
		sql.append(", InvestmentRef = :InvestmentRef");
		// sql.append(", OfferAmount = :OfferAmount");
		sql.append(", PromotionCode = :PromotionCode");
		sql.append(", FinPreApprovedRef = :FinPreApprovedRef");
		sql.append(", LimitValid = :LimitValid");
		sql.append(", NoOfSteps = :NoOfSteps");
		sql.append(", FeeAccountId = :FeeAccountId");
		sql.append(", DdaReferenceNo = :DdaReferenceNo");
		sql.append(", SupplementRent = :SupplementRent");
		sql.append(", RoundingTarget = :RoundingTarget");
		sql.append(", LastRepayCpzDate = :LastRepayCpzDate");
		// sql.append(", EndUse = :EndUse");
		sql.append(", NextGrcCpzDate = :NextGrcCpzDate");
		sql.append(", PlanEMIHMethod = :PlanEMIHMethod");
		// sql.append(", PoSource = :PoSource");
		sql.append(", AdvanceEMI = :AdvanceEMI");
		sql.append(", AnualizedPercRate = :AnualizedPercRate");
		sql.append(", FinCcy = :FinCcy");
		sql.append(", TaskId = :TaskId");
		sql.append(", BpiPftDaysBasis = :BpiPftDaysBasis");
		sql.append(", StepType = :StepType");
		sql.append(", WifReference = :WifReference");
		sql.append(", InitiateDate = :InitiateDate");
		// sql.append(", ProcessType = :ProcessType");
		// sql.append(", OfferProduct = :OfferProduct");
		sql.append(", FinApprovedDate = :FinApprovedDate");
		sql.append(", GrcAdvBaseRate = :GrcAdvBaseRate");
		// sql.append(", SourcingBranch = :SourcingBranch");
		sql.append(", BpiTreatment = :BpiTreatment");
		sql.append(", NextRepayDate = :NextRepayDate");
		sql.append(", NextDepDate = :NextDepDate");
		sql.append(", DroppingMethod = :DroppingMethod");
		sql.append(", RepayBaseRate = :RepayBaseRate");
		sql.append(", SchCalOnRvw = :SchCalOnRvw");
		sql.append(", AvailedReAgeH = :AvailedReAgeH");
		sql.append(", FeeChargeAmt = :FeeChargeAmt");
		sql.append(", MigratedFinance = :MigratedFinance");
		// sql.append(", Verification = :Verification");
		sql.append(", FixedTenorRate = :FixedTenorRate");
		sql.append(", LastMntOn = :LastMntOn");
		// sql.append(", SurrogateType = :SurrogateType");
		sql.append(", GrcMaxAmount = :GrcMaxAmount");
		sql.append(", PlanEMIHLockPeriod = :PlanEMIHLockPeriod");
		sql.append(", InitiateUser = :InitiateUser");
		sql.append(", RepayPftFrq = :RepayPftFrq");
		sql.append(", ProfitDaysBasis = :ProfitDaysBasis");
		sql.append(", JointAccount = :JointAccount");
		sql.append(", FinBranch = :FinBranch");
		// sql.append(", StrtPrdHdays = :StrtPrdHdays");
		sql.append(", GrcPftFrq = :GrcPftFrq");
		sql.append(", FinSourceID = :FinSourceID");
		sql.append(", EligibilityMethod = :EligibilityMethod");
		sql.append(", CalRoundingMode = :CalRoundingMode");
		// sql.append(", CustSegmentation = :CustSegmentation");
		sql.append(", DeductFeeDisb = :DeductFeeDisb");
		sql.append(", FinType = :FinType");
		sql.append(", EffectiveRateOfReturn = :EffectiveRateOfReturn");
		sql.append(", GraceTerms = :GraceTerms");
		sql.append(", DownPayment = :DownPayment");
		sql.append(", FinAssetValue = :FinAssetValue");
		sql.append(", RolloverFrq = :RolloverFrq");
		// sql.append(", AlwUnderConstruction = :AlwUnderConstruction");
		sql.append(", InsuranceAmt = :InsuranceAmt");
		sql.append(", FinRemarks = :FinRemarks");
		sql.append(", Priority = :Priority");
		// sql.append(", Rog = :Rog");
		// sql.append(", ParentRef = :ParentRef");
		sql.append(", AllowedDefFrqChange = :AllowedDefFrqChange");
		sql.append(", DeviationApproval = :DeviationApproval");
		sql.append(", ManualSchedule = :ManualSchedule");
		// sql.append(", LeadSource = :LeadSource");
		sql.append(", GraceBaseRate = :GraceBaseRate");
		sql.append(", ReferralId = :ReferralId");
		sql.append(", SamplingRequired = :SamplingRequired");
		// sql.append(", ExistingLanRefNo = :ExistingLanRefNo");
		sql.append(", AlwBPI = :AlwBPI");
		sql.append(", AlwManualSteps = :AlwManualSteps");
		sql.append(", GrcMargin = :GrcMargin");
		sql.append(", RepayMargin = :RepayMargin");
		sql.append(", RepayRateBasis = :RepayRateBasis");
		sql.append(", LinkedFinRef = :LinkedFinRef");
		// sql.append(", CampaignName = :CampaignName");
		sql.append(", TotalRepayAmt = :TotalRepayAmt");
		sql.append(", JointCustId = :JointCustId");
		sql.append(", TakeOverFinance = :TakeOverFinance");
		sql.append(", TotalGracePft = :TotalGracePft");
		// sql.append(", LoanCategory = :LoanCategory");
		sql.append(", RepaySpecialRate = :RepaySpecialRate");
		sql.append(", RecordType = :RecordType");
		sql.append(", FinCancelAc = :FinCancelAc");
		sql.append(", ReAgeCpz = :ReAgeCpz");
		sql.append(", GraceSpecialRate = :GraceSpecialRate");
		// sql.append(", AlwRateChangeAnyDate = :AlwRateChangeAnyDate");
		sql.append(", AllowRepayCpz = :AllowRepayCpz");
		sql.append(", NextRepayRvwDate = :NextRepayRvwDate");
		sql.append(", PlanEMIHMaxPerYear = :PlanEMIHMaxPerYear");
		// sql.append(", FinSchCalCodeOnRvw = :FinSchCalCodeOnRvw");
		sql.append(", GrcSchdMthd = :GrcSchdMthd");
		sql.append(", LastMntBy = :LastMntBy");
		sql.append(", NextUserId = :NextUserId");
		sql.append(", FinStartDate = :FinStartDate");
		sql.append(", MaturityDate = :MaturityDate");
		sql.append(", ClosingStatus = :ClosingStatus");
		sql.append(", AccountType = :AccountType");
		sql.append(", PftServicingODLimit = :PftServicingODLimit");
		sql.append(", QuickDisb = :QuickDisb");
		// sql.append(", FlexiType = :FlexiType");
		// sql.append(", AutoIncGrcEndDate = :AutoIncGrcEndDate");
		sql.append(", GrcMinRate = :GrcMinRate");
		sql.append(", PastduePftCalMthd = :PastduePftCalMthd");
		sql.append(", MMAId = :MMAId");
		sql.append(", ReAgeBucket = :ReAgeBucket");

		// For InActive Loans, Update Loan Closed Date
		if (!financeMain.isFinIsActive()) {
			if (financeMain.getClosedDate() == null) {
				financeMain.setClosedDate(SysParamUtil.getAppDate());
			}
			sql.append(", ClosedDate = :ClosedDate");
		}

		sql.append(" where FinReference = :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeMain);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public FinanceMain isFlexiLoan(String finReference) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder("SELECT FinReference, FinType, AlwFlexi, FlexiType From FinanceMain");
		sql.append(" Where FinReference = :FinReference");

		logger.debug("selectSql: " + sql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public boolean isFinReferenceExitsinLQ(String finReference, TableType tempTab, boolean wif) {
		logger.debug("Entering");

		int count = 0;

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder("SELECT count(*) From ");
		if (wif) {
			sql.append("WifFinanceMain");
		} else {
			sql.append("FinanceMain");
		}
		sql.append(StringUtils.trimToEmpty(tempTab.getSuffix()));
		sql.append(" Where FinReference = :FinReference");
		sql.append(" and RCDMAINTAINSTS is null");

		logger.debug("selectSql: " + sql.toString());

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}

		if (count > 0) {
			return true;
		}

		logger.debug("Leaving");
		return false;
	}

	@Override
	public List<FinanceMain> getFinanceMainForLinkedLoans(long custId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" Select DISTINCT T1.FinReference, T1.FINTYPE, T1.FINSTARTDATE, T1.FINSTATUS, T1.FINCCY, T1.FinCurrAssetValue, T1.FeeChargeAmt, T1.InsuranceAmt, T1.FinRepaymentAmount");
		selectSql.append(" From FinanceMain T1");
		selectSql.append(" where T1.CustId = :CustId");

		source.addValue("CustId", custId);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		logger.debug("selectSql: " + selectSql.toString());

		List<FinanceMain> finMains = new ArrayList<>();
		try {
			finMains = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			finMains = new ArrayList<>();
		}

		logger.debug(Literal.LEAVING);

		return finMains;
	}

	@Override
	public List<FinanceMain> getFinanceMainForLinkedLoans(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" Select DISTINCT T1.FinReference, T1.FINTYPE, T1.FINSTARTDATE, T1.FINSTATUS, T1.FINCCY, T1.FinCurrAssetValue, T1.FeeChargeAmt, T1.InsuranceAmt, T1.FinRepaymentAmount");
		selectSql.append(" From FinanceMain T1");
		selectSql.append(" Inner Join CollateralAssignment T2 On T2.Reference = T1.FinReference");
		selectSql.append(" Where T2.COLLATERALREF In (select collateralref from COLLATERALASSIGNMENT ");
		selectSql.append(" Where Reference = :Reference )");

		source.addValue("Reference", finReference);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		logger.debug("selectSql: " + selectSql.toString());

		List<FinanceMain> finMains = new ArrayList<>();
		try {
			finMains = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			finMains = new ArrayList<>();
		}

		logger.debug(Literal.LEAVING);

		return finMains;
	}

	@Override
	public Map<String, Object> getGSTDataMap(String finReference, TableType tableType) {
		Map<String, Object> map = new HashMap<>();

		StringBuilder sql = new StringBuilder();
		sql.append("select fm.FinReference, fm.FinCCY, fm.finbranch FinBranch, cu.custdftbranch CustBranch");
		sql.append(", ca.custaddrprovince CustProvince, ca.custaddrcountry CustCountry, cu.ResidentialStatus ");
		sql.append(", cu.custResidentialSts CustResidentialSts");

		if (TableType.MAIN_TAB == tableType) {
			sql.append(" from FinanceMain fm");
			sql.append(" inner join Customers cu on cu.custId = fm.custid");
			sql.append(" inner join CustomerAddresses ca on ca.custId = cu.custid");
		} else if (TableType.TEMP_TAB == tableType) {
			// PT: When the loan is in TEMP table the customer will always be in MAIN table,
			// so changing the customers tables from TEMP to MAIN.
			sql.append(" from FinanceMain_Temp fm");
			sql.append(" inner join Customers cu on cu.custId = fm.custid");
			sql.append(" inner join CustomerAddresses ca on ca.custId = cu.custid");
		} else if (TableType.VIEW == tableType) {
			sql.append(" from FinanceMain_View fm");
			sql.append(" inner join Customers_View cu on cu.custId = fm.custid");
			sql.append(" inner join CustomerAddresses_View ca on ca.custId = cu.custid");
		}

		sql.append(" and custaddrpriority = :AddrPriority");
		sql.append(" Where fm.FinReference = :FinReference");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("AddrPriority", Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH));

		logger.trace(Literal.SQL + sql.toString());

		try {
			this.jdbcTemplate.query(sql.toString(), source, new RowMapper<Map<String, Object>>() {

				@Override
				public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
					map.put("FinReference", rs.getString("FinReference"));
					map.put("FinCCY", rs.getString("FinCCY"));
					map.put("FinBranch", rs.getString("FinBranch"));
					map.put("CustBranch", rs.getString("CustBranch"));
					map.put("CustProvince", rs.getString("CustProvince"));
					map.put("CustCountry", rs.getString("CustCountry"));
					map.put("ResidentialStatus", rs.getString("ResidentialStatus"));
					map.put("CustResidentialSts", rs.getString("CustResidentialSts"));
					return map;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return map;
	}

	@Override
	public Map<String, Object> getGSTDataMap(long custId, TableType tableType) {
		Map<String, Object> map = new HashMap<>();

		StringBuilder sql = new StringBuilder();
		sql.append("select cu.custdftbranch CustBranch");
		sql.append(", ca.custaddrprovince CustProvince, ca.custaddrcountry CustCountry, ResidentialStatus");
		sql.append(", cu.custResidentialSts CustResidentialSts");

		if (TableType.MAIN_TAB == tableType) {
			sql.append(" from Customers cu");
			sql.append(" inner join CustomerAddresses ca on ca.custId = cu.custid");
		} else if (TableType.TEMP_TAB == tableType) {
			sql.append(" from Customers_Temp cu");
			sql.append(" inner join CustomerAddresses_Temp ca on ca.custId = cu.custid");
		} else if (TableType.VIEW == tableType) {
			sql.append(" from Customers_View cu");
			sql.append(" inner join CustomerAddresses_View ca on ca.custId = cu.custid");
		}

		sql.append(" and custaddrpriority = :AddrPriority");
		sql.append(" Where cu.CustId = :CustId");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", custId);
		source.addValue("AddrPriority", Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH));

		try {
			this.jdbcTemplate.query(sql.toString(), source, new RowMapper<Map<String, Object>>() {

				@Override
				public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
					map.put("CustBranch", rs.getString("CustBranch"));
					map.put("CustProvince", rs.getString("CustProvince"));
					map.put("CustCountry", rs.getString("CustCountry"));
					map.put("ResidentialStatus", rs.getString("ResidentialStatus"));
					map.put("CustResidentialSts", rs.getString("CustResidentialSts"));
					return map;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return map;
	}

	@Override
	public boolean isFinActive(String finReference) {
		StringBuilder sql = new StringBuilder();
		sql.append("select FinisActive");
		sql.append(" From FinanceMain");
		sql.append(" Where FinReference = :finReference");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("finReference", finReference);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), paramSource, Boolean.class);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return false;
	}

	@Override
	public String getFinanceMainByRcdMaintenance(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = null;

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" RcdMaintainSts");
		sql.append(" from FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());
		try {
			fm = this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<FinanceMain>() {
						@Override
						public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinanceMain fm = new FinanceMain();

							fm.setRcdMaintainSts(rs.getString("RcdMaintainSts"));

							return fm;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return fm.getRcdMaintainSts();
	}

	@Override
	public void deleteFinreference(FinanceMain financeMain, TableType tableType, boolean wifi, boolean finilize) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from");
		if (wifi) {
			sql.append(" WIFFinanceMain");
		} else {
			sql.append(" FinanceMain");
		}
		sql.append(tableType.getSuffix());
		sql.append(" where FinReference = :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeMain);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public FinanceMain getFinanceMainByOldFinReference(String oldFinReference, boolean active) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT  FinReference, PlanDeferCount, ");
		selectSql.append("  AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		selectSql.append(
				" AvailedDefFrqChange, FinIsActive,PromotionCode,OldFinReference,FinType,FinCategory, FinBranch,");
		selectSql.append(" CustID,PromotionCode");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where OldFinReference =:OldFinReference AND FinIsActive = :FinIsActive");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("OldFinReference", oldFinReference);
		source.addValue("FinIsActive", active);

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}

	// IND AS - START

	/**
	 * Calculate Average POS
	 * 
	 * @param finReference
	 */
	@Override
	public List<FinanceMain> getFinancesByFinApprovedDate(Date finApprovalStartDate, Date finApprovalEndDate) {
		logger.debug("Entering");

		List<FinanceMain> finMains = null;

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference, FinType, FinCcy, CustID, FinBranch, ");
		selectSql.append(" FinStartDate, FinApprovedDate, MaturityDate, FinAssetValue, FinCurrAssetValue, FinAmount, ");
		selectSql.append(" FinCategory, ProductCategory, FinStatus, ");
		selectSql.append(" CalRoundingMode, RoundingTarget, ProfitDaysBasis, ");
		selectSql.append(" ClosingStatus, FinIsActive, EntityCode ");

		selectSql.append(" WHERE FinApprovedDate >= :FinApprovalStartDate And FinApprovedDate <= :FinApprovalEndDate");

		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinApprovalStartDate", finApprovalStartDate);
		source.addValue("FinApprovalEndDate", finApprovalEndDate);

		try {
			RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
			finMains = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finMains = new ArrayList<FinanceMain>();
		}

		logger.debug("Leaving");
		return finMains;
	}

	/**
	 * Method to get Finance for income Amortization.
	 * 
	 * @param finReference
	 */
	@Override
	public FinanceMain getFinanceForIncomeAMZ(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinType, CustID, ClosingStatus, FinIsActive, MaturityDate, ClosedDate");
		sql.append(" from FinanceMain");
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<FinanceMain>() {
						@Override
						public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinanceMain fm = new FinanceMain();

							fm.setFinReference(rs.getString("FinReference"));
							fm.setFinType(rs.getString("FinType"));
							fm.setCustID(rs.getLong("CustID"));
							fm.setClosingStatus(rs.getString("ClosingStatus"));
							fm.setFinIsActive(rs.getBoolean("FinIsActive"));
							fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
							fm.setClosedDate(rs.getTimestamp("ClosedDate"));

							return fm;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * 
	 */
	@Override
	public List<FinanceMain> getFinListForAMZ(Date monthEndDate) {
		logger.debug("Entering");

		// get the finances list
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("MonthStartDate", DateUtility.getMonthStart(monthEndDate));

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference, FinType, FinCcy, CustID, FinBranch, ");
		selectSql.append(" FinStartDate, FinApprovedDate, MaturityDate, FinAssetValue, FinCurrAssetValue, FinAmount, ");
		selectSql.append(" FinCategory, ProductCategory, FinStatus, ");
		selectSql.append(" CalRoundingMode, RoundingTarget, ProfitDaysBasis, ");
		selectSql.append(" ClosingStatus, FinIsActive, EntityCode, ClosedDate ");

		selectSql.append(" From FinanceMain");
		selectSql.append(" WHERE MaturityDate >= :MonthStartDate ");

		logger.debug("selectSql : " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public int getCountByFinReference(String finReference, boolean active) {

		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setFinIsActive(active);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");

		selectSql.append(" From FinanceMain");

		selectSql.append(" Where FinReference =:FinReference AND FinIsActive = :FinIsActive");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}

	}

	@Override
	public int getCountByOldFinReference(String hostReference) {

		logger.debug("Entering");

		FinanceMainExtension financeMainExtension = new FinanceMainExtension();
		financeMainExtension.setHostreference(hostReference);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");

		selectSql.append(" From Financemain_Extension");

		selectSql.append(" Where Hostreference =:Hostreference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMainExtension);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}

	}

	// IND AS - END

	@Override
	public long getLoanWorkFlowIdByFinRef(String loanReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT WorkflowId From ");
		sql.append("FinanceMain");
		sql.append(type);
		sql.append(" Where FinReference = :FinReference");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", loanReference);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return 0;
	}

	@Override
	public String getLovDescEntityCode(String finReference, String type) {
		StringBuilder sql = new StringBuilder("SELECT LovDescEntityCode From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = :FinReference");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public long saveHostRef(FinanceMainExtension financeMainExtension) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into ");
		sql.append("FINANCEMAIN_EXTENSION");
		sql.append(" (FinId, Finreference, Hostreference, Oldhostreference)");
		sql.append(" values (:finId, :finreference, :hostreference, :oldhostreference)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeMainExtension);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return financeMainExtension.getId();
	}

	@Override
	public FinanceMain getFinanceMainByHostReference(String oldFinReference, boolean active) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT  FinReference, PlanDeferCount, svAmount,CbAmount, ");
		selectSql.append(
				"  AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange, FinAmount, DownPayment,FinStartDate,PROMOTIONSEQID,");
		selectSql.append(
				" AvailedDefFrqChange, FinIsActive,PromotionCode,OldFinReference, FinCategory,PromotionSeqId,FinType,FinBranch,MandateId,custID");
		selectSql.append(" From FinanceMain");
		selectSql.append(
				" Where FinReference = (Select FinReference from Financemain_Extension Where HostReference = :OldFinReference)");
		selectSql.append(" AND FinIsActive = :FinIsActive");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("OldFinReference", oldFinReference);
		source.addValue("FinIsActive", active);

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}

	@Override
	public int getCountByExternalReference(String oldHostReference) {

		logger.debug("Entering");

		FinanceMainExtension financeMain = new FinanceMainExtension();
		financeMain.setOldhostreference(oldHostReference);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");

		selectSql.append(" From Financemain_Extension");

		selectSql.append(" Where OldHostreference =:Oldhostreference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}

	}

	@Override
	public int getCountByOldHostReference(String oldHostReference) {

		logger.debug("Entering");

		FinanceMainExtension financeMain = new FinanceMainExtension();
		financeMain.setOldhostreference(oldHostReference);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) ");
		selectSql.append(" From Financemain Where Finreference = (Select Finreference From FinanceMain_Extension");
		selectSql.append(" Where OldHostReference = :Oldhostreference)  AND FinIsActive = 0");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
	}

	// Reinstate Loan
	@Override
	public void updateRejectFinanceMain(FinanceMain financeMain, TableType tableType, boolean isWIF) {

		StringBuilder sql = new StringBuilder("update financemain");
		sql.append(tableType.getSuffix());
		sql.append(" set Approved = :Approved");
		sql.append(", ProcessAttributes = :ProcessAttributes");
		sql.append(", FinIsActive = :FinIsActive");
		sql.append(", NextTaskId = :NextTaskId");
		sql.append(", RecordStatus= :RecordStatus");
		sql.append(", NextRoleCode = :NextRoleCode");
		sql.append(" where FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeMain);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinanceMain getFinanceMainStutusById(String id, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinReference,RecordStatus, RoleCode, NextRoleCode from FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("FinReference", id);

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}

	@Override
	public FinanceMain getFinanceDetailsForInsurance(String finReference, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(finReference);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference, FinType, Finccy ");
		selectSql.append(" From FinanceMain");

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			financeMain = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}

	@Override
	public List<FinanceMain> getFinMainListBySQLQueryRule(String whereClause, String type) {
		StringBuilder sql = new StringBuilder("SELECT FinReference,GraceTerms, NumberOfTerms,");
		sql.append(" GrcPeriodEndDate, GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate,");
		sql.append(" AllowGrcPftRvw,GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,");
		sql.append(" RepayBaseRate,RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, ");
		sql.append(" AllowRepayRvw,RepayRvwFrq,NextRepayRvwDate,AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		sql.append(" MaturityDate, CpzAtGraceEnd,DownPayment, ReqRepayAmount, TotalProfit, DownPayBank,");
		sql.append(" TotalCpz,TotalGrossPft,TotalGracePft,TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		sql.append(" NextRepayPftDate,GrcRateBasis,RepayRateBasis, FinType,FinRemarks, FinCcy, ScheduleMethod,");
		sql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,DownPaySupl,");
		sql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments, PlanDeferCount, ");
		sql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		sql.append(" AvailedDefFrqChange, RecalType, FinAssetValue, DisbAccountId, RepayAccountId,FinIsActive,");
		sql.append(" LastRepayDate, LastRepayPftDate,LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, ");
		sql.append(" GrcMargin, RepayMargin, FinCommitmentRef, DepreciationFrq, FinCurrAssetValue,");
		sql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount, ClosingStatus, FinApprovedDate,");
		sql.append(" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq , GrcProfitDaysBasis, ");
		sql.append(" LinkedFinRef, GrcMinRate, GrcMaxRate , RpyMinRate, RpyMaxRate,GrcSchdMthd, StepPolicy,");
		sql.append(" ManualSchedule, TakeOverFinance , GrcAdvBaseRate ,GrcAdvMargin ,GrcAdvPftRate,");
		sql.append(" SupplementRent, IncreasedCost, feeAccountId, MinDownPayPerc,TDSApplicable, FeeChargeAmt,");
		sql.append(" PlanEMIHMethod, PlanEMIHMaxPerYear, PlanEMIHMax, PlanEMIHLockPeriod , PlanEMICpz, ");
		sql.append(" DeductFeeDisb,RvwRateApplFor, SchCalOnRvw,PastduePftCalMthd,DroppingMethod,RateChgAnyDay,");
		sql.append(" InvestmentRef,DownPayAccount,SecurityDeposit, RcdMaintainSts,FinRepayMethod, FinCancelAc,");
		sql.append(" MigratedFinance,ScheduleMaintained,ScheduleRegenerated,CustDSR,JointAccount,JointCustId,");
		sql.append(" Blacklisted,OverrideLimit,FinPurpose,FinStatus,FinStsReason,InitiateUser,RpyAdvMargin,");
		sql.append(" BankName, Iban, AccountType, DdaReferenceNo, NextUserId, Priority, AlwManualSteps,");
		sql.append(" RolloverFrq, NextRolloverDate,ShariaStatus,InitiateDate,MMAId,AccountsOfficer,DsaCode, ");
		sql.append(" ReferralId, DmaCode, SalesDepartment, QuickDisb, WifReference, UnPlanEMIHLockPeriod, ");
		sql.append(" MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, PromotionCode, ApplicationNo, AlwBPI,");
		sql.append(" CalRoundingMode , AlwMultiDisb, BpiAmount, PastduePftMargin,FinCategory,ProductCategory,");
		sql.append(" DeviationApproval,FinPreApprovedRef,MandateID,FirstDroplineDate,PftServicingODLimit,");
		sql.append(
				" UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi,BpiTreatment, PlanEMIHAlw, PlanEMIHAlwInGrace, InsuranceAmt,");
		sql.append(" RpyAdvPftRate, StepType, DroplineFrq,RpyAdvBaseRate,NoOfSteps,StepFinance,FinContractDate");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(" , lovDescFinTypeName, lovDescFinBranchName, ");
			sql.append(" lovDescAccruedTillLBD, lovDescFinScheduleOn, CostOfFunds, ARFSuspendSubvention,");
			sql.append(" SubventionTillMonths, AllowSuspendSubvention, SuspendBucket,SubventionApplicable,");
			sql.append(" LovDescStepPolicyName,CustStsDescription, lovDescAccountsOfficer,DsaCodeDesc, ");
			sql.append(" ReAgeBucket, FinLimitRef, ReferralIdDesc, DmaCodeDesc, SalesDepartmentDesc,");
			sql.append(" CustNationality, CustParentCountry, CustGenderCode, CustIsStaff, CustAddrProvince");
			sql.append(" FinDivision, CustIndustry, CustCtgCode, CustDftBranch, CustEmpSts, CustMaritalSts, ");
			sql.append(" CustRiskCountry, CustSector,CustSegment, CustSubSector, CustSubSegment, CustTypeCode");
		}

		if (type.equals("_LCFTView")) {
			sql.append(" , 1 LimitValid");
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

		logger.debug("selectSql: " + sql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.jdbcTemplate.query(sql.toString(), new MapSqlParameterSource(), typeRowMapper);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<>();

	}

	@Override
	public FinanceMain getFinanceMainDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select T1.FinIsActive, T1.FinStartDate, T1.FinBranch,T36.ENTITYCODE As LovDescEntityCode");
		sql.append(", T7.FINDIVISION As LovDescFinDivision FROM FINANCEMAIN T1 ");
		sql.append(" JOIN RMTFINANCETYPES T7 ON T1.FINTYPE = T7.FINTYPE");
		sql.append(" LEFT JOIN RMTBRANCHES T12 ON T1.FINBRANCH = T12.BRANCHCODE");
		sql.append(" LEFT JOIN FINPFTDETAILS T17 ON T17.FINREFERENCE = T1.FINREFERENCE");
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
		sql.append(" AND T38.FIELDCODE = :ELGMETHOD");
		sql.append(" LEFT JOIN LOANPURPOSES T39 ON T1.FINPURPOSE = T39.LOANPURPOSECODE");
		sql.append(" LEFT JOIN AMTVEHICLEDEALER T40 ON T1.CONNECTOR = T40.DEALERID");
		sql.append(" LEFT JOIN BUSINESS_VERTICAL BV ON BV.ID = T1.BUSINESSVERTICAL");
		sql.append(" Where T1.FinReference =:FinReference");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ELGMETHOD", "ELGMETHOD");
		source.addValue("FinReference", finReference);

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public boolean isFinExistsByPromotionSeqID(long referenceId) {
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from FinanceMain_View ");
		sql.append(" Where PromotionSeqID = :PromotionSeqID ");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("PromotionSeqID", referenceId);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return false;
	}

	@Override
	public boolean isRepayFrqExists(String brType) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from FinanceMain_View ");
		sql.append(" Where RepayBaseRate = :RepayBaseRate ");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("RepayBaseRate", brType);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}
		return false;
	}

	@Override
	public boolean isGrcRepayFrqExists(String brType) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select COUNT(*) from FinanceMain_View ");
		sql.append(" Where GrcPftRvwFrq = :GrcPftRvwFrq ");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("GrcPftRvwFrq", brType);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}
		return false;
	}

	@Override
	public Date getFinStartDate(String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		StringBuilder sql = new StringBuilder("SELECT FinStartdate From FinanceMain");
		sql.append(" Where FinReference = :FinReference");
		logger.debug("selectSql: " + sql.toString());

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Date.class);
	}

	@Override
	public FinanceMain getFinanceMain(String finReference, String[] columns, String type) {
		StringBuilder sql = new StringBuilder("select ");
		StringBuilder fields = new StringBuilder();

		for (String column : columns) {
			if (fields.length() > 0) {
				fields.append(",");
			}

			fields.append(column);
		}
		sql.append(fields.toString());
		sql.append(" From");
		sql.append(" FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where FinReference = :FinReference");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;

	}

	@Override
	public List<FinanceEnquiry> getAllFinanceDetailsByCustId(long custId) {
		logger.debug(Literal.ENTERING);

		List<FinanceEnquiry> finEnquiry = null;

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinBranch, FinType, FinCcy, ScheduleMethod, ProfitDaysBasis");
		sql.append(", FinStartDate, NumberOfTerms, CustID, FinAmount, GrcPeriodEndDate, MaturityDate");
		sql.append(", FinRepaymentAmount, FinIsActive, AllowGrcPeriod, LovDescFinTypeName, LovDescCustCIF");
		sql.append(", LovDescCustShrtName, LovDescFinBranchName, Blacklisted, LovDescFinScheduleOn");
		sql.append(", FeeChargeAmt, ClosingStatus, CustTypeCtg, GraceTerms, lovDescFinDivision");
		sql.append(", LovDescProductCodeName, Defferments, FinRepayMethod, MandateID");
		sql.append(" from FinanceEnquiry_View");
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());
		try {
			finEnquiry = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, custId);
				}
			}, new RowMapper<FinanceEnquiry>() {
				@Override
				public FinanceEnquiry mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceEnquiry finEnquiry = new FinanceEnquiry();

					finEnquiry.setFinReference(rs.getString("FinReference"));
					finEnquiry.setFinBranch(rs.getString("FinBranch"));
					finEnquiry.setFinType(rs.getString("FinType"));
					finEnquiry.setFinCcy(rs.getString("FinCcy"));
					finEnquiry.setScheduleMethod(rs.getString("ScheduleMethod"));
					finEnquiry.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
					finEnquiry.setFinStartDate(rs.getTimestamp("FinStartDate"));
					finEnquiry.setNumberOfTerms(rs.getInt("NumberOfTerms"));
					finEnquiry.setCustID(rs.getLong("CustID"));
					finEnquiry.setFinAmount(rs.getBigDecimal("FinAmount"));
					finEnquiry.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
					finEnquiry.setMaturityDate(rs.getTimestamp("MaturityDate"));
					finEnquiry.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
					finEnquiry.setFinIsActive(rs.getBoolean("FinIsActive"));
					finEnquiry.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
					finEnquiry.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
					finEnquiry.setLovDescCustCIF(rs.getString("LovDescCustCIF"));
					finEnquiry.setLovDescCustShrtName(rs.getString("LovDescCustShrtName"));
					finEnquiry.setLovDescFinBranchName(rs.getString("LovDescFinBranchName"));
					finEnquiry.setBlacklisted(rs.getBoolean("Blacklisted"));
					finEnquiry.setLovDescFinScheduleOn(rs.getString("LovDescFinScheduleOn"));
					finEnquiry.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
					finEnquiry.setClosingStatus(rs.getString("ClosingStatus"));
					finEnquiry.setCustTypeCtg(rs.getString("CustTypeCtg"));
					finEnquiry.setGraceTerms(rs.getInt("GraceTerms"));
					finEnquiry.setLovDescFinDivision(rs.getString("lovDescFinDivision"));
					finEnquiry.setLovDescProductCodeName(rs.getString("lovDescProductCodeName"));
					finEnquiry.setDefferments(rs.getInt("Defferments"));
					finEnquiry.setFinRepayMethod(rs.getString("FinRepayMethod"));
					finEnquiry.setMandateID(rs.getLong("MandateID"));

					return finEnquiry;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return finEnquiry;

	}

	private StringBuilder getFinSelectQueryForEod() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, GrcPeriodEndDate, AllowGrcPeriod, GraceBaseRate, GraceSpecialRate");
		sql.append(", GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw, GrcPftRvwFrq, NextGrcPftRvwDate");
		sql.append(", AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate, RepaySpecialRate, RepayProfitRate");
		sql.append(", RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate, AllowRepayRvw, RepayRvwFrq");
		sql.append(", NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate, MaturityDate");
		sql.append(", CpzAtGraceEnd, GrcRateBasis, RepayRateBasis, FinType, FinCcy, ProfitDaysBasis");
		sql.append(", FirstRepay, LastRepay, ScheduleMethod, FinStartDate, FinAmount, CustID, FinBranch");
		sql.append(", FinSourceID, RecalType, FinIsActive, LastRepayDate, LastRepayPftDate, LastRepayRvwDate");
		sql.append(", LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd, GrcMargin, RepayMargin, ClosingStatus");
		sql.append(", FinRepayPftOnFrq, GrcProfitDaysBasis, GrcMinRate, GrcMaxRate, GrcMaxAmount, RpyMinRate");
		sql.append(", RpyMaxRate, ManualSchedule, CalRoundingMode, RoundingTarget, RvwRateApplFor");
		sql.append(", SchCalOnRvw, PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin");
		sql.append(", FinRepayMethod, MigratedFinance, ScheduleMaintained, ScheduleRegenerated, MandateID");
		sql.append(", FinStatus, DueBucket, FinStsReason, BankName, Iban, AccountType, DdaReferenceNo");
		sql.append(", PromotionCode, FinCategory, ProductCategory, ReAgeBucket, TDSApplicable, BpiTreatment");
		sql.append(", FinRepaymentAmount, GrcAdvType, AdvType, SanBsdSchdle");
		sql.append(", PromotionSeqId, SvAmount, CbAmount, EmployeeName");
		sql.append(", AlwGrcAdj, EndGrcPeriodAftrFullDisb, AutoIncGrcEndDate");
		sql.append(", Version, LastMntOn, ReferralId, GraceTerms");
		sql.append(", FinAssetValue, FinCurrAssetValue");
		sql.append(", NumberOfTerms, Alwmultidisb");
		sql.append(" from FinanceMain");
		return sql;
	}

	public class FinRowMapperForEod implements RowMapper<FinanceMain> {

		@Override
		public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {

			FinanceMain fm = new FinanceMain();

			fm.setFinReference(rs.getString("FinReference"));
			fm.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
			fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
			fm.setGraceBaseRate(rs.getString("GraceBaseRate"));
			fm.setGraceSpecialRate(rs.getString("GraceSpecialRate"));
			fm.setGrcPftRate(rs.getBigDecimal("GrcPftRate"));
			fm.setGrcPftFrq(rs.getString("GrcPftFrq"));
			fm.setNextGrcPftDate(rs.getTimestamp("NextGrcPftDate"));
			fm.setAllowGrcPftRvw(rs.getBoolean("AllowGrcPftRvw"));
			fm.setGrcPftRvwFrq(rs.getString("GrcPftRvwFrq"));
			fm.setNextGrcPftRvwDate(rs.getTimestamp("NextGrcPftRvwDate"));
			fm.setAllowGrcCpz(rs.getBoolean("AllowGrcCpz"));
			fm.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
			fm.setNextGrcCpzDate(rs.getTimestamp("NextGrcCpzDate"));
			fm.setRepayBaseRate(rs.getString("RepayBaseRate"));
			fm.setRepaySpecialRate(rs.getString("RepaySpecialRate"));
			fm.setRepayProfitRate(rs.getBigDecimal("RepayProfitRate"));
			fm.setRepayFrq(rs.getString("RepayFrq"));
			fm.setNextRepayDate(rs.getTimestamp("NextRepayDate"));
			fm.setRepayPftFrq(rs.getString("RepayPftFrq"));
			fm.setNextRepayPftDate(rs.getTimestamp("NextRepayPftDate"));
			fm.setAllowRepayRvw(rs.getBoolean("AllowRepayRvw"));
			fm.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
			fm.setNextRepayRvwDate(rs.getTimestamp("NextRepayRvwDate"));
			fm.setAllowRepayCpz(rs.getBoolean("AllowRepayCpz"));
			fm.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
			fm.setNextRepayCpzDate(rs.getTimestamp("NextRepayCpzDate"));
			fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
			fm.setCpzAtGraceEnd(rs.getBoolean("CpzAtGraceEnd"));
			fm.setGrcRateBasis(rs.getString("GrcRateBasis"));
			fm.setRepayRateBasis(rs.getString("RepayRateBasis"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
			fm.setFirstRepay(rs.getBigDecimal("FirstRepay"));
			fm.setLastRepay(rs.getBigDecimal("LastRepay"));
			fm.setScheduleMethod(rs.getString("ScheduleMethod"));
			fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
			fm.setFinAmount(rs.getBigDecimal("FinAmount"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinSourceID(rs.getString("FinSourceID"));
			fm.setRecalType(rs.getString("RecalType"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setLastRepayDate(rs.getTimestamp("LastRepayDate"));
			fm.setLastRepayPftDate(rs.getTimestamp("LastRepayPftDate"));
			fm.setLastRepayRvwDate(rs.getTimestamp("LastRepayRvwDate"));
			fm.setLastRepayCpzDate(rs.getTimestamp("LastRepayCpzDate"));
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
			fm.setMandateID(rs.getLong("MandateID"));
			fm.setFinStatus(rs.getString("FinStatus"));
			fm.setDueBucket(rs.getInt("DueBucket"));
			fm.setFinStsReason(rs.getString("FinStsReason"));
			fm.setBankName(rs.getString("BankName"));
			fm.setIban(rs.getString("Iban"));
			fm.setAccountType(rs.getString("AccountType"));
			fm.setDdaReferenceNo(rs.getString("DdaReferenceNo"));
			fm.setPromotionCode(rs.getString("PromotionCode"));
			fm.setFinCategory(rs.getString("FinCategory"));
			fm.setProductCategory(rs.getString("ProductCategory"));
			fm.setReAgeBucket(rs.getInt("ReAgeBucket"));
			fm.setTDSApplicable(rs.getBoolean("TDSApplicable"));
			fm.setBpiTreatment(rs.getString("BpiTreatment"));
			fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
			fm.setGrcAdvType(rs.getString("GrcAdvType"));
			fm.setAdvType(rs.getString("AdvType"));
			fm.setSanBsdSchdle(rs.getBoolean("SanBsdSchdle"));
			fm.setPromotionSeqId(rs.getLong("PromotionSeqId"));
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
			fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			fm.setAlwMultiDisb(rs.getBoolean("Alwmultidisb"));

			return fm;

		}

	}

	@Override
	public void updateCustChange(long newCustId, long mandateId, String finReference, String type) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setCustID(newCustId);
		financeMain.setMandateID(mandateId);

		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(type);
		sql.append(" Set CustID = :CustID, MandateID = :MandateID");
		sql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);

		int recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<UserPendingCases> getUserPendingCasesDetails(long usrId, String roleCodes) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append("  T1.finreference, T1.recordstatus");
		sql.append(", T1.rolecode, T2.roledesc, T1.FinType, T1.InitiateDate");
		sql.append(", T3.CustShrtName, T3.PhoneNumber");
		sql.append(" FROM Financemain_Temp T1");
		sql.append(" JOIN Secroles T2 ON T1.nextrolecode = T2.rolecd");
		sql.append(" JOIN Customers T3 ON T1.custid = T3.custid");
		sql.append(" Where T1.nextrolecode = :rolecd");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("usrId", usrId);
		source.addValue("rolecd", roleCodes);
		RowMapper<UserPendingCases> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(UserPendingCases.class);
		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	private String getFinMainAllQuery(String type, boolean wif) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  FinReference, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod, GraceBaseRate");
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
		sql.append(", RecalType, FinAssetValue, DisbAccountId, RepayAccountId, FinIsActive, Version");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, MinDownPayPerc, LastRepayDate, LastRepayPftDate, LastRepayRvwDate");
		sql.append(", LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd, GrcMargin, RepayMargin, FinCurrAssetValue");
		sql.append(", FinCommitmentRef, DepreciationFrq, FinContractDate, NextDepDate, LastDepDate");
		sql.append(", FinAccount, FinCustPftAccount, TotalRepayAmt, FinApprovedDate, FeeChargeAmt");
		sql.append(", FinRepayPftOnFrq, AnualizedPercRate, EffectiveRateOfReturn, DownPayBank, DownPaySupl");
		sql.append(", GraceTerms, GrcProfitDaysBasis, StepFinance, StepType, StepPolicy, AlwManualSteps");
		sql.append(", NoOfSteps, LinkedFinRef, GrcMinRate, GrcMaxRate, GrcMaxAmount, RpyMinRate, RpyMaxRate");
		sql.append(", ManualSchedule, TakeOverFinance, GrcAdvBaseRate, GrcAdvMargin, GrcAdvPftRate");
		sql.append(", RpyAdvBaseRate, RpyAdvMargin, RpyAdvPftRate, SupplementRent, IncreasedCost, FeeAccountId");
		sql.append(", TDSApplicable, InsuranceAmt, DeductInsDisb, AlwBPI, BpiTreatment, PlanEMIHAlw");
		sql.append(", PlanEMIHMethod, PlanEMIHMaxPerYear, PlanEMIHMax, PlanEMIHLockPeriod, PlanEMICpz");
		sql.append(", CalRoundingMode, RoundingTarget, AlwMultiDisb, BpiAmount, DeductFeeDisb, RvwRateApplFor");
		sql.append(", SchCalOnRvw, PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin");
		sql.append(", FinCategory, ProductCategory, AdvanceEMI, BpiPftDaysBasis, FixedTenorRate, FixedRateTenor");
		sql.append(", GrcAdvType, GrcAdvTerms, AdvType, AdvTerms, AdvStage, AllowDrawingPower, AllowRevolving");
		sql.append(", SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount, AppliedLoanAmt");
		sql.append(", FinIsRateRvwAtGrcEnd, ClosingStatus");

		if (!wif) {
			sql.append(", DmaCode, TdsPercentage, FinStsReason, Connector, samplingRequired, LimitApproved");
			sql.append(", NextUserId, VanCode, FinLimitRef, ShariaStatus, RolloverFrq, DdaReferenceNo");
			sql.append(", legalRequired, CreditInsAmt, Blacklisted, FinRepayMethod, FirstDroplineDate");
			sql.append(", CustDSR, BankName, DownPayAccount, AccountsOfficer, QuickDisb, UnPlanEMICpz");
			sql.append(", ReAgeCpz, AvailedReAgeH, iban, SalesDepartment, DroplineFrq, NextRolloverDate");
			sql.append(", SecurityDeposit, PromotionCode, TdsLimitAmt, MigratedFinance, MaxReAgeHolidays");
			sql.append(", WifReference, UnPlanEMIHLockPeriod, TdsEndDate, Priority, Discrepancy, DeviationApproval");
			sql.append(", ScheduleMaintained, FinPurpose, ScheduleRegenerated, SecurityCollateral, RcdMaintainSts");
			sql.append(", MaxUnplannedEmi, DsaCode, ReferralId, MMAId, InitiateDate, ProcessAttributes");
			sql.append(", VanReq, InvestmentRef, FinPreApprovedRef, EmployeeName, OverrideLimit, TdsStartDate");
			sql.append(", MandateID, LimitValid, FinCancelAc, ApplicationNo, EligibilityMethod, PftServicingODLimit");
			sql.append(", BusinessVertical, ReAgeBucket, JointCustId, InitiateUser, AccountType, Approved");
			sql.append(", JointAccount, FinStatus, AvailedUnPlanEmi, PlanEMIHAlwInGrace");

			// HL
			sql.append(", ReqLoanAmt, ReqLoanTenor, FinOcrRequired, OfferProduct, OfferAmount, CustSegmentation");
			sql.append(", BaseProduct, ProcessType, BureauTimeSeries, CampaignName, ExistingLanRefNo, OfferId");
			sql.append(", LeadSource, PoSource, Rsa, Verification, SourcingBranch, SourChannelCategory, AsmName");
			sql.append(", AlwGrcAdj, EndGrcPeriodAftrFullDisb, AutoIncGrcEndDate,InstBasedSchd, ParentRef");
			sql.append(", AlwLoanSplit, LoanSplitted, Pmay");
		}

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescFinTypeName, LovDescFinMaxAmt, LovDescFinMinAmount, LovDescFinBranchName");

			if (!wif) {
				sql.append(", LovDescFinScheduleOn, LovDescAccruedTillLBD, CustStsDescription");
				sql.append(", LovDescSourceCity, LovDescFinDivision, FinBranchProvinceCode, LovDescStepPolicyName");
				sql.append(", LovDescAccountsOfficer, DsaCodeDesc, ReferralIdDesc, EmployeeNameDesc, DmaCodeDesc");
				sql.append(", SalesDepartmentDesc, LovDescEntityCode, LovEligibilityMethod");
				sql.append(", LovDescEligibilityMethod, LovDescFinPurposeName, ConnectorCode");
				sql.append(
						", ConnectorDesc, BusinessVerticalCode, BusinessVerticalDesc, LovDescSourcingBranch, EmployeeName");
			}
		}

		if (!wif) {
			sql.append(" from FinanceMain");
		} else {
			sql.append(" from WIFFinanceMain");
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

			fm.setFinReference(rs.getString("FinReference"));
			fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
			fm.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
			fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
			fm.setGraceBaseRate(rs.getString("GraceBaseRate"));
			fm.setGraceSpecialRate(rs.getString("GraceSpecialRate"));
			fm.setGrcPftRate(rs.getBigDecimal("GrcPftRate"));
			fm.setGrcPftFrq(rs.getString("GrcPftFrq"));
			fm.setNextGrcPftDate(rs.getTimestamp("NextGrcPftDate"));
			fm.setAllowGrcPftRvw(rs.getBoolean("AllowGrcPftRvw"));
			fm.setGrcPftRvwFrq(rs.getString("GrcPftRvwFrq"));
			fm.setNextGrcPftRvwDate(rs.getTimestamp("NextGrcPftRvwDate"));
			fm.setAllowGrcCpz(rs.getBoolean("AllowGrcCpz"));
			fm.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
			fm.setNextGrcCpzDate(rs.getTimestamp("NextGrcCpzDate"));
			fm.setRepayBaseRate(rs.getString("RepayBaseRate"));
			fm.setRepaySpecialRate(rs.getString("RepaySpecialRate"));
			fm.setRepayProfitRate(rs.getBigDecimal("RepayProfitRate"));
			fm.setRepayFrq(rs.getString("RepayFrq"));
			fm.setNextRepayDate(rs.getTimestamp("NextRepayDate"));
			fm.setRepayPftFrq(rs.getString("RepayPftFrq"));
			fm.setNextRepayPftDate(rs.getTimestamp("NextRepayPftDate"));
			fm.setAllowRepayRvw(rs.getBoolean("AllowRepayRvw"));
			fm.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
			fm.setNextRepayRvwDate(rs.getTimestamp("NextRepayRvwDate"));
			fm.setAllowRepayCpz(rs.getBoolean("AllowRepayCpz"));
			fm.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
			fm.setNextRepayCpzDate(rs.getTimestamp("NextRepayCpzDate"));
			fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
			fm.setCpzAtGraceEnd(rs.getBoolean("CpzAtGraceEnd"));
			fm.setDownPayment(rs.getBigDecimal("DownPayment"));
			//fm.setGraceFlatAmount(rs.getBigDecimal("GraceFlatAmount")); //(Not available in Bean)
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
			fm.setReqMaturity(rs.getTimestamp("ReqMaturity"));
			fm.setCalTerms(rs.getInt("CalTerms"));
			fm.setCalMaturity(rs.getTimestamp("CalMaturity"));
			fm.setFirstRepay(rs.getBigDecimal("FirstRepay"));
			fm.setLastRepay(rs.getBigDecimal("LastRepay"));
			fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
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
			fm.setDisbAccountId(rs.getString("DisbAccountId"));
			fm.setRepayAccountId(rs.getString("RepayAccountId"));
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
			fm.setLastRepayDate(rs.getTimestamp("LastRepayDate"));
			fm.setLastRepayPftDate(rs.getTimestamp("LastRepayPftDate"));
			fm.setLastRepayRvwDate(rs.getTimestamp("LastRepayRvwDate"));
			fm.setLastRepayCpzDate(rs.getTimestamp("LastRepayCpzDate"));
			fm.setAllowGrcRepay(rs.getBoolean("AllowGrcRepay"));
			fm.setGrcSchdMthd(rs.getString("GrcSchdMthd"));
			fm.setGrcMargin(rs.getBigDecimal("GrcMargin"));
			fm.setRepayMargin(rs.getBigDecimal("RepayMargin"));
			fm.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			fm.setFinCommitmentRef(rs.getString("FinCommitmentRef"));
			fm.setDepreciationFrq(rs.getString("DepreciationFrq"));
			fm.setFinContractDate(rs.getTimestamp("FinContractDate"));
			fm.setNextDepDate(rs.getTimestamp("NextDepDate"));
			fm.setLastDepDate(rs.getTimestamp("LastDepDate"));
			fm.setFinAccount(rs.getString("FinAccount"));
			fm.setFinCustPftAccount(rs.getString("FinCustPftAccount"));
			fm.setTotalRepayAmt(rs.getBigDecimal("TotalRepayAmt"));
			fm.setFinApprovedDate(rs.getTimestamp("FinApprovedDate"));
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
			fm.setTakeOverFinance(rs.getBoolean("TakeOverFinance"));
			fm.setGrcAdvBaseRate(rs.getString("GrcAdvBaseRate"));
			fm.setGrcAdvMargin(rs.getBigDecimal("GrcAdvMargin"));
			fm.setGrcAdvPftRate(rs.getBigDecimal("GrcAdvPftRate"));
			fm.setRpyAdvBaseRate(rs.getString("RpyAdvBaseRate"));
			fm.setRpyAdvMargin(rs.getBigDecimal("RpyAdvMargin"));
			fm.setRpyAdvPftRate(rs.getBigDecimal("RpyAdvPftRate"));
			fm.setSupplementRent(rs.getBigDecimal("SupplementRent"));
			fm.setIncreasedCost(rs.getBigDecimal("IncreasedCost"));
			fm.setFeeAccountId(rs.getString("FeeAccountId"));
			fm.setTDSApplicable(rs.getBoolean("TDSApplicable"));
			fm.setInsuranceAmt(rs.getBigDecimal("InsuranceAmt"));
			fm.setDeductInsDisb(rs.getBigDecimal("DeductInsDisb"));
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
			fm.setPromotionSeqId(rs.getLong("PromotionSeqId"));
			fm.setSvAmount(rs.getBigDecimal("SvAmount"));
			fm.setCbAmount(rs.getBigDecimal("CbAmount"));
			fm.setAppliedLoanAmt(rs.getBigDecimal("AppliedLoanAmt"));
			fm.setFinIsRateRvwAtGrcEnd(rs.getBoolean("FinIsRateRvwAtGrcEnd"));
			fm.setClosingStatus(rs.getString("ClosingStatus"));

			if (!wIf) {
				fm.setDmaCode(rs.getString("DmaCode"));
				fm.setTdsPercentage(rs.getBigDecimal("TdsPercentage"));
				fm.setFinStsReason(rs.getString("FinStsReason"));
				fm.setConnector(rs.getLong("Connector"));
				fm.setSamplingRequired(rs.getBoolean("samplingRequired"));
				//fm.setLimitApproved(rs.getString("LimitApproved"));  //(Not available in Bean)
				fm.setNextUserId(rs.getString("NextUserId"));
				fm.setVanCode(rs.getString("VanCode"));
				fm.setFinLimitRef(rs.getString("FinLimitRef"));
				fm.setShariaStatus(rs.getString("ShariaStatus"));
				fm.setRolloverFrq(rs.getString("RolloverFrq"));
				fm.setDdaReferenceNo(rs.getString("DdaReferenceNo"));
				fm.setAvailedUnPlanEmi(rs.getInt("AvailedUnPlanEmi"));
				fm.setLegalRequired(rs.getBoolean("legalRequired"));
				//fm.setCreditInsAmt(rs.getBigDecimal("CreditInsAmt"));  //(Not available in Bean)
				fm.setBlacklisted(rs.getBoolean("Blacklisted"));
				fm.setFinRepayMethod(rs.getString("FinRepayMethod"));
				fm.setFirstDroplineDate(rs.getTimestamp("FirstDroplineDate"));
				fm.setCustDSR(rs.getBigDecimal("CustDSR"));
				fm.setBankName(rs.getString("BankName"));
				fm.setDownPayAccount(rs.getString("DownPayAccount"));
				fm.setAccountsOfficer(rs.getLong("AccountsOfficer"));
				fm.setQuickDisb(rs.getBoolean("QuickDisb"));
				fm.setUnPlanEMICpz(rs.getBoolean("UnPlanEMICpz"));
				fm.setReAgeCpz(rs.getBoolean("ReAgeCpz"));
				fm.setAvailedReAgeH(rs.getInt("AvailedReAgeH"));
				fm.setIban(rs.getString("iban"));
				fm.setSalesDepartment(rs.getString("SalesDepartment"));
				fm.setDroplineFrq(rs.getString("DroplineFrq"));
				fm.setNextRolloverDate(rs.getTimestamp("NextRolloverDate"));
				fm.setSecurityDeposit(rs.getBigDecimal("SecurityDeposit"));
				fm.setPromotionCode(rs.getString("PromotionCode"));
				fm.setTdsLimitAmt(rs.getBigDecimal("TdsLimitAmt"));
				fm.setMigratedFinance(rs.getBoolean("MigratedFinance"));
				fm.setMaxReAgeHolidays(rs.getInt("MaxReAgeHolidays"));
				fm.setWifReference(rs.getString("WifReference"));
				fm.setUnPlanEMIHLockPeriod(rs.getInt("UnPlanEMIHLockPeriod"));
				fm.setTdsEndDate(rs.getTimestamp("TdsEndDate"));
				fm.setPriority(rs.getInt("Priority"));
				//fm.setDiscrepancy(rs.getString("Discrepancy"));  //(Not available in Bean)
				fm.setDeviationApproval(rs.getBoolean("DeviationApproval"));
				fm.setScheduleMaintained(rs.getBoolean("ScheduleMaintained"));
				fm.setFinPurpose(rs.getString("FinPurpose"));
				fm.setScheduleRegenerated(rs.getBoolean("ScheduleRegenerated"));
				//fm.setSecurityCollateral(rs.getString("SecurityCollateral")); //(Not available in Bean)
				fm.setRcdMaintainSts(rs.getString("RcdMaintainSts"));
				fm.setMaxUnplannedEmi(rs.getInt("MaxUnplannedEmi"));
				fm.setDsaCode(rs.getString("DsaCode"));
				fm.setReferralId(rs.getString("ReferralId"));
				fm.setMMAId(rs.getLong("MMAId"));
				fm.setInitiateDate(rs.getTimestamp("InitiateDate"));
				fm.setProcessAttributes(rs.getString("ProcessAttributes"));
				fm.setVanReq(rs.getBoolean("VanReq"));
				fm.setInvestmentRef(rs.getString("InvestmentRef"));
				fm.setFinPreApprovedRef(rs.getString("FinPreApprovedRef"));
				fm.setEmployeeName(rs.getString("EmployeeName"));
				fm.setOverrideLimit(rs.getBoolean("OverrideLimit"));
				fm.setTdsStartDate(rs.getTimestamp("TdsStartDate"));
				fm.setMandateID(rs.getLong("MandateID"));
				fm.setLimitValid(rs.getBoolean("LimitValid"));
				fm.setFinCancelAc(rs.getString("FinCancelAc"));
				fm.setApplicationNo(rs.getString("ApplicationNo"));
				fm.setEligibilityMethod(rs.getLong("EligibilityMethod"));
				fm.setPftServicingODLimit(rs.getBoolean("PftServicingODLimit"));
				fm.setBusinessVertical(rs.getLong("BusinessVertical"));
				fm.setReAgeBucket(rs.getInt("ReAgeBucket"));
				fm.setJointCustId(rs.getLong("JointCustId"));
				fm.setInitiateUser(rs.getLong("InitiateUser"));
				fm.setAccountType(rs.getString("AccountType"));
				fm.setApproved(rs.getString("Approved"));
				fm.setJointAccount(rs.getBoolean("JointAccount"));
				fm.setFinStatus(rs.getString("FinStatus"));
				fm.setPlanEMIHAlwInGrace(rs.getBoolean("PlanEMIHAlwInGrace"));

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
				fm.setAsmName(rs.getLong("AsmName"));
				fm.setAlwGrcAdj(rs.getBoolean("AlwGrcAdj"));
				fm.setEndGrcPeriodAftrFullDisb(rs.getBoolean("EndGrcPeriodAftrFullDisb"));
				fm.setAutoIncGrcEndDate(rs.getBoolean("AutoIncGrcEndDate"));
				fm.setInstBasedSchd(rs.getBoolean("InstBasedSchd"));
				fm.setParentRef(rs.getString("ParentRef"));
				fm.setAlwLoanSplit(rs.getBoolean("AlwLoanSplit"));
				fm.setLoanSplitted(rs.getBoolean("LoanSplitted"));
				fm.setPmay(rs.getBoolean("Pmay"));
			}

			if (StringUtils.trimToEmpty(type).contains("View")) {
				fm.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
				//fm.setLovDescFinMaxAmt(rs.getBigDecimal("LovDescFinMaxAmt"));  //(Not available in Bean)
				//	fm.setLovDescFinMinAmount(rs.getBigDecimal("LovDescFinMinAmount")); //(Not available in Bean)
				fm.setLovDescFinBranchName(rs.getString("LovDescFinBranchName"));

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
				}
			}

			return fm;
		}
	}

	@Override
	public Long getCustomerIdByFin(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select distinct CustId");
		sql.append(" from (Select CustId, FinReference from FinanceMain_Temp");
		sql.append(" union all");
		sql.append(" Select CustId, FinReference from FinanceMain");
		sql.append(") T where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<Long>() {

						@Override
						public Long mapRow(ResultSet rs, int arg1) throws SQLException {
							return rs.getLong("CustId");
						}
					});
		} catch (EmptyResultDataAccessException e) {
			// logger.error(Literal.EXCEPTION, e);
		}
		return null;
	}

	@Override
	public FinanceMain getFinDetailsForHunter(String leadId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT T1.FinReference,T1.FinType");
		sql.append(" From FinanceMain");
		sql.append(type);
		sql.append(" T1 INNER JOIN RMTFinanceTypes T2 ON T1.FinType=T2.FinType ");
		sql.append(" Where T1.offerId =:offerId");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("offerId", leadId);

		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<FinanceMain> getFinanceByInvReference(String finReference, String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder(" SELECT * from FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		List<FinanceMain> financeMainList = new ArrayList<FinanceMain>();
		try {
			financeMainList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug("Leaving");
		return financeMainList;

	}

	@Override
	public List<String> getInvestmentFinRef(String finReference, String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("InvestmentRef", finReference);

		StringBuilder sql = new StringBuilder(" SELECT FinReference from FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where InvestmentRef =:InvestmentRef");

		logger.debug("selectSql: " + sql.toString());

		try {
			return this.jdbcTemplate.queryForList(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			return null;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

	}

	@Override
	public List<String> getParentRefifAny(String finReference, String type, boolean isFromAgr) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ParentRef", finReference);

		StringBuilder sql = new StringBuilder(" SELECT FinReference from FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		if (isFromAgr) {
			sql.append(" Where ParentRef =:ParentRef or investmentref = :ParentRef");
		} else {
			sql.append(" Where ParentRef =:ParentRef");
		}

		logger.debug("selectSql: " + sql.toString());

		try {
			return this.jdbcTemplate.queryForList(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			return null;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

	}

	/**
	 * This method will update the pmay flag in financemain table
	 * 
	 * @param finReference
	 * @param pmay
	 * @param type
	 */
	@Override
	public void updatePmay(String finReference, boolean pmay, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("pMay", pmay);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(type);
		updateSql.append(" Set pMay =:pMay ");
		updateSql.append(" Where FinReference =:FinReference");
		logger.trace("updateSql: " + updateSql.toString());
		recordCount = this.jdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	/**
	 * This method will return dms lead details by using offer id
	 * 
	 * @param offerID
	 * @return DMSLeadDetails
	 */
	@Override
	public FinCustomerDetails getDetailsByOfferID(String offerID) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		FinCustomerDetails customerDetails = null;
		source.addValue("OfferID", offerID);

		StringBuilder selectSql = new StringBuilder("Select custshrtname,custcif,t2.finreference,");
		selectSql.append(" t2.offerid from customers t1 JOIN (select offerid,finreference,custid from financemain");
		selectSql.append(" UNION select offerid, finreference, custid from financemain_temp)");
		selectSql.append(" t2  on t1.custid = t2.custid");
		selectSql.append(" Where offerID =:OfferID");

		logger.trace(Literal.SQL + selectSql.toString());
		try {
			SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(selectSql.toString(), source);
			if (rowSet != null) {
				customerDetails = new FinCustomerDetails();
				while (rowSet.next()) {
					FinCustomerDetails.Category category = customerDetails.new Category();
					customerDetails.setFinReference(rowSet.getString("finreference"));
					category.setName(rowSet.getString("custshrtname"));
					category.setCategory("Primary");
					category.setCif(rowSet.getString("custcif"));
					customerDetails.getCif().add(category);
				}
				return getJointAccountDetails(customerDetails);
			}
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private FinCustomerDetails getJointAccountDetails(FinCustomerDetails finCustomerDetails) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource maParameterSource = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("Select custshrtname, t1.custcif, 'Co-Applicant' as category");
		selectSql.append(" from customers t1 JOIN (select custcif,finreference from finjointaccountdetails");
		selectSql.append(" UNION select custcif,finreference from finjointaccountdetails_temp)");
		selectSql.append(" t2  on t1.custcif = t2.custcif");
		selectSql.append(" Where t2.FinReference =:FinReference");

		logger.trace(Literal.SQL + selectSql.toString());

		maParameterSource.addValue("FinReference", finCustomerDetails.getFinReference());
		try {
			SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(selectSql.toString(), maParameterSource);
			if (rowSet != null) {
				while (rowSet.next()) {
					FinCustomerDetails.Category category = finCustomerDetails.new Category();
					category.setCif(rowSet.getString("custcif"));
					category.setCategory(rowSet.getString("category"));
					category.setName(rowSet.getString("custshrtname"));
					finCustomerDetails.getCif().add(category);
				}
			}
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return finCustomerDetails;
	}

	@Override
	public DMSQueue getOfferIdByFin(DMSQueue dmsQueue) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" OfferId, ApplicationNo");
		sql.append(" from (Select T1.OfferId, T1.FinReference, T1.ApplicationNo");
		sql.append(" from FinanceMain_Temp T1");
		sql.append(" union all");
		sql.append(" Select");
		sql.append(" T1.OfferId, T1.FinReference, T1.ApplicationNo");
		sql.append(" from FinanceMain T1");
		sql.append(" WHERE NOT (EXISTS ( SELECT 1  FROM FINANCEMAIN_TEMP");
		sql.append(" WHERE FINANCEMAIN_TEMP.FINREFERENCE = T1.FINREFERENCE))");
		sql.append(") T where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { dmsQueue.getFinReference() },
					new RowMapper<DMSQueue>() {

						@Override
						public DMSQueue mapRow(ResultSet rs, int arg1) throws SQLException {
							dmsQueue.setOfferId(rs.getString("OfferId"));
							dmsQueue.setApplicationNo(rs.getString("ApplicationNo"));
							return dmsQueue;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return null;
	}

	@Override
	public FinanceMain getEHFinanceMain(final String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod, GrcPftFrq, AllowGrcPftRvw");
		sql.append(", GrcPftRvwFrq, AllowGrcCpz, GrcCpzFrq, RepayFrq, RepayPftFrq, AllowRepayRvw, RepayRvwFrq");
		sql.append(", AllowRepayCpz, RepayCpzFrq, MaturityDate, CpzAtGraceEnd, TotalProfit, TotalCpz");
		sql.append(", TotalGrossPft, TotalGrossGrcPft, TotalGracePft, TotalGraceCpz, GrcRateBasis");
		sql.append(", RepayRateBasis, FinType, ScheduleMethod, ProfitDaysBasis, FinStartDate, FinAmount");
		sql.append(", CustID, FinBranch, FinSourceID, RecalType, Version, AllowGrcRepay, GrcSchdMthd");
		sql.append(", FinRepayPftOnFrq, FinStatus, GraceTerms, FinRepayMethod, GrcProfitDaysBasis");
		sql.append(", TDSApplicable, DroplineFrq, FirstDroplineDate, AlwBPI, BpiTreatment, CalRoundingMode");
		sql.append(", RoundingTarget, MaxUnplannedEmi, AvailedUnPlanEmi, BpiAmount, DroppingMethod");
		sql.append(", FinCategory, ProductCategory, BpiPftDaysBasis,FinCcy,AdvTerms, AdvStage,AdvType, AdvanceEMI");
		sql.append(", FinIsActive, LastRepayRvwDate, PastduePftCalMthd, FinAssetValue, DueBucket");
		sql.append(" from FinanceMain");
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcTemplate.getJdbcOperations().queryForObject(sql.toString(), new Object[] { finReference },
					new RowMapper<FinanceMain>() {
						@Override
						public FinanceMain mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinanceMain fm = new FinanceMain();

							fm.setFinReference(rs.getString("FinReference"));
							fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
							fm.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
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
							fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
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
							fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
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
							fm.setFirstDroplineDate(rs.getTimestamp("FirstDroplineDate"));
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
							fm.setLastRepayRvwDate(rs.getTimestamp("LastRepayRvwDate"));
							fm.setPastduePftCalMthd(rs.getString("PastduePftCalMthd"));
							fm.setDueBucket(rs.getInt("DueBucket"));
							return fm;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void updateEHFinanceMain(FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("update ");
		sql.append(" FinanceMain");
		sql.append(" set NumberOfTerms = :NumberOfTerms,GraceTerms=:GraceTerms, ");
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
		sql.append(" MaxUnplannedEmi=:MaxUnplannedEmi, AvailedUnPlanEmi=:AvailedUnPlanEmi, ");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(" where FinReference = :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(financeMain);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public FinanceMain getFinBasicDetails(String finReference, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("");
		sql.append("SELECT T1.FinReference, T1.CustID, T1.FinCcy, T1.FinBranch, T1.FinType, T1.ScheduleMethod, ");
		sql.append(" T1.ProfitDaysBasis, T1.GrcPeriodEndDate, T1.AllowGrcPeriod, T1.ProductCategory, T1.FinCategory, ");
		sql.append(" T3.CustCIF as lovDescCustCIF, T3.CustShrtName  as lovDescCustShrtName, ClosingStatus ");
		sql.append(" From FinanceMain");
		sql.append(type);
		sql.append(" T1 INNER JOIN RMTFinanceTypes T2 ON T1.FinType=T2.FinType ");
		sql.append(" INNER JOIN Customers T3 ON T1.CustID = T3.CustID ");
		sql.append(" Where T1.FinReference =:FinReference");
		logger.debug(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void updateDeductFeeDisb(FinanceMain financeMain, TableType tableType) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("update FinanceMain");
		sql.append(tableType.getSuffix());
		sql.append(" set DeductFeeDisb=:DeductFeeDisb, LastMntOn = :LastMntOn ");
		sql.append(" where FinReference = :FinReference ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DeductFeeDisb", financeMain.getDeductFeeDisb());
		source.addValue("LastMntOn", financeMain.getLastMntOn());
		source.addValue("FinReference", financeMain.getFinReference());
		source.addValue("PrevMntOn", financeMain.getPrevMntOn());
		source.addValue("Version", financeMain.getVersion());

		int recordCount = jdbcTemplate.update(sql.toString(), source);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public FinanceMain getFinanceMain(String finReference, String[] columns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserPendingCases> getUserPendingCasesDetails(String userLogin, String roleCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getClosedDate(String finReference) {
		logger.debug("Entering");

		Date closedDate = null;

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder("Select ClosedDate From FinanceMain");

		sql.append(" Where FinReference = :FinReference");

		logger.debug("selectSql: " + sql.toString());

		try {
			closedDate = this.jdbcTemplate.queryForObject(sql.toString(), source, Date.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			closedDate = null;
		}

		logger.debug("Leaving");
		return closedDate;
	}

	@Override
	public void updateTdsApplicable(FinanceMain financeMain) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set tDSApplicable = :tDSApplicable");
		updateSql.append(" where finReference = :finReference");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean ispmayApplicable(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Pmay");
		sql.append(" From FinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		boolean isPmay = false;

		try {
			isPmay = this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, Boolean.class);

		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return isPmay;
	}

	@Override
	public void updateRepaymentAmount(FinanceMain financeMain) {
		logger.debug("Entering");

		int recordCount = 0;

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinRepaymentAmount =:FinRepaymentAmount ");
		updateSql.append(" , FinIsActive = :FinIsActive, ClosingStatus =:ClosingStatus ");
		updateSql.append(" , FinStatus = :FinStatus , FinStsReason = :FinStsReason ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

}
