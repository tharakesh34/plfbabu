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
 * FileName    		:  UploadFinPftDetailServiceImpl.java									*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.app.eod.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.eod.service.UploadFinPftDetailService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ExecutionStatus;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.model.EodFinProfitDetail;
import com.pennant.coreinterface.process.UploadProfitDetailProcess;

public class UploadFinPftDetailServiceImpl implements UploadFinPftDetailService {
	private Logger logger = Logger.getLogger(UploadFinPftDetailServiceImpl.class);
	private DataSource dataSource;	
	private UploadProfitDetailProcess uploadProfitDetailProcess;

	public UploadFinPftDetailServiceImpl() {
		super();
	}
	
    @Override
	public void doUploadPftDetails(Object object) throws Exception{
		logger.info("Entering");
		List<EodFinProfitDetail> finPftDetails = new ArrayList<EodFinProfitDetail>();

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		EodFinProfitDetail finPftDetail = null;
		boolean isFirstCall = true;
		logger.info("Start Time: " + DateUtility.getSysDate());
		
		ChunkContext context = null;
		ExecutionStatus exeStatus = null;
		if(object instanceof ChunkContext) {
			context = (ChunkContext)object;
		} else {
			exeStatus = (ExecutionStatus)object;
		}

		try{
			
			connection = DataSourceUtils.doGetConnection(this.dataSource);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(getCountQuery());
			
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			
			if(exeStatus != null) {
				exeStatus.setActualCount(count);
			} else {
				BatchUtil.setExecution(context,  "TOTAL", String.valueOf(count));
			}
			statement.close();
			resultSet.close();
			
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
				finPftDetail.setCRBODPrincipal(resultSet.getBigDecimal("CRBODPrincipal")); 
				finPftDetail.setCRBODProfit(resultSet.getBigDecimal("CRBODProfit")); 
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
				finPftDetail.setInsPaidAmt(resultSet.getBigDecimal("InsPaidAmt")); 
				finPftDetail.setAdminPaidAmt(resultSet.getBigDecimal("AdminPaidAmt")); 
				finPftDetail.setInsCal(resultSet.getBigDecimal("InsCal")); 
				finPftDetail.setNOInst(resultSet.getLong("NOInst")); 
				finPftDetail.setNOPaidInst(resultSet.getLong("NOPaidInst")); 
				finPftDetail.setNOODInst(resultSet.getLong("NOODInst"));
				finPftDetail.setCRBODInst(resultSet.getInt("CRBODInst"));
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
				finPftDetail.setCRBODDays(resultSet.getInt("CRBODDays"));
				finPftDetail.setFirstODDate(resultSet.getDate("FirstODDate")); 
				finPftDetail.setLastODDate(resultSet.getDate("LastODDate")); 
				finPftDetail.setCRBFirstODDate(resultSet.getDate("CRBFirstODDate")); 
				finPftDetail.setCRBLastODDate(resultSet.getDate("CRBLastODDate")); 
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
				
				finPftDetail.setSalariedCustomer(StringUtils.trimToEmpty(resultSet.getString("CustEmpSts")).equals(PennantConstants.CUSTEMPSTS_EMPLOYED));
				if(context != null) {
					BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(resultSet.getRow()));
				} else {
					exeStatus.setProcessedCount(resultSet.getRow());
				}
				
				finPftDetails.add(finPftDetail);
				// Number of finances to be uploaded for each call to AS400 is dependent on the array size mentioned in the AS400 program PTPFF21R 
				if(finPftDetails.size() == 80) { 
					getUploadProfitDetailProcess().doUploadPftDetails(finPftDetails, isFirstCall);

					isFirstCall = false;
					finPftDetails.clear();
				}
			}

