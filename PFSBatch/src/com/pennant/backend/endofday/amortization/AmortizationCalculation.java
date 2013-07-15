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
 * FileName    		:  AmortizationCalculation.java													*                           
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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FinanceProfitDetailFiller;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.util.PennantConstants;

public class AmortizationCalculation implements Tasklet {
	
	private Logger logger = Logger.getLogger(AmortizationCalculation.class);

	private FinanceMainDAO finMainDAO;
	private FinanceScheduleDetailDAO finScheduleDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceProfitDetailFiller financeProfitDetailFiller;
	private BatchAdminDAO batchAdminDAO;
	private DataSource dataSource;

	private Date dateValueDate = null;
	

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
			
		logger.debug("START: Amortization Caluclation for Value Date: "+ dateValueDate);		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);	
		
		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		selQuery = prepareSelectQuery(selQuery);

		FinanceMain financeMain = null;
		List<FinanceScheduleDetail> schdDetails = null;
		FinanceProfitDetail pftDetail = null;

		try {
			
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				
				financeMain = getFinMainDAO().getFinanceMainForDataSet(resultSet.getString("FinReference"));
				schdDetails = getFinScheduleDAO().getFinScheduleDetails(resultSet.getString("FinReference"), "", false);
				pftDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(resultSet.getString("FinReference"));
				AEAmounts aeAmounts = new AEAmounts();
				AEAmountCodes aeAmountCodes = aeAmounts.procAEAmounts(financeMain, schdDetails, pftDetail, dateValueDate);

				getFinanceProfitDetailFiller().prepareFinPftDetails(aeAmountCodes, pftDetail, dateValueDate);

				// UPDATE
				getFinanceProfitDetailDAO().update(pftDetail);
				
				getBatchAdminDAO().saveStepDetails(pftDetail.getFinReference(), getAmortization(pftDetail), context.getStepContext().getStepExecution().getId());	
				context.getStepContext().getStepExecution().getExecutionContext().putInt("FIELD_COUNT", resultSet.getRow());
			}
			
		}catch (DataAccessException e) {
			logger.error(e);
			throw new DataAccessException(e.getMessage()) {};
		} catch (SQLException e) {
			logger.error(e);
			throw new SQLException(e.getMessage()) {};
		} finally {
			financeMain = null;
			schdDetails = null;
			pftDetail = null;
			resultSet.close();
			sqlStatement.close();
		}

		logger.debug("COMPLETE: Amortization Caluclation for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private StringBuffer prepareSelectQuery(StringBuffer selQuery) {
		
		selQuery.append(" SELECT FinReference, FinBranch, FinType  FROM FinanceMain");
		selQuery.append(" WHERE FinStartDate <='"+  dateValueDate +"'");
		selQuery.append(" AND MaturityDate >= '"+  dateValueDate +"'");
		selQuery.append(" AND FinIsActive = '1'");
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

	public FinanceMainDAO getFinMainDAO() {
		return finMainDAO;
	}
	public void setFinMainDAO(FinanceMainDAO finMainDAO) {
		this.finMainDAO = finMainDAO;
	}

	public FinanceScheduleDetailDAO getFinScheduleDAO() {
		return finScheduleDAO;
	}
	public void setFinScheduleDAO(FinanceScheduleDetailDAO finScheduleDAO) {
		this.finScheduleDAO = finScheduleDAO;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}
	public void setFinanceProfitDetailDAO(
			FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}
	
	public BatchAdminDAO getBatchAdminDAO() {
		return batchAdminDAO;
	}

	public void setBatchAdminDAO(BatchAdminDAO batchAdminDAO) {
		this.batchAdminDAO = batchAdminDAO;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setFinanceProfitDetailFiller(FinanceProfitDetailFiller financeProfitDetailFiller) {
		this.financeProfitDetailFiller = financeProfitDetailFiller;
	}
	public FinanceProfitDetailFiller getFinanceProfitDetailFiller() {
		return financeProfitDetailFiller;
	}
	
}