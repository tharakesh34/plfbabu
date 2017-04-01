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
	
	 private static final String INSERT_QUERY="INSERT INTO FinPftDetails_SnapShot  SELECT :AppDate,FinReference,CustId,FinBranch,FinType,LastMdfDate,"
	 		+ "TotalPftSchd,TotalPftCpz,TotalPftPaid,TotalPftBal,TotalPftPaidInAdv,TotalPriPaid,TotalPriBal,TdSchdPft,TdPftCpz,TdSchdPftPaid,TdSchdPftBal,"
	 		+ "TdPftAccrued,TdPftAccrueSusp,TdPftAmortized,TdPftAmortizedSusp,TdSchdPri,TdSchdPriPaid,TdSchdPriBal,AcrTillNBD,AcrTillLBD,AcrTodayToNBD,AmzTillNBD,"
	 		+ "AmzTillLBD,AmzTodayToNBD,RepayFrq,CustCIF,FinCcy,FinPurpose,FinContractDate,FinApprovedDate,FinStartDate,MaturityDate,FullPaidDate,FinAmount,DownPayment,"
	 		+ "CurReducingRate,curFlatRate,TotalpriSchd,EarlyPaidAmt,ODPrincipal,ODProfit,PenaltyPaid,PenaltyDue,PenaltyWaived,NSchdDate,NSchdPri,NSchdPft,NSchdPriDue,"
	 		+ "NSchdPftDue,AccruePft,EarnedPft,Unearned,PftInSusp,SuspPft,PftAccrueTsfd,FinStatus,FinStsReason,FinWorstStatus,AdminPaidAmt,NOInst,NOPaidInst,NOODInst,"
	 		+ "FinAccount,FinAcType,DisbAccountId,DisbActCcy,RepayAccountId,FinCustPftAccount,IncomeAccount,UEIncomeSuspAccount,FinCommitmentRef,FinIsActive,NORepayments,"
	 		+ "FirstRepayDate,FirstRepayAmt,LastRepayAmt,ODDays,FirstODDate,LastODDate,ClosingStatus,FinCategory,LastRpySchDate,NextRpySchDate,LastRpySchPri,LastRpySchPft,"
	 		+ "LatestRpyDate,LatestRpyPri,LatestRpyPft,LatestWriteOffDate,TotalWriteoff,PrvPftAccrueTsfd,SuspPftAccrueTsfd,AccumulatedDepPri,DepreciatePri,AcrTsfdInSusp,"
	 		+ "CRBFirstODDate,CRBLastODDate,CRBODPrincipal,CRBODProfit,CRBODDays,CRBODInst,TotalAdvPftSchd,TotalRbtSchd,TotalPriPaidInAdv,TdSchdAdvPft,TdSchdRbt,"
	 		+ "TdPftAmortizedNormal,TdPftAmortizedPD,AmzTillLBDNormal,AmzTillLBDPD,AmzTillLBDPIS,InsPaidAmt,InsCal from FinPftDetails";
	
	/**
	 * Method for prepare Snapshot details
	 * 
	 * @param date
	 */
	public void doSnapshotPreparation(Date date) throws Exception {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);
		logger.debug("selectSql: "+ INSERT_QUERY);
		try {
			this.namedParameterJdbcTemplate.update(INSERT_QUERY, source);
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
