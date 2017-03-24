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
 * FileName    		:  DisbursementPostings.java													*                           
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
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.BatchUtil;

public class CommencementPostings implements Tasklet {

	private Logger logger = Logger.getLogger(CommencementPostings.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private DataSource dataSource;

	private Date dateValueDate = null;
	private Date dateAppDate = null;
	
	int postings = 0;
	int processed = 0;
	
	public CommencementPostings() {
		
	}
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		dateValueDate = DateUtility.getValueDate();
		dateAppDate = DateUtility.getAppDate();

		logger.debug("START: Commencement Postings for Value Date: "+ dateValueDate);
		
		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setDate(1, (java.sql.Date)dateValueDate);
			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			sqlStatement.close();
			resultSet.close();
			
			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setDate(1, (java.sql.Date)dateValueDate);
			resultSet = sqlStatement.executeQuery();
			
			FinanceMain financeMain = null;
			List<FinanceScheduleDetail> scheduleDetailList = null;
			FinanceProfitDetail finPftDetail = null;
			
			while (resultSet.next()) {
				
				String finReference = resultSet.getString("FinReference");

				financeMain = getFinanceMainDAO().getFinanceMainForBatch(finReference);
				financeMain.setCurDisbursementAmt(financeMain.getFinAmount());
				scheduleDetailList = getFinanceScheduleDetailDAO().getFinSchdDetailsForBatch(finReference);
				
				finPftDetail = new FinanceProfitDetail();
				finPftDetail.setFinReference(finReference);
				finPftDetail.setAcrTillLBD(resultSet.getBigDecimal("AcrTillLBD"));
				finPftDetail.setTdPftAmortizedSusp(resultSet.getBigDecimal("TdPftAmortizedSusp"));
				finPftDetail.setAmzTillLBD(resultSet.getBigDecimal("AmzTillLBD"));

				DataSet dataSet = AEAmounts.createDataSet(financeMain, AccountEventConstants.ACCEVENT_GRACEEND, financeMain.getGrcPeriodEndDate(), financeMain.getGrcPeriodEndDate());
				dataSet.setNewRecord(false);
				dataSet.setFeeAmount(financeMain.getFeeChargeAmt() == null ?  BigDecimal.ZERO : financeMain.getFeeChargeAmt());
				dataSet.setInsAmount(financeMain.getInsuranceAmt() == null ?  BigDecimal.ZERO : financeMain.getInsuranceAmt());

				//AmountCodes Preparation
				AEAmountCodes amountCodes = AEAmounts.procAEAmounts(financeMain, scheduleDetailList, finPftDetail, dateValueDate);

				//Postings Process
				List<Object> returnList = getPostingsPreparationUtil().processPostingDetails(dataSet,amountCodes, true, 
						resultSet.getBoolean("AllowRIAInvestment"), "Y", dateAppDate,false, Long.MIN_VALUE);
				
				if(returnList!=null && !returnList.isEmpty()) {
					if((Boolean)returnList .get(0)) {
						
						if(returnList.get(4) != null && StringUtils.isNotBlank((String)returnList.get(4))){
							//Update Finance Account for Reference
							getFinanceMainDAO().updateFinAccounts(finReference, (String)returnList.get(4));
						}
						
						postings++;
					}
				}

				processed = resultSet.getRow();
				BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(processed));
				BatchUtil.setExecution(context,  "INFO", getInfo());
				
				finPftDetail = null;
				
			}
			
			BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(processed));
			BatchUtil.setExecution(context,  "INFO", getInfo());
			
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			if(resultSet !=null) {
				resultSet.close();
			}
			if(sqlStatement != null) {
				sqlStatement.close();
			}
		}

		logger.debug("COMPLETE: Commencement Postings for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}
	
	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private String getCountQuery() {
		
		StringBuilder selQuery = new StringBuilder(" SELECT count(T1.FinReference)" );
		selQuery.append(" FROM FinanceMain  T1 " );
		selQuery.append(" INNER JOIN FinPftDetails  T2 ON T1.FinReference = T2.FinReference " );
		selQuery.append(" INNER JOIN RMTFinanceTypes  T3 ON T2.FinType = T3.FinType AND T3.FinAEGraceEnd != 0 " );
		selQuery.append(" WHERE  T1.FinIsActive = 1 And T1.GrcPeriodEndDate = ? ");
		return selQuery.toString();
		
	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private String prepareSelectQuery() {
		
		StringBuilder selQuery = new StringBuilder(" SELECT T1.FinReference, T3.AllowRIAInvestment, T2.AcrTillLBD, " );
		selQuery.append(" T2.TdPftAmortizedSusp, T2.AmzTillLBD  FROM FinanceMain  T1 " );
		selQuery.append(" INNER JOIN FinPftDetails  T2 ON T1.FinReference = T2.FinReference " );
		selQuery.append(" INNER JOIN RMTFinanceTypes  T3 ON T2.FinType = T3.FinType AND T3.FinAEGraceEnd != 0 " );
		selQuery.append(" WHERE  T1.FinIsActive = 1 And T1.GrcPeriodEndDate = ? ");
		return selQuery.toString();
		
	}
	
	private String getInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append("Total Commencement Posting's").append(": ").append(postings);
		return builder.toString();
	}
		
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}
	public void setFinanceProfitDetailDAO(
			FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}