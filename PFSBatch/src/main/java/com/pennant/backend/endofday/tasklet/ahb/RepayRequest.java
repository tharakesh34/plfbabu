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
 * FileName    		:  RepayRequest.java													*                           
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
package com.pennant.backend.endofday.tasklet.ahb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.core.ServiceUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.BatchFileUtil;
import com.pennant.eod.PaymentRecoveryService;
import com.pennant.eod.beans.PaymentRecoveryHeader;

public class RepayRequest implements Tasklet {

	private Logger					logger		= Logger.getLogger(RepayRequest.class);

	private DataSource				dataSource;
	private ServiceUtil				serviceUtil;
	private PaymentRecoveryService	paymentRecoveryService;

	int								repayments	= 0;
	String							logRefernce	= "";

	public RepayRequest() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		logger.debug("START: Repayments Due Today Queue Postings for Value Date: " + DateUtility.getValueDate());

		// FETCH Finance Repayment Queues
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		int processed = 0;
		try {

			// header
			PaymentRecoveryHeader header = new PaymentRecoveryHeader();
			header.setBatchRefNumber(BatchFileUtil.getBatchReference());
			header.setBatchType(BatchFileUtil.BATCH_CODE);
			header.setFileName(BatchFileUtil.getAutoPayReqFileName());
			header.setFileCreationDate(DateUtility.getSysDate());
			header.setNumberofRecords(0);
			getPaymentRecoveryService().save(header);

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			resultSet.close();
			sqlStatement.close();

			// Finance Repayments Details
			sqlStatement = connection.prepareStatement(repayQueue);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				String finRef = resultSet.getString("FinReference");
				logRefernce = finRef;

				// Prepare Finance RepayQueue Data
				BeanPropertyRowMapper<FinRepayQueue> beanPropertyRowMapper = new BeanPropertyRowMapper<>(FinRepayQueue.class);
				FinRepayQueue finRepayQueue = beanPropertyRowMapper.mapRow(resultSet, resultSet.getRow());
				getServiceUtil().processRepayRequest(DateUtility.getValueDate(),finRepayQueue, header);

				processed = resultSet.getRow();
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));

			}

			getPaymentRecoveryService().updateTotal(header);

		} catch (Exception e) {
			logger.error("Finrefernce :" + logRefernce, e);
			throw e;
		} finally {

			if (resultSet != null) {
				resultSet.close();
			}

			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}

		logger.debug("END: Repayments Due Today Queue Postings for Value Date: " + DateUtility.getValueDate());
		return RepeatStatus.FINISHED;

	}

	/**
	 * Method for get count of Repay postings
	 * 
	 * @return selQuery
	 */
	private String getCountQuery() {

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT count(*)");
		selectSql.append(" FROM FinRpyQueue RQ  INNER JOIN FinPftDetails PD ON PD.FinReference = RQ.FinReference ");
		return selectSql.toString();
	}

	
	
	String	repayQueue	= "	SELECT RQ.FinReference, RQ.FinType, RQ.RpyDate, RQ.FinPriority, RQ.Branch,RQ.LinkedFinRef,"
								+ "RQ.CustomerID, RQ.FinRpyFor, RQ.SchdPft, RQ.SchdPri, RQ.SchdPftPaid, RQ.SchdPriPaid, RQ.SchdPftBal, RQ.SchdPriBal, RQ.SchdIsPftPaid, RQ.SchdIsPriPaid,"
								+ "(RQ.SchdPftBal+ RQ.SchdPriBal)  RepayQueueBal, PD.AcrTillLBD, PD.PftAmzSusp, PD.AmzTillLBD, "
								+ "RQ.SchdFee, RQ.SchdFeePaid, RQ.SchdFeeBal, RQ.SchdIns, RQ.SchdInsPaid, RQ.SchdInsBal, "
								+ "RQ.SchdSuplRent, RQ.SchdSuplRentPaid, RQ.SchdSuplRentBal, "
								+ "RQ.SchdIncrCost, RQ.SchdIncrCostPaid, RQ.SchdIncrCostBal,RQ.AdvProfit,RQ.SchdRate,RQ.Rebate "
								+ "FROM FinRpyQueue RQ  INNER JOIN FinPftDetails PD ON PD.FinReference = RQ.FinReference "
								+ " ORDER BY RQ.RpyDate, RQ.FinPriority, RQ.FinReference, RQ.FinRpyFor , RQ.LinkedFinRef ASC ";



	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public PaymentRecoveryService getPaymentRecoveryService() {
		return paymentRecoveryService;
	}

	public void setPaymentRecoveryService(PaymentRecoveryService paymentRecoveryService) {
		this.paymentRecoveryService = paymentRecoveryService;
	}

	public ServiceUtil getServiceUtil() {
		return serviceUtil;
	}

	public void setServiceUtil(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}

}
