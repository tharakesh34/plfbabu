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
 * FileName    		:  AmortizationPostings.java													*                           
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
package com.pennant.backend.endofday.tasklet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.eod.service.AmortizationService;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.AccountEngineExecutionRIA;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.util.EODProperties;

public class AmortizationPostings implements Tasklet {
	private Logger						logger	= Logger.getLogger(AmortizationPostings.class);

	private AmortizationService			amortizationService;
	private DataSource					dataSource;
	private FinContributorDetailDAO		finContributorDetailDAO;
	private AccountEngineExecution		engineExecution;
	private AccountEngineExecutionRIA	engineExecutionRIA;
	private PostingsPreparationUtil		postingsPreparationUtil;
	private FinanceProfitDetailDAO		financeProfitDetailDAO;

	public AmortizationPostings() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getValueDate();

		logger.debug("START: Amortization Postings for Value Date: " + valueDate);

		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());

			preparedStatement = connection.prepareStatement(prepareCountQuery());
			preparedStatement.setDate(1, DateUtility.getDBDate(valueDate.toString()));
			resultSet = preparedStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			resultSet.close();
			preparedStatement.close();

			preparedStatement = connection.prepareStatement(prepareSelectQuery());
			preparedStatement.setDate(1, DateUtility.getDBDate(valueDate.toString()));
			resultSet = preparedStatement.executeQuery();

			List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
			List<FinanceProfitDetail> pftDetailList = new ArrayList<FinanceProfitDetail>();

			long linkedTranId = Long.MIN_VALUE;
			int processed = 0;

			while (resultSet.next()) {

				String finReference = resultSet.getString("FinReference");

				// Amount Codes preparation using FinProfitDetails
				AEAmountCodes amountCodes = new AEAmountCodes();
				amountCodes.setFinReference(finReference);
				amountCodes.setDAccrue(resultSet.getBigDecimal("PftAccrued").subtract(resultSet.getBigDecimal("AcrTillLBD")));
				amountCodes.setPft(resultSet.getBigDecimal("TotalPftSchd"));
				amountCodes.setPftAB(resultSet.getBigDecimal("TotalPftBal"));
				amountCodes.setPftAP(resultSet.getBigDecimal("TotalPftPaid"));
				amountCodes.setPftS(resultSet.getBigDecimal("TdSchdPft"));
				amountCodes.setPftSB(resultSet.getBigDecimal("TdSchdPftBal"));
				amountCodes.setPftSP(resultSet.getBigDecimal("TdSchdPftPaid"));
				amountCodes.setAccrueTsfd(resultSet.getBigDecimal("pftAccrued").subtract(resultSet.getBigDecimal("pftAccrueSusp")));// Distributed

				// **** Accounting Set Execution for Amortization ******//
				// Get Event From Finance Suspend Headers for Accounting Set
				FinanceType financeType = EODProperties.getFinanceType(resultSet.getString("FinType").trim());
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
				dataSet.setPostDate(DateUtility.getAppDate());
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

				FinanceProfitDetail pftDetail = new FinanceProfitDetail();
				pftDetail.setAcrTillLBD(resultSet.getBigDecimal("pftAccrued"));
				
				//FIXME: PV 14APR17 based on new finpftdetails
				pftDetail.setAmzTillLBD(resultSet.getBigDecimal(""));
				pftDetail.setLastMdfDate(valueDate);
				pftDetailList.add(pftDetail);

				if (list.size() == 300) {
					linkedTranId = postAndClearData(list, valueDate, linkedTranId, pftDetailList);
				}

				processed = resultSet.getRow();
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));
			}

			if (list.size() > 0) {

				linkedTranId = postAndClearData(list, valueDate, linkedTranId, pftDetailList);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}finally{
			if (resultSet != null) {
				resultSet.close();
			}

			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}

		logger.debug("COMPLETE: Amortization Postings for Value Date: " + valueDate);
		return RepeatStatus.FINISHED;
	}

	private Long postAndClearData(List<ReturnDataSet> list, Date valueDate, long linkedTranId, List<FinanceProfitDetail> pftDetailList) throws Exception {

		List<Object> returnList = getPostingsPreparationUtil().postingAccruals(list, "9999", valueDate, "Y", true, "N", linkedTranId);

		if (!(Boolean) returnList.get(0)) {
			// THROW ERROR FOR POSTINGS NOT SUCCESS
			logger.fatal("Postings Failed for Accrual Posting Process");
		}

		// Reset Back Same Linked Transaction ID to all Postings
		getFinanceProfitDetailDAO().updateBatchList(pftDetailList, "");
		list.clear();
		pftDetailList.clear();
		return (Long) returnList.get(1);
	}

	private String prepareCountQuery() {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" SELECT count(T1.FinReference)");
		sqlQuery.append(" FROM FinanceMain T1");
		sqlQuery.append(" INNER JOIN FinPftDetails");
		sqlQuery.append(" T4 ON T1.FinReference = T4.FinReference ");
		sqlQuery.append(" WHERE T1.FinStartDate <=?");
		sqlQuery.append(" AND T1.FinIsActive = 1");
		return sqlQuery.toString();

	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @return<String> sqlQuery
	 */
	private String prepareSelectQuery() {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" SELECT T1.FinReference, T1.FinBranch, T1.FinType, T1.FinCcy ,T1.NextRepayDate ,T1.CustID , T1.GraceTerms, ");
		sqlQuery.append(" T1.DisbAccountId ,T1.RepayAccountId ,T1.FinAmount  DisburseAmount , (T1.FinAmount - T1.FinRepaymentAmount)  FinAmount ,");
		sqlQuery.append(" T1.DownPayment , T1.NumberOfTerms, T1.FinAccount, T1.FinCustPftAccount, T4.PftInSusp, ");
		sqlQuery.append(" T4.TotalPftSchd, T4.TotalPftCpz, T4.TotalPftPaid, T4.TotalPftBal, T4.TdSchdPft, T4.TdPftCpz, T4.TdSchdPftPaid,  ");
		sqlQuery.append(" T4.TdSchdPftBal, T4.pftAccrued, T4.pftAccrueSusp");
		sqlQuery.append(" FROM FinanceMain  T1");
		sqlQuery.append(" INNER JOIN FinPftDetails");
		sqlQuery.append("  T4 ON T1.FinReference = T4.FinReference ");
		sqlQuery.append(" WHERE T1.FinStartDate <=?");
		sqlQuery.append(" AND T1.FinIsActive = 1");
		return sqlQuery.toString();

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AmortizationService getAmortizationService() {
		return amortizationService;
	}

	public void setAmortizationService(AmortizationService amortizationService) {
		this.amortizationService = amortizationService;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public FinContributorDetailDAO getFinContributorDetailDAO() {
		return finContributorDetailDAO;
	}

	public void setFinContributorDetailDAO(FinContributorDetailDAO finContributorDetailDAO) {
		this.finContributorDetailDAO = finContributorDetailDAO;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public AccountEngineExecutionRIA getEngineExecutionRIA() {
		return engineExecutionRIA;
	}

	public void setEngineExecutionRIA(AccountEngineExecutionRIA engineExecutionRIA) {
		this.engineExecutionRIA = engineExecutionRIA;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

}