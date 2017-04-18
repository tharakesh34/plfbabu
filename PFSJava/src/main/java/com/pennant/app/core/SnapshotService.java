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
	
	 private static final String snapshotQuery="INSERT INTO FinPftDetails_SnapShot  SELECT :AppDate,FinReference,CustId,FinBranch,FinType,LastMdfDate,"
	 		+ "TotalPftSchd,TotalPftCpz,TotalPftPaid,TotalPftBal,TotalPftPaidInAdv,TotalPriPaid,TotalPriBal,TdSchdPft,TdPftCpz,TdSchdPftPaid,TdSchdPftBal,"
	 		+ "pftAccrued,pftAccrueSusp,PftAmz,PftAmzSusp,TdSchdPri,TdSchdPriPaid,TdSchdPriBal,AcrTillLBD,"
	 		+ "AmzTillLBD,RepayFrq,CustCIF,FinCcy,FinPurpose,FinContractDate,FinApprovedDate,FinStartDate,MaturityDate,FullPaidDate,FinAmount,DownPayment,"
	 		+ "CurReducingRate,curFlatRate,TotalpriSchd,ODPrincipal,ODProfit,PenaltyPaid,PenaltyDue,PenaltyWaived,NSchdDate,NSchdPri,NSchdPft,NSchdPriDue,"
	 		+ "NSchdPftDue,EarnedPft,Unearned,PftInSusp,FinStatus,FinStsReason,FinWorstStatus,NOInst,NOPaidInst,NOODInst,"
	 		+ "FinAccount,FinAcType,DisbAccountId,DisbActCcy,RepayAccountId,FinCustPftAccount,IncomeAccount,UEIncomeSuspAccount,FinCommitmentRef,FinIsActive,"
	 		+ "FirstRepayDate,FirstRepayAmt,FinalRepayAmt,CurODDays,FirstODDate,prvODDate,ClosingStatus,FinCategory,PrvRpySchDate,PrvRpySchPri,PrvRpySchPft,"
	 		+ "LatestRpyDate,LatestRpyPri,LatestRpyPft,TotalWriteoff,AccumulatedDepPri,DepreciatePri,AcrTsfdInSusp,"
	 		+ "TotalAdvPftSchd,TotalRbtSchd,TotalPriPaidInAdv,TdSchdAdvPft,TdSchdRbt,"
	 		+ "PftAmzNormal,PftAmzPD,AmzTillLBDNormal,AmzTillLBDPD,AmzTillLBDPIS from FinPftDetails";
	
	/**
	 * Method for prepare Snapshot details
	 * 
	 * @param date
	 */
	public void doSnapshotPreparation(Date date) throws Exception {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);
		logger.debug("selectSql: "+ snapshotQuery);
		try {
			this.namedParameterJdbcTemplate.update(snapshotQuery, source);
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
