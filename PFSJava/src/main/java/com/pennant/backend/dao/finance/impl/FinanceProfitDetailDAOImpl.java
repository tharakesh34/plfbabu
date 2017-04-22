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
 * FileName    		:  FinanceProfitDetailDAOImpl.java                                                   * 	  
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

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
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
				"Select FinReference, CustId, FinBranch, FinType, LastMdfDate, FinIsActive, ");
		selectSql.append(" TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPftPaidInAdv,");
		selectSql.append(" TotalPriPaid, TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid,");
		selectSql.append(" TdSchdPftBal, PftAccrued, PftAccrueSusp, PftAmz, PftAmzSusp,");
		selectSql.append(" TdSchdPri, TdSchdPriPaid, TdSchdPriBal, AcrTillLBD,");
		selectSql.append(" AmzTillLBD, FinWorstStatus, FinStatus, FinStsReason, ");
		selectSql.append(" ClosingStatus, FinCategory, PrvRpySchDate, NSchdDate, PrvRpySchPri, PrvRpySchPft, ");
		selectSql.append(" LatestRpyDate, LatestRpyPri, LatestRpyPft, TotalWriteoff, FirstODDate, PrvODDate, ");
		selectSql.append(" ODPrincipal, ODProfit, CurODDays, FinStartDate, ExcessAmt, EmiInAdvance, ");
		selectSql.append(" PayableAdvise, ExcessAmtResv, EmiInAdvanceResv, PayableAdviseResv");
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
	public FinanceProfitDetail getFinPftDetailForBatch(String finReference) {
		logger.debug("Entering");

		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("Select FinReference, FinStartDate, MaturityDate,  ");
		selectSql.append(" FinStatus , FinStsReason ");
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
		selectSql.append(" TotalPftPaidInAdv, TotalPriPaidInAdv, ExcessAmt, EmiInAdvance, PayableAdvise");
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
		//		updateSql.append(" TotPftOnPDWaived = :TotPftOnPDWaived,");
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
		updateSql.append("  PrvRpySchPft = :PrvRpySchPft,");
		updateSql.append(" NSchdDate = :NSchdDate, NSchdPri = :NSchdPri, NSchdPft = :NSchdPft, ");
		updateSql.append(" NSchdPriDue = :NSchdPriDue, NSchdPftDue = :NSchdPftDue,");
		updateSql.append(" AccumulatedDepPri = :AccumulatedDepPri, DepreciatePri = :DepreciatePri,");
		updateSql.append(" TdSchdAdvPft = :TdSchdAdvPft, TdSchdRbt = :TdSchdRbt, TotalAdvPftSchd = :TotalAdvPftSchd,");
		updateSql.append(" TotalRbtSchd = :TotalRbtSchd, TotalPriPaidInAdv = :TotalPriPaidInAdv,");
		updateSql.append(" TotalPftPaidInAdv = :TotalPftPaidInAdv, ExcessAmt = :ExcessAmt, ");
		updateSql.append(" EmiInAdvance = :EmiInAdvance, PayableAdvise = :PayableAdvise, ExcessAmtResv = :ExcessAmtResv,");
		updateSql.append(" EmiInAdvanceResv = :EmiInAdvanceResv, PayableAdviseResv = :PayableAdviseResv,  ");
		updateSql.append(" LastMdfDate = :LastMdfDate");
		if (isRpyProcess) {
			updateSql
					.append(" ,LatestRpyDate = :LatestRpyDate, LatestRpyPri =:LatestRpyPri, LatestRpyPft = :LatestRpyPft ");
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
		updateSql
				.append(" Set TotalPftSchd = :TotalPftSchd , TotalPftCpz = :TotalPftCpz , TotalPftPaid = :TotalPftPaid , TotalPftBal = :TotalPftBal , ");
		updateSql
				.append(" TotalPftPaidInAdv = :TotalPftPaidInAdv , TotalPriPaid = :TotalPriPaid , TotalPriBal = :TotalPriBal , TdSchdPft = :TdSchdPft , ");
		updateSql
				.append(" TdPftCpz = :TdPftCpz , TdSchdPftPaid = :TdSchdPftPaid , TdSchdPftBal = :TdSchdPftBal , PftAccrued = :PftAccrued , ");
		updateSql.append(" PftAccrueSusp = :PftAccrueSusp , TdSchdPri = :TdSchdPri , ");
		updateSql.append(" TdSchdPriPaid = :TdSchdPriPaid , TdSchdPriBal = :TdSchdPriBal , PftAmz = :PftAmz , ");
		updateSql.append(" PftAmzSusp = :PftAmzSusp , ");
		updateSql
				.append(" FullPaidDate = :FullPaidDate , CurReducingRate = :CurReducingRate , CurFlatRate = :CurFlatRate , TotalpriSchd = :TotalpriSchd , ");
		updateSql.append(" ODPrincipal = :ODPrincipal , ODProfit = :ODProfit , PenaltyPaid = :PenaltyPaid , ");
		updateSql
				.append(" PenaltyDue = :PenaltyDue , PenaltyWaived = :PenaltyWaived , NSchdDate = :NSchdDate , NSchdPri = :NSchdPri , NSchdPft = :NSchdPft , ");
		updateSql.append(" NSchdPriDue = :NSchdPriDue , NSchdPftDue = :NSchdPftDue , ");
		updateSql
				.append(" PftInSusp = :PftInSusp , AccumulatedDepPri=:AccumulatedDepPri, DepreciatePri=:DepreciatePri, ");
		updateSql
				.append(" FinWorstStatus = :FinWorstStatus , NOInst = :NOInst , NOPaidInst = :NOPaidInst , NOODInst = :NOODInst ,  ");
		updateSql
				.append(" FirstRepayAmt = :FirstRepayAmt , FinalRepayAmt = :FinalRepayAmt ,FinIsActive=:FinIsActive, ");
		updateSql
				.append(" CurODDays = :CurODDays, FirstODDate =:FirstODDate , PrvODDate = :PrvODDate,  FinStatus=:FinStatus, FinStsReason =:FinStsReason, ");
		updateSql.append(" ClosingStatus = :ClosingStatus, PrvRpySchDate = :PrvRpySchDate, ");
		updateSql
				.append(" PrvRpySchPri = :PrvRpySchPri, PrvRpySchPft = :PrvRpySchPft, TotalWriteoff = :TotalWriteoff, ");
		updateSql
				.append(" TotalAdvPftSchd = :TotalAdvPftSchd, TotalRbtSchd = :TotalRbtSchd, TotalPriPaidInAdv=:TotalPriPaidInAdv, TdSchdAdvPft=:TdSchdAdvPft, ");
		updateSql.append(" TdSchdRbt = :TdSchdRbt, PftAmzNormal = :PftAmzNormal, PftAmzPD=:PftAmzPD ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finProfitDetails.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void updateBatchList(List<FinanceProfitDetail> finProfitDetails, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails").append(type);
		updateSql.append(" Set AcrTillLBD=:AcrTillLBD, LastMdfDate =:LastMdfDate,");
		updateSql
				.append(" AmzTillLBD= :AmzTillLBD, AmzTillLBDNormal =:AmzTillLBDNormal, AmzTillLBDPD = :AmzTillLBDPD, ");
		updateSql.append(" AmzTillLBDPIS = :AmzTillLBDPIS ");
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
		insertSql.append(" CurODDays, MaxODDays, FirstODDate, PrvODDate, ClosingStatus, FinCategory,");
		insertSql.append(" PrvRpySchDate, PrvRpySchPri, PrvRpySchPft, LatestRpyDate, LatestRpyPri,");
		insertSql.append(" LatestRpyPft, TotalWriteoff, AccumulatedDepPri, DepreciatePri, TotalAdvPftSchd,");
		insertSql.append(" TotalRbtSchd, TotalPriPaidInAdv, TdSchdAdvPft, TdSchdRbt, PftAmzNormal, PftAmzPD,");
		insertSql.append(" AmzTillLBDNormal, AmzTillLBDPD, AmzTillLBDPIS, CalPftOnPD, PftOnPDMethod,");
		insertSql.append(" PftOnPDMrg, TotPftOnPD, TotPftOnPDPaid, TotPftOnPDWaived, TotPftOnPDDue,");
		insertSql.append(" AcrSuspTillLBD, PrvMthAmz, PrvMthAmzNrm, PrvMthAmzPD, PrvMthAmzSusp, PrvMthAcr,");
		insertSql.append(" PrvMthAcrSusp, FirstDisbDate, LatestDisbDate, FutureInst, RemainingTenor,");
		insertSql.append(" TotalTenor, ExcessAmt, EmiInAdvance, PayableAdvise, ");
		insertSql.append(" ExcessAmtResv, EmiInAdvanceResv, PayableAdviseResv) ");
		insertSql.append(" Values");
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
		insertSql.append(" :CurODDays, :MaxODDays, :FirstODDate, :PrvODDate, :ClosingStatus, :FinCategory,");
		insertSql.append(" :PrvRpySchDate, :PrvRpySchPri, :PrvRpySchPft, :LatestRpyDate, :LatestRpyPri,");
		insertSql.append(" :LatestRpyPft, :TotalWriteoff, :AccumulatedDepPri, :DepreciatePri, :TotalAdvPftSchd,");
		insertSql.append(" :TotalRbtSchd, :TotalPriPaidInAdv, :TdSchdAdvPft, :TdSchdRbt, :PftAmzNormal, :PftAmzPD,");
		insertSql.append(" :AmzTillLBDNormal, :AmzTillLBDPD, :AmzTillLBDPIS, :CalPftOnPD, :PftOnPDMethod,");
		insertSql.append(" :PftOnPDMrg, :TotPftOnPD, :TotPftOnPDPaid, :TotPftOnPDWaived, :TotPftOnPDDue,");
		insertSql.append(" :AcrSuspTillLBD, :PrvMthAmz, :PrvMthAmzNrm, :PrvMthAmzPD, :PrvMthAmzSusp, :PrvMthAcr,");
		insertSql.append(" :PrvMthAcrSusp, :FirstDisbDate, :LatestDisbDate, :FutureInst, :RemainingTenor,");
		insertSql.append(" :TotalTenor, :ExcessAmt, :EmiInAdvance, :PayableAdvise, ");
		insertSql.append(" :ExcessAmtResv, :EmiInAdvanceResv, :PayableAdviseResv) ");

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

		StringBuilder selectSql = new StringBuilder("Select AmzTillLBD-TdSchdPftPaid ");
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
		updateSql
				.append(" Set LatestRpyDate = :LatestRpyDate, LatestRpyPri =:LatestRpyPri, LatestRpyPft = :LatestRpyPft ");
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

}
