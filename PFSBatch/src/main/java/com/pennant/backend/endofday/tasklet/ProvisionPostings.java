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
 * FileName    		:  ProvisionPostings.java													*                           
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
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

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
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.BatchUtil;

public class ProvisionPostings implements Tasklet {
	private Logger						logger			= Logger.getLogger(ProvisionPostings.class);

	private FinanceMainDAO				financeMainDAO;
	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private FinanceProfitDetailDAO		financeProfitDetailDAO;
	private PostingsPreparationUtil		postingsPreparationUtil;
	private ProvisionDAO				provisionDAO;
	private ProvisionMovementDAO		provisionMovementDAO;
	private DataSource					dataSource;

	private Date						dateValueDate	= null;

	int									postings		= 0;
	int									processed		= 0;

	public ProvisionPostings() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		dateValueDate = DateUtility.getAppValueDate();

		logger.debug("START: Provision Postings for Value Date: " + dateValueDate);

		//Process for Provision Posting IF only Bank Allowed for ALLOWED AUTO PROVISION
		String alwAutoProv = SysParamUtil.getValueAsString("ALW_PROV_EOD");
		if ("Y".equals(alwAutoProv)) {

			// READ REPAYMENTS DUE TODAY
			Connection connection = null;
			ResultSet resultSet = null;
			PreparedStatement sqlStatement = null;

			try {

				// Fetch Overdue Finances for Provision Calculation
				connection = DataSourceUtils.doGetConnection(getDataSource());
				sqlStatement = connection.prepareStatement(getCountQuery());
				resultSet = sqlStatement.executeQuery();
				int count = 0;
				if (resultSet.next()) {
					count = resultSet.getInt(1);
				}
				BatchUtil.setExecution(context, "TOTAL", String.valueOf(count));
				sqlStatement.close();
				resultSet.close();

				sqlStatement = connection.prepareStatement(prepareProvMovementSelectQuery());
				resultSet = sqlStatement.executeQuery();

				FinanceMain financeMain = null;
				List<FinanceScheduleDetail> schdDetails = null;
				FinanceProfitDetail pftDetail = null;

				while (resultSet.next()) {

					ProvisionMovement movement = prepareProvisionMovementData(resultSet);

					financeMain = getFinanceMainDAO().getFinanceMainForBatch(resultSet.getString("FinReference"));
					schdDetails = getFinanceScheduleDetailDAO().getFinSchdDetailsForBatch(
							resultSet.getString("FinReference"));

					pftDetail = new FinanceProfitDetail();
					pftDetail.setFinReference(resultSet.getString("FinReference"));
					pftDetail.setAcrTillLBD(resultSet.getBigDecimal("AcrTillLBD"));
					pftDetail.setPftAmzSusp(resultSet.getBigDecimal("PftAmzSusp"));
					pftDetail.setAmzTillLBD(resultSet.getBigDecimal("AmzTillLBD"));

					Date dueFromDate = resultSet.getDate("DueFromDate");
					AEEvent aeEvent = AEAmounts.procAEAmounts(financeMain, schdDetails, pftDetail,
							AccountEventConstants.ACCEVENT_PROVSN, dateValueDate, dueFromDate);
					AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
					amountCodes.setProvDue(movement.getProvisionDue());
					amountCodes.setProvAmt(movement.getProvisionedAmt());

					HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
					aeEvent.setDataMap(dataMap);
					
					aeEvent = getPostingsPreparationUtil().processPostingDetails(aeEvent);


					if (aeEvent.isPostingSucess()) {
							movement.setProvisionedAmt(movement.getProvisionedAmt().add(movement.getProvisionDue()));
							movement.setProvisionDue(BigDecimal.ZERO);
							movement.setProvisionPostSts("C");
							movement.setLinkedTranId(aeEvent.getLinkedTranId());

							//Update Provision Movement Details
							getProvisionDAO().updateProvAmt(movement, "");
							getProvisionMovementDAO().update(movement, "");

							postings++;
					}
					
					pftDetail = null;

					processed = resultSet.getRow();

					BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));
					BatchUtil.setExecution(context, "INFO", getInfo());
				}

				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));
				BatchUtil.setExecution(context, "INFO", getInfo());

			} catch (Exception e) {
				logger.error("Exception: ", e);
				throw e;
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}

				if (sqlStatement != null) {
					sqlStatement.close();
				}
			}
		}
		logger.debug("COMPLETED: Provision Postings for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for Preparation for Provision Movement Details
	 * 
	 * @param provision
	 * @param valueDate
	 * @throws SQLException
	 */
	private ProvisionMovement prepareProvisionMovementData(ResultSet resultSet) throws SQLException {

		ProvisionMovement movement = new ProvisionMovement();
		movement.setFinReference(resultSet.getString("FinReference"));
		movement.setProvMovementDate(resultSet.getDate("ProvMovementDate"));
		movement.setProvMovementSeq(resultSet.getInt("ProvMovementSeq"));
		movement.setDueFromDate(resultSet.getDate("DueFromDate"));
		movement.setProvisionDue(resultSet.getBigDecimal("ProvisionDue"));
		movement.setProvisionedAmt(resultSet.getBigDecimal("ProvisionedAmt"));
		return movement;

	}

	/**
	 * Method for get count of Provisions
	 * 
	 * @return selQuery
	 */
	private String getCountQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT count(T1.FinReference)");
		selQuery.append(" FROM FinProvMovements AS T1 ");
		selQuery.append(" INNER JOIN FinanceMain AS T2 ON T1.FinReference = T2.FinReference ");
		selQuery.append(" INNER JOIN FinPftDetails AS T4 ON T1.FinReference = T4.FinReference ");
		selQuery.append(" INNER JOIN RMTFinanceTypes AS T3 ON T2.FinType = T3.FinType ");
		selQuery.append(" WHERE T1.ProvisionPostSts = 'R'");
		return selQuery.toString();

	}

	/**
	 * Method for preparation of Select Query To get Provision Details data
	 * 
	 * @param selQuery
	 * @return
	 */
	private String prepareProvMovementSelectQuery() {

		StringBuilder selQuery = new StringBuilder(
				" SELECT T1.FinReference,T1.ProvMovementDate,T1.ProvMovementSeq , T1.DueFromDate, ");
		selQuery.append(" T1.ProvisionedAmt, T1.ProvisionDue, T3.AllowRIAInvestment, T4.AcrTillLBD, T4.PftAmzSusp, T4.AmzTillLBD ");
		selQuery.append(" FROM FinProvMovements AS T1 ");
		selQuery.append(" INNER JOIN FinanceMain AS T2 ON T1.FinReference = T2.FinReference ");
		selQuery.append(" INNER JOIN FinPftDetails AS T4 ON T1.FinReference = T4.FinReference ");
		selQuery.append(" INNER JOIN RMTFinanceTypes AS T3 ON T2.FinType = T3.FinType ");
		selQuery.append(" WHERE T1.ProvisionPostSts = 'R'");
		return selQuery.toString();

	}

	private String getInfo() {
		StringBuilder builder = new StringBuilder();

		builder.append("Total Provision Posting's").append(": ").append(postings);

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

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public ProvisionDAO getProvisionDAO() {
		return provisionDAO;
	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public ProvisionMovementDAO getProvisionMovementDAO() {
		return provisionMovementDAO;
	}

	public void setProvisionMovementDAO(ProvisionMovementDAO provisionMovementDAO) {
		this.provisionMovementDAO = provisionMovementDAO;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

}
