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
 * FileName    		:  RepayQueuePostings.java													*                           
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
package com.pennant.backend.endofday.repayqueue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.OverDueRecoveryPostingsUtil;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.SuspensePostingUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.eod.util.EODProperties;

public class RepayQueuePostings implements Tasklet {
	
	private Logger logger = Logger.getLogger(RepayQueuePostings.class);

	private FinanceProfitDetailDAO profitDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinStatusDetailDAO finStatusDetailDAO;
	private CustomerStatusCodeDAO customerStatusCodeDAO;
	private RepaymentPostingsUtil postingsUtil;
	private OverDueRecoveryPostingsUtil recoveryPostingsUtil;	
	private AccountInterfaceService accountInterfaceService;
	private SuspensePostingUtil suspensePostingUtil;
	private FinODDetailsDAO finODDetailsDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private FinLogEntryDetailDAO finLogEntryDetailDAO; 
	private FinRepayQueueDAO finRepayQueueDAO;
	
	private DataSource dataSource;
	
	private Date dateValueDate = null;
	private Date dateNextBusinessDate = null;
	
	private BigDecimal repayAmountBal;
	
	int repayments = 0;
	int overDues = 0;
	int suspenses = 0;
	int processed = 0;
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
		dateNextBusinessDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_NEXT_BUS_DATE").toString());

		logger.debug("START: Repayments Due Today Queue Postings for Value Date: " + dateValueDate);
		
		// FETCH Finance Repayment Queues
		Connection connection = null;
		ResultSet resultSet = null;
		ResultSet rs = null;
		PreparedStatement sqlStatement = null;
		
		FinRepayQueue finRepayQueue = null;
		FinanceMain financeMain = null;
		FinanceType financeType = null;
		
