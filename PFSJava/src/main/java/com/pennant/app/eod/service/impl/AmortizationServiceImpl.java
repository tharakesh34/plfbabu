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
 * FileName    		:  AmortizationServiceImpl.java											*                           
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.eod.service.AmortizationService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.AccountEngineExecutionRIA;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.ExecutionStatus;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.eod.util.EODProperties;

public class AmortizationServiceImpl implements AmortizationService {
	public final static Logger			logger	= Logger.getLogger(AmortizationServiceImpl.class);
	private DataSource					dataSource;

	private FinanceMainDAO				financeMainDAO;
	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private FinanceProfitDetailDAO		financeProfitDetailDAO;
	private FinanceSuspHeadDAO			financeSuspHeadDAO;
	private AccountEngineExecution		engineExecution;
	private PostingsPreparationUtil		postingsPreparationUtil;
	private FinContributorDetailDAO		finContributorDetailDAO;
	private CustomerStatusCodeDAO		customerStatusCodeDAO;
	private AccountEngineExecutionRIA	engineExecutionRIA;
	private PostingsDAO					postingsDAO;

	public AmortizationServiceImpl() {
		//
	}

	/**
	 * Method for Calculating Accrual Details
	 */
	@Override
	public void doAccrualCalculation(ExecutionStatus exeStatus, Date valueDate) throws Exception {
		logger.info("Entering");

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {

			int processed = 0;

			boolean isMonthEndTsfd = false;
			Date monthEndDate = DateUtility.getMonthEndDate(valueDate);
			if (monthEndDate.compareTo(valueDate) == 0) {
				isMonthEndTsfd = true;
			}

			String type = "";
			valueDate = DateUtility.addDays(valueDate, 1);

			connection = DataSourceUtils.doGetConnection(this.dataSource);
			statement = connection.prepareStatement(getCountQuery());
			statement.setDate(1, DateUtility.getDBDate(valueDate.toString()));
			resultSet = statement.executeQuery();
			int accruals = 0;
			if (resultSet.next()) {
				accruals=resultSet.getInt(1);
			}
			resultSet.close();
			statement.close();

			exeStatus.setActualCount(accruals);

			statement = connection.prepareStatement(getSelectQuery());
			statement.setDate(1, DateUtility.getDBDate(valueDate.toString()));
			resultSet = statement.executeQuery();

			List<FinanceProfitDetail> pftDetailsList = new ArrayList<FinanceProfitDetail>();

			getFinanceProfitDetailDAO().refreshTemp();
			type = "_Temp";

			while (resultSet.next()) {

				String finReference = resultSet.getString("FinReference");

				// get Finance main
				FinanceMain financeMain = getFinanceMainDAO().getFinanceMainForPftCalc(finReference);
				// get Schedule Details
				List<FinanceScheduleDetail> scheduleDetailList = getFinanceScheduleDetailDAO().getFinSchdDetailsForBatch(finReference);

				//call the Calculations
				FinanceProfitDetail finPftDetail = AEAmounts.calProfitDetails(financeMain, scheduleDetailList,null, valueDate);

				finPftDetail.setFinStatus(financeMain.getFinStatus());
				finPftDetail.setFinStsReason(financeMain.getFinStsReason());
				finPftDetail.setFinIsActive(financeMain.isFinIsActive());
				finPftDetail.setClosingStatus(financeMain.getClosingStatus());

				pftDetailsList.add(finPftDetail);
				if (pftDetailsList.size() == 500) {
					update(pftDetailsList, type);
					pftDetailsList.clear();
				}

				financeMain = null;
				scheduleDetailList = null;

				processed = resultSet.getRow();
				exeStatus.setProcessedCount(processed);

			}

			if (pftDetailsList.size() > 0) {
				update(pftDetailsList, type);
				pftDetailsList = null;
			}
			
			statement.close();
			resultSet.close();
			
			if (isMonthEndTsfd) {

				java.sql.Date monthStartDate = DateUtility.getDBDate(DateUtility.getMonthStartDate(valueDate).toString());
				java.sql.Date monthLastDate = DateUtility.getDBDate(DateUtility.getMonthEndDate(valueDate).toString());
				java.sql.Date lastmonthEndDate = DateUtility.addDays(monthStartDate, -1);

				// Set Profit Accrual Transfered to Previous Transfered amount
				// for all matured Finances before Last month
				statement = connection.prepareStatement(resetAccrueTsfd(type));
				statement.setDate(1, lastmonthEndDate);
				statement.executeQuery();
				statement.close();

				// Update Suspense status for all matured finances , even any of
				// the customer finance is in Suspense
				statement = connection.prepareStatement(updateSuspSts(type));
				statement.executeQuery();
				statement.close();

				// Setting Accrue Transfered Amount Values reset to ZERO
				//FIXME: PV 14APR17
				getFinanceProfitDetailDAO().resetAcrTsfdInSusp();

				// Executing Query to prepare Accrual Transfered of this month
				statement = connection.prepareStatement(getSuspAcrTsfdRefDetail());
				statement.setDate(1, monthStartDate);
				statement.setDate(2, monthLastDate);
				resultSet = statement.executeQuery();

				// Preparing List of Data for Accrue Transfered Amount with in
				// Suspense status
				Map<String, BigDecimal> dataMap = new HashMap<String, BigDecimal>();

				String pastFinRef = "";
				BigDecimal pastAcrTsfd = BigDecimal.ZERO;

				while (resultSet.next()) {

					String finReference = resultSet.getString("FinReference");
					if (!finReference.equals(pastFinRef)) {
						pastFinRef = finReference;
						pastAcrTsfd = BigDecimal.ZERO;
					}

					Date rpySchDate = resultSet.getDate("RpySchDate");
					Date curSchDate = resultSet.getDate("CurSchDate");
					Date postDate = resultSet.getDate("PostDate");
					BigDecimal pftPaid = resultSet.getBigDecimal("PftPaid");

					if (rpySchDate.compareTo(curSchDate) == 0) {

						Date prvSchDate = resultSet.getDate("PrvSchDate");
						Date suspDate = resultSet.getDate("SuspDate");

						BigDecimal totalPft = resultSet.getBigDecimal("TotalPft");
						BigDecimal pastAccrued = (totalPft.divide(new BigDecimal(DateUtility.getDaysBetween(curSchDate, prvSchDate)), 0, RoundingMode.HALF_DOWN)).multiply(new BigDecimal(DateUtility.getDaysBetween(prvSchDate, suspDate)));

						if (pastAccrued.compareTo(pastAcrTsfd.add(pftPaid)) >= 0) {
							pastAcrTsfd = pastAcrTsfd.add(pftPaid);
							pftPaid = BigDecimal.ZERO;
						} else {
							pftPaid = pftPaid.subtract(pastAccrued.subtract(pastAcrTsfd));
							pastAcrTsfd = pastAccrued;
						}
					}

					if (postDate.compareTo(monthEndDate) <= 0 && postDate.compareTo(monthStartDate) >= 0) {
						if (dataMap.containsKey(finReference)) {
							pftPaid = pftPaid.add(dataMap.get(finReference));
							dataMap.remove(finReference);
						}
						dataMap.put(finReference, pftPaid);
					}
				}
				
				statement.close();
				resultSet.close();

				// Update Accrue Transfered amount this month which are in
				// Suspense status
				if (dataMap != null && !dataMap.isEmpty()) {

					List<AccountHoldStatus> list = new ArrayList<AccountHoldStatus>();
					List<String> finRefList = new ArrayList<String>(dataMap.keySet());
					for (int i = 0; i < finRefList.size(); i++) {
						AccountHoldStatus status = new AccountHoldStatus();
						status.setAccount(finRefList.get(i));
						status.setCurODAmount(dataMap.get(finRefList.get(i)));

						list.add(status);

						if (list.size() == 500) {
							//FIXME: 14APR17
							getFinanceProfitDetailDAO().updateAcrTsfdInSusp(list);
							list.clear();
						}
					}
					finRefList = null;
					if (list.size() > 0) {
						//FIXME: 14APR17
						getFinanceProfitDetailDAO().updateAcrTsfdInSusp(list);
						list = null;
					}
				}
				dataMap = null;

				// Adding Monthly Accrual Profit Transfers and Depreciation
				// Values
				getFinanceProfitDetailDAO().saveAccumulates(valueDate);

				// Update Transfered Month End Accrual Amount
				statement = connection.prepareStatement(updateAccrueTsfdQuery(type));
				statement.setDate(1, monthStartDate);
				statement.setDate(2, monthLastDate);
				statement.executeUpdate();

			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {

			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
		}
		logger.info("Leaving");
	}

	private void update(List<FinanceProfitDetail> pftDetailsList, String type) {
		getFinanceProfitDetailDAO().update(pftDetailsList, type);
	}

	@Override
	public void doAccrualPosting(ExecutionStatus exeStatus, Date valueDate, String postBranch) throws Exception {
		logger.info("Entering");

		Date dateAppDate = DateUtility.getAppDate();

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		FinanceProfitDetail pftDetail = null;

		try {
			int processed = 0;

			String type = "_Temp";
			connection = DataSourceUtils.doGetConnection(this.dataSource);
			preparedStatement = connection.prepareStatement(prepareCountQuery(type));
			preparedStatement.setDate(1, DateUtility.getDBDate(valueDate.toString()));
			resultSet = preparedStatement.executeQuery();

			int accruals = 0;
			if (resultSet.next()) {
				accruals=resultSet.getInt(1);
			}

			exeStatus.setActualCount(accruals);
			resultSet.close();
			preparedStatement.close();

			preparedStatement = connection.prepareStatement(prepareSelectQuery(type));
			preparedStatement.setDate(1, DateUtility.getDBDate(valueDate.toString()));
			resultSet = preparedStatement.executeQuery();

			List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
			List<FinanceProfitDetail> pftDetailList = new ArrayList<FinanceProfitDetail>();

			getPostingsDAO().deleteAll(type);

			String finReference = "";
			FinanceType financeType = null;
			long linkedTranId = Long.MIN_VALUE;

			while (resultSet.next()) {

				finReference = resultSet.getString("FinReference");

				// Amount Codes preparation using FinProfitDetails
				AEAmountCodes amountCodes = new AEAmountCodes();
				amountCodes.setFinReference(finReference);
				amountCodes.setDAccrue(resultSet.getBigDecimal("PftAccrued").subtract(resultSet.getBigDecimal("AcrTillLBD")));
				amountCodes.setPft(resultSet.getBigDecimal("TotalPftSchd").add(resultSet.getBigDecimal("TotalPftCpz")));
				amountCodes.setPftAB(resultSet.getBigDecimal("TotalPftBal"));
				amountCodes.setPftAP(resultSet.getBigDecimal("TotalPftPaid"));
				amountCodes.setPftS(resultSet.getBigDecimal("TdSchdPft").add(resultSet.getBigDecimal("TdPftCpz")));
				amountCodes.setPftSB(resultSet.getBigDecimal("TdSchdPftBal"));
				amountCodes.setPftSP(resultSet.getBigDecimal("TdSchdPftPaid"));
				amountCodes.setAccrueTsfd(resultSet.getBigDecimal("pftAccrued").subtract(resultSet.getBigDecimal("PftAccrueSusp")));// Distributed
																																		// Accrual
																																		// Amount

				// **** Accounting Set Execution for Amortization ******//
				// Get Event From Finance Suspend Headers for Accounting Set
				financeType = EODProperties.getFinanceType(resultSet.getString("FinType").trim());
				String eventCode = AccountEventConstants.ACCEVENT_AMZ;
				if (resultSet.getBoolean("PftInSusp")) {
					eventCode = AccountEventConstants.ACCEVENT_AMZSUSP;
				}

				// DataSet Object preparation for AccountingSet Execution
				DataSet dataSet = new DataSet();
				dataSet.setFinReference(finReference);
				dataSet.setFinEvent(eventCode);
				dataSet.setFinBranch(resultSet.getString("FinBranch"));
				dataSet.setFinCcy(resultSet.getString("FinCcy"));
				dataSet.setPostDate(dateAppDate);
				dataSet.setValueDate(valueDate);
				dataSet.setSchdDate(resultSet.getDate("NextRepayDate"));
				dataSet.setFinType(resultSet.getString("FinType"));
				dataSet.setFinAccount(resultSet.getString("FinAccount"));
				dataSet.setFinCustPftAccount(resultSet.getString("FinCustPftAccount"));
				dataSet.setCustId(resultSet.getLong("CustID"));
				dataSet.setDisburseAccount(resultSet.getString("DisbAccountId"));
				dataSet.setRepayAccount(resultSet.getString("RepayAccountId"));
				dataSet.setFinAmount(resultSet.getBigDecimal("FinAmount"));
				dataSet.setDisburseAmount(resultSet.getBigDecimal("DisburseAmount"));
				dataSet.setDownPayment(resultSet.getBigDecimal("DownPayment"));
				dataSet.setNoOfTerms(resultSet.getInt("NumberOfTerms") + resultSet.getInt("GraceTerms"));
				dataSet.setNewRecord(false);

				// Accounting Set Execution to get Posting Details List

				if (financeType.isAllowRIAInvestment()) {

					List<FinContributorDetail> contributorDetailList = getFinContributorDetailDAO().getFinContributorDetailByFinRef(dataSet.getFinReference(), "_AView");

					List<AEAmountCodesRIA> riaDetailList = getEngineExecutionRIA().prepareRIADetails(contributorDetailList, dataSet.getFinReference());
					list = getEngineExecutionRIA().getAccEngineExecResults(dataSet, amountCodes, "Y", riaDetailList, null);

				} else {
					list.addAll(getEngineExecution().getAccEngineExecResults(dataSet, amountCodes, "Y", null, true, financeType));
				}

				pftDetail = new FinanceProfitDetail();
				pftDetail.setAcrTillLBD(resultSet.getBigDecimal("pftAccrued"));
				
				//FIXME: PV 14APR17 based on finPftDetails
				pftDetail.setAmzTillLBD(resultSet.getBigDecimal(""));
				pftDetail.setLastMdfDate(valueDate);
				pftDetailList.add(pftDetail);

				if (list.size() > 290 || list.size() == 300) {

					List<Object> returnList = getPostingsPreparationUtil().postingAccruals(list, postBranch, valueDate, "Y", true, "Y", linkedTranId);

					if (!(Boolean) returnList.get(0)) {
						// THROW ERROR FOR POSTINGS NOT SUCCESS
						logger.fatal("Postings Failed for Accrual Posting Process");
					}

					// Reset Back Same Linked Transaction ID to all Postings
					linkedTranId = (Long) returnList.get(1);

					getFinanceProfitDetailDAO().updateBatchList(pftDetailList, "_Temp");

					list.clear();
					pftDetailList.clear();

				}

				processed = resultSet.getRow();
				exeStatus.setProcessedCount(processed);

				amountCodes = null;
				dataSet = null;
			}

			if (list.size() > 0) {

				List<Object> returnList = getPostingsPreparationUtil().postingAccruals(list, postBranch, valueDate, "Y", true, "Y", linkedTranId);

				if (!(Boolean) returnList.get(0)) {
					// THROW ERROR FOR POSTINGS NOT SUCCESS
					logger.fatal("Postings Failed for Accrual Posting Process");
				}

				getFinanceProfitDetailDAO().updateBatchList(pftDetailList, "_Temp");
			}

			list = null;
			pftDetailList = null;

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			pftDetail = null;
			if (resultSet!=null) {
				resultSet.close();
			}
			if (preparedStatement!=null) {
				preparedStatement.close();
			}
		}

		logger.debug("COMPLETE: Amortization Postings for Value Date: " + valueDate);

	}

	/**
	 * Method for preparation of Select Query To get Active finances based on
	 * data
	 * 
	 * @return sqlQuery
	 */
	private String getSelectQuery() {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" SELECT F.FinReference, P.AcrTillLBD, P.PftAmzSusp, ");
		sqlQuery.append(" P.AmzTillLBD, P.FirstODDate, P.PrvODDate FROM FinanceMain F ");
		sqlQuery.append(" INNER JOIN FinPftDetails P ON F.FinReference = P.FinReference ");
		sqlQuery.append(" WHERE P.FinIsActive = 1");
		sqlQuery.append(" AND F.FinStartDate <=? ");
		return sqlQuery.toString();
	}

	/**
	 * Method for Update Accrual Transfered Amount
	 * 
	 * @return sqlQuery
	 */
	private String updateAccrueTsfdQuery(String type) {

		//FIXME: PV 14APR17
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" Update FinPftDetails");
		sqlQuery.append(" where FinIsActive = 1 OR (FinIsActive = 0 AND LatestRpyDate >= ? AND LatestRpyDate <= ? )  ");
		return sqlQuery.toString();
	}

