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
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.MonthlyAccumulateDetail;

/**
 * DAO methods implementation for the <b>FinanceProfitDetail model</b> class.<br>
 * 
 */
public class FinanceProfitDetailDAOImpl implements FinanceProfitDetailDAO {

	private static Logger logger = Logger.getLogger(FinanceProfitDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 *  Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public FinanceProfitDetail getFinProfitDetailsById(String finReference) {
		logger.debug("Entering");
		
		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);
	    
		StringBuilder selectSql = new StringBuilder("Select FinReference, CustId, FinBranch, FinType, LastMdfDate," );
		selectSql.append(" TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPftPaidInAdv," );
		selectSql.append(" TotalPriPaid, TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid," );
		selectSql.append(" TdSchdPftBal, TdPftAccrued, TdPftAccrueSusp, TdPftAmortized, TdPftAmortizedSusp," );
		selectSql.append(" TdSchdPri, TdSchdPriPaid, TdSchdPriBal, AcrTillNBD, AcrTillLBD,");
		selectSql.append(" AcrTodayToNBD,AmzTillNBD,AmzTillLBD,AmzTodayToNBD, FinWorstStatus, FinStatus, FinStsReason, ");
		selectSql.append(" ClosingStatus, FinCategory, LastRpySchDate, NextRpySchDate, LastRpySchPri, LastRpySchPft, ");
		selectSql.append(" LatestRpyDate, LatestWriteOffDate, LatestRpyPri, LatestRpyPft, TotalWriteoff, FirstODDate, LastODDate, ");
		selectSql.append(" ODPrincipal, ODProfit, ODDays ");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finProfitDetails = null;
		}
		logger.debug("Leaving");
		return finProfitDetails;
	}
	
	/**
	 *  Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public FinanceProfitDetail getFinPftDetailForBatch(String finReference) {
		logger.debug("Entering");
		
		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);
	    
		StringBuilder selectSql = new StringBuilder("Select FinReference, TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, " );
		selectSql.append(" TdSchdPft, TdPftCpz, TdSchdPftPaid, TdSchdPftBal, TdPftAccrued, TdPftAccrueSusp, FirstODDate, LastODDate, " );
		selectSql.append(" AcrTillNBD, AcrTodayToNBD, AmzTillNBD,  FinStatus , FinStsReason ");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finProfitDetails = null;
		}
		logger.debug("Leaving");
		return finProfitDetails;
	}
	
	/**
	 *  Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference) {
		logger.debug("Entering");
		
		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);
	    
		StringBuilder selectSql = new StringBuilder("Select FinReference, TotalPftPaid, TotalPftBal, " );
		selectSql.append(" TotalPriPaid, TotalPriBal, NoInst, NoPaidInst");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finProfitDetails = null;
		}
		logger.debug("Leaving");
		return finProfitDetails;
	}

	/**
	 *  Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public FinanceProfitDetail getFinProfitDetailsByRef(String finReference) {
		logger.debug("Entering");
		
		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);
	    
		StringBuilder selectSql = new StringBuilder("Select AcrTillLBD, TdPftAmortizedSusp,  AmzTillLBD ");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finProfitDetails = null;
		}
		logger.debug("Leaving");
		return finProfitDetails;

	}
	
	/**
	 *  Method for get the FinanceProfitDetail Object by Key finReference
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
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);
		
		try {
			finProfitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finProfitDetails = null;
		}
		logger.debug("Leaving");
		return finProfitDetails;
		
	}

	@Override
	public void update(FinanceProfitDetail finProfitDetails, boolean isRpyProcess) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails ");
		updateSql.append(" Set TotalPftSchd = :TotalPftSchd , TotalPftCpz = :TotalPftCpz , TotalPftPaid = :TotalPftPaid , TotalPftBal = :TotalPftBal , " );
		updateSql.append(" TotalPftPaidInAdv = :TotalPftPaidInAdv , TotalPriPaid = :TotalPriPaid , TotalPriBal = :TotalPriBal , TdSchdPft = :TdSchdPft , " );
		updateSql.append(" TdPftCpz = :TdPftCpz , TdSchdPftPaid = :TdSchdPftPaid , TdSchdPftBal = :TdSchdPftBal , TdPftAccrued = :TdPftAccrued , " );
		updateSql.append(" TdPftAccrueSusp = :TdPftAccrueSusp , AcrTillNBD = :AcrTillNBD , AcrTodayToNBD = :AcrTodayToNBD , TdSchdPri = :TdSchdPri , " );
		updateSql.append(" TdSchdPriPaid = :TdSchdPriPaid , TdSchdPriBal = :TdSchdPriBal , TdPftAmortized = :TdPftAmortized , " );
		updateSql.append(" TdPftAmortizedSusp = :TdPftAmortizedSusp , AmzTillNBD = :AmzTillNBD , AmzTodayToNBD = :AmzTodayToNBD , " );
		updateSql.append(" FullPaidDate = :FullPaidDate , CurReducingRate = :CurReducingRate , CurFlatRate = :CurFlatRate , TotalpriSchd = :TotalpriSchd , " );
		updateSql.append(" EarlyPaidAmt = :EarlyPaidAmt , ODPrincipal = :ODPrincipal , ODProfit = :ODProfit , PenaltyPaid = :PenaltyPaid , " );
		updateSql.append(" PenaltyDue = :PenaltyDue , PenaltyWaived = :PenaltyWaived , NSchdDate = :NSchdDate , NSchdPri = :NSchdPri , NSchdPft = :NSchdPft , " );
		updateSql.append(" NSchdPriDue = :NSchdPriDue , NSchdPftDue = :NSchdPftDue , AccruePft = :AccruePft , EarnedPft = :EarnedPft , " );
		updateSql.append(" Unearned = :Unearned , PftInSusp = :PftInSusp , SuspPft = :SuspPft , " );
		updateSql.append(" FinWorstStatus = :FinWorstStatus , NOInst = :NOInst , NOPaidInst = :NOPaidInst , NOODInst = :NOODInst , " );
		updateSql.append(" NORepayments = :NORepayments , FirstRepayAmt = :FirstRepayAmt , LastRepayAmt = :LastRepayAmt,FinIsActive=:FinIsActive, " );
		updateSql.append(" ODDays = :ODDays, FirstODDate =:FirstODDate , LastODDate = :LastODDate, FinStatus=:FinStatus, FinStsReason =:FinStsReason, " );
		updateSql.append(" ClosingStatus = :ClosingStatus, LastRpySchDate = :LastRpySchDate, NextRpySchDate = :NextRpySchDate, ");
		updateSql.append(" LastRpySchPri = :LastRpySchPri, LastRpySchPft = :LastRpySchPft, ");
		if(isRpyProcess){
			updateSql.append(" LatestRpyDate = :LatestRpyDate, LatestRpyPri =:LatestRpyPri, LatestRpyPft = :LatestRpyPft, ");
		}
		updateSql.append(" LatestWriteOffDate = :LatestWriteOffDate, TotalWriteoff = :TotalWriteoff ");
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
		updateSql.append(" Set TotalPftSchd = :TotalPftSchd , TotalPftCpz = :TotalPftCpz , TotalPftPaid = :TotalPftPaid , TotalPftBal = :TotalPftBal , " );
		updateSql.append(" TotalPftPaidInAdv = :TotalPftPaidInAdv , TotalPriPaid = :TotalPriPaid , TotalPriBal = :TotalPriBal , TdSchdPft = :TdSchdPft , " );
		updateSql.append(" TdPftCpz = :TdPftCpz , TdSchdPftPaid = :TdSchdPftPaid , TdSchdPftBal = :TdSchdPftBal , TdPftAccrued = :TdPftAccrued , " );
		updateSql.append(" TdPftAccrueSusp = :TdPftAccrueSusp , AcrTillNBD = :AcrTillNBD , AcrTodayToNBD = :AcrTodayToNBD , TdSchdPri = :TdSchdPri , " );
		updateSql.append(" TdSchdPriPaid = :TdSchdPriPaid , TdSchdPriBal = :TdSchdPriBal , TdPftAmortized = :TdPftAmortized , " );
		updateSql.append(" TdPftAmortizedSusp = :TdPftAmortizedSusp , AmzTillNBD = :AmzTillNBD , AmzTodayToNBD = :AmzTodayToNBD , " );
		updateSql.append(" FullPaidDate = :FullPaidDate , CurReducingRate = :CurReducingRate , CurFlatRate = :CurFlatRate , TotalpriSchd = :TotalpriSchd , " );
		updateSql.append(" EarlyPaidAmt = :EarlyPaidAmt , ODPrincipal = :ODPrincipal , ODProfit = :ODProfit , PenaltyPaid = :PenaltyPaid , " );
		updateSql.append(" PenaltyDue = :PenaltyDue , PenaltyWaived = :PenaltyWaived , NSchdDate = :NSchdDate , NSchdPri = :NSchdPri , NSchdPft = :NSchdPft , " );
		updateSql.append(" NSchdPriDue = :NSchdPriDue , NSchdPftDue = :NSchdPftDue , AccruePft = :AccruePft , EarnedPft = :EarnedPft , " );
		updateSql.append(" Unearned = :Unearned , PftInSusp = :PftInSusp , SuspPft = :SuspPft , AccumulatedDepPri=:AccumulatedDepPri, DepreciatePri=:DepreciatePri, " );
		updateSql.append(" FinWorstStatus = :FinWorstStatus , NOInst = :NOInst , NOPaidInst = :NOPaidInst , NOODInst = :NOODInst , " );
		updateSql.append(" NORepayments = :NORepayments , FirstRepayAmt = :FirstRepayAmt , LastRepayAmt = :LastRepayAmt ,FinIsActive=:FinIsActive, " );
		updateSql.append(" ODDays = :ODDays, FirstODDate =:FirstODDate , LastODDate = :LastODDate,  FinStatus=:FinStatus, FinStsReason =:FinStsReason, " );
		updateSql.append(" ClosingStatus = :ClosingStatus, LastRpySchDate = :LastRpySchDate, NextRpySchDate = :NextRpySchDate, ");
		updateSql.append(" LastRpySchPri = :LastRpySchPri, LastRpySchPft = :LastRpySchPft, LatestWriteOffDate = :LatestWriteOffDate, TotalWriteoff = :TotalWriteoff ");
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
		updateSql.append(" Set AcrTillLBD=:AcrTillLBD, AcrTodayToNBD=:AcrTodayToNBD," );
		updateSql.append(" AmzTillLBD= :AmzTillLBD,AmzTodayToNBD = :AmzTodayToNBD , LastMdfDate =:LastMdfDate ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finProfitDetails.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

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
	public String save(FinanceProfitDetail finProfitDetails, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinPftDetails").append(type);
		insertSql.append(" (FinReference, CustId , FinBranch , FinType , LastMdfDate , TotalPftSchd , TotalPftCpz , TotalPftPaid ," );
		insertSql.append(" TotalPftBal , TotalPftPaidInAdv , TotalPriPaid , TotalPriBal , TdSchdPft , TdPftCpz , TdSchdPftPaid ," );
		insertSql.append(" TdSchdPftBal , TdPftAccrued , TdPftAccrueSusp , TdPftAmortized , TdPftAmortizedSusp , TdSchdPri ," );
		insertSql.append(" TdSchdPriPaid , TdSchdPriBal , AcrTillNBD , AcrTillLBD , AcrTodayToNBD , AmzTillNBD , AmzTillLBD ," );
		insertSql.append(" AmzTodayToNBD , RepayFrq , CustCIF , FinCcy , FinPurpose , FinContractDate , FinApprovedDate ," );
		insertSql.append(" FinStartDate , MaturityDate , FullPaidDate , FinAmount , DownPayment , CurReducingRate , curFlatRate ," );
		insertSql.append(" TotalpriSchd , EarlyPaidAmt , ODPrincipal , ODProfit , PenaltyPaid , PenaltyDue , PenaltyWaived ," );
		insertSql.append(" NSchdDate , NSchdPri , NSchdPft , NSchdPriDue , NSchdPftDue , AccruePft , EarnedPft , Unearned ," );
		insertSql.append(" PftInSusp , SuspPft , PftAccrueTsfd , FinStatus , FinStsReason , FinWorstStatus , TAKAFULPaidAmt ," );
		insertSql.append(" AdminPaidAmt , TAKAFULInsCal , NOInst , NOPaidInst , NOODInst , FinAccount , FinAcType , DisbAccountId ," );
		insertSql.append(" DisbActCcy , RepayAccountId , FinCustPftAccount , IncomeAccount , UEIncomeSuspAccount , FinCommitmentRef ," );
		insertSql.append(" FinIsActive , NORepayments , FirstRepayDate , FirstRepayAmt , LastRepayAmt, ODDays , FirstODDate , LastODDate , " );
		insertSql.append(" ClosingStatus , FinCategory , LastRpySchDate ,  NextRpySchDate , LastRpySchPri , LastRpySchPft, ");
		insertSql.append(" LatestRpyDate, LatestWriteOffDate, LatestRpyPri, LatestRpyPft, TotalWriteoff )");
		insertSql.append(" VALUES (:FinReference, :CustId , :FinBranch , :FinType , :LastMdfDate , :TotalPftSchd , :TotalPftCpz ," );
		insertSql.append(" :TotalPftPaid , :TotalPftBal , :TotalPftPaidInAdv , :TotalPriPaid , :TotalPriBal , :TdSchdPft , :TdPftCpz ," );
		insertSql.append(" :TdSchdPftPaid , :TdSchdPftBal , :TdPftAccrued , :TdPftAccrueSusp , :TdPftAmortized , :TdPftAmortizedSusp ," );
		insertSql.append(" :TdSchdPri , :TdSchdPriPaid , :TdSchdPriBal , :AcrTillNBD , :AcrTillLBD , :AcrTodayToNBD , :AmzTillNBD ," );
		insertSql.append(" :AmzTillLBD , :AmzTodayToNBD , :RepayFrq , :CustCIF , :FinCcy , :FinPurpose , :FinContractDate , :FinApprovedDate ," );
		insertSql.append(" :FinStartDate , :MaturityDate , :FullPaidDate , :FinAmount , :DownPayment , :CurReducingRate , :curFlatRate ," );
		insertSql.append(" :TotalpriSchd , :EarlyPaidAmt , :ODPrincipal , :ODProfit , :PenaltyPaid , :PenaltyDue , :PenaltyWaived ," );
		insertSql.append(" :NSchdDate , :NSchdPri , :NSchdPft , :NSchdPriDue , :NSchdPftDue , :AccruePft , :EarnedPft , :Unearned ," );
		insertSql.append(" :PftInSusp , :SuspPft , :PftAccrueTsfd , :FinStatus , :FinStsReason , :FinWorstStatus , :TAKAFULPaidAmt ," );
		insertSql.append(" :AdminPaidAmt , :TAKAFULInsCal , :NOInst , :NOPaidInst , :NOODInst , :FinAccount , :FinAcType , :DisbAccountId ," );
		insertSql.append(" :DisbActCcy , :RepayAccountId , :FinCustPftAccount , :IncomeAccount , :UEIncomeSuspAccount , :FinCommitmentRef ," );
		insertSql.append(" :FinIsActive , :NORepayments , :FirstRepayDate , :FirstRepayAmt , :LastRepayAmt , :ODDays , :FirstODDate , :LastODDate ,");
		insertSql.append(" :ClosingStatus , :FinCategory , :LastRpySchDate , :NextRpySchDate , :LastRpySchPri , :LastRpySchPft, ");
		insertSql.append(" :LatestRpyDate, :LatestWriteOffDate, :LatestRpyPri, :LatestRpyPft, :TotalWriteoff )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finProfitDetails.getFinReference();
	}
	
	@Override
	public void save(List<FinanceProfitDetail> finProfitDetails, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinPftDetails").append(type);
		insertSql.append(" (FinReference, CustId , FinBranch , FinType , LastMdfDate , TotalPftSchd , TotalPftCpz , TotalPftPaid ," );
		insertSql.append(" TotalPftBal , TotalPftPaidInAdv , TotalPriPaid , TotalPriBal , TdSchdPft , TdPftCpz , TdSchdPftPaid ," );
		insertSql.append(" TdSchdPftBal , TdPftAccrued , TdPftAccrueSusp , TdPftAmortized , TdPftAmortizedSusp , TdSchdPri ," );
		insertSql.append(" TdSchdPriPaid , TdSchdPriBal , AcrTillNBD , AcrTillLBD , AcrTodayToNBD , AmzTillNBD , AmzTillLBD ," );
		insertSql.append(" AmzTodayToNBD , RepayFrq , CustCIF , FinCcy , FinPurpose , FinContractDate , FinApprovedDate ," );
		insertSql.append(" FinStartDate , MaturityDate , FullPaidDate , FinAmount , DownPayment , CurReducingRate , curFlatRate ," );
		insertSql.append(" TotalpriSchd , EarlyPaidAmt , ODPrincipal , ODProfit , PenaltyPaid , PenaltyDue , PenaltyWaived ," );
		insertSql.append(" NSchdDate , NSchdPri , NSchdPft , NSchdPriDue , NSchdPftDue , AccruePft , EarnedPft , Unearned ," );
		insertSql.append(" PftInSusp , SuspPft , PftAccrueTsfd , FinStatus , FinStsReason , FinWorstStatus , TAKAFULPaidAmt ," );
		insertSql.append(" AdminPaidAmt , TAKAFULInsCal , NOInst , NOPaidInst , NOODInst , FinAccount , FinAcType , DisbAccountId ," );
		insertSql.append(" DisbActCcy , RepayAccountId , FinCustPftAccount , IncomeAccount , UEIncomeSuspAccount , FinCommitmentRef ," );
		insertSql.append(" FinIsActive , NORepayments , FirstRepayDate , FirstRepayAmt , LastRepayAmt , ODDays , FirstODDate , LastODDate , " );
		insertSql.append(" ClosingStatus , FinCategory , LastRpySchDate ,  NextRpySchDate , LastRpySchPri , LastRpySchPft, ");
		insertSql.append(" LatestRpyDate, LatestWriteOffDate, LatestRpyPri, LatestRpyPft, TotalWriteoff )");
		insertSql.append(" VALUES (:FinReference, :CustId , :FinBranch , :FinType , :LastMdfDate , :TotalPftSchd , :TotalPftCpz ," );
		insertSql.append(" :TotalPftPaid , :TotalPftBal , :TotalPftPaidInAdv , :TotalPriPaid , :TotalPriBal , :TdSchdPft , :TdPftCpz ," );
		insertSql.append(" :TdSchdPftPaid , :TdSchdPftBal , :TdPftAccrued , :TdPftAccrueSusp , :TdPftAmortized , :TdPftAmortizedSusp ," );
		insertSql.append(" :TdSchdPri , :TdSchdPriPaid , :TdSchdPriBal , :AcrTillNBD , :AcrTillLBD , :AcrTodayToNBD , :AmzTillNBD ," );
		insertSql.append(" :AmzTillLBD , :AmzTodayToNBD , :RepayFrq , :CustCIF , :FinCcy , :FinPurpose , :FinContractDate , :FinApprovedDate ," );
		insertSql.append(" :FinStartDate , :MaturityDate , :FullPaidDate , :FinAmount , :DownPayment , :CurReducingRate , :curFlatRate ," );
		insertSql.append(" :TotalpriSchd , :EarlyPaidAmt , :ODPrincipal , :ODProfit , :PenaltyPaid , :PenaltyDue , :PenaltyWaived ," );
		insertSql.append(" :NSchdDate , :NSchdPri , :NSchdPft , :NSchdPriDue , :NSchdPftDue , :AccruePft , :EarnedPft , :Unearned ," );
		insertSql.append(" :PftInSusp , :SuspPft , :PftAccrueTsfd , :FinStatus , :FinStsReason , :FinWorstStatus , :TAKAFULPaidAmt ," );
		insertSql.append(" :AdminPaidAmt , :TAKAFULInsCal , :NOInst , :NOPaidInst , :NOODInst , :FinAccount , :FinAcType , :DisbAccountId ," );
		insertSql.append(" :DisbActCcy , :RepayAccountId , :FinCustPftAccount , :IncomeAccount , :UEIncomeSuspAccount , :FinCommitmentRef ," );
		insertSql.append(" :FinIsActive , :NORepayments , :FirstRepayDate , :FirstRepayAmt , :LastRepayAmt , :ODDays , :FirstODDate , :LastODDate ,");
		insertSql.append(" :ClosingStatus , :FinCategory , :LastRpySchDate , :NextRpySchDate , :LastRpySchPri , :LastRpySchPft, ");
		insertSql.append(" :LatestRpyDate, :LatestWriteOffDate, :LatestRpyPri, :LatestRpyPft, :TotalWriteoff )");
		
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finProfitDetails.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	@Override
    public BigDecimal getAccrueAmount(String finReference) {
		logger.debug("Entering");
		
		BigDecimal accruedAmount = null;
		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setFinReference(finReference);
	    
		StringBuilder selectSql = new StringBuilder("Select AcrTillNBD ");
		selectSql.append(" From FinPftDetails Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		try {
			accruedAmount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
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
			this.namedParameterJdbcTemplate.update("Truncate table FinPftDetails_Temp", beanParameters);
			this.namedParameterJdbcTemplate.update("INSERT INTO FinPftDetails_Temp  SELECT * FROM FinPftDetails", beanParameters);

		} catch (DataAccessException e) {
			logger.error(e);
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
		updateSql.append(" Set LatestRpyDate = :LatestRpyDate, LatestRpyPri =:LatestRpyPri, LatestRpyPft = :LatestRpyPft ");
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

		StringBuilder insertSql = new StringBuilder(" INSERT INTO MonthlyAccumulateDetail ");
		insertSql.append(" Select FinReference, :MonthEndDate AS  MonthEndDate, (TotalPftPaid+TdPftAccrued) AS PftAccrued, " );
		insertSql.append(" (TotalPftPaid + TdPftAccrued - PftAccrueTsfd) AS PftTsfd , SuspPftAccrueTsfd AS SuspPftAccrued, " );
		insertSql.append(" (SuspPftAccrueTsfd - TdPftAccrueSusp) AS SuspPftTsfd, AccumulatedDepPri, DepreciatePri " );
		insertSql.append(" FROM FinPftDetails where FinIsActive = 1 OR (FinIsActive = 0 AND LatestRpyDate >= :MonthStartDate AND LatestRpyDate <= :MonthEndDate ) ");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accumulateDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
    }
	
}
