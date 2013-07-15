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
package com.pennant.backend.endofday.disbursement;

import java.lang.reflect.InvocationTargetException;
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

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class DisbursementPostings implements Tasklet {

	private Logger logger = Logger.getLogger(DisbursementPostings.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;
	

	private Date dateValueDate = null;
	private Date dateAppDate = null;

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
		dateAppDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString());

		logger.debug("START: Disbursement Postings for Value Date: "+ dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		selQuery = prepareSelectQuery(selQuery,dateValueDate);

		try {

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			
			while (resultSet.next()) {

				FinanceDisbursement disbursement = new FinanceDisbursement();
				disbursement.setFinReference(resultSet.getString("FinReference"));
				disbursement.setDisbDate(resultSet.getDate("DisbDate"));
				disbursement.setDisbSeq(resultSet.getInt("DisbSeq"));

				FinanceMain financeMain = getFinanceMainDAO().getFinanceMainForDataSet(resultSet.getString("FinReference"));
				List<FinanceScheduleDetail> scheduleDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(
						resultSet.getString("FinReference"), "", false);
				FinanceProfitDetail pftDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(resultSet.getString("FinReference"));

				AEAmounts aeAmounts = new AEAmounts();
				DataSet dataSet = aeAmounts.createDataSet(financeMain, "ADDDBSN", dateValueDate,
						financeMain.getFinStartDate());
				dataSet.setNewRecord(false);

				//AmountCodes Preparation
				AEAmountCodes amountCodes = aeAmounts.procAEAmounts(financeMain, scheduleDetails,
						pftDetail, dateValueDate);

				//Postings Process
				getPostingsPreparationUtil().processPostingDetails(dataSet,amountCodes, true, 
						resultSet.getBoolean("AllowRIAInvestment"), "Y", dateAppDate, null, false);

				//Disbursement Details Updation
				disbursement.setDisbDisbursed(true);
				getFinanceDisbursementDAO().update(disbursement, "", false);
				
				getBatchAdminDAO().saveStepDetails(disbursement.getFinReference(), getDisbursement(disbursement), context.getStepContext().getStepExecution().getId());
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
			resultSet.close();
			sqlStatement.close();
		}

		logger.debug("COMPLETE: Disbursement Postings for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private StringBuffer prepareSelectQuery(StringBuffer selQuery, Date valueDate) {
		
		selQuery.append(" SELECT T1.FinReference, T1.DisbDate, T1.DisbSeq, T1.DisbAccountId, T1.DisbAmount, " );
		selQuery.append(" T1.DisbDisbursed, T1.DisbRemarks, T3.AllowRIAInvestment " );
		selQuery.append(" FROM FinDisbursementDetails AS T1 " );
		selQuery.append(" INNER JOIN FinanceMain AS T2 ON T1.FinReference = T2.FinReference " );
		selQuery.append(" INNER JOIN RMTFinanceTypes AS T3 ON T2.FinType = T3.FinType " );
		selQuery.append(" WHERE T1.DisbDisbursed = '0' AND T1.DisbDate ='"+valueDate+"'");
		return selQuery;
		
	}
	
	private String getDisbursement(FinanceDisbursement disbursement) {
		StringBuffer strdisbursement = new StringBuffer();

		if (disbursement != null) {
			strdisbursement.append("DisbDate");
			strdisbursement.append("-");
			
			strdisbursement.append(DateUtility.formatUtilDate(disbursement.getDisbDate(), PennantConstants.dateFormat));
			strdisbursement.append(";");
			
			strdisbursement.append("DisbSeq");
			strdisbursement.append("-");
			strdisbursement.append(disbursement.getDisbSeq());
			strdisbursement.append(";");
			
			strdisbursement.append("DisbAccountId");
			strdisbursement.append("-");
			strdisbursement.append(disbursement.getDisbAccountId());
			strdisbursement.append(";");
			
			strdisbursement.append("DisbAmount");
			strdisbursement.append("-");
			strdisbursement.append(disbursement.getDisbAmount()); //TODO AMTFORMART
			strdisbursement.append(";");
			
			strdisbursement.append("DisbDisbursed");
			strdisbursement.append("-");
			strdisbursement.append(disbursement.isDisbDisbursed());
			strdisbursement.append(";");
			
			strdisbursement.append("DisbRemarks");
			strdisbursement.append("-");
			strdisbursement.append(disbursement.getDisbRemarks()); 
			strdisbursement.append(";");
						
		}
		return strdisbursement.toString();

	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}
	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
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

	public BatchAdminDAO getBatchAdminDAO() {
		return batchAdminDAO;
	}

	public void setBatchAdminDAO(BatchAdminDAO batchAdminDAO) {
		this.batchAdminDAO = batchAdminDAO;
	}
	

}