		try {
			
			//Remove Account Holds before Repayments Process
			logger.debug("START: Remove Account Holds for Value Date: " + dateValueDate);
			getAccountInterfaceService().removeAccountHolds();
			logger.debug("END: Remove Account Holds for Value Date: " + dateValueDate);
			
			//Finance Repayments Details
			connection = DataSourceUtils.doGetConnection(getDataSource());	
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = sqlStatement.executeQuery();
			resultSet.next();
			BatchUtil.setExecution(context, "TOTAL", String.valueOf(resultSet.getInt(1)));
			
			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = sqlStatement.executeQuery();
			
			while (resultSet.next()) {
				
				financeType = EODProperties.getFinanceType(resultSet.getString("FinType").trim());
				
				//Prepare Finance Main Object data with Reference to Finance Reference
				sqlStatement = connection.prepareStatement(prepareFinanceQuery());
				sqlStatement.setString(1, resultSet.getString("FinReference"));
				rs = sqlStatement.executeQuery();
				while (rs.next()) {
					financeMain = doWriteDataToBean(financeMain, rs);
				}
				
				// Prepare Finance RepayQueue Data
				finRepayQueue = doWriteDataToBean(finRepayQueue, resultSet);
				
				boolean allowRIAInvestment = financeType.isAllowRIAInvestment();
				String finReference = finRepayQueue.getFinReference();
				repayAmountBal = BigDecimal.ZERO;
				
				//Check whether Schedule came for Updation of Flags or for Repayments
				if(resultSet.getBigDecimal("RepayQueueBal").compareTo(BigDecimal.ZERO) == 0){
					getPostingsUtil().updateSchdlDetail(finRepayQueue);
					continue;
				}
				
				long linkedTranId = Long.MIN_VALUE;
				
				//Repayments Only for "AUTO" Payment Finances
				String repayMethod = resultSet.getString("FinRepayMethod");
				boolean doProcessPostings = false;
				if(StringUtils.trimToEmpty(repayMethod).equals(PennantConstants.REPAYMTH_AUTO)){
					doProcessPostings = true;
				}

				//Overdue Recovery Calculations & Recovery Posting
				if(finRepayQueue.getRpyDate().compareTo(dateValueDate) < 0){
					List<Object> odObjDetails = getRecoveryPostingsUtil().recoveryProcess(financeMain, finRepayQueue, 
							dateValueDate, allowRIAInvestment, doProcessPostings, false, linkedTranId, financeType.getFinDivision());

					if(odObjDetails!=null && !odObjDetails.isEmpty()) {
						if(!doProcessPostings || (Boolean)odObjDetails.get(0)) {
							overDues++;
						}else{
							//THROW ERROR FOR POSTINGS NOT SUCCESS
							logger.error("Postings Failed for OverDue Payment Process");
							//throw new Exception("Postings Failed for OverDue Payment Process");
						}
						
						if(doProcessPostings){
							linkedTranId = (Long) odObjDetails.get(1);
						}
					}
					odObjDetails = null;
				}
				
				//Repayments postings By Available Balance Check
				if (doProcessPostings && !StringUtils.trimToEmpty(financeMain.getRepayAccountId()).equals("") && 
						checkPaymentBalance(financeType.isFinIsAlwPartialRpy(), resultSet.getBigDecimal("RepayQueueBal"), 
								financeMain.getRepayAccountId(), financeType.getFinDivision())) {
					
					//Remove Below line for Single Transaction Posting Entry
					linkedTranId = Long.MIN_VALUE;

					// Get Schedule details List Data
					List<FinanceScheduleDetail> scheduleDetails = getFinanceScheduleDetailDAO().getFinSchdDetailsForBatch(finReference);

					// Get ProfitDetails based on Finance reference
					FinanceProfitDetail pftDetail = new FinanceProfitDetail();
					pftDetail.setFinReference(finReference);
					pftDetail.setAcrTillLBD(resultSet.getBigDecimal("AcrTillLBD"));
					pftDetail.setTdPftAmortizedSusp(resultSet.getBigDecimal("TdPftAmortizedSusp"));
					pftDetail.setAmzTillLBD(resultSet.getBigDecimal("AmzTillLBD"));

					List<Object> returnList = getPostingsUtil().postingsEODRepayProcess(financeMain, scheduleDetails, pftDetail, 
							dateValueDate, finRepayQueue, repayAmountBal, allowRIAInvestment, linkedTranId);
					
					if(!(Boolean)returnList.get(0)){
						//THROW ERROR FOR POSTINGS NOT SUCCESS
						logger.error("Postings Failed for Repayment Process: "+returnList.get(1));
						//throw new Exception("Postings Failed for Repayments Process: "+returnList.get(1));
					}
					
					//Create log entry for Action for Schedule Repayments Modification
					FinLogEntryDetail entryDetail = new FinLogEntryDetail();
					entryDetail.setFinReference(finReference);
					entryDetail.setEventAction(PennantConstants.SCH_REPAY);
					entryDetail.setSchdlRecal(false);
					entryDetail.setPostDate(dateValueDate);
					entryDetail.setReversalCompleted(false);
					getFinLogEntryDetailDAO().save(entryDetail);
					
					repayments++;
					
					scheduleDetails = null;
				} else {
					
					if (finRepayQueue.getRpyDate().compareTo(dateValueDate) <= 0) {
						
						//Overdue Details preparation 
						getRecoveryPostingsUtil().overDueDetailPreparation(finRepayQueue, financeMain.getProfitDaysBasis(), dateValueDate, true);
						
						// Finance Suspense
						List<Object> returnList = getSuspensePostingUtil().suspensePreparation(financeMain, finRepayQueue, dateValueDate, allowRIAInvestment, false);
						
						if(!(Boolean)returnList.get(0)){
							//THROW ERROR FOR POSTINGS NOT SUCCESS
							logger.error("Postings Failed for Suspense Preparation Process : "+returnList.get(2));
							//throw new Exception("Postings Failed for Suspense Preparation Process : "+returnList.get(2));
						}
						
						boolean isDueSuspNow = (Boolean) returnList.get(1);
						//Check Current Finance Max Status For updation
						String curFinStatus = getCustomerStatusCodeDAO().getFinanceStatus(financeMain.getFinReference(), true);
						String finStsReason = financeMain.getFinStsReason();
						boolean isStsChanged = false;
						
						if(!financeMain.getFinStatus().equals(curFinStatus)){
							isStsChanged = true;
						}
						
						// Finance Main Details Update
						financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount());
						
						if (isStsChanged) {
							
							BigDecimal feeChargeAmt = financeMain.getFeeChargeAmt() == null? BigDecimal.ZERO : financeMain.getFeeChargeAmt();
							getFinanceMainDAO().updateRepaymentAmount(financeMain.getFinReference(), financeMain.getFinAmount().add(feeChargeAmt), 
									financeMain.getFinRepaymentAmount(), curFinStatus, PennantConstants.FINSTSRSN_SYSTEM,false);
						}
						
						//Finance Status Details insertion, if status modified then change to High Risk Level
						if(isStsChanged){
							FinStatusDetail statusDetail = new FinStatusDetail();
							statusDetail.setFinReference(financeMain.getFinReference());
							statusDetail.setValueDate(dateValueDate);
							statusDetail.setCustId(financeMain.getCustID());
							statusDetail.setFinStatus(curFinStatus);			
							statusDetail.setFinStatusReason(finStsReason);			
							
							getFinStatusDetailDAO().saveOrUpdateFinStatus(statusDetail);
						}
						
						if(isDueSuspNow) {
							suspenses++;
						}
					}
				}
				processed = resultSet.getRow();
				BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(processed));
				BatchUtil.setExecution(context,  "INFO", getInfo());
				
