package com.pennant.app.core;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class SnapshotService {

	private static Logger logger = Logger.getLogger(SnapshotService.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * Method for prepare Snapshot details
	 * 
	 * @param date
	 */
	public void doSnapshotPreparation(Date date) throws Exception {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);
		
		StringBuilder updateQuery = new StringBuilder(" INSERT INTO FinPftDetails_SnapShot ");
		updateQuery.append(" SELECT :AppDate, FinReference, CustId, FinBranch, FinType, LastMdfDate, TotalPftSchd, TotalPftCpz,  ");
		updateQuery.append(" TotalPftPaid, TotalPftBal, TotalPftPaidInAdv, TotalPriPaid, TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid,  ");
		updateQuery.append(" TdSchdPftBal, TdPftAccrued, TdPftAccrueSusp, TdPftAmortized, TdPftAmortizedSusp, TdSchdPri, TdSchdPriPaid,  ");
		updateQuery.append(" TdSchdPriBal, AcrTillNBD, AcrTillLBD, AcrTodayToNBD, AmzTillNBD, AmzTillLBD, AmzTodayToNBD, RepayFrq,  ");
		updateQuery.append(" CustCIF, FinCcy, FinPurpose, FinContractDate, FinApprovedDate, FinStartDate, MaturityDate, FullPaidDate,  ");
		updateQuery.append(" FinAmount, DownPayment, CurReducingRate, curFlatRate, TotalpriSchd, EarlyPaidAmt, ODPrincipal, ODProfit,  ");
		updateQuery.append(" PenaltyPaid, PenaltyDue, PenaltyWaived, NSchdDate, NSchdPri, NSchdPft, NSchdPriDue, NSchdPftDue, AccruePft,  ");
		updateQuery.append(" EarnedPft, Unearned, PftInSusp, SuspPft, PftAccrueTsfd, FinStatus, FinStsReason, FinWorstStatus,  ");
		updateQuery.append(" InsPaidAmt, AdminPaidAmt, InsCal, NOInst, NOPaidInst, NOODInst, FinAccount, FinAcType,  ");
		updateQuery.append(" DisbAccountId, DisbActCcy, RepayAccountId, FinCustPftAccount, IncomeAccount, UEIncomeSuspAccount,  ");
		updateQuery.append(" FinCommitmentRef, FinIsActive, NORepayments, FirstRepayDate, FirstRepayAmt, LastRepayAmt, ODDays,  ");
		updateQuery.append(" FirstODDate, LastODDate, ClosingStatus, FinCategory, LastRpySchDate, NextRpySchDate, LastRpySchPri,  ");
		updateQuery.append(" LastRpySchPft, LatestRpyDate, LatestRpyPri, LatestRpyPft, LatestWriteOffDate, TotalWriteoff, PrvPftAccrueTsfd,  ");
		updateQuery.append(" SuspPftAccrueTsfd, AccumulatedDepPri, DepreciatePri, AcrTsfdInSusp, CRBFirstODDate, CRBLastODDate,  ");
		updateQuery.append(" CRBODPrincipal, CRBODProfit, CRBODDays, CRBODInst ");
		updateQuery.append(" from FinPftDetails");

		logger.debug("selectSql: "+ updateQuery.toString());

		try {
			this.namedParameterJdbcTemplate.update(updateQuery.toString(), source);
		} catch(EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			throw dae;
		}
		logger.debug("Leaving");
		
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}
