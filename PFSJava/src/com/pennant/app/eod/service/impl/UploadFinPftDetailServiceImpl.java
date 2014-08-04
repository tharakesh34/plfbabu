package com.pennant.app.eod.service.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.eod.service.UploadFinPftDetailService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ExecutionStatus;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.vo.EodFinProfitDetail;
import com.pennant.equation.process.UploadFinProfitDetailsProcess;

public class UploadFinPftDetailServiceImpl implements UploadFinPftDetailService {
	private Logger logger = Logger.getLogger(UploadFinPftDetailServiceImpl.class);
	private DataSource dataSource;	
	private UploadFinProfitDetailsProcess uploadFinProfitDetailsProcess;

	@Override
	public void doUploadPftDetails(Object object) throws Exception{
		logger.info("Entering");
		List<EodFinProfitDetail> finPftDetails = new ArrayList<EodFinProfitDetail>();

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		EodFinProfitDetail finPftDetail = null;
		boolean isFirstCall = true;
		System.out.println("Start Time: "+ DateUtility.formateDate(new Date(System.currentTimeMillis()), PennantConstants.timeFormat));
		
		
		ChunkContext context = null;
		ExecutionStatus exeStatus = null;
		if(object != null &&  object instanceof ChunkContext) {
			context = (ChunkContext)object;
		} else {
			exeStatus = (ExecutionStatus)object;
		}

		try{
			
			connection = DataSourceUtils.doGetConnection(this.dataSource);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(getCountQuery());
			resultSet.next();
			
			if(exeStatus != null) {
				exeStatus.setActualCount(resultSet.getInt(1));
			} else {
				BatchUtil.setExecution(context,  "TOTAL", String.valueOf(resultSet.getInt(1)));
			}
			
			statement = connection.createStatement();
			resultSet = statement.executeQuery(getQuery());

			while (resultSet.next()) {
				finPftDetail = new EodFinProfitDetail();
				finPftDetail.setFinReference(resultSet.getString("FinReference"));
				finPftDetail.setCustCIF(resultSet.getString("CustCIF"));
				finPftDetail.setFinBranch(resultSet.getString("FinBranch")); 
				finPftDetail.setFinType(resultSet.getString("FinType")); 
				finPftDetail.setLastMdfDate(resultSet.getDate("LastMdfDate")); 
				finPftDetail.setTotalPftSchd(resultSet.getBigDecimal("TotalPftSchd")); 
				finPftDetail.setTotalPftCpz(resultSet.getBigDecimal("TotalPftCpz")); 
				finPftDetail.setTotalPftPaid(resultSet.getBigDecimal("TotalPftPaid")); 
				finPftDetail.setTotalPftBal(resultSet.getBigDecimal("TotalPftBal")); 
				finPftDetail.setTotalPftPaidInAdv(resultSet.getBigDecimal("TotalPftPaidInAdv")); 
				finPftDetail.setTotalPriPaid(resultSet.getBigDecimal("TotalPriPaid")); 
				finPftDetail.setTotalPriBal(resultSet.getBigDecimal("TotalPriBal")); 
				finPftDetail.setTdSchdPft(resultSet.getBigDecimal("TdSchdPft")); 
				finPftDetail.setTdPftCpz(resultSet.getBigDecimal("TdPftCpz")); 
				finPftDetail.setTdSchdPftPaid(resultSet.getBigDecimal("TdSchdPftPaid")); 
				finPftDetail.setTdSchdPftBal(resultSet.getBigDecimal("TdSchdPftBal")); 
				finPftDetail.setTdPftAccrued(resultSet.getBigDecimal("TdPftAccrued")); 
				finPftDetail.setTdPftAccrueSusp(resultSet.getBigDecimal("TdPftAccrueSusp")); 
				finPftDetail.setTdPftAmortized(resultSet.getBigDecimal("TdPftAmortized")); 
				finPftDetail.setTdPftAmortizedSusp(resultSet.getBigDecimal("TdPftAmortizedSusp")); 
				finPftDetail.setTdSchdPri(resultSet.getBigDecimal("TdSchdPri")); 
				finPftDetail.setTdSchdPriPaid(resultSet.getBigDecimal("TdSchdPriPaid")); 
				finPftDetail.setTdSchdPriBal(resultSet.getBigDecimal("TdSchdPriBal")); 
				finPftDetail.setAcrTillNBD(resultSet.getBigDecimal("AcrTillNBD")); 
				finPftDetail.setAcrTillLBD(resultSet.getBigDecimal("AcrTillLBD")); 
				finPftDetail.setAcrTodayToNBD(resultSet.getBigDecimal("AcrTodayToNBD")); 
				finPftDetail.setAmzTillNBD(resultSet.getBigDecimal("AmzTillNBD")); 
				finPftDetail.setAmzTillLBD(resultSet.getBigDecimal("AmzTillLBD")); 
				finPftDetail.setAmzTodayToNBD(resultSet.getBigDecimal("AmzTodayToNBD")); 
				finPftDetail.setRepayFrq(resultSet.getString("RepayFrq")); 
				finPftDetail.setFinCcy(resultSet.getString("FinCcy")); 
				finPftDetail.setFinPurpose(resultSet.getString("FinPurpose")); 
				finPftDetail.setFinContractDate(resultSet.getDate("FinContractDate")); 
				finPftDetail.setFinApprovedDate(resultSet.getDate("FinApprovedDate")); 
				finPftDetail.setFinStartDate(resultSet.getDate("FinStartDate")); 
				finPftDetail.setMaturityDate(resultSet.getDate("MaturityDate")); 
				finPftDetail.setFullPaidDate(resultSet.getDate("FullPaidDate")); 
				finPftDetail.setFinAmount(resultSet.getBigDecimal("FinAmount")); 
				finPftDetail.setDownPayment(resultSet.getBigDecimal("DownPayment")); 
				finPftDetail.setCurReducingRate(resultSet.getBigDecimal("CurReducingRate")); 
				finPftDetail.setCurFlatRate(resultSet.getBigDecimal("curFlatRate")); 
				finPftDetail.setTotalpriSchd(resultSet.getBigDecimal("TotalpriSchd")); 
				finPftDetail.setEarlyPaidAmt(resultSet.getBigDecimal("EarlyPaidAmt")); 
				finPftDetail.setODPrincipal(resultSet.getBigDecimal("ODPrincipal")); 
				finPftDetail.setODProfit(resultSet.getBigDecimal("ODProfit")); 
				finPftDetail.setPenaltyPaid(resultSet.getBigDecimal("PenaltyPaid")); 
				finPftDetail.setPenaltyDue(resultSet.getBigDecimal("PenaltyDue")); 
				finPftDetail.setPenaltyWaived(resultSet.getBigDecimal("PenaltyWaived")); 
				finPftDetail.setNSchdDate(resultSet.getDate("NSchdDate")); 
				finPftDetail.setNSchdPri(resultSet.getBigDecimal("NSchdPri")); 
				finPftDetail.setNSchdPft(resultSet.getBigDecimal("NSchdPft")); 
				finPftDetail.setNSchdPriDue(resultSet.getBigDecimal("NSchdPriDue")); 
				finPftDetail.setNSchdPftDue(resultSet.getBigDecimal("NSchdPftDue")); 
				finPftDetail.setAccruePft(resultSet.getBigDecimal("AccruePft")); 
				finPftDetail.setEarnedPft(resultSet.getBigDecimal("EarnedPft")); 
				finPftDetail.setUnearned(resultSet.getBigDecimal("Unearned")); 
				finPftDetail.setPftInSusp(resultSet.getBoolean("PftInSusp")); 
				finPftDetail.setSuspPft(resultSet.getBigDecimal("SuspPft")); 
				finPftDetail.setPftAccrueTsfd(resultSet.getBigDecimal("PftAccrueTsfd")); 
				finPftDetail.setFinStatus(resultSet.getString("FinStatus")); 
				finPftDetail.setFinStsReason(resultSet.getString("FinStsReason")); 
				finPftDetail.setFinWorstStatus(resultSet.getString("FinWorstStatus")); 
				finPftDetail.setTAKAFULPaidAmt(resultSet.getBigDecimal("TAKAFULPaidAmt")); 
				finPftDetail.setAdminPaidAmt(resultSet.getBigDecimal("AdminPaidAmt")); 
				finPftDetail.setTAKAFULInsCal(resultSet.getBigDecimal("TAKAFULInsCal")); 
				finPftDetail.setNOInst(resultSet.getLong("NOInst")); 
				finPftDetail.setNOPaidInst(resultSet.getLong("NOPaidInst")); 
				finPftDetail.setNOODInst(resultSet.getLong("NOODInst"));
				finPftDetail.setFinAccount(resultSet.getString("FinAccount"));
				finPftDetail.setFinAcType(resultSet.getString("FinAcType"));
				finPftDetail.setDisbAccountId(resultSet.getString("DisbAccountId"));
				finPftDetail.setDisbActCcy(resultSet.getString("DisbActCcy"));
				finPftDetail.setRepayAccountId(resultSet.getString("RepayAccountId"));
				finPftDetail.setFinCustPftAccount(resultSet.getString("FinCustPftAccount"));
				finPftDetail.setIncomeAccount(resultSet.getString("IncomeAccount"));
				finPftDetail.setUEIncomeSuspAccount(resultSet.getString("UEIncomeSuspAccount"));
				finPftDetail.setFinCommitmentRef(resultSet.getString("FinCommitmentRef"));
				finPftDetail.setFinIsActive(resultSet.getBoolean("FinIsActive"));
				finPftDetail.setNORepayments(resultSet.getLong("NORepayments"));
				finPftDetail.setFirstRepayDate(resultSet.getDate("FirstRepayDate"));
				finPftDetail.setFirstRepayAmt(resultSet.getBigDecimal("FirstRepayAmt")); 
				finPftDetail.setLastRepayAmt(resultSet.getBigDecimal("LastRepayAmt"));
				finPftDetail.setoDDays(resultSet.getInt("ODDays"));
				finPftDetail.setFirstODDate(resultSet.getDate("FirstODDate")); 
				finPftDetail.setLastODDate(resultSet.getDate("LastODDate")); 
				finPftDetail.setClosingStatus(resultSet.getString("ClosingStatus"));
				finPftDetail.setFinCategory(resultSet.getString("FinCategory"));
				finPftDetail.setLastRpySchDate(resultSet.getDate("LastRpySchDate"));
				finPftDetail.setNextRpySchDate(resultSet.getDate("NextRpySchDate"));
				finPftDetail.setLastRpySchPri(resultSet.getBigDecimal("LastRpySchPri"));
				finPftDetail.setLastRpySchPft(resultSet.getBigDecimal("LastRpySchPft"));
				finPftDetail.setLatestRpyDate(resultSet.getDate("LatestRpyDate"));
				finPftDetail.setLatestRpyPri(resultSet.getBigDecimal("LatestRpyPri"));
				finPftDetail.setLatestRpyPft(resultSet.getBigDecimal("LatestRpyPft"));
				finPftDetail.setLatestWriteOffDate(resultSet.getDate("LatestWriteOffDate"));
				finPftDetail.setTotalWriteoff(resultSet.getBigDecimal("TotalWriteoff"));
				
				if(context != null) {
					BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(resultSet.getRow()));
				} else {
					exeStatus.setProcessedCount(resultSet.getRow());
				}
				
				finPftDetails.add(finPftDetail);
				if(finPftDetails.size() == Integer.parseInt(SystemParameterDetails.getSystemParameterValue("UPLOAD_PFT_DTL_COUNT").toString())) { 
					getUploadFinProfitDetailsProcess().doUploadPftDetails(finPftDetails, isFirstCall);

					isFirstCall = false;
					finPftDetails.clear();
				}
			}

			if(!finPftDetails.isEmpty()) { 
				getUploadFinProfitDetailsProcess().doUploadPftDetails(finPftDetails, isFirstCall);
				finPftDetails.clear();
			}

		}catch (Exception e) {
			logger.error(e);
			throw e;
		} finally {
			if(resultSet != null) {
				resultSet.close();
			}

			if(statement != null) {
				statement.close();
			}

			finPftDetail = null;
		}
		System.out.println("Completed Time: "+ DateUtility.formateDate(new Date(System.currentTimeMillis()), PennantConstants.timeFormat));
		logger.info("Leaving");
	}

	private String getQuery() {
		StringBuilder selectSql = new StringBuilder(" SELECT FinReference, CustCIF, FinBranch, FinType,"); 
		selectSql.append(" LastMdfDate, TotalPftSchd, TotalPftCpz, TotalPftPaid,"); 
		selectSql.append(" TotalPftBal, TotalPftPaidInAdv, TotalPriPaid, TotalPriBal,");
		selectSql.append(" TdSchdPft, TdPftCpz, TdSchdPftPaid, TdSchdPftBal,");
		selectSql.append(" TdPftAccrued, TdPftAccrueSusp,  TdPftAmortized, TdPftAmortizedSusp,");
		selectSql.append(" TdSchdPri, TdSchdPriPaid, TdSchdPriBal, AcrTillNBD, AcrTillLBD,");
		selectSql.append(" AcrTodayToNBD, AmzTillNBD, AmzTillLBD, AmzTodayToNBD,");
		selectSql.append(" RepayFrq, FinCcy, FinPurpose, FinContractDate,");
		selectSql.append(" FinApprovedDate, FinStartDate, MaturityDate,FullPaidDate,");
		selectSql.append(" FinAmount, DownPayment, CurReducingRate, curFlatRate,");
		selectSql.append(" TotalpriSchd, EarlyPaidAmt, ODPrincipal, ODProfit,");
		selectSql.append(" PenaltyPaid, PenaltyDue, PenaltyWaived, NSchdDate,");
		selectSql.append(" NSchdPri, NSchdPft, NSchdPriDue, NSchdPftDue,");
		selectSql.append(" AccruePft, EarnedPft,Unearned, PftInSusp, SuspPft,");
		selectSql.append(" PftAccrueTsfd, FinStatus, FinStsReason,");
		selectSql.append(" FinWorstStatus, TAKAFULPaidAmt, AdminPaidAmt,"); 
		selectSql.append(" TAKAFULInsCal, NOInst, NOPaidInst, NOODInst, FinAccount,");
		selectSql.append(" FinAcType, DisbAccountId, DisbActCcy, RepayAccountId,");
		selectSql.append(" FinCustPftAccount,IncomeAccount, UEIncomeSuspAccount,");
		selectSql.append(" FinCommitmentRef, FinIsActive, NORepayments, FirstRepayDate,");
		selectSql.append(" FirstRepayAmt, LastRepayAmt, ODDays, FirstODDate, LastODDate,");
		selectSql.append(" ClosingStatus, FinCategory, LastRpySchDate, NextRpySchDate, LastRpySchPri, LastRpySchPft, ");
		selectSql.append(" LatestRpyDate, LatestRpyPri, LatestRpyPft, LatestWriteOffDate, TotalWriteoff ");
		selectSql.append(" FROM  FinPftDetails ");

		logger.debug("selectSql: " + selectSql.toString());

		return selectSql.toString();
	}
	
	private String getCountQuery() {
		StringBuilder selectSql = new StringBuilder(" SELECT count(FinReference) FROM  FinPftDetails ");

		logger.debug("selectSql: " + selectSql.toString());

		return selectSql.toString();
	}

	public UploadFinProfitDetailsProcess getUploadFinProfitDetailsProcess() {
		return uploadFinProfitDetailsProcess;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setUploadFinProfitDetailsProcess(
			UploadFinProfitDetailsProcess uploadFinProfitDetailsProcess) {
		this.uploadFinProfitDetailsProcess = uploadFinProfitDetailsProcess;
	}

	public static BigDecimal getAS400Date(String currentDate) {
		BigDecimal as400Date = null;
		BigDecimal dateInt = null;

		if (currentDate == null)
			return null;

		if (!currentDate.trim().equals("")) {
			dateInt = new BigDecimal(currentDate.substring(6, 10)+ currentDate.substring(3, 5) + currentDate.substring(0, 2));
			as400Date = new BigDecimal(19000000).subtract(dateInt);
			as400Date = new BigDecimal(-1).multiply(as400Date);
		} else
			as400Date = null;

		return as400Date;

	}

}