			if(!finPftDetails.isEmpty()) { 
				getUploadProfitDetailProcess().doUploadPftDetails(finPftDetails, isFirstCall);
				finPftDetails.clear();
			}

		}catch (Exception e) {
			logger.error("Exception: ", e);
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

		logger.info("Completed Time: " + DateUtility.getSysDate());
		logger.info("Leaving");
	}

	private String getQuery() {
		
		StringBuilder selectSql = new StringBuilder(" SELECT T1.FinReference, T1.CustCIF, T1.FinBranch, T1.FinType,"); 
		selectSql.append(" T1.LastMdfDate, T1.TotalPftSchd, T1.TotalPftCpz, T1.TotalPftPaid,"); 
		selectSql.append(" T1.TotalPftBal, T1.TotalPftPaidInAdv, T1.TotalPriPaid, T1.TotalPriBal,");
		selectSql.append(" T1.TdSchdPft, T1.TdPftCpz, T1.TdSchdPftPaid, T1.TdSchdPftBal,");
		selectSql.append(" T1.TdPftAccrued, T1.TdPftAccrueSusp, T1.TdPftAmortized, T1.TdPftAmortizedSusp,");
		selectSql.append(" T1.TdSchdPri, T1.TdSchdPriPaid, T1.TdSchdPriBal, T1.AcrTillNBD, T1.AcrTillLBD,");
		selectSql.append(" T1.AcrTodayToNBD, T1.AmzTillNBD, T1.AmzTillLBD, T1.AmzTodayToNBD,");
		selectSql.append(" T1.RepayFrq, T1.FinCcy, T1.FinPurpose, T1.FinContractDate,");
		selectSql.append(" T1.FinApprovedDate, T1.FinStartDate, T1.MaturityDate, T1.FullPaidDate,");
		selectSql.append(" T1.FinAmount, T1.DownPayment, T1.CurReducingRate, T1.curFlatRate,");
		selectSql.append(" T1.TotalpriSchd, T1.EarlyPaidAmt, T1.ODPrincipal, T1.ODProfit,T1.CRBODPrincipal, T1.CRBODProfit,");
		selectSql.append(" T1.PenaltyPaid, T1.PenaltyDue, T1.PenaltyWaived, T1.NSchdDate,");
		selectSql.append(" T1.NSchdPri, T1.NSchdPft, T1.NSchdPriDue, T1.NSchdPftDue,");
		selectSql.append(" T1.AccruePft, T1.EarnedPft, T1.Unearned, T1.PftInSusp, T1.SuspPft,");
		selectSql.append(" T1.PftAccrueTsfd, T1.FinStatus, T1.FinStsReason,");
		selectSql.append(" T1.FinWorstStatus, T1.InsPaidAmt, T1.AdminPaidAmt,"); 
		selectSql.append(" T1.InsCal, T1.NOInst, T1.NOPaidInst, T1.NOODInst,T1.CRBODInst, T1.FinAccount,");
		selectSql.append(" T1.FinAcType, T1.DisbAccountId, T1.DisbActCcy, T1.RepayAccountId,");
		selectSql.append(" T1.FinCustPftAccount, T1.IncomeAccount, T1.UEIncomeSuspAccount,");
		selectSql.append(" T1.FinCommitmentRef, T1.FinIsActive, T1.NORepayments, T1.FirstRepayDate,");
		selectSql.append(" T1.FirstRepayAmt, T1.LastRepayAmt,T1.CRBODDays, T1.ODDays, T1.FirstODDate, T1.LastODDate,T1.CRBFirstODDate, T1.CRBLastODDate, ");
		selectSql.append(" T1.ClosingStatus, T1.FinCategory, T1.LastRpySchDate, T1.NextRpySchDate, T1.LastRpySchPri, T1.LastRpySchPft,");
		selectSql.append(" T1.LatestRpyDate, T1.LatestRpyPri, T1.LatestRpyPft, T1.LatestWriteOffDate, T1.TotalWriteoff , ");
		selectSql.append(" T2.CustEmpSts");
		selectSql.append(" FROM  FinPftDetails T1 INNER JOIN Customers T2 ON  T1.CustCIF = T2.CustCIF");


		logger.debug("selectSql: " + selectSql.toString());

		return selectSql.toString();
	}
	
	private String getCountQuery() {
		StringBuilder selectSql = new StringBuilder(" SELECT count(FinReference) FROM  FinPftDetails ");

		logger.debug("selectSql: " + selectSql.toString());

		return selectSql.toString();
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public UploadProfitDetailProcess getUploadProfitDetailProcess() {
		return uploadProfitDetailProcess;
	}
	public void setUploadProfitDetailProcess(UploadProfitDetailProcess uploadProfitDetailProcess) {
		this.uploadProfitDetailProcess = uploadProfitDetailProcess;
	}

}
