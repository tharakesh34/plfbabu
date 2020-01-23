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
 * FileName    		:  FinanceProfitDetailDAOImpl.java                                      * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-02-2012    														*
 *                                                                  						*
 * Modified Date    :  09-02-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-02-2012       Pennant	                 0.1                                            * 
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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.MonthlyAccumulateDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceProfitDetail model</b>
 * class.<br>
 * 
 */
public class FinanceProfitDetailDAOImpl extends BasicDao<FinanceProfitDetail> implements FinanceProfitDetailDAO {
	private static Logger logger = Logger.getLogger(FinanceProfitDetailDAOImpl.class);

	public FinanceProfitDetailDAOImpl() {
		super();
	}

	/**
	 * Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public FinanceProfitDetail getFinProfitDetailsById(String finReference) {
		logger.debug("Entering");

		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				"Select FinReference, CustId, FinBranch, FinType, FinCcy, LastMdfDate, FinIsActive, ");
		selectSql.append(" TotalPriSchd, TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPftPaidInAdv,");
		selectSql.append(" TotalPriPaid, TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid,");
		selectSql.append(" TdSchdPftBal, PftAccrued, PftAccrueSusp, PftAmz, PftAmzSusp,");
		selectSql.append(" TdSchdPri, TdSchdPriPaid, TdSchdPriBal, AcrTillLBD,");
		selectSql.append(
				" AmzTillLBD, LpiTillLBD, LppTillLBD,GstLpiTillLBD, GstLppTillLBD, FinWorstStatus, FinStatus, FinStsReason, ");
		selectSql.append(" ClosingStatus, FinCategory, PrvRpySchDate, NSchdDate, PrvRpySchPri, PrvRpySchPft, ");
		selectSql.append(" LatestRpyDate, LatestRpyPri, LatestRpyPft, TotalWriteoff, FirstODDate, PrvODDate, ");
		selectSql.append(" ODPrincipal, ODProfit, CurODDays, ActualODDays, FinStartDate,MaturityDate, ");
		selectSql.append(" ProductCategory,ExcessAmt, EmiInAdvance, PrvMthAmz, ");
		selectSql
				.append(" PayableAdvise, ExcessAmtResv, EmiInAdvanceResv, PayableAdviseResv, PenaltyPaid, PenaltyDue,");
		selectSql.append(" GapIntAmz, GapIntAmzLbd, PrvMthGapIntAmz, PrvMthGapIntAmz, SvAmount, CbAmount ");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finProfitDetails = null;
		}
		logger.debug("Leaving");
		return finProfitDetails;
	}

	/**
	 * Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public List<FinanceProfitDetail> getFinProfitDetailsByCustId(long custID, boolean isActive) {
		StringBuilder sql = getProfitDetailQuery();

		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(EodConstants.SQL_NOLOCK);
		}
		sql.append(" where CustId = ?");

		if (isActive) {
			sql.append(" and FinIsActive = ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		ProfitDetailRowMapper rowMapper = new ProfitDetailRowMapper();

		try {
			return this.jdbcTemplate.getJdbcOperations().query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, custID);
					if (isActive) {
						ps.setBoolean(2, isActive);
					}
				}
			}, rowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		return new ArrayList<>();

	}

	private StringBuilder getProfitDetailQuery() {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" FinReference, CustId, FinBranch, FinType, FinCcy, LastMdfDate, FinIsActive, TotalPftSchd");
		sql.append(", TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPftPaidInAdv, TotalPriPaid, TotalPriBal");
		sql.append(", TdSchdPft, TdPftCpz, TdSchdPftPaid, TdSchdPftBal, PftAccrued, PftAccrueSusp");
		sql.append(", PftAmz, PftAmzSusp, TdSchdPri, TdSchdPriPaid, TdSchdPriBal, AcrTillLBD, AmzTillLBD");
		sql.append(", LpiTillLBD, LppTillLBD, GstLpiTillLBD, GstLppTillLBD, FinWorstStatus, FinStatus");
		sql.append(", FinStsReason, ClosingStatus, FinCategory, PrvRpySchDate, NSchdDate, PrvRpySchPri");
		sql.append(", PrvRpySchPft, LatestRpyDate, LatestRpyPri, LatestRpyPft, TotalWriteoff, FirstODDate");
		sql.append(", PrvODDate, ODPrincipal, ODProfit, CurODDays, ActualODDays, FinStartDate, FullPaidDate");
		sql.append(", ExcessAmt, EmiInAdvance, PayableAdvise, ExcessAmtResv, EmiInAdvanceResv, PayableAdviseResv");
		sql.append(", AMZMethod, GapIntAmz, GapIntAmzLbd, SvAmount, CbAmount");
		sql.append(" from FinPftDetails");
		return sql;
	}

	/**
	 * Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public FinanceProfitDetail getFinProfitDetailsByFinRef(String finReference, boolean isActive) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getProfitDetailQuery();
		sql.append(" where FinReference = ?");

		if (isActive) {
			sql.append(" and FinIsActive = ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		ProfitDetailRowMapper rowMapper = new ProfitDetailRowMapper();
		return this.jdbcTemplate.getJdbcOperations().queryForObject(sql.toString(),
				new Object[] { finReference, isActive }, rowMapper);
	}

	/**
	 * Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference) {
		logger.debug("Entering");

		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("Select FinReference, TotalPftPaid, TotalPftBal, ");
		selectSql.append(" TotalPriPaid, TotalPriBal, NoInst, NoPaidInst");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finProfitDetails = null;
		}
		logger.debug("Leaving");
		return finProfitDetails;
	}

	/**
	 * Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public FinanceProfitDetail getFinProfitDetailsByRef(String finReference) {
		logger.debug("Entering");

		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				"Select AcrTillLBD, PftAmzSusp,  AmzTillLBD,LpiTillLBD, LppTillLBD,GstLpiTillLBD, GstLppTillLBD ");
		selectSql.append(" , GapIntAmzLbd");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finProfitDetails = null;
		}
		logger.debug("Leaving");
		return finProfitDetails;

	}

	/**
	 * Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public FinanceProfitDetail getProfitDetailForWriteOff(String finReference) {
		logger.debug("Entering");

		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("Select ODPrincipal, ODProfit, PenaltyDue ");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finProfitDetails = null;
		}
		logger.debug("Leaving");
		return finProfitDetails;

	}

	/**
	 * Method for get the FinanceProfitDetail Object by Key finReference.<br>
	 * Details to be fetched for the purpose of statement summary
	 * 
	 * @param finReference
	 * @return FinanceProfitDetail
	 */
	@Override
	public FinanceProfitDetail getFinProfitDetailsForSummary(String finReference) {
		logger.debug("Entering");

		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("Select FinReference, CustId, TotalPftSchd, TotalPftCpz,");
		selectSql.append(" TotalPftPaid, TotalPftBal, TotalPftPaidInAdv, TotalPriPaid, TotalPriBal, FinStartDate,");
		selectSql.append(" NOInst, MaturityDate, FirstRepayAmt, NSchdDate, NSchdPri, NSchdPft, FirstRepayDate,");
		selectSql.append(" NSchdDate, PrvRpySchDate, ODPrincipal, ODProfit, NOODInst, NOPaidInst, ClosingStatus, ");
		selectSql.append(
				" TotalPftPaidInAdv, TotalPriPaidInAdv, AmzTillLBD,LpiTillLBD, LppTillLBD,GstLpiTillLBD, GstLppTillLBD, TdSchdPftPaid,");
		selectSql.append(" ExcessAmt, EmiInAdvance, PayableAdvise, GapIntAmzLbd, SvAmount, CbAmount");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finProfitDetails = null;
		}
		logger.debug("Leaving");
		return finProfitDetails;
	}

	@Override
	public void update(FinanceProfitDetail finProfitDetails, boolean isRpyProcess) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails Set");
		updateSql.append(" PftAccrued = :PftAccrued, PftAccrueSusp = :PftAccrueSusp, PftAmz = :PftAmz,");
		updateSql.append(" PftAmzSusp = :PftAmzSusp, PftAmzNormal = :PftAmzNormal, PftAmzPD = :PftAmzPD,");
		updateSql.append(" PftInSusp = :PftInSusp, CurFlatRate = :CurFlatRate, CurReducingRate = :CurReducingRate,");
		updateSql.append(" TotalPftSchd = :TotalPftSchd, TotalPftCpz = :TotalPftCpz, TotalPftPaid = :TotalPftPaid,");
		updateSql.append(" TotalPftBal = :TotalPftBal, TdSchdPft = :TdSchdPft, TdPftCpz = :TdPftCpz,");
		updateSql.append(" TdSchdPftPaid = :TdSchdPftPaid, TdSchdPftBal = :TdSchdPftBal,");
		updateSql.append(" TotalpriSchd = :TotalpriSchd,TotalPriPaid = :TotalPriPaid, TotalPriBal = :TotalPriBal,");
		updateSql.append(" TdSchdPri = :TdSchdPri, TdSchdPriPaid = :TdSchdPriPaid, TdSchdPriBal = :TdSchdPriBal,");
		updateSql.append(" CalPftOnPD = :CalPftOnPD, PftOnPDMethod = :PftOnPDMethod, PftOnPDMrg = :PftOnPDMrg,");
		updateSql.append(" TotPftOnPD = :TotPftOnPD,TotPftOnPDPaid = :TotPftOnPDPaid,");
		updateSql.append(" TotPftOnPDWaived = :TotPftOnPDWaived, TotPftOnPDDue = :TotPftOnPDDue,");
		updateSql.append(" NOInst = :NOInst, NOPaidInst = :NOPaidInst, NOODInst = :NOODInst,");
		updateSql.append(" FutureInst = :FutureInst, RemainingTenor = :RemainingTenor, TotalTenor = :TotalTenor,");
		updateSql.append(" ODPrincipal = :ODPrincipal, ODProfit = :ODProfit, CurODDays = :CurODDays,");
		updateSql.append(" MaxODDays = :MaxODDays, FirstODDate = :FirstODDate, PrvODDate = :PrvODDate,");
		updateSql.append(" PenaltyPaid = :PenaltyPaid, PenaltyDue = :PenaltyDue, PenaltyWaived = :PenaltyWaived,");
		updateSql.append(" FirstRepayDate = :FirstRepayDate, FirstRepayAmt = :FirstRepayAmt,");
		updateSql.append(" FinalRepayAmt = :FinalRepayAmt, FirstDisbDate = :FirstDisbDate,");
		updateSql.append(" LatestDisbDate = :LatestDisbDate, FullPaidDate = :FullPaidDate,");
		updateSql.append(" PrvRpySchDate = :PrvRpySchDate, PrvRpySchPri = :PrvRpySchPri,");
		updateSql.append(" PrvRpySchPft = :PrvRpySchPft, RepayFrq=:RepayFrq,");
		updateSql.append(" NSchdDate = :NSchdDate, NSchdPri = :NSchdPri, NSchdPft = :NSchdPft, ");
		updateSql.append(" NSchdPriDue = :NSchdPriDue, NSchdPftDue = :NSchdPftDue,");
		updateSql.append(" AccumulatedDepPri = :AccumulatedDepPri, DepreciatePri = :DepreciatePri,");
		updateSql.append(" TdSchdAdvPft = :TdSchdAdvPft, TdSchdRbt = :TdSchdRbt, TotalAdvPftSchd = :TotalAdvPftSchd,");
		updateSql.append(" TotalRbtSchd = :TotalRbtSchd, TotalPriPaidInAdv = :TotalPriPaidInAdv,");
		updateSql.append(
				" TotalPftPaidInAdv = :TotalPftPaidInAdv, LastMdfDate = :LastMdfDate,MaturityDate=:MaturityDate, ");
		updateSql.append(
				" FinIsActive = :FinIsActive, ClosingStatus = :ClosingStatus,FinStatus=:FinStatus, ActualODDays = :ActualODDays,");
		updateSql.append(
				" AmzTillLBD=:AmzTillLBD, LpiTillLBD=:LpiTillLBD, LppTillLBD=:LppTillLBD,GstLpiTillLBD=:GstLpiTillLBD, GstLppTillLBD=:GstLppTillLBD ");
		updateSql.append(",GapIntAmz = :GapIntAmz, GapIntAmzLbd = :GapIntAmzLbd");
		/*
		 * updateSql.append(" ExcessAmt = :ExcessAmt, "); updateSql.
		 * append(" EmiInAdvance = :EmiInAdvance, PayableAdvise = :PayableAdvise, "
		 * ); updateSql.
		 * append(" ExcessAmtResv = :ExcessAmtResv,  EmiInAdvanceResv = :EmiInAdvanceResv, "
		 * ); updateSql.
		 * append("  PayableAdviseResv = :PayableAdviseResv,  LastMdfDate = :LastMdfDate"
		 * );
		 */
		if (isRpyProcess) {
			updateSql.append(" ,LatestRpyDate = :LatestRpyDate, ");
			updateSql.append(" LatestRpyPri =:LatestRpyPri, LatestRpyPft = :LatestRpyPft ");
		}

		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void update(List<FinanceProfitDetail> finProfitDetails, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails").append(type);
		updateSql.append(
				" Set TotalPftSchd = :TotalPftSchd , TotalPftCpz = :TotalPftCpz , TotalPftPaid = :TotalPftPaid , TotalPftBal = :TotalPftBal , ");
		updateSql.append(
				" TotalPftPaidInAdv = :TotalPftPaidInAdv , TotalPriPaid = :TotalPriPaid , TotalPriBal = :TotalPriBal , TdSchdPft = :TdSchdPft , ");
		updateSql.append(
				" TdPftCpz = :TdPftCpz , TdSchdPftPaid = :TdSchdPftPaid , TdSchdPftBal = :TdSchdPftBal , PftAccrued = :PftAccrued , ");
		updateSql.append(" PftAccrueSusp = :PftAccrueSusp , TdSchdPri = :TdSchdPri , ");
		updateSql.append(" TdSchdPriPaid = :TdSchdPriPaid , TdSchdPriBal = :TdSchdPriBal , PftAmz = :PftAmz , ");
		updateSql.append(" PftAmzSusp = :PftAmzSusp , ");
		updateSql.append(
				" FullPaidDate = :FullPaidDate , CurReducingRate = :CurReducingRate , CurFlatRate = :CurFlatRate , TotalpriSchd = :TotalpriSchd , ");
		updateSql.append(" ODPrincipal = :ODPrincipal , ODProfit = :ODProfit , PenaltyPaid = :PenaltyPaid , ");
		updateSql.append(
				" PenaltyDue = :PenaltyDue , PenaltyWaived = :PenaltyWaived , NSchdDate = :NSchdDate , NSchdPri = :NSchdPri , NSchdPft = :NSchdPft , ");
		updateSql.append(" NSchdPriDue = :NSchdPriDue , NSchdPftDue = :NSchdPftDue , ");
		updateSql.append(
				" PftInSusp = :PftInSusp , AccumulatedDepPri=:AccumulatedDepPri, DepreciatePri=:DepreciatePri, ");
		updateSql.append(
				" FinWorstStatus = :FinWorstStatus , NOInst = :NOInst , NOPaidInst = :NOPaidInst , NOODInst = :NOODInst ,  ");
		updateSql
				.append(" FirstRepayAmt = :FirstRepayAmt , FinalRepayAmt = :FinalRepayAmt ,FinIsActive=:FinIsActive, ");
		updateSql.append(
				" CurODDays = :CurODDays, FirstODDate =:FirstODDate , PrvODDate = :PrvODDate,  FinStatus=:FinStatus, FinStsReason =:FinStsReason, ");
		updateSql.append(" ClosingStatus = :ClosingStatus, PrvRpySchDate = :PrvRpySchDate, ");
		updateSql.append(
				" PrvRpySchPri = :PrvRpySchPri, PrvRpySchPft = :PrvRpySchPft, TotalWriteoff = :TotalWriteoff, ");
		updateSql.append(
				" TotalAdvPftSchd = :TotalAdvPftSchd, TotalRbtSchd = :TotalRbtSchd, TotalPriPaidInAdv=:TotalPriPaidInAdv, TdSchdAdvPft=:TdSchdAdvPft, ");
		updateSql.append(" TdSchdRbt = :TdSchdRbt, PftAmzNormal = :PftAmzNormal, PftAmzPD=:PftAmzPD ");
		updateSql.append(" ,GapIntAmz = :GapIntAmz, GapIntAmzLbd = :GapIntAmzLbd ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finProfitDetails.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void updateLBDAccruals(FinanceProfitDetail finProfitDetail, boolean isMonthEnd) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails Set");
		updateSql.append(
				" AmzTillLBD = :AmzTillLBD, LpiTillLBD=:LpiTillLBD, LppTillLBD=:LppTillLBD, GstLpiTillLBD=:GstLpiTillLBD, GstLppTillLBD=:GstLppTillLBD, AmzTillLBDNormal= :AmzTillLBDNormal, ");
		updateSql.append(" AmzTillLBDPD = :AmzTillLBDPD, AmzTillLBDPIS = :AmzTillLBDPIS,");
		updateSql.append(" AcrTillLBD = :AcrTillLBD, AcrSuspTillLBD = :AcrSuspTillLBD, PrvMthAmz = :PrvMthAmz,");
		updateSql.append(" PrvMthAmzNrm = :PrvMthAmzNrm, PrvMthAmzPD = :PrvMthAmzPD, PrvMthAmzSusp = :PrvMthAmzSusp,");
		updateSql.append(" PrvMthAcr = :PrvMthAcr, PrvMthAcrSusp = :PrvMthAcrSusp");
		updateSql.append(" ,GapIntAmzLbd = :GapIntAmzLbd, PrvMthGapIntAmz = :PrvMthGapIntAmz ");
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetail);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void updateCpzDetail(List<FinanceProfitDetail> pftDetailsList, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails").append(type);
		updateSql.append(" Set TdPftCpz=:TdPftCpz, LastMdfDate =:LastMdfDate ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(pftDetailsList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void save(FinanceProfitDetail finProfitDetails) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinPftDetails");
		insertSql.append(" (FinReference, CustId, FinBranch, FinType, LastMdfDate, TotalPftSchd,");
		insertSql.append(" TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPftPaidInAdv, TotalPriPaid,");
		insertSql.append(" TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid, TdSchdPftBal, PftAccrued,");
		insertSql.append(" PftAccrueSusp, PftAmz, PftAmzSusp, TdSchdPri, TdSchdPriPaid, TdSchdPriBal,");
		insertSql.append(
				" AcrTillLBD, AmzTillLBD,LpiTillLBD, LppTillLBD,GstLpiTillLBD, GstLppTillLBD, RepayFrq, CustCIF, FinCcy, FinPurpose, FinContractDate,");
		insertSql.append(" FinApprovedDate, FinStartDate, MaturityDate, FullPaidDate, FinAmount,");
		insertSql.append(" DownPayment, CurReducingRate, CurFlatRate, TotalpriSchd, ODPrincipal, ODProfit,");
		insertSql.append(" PenaltyPaid, PenaltyDue, PenaltyWaived, NSchdDate, NSchdPri, NSchdPft,");
		insertSql.append(" NSchdPriDue, NSchdPftDue, PftInSusp, FinStatus, FinStsReason, FinWorstStatus,");
		insertSql.append(" NOInst, NOPaidInst, NOODInst, FinAccount, FinAcType, DisbAccountId, DisbActCcy,");
		insertSql.append(" RepayAccountId, FinCustPftAccount, IncomeAccount, UEIncomeSuspAccount,");
		insertSql.append(" FinCommitmentRef, FinIsActive, FirstRepayDate, FirstRepayAmt, FinalRepayAmt,");
		insertSql.append(" CurODDays, ActualODDays, MaxODDays, FirstODDate, PrvODDate, ClosingStatus, FinCategory,");
		insertSql.append(" PrvRpySchDate, PrvRpySchPri, PrvRpySchPft, LatestRpyDate, LatestRpyPri,");
		insertSql.append(" LatestRpyPft, TotalWriteoff, AccumulatedDepPri, DepreciatePri, TotalAdvPftSchd,");
		insertSql.append(" TotalRbtSchd, TotalPriPaidInAdv, TdSchdAdvPft, TdSchdRbt, PftAmzNormal, PftAmzPD,");
		insertSql.append(" AmzTillLBDNormal, AmzTillLBDPD, AmzTillLBDPIS, CalPftOnPD, PftOnPDMethod,");
		insertSql.append(" PftOnPDMrg, TotPftOnPD, TotPftOnPDPaid, TotPftOnPDWaived, TotPftOnPDDue,");
		insertSql.append(" AcrSuspTillLBD, PrvMthAmz, PrvMthAmzNrm, PrvMthAmzPD, PrvMthAmzSusp, PrvMthAcr,");
		insertSql.append(" PrvMthAcrSusp, FirstDisbDate, LatestDisbDate, FutureInst, RemainingTenor,");
		insertSql.append(" TotalTenor,ProductCategory");
		insertSql.append(" ,ExcessAmt, EmiInAdvance, PayableAdvise, ");
		insertSql.append(
				" ExcessAmtResv, EmiInAdvanceResv, PayableAdviseResv, GapIntAmz, GapIntAmzLbd, PrvMthGapIntAmz, SvAmount, CbAmount ");
		insertSql.append(" ) Values");
		insertSql.append(" (:FinReference, :CustId, :FinBranch, :FinType, :LastMdfDate, :TotalPftSchd, ");
		insertSql.append(" :TotalPftCpz, :TotalPftPaid, :TotalPftBal, :TotalPftPaidInAdv, :TotalPriPaid, ");
		insertSql.append(" :TotalPriBal, :TdSchdPft, :TdPftCpz, :TdSchdPftPaid, :TdSchdPftBal, :PftAccrued, ");
		insertSql.append(" :PftAccrueSusp, :PftAmz, :PftAmzSusp, :TdSchdPri, :TdSchdPriPaid, :TdSchdPriBal, ");
		insertSql.append(
				" :AcrTillLBD, :AmzTillLBD,:LpiTillLBD, :LppTillLBD,:GstLpiTillLBD, :GstLppTillLBD, :RepayFrq, :CustCIF, :FinCcy, :FinPurpose, :FinContractDate,");
		insertSql.append(" :FinApprovedDate, :FinStartDate, :MaturityDate, :FullPaidDate, :FinAmount, ");
		insertSql.append(" :DownPayment, :CurReducingRate, :CurFlatRate, :TotalpriSchd, :ODPrincipal, :ODProfit,");
		insertSql.append(" :PenaltyPaid, :PenaltyDue, :PenaltyWaived, :NSchdDate, :NSchdPri, :NSchdPft,");
		insertSql.append(" :NSchdPriDue, :NSchdPftDue, :PftInSusp, :FinStatus, :FinStsReason, :FinWorstStatus,");
		insertSql.append(" :NOInst, :NOPaidInst, :NOODInst, :FinAccount, :FinAcType, :DisbAccountId, :DisbActCcy,");
		insertSql.append(" :RepayAccountId, :FinCustPftAccount, :IncomeAccount, :UEIncomeSuspAccount,");
		insertSql.append(" :FinCommitmentRef, :FinIsActive, :FirstRepayDate, :FirstRepayAmt, :FinalRepayAmt,");
		insertSql.append(
				" :CurODDays, :ActualODDays, :MaxODDays, :FirstODDate, :PrvODDate, :ClosingStatus, :FinCategory,");
		insertSql.append(" :PrvRpySchDate, :PrvRpySchPri, :PrvRpySchPft, :LatestRpyDate, :LatestRpyPri,");
		insertSql.append(" :LatestRpyPft, :TotalWriteoff, :AccumulatedDepPri, :DepreciatePri, :TotalAdvPftSchd,");
		insertSql.append(" :TotalRbtSchd, :TotalPriPaidInAdv, :TdSchdAdvPft, :TdSchdRbt, :PftAmzNormal, :PftAmzPD,");
		insertSql.append(" :AmzTillLBDNormal, :AmzTillLBDPD, :AmzTillLBDPIS, :CalPftOnPD, :PftOnPDMethod,");
		insertSql.append(" :PftOnPDMrg, :TotPftOnPD, :TotPftOnPDPaid, :TotPftOnPDWaived, :TotPftOnPDDue,");
		insertSql.append(" :AcrSuspTillLBD, :PrvMthAmz, :PrvMthAmzNrm, :PrvMthAmzPD, :PrvMthAmzSusp, :PrvMthAcr,");
		insertSql.append(" :PrvMthAcrSusp, :FirstDisbDate, :LatestDisbDate, :FutureInst, :RemainingTenor,");
		insertSql.append(" :TotalTenor,:ProductCategory ");
		insertSql.append(" , :ExcessAmt, :EmiInAdvance, :PayableAdvise, ");
		insertSql.append(
				" :ExcessAmtResv, :EmiInAdvanceResv, :PayableAdviseResv, :GapIntAmz, :GapIntAmzLbd, :PrvMthGapIntAmz, :SvAmount, :CbAmount ");
		insertSql.append(" ) ");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public BigDecimal getAccrueAmount(String finReference) {
		logger.debug("Entering");

		BigDecimal accruedAmount = null;
		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("Select PftAccrued ");
		selectSql.append(" From FinPftDetails Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		try {
			accruedAmount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			accruedAmount = BigDecimal.ZERO;
		}
		logger.debug("Leaving");
		return accruedAmount;
	}

	@Override
	public void refreshTemp() {
		logger.debug("Entering");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource("");
		try {
			this.jdbcTemplate.update("DELETE FROM FinPftDetails_Temp", beanParameters);
			this.jdbcTemplate.update("INSERT INTO FinPftDetails_Temp  SELECT * FROM FinPftDetails", beanParameters);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			beanParameters = null;
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Updating Latest Repayment Details On End Of Day Process
	 */
	@Override
	public void updateLatestRpyDetails(FinanceProfitDetail financeProfitDetail) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails ");
		updateSql.append(
				" Set LatestRpyDate = :LatestRpyDate, LatestRpyPri =:LatestRpyPri, LatestRpyPft = :LatestRpyPft ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeProfitDetail);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Updation of Repayment Account ID on Finance Basic Details
	 * Maintenance
	 */
	@Override
	public void updateRpyAccount(String finReference, String repayAccountId) {
		logger.debug("Entering");

		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);
		finProfitDetails.setRepayAccountId(repayAccountId);

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails ");
		updateSql.append(" Set RepayAccountId = :RepayAccountId Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Updation of Finance Active Status after Finance Cancellation
	 */
	@Override
	public void UpdateActiveSts(String finReference, boolean isActive) {
		logger.debug("Entering");

		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);
		finProfitDetails.setFinIsActive(isActive);

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails ");
		updateSql.append(" Set FinIsActive = :FinIsActive Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void saveAccumulates(Date valueDate) {
		logger.debug("Entering");

		MonthlyAccumulateDetail accumulateDetail = new MonthlyAccumulateDetail();
		accumulateDetail.setMonthEndDate(valueDate);
		accumulateDetail.setMonthStartDate(DateUtility.getMonthStart(valueDate));

		// FIXME: PV 14APR17 based on finPftDetails
		StringBuilder insertSql = new StringBuilder(" INSERT INTO MonthlyAccumulateDetail ");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accumulateDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Updation of Repayment Account ID on Finance Basic Details
	 * Maintenance
	 */
	@Override
	public void resetAcrTsfdInSusp() {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails ");
		updateSql.append(" Set AcrTsfdInSusp = 0 Where AcrTsfdInSusp != 0");

		logger.debug("updateSql: " + updateSql.toString());
		this.jdbcTemplate.getJdbcOperations().update(updateSql.toString());
		logger.debug("Leaving");
	}

	@Override
	public void updateAcrTsfdInSusp(List<AccountHoldStatus> list) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails ");
		updateSql.append(" Set AcrTsfdInSusp = :CurODAmount Where FinReference = :Account");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(list.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void updateEOD(FinanceProfitDetail finProfitDetails, boolean posted, boolean monthend) {

		StringBuilder sql = new StringBuilder("Update FinPftDetails Set");
		sql.append(" PftAccrued = :PftAccrued, PftAccrueSusp = :PftAccrueSusp, PftAmz = :PftAmz,");
		sql.append(" PftAmzSusp = :PftAmzSusp, PftAmzNormal = :PftAmzNormal, PftAmzPD = :PftAmzPD,");
		sql.append(" PftInSusp = :PftInSusp, CurFlatRate = :CurFlatRate, CurReducingRate = :CurReducingRate,");
		sql.append(" TotalPftSchd = :TotalPftSchd, TotalPftCpz = :TotalPftCpz, TotalPftPaid = :TotalPftPaid,");
		sql.append(" TotalPftBal = :TotalPftBal, TdSchdPft = :TdSchdPft, TdPftCpz = :TdPftCpz,");
		sql.append(" TdSchdPftPaid = :TdSchdPftPaid, TdSchdPftBal = :TdSchdPftBal,");
		sql.append(" TotalpriSchd = :TotalpriSchd,TotalPriPaid = :TotalPriPaid, TotalPriBal = :TotalPriBal,");
		sql.append(" TdSchdPri = :TdSchdPri, TdSchdPriPaid = :TdSchdPriPaid, TdSchdPriBal = :TdSchdPriBal,");
		sql.append(" CalPftOnPD = :CalPftOnPD, PftOnPDMethod = :PftOnPDMethod, PftOnPDMrg = :PftOnPDMrg,");
		sql.append(" TotPftOnPD = :TotPftOnPD,TotPftOnPDPaid = :TotPftOnPDPaid,");
		sql.append(" TotPftOnPDWaived = :TotPftOnPDWaived, TotPftOnPDDue = :TotPftOnPDDue,");
		sql.append(" NOInst = :NOInst, NOPaidInst = :NOPaidInst, NOODInst = :NOODInst,");
		sql.append(" FutureInst = :FutureInst, RemainingTenor = :RemainingTenor, TotalTenor = :TotalTenor,");
		sql.append(
				" ODPrincipal = :ODPrincipal, ODProfit = :ODProfit, CurODDays = :CurODDays, ActualODDays = :ActualODDays,");
		sql.append(" MaxODDays = :MaxODDays, FirstODDate = :FirstODDate, PrvODDate = :PrvODDate,");
		sql.append(" PenaltyPaid = :PenaltyPaid, PenaltyDue = :PenaltyDue, PenaltyWaived = :PenaltyWaived,");
		sql.append(" FirstRepayDate = :FirstRepayDate, FirstRepayAmt = :FirstRepayAmt,");
		sql.append(" FinalRepayAmt = :FinalRepayAmt, FirstDisbDate = :FirstDisbDate,");
		sql.append(" LatestDisbDate = :LatestDisbDate, FullPaidDate = :FullPaidDate,");
		sql.append(" PrvRpySchDate = :PrvRpySchDate, PrvRpySchPri = :PrvRpySchPri,");
		sql.append(" PrvRpySchPft = :PrvRpySchPft,");
		sql.append(" NSchdDate = :NSchdDate, NSchdPri = :NSchdPri, NSchdPft = :NSchdPft, ");
		sql.append(" NSchdPriDue = :NSchdPriDue, NSchdPftDue = :NSchdPftDue,");
		sql.append(" AccumulatedDepPri = :AccumulatedDepPri, DepreciatePri = :DepreciatePri,");
		sql.append(" TdSchdAdvPft = :TdSchdAdvPft, TdSchdRbt = :TdSchdRbt, TotalAdvPftSchd = :TotalAdvPftSchd,");
		sql.append(" TotalRbtSchd = :TotalRbtSchd, TotalPriPaidInAdv = :TotalPriPaidInAdv,");
		sql.append(" FinStatus = :FinStatus, FinStsReason = :FinStsReason, FinWorstStatus = :FinWorstStatus, ");
		sql.append(" TotalPftPaidInAdv = :TotalPftPaidInAdv, LastMdfDate = :LastMdfDate, ");
		sql.append(" AMZMethod = :AMZMethod, GapIntAmz = :GapIntAmz ");

		if (posted) {
			sql.append(
					" ,AmzTillLBD = :AmzTillLBD, LpiTillLBD=:LpiTillLBD, LppTillLBD=:LppTillLBD,GstLpiTillLBD=:GstLpiTillLBD, GstLppTillLBD=:GstLppTillLBD, AmzTillLBDNormal= :AmzTillLBDNormal, ");
			sql.append(" AmzTillLBDPD = :AmzTillLBDPD, AmzTillLBDPIS = :AmzTillLBDPIS,");
			sql.append(" AcrTillLBD = :AcrTillLBD, AcrSuspTillLBD = :AcrSuspTillLBD, GapIntAmzLbd = :GapIntAmzLbd ");
		}

		if (monthend) {
			sql.append(" ,PrvMthAmz = :PrvMthAmz, PrvMthAmzNrm = :PrvMthAmzNrm, ");
			sql.append(" PrvMthAmzPD = :PrvMthAmzPD, PrvMthAmzSusp = :PrvMthAmzSusp,");
			sql.append(" PrvMthAcr = :PrvMthAcr, PrvMthAcrSusp = :PrvMthAcrSusp, PrvMthGapIntAmz = :PrvMthGapIntAmz");
		}

		sql.append(" Where FinReference =:FinReference");

		/*
		 * updateSql.append(" ExcessAmt = :ExcessAmt, "); updateSql.
		 * append(" EmiInAdvance = :EmiInAdvance, PayableAdvise = :PayableAdvise, "
		 * ); updateSql.
		 * append(" ExcessAmtResv = :ExcessAmtResv,  EmiInAdvanceResv = :EmiInAdvanceResv, "
		 * ); updateSql.
		 * append("  PayableAdviseResv = :PayableAdviseResv,  LastMdfDate = :LastMdfDate"
		 * );
		 */

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
	}

	/**
	 * 
	 */
	public void updateODDetailsEOD(Date valueDate) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" MERGE INTO FINPFTDETAILS T1 ");
		updateSql.append(" USING (select T1.FinREFERENCE, SUM(FINCURODPRI) ODPRINCIPAL, SUM(FINCURODPFT) ODPROFIT,");
		updateSql.append(" MAX(FINCURODDAYS) ActualODDays, SUM(TOTWAIVED) PENALTYWAIVED, ");
		updateSql.append(" SUM(TOTPENALTYPAID) PENALTYPAID, SUM(TOTPENALTYBAL) PENALTYDUE,");
		updateSql.append(" MIN(T1.FINODSCHDDATE) FIRSTODDATE, MAX(T1.FINODSCHDDATE) PRVODDATE ");
		updateSql.append(" from FinODdetails T1");
		updateSql.append(" Inner Join FinanceMain T2 on T1.FinReference = T2.FINREFERENCE");
		updateSql.append(" Inner Join FinPftDetails T3 on T1.FinReference = T3.FINREFERENCE");
		updateSql.append(" where (T2.FinIsActive = '1' or (T2.FinIsActive = '0' and T3.LatestRpyDate = :valueDate))");
		updateSql.append(" Group BY T1.FinReference ) T2 ");
		updateSql.append(" ON (T1.FinReference = T2.FinReference)");
		updateSql.append(" WHEN MATCHED THEN UPDATE SET T1.ODPRINCIPAL = T2.ODPRINCIPAL, T1.ODPROFIT = T2.ODPROFIT, ");
		updateSql.append(" T1.ActualODDays = T2.ActualODDays, T1.PENALTYWAIVED = T2.PENALTYWAIVED, ");
		updateSql.append(" T1.PENALTYPAID = T2.PENALTYPAID, T1.PENALTYDUE = T2.PENALTYDUE,");
		updateSql.append(" T1.FIRSTODDATE = T2.FIRSTODDATE, T1.PRVODDATE = T2.PRVODDATE");

		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("valueDate", valueDate);

		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * 
	 */
	public void updateTDDetailsEOD(Date valueDate) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" MERGE INTO Finpftdetails FP ");
		updateSql.append(
				" USING ( Select  FS.FINREFERENCE,SUM(FS.PROFITSCHD)PROFITSCHD,SUM(FS.SCHDPFTPAID)SCHDPFTPAID,");
		updateSql.append(" SUM(FS.PROFITSCHD-FS.SCHDPFTPAID)PROFITSCHD_SCHDPFTPAID,");
		updateSql.append(
				" SUM(FS.PRINCIPALSCHD)PRINCIPALSCHD,SUM(FS.SCHDPRIPAID)SCHDPRIPAID,SUM(FS.PRINCIPALSCHD-FS.SCHDPRIPAID) PRINCIPALSCHD_SCHDPRIPAID,");
		updateSql.append(" SUM(CPZAMOUNT) TDPFTCPZ");
		updateSql.append(" from FINSCHEDULEDETAILS FS inner join ");
		updateSql.append(" Finpftdetails FP on FS.FINREFERENCE = FP.FINREFERENCE ");
		updateSql.append(" where (FP.Finisactive=1 or (FP.FinIsActive = '0' and FP.LatestRpyDate = :valueDate))");
		updateSql.append(" and FS.SCHDATE <= :valueDate");
		updateSql.append(" group by  FS.FINREFERENCE ) T2");
		updateSql.append(
				" ON (T2.FINREFERENCE = FP.FINREFERENCE and (T2.PRINCIPALSCHD_SCHDPRIPAID != FP.TDSCHDPRIBAL or T2.PROFITSCHD_SCHDPFTPAID != FP.TDSCHDPFTBAL");
		updateSql.append(
				" or FP.TDSCHDPRIPAID != T2.SCHDPRIPAID or FP.TDSCHDPFTPAID!= T2.SCHDPFTPAID Or FP.TDPFTCPZ != T2.TDPFTCPZ))");
		updateSql.append(
				" WHEN MATCHED THEN UPDATE SET FP.TDSCHDPFT = T2.PROFITSCHD, FP.TDSCHDPFTPAID =  T2.SCHDPFTPAID,");
		updateSql.append(" FP.TDSCHDPFTBAL = T2.PROFITSCHD_SCHDPFTPAID, FP.TDSCHDPRI =  T2.PRINCIPALSCHD,");
		updateSql.append(
				" FP.TDSCHDPRIPAID =  T2.SCHDPRIPAID, FP.TDSCHDPRIBAL =  T2.PRINCIPALSCHD_SCHDPRIPAID, FP.TDPFTCPZ = T2.TDPFTCPZ ");

		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("valueDate", valueDate);

		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * 
	 */
	public void updateReceivableDetailsEOD(Date valueDate) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" MERGE INTO Finpftdetails FP ");
		updateSql.append(" USING ( Select MA.FINREFERENCE, SUM(MA.ADVISEAMOUNT) RECEIVABLEADVISE, ");
		updateSql.append(" SUM(MA.ADVISEAMOUNT - MA.PAIDAMOUNT - MA.WAIVEDAMOUNT) RECEIVABLEADVISEBAL");
		updateSql.append(" from ManualAdvise MA inner join ");
		updateSql.append(" Finpftdetails FP on MA.FINREFERENCE = FP.FINREFERENCE ");
		updateSql.append(" where (FP.Finisactive=1 or (FP.FinIsActive = '0' and FP.LatestRpyDate = :valueDate))");
		updateSql.append(" and MA.AdviseType = '1' and BounceID <= 0 ");
		updateSql.append(" group by  MA.FINREFERENCE ) T2");
		updateSql.append(" ON (T2.FINREFERENCE = FP.FINREFERENCE ) ");
		updateSql.append(
				" WHEN MATCHED THEN UPDATE SET FP.RECEIVABLEADVISE = T2.RECEIVABLEADVISE, FP.RECEIVABLEADVISEBAL = T2.RECEIVABLEADVISEBAL");

		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("valueDate", valueDate);

		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * 
	 */
	public void updateBounceDetailsEOD(Date valueDate) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" MERGE INTO Finpftdetails FP ");
		updateSql.append(" USING ( Select MA.FINREFERENCE, SUM(MA.ADVISEAMOUNT) BOUNCEAMT, ");
		updateSql.append(" SUM(MA.PAIDAMOUNT) BOUNCEAMTPAID, ");
		updateSql.append(" SUM(MA.ADVISEAMOUNT - MA.PAIDAMOUNT - MA.WAIVEDAMOUNT) BOUNCEAMTDUE");
		updateSql.append(" from ManualAdvise MA inner join ");
		updateSql.append(" Finpftdetails FP on MA.FINREFERENCE = FP.FINREFERENCE ");
		updateSql.append(" where (FP.Finisactive=1 or (FP.FinIsActive = '0' and FP.LatestRpyDate = :valueDate))");
		updateSql.append(" and MA.AdviseType = '1' and BounceID > 0 ");
		updateSql.append(" group by  MA.FINREFERENCE ) T2");
		updateSql.append(" ON (T2.FINREFERENCE = FP.FINREFERENCE ) ");
		// updateSql.append(" ( FP.BOUNCEAMT != T2.BOUNCEAMT or FP.BOUNCEAMTPAID
		// != T2.BOUNCEAMTPAID or FP.BOUNCEAMTDUE != T2.BOUNCEAMTDUE))");
		updateSql.append(
				" WHEN MATCHED THEN UPDATE SET FP.BOUNCEAMT = T2.BOUNCEAMT, FP.BOUNCEAMTPAID = T2.BOUNCEAMTPAID, FP.BOUNCEAMTDUE = T2.BOUNCEAMTDUE");

		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("valueDate", valueDate);

		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * @param finReference
	 * @param type
	 * 
	 *            method return curOddays from FinPFtDetails Based On Reference
	 */
	@Override
	public int getCurOddays(String finReference, String type) {
		logger.debug("Entering");
		try {
			FinanceProfitDetail financeProfitDetails = new FinanceProfitDetail();
			financeProfitDetails.setFinReference(finReference);
			StringBuilder selectSql = new StringBuilder("Select CURODDAYS ");
			selectSql.append(" From Finpftdetails");
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" Where FinReference =:FinReference ");
			logger.debug("selectSql: " + selectSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeProfitDetails);
			logger.debug("Leaving");
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (Exception e) {
			logger.debug(e);
		}
		return 0;
	}

	/**
	 * @param finReference
	 * @param type
	 * 
	 *            method return PFTINSUSP from FinPFtDetails Based On Reference
	 */
	@Override
	public boolean isSuspenseFinance(String finReference) {
		logger.debug("Entering");
		try {
			MapSqlParameterSource source = new MapSqlParameterSource();
			source.addValue("FinReference", finReference);
			
			StringBuilder selectSql = new StringBuilder("Select PFTINSUSP ");
			selectSql.append(" From Finpftdetails");
			selectSql.append(" Where FinReference =:FinReference ");
			
			logger.debug("selectSql: " + selectSql.toString());
			logger.debug("Leaving");
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Boolean.class);
		} catch (Exception e) {
			logger.debug(e);
		}
		return false;
	}

	@Override
	public BigDecimal getTotalCustomerExposre(long custId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select  coalesce(sum((NSchdPri+NSchdPft)), 0) from FinPftdetails");
		sql.append(" where finreference in (select finreference from financemain where custId = :custId)");
		logger.debug(Literal.SQL + sql.toString());

		BigDecimal totalExposer = BigDecimal.ZERO;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custId", custId);

		try {
			totalExposer = this.jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return totalExposer;
	}

	@Override
	public BigDecimal getTotalCoApplicantsExposre(String finReferece) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select  coalesce(sum((NSchdPri+NSchdPft)), 0) from FinPftdetails");
		sql.append(" where finreference in (select finreference from financemain where custId in (");
		sql.append(" select custId from finjointaccountdetails_view where finreference = :finreference))");
		logger.debug(Literal.SQL + sql.toString());

		BigDecimal totalExposer = BigDecimal.ZERO;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("finreference", finReferece);

		try {
			totalExposer = this.jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return totalExposer;
	}

	@Override
	public void updateFinPftMaturity(String finReference, String closingStatus, boolean finIsActive) {
		logger.debug("Entering");
		FinanceProfitDetail pftDetail = new FinanceProfitDetail();
		pftDetail.setFinReference(finReference);
		pftDetail.setClosingStatus(closingStatus);
		pftDetail.setFinIsActive(finIsActive);

		StringBuilder updateSql = new StringBuilder("Update finpftdetails ");
		updateSql.append(" Set FinIsActive = :FinIsActive, ClosingStatus = :ClosingStatus ");
		updateSql.append(" Where FinReference = :FinReference ");
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pftDetail);

		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public Date getFirstRePayDateByFinRef(String finReference) {
		logger.debug("Entering");
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		Date accruedAmount = null;

		StringBuilder selectSql = new StringBuilder(" SELECT FIRSTREPAYDATE FROM FINPFTDETAILS");
		selectSql.append(" WHERE FINREFERENCE = :FINREFERENCE");

		paramMap.addValue("FINREFERENCE", finReference);
		logger.debug("selectSql: " + selectSql.toString());

		try {
			accruedAmount = this.jdbcTemplate.queryForObject(selectSql.toString(), paramMap, Date.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return accruedAmount;
	}

	@Override
	public BigDecimal getMaxRpyAmount(String finReference) {
		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT MaxRpyAmount  FROM  Finpftdetails");
		selectSql.append(" WHERE FINREFERENCE = :FINREFERENCE");
		source.addValue("FINREFERENCE", finReference);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);
	}

	@Override
	public BigDecimal getGoldPOSByCustCif(String custCif, String promotionCode, String repledgeRef) {
		logger.debug("Entering");

		BigDecimal posBal = null;

		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("CustCIF", custCif);
		beanParameters.addValue("ProductCategory", FinanceConstants.PRODUCT_GOLD);
		beanParameters.addValue("PromotionCode", promotionCode);
		beanParameters.addValue("FinReference", repledgeRef);

		StringBuilder selectSql = new StringBuilder("Select SUM(P.TotalPriBal) POSBAL ");
		selectSql.append(" From FinPftDetails P INNER JOIN FINANCEMAIN F ON P.FinReference = F.FinReference ");
		selectSql.append("  Where P.CustCIF =:CustCIF AND F.ProductCategory = :ProductCategory AND F.FinIsActive = 1 ");
		if (StringUtils.isNotBlank(promotionCode)) {
			selectSql.append(" AND F.PromotionCode=:PromotionCode ");
		}
		if (StringUtils.isNotBlank(repledgeRef)) {
			selectSql.append(" AND F.FinReference!=:FinReference ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		try {
			posBal = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			posBal = BigDecimal.ZERO;
		}

		if (posBal == null) {
			posBal = BigDecimal.ZERO;
		}

		logger.debug("Leaving");
		return posBal;
	}

	@Override
	public List<FinanceProfitDetail> getFinProfitListByFinRefList(List<String> finRefList) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select FinReference, NSCHDPRI , NSCHDPFT, CURODDAYS");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" WHERE FinReference IN (:FinReference) ");

		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finRefList);

		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);

		List<FinanceProfitDetail> finPftDetails = new ArrayList<>();
		try {
			finPftDetails = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			finPftDetails = new ArrayList<>();
		}

		logger.debug("Leaving");

		return finPftDetails;
	}

	@Override
	public void updateAssignmentBPIAmounts(FinanceProfitDetail finProfitDetails) {

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails Set");
		updateSql.append(" AssignBPI1 = :AssignBPI1, AssignBPI2 = :AssignBPI2 ");
		updateSql.append(" Where FinReference = :FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

	}

	// IND AS - START
	@Override
	public List<FinanceProfitDetail> getFinPftListForIncomeAMZ(Date curMonthStart) {

		StringBuilder selectSql = new StringBuilder(
				"Select FinReference, CustId, FinBranch, FinType, FinCcy, LastMdfDate, FinIsActive, ");
		selectSql.append(" TotalPriSchd, TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, ");
		selectSql.append(" TotalPriPaid, TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid, ");
		selectSql.append(" TdSchdPftBal, PftAccrued, PftAccrueSusp, PftAmz, PftAmzSusp, ");
		selectSql.append(" TdSchdPri, TdSchdPriPaid, TdSchdPriBal, PrvMthAmz, ");
		selectSql.append(" ClosingStatus, FinCategory, TotalWriteoff, ");
		selectSql.append(" ODPrincipal, ODProfit, CurODDays, ActualODDays, FinStartDate, MaturityDate, LatestRpyDate ");
		selectSql.append(" ,GapIntAmz, GapIntAmzLbd, PrvMthGapIntAmz ");

		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where MaturityDate >= :MaturityDate ");

		// logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("MaturityDate", curMonthStart);

		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	/**
	 * Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public FinanceProfitDetail getFinProfitForAMZ(String finReference) {

		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				"Select FinReference, CustId, FinBranch, FinType, FinCcy, LastMdfDate, FinIsActive, ");
		selectSql.append(" TotalPriSchd, TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, ");
		selectSql.append(" TotalPriPaid, TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid, ");
		selectSql.append(" TdSchdPftBal, PftAccrued, PftAccrueSusp, PftAmz, PftAmzSusp, ");
		selectSql.append(" TdSchdPri, TdSchdPriPaid, TdSchdPriBal, PrvMthAmz, ");
		selectSql.append(" ClosingStatus, FinCategory, TotalWriteoff, ");
		selectSql.append(" ODPrincipal, ODProfit, CurODDays, ActualODDays, FinStartDate, MaturityDate, LatestRpyDate ");
		selectSql.append(" ,GapIntAmz, GapIntAmzLbd, PrvMthGapIntAmz ");

		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference = :FinReference");

		// logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finProfitDetails = null;
		}
		return finProfitDetails;
	}

	/**
	 * Update AMZMethod for same month created and EarlySettled Loans
	 */
	@Override
	public void updateAMZMethod(String finReference, String amzMethod) {

		StringBuilder updateSql = new StringBuilder(" Update FinPftDetails");
		updateSql.append(" Set AMZMethod = :AMZMethod Where FinReference = :FinReference");
		logger.debug("updateSql: " + updateSql.toString());

		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("FinReference", finReference);
		beanParameters.addValue("AMZMethod", amzMethod);

		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
	}
	// IND AS - END

	@Override
	public void updateSchPaid(FinanceProfitDetail profitDetail) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinPftDetails set");
		sql.append(" TotalPftPaid = :TotalPftPaid");
		sql.append(", TotalPriPaid = :TotalPriPaid");
		// sql.append(", TdTdsPaid = :TdTdsPaid");
		// sql.append(", TdTdsBal = :TdTdsBal");
		sql.append(" where FinReference =:FinReference");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(profitDetail);
		int recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	public class ProfitDetailRowMapper implements RowMapper<FinanceProfitDetail> {

		@Override
		public FinanceProfitDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceProfitDetail pftd = new FinanceProfitDetail();

			pftd.setFinReference(rs.getString("FinReference"));
			pftd.setCustId(rs.getLong("CustId"));
			pftd.setFinBranch(rs.getString("FinBranch"));
			pftd.setFinType(rs.getString("FinType"));
			pftd.setFinCcy(rs.getString("FinCcy"));
			pftd.setLastMdfDate(rs.getDate("LastMdfDate"));
			pftd.setFinIsActive(rs.getBoolean("FinIsActive"));
			pftd.setTotalPftSchd(rs.getBigDecimal("TotalPftSchd"));
			pftd.setTotalPftCpz(rs.getBigDecimal("TotalPftCpz"));
			pftd.setTotalPftPaid(rs.getBigDecimal("TotalPftPaid"));
			pftd.setTotalPftBal(rs.getBigDecimal("TotalPftBal"));
			pftd.setTotalPftPaidInAdv(rs.getBigDecimal("TotalPftPaidInAdv"));
			pftd.setTotalPriPaid(rs.getBigDecimal("TotalPriPaid"));
			pftd.setTotalPriBal(rs.getBigDecimal("TotalPriBal"));
			pftd.setTdSchdPft(rs.getBigDecimal("TdSchdPft"));
			pftd.setTdPftCpz(rs.getBigDecimal("TdPftCpz"));
			pftd.setTdSchdPftPaid(rs.getBigDecimal("TdSchdPftPaid"));
			pftd.setTdSchdPftBal(rs.getBigDecimal("TdSchdPftBal"));
			pftd.setPftAccrued(rs.getBigDecimal("PftAccrued"));
			pftd.setPftAccrueSusp(rs.getBigDecimal("PftAccrueSusp"));
			pftd.setPftAmz(rs.getBigDecimal("PftAmz"));
			pftd.setPftAmzSusp(rs.getBigDecimal("PftAmzSusp"));
			pftd.setTdSchdPri(rs.getBigDecimal("TdSchdPri"));
			pftd.setTdSchdPriPaid(rs.getBigDecimal("TdSchdPriPaid"));
			pftd.setTdSchdPriBal(rs.getBigDecimal("TdSchdPriBal"));
			pftd.setAcrTillLBD(rs.getBigDecimal("AcrTillLBD"));
			pftd.setAmzTillLBD(rs.getBigDecimal("AmzTillLBD"));
			pftd.setLpiTillLBD(rs.getBigDecimal("LpiTillLBD"));
			pftd.setLppTillLBD(rs.getBigDecimal("LppTillLBD"));
			pftd.setGstLpiTillLBD(rs.getBigDecimal("GstLpiTillLBD"));
			pftd.setGstLppTillLBD(rs.getBigDecimal("GstLppTillLBD"));
			pftd.setFinWorstStatus(rs.getString("FinWorstStatus"));
			pftd.setFinStatus(rs.getString("FinStatus"));
			pftd.setFinStsReason(rs.getString("FinStsReason"));
			pftd.setClosingStatus(rs.getString("ClosingStatus"));
			pftd.setFinCategory(rs.getString("FinCategory"));
			pftd.setPrvRpySchDate(rs.getDate("PrvRpySchDate"));
			pftd.setNSchdDate(rs.getDate("NSchdDate"));
			pftd.setPrvRpySchPri(rs.getBigDecimal("PrvRpySchPri"));
			pftd.setPrvRpySchPft(rs.getBigDecimal("PrvRpySchPft"));
			pftd.setLatestRpyDate(rs.getDate("LatestRpyDate"));
			pftd.setLatestRpyPri(rs.getBigDecimal("LatestRpyPri"));
			pftd.setLatestRpyPft(rs.getBigDecimal("LatestRpyPft"));
			pftd.setTotalWriteoff(rs.getBigDecimal("TotalWriteoff"));
			pftd.setFirstODDate(rs.getDate("FirstODDate"));
			pftd.setPrvODDate(rs.getDate("PrvODDate"));
			pftd.setODPrincipal(rs.getBigDecimal("ODPrincipal"));
			pftd.setODProfit(rs.getBigDecimal("ODProfit"));
			pftd.setCurODDays(rs.getInt("CurODDays"));
			pftd.setActualODDays(rs.getInt("ActualODDays"));
			pftd.setFinStartDate(rs.getDate("FinStartDate"));
			pftd.setFullPaidDate(rs.getDate("FullPaidDate"));
			pftd.setExcessAmt(rs.getBigDecimal("ExcessAmt"));
			pftd.setEmiInAdvance(rs.getBigDecimal("EmiInAdvance"));
			pftd.setPayableAdvise(rs.getBigDecimal("PayableAdvise"));
			pftd.setExcessAmtResv(rs.getBigDecimal("ExcessAmtResv"));
			pftd.setEmiInAdvanceResv(rs.getBigDecimal("EmiInAdvanceResv"));
			pftd.setPayableAdviseResv(rs.getBigDecimal("PayableAdviseResv"));
			pftd.setAMZMethod(rs.getString("AMZMethod"));
			pftd.setGapIntAmz(rs.getBigDecimal("GapIntAmz"));
			pftd.setGapIntAmzLbd(rs.getBigDecimal("GapIntAmzLbd"));
			pftd.setSvAmount(rs.getBigDecimal("SvAmount"));
			pftd.setCbAmount(rs.getBigDecimal("CbAmount"));

			return pftd;
		}

	}

}
