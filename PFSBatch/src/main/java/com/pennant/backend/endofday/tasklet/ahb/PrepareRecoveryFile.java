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
 * FileName    		:  PrepareRecoveryFile.java													*                           
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

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.BatchFileUtil;
import com.pennant.eod.beans.PaymentRecoveryDetail;
import com.pennant.eod.beans.PaymentRecoveryHeader;
import com.pennant.eod.dao.PaymentRecoveryHeaderDAO;

public class PrepareRecoveryFile implements Tasklet {

	private static Logger				logger		= Logger.getLogger(PrepareRecoveryFile.class);

	private PaymentRecoveryHeaderDAO	paymentRecoveryHeaderDAO;
	private DataSource					dataSource;
	String								logRefernce	= "";

	public PrepareRecoveryFile() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		logger.debug("START: Request File Preparation  for Value Date: " + DateUtility.getAppValueDate());

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		int processed = 0;
		String batchRef = BatchFileUtil.getBatchReference();

		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setString(1, batchRef);
			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			resultSet.close();
			sqlStatement.close();

			PaymentRecoveryHeader header = getPaymentRecoveryHeaderDAO().getPaymentRecoveryHeader(batchRef);

			File file = BatchFileUtil.getBatchFile(BatchFileUtil.getAutoPayReqFileName());
			FileWriter filewriter = BatchFileUtil.getFileWriter(file);
			// header
			BatchFileUtil.writeline(filewriter, writeHeader(header));

			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setString(1, batchRef);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				logRefernce = resultSet.getString("FinanceReference");
				// details
				BatchFileUtil.writeline(filewriter, writeDetails(resultSet));
				processed++;
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));
			}

			resultSet.close();
			sqlStatement.close();

			String rpyMethod = ImplementationConstants.REPAY_HIERARCHY_METHOD;

			if (rpyMethod.equals(RepayConstants.REPAY_HIERARCHY_FIPCS) || rpyMethod.equals(RepayConstants.REPAY_HIERARCHY_FPICS)) {

				sqlStatement = connection.prepareStatement(prepareChargesQuery());
				sqlStatement.setString(1, batchRef);
				resultSet = sqlStatement.executeQuery();

				while (resultSet.next()) {
					logRefernce = resultSet.getString("FinanceReference");
					// details
					BatchFileUtil.writeline(filewriter, writeDetails(resultSet));
					processed++;
					BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));
				}
			}
			// footer
			BatchFileUtil.writeline(filewriter, writeFooter(header));
			filewriter.close();
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

		logger.debug("START: Request File Preparation for Value Date: " + DateUtility.getAppValueDate());
		return RepeatStatus.FINISHED;

	}

	/**
	 * @param header
	 * @return
	 */
	private String writeHeader(PaymentRecoveryHeader header) {
		logger.debug(" Entering ");

		StringBuilder builder = new StringBuilder();
		addField(builder, BatchFileUtil.HEADER);
		addField(builder, header.getBatchType());
		addField(builder, header.getBatchRefNumber());
		addField(builder, header.getFileName());
		builder.append(DateUtility.format(header.getFileCreationDate(), DateUtility.DateFormat.SHORT_DATE_TIME));

		logger.debug(" Leaving ");
		return builder.toString();
	}

	/**
	 * @param resultSet
	 * @param header
	 * @return
	 * @throws SQLException
	 */
	private String writeDetails(ResultSet resultSet) throws SQLException {
		logger.debug(" Entering ");

		StringBuilder builder = new StringBuilder();
		addField(builder, BatchFileUtil.DETAILS);
		addField(builder, resultSet.getString("TransactionReference"));
		addField(builder, resultSet.getString("PrimaryDebitAccount"));
		addField(builder, resultSet.getString("SecondaryDebitAccounts"));
		addField(builder, resultSet.getString("CreditAccount"));

		addField(builder, DateUtility.format(resultSet.getDate("ScheduleDate"), PennantConstants.DBDateFormat));
		addField(builder, resultSet.getString("FinanceReference"));
		addField(builder, resultSet.getString("CustomerReference"));
		addField(builder, resultSet.getString("DebitCurrency"));
		addField(builder, resultSet.getString("CreditCurrency"));
		
		int code = CurrencyUtil.getFormat(resultSet.getString("DebitCurrency"));
		String amount = PennantApplicationUtil.formateAmount(resultSet.getBigDecimal("PaymentAmount"), code).toString();
		addField(builder, amount);
		addField(builder, resultSet.getString("TransactionPurpose"));
		addField(builder, resultSet.getString("FinanceBranch"));
		addField(builder, resultSet.getString("FinanceType"));
		builder.append(resultSet.getString("FinancePurpose"));
		logger.debug(" Leaving ");
		return builder.toString();
	}

	/**
	 * @param header
	 * @return
	 */
	private String writeFooter(PaymentRecoveryHeader header) {
		logger.debug(" Entering ");

		StringBuilder builder = new StringBuilder();
		addField(builder, BatchFileUtil.FOOTER);
		builder.append(header.getNumberofRecords());

		logger.debug(" Leaving ");
		return builder.toString();
	}

	/**
	 * Method for prepare line
	 * 
	 * @param line
	 * @param value
	 */
	public void addField(StringBuilder line, String value) {
		line.append(value).append(BatchFileUtil.DELIMITER);

	}

	/**
	 * Method for get count of Schedule data
	 * 
	 * @return selQuery
	 */
	private String getCountQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT count(*) ");
		selQuery.append("  From PaymentRecoveryDetail where BatchRefNumber = ?  ");
		return selQuery.toString();

	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private String prepareSelectQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT * From PaymentRecoveryDetail ");
		selQuery.append(" where BatchRefNumber = ?  and  priority > 0 order  by PrimaryDebitAccount ,priority ");
		return selQuery.toString();

	}

	private String prepareChargesQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT * From PaymentRecoveryDetail ");
		selQuery.append(" where BatchRefNumber = ?  and  priority < 0 order by PrimaryDebitAccount , priority desc ");
		return selQuery.toString();

	}

	/**
	 * @param groupedList
	 * @param accNumber
	 * @param list
	 */
	public void addEntryRelatedToAccount(List<PaymentRecoveryDetail> groupedList, String accNumber, List<PaymentRecoveryDetail> list) {
		logger.debug(" Entering ");
		for (PaymentRecoveryDetail paymentRecoveryDetail : list) {
			if (paymentRecoveryDetail.getPrimaryDebitAccount().equals(accNumber)) {
				groupedList.add(paymentRecoveryDetail);
			}
		}

		logger.debug(" Leaving ");
	}

	/**
	 * @param transOrder
	 * @param listoftransactions
	 * @return
	 */
	public ReturnDataSet getMatchingEntry(int transOrder, List<ReturnDataSet> listoftransactions) {
		logger.debug(" Entering ");

		for (ReturnDataSet returnDataSet : listoftransactions) {
			if (returnDataSet.getDerivedTranOrder() == transOrder) {
				return returnDataSet;
			}
		}

		logger.debug(" Leaving ");
		return null;
	}

	public PaymentRecoveryHeaderDAO getPaymentRecoveryHeaderDAO() {
		return paymentRecoveryHeaderDAO;
	}

	public void setPaymentRecoveryHeaderDAO(PaymentRecoveryHeaderDAO paymentRecoveryHeaderDAO) {
		this.paymentRecoveryHeaderDAO = paymentRecoveryHeaderDAO;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