	/**
	 * Method for Update Accrual Transfered Amount for Inactive Fiannces
	 * 
	 * @return sqlQuery
	 */
	private String resetAccrueTsfd(String type) {
		//FIXME: PV 14APR17
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" Update FinPftDetails");
		sqlQuery.append(StringUtils.trimToEmpty(type));
		return sqlQuery.toString();
	}

	/**
	 * Method for Update Suspense Status for Inactive Finance
	 * 
	 * @return sqlQuery
	 */
	private String updateSuspSts(String type) {

		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" Update FinPftDetails");
		sqlQuery.append(StringUtils.trimToEmpty(type));
		sqlQuery.append(" SET PftInSusp = 0 WHERE FinIsActive = 0 AND PftInSusp = 1  ");
		return sqlQuery.toString();
	}

	/**
	 * Method for Update Accrual Transfered Amount
	 * 
	 * @return sqlQuery
	 */
	private String getSuspAcrTsfdRefDetail() {

		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" Select T.FinReference, T.SuspDate, T.PrvSchDate, T.CurSchDate, T1.FinPostDate AS PostDate, ");
		sqlQuery.append(" T1.FinSchdDate AS RpySchDate, T1.FinSchdPftPaid AS PftPaid, (T2.ProfitSchd) AS TotalPft ");
		sqlQuery.append(" FROM (Select T1.FinReference, Convert(DateTime,DATEADD(dd,-(DAY(CustStsChgDate)-1),CustStsChgDate))SuspDate, ");
		sqlQuery.append(" MAX(COALESCE(T3.SchDate, T1.FinStartDate)) PrvSchDate,MIN(T5.SchDate)CurSchDate ");
		sqlQuery.append(" FROM FinpftDetails T1 INNER JOIN Customers T2 ON T1.CustId=T2.CustID LEFT JOIN ");
		sqlQuery.append(" FinScheduleDetails T3 ON T3.FinReference=T1.FinReference and ");
		sqlQuery.append(" T3.SchDate < Convert(DateTime,DATEADD(dd,-(DAY(CustStsChgDate)-1),CustStsChgDate)) LEFT JOIN ");
		sqlQuery.append(" FinScheduleDetails T5 ON T5.FinReference=T1.FinReference and  ");
		sqlQuery.append(" T5.SchDate >= Convert(DateTime,DATEADD(dd,-(DAY(CustStsChgDate)-1),CustStsChgDate)) ");
		sqlQuery.append(" Where PftInSusp= 1  and (FinIsActive = 1 OR (FinIsActive = 0 AND LatestRpyDate >= ? AND LatestRpyDate <= ? )) ");
		sqlQuery.append(" and MaturityDate > Convert(DateTime,DATEADD(dd,-(DAY(CustStsChgDate)-1),CustStsChgDate)) ");
		sqlQuery.append(" Group By T1.FinReference,T2.CustStsChgDate) T INNER JOIN FinRepayDetails T1 ON T.FinReference=T1.FinReference ");
		sqlQuery.append(" Inner Join FinScheduleDetails T2 ON T2.FinReference=T1.FinReference and T2.SchDate=T1.FinSchdDate ");
		sqlQuery.append(" where T1.FinSchdDate>= CurSchDate AND T1.FinReference IN (Select FinReference from FinRepayDetails ");
		sqlQuery.append(" where FinPostDate >= ? and FinPostDate <= ?) ");
		return sqlQuery.toString();
	}

	/**
	 * Method for preparation of Select Query To get Active finances based on
	 * data
	 * 
	 * @return sqlQuery
	 */
	private String getCountQuery() {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" SELECT count(F.FinReference) FROM FinanceMain F ");
		sqlQuery.append(" INNER JOIN FinPftDetails P ON F.FinReference = P.FinReference ");
		sqlQuery.append(" WHERE P.FinIsActive = 1");
		sqlQuery.append(" AND F.FinStartDate <=? ");
		return sqlQuery.toString();
	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @return sqlQuery
	 */
	private String prepareSelectQuery(String type) {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" SELECT T1.FinReference, T1.FinBranch, T1.FinType, T1.FinCcy ,T1.NextRepayDate ,T1.CustID , T1.GraceTerms, ");
		sqlQuery.append(" T1.DisbAccountId ,T1.RepayAccountId ,T1.FinAmount  DisburseAmount , (T1.FinAmount - T1.FinRepaymentAmount)  FinAmount ,");
		sqlQuery.append(" T1.DownPayment , T1.NumberOfTerms, T1.FinAccount, T1.FinCustPftAccount, T4.PftInSusp, ");
		sqlQuery.append(" T4.TotalPftSchd, T4.TotalPftCpz, T4.TotalPftPaid, T4.TotalPftBal, T4.TdSchdPft, T4.TdPftCpz, T4.TdSchdPftPaid,  ");
		sqlQuery.append(" T4.TdSchdPftBal, T4.PftAccrued, T4.PftAccrueSusp, ");
		sqlQuery.append(" FROM FinanceMain  T1");
		sqlQuery.append(" INNER JOIN FinPftDetails").append(type);
		sqlQuery.append("  T4 ON T1.FinReference = T4.FinReference ");
		sqlQuery.append(" WHERE T1.FinStartDate <=?");
		sqlQuery.append(" AND T1.FinIsActive = 1");
		return sqlQuery.toString();

	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @return sqlQuery
	 */
	private String prepareCountQuery(String type) {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" SELECT count(T1.FinReference)");
		sqlQuery.append(" FROM FinanceMain T1");
		sqlQuery.append(" INNER JOIN FinPftDetails").append(type);
		sqlQuery.append(" T4 ON T1.FinReference = T4.FinReference ");
		sqlQuery.append(" WHERE T1.FinStartDate <=?");
		sqlQuery.append(" AND T1.FinIsActive = 1");
		return sqlQuery.toString();

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setFinContributorDetailDAO(FinContributorDetailDAO finContributorDetailDAO) {
		this.finContributorDetailDAO = finContributorDetailDAO;
	}

	public FinContributorDetailDAO getFinContributorDetailDAO() {
		return finContributorDetailDAO;
	}

	public void setEngineExecutionRIA(AccountEngineExecutionRIA engineExecutionRIA) {
		this.engineExecutionRIA = engineExecutionRIA;
	}

	public AccountEngineExecutionRIA getEngineExecutionRIA() {
		return engineExecutionRIA;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
		return customerStatusCodeDAO;
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

}
