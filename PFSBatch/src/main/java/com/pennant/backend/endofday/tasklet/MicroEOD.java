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
 * FileName    		:  NextBussinessDateUpdation.java										*                           
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.DateUtility;
import com.pennant.eod.EodService;
import com.pennant.eod.constants.EodConstants;

public class MicroEOD implements Tasklet {

	private Logger						logger	= Logger.getLogger(MicroEOD.class);

	private static final String			sqlCustForProcess		= "SELECT CustId FROM CustomerQueuing WHERE ThreadId=? AND Progress = 0 ";
	
	private EodService					eodService;
	private DataSource					dataSource;
	
	private PlatformTransactionManager	transactionManager;

	public MicroEOD() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Micro EOD On : " + valueDate);
		int threadId = (int) context.getStepContext().getStepExecutionContext().get(EodConstants.THREAD);

		logger.info("process Statred by the Thread : " + threadId + " with date " + valueDate.toString());
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		long custId = 0;
		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setReadOnly(true);
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus txStatus = null;
		try {
			connection = DataSourceUtils.doGetConnection(dataSource);
			sqlStatement = connection.prepareStatement(sqlCustForProcess);
			sqlStatement.setInt(1, threadId);
			resultSet = sqlStatement.executeQuery();
			int count = 0;
			
			//Read all customers to be processed with Forward Cursor
			while (resultSet.next()) {
				custId = resultSet.getLong("CustId");
				//update start
				eodService.updateStart(threadId, custId);

				//BEGIN TRANSACTION
				txStatus = transactionManager.getTransaction(txDef);

				//process
				eodService.doProcess(connection, custId, valueDate);

				//Update Status
				eodService.updateEnd(threadId, custId);

				//COMMIT THE TRANSACTION
				transactionManager.commit(txStatus);
				count++;

				context.getStepContext().getStepExecution().getExecutionContext().put("Completed", count);
			}

			resultSet.close();
			sqlStatement.close();
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			logger.error("Exception: ", e);
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
		}

		logger.debug("COMPLETE: Micro EOD On :" + valueDate);
		return RepeatStatus.FINISHED;
	}

	public void setEodService(EodService eodService) {
		this.eodService = eodService;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
