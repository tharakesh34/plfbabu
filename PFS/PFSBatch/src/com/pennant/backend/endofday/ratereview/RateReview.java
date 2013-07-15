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
 * FileName    		:  RateReview.java													*                           
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
package com.pennant.backend.endofday.ratereview;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FinanceProfitDetailFiller;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.dao.finance.DefermentHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.PennantConstants;

public class RateReview implements Tasklet {
	
	private Logger logger = Logger.getLogger(RateReview.class);
	
	private FinanceMainDAO	         financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RepayInstructionDAO	     repayInstructionDAO;
	private DefermentDetailDAO	     defermentDetailDAO;
	private DefermentHeaderDAO	     defermentHeaderDAO;
	private FinanceTypeDAO	         financeTypeDAO;
	private FinanceProfitDetailDAO	 financeProfitDetailDAO;
	private FinanceProfitDetailFiller financeProfitDetailFiller;
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
		
		logger.debug("START: Rate Review for Value Date: "+ DateUtility.addDays(dateValueDate,-1));
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);
		
		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		selQuery = prepareSelectQuery(selQuery);
		
		try {
			
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			
			FinScheduleData finScheduleData = null;
			FinanceProfitDetail profitDetail = null;

			while (resultSet.next()) {	

				//Fetching Finance Schedule Data by FinReference
				finScheduleData = getFinSchDataByFinRef(resultSet.getString("FinReference"), "_AView");
				
				//Rate Changes applied for Finance Schedule Data
				finScheduleData = ScheduleCalculator.refreshRates(finScheduleData);
				
				//Finance Profit Details
				profitDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(resultSet.getString("FinReference"));
				
				//Amount Codes Details Preparation
				AEAmounts aeAmounts = new AEAmounts();
				AEAmountCodes amountCodes = aeAmounts.procAEAmounts(finScheduleData.getFinanceMain(),
						finScheduleData.getFinanceScheduleDetails(), profitDetail, dateValueDate);
				
				//DataSet preparation
				DataSet dataSet = aeAmounts.createDataSet(finScheduleData.getFinanceMain(), 
						"RATCHG", dateValueDate, dateAppDate);
				
				//Posting Preparation Util
				getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, true, 
						finScheduleData.getFinanceType().isAllowRIAInvestment(),"Y", dateAppDate, null, false);
								
				//Preparation Of Profit Details with new Schedule Data
				profitDetail = getFinanceProfitDetailFiller().prepareFinPftDetails(amountCodes, profitDetail, dateValueDate);

				//Update New Finance Schedule Details Data
				listSave(finScheduleData, profitDetail);
				
				getBatchAdminDAO().saveStepDetails(profitDetail.getFinReference(), getRateReview(dataSet), context.getStepContext().getStepExecution().getId());
				context.getStepContext().getStepExecution().getExecutionContext().putInt("FIELD_COUNT", resultSet.getRow());
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new SQLException(e.getMessage()) {};
		} finally {
			resultSet.close();
			sqlStatement.close();
		}
		logger.debug("COMPLETE: Rate Review for Value Date: " + DateUtility.addDays(dateValueDate,-1));
		return RepeatStatus.FINISHED;

	}

	/**
	 * Method for preparation of Select Query To get Finances , which are
	 * changed rates on Particular dates
	 * 
	 * @param selQuery
	 * @return
	 */
	private StringBuffer prepareSelectQuery(StringBuffer selQuery) {
		
		selQuery.append(" SELECT fm.FinReference AS FinReference " );
		selQuery.append(" FROM FinanceMain AS fm" );
		selQuery.append(" WHERE fm.FinIsActive ='1'  AND AllowGrcPftRvw = '1' AND LastRepayRvwDate < GrcPeriodEndDate " );
		selQuery.append(" AND NextGrcPftRvwDate = '"+dateValueDate+"' AND GraceBaseRate IS NOT NULL AND GraceBaseRate <> ''" );
		selQuery.append(" UNION " );
		selQuery.append(" SELECT fm.FinReference AS FinReference " );
		selQuery.append(" FROM FinanceMain AS fm" );
		selQuery.append(" WHERE fm.FinIsActive ='1'  AND AllowRepayRvw = '1' AND LastRepayRvwDate < MaturityDate " );
		selQuery.append(" AND NextRepayRvwDate = '"+dateValueDate+"' AND RepayBaseRate IS NOT NULL AND RepayBaseRate <> ''" );
		return selQuery;
		
	}
	
	/**
	 * Method for fetching Finance Schedule Data based on FinReference
	 * @param financeReference
	 * @param type
	 * @return
	 */
	public FinScheduleData getFinSchDataByFinRef(String financeReference, String type) {
		logger.debug("Entering");
		
		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(financeReference, type, false));
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(financeReference, type, false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(financeReference, type, false));
		finSchData.setDefermentHeaders(getDefermentHeaderDAO().getDefermentHeaders(financeReference, type, false));
		finSchData.setDefermentDetails(getDefermentDetailDAO().getDefermentDetails(financeReference, type, false));
		finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(finSchData.getFinanceMain().getFinType(), type));
		logger.debug("Leaving");
		return finSchData;
		
	}
	
	/**
	 * Method to save Finance Related sublist 
	 * @param schdueleData
	 */
	public void listSave(FinScheduleData schdueleData, FinanceProfitDetail profitDetail) {
		logger.debug("Entering ");
		
		//FinanceMain updation 
		schdueleData.getFinanceMain().setVersion(schdueleData.getFinanceMain().getVersion()+1);
		getFinanceMainDAO().update(schdueleData.getFinanceMain(), "", false);
		
		// Finance Schedule Details
		getFinanceScheduleDetailDAO().updateList(schdueleData.getFinanceScheduleDetails(), "");

		//Finance Defferment Header Details
		getDefermentHeaderDAO().updateList(schdueleData.getDefermentHeaders(), "",false);

		//Finance Defferment Details
		getDefermentDetailDAO().updateList(schdueleData.getDefermentDetails(),  "",false);

		//Finance Repay Instruction Details
		getRepayInstructionDAO().deleteByFinReference(schdueleData.getFinanceMain().getFinReference(), "", false);
		for (int i = 0; i < schdueleData.getRepayInstructions().size(); i++) {
			schdueleData.getRepayInstructions().get(i).setFinReference(schdueleData.getFinanceMain().getFinReference());
		}
		getRepayInstructionDAO().saveList(schdueleData.getRepayInstructions(),  "",false);
		
		// UPDATE Finance Profit Details
		getFinanceProfitDetailDAO().update(profitDetail);

		logger.debug("Leaving ");
	}
	
	private String getRateReview(DataSet dataSet) {
		StringBuffer strprovsn = new StringBuffer();

		if (dataSet != null) {
			strprovsn.append("FinBranch");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinBranch());
			strprovsn.append(";");

			strprovsn.append("PostDate");
			strprovsn.append("-");
			strprovsn.append(DateUtility.formatUtilDate(dataSet.getPostDate(), PennantConstants.dateFormat));
			strprovsn.append(";");

			strprovsn.append("ValueDate");
			strprovsn.append("-");
			strprovsn.append(DateUtility.formatUtilDate(dataSet.getValueDate(), PennantConstants.dateFormat));
			strprovsn.append(";");

			strprovsn.append("SchdDate");
			strprovsn.append("-");
			strprovsn.append(DateUtility.formatUtilDate(dataSet.getSchdDate(), PennantConstants.dateFormat));
			strprovsn.append(";");

			strprovsn.append("FinType");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinType());
			strprovsn.append(";");
			
			strprovsn.append("FinCcy");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinCcy());
			strprovsn.append(";");

			strprovsn.append("DisburseAccount");
			strprovsn.append("-");
			strprovsn.append(dataSet.getDisburseAccount());
			strprovsn.append(";");

			strprovsn.append("RepayAccount");
			strprovsn.append("-");
			strprovsn.append(dataSet.getRepayAccount());
			strprovsn.append(";");

			strprovsn.append("FinAccount");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinAccount());
			strprovsn.append(";");

			strprovsn.append("FinAmount");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinAmount());
			strprovsn.append(";");

			strprovsn.append("FinCustPftAccount");
			strprovsn.append("-");
			strprovsn.append(dataSet.getFinCustPftAccount());
			strprovsn.append(";");

			strprovsn.append("NewRecord");
			strprovsn.append("-");
			strprovsn.append(dataSet.isNewRecord());
			strprovsn.append(";");

			strprovsn.append("DownPayment");
			strprovsn.append("-");
			strprovsn.append(dataSet.getDownPayment()); //TODO AMTFORMART
			strprovsn.append(";");

			strprovsn.append("NoOfTerms");
			strprovsn.append("-");
			strprovsn.append(dataSet.getNoOfTerms());
			strprovsn.append(";");



		}

		return strprovsn.toString();
	}

	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public DefermentDetailDAO getDefermentDetailDAO() {
		return defermentDetailDAO;
	}
	public void setDefermentDetailDAO(DefermentDetailDAO defermentDetailDAO) {
		this.defermentDetailDAO = defermentDetailDAO;
	}

	public DefermentHeaderDAO getDefermentHeaderDAO() {
		return defermentHeaderDAO;
	}
	public void setDefermentHeaderDAO(DefermentHeaderDAO defermentHeaderDAO) {
		this.defermentHeaderDAO = defermentHeaderDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}
	public void setFinanceProfitDetailDAO(
			FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setFinanceProfitDetailFiller(FinanceProfitDetailFiller financeProfitDetailFiller) {
		this.financeProfitDetailFiller = financeProfitDetailFiller;
	}
	public FinanceProfitDetailFiller getFinanceProfitDetailFiller() {
		return financeProfitDetailFiller;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public BatchAdminDAO getBatchAdminDAO() {
		return batchAdminDAO;
	}

	public void setBatchAdminDAO(BatchAdminDAO batchAdminDAO) {
		this.batchAdminDAO = batchAdminDAO;
	}

}
