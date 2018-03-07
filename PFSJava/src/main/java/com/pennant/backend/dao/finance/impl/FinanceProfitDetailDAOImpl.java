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
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.MonthlyAccumulateDetail;

/**
 * DAO methods implementation for the <b>FinanceProfitDetail model</b> class.<br>
 * 
 */
public class FinanceProfitDetailDAOImpl implements FinanceProfitDetailDAO {

	private static Logger				logger	= Logger.getLogger(FinanceProfitDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinanceProfitDetailDAOImpl() {
		super();
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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
		selectSql.append(" AmzTillLBD, FinWorstStatus, FinStatus, FinStsReason, ");
		selectSql.append(" ClosingStatus, FinCategory, PrvRpySchDate, NSchdDate, PrvRpySchPri, PrvRpySchPft, ");
		selectSql.append(" LatestRpyDate, LatestRpyPri, LatestRpyPft, TotalWriteoff, FirstODDate, PrvODDate, ");
		selectSql.append(" ODPrincipal, ODProfit, CurODDays, ActualODDays, FinStartDate,MaturityDate, ");
		selectSql.append(" ProductCategory,ExcessAmt, EmiInAdvance, PrvMthAmz, ");
		selectSql.append(" PayableAdvise, ExcessAmtResv, EmiInAdvanceResv, PayableAdviseResv, PenaltyPaid, PenaltyDue");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
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
		logger.debug("Entering");

		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setCustId(custID);
		finProfitDetails.setFinIsActive(isActive);

		StringBuilder selectSql = new StringBuilder("Select FinReference, CustId,  ");
		selectSql.append(" FinBranch, FinType, FinCcy, LastMdfDate, FinIsActive,");
		selectSql.append(" TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPftPaidInAdv,");
		selectSql.append(" TotalPriPaid, TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid,");
		selectSql.append(" TdSchdPftBal, PftAccrued, PftAccrueSusp, PftAmz, PftAmzSusp,");
		selectSql.append(" TdSchdPri, TdSchdPriPaid, TdSchdPriBal, AcrTillLBD,");
		selectSql.append(" AmzTillLBD, FinWorstStatus, FinStatus, FinStsReason, ");
		selectSql.append(" ClosingStatus, FinCategory, PrvRpySchDate, NSchdDate, PrvRpySchPri, PrvRpySchPft, ");
		selectSql.append(" LatestRpyDate, LatestRpyPri, LatestRpyPft, TotalWriteoff, FirstODDate, PrvODDate, ");
		selectSql.append(" ODPrincipal, ODProfit, CurODDays, ActualODDays, FinStartDate,FullPaidDate, ");
		selectSql.append(" ExcessAmt, EmiInAdvance, ");
		selectSql.append(" PayableAdvise, ExcessAmtResv, EmiInAdvanceResv, PayableAdviseResv");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where CustId =:CustId");

		if (isActive) {
			selectSql.append(" AND FinIsActive = :FinIsActive ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);

		List<FinanceProfitDetail> finPftDetails = this.namedParameterJdbcTemplate.query(selectSql.toString(),
				beanParameters, typeRowMapper);

		logger.debug("Leaving");
		return finPftDetails;
	}
	
	/**
	 * Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public FinanceProfitDetail getFinProfitDetailsByFinRef(String finReference, boolean isActive) {
		logger.debug("Entering");

		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);
		finProfitDetails.setFinIsActive(isActive);

		StringBuilder selectSql = new StringBuilder("Select FinReference, CustId,  ");
		selectSql.append(" FinBranch, FinType, FinCcy, LastMdfDate, FinIsActive,");
		selectSql.append(" TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPftPaidInAdv,");
		selectSql.append(" TotalPriPaid, TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid,");
		selectSql.append(" TdSchdPftBal, PftAccrued, PftAccrueSusp, PftAmz, PftAmzSusp,");
		selectSql.append(" TdSchdPri, TdSchdPriPaid, TdSchdPriBal, AcrTillLBD,");
		selectSql.append(" AmzTillLBD, FinWorstStatus, FinStatus, FinStsReason, ");
		selectSql.append(" ClosingStatus, FinCategory, PrvRpySchDate, NSchdDate, PrvRpySchPri, PrvRpySchPft, ");
		selectSql.append(" LatestRpyDate, LatestRpyPri, LatestRpyPft, TotalWriteoff, FirstODDate, PrvODDate, ");
		selectSql.append(" ODPrincipal, ODProfit, CurODDays, ActualODDays, FinStartDate,FullPaidDate, ");
		selectSql.append(" ExcessAmt, EmiInAdvance, ");
		selectSql.append(" PayableAdvise, ExcessAmtResv, EmiInAdvanceResv, PayableAdviseResv");

		selectSql.append(" From FinPftDetails Where FinReference = :FinReference");
		selectSql.append(" AND FinIsActive = :FinIsActive ");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
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
			finProfitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
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

		StringBuilder selectSql = new StringBuilder("Select AcrTillLBD, PftAmzSusp,  AmzTillLBD ");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
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
			finProfitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
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
		selectSql.append(" TotalPftPaidInAdv, TotalPriPaidInAdv, AmzTillLBD, TdSchdPftPaid,");
		selectSql.append(" ExcessAmt, EmiInAdvance, PayableAdvise");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
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
		updateSql.append(" TotalPftPaidInAdv = :TotalPftPaidInAdv, LastMdfDate = :LastMdfDate,MaturityDate=:MaturityDate, ");
		updateSql.append(" FinIsActive = :FinIsActive, ClosingStatus = :ClosingStatus,FinStatus=:FinStatus, ActualODDays = :ActualODDays ");
		/*
		 * updateSql.append(" ExcessAmt = :ExcessAmt, ");
		 * updateSql.append(" EmiInAdvance = :EmiInAdvance, PayableAdvise = :PayableAdvise, ");
		 * updateSql.append(" ExcessAmtResv = :ExcessAmtResv,  EmiInAdvanceResv = :EmiInAdvanceResv, ");
		 * updateSql.append("  PayableAdviseResv = :PayableAdviseResv,  LastMdfDate = :LastMdfDate");
		 */
		if (isRpyProcess) {
			updateSql.append(" ,LatestRpyDate = :LatestRpyDate, ");
			updateSql.append(" LatestRpyPri =:LatestRpyPri, LatestRpyPft = :LatestRpyPft ");
		}

		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

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
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finProfitDetails.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void updateLBDAccruals(FinanceProfitDetail finProfitDetail, boolean isMonthEnd) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails Set");
		updateSql.append(" AmzTillLBD = :AmzTillLBD, AmzTillLBDNormal= :AmzTillLBDNormal, ");
		updateSql.append(" AmzTillLBDPD = :AmzTillLBDPD, AmzTillLBDPIS = :AmzTillLBDPIS,");
		updateSql.append(" AcrTillLBD = :AcrTillLBD, AcrSuspTillLBD = :AcrSuspTillLBD, PrvMthAmz = :PrvMthAmz,");
		updateSql.append(" PrvMthAmzNrm = :PrvMthAmzNrm, PrvMthAmzPD = :PrvMthAmzPD, PrvMthAmzSusp = :PrvMthAmzSusp,");
		updateSql.append(" PrvMthAcr = :PrvMthAcr, PrvMthAcrSusp = :PrvMthAcrSusp");
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetail);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

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
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

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
		insertSql.append(" AcrTillLBD, AmzTillLBD, RepayFrq, CustCIF, FinCcy, FinPurpose, FinContractDate,");
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
		insertSql.append(" ExcessAmtResv, EmiInAdvanceResv, PayableAdviseResv ");
		insertSql.append(" ) Values");
		insertSql.append(" (:FinReference, :CustId, :FinBranch, :FinType, :LastMdfDate, :TotalPftSchd, ");
		insertSql.append(" :TotalPftCpz, :TotalPftPaid, :TotalPftBal, :TotalPftPaidInAdv, :TotalPriPaid, ");
		insertSql.append(" :TotalPriBal, :TdSchdPft, :TdPftCpz, :TdSchdPftPaid, :TdSchdPftBal, :PftAccrued, ");
		insertSql.append(" :PftAccrueSusp, :PftAmz, :PftAmzSusp, :TdSchdPri, :TdSchdPriPaid, :TdSchdPriBal, ");
		insertSql.append(" :AcrTillLBD, :AmzTillLBD, :RepayFrq, :CustCIF, :FinCcy, :FinPurpose, :FinContractDate,");
		insertSql.append(" :FinApprovedDate, :FinStartDate, :MaturityDate, :FullPaidDate, :FinAmount, ");
		insertSql.append(" :DownPayment, :CurReducingRate, :CurFlatRate, :TotalpriSchd, :ODPrincipal, :ODProfit,");
		insertSql.append(" :PenaltyPaid, :PenaltyDue, :PenaltyWaived, :NSchdDate, :NSchdPri, :NSchdPft,");
		insertSql.append(" :NSchdPriDue, :NSchdPftDue, :PftInSusp, :FinStatus, :FinStsReason, :FinWorstStatus,");
		insertSql.append(" :NOInst, :NOPaidInst, :NOODInst, :FinAccount, :FinAcType, :DisbAccountId, :DisbActCcy,");
		insertSql.append(" :RepayAccountId, :FinCustPftAccount, :IncomeAccount, :UEIncomeSuspAccount,");
		insertSql.append(" :FinCommitmentRef, :FinIsActive, :FirstRepayDate, :FirstRepayAmt, :FinalRepayAmt,");
		insertSql.append(" :CurODDays, :ActualODDays, :MaxODDays, :FirstODDate, :PrvODDate, :ClosingStatus, :FinCategory,");
		insertSql.append(" :PrvRpySchDate, :PrvRpySchPri, :PrvRpySchPft, :LatestRpyDate, :LatestRpyPri,");
		insertSql.append(" :LatestRpyPft, :TotalWriteoff, :AccumulatedDepPri, :DepreciatePri, :TotalAdvPftSchd,");
		insertSql.append(" :TotalRbtSchd, :TotalPriPaidInAdv, :TdSchdAdvPft, :TdSchdRbt, :PftAmzNormal, :PftAmzPD,");
		insertSql.append(" :AmzTillLBDNormal, :AmzTillLBDPD, :AmzTillLBDPIS, :CalPftOnPD, :PftOnPDMethod,");
		insertSql.append(" :PftOnPDMrg, :TotPftOnPD, :TotPftOnPDPaid, :TotPftOnPDWaived, :TotPftOnPDDue,");
		insertSql.append(" :AcrSuspTillLBD, :PrvMthAmz, :PrvMthAmzNrm, :PrvMthAmzPD, :PrvMthAmzSusp, :PrvMthAcr,");
		insertSql.append(" :PrvMthAcrSusp, :FirstDisbDate, :LatestDisbDate, :FutureInst, :RemainingTenor,");
		insertSql.append(" :TotalTenor,:ProductCategory ");
		insertSql.append(" , :ExcessAmt, :EmiInAdvance, :PayableAdvise, ");
		insertSql.append(" :ExcessAmtResv, :EmiInAdvanceResv, :PayableAdviseResv ");
		insertSql.append(" ) ");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
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
			accruedAmount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					BigDecimal.class);
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
			this.namedParameterJdbcTemplate.update("DELETE FROM FinPftDetails_Temp", beanParameters);
			this.namedParameterJdbcTemplate.update("INSERT INTO FinPftDetails_Temp  SELECT * FROM FinPftDetails",
					beanParameters);

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
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Updation of Repayment Account ID on Finance Basic Details Maintenance
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
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
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
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void saveAccumulates(Date valueDate) {
		logger.debug("Entering");

		MonthlyAccumulateDetail accumulateDetail = new MonthlyAccumulateDetail();
		accumulateDetail.setMonthEndDate(valueDate);
		accumulateDetail.setMonthStartDate(DateUtility.getMonthStartDate(valueDate));

		//FIXME: PV 14APR17 based on finPftDetails
		StringBuilder insertSql = new StringBuilder(" INSERT INTO MonthlyAccumulateDetail ");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accumulateDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Updation of Repayment Account ID on Finance Basic Details Maintenance
	 */
	@Override
	public void resetAcrTsfdInSusp() {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails ");
		updateSql.append(" Set AcrTsfdInSusp = 0 Where AcrTsfdInSusp != 0");

		logger.debug("updateSql: " + updateSql.toString());
		this.namedParameterJdbcTemplate.getJdbcOperations().update(updateSql.toString());
		logger.debug("Leaving");
	}

	@Override
	public void updateAcrTsfdInSusp(List<AccountHoldStatus> list) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails ");
		updateSql.append(" Set AcrTsfdInSusp = :CurODAmount Where FinReference = :Account");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(list.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void updateEOD(FinanceProfitDetail finProfitDetails, boolean posted, boolean monthend) {
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
		updateSql.append(" ODPrincipal = :ODPrincipal, ODProfit = :ODProfit, CurODDays = :CurODDays, ActualODDays = :ActualODDays,");
		updateSql.append(" MaxODDays = :MaxODDays, FirstODDate = :FirstODDate, PrvODDate = :PrvODDate,");
		updateSql.append(" PenaltyPaid = :PenaltyPaid, PenaltyDue = :PenaltyDue, PenaltyWaived = :PenaltyWaived,");
		updateSql.append(" FirstRepayDate = :FirstRepayDate, FirstRepayAmt = :FirstRepayAmt,");
		updateSql.append(" FinalRepayAmt = :FinalRepayAmt, FirstDisbDate = :FirstDisbDate,");
		updateSql.append(" LatestDisbDate = :LatestDisbDate, FullPaidDate = :FullPaidDate,");
		updateSql.append(" PrvRpySchDate = :PrvRpySchDate, PrvRpySchPri = :PrvRpySchPri,");
		updateSql.append(" PrvRpySchPft = :PrvRpySchPft,");
		updateSql.append(" NSchdDate = :NSchdDate, NSchdPri = :NSchdPri, NSchdPft = :NSchdPft, ");
		updateSql.append(" NSchdPriDue = :NSchdPriDue, NSchdPftDue = :NSchdPftDue,");
		updateSql.append(" AccumulatedDepPri = :AccumulatedDepPri, DepreciatePri = :DepreciatePri,");
		updateSql.append(" TdSchdAdvPft = :TdSchdAdvPft, TdSchdRbt = :TdSchdRbt, TotalAdvPftSchd = :TotalAdvPftSchd,");
		updateSql.append(" TotalRbtSchd = :TotalRbtSchd, TotalPriPaidInAdv = :TotalPriPaidInAdv,");
		updateSql.append(" FinStatus = :FinStatus, FinStsReason = :FinStsReason, FinWorstStatus = :FinWorstStatus, ");
		updateSql.append(" TotalPftPaidInAdv = :TotalPftPaidInAdv, LastMdfDate = :LastMdfDate");

		if (posted) {
			updateSql.append(" ,AmzTillLBD = :AmzTillLBD, AmzTillLBDNormal= :AmzTillLBDNormal, ");
			updateSql.append(" AmzTillLBDPD = :AmzTillLBDPD, AmzTillLBDPIS = :AmzTillLBDPIS,");
			updateSql.append(" AcrTillLBD = :AcrTillLBD, AcrSuspTillLBD = :AcrSuspTillLBD ");
		}

		if (monthend) {
			updateSql.append(" ,PrvMthAmz = :PrvMthAmz, PrvMthAmzNrm = :PrvMthAmzNrm, ");
			updateSql.append(" PrvMthAmzPD = :PrvMthAmzPD, PrvMthAmzSusp = :PrvMthAmzSusp,");
			updateSql.append(" PrvMthAcr = :PrvMthAcr, PrvMthAcrSusp = :PrvMthAcrSusp");
		}

		updateSql.append(" Where FinReference =:FinReference");

		/*
		 * updateSql.append(" ExcessAmt = :ExcessAmt, ");
		 * updateSql.append(" EmiInAdvance = :EmiInAdvance, PayableAdvise = :PayableAdvise, ");
		 * updateSql.append(" ExcessAmtResv = :ExcessAmtResv,  EmiInAdvanceResv = :EmiInAdvanceResv, ");
		 * updateSql.append("  PayableAdviseResv = :PayableAdviseResv,  LastMdfDate = :LastMdfDate");
		 */

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
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

		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * 
	 */
	public void updateTDDetailsEOD(Date valueDate) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" MERGE INTO Finpftdetails FP ");
		updateSql.append(" USING ( Select  FS.FINREFERENCE,SUM(FS.PROFITSCHD)PROFITSCHD,SUM(FS.SCHDPFTPAID)SCHDPFTPAID,");
		updateSql.append(" SUM(FS.PROFITSCHD-FS.SCHDPFTPAID)PROFITSCHD_SCHDPFTPAID,");
		updateSql.append(" SUM(FS.PRINCIPALSCHD)PRINCIPALSCHD,SUM(FS.SCHDPRIPAID)SCHDPRIPAID,SUM(FS.PRINCIPALSCHD-FS.SCHDPRIPAID) PRINCIPALSCHD_SCHDPRIPAID,");
		updateSql.append(" SUM(CPZAMOUNT) TDPFTCPZ");
		updateSql.append(" from FINSCHEDULEDETAILS FS inner join ");
		updateSql.append(" Finpftdetails FP on FS.FINREFERENCE = FP.FINREFERENCE ");
		updateSql.append(" where (FP.Finisactive=1 or (FP.FinIsActive = '0' and FP.LatestRpyDate = :valueDate))");
		updateSql.append(" and FS.SCHDATE <= :valueDate");
		updateSql.append(" group by  FS.FINREFERENCE ) T2");
		updateSql.append(" ON (T2.FINREFERENCE = FP.FINREFERENCE and (T2.PRINCIPALSCHD_SCHDPRIPAID != FP.TDSCHDPRIBAL or T2.PROFITSCHD_SCHDPFTPAID != FP.TDSCHDPFTBAL");
		updateSql.append(" or FP.TDSCHDPRIPAID != T2.SCHDPRIPAID or FP.TDSCHDPFTPAID!= T2.SCHDPFTPAID Or FP.TDPFTCPZ != T2.TDPFTCPZ))");
		updateSql.append(" WHEN MATCHED THEN UPDATE SET FP.TDSCHDPFT = T2.PROFITSCHD, FP.TDSCHDPFTPAID =  T2.SCHDPFTPAID,");
		updateSql.append(" FP.TDSCHDPFTBAL = T2.PROFITSCHD_SCHDPFTPAID, FP.TDSCHDPRI =  T2.PRINCIPALSCHD,");
		updateSql.append(" FP.TDSCHDPRIPAID =  T2.SCHDPRIPAID, FP.TDSCHDPRIBAL =  T2.PRINCIPALSCHD_SCHDPRIPAID, FP.TDPFTCPZ = T2.TDPFTCPZ ");

		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("valueDate", valueDate);

		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
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
		updateSql.append(" WHEN MATCHED THEN UPDATE SET FP.RECEIVABLEADVISE = T2.RECEIVABLEADVISE, FP.RECEIVABLEADVISEBAL = T2.RECEIVABLEADVISEBAL");

		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("valueDate", valueDate);

		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
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
		//	updateSql.append(" ( FP.BOUNCEAMT != T2.BOUNCEAMT or FP.BOUNCEAMTPAID != T2.BOUNCEAMTPAID or FP.BOUNCEAMTDUE != T2.BOUNCEAMTDUE))");
		updateSql.append(" WHEN MATCHED THEN UPDATE SET FP.BOUNCEAMT = T2.BOUNCEAMT, FP.BOUNCEAMTPAID = T2.BOUNCEAMTPAID, FP.BOUNCEAMTDUE = T2.BOUNCEAMTDUE");

		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("valueDate", valueDate);

		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * @param finReference
	 * @param type
	 * 
	 * method return curOddays from FinPFtDetails Based On Reference
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
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (Exception e) {
			logger.debug(e);
		}
		return 0;
	}

}
