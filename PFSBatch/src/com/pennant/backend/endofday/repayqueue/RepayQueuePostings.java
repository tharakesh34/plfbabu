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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

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
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.SuspensePostingUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class RepayQueuePostings implements Tasklet {
	
	private Logger logger = Logger.getLogger(RepayQueuePostings.class);

	private FinanceProfitDetailDAO profitDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RepaymentPostingsUtil postingsUtil;
	private AccountInterfaceService accountInterfaceService;
	private SuspensePostingUtil suspensePostingUtil;
	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;
	
	
	private List<FinanceScheduleDetail> scheduleDetails = null;
	private FinanceProfitDetail financeProfitDetail = null;
	private Date dateValueDate = null;
	private FinanceMain financeMain = null;
	private BigDecimal repayAmountBal;
	private FinRepayQueue finRepayQueue = null;

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());

		logger.debug("START: Repayments Due Today Queue Postings for Value Date: " + dateValueDate);

		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);
		
		// FETCH Finance Repayment Queues
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		selQuery = prepareSelectQuery(selQuery);
		
		try {
			
			connection = DataSourceUtils.doGetConnection(getDataSource());	
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			
			while (resultSet.next()) {

				// Prepare Finance RepayQueue Data
				finRepayQueue = doWriteDataToBean(resultSet);
				String finReference = finRepayQueue.getFinReference();

				// Get the Finance Main Data, Schedule details List &
				// ProfitDetails based on Finance reference
				financeMain = getFinanceMainDAO().getFinanceMainForDataSet(finReference);
				scheduleDetails = new ArrayList<FinanceScheduleDetail>(financeMain.getNumberOfTerms()+1);
				scheduleDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, "", false);
				financeProfitDetail = getProfitDetailDAO().getFinProfitDetailsById(finReference);
				
				if (checkPaymentBalance(resultSet.getBoolean("AlwPartialRpy"), resultSet.getBigDecimal("RepayQueueBal"))) {

					getPostingsUtil().postingsRepayProcess(financeMain, scheduleDetails,
							financeProfitDetail, dateValueDate, finRepayQueue, repayAmountBal,
							resultSet.getBoolean("AllowRIAInvestment"));

				} else {

					// Finance Over Due Details
					FinanceScheduleDetail scheduleDetail = getFinanceScheduleDetailDAO().getFinanceScheduleDetailById(
							finRepayQueue.getFinReference(), finRepayQueue.getRpyDate(), "", false);

					FinODDetails finODDetails = getPostingsUtil().overDuesPreparation(finRepayQueue, scheduleDetail, dateValueDate);

					// Finance Suspence
					getSuspensePostingUtil().suspensePreparation(financeMain, financeProfitDetail, 
							finODDetails, dateValueDate, resultSet.getBoolean("AllowRIAInvestment"));
				}
				getBatchAdminDAO().saveStepDetails(resultSet.getString("FinReference"), getRepayQueue(finRepayQueue), context.getStepContext().getStepExecution().getId());
				context.getStepContext().getStepExecution().getExecutionContext().putInt("FIELD_COUNT", resultSet.getRow());
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new SQLException(e.getMessage()) {};
		} catch (AccountNotFoundException e) {
			logger.error(e);
			throw new AccountNotFoundException(e.getMessage()) {};
		} catch (IllegalAccessException e) {
			logger.error(e);
			throw new IllegalAccessException(e.getMessage()) {};
		} catch (InvocationTargetException e) {
			logger.error(e);
			throw new InvocationTargetException(e, e.getMessage()) {};
		} finally {
			financeMain = null;
			scheduleDetails = null;
			financeProfitDetail = null;
			finRepayQueue = null;
			resultSet.close();
			sqlStatement.close();
		}

		logger.debug("END: Repayments Due Today Queue Postings for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;

	}

	/**
	 * Method for Preparation of Select Query for Preparing resultSet
	 * 
	 * @param selectSql
	 * @return
	 */
	private StringBuffer prepareSelectQuery(StringBuffer selectSql) {

		selectSql = new StringBuffer(" SELECT  RpyDate, FinPriority, FinRpyQueue.FinType AS FinType, FinReference,");
		selectSql.append(" Branch, CustomerID, FinRpyFor, SchdPft, SchdPri,");
		selectSql.append(" SchdPftPaid, SchdPriPaid, SchdPftBal, SchdPriBal,");
		selectSql.append(" SchdIsPftPaid, SchdIsPriPaid , RMTFinanceTypes.FinIsAlwPartialRpy AS AlwPartialRpy ,");
		selectSql.append(" (SchdPftBal+SchdPriBal) AS RepayQueueBal, RMTFinanceTypes.AllowRIAInvestment ");
		selectSql.append(" FROM FinRpyQueue , RMTFinanceTypes ");
		selectSql.append(" WHERE  FinRpyQueue.FinType= RMTFinanceTypes.FinType ");
		selectSql.append(" AND (SchdIsPftPaid =0  OR  SchdIsPriPaid =0 ) AND RpyDate <= '"+dateValueDate+"'");
		selectSql.append(" ORDER BY RpyDate,FinPriority, FinRpyFor ASC ");
		return selectSql;
		
	}

	/**
	 * Method for Creating RepayQueue Object using resultSet
	 * 
	 * @param resultSet
	 * @return
	 */
	@SuppressWarnings("serial")
	private FinRepayQueue doWriteDataToBean(ResultSet resultSet) {

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
	
	/**-----------------------------------------------------*/
	//###### Condition for Checking Available Balance ######//
	/**-----------------------------------------------------*/
	
	private boolean checkPaymentBalance(boolean isAlwPartialRpy, BigDecimal repayQueueBal) {

		boolean isPayNow = false;
		repayAmountBal = new BigDecimal(0);
		
		IAccounts iAccount = new IAccounts();
		iAccount.setAccountId(financeMain.getRepayAccountId());

		// Check Available Funding Account Balance
		iAccount = getAccountInterfaceService().fetchAccountAvailableBal(iAccount,false);

		// Set Requested Repayment Amount as RepayAmount Balance
		if (iAccount.getAcAvailableBal().compareTo(repayQueueBal) >= 0) {
			repayAmountBal = repayQueueBal;
			isPayNow = true;
		} else {
			if (isAlwPartialRpy && iAccount.getAcAvailableBal().intValue() > 0) {
				repayAmountBal = iAccount.getAcAvailableBal();
				isPayNow = true;
			}
		}

		return isPayNow;
	}
	
	private String getRepayQueue(FinRepayQueue repayQueue) {
		StringBuffer strRepayQueue = new StringBuffer();


		if (strRepayQueue != null) {
			strRepayQueue.append("RpyDate");
			strRepayQueue.append("-");
			strRepayQueue.append(DateUtility.formatUtilDate(repayQueue.getRpyDate(), PennantConstants.dateFormat));
			strRepayQueue.append(";");

			strRepayQueue.append("FinRpyFor");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getFinRpyFor());
			strRepayQueue.append(";");
			
			strRepayQueue.append("FinPriority");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getFinPriority());
			strRepayQueue.append(";");

			strRepayQueue.append("FinType");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getFinType());
			strRepayQueue.append(";");
			
						
			strRepayQueue.append("FinBranch");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getBranch());
			strRepayQueue.append(";");
				
			strRepayQueue.append("SchdPft");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getSchdPft()); //TODO AMTFORMART
			strRepayQueue.append(";");
			
			strRepayQueue.append("SchdPri");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getSchdPri()); //TODO AMTFORMART
			strRepayQueue.append(";");

			strRepayQueue.append("SchdPftPaid");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getSchdPftPaid()); //TODO AMTFORMART
			strRepayQueue.append(";");
			
			strRepayQueue.append("SchdPriPaid");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getSchdPriPaid()); //TODO AMTFORMART
			strRepayQueue.append(";");
			
			strRepayQueue.append("SchdPftBal");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getSchdPftBal()); //TODO AMTFORMART
			strRepayQueue.append(";");
			
			strRepayQueue.append("SchdPriBal");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getSchdPriBal()); //TODO AMTFORMART
			strRepayQueue.append(";");
			
			strRepayQueue.append("SchdIsPftPaid");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.isSchdIsPftPaid());
			strRepayQueue.append(";");

			strRepayQueue.append("RefundAmount");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.getRefundAmount()); //TODO AMTFORMART
			strRepayQueue.append(";");
			
			strRepayQueue.append("RcdNotExist");
			strRepayQueue.append("-");
			strRepayQueue.append(repayQueue.isRcdNotExist()); 
			strRepayQueue.append(";");
	
		}

		return strRepayQueue.toString();
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

	public void setSuspensePostingUtil(SuspensePostingUtil suspensePostingUtil) {
		this.suspensePostingUtil = suspensePostingUtil;
	}
	public SuspensePostingUtil getSuspensePostingUtil() {
		return suspensePostingUtil;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}

	public BatchAdminDAO getBatchAdminDAO() {
		return batchAdminDAO;
	}

	public void setBatchAdminDAO(BatchAdminDAO batchAdminDAO) {
		this.batchAdminDAO = batchAdminDAO;
	}

}
