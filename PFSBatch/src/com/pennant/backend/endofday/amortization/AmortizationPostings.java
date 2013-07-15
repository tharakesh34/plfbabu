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
package com.pennant.backend.endofday.amortization;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class AmortizationPostings implements Tasklet {

	private Logger logger = Logger.getLogger(AmortizationPostings.class);

	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;
	
	

	private Date dateValueDate = null;
	private Date dateAppDate = null;
	private BigDecimal zeroValue = BigDecimal.ZERO;

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
		dateAppDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString());
	
		logger.debug("START: Amortization Postings for Value Date: "+ dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		selQuery = prepareSelectQuery(selQuery);

		FinanceProfitDetail pftDetail = null;

		try {
			
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			
			while (resultSet.next()) {

				pftDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(resultSet.getString("FinReference"));

				//Amount Codes preparation using FinProfitDetails
				AEAmountCodes amountCodes = new AEAmountCodes();
				amountCodes.setFinReference(resultSet.getString("FinReference"));
				amountCodes.setDAccrue(pftDetail.getAcrTodayToNBD());
				amountCodes.setNAccrue(pftDetail.getAcrTillNBD());
				amountCodes.setPft(pftDetail.getTotalPftSchd().add(pftDetail.getTotalPftCpz()));
				amountCodes.setPftAB(pftDetail.getTotalPftBal());
				amountCodes.setPftAP(pftDetail.getTotalPftPaid());
				amountCodes.setPftS(pftDetail.getTdSchdPft().add(pftDetail.getTdPftCpz()));
				amountCodes.setPftSB(pftDetail.getTdSchdPftBal());
				amountCodes.setPftSP(pftDetail.getTdSchdPftPaid());

				// +++++Accounting Set Execution for Amortization+++++++//
				// Get Event From Finance Suspend Headers for Accounting Set
				FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(
						resultSet.getString("FinReference"),"");
				String eventCode = "AMZ";
				if (suspHead != null && suspHead.isFinIsInSusp()) {
					eventCode = "AMZSUSP";
				}

				//DataSet Object preparation for AccountingSet Execution
				DataSet dataSet = new DataSet();
				dataSet.setFinReference(resultSet.getString("FinReference"));
				dataSet.setFinEvent(eventCode);
				dataSet.setFinBranch(resultSet.getString("FinBranch"));
				dataSet.setFinCcy(resultSet.getString("FinCcy"));
				dataSet.setPostDate(dateAppDate);
				dataSet.setValueDate(dateValueDate);
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
				dataSet.setNoOfTerms(resultSet.getInt("NumberOfTerms"));
				dataSet.setNewRecord(false);

				//Postings Preparation for Amortization
				List<Object> returnlist = getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, true,
						resultSet.getBoolean("AllowRIAInvestment"), "Y", dateAppDate, null, false);
				
				/*if(returnlist != null && !((Boolean) returnlist.get(0))){
					//TODO -- Accounting Posting Details Execution Failed Case Check
				}*/

				//Update Finance Profit Details
				if(eventCode.equals("AMZSUSP")) {
					pftDetail.setTdPftAccrueSusp(pftDetail.getTdPftAccrued());
				}else {
					pftDetail.setTdPftAccrueSusp(zeroValue);
				}

				pftDetail.setAcrTillLBD(pftDetail.getTdPftAccrued()); 
				pftDetail.setAcrTodayToNBD(zeroValue);
				pftDetail.setAmzTillLBD(pftDetail.getAmzTillNBD());
				pftDetail.setAmzTodayToNBD(zeroValue);
				pftDetail.setLastMdfDate(dateValueDate);
				getFinanceProfitDetailDAO().update(pftDetail);

				getBatchAdminDAO().saveStepDetails(dataSet.getFinReference(), getAmortization(pftDetail), context.getStepContext().getStepExecution().getId());
				context.getStepContext().getStepExecution().getExecutionContext().putInt("FIELD_COUNT", resultSet.getRow());
			}
			
		} catch (AccountNotFoundException e) {
			logger.error(e);
			throw new AccountNotFoundException(e.getMessage()) {};
		} catch (SQLException e) {
			logger.error(e);
			throw new SQLException(e.getMessage()) {};
		} catch (IllegalAccessException e) {
			logger.error(e);
			throw new IllegalAccessException(e.getMessage()) {};
		} catch (InvocationTargetException e) {
			logger.error(e);
			throw new InvocationTargetException(e, e.getMessage()) {};
		} finally {
			pftDetail = null;
			resultSet.close();
			sqlStatement.close();
		}

		logger.debug("COMPLETE: Amortization Postings for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private StringBuffer prepareSelectQuery(StringBuffer selQuery) {
		
		selQuery.append(" SELECT T1.FinReference, T1.FinBranch, T1.FinType, T1.FinCcy ,T1.NextRepayDate ,T1.CustID , ");
		selQuery.append(" T1.DisbAccountId ,T1.RepayAccountId ,T1.FinAmount AS DisburseAmount , (T1.FinAmount - T1.FinRepaymentAmount) AS FinAmount ," );
		selQuery.append(" T1.DownPayment , T1.NumberOfTerms, T1.FinAccount, T1.FinCustPftAccount, T2.AllowRIAInvestment " );
		selQuery.append(" FROM FinanceMain AS T1 INNER JOIN RMTFinanceTypes as T2 ON T1.FinType = T2.FinType ");
		selQuery.append(" WHERE T1.FinStartDate <='"+  dateValueDate +"'");
		selQuery.append(" AND T1.MaturityDate >= '"+  dateValueDate +"'");
		selQuery.append(" AND T1.FinIsActive = '1'");
		return selQuery;
		
	}
	
	private String getAmortization(FinanceProfitDetail fpdtl) {
		StringBuffer strodcr = new StringBuffer();
		
		if(fpdtl != null) {
		
			strodcr.append("FinBranch");
			strodcr.append("-");
			strodcr.append(fpdtl.getFinBranch());
			strodcr.append(";");
			
			strodcr.append("FinType");
			strodcr.append("-");
			strodcr.append(fpdtl.getFinType());
			strodcr.append(";");
			
			strodcr.append("LastMdfDate");
			strodcr.append("-");
			strodcr.append(DateUtility.formatUtilDate(fpdtl.getLastMdfDate(), PennantConstants.dateFormat));
			strodcr.append(";");
			
			strodcr.append("TotalPftSchd");
			strodcr.append("-");
			strodcr.append(fpdtl.getTotalPftSchd()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("TotalPftCpz");
			strodcr.append("-");
			strodcr.append(fpdtl.getTotalPftCpz()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("TotalPftPaid");
			strodcr.append("-");
			strodcr.append(fpdtl.getTotalPftPaid()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("TotalPftBal");
			strodcr.append("-");
			strodcr.append(fpdtl.getTotalPftBal()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("TotalPftPaidInAdv");
			strodcr.append("-");
			strodcr.append(fpdtl.getTotalPftPaidInAdv()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("TotalPriPaid");
			strodcr.append("-");
			strodcr.append(fpdtl.getTotalPriPaid()); //TODO AMTFORMART
			strodcr.append(";");

			strodcr.append("TotalPriBal");
			strodcr.append("-");
			strodcr.append(fpdtl.getTotalPriBal()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("TdSchdPft");
			strodcr.append("-");
			strodcr.append(fpdtl.getTdSchdPft()); //TODO AMTFORMART
			strodcr.append(";");
						
			strodcr.append("TdPftCpz");
			strodcr.append("-");
			strodcr.append(fpdtl.getTdPftCpz()); //TODO AMTFORMART
			strodcr.append(";");
						
			strodcr.append("TdSchdPftPaid");
			strodcr.append("-");
			strodcr.append(fpdtl.getTdSchdPftPaid()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("TdSchdPftBal");
			strodcr.append("-");
			strodcr.append(fpdtl.getTdSchdPftBal()); //TODO AMTFORMART
			strodcr.append(";");
						
			strodcr.append("TdPftAccrued");
			strodcr.append("-");
			strodcr.append(fpdtl.getTdPftAccrued()); //TODO AMTFORMART
			strodcr.append(";");
						
			strodcr.append("TdPftAccrueSusp");
			strodcr.append("-");
			strodcr.append(fpdtl.getTdPftAccrueSusp()); //TODO AMTFORMART
			strodcr.append(";");
			strodcr.append(";");
			
			strodcr.append("TdPftAmortized");
			strodcr.append("-");
			strodcr.append(fpdtl.getTdPftAmortized()); //TODO AMTFORMART
			strodcr.append(";");
			strodcr.append(";");
			
			strodcr.append("TdPftAmortizedSusp");
			strodcr.append("-");
			strodcr.append(fpdtl.getTdPftAmortizedSusp()); //TODO AMTFORMART
			strodcr.append(";");
						
			strodcr.append("TdSchdPri");
			strodcr.append("-");
			strodcr.append(fpdtl.getTdSchdPri()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("TdSchdPriPaid");
			strodcr.append("-");
			strodcr.append(fpdtl.getTdSchdPriPaid()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("TdSchdPriBal");
			strodcr.append("-");
			strodcr.append(fpdtl.getTdSchdPriBal()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("AcrTillNBD");
			strodcr.append("-");
			strodcr.append(fpdtl.getAcrTillNBD()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("AcrTillLBD");
			strodcr.append("-");
			strodcr.append(fpdtl.getAcrTillLBD()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("AcrTodayToNBD");
			strodcr.append("-");
			strodcr.append(fpdtl.getAcrTodayToNBD()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("AmzTillNBD");
			strodcr.append("-");
			strodcr.append(fpdtl.getAmzTillNBD()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("AmzTillLBD");
			strodcr.append("-");
			strodcr.append(fpdtl.getAmzTillLBD()); //TODO AMTFORMART
			strodcr.append(";");
			
			strodcr.append("AmzTodayToNBD");
			strodcr.append("-");
			strodcr.append(fpdtl.getAmzTodayToNBD()); //TODO AMTFORMART
			strodcr.append(";");
			
		}
		
		
		return strodcr.toString();

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}
	public void setFinanceProfitDetailDAO(
			FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}
	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
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