				financeMain = null;
				finRepayQueue = null;
			}
			
			//Customer Statuses Updation depends on Finance Status
			List<FinStatusDetail> custStatuses = getFinStatusDetailDAO().getFinStatusDetailList(dateValueDate);
			if(custStatuses != null && custStatuses.size() > 0){
				
				//Customer Status Date Updation
				List<Long> custIdList = new ArrayList<Long>();
				for (FinStatusDetail finSts : custStatuses) {
					custIdList.add(finSts.getCustId());
				}
				
				List<FinStatusDetail> suspDateStsList = getFinanceSuspHeadDAO().getCustSuspDate(custIdList);
				Map<Long, Date> suspDateMap = new HashMap<Long, Date>();
				for (FinStatusDetail suspDatests : suspDateStsList) {
					suspDateMap.put(suspDatests.getCustId(), suspDatests.getValueDate());
				}
				
				for (FinStatusDetail finSts : custStatuses) {
					if(suspDateMap.containsKey(finSts.getCustId())){
						finSts.setValueDate(suspDateMap.get(finSts.getCustId()));
					}else{
						finSts.setValueDate(null);
					}
				}
				
				getFinStatusDetailDAO().updateCustStatuses(custStatuses);
				
				custIdList = null;
				suspDateStsList = null;
				custStatuses = null;
				suspDateMap = null;
			}
			
			BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(processed));
			BatchUtil.setExecution(context,  "INFO", getInfo());
			
			//Process Account Holds only If date Value Date is Same As Next Bussiness Date
			Date futureValueDate = DateUtility.addDays(dateValueDate, 1);
			if(dateNextBusinessDate.compareTo(futureValueDate) == 0){
				
				//Adding Holdings To Accounts After Repayments Process
				List<AccountHoldStatus> accountsList = getFinODDetailsDAO().getFinODAmtByRepayAc(dateValueDate);
				List<AccountHoldStatus> returnAcList = null;
				if(!accountsList.isEmpty()){

					logger.debug("START: Adding Account Holds for Value Date: " + dateValueDate);

					returnAcList = new ArrayList<AccountHoldStatus>();

					//Sending 2000 Records At a time to Process for Holding
					while (!accountsList.isEmpty()) {

						List<AccountHoldStatus> subAcList = null;
						if(accountsList.size() > 2000){
							subAcList = accountsList.subList(0, 2000);
						}else{
							subAcList = accountsList;
						}
						returnAcList.addAll(getAccountInterfaceService().addAccountHolds(subAcList,dateValueDate));

						if(accountsList.size() > 2000){
							accountsList.subList(0, 2000).clear();
						}else{
							accountsList.clear();
						}
					}

					//Save Returned Account List For Report Purpose
					getFinODDetailsDAO().saveHoldAccountStatus(returnAcList);

					logger.debug("END: Adding Account Holds for Value Date: " + dateValueDate);
				}
			}
			
			//Clear Repay Queue Details
			getFinRepayQueueDAO().deleteRepayQueue();
			
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}  finally {
			financeMain = null;
			finRepayQueue = null;
			
			if(rs != null) {
				rs.close();
			}
			
			if(resultSet!= null) {
				resultSet.close();
			}
			
			if(sqlStatement != null) {
				sqlStatement.close();
			}
		}

		logger.debug("END: Repayments Due Today Queue Postings for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;

	}
	

	/**
	 * Method for get count of Repay postings
	 * @return selQuery 
	 */
	private String getCountQuery() {

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT count(1)");
		selectSql.append(" FROM Financemain FM ");
		selectSql.append(" INNER JOIN FinRpyQueue RQ ON RQ.FinReference = FM.FinReference ");
		selectSql.append(" INNER JOIN FinPftDetails PD ON PD.FinReference = FM.FinReference ");
		selectSql.append(" WHERE RQ.RpyDate <= ? AND (SchdIsPftPaid = 0 OR SchdIsPriPaid = 0)");
		return selectSql.toString();
	}

	/**
	 * Method for Preparation of Select Query for Preparing resultSet
	 * 
	 * @param selectSql
	 * @return
	 */
	private String prepareSelectQuery() {

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RQ.FinReference, RQ.FinType, RQ.RpyDate, RQ.FinPriority, RQ.Branch, " );
		selectSql.append(" RQ.CustomerID, RQ.FinRpyFor, RQ.SchdPft, RQ.SchdPri, RQ.SchdPftPaid, RQ.SchdPriPaid, " );
		selectSql.append(" RQ.SchdPftBal, RQ.SchdPriBal, RQ.SchdIsPftPaid, RQ.SchdIsPriPaid, " );
		selectSql.append(" (RQ.SchdPftBal+ RQ.SchdPriBal) AS RepayQueueBal, PD.AcrTillLBD, PD.TdPftAmortizedSusp, PD.AmzTillLBD " );
		selectSql.append(" FROM FinRpyQueue RQ  INNER JOIN FinPftDetails PD ON PD.FinReference = RQ.FinReference " );
		selectSql.append(" WHERE RQ.RpyDate <= ? AND (SchdIsPftPaid = 0 OR SchdIsPriPaid = 0) " );
		selectSql.append(" ORDER BY RQ.RpyDate, RQ.FinPriority, RQ.FinReference, RQ.FinRpyFor ASC " );
		return selectSql.toString();
	}
	
	/**
	 * Method for Preparation of Select Query for Preparing resultSet
	 * 
	 * @param selectSql
	 * @return
	 */
	private String prepareFinanceQuery() {
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference, GrcPeriodEndDate, FinRepaymentAmount, DisbAccountId, RepayAccountId, " );
		selectSql.append(" FinAccount, FinCustPftAccount, FinCommitmentRef, FinCcy, FinBranch, CustID, FinAmount, " );
		selectSql.append(" FeeChargeAmt, DownPayment, DownPayBank, DownPaySupl, DownPayAccount, SecurityDeposit, " );
		selectSql.append(" FinType, FinStartDate, NumberOfTerms, GraceTerms,  NextGrcPftDate, NextRepayDate, " );
		selectSql.append(" LastRepayPftDate, NextRepayPftDate, LastRepayRvwDate, NextRepayRvwDate, FinAssetValue, " );
		selectSql.append(" FinCurrAssetValue, RecordType, ProfitDaysBasis, FeeChargeAmt, FinStatus, " );
		selectSql.append(" FinStsReason, MaturityDate, FinRepayMethod  " );
		selectSql.append(" FROM Financemain  WHERE FinReference = ? " );
		return selectSql.toString();
	}

	/**
	 * Method for Creating RepayQueue Object using resultSet
	 * 
	 * @param FinRepayQueue finRepayQueue
	 * @param ResultSet resultSet
	 * @return FinRepayQueue finRepayQueue
	 */
	@SuppressWarnings("serial")
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, ResultSet resultSet) {

		finRepayQueue = new FinRepayQueue();

		try {
			finRepayQueue.setFinReference(resultSet.getString("FinReference"));
			finRepayQueue.setBranch(resultSet.getString("Branch"));
			finRepayQueue.setFinType(resultSet.getString("FinType"));
			finRepayQueue.setCustomerID(resultSet.getLong("CustomerID"));
			finRepayQueue.setRpyDate(resultSet.getDate("RpyDate"));
			finRepayQueue.setFinPriority(resultSet.getInt("FinPriority"));
			finRepayQueue.setFinRpyFor(resultSet.getString("FinRpyFor"));
			finRepayQueue.setSchdPft(resultSet.getBigDecimal("SchdPft"));
			finRepayQueue.setSchdPri(resultSet.getBigDecimal("SchdPri"));
			finRepayQueue.setSchdPftPaid(resultSet.getBigDecimal("SchdPftPaid"));
			finRepayQueue.setSchdPriPaid(resultSet.getBigDecimal("SchdPriPaid"));
			finRepayQueue.setSchdPftBal(resultSet.getBigDecimal("SchdPftBal"));
			finRepayQueue.setSchdPriBal(resultSet.getBigDecimal("SchdPriBal"));
			finRepayQueue.setSchdIsPftPaid(resultSet.getBoolean("SchdIsPftPaid"));
			finRepayQueue.setSchdIsPriPaid(resultSet.getBoolean("SchdIsPriPaid"));

		} catch (SQLException e) {
			throw new DataAccessException(e.getMessage()) { };
		}
		return finRepayQueue;
	}
	
	/**
	 * Method for Creating RepayQueue Object using resultSet
	 * 
	 * @param FinanceMain
	 *            financeMain
	 * @param ResultSet
	 *            resultSet
	 * @return FinanceMain financeMain
	 */
	@SuppressWarnings("serial")
	private FinanceMain doWriteDataToBean(FinanceMain financeMain, ResultSet resultSet) {

		financeMain = new FinanceMain();

		try {
			financeMain.setFinReference(resultSet.getString("FinReference"));
			financeMain.setGrcPeriodEndDate(resultSet.getDate("GrcPeriodEndDate"));
			financeMain.setFinRepaymentAmount(resultSet.getBigDecimal("FinRepaymentAmount"));
			financeMain.setDisbAccountId(resultSet.getString("DisbAccountid"));
			financeMain.setRepayAccountId(resultSet.getString("RepayAccountid"));
			financeMain.setFinAccount(resultSet.getString("FinAccount"));
			financeMain.setFinCustPftAccount(resultSet.getString("FinCustPftAccount"));
			financeMain.setFinCommitmentRef(resultSet.getString("FinCommitmentRef"));
			financeMain.setFinCcy(resultSet.getString("FinCcy"));
			financeMain.setFinBranch(resultSet.getString("Branch"));
			financeMain.setCustID(resultSet.getLong("CustomerID"));
			financeMain.setFinAmount(resultSet.getBigDecimal("FinAmount"));
			financeMain.setFeeChargeAmt(resultSet.getBigDecimal("FeeChargeAmt"));
			financeMain.setDownPayment(resultSet.getBigDecimal("DownPayment"));
			financeMain.setDownPayBank(resultSet.getBigDecimal("DownPayBank"));
			financeMain.setDownPaySupl(resultSet.getBigDecimal("DownPaySupl"));
			financeMain.setDownPayAccount(resultSet.getString("DownPayAccount"));
			financeMain.setSecurityDeposit(resultSet.getBigDecimal("SecurityDeposit"));
			financeMain.setFinType(resultSet.getString("FinType"));
			financeMain.setFinStartDate(resultSet.getDate("FinStartDate"));
			financeMain.setNumberOfTerms(resultSet.getInt("NumberOfTerms"));
			financeMain.setGraceTerms(resultSet.getInt("GraceTerms"));
			financeMain.setNextGrcPftDate(resultSet.getDate("NextGrcPftDate"));
			financeMain.setNextRepayDate(resultSet.getDate("nextRepayDate"));
			financeMain.setLastRepayPftDate(resultSet.getDate("LastRepayPftDate"));
			financeMain.setNextRepayPftDate(resultSet.getDate("NextRepayPftDate"));
			financeMain.setLastRepayRvwDate(resultSet.getDate("LastRepayRvwDate"));
			financeMain.setNextRepayRvwDate(resultSet.getDate("NextRepayRvwDate"));
			financeMain.setFinAssetValue(resultSet.getBigDecimal("FinAssetValue"));
			financeMain.setFinCurrAssetValue(resultSet.getBigDecimal("FinCurrAssetValue"));
			financeMain.setRecordType(resultSet.getString("RecordType"));
			financeMain.setProfitDaysBasis(resultSet.getString("ProfitDaysBasis"));
			financeMain.setFeeChargeAmt(resultSet.getBigDecimal("FeeChargeAmt"));
			financeMain.setFinStatus(resultSet.getString("FinStatus"));
			financeMain.setFinStsReason(resultSet.getString("FinStsReason"));
			financeMain.setMaturityDate(resultSet.getDate("MaturityDate"));

		} catch (SQLException e) {
			throw new DataAccessException(e.getMessage()) { };
		}
		return financeMain;
	}
	
	/**-----------------------------------------------------*/
	//###### Condition for Checking Available Balance ######//
	/**-----------------------------------------------------
	 * @throws AccountNotFoundException */
	
	private boolean checkPaymentBalance(boolean isAlwPartialRpy, BigDecimal repayQueueBal, String repayAccountId, String finDivision) throws AccountNotFoundException {

		boolean isPayNow = false;
		boolean accFound = false;
		repayAmountBal = BigDecimal.ZERO;
		
		String acType = SystemParameterDetails.getSystemParameterValue("ALWFULLPAY_TSR_ACTYPE").toString();
			
		// Check Available Funding Account Balance
		IAccounts iAccount = getAccountInterfaceService().fetchAccountAvailableBal(repayAccountId);

		//Account Type Check
		String[] acTypeList = acType.split(",");
		for (int i = 0; i < acTypeList.length; i++) {
			if(iAccount.getAcType().equals(acTypeList[i].trim())){
				accFound = true;
				break;
			}
		}
		
		// Set Requested Repayments Amount as RepayAmount Balance
		if (iAccount.getAcAvailableBal().compareTo(repayQueueBal) >= 0) {
			repayAmountBal = repayQueueBal;
			isPayNow = true;
		} else if (accFound && StringUtils.trimToEmpty(finDivision).equals(PennantConstants.FIN_DIVISION_TREASURY)) {
			repayAmountBal = repayQueueBal;
			isPayNow = true;
		} else {
			if (isAlwPartialRpy && iAccount.getAcAvailableBal().compareTo(BigDecimal.ZERO) > 0) {
				repayAmountBal = iAccount.getAcAvailableBal();
				isPayNow = true;
			}
		}
		
		iAccount = null;
		return isPayNow;
	}
	
	private String getInfo() {
		StringBuilder builder = new StringBuilder();

		builder.append("Total Repayment's").append(": ").append(repayments);
		builder.append("\n");
		builder.append("Total Overdue's").append(": ").append(overDues);
		builder.append("\n");
		builder.append("Total Suspense's").append(": ").append(suspenses);
		return builder.toString();
	}
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setProfitDetailDAO(FinanceProfitDetailDAO profitDetailDAO) {
		this.profitDetailDAO = profitDetailDAO;
	}
	public FinanceProfitDetailDAO getProfitDetailDAO() {
		return profitDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}
	public FinStatusDetailDAO getFinStatusDetailDAO() {
		return finStatusDetailDAO;
	}

	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
		return customerStatusCodeDAO;
	}
	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public RepaymentPostingsUtil getPostingsUtil() {
		return postingsUtil;
	}
	public void setPostingsUtil(RepaymentPostingsUtil postingsUtil) {
		this.postingsUtil = postingsUtil;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}
	public void setAccountInterfaceService(
			AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}
	
	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
		this.recoveryPostingsUtil = recoveryPostingsUtil;
	}
	public OverDueRecoveryPostingsUtil getRecoveryPostingsUtil() {
		return recoveryPostingsUtil;
	}

	public void setSuspensePostingUtil(SuspensePostingUtil suspensePostingUtil) {
		this.suspensePostingUtil = suspensePostingUtil;
	}
	public SuspensePostingUtil getSuspensePostingUtil() {
		return suspensePostingUtil;
	}
	
	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}
	
	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
		return finLogEntryDetailDAO;
	}
	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}
	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public FinRepayQueueDAO getFinRepayQueueDAO() {
		return finRepayQueueDAO;
	}
	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		this.finRepayQueueDAO = finRepayQueueDAO;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}


